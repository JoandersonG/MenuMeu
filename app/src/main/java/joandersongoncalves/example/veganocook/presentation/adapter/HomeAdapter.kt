package joandersongoncalves.example.veganocook.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import joandersongoncalves.example.veganocook.R
import joandersongoncalves.example.veganocook.data.model.Category
import joandersongoncalves.example.veganocook.data.model.HomeRecipeSet
import joandersongoncalves.example.veganocook.data.model.Recipe
import kotlinx.android.synthetic.main.home_category_adapter.view.*

class HomeAdapter(
    private val onRecipeClickListener: ((recipe: Recipe) -> Unit),
    private val onCategoryClickListener: ((category: Category) -> Unit)
) : RecyclerView.Adapter<HomeAdapter.MyViewHolder>() {

    private var recipes = emptyList<HomeRecipeSet>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.home_category_adapter, parent, false)
        return MyViewHolder(itemView, onRecipeClickListener, onCategoryClickListener)
    }

    override fun getItemCount(): Int {
        return recipes.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindView(recipes[position])
    }

    internal fun setRecipes(recipes: List<HomeRecipeSet>) {
        this.recipes = recipes
        notifyDataSetChanged()
    }

    class MyViewHolder(
        itemView: View,
        private val onRecipeClickListener: ((recipe: Recipe) -> Unit),
        private val onCategoryClickListener: ((category: Category) -> Unit)
    ) : RecyclerView.ViewHolder(itemView) {

        private val recipeImage1 = itemView.ivRecipeOneHomeAdapter
        private val recipeImage2 = itemView.ivRecipeTwoHomeAdapter
        private val recipeImage3 = itemView.ivRecipeThreeHomeAdapter

        private val recipeTitle1 = itemView.tvRecipeOneTitleHomeAdapter
        private val recipeTitle2 = itemView.tvRecipeTwoTitleHomeAdapter
        private val recipeTitle3 = itemView.tvRecipeThreeTitleHomeAdapter

        private val categoryTitle = itemView.tvCategoryTitleHomeAdapter

        private val categoryLayout = itemView.layoutCategoryTitleHomeAdapter

        private val layout1 = itemView.layoutRecipeOneHomeAdapter
        private val layout2 = itemView.layoutRecipeTwoHomeAdapter
        private val layout3 = itemView.layoutRecipeThreeHomeAdapter

        fun bindView(homeRecipeSet: HomeRecipeSet) {

            //setting layout clicks
            layout1.setOnClickListener {
                homeRecipeSet.recipe1?.let { it1 -> onRecipeClickListener.invoke(it1) }
            }
            layout2.setOnClickListener {
                homeRecipeSet.recipe2?.let { it1 -> onRecipeClickListener.invoke(it1) }
            }
            layout3.setOnClickListener {
                homeRecipeSet.recipe3?.let { it1 -> onRecipeClickListener.invoke(it1) }
            }
            categoryLayout.setOnClickListener {
                onCategoryClickListener.invoke(homeRecipeSet.category)
            }


            categoryTitle.text = homeRecipeSet.category.categoryName

            if (homeRecipeSet.recipe1 != null) { //has one recipe
                recipeTitle1.text = homeRecipeSet.recipe1.name
                Picasso
                    .get()
                    .load(homeRecipeSet.recipe1.video.mediumThumbnailUrl)
                    .placeholder(R.drawable.placeholder)
                    .into(recipeImage1)
            } else {
                //remove this view
                layout1.visibility = View.GONE
            }
            if (homeRecipeSet.recipe2 != null) { //has two recipes
                recipeTitle2.text = homeRecipeSet.recipe2.name
                Picasso
                    .get()
                    .load(homeRecipeSet.recipe2.video.mediumThumbnailUrl)
                    .placeholder(R.drawable.placeholder)
                    .into(recipeImage2)
            } else {
                layout2.visibility = View.GONE
            }
            if (homeRecipeSet.recipe3 != null) { //has three recipes
                recipeTitle3.text = homeRecipeSet.recipe3.name
                Picasso
                    .get()
                    .load(homeRecipeSet.recipe3.video.mediumThumbnailUrl)
                    .placeholder(R.drawable.placeholder)
                    .into(recipeImage3)
            } else {
                layout3.visibility = View.GONE
            }
        }
    }
}