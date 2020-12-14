package joandersongoncalves.example.menumeu.data

import joandersongoncalves.example.menumeu.data.response.VideoBodyResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface YouTubeServices {
    @GET("videos")
    fun getVideos(
        @Query("id") idVideo: String = "go4DMa5-fZM",
        @Query("key") apiKey: String = "api_key_here",
        @Query("part") partResponses: String = "snippet",
        @Query("type") type: String = "video"
    ): Call<VideoBodyResponse>
}