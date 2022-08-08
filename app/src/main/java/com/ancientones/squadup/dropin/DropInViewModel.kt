package com.ancientones.squadup.dropin

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.ktx.Firebase
import java.util.HashMap
import com.google.firebase.database.ktx.database



class DropInViewModel: ViewModel() {

    private val _sport = MutableLiveData<String>()
    private val _skillLevel = MutableLiveData<String>()
    private val _location = MutableLiveData<GeoPoint>()
    private val _date = MutableLiveData<String>()
    private val _startTime = MutableLiveData<String>()
    private val _endTime = MutableLiveData<String>()
    private val _numParticipants = MutableLiveData<Long>()
    private val _comments = MutableLiveData<String>()
    private val _hostID = MutableLiveData<String>()
    private val _firstName = MutableLiveData<String>()
    private val _lastName = MutableLiveData<String>()
    private val _members = MutableLiveData<MutableList<String>>()

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
    val numParticipants: LiveData<Long>
        get(){
            return _numParticipants
        }

    val comments: LiveData<String>
        get(){
            return _comments
        }

    val members: LiveData<MutableList<String>>
        get(){
            return _members
        }

    val hostID: LiveData<String>
        get(){
            return _hostID
        }
    val firstName: LiveData<String>
        get(){
            return _firstName
        }
    val lastName: LiveData<String>
        get(){
            return _lastName
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
                    _location.value = document.getGeoPoint("location")
                    _startTime.value = document.get("startTime").toString()
                    _endTime.value = document.get("endTime").toString()
                    _comments.value = document.get("comments").toString()
                    _numParticipants.value = document.get("numParticipants").toString().toLong()
                    _skillLevel.value = document.get("skillLevel").toString()
                    println("debug: ${document.get("members")}")
                    _hostID.value = document.get("hostID").toString()
                    _members.value = document.get("members") as MutableList<String>
                } else {
                    println("No document")
                }
            }
            .addOnFailureListener {
                println("failed to get document")
            }
    }
    fun addMembertoDropIn(documentID: String){

    }
    fun fetchUserID(){

        val userId = Firebase.auth.currentUser!!.uid
        Firebase.database.getReference("Users")
            .child(userId)
            .get()
            .addOnSuccessListener {
                Log.i("firebase", "got user: ${it.value}")
                val userMap: HashMap<String, String> = it.value as HashMap<String, String>

                _firstName.value = userMap["firstName"]
                _lastName.value = userMap["lastName"]

            }
            .addOnFailureListener{
                Log.e("firebase", "error getting data", it)
            }
    }

}

