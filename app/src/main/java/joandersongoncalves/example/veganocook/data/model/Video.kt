package joandersongoncalves.example.veganocook.data.model

data class Video (
    val title: String,
    val description: String,
    val id: String?,
    val thumbnailUrl: String?
) {
    override fun toString(): String {
        return "title: " + title +
                "\ndescription: " + description
    }
}