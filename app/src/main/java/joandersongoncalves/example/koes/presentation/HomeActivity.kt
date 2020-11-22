package joandersongoncalves.example.koes.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import joandersongoncalves.example.koes.R
import joandersongoncalves.example.koes.presentation.adapter.HomeAdapter
import joandersongoncalves.example.koes.presentation.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_toolbar.*

class HomeActivity : AppCompatActivity() {

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val adapter = settingRecyclerView()

        settingViewModel(adapter)

        settingHomeScreenButtons()

        setSupportActionBar(appToolbarHome);
    }

    private fun settingHomeScreenButtons() {
        btCreateRecipe.setOnClickListener {
            startActivity(Intent(this, CreateRecipeActivity::class.java))
        }
        btFavorite.setOnClickListener {
            val intent = CategoryActivity.getStartActivity(this, getString(R.string.favorite))
            startActivity(intent)
        }
        btAllRecipes.setOnClickListener {
            val intent = CategoryActivity.getStartActivity(this, getString(R.string.all_recipes))
            startActivity(intent)
        }
        searchButton.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }
        btEditHomeScreen.setOnClickListener {
            val newIntent = Intent(this, EditHomeScreenActivity::class.java)
            startActivityForResult(newIntent, 0)
        }
    }

    private fun settingViewModel(homeAdapter: HomeAdapter) {
        viewModel.updateRecipesToShow()

        viewModel.homeRecipeSets.observe(this, Observer {
            homeAdapter.setRecipes(it)
        })
    }

    private fun settingRecyclerView(): HomeAdapter {
        val homeAdapter = HomeAdapter({
            val intent = RecipeDetailsActivity.getStartIntent(this@HomeActivity, it)
            val reqCode = 1
            this@HomeActivity.startActivityForResult(intent, reqCode)
        }, {
            val intent = CategoryActivity.getStartActivity(this, it.categoryName)
            startActivity(intent)
        })
        with(rvRecipesHomeActivity) {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            setHasFixedSize(true)
            adapter = homeAdapter
        }
        return homeAdapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == AppConstantCodes.RETURN_UPDATED_RECIPE) {
            //update recipes
            viewModel.updateRecipesToShow()
        }
        if (resultCode == AppConstantCodes.DELETE_RECIPE) {
            Snackbar.make(
                ScrollViewMainActivity,
                R.string.success_deleting_recipe,
                Snackbar.LENGTH_SHORT
            ).show()
            //update recipes
            viewModel.updateRecipesToShow()
        }
        if (resultCode == AppConstantCodes.CHANGES_MADE_EDIT_HOME) {
            Snackbar.make(
                ScrollViewMainActivity,
                R.string.success_updating_home_screen,
                Snackbar.LENGTH_SHORT
            ).show()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}
