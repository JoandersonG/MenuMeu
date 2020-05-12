package joandersongoncalves.example.veganocook.presentation

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import joandersongoncalves.example.veganocook.R
import joandersongoncalves.example.veganocook.data.ApiService
import joandersongoncalves.example.veganocook.data.model.YouTubeVideo
import joandersongoncalves.example.veganocook.data.response.VideoBodyResponse
import kotlinx.android.synthetic.main.activity_create_recipe.*
import kotlinx.android.synthetic.main.app_toolbar.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateRecipeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_recipe)

        //setting the right toolbar for this activity
        viewFlipperAppToolbar.displayedChild = 1
        appToolbarOther.setTitle(R.string.add_recipe)
        AppToolbarSetup.setBackButton(appToolbarOther, this)

        btCreateRecipeCancel.setOnClickListener {
            // close activity
            finish()
        }
        getVideos()
    }

    fun getVideos() {
        ApiService.service.getVideos("go4DMa5-fZM",getString(R.string.youtube_api_key))
            .enqueue(object: Callback<VideoBodyResponse> {
                override fun onFailure(call: Call<VideoBodyResponse>, t: Throwable) {
                    Toast.makeText(applicationContext,"Erro ao conectar com API", Toast.LENGTH_SHORT).show()
                    println("Erro ao conectar à API")
                }

                override fun onResponse(
                    call: Call<VideoBodyResponse>,
                    response: Response<VideoBodyResponse>
                ) {
                    if (response.isSuccessful) {
                        var youTubeVideo = YouTubeVideo("titulo", "descrição", null, null)
                        response.body()?.let {videoBodyResponse ->
                            for (result in videoBodyResponse.itemsResult) { //for?
                                youTubeVideo = result.snippetVideoResponse.getVideoModel()
                            }
                        }
                        response.body()?.itemsResult?.get(0)?.snippetVideoResponse.toString()
                        println("video result is $youTubeVideo")
                    }
                    else {
                        println(call.isCanceled)
                        println("não rolou: ${response.errorBody()}")
                    }
                }

            })
    }
}
