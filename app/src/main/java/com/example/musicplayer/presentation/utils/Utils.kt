package com.example.musicplayer.presentation.utils

import android.app.Activity
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.view.View
import android.view.animation.TranslateAnimation
import android.widget.Toast

fun Activity.showToast(txt: String) {
    Toast.makeText(this, txt, Toast.LENGTH_SHORT).show()
}

fun String.getLength(): Int {
    return this.length
}

fun milliSecsToHHMM(milliseconds: Int?): String {
    val min = milliseconds?.div(1000)?.div(60)
    val seconds = milliseconds?.div(1000)?.mod(60)

    val sec = if (seconds!! < 10) {
        "0$seconds"
    } else "$seconds"

    return "$min:$sec"
}

fun View.slideUp(duration: Int = 500) {
    visibility = View.VISIBLE
    val animate = TranslateAnimation(0f, 0f, 0f, 0f)
    animate.duration = duration.toLong()
    animate.fillAfter = true
    this.startAnimation(animate)
}

fun View.slideDown(duration: Int = 500) {
    visibility = View.VISIBLE
    val animate = TranslateAnimation(0f, 0f, 0f, this.height.toFloat())
    animate.duration = duration.toLong()
    animate.fillAfter = true
    this.startAnimation(animate)
}


object MediaPlayerProvider {
    private var mediaPlayerInstance: MediaPlayer? = null

    fun getMediaPLayerInstance(): MediaPlayer {
        //releaseMedialPlayer()
        return mediaPlayerInstance ?: synchronized(this) {
            val mediaPlayer = MediaPlayer()
            mediaPlayerInstance = mediaPlayer.apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                //setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK)
            }
            mediaPlayer
        }
    }

    fun resetMediaPlayer() {
        mediaPlayerInstance?.let {
            mediaPlayerInstance?.stop()
            mediaPlayerInstance?.reset()
        }
    }

    fun releaseMedialPlayer() {
        mediaPlayerInstance?.let {
            mediaPlayerInstance?.stop()
            mediaPlayerInstance?.release()
            mediaPlayerInstance = null
        }
    }

    fun milliSecsToHHMM(milliseconds: Int?): String {
        val min = milliseconds?.div(1000)?.div(60)
        val seconds = milliseconds?.div(1000)?.mod(60)

        val sec = if (seconds!! < 10) {
            "0$seconds"
        } else "$seconds"

        return "$min:$sec"
    }
}