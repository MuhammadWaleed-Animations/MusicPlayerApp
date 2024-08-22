package com.example.mediaplayerapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mediaplayerapp.ui.theme.MediaPlayerAppTheme

class MainActivity : ComponentActivity() {

    // Add image references corresponding to the music tracks
    private val backgroundImages = arrayOf(R.drawable.jojo, R.drawable.sparkle,R.drawable.tokyo_ghoul,R.drawable.kira,R.drawable.rezero,R.drawable.attack_on_titan,R.drawable.taki,R.drawable.grand_escape,R.drawable.jujutsu_kaisen)

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
        var currentImageIndex by remember { mutableStateOf(0) } // Track the current image index

        Box(modifier = Modifier.fillMaxSize()) {
            // Dynamic background image
            Image(
                painter = painterResource(id = backgroundImages[currentImageIndex]),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Overlay with controls
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x99000000)) // Semi-transparent overlay
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Now Playing",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Previous, Play/Pause, and Next buttons
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = {
                        currentImageIndex = (currentImageIndex - 1 + backgroundImages.size) % backgroundImages.size
                        startService(MediaPlayerService.ACTION_PREVIOUS)
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_previous),
                            contentDescription = "Previous",
                            tint = Color.White
                        )
                    }

                    IconButton(onClick = {
                        val action = if (isPlaying) MediaPlayerService.ACTION_PAUSE else MediaPlayerService.ACTION_PLAY
                        startService(action)
                        isPlaying = !isPlaying
                    }) {
                        Icon(
                            painter = painterResource(id = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play),
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = Color.White
                        )
                    }

                    IconButton(onClick = {
                        currentImageIndex = (currentImageIndex + 1) % backgroundImages.size
                        startService(MediaPlayerService.ACTION_NEXT)
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_next),
                            contentDescription = "Next",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }

    private fun startService(action: String) {
        val serviceIntent = Intent(this@MainActivity, MediaPlayerService::class.java).apply {
            this.action = action
        }
        startForegroundService(serviceIntent)
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        MediaPlayerAppTheme {
            MainScreen()
        }
    }
}
