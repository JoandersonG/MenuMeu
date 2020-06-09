package joandersongoncalves.example.veganocook.data.database

import androidx.room.*
import joandersongoncalves.example.veganocook.data.model.Category
import joandersongoncalves.example.veganocook.data.model.CategoryWithRecipes
import joandersongoncalves.example.veganocook.data.model.Recipe
import joandersongoncalves.example.veganocook.data.model.RecipeCategoryCrossRef

@Dao
abstract class RecipeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertRecipe(recipe: Recipe)

    suspend fun insertRecipeWithCategories(recipe: Recipe) {
        insertRecipe(recipe)
        val recipeId = getLastInsertedRecipe()[0].recipeId
        for (category in recipe.categories) {
            insertRecipeCategoryCrossRef(RecipeCategoryCrossRef(recipeId, category))
        }
    }

    @Query("SELECT * FROM recipes ORDER BY recipe_id DESC LIMIT 1 ")
    abstract suspend fun getLastInsertedRecipe(): List<Recipe>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertCategory(category: Category)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertRecipeCategoryCrossRef(value: RecipeCategoryCrossRef)

    @Update
    abstract fun updateRecipe(recipe: Recipe)

    @Delete
    abstract fun deleteRecipe(recipe: Recipe)

    @Query("DELETE FROM recipes")
    abstract suspend fun deleteAllRecipes()

    @Query("DELETE FROM recipes_categories")
    abstract suspend fun deleteAllRecipeCategoriesCrossRef()

    suspend fun deleteAll() {
        deleteAllRecipes()
        deleteAllRecipeCategoriesCrossRef()
    }

    @Query("SELECT * FROM recipes")
    abstract suspend fun getAllRecipes(): List<Recipe>

    @Transaction
    @Query("SELECT * FROM categories WHERE category_name = :category")
    abstract suspend fun getCategoryWithRecipes(category: String): List<CategoryWithRecipes>

}