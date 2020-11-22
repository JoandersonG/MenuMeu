package joandersongoncalves.example.koes.presentation.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import joandersongoncalves.example.koes.R
import kotlinx.android.synthetic.main.fragment_no_category_error.view.*

class DialogErrorNoCategoryFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_no_category_error, container, false)

        //to allow corner radius:
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        rootView.btErrorDialogNoCategory.setOnClickListener {
            dismiss()
        }

        return rootView
    }
}
