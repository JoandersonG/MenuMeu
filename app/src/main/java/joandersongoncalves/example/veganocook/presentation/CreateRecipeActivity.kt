package joandersongoncalves.example.veganocook.presentation

import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.iterator
import androidx.lifecycle.Observer
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import joandersongoncalves.example.veganocook.R
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

        val viewModel: CreateRecipeViewModel by viewModels()

        viewModel.videoLiveData.observe(this, Observer {
            it?.let {
                fillFields(it.title, it.description)
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

        // setting cancel button
        btCreateRecipeCancel.setOnClickListener {
            // close activity
            finish()
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
            viewModel.chipCheckedChange(R.id.chipDinner, isChecked)
        }

        //pressing save button
        btCreateRecipeSave.setOnClickListener {
            if (validateData()) {
                viewModel.saveNewRecipe()
            }
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
    private fun fillFields(title: String, description: String) {

        textInputTitle.setText(title)
        textInputDescription.setText(description)

        //show fields on ViewFlipper
        viewFlipperCreateRecipeActivity.displayedChild = 1

    }
}
