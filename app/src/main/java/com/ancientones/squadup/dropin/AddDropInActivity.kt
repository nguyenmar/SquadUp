package com.ancientones.squadup.dropin

import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ancientones.squadup.R
import com.ancientones.squadup.database.models.Chat
import com.ancientones.squadup.databinding.ActivityAddDropInBinding
import com.ancientones.squadup.ui.chat.ChatActivity
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.IOException
import java.util.*
import kotlin.collections.HashMap


class AddDropInActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddDropInBinding
    private lateinit var name: String;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddDropInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val listOfSports = resources.getStringArray(R.array.listOfSports)
        val sportSpinner = findViewById<Spinner>(R.id.sport_spinner)

        binding.saveButton.setOnClickListener {
            saveFireStore()
        }

       // set users name
        Firebase.database.getReference("Users")
            .child(Firebase.auth.currentUser?.uid.toString())
            .get()
            .addOnSuccessListener {
                Log.i("firebase", "got user: ${it.value}")
                val userMap: HashMap<String, String> = it.value as HashMap<String, String>
                name = "${userMap["firstName"]} ${userMap["lastName"]}";
            }
    }

    private fun saveFireStore(){
        val db = FirebaseFirestore.getInstance()
        val dropin: MutableMap<String,Any> = HashMap()
        val latlng = getLocationFromAddress(binding.locationText.text.toString())


        if (latlng != null) {
            dropin["location"] = GeoPoint(latlng.latitude, latlng.longitude)
        }

        if (binding.sportSpinner.selectedItem != null) {
            dropin["sport"] = binding.sportSpinner.selectedItem.toString()
        }

        if (binding.levelSpinner.selectedItem != null) {
            dropin["skillLevel"] = binding.levelSpinner.selectedItem.toString()
        }

        if (binding.commentsText.text != null) {
            dropin["comments"] = binding.commentsText.text.toString()
        }

        if (binding.participantsText.text != null) {
            dropin["numParticipants"] = binding.participantsText.text.toString()
        }


        db.collection("dropin")
            .add(dropin)
            .addOnSuccessListener {
                Toast.makeText((this), "Drop-in successfully created", Toast.LENGTH_SHORT).show()

                // create chat
                val title = "${name}'s ${dropin["sport"]} drop-in";
                db.collection( ChatActivity.CHAT_COLLECTION_NAME ).document(it.id)
                    .set( Chat("", it.id, title) );
            }
            .addOnFailureListener {Toast.makeText((this), "Drop-in failed to be created", Toast.LENGTH_SHORT).show()
            }

        finish()
    }

    fun getLocationFromAddress(addressString: String): LatLng? {
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
