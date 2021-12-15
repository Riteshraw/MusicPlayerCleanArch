package com.example.musicplayer.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.musicplayer.data.dao.Folder
import com.example.musicplayer.data.dao.Song
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {

    @Query("SELECT * FROM song_table ORDER BY rowid ASC")
    fun getAllSongs(): List<Song>

    @Query("SELECT folder as name, count(name) as songCount FROM song_table GROUP BY folder ORDER BY name ASC;")
    suspend fun getAllFolderList(): List<Folder>

    @Query("SELECT folder as name, count(name) as songCount FROM song_table GROUP BY folder ORDER BY name ASC;")
    fun getAllFolderListReturnLiveData(): LiveData<List<Folder>>

    @Query("SELECT folder as name, count(name) as songCount FROM song_table GROUP BY folder ORDER BY name ASC;")
    fun getAllFolderListByFlow(): Flow<List<Folder>>

    @Query("SELECT * FROM song_table where folder=:folderName ORDER BY name ASC;")
    fun getAllSongsByFolderName(folderName: String): List<Song>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(song: Song)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllSongs(songList: List<Song>): List<Long>

    @Query("DELETE FROM song_table")
    suspend fun deleteAll()
}

