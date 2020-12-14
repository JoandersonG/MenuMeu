package joandersongoncalves.example.menumeu.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import joandersongoncalves.example.menumeu.data.RecipeRepository
import joandersongoncalves.example.menumeu.data.database.RecipeDatabase
import joandersongoncalves.example.menumeu.data.model.Category
import joandersongoncalves.example.menumeu.data.model.Recipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class CategoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RecipeRepository
    var recipesByCategory = MutableLiveData<List<Recipe>>()
    var allCategories = MutableLiveData<List<Category>>()
    var selectedCategoriesOnFilter = MutableLiveData<List<Category>>()
    var categoryTitle = MutableLiveData<String?>()
    var isFavoriteRecipesOnly = MutableLiveData<Boolean>()

    init {
        val recipeDao = RecipeDatabase.getDatabase(application, viewModelScope).recipeDao()
        repository = RecipeRepository(recipeDao)
        allCategories.value = listOf()
        selectedCategoriesOnFilter.value = listOf()
        categoryTitle.value = null
        isFavoriteRecipesOnly.value = false
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
                        isFavoriteRecipesOnly.value?.let { isFavRecipes ->
                            if (isFavRecipes) {//test if it is supposed to be showing only favorite recipes
                                if (recipeOnResult.isFavorite) { //it's favorite
                                    listRecipe.add(recipeOnResult)
                                }
                                true
                            } else {
                                listRecipe.add(recipeOnResult)
                                true
                            }
                        }
                    }
                }
            }
            if (it.isEmpty() && isFavoriteRecipesOnly.value!!) {
                listRecipe.addAll(repository.getFavoriteRecipes())
            } else if (it.isEmpty()) { //get all results, cause there's no category filter
                listRecipe.addAll(repository.getAllRecipes())
            }
            true
        }
        //get two (or three, doesn't matter) categories from each recipe for showing with the adapter
        for (recipe in listRecipe) {
            val categories: MutableList<Category> =
                repository.getThreeCategories(recipe.recipeId) as MutableList<Category>
            //do not save category that we're in right now in the CategoryActivity
            if (categories.size >= 3) {
                categories.remove(Category(categoryTitle.value.toString()))
            }
            recipe.categories = categories
        }
        recipesByCategory.postValue(listRecipe)
    }

    fun cleanSelectedCategories() {
        selectedCategoriesOnFilter.value = listOf()
    }

    fun toggleFavoriteRecipe(recipe: Recipe) = viewModelScope.launch(Dispatchers.IO) {
        recipe.categories = repository.getRecipeWithCategories(recipe.recipeId)
        repository.updateRecipe(recipe)
        updateAllRecipes()
    }

    fun setToolbarTitle(string: String) {
        categoryTitle.value = string
    }
}