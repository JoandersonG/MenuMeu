package joandersongoncalves.example.koes.data

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