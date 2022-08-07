package com.ancientones.squadup.dropin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.ktx.Firebase

class DropInViewModel: ViewModel() {

    private val _sport = MutableLiveData<String>()
    private val _skillLevel = MutableLiveData<String>()
    private val _location = MutableLiveData<GeoPoint>()
    private val _date = MutableLiveData<String>()
    private val _startTime = MutableLiveData<String>()
    private val _endTime = MutableLiveData<String>()
    private val _numParticipants = MutableLiveData<String>()
    private val _comments = MutableLiveData<String>()
    private val _userID = MutableLiveData<String>()

    val sport: LiveData<String>
        get(){
            return _sport
        }

    val skillLevel: LiveData<String>
        get(){
            return _skillLevel
        }
    val location: LiveData<GeoPoint>
        get(){
            return _location
        }
    val date: LiveData<String>
        get(){
            return _date
        }
    val startTime: LiveData<String>
        get(){
            return _startTime
        }
    val endTime: LiveData<String>
        get(){
            return _endTime
        }
    val numParticipants: LiveData<String>
        get(){
            return _numParticipants
        }

    val comments: LiveData<String>
        get(){
            return _comments
        }
    val userID: LiveData<String>
        get(){
            return _userID
        }

    fun fetchDropIn(documentID: String){
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("dropin").document(documentID)
        println("documentID --- $documentID")
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    println("DocumentSnapshot data: ${document.data}")
                    _sport.value = document.get("sport").toString()
                    println(document.get("sport").toString())
                    _location.value = document.getGeoPoint("location")
                    _comments.value = document.get("comments").toString()
                } else {
                    println("No document")
                }
            }
            .addOnFailureListener {
                println("failed to get document")
            }
    }

    fun fetchUserID(){
        _userID.value = Firebase.auth.currentUser!!.uid
    }
}

