package com.example.musicplayer.presentation.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.MusicPlayerApplication
import com.example.musicplayer.R
import com.example.musicplayer.presentation.adapter.FolderClickListener
import com.example.musicplayer.presentation.adapter.FolderListAdapter
import com.example.musicplayer.data.dao.Folder
import com.example.musicplayer.databinding.FragmentFolderListBinding
import com.example.musicplayer.presentation.di.FolderViewModelFactory
import com.example.musicplayer.domain.repo.MusicRepo
import com.example.musicplayer.presentation.vm.FolderListVM
import javax.inject.Inject

class FolderListFragment : Fragment() {
    @Inject
    lateinit var viewModelFactory: FolderViewModelFactory

    @Inject
    lateinit var repo: MusicRepo
    private lateinit var recyclerView: RecyclerView
    private lateinit var binding: FragmentFolderListBinding
    private lateinit var position: Integer
    private lateinit var vm: FolderListVM
    private lateinit var folderAdapter: FolderListAdapter

    override fun onAttach(context: Context) {
        (requireActivity().application as MusicPlayerApplication).appComponent.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return if (::binding.isInitialized)
            binding.root
        else {
            binding =
                DataBindingUtil.inflate(inflater, R.layout.fragment_folder_list, container, false)
            binding.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //val vm: FolderListVM by activityViewModels()
        //val vm = (requireActivity() as MainActivity).vm
        if (::vm.isInitialized) {
            Log.v("TAG_VM", "FolderListFrag : $vm")
        } else {
            vm = ViewModelProvider(requireActivity(), viewModelFactory)[FolderListVM::class.java]
            binding.vm = vm
            Log.v("TAG_VM", "FolderListFrag : $vm")
//            initialiseRecyclerview(view, vm)
            Log.v("TAG_VM", "FolderListFrag Repo : $repo")
        }
        initialiseRecyclerview(view, vm)
    }

    private fun initialiseRecyclerview(view: View, vm: FolderListVM) {
        if (::position.isInitialized) {
            recyclerView.scrollToPosition(position as Int)
        } else {
            recyclerView = view.findViewById<RecyclerView>(R.id.folder_list)
            folderAdapter = FolderListAdapter(FolderClickListener { folder, position ->
                this.position = position
                gotoSongListFragment(folder)
            })

            recyclerView.adapter = folderAdapter
            recyclerView.layoutManager = LinearLayoutManager(activity)
            recyclerView.addItemDecoration(
                DividerItemDecoration(
                    recyclerView.context,
                    DividerItemDecoration.VERTICAL
                )
            )

            //vm.getAllFolders()

            /*vm.allFoldersList.observe(viewLifecycleOwner, {
                if (it == null) {
                    vm.getSongsByContentProvider()
                } else {
                    //activity?.showToast(it.size.toString())
                    folderAdapter.submitList(it)
                }
            })*/
        }
        vm.allFoldersList.observe(viewLifecycleOwner, {
            if (it == null) {
                vm.getSongsByContentProvider()
            } else {
                //activity?.showToast(it.size.toString())
                folderAdapter.submitList(it)
            }
        })
    }

    private fun gotoSongListFragment(folder: Folder) {
        //activity?.showToast("Folder Name : ${folder.name}")

        val action = FolderListFragmentDirections.actionFolderListFragmentToSongListFragment()
        action.folderName = folder.name
        NavHostFragment.findNavController(this).navigate(action)
    }

}