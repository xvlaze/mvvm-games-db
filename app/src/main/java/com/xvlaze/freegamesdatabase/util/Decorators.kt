package com.xvlaze.freegamesdatabase.util

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Rect
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.xvlaze.freegamesdatabase.R

object Decorators {
    class Decorator(private var spaceSize: Int, private var vertical: Boolean): RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect, view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            with(outRect) {
                when {
                    vertical -> {
                        if (parent.getChildAdapterPosition(view) == 0) {
                            top = spaceSize
                        }
                        left = spaceSize
                        right = spaceSize
                        bottom = spaceSize
                    }
                    else -> {
                        if (parent.getChildAdapterPosition(view) == 0) {
                            left = spaceSize
                        }
                        top = spaceSize
                        right = spaceSize
                        bottom = spaceSize
                    }
                }
            }
        }
    }

    fun colorize(context: Context, genre: String): ColorStateList {
        return ColorStateList.valueOf(
            ContextCompat.getColor(
                context,
                when (genre.lowercase()) {
                    "shooter" -> R.color.shooter
                    "racing" -> R.color.racing
                    "mmo" -> R.color.mmo
                    "card game" -> R.color.cards
                    "fighting" -> R.color.fighting
                    "strategy" -> R.color.strategy
                    "mmorpg" -> R.color.mmorpg
                    "moba" -> R.color.moba
                    "social" -> R.color.social
                    else -> R.color.mmorpg
                }
            )
        )
    }
}