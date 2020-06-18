package joandersongoncalves.example.veganocook.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import joandersongoncalves.example.veganocook.R
import joandersongoncalves.example.veganocook.data.model.Recipe
import kotlinx.android.synthetic.main.activity_recipe_details.*
import kotlinx.android.synthetic.main.app_toolbar.*

class RecipeDetailsActivity : YouTubeBaseActivity() {

    private var recipe: Recipe? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_details)

        // retrieving data from parent activity
        if (!intent.hasExtra(EXTRA_RECIPE)) {
            Toast.makeText(this, R.string.error_showing_recipe, Toast.LENGTH_SHORT).show()
            finish()
        }
        recipe = intent.getParcelableExtra(EXTRA_RECIPE)

        updateFields(recipe)

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
                            //send to previous activity to delete
                            val intent = Intent()
                            intent.putExtra(RECIPE_TO_BE_DELETED, recipe)
                            setResult(DELETE_RECIPE, intent)
                            //finnish activity
                            finish()

                        }
                        .show()
                }
            }
            return@setOnMenuItemClickListener true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            recipe = data.getParcelableExtra(UPDATED_RECIPE)
            updateFields(recipe)
            setResult(RETURN_UPDATED_RECIPE)
        }
    }

    private fun updateFields(recipe: Recipe?) {
        tvTitle.text = recipe?.name
        tvDescription.text = recipe?.description
        val videoUrl = recipe?.video?.url
        //executa o player de vídeo do arquivo XML
        videoUrl?.let { runYouTubePlayer(it) }
    }

    private fun runYouTubePlayer(videoUrl: String) {
        youTubePlayer.initialize(
            getString(R.string.youtube_api_key),
            object : YouTubePlayer.OnInitializedListener {
            override fun onInitializationSuccess(
                p0: YouTubePlayer.Provider?,
                p1: YouTubePlayer?,
                p2: Boolean
            ) {
                p1?.loadVideo(videoUrl)
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
        private const val EXTRA_RECIPE = "EXTRA_RECIPE"
        private const val UPDATED_RECIPE = "UPDATED_RECIPE"
        const val RECIPE_TO_BE_DELETED = "RECIPE_TO_BE_DELETED"
        const val DELETE_RECIPE = 3
        const val RETURN_UPDATED_RECIPE = 2

        fun getStartIntent(context: Context, recipe: Recipe): Intent {
            return Intent(context, RecipeDetailsActivity::class.java).apply {
                putExtra(EXTRA_RECIPE, recipe)
            }
        }
    }


}
