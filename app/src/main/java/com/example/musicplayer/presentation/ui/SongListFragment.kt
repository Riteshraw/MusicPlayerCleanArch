package com.example.musicplayer.presentation.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.MusicPlayerApplication
import com.example.musicplayer.R
import com.example.musicplayer.presentation.adapter.SongClickListener
import com.example.musicplayer.presentation.adapter.SongListAdapter
import com.example.musicplayer.data.dao.Song
import com.example.musicplayer.presentation.di.FolderViewModelFactory
import com.example.musicplayer.presentation.vm.FolderListVM
import javax.inject.Inject

class SongListFragment : Fragment() {
    //private lateinit var viewModel: SongListVM
    private lateinit var viewModel: FolderListVM

    //private lateinit var viewModelFactory: VMFactory
    private lateinit var songList: List<Song>

    @Inject
    lateinit var vmf: FolderViewModelFactory

    override fun onAttach(context: Context) {
        (requireActivity().application as MusicPlayerApplication).appComponent.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        /*viewModelFactory = VMFactory(
            (requireActivity().application as MusicPlayerApplication).repoMusic,
            requireActivity().application
        )*/
        //viewModel = SongListVM by viewModels { viewModelFactory }
        //viewModel = ViewModelProvider(this, viewModelFactory).get(SongListVM::class.java)

        viewModel = ViewModelProvider(requireActivity(), vmf)[FolderListVM::class.java]
        Log.v("TAG_VM", "SongListFragment folderVM : $viewModel")
//        viewModel = ViewModelProvider(this,vmf)[SongListVM::class.java]
//        Log.v("TAG_VM", "SongListFragment songVM : $viewModel")


        viewModel.getAllSongsByFolderName(SongListFragmentArgs.fromBundle(requireArguments()).folderName)

        return inflater.inflate(R.layout.fragment_song_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialiseRecyclerview(view)
    }

    private fun initialiseRecyclerview(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.song_list)
        val songAdapter = SongListAdapter(SongClickListener { song, position ->
            //gotoPlayerFragment(song,position)
            updatePositionInVM(position)
        })

        recyclerView.adapter = songAdapter
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                recyclerView.context,
                DividerItemDecoration.VERTICAL
            )
        )

        viewModel.songListLiveData.observe(viewLifecycleOwner, {
            //activity?.showToast(it.size.toString())
            songList = it
            songAdapter.submitList(it)
        })

    }

    private fun gotoPlayerFragment(song: Song, position: Int) {
        //activity?.showToast("Song : ${song.name}")

        val action = SongListFragmentDirections.actionSongListFragmentToPlayerFragment()
        action.position = position
        action.song = song
        action.songList = songList.toTypedArray()
        NavHostFragment.findNavController(this).navigate(action)

    }

    private fun updatePositionInVM(position: Int) {
        viewModel.setSongByPosition(position)
    }
}