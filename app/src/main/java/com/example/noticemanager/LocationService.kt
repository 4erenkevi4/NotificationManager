package com.example.noticemanager

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.app.NotificationManager
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import java.util.*

@Suppress("UNREACHABLE_CODE")
class LocationService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(12345, createNotification("Your location:", getAdressInfo(), applicationContext))
        return START_STICKY
    }

    @SuppressLint("MissingPermission")
    private fun getAdressInfo(): String {
        var address = ""
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val providers: List<String> = locationManager.getProviders(true)
        for (provider in providers) {
            locationManager.requestLocationUpdates(provider, 3000, 2f) { }
            val location = locationManager.getLastKnownLocation(provider)
            val latitude = location?.latitude
            val longitude = location?.longitude
            address = Geocoder(this, Locale.ENGLISH).getFromLocation(
                latitude!!,
                longitude!!,
                1
            )[0].getAddressLine(0)
            createNotification("Your location", address, applicationContext)
        }
        return address
    }

    private fun createNotification(description: String, address: String, context: Context
    ): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(CHANNELID, description, NotificationManager.IMPORTANCE_HIGH)
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
            notificationManager?.createNotificationChannel(notificationChannel)

        }

        val builder = NotificationCompat.Builder(context, CHANNELID)
            .setSmallIcon(R.drawable.ic_baseline_location_on_24)
            .setContentTitle(description)
            .setContentText(address)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        NotificationManagerCompat.from(context).notify(123, builder.build())
        return builder.build()
    }
}