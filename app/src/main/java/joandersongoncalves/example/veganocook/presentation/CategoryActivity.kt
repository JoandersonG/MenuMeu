package joandersongoncalves.example.veganocook.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import joandersongoncalves.example.veganocook.R
import joandersongoncalves.example.veganocook.data.model.Recipe
import joandersongoncalves.example.veganocook.presentation.adapter.RecipeAdapter
import joandersongoncalves.example.veganocook.presentation.viewmodel.CategoryViewModel
import kotlinx.android.synthetic.main.activity_category.*
import kotlinx.android.synthetic.main.app_toolbar.*

class CategoryActivity : AppCompatActivity() {

    private val viewModel: CategoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        // setting RecyclerView
        val recipeAdapter = RecipeAdapter { recipe ->
            val intent = RecipeDetailsActivity.getStartIntent(this@CategoryActivity, recipe)
            val reqCode = 1
            this@CategoryActivity.startActivityForResult(intent, reqCode)
        }
        with(rvRecipesByCategory) {
            layoutManager = GridLayoutManager(this@CategoryActivity, 2)
            setHasFixedSize(true)
            adapter = recipeAdapter
        }

        // setting viewModel
        intent.getStringExtra(EXTRA_CATEGORY_TITLE)?.let {
            val category = when (it) {
                getString(R.string.breakfest) -> Recipe.BREAKFAST
                getString(R.string.lunch) -> Recipe.LUNCH
                getString(R.string.dinner) -> Recipe.DINNER
                getString(R.string.snack) -> Recipe.SNACK
                else -> ""
            }
            viewModel.category = category
            viewModel.getCategoryWithRecipes()

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RecipeDetailsActivity.RETURN_UPDATED_RECIPE) {
            viewModel.getCategoryWithRecipes()
        }
        if (resultCode == RecipeDetailsActivity.DELETE_RECIPE) {
            if (data != null && data.hasExtra(RecipeDetailsActivity.RECIPE_TO_BE_DELETED)) {
                //delete the recipe received:
                viewModel.deleteRecipe(data.getParcelableExtra(RecipeDetailsActivity.RECIPE_TO_BE_DELETED)!!)
                //update the recipe list:
                viewModel.getCategoryWithRecipes()
                //snackbar confirming exclusion:
                Snackbar.make(
                    baseLayoutCategoryActivity,
                    R.string.success_deleting_recipe,
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
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
