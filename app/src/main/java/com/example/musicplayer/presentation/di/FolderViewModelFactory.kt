package com.example.musicplayer.presentation.di

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.musicplayer.data.dataProviders.SongDataProvider
import com.example.musicplayer.domain.repo.MusicRepo
import com.example.musicplayer.presentation.vm.FolderListVM
import com.example.musicplayer.presentation.vm.PlayerVM
import com.example.musicplayer.presentation.vm.SongListVM
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FolderViewModelFactory @Inject constructor(
    private val repository: MusicRepo,
    private val songDataProvider: SongDataProvider,
    private val application: Application
) : ViewModelProvider.Factory {

    /*override fun <T : ViewModel?> create(modelClass: Class<T>): T  =
        FolderListVM(repository, application) as T*/

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(FolderListVM::class.java) -> FolderListVM(repository,songDataProvider,application) as T
            modelClass.isAssignableFrom(SongListVM::class.java) -> SongListVM(repository,application) as T
            modelClass.isAssignableFrom(PlayerVM::class.java) -> PlayerVM(application) as T
            else -> throw java.lang.IllegalArgumentException("ViewModel class not found")
        }
    }
}




