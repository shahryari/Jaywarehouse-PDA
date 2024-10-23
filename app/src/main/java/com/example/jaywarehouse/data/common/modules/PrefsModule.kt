package com.example.jaywarehouse.data.common.modules

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.jaywarehouse.data.common.utils.Prefs
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val Context.dataStore : DataStore<Preferences> by preferencesDataStore("settings")


val prefsModule = module {
    single<Prefs> {
        Prefs(androidContext())
    }
}