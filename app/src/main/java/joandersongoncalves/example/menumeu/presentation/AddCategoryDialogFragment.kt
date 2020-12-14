package joandersongoncalves.example.menumeu.presentation

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.get
import androidx.core.view.iterator
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.google.android.flexbox.FlexboxLayout
import joandersongoncalves.example.menumeu.R
import joandersongoncalves.example.menumeu.data.model.Category
import joandersongoncalves.example.menumeu.presentation.fragment.DeleteCategoryFragment
import joandersongoncalves.example.menumeu.presentation.fragment.DialogErrorNoCategoryFragment
import joandersongoncalves.example.menumeu.presentation.viewmodel.CreateRecipeViewModel
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

        //to allow corner radius:
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

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
                val errorCategoriesFragment =
                    DialogErrorNoCategoryFragment()
                val fm = requireActivity().supportFragmentManager
                errorCategoriesFragment.show(fm, "no category error fragment")

            } else {
                viewModel.saveSelectedCategoriesOnDialog()
                dismiss()
            }
        }

        rootView.tvBtCreateNewCategory.setOnClickListener {
            rootView.layoutAddFavorite.visibility =
                if (rootView.layoutAddFavorite.visibility == View.VISIBLE) {
                    View.GONE
            } else {
                    View.VISIBLE
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

    private fun loadObservers(rootView: View, inflater: LayoutInflater) {

        //adding all categories and selected ones to view
        viewModel.allCategories.observe(owner, Observer { listCategory ->
            rootView.flexboxLayoutDialogAddCategory.removeAllViews()
            for (category in listCategory) {
                val newCategoryView = inflater.inflate(
                    R.layout.chip_add_categories_dialog,
                    rootView.flexboxLayoutDialogAddCategory,
                    false
                ) as ConstraintLayout
                val chipTitleTextView = newCategoryView[0] as TextView
                chipTitleTextView.text = category.categoryName
                viewModel.selectedCategoriesOnDialog.value?.let {
                    newCategoryView.background = if (it.contains(category)) {
                        //textColor:
                        chipTitleTextView.setTextColor(Color.WHITE)
                        //background:
                        context?.getDrawable(R.drawable.primary_ripple_stroke_background)
                    } else {
                        //textColor:
                        chipTitleTextView.setTextColor(Color.BLACK)
                        //background:
                        context?.getDrawable(R.drawable.white_ripple_stroke_background)
                    }
                }

                //onClick on delete button
                val chipDeleteButton = newCategoryView[1] as ImageButton
                chipDeleteButton.setOnClickListener {
                    val deleteCategoriesFragment =
                        DeleteCategoryFragment(
                            category,
                            viewModel
                        )
                    val fm = requireActivity().supportFragmentManager
                    deleteCategoriesFragment.show(fm, "delete category fragment")
                }

                newCategoryView.setOnClickListener { view ->
                    viewModel.changeSelectionOnCategoryOnDialog(category)
                    viewModel.selectedCategoriesOnDialog.value?.let { selectedCateg ->
                        view.background = if (selectedCateg.contains(category)) {
                            //textColor:
                            chipTitleTextView.setTextColor(Color.WHITE)
                            //background:
                            context?.getDrawable(R.drawable.primary_ripple_stroke_background)
                        } else {
                            //textColor:
                            chipTitleTextView.setTextColor(Color.BLACK)
                            //background:
                            context?.getDrawable(R.drawable.white_ripple_stroke_background)
                        }
                    }
                }
                rootView.flexboxLayoutDialogAddCategory.addView(newCategoryView)
            }
        })

        viewModel.selectedCategoriesOnDialog.observe(owner, Observer {
            if (flexboxLayoutDialogAddCategory != null) {
                for (category in it) {
                    for (view in flexboxLayoutDialogAddCategory) {
                        val flexboxLayout = (view as FlexboxLayout)
                        val tvTitle = (flexboxLayout[0] as TextView)
                        if (tvTitle.text == it) { //check
                            tvTitle.setTextColor(Color.WHITE)
                            flexboxLayout.background =
                                context?.getDrawable(R.drawable.primary_ripple_stroke_background)
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