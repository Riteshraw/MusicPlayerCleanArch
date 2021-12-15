package com.example.musicplayer.presentation.di

import android.content.Context
import com.example.musicplayer.data.dataProviders.SongDataProvider
import com.example.musicplayer.data.db.SongDao
import com.example.musicplayer.domain.repo.MusicRepo
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepoModule {

    @Singleton
    @Provides
    fun provideMusicRepo(songDao: SongDao) = MusicRepo(songDao)

    @Singleton
    @Provides
    fun provideSongDataProvider(context: Context) = SongDataProvider(context)

}