package joandersongoncalves.example.veganocook.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import joandersongoncalves.example.veganocook.R
import joandersongoncalves.example.veganocook.data.model.Recipe
import kotlinx.android.synthetic.main.adapter_recipe.view.*

class RecipeAdapter(
    private val onItemClickListener: ((recipe: Recipe) -> Unit)
) : RecyclerView.Adapter<RecipeAdapter.MyViewHolder>() {

    private var recipes = emptyList<Recipe>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.adapter_recipe, parent, false)
        return MyViewHolder(itemView, onItemClickListener)
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
        private val onItemClickListener: ((recipe: Recipe) -> Unit)
    ) : RecyclerView.ViewHolder(itemView) {

        private val recipeImage = itemView.ivRecipe
        private val recipeName = itemView.tvRecipeName
//        private val layoutRecipeAdapter = itemView.layoutRecipeAdapter

        fun bindView(recipe: Recipe) {
            recipeName.text = recipe.name
            Picasso
                .get()
                .load(recipe.video.mediumThumbnailUrl)
                .placeholder(R.drawable.placeholder)
                .into(recipeImage)
            itemView.setOnClickListener {
                onItemClickListener.invoke(recipe)
            }
        }

    }
}