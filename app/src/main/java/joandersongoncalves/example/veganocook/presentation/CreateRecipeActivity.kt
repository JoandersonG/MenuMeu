package joandersongoncalves.example.veganocook.presentation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.CompoundButton
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.core.view.size
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
import java.io.Serializable


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
                fillFields(it.title, it.description, "https://youtu.be/${it.url}", null)
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
                    CreateRecipeViewModel.ERROR_CONNECTING_API -> {
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
        var recipe = intent.getParcelableExtra<Recipe>(AppConstantCodes.EXTRA_RECIPE)
        recipe?.let { rec ->
            fillFields(
                rec.name,
                rec.description,
                "https://youtu.be/${rec.video.url}",
                rec.isFavorite
            )
            viewModel.favorite.value = rec.isFavorite
            //categories:
            viewModel.getCategoriesFromRecipe(rec)
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
                    intent.putExtra(AppConstantCodes.UPDATED_RECIPE, recipe)
                    intent.putExtra(
                        AppConstantCodes.UPDATED_RECIPE_CATEGORIES,
                        viewModel.recipeCategories.value?.toList() as Serializable
                    )
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

        //setting add to favorite
        viewModel.favorite.observe(this, Observer {
            checkBoxAddFavorite.isChecked = it
        })
        layoutAddFavorite.setOnClickListener {
            viewModel.changeFavoriteState()
        }

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

        //listener for checking chips
        val checkedListener = CompoundButton.OnCheckedChangeListener { compoundButton, _ ->
            viewModel.removeCategoryFromSelected(Category(compoundButton.text.toString()))
            Snackbar.make(
                viewFlipperCreateRecipeActivity, R.string.category_removed,
                Snackbar.LENGTH_LONG
            )
                .setAction(R.string.undo) {
                    viewModel.addCategoryIntoSelected(Category(compoundButton.text.toString()))
                }
                .show()
        }

        //setting observer to add categories to Chips
        viewModel.recipeCategories.observe(this, Observer { listCategory ->
            val addCategoryChip = chipGroupCategories[0]
            chipGroupCategories.removeAllViews()
            chipGroupCategories.addView(addCategoryChip)
            for (category in listCategory) {
                val newCategoryView = layoutInflater.inflate(
                    R.layout.chip_all_categories,
                    chipGroupCategories,
                    false
                ) as Chip
                newCategoryView.text = category.categoryName
                newCategoryView.isCheckable = true
                viewModel.recipeCategories.value?.let {
                    //select this chip
                    newCategoryView.isChecked = true
                }
                newCategoryView.setOnCheckedChangeListener(checkedListener)
                chipGroupCategories.addView(newCategoryView)
            }
        })

        //handling pressing add category button
        chipAddCategory.setOnClickListener {
            viewModel.recipeCategories.value?.let {
                val addCategoriesFragment =
                    AddCategoryDialogFragment(this, viewModel)
                val fm = supportFragmentManager
                addCategoriesFragment.show(fm, "add category fragment")
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
        if (chipGroupCategories.size <= 1) {
            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.error_select_category)
                .setPositiveButton(R.string.ok) { _, _ ->/*does nothing*/ }
                .show()
            return false
        }

        return true
    }

    //fill layout filds with info in video
    private fun fillFields(
        title: String,
        description: String,
        videoId: String? = null,
        isFavorite: Boolean?
    ) {

        textInputTitle.setText(title)
        textInputDescription.setText(description)
        textInputYoutubeLink.setText(videoId)
        if (isFavorite != null) {
            checkBoxAddFavorite.isChecked = isFavorite
        }

        //show fields on ViewFlipper
        viewFlipperCreateRecipeActivity.displayedChild = 1

    }

    companion object {
        fun getStartIntent(context: Context, recipe: Recipe): Intent {
            return Intent(context, CreateRecipeActivity::class.java).apply {
                putExtra(AppConstantCodes.EXTRA_RECIPE, recipe)
            }
        }
    }
}
