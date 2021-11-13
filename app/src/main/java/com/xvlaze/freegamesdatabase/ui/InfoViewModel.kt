package com.xvlaze.freegamesdatabase.ui

import android.app.Application
import android.os.Build
import android.widget.ImageButton
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.databinding.BindingAdapter
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xvlaze.freegamesdatabase.model.Game
import com.xvlaze.freegamesdatabase.model.GameResponse
import com.xvlaze.freegamesdatabase.repository.MainRepository
import com.xvlaze.freegamesdatabase.api.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.random.Random

class InfoViewModel (application: Application): AndroidViewModel(application) {

    private val repository = MainRepository(RetrofitService.getInstance(), application.applicationContext)
    val gameList = MutableLiveData<ArrayList<Game>>()
    var gameDetail = MutableLiveData<Game>()
    val suggestedGames = MutableLiveData<ArrayList<Game>>()
    val errorMessage = MutableLiveData<String>()
    private val wishlist = MutableLiveData<ArrayList<Game>>()
    val isWishlisted = MutableLiveData<Boolean>()
    val MAX_SUGGESTIONS = 15

    fun retrieveGamelist() { // No importa el nombre que tenga, pero es más fácil de recordar si mantenemos los nombres de método de las funciones de nuestra API.
        val response = repository.retrieveGamelist()
        response.enqueue(object: Callback<GameResponse> { // TODO: Esto lo tengo que mirar.
            override fun onResponse(call: Call<GameResponse>, response: Response<GameResponse>) {
                val games = ArrayList<Game>()
                for (gameResponse in response.body()!!) {
                    games.add(Game(gameResponse))
                }
                gameList.postValue(games)
            }

            override fun onFailure(call: Call<GameResponse>, t: Throwable) {
                errorMessage.postValue(t.message)
            }
        })
    }

    fun showGameInfo(id: Int) {
        gameList.value!!.forEach { game ->
            if (game.getId() == id) {
                gameDetail.postValue(game)
                return
            }
        }
    }

    fun suggestGames(id: Int, genre: String?) {
        val response = repository.suggestGames(genre?.lowercase())
        response.enqueue(object: Callback<GameResponse> {
            override fun onResponse(call: Call<GameResponse>, response: Response<GameResponse>) {
                val suggestions = ArrayList<Game>()
                val suggestionsNumber = if (response.body()?.size ?: 0 < MAX_SUGGESTIONS + 1) {
                    response.body()?.size ?: 0
                } else MAX_SUGGESTIONS + 1

                if (suggestionsNumber > 0) {
                    val numbers = ArrayList<Int>(suggestionsNumber - 1)
                    for (i in 1 until suggestionsNumber) {
                        response.body()?.let { gamesResponse ->
                            var randomId: Int
                            do {
                                randomId =
                                    gamesResponse.map { randomGame -> randomGame.id }.random()
                            } while (randomId == id || randomId == 0 || numbers.contains(randomId))
                            numbers.add(randomId)
                        }
                    }
                    numbers
                        .map { j -> response.body()?.first { it -> it.id == j } }
                        .forEach { element ->
                            element?.let { Game(it) }?.let { suggestions.add(it) }
                        }
                    suggestedGames.postValue(suggestions)
                }
            }

            override fun onFailure(call: Call<GameResponse>, t: Throwable) {
                errorMessage.postValue(t.message)
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun wishlistGame(id: Int) {
        repository.wishlistGame(id)
        isWishlisted.postValue(true)
        wishlist.postValue(wishlistToGameList())
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun unwishlistGame(id: Int) {
        repository.unwishlistGame(id)
        isWishlisted.postValue(false)
        wishlist.postValue(wishlistToGameList())
    }

    @BindingAdapter("load")
    fun setImageViewResource(imageButton: ImageButton, resource: Int) {
        imageButton.setImageResource(resource)
    }

    fun isGameWishlisted(id: Int) {
        isWishlisted.postValue(repository.isGameWishlisted(id))
    }

    private fun wishlistToGameList(): ArrayList<Game> {
        val wishlistedGames = ArrayList<Game>()
        gameList.value!!.forEach { game ->
            repository.getWishlist().forEach { gameId ->
                if (game.getId() == gameId) {
                    wishlistedGames.add(game)
                }
            }
        }
        return wishlistedGames
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun handleButton(id: Int) {
        if (!repository.isGameWishlisted(id)) {
            wishlistGame(id)
        }
        else {
            unwishlistGame(id)
        }
    }

    fun resetGameDetail() {
        gameDetail = MutableLiveData<Game>()
    }

    class MyViewModelFactory(val app: Application): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when {
                modelClass.isAssignableFrom(InfoViewModel::class.java) -> InfoViewModel(app) as T
                else -> throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }
}