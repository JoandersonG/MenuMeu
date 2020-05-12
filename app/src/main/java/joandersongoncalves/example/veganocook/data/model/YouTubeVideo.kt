package joandersongoncalves.example.veganocook.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class YouTubeVideo(
    val title: String,
    val description: String,
    val id: String?,
    val thumbnailUrl: String?
) : Parcelable {
    override fun toString(): String {
        return "title: " + title +
                "\ndescription: " + description
    }
}