package joandersongoncalves.example.veganocook.data

import joandersongoncalves.example.veganocook.data.response.VideoBodyResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface YouTubeServices {
    @GET("videos")
    fun getVideos(
        @Query("id") idVideo: String = "go4DMa5-fZM",
        @Query("key") apiKey: String = "AIzaSyBtNj8tCjMb3lqRHNzN67KIAXHouknm_as",
        @Query("part") partResponses: String = "snippet",
        @Query("type") type: String = "video"
    ): Call<VideoBodyResponse>
}