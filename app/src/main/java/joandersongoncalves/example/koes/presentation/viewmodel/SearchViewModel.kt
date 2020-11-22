package joandersongoncalves.example.koes.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import joandersongoncalves.example.koes.data.RecipeRepository
import joandersongoncalves.example.koes.data.database.RecipeDatabase
import joandersongoncalves.example.koes.data.model.Recipe
import joandersongoncalves.example.koes.data.model.SearchHistoryEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    val recipesResult = MutableLiveData<List<Recipe>>()
    val query = MutableLiveData<String>()
    val previousSearchEntries = MutableLiveData<List<String>>()

    private val repository: RecipeRepository

    init {
        val recipeDao = RecipeDatabase.getDatabase(application, viewModelScope).recipeDao()
        repository = RecipeRepository(recipeDao)
        recipesResult.value = mutableListOf()
        previousSearchEntries.value = listOf()
        query.value = ""
    }

    fun getPreviousSearchEntries() = viewModelScope.launch(Dispatchers.IO) {
        previousSearchEntries.postValue(repository.getPreviousSearchEntries())
    }

    private fun saveQueryIntoSearchHistory() {
        if (query.value == "") {
            //not saving empty string
            return
        }
        val shEntry = SearchHistoryEntry(query.value!!, System.currentTimeMillis())
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertSearchHistoryEntry(shEntry)
        }
    }

    fun searchForResults() = viewModelScope.launch(Dispatchers.IO) {
        query.value?.let {
            val results = repository.queryByName(it)
            for (r in results) {
                r.categories = repository.getThreeCategories(r.recipeId)
            }
            recipesResult.postValue(results)
            saveQueryIntoSearchHistory()
        }
    }

    fun deleteSearchHistoryEntry(entry: String) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteSearchHistoryEntryByName(entry);
        getPreviousSearchEntries();
    }
}