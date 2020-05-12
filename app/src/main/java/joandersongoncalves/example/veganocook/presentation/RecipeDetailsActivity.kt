package joandersongoncalves.example.veganocook.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import joandersongoncalves.example.veganocook.R
import joandersongoncalves.example.veganocook.data.model.YouTubeVideo
import kotlinx.android.synthetic.main.activity_recipe_details.*
import kotlinx.android.synthetic.main.app_toolbar.*

class RecipeDetailsActivity : YouTubeBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_details)

        //setting the right toolbar for this activity
        viewFlipperAppToolbar.displayedChild = 1
        AppToolbarSetup.setBackButton(appToolbarOther, this)

        val video = intent.getParcelableExtra<YouTubeVideo>(EXTRA_VIDEO)
        tvTitle.text = video?.title
        tvDescription.text = video?.description

        //executa o player de vídeo do arquivo XML
        runYouTubePlayer()
    }

    private fun runYouTubePlayer() {
        youTubePlayer.initialize(
            getString(R.string.youtube_api_key),
            object : YouTubePlayer.OnInitializedListener {
            override fun onInitializationSuccess(
                p0: YouTubePlayer.Provider?,
                p1: YouTubePlayer?,
                p2: Boolean
            ) {
                p1?.loadVideo("go4DMa5-fZM")
            }

            override fun onInitializationFailure(
                p0: YouTubePlayer.Provider?,
                p1: YouTubeInitializationResult?
            ) {

            }
            })
    }

    companion object {
        private const val EXTRA_VIDEO = "EXTRA_VIDEO"

        fun getStartIntent(context: Context, video: YouTubeVideo): Intent {
            return Intent(context, RecipeDetailsActivity::class.java).apply {
                putExtra(EXTRA_VIDEO, video)
            }
        }
    }


}
