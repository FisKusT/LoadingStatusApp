package com.fiskus.loadingstatusapp.utils

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.fiskus.loadingstatusapp.R
import com.fiskus.loadingstatusapp.ui.DetailActivity

//TODO: send status and URL data on action
/**
 * Extension of NotificationManager
 * Send notification to device
 * @param context- application context
 * @param isDownloadSuccessful- flag that indicates the download status
 * */
fun NotificationManager.sendDownloadCompletedNotification(context: Context, downloadMessage: String?, isDownloadSuccessful:Boolean) {
    val loadDetailsActivityIntent = Intent(context, DetailActivity::class.java)
    loadDetailsActivityIntent.putExtra(Constants.EXTRA_DOWNLOAD_RESULT_STATUS, isDownloadSuccessful)
    loadDetailsActivityIntent.putExtra(Constants.EXTRA_DOWNLOAD_MESSAGE, downloadMessage)

    //load details activity intent
    val loadDetailsActivityPendingIntent = PendingIntent
        .getActivity(context,
            Constants.SEND_DOWNLOAD_COMPLETED_NOTIFICATION_ID,
            loadDetailsActivityIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)


    //send notification with load details activity action
    val notificationToSend = getNotificationCompatBuilderWithOrdinaryValues(context,
        Constants.DOWNLOAD_NOTIFICATION_CHANNEL_ID,
        context.getString(R.string.notification_title),
        context.getString(R.string.notification_description),
        R.drawable.ic_download,
        NotificationCompat.PRIORITY_HIGH)
        .addAction(
            R.drawable.ic_download,
            context.getString(R.string.notification_button),
            loadDetailsActivityPendingIntent
        )
        .build()

    // call notify to send the notification
    notify(Constants.SEND_DOWNLOAD_COMPLETED_NOTIFICATION_ID, notificationToSend)

}

/**
 * Get notification compat builder with ordinary values
 * */
private fun getNotificationCompatBuilderWithOrdinaryValues(
    context: Context,
    channelId: String,
    title:String,
    message: String,
    smallIconDrawable: Int,
    priority:Int) = NotificationCompat.Builder(context, channelId)
    .setContentTitle(title)
    .setSmallIcon(smallIconDrawable)
    .setContentText(message)
    .setPriority(priority)

/**
 * Create notification channel
 * */
fun createChannel(activity: Activity, channelId: String, channelName: String) {
    //check api level version
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        //create channel
        val notificationChannel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            setShowBadge(false)
            enableLights(true)
            lightColor = Color.RED
            enableVibration(true)
            description = "Time for breakfast"
        }
        getNotificationManager(activity).createNotificationChannel(notificationChannel)
    }
}

/**
 * Get notification manager
 * */
fun getNotificationManager(activity: Activity) = ContextCompat.getSystemService(activity, NotificationManager::class.java) as NotificationManager


