package com.ancientones.squadup.dropin

import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.ancientones.squadup.R
import com.ancientones.squadup.databinding.ActivityEditDropInBinding
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import java.io.IOException
import java.util.HashMap

//Need to: only let creator of drop in be able to edit it (this should be done in mapfragment)
//Implement date/time
//Convert from latlng back to address

class EditDropInActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditDropInBinding

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

        loadDropIn()

        populateViews()

        binding.updateButton.setOnClickListener {
            saveFireStore()
        }
    }

    private fun loadDropIn() {

        val db = FirebaseFirestore.getInstance()

        val docRef = db.collection("dropin").document(documentID)
        println("documentID --- $documentID")
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    println("DocumentSnapshot data: ${document.data}")
                    comments = document.get("comment").toString()
                    location = document.getGeoPoint("location")
                    startTime = document.get("").toString()
                    endTime = document.get("").toString()
                    sport = document.get("sport").toString()
                    skillLevel = document.get("skillLevel").toString()
                    numParticipants = document.get("numParticipants").toString()

                } else {
                    println("No document")
                }
            }
            .addOnFailureListener { exception ->
                println("failed to get document")
            }

    }

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


}