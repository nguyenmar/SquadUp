package com.ancientones.squadup

import android.os.*
import java.util.*
import android.app.Service
import android.content.Intent
import android.location.Criteria
import com.google.android.gms.maps.model.LatLng
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

//Note: Ideas taken from CMPT 362 Class Lecture examples

class TrackingService : Service(), LocationListener {

    private lateinit var locationList : ArrayList<LatLng>
    private lateinit var myBinder: MyBinder
    private lateinit var locationManager: LocationManager
    private var msgHandler: Handler? = null

    override fun onCreate() {
        super.onCreate()

        locationList = ArrayList()
        myBinder = MyBinder()

        initLocationManager()
    }

    private fun initLocationManager() {
        try {
            locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
            val criteria = Criteria()
            criteria.accuracy = Criteria.ACCURACY_FINE
            val provider: String? = locationManager.getBestProvider(criteria, true)

            if (provider != null) {
                // Log.d("tag","we get here")
                val location = locationManager.getLastKnownLocation(provider)

                if (location != null) {
                    onLocationChanged(location)
                }

                locationManager.requestLocationUpdates(provider, 0, 0f, this)
            }

        } catch (e: SecurityException) {
            //can log
        }
    }

    override fun onBind(intent: Intent): IBinder {
        return myBinder
    }

    override fun onUnbind(intent: Intent): Boolean {
        return true
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    override fun onLocationChanged(location: Location) {

        val lat = location.latitude
        val lng = location.longitude
        val latLng = LatLng(lat, lng)

        locationList.add(latLng)
        sendMessage()
    }

    private fun sendMessage() {
        try {
            if(msgHandler != null) {
                val bundle = Bundle()

                bundle.putString(LOC_KEY, fromArrayList(locationList))

                val message = msgHandler?.obtainMessage()

                if (message != null) {
                    message.data = bundle
                    message.what = 10
                    msgHandler?.sendMessage(message)
                }
            }
        }
        catch (t: Throwable) {
            //can log
        }
    }

    inner class MyBinder : Binder() {
        fun setMessageHandler(msgHandler: Handler) {
            this@TrackingService.msgHandler = msgHandler
        }
    }

    private fun fromArrayList(array: ArrayList<LatLng>): String {
        val gson = Gson()
        val listType: Type = object : TypeToken<ArrayList<LatLng>>() {}.type
        return gson.toJson(array, listType)
    }

    companion object {
        const val LOC_KEY = "LOC_KEY"
    }


}