package joandersongoncalves.example.koes.data.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import joandersongoncalves.example.koes.data.model.YouTubeVideo

@JsonClass(generateAdapter = true)
data class SnippetVideoResponse (
    @Json(name = "title")
    val title: String,
    @Json(name = "description")
    val description: String,
    val thumbnails: ThumbnailsResponse
) {

    override fun toString(): String {
        return "nome é $title\ndescrição é $description"
    }

    fun getVideoModel() = YouTubeVideo(
        title = this.title,
        description = this.description,
        url = "urlHere",
        defaultThumbnailUrl = thumbnails.default.thumbnailUrl,
        mediumThumbnailUrl = thumbnails.medium.thumbnailUrl
    )

}
