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

class RecipeDetailsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RecipeRepository
    val recipe = MutableLiveData<Recipe>()
    val categories = MutableLiveData<List<Category>>()

    init {
        val recipeDao = RecipeDatabase.getDatabase(application, viewModelScope).recipeDao()
        repository = RecipeRepository(recipeDao)
    }

    fun deleteRecipe(recipe: Recipe) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteRecipe(recipe)
    }

    fun updateRecipe(recipe: Recipe) = viewModelScope.launch(Dispatchers.IO) {
        if (recipe.categories.isEmpty()) {
            //get categories
            recipe.categories = repository.getRecipeWithCategories(recipe.recipeId)
        }
        //then update
        repository.updateRecipe(recipe)
    }

    fun getRecipeCategories() = viewModelScope.launch(Dispatchers.IO) {
        categories.postValue(recipe.value?.let { repository.getRecipeWithCategories(it.recipeId) })
    }
}