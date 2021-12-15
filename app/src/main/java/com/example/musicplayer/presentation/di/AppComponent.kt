package com.example.musicplayer.presentation.di

import com.example.musicplayer.presentation.ui.MainActivity
import com.example.musicplayer.presentation.ui.FolderListFragment
import com.example.musicplayer.presentation.ui.PlayerFragment
import com.example.musicplayer.presentation.ui.SongListFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [FolderViewModule::class,DBModule::class, RepoModule::class])
interface ApplicationComponent {

    /*@Component.Factory
    interface Factory {
        fun create(): ApplicationComponent
    }*/
   /*@Component.Builder
    interface Builder {
        *//*@BindsInstance
        fun application(application: Application): Builder*//*

        fun build(): ApplicationComponent
    }*/


    fun inject(activity: MainActivity)
    fun inject(fragment: FolderListFragment)
    fun inject(fragment: SongListFragment)
    fun inject(fragment: PlayerFragment)

}