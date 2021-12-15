package com.example.musicplayer.presentation.di

import android.app.Application
import android.content.Context
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class ContextModule {  // to allow abstract method make module abstract

    //@Binds works on an abstract method
    @Singleton
    @Binds   // @Binds, binds the Application instance to Context
    abstract fun context(appInstance: Application): Context //just return the super-type you need

}