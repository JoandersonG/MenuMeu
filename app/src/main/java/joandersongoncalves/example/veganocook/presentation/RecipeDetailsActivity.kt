package joandersongoncalves.example.veganocook.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import joandersongoncalves.example.veganocook.R
import joandersongoncalves.example.veganocook.data.model.Recipe
import kotlinx.android.synthetic.main.activity_recipe_details.*
import kotlinx.android.synthetic.main.app_toolbar.*

class RecipeDetailsActivity : YouTubeBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_details)

        //setting the right toolbar for this activity
        viewFlipperAppToolbar.displayedChild = 1
        AppToolbarSetup.setBackButton(appToolbarOther, this)

        // retrieving data from parent activity
        val recipe = intent.getParcelableExtra<Recipe>(EXTRA_RECIPE)
        tvTitle.text = recipe?.name
        tvDescription.text = recipe?.description
        val videoUrl = recipe?.videoUrl

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

        fun getStartIntent(context: Context, recipe: Recipe): Intent {
            return Intent(context, RecipeDetailsActivity::class.java).apply {
                putExtra(EXTRA_RECIPE, recipe)
            }
        }
    }


}
