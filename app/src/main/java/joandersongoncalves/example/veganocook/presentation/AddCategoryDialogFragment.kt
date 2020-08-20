package joandersongoncalves.example.veganocook.presentation

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.CompoundButton
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.view.iterator
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import joandersongoncalves.example.veganocook.R
import joandersongoncalves.example.veganocook.data.model.Category
import joandersongoncalves.example.veganocook.presentation.viewmodel.CreateRecipeViewModel
import kotlinx.android.synthetic.main.fragment_dialog_add_category.*
import kotlinx.android.synthetic.main.fragment_dialog_add_category.view.*

class AddCategoryDialogFragment(
    private val owner: LifecycleOwner,
    private val viewModel: CreateRecipeViewModel
) : DialogFragment() {

    //private val viewModel: AddCategoryDialogViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_dialog_add_category, container, false)


        settingViewModel()

        loadObservers(rootView, inflater)

        //handling pressing enter on softkeyboard on textInputYoutubeLink
        handlingEnterPressingOnSoftKeyboard(rootView)

        settingButtonsListeners(rootView)

        return rootView
    }

    private fun settingViewModel() {
        //update all categories to be shown
        viewModel.getAllCategories()

        viewModel.cleanSelectedCategoriesOnDialog()
        viewModel.recipeCategories.value?.let {
            for (category in it) {
                viewModel.addToSelectedCategoriesOnDialog(category)
            }
        }

    }

    private fun settingButtonsListeners(rootView: View) {
        rootView.buttonSaveNewCategory.setOnClickListener {
            createNewCategory(rootView)
        }

        rootView.btAddCategoryDialogCancel.setOnClickListener {
            viewModel.cleanSelectedCategoriesOnDialog()
            dismiss()
        }

        rootView.btAddCategoryDialogSave.setOnClickListener {
            //send categories selected back to activity
            if (noCategoriesSelected()) {
                context?.let { it1 ->
                    MaterialAlertDialogBuilder(it1)
                        .setTitle(R.string.error_select_category)
                        .setPositiveButton(R.string.ok) { _, _ ->/*does nothing*/ }
                        .show()
                }
            } else {
                viewModel.saveSelectedCategoriesOnDialog()
                dismiss()
            }
        }

        rootView.chipCreateCategory.setOnClickListener {
            if (layoutAddFavorite.isVisible) {
                //layoutAddFavorite.setPadding(0,0,0,0)
                layoutAddFavorite.visibility = View.GONE
            } else {
                //layoutAddFavorite.setPadding(0,16,0,0)
                layoutAddFavorite.visibility = View.VISIBLE
            }
        }
    }

    private fun handlingEnterPressingOnSoftKeyboard(rootView: View) {
        val listener = TextView.OnEditorActionListener { _, actionId, event ->
            if (event == null) {
                if (actionId == EditorInfo.IME_ACTION_DONE) ;
                else if (actionId == EditorInfo.IME_ACTION_NEXT) ;
                else return@OnEditorActionListener false // Let system handle all other null KeyEvents
            } else if (actionId == EditorInfo.IME_NULL) {
                if (event.action == KeyEvent.ACTION_DOWN) ;
                else return@OnEditorActionListener true // We consume the event when the key is released.
            } else return@OnEditorActionListener false

            //when enter is pressed do:
            createNewCategory(rootView)

            true
        }
        rootView.textInputNewCategory.setOnEditorActionListener(listener)
    }

    //listener for pressing close icon on Chips
    private fun getCloseListenerForChips() = View.OnClickListener {
        //confirmation on delete
        context?.let { it1 ->
            MaterialAlertDialogBuilder(it1)
                .setTitle(getString(R.string.confirmation_delete_category))
                .setMessage("\n ${getString(R.string.this_action_cannot_undone)}")
                .setPositiveButton(R.string.delete) { _, _ ->
                    //delete view
                    viewModel.deleteCategory(Category((it as Chip).text.toString()))
                }
                .setNegativeButton(R.string.cancel) { _, _ -> /*does nothing*/ }
                .show()
        }
    }

    //listener for checking chips
    private fun getCheckListenerForChips() =
        CompoundButton.OnCheckedChangeListener { compoundButton, _ ->
            viewModel.changeSelectionOnCategoryOnDialog(Category(compoundButton.text.toString()))
        }

    private fun loadObservers(rootView: View, inflater: LayoutInflater) {

        //adding all categories and selected ones to view
        viewModel.allCategories.observe(owner, Observer { listCategory ->
            rootView.chipGroupAllCategories.removeAllViews()
            for (category in listCategory) {
                val newCategoryView = inflater.inflate(
                    R.layout.chip_all_categories,
                    rootView.chipGroupAllCategories,
                    false
                ) as Chip
                newCategoryView.text = category.categoryName
                newCategoryView.isCloseIconVisible = true
                newCategoryView.isCheckable = true
                newCategoryView.setOnCloseIconClickListener(getCloseListenerForChips())
                viewModel.selectedCategoriesOnDialog.value?.let {
                    if (it.contains(category)) {
                        newCategoryView.isChecked = true
                    }
                }
                newCategoryView.setOnCheckedChangeListener(getCheckListenerForChips())
                rootView.chipGroupAllCategories.addView(newCategoryView)
            }
        })

        viewModel.selectedCategoriesOnDialog.observe(owner, Observer {
            println("observer do selectedCategoriesOnDialog executando. Value is $it")
            if (chipGroupAllCategories != null) {
                for (category in it) {
                    for (chip in chipGroupAllCategories) {
                        if ((chip as Chip).text == it) {
                            chip.isChecked = true
                        }
                    }
                }
            }
        })

        //newCategoryField observer
        viewModel.newCategoryField.observe(owner, Observer {
            rootView.textInputNewCategory.setText(it)
        })
    }

    private fun createNewCategory(rootView: View) {
        val result = validateNewRecipeName(rootView.textInputNewCategory.text.toString())
        if (result != null) {
            //failed validating field
            textInputNewCategory.error = result
            return
        }
        //success validating field
        viewModel.newCategoryField.value = rootView.textInputNewCategory.text.toString()
        viewModel.newCategoryField.value?.let {
            viewModel.saveNewCategory(Category(it))
            viewModel.changeSelectionOnCategoryOnDialog(Category(it))
            viewModel.newCategoryField.value = ""
        }
    }

    private fun noCategoriesSelected(): Boolean {
        viewModel.selectedCategoriesOnDialog.value?.let {
            return it.isEmpty()
        }
        return true
    }

    private fun validateNewRecipeName(name: String): String? {
        var test = false
        for (char in name) {
            if (char.toInt() < 65) {
                continue
            }
            //char is an accepted character
            test = true
            break
        }
        if (!test) {
            return getString(R.string.error_invalid_name)
        }
        return null
    }
}