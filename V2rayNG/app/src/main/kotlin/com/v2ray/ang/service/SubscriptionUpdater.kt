package com.v2ray.ang.service

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkerParameters
import androidx.work.multiprocess.RemoteWorkManager
import com.v2ray.ang.AngApplication
import com.v2ray.ang.R
import com.v2ray.ang.util.MmkvManager
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit




class UpdateTask(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    private val notificationManager = NotificationManagerCompat.from(applicationContext)
    private val notification = NotificationCompat.Builder(applicationContext, "update-subscription")
        .setWhen(0)
        .setTicker("Update")
        .setContentTitle("Update subscription")
        .setSmallIcon(R.drawable.ic_stat_name)
        .setCategory(NotificationCompat.CATEGORY_SERVICE)


    override suspend fun doWork(): Result {

        println("start updating")
        val subs = MmkvManager.decodeSubscriptions().filter { it.second.autoUpdate }

        println("subs with auto update is: $subs")
        for (i in subs) {

            val subscription = i.second

            delay(10000)


            notification.setContentText(subscription.remarks)


            notificationManager.notify(3, notification.build())

        }


        notificationManager.cancel(3)
        return Result.success()
    }
}
