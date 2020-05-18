package joandersongoncalves.example.veganocook.data.model

data class Recipe(
    val video: YouTubeVideo,
    val categories: List<RecipeCategory>
)

enum class RecipeCategory {
    BREAKFAST,
    LUNCH,
    DINNER,
    SNACK
}