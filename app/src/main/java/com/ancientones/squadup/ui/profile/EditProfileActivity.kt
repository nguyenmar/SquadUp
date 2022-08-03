package com.ancientones.squadup.ui.profile

import android.app.AlertDialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import com.ancientones.squadup.R
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class EditProfileActivity : AppCompatActivity() {
    lateinit var imageView: ImageView
    lateinit var fNameEditText: EditText
    lateinit var lNameEditText: EditText
    lateinit var ageEditText: EditText
    lateinit var heightEditText: EditText
    lateinit var phoneNumberEditText: EditText
    lateinit var userDescriptionEditText: EditText
    lateinit var userID: String
    lateinit var sexRadioGroup: RadioGroup
    lateinit var sexRadioButtonMale: RadioButton
    lateinit var sexRadioButtonFemale: RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        imageView = findViewById(R.id.display_picture)
        imageView.setImageResource(R.drawable.temporary_display_photo)
        fNameEditText = findViewById(R.id.edit_profile_fname)
        lNameEditText = findViewById(R.id.edit_profile_lname)
        ageEditText = findViewById(R.id.edit_profile_age)
        heightEditText = findViewById(R.id.edit_profile_height)
        phoneNumberEditText = findViewById(R.id.edit_profile_phone_number)
        userDescriptionEditText = findViewById(R.id.edit_profile_about_me)
        sexRadioGroup = findViewById(R.id.radio_group)
        sexRadioButtonMale = findViewById(R.id.radio_male)
        sexRadioButtonFemale = findViewById(R.id.radio_female)

        val fName = intent.getStringExtra("firstName")
        val lname = intent.getStringExtra("lastName")
        val sex = intent.getStringExtra("userSex")
        val height = intent.getStringExtra("userHeight")
        val phoneNumber = intent.getStringExtra("userPhone")
        val userDescription = intent.getStringExtra("userDescription")
        if(sex.equals("Male")) {
            sexRadioButtonMale.isChecked = true
        } else if (sex.equals("Female")) {
            sexRadioButtonFemale.isChecked = true
        }
        userID = intent.getStringExtra("userID").toString()
        fNameEditText.setText(fName)
        lNameEditText.setText(lname)
        heightEditText.setText(height)
        phoneNumberEditText.setText(phoneNumber)
        userDescriptionEditText.setText(userDescription)

    }

    @Override
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.save_profile_menu, menu)
        return true
    }

    fun saveProfile(item: MenuItem) {
        // TODO: Save new info to database
        val db = Firebase.database.getReference("Users").child(userID)
        db.child("firstName").setValue("${fNameEditText.text}")
        db.child("lastName").setValue("${lNameEditText.text}")
        val selectedSex = sexRadioGroup.checkedRadioButtonId
        val radioButton = findViewById<RadioButton>(selectedSex)
        db.child("userSex").setValue("${radioButton.text}")
        db.child("userHeight").setValue(heightEditText.text.toString().toInt())
        db.child("userPhone").setValue("${phoneNumberEditText.text}")
        db.child("userDescription").setValue("${userDescriptionEditText.text}")
        finish()
    }

    fun displayPictureOnClick(view: View){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.change_display_photo).setItems(R.array.change_display_photo_array,
            DialogInterface.OnClickListener { dialog, which ->
                if(which == 0) {    //Open Camera
//                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//                    intent.putExtra(MediaStore.EXTRA_OUTPUT, tempImageUri)
//                    cameraResult.launch(intent)
                } else {            //Select from Gallery
//                    val intent = Intent(Intent.ACTION_PICK)
//                    intent.setType("image/*")
//                    result.launch(intent)
                }
            })

        builder.show()
    }

}