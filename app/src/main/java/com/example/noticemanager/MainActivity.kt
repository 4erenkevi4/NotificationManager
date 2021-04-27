package com.example.noticemanager

import android.Manifest.permission.*
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.util.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    lateinit var addButton: TextView
    lateinit var geoButton: TextView
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
        geoButton = findViewById(R.id.geo_button)
        timeTextView = findViewById(R.id.time_info)
        timeInfoTextView = findViewById(R.id.textview_time)


        val animForButton = AnimationUtils.loadAnimation(this, R.anim.add_go)
        initNotificationChannel()
        addButton.setOnClickListener {
            if (editText.text.toString() == "") {
                addButton.startAnimation(animForButton)
                Toast.makeText(this, NOTIFICATION, 300.toInt()).show()
                timeTextView.text = "1"
                timeInfoTextView.text = DEFAULTTIMEINFO
                val requestBatteryWork = OneTimeWorkRequestBuilder<BatteryWork>()
                    .setInitialDelay(DEFAULTDURATION, TimeUnit.SECONDS)
                    .build()
                val requestMemoryWork = OneTimeWorkRequestBuilder<MemoryWork>()
                    .setInitialDelay(DEFAULTDURATION + 2, TimeUnit.SECONDS)
                    .build()
                val requestList = listOf(requestBatteryWork, requestMemoryWork)
                WorkManager.getInstance(this).enqueue(requestList)
            } else {
                addButton.startAnimation(animForButton)
                val text = editText.text.toString()
                val duration: Long = text.toLong()
                timeTextView.text = duration.toString()
                Toast.makeText(this, "Next notification will be in $duration second", 300.toInt())
                    .show()
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
        geoButton.setOnClickListener {
            if (editText.text.toString() == "") {
                timeTextView.text = "1"
                timeInfoTextView.text = DEFAULTTIMEINFO
                checkPermissions(DEFAULTDURATION)
            } else {
                val text = editText.text.toString()
                val duration: Long = text.toLong()
                timeTextView.text = duration.toString()
                Thread.sleep(duration)
                checkPermissions(duration)
            }
        }
    }

    private fun initNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(
                CHANNELID,
                DESCRIPTION,
                NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager: NotificationManager =
                applicationContext.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun checkPermissions(duration: Long) {
        Dexter.withContext(this)
            .withPermissions(ACCESS_FINE_LOCATION)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    report?.let {
                        if (report.areAllPermissionsGranted()) {
                            Toast.makeText(
                                this@MainActivity,
                                "Geolocation service started!",
                                300.toInt()
                            ).show()
                            val cal = Calendar.getInstance()
                            val serviceIntent =
                                Intent(this@MainActivity, LocationService::class.java)
                            val pendingIntent =
                                PendingIntent.getService(this@MainActivity, 0, serviceIntent, 0)
                            val alarm =
                                this@MainActivity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                            alarm.setRepeating(
                                AlarmManager.RTC_WAKEUP,
                                cal.timeInMillis,
                                duration,
                                pendingIntent
                            )
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                    Toast.makeText(
                        this@MainActivity,
                        "Geolocation service not started! You have not given permission for this Application!",
                        300.toInt()
                    ).show()
                }
            })
            .withErrorListener {
                Toast.makeText(this@MainActivity, "Error - " + (it.name), 300.toInt()).show()
            }
            .check()
    }
}
