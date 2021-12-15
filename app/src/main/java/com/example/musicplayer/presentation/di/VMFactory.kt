package com.example.musicplayer.presentation.di

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.musicplayer.data.dao.Song
import com.example.musicplayer.domain.repo.MusicRepo
import com.example.musicplayer.presentation.vm.SongListVM

/*@Singleton*/
class VMFactory /*@Inject constructor*/(private val repository: MusicRepo, private val context: Application) :
    ViewModelProvider.Factory {
    private lateinit var songlist:List<Song>
    private var pos:Int =0

    constructor(songList:List<Song>, position:Int,repository: MusicRepo,context: Application) : this(repository,context){
        songlist = songList
        pos = position
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            //modelClass.isAssignableFrom(FolderListVM::class.java) -> FolderListVM(repository, context) as T
            modelClass.isAssignableFrom(SongListVM::class.java) -> SongListVM(repository,context) as T
            //modelClass.isAssignableFrom(PlayerVM::class.java) -> PlayerVM(songlist,pos,context) as T
            else -> throw java.lang.IllegalArgumentException("ViewModel class not found")
        }
    }

}