package joandersongoncalves.example.veganocook.data.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VideosResponse (
    val id: String,
    @Json(name = "snippet")
    val snippetVideoResponse : SnippetVideoResponse
)
