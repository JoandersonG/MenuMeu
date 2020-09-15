package joandersongoncalves.example.veganocook.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.lifecycle.Observer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayerSupportFragment
import joandersongoncalves.example.veganocook.R
import joandersongoncalves.example.veganocook.data.model.Category
import joandersongoncalves.example.veganocook.data.model.Recipe
import joandersongoncalves.example.veganocook.presentation.viewmodel.RecipeDetailsViewModel
import kotlinx.android.synthetic.main.activity_recipe_details.*
import kotlinx.android.synthetic.main.app_toolbar.*
import kotlinx.android.synthetic.main.include_recipe_details_video_description.*
import java.io.Serializable


class RecipeDetailsActivity : AppCompatActivity() {

    private val viewModel: RecipeDetailsViewModel by viewModels()

    private var recipe: Recipe? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_details)

        retrieveDataFromParentActivity()

        settingViewModel()

        runYouTubePlayer()

        settingToolbar()

        settingShowAndHideDescription()
    }

    private fun settingViewModel() {
        viewModel.recipe.observe(this, Observer {
            updateFields(it)
        })
        viewModel.recipe.value = recipe
    }

    private fun isShowingAllVideoDescriptionOnView(): Boolean {
        return tvVideoDescriptionRecipeDetails.maxLines != 5
    }

    private fun settingShowAndHideDescription() {

        btSeeHideDescriptionRecipeDetails.setOnClickListener {
            btSeeHideDescriptionRecipeDetails.text = if (isShowingAllVideoDescriptionOnView()) {
                tvVideoDescriptionRecipeDetails.maxLines = 5
                getString(R.string.show_video_description)
            } else {
                tvVideoDescriptionRecipeDetails.maxLines = 300
                getString(R.string.hide_video_description)
            }
        }
    }

    private fun settingToolbar() {
        viewFlipperAppToolbar.displayedChild = 3
        AppToolbarSetup.setBackButton(appToolbarRecipeDetails, this)
        appToolbarRecipeDetails.setOnMenuItemClickListener { item ->
            when (item.title) {
                getString(R.string.edit) -> {
                    val requestCode = 1
                    startActivityForResult(
                        CreateRecipeActivity.getStartIntent(this, recipe!!),
                        requestCode
                    )
                }
                getString(R.string.delete) -> {
                    //deleting confirmation
                    MaterialAlertDialogBuilder(this)
                        .setTitle(R.string.confirmation_delete)
                        .setNegativeButton(R.string.cancel) { _, _ ->/*does nothing*/ }
                        .setPositiveButton(R.string.ok) { _, _ ->
                            if (recipe != null) {
                                viewModel.deleteRecipe(recipe!!)
                                setResult(AppConstantCodes.DELETE_RECIPE)
                                //finnish activity
                                finish()
                            }
                        }
                        .show()
                }
                getString(R.string.add_to_favorite) -> {
                    item.isChecked = if (item.isChecked) {
                        item.setIcon(R.drawable.ic_favorite_outlined_24dp)
                        Snackbar.make(
                            LinearLayoutRecipeDetails,
                            R.string.recipe_removed_from_favorites,
                            Snackbar.LENGTH_SHORT
                        ).show()
                        false
                    } else {
                        item.setIcon(R.drawable.ic_favorite_filled_24dp)
                        Snackbar.make(
                            LinearLayoutRecipeDetails,
                            R.string.recipe_added_to_favorites,
                            Snackbar.LENGTH_SHORT
                        ).show()
                        true
                    }
                    recipe?.isFavorite = item.isChecked
                    if (recipe != null) {
                        viewModel.updateRecipe(recipe!!)
                    }
                }
            }
            return@setOnMenuItemClickListener true
        }
    }

    private fun retrieveDataFromParentActivity() {
        if (!intent.hasExtra(AppConstantCodes.EXTRA_RECIPE)) {
            Toast.makeText(this, R.string.error_showing_recipe, Toast.LENGTH_SHORT).show()
            finish()
        }
        recipe = intent.getParcelableExtra(AppConstantCodes.EXTRA_RECIPE)
        recipe?.categories =
            intent.getSerializableExtra(AppConstantCodes.RECIPE_CATEGORIES) as List<Category>
    }

    private fun setFavoriteCheckButton(isChecked: Boolean) {
        if (isChecked) {
            appToolbarRecipeDetails.menu[0].setIcon(R.drawable.ic_favorite_filled_24dp)
        } else {
            appToolbarRecipeDetails.menu[0].setIcon(R.drawable.ic_favorite_outlined_24dp)
        }
        appToolbarRecipeDetails.menu[0].isChecked = isChecked
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            recipe = data.getParcelableExtra(AppConstantCodes.UPDATED_RECIPE)
            recipe?.categories =
                data.getSerializableExtra(AppConstantCodes.UPDATED_RECIPE_CATEGORIES) as List<Category>
            recipe?.let {
                intent = getStartIntent(this, it)
                //recreating activity for updating video
                recreate()
            }
            viewModel.recipe.value = recipe
            setResult(AppConstantCodes.RETURN_UPDATED_RECIPE)
        }
    }

    private fun updateFields(recipe: Recipe) {
        tvTitle.text = recipe.name
        tvVideoDescriptionRecipeDetails.text = recipe.description
        setFavoriteCheckButton(recipe.isFavorite)

        recipe.categories.let {
            layoutChipCategoryRecipeDetails.removeAllViews()
            for (category in it) {
                val newCategoryView = layoutInflater.inflate(
                    R.layout.chip_category_recipe_details,
                    layoutChipCategoryRecipeDetails,
                    false
                ) as TextView
                newCategoryView.text = category.categoryName
                layoutChipCategoryRecipeDetails.addView(newCategoryView)
            }
        }
    }

    private fun runYouTubePlayer() {

        (youTubePlayerFragment as YouTubePlayerSupportFragment?)?.initialize(
            getString(R.string.youtube_api_key),
            object : YouTubePlayer.OnInitializedListener {
                override fun onInitializationSuccess(
                    p0: YouTubePlayer.Provider?,
                    p1: YouTubePlayer?,
                    p2: Boolean
                ) {
                    p1?.loadVideo(viewModel.recipe.value?.video?.url)
                }

                override fun onInitializationFailure(
                    p0: YouTubePlayer.Provider?,
                    p1: YouTubeInitializationResult?
                ) {
                    Snackbar.make(
                        LinearLayoutRecipeDetails,
                        R.string.error_showing_video,
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            })
    }

    companion object {
        fun getStartIntent(context: Context, recipe: Recipe): Intent {
            return Intent(context, RecipeDetailsActivity::class.java).apply {
                putExtra(AppConstantCodes.EXTRA_RECIPE, recipe)
                putExtra(AppConstantCodes.RECIPE_CATEGORIES, recipe.categories as Serializable)
            }
        }
    }
}
