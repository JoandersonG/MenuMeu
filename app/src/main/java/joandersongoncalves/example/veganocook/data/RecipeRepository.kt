package joandersongoncalves.example.veganocook.data

import joandersongoncalves.example.veganocook.data.database.RecipeDao
import joandersongoncalves.example.veganocook.data.model.Recipe

class RecipeRepository(private val recipeDao: RecipeDao) {

    suspend fun insert(recipe: Recipe) {
        recipeDao.insertRecipeWithCategories(recipe)
    }

    suspend fun getAllRecipes(): List<Recipe> {
        return recipeDao.getAllRecipes()
    }

    suspend fun getRecipesByCategory(category: String): List<Recipe> {
        val listCategoryWithRecipes = recipeDao.getCategoryWithRecipes(category)
        return listCategoryWithRecipes[0].recipes

    }
}