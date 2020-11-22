package joandersongoncalves.example.koes.data.model

//set of three recipes and one category they all make part of
data class HomeRecipeSet(
    val category: Category,
    val recipe1: Recipe?,
    val recipe2: Recipe?,
    val recipe3: Recipe?
) {
    override fun toString(): String {
        return "category: $category \n recipe1: $recipe1 \n recipe2: $recipe2 \n recipe3: $recipe3"
    }
}