package com.ancientones.squadup.ui.profile

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ancientones.squadup.R
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

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
    lateinit var imageUri: Uri
    lateinit var firebaseStorage: FirebaseStorage
    lateinit var storageReference: StorageReference
    var newPhotoFlag: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        firebaseStorage = Firebase.storage
        storageReference = firebaseStorage.reference
        userID = intent.getStringExtra("userID").toString()
        imageView = findViewById(R.id.display_picture)

        val imageRef = storageReference.child("images/${userID}")
        imageRef.getBytes(1024 * 1024).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
            imageView.setImageBitmap(bitmap)
        }.addOnFailureListener() {
            println("DEBUG: User does not currently have a set display photo.")
            imageView.setImageResource(R.drawable.temporary_display_photo)
        }
        imageUri = Uri.parse("android.resource://com.ancientones.squadup/drawable/temporary_display_photo")

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
        val lName = intent.getStringExtra("lastName")
        val age = intent.getStringExtra("userAge")
        val sex = intent.getStringExtra("userSex")
        val height = intent.getStringExtra("userHeight")
        val phoneNumber = intent.getStringExtra("userPhone")
        val userDescription = intent.getStringExtra("userDescription")
        if(sex.equals("Male")) {
            sexRadioButtonMale.isChecked = true
        } else if (sex.equals("Female")) {
            sexRadioButtonFemale.isChecked = true
        }
        fNameEditText.setText(fName)
        lNameEditText.setText(lName)
        ageEditText.setText(age)
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
        db.child("userAge").setValue("${ageEditText.text}")
        val selectedSex = sexRadioGroup.checkedRadioButtonId
        val radioButton = findViewById<RadioButton>(selectedSex)
        db.child("userSex").setValue("${radioButton.text}")
        db.child("userHeight").setValue(heightEditText.text.toString().toInt())
        db.child("userPhone").setValue("${phoneNumberEditText.text}")
        db.child("userDescription").setValue("${userDescriptionEditText.text}")
        if (newPhotoFlag == 0) {
            uploadPicture()
        }

        finish()
    }

    fun displayPictureOnClick(view: View){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.change_display_photo).setItems(R.array.change_display_photo_array,
            DialogInterface.OnClickListener { _, _ ->
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                galleryResult.launch(intent)
            })

        builder.show()
    }

    private val galleryResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if(it.resultCode == Activity.RESULT_OK) {
            imageUri = it.data?.data!!
            imageView.setImageURI(imageUri)
            newPhotoFlag = 0
        }
    }

    fun uploadPicture() {
        val displayPhotoRef = storageReference.child("images/${userID}")
        displayPhotoRef.putFile(imageUri)
        }
    }