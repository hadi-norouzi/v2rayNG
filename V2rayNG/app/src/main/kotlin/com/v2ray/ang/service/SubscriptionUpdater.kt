package com.v2ray.ang.service

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.v2ray.ang.R
import com.v2ray.ang.util.MmkvManager
import kotlinx.coroutines.delay

object SubscriptionUpdater {

    class UpdateTask(context: Context, params: WorkerParameters) :
        CoroutineWorker(context, params) {

        private val notificationManager = NotificationManagerCompat.from(applicationContext)
        private val notification = NotificationCompat.Builder(applicationContext,"update-subscription")

            .setWhen(0)
            .setTicker("Update")
            .setContentTitle("Update subscription")
            .setSmallIcon(R.drawable.ic_stat_name)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)


        @SuppressLint("MissingPermission")
        override suspend fun doWork(): Result {
            val subs = MmkvManager.decodeSubscriptions().filter { it.second.autoUpdate }

            for (i in subs) {

                val subscription = i.second

                delay(3000)


                notification.setContentText(subscription.remarks)

                notificationManager.notify(3, notification.build())

            }


            notificationManager.cancel(3)
            return Result.success()
        }
    }
}