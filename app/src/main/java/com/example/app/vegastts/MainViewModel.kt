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
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val permissionsApi: PermissionsApi,
    private val globalSettingsRepositoryApi: GlobalSettingsRepositoryApi,
    ) : ViewModel() {

    private val _settingsViewState = MutableStateFlow(mapSettingsToViewState(globalSettingsRepositoryApi.settings.value))
    val settingsViewState: StateFlow<SettingsViewState> = _settingsViewState

    val permissionsViewState = mutableStateOf(PermissionsViewState())

    var sender = ""
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

        sender = settings.smsFrom.toString()

        return SettingsViewState(
            smsFrom = settings.smsFrom,
        )
    }

    fun onSmsFrom(smsFrom: String) {

        sender = smsFrom

        viewModelScope.launch(Dispatchers.Default) {
            globalSettingsRepositoryApi.setSettings(
                globalSettingsRepositoryApi.settings.value.copy(
                    smsFrom = smsFrom
                )
            )
        }
    }

}
