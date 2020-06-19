package joandersongoncalves.example.veganocook.data.database

import androidx.room.*
import joandersongoncalves.example.veganocook.data.model.*

@Dao
abstract class RecipeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertRecipe(recipe: Recipe)

    suspend fun insertRecipeWithCategories(recipe: Recipe) {
        insertRecipe(recipe)
        val recipeId = getLastInsertedRecipe()[0].recipeId
        for (category in recipe.categories) {
            insertRecipeCategoryCrossRef(RecipeCategoryCrossRef(recipeId, category.categoryName))
        }
    }

    suspend fun updateRecipeWithCategories(recipe: Recipe) {
        //recuperar todos as categorias associadas com essa recipe

        //remover todas essas associações

        //criar todas as novas associações


        deleteAllRecipeCategoriesCrossRef(recipe.recipeId)
        updateRecipe(recipe)
        for (category in recipe.categories) {
            //println("adicionando a categoria $category em ${recipe.name}")
            insertRecipeCategoryCrossRef(
                RecipeCategoryCrossRef(
                    recipe.recipeId,
                    category.categoryName
                )
            )
        }
    }

    @Query("SELECT * FROM recipes ORDER BY recipe_id DESC LIMIT 1 ")
    abstract suspend fun getLastInsertedRecipe(): List<Recipe>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertCategory(category: Category)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertRecipeCategoryCrossRef(value: RecipeCategoryCrossRef)

    @Update
    abstract suspend fun updateRecipe(recipe: Recipe)

    @Delete
    abstract suspend fun deleteRecipe(recipe: Recipe)

    @Query("DELETE FROM recipes")
    abstract suspend fun deleteAllRecipes()

    @Query("DELETE FROM recipes_categories")
    abstract suspend fun deleteAllRecipeCategoriesCrossRef()

    @Query("DELETE FROM recipes_categories WHERE recipe_id = :recipeId")
    abstract suspend fun deleteAllRecipeCategoriesCrossRef(recipeId: Int)

    suspend fun deleteAll() {
        deleteAllRecipes()
        deleteAllRecipeCategoriesCrossRef()
    }

    @Query("SELECT * FROM recipes")
    abstract suspend fun getAllRecipes(): List<Recipe>

    @Transaction
    @Query("SELECT * FROM categories WHERE category_name = :category")
    abstract suspend fun getCategoryWithRecipes(category: String): List<CategoryWithRecipes>

    @Transaction
    @Query("SELECT * FROM recipes WHERE recipe_id = :recipeId")
    abstract suspend fun getRecipeWithCategories(recipeId: Int): List<RecipesWithCategories>

    @Query("SELECT * FROM recipes WHERE name LIKE :q")
    abstract suspend fun queryByName(q: String): List<Recipe>

}