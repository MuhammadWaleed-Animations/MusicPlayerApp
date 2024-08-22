package com.example.mediaplayerapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.mediaplayerapp.ui.theme.MediaPlayerAppTheme

class MainActivity : ComponentActivity() {

    private val requestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted, you can proceed to start the service
        } else {
            // Permission denied, handle it accordingly
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MediaPlayerAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }

    @Composable
    fun MainScreen() {
        var isPlaying by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = {
                if (checkNotificationPermission()) {
                    val serviceIntent = Intent(this@MainActivity, MediaPlayerService::class.java).apply {
                        action = if (isPlaying) MediaPlayerService.ACTION_PAUSE else MediaPlayerService.ACTION_PLAY
                    }
                    startForegroundService(serviceIntent)
                    isPlaying = !isPlaying
                } else {
                    requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }) {
                Text(if (isPlaying) "Pause Media Player" else "Play Media Player")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row {
                Button(onClick = {
                    if (checkNotificationPermission()) {
                        val serviceIntent = Intent(this@MainActivity, MediaPlayerService::class.java).apply {
                            action = MediaPlayerService.ACTION_PREVIOUS
                        }
                        startForegroundService(serviceIntent)
                    } else {
                        requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }) {
                    Text("Previous")
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(onClick = {
                    if (checkNotificationPermission()) {
                        val serviceIntent = Intent(this@MainActivity, MediaPlayerService::class.java).apply {
                            action = MediaPlayerService.ACTION_NEXT
                        }
                        startForegroundService(serviceIntent)
                    } else {
                        requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }) {
                    Text("Next")
                }
            }
        }
    }

    private fun checkNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Permissions are automatically granted on older versions
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        MediaPlayerAppTheme {
            MainScreen()
        }
    }
}
