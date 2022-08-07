package com.ancientones.squadup.ui.profile

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class ProfileImgViewModel : ViewModel() {

    val userImage = MutableLiveData<Bitmap>()
    val hasImage = MutableLiveData<Boolean>()

    fun fetchUserImage() {
        val imageRef = Firebase.storage.reference.child("images/${Firebase.auth.currentUser!!.uid}")
        imageRef.getBytes(1024 * 1024).addOnSuccessListener {
            userImage.value = BitmapFactory.decodeByteArray(it, 0, it.size)
            hasImage.value = true
        }.addOnFailureListener {
            println("DEBUG: User does not currently have a set display photo.")
            hasImage.value = false
        }
    }
}