package com.example.noticemanager

import android.app.*
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.work.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    lateinit var addButton: TextView
    lateinit var timeTextView: TextView
    lateinit var timeInfoTextView: TextView
    lateinit var notificationChannel: NotificationChannel
    lateinit var editText: EditText

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        editText = findViewById(R.id.editText)
        addButton = findViewById(R.id.addButton)
        timeTextView = findViewById(R.id.time_info)
        timeInfoTextView = findViewById(R.id.textview_time)
        val animForButton = AnimationUtils.loadAnimation(this, R.anim.add_go)
        initNotificationChannel()
        addButton.setOnClickListener {
            if (editText.text.toString() == "") {
                addButton.startAnimation(animForButton)
                Toast.makeText(this, notification, 300.toInt()).show()
                timeTextView.text = "1"
                timeTextView.text = defaultTimeInfo
                val requestBatteryWork = OneTimeWorkRequestBuilder<BatteryWork>()
                        .setInitialDelay(defaultduration, TimeUnit.SECONDS)
                        .build()
                val requestMemoryWork = OneTimeWorkRequestBuilder<MemoryWork>()
                        .setInitialDelay(defaultduration + 2, TimeUnit.SECONDS)
                        .build()
                val requestList = listOf(requestBatteryWork, requestMemoryWork)
                WorkManager.getInstance(this).enqueue(requestList)
            } else {
                addButton.startAnimation(animForButton)
                val text = editText.text.toString()
                val duration: Long = text.toLong()
                timeTextView.text = duration.toString()
                Toast.makeText(this, "Next notification will be in $duration second", 300.toInt()).show()
                val requestBatteryWork = OneTimeWorkRequestBuilder<BatteryWork>()
                        .setInitialDelay(duration, TimeUnit.SECONDS)
                        .build()
                val requestMemoryWork = OneTimeWorkRequestBuilder<MemoryWork>()
                        .setInitialDelay(duration + 2, TimeUnit.SECONDS)
                        .build()
                val requestList = listOf(requestBatteryWork, requestMemoryWork)
                WorkManager.getInstance(this).enqueue(requestList)
            }
        }
    }

    private fun initNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(
                    channelId,
                    description,
                    NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager: NotificationManager =
                    applicationContext.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}


