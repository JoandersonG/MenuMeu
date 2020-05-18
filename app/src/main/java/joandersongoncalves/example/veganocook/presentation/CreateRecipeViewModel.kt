package joandersongoncalves.example.veganocook.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import joandersongoncalves.example.veganocook.R
import joandersongoncalves.example.veganocook.data.ApiService
import joandersongoncalves.example.veganocook.data.model.Recipe
import joandersongoncalves.example.veganocook.data.model.RecipeCategory
import joandersongoncalves.example.veganocook.data.model.YouTubeVideo
import joandersongoncalves.example.veganocook.data.response.VideoBodyResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateRecipeViewModel : ViewModel() {

    val videoLiveData = MutableLiveData<YouTubeVideo>()
    val videoRetrieveResponseLiveData = MutableLiveData<Int>()//holds the errors, if any
    private val recipeCategories = mutableListOf<RecipeCategory>()

    fun getVideo(idVideo: String, apiKey: String) {
        ApiService.service.getVideos(idVideo, apiKey)
            .enqueue(object : Callback<VideoBodyResponse> {
                override fun onFailure(call: Call<VideoBodyResponse>, t: Throwable) {
                    videoRetrieveResponseLiveData.value = ERROR_CONECTING_API
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
                                //show the result
                                videoLiveData.value = youTubeVideo
                                videoRetrieveResponseLiveData.value = VIDEO_RETRIEVE_SUCCESS
                            } else {
                                //didn't find the video
                                videoRetrieveResponseLiveData.value = ERROR_INVALID_LINK
                            }
                        }
                    } else {
                        videoRetrieveResponseLiveData.value = ERROR_RETRIEVING_INFORMATION
                    }
                }

            })
    }

    fun chipCheckedChange(id: Int, checked: Boolean) {
        if (checked) {
            // add to the list
            when (id) {
                R.id.chipBreakfast -> {
                    recipeCategories.add(RecipeCategory.BREAKFAST)
                }
                R.id.chipLunch -> {
                    recipeCategories.add(RecipeCategory.BREAKFAST)
                }
                R.id.chipDinner -> {
                    recipeCategories.add(RecipeCategory.DINNER)
                }
                R.id.chipSnack -> {
                    recipeCategories.add(RecipeCategory.SNACK)
                }
            }
        } else {
            //remove from the list
            when (id) {
                R.id.chipBreakfast -> {
                    recipeCategories.remove(RecipeCategory.BREAKFAST)
                }
                R.id.chipLunch -> {
                    recipeCategories.remove(RecipeCategory.LUNCH)
                }
                R.id.chipDinner -> {
                    recipeCategories.remove(RecipeCategory.DINNER)
                }
                R.id.chipSnack -> {
                    recipeCategories.remove(RecipeCategory.SNACK)
                }
            }
        }

    }

    fun saveNewRecipe() {

        val recipe = videoLiveData.value?.let { it ->
            Recipe(it, recipeCategories)
        }

        // save created recipe on database:
        println("Recipe: $recipe")

    }

    companion object {
        const val VIDEO_RETRIEVE_SUCCESS = 0
        const val ERROR_INVALID_LINK = 1
        const val ERROR_RETRIEVING_INFORMATION = 2
        const val ERROR_CONECTING_API = 3
    }
}