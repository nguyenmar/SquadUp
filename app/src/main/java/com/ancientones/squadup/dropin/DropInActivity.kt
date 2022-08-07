package com.ancientones.squadup.dropin

import android.content.Intent
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
    private var documentID = ""

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
        //var documentID = ""
        var startTime = ""
        var endTime = ""
        var numParticipants: Long = 0
        var list: MutableList<String> = ArrayList()
        var currentUser = ""
        if (bundle != null){
            documentID = bundle["documentID"].toString()
        }

        dropInViewModel.fetchDropIn(documentID)
        dropInViewModel.fetchUserID()

        dropInViewModel.members.observe(this) {
            list = dropInViewModel.members.value!!
        }

        dropInViewModel.sport.observe(this) {
            binding.titleDropIn.text = "${dropInViewModel.sport.value} Drop-in"
        }
        dropInViewModel.firstName.observe(this) {
            binding.hostName.text = "${dropInViewModel.firstName.value}"
        }
        dropInViewModel.userID.observe(this) {
            currentUser = "${dropInViewModel.userID.value}"
            println("debug: currentuser in observe: $currentUser")
        }
        println("debug: current user: $currentUser")
        dropInViewModel.startTime.observe(this) {
            startTime = "${dropInViewModel.startTime.value}"
        }
        dropInViewModel.endTime.observe(this) {
            endTime = "${dropInViewModel.endTime.value}"
        }
        dropInViewModel.comments.observe(this) {
            binding.aboutDropIn.text = "${dropInViewModel.comments.value}"
        }
        dropInViewModel.numParticipants.observe(this) {
            binding.participantsDropIn.text = "${dropInViewModel.numParticipants.value}"
            numParticipants = dropInViewModel.numParticipants.value!!

        }
        dropInViewModel.skillLevel.observe(this) {
            binding.skillLevelDropIn.text = "${dropInViewModel.skillLevel.value}"
        }

        println("debug: list size ${list.count()}")
        binding.onTheWay.text = "${list.count()} on their way"
        val ontheirWay = numParticipants - list.count()
        binding.spotsRemain.text = "$ontheirWay on their way"

        binding.timeDropIn.text = "$startTime - $endTime"

        if (dropInViewModel.hostID == dropInViewModel.userID) {
            binding.joinButton.setText("Update Drop-In")
            binding.joinButton.setOnClickListener {
                updateDropIn()
            }
        }
        else {
            binding.joinButton.setOnClickListener {
                joinDropIn()
            }
        }
    }

    private fun updateDropIn() {
        val intent = Intent(this, EditDropInActivity::class.java)
        intent.putExtra("documentID", documentID)
        startActivity(intent)
    }

    private fun joinDropIn() {
        //temporary until hostID == userID figured out
        val intent = Intent(this, EditDropInActivity::class.java)
        intent.putExtra("documentID", documentID)
        startActivity(intent)
        //finish()
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