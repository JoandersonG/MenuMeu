package joandersongoncalves.example.veganocook.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import joandersongoncalves.example.veganocook.R
import joandersongoncalves.example.veganocook.presentation.adapter.HomeAdapter
import joandersongoncalves.example.veganocook.presentation.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_toolbar.*

class HomeActivity : AppCompatActivity() {

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        //setting RecyclerView
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

        viewModel.updateRecipesToShow()

        viewModel.homeRecipeSets.observe(this, Observer {
            homeAdapter.setRecipes(it)
        })

        //setting home screen buttons
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

        setSupportActionBar(appToolbarHome);
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RecipeDetailsActivity.RETURN_UPDATED_RECIPE) {
            //update recipes
            viewModel.updateRecipesToShow()
        }
        if (resultCode == RecipeDetailsActivity.DELETE_RECIPE) {
            Snackbar.make(
                ScrollViewMainActivity,
                R.string.success_deleting_recipe,
                Snackbar.LENGTH_SHORT
            ).show()
            //update recipes
            viewModel.updateRecipesToShow()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}
