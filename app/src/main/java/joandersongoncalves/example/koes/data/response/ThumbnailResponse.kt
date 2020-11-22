package joandersongoncalves.example.koes.data.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ThumbnailResponse(
    @Json(name = "url")
    val thumbnailUrl: String
)