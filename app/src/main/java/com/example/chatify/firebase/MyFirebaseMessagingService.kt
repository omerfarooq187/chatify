package com.example.chatify.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import com.example.chatify.KEY_TEXT_REPLY
import com.example.chatify.MainActivity
import com.example.chatify.NOTIFICATION_CHANNEL_ID
import com.example.chatify.NOTIFICATION_ID
import com.example.chatify.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        if (message.notification != null) {
            val notificationTitle = message.notification!!.title
            val notificationBody = message.notification!!.body
            showNotification(notificationTitle, notificationBody)
        }
    }

    private fun showNotification(title: String?, message: String?) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val replyLabel = "Enter your reply here"

        val remoteInput = RemoteInput.Builder(KEY_TEXT_REPLY)
            .setLabel(replyLabel)
            .build()

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        // Add the reply action
        val replyAction = NotificationCompat.Action.Builder(
            R.drawable.reply_icon, "Reply", pendingIntent)
            .addRemoteInput(remoteInput)
            .build()

        val notificationBuilder = NotificationCompat.Builder(this, "ChatNotifications")
            .setSmallIcon(R.drawable.mobile_icon)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .addAction(replyAction)
            .setContentIntent(pendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Chat Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }
}
