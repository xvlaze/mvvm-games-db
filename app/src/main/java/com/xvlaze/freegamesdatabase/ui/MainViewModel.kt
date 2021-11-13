package com.xvlaze.freegamesdatabase.ui

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xvlaze.freegamesdatabase.api.RetrofitService
import com.xvlaze.freegamesdatabase.model.Game
import com.xvlaze.freegamesdatabase.model.GameResponse
import com.xvlaze.freegamesdatabase.repository.MainRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel (application: Application): AndroidViewModel(application) {

    private val repository = MainRepository(RetrofitService.getInstance(), application.applicationContext)
    val fullGameList = MutableLiveData<ArrayList<Game>>()
    val filteredGameList = MutableLiveData<ArrayList<Game>>()
    var gameDetail = MutableLiveData<Game>()
    val errorMessage = MutableLiveData<String>()
    val genresList = MutableLiveData<ArrayList<String>>()
    val wishlist = MutableLiveData<ArrayList<Game>>()
    val currentFragment = MutableLiveData<Int>()

    @RequiresApi(Build.VERSION_CODES.O)
    fun setup() {
        repository.setupWishlist()
        retrieveGamelist()
    }

    fun retrieveGamelist() { // No importa el nombre que tenga, pero es más fácil de recordar si mantenemos los nombres de método de las funciones de nuestra API.
        val response = repository.retrieveGamelist()
        response.enqueue(object: Callback<GameResponse> {
            override fun onResponse(call: Call<GameResponse>, response: Response<GameResponse>) {
                fullGameList.postValue(response.body()?.map { Game(it) } as ArrayList<Game>)
                filteredGameList.postValue(response.body()?.map { Game(it) } as ArrayList<Game>)
                // TODO: Algunas se repiten por culpa del desarrollador de la API.
                genresList.postValue(response.body()?.map { it.genre.trim() }?.distinct() as ArrayList<String>)
            }

            override fun onFailure(call: Call<GameResponse>, t: Throwable) {
                errorMessage.postValue(t.message)
            }
        })
    }

    fun showGameInfo(id: Int) {
        filteredGameList.value!!.forEach { game ->
            if (game.getId() == id) {
                gameDetail.postValue(game)
                return
            }
        }
    }

    fun resetGameDetail() {
        gameDetail = MutableLiveData<Game>()
    }

    fun filter(genres: ArrayList<String>, term: String) {
        filteredGameList.postValue(
            if (genres.isNotEmpty()) {
                (fullGameList.value?.filter {
                    it.getTitle().contains(term, true) &&
                            it.getGenre() in genres
                } as ArrayList<Game>?)}
            else {
                (fullGameList.value?.filter {
                    it.getTitle().contains(term, true)
                } as ArrayList<Game>?)
            } ?: ArrayList())
    }

    private fun wishlistToGameList(): ArrayList<Game> {
        val wishlistedGames = ArrayList<Game>()
        filteredGameList.value!!.map {
            repository.getWishlist()
                .filter { gameId -> it.getId() == gameId }
                .forEach { _ -> wishlistedGames += it }
        }
        return wishlistedGames
    }

    fun showWishlist() {
        wishlist.postValue(wishlistToGameList())
    }

    fun setCurrentFragment(fragmentId: Int) {
        currentFragment.postValue(fragmentId)
    }

    class MyViewModelFactory(val app: Application): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                MainViewModel(app) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }
}