package com.ancientones.squadup.dropin

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DialogViewModel: ViewModel() {
    private val _date = MutableLiveData<String>()
    private val _startTime = MutableLiveData<String>()
    private val _endTime = MutableLiveData<String>()

    fun setDate(value: String) {
        _date.value = value
    }

    fun getDate(): String? {
        return _date.value
    }

    fun setStartTime(value: String) {
        _startTime.value = value
    }

    fun getStartTime(): String? {
        return _startTime.value
    }

    fun setEndTime(value: String) {
        _endTime.value = value
    }

    fun getEndTime(): String? {
        return _endTime.value
    }
}