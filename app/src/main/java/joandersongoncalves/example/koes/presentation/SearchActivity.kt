package joandersongoncalves.example.koes.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import joandersongoncalves.example.koes.R
import joandersongoncalves.example.koes.presentation.adapter.SearchResultAdapter
import joandersongoncalves.example.koes.presentation.viewmodel.SearchViewModel
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.app_toolbar.*
import kotlinx.android.synthetic.main.include_previous_search.*


class SearchActivity : AppCompatActivity() {
    private val viewModel: SearchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val adapter = settingRecyclerView()

        settingViewModel(adapter)

        manageSearch()

        settingPreviousSearches()

        layoutForPreviousSearch.visibility = View.VISIBLE

        showKeyboard()
        //setting viewFlipper
        viewFlipperSearch.displayedChild = 0

        settingToolbar()
    }

    private fun settingPreviousSearches() {
        //update previous search
        viewModel.getPreviousSearchEntries()

        //onclick actions
        layoutPreviousEntry1.setOnClickListener {
            //insert into searchBar
            etSearch.setText(tvPreviousSearch1.text.toString())
            viewModel.query.value = tvPreviousSearch1.text.toString()
        }
        layoutPreviousEntry2.setOnClickListener {
            //insert into searchBar
            etSearch.setText(tvPreviousSearch2.text.toString())
            viewModel.query.value = tvPreviousSearch2.text.toString()
        }
        layoutPreviousEntry3.setOnClickListener {
            //insert into searchBar
            etSearch.setText(tvPreviousSearch3.text.toString())
            viewModel.query.value = tvPreviousSearch3.text.toString()
        }

        ibDeletePreviousSearchEntry1.setOnClickListener {
            //delete search history entry
            viewModel.deleteSearchHistoryEntry(tvPreviousSearch1.text.toString())
        }

        ibDeletePreviousSearchEntry2.setOnClickListener {
            //delete search history entry
            viewModel.deleteSearchHistoryEntry(tvPreviousSearch2.text.toString())
        }

        ibDeletePreviousSearchEntry3.setOnClickListener {
            //delete search history entry
            viewModel.deleteSearchHistoryEntry(tvPreviousSearch3.text.toString())
        }
    }

    private fun showKeyboard() {
        val handlerUI = Handler(Looper.getMainLooper())
        val r = Runnable {
            etSearch.requestFocus()
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }
        handlerUI.post(r)
    }

    private fun settingToolbar() {
        viewFlipperAppToolbar.displayedChild = 2
        setSupportActionBar(appToolbarSearch)
        appToolbarSearch.title = ""
        AppToolbarSetup.setBackButton(appToolbarSearch, this)
    }

    private fun settingViewModel(searchResultAdapter: SearchResultAdapter) {
        viewModel.recipesResult.observe(this, {
            searchResultAdapter.updateRecipes(it)
            if (viewFlipperSearch.displayedChild > 0 && it.isEmpty()) {
                //no results found
                viewFlipperSearch.displayedChild = 3
            }
            if (viewFlipperSearch.displayedChild == 1) {
                viewFlipperSearch.displayedChild = 2
            }
        })

        viewModel.query.observe(this, {
            if (it == "") {
                //show previous search history
                if (thereIsSearchHistoryToBeShown()) {
                    layoutForPreviousSearch.visibility = View.VISIBLE
                }
                //change drawable end icon
                appSearchbarLayout.endIconDrawable = ContextCompat.getDrawable(
                    this@SearchActivity,
                    R.drawable.ic_search_24dp
                )
            } else {
                //change drawable end icon
                appSearchbarLayout.endIconDrawable = ContextCompat.getDrawable(
                    this@SearchActivity,
                    R.drawable.ic_baseline_clear_24dp
                )
            }
        })

        viewModel.previousSearchEntries.observe(this, {
            if (thereIsSearchHistoryToBeShown()) {
                layoutForPreviousSearch.visibility = View.VISIBLE
            } else {
                layoutForPreviousSearch.visibility = View.GONE
            }
            if (it.size >= 3) {
                tvPreviousSearch1.text = it[2]
                layoutPreviousEntry1.visibility = View.VISIBLE
            } else {
                layoutPreviousEntry1.visibility = View.GONE
            }
            if (it.size >= 2) {
                tvPreviousSearch2.text = it[1]
                layoutPreviousEntry2.visibility = View.VISIBLE
            } else {
                layoutPreviousEntry2.visibility = View.GONE
            }
            if (it.isNotEmpty()) {
                tvPreviousSearch3.text = it[0]
                layoutPreviousEntry3.visibility = View.VISIBLE
            } else {
                layoutPreviousEntry3.visibility = View.GONE
            }
        })
    }

    private fun thereIsSearchHistoryToBeShown(): Boolean {
        return viewModel.previousSearchEntries.value!!.isNotEmpty()
    }

    private fun settingRecyclerView(): SearchResultAdapter {
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
        return searchResultAdapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppConstantCodes.RETURN_UPDATED_RECIPE) {
            viewModel.searchForResults()
        }

    }

    private fun performSearch() {
        //show loading screen
        viewFlipperSearch.displayedChild = 1
        viewModel.searchForResults()
        // Hiding keyboard
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(etSearch?.applicationWindowToken, 0)
    }

    private fun manageSearch() {
        appSearchbarLayout.setEndIconOnClickListener {
            etSearch.setText("")
            viewModel.query.value = ""
            showKeyboard()
            viewModel.getPreviousSearchEntries()
        }

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.query.value = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                layoutForPreviousSearch.visibility = View.GONE
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
    }
}

