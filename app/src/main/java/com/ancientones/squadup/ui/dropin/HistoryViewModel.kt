package com.ancientones.squadup.ui.dropin

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class HistoryViewModel: ViewModel(){
    private val _documentList = MutableLiveData<MutableList<String>>()

    val documentList: LiveData<MutableList<String>>
        get(){
            return _documentList
        }

    fun getCompletedDropins(){
        val db = FirebaseFirestore.getInstance()
        val documentIDs: MutableList<String> = ArrayList()
        db.collection("dropin")
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Log.w(ContentValues.TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                if (value != null) {
                    println("debug: getting completed drop-ins")
                    documentIDs.clear()
                    val documents = value.documents

                    documents.forEach {
                        println("debug: ${it.id}")
                        val isCompleted = it.getBoolean("isCompleted")
                        val sportType = it.getString("sport")

                        if (isCompleted == true) {
                            documentIDs.add("${it.id}")
                        }
                    }
                    _documentList.value = documentIDs
                }
            }

        println(documentIDs)
    }
}