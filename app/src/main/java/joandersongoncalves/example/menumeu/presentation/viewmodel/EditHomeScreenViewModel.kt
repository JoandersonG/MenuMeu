package joandersongoncalves.example.menumeu.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import joandersongoncalves.example.menumeu.data.RecipeRepository
import joandersongoncalves.example.menumeu.data.database.RecipeDatabase
import joandersongoncalves.example.menumeu.data.model.Category
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditHomeScreenViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RecipeRepository
    val toolbarTitle = MutableLiveData<String>()
    val allCategories: MutableLiveData<List<Category>> by lazy {
        MutableLiveData<List<Category>>().also {
            getAllCategoriesFromDatabase()
        }
    }

    init {
        val recipeDao = RecipeDatabase.getDatabase(application, viewModelScope).recipeDao()
        repository = RecipeRepository(recipeDao)
        toolbarTitle.value = ""
    }

    private fun getAllCategoriesFromDatabase() = viewModelScope.launch(Dispatchers.IO) {
        allCategories.postValue(repository.getAllCategories())
    }

    fun updateCategory(category: Category) {
        val categoryFromAllCategories = allCategories.value?.indexOf(category)?.let {
            allCategories.value?.get(it)
        }
        categoryFromAllCategories?.isShowedOnHome = category.isShowedOnHome

    }

    fun saveAllCategoriesChanges() = viewModelScope.launch(Dispatchers.IO) {
        for (category in allCategories.value!!) {
            repository.updateCategory(category)
        }
    }

    fun setToolbarTitle(title: String) {
        toolbarTitle.value = title
    }
}