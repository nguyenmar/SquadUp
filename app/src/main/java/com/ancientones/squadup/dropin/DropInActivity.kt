package com.ancientones.squadup.dropin

import android.location.Geocoder
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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.ktx.Firebase
import java.util.ArrayList

class DropInActivity : AppCompatActivity(), OnMapReadyCallback{
    private lateinit var binding: ActivityDropInBinding
    private lateinit var mMap: GoogleMap
    //private var location: GeoPoint = GeoPoint(0.0,0.0)
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
        var numParticipants: Long = 0
        var currentUser = Firebase.auth.currentUser!!.uid

        if (bundle != null){
            documentID = bundle["documentID"].toString()
        }

        dropInViewModel.fetchDropIn(documentID)
        dropInViewModel.fetchUserID()

        dropInViewModel.members.observe(this) {
            binding.onTheWay.text = "${dropInViewModel.members.value!!.count()} on their way"
            val listCount = dropInViewModel.members.value!!.count().toLong()
            val spotRemain = dropInViewModel.numParticipants.value?.minus(listCount)
            binding.spotsRemain.text = "$spotRemain spots remaining"
        }

        dropInViewModel.sport.observe(this) {
            binding.titleDropIn.text = "${dropInViewModel.sport.value} Drop-in"
        }
        dropInViewModel.firstName.observe(this) {
            binding.hostName.text = "${dropInViewModel.firstName.value} ${dropInViewModel.lastName.value}"
        }
        dropInViewModel.lastName.observe(this) {
            binding.hostName.text = "${dropInViewModel.firstName.value} ${dropInViewModel.lastName.value}"
        }
        dropInViewModel.startTime.observe(this) {
            binding.timeDropIn.text = "${dropInViewModel.startTime.value} - ${dropInViewModel.endTime.value}"
        }
        dropInViewModel.endTime.observe(this) {
            binding.timeDropIn.text = "${dropInViewModel.startTime.value} - ${dropInViewModel.endTime.value}"
        }
        dropInViewModel.comments.observe(this) {
            binding.aboutDropIn.text = "${dropInViewModel.comments.value}"
        }
        dropInViewModel.numParticipants.observe(this) {
            binding.participantsDropIn.text = "${dropInViewModel.numParticipants.value}"
            /*val listCount = dropInViewModel.members.value!!.count().toLong()
            val spotRemain = dropInViewModel.numParticipants.value?.minus(listCount)
            binding.spotsRemain.text = "$spotRemain spots remaining"*/

        }
        dropInViewModel.skillLevel.observe(this) {
            binding.skillLevelDropIn.text = "${dropInViewModel.skillLevel.value}"
        }

        binding.joinButton.setOnClickListener {
            joinDropIn()
        }
    }

    private fun joinDropIn(){
        finish()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        println("on map rdy")
        mMap = googleMap
        var location: GeoPoint
        dropInViewModel.location.observe(this) {
            location = dropInViewModel.location.value!!
            println(location)
            mMap.addMarker(
                MarkerOptions()
                    .position(LatLng(location.latitude, location.longitude))
                    .title("Drop-in")
            )
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 15f)
            mMap.animateCamera(cameraUpdate)
            binding.locationDropIn.text = getAddressfromLatLng(location)
        }
    }


    fun getAddressfromLatLng(geoPoint: GeoPoint): String{
        var coder: Geocoder = Geocoder(applicationContext)

        var fullAddress = coder.getFromLocation(geoPoint.latitude,geoPoint.longitude, 1)

        val address: String = fullAddress[0].getAddressLine(0)
        val city: String = fullAddress[0].getLocality()
        val state: String = fullAddress[0].getAdminArea()
        val zip: String = fullAddress[0].getPostalCode()
        val country: String = fullAddress[0].getCountryName()

        return address
    }
}