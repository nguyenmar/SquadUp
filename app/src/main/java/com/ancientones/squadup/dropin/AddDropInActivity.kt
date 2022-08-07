package com.ancientones.squadup.dropin

import android.location.Geocoder
import android.os.Bundle
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.ancientones.squadup.R
import com.ancientones.squadup.databinding.ActivityAddDropInBinding
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import java.io.IOException
import java.util.*


class AddDropInActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddDropInBinding
    private lateinit var dropInViewModel: DropInViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddDropInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dropInViewModel = ViewModelProvider(this).get(DropInViewModel::class.java)
        dropInViewModel.fetchUserID()

        binding.saveButton.setOnClickListener {
            saveFireStore()
        }

    }
    private fun validateValues(): Boolean{
        if (binding.locationText.text.isEmpty()) {
            binding.locationText.requestFocus()
            binding.locationText.error = "Location is empty"
            return false
        }

        if (getLocationFromAddress(binding.locationText.text.toString()) == null) {
            binding.locationText.requestFocus()
            binding.locationText.error = "Address is invalid"
            return false
        }

        if (binding.startTime.text.isEmpty()) {
            binding.startTime.requestFocus()
            binding.startTime.error = "Start time is empty"
            return false
        }

        if (binding.endTime.text.isEmpty()) {
            binding.endTime.requestFocus()
            binding.endTime.error = "End time is empty"
            return false
        }

        if (binding.participantsText.text.isEmpty()) {
            binding.participantsText.requestFocus()
            binding.participantsText.error = "Number of Participants is empty"
            return false
        }

        if (binding.commentsText.text.isEmpty()) {
            binding.commentsText.requestFocus()
            binding.commentsText.error = "Comments is empty"
            return false
        }
       return true
    }
    private fun saveFireStore(){
        println("save button pressed")
        val validated = validateValues()

        if (validated){
            val db = FirebaseFirestore.getInstance()
            val dropin: MutableMap<String,Any> = HashMap()
            val list: MutableList<String> = ArrayList()

            val latlng = getLocationFromAddress(binding.locationText.text.toString())
            if (latlng != null) {
                dropin["location"] = GeoPoint(latlng.latitude, latlng.longitude)
            }
            dropInViewModel.userID.observe(this) {
                println("debug: hostID: ${dropInViewModel.userID.value}")
                dropin["hostID"] = "${dropInViewModel.userID.value}"
                list.add("${dropInViewModel.userID.value}")
            }

            dropin["sport"] = binding.sportSpinner.selectedItem.toString()
            dropin["skillLevel"] = binding.levelSpinner.selectedItem.toString()
            dropin["comments"] = binding.commentsText.text.toString()
            dropin["startTime"] = binding.startTime.text.toString()
            dropin["endTime"] = binding.endTime.text.toString()
            dropin["numParticipants"] = binding.participantsText.text as Int
            dropin["members"] = list
            db.collection("dropin")
                .add(dropin)
                .addOnSuccessListener { Toast.makeText((this), "Drop-in successfully created", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {Toast.makeText((this), "Drop-in failed to be created", Toast.LENGTH_SHORT).show()
                }
            finish()
        }

    }

    private fun getLocationFromAddress(addressString: String): LatLng? {
        var coder: Geocoder = Geocoder(applicationContext, Locale.CANADA)
        var latlng: LatLng? = null
        println("debug: address $addressString")
        try {
            var address = coder.getFromLocationName(addressString, 5)
            println("debug: $address")

            if (address.isEmpty()){
                println("debug: address is null")
                return null
            }

            var location = address[0]
            latlng = LatLng(location.latitude, location.longitude)

        } catch (ex: IOException) {
            ex.printStackTrace()
        }
        return latlng
    }

}