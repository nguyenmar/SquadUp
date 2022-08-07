package com.ancientones.squadup.dropin

import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.ancientones.squadup.R
import com.ancientones.squadup.databinding.ActivityEditDropInBinding
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import java.io.IOException
import java.util.ArrayList
import java.util.HashMap

//Need to: only let creator of drop in be able to edit it (this should be done in mapfragment)
//Implement date/time
//Convert from latlng back to address

class EditDropInActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditDropInBinding
    private lateinit var dropInViewModel: DropInViewModel

    var documentID = ""
    var comments = ""
    var location: GeoPoint? = null
    var startTime = ""
    var endTime = ""
    var sport = ""
    var skillLevel = ""
    var numParticipants = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditDropInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle = intent.extras

        if (bundle != null){
            documentID = bundle["documentID"].toString()
        }

        dropInViewModel = ViewModelProvider(this).get(DropInViewModel::class.java)

        loadDropIn()

        //populateViews()

        binding.updateButton.setOnClickListener {
            saveFireStore()
        }
    }

    private fun loadDropIn() {

        val sportSpinner = binding.editSportSpinner
        val skillSpinner = binding.editLevelSpinner
        val locationEdit = binding.editLocationText
        val dateEdit = binding.editDate
        val startTimeEdit = binding.editStartTime
        val endTimeEdit = binding.editEndTime
        val participantsEdit = binding.editParticipantsText
        val commentEdit = binding.editCommentsText

        dropInViewModel.fetchDropIn(documentID)
        dropInViewModel.fetchUserID()

        dropInViewModel.sport.observe(this) {
            var sportInt = arrayOf("Soccer","Basketball","Tennis","Baseball","Golf","Volleyball",
                "Badminton","Football","Hockey","Rugby","Others").indexOf(dropInViewModel.sport.value)
            sportSpinner.setSelection(sportInt)
        }
        dropInViewModel.skillLevel.observe(this) {
            var skillInt = arrayOf("Beginner","Intermediate","Advanced","Elite","All").indexOf(dropInViewModel.skillLevel.value)
            skillSpinner.setSelection(skillInt)
        }
        dropInViewModel.location.observe(this) {
            locationEdit.setText(getAddressfromLatLng(dropInViewModel.location.value!!))
        }
        dropInViewModel.startTime.observe(this) {
            startTimeEdit.setText("${dropInViewModel.startTime.value}")
        }
        dropInViewModel.endTime.observe(this) {
            endTimeEdit.setText("${dropInViewModel.endTime.value}")
        }
        dropInViewModel.comments.observe(this) {
            commentEdit.setText("${dropInViewModel.comments.value}")
        }
        dropInViewModel.numParticipants.observe(this) {
            participantsEdit.setText("${dropInViewModel.numParticipants.value}")

        }

    }

    /* Old idea
    private fun populateViews() {
        val sportSpinner = binding.editSportSpinner
        val skillSpinner = binding.editLevelSpinner
        val locationEdit = binding.editLocationText
        val dateEdit = binding.editDate
        val startTimeEdit = binding.editStartTime
        val endTimeEdit = binding.editEndTime
        val participantsEdit = binding.editParticipantsText
        val commentEdit = binding.editCommentsText

        sportSpinner.setSelection(1) //replace with int of position
        skillSpinner.setSelection(1) //replace with int of position
        locationEdit.setText(location.toString()) //replace with conversion back to street?
        //dateEdit.setText()
        startTimeEdit.setText(startTime)
        endTimeEdit.setText(endTime)
        participantsEdit.setText(numParticipants)
        commentEdit.setText(comments)
    }

     */

    private fun saveFireStore(){
        val db = FirebaseFirestore.getInstance()
        val dropin: MutableMap<String,Any> = HashMap()
        val latlng = getLocationFromAddress(binding.editLocationText.text.toString())


        if (latlng != null) {
            dropin["location"] = GeoPoint(latlng.latitude, latlng.longitude)
        }

        if (binding.editSportSpinner.selectedItem != null) {
            dropin["sport"] = binding.editSportSpinner.selectedItem.toString()
        }

        if (binding.editLevelSpinner.selectedItem != null) {
            dropin["skillLevel"] = binding.editLevelSpinner.selectedItem.toString()
        }

        if (binding.editCommentsText.text != null) {
            dropin["comments"] = binding.editCommentsText.text.toString()
        }

        if (binding.editParticipantsText.text != null) {
            dropin["numParticipants"] = binding.editParticipantsText.text.toString()
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
                return null
            }

            var location = address[0]
            latlng = LatLng(location.latitude, location.longitude)

        } catch (ex: IOException) {
            ex.printStackTrace()
        }
        return latlng
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