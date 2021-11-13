package com.xvlaze.freegamesdatabase.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.xvlaze.freegamesdatabase.databinding.SuggestionElementBinding
import com.xvlaze.freegamesdatabase.model.Game

class SuggestionsAdapter: RecyclerView.Adapter<SuggestionsAdapter.MainViewHolder>() {
    private var games = mutableListOf<Game>()
    private lateinit var listener: OnItemClickListener

    @SuppressLint("NotifyDataSetChanged")
    fun setGamesList(games: ArrayList<Game>) {
        this.games = games.toMutableList()
        notifyDataSetChanged()
    }

    fun getGamesList(): List<Game> {
        return games
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = SuggestionElementBinding.inflate(layoutInflater, parent, false)
        return MainViewHolder(binding, listener)
    }

    override fun getItemCount(): Int = games.size

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val game = games[position]
        Picasso.get().load(game.getThumbnail()).into(holder.binding.gameThumbnail)
        holder.binding.gameTitle.text = game.getTitle()
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    class MainViewHolder(val binding: SuggestionElementBinding, listener: OnItemClickListener): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener{
                listener.onItemClick(adapterPosition)
            }
        }
    }
}

