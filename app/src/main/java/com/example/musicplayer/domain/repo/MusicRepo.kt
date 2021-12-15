package com.example.musicplayer.domain.repo

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.example.musicplayer.data.dao.Folder
import com.example.musicplayer.data.dao.Song
import com.example.musicplayer.data.db.SongDao
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicRepo @Inject constructor(private val songDao: SongDao) {

    companion object {
        //@Volatile
        private var musicRepoInstance: MusicRepo? = null

        fun getFolderListRepo(songDao: SongDao): MusicRepo {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the object
            return musicRepoInstance ?: MusicRepo(songDao)
        }
    }

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val getAllFolderListByFlow: Flow<List<Folder>> = songDao.getAllFolderListByFlow()

    suspend fun getAllFolderList(): List<Folder> {
        return songDao.getAllFolderList()
    }

    suspend fun getAllFolderListReturnLiveData(): LiveData<List<Folder>> {
        return songDao.getAllFolderListReturnLiveData()
    }

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(song: Song) {
        songDao.insert(song)
    }

    suspend fun insertAllSongs(songList: List<Song>): List<Long>? {
        return songDao.insertAllSongs(songList)
    }

    suspend fun deleteSongsDB() {
        songDao.deleteAll()
    }

    suspend fun getAllSongsByFolderName(folderName: String): List<Song> =
        withContext(IO) {
            songDao.getAllSongsByFolderName(folderName)
        }


}