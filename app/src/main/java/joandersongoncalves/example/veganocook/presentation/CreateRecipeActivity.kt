package joandersongoncalves.example.veganocook.presentation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.iterator
import androidx.lifecycle.Observer
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import joandersongoncalves.example.veganocook.R
import joandersongoncalves.example.veganocook.data.model.Category
import joandersongoncalves.example.veganocook.data.model.Recipe
import joandersongoncalves.example.veganocook.presentation.viewmodel.CreateRecipeViewModel
import kotlinx.android.synthetic.main.activity_create_recipe.*
import kotlinx.android.synthetic.main.app_toolbar.*


@Suppress("ControlFlowWithEmptyBody")
class CreateRecipeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_recipe)

        //setting the right toolbar for this activity
        viewFlipperAppToolbar.displayedChild = 1
        appToolbarOther.setTitle(R.string.add_recipe)
        AppToolbarSetup.setBackButton(appToolbarOther, this)

        // setting viewModel
        val viewModel: CreateRecipeViewModel by viewModels()

        viewModel.videoLiveData.observe(this, Observer {
            it?.let {
                fillFields(it.title, it.description, "https://youtu.be/${it.url}")
            }
        })
        viewModel.videoRetrieveResponseLiveData.observe(this, Observer {
            it?.let {
                when (it) {
                    CreateRecipeViewModel.ERROR_INVALID_LINK -> {
                        textInputYoutubeLink.error = getString(R.string.error_invalid_link)
                    }
                    CreateRecipeViewModel.ERROR_RETRIEVING_INFORMATION -> {
                        Snackbar.make(
                            viewFlipperCreateRecipeActivity, R.string.error_retrieving_information,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    CreateRecipeViewModel.ERROR_CONECTING_API -> {
                        Snackbar.make(
                            viewFlipperCreateRecipeActivity,
                            R.string.error_conecting_api,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })

        // getting extra, if any
        var recipe = intent.getParcelableExtra<Recipe>(EXTRA_RECIPE)
        recipe?.let { rec ->
            //viewModel.getVideo(it.videoUrl,getString(R.string.youtube_api_key))
            fillFields(rec.name, rec.description, "https://youtu.be/${rec.videoUrl}")
            //categories:
            viewModel.getCategoriesFromRecipe(rec)
            //update categories to chips
            viewModel.recipeCategories.observe(this, Observer {
                if (it.contains(Category(Recipe.BREAKFAST))) {
                    chipBreakfast.isChecked = true
                    viewModel.chipCheckedChange(R.id.chipBreakfast, true)
                }
                if (it.contains(Category(Recipe.LUNCH))) {
                    chipLunch.isChecked = true
                    viewModel.chipCheckedChange(R.id.chipLunch, true)
                }
                if (it.contains(Category(Recipe.DINNER))) {
                    chipDinner.isChecked = true
                    viewModel.chipCheckedChange(R.id.chipDinner, true)
                }
                if (it.contains(Category(Recipe.SNACK))) {
                    chipSnack.isChecked = true
                    viewModel.chipCheckedChange(R.id.chipSnack, true)
                }
            })
        }

        // setting cancel button
        btCreateRecipeCancel.setOnClickListener {
            // close activity
            finish()
        }

        //pressing save button
        btCreateRecipeSave.setOnClickListener {
            if (validateData()) {
                val worked: Boolean
                val idMessage: Int
                //if it's editing an existing recipe
                if (recipe != null) {
                    recipe = viewModel.updateRecipe(
                        textInputTitle.text.toString(),
                        textInputDescription.text.toString(),
                        recipe!!
                    )
                    idMessage = R.string.success_updating_recipe

                    val intent = Intent()
                    intent.putExtra(UPDATED_RECIPE, recipe)
                    setResult(Activity.RESULT_OK, intent)
                } else {
                    worked = viewModel.saveNewRecipe(
                        textInputTitle.text.toString(),
                        textInputDescription.text.toString()
                    )
                    idMessage = if (worked) {
                        R.string.success_saving_recipe
                    } else {
                        R.string.error_saving_recipe
                    }
                }

                Toast.makeText(this, idMessage, Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        // setting search button
        btSearch.setOnClickListener { handleYoutubeLinkSearch(viewModel) }

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
            handleYoutubeLinkSearch(viewModel)

            true
        }
        textInputYoutubeLink.setOnEditorActionListener(listener)

        //handling checking chips
        chipBreakfast.setOnCheckedChangeListener { _, isChecked ->
            viewModel.chipCheckedChange(R.id.chipBreakfast, isChecked)
        }
        chipLunch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.chipCheckedChange(R.id.chipLunch, isChecked)
        }
        chipDinner.setOnCheckedChangeListener { _, isChecked ->
            viewModel.chipCheckedChange(R.id.chipDinner, isChecked)
        }
        chipSnack.setOnCheckedChangeListener { _, isChecked ->
            viewModel.chipCheckedChange(R.id.chipSnack, isChecked)
        }
    }

    private fun handleYoutubeLinkSearch(viewModel: CreateRecipeViewModel) {
        val link = textInputYoutubeLink.text
        if (link.toString().startsWith("https://youtu.be/", false)) {
            //its a valid link
            //remove prefix
            val videoLink = link.toString().removePrefix("https://youtu.be/")
            viewModel.getVideo(videoLink, getString(R.string.youtube_api_key))
        } else {
            //invalid link
            textInputYoutubeLink.error = getString(R.string.error_invalid_link)
        }
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
    private fun fillFields(title: String, description: String, videoId: String? = null) {

        textInputTitle.setText(title)
        textInputDescription.setText(description)

        textInputYoutubeLink.setText(videoId)

        //show fields on ViewFlipper
        viewFlipperCreateRecipeActivity.displayedChild = 1

    }

    companion object {
        private const val EXTRA_RECIPE = "EXTRA_RECIPE"
        private const val UPDATED_RECIPE = "UPDATED_RECIPE"

        fun getStartIntent(context: Context, recipe: Recipe): Intent {
            return Intent(context, CreateRecipeActivity::class.java).apply {
                putExtra(EXTRA_RECIPE, recipe)
            }
        }
    }
}
