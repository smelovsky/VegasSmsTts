package com.example.app.vegastts

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rhonda.app.vegasrhonda.permissions.PermissionsApi
import com.rhonda.app.vegasrhonda.permissions.PermissionsViewState
import com.rhonda.data.repository.api.GlobalSettingsRepositoryApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsViewState(
    val smsFrom: String?,
    val keepScreenOn: Boolean,
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val permissionsApi: PermissionsApi,
    private val globalSettingsRepositoryApi: GlobalSettingsRepositoryApi,
    ) : ViewModel() {

    private val _settingsViewState = MutableStateFlow(mapSettingsToViewState(globalSettingsRepositoryApi.settings.value))
    val settingsViewState: StateFlow<SettingsViewState> = _settingsViewState

    val permissionsViewState = mutableStateOf(PermissionsViewState())

    var smsBody = mutableStateOf("No message ")

    fun getPermissionsApi() : PermissionsApi {
        return permissionsApi
    }

    init {
        viewModelScope.launch(Dispatchers.Default) {
            globalSettingsRepositoryApi.settings.collect {
                _settingsViewState.value = mapSettingsToViewState(it)
            }
        }

    }

    private fun mapSettingsToViewState(settings: GlobalSettingsRepositoryApi.SettingsDb): SettingsViewState {

        return SettingsViewState(
            smsFrom = settings.smsFrom,
            keepScreenOn = settings.keepScreenOn,
        )
    }

    fun onSmsFrom(smsFrom: String) {
        viewModelScope.launch(Dispatchers.Default) {
            globalSettingsRepositoryApi.setSettings(
                globalSettingsRepositoryApi.settings.value.copy(
                    smsFrom = smsFrom
                )
            )
        }
    }

    fun getSmsFrom () : String {
        return globalSettingsRepositoryApi.settings.value.smsFrom.toString()
    }

    fun onKeepScreenOn(keepScreenOn: Boolean) {
        viewModelScope.launch(Dispatchers.Default) {
            globalSettingsRepositoryApi.setSettings(
                globalSettingsRepositoryApi.settings.value.copy(
                    keepScreenOn = keepScreenOn
                )
            )
        }
    }
    fun getKeepScreenOn () : Boolean {
        return globalSettingsRepositoryApi.settings.value.keepScreenOn
    }

}
