package com.example.musicplayer.presentation.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = Constants.USER_PREFERENCES_NAME)

open class MusicPreferences(context: Context) {
    private val appContext = context

    fun <T> getKeyValue(key: Preferences.Key<T>): Flow<T?> =
        appContext.dataStore.data.map {
            it[key]
        }

    suspend fun getBooleanKeyValue(key: Preferences.Key<Boolean>): Boolean =
        appContext.dataStore.data.first()[key] ?: true

    suspend fun <T> saveKeyValue(key: Preferences.Key<T>, value: T) {
        appContext.dataStore.edit {
            it[key] = value
        }
    }

    suspend fun <T> clearKeyValue(key: Preferences.Key<T>) {
        appContext.dataStore.edit {
            it.remove(key)
        }
    }

    companion object {
        //private val APPLICATION_FIRST_RUN = stringPreferencesKey(Constants.APPLICATION_FIRST_RUN)
        val applicationFirstRun = booleanPreferencesKey(Constants.APPLICATION_FIRST_RUN)
    }

}