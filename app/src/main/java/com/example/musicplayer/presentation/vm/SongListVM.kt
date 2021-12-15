package com.example.musicplayer.presentation.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.data.dao.Song
import com.example.musicplayer.domain.repo.MusicRepo
import kotlinx.coroutines.launch

class SongListVM(private val repository: MusicRepo, application: Application) :
    AndroidViewModel(application) {

    private var _songList = MutableLiveData<List<Song>>()
    val songList: LiveData<List<Song>>
        get() = _songList

    fun getAllSongsByFolderName(folderName: String) {
        viewModelScope.launch {
            _songList.postValue(repository.getAllSongsByFolderName(folderName))
        }
    }

}