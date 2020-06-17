package joandersongoncalves.example.veganocook.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import joandersongoncalves.example.veganocook.R
import joandersongoncalves.example.veganocook.data.ApiService
import joandersongoncalves.example.veganocook.data.RecipeRepository
import joandersongoncalves.example.veganocook.data.database.RecipeDatabase
import joandersongoncalves.example.veganocook.data.model.Category
import joandersongoncalves.example.veganocook.data.model.Recipe
import joandersongoncalves.example.veganocook.data.model.YouTubeVideo
import joandersongoncalves.example.veganocook.data.response.VideoBodyResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateRecipeViewModel(application: Application) : AndroidViewModel(application) {

    val videoLiveData = MutableLiveData<YouTubeVideo>()
    val videoRetrieveResponseLiveData = MutableLiveData<Int>()//holds the errors, if any
    val recipeCategories = MutableLiveData<MutableList<Category>>()

    private val repository: RecipeRepository

    init {
        val recipeDao = RecipeDatabase.getDatabase(application, viewModelScope).recipeDao()
        repository = RecipeRepository(recipeDao)
        recipeCategories.value = mutableListOf()
    }

    private fun insert(recipe: Recipe) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(recipe)
    }

    fun getCategoriesFromRecipe(recipe: Recipe) = viewModelScope.launch(Dispatchers.IO) {
//        val recipeId = repository.getRecipeId(recipe)
        recipeCategories.postValue(
            repository.getRecipeWithCategories(recipe.recipeId).toMutableList()
        )
    }

    fun getVideo(idVideo: String, apiKey: String) {
        ApiService.service.getVideos(idVideo, apiKey)
            .enqueue(object : Callback<VideoBodyResponse> {
                override fun onFailure(call: Call<VideoBodyResponse>, t: Throwable) {
                    videoRetrieveResponseLiveData.value =
                        ERROR_CONECTING_API
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

    fun chipCheckedChange(id: Int, checked: Boolean) {
        if (checked) {
            // add to the list
            when (id) {
                R.id.chipBreakfast -> {
                    recipeCategories.value?.add(Category(Recipe.BREAKFAST))
                }
                R.id.chipLunch -> {
                    recipeCategories.value?.add(Category(Recipe.LUNCH))
                }
                R.id.chipDinner -> {
                    recipeCategories.value?.add(Category(Recipe.DINNER))
                }
                R.id.chipSnack -> {
                    recipeCategories.value?.add(Category(Recipe.SNACK))
                }
            }
        } else {
            //remove from the list
            when (id) {
                R.id.chipBreakfast -> {
                    recipeCategories.value?.let {
                        for (category in it.toList()) {
                            if (category == Category(Recipe.BREAKFAST)) {
                                it.remove(category)
                            }
                        }
                    }
                }
                R.id.chipLunch -> {
                    recipeCategories.value?.let {
                        for (category in it.toList()) {
                            if (category == Category(Recipe.LUNCH)) {
                                it.remove(category)
                            }
                        }
                    }
                }
                R.id.chipDinner -> {
                    recipeCategories.value?.let {
                        for (category in it.toList()) {
                            if (category == Category(Recipe.DINNER)) {
                                it.remove(category)
                            }
                        }
                    }
                }
                R.id.chipSnack -> {
                    recipeCategories.value?.let {
                        for (category in it.toList()) {
                            if (category == Category(Recipe.SNACK)) {
                                it.remove(category)
                            }
                        }
                    }
                }
            }
        }

    }

    fun saveNewRecipe(name: String, description: String): Boolean {

        val recipe = videoLiveData.value?.let { video ->
            recipeCategories.value?.toList()?.let { listCategories ->
                Recipe(video.url, name, description, listCategories)
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
                recipe.videoUrl = it.url
            }
            recipeCategories.value?.let {
                recipe.categories = it.toList()
            }
            repository.updateRecipe(recipe)
        }
        return recipe
    }

    companion object {
        const val VIDEO_RETRIEVE_SUCCESS = 0
        const val ERROR_INVALID_LINK = 1
        const val ERROR_RETRIEVING_INFORMATION = 2
        const val ERROR_CONECTING_API = 3
    }
}