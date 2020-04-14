package com.example.swimming.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.swimming.R
import com.example.swimming.ui.profile.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

// FCM
class MyFirebaseMessagingService : FirebaseMessagingService() {
    var title: String? = null
    var contents: String? = null

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        title = p0.notification!!.title
        contents = p0.notification!!.body!!

        sendNotification()
    }

    private fun sendNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = "채널"
            val channelName = "채널명"

            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("message", "notification")
            val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_ONE_SHOT)

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationChannel = NotificationChannel(channel, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            notificationChannel.description = "채널에 대한 설명"
            notificationChannel.enableLights(true)
            notificationChannel.enableVibration(true)
            notificationChannel.setShowBadge(true)
            notificationManager.createNotificationChannel(notificationChannel)

            val builder = NotificationCompat.Builder(this, channel)
                .setSmallIcon(R.drawable.ic_launcher_swimming_foreground)
                .setContentTitle(title)
                .setContentText(contents)
                .setContentIntent(pendingIntent)
                .setChannelId(channel)
                .setAutoCancel(true)

                .setDefaults(Notification.DEFAULT_SOUND)
                .setDefaults(Notification.DEFAULT_VIBRATE)

            val manager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.notify(9999, builder.build())

        } else {
            val build = NotificationCompat.Builder(this, "")
                .setSmallIcon(R.drawable.ic_launcher_swimming_foreground)
                .setContentTitle(title)
                .setContentText(contents)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setDefaults(Notification.DEFAULT_VIBRATE)

            val manager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.notify(9999, build.build())
        }
    }
}