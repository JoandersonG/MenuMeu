package joandersongoncalves.example.veganocook.data

import joandersongoncalves.example.veganocook.data.database.RecipeDao
import joandersongoncalves.example.veganocook.data.model.Category
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
        return if (listCategoryWithRecipes.isNotEmpty()) {
            listCategoryWithRecipes[0].recipes
        } else {
            listOf()
        }
    }

    suspend fun getRecipeWithCategories(recipeId: Int): List<Category> {
        val listRecipeWithCategories = recipeDao.getRecipeWithCategories(recipeId)
        return if (listRecipeWithCategories.isNotEmpty()) {
            listRecipeWithCategories[0].categories
        } else {
            listOf()
        }
    }

    suspend fun updateRecipe(recipe: Recipe) {
        recipeDao.updateRecipeWithCategories(recipe)
    }

    suspend fun deleteRecipe(recipe: Recipe) {
        recipeDao.deleteRecipe(recipe)
    }
}