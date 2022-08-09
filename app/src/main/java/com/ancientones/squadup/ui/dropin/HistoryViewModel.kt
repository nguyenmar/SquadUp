package com.ancientones.squadup.ui.dropin

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class HistoryViewModel: ViewModel(){
    private val _documentList = MutableLiveData<MutableList<String>>()

    val documentList: LiveData<MutableList<String>>
        get(){
            return _documentList
        }

    fun getCompletedDropins(){
        val db = FirebaseFirestore.getInstance()
        val completedIDs: MutableList<String> = ArrayList()

        db.collection("dropin")
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Log.w(ContentValues.TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                if (value != null) {
                    completedIDs.clear()
                    val documents = value.documents

                    documents.forEach {
//                        println("debug: ${it}")
                        val isCompleted = it.getBoolean("isCompleted")
                        val participants = it["members"] as ArrayList<String>
                        var isParticipant = false

                        if(participants.contains(Firebase.auth.currentUser!!.uid)) {
                            isParticipant = true
                        }

                        if (isCompleted == true && isParticipant) {
                            completedIDs.add(it.id)
                        }
                    }
                    _documentList.value = completedIDs
                }
            }
    }
}