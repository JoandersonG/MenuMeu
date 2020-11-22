package joandersongoncalves.example.koes.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import joandersongoncalves.example.koes.R
import joandersongoncalves.example.koes.data.model.Category
import joandersongoncalves.example.koes.data.model.Recipe
import kotlinx.android.synthetic.main.adapter_recipe.view.*

class RecipeAdapter(
    private val onItemClickListener: ((recipe: Recipe) -> Unit),
    private val onFavoriteToggleClickListener: ((recipe: Recipe) -> Unit)
) : RecyclerView.Adapter<RecipeAdapter.MyViewHolder>() {

    private var recipes = emptyList<Recipe>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.adapter_recipe, parent, false)
        return MyViewHolder(itemView, onItemClickListener, onFavoriteToggleClickListener)
    }

    override fun getItemCount(): Int {
        return recipes.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindView(recipes[position])
    }

    internal fun setRecipes(recipes: List<Recipe>) {
        this.recipes = recipes
        notifyDataSetChanged()
    }

    class MyViewHolder(
        itemView: View,
        private val onItemClickListener: ((recipe: Recipe) -> Unit),
        private val onFavoriteToggleClickListener: ((recipe: Recipe) -> Unit)
    ) : RecyclerView.ViewHolder(itemView) {

        private val recipeImage = itemView.ivRecipe
        private val recipeName = itemView.tvRecipeName
        private val category1 = itemView.categoryOneAdapterRecipe
        private val category2 = itemView.categoryTwoAdapterRecipe
        private val toggleFavorite = itemView.btToggleFavoriteCategoryAdapter

        fun bindView(recipe: Recipe) {
            recipeName.text = recipe.name

            setRecipeImageToView(recipe)

            //setting click action on adapter
            itemView.setOnClickListener {
                onItemClickListener.invoke(recipe)
            }

            setFavoriteToggleButton(recipe)

            setCategoriesOnView(recipe)
        }

        private fun setCategoriesOnView(recipe: Recipe) {
            if (!recipe.categories.isNullOrEmpty()) {
                if (recipe.categories.size > 1) {
                    addCategoryToView(recipe.categories[0], recipe.categories[1])
                } else {
                    addCategoryToView(recipe.categories[0], null)
                }
            }
        }

        private fun setRecipeImageToView(recipe: Recipe) {
            Picasso
                .get()
                .load(recipe.video.mediumThumbnailUrl)
                .placeholder(R.drawable.placeholder)
                .into(recipeImage)
        }

        /*
        method responsible for setting functionality and state of favorite button on adapter view
         */
        private fun setFavoriteToggleButton(recipe: Recipe) {
            //setting start state
            if (recipe.isFavorite) {
                toggleFavorite.setImageResource(R.drawable.ic_favorite_filled_24dp)
            } else {
                toggleFavorite.setImageResource(R.drawable.ic_favorite_unchecked_24dp)
            }

            //setting onClick changing favorite state
            toggleFavorite.setOnClickListener {
                recipe.isFavorite = !recipe.isFavorite
                if (recipe.isFavorite) {
                    //remove from favorite
                    toggleFavorite.setImageResource(R.drawable.ic_favorite_unchecked_24dp)
                } else {
                    //add to favorite
                    toggleFavorite.setImageResource(R.drawable.ic_favorite_filled_24dp)
                }
                onFavoriteToggleClickListener.invoke(recipe)
            }
        }

        /*
        method for adding until two categories to adapter view
        */
        private fun addCategoryToView(category1: Category, category2: Category?) {
            this.category1.text = category1.categoryName
            if (category2 != null) {
                this.category2.text = category2.categoryName
            } else {
                this.category2.visibility = View.GONE
            }
        }

    }
}