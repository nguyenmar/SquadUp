package com.ancientones.squadup.dropin

import android.location.Geocoder
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ancientones.squadup.databinding.ActivityDropInBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint


class DropInActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDropInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDropInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        val bundle = intent.extras
        var documentID = ""
        var comments = ""
        var location: GeoPoint? = null
        var startTime = ""
        var endTime = ""
        var sport = ""
        var skillLevel = ""
        var numParticipants = ""

        if (bundle != null){
            documentID = bundle["documentID"].toString()
        }
        val db = FirebaseFirestore.getInstance()


        val docRef = db.collection("dropin").document(documentID)
        println("documentID --- $documentID")
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    println("DocumentSnapshot data: ${document.data}")
                    comments = document.get("comment").toString()
                    location = document.getGeoPoint("location")

                } else {
                    println("No document")
                }
            }
            .addOnFailureListener { exception ->
                println("failed to get document")
            }
        binding.joinButton.setOnClickListener {
            finish()
        }
    }

/*    fun getAddressfromLatLng(geoPoint: GeoPoint){
        var coder: Geocoder = Geocoder(applicationContext)

        var fullAddress = coder.getFromLocation(geoPoint.latitude,geoPoint.longitude 1)

        val address: String = fullAddress[0].getAddressLine(0)
        val city: String = fullAddress[0].getLocality()
        val state: String = fullAddress[0].getAdminArea()
        val zip: String = fullAddress[0].getPostalCode()
        val country: String = fullAddress[0].getCountryName()
    }*/
}