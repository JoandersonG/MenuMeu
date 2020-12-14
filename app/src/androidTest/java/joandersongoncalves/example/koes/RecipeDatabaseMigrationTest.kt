package joandersongoncalves.example.koes

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import joandersongoncalves.example.koes.data.database.MIGRATION_1_2
import joandersongoncalves.example.koes.data.database.MIGRATION_2_3
import joandersongoncalves.example.koes.data.database.RecipeDatabase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RecipeDatabaseMigrationTest {
    private val TEST_DB = "migration-test"
    private lateinit var database: SupportSQLiteDatabase

    @JvmField
    @Rule
    val migrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        RecipeDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    fun migration1to2() {
        database = migrationTestHelper.createDatabase(TEST_DB, 1)
            .apply {
                execSQL(
                    """
                    INSERT INTO recipes 
                    (name,description,is_favorite, video_title,video_description,video_url,default_thumbnail_url, medium_thumbnail_url)
                    VALUES('Bacalhonese vegana','Uma comida gostosa',1,'bacalhonese','descrição do vídeo bacalhonese','url bacalhonese','url thumb def','med thumb url')
                """.trimIndent()
                )

                close()
            }

        database = migrationTestHelper.runMigrationsAndValidate(
            TEST_DB,
            2,
            true,
            MIGRATION_1_2
        )
    }

    @Test
    fun migration2to3() {
        database = migrationTestHelper.createDatabase(TEST_DB, 2)
            .apply {
                execSQL(
                    """
                    INSERT INTO recipes 
                    (name,description,ingredients,is_favorite, video_title,video_description,video_url,default_thumbnail_url, medium_thumbnail_url)
                    VALUES('Bacalhonese vegana','Uma comida gostosa','uma batata, duas cebolas',1,'bacalhonese','descrição do vídeo bacalhonese','url bacalhonese','url thumb def','med thumb url')
                """.trimIndent()
                )
                close()
            }

        database = migrationTestHelper.runMigrationsAndValidate(
            TEST_DB,
            3,
            true,
            MIGRATION_2_3
        )
    }
}