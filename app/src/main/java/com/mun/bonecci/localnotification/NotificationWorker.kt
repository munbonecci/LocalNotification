package com.mun.bonecci.localnotification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.mun.bonecci.localnotification.Constants.NOTIFICATION_MESSAGE_PARAM
import com.mun.bonecci.localnotification.Constants.NOTIFICATION_TITLE_PARAM

/**
 * Worker class responsible for displaying notifications.
 *
 * @param context The application context.
 * @param params The input parameters for the worker.
 */
class NotificationWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    /**
     * Performs the work of displaying a notification.
     *
     * @return The result of the work, indicating success or failure.
     */
    override fun doWork(): Result {
        // Retrieve title and message from input data
        val title = inputData.getString(NOTIFICATION_TITLE_PARAM)
        val message = inputData.getString(NOTIFICATION_MESSAGE_PARAM)

        // Show the notification with the provided title and message
        showNotification(title, message)

        // Indicate that the work was successful
        return Result.success()
    }

    /**
     * Displays a notification with the given title and message.
     *
     * @param title The title of the notification.
     * @param message The message content of the notification.
     */
    private fun showNotification(title: String? = "Hello", message: String? = "World") {
        // Get the system notification manager
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel for Android Oreo and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Build the notification
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        // Display the notification
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        private const val CHANNEL_ID = "notification_channel"
        private const val CHANNEL_NAME = "Notification Channel"
        private const val NOTIFICATION_ID = 1
    }
}