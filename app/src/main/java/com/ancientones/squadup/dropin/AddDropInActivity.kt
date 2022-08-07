package com.ancientones.squadup.dropin

import android.location.Geocoder
import android.os.Bundle
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ancientones.squadup.R
import com.ancientones.squadup.databinding.ActivityAddDropInBinding
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import java.io.IOException
import java.util.*


class AddDropInActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddDropInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddDropInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.saveButton.setOnClickListener {
            saveFireStore()
        }

    }

    private fun saveFireStore(){

        //validateValues()

        val db = FirebaseFirestore.getInstance()
        val dropin: MutableMap<String,Any> = HashMap()
        val latlng = getLocationFromAddress(binding.locationText.text.toString())


        if (latlng != null) {
            dropin["location"] = GeoPoint(latlng.latitude, latlng.longitude)
        }
        else
        {
            binding.locationText.requestFocus()
            binding.locationText.error = "Location is invalid"
        }


        if (binding.sportSpinner.selectedItem != null) {
            dropin["sport"] = binding.sportSpinner.selectedItem.toString()
        }

        if (binding.levelSpinner.selectedItem != null) {
            dropin["skillLevel"] = binding.levelSpinner.selectedItem.toString()
        }

        if (binding.commentsText.text != null) {
            dropin["comments"] = binding.commentsText.text.toString()
        }

        if (binding.participantsText.text != null) {
            dropin["numParticipants"] = binding.participantsText.text.toString()
        }


        db.collection("dropin")
            .add(dropin)
            .addOnSuccessListener { Toast.makeText((this), "Drop-in successfully created", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {Toast.makeText((this), "Drop-in failed to be created", Toast.LENGTH_SHORT).show()
            }

        finish()
    }

    private fun getLocationFromAddress(addressString: String): LatLng? {
        var coder: Geocoder = Geocoder(applicationContext)
        var latlng: LatLng? = null
        println("debug: address $addressString")
        try {
            var address = coder.getFromLocationName(addressString, 5)

            if (address == null){
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