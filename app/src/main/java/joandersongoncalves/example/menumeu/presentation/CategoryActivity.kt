package joandersongoncalves.example.menumeu.presentation

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.core.view.size
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import joandersongoncalves.example.menumeu.R
import joandersongoncalves.example.menumeu.data.model.Category
import joandersongoncalves.example.menumeu.presentation.adapter.RecipeAdapter
import joandersongoncalves.example.menumeu.presentation.viewmodel.CategoryViewModel
import kotlinx.android.synthetic.main.activity_category.*
import kotlinx.android.synthetic.main.app_toolbar.*
import kotlinx.android.synthetic.main.recipe_filters.*

class CategoryActivity : AppCompatActivity() {

    private val viewModel: CategoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        val recipeAdapter = setRecyclerView()

        viewModelObserversSetup(recipeAdapter)

        getIntentExtra()

        appToolbarSetup()

        drawerSetup()
    }

    private fun appToolbarSetup() {
        viewFlipperAppToolbar.displayedChild = 1
        AppToolbarSetup.setBackButton(appToolbarGeneral, this)
    }

    private fun getIntentExtra() {
        intent.getStringExtra(AppConstantCodes.EXTRA_CATEGORY_TITLE)?.let {
            when (it) {
                getString(R.string.favorite) -> {
                    viewModel.isFavoriteRecipesOnly.value = true
                    viewModel.updateAllRecipes()
                }
                getString(R.string.all_recipes) -> {
                    viewModel.updateAllRecipes()
                }
                else -> {
                    viewModel.checkedChangeOnSelectedCategory(Category(it))
                }
            }
        }
    }

    private fun drawerSetup() {
        drawerLayoutCategoryActivity.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        //setting button Filters
        textOpenFilters.setOnClickListener {
            drawerLayoutCategoryActivity.openDrawer(drawerFilters)
            addAllCategoriesIntoChips()
        }

        //setting button Clean All
        drawerFilters
            .getHeaderView(0)
            .findViewById<Button>(R.id.btCleanSelectionFiltersDrawer)
            .setOnClickListener {
                //clean filter selection
                viewModel.cleanSelectedCategories()
                //recreate all category chips
                addAllCategoriesIntoChips()
            }
        drawerFilters
            .getHeaderView(0)
            .findViewById<LinearLayout>(R.id.layoutFavoriteFilter)
            .setOnClickListener {
                //checkBoxFavorite.isChecked = !checkBoxFavorite.isChecked
                viewModel.changeFavoriteRecipesOnly()
            }
        viewModel.getAllCategories()
    }

    private fun viewModelObserversSetup(recipeAdapter: RecipeAdapter) {
        viewModel.isFavoriteRecipesOnly.observe(this, {
            drawerFilters
                .getHeaderView(0)
                .findViewById<CheckBox>(R.id.checkBoxFavorite)
                .isChecked = it
        })
        viewModel.recipesByCategory.observe(this, Observer {
            recipeAdapter.setRecipes(it)
            if (it.isEmpty()) { //the results returned zero recipes
                viewFlipperCategoryActivity.displayedChild = 1
            } else { //results returned at least one recipe
                viewFlipperCategoryActivity.displayedChild = 0
            }
        })

        //setting activity title
        viewModel.categoryTitle.observe(this, Observer {
            tvAppToolbarOtherTitle.text = it
        })

        viewModel.selectedCategoriesOnFilter.observe(this, Observer {
            if (viewModel.isFavoriteRecipesOnly.value!!) {
                //this means is it should be favorite only, so show title Favorites
                viewModel.setToolbarTitle(getString(R.string.favorite))
            } else if (it.isEmpty() || it.size > 1) {
                //this means there is no selected categories on filter
                //or there is multiple selected categories, so show title All Recipes
                viewModel.setToolbarTitle(getString(R.string.all_recipes))
            } else {
                //ths means there is only one selected category, so show that category title
                viewModel.setToolbarTitle(it[0].categoryName)
            }
            //now filter categories
            viewModel.updateAllRecipes()
        })
    }

    private fun setRecyclerView(): RecipeAdapter {
        val recipeAdapter = RecipeAdapter({ recipe -> //onClick: item
            val intent = RecipeDetailsActivity.getStartIntent(this@CategoryActivity, recipe)
            val reqCode = 1
            this@CategoryActivity.startActivityForResult(intent, reqCode)
        }, { recipe -> //onClick: favorite button
            //update the database
            viewModel.toggleFavoriteRecipe(recipe)

            //show success toggling favorite
            Snackbar.make(
                baseLayoutCategoryActivity,
                if (recipe.isFavorite)
                    getString(R.string.added_to_favorites)
                else
                    getString(R.string.removed_from_favorites),
                Snackbar.LENGTH_SHORT
            ).show()
        })
        with(rvRecipesByCategory) {
            layoutManager = GridLayoutManager(this@CategoryActivity, 2)
            setHasFixedSize(true)
            adapter = recipeAdapter
        }
        return recipeAdapter
    }

    private fun addAllCategoriesIntoChips() {
        viewModel.allCategories.value?.let { allCategories ->
            flexboxLayoutRecipeFilters.removeAllViews()
            for (category in allCategories) {
                LayoutInflater.from(this).inflate(
                    R.layout.recipe_filter_category_chip,
                    flexboxLayoutRecipeFilters
                )
                val categoryId = flexboxLayoutRecipeFilters.size - 1
                val tvCategory = (flexboxLayoutRecipeFilters[categoryId] as TextView)
                tvCategory.apply {
                    text = category.categoryName
                    viewModel.selectedCategoriesOnFilter.value?.let { selectedCategories ->
                        background = if (selectedCategories.contains(category)) {
                            setTextColor(Color.WHITE)
                            getDrawable(R.drawable.primary_ripple_stroke_background)
                        } else {
                            setTextColor(Color.BLACK)
                            getDrawable(R.drawable.white_ripple_stroke_background)
                        }
                    }

                    setOnClickListener {
                        category.isShowedOnHome = !category.isShowedOnHome
                        viewModel.checkedChangeOnSelectedCategory(category)
                        viewModel.selectedCategoriesOnFilter.value?.let { selectedCategories ->
                            background = if (selectedCategories.contains(category)) {
                                setTextColor(Color.WHITE)
                                getDrawable(R.drawable.primary_ripple_stroke_background)
                            } else {
                                setTextColor(Color.BLACK)
                                getDrawable(R.drawable.white_ripple_stroke_background)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        if (drawerLayoutCategoryActivity.isDrawerOpen(drawerFilters)) {
            drawerLayoutCategoryActivity.closeDrawers()
        } else {
            super.onBackPressed()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppConstantCodes.RETURN_UPDATED_RECIPE) {
            viewModel.updateAllRecipes()
        }
        if (resultCode == AppConstantCodes.DELETE_RECIPE) {
            //snackbar confirming exclusion:
            Snackbar.make(
                baseLayoutCategoryActivity,
                R.string.success_deleting_recipe,
                Snackbar.LENGTH_SHORT
            ).show()
        }
        viewModel.getAllCategories() //updating categories for any possible updates
    }

    companion object {
        fun getStartActivity(context: Context, title: String): Intent {
            return Intent(context, CategoryActivity::class.java).apply {
                putExtra(AppConstantCodes.EXTRA_CATEGORY_TITLE, title)
            }
        }
    }
}
