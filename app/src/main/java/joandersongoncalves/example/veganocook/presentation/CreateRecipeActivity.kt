package joandersongoncalves.example.veganocook.presentation

import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.iterator
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import joandersongoncalves.example.veganocook.R
import joandersongoncalves.example.veganocook.data.ApiService
import joandersongoncalves.example.veganocook.data.model.Recipe
import joandersongoncalves.example.veganocook.data.model.RecipeCategory
import joandersongoncalves.example.veganocook.data.model.YouTubeVideo
import joandersongoncalves.example.veganocook.data.response.VideoBodyResponse
import kotlinx.android.synthetic.main.activity_create_recipe.*
import kotlinx.android.synthetic.main.app_toolbar.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@Suppress("ControlFlowWithEmptyBody")
class CreateRecipeActivity : AppCompatActivity() {

    private val recipeCategories = mutableListOf<RecipeCategory>()
    private var videoLink = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_recipe)

        //setting the right toolbar for this activity
        viewFlipperAppToolbar.displayedChild = 1
        appToolbarOther.setTitle(R.string.add_recipe)
        AppToolbarSetup.setBackButton(appToolbarOther, this)

        // setting cancel button
        btCreateRecipeCancel.setOnClickListener {
            // close activity
            finish()
        }

        // setting search button
        btSearch.setOnClickListener { handleYoutubeLinkSearch() }

        //handling pressing enter on softkeyboard on textInputYoutubeLink
        val listener = OnEditorActionListener { _, actionId, event ->
            if (event == null) {
                if (actionId == EditorInfo.IME_ACTION_DONE) ;
                else if (actionId == EditorInfo.IME_ACTION_NEXT) ;
                else return@OnEditorActionListener false // Let system handle all other null KeyEvents
            } else if (actionId == EditorInfo.IME_NULL) {
                if (event.action == KeyEvent.ACTION_DOWN) ;
                else return@OnEditorActionListener true // We consume the event when the key is released.
            } else return@OnEditorActionListener false

            //when enter is pressed do:
            handleYoutubeLinkSearch()

            true
        }
        textInputYoutubeLink.setOnEditorActionListener(listener)

        //handling checking chips
        chipBreakfast.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                recipeCategories.add(RecipeCategory.BREAKFAST)
            } else {
                recipeCategories.remove(RecipeCategory.BREAKFAST)
            }
        }
        chipLunch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                recipeCategories.add(RecipeCategory.LUNCH)
            } else {
                recipeCategories.remove(RecipeCategory.LUNCH)
            }
        }
        chipDinner.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                recipeCategories.add(RecipeCategory.DINNER)
            } else {
                recipeCategories.remove(RecipeCategory.DINNER)
            }
        }
        chipSnack.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                recipeCategories.add(RecipeCategory.SNACK)
            } else {
                recipeCategories.remove(RecipeCategory.SNACK)
            }
        }

        //pressing save button
        btCreateRecipeSave.setOnClickListener {
            if (validateData()) {
                val recipe = Recipe(videoLink, recipeCategories)
                //add to database
                println("Receita é $recipe")
            }
        }
    }

    private fun handleYoutubeLinkSearch() {
        val link = textInputYoutubeLink.text
        if (link.toString().startsWith("https://youtu.be/", false)) {
            //its a valid link
            //remove prefix
            videoLink = link.toString().removePrefix("https://youtu.be/")
            getVideo(videoLink)
        } else {
            //invalid link
            textInputYoutubeLink.error = getString(R.string.error_invalid_link)
        }
    }

    private fun getVideo(idVideo: String) {
        ApiService.service.getVideos(idVideo, getString(R.string.youtube_api_key))
            .enqueue(object: Callback<VideoBodyResponse> {
                override fun onFailure(call: Call<VideoBodyResponse>, t: Throwable) {
                    Snackbar.make(
                        viewFlipperCreateRecipeActivity,
                        R.string.error_conecting_api,
                        Snackbar.LENGTH_SHORT
                    ).show()
                }

                override fun onResponse(
                    call: Call<VideoBodyResponse>,
                    response: Response<VideoBodyResponse>
                ) {
                    if (response.isSuccessful) {
                        var youTubeVideo: YouTubeVideo
                        response.body()?.let { videoBodyResponse ->
                            if (videoBodyResponse.itemsResult.isNotEmpty()) {
                                //get the result
                                youTubeVideo =
                                    videoBodyResponse.itemsResult[0].snippetVideoResponse.getVideoModel()
                                //show the result
                                fillFields(youTubeVideo)
                            } else {
                                //didn't find the video
                                textInputYoutubeLink.error = getString(R.string.error_invalid_link)
                            }
                        }
                    } else {
                        Snackbar.make(
                            viewFlipperCreateRecipeActivity, R.string.error_retrieving_information,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }

            })
    }

    private fun validateData(): Boolean {

        //test title field
        if (textInputTitle.text == null || textInputTitle.text.toString() == "") {
            //invalid
            textInputTitle.error = getString(R.string.error_insert_title)
            return false
        }

        //test categories
        var test = false
        for (chip in chipGroupCategories) {
            if ((chip as Chip).isChecked) {
                //error: select a category
                test = true
            }
        }
        if (!test) {
            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.error_select_category)
                .setPositiveButton(R.string.ok) { _, _ ->/*does nothing*/ }
                .show()
            return false
        }

        return true
    }

    //fill layout filds with info in video
    private fun fillFields(video: YouTubeVideo) {

        textInputTitle.setText(video.title)
        textInputDescription.setText(video.description)

        //show fields on ViewFlipper
        viewFlipperCreateRecipeActivity.displayedChild = 1

    }
}
