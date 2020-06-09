package joandersongoncalves.example.veganocook.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import joandersongoncalves.example.veganocook.data.RecipeRepository
import joandersongoncalves.example.veganocook.data.database.RecipeDatabase
import joandersongoncalves.example.veganocook.data.model.Recipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class CategoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RecipeRepository
    var recipesByCategory = MutableLiveData<List<Recipe>>()

    init {
        val recipeDao = RecipeDatabase.getDatabase(application, viewModelScope).recipeDao()
        repository = RecipeRepository(recipeDao)
    }

    fun getCategoryWithRecipes(category: String) = viewModelScope.launch(Dispatchers.IO) {
        if (category == "") { //all recipes
            recipesByCategory.postValue(repository.getAllRecipes())
        } else {
            recipesByCategory.postValue(repository.getRecipesByCategory(category))
        }
    }
}