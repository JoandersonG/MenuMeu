package joandersongoncalves.example.menumeu.data

import joandersongoncalves.example.menumeu.data.database.RecipeDao
import joandersongoncalves.example.menumeu.data.model.Category
import joandersongoncalves.example.menumeu.data.model.HomeRecipeSet
import joandersongoncalves.example.menumeu.data.model.Recipe
import joandersongoncalves.example.menumeu.data.model.SearchHistoryEntry

class RecipeRepository(private val recipeDao: RecipeDao) {

    suspend fun insert(recipe: Recipe) {
        recipeDao.insertRecipeWithCategories(recipe)
    }

    suspend fun getAllRecipes(): List<Recipe> {
        return recipeDao.getAllRecipes()
    }

    suspend fun getRecipesByCategory(category: String): List<Recipe> {
        return recipeDao.getRecipesByCategory(category)
    }

    suspend fun getRecipeWithCategories(recipeId: Int): List<Category> {
        val listRecipeWithCategories = recipeDao.getRecipeWithCategories(recipeId)
        return if (listRecipeWithCategories.isNotEmpty()) {
            listRecipeWithCategories[0].categories
        } else {
            listOf()
        }
    }

    suspend fun getThreeCategories(recipeId: Int): List<Category> {
        return recipeDao.getThreeCategories(recipeId)
    }

    suspend fun getFavoriteRecipes(): List<Recipe> {
        return recipeDao.getFavoriteRecipes()
    }

    suspend fun getPreviousSearchEntries(): List<String> {
        val entries = recipeDao.getPreviousSearchEntries()
        val sEntriesAsString: MutableList<String> = ArrayList()
        for (e in entries) {
            sEntriesAsString.add(e.getTitle());
        }
        return sEntriesAsString;
    }

    suspend fun updateRecipe(recipe: Recipe) {
        recipeDao.updateRecipeWithCategories(recipe)
    }

    suspend fun deleteRecipe(recipe: Recipe) {
        recipeDao.deleteRecipe(recipe)
    }

    suspend fun queryByName(q: String): List<Recipe> {
        return recipeDao.queryByName("%$q%")
    }

    suspend fun getAllCategories(): List<Category> {
        return recipeDao.getAllCategories()
    }

    suspend fun saveNewCategory(category: Category) {
        recipeDao.insertCategory(category)
    }

    suspend fun deleteCategory(category: Category) {
        recipeDao.deleteCategory(category)
    }

    suspend fun getRecipesToShow(): List<HomeRecipeSet> {
        return recipeDao.getRecipesToShow()
    }

    suspend fun updateCategory(category: Category) {
        recipeDao.updateCategory(category)
    }

    suspend fun insertSearchHistoryEntry(entry: SearchHistoryEntry) {
        recipeDao.insertSearchEntry(entry);
    }

    suspend fun deleteSearchHistoryEntryByName(entry: String) {
        recipeDao.deleteSearchHistoryEntryName(entry);
    }
}