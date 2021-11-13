package com.xvlaze.freegamesdatabase.model

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

@Serializable
object Wishlist {
    private lateinit var wishlist: ArrayList<Int>
    private val format = Json { prettyPrint = true }

    fun setup(c: Context) {
        val dir = c.filesDir.path + "/favs.json"

        if (Files.exists(Paths.get(dir))) {
            wishlist = format.decodeFromString(File(dir).readText(Charsets.UTF_8))
        }
        else {
            wishlist = ArrayList()
            val jsonString = format.encodeToString(wishlist)
            File(dir).writeText(jsonString)
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun serialize(c: Context) {
        val jsonString = format.encodeToString(wishlist)
        File(c.filesDir.path + "/favs.json").writeText(jsonString)
    }

    fun add(id: Int) {
        wishlist.add(id)
    }

    fun remove(id: Int) {
        wishlist.remove(id)
    }

    fun getWishlist(c: Context): ArrayList<Int> {
        return format.decodeFromString(File(c.filesDir.path + "/favs.json").readText(Charsets.UTF_8))
    }
}