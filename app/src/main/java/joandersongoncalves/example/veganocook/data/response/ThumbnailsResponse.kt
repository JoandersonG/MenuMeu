package joandersongoncalves.example.veganocook.data.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ThumbnailsResponse(
    val default: ThumbnailResponse,
    val medium: ThumbnailResponse
)
