package com.example.noticemanager

import android.content.Context
import android.os.StatFs
import android.os.Environment
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class MemoryWork(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    private var batteryInfo: String = ""
    @RequiresApi(Build.VERSION_CODES.O)
    override fun doWork(): Result {
        batteryInfo = "Available memory: " + MemoryInfo() + " MB"
        createNotification()
        return Result.success()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotification() {
        val builder = NotificationCompat.Builder(applicationContext, channelId)
                .setSmallIcon(R.drawable.device_information)
                .setContentText(batteryInfo)
                .setContentTitle(description)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
        with(NotificationManagerCompat.from(applicationContext)) {
            notify(12345, builder.build())
        }
    }

    fun MemoryInfo(): Long {
        val memoryinfo = StatFs(Environment.getDataDirectory().path)
        val bytesAvailable: Long = memoryinfo.blockSizeLong * memoryinfo.availableBlocksLong
        return bytesAvailable / bytes
    }
}

