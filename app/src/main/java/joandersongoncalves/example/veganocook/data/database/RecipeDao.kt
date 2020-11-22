package joandersongoncalves.example.veganocook.data.database

import androidx.room.*
import joandersongoncalves.example.veganocook.data.model.*

@Dao
abstract class RecipeDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertSearchEntryToDatabase(entry: SearchHistoryEntry)


    @Query("SELECT COUNT(entry_title) FROM search_history")
    abstract fun getAmountSearchEntries(): Int

    @Query("SELECT * FROM search_history ORDER BY creation_timestamp ASC LIMIT 1")
    abstract suspend fun getOldestSearchEntry(): SearchHistoryEntry

    @Delete
    abstract suspend fun deleteSearchHistoryEntry(entry: SearchHistoryEntry)

    @Query("DELETE FROM search_history WHERE entry_title = :name")
    abstract suspend fun deleteSearchHistoryEntryName(name: String)

    suspend fun insertSearchEntry(entry: SearchHistoryEntry) {
        //if there is already 3 entries, remove the oldest one and add new entry
        if (getAmountSearchEntries() >= 3) {
            val oldest = getOldestSearchEntry()
            deleteSearchHistoryEntry(oldest)
        }
        insertSearchEntryToDatabase(entry)
    }

    @Query("SELECT * FROM search_history ORDER BY creation_timestamp ASC limit 3")
    abstract suspend fun getPreviousSearchEntries(): List<SearchHistoryEntry>;

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertRecipe(recipe: Recipe)

    suspend fun insertRecipeWithCategories(recipe: Recipe) {
        insertRecipe(recipe)
        val recipeId = getLastInsertedRecipe()[0].recipeId
        for (category in recipe.categories) {
            insertRecipeCategoryCrossRef(RecipeCategoryCrossRef(recipeId, category.categoryName))
        }
    }

    suspend fun updateRecipeWithCategories(recipe: Recipe) {
        //deleting all categories - recipe associeation that envolves this recipe
        deleteAllRecipeCategoriesCrossRef(recipe.recipeId)
        updateRecipe(recipe)
        //reset all categories - recipe associations
        for (category in recipe.categories) {
            insertRecipeCategoryCrossRef(
                RecipeCategoryCrossRef(
                    recipe.recipeId,
                    category.categoryName
                )
            )
        }
    }

    @Query("SELECT * FROM recipes ORDER BY recipe_id DESC LIMIT 1 ")
    abstract suspend fun getLastInsertedRecipe(): List<Recipe>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertCategory(category: Category)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertRecipeCategoryCrossRef(value: RecipeCategoryCrossRef)

    @Update
    abstract suspend fun updateRecipe(recipe: Recipe)

    @Delete
    abstract suspend fun deleteRecipe(recipe: Recipe)

    @Query("DELETE FROM recipes")
    abstract suspend fun deleteAllRecipes()

    @Query("DELETE FROM recipes_categories")
    abstract suspend fun deleteAllRecipeCategoriesCrossRef()

    @Query("DELETE FROM recipes_categories WHERE recipe_id = :recipeId")
    abstract suspend fun deleteAllRecipeCategoriesCrossRef(recipeId: Int)

    suspend fun deleteAll() {
        deleteAllRecipes()
        deleteAllRecipeCategoriesCrossRef()
    }

    @Query("SELECT * FROM recipes")
    abstract suspend fun getAllRecipes(): List<Recipe>

    @Transaction
    @Query(
        "SELECT * FROM recipes " +
                "INNER JOIN recipes_categories ON recipes.recipe_id = recipes_categories.recipe_id " +
                "INNER JOIN categories ON recipes_categories.category_name = categories.category_name " +
                "WHERE categories.category_name = :category ORDER BY recipes.name ASC"
    )
    abstract suspend fun getRecipesByCategory(category: String): List<Recipe>

    @Transaction
    //@Query("SELECT * FROM categories WHERE category_name = :category LIMIT 3")
    @Query(
        "SELECT * FROM recipes " +
                "INNER JOIN recipes_categories ON recipes.recipe_id = recipes_categories.recipe_id " +
                "INNER JOIN categories ON recipes_categories.category_name = categories.category_name " +
                "WHERE categories.category_name = :category " +
                "LIMIT 3"
    )
    abstract suspend fun getThreeRecipes(category: String): List<Recipe>

    @Transaction
    @Query("SELECT * FROM recipes WHERE recipe_id = :recipeId")
    abstract suspend fun getRecipeWithCategories(recipeId: Int): List<RecipesWithCategories>

    @Transaction
    @Query(
        "SELECT * FROM categories " +
                "INNER JOIN recipes_categories " +
                "ON recipes_categories.category_name = categories.category_name " +
                "INNER JOIN recipes " +
                "ON recipes.recipe_id = recipes_categories.recipe_id " +
                "WHERE recipes.recipe_id = :recipeId LIMIT 3"
    )
    abstract suspend fun getThreeCategories(recipeId: Int): List<Category>

    @Query("SELECT * FROM recipes WHERE is_favorite = :isFavorite")
    abstract suspend fun getFavoriteRecipes(isFavorite: Boolean = true): List<Recipe>

    @Query("SELECT * FROM recipes WHERE name LIKE :q")
    abstract suspend fun queryByName(q: String): List<Recipe>

    @Query("SELECT * FROM categories")
    abstract suspend fun getAllCategories(): List<Category>

    @Delete
    abstract suspend fun deleteCategory(category: Category)

    @Query("SELECT * FROM categories WHERE is_showed_home = :isShowed")
    abstract suspend fun getCategoriesToShowOnHome(isShowed: Boolean = true): List<Category>

    suspend fun getRecipesToShow(): List<HomeRecipeSet> {
        //first find categories that are to be shown on home screen
        //then get 0-3 recipes from each category

        val homeRecipeSets = mutableListOf<HomeRecipeSet>()

        val categories = getCategoriesToShowOnHome()
        for (category in categories) {

            val threeRecipes = getThreeRecipes(category.categoryName)
            val recipe1 = if (threeRecipes.size >= 1) {
                threeRecipes[0]
            } else {
                null
            }
            val recipe2 = if (threeRecipes.size >= 2) {
                threeRecipes[1]
            } else {
                null
            }
            val recipe3 = if (threeRecipes.size >= 3) {
                threeRecipes[2]
            } else {
                null
            }
            if ((recipe1 == recipe2) && (recipe2 == recipe3) && (recipe3 == null)) {
                //all recipes are null, do not add this
                continue
            }
            val homeRecipeSet = HomeRecipeSet(category, recipe1, recipe2, recipe3)
            homeRecipeSets.add(homeRecipeSet)
        }

        return homeRecipeSets
    }

    @Update
    abstract suspend fun updateCategory(category: Category)
}