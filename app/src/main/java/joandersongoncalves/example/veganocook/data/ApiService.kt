package joandersongoncalves.example.veganocook.data

import com.squareup.moshi.Moshi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object ApiService {
    private fun initRetrofit() : Retrofit {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/youtube/v3/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        return retrofit
    }

    val service: YouTubeServices = initRetrofit().create(YouTubeServices::class.java)
}