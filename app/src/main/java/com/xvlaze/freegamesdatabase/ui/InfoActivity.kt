package com.xvlaze.freegamesdatabase.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View.GONE
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.squareup.picasso.Picasso
import com.xvlaze.freegamesdatabase.R
import com.xvlaze.freegamesdatabase.adapters.SuggestionsAdapter
import com.xvlaze.freegamesdatabase.databinding.ActivityInfoBinding
import com.xvlaze.freegamesdatabase.util.Decorators

class InfoActivity: AppCompatActivity() {
    private lateinit var binding: ActivityInfoBinding
    private val myAdapter = SuggestionsAdapter()
    private var id: Int = -1
    private lateinit var viewModel: InfoViewModel

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            InfoViewModel.MyViewModelFactory(application)
        ).get(
            InfoViewModel::class.java)

        Picasso.get().load(intent.getStringExtra("game_thumbnail")).into(binding.gameThumbnail)
        id = intent.getIntExtra("id", -1)
        binding.gameTitle.text = intent.getStringExtra("game_title")
        binding.gameDesc.text = intent.getStringExtra("game_desc")
        binding.gameDeveloper.text = intent.getStringExtra("game_developer")
        binding.gamePublisher.text = intent.getStringExtra("game_publisher")
        binding.gameReleaseDate.text = intent.getStringExtra("game_release_date")
        binding.gameGenre.text = intent.getStringExtra("game_genre")
        binding.gameGenre.backgroundTintList = intent.getStringExtra("game_genre")?.let { Decorators.colorize(this, it) }

        myAdapter.setOnItemClickListener(object: SuggestionsAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                viewModel.showGameInfo(myAdapter.getGamesList()[position].getId())
            }
        })

        viewModel.retrieveGamelist()
        viewModel.gameList.observe(this, {
            viewModel.isGameWishlisted(id)
        })

        viewModel.gameDetail.observe(this, {
            Intent(this, InfoActivity::class.java).apply {
                putExtra("id", it.getId())
                putExtra("game_thumbnail", it.getThumbnail())
                putExtra("game_title", it.getTitle())
                putExtra("game_desc", it.getDescription())
                putExtra("game_developer", it.getDeveloper())
                putExtra("game_publisher", it.getPublisher())
                putExtra("game_release_date", it.getReleaseDate())
                putExtra("game_genre", it.getGenre())
                putExtra("suggest_more", false)
                startActivity(this)
            }
        })

        if (intent.getBooleanExtra("suggest_more", true)) {
            binding.rvSuggested.apply {
                adapter = myAdapter
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                addItemDecoration(Decorators.Decorator(15, false))
            }
            viewModel.suggestGames(
                intent.getIntExtra(
                    "id",
                    0
                ),
                intent.getStringExtra("game_genre")
            )
            viewModel.suggestedGames.observe(this, {
                myAdapter.setGamesList(it)
            })
        }
        else {
            binding.otherGames.visibility = GONE
        }

        viewModel.isWishlisted.observe(this, { isWishlisted ->
            if (isWishlisted) {
                binding.wishlistBtn.setImageResource(R.drawable.ic_baseline_favorite_24)
            }
            else {
                binding.wishlistBtn.setImageResource(R.drawable.ic_baseline_favorite_border_24)
            }
        })

        binding.wishlistBtn.setOnClickListener {
            viewModel.handleButton(id)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        viewModel.resetGameDetail()
        finish()
    }
}