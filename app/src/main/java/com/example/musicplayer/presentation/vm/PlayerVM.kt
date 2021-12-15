package com.example.musicplayer.presentation.vm

import android.app.Application
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.widget.Toast
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.musicplayer.data.dao.Song
import com.example.musicplayer.presentation.utils.MediaPlayerProvider
import java.io.File
import java.io.IOException
import java.util.*

//Req-
//1.Somglist
//2.Position: current song to be played - that can be passed as a position from Fragment

//livedata-
//1.Song: to be played need to be observed and had to be provided as Livedata
class PlayerVM(
    /*private val songList: List<Song>,
    private var position: Int,*/
    application: Application,
) : AndroidViewModel(application) {
    private lateinit var songList: List<Song>
    private var position: Int = 0
    private val context = application
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

    private var _currentSong = MutableLiveData<Song>()
    val currentSong: LiveData<Song>
        get() = _currentSong


    fun setCurrentSong() {
        //Check
        //1. if there is no next song i.e list ends, then reset position to start of list
        //2. there is no prev song i.e it is start of list
        if (position >= listSize || position < 0)
            resetPosition()

        _currentSong.postValue(songList[position])
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
        val uri: Uri = Uri.fromFile(File(_currentSong.value?.path))

        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayerProvider.getMediaPLayerInstance().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                try {
                    setDataSource(context, uri)
                    prepareAsync()
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
        if (mediaPlayer!!.isPlaying()) {
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

    fun removeObserverFromSong(viewLifecycleOwner: LifecycleOwner) {
        currentSong.removeObservers(viewLifecycleOwner)
    }

    fun setSongListAndPositionInVM(songList: List<Song>, position: Int) {
        this.songList = songList
        this.position = position
        //listSize = songList.size
    }
}