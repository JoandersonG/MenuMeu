package joandersongoncalves.example.veganocook.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import joandersongoncalves.example.veganocook.R
import joandersongoncalves.example.veganocook.data.model.Recipe
import joandersongoncalves.example.veganocook.presentation.adapter.RecipeAdapter
import joandersongoncalves.example.veganocook.presentation.viewmodel.CategoryViewModel
import kotlinx.android.synthetic.main.activity_category.*
import kotlinx.android.synthetic.main.app_toolbar.*

class CategoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        // setting RecyclerView
        val recipeAdapter = RecipeAdapter { recipe ->
            val intent = RecipeDetailsActivity.getStartIntent(this@CategoryActivity, recipe)
            this@CategoryActivity.startActivity(intent)
        }
        with(rvRecipesByCategory) {
            layoutManager = GridLayoutManager(this@CategoryActivity, 2)
            setHasFixedSize(true)
            adapter = recipeAdapter
        }

        // setting viewModel
        val viewModel: CategoryViewModel by viewModels()

        intent.getStringExtra(EXTRA_CATEGORY_TITLE)?.let {
            val category = when (it) {
                getString(R.string.breakfest) -> Recipe.BREAKFAST
                getString(R.string.lunch) -> Recipe.LUNCH
                getString(R.string.dinner) -> Recipe.DINNER
                getString(R.string.snack) -> Recipe.SNACK
                else -> ""
            }
            viewModel.getCategoryWithRecipes(category)

        }

        viewModel.recipesByCategory.observe(this, Observer {
            it?.let {
                recipeAdapter.setRecipes(it)
            }
        })

        //setting the right toolbar for this activity
        viewFlipperAppToolbar.displayedChild = 1
        appToolbarOther.title = intent.getStringExtra(EXTRA_CATEGORY_TITLE)
        AppToolbarSetup.setBackButton(appToolbarOther, this)
    }

    companion object {
        private const val EXTRA_CATEGORY_TITLE = "EXTRA_CATEGORY_TITLE"

        fun getStartActivity(context: Context, title: String): Intent {
            return Intent(context, CategoryActivity::class.java).apply {
                putExtra(EXTRA_CATEGORY_TITLE, title)
            }
        }
    }
}
