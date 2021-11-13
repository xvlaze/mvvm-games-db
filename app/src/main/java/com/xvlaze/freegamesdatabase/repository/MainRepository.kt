package com.xvlaze.freegamesdatabase.repository

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.xvlaze.freegamesdatabase.api.RetrofitService
import com.xvlaze.freegamesdatabase.model.Wishlist

// Esto implementa las funciones de la API que hemos declarado en RetrofitService.kt
class MainRepository (private val retrofitService: RetrofitService, private val c: Context) {

    fun retrieveGamelist() = retrofitService.retrieveGamelist()
    fun suggestGames(genre: String?) = retrofitService.suggestGames("$genre")

    fun setupWishlist() {
        Wishlist.setup(c)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun wishlistGame(id: Int) {
        Wishlist.add(id)
        Wishlist.serialize(c)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun unwishlistGame(id: Int) {
        Wishlist.remove(id)
        Wishlist.serialize(c)
    }

    fun getWishlist(): ArrayList<Int>  {
        return Wishlist.getWishlist(c)
    }

    fun isGameWishlisted(id: Int): Boolean {
        return Wishlist.getWishlist(c).contains(id)
    }
}