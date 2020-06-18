package joandersongoncalves.example.veganocook.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import kotlinx.android.parcel.Parcelize

@Parcelize
data class YouTubeVideo(
    @ColumnInfo(name = "video_title")
    val title: String,
    @ColumnInfo(name = "video_description")
    val description: String,
    @ColumnInfo(name = "video_url")
    var url: String,
    @ColumnInfo(name = "default_thumbnail_url")
    var defaultThumbnailUrl: String?,
    @ColumnInfo(name = "medium_thumbnail_url")
    var mediumThumbnailUrl: String?
) : Parcelable {
    override fun toString(): String {
        return "title: " + title +
                "\ndescription: " + description
    }
}