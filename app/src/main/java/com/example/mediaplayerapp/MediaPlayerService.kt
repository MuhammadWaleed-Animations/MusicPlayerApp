package com.example.mediaplayerapp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService

var current: Int = 0 // Adjusted to start at the first index

class MediaPlayerService : LifecycleService() {

    companion object {
        const val ACTION_PLAY = "ACTION_PLAY"
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val ACTION_PREVIOUS = "ACTION_PREVIOUS"
        const val ACTION_NEXT = "ACTION_NEXT"
        const val NOTIFICATION_ID = 1
    }

    private var mediaPlayer: MediaPlayer? = null
    private val CHANNEL_ID = "MediaPlayerServiceChannel"
    private val musicResId = intArrayOf(R.raw.giornos_theme_but_only_the_best_part,R.raw.sparkle,R.raw.tokyo_ghoul_unravel,R.raw.death_note,R.raw.styx_helix,R.raw.i_would_still_love_you,R.raw.is_there_still_anything_that_love_can_do,R.raw.grand_escape,R.raw.specilz)

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        mediaPlayer = MediaPlayer.create(this, musicResId[current])

        startForeground(NOTIFICATION_ID, createNotification("Preparing media...", false))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        when (intent?.action) {
            ACTION_PLAY -> {
                if (mediaPlayer?.isPlaying == false) {
                    mediaPlayer?.start()
                    updateNotification("Playing media...", true)
                }
            }
            ACTION_PAUSE -> {
                if (mediaPlayer?.isPlaying == true) {
                    mediaPlayer?.pause()
                    updateNotification("Media paused", false)
                }
            }
            ACTION_PREVIOUS -> {
                playPreviousTrack()
            }
            ACTION_NEXT -> {
                playNextTrack()
            }
        }
        return START_STICKY
    }

    private fun playPreviousTrack() {
        mediaPlayer?.release()
        current = if (current - 1 < 0) musicResId.size - 1 else current - 1
        mediaPlayer = MediaPlayer.create(this, musicResId[current])
        mediaPlayer?.start()
        updateNotification("Playing media...", true)
    }

    private fun playNextTrack() {
        mediaPlayer?.release()
        current = if (current + 1 >= musicResId.size) 0 else current + 1
        mediaPlayer = MediaPlayer.create(this, musicResId[current])
        mediaPlayer?.start()
        updateNotification("Playing media...", true)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun createNotification(contentText: String, isPlaying: Boolean): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val playPauseAction = if (isPlaying) {
            val pauseIntent = Intent(this, MediaPlayerService::class.java).apply { action = ACTION_PAUSE }
            val pausePendingIntent = PendingIntent.getService(this, 1, pauseIntent, PendingIntent.FLAG_IMMUTABLE)
            NotificationCompat.Action.Builder(R.drawable.ic_pause, "Pause", pausePendingIntent).build()
        } else {
            val playIntent = Intent(this, MediaPlayerService::class.java).apply { action = ACTION_PLAY }
            val playPendingIntent = PendingIntent.getService(this, 2, playIntent, PendingIntent.FLAG_IMMUTABLE)
            NotificationCompat.Action.Builder(R.drawable.ic_play, "Play", playPendingIntent).build()
        }

        val previousIntent = Intent(this, MediaPlayerService::class.java).apply { action = ACTION_PREVIOUS }
        val previousPendingIntent = PendingIntent.getService(this, 3, previousIntent, PendingIntent.FLAG_IMMUTABLE)
        val previousAction = NotificationCompat.Action.Builder(R.drawable.ic_previous, "Previous", previousPendingIntent).build()

        val nextIntent = Intent(this, MediaPlayerService::class.java).apply { action = ACTION_NEXT }
        val nextPendingIntent = PendingIntent.getService(this, 4, nextIntent, PendingIntent.FLAG_IMMUTABLE)
        val nextAction = NotificationCompat.Action.Builder(R.drawable.ic_next, "Next", nextPendingIntent).build()

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Media Player Service")
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_music_note)
            .setContentIntent(pendingIntent)
            .addAction(previousAction)
            .addAction(playPauseAction)
            .addAction(nextAction)
            .setOngoing(isPlaying)
            .setSound(null) // Mute the notification sound
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Media Player Service Channel",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Channel for media player service"
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun updateNotification(contentText: String, isPlaying: Boolean) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = createNotification(contentText, isPlaying)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}
