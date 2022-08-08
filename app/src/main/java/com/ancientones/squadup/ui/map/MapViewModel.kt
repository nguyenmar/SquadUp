package com.ancientones.squadup.ui.map

import android.os.*
import androidx.lifecycle.ViewModel
import android.content.ComponentName
import android.content.ServiceConnection
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ancientones.squadup.TrackingService

//Note: Ideas taken from CMPT 362 Class Lecture examples

class MapViewModel: ViewModel(), ServiceConnection {

    private var messageHandler: MessageHandler = MessageHandler(Looper.getMainLooper())
    private var _bundle = MutableLiveData<Bundle>()

    val bundle: LiveData<Bundle> get() {
        return _bundle
    }

    override fun onServiceConnected(name: ComponentName, binder: IBinder) {
        val tmpBinder = binder as TrackingService.MyBinder
        tmpBinder.setMessageHandler(messageHandler)
    }

    override fun onServiceDisconnected(name: ComponentName?) {}

    inner class MessageHandler(looper: Looper) : Handler(looper) {
        override fun handleMessage(msg: Message) {
            if (msg.what == 10) {
                _bundle.value = msg.data
            }
        }
    }
}