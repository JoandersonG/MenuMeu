package joandersongoncalves.example.veganocook.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import joandersongoncalves.example.veganocook.R
import joandersongoncalves.example.veganocook.data.ApiService
import joandersongoncalves.example.veganocook.data.model.Video
import joandersongoncalves.example.veganocook.data.response.VideoBodyResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateRecipeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_recipe)
    }

    fun getVideos() {
        println("entrou no get videos")
        ApiService.service.getVideos("go4DMa5-fZM")
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
                        var video: Video = Video("titulo", "descrição",null, null)
                        response.body()?.let {videoBodyResponse ->
                            for (result in videoBodyResponse.itemsResult) { //for?
                                video = result.snippetVideoResponse.getVideoModel()
                            }
                        }
                        response.body()?.itemsResult?.get(0)?.snippetVideoResponse.toString()
                        println("video result is " + video)
                    }
                    else {
                        println(call.isCanceled)
                        println("não rolou: ${response.errorBody()}")
                    }
                }

            })
    }
}
