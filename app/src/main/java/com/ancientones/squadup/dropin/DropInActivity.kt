package com.ancientones.squadup.dropin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ancientones.squadup.R
import com.ancientones.squadup.databinding.ActivityDropInBinding
import com.ancientones.squadup.ui.chat.ChatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint

class DropInActivity : AppCompatActivity(), OnMapReadyCallback{
    private lateinit var binding: ActivityDropInBinding
    private lateinit var mMap: GoogleMap
    private var location: GeoPoint = GeoPoint(0.0,0.0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mapFragment = SupportMapFragment.newInstance()
        supportFragmentManager
            .beginTransaction()
            .add(R.id.map, mapFragment)
            .commit()

        mapFragment.getMapAsync(this)


        binding = ActivityDropInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        val bundle = intent.extras
        var documentID = ""
        var comments = ""
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
                    comments = document.get("comments").toString()
                    println(comments)
                    sport = document.get("sport").toString()
                    document.getGeoPoint("location")?.let { setGeoPoint(it) }
                    binding.aboutDropIn.text = comments
                    binding.titleDropIn.text = sport + " Drop-in"


                } else {
                    println("No document")
                }
            }
            .addOnFailureListener { exception ->
                println("failed to get document")
            }

        println("debug: location $location")

        //binding.locationDropIn.text = getAddressfromLatLng(location)
        binding.joinButton.setOnClickListener {
            finish()
        }

        // Open chat button
        binding.openChatButton.setOnClickListener {
            val chatIntent = Intent(this, ChatActivity::class.java);
            chatIntent.putExtra( ChatActivity.CHAT_ID_KEY, documentID );
            startActivity( chatIntent );
        }
    }
    fun setGeoPoint(geoPoint: GeoPoint){
        location = geoPoint
        println("debug: geopoint: $location")
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.addMarker(
            MarkerOptions()
                .position(LatLng(49.2578, -123.0594))
                .title("Drop-in")
        )
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(LatLng(49.2578, -123.0594), 15f)
        mMap.animateCamera(cameraUpdate)
    }


/*    fun getAddressfromLatLng(geoPoint: GeoPoint): String{
        var coder: Geocoder = Geocoder(applicationContext)

        var fullAddress = coder.getFromLocation(geoPoint.latitude,geoPoint.longitude, 1)

        val address: String = fullAddress[0].getAddressLine(0)
        val city: String = fullAddress[0].getLocality()
        val state: String = fullAddress[0].getAdminArea()
        val zip: String = fullAddress[0].getPostalCode()
        val country: String = fullAddress[0].getCountryName()

        return address
    }*/
}
