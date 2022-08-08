package com.ancientones.squadup.ui.map

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ancientones.squadup.MapViewModel
import com.ancientones.squadup.R
import com.ancientones.squadup.TrackingService
import com.ancientones.squadup.databinding.ActivityMainBinding
import com.ancientones.squadup.dropin.AddDropInActivity
import com.ancientones.squadup.dropin.DropInActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var intent: Intent

    private lateinit var mMap: GoogleMap
    private var mapCentered = false

    private var bound = false

    private lateinit var locationList: ArrayList<LatLng>

    private lateinit var mapViewModel: MapViewModel

    private lateinit var fab: FloatingActionButton

    private lateinit var binding: ActivityMainBinding

    var mMarkers: MutableList<Marker> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (isAdded) {
            intent = Intent(requireActivity(), TrackingService::class.java)
        }
        mapViewModel = ViewModelProvider(this)[MapViewModel::class.java]

        locationList = ArrayList()

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
        //mapViewModel.fetchDropIns()

        val db = FirebaseFirestore.getInstance()
        var location: LatLng = LatLng(0.0,0.0)

        /*mapViewModel.isCompleted.observe(this) {

        }*/



        db.collection("dropin")
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                if(value != null){
                    println("debug: database changed")
                    val documents = value.documents
                    for(marker: Marker in mMarkers)
                    {
                        marker.remove()
                    }
                    mMarkers.clear()

                    documents.forEach{
                        println("debug: ${it.id}")
                        val documentID = it.id
                        val isCompleted = it.getBoolean("isCompleted")

                        if (isCompleted == false) {
                            val geoPoint = it.getGeoPoint("location")
                            println(documentID)
                            println(geoPoint)
                            if (geoPoint != null) {
                                location = LatLng(geoPoint.latitude, geoPoint.longitude)
                            }
                            var marker = mMap.addMarker(MarkerOptions().position(location).title(documentID))

                            if (marker != null) {
                                mMarkers.add(marker)
                            }
                        }

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

}