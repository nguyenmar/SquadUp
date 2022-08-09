package com.ancientones.squadup.ui.profile

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.ancientones.squadup.R
import com.ancientones.squadup.databinding.ActivityRatingBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class RateProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRatingBinding

    private lateinit var imageView: ImageView
    private lateinit var profileImgViewModel: ProfileImgViewModel

    private lateinit var userID: String // user being rated, passed in through an intent
    private lateinit var firstName: String

    private var hasModifiedRating: Boolean = false

    private lateinit var addBtn: Button
    private lateinit var cancelBtn: Button
    private lateinit var description: TextView
    private lateinit var ratingBar: RatingBar
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRatingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        addBtn = findViewById(R.id.addBtn)
        cancelBtn = findViewById(R.id.cancelBtn)
        description = findViewById(R.id.description)
        ratingBar = findViewById(R.id.teamworkRating)
        imageView = findViewById(R.id.display_picture)

        userID = intent.getStringExtra("userID").toString()
        firstName = intent.getStringExtra("firstName").toString()
        dbRef = Firebase.database.getReference("Users").child(userID)

        profileImgViewModel = ViewModelProvider(this).get(ProfileImgViewModel::class.java)
        profileImgViewModel.fetchExternalUserImage(userID)

        description.text = "Rate your experience playing with $firstName"

        addBtn.setOnClickListener{
            if(hasModifiedRating) {
                saveRating()
            }
            else{
                Toast.makeText(baseContext, "Please set a rating.", Toast.LENGTH_SHORT).show()
            }
        }
        
        cancelBtn.setOnClickListener{
            finish()
        }

        ratingBar.setOnRatingBarChangeListener { _, _, _ ->
            hasModifiedRating = true
        }

        profileImgViewModel.userImage.observe(this) {
            setUserImage()
        }

        profileImgViewModel.hasImage.observe(this) {
            setUserImage()
        }
    }


    private fun saveRating() {
        var hasRated: List<String>
        var teamworkRatings: List<Double>

        val newRating = ratingBar.rating.toDouble()

        dbRef.child("hasRated").get().addOnSuccessListener {
            if (it.value != null) {
                hasRated = it.value as List<String>
                hasRated = hasRated.plus(Firebase.auth.currentUser!!.uid)
            }
            else {
                hasRated = listOf(Firebase.auth.currentUser!!.uid)
            }
            dbRef.child("hasRated").setValue(hasRated)
            println("hasRated: $hasRated")
        }

        dbRef.child("teamworkRatings").get().addOnSuccessListener {
            if (it.value != null) {
                teamworkRatings = it.value as List<Double>
                teamworkRatings = teamworkRatings.plus(newRating)
            }
            else {
                teamworkRatings = listOf(newRating)
            }
            dbRef.child("teamworkRatings").setValue(teamworkRatings)
            println("ratings: $teamworkRatings")
        }
        finish()
    }

    private fun setUserImage() {
        if(profileImgViewModel.hasImage.value == true) {
            imageView.setImageBitmap(profileImgViewModel.userImage.value)
        }
        else{
            imageView.setImageResource(R.drawable.temporary_display_photo)
        }
    }

}