package com.ancientones.squadup.dropin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.ancientones.squadup.R
import com.ancientones.squadup.databinding.ActivityDropInBinding
import com.ancientones.squadup.ui.profile.ProfileViewModel
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
    private lateinit var dropInViewModel: DropInViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dropInViewModel = ViewModelProvider(this).get(DropInViewModel::class.java)

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


        if (bundle != null){
            documentID = bundle["documentID"].toString()
        }

        dropInViewModel.fetchDropIn(documentID)

        dropInViewModel.sport.observe(this) {
            binding.titleDropIn.text = "${dropInViewModel.sport.value} Drop-in"
        }

        binding.joinButton.setOnClickListener {
            finish()
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