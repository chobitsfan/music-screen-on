package org.chobitstai.musicscreenon

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import java.util.*

class ScreenOnService : Service() {
    private val timer = Timer()
    private var origScreenTimeout = 0
    companion object {
        var running = false
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        Log.d("MusicScreenOn", "service destroy")
        running = false
        timer.cancel()
        Settings.System.putInt(contentResolver, Settings.System.SCREEN_OFF_TIMEOUT, origScreenTimeout)
        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(10)
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        running = true
        val myIntent = Intent(this, MainActivity::class.java)
        val pendingIntent =  PendingIntent.getActivity(this,0,myIntent,PendingIntent.FLAG_UPDATE_CURRENT)
        val notification = Notification.Builder(this, "MyApp")
            .setContentTitle("Extend screen timeout while playing music")
            //.setContentText(getText(R.string.notification_message))
            .setSmallIcon(R.drawable.outline_info_24)
            .setContentIntent(pendingIntent)
            //.setTicker(getText(R.string.ticker_text))
            .build()
        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(10, notification)
        startForeground(10, notification)

        origScreenTimeout = Settings.System.getInt(contentResolver, Settings.System.SCREEN_OFF_TIMEOUT)
        Settings.System.putInt(contentResolver, Settings.System.SCREEN_OFF_TIMEOUT, 1000 * 60 * 10)

        timer.schedule(object : TimerTask() {
            val am : AudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            var music_stopped_count = 0
            override fun run() {
                if (!am.isMusicActive) {
                    music_stopped_count++
                    if (music_stopped_count > 2) {
                        cancel()
                        stopSelf()
                    }
                }
            }
        }, 60000, 10000)
        return START_NOT_STICKY
    }
}