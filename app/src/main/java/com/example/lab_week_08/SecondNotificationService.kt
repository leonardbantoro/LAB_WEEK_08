package com.example.lab_week_08

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData

class SecondNotificationService : Service() {

    private lateinit var serviceHandler: Handler
    private val CHANNEL_ID = "002"

    override fun onCreate() {
        super.onCreate()
        val thread = HandlerThread("SecondServiceThread")
        thread.start()
        serviceHandler = Handler(thread.looper)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val id = intent?.getStringExtra(MainActivity.EXTRA_ID) ?: "Unknown"

        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Second Foreground Service")
            .setContentText("Running SecondNotificationService for ID: $id")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        startForeground(2, notification)

        serviceHandler.postDelayed({
            Log.d("SecondNotificationService", "Completed for ID: $id")
            trackingCompletion.postValue(id)
            stopSelf()
        }, 3000)

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Second Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    companion object {
        val trackingCompletion = MutableLiveData<String>()
    }
}
