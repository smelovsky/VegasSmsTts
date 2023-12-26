package com.example.app.vegastts

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.PowerManager
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.app.vegastts.ui.theme.VegasTTSTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale


lateinit var mainViewModel: MainViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity(), TextToSpeech.OnInitListener {

    private lateinit var tts: TextToSpeech
    private var isTextToSpeechSuccess: Boolean = false

    private lateinit var wakeLockPowerManager: PowerManager.WakeLock

    var isAppInited: Boolean = false
    var isFistStart: Boolean = true

    override fun onInit(i: Int) {
        if (i == TextToSpeech.SUCCESS) {
            isTextToSpeechSuccess = true
        }
    }

    private fun speakOut(message: String) {
        tts.speak(message, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    @SuppressLint("InvalidWakeLockTag")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tts = TextToSpeech(this, this)

        setContent {

            mainViewModel = hiltViewModel()
            isAppInited = true

            mainViewModel.getPermissionsApi().hasAllPermissions(this)

            val activity = this

            if (isFistStart) {
                if (mainViewModel.permissionsViewState.value.permissionsGranted) {

                    val powerManager = getSystemService(POWER_SERVICE) as PowerManager
                    wakeLockPowerManager = powerManager.newWakeLock(
                        PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.SCREEN_BRIGHT_WAKE_LOCK,
                        "WakeLock")

                    if (mainViewModel.getKeepScreenOn()) wakeLockPowerManager.acquire()

                    isFistStart = false
                }
            }

            val settingsViewState = mainViewModel.settingsViewState.collectAsState()

            VegasTTSTheme {
                val context = LocalContext.current
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        topBar = { TopAppBar (
                            title = { Text(context.getResources().getString(R.string.app_name)) },
                            actions = {
                                IconButton(onClick = {
                                    exitFromApp()
                                }) {
                                    Icon(
                                        Icons.Outlined.ExitToApp,
                                        contentDescription = "Exit",
                                    )
                                }
                            }
                        )
                        },
                        bottomBar = {

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column { Text("Keep screen on") }
                                Spacer(Modifier.width(10.dp))
                                Switch(
                                    checked = settingsViewState.value.keepScreenOn,
                                    onCheckedChange = {
                                        if (it) wakeLockPowerManager.acquire() else wakeLockPowerManager.release()
                                        mainViewModel.onKeepScreenOn(it)
                                    },
                                )
                            }
                        },
                    ) { innerPadding ->

                        val focusManager = LocalFocusManager.current

                        Box(
                            modifier = Modifier
                                .padding(innerPadding)
                                .fillMaxSize()
                                .padding(4.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                            ) {

                                if (!mainViewModel.permissionsViewState.value.permissionsGranted) {

                                    OutlinedButton(
                                        modifier = Modifier
                                            .align(Alignment.CenterHorizontally),
                                        onClick = {
                                            if (!mainViewModel.getPermissionsApi().hasBasePermissions(activity)) {
                                                mainViewModel.getPermissionsApi().requestBasePermissions(activity)
                                            }
                                        }
                                    ) {
                                        Text("Get permissions")
                                    }

                                } else {

                                    EditSingleLineWidget(
                                        modifier = Modifier.fillMaxWidth(),
                                        label = "Phone number or sender",
                                        text = settingsViewState.value.smsFrom.toString(),
                                        keyboardType = KeyboardType.Text,
                                        isError = settingsViewState.value.smsFrom.toString().isEmpty(),
                                        onTextChanged = { inputText ->
                                            mainViewModel.onSmsFrom(inputText.trim())
                                        },
                                        onDone = { focusManager.clearFocus(true) },
                                    )

                                    Spacer(modifier = Modifier.height(20.dp))

                                    Text("SMS",
                                        modifier = Modifier
                                            .align(Alignment.CenterHorizontally))
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text("\"${mainViewModel.smsBody.value}\"",
                                        modifier = Modifier
                                            .align(Alignment.CenterHorizontally))

                                    Spacer(modifier = Modifier.height(20.dp))

                                    OutlinedButton(
                                        modifier = Modifier
                                            .align(Alignment.CenterHorizontally),
                                        onClick = {
                                            if (isTextToSpeechSuccess) {
                                                val locale = Locale("ru","RU")
                                                tts.language = locale

                                                val result = tts.setLanguage(locale)
                                                if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                                                    speakOut(mainViewModel.smsBody.value)
                                                } else {
                                                    Toast.makeText(context, "This Language is not supported", Toast.LENGTH_LONG).show()
                                                }
                                            } else {
                                                Toast.makeText(context, "Text To Speech is not supported", Toast.LENGTH_LONG).show()
                                            }

                                        }
                                    ) {
                                        Text("Text To Speech")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun exitFromApp() {
        this.finish()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()

        if (isAppInited) {
            mainViewModel.getPermissionsApi().hasAllPermissions(this)
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSingleLineWidget(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    label: String,
    text: String,
    isError: Boolean = false,
    onTextChanged: (String) -> Unit,
    onFocusChanged: (FocusState) -> Unit = {},
    onDone: () -> Unit = {},
) {
    var value by remember(text){ mutableStateOf(text)}
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier,
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { onFocusChanged(it) },
            readOnly = readOnly,
            enabled = enabled,
            isError = isError,
            maxLines = 1,
            singleLine = true,
            visualTransformation = VisualTransformation.None,
            value = value,
            onValueChange = { inputText ->
                onTextChanged(inputText)
                value = inputText
            },
            label = { Text(label) },
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Done,

                ),
            keyboardActions = KeyboardActions(
                onDone = {
                    onDone()
                    focusManager.clearFocus(true)
                }
            ),
            trailingIcon = {
            },
        )
    }
}

