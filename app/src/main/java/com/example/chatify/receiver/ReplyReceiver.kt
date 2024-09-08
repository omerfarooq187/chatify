package com.example.chatify.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import com.example.chatify.KEY_TEXT_REPLY
import com.example.chatify.R
import com.example.chatify.REPLY_NOTIFICATION_ID

class ReplyReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val replyText = intent?.let { RemoteInput.getResultsFromIntent(it)?.getCharSequence(KEY_TEXT_REPLY) }
        if (replyText!=null) {
            Log.d("ReplyReceiver", "Reply received: $replyText")

            val replyNotification = NotificationCompat.Builder(context!!, "ChatNotifications")
                .setSmallIcon(R.drawable.mobile_icon)
                .setContentText("Reply sent")
                .build()

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(REPLY_NOTIFICATION_ID, replyNotification)
        }
    }
}