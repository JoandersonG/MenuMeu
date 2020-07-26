package joandersongoncalves.example.veganocook.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import joandersongoncalves.example.veganocook.data.RecipeRepository
import joandersongoncalves.example.veganocook.data.database.RecipeDatabase
import joandersongoncalves.example.veganocook.data.model.Category
import joandersongoncalves.example.veganocook.data.model.Recipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class CategoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RecipeRepository
    var recipesByCategory = MutableLiveData<List<Recipe>>()
    var allCategories = MutableLiveData<List<Category>>()
    var selectedCategoriesOnFilter = MutableLiveData<List<Category>>()
    var category: String

    init {
        val recipeDao = RecipeDatabase.getDatabase(application, viewModelScope).recipeDao()
        repository = RecipeRepository(recipeDao)
        allCategories.value = listOf()
        selectedCategoriesOnFilter.value = listOf()
        category = ""
    }

    fun getCategoryWithRecipes() = viewModelScope.launch(Dispatchers.IO) {
        when (category) {
            "" -> {//all recipes
                val recipes = repository.getAllRecipes()
                for (recipe in recipes) {
                    recipe.categories = repository.getRecipeWithCategories(recipe.recipeId)
                }
                recipesByCategory.postValue(recipes)
            }
            Recipe.BREAKFAST, Recipe.LUNCH, Recipe.DINNER, Recipe.SNACK -> {
                val recipes = repository.getRecipesByCategory(category)
                for (recipe in recipes) {
                    recipe.categories =
                        repository.getRecipeWithCategories(recipe.recipeId)
                }
                recipesByCategory.postValue(recipes)
            }
            "FAVORITE" -> {
                val recipes = repository.getFavoriteRecipes()
                for (recipe in recipes) {
                    recipe.categories = repository.getRecipeWithCategories(recipe.recipeId)
                }
                recipesByCategory.postValue(recipes)
            }
        }
    }

    fun deleteRecipe(recipe: Recipe) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteRecipe(recipe)
        getCategoryWithRecipes()
    }

    fun updateRecipe(recipe: Recipe) = viewModelScope.launch(Dispatchers.IO) {
        if (recipe.categories.isEmpty()) {
            //get categories
            recipe.categories = repository.getRecipeWithCategories(recipe.recipeId)
        }
        //then update
        repository.updateRecipe(recipe)
        getCategoryWithRecipes()
    }

    fun getAllCategories() = viewModelScope.launch(Dispatchers.IO) {
        allCategories.postValue(repository.getAllCategories())
    }

    fun checkedChangeOnSelectedCategory(category: Category) {
        var newList = mutableListOf<Category>()
        selectedCategoriesOnFilter.value?.let {
            newList = it.toMutableList()
            if (it.contains(category)) {
                println("checked before, now removing")
                newList.remove(category)
            } else {
                println("not checked before, now adding")
                newList.add(category)
            }
        }
        selectedCategoriesOnFilter.value = newList
    }

    fun updateAllRecipes() = viewModelScope.launch(Dispatchers.IO) {
        val listRecipe = mutableListOf<Recipe>()
        selectedCategoriesOnFilter.value?.let {
            for (category in it) {
                val result = repository.getRecipesByCategory(category.categoryName)
                for (recipeOnResult in result) {
                    if (!listRecipe.contains(recipeOnResult)) {
                        listRecipe.add(recipeOnResult)
                    }
                }
            }
            if (it.isEmpty()) { //get all results, cause there's no category filter
                listRecipe.addAll(repository.getAllRecipes())
            }
        }
        recipesByCategory.postValue(listRecipe)
    }

    fun cleanSelectedCategories() {
        selectedCategoriesOnFilter.value = listOf()
    }
}