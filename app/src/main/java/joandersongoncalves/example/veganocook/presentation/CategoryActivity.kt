package joandersongoncalves.example.veganocook.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import joandersongoncalves.example.veganocook.R
import joandersongoncalves.example.veganocook.data.model.YouTubeVideo
import joandersongoncalves.example.veganocook.presentation.adapter.RecipeAdapter
import kotlinx.android.synthetic.main.activity_category.*
import kotlinx.android.synthetic.main.app_toolbar.*

class CategoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)


        with(rvRecipesByCategory) {
            layoutManager = GridLayoutManager(this@CategoryActivity, 2)
            setHasFixedSize(true)
            adapter = RecipeAdapter(videoExemples()) { youtubeVideo ->
                val intent =
                    RecipeDetailsActivity.getStartIntent(this@CategoryActivity, youtubeVideo)
                this@CategoryActivity.startActivity(intent)
            }
        }

        //setting the right toolbar for this activity
        viewFlipperAppToolbar.displayedChild = 1
        appToolbarOther.title = intent.getStringExtra(EXTRA_CATEGORY_TITLE)

        AppToolbarSetup.setBackButton(appToolbarOther, this)
    }

    /*
    companion object {
        private const val EXTRA_VIDEO = "EXTRA_VIDEO"

        fun getStartIntent(context: Context, video: YouTubeVideo): Intent {
            return Intent(context,RecipeDetailsActivity::class.java).apply {
                putExtra(EXTRA_VIDEO, video)
            }
        }
    }
    */
    companion object {
        private const val EXTRA_CATEGORY_TITLE = "EXTRA_CATEGORY_TITLE"

        fun getStartActivity(conext: Context, title: String): Intent {
            return Intent(conext, CategoryActivity::class.java).apply {
                putExtra(EXTRA_CATEGORY_TITLE, title)
            }
        }
    }


    private fun videoExemples(): List<YouTubeVideo> {
        return listOf(
            YouTubeVideo("Nhoque delicioso", "Este é um nhoque divino...", null, null),
            YouTubeVideo(
                "Lasanha de beringela",
                "Este é uma lasanha de comer rezando...",
                null,
                null
            ),
            YouTubeVideo(
                "Farofa proteica",
                "Esta farofa vai te fazer chorar de alegria...",
                null,
                null
            ),
            YouTubeVideo(
                "Smoothie verde",
                "Este smoothie vai te fazer muito mais saudável...",
                null,
                null
            )
        )
    }

}
