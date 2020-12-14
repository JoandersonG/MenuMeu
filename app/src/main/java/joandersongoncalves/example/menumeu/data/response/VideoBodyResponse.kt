package joandersongoncalves.example.menumeu.data.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VideoBodyResponse (
    @Json(name = "items")
    val itemsResult: List<VideosResponse>
)