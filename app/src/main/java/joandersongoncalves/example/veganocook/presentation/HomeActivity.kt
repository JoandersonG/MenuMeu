package joandersongoncalves.example.veganocook.presentation

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.youtube.player.YouTubeBaseActivity
import joandersongoncalves.example.veganocook.R
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_toolbar.*

class HomeActivity : YouTubeBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        btBreakfest.setOnClickListener {
            val intent = CategoryActivity.getStartActivity(this, getString(R.string.breakfest))
            startActivity(intent)
        }

        btLunch.setOnClickListener {
            val intent = CategoryActivity.getStartActivity(this, getString(R.string.lunch))
            startActivity(intent)
        }

        btDinner.setOnClickListener {
            val intent = CategoryActivity.getStartActivity(this, getString(R.string.dinner))
            startActivity(intent)
        }

        btSnack.setOnClickListener {
            val intent = CategoryActivity.getStartActivity(this, getString(R.string.snack))
            startActivity(intent)
        }

        btAllRecipies.setOnClickListener {
            val intent = CategoryActivity.getStartActivity(this, getString(R.string.all_recipes))
            startActivity(intent)
        }

        btAddRecipe.setOnClickListener {
            startActivity(Intent(this, CreateRecipeActivity::class.java))
        }

        appToolbarHome.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.searchIcon -> {
                    startActivity(Intent(this, SearchActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        println("onCreateOptionsMenu entrou")
        menuInflater.inflate(R.menu.home_top_app_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        println("onOptionsItemSelected entrou")
        return when (item.itemId) {
            R.id.searchIcon -> {
                println("searchIcon entrou")
                startActivity(Intent(this, SearchActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
