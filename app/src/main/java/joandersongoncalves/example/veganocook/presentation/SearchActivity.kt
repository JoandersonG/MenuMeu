package joandersongoncalves.example.veganocook.presentation

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import joandersongoncalves.example.veganocook.R
import joandersongoncalves.example.veganocook.presentation.adapter.SearchResultAdapter
import joandersongoncalves.example.veganocook.presentation.viewmodel.SearchViewModel
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.app_toolbar.*

class SearchActivity : AppCompatActivity() {
    private var searchView: SearchView? = null
    private val viewModel: SearchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        //setting RecyclerView
        val searchResultAdapter = SearchResultAdapter { recipe ->
            val intent = RecipeDetailsActivity.getStartIntent(this@SearchActivity, recipe)
            val reqCode = 1
            this@SearchActivity.startActivityForResult(intent, reqCode)
        }
        with(rvSearchResults) {
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this@SearchActivity)
            setHasFixedSize(true)
            adapter = searchResultAdapter
        }

        //setting viewModel
        viewModel.recipesResult.observe(this, Observer {
            searchResultAdapter.updateRecipes(it)
            if (viewFlipperSearch.displayedChild > 0 && it.isEmpty()) {
                //no results found
                viewFlipperSearch.displayedChild = 3
            }
            if (viewFlipperSearch.displayedChild == 1) {
                viewFlipperSearch.displayedChild = 2
            }
        })

        //setting viewFlipper
        viewFlipperSearch.displayedChild = 0

        //setting SearchActivity toolbar
        viewFlipperAppToolbar.displayedChild = 2
        setSupportActionBar(appToolbarSearch)
        appToolbarSearch.title = ""
        AppToolbarSetup.setBackButton(appToolbarSearch, this)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RecipeDetailsActivity.RETURN_UPDATED_RECIPE) {
            viewModel.query.value?.let { viewModel.queryByName(it) }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search_top_app_bar, menu)

        val searchManager = getSystemService((Context.SEARCH_SERVICE)) as SearchManager

        searchView = menu.findItem(R.id.search).actionView as SearchView
        val searchableInfo = searchManager.getSearchableInfo(componentName)
        searchView?.setSearchableInfo(searchableInfo)
        searchView?.isIconified = false

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    viewModel.query.value = it
                    viewModel.queryByName(it)
                    viewFlipperSearch.displayedChild = 1
                }

                // Escondendo o teclado
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(searchView?.applicationWindowToken, 0)

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        searchView?.setOnCloseListener {
            //tapping on close clears the text
            searchView?.setQuery("", false)
            //finish()
            false
        }

        return true
    }
}
