/*
 * Copyright (C) 2020-2023 Rhonda Software.
 * All rights reserved.
 */

package com.rhonda.data.repository.impl

import android.content.Context
import androidx.core.content.edit
import com.rhonda.data.repository.api.GlobalSettingsRepositoryApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class GlobalSettingsRepositoryImpl(context: Context) : GlobalSettingsRepositoryApi {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(GlobalSettingsRepositoryApi.DEFAULT_SETTINGS)
    override val settings: StateFlow<GlobalSettingsRepositoryApi.SettingsDb> = _settings

    init {
        load()
    }

    override fun setSettings(newSettings: GlobalSettingsRepositoryApi.SettingsDb) {
        save(newSettings)
        _settings.value = newSettings
    }

    private fun load() {
        _settings.value = try {
            GlobalSettingsRepositoryApi.SettingsDb(
                smsFrom = prefs.getString(
                    SMS_FROM,
                    GlobalSettingsRepositoryApi.DEFAULT_SETTINGS.smsFrom
                ),
            )
        } catch (ignored: Exception) {
            GlobalSettingsRepositoryApi.DEFAULT_SETTINGS
        }
    }

    private fun save(settings: GlobalSettingsRepositoryApi.SettingsDb) {
        prefs.edit(commit = true) {
            putString(SMS_FROM, settings.smsFrom)
        }
    }

    companion object {
        private const val PREFS_NAME = "global_settings"
        private const val SMS_FROM = "smsFrom"

    }
}
