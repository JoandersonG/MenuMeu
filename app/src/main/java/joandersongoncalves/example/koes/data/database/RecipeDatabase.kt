package joandersongoncalves.example.koes.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import joandersongoncalves.example.koes.data.model.Category
import joandersongoncalves.example.koes.data.model.Recipe
import joandersongoncalves.example.koes.data.model.RecipeCategoryCrossRef
import joandersongoncalves.example.koes.data.model.SearchHistoryEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(
    entities = [Recipe::class, Category::class, RecipeCategoryCrossRef::class, SearchHistoryEntry::class],
    version = 3
)
abstract class RecipeDatabase : RoomDatabase() {

    abstract fun recipeDao(): RecipeDao

    companion object {

        @Volatile
        private var INSTANCE: RecipeDatabase? = null

        // assure only one instance
        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): RecipeDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RecipeDatabase::class.java,
                    "recipe_database"
                )
                    .addCallback(RecipeDatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()
                INSTANCE = instance
                return instance
            }
        }

        private class RecipeDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {

            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch {
                        insertCategories(database.recipeDao())
                    }
                }
            }

            suspend fun insertCategories(recipeDao: RecipeDao) {
                recipeDao.insertCategory(Category(Recipe.BREAKFAST, true))
                recipeDao.insertCategory(Category(Recipe.LUNCH, true))
                recipeDao.insertCategory(Category(Recipe.DINNER, true))
                recipeDao.insertCategory(Category(Recipe.SNACK, true))
            }

            suspend fun populateDatabase(recipeDao: RecipeDao) {
                recipeDao.deleteAll()
            }
        }

    }
}

/*
* Database version 2 adds SearchHistory table.
*/
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS search_history (`entry_title` TEXT NOT NULL, `creation_timestamp` INTEGER, PRIMARY KEY(`entry_title`))"
        )
    }
}
/*
* Database version 3 adds ingredients text field
*/
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE recipes ADD COLUMN ingredients TEXT"
        )
    }
}