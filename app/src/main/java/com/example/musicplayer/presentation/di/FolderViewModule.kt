package com.example.musicplayer.presentation.di

import android.app.Application
import com.example.musicplayer.data.dataProviders.SongDataProvider
import com.example.musicplayer.domain.repo.MusicRepo
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class FolderViewModule {

    @Singleton
    @Provides
    fun folderViewModelFactory(
        repository: MusicRepo, songDataProvider: SongDataProvider, application: Application
    ): FolderViewModelFactory = FolderViewModelFactory(repository, songDataProvider, application)

}