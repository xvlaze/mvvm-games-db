package com.xvlaze.freegamesdatabase.model

class Game (private val game: GameResponseItem) {
    private var isWishlisted = false

    fun getId() = game.id

    fun getTitle() = game.title

    fun getThumbnail() = game.thumbnail

    fun getDeveloper() = game.developer

    fun getReleaseDate() = game.release_date

    fun getGenre() = game.genre

    fun getPublisher() = game.publisher

    fun getDescription() = game.short_description

    fun getWishlistStatus() = isWishlisted

    fun changeWishlistStatus() {
        isWishlisted = isWishlisted != true
    }
}