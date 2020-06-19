package joandersongoncalves.example.veganocook.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import joandersongoncalves.example.veganocook.R
import joandersongoncalves.example.veganocook.data.model.Recipe
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

        fun bindView(recipe: Recipe) {
            resultTitle.text = recipe.name
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