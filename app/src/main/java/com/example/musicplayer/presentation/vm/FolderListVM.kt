package com.example.musicplayer.presentation.vm

import android.app.Application
import android.media.MediaPlayer
import android.net.Uri
import android.widget.Toast
import androidx.databinding.ObservableField
import androidx.lifecycle.*
import com.example.musicplayer.data.dao.Folder
import com.example.musicplayer.data.dao.Song
import com.example.musicplayer.data.dataProviders.SongDataProvider
import com.example.musicplayer.domain.repo.MusicRepo
import com.example.musicplayer.presentation.utils.MediaPlayerProvider
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.util.*
import javax.inject.Singleton

@Singleton
class FolderListVM(
    private val repository: MusicRepo,
    private val songDataProvider: SongDataProvider,
    application: Application
) :
    AndroidViewModel(application) {

    private val context = application
    private var _allFoldersList = MutableLiveData<List<Folder>>()
    val allFoldersList: LiveData<List<Folder>>
        get() = _allFoldersList

    //SongList Fields

    private var _songListLiveData = MutableLiveData<List<Song>>()
    val songListLiveData: LiveData<List<Song>>
        get() = _songListLiveData

    //Player fields
    private lateinit var songList: List<Song>

    private var position: Int = 0
    private var mediaPlayer: MediaPlayer? = null
    private var timer: Timer? = null

    private val listSize: Int by lazy {
        songList.size
    }

    var seekBarValue = ObservableField(0)
    var seekBarMAxValue = ObservableField(0)
    var playImgStatus = ObservableField(true)
    var totalDurationInMMSS = ObservableField("00:00")
    var durationElapsedInMMSS = ObservableField("00:00")

    private lateinit var song: Song
    private var _currentSong = MutableLiveData<Song>()
    val currentSong: LiveData<Song>
        get() = _currentSong

    private var _isPlayerFragmentVisible = MutableLiveData<Boolean>()
    val isPlayerFragmentVisible: LiveData<Boolean>
        get() = _isPlayerFragmentVisible

    private var _positionLD = MutableLiveData<Int>()
    val positionLD: LiveData<Int>
        get() = _positionLD

    /** Gets all songs present in device using content provider
     */
    fun getSongsByContentProvider() {
        //val songList = SongDataProvider(getApplication()).getSongsList()
        val allSongList = songDataProvider.getSongsList()
        insertAllSongsInDB(allSongList)
    }

    //return list of Folders
    fun getAllFolders() {
        viewModelScope.launch(IO) {
            val result = repository.getAllFolderList()
            _allFoldersList.postValue(repository.getAllFolderList())
        }
    }

    //return LiveData List of Folders
    fun allFoldersReturnLiveData() {
        viewModelScope.launch(IO) {
            repository.getAllFolderListReturnLiveData()
        }
    }

    //Using Flow to return Folder List
    val allFoldersByFlow: LiveData<List<Folder>> = repository.getAllFolderListByFlow.asLiveData()

    //Launching a new coroutine to insert the data in a non-blocking way
    fun insert(song: Song) = viewModelScope.launch(IO) {
        repository.insert(song)
    }

    //Launching a new coroutine to insert the list of songs in a non-blocking way
    private fun insertAllSongsInDB(allSongList: List<Song>) = viewModelScope.launch(IO) {
        //delete all songs before populating DB
        repository.deleteSongsDB()
        //populate DB
        var listId: List<Long>? = repository.insertAllSongs(allSongList)
        if (listId.isNullOrEmpty())
            Toast.makeText(context, "Error fetching songs from Device...", Toast.LENGTH_SHORT)
                .show()
        else
            getAllFolders()
    }

    //SongList functions

    fun getAllSongsByFolderName(folderName: String) {
        viewModelScope.launch(IO) {
            songList = repository.getAllSongsByFolderName(folderName)
            _songListLiveData.postValue(songList)
        }
    }

    //Player functions

    fun setCurrentSong() {
        //Check
        //1. if there is no next song i.e list ends, then reset position to start of list
        //2. there is no prev song i.e it is start of list
        if (position >= listSize || position < 0)
            resetPosition()

        setSongAndLiveData()
        resetMediaPlayer()
        onPlayClick()
    }

    /**Resetting list position so that songs list starts again*/
    private fun resetPosition() {
        position = 0
    }

    /**Change song to next by inc the position if isNext if true or goto prev by dec if false **/
    fun changeSong(isNext: Boolean) {
        if (isNext)
            position += 1
        else
            position -= 1

        setCurrentSong()
    }

    //Start music player if first time click or stop if already playing
    fun onPlayClick() {
        val uri: Uri = Uri.fromFile(File(song.path))

        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayerProvider.getMediaPLayerInstance().apply {
                try {
                    setDataSource(context, uri)
                    prepareAsync()
                    //start()
                } catch (e: IOException) {
                    Toast.makeText(context, "mp3 not found", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
            mediaPlayer?.setOnPreparedListener {
                mediaAction()
            }
        } else mediaAction()
    }

    private fun mediaAction() {
        seekBarMAxValue.set(mediaPlayer?.duration)
        totalDurationInMMSS.set(MediaPlayerProvider.milliSecsToHHMM(mediaPlayer?.duration))
        if (mediaPlayer!!.isPlaying) {
            playImgStatus.set(true)
            mediaPlayer?.pause()
        } else {
            mediaPlayer?.start()
            playImgStatus.set(false)
            startProgressSeekbar()
        }
    }

    private fun startProgressSeekbar() {
        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                seekBarValue.set(mediaPlayer?.currentPosition)
                durationElapsedInMMSS.set(MediaPlayerProvider.milliSecsToHHMM(mediaPlayer?.currentPosition))
                //change to next track if song duration is over
                if ((mediaPlayer?.currentPosition)?.div(1000) == (mediaPlayer?.duration)?.div(1000)) {
                    changeSong(true)
                }
            }
        }, 0, 1000)

    }

    fun seekTo(progress: Int) {
        mediaPlayer?.seekTo(progress)
    }

    private fun resetMediaPlayer() {
        cancelTimer()
        MediaPlayerProvider.resetMediaPlayer()
        if (mediaPlayer != null) {
            mediaPlayer = null
        }
    }

    fun releaseResources() {
        cancelTimer()
        MediaPlayerProvider.releaseMedialPlayer()
        if (mediaPlayer != null) {
            /*mediaPlayer?.stop()
            mediaPlayer?.release()*/
            mediaPlayer = null
        }
    }

    private fun cancelTimer() {
        timer?.cancel()
    }

    /** setting song value for music player
    and _currentSongValue for UI to change  **/
    private fun setSongAndLiveData() {
        song = songList[position]
        _currentSong.postValue(song)
    }

    fun removeObserverFromSong(viewLifecycleOwner: LifecycleOwner) {
        currentSong.removeObservers(viewLifecycleOwner)
    }

    fun setSongListAndPositionInVM(songList: List<Song>, position: Int) {
        this.songList = songList
        this.position = position
        //listSize = songList.size
    }

    fun setSongByPosition(position: Int) {
        this.position = position
        _positionLD.value = position
        releaseResources()
        setCurrentSong()
        setPlayerFragmentVisibilityVariable(true)
    }

    fun setPlayerFragmentVisibilityVariable(value: Boolean) {
        _isPlayerFragmentVisible.value = value
    }

}
