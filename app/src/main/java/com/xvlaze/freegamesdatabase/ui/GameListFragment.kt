package com.xvlaze.freegamesdatabase.ui

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import com.xvlaze.freegamesdatabase.R
import com.xvlaze.freegamesdatabase.adapters.GamesAdapter
import com.xvlaze.freegamesdatabase.databinding.GameListFragmentBinding
import com.xvlaze.freegamesdatabase.util.Decorators

class GameListFragment : Fragment() {

    companion object {
        fun newInstance() = GameListFragment()
    }

    private lateinit var viewModel: MainViewModel
    private var _binding: GameListFragmentBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var adapter: GamesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        _binding = GameListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = activity?.let { ViewModelProvider(it).get(MainViewModel::class.java) }!!

        binding.progress.visibility = View.VISIBLE
        binding.rvGames.addItemDecoration(Decorators.Decorator(15, true))

        // Definimos qué va a pasar en la vista cuando un elemento del ViewModel cambie; en este caso, la lista que hemos creado (que recibimos de Retrofit) y un código de error.
        viewModel.filteredGameList.observe(viewLifecycleOwner, {
            // Aquí actualizamos la vista (esta actividad).
            adapter = GamesAdapter(it)
            // Configuramos la recyclerView.
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
                            putExtra("suggest_more", true)
                            startActivity(this)
                        }
                        viewModel.resetGameDetail()
                    })
                }
            })
            binding.rvGames.adapter = adapter
            binding.progress.visibility = View.GONE
        })
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val item = menu.findItem(R.id.wishlist_btn)
        item.isVisible = true
    }
}