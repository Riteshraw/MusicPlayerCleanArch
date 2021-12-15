package com.example.musicplayer

import android.app.Application
import com.example.musicplayer.presentation.di.*

class MusicPlayerApplication : Application() {
    lateinit var appComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerApplicationComponent.builder()
            .dBModule(DBModule(this))
            .build()

    }

    /*val database by lazy { SongRoomDB.getDatabase(this) }
    val repoMusic by lazy { MusicRepo(database.songDao()) }
    val repoSongList by lazy { SongListRepo(database.songDao()) }
    val musicPreferences: MusicPreferences by lazy { MusicPreferences(this) }*/
}