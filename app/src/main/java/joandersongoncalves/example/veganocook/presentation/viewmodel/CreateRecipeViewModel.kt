package joandersongoncalves.example.veganocook.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import joandersongoncalves.example.veganocook.R
import joandersongoncalves.example.veganocook.data.ApiService
import joandersongoncalves.example.veganocook.data.RecipeRepository
import joandersongoncalves.example.veganocook.data.database.RecipeDatabase
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
    private val recipeCategories = mutableListOf<String>()

    private val repository: RecipeRepository

    init {
        val recipeDao = RecipeDatabase.getDatabase(application, viewModelScope).recipeDao()
        repository = RecipeRepository(recipeDao)
    }

    fun insert(recipe: Recipe) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(recipe)
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
                    recipeCategories.add(Recipe.BREAKFAST)
                }
                R.id.chipLunch -> {
                    recipeCategories.add(Recipe.LUNCH)
                }
                R.id.chipDinner -> {
                    recipeCategories.add(Recipe.DINNER)
                }
                R.id.chipSnack -> {
                    recipeCategories.add(Recipe.SNACK)
                }
            }
        } else {
            //remove from the list
            when (id) {
                R.id.chipBreakfast -> {
                    recipeCategories.remove(Recipe.BREAKFAST)
                }
                R.id.chipLunch -> {
                    recipeCategories.remove(Recipe.LUNCH)
                }
                R.id.chipDinner -> {
                    recipeCategories.remove(Recipe.DINNER)
                }
                R.id.chipSnack -> {
                    recipeCategories.remove(Recipe.SNACK)
                }
            }
        }

    }

    fun saveNewRecipe(name: String, description: String): Boolean {

        val recipe = videoLiveData.value?.let { it ->
            Recipe(it.url, name, description, recipeCategories.toList())
        }

        // save created recipe on database:
        recipe?.let {
            insert(recipe)
            return true
        }
        return false
    }

    companion object {
        const val VIDEO_RETRIEVE_SUCCESS = 0
        const val ERROR_INVALID_LINK = 1
        const val ERROR_RETRIEVING_INFORMATION = 2
        const val ERROR_CONECTING_API = 3
    }
}