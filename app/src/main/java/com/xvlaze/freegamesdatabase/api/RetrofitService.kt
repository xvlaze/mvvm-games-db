package com.xvlaze.freegamesdatabase.api

import com.xvlaze.freegamesdatabase.model.GameResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitService {
    @GET("games")
    fun retrieveGamelist(): Call<GameResponse>
    @GET("games")
    fun suggestGames(@Query("category") category: String): Call<GameResponse>

    companion object {
        private var retrofitService: RetrofitService? = null

        fun getInstance(): RetrofitService {
            if (retrofitService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://www.freetogame.com/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                retrofitService = retrofit.create(RetrofitService::class.java)
            }
            return retrofitService!!
        }
    }
}