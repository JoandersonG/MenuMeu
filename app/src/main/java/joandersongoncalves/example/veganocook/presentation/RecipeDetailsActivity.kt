package joandersongoncalves.example.veganocook.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.google.android.material.chip.Chip
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
import java.io.Serializable


class RecipeDetailsActivity : AppCompatActivity() {

    private val viewModel: RecipeDetailsViewModel by viewModels()

    private var recipe: Recipe? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_details)

        // retrieving data from parent activity
        if (!intent.hasExtra(AppConstantCodes.EXTRA_RECIPE)) {
            Toast.makeText(this, R.string.error_showing_recipe, Toast.LENGTH_SHORT).show()
            finish()
        }
        val recipe: Recipe? = intent.getParcelableExtra(AppConstantCodes.EXTRA_RECIPE)
        recipe?.categories =
            intent.getSerializableExtra(AppConstantCodes.RECIPE_CATEGORIES) as List<Category>
        viewModel.recipe.observe(this, Observer {
            updateFields(it)
        })
        viewModel.recipe.value = recipe

        //setting the youtube Player
        runYouTubePlayer()

        //setting the right toolbar for this activity
        viewFlipperAppToolbar.displayedChild = 3
        AppToolbarSetup.setBackButton(appToolbarRecipeDetais, this)
        appToolbarRecipeDetais.setOnMenuItemClickListener { item ->
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
                                viewModel.deleteRecipe(recipe)
                                setResult(AppConstantCodes.DELETE_RECIPE)
                                //finnish activity
                                finish()
                            }
                        }
                        .show()
                }
                getString(R.string.add_to_favorite) -> {
                    if (recipe != null) {
                        viewModel.updateRecipe(recipe)
                    }
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
                }
            }
            return@setOnMenuItemClickListener true
        }

        //setting show/hide description functionality
        layoutShowHideDescription.setOnClickListener {
            tvDescription.visibility = if (tvDescription.isVisible) {
                imageShowHideDescription.setImageDrawable(getDrawable(R.drawable.ic_keyboard_arrow_down_black_24dp))
                View.GONE
            } else {
                imageShowHideDescription.setImageDrawable(getDrawable(R.drawable.ic_keyboard_arrow_up_black_24dp))
                View.VISIBLE
            }
        }
    }

    private fun setFavoriteCheckButton(isChecked: Boolean) {
        if (isChecked) {
            appToolbarRecipeDetais.menu[0].setIcon(R.drawable.ic_favorite_filled_24dp)
        } else {
            appToolbarRecipeDetais.menu[0].setIcon(R.drawable.ic_favorite_outlined_24dp)
        }
        appToolbarRecipeDetais.menu[0].isChecked = isChecked
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
        tvDescription.text = recipe.description
        setFavoriteCheckButton(recipe.isFavorite)

        //getting and putting categories into chips
        recipe.categories.let {
            chipGroupRecipeDetailsActivity.removeAllViews()
            for (category in it) {
                val categoryChip = layoutInflater.inflate(
                    R.layout.chip_all_categories,
                    chipGroupRecipeDetailsActivity,
                    false
                ) as Chip
                categoryChip.isCheckable = false
                categoryChip.isClickable = false
                categoryChip.text = category.categoryName
                chipGroupRecipeDetailsActivity.addView(categoryChip)
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
