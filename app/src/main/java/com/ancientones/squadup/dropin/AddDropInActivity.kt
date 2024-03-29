package com.ancientones.squadup.dropin

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ancientones.squadup.R
import com.ancientones.squadup.database.models.Chat
import android.view.View
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import com.ancientones.squadup.databinding.ActivityAddDropInBinding
import com.ancientones.squadup.ui.chat.ChatActivity
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.ktx.Firebase
import java.io.IOException
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class AddDropInActivity : AppCompatActivity(), TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {
    private lateinit var binding: ActivityAddDropInBinding
    private lateinit var name: String;
    private lateinit var dropInViewModel: DropInViewModel
    private val calendar = Calendar.getInstance()
    private lateinit var dateText: EditText
    private lateinit var startTimeText: EditText
    private lateinit var endTimeText: EditText
    private var isStartTime: Boolean = false
    companion object{
        val dialogViewModel = DialogViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddDropInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dropInViewModel = ViewModelProvider(this).get(DropInViewModel::class.java)
        dropInViewModel.fetchUserID()

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

        startTimeText = binding.startTime
        endTimeText = binding.endTime
        dateText = binding.date

        startTimeText.setOnClickListener {
            onClickTime(binding.root)
            isStartTime = true
        }
        endTimeText.setOnClickListener {
            onClickTime(binding.root)
            isStartTime = false
        }

        dateText.setOnClickListener {
            onClickDate(binding.root)
        }

    }
    private fun validateValues(): Boolean{
        if (binding.locationText.text.isEmpty()) {
            binding.locationText.requestFocus()
            binding.locationText.error = "Location is empty"
            return false
        }

        if (getLocationFromAddress(binding.locationText.text.toString()) == null) {
            binding.locationText.requestFocus()
            binding.locationText.error = "Address is invalid"
            return false
        }

        if (binding.startTime.text.isEmpty()) {
            binding.startTime.requestFocus()
            binding.startTime.error = "Start time is empty"
            return false
        }

        if (binding.endTime.text.isEmpty()) {
            binding.endTime.requestFocus()
            binding.endTime.error = "End time is empty"
            return false
        }

        if (binding.participantsText.text.isEmpty()) {
            binding.participantsText.requestFocus()
            binding.participantsText.error = "Number of Participants is empty"
            return false
        }

        if (binding.commentsText.text.isEmpty()) {
            binding.commentsText.requestFocus()
            binding.commentsText.error = "Comments is empty"
            return false
        }
       return true
    }
    private fun saveFireStore(){
        println("save button pressed")
        val validated = validateValues()

        if (validated){
            val db = FirebaseFirestore.getInstance()
            val dropin: MutableMap<String,Any> = HashMap()
            val list: MutableList<String> = ArrayList()

            val latlng = getLocationFromAddress(binding.locationText.text.toString())
            if (latlng != null) {
                dropin["location"] = GeoPoint(latlng.latitude, latlng.longitude)
            }
            var currentUser = Firebase.auth.currentUser!!.uid
            list.add(currentUser)

            dropin["hostID"] = currentUser
            dropin["sport"] = binding.sportSpinner.selectedItem.toString()
            dropin["skillLevel"] = binding.levelSpinner.selectedItem.toString()
            dropin["comments"] = binding.commentsText.text.toString()
            dropin["startTime"] = binding.startTime.text.toString()
            dropin["endTime"] = binding.endTime.text.toString()
            dropin["date"] = binding.date.text.toString()
            dropin["numParticipants"] = binding.participantsText.text.toString().toInt()
            dropin["members"] = list
            dropin["isCompleted"] = false
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

    }

    private fun getLocationFromAddress(addressString: String): LatLng? {
        var coder: Geocoder = Geocoder(applicationContext, Locale.CANADA)
        var latlng: LatLng? = null
        println("debug: address $addressString")
        try {
            var address = coder.getFromLocationName(addressString, 5)
            println("debug: $address")

            if (address.isEmpty()){
                println("debug: address is null")
                return null
            }

            var location = address[0]
            latlng = LatLng(location.latitude, location.longitude)

        } catch (ex: IOException) {
            ex.printStackTrace()
        }
        return latlng
    }

    private fun onClickTime(view: View){
        val timePickerDialog = TimePickerDialog(this, this,
            calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false
        )
        timePickerDialog.show()
    }

    private fun onClickDate(view: View){
        val datePickerDialog = DatePickerDialog(this, this,
            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {

        var fmt = SimpleDateFormat("HH:mm")
        var time = fmt.parse("$hourOfDay:$minute")
        val fmtOut = SimpleDateFormat("HH:mm")
        val formattedTime = fmtOut.format(time)

        if (isStartTime) {

            binding.startTime.setText(formattedTime)
            dialogViewModel.setStartTime(formattedTime)
        }
        else {
            binding.endTime.setText(formattedTime)
            dialogViewModel.setEndTime(formattedTime)
        }

    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val date = "${DateFormatSymbols().months[month]} $dayOfMonth, $year"
        dialogViewModel.setDate(date)
        binding.date.setText(date)
    }

}
