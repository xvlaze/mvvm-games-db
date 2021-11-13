package com.xvlaze.freegamesdatabase.ui

import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.SearchView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.chip.Chip
import com.xvlaze.freegamesdatabase.R
import com.xvlaze.freegamesdatabase.databinding.ActivityMainBinding
import com.xvlaze.freegamesdatabase.util.Decorators

class MainActivity: AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var viewModel: MainViewModel

    private val selectedGenres = ArrayList<String>()

    private val gameListFragment = GameListFragment()
    private val wishlistFragment = WishlistFragment()
    private lateinit var searchView: SearchView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*
        Creamos el ViewModel que nos ayudará a actualizar la UI.
        */
        viewModel = ViewModelProvider(
            this, // Es para esta actividad.
            MainViewModel.MyViewModelFactory(application) // No podemos instanciar el ViewModel direcatmente, necesitamos una Factory.
        ).get(
            MainViewModel::class.java) // ?

        setSupportActionBar(binding.toolbar)

        binding.filterAll.visibility = GONE
        var noGenresChecked: Boolean
        viewModel.genresList.observe(this, { genres ->
            val genreChips = binding.filterChips
            for (genre in genres) {
                val chip = Chip(this, null, R.style.ChipTextAppearance)
                chip.apply {
                    chipBackgroundColor = Decorators.colorize(applicationContext, genre)
                    text = genre
                    typeface = Typeface.DEFAULT_BOLD
                    setTextColor(resources.getColor(R.color.white, null))
                    id = View.generateViewId()
                    isClickable = true
                    isCheckable = true
                    isCheckedIconVisible = true
                    isFocusable = true
                    setOnCheckedChangeListener { buttonView, isChecked ->
                        val genreString = buttonView.text.toString()
                        when {
                            isChecked -> {
                                selectedGenres.add(genreString)
                            }
                            else -> {
                                selectedGenres.remove(genreString)
                            }
                        }

                        val term = searchView.query.toString()
                        viewModel.filter(selectedGenres, term)
                        noGenresChecked = selectedGenres.isEmpty()
                        binding.filterAll.isChecked = noGenresChecked
                    }
                }
                genreChips.addView(chip)
            }

            binding.filterAll.apply {
                visibility = VISIBLE
                isClickable = true
                isCheckable = true
                isFocusable = true
                isChecked = true
                setOnClickListener {
                    if (!binding.filterAll.isChecked) binding.filterAll.isChecked = true
                    else {
                        resetGenreFilters()
                    }
                }
            }
        })

        viewModel.errorMessage.observe(this, {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        })

        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_out
            )
            .setReorderingAllowed(true)
            .replace(R.id.mainFragment, gameListFragment)
            .commit()
        viewModel.setCurrentFragment(R.id.gameListFragment)

        viewModel.currentFragment.observe(this, { fragmentId ->
            when (fragmentId) {
                R.id.gameListFragment -> {
                    binding.fragmentName.text = "All Games"
                }
                R.id.wishlistFragment -> {
                    binding.fragmentName.text = "Wishlist"
                }
            }
        })

        /*
        Llamamos a la función que queremos que lance el ViewModel. Esto puede hacerse al clic de un botón, etc. En esta app
        queremos que se ejecute al inicio, así que la ponemos aquí mismo.
         */
        viewModel.setup()
    }

    fun resetGenreFilters() {
        selectedGenres.clear()
        binding.filterChips.forEach { chip -> (chip as Chip).isChecked = false }
        val term = searchView.query.toString()
        viewModel.filter(selectedGenres, term)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)

        val search = menu.findItem(R.id.appSearchBar)
        searchView = search.actionView as SearchView
        searchView.queryHint = "Search"
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(term: String?): Boolean {
                viewModel.filter(selectedGenres, term ?: "")
                return true
            }

            override fun onQueryTextChange(term: String): Boolean {
                viewModel.filter(selectedGenres, term)
                return true
            }
        })

        val wishlistButton = menu.findItem(R.id.wishlist_btn)
        wishlistButton.setOnMenuItemClickListener {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.slide_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.slide_out
                )
                .setReorderingAllowed(true)
                .replace(R.id.mainFragment, wishlistFragment)
                .addToBackStack(gameListFragment.javaClass.name)
                .commit()
            viewModel.setCurrentFragment(R.id.wishlistFragment)
            resetGenreFilters()
            true
        }
        return super.onCreateOptionsMenu(menu)
    }
}