package joandersongoncalves.example.veganocook.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import joandersongoncalves.example.veganocook.R
import joandersongoncalves.example.veganocook.data.model.YouTubeVideo
import kotlinx.android.synthetic.main.adapter_recipe.view.*

class RecipeAdapter(
    private val videos: List<YouTubeVideo>,
    private val onItemClickListener: ((youtubeVideo: YouTubeVideo) -> Unit)
) : RecyclerView.Adapter<RecipeAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.adapter_recipe, parent, false)
        return MyViewHolder(itemView, onItemClickListener)
    }

    override fun getItemCount(): Int {
        return videos.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindView(videos[position])
    }

    class MyViewHolder(
        itemView: View,
        private val onItemClickListener: ((youtubeVideo: YouTubeVideo) -> Unit)
    ) : RecyclerView.ViewHolder(itemView) {

        //        private val recipeImage = itemView.ivRecipe
        private val recipeName = itemView.tvRecipeName
//        private val layoutRecipeAdapter = itemView.layoutRecipeAdapter

        fun bindView(video: YouTubeVideo) {
            //recipeImage =
            recipeName.text = video.title

            itemView.setOnClickListener {
                onItemClickListener.invoke(video)
            }
        }

    }
}