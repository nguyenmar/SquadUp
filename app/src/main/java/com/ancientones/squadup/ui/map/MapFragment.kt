package com.ancientones.squadup.ui.map

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.ancientones.squadup.AlertDialogFragment
import com.ancientones.squadup.MapViewModel
import com.ancientones.squadup.R
import com.ancientones.squadup.TrackingService
import com.ancientones.squadup.databinding.ActivityMainBinding
import com.ancientones.squadup.dropin.AddDropInActivity
import com.ancientones.squadup.dropin.DropInActivity
import com.ancientones.squadup.dropin.DropInViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.Math.*
import java.lang.reflect.Type
import java.util.concurrent.Flow
import kotlin.math.pow


class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var intent: Intent

    private lateinit var mMap: GoogleMap
    private var mapCentered = false

    private var bound = false

    private lateinit var locationList: ArrayList<LatLng>

    private lateinit var mapViewModel: MapViewModel

    private lateinit var fab: FloatingActionButton

    private lateinit var binding: ActivityMainBinding

    private lateinit var dropInViewModel: DropInViewModel

    private lateinit var currentUser: String

    private lateinit var userDropIns: ArrayList<String>

    lateinit var dialogFragment: AlertDialogFragment

    private var dialogShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (isAdded) {
            intent = Intent(requireActivity(), TrackingService::class.java)
        }
        mapViewModel = ViewModelProvider(this)[MapViewModel::class.java]

        locationList = ArrayList()

        userDropIns = ArrayList()

        dropInViewModel = ViewModelProvider(this).get(DropInViewModel::class.java)

        currentUser = Firebase.auth.currentUser!!.uid



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView: View = inflater.inflate(R.layout.fragment_map, container, false)



        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)

        fab = rootView.findViewById(R.id.fab)

        fabOnClick()

        return rootView
    }

    //Floating Action Button OnClick, change to what action is needed
    private fun fabOnClick() {
        fab.setOnClickListener { view ->
            val intent = Intent(context, AddDropInActivity::class.java)
            startActivity(intent)
        }

    }



    override fun onMapReady(googleMap: GoogleMap) {
        checkPermission()
        mMap = googleMap
        mMap.isMyLocationEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true

        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL


        mapViewModel.bundle.observe(this) {
            updateMap(it)
        }

        val db = FirebaseFirestore.getInstance()
        var location: LatLng = LatLng(0.0,0.0)

        db.collection("dropin")
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                if(value != null){
                    val documents = value.documents
                    documents.forEach{ it ->
                        val membersList = it.get("members") as MutableList<String>
                        val documentID = it.id
                        val geoPoint = it.getGeoPoint("location")
                        println(documentID)
                        println(geoPoint)
                        if (geoPoint != null) {
                            location = LatLng(geoPoint.latitude, geoPoint.longitude)
                        }

                        membersList.forEach { it ->
                            if (it == Firebase.auth.currentUser!!.uid) {
                                //save location and drop in info
                                userDropIns.add(documentID)
                            }
                        }

                        mMap.addMarker(MarkerOptions().position(location).title(documentID))
                    }
                }
            }


        mMap.setOnMarkerClickListener { marker ->
            val intent = Intent(context, DropInActivity::class.java)
            intent.putExtra("documentID", marker.title)
            startActivity(intent)
            true
        }
    }

    private fun updateMap(bundle: Bundle) {

        if (bundle.getString(TrackingService.LOC_KEY) != null) {
                locationList = toArrayList(bundle.getString(TrackingService.LOC_KEY)!!)
            }

        if (!mapCentered) {
            val latLng = locationList.last()
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 12f)
            mMap.animateCamera(cameraUpdate)
            mapCentered = true
        }

        //check here if user is within a certain distance of drop ins they joined (and time is soon)
        userDropIns.forEach { it ->
            dropInViewModel.fetchDropIn(it)

            dropInViewModel.location.observe(this) {
                val location = dropInViewModel.location.value!!
                val locationLatLng = LatLng(location.latitude, location.longitude)
                if (closeTo(locationLatLng, locationList.last()) && !dialogShown) {
                    //dialog
                    dialogFragment = AlertDialogFragment()
                    dialogFragment.dialogType = "Automatic"
                    dialogFragment.show(requireActivity().supportFragmentManager, "dialog")
                    dialogShown = true
                }
            }
        }

    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0)
        else {
            requireContext().startService(intent)
            if (!bound) {
                requireContext().bindService(intent, mapViewModel, Context.BIND_AUTO_CREATE)
                bound = true
            }
        }
    }

    private fun toArrayList(json: String): ArrayList<LatLng> {
        val gson = Gson()
        val listType: Type = object : TypeToken<ArrayList<LatLng>>() {}.type
        return gson.fromJson(json, listType)
    }

    //Credit : https://www.geeksforgeeks.org/haversine-formula-to-find-distance-between-two-points-on-a-sphere/
    //Returns if distance between two locations is less than 1 km
    private fun closeTo(location1: LatLng, location2: LatLng): Boolean {
        val lat = abs(location1.latitude - location2.latitude) * Math.PI / 180.0
        val lon = abs(location1.longitude - location2.longitude) * Math.PI / 180.0

        val latitude1 = location1.latitude * Math.PI / 180.0
        val latitude2 = location2.latitude * Math.PI / 180.0

        val a = sin(lat / 2).pow(2) + sin(lon / 2).pow(2) * cos(latitude1) * cos(latitude2)

        val rad = 6371

        val c = 2 * asin(sqrt(a))

        return (rad * c <= 1)
    }

}