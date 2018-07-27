package de.metzgore.beansplan.notifications

import android.app.*
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.v4.app.AlarmManagerCompat
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.text.format.DateUtils
import de.metzgore.beansplan.MainActivity
import de.metzgore.beansplan.R
import de.metzgore.beansplan.data.room.relations.ShowWithReminder


object NotificationHelper {

    private const val REMINDER_CHANNEL_ID = "reminder_channel"

    private val twitchIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.twitch.tv/rocketbeanstv"))
    private val youtubeIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/rocketbeanstv/live"))

    fun createNotificationChannel(context: Context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.notification_channel_reminder_name)
            val description = context.getString(R.string.notification_channel_reminder_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(REMINDER_CHANNEL_ID, name, importance)
            channel.description = description
            channel.enableLights(true)
            channel.enableVibration(true)
            channel.setShowBadge(true)

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(context: Context, showWithReminder: ShowWithReminder): Notification {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        val builder = NotificationCompat.Builder(context, REMINDER_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("${showWithReminder.show.title} - ${showWithReminder.show.topic}")
                .setContentText(DateUtils.formatDateTime(context, showWithReminder.show.timeStart.time, DateUtils.FORMAT_SHOW_TIME))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setContentIntent(pendingIntent)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .addAction(R.drawable.ic_youtube, context.getString(R.string.notification_action_youtube),
                        PendingIntent.getActivity(context, 1, youtubeIntent, 0))
                //TODO use twitch icon
                .addAction(R.drawable.ic_youtube, context.getString(R.string.notification_action_twitch),
                        PendingIntent.getActivity(context, 2, twitchIntent, 0))

        return builder.build()
    }

    fun scheduleNotification(context: Context, showWithReminder: ShowWithReminder) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        AlarmManagerCompat.setExactAndAllowWhileIdle(alarmManager, AlarmManager.RTC_WAKEUP,
                showWithReminder.reminder!![0].timestamp.time, getPendingIntent(context, showWithReminder))
    }

    private fun getPendingIntent(context: Context, showWithReminder: ShowWithReminder): PendingIntent {
        val notificationIntent = Intent(context, NotificationPublisher::class.java)
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, showWithReminder.reminder!![0].id)
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, buildNotification(context, showWithReminder))
        return PendingIntent.getBroadcast(context, showWithReminder.reminder!![0].id.toInt(),
                notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT)
    }

    fun unscheduleNotification(context: Context, showWithReminder: ShowWithReminder) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(getPendingIntent(context, showWithReminder))
    }
}