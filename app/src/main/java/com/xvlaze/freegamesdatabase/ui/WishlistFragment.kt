package com.xvlaze.freegamesdatabase.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.xvlaze.freegamesdatabase.R
import com.xvlaze.freegamesdatabase.adapters.GamesAdapter
import com.xvlaze.freegamesdatabase.databinding.WishlistFragmentBinding
import com.xvlaze.freegamesdatabase.util.Decorators


class WishlistFragment : Fragment() {

    companion object {
        fun newInstance() = WishlistFragment()
    }

    private lateinit var viewModel: MainViewModel
    private var _binding: WishlistFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: GamesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        setHasOptionsMenu(true)
        _binding = WishlistFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.setCurrentFragment(R.id.gameListFragment)
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = activity?.let { ViewModelProvider(it).get(MainViewModel::class.java) }!!
        watchWatchlist()
        viewModel.retrieveGamelist()
        binding.rvWishlist.addItemDecoration(Decorators.Decorator(15, true))
    }

    override fun onResume() {
        super.onResume()
        watchWatchlist()
    }

    private fun watchWatchlist() {
        viewModel.filteredGameList.observe(viewLifecycleOwner, {
            viewModel.showWishlist()
            viewModel.wishlist.observe(viewLifecycleOwner, {
                adapter = GamesAdapter(it)
                adapter.setOnItemClickListener(object: GamesAdapter.OnItemClickListener {
                    override fun onItemClick(position: Int) {
                        viewModel.showGameInfo(adapter.getGamesList()[position].getId())
                        viewModel.gameDetail.observe(viewLifecycleOwner, {
                            Intent(activity, InfoActivity::class.java).apply {
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
                            viewModel.resetGameDetail()
                        })
                    }
                })
                binding.rvWishlist.adapter = adapter
            })
        })
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val item = menu.findItem(R.id.wishlist_btn)
        item.isVisible = false
    }
}