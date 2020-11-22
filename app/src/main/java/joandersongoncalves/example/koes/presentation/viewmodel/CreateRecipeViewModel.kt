package joandersongoncalves.example.koes.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import joandersongoncalves.example.koes.data.ApiService
import joandersongoncalves.example.koes.data.RecipeRepository
import joandersongoncalves.example.koes.data.database.RecipeDatabase
import joandersongoncalves.example.koes.data.model.Category
import joandersongoncalves.example.koes.data.model.Recipe
import joandersongoncalves.example.koes.data.model.YouTubeVideo
import joandersongoncalves.example.koes.data.response.VideoBodyResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateRecipeViewModel(application: Application) : AndroidViewModel(application) {

    val videoLiveData = MutableLiveData<YouTubeVideo>()
    val videoRetrieveResponseLiveData = MutableLiveData<Int>()//holds the errors, if any
    val recipeCategories = MutableLiveData<List<Category>>()
    val favorite = MutableLiveData<Boolean>()
    val allCategories = MutableLiveData<List<Category>>()
    val selectedCategoriesOnDialog = MutableLiveData<MutableList<Category>>()
    val newCategoryField = MutableLiveData<String>()

    private val repository: RecipeRepository

    init {
        val recipeDao = RecipeDatabase.getDatabase(application, viewModelScope).recipeDao()
        repository = RecipeRepository(recipeDao)
        recipeCategories.value = listOf()
        selectedCategoriesOnDialog.value = mutableListOf()
        favorite.value = false
    }

    private fun insert(recipe: Recipe) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(recipe)
    }

    fun getCategoriesFromRecipe(recipe: Recipe) = viewModelScope.launch(Dispatchers.IO) {
        recipeCategories.postValue(
            repository.getRecipeWithCategories(recipe.recipeId)
        )
    }

    fun getVideo(idVideo: String, apiKey: String) {
        ApiService.service.getVideos(idVideo, apiKey)
            .enqueue(object : Callback<VideoBodyResponse> {
                override fun onFailure(call: Call<VideoBodyResponse>, t: Throwable) {
                    videoRetrieveResponseLiveData.value =
                        ERROR_CONNECTING_API
                }

                override fun onResponse(
                    call: Call<VideoBodyResponse>,
                    response: Response<VideoBodyResponse>
                ) {
                    if (response.isSuccessful) {
                        var youTubeVideo: YouTubeVideo
                        response.body()?.let { videoBodyResponse ->
                            if (videoBodyResponse.itemsResult.isNotEmpty()) {
                                //get the result
                                youTubeVideo =
                                    videoBodyResponse.itemsResult[0].snippetVideoResponse.getVideoModel()
                                youTubeVideo.url = videoBodyResponse.itemsResult[0].id
                                //show the result
                                videoLiveData.value = youTubeVideo
                                videoRetrieveResponseLiveData.value =
                                    VIDEO_RETRIEVE_SUCCESS
                            } else {
                                //didn't find the video
                                videoRetrieveResponseLiveData.value =
                                    ERROR_INVALID_LINK
                            }
                        }
                    } else {
                        videoRetrieveResponseLiveData.value =
                            ERROR_RETRIEVING_INFORMATION
                    }
                }

            })
    }

    fun saveNewRecipe(name: String, description: String): Boolean {

        val recipe = videoLiveData.value?.let { video ->
            recipeCategories.value?.toList()?.let { listCategories ->
                Recipe(video, name, description, favorite.value!!, listCategories)
            }
        }

        // save created recipe on database:
        recipe?.let {
            insert(recipe)
            return true
        }
        return false
    }

    fun updateRecipe(name: String, description: String, recipe: Recipe): Recipe {

        viewModelScope.launch(Dispatchers.IO) {
            recipe.name = name
            recipe.description = description
            videoLiveData.value?.let {
                recipe.video = it
            }
            recipeCategories.value?.let {
                recipe.categories = it
            }
            favorite.value?.let {
                recipe.isFavorite = it
            }
            repository.updateRecipe(recipe)
        }
        return recipe
    }

    fun changeFavoriteState() {
        favorite.value?.let {
            favorite.value = !it
        }
    }

    fun removeCategoryFromSelected(category: Category) {
        recipeCategories.value?.let {
            val listCategories: MutableList<Category> = it.toMutableList()
            listCategories.remove(category)
            recipeCategories.value = listCategories
        }
    }

    fun addCategoryIntoSelected(category: Category) {
        recipeCategories.value?.let {
            val listCategories: MutableList<Category> = it.toMutableList()
            listCategories.add(category)
            recipeCategories.value = listCategories
        }
    }

    fun changeSelectionOnCategoryOnDialog(category: Category) {
        selectedCategoriesOnDialog.value?.let {
            if (it.contains(category)) {
                it.remove(category)
            } else {
                it.add(category)
            }
        }
    }

    fun saveNewCategory(category: Category) = viewModelScope.launch(Dispatchers.IO) {
        repository.saveNewCategory(category)
        getAllCategories()
    }

    fun getAllCategories() = viewModelScope.launch(Dispatchers.IO) {
        allCategories.postValue(repository.getAllCategories().toMutableList())
    }

    fun deleteCategory(category: Category) = viewModelScope.launch(Dispatchers.IO) {
        println("before deleting $category selectedCategoriesOnDialog is ${selectedCategoriesOnDialog.value}")
        repository.deleteCategory(category)
        selectedCategoriesOnDialog.value?.remove(category)
        val newList = recipeCategories.value?.toMutableList()
        newList?.remove(category)
        recipeCategories.postValue(newList)
        getAllCategories()
        println("after deleting $category selectedCategoriesOnDialog is ${selectedCategoriesOnDialog.value}")
    }

    fun cleanSelectedCategoriesOnDialog() {
        selectedCategoriesOnDialog.value = mutableListOf()
    }

    fun saveSelectedCategoriesOnDialog() {
        recipeCategories.value = selectedCategoriesOnDialog.value
        cleanSelectedCategoriesOnDialog()
    }

    fun addToSelectedCategoriesOnDialog(category: Category) {
        //avoid possible duplicates
        selectedCategoriesOnDialog.value?.let {
            if (!it.contains(category)) {
                it.add(category)
            }
        }
    }

    companion object {
        const val VIDEO_RETRIEVE_SUCCESS = 0
        const val ERROR_INVALID_LINK = 1
        const val ERROR_RETRIEVING_INFORMATION = 2
        const val ERROR_CONNECTING_API = 3
    }
}