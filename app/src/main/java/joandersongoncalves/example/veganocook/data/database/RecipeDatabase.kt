package joandersongoncalves.example.veganocook.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import joandersongoncalves.example.veganocook.data.model.Category
import joandersongoncalves.example.veganocook.data.model.Recipe
import joandersongoncalves.example.veganocook.data.model.RecipeCategoryCrossRef
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(
    entities = [Recipe::class, Category::class, RecipeCategoryCrossRef::class, SearchHistoryEntry::class],
    version = 2
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
                ).addCallback(RecipeDatabaseCallback(scope))
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