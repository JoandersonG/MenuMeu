package joandersongoncalves.example.veganocook.data.model

import android.os.Parcelable
import androidx.room.*
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "recipes")
data class Recipe(
    @ColumnInfo(name = "recipe_id") @PrimaryKey(autoGenerate = true) var recipeId: Int,
    @ColumnInfo(name = "video_url") val videoUrl: String,
    val name: String,
    val description: String
) : Parcelable {
    @IgnoredOnParcel
    @Ignore
    var categories = listOf<String>()

    constructor(
        videoUrl: String,
        name: String,
        description: String,
        categories: List<String>
    ) : this(0, videoUrl, name, description) {
        this.categories = categories
    }

    override fun toString(): String {
        return "url: $videoUrl, name: $name, categories: $categories"
    }

    companion object {
        const val BREAKFAST = "BREAKFAST"
        const val LUNCH = "LUNCH"
        const val DINNER = "DINNER"
        const val SNACK = "SNACK"
    }
}

@Entity(tableName = "categories")
data class Category(
    @ColumnInfo(name = "category_name") @PrimaryKey val categoryName: String
)

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