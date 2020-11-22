package joandersongoncalves.example.koes.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import joandersongoncalves.example.koes.R
import joandersongoncalves.example.koes.data.model.Recipe
import kotlinx.android.synthetic.main.adapter_search_result.view.*

class SearchResultAdapter(
    private val onItemClickListener: ((recipe: Recipe) -> Unit)
) : RecyclerView.Adapter<SearchResultAdapter.MyViewHolder>() {

    private var recipes = emptyList<Recipe>()

    fun updateRecipes(recipes: List<Recipe>) {
        this.recipes = recipes
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_search_result, parent, false)
        return MyViewHolder(itemView, onItemClickListener)
    }

    override fun getItemCount(): Int {
        return recipes.size
        //return 15
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindView(recipes[position])
    }

    class MyViewHolder(
        itemView: View,
        private val onItemClickListener: ((recipe: Recipe) -> Unit)
    ) : RecyclerView.ViewHolder(itemView) {

        private val resultImage = itemView.resultImage
        private val resultTitle = itemView.resultTitle
        private val category1 = itemView.categoryOneAdapterRecipe
        private val category2 = itemView.categoryTwoAdapterRecipe
        private val category3 = itemView.categoryThreeAdapterRecipe

        fun bindView(recipe: Recipe) {
            resultTitle.text = recipe.name
            if (recipe.categories.isNotEmpty()) {
                category1.text = recipe.categories[0].categoryName
            } else {
                category1.visibility = View.GONE
            }
            if (recipe.categories.size >= 2) {
                category2.text = recipe.categories[1].categoryName
            } else {
                category2.visibility = View.GONE
            }
            if (recipe.categories.size >= 3) {
                category3.text = recipe.categories[2].categoryName
            } else {
                category3.visibility = View.GONE
            }
            Picasso
                .get()
                .load(recipe.video.mediumThumbnailUrl)
                .placeholder(R.drawable.placeholder)
                .into(resultImage)
            itemView.setOnClickListener {
                onItemClickListener.invoke(recipe)
            }
        }
    }
}