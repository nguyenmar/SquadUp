package com.ancientones.squadup.ui.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
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

    fun fetchUser(userReference: DatabaseReference) {
        val userListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.value
                Log.i("firebase", "got user: $user")
                val userMap: HashMap<String, String> = user as HashMap<String, String>

                _firstName.value = userMap["firstName"]
                _lastName.value = userMap["lastName"]
                _userAge.value = userMap["userAge"]
                _userSex.value = userMap["userSex"]
                _userHeight.value = userMap["userHeight"].toString()
                _userPhone.value = userMap["userPhone"]
                _userDescription.value = userMap["userDescription"]

                if (userMap["teamworkRatings"] != null) {
                    var ratings: List<Double> = userMap["teamworkRatings"] as List<Double>
                    _userRating.value = ratings.average()
                }
                else {
                    _userRating.value = 0.0
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // TODO: implement
            }
        }
        userReference.addValueEventListener(userListener)
    }
}