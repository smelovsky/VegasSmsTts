/*
 * Copyright (C) 2020-2023 Rhonda Software.
 * All rights reserved.
 */

package com.rhonda.data.repository.api

import kotlinx.coroutines.flow.StateFlow

interface GlobalSettingsRepositoryApi {

    val settings: StateFlow<SettingsDb>

    fun setSettings(newSettings: SettingsDb)

    companion object {
        val DEFAULT_SETTINGS = SettingsDb(
            smsFrom = "+7",
            keepScreenOn = true,
        )
    }

    data class SettingsDb(
        val smsFrom: String?,
        val keepScreenOn: Boolean,
    )
}
