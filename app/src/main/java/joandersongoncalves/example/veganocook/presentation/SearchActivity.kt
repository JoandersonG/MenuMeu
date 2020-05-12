package joandersongoncalves.example.veganocook.presentation

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import joandersongoncalves.example.veganocook.R
import joandersongoncalves.example.veganocook.data.model.YouTubeVideo
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.app_toolbar.*

class SearchActivity : AppCompatActivity() {
    private var searchView: SearchView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        //setting RecyclerView
        with(rvSearchResults) {
            layoutManager = androidx.recyclerview.widget.GridLayoutManager(this@SearchActivity, 2)
            setHasFixedSize(true)
            adapter = joandersongoncalves.example.veganocook.presentation.adapter.RecipeAdapter(
                videoExemples()
            ) { youtubeVideo ->
                val intent = RecipeDetailsActivity.getStartIntent(this@SearchActivity, youtubeVideo)
                this@SearchActivity.startActivity(intent)

            }
        }

        //setting SearchActivity toolbar
        viewFlipperAppToolbar.displayedChild = 2
        setSupportActionBar(appToolbarSearch)
        appToolbarSearch.title = ""
        AppToolbarSetup.setBackButton(appToolbarSearch, this)

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
                println("Query: $query")
                Toast.makeText(this@SearchActivity, "Query: $query", Toast.LENGTH_LONG).show()

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


    private fun videoExemples(): List<YouTubeVideo> {
        return listOf(
            YouTubeVideo("Nhoque delicioso", "Este é um nhoque divino...", null, null),
            YouTubeVideo(
                "Lasanha de beringela",
                "Este é uma lasanha de comer rezando...",
                null,
                null
            ),
            YouTubeVideo(
                "Farofa proteica",
                "Esta farofa vai te fazer chorar de alegria...",
                null,
                null
            ),
            YouTubeVideo(
                "Smoothie verde",
                "Este smoothie vai te fazer muito mais saudável...",
                null,
                null
            ),
            YouTubeVideo("Pão vegano fácil", "Este pão vai te fazer sonhar acordado...", null, null)
        )
    }

}
