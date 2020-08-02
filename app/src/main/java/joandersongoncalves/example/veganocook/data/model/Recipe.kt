package joandersongoncalves.example.veganocook.data.model

import android.os.Parcelable
import androidx.room.*
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "recipes")
data class Recipe(
    @ColumnInfo(name = "recipe_id") @PrimaryKey(autoGenerate = true) var recipeId: Int,
    @Embedded var video: YouTubeVideo,
    var name: String,
    var description: String,
    @ColumnInfo(name = "is_favorite")
    var isFavorite: Boolean
) : Parcelable {
    @IgnoredOnParcel
    @Ignore
    var categories = listOf<Category>()

    constructor(
        video: YouTubeVideo,
        name: String,
        description: String,
        favorite: Boolean,
        categories: List<Category>
    ) : this(0, video, name, description, favorite) {
        this.categories = categories
    }

    override fun toString(): String {
        return "url: ${video.url}, name: $name, categories: $categories"
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }
        if (other::class.java != Recipe::class.java) {
            return false
        }
        return recipeId == (other as Recipe).recipeId
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }

    companion object {
        const val BREAKFAST = "BREAKFAST"
        const val LUNCH = "LUNCH"
        const val DINNER = "DINNER"
        const val SNACK = "SNACK"
    }
}

@Parcelize
@Entity(tableName = "categories")
data class Category(
    @ColumnInfo(name = "category_name") @PrimaryKey val categoryName: String,
    @ColumnInfo(name = "is_showed_home") val isShowedOnHome: Boolean = false
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other.javaClass != this.javaClass) return false
        return (other as Category).categoryName == this.categoryName
    }

    override fun hashCode(): Int {
        return categoryName.hashCode()
    }
}

@Entity(primaryKeys = ["recipe_id", "category_name"], tableName = "recipes_categories")
data class RecipeCategoryCrossRef(
    @ColumnInfo(name = "recipe_id") val recipeId: Int,
    @ColumnInfo(name = "category_name") val categoryName: String
)

data class CategoryWithRecipes(
    @Embedded val category: Category,
    @Relation(
        parentColumn = "category_name",
        entityColumn = "recipe_id",
        associateBy = Junction(RecipeCategoryCrossRef::class)
    )
    val recipes: List<Recipe>
)

data class RecipesWithCategories(
    @Embedded val recipe: Recipe,
    @Relation(
        parentColumn = "recipe_id",
        entityColumn = "category_name",
        associateBy = Junction(RecipeCategoryCrossRef::class)
    )
    val categories: List<Category>
)