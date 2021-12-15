package com.example.musicplayer.domain.repo

import com.example.musicplayer.data.dao.Song
import com.example.musicplayer.data.db.SongDao
import javax.inject.Inject

class SongListRepo @Inject constructor(private val songDao: SongDao) {

    suspend fun getAllSongsByFolderName(folderName: String): List<Song> =
        songDao.getAllSongsByFolderName(folderName)

}