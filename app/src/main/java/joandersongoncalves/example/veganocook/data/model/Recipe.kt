package joandersongoncalves.example.veganocook.data.model

data class Recipe(
    val videoLink: String,
    val categories: List<RecipeCategory>
)

enum class RecipeCategory {
    BREAKFAST,
    LUNCH,
    DINNER,
    SNACK
}