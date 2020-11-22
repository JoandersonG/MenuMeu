package joandersongoncalves.example.koes.presentation

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.core.view.size
import androidx.lifecycle.Observer
import joandersongoncalves.example.koes.R
import joandersongoncalves.example.koes.data.model.Category
import joandersongoncalves.example.koes.presentation.viewmodel.EditHomeScreenViewModel
import kotlinx.android.synthetic.main.activity_edit_home_screen.*
import kotlinx.android.synthetic.main.app_toolbar.*

class EditHomeScreenActivity : AppCompatActivity() {

    private val viewModel: EditHomeScreenViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_home_screen)

        setAppToolbar()

        setViewModel()

        setClickListeners()
    }

    private fun setClickListeners() {
        btCancelEditHome.setOnClickListener {
            setResult(AppConstantCodes.NO_CHANGES_MADE_EDIT_HOME)
            finish()
        }

        btSaveEditHome.setOnClickListener {
            viewModel.saveAllCategoriesChanges()
            setResult(AppConstantCodes.CHANGES_MADE_EDIT_HOME)
            finish()
        }
    }

    private fun setViewModel() {
        viewModel.allCategories.observe(this, Observer {
            addCategoriesToView(it)
        })
        viewModel.toolbarTitle.observe(this, Observer {
            tvAppToolbarOtherTitle.text = it
        })
    }

    private fun addCategoriesToView(allCategories: List<Category>) {
        flexboxLayoutEditHomeScreen.removeAllViews()
        for (category in allCategories) {
            LayoutInflater.from(this).inflate(
                R.layout.edit_home_screen_category,
                flexboxLayoutEditHomeScreen
            )
            val categoryId = flexboxLayoutEditHomeScreen.size - 1
            val tvCategory = (flexboxLayoutEditHomeScreen[categoryId] as TextView)
            tvCategory.apply {
                text = category.categoryName
                background = if (category.isShowedOnHome) {
                    setTextColor(Color.WHITE)
                    getDrawable(R.drawable.primary_ripple_stroke_background)
                } else {
                    setTextColor(Color.BLACK)
                    getDrawable(R.drawable.white_ripple_stroke_background)
                }

                setOnClickListener {
                    category.isShowedOnHome = !category.isShowedOnHome
                    viewModel.updateCategory(category)
                    background = if (category.isShowedOnHome) {
                        setTextColor(Color.WHITE)
                        getDrawable(R.drawable.primary_ripple_stroke_background)
                    } else {
                        setTextColor(Color.BLACK)
                        getDrawable(R.drawable.white_ripple_stroke_background)
                    }
                }
            }
        }
    }

    private fun setAppToolbar() {
        viewModel.setToolbarTitle(getText(R.string.edit_home_screen).toString())
        AppToolbarSetup.setBackButton(appToolbarGeneral, this)
        viewFlipperAppToolbar.displayedChild = 1

    }
}
