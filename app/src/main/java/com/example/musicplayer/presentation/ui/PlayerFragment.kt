package com.example.musicplayer.presentation.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.musicplayer.MusicPlayerApplication
import com.example.musicplayer.R
import com.example.musicplayer.data.dao.Song
import com.example.musicplayer.databinding.FragmentPlayerBinding
import com.example.musicplayer.presentation.di.FolderViewModelFactory
import com.example.musicplayer.presentation.vm.FolderListVM
import javax.inject.Inject

class PlayerFragment : Fragment() {
    @Inject
    lateinit var viewModelFactory: FolderViewModelFactory

    private lateinit var vm: FolderListVM
    private lateinit var song: Song
    private var position: Int = 0
    private lateinit var songList: List<Song>
    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        (requireActivity().application as MusicPlayerApplication).appComponent.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        position = PlayerFragmentArgs.fromBundle(requireArguments()).position
        songList = PlayerFragmentArgs.fromBundle(requireArguments()).songList!!.toList()

        //vm = ViewModelProvider(this, viewModelFactory).get(PlayerVM::class.java)
        //Log.v("TAG_VM", "PlayerFragment playerVM : $vm")
        vm = ViewModelProvider(requireActivity(), viewModelFactory).get(FolderListVM::class.java)
        Log.v("TAG_VM", "PlayerFragment playerVM : $vm")
        vm.setPlayerFragmentVisibilityVariable(true)
        setSongListAndPositionInVM(songList, position)
        // Inflate the layout for this fragment
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_player, container, false)
        return binding.root
    }

    private fun setSongListAndPositionInVM(songList: List<Song>, position: Int) {
        vm.setSongListAndPositionInVM(songList, position)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm.releaseResources()

        vm.currentSong.observe(viewLifecycleOwner, Observer {
            _binding?.song = it
            _binding?.vm = vm
            song = it
            vm.releaseResources()
            vm.onPlayClick()
        })

        binding.progressBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar,
                progress: Int,
                fromUser: Boolean
            ) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                vm.seekTo(seekBar.progress)
            }
        })

        vm.setCurrentSong()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //vm.cancelTimer()
        vm.removeObserverFromSong(viewLifecycleOwner)
        _binding = null
    }

    override fun onDestroy() {
        vm.setPlayerFragmentVisibilityVariable(false)
        super.onDestroy()
    }

}