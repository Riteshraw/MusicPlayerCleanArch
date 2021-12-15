package com.example.musicplayer.presentation.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.musicplayer.data.db.SongDao
import com.example.musicplayer.data.db.SongRoomDB
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DBModule(private val application: Application) {

    @Singleton
    @Provides
    fun provideApplicationContext(): Context = application.applicationContext

    @Singleton
    @Provides
    fun provideApplication(): Application = application

    @Singleton
    @Provides
    fun provideDb(context: Application): SongRoomDB {
        return Room.databaseBuilder(
            context,
            SongRoomDB::class.java,
            "word_database"
        ).build()
    }

    @Singleton
    @Provides
    fun provideSongDao(db: SongRoomDB): SongDao = db.songDao()


}
