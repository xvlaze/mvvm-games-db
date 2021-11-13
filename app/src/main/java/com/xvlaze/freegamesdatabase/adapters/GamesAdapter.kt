package com.xvlaze.freegamesdatabase.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.xvlaze.freegamesdatabase.databinding.GameElementBinding
import com.xvlaze.freegamesdatabase.model.Game
import com.xvlaze.freegamesdatabase.util.Decorators

class GamesAdapter(games: MutableList<Game>): RecyclerView.Adapter<GamesAdapter.MainViewHolder>() {
    private lateinit var listener: OnItemClickListener
    private var gamesFilterList = mutableListOf<Game>()

    init {
        gamesFilterList = games
    }

    fun getGamesList(): List<Game> {
        return gamesFilterList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = GameElementBinding.inflate(layoutInflater, parent, false)
        return MainViewHolder(binding, listener)
    }

    override fun getItemCount(): Int = gamesFilterList.size

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val game = gamesFilterList[position]
        Picasso.get().load(game.getThumbnail()).into(holder.binding.gameThumbnail)
        holder.binding.gameTitle.text = game.getTitle()
        holder.binding.gameDesc.text = game.getDescription()
        holder.binding.gameGenre.text = game.getGenre()
        holder.binding.gameGenre.backgroundTintList = Decorators.colorize(holder.itemView.context, game.getGenre())
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    class MainViewHolder(val binding: GameElementBinding, listener: OnItemClickListener): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener{
                listener.onItemClick(adapterPosition)
            }
        }
    }
}

