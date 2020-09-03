package joandersongoncalves.example.veganocook.presentation.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import joandersongoncalves.example.veganocook.R
import joandersongoncalves.example.veganocook.data.model.Category
import joandersongoncalves.example.veganocook.presentation.viewmodel.CreateRecipeViewModel
import kotlinx.android.synthetic.main.fragment_delete_category.view.*

class DeleteCategoryFragment(
    private val category: Category,
    private val viewModel: CreateRecipeViewModel
) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_delete_category, container, false)

        //to allow corner radius:
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        rootView.tvCategoryName.text = category.categoryName

        onClickSetup(rootView)

        return rootView
    }

    private fun onClickSetup(rootView: View) {
        rootView.btDeleteCategoryDialogCancel.setOnClickListener {
            dismiss()
        }
        rootView.btDeleteCategoryDialogDelete.setOnClickListener {
            viewModel.deleteCategory(category)
            dismiss()
        }
    }

}
