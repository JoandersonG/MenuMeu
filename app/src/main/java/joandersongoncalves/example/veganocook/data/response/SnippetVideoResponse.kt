package joandersongoncalves.example.veganocook.data.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import joandersongoncalves.example.veganocook.data.model.Video

@JsonClass(generateAdapter = true)
data class SnippetVideoResponse (
    @Json(name = "title")
    val title: String,
    @Json(name = "description")
    val description: String
) {

    override fun toString(): String {
        return "nome é " + title + "\ndescrição é " + description
    }

    fun getVideoModel() = Video(
        title = this.title,
        description = this.description,
        id = null,
        thumbnailUrl = null)

}
