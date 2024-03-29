package com.ancientones.squadup.dropin

import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import com.ancientones.squadup.AlertDialogFragment
import com.ancientones.squadup.R
import com.ancientones.squadup.databinding.ActivityDropInBinding
import com.ancientones.squadup.ui.chat.ChatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.HashMap


class DropInActivity : AppCompatActivity(), OnMapReadyCallback{
    private lateinit var binding: ActivityDropInBinding
    private lateinit var mMap: GoogleMap
    //private var location: GeoPoint = GeoPoint(0.0,0.0)
    private lateinit var dropInViewModel: DropInViewModel
    private var documentID = ""
    var currentUser = Firebase.auth.currentUser!!.uid
    lateinit var dialogFragment: AlertDialogFragment
    private val calendar = Calendar.getInstance()

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

        if (bundle != null) {
            documentID = bundle["documentID"].toString()
        }

        populateDropIn()

    }

    private fun populateDropIn() {
        var startTime = ""
        var endTime = ""
        var numParticipants: Long = 0

        dropInViewModel.fetchDropIn(documentID)
        dropInViewModel.fetchUserID()
        dropInViewModel.members.observe(this) {
            var list = dropInViewModel.members.value!!
            binding.onTheWay.text = "${dropInViewModel.members.value!!.count()} on their way"
            val listCount = dropInViewModel.members.value!!.count().toLong()
            val spotRemain = dropInViewModel.numParticipants.value?.minus(listCount)
            binding.spotsRemain.text = "$spotRemain spots remaining"
            if("${dropInViewModel.hostID.value}" != currentUser){
                if(list.contains(currentUser)){
                    println("member is present in list")
                    binding.joinButton.text = "Leave Drop-in"
                }
            }
        }

        dropInViewModel.sport.observe(this) {
            binding.titleDropIn.text = "${dropInViewModel.sport.value} Drop-in"
        }
        dropInViewModel.firstName.observe(this) {
            binding.hostName.text =
                "${dropInViewModel.firstName.value} ${dropInViewModel.lastName.value}"
        }
        dropInViewModel.lastName.observe(this) {
            binding.hostName.text =
                "${dropInViewModel.firstName.value} ${dropInViewModel.lastName.value}"
        }
        dropInViewModel.date.observe(this) {
            binding.dateDropIn.text =
                "${dropInViewModel.date.value}"
        }

        dropInViewModel.startTime.observe(this) {
            binding.timeDropIn.text =
                "${dropInViewModel.startTime.value} - ${dropInViewModel.endTime.value}"
        }
        dropInViewModel.endTime.observe(this) {
            binding.timeDropIn.text =
                "${dropInViewModel.startTime.value} - ${dropInViewModel.endTime.value}"
        }
        dropInViewModel.comments.observe(this) {
            binding.aboutDropIn.text = "${dropInViewModel.comments.value}"
        }
        dropInViewModel.numParticipants.observe(this) {
            binding.participantsDropIn.text = "${dropInViewModel.numParticipants.value}"
        }
        dropInViewModel.skillLevel.observe(this) {
            binding.skillLevelDropIn.text = "${dropInViewModel.skillLevel.value}"
        }
        dropInViewModel.hostID.observe(this) {
            if ("${dropInViewModel.hostID.value}" == currentUser) {
                binding.joinButton.text = "Update Drop-in"
                if(correctDate("${dropInViewModel.date.value}", "${dropInViewModel.startTime.value}")){
                    binding.joinButton.text = "Complete Drop-in"
                }
            }


            binding.joinButton.setOnClickListener {
                if (binding.joinButton.text == "Update Drop-in") {
                    updateDropIn()
                }
                else if (binding.joinButton.text == "Leave Drop-in"){
                    leaveDropIn()
                }
                else if (binding.joinButton.text == "Complete Drop-in"){
                    completeDropIn()
                }
                else{
                    joinDropIn()
                }
            }
        }

        // Open chat button
        binding.openChatButton.setOnClickListener {
            val chatIntent = Intent(this, ChatActivity::class.java);
            chatIntent.putExtra( ChatActivity.CHAT_ID_KEY, documentID );
            startActivity( chatIntent );
        }
    }

    private fun correctDate(date: String, startTime: String): Boolean {

        println("running correct Date check")

        var sdf = SimpleDateFormat("MMMM d, yyyy")
        var currentDate: String = sdf.format(Date())

        var sdfTime = SimpleDateFormat("H:m")
        var currentTime: String = sdfTime.format(Date())

        val formatter = DateTimeFormatter.ofPattern("H:m")
        var currentTimeParsed = LocalTime.parse(currentTime, formatter)
        var startTimeParsed = LocalTime.parse(startTime, formatter)

        println(startTimeParsed)
        println(currentTimeParsed)

        if (currentDate == date && currentTimeParsed.isBefore(startTimeParsed) &&
            Duration.between(startTimeParsed, currentTimeParsed).toMinutes() <= 5) {
            println("returning true")
            return true
        }
        println("returning false")
        return false
    }

    private fun updateDropIn() {
        val intent = Intent(this, EditDropInActivity::class.java)
        intent.putExtra("documentID", documentID)
        startActivity(intent)
    }

    private fun joinDropIn() {
        val db = FirebaseFirestore.getInstance()
        db.collection("dropin").document(documentID)

        val addUserToArrayMap: MutableMap<String, Any> = HashMap()
        addUserToArrayMap["members"] = FieldValue.arrayUnion(currentUser)

        db.collection("dropin").document(documentID)
            .update(addUserToArrayMap)

        dialogFragment = AlertDialogFragment()
        dialogFragment.dialogType = "Join"
        dialogFragment.show(supportFragmentManager, "dialog")
    }

    private fun leaveDropIn(){
        val db = FirebaseFirestore.getInstance()
        db.collection("dropin").document(documentID)

        val removeUserFromArrayMap: MutableMap<String, Any> = HashMap()
        removeUserFromArrayMap["members"] = FieldValue.arrayRemove(currentUser)

        db.collection("dropin").document(documentID)
            .update(removeUserFromArrayMap)

        finish()
    }

    private fun completeDropIn(){
        //send rating activity notification to members
        //mark drop-in as complete
        val db = FirebaseFirestore.getInstance()
        db.collection("dropin").document(documentID)
            .update("isCompleted", true)
            .addOnSuccessListener { Toast.makeText((this), "Drop-in successfully completed", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { Toast.makeText((this), "Drop-in failed to be completed", Toast.LENGTH_SHORT).show()
            }

        finish()

        //delete chat

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

    override fun onResume() {
        super.onResume()
        populateDropIn()
    }
}
