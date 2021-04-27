package com.example.noticemanager

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class BatteryWork(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    private var batteryInfo: String = ""

    @RequiresApi(Build.VERSION_CODES.O)
    override fun doWork(): Result {
        createNotification()
        return Result.success()
    }

    @RequiresApi(Build.VERSION_CODES.O)
   private fun createNotification() {
        val builder = NotificationCompat.Builder(applicationContext, CHANNELID)
            .setSmallIcon(R.drawable.device_information)
            .setContentText(initBroadcast())
            .setContentTitle(DESCRIPTION)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
        with(NotificationManagerCompat.from(applicationContext)) {
            notify(1234, builder.build())
        }
    }

    private fun initBroadcast(): String {
        val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val intent: Intent? = applicationContext.registerReceiver(null, intentFilter)
        val rawlevel: Int = intent!!.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        var level = -1
        if (rawlevel >= 0 && scale > 0) {
            level = (rawlevel * 100) / scale
        }
        batteryInfo = "Battery charged: $level%"
        return batteryInfo
    }
}
