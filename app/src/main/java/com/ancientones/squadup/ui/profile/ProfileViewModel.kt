package com.ancientones.squadup.ui.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.HashMap

class ProfileViewModel : ViewModel() {

    private val _firstName = MutableLiveData<String>()
    private val _lastName = MutableLiveData<String>()
    private val _userAge = MutableLiveData<String>()
    private val _userSex = MutableLiveData<String>()
    private val _userPhone = MutableLiveData<String>()
    private val _userHeight = MutableLiveData<String>()
    private val _userRating = MutableLiveData<Double>()
    private val _userDescription = MutableLiveData<String>()
    private val _userDropIn = MutableLiveData<Int>()

    // TODO: if we have extra time
    //private val _attendance = MutableLiveData<Double>()

    val firstName: LiveData<String>
        get(){
            return _firstName
        }

    val lastName: LiveData<String>
        get(){
            return _lastName
        }

    val userAge: LiveData<String>
        get(){
            return _userAge
        }

    val userSex: LiveData<String>
        get(){
            return _userSex
        }

    val userPhone: LiveData<String>
        get(){
            return _userPhone
        }

    val userHeight: LiveData<String>
        get(){
            return _userHeight
        }

    val userRating: LiveData<Double>
        get(){
            return _userRating
        }

    val userDescription: LiveData<String>
        get(){
            return _userDescription
        }

    val userDropIn: LiveData<Int>
        get(){
            return _userDropIn
        }

    fun fetchUser() {
        val userId = Firebase.auth.currentUser!!.uid

        Firebase.database.getReference("Users")
            .child(userId)
            .get()
            .addOnSuccessListener {
                Log.i("firebase", "got user: ${it.value}")
                val userMap: HashMap<String, String> = it.value as HashMap<String, String>

                _firstName.value = userMap["firstName"]
                _lastName.value = userMap["lastName"]
                _userAge.value = userMap["userAge"]
                _userSex.value = userMap["userSex"]
                _userHeight.value = userMap["userHeight"].toString()
                _userPhone.value = userMap["userPhone"]
                _userDescription.value = userMap["userDescription"]

                calculateRating()
            }
            .addOnFailureListener{
                Log.e("firebase", "error getting data", it)
            }
    }

    private fun calculateRating() {
        var ratings: List<Double>
        Firebase.database.getReference("Users").child(Firebase.auth.currentUser!!.uid).child("teamworkRatings")
            .get()
            .addOnSuccessListener {
                if(it.value != null) {
                    ratings = it.value as List<Double>
                    _userRating.value = ratings.average()
                }
                else {
                    _userRating.value = 0.0
                }
            }
    }
}