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
import android.telephony.PhoneNumberUtils
import android.util.Patterns
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.ancientones.squadup.MainActivity
import com.ancientones.squadup.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

class SetUpProfileActivity : AppCompatActivity() {
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private lateinit var mAuth: FirebaseAuth
    lateinit var imageView: ImageView
    lateinit var ageEditText: EditText
    lateinit var heightEditText: EditText
    lateinit var phoneNumberEditText: EditText
    lateinit var userDescriptionEditText: EditText
    lateinit var userID: String
    lateinit var sexRadioGroup: RadioGroup
    lateinit var sexRadioButtonMale: RadioButton
    lateinit var sexRadioButtonFemale: RadioButton
    lateinit var imageUri: Uri
    private lateinit var profileImgViewModel: ProfileImgViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_up_profile)

        checkPermissions(this)
        mAuth = Firebase.auth

        firebaseStorage = Firebase.storage
        storageReference = firebaseStorage.reference
        profileImgViewModel = ViewModelProvider(this).get(ProfileImgViewModel::class.java)

        userID = Firebase.auth.currentUser!!.uid
        imageView = findViewById(R.id.setup_display_picture)
        imageUri = Uri.parse("android.resource://com.ancientones.squadup/drawable/temporary_display_photo")

        ageEditText = findViewById(R.id.setup_profile_age)
        heightEditText = findViewById(R.id.setup_profile_height)
        phoneNumberEditText = findViewById(R.id.setup_profile_phone_number)
        userDescriptionEditText = findViewById(R.id.setup_profile_about_me)
        sexRadioGroup = findViewById(R.id.setup_radio_group)
        sexRadioButtonMale = findViewById(R.id.setup_radio_male)
        sexRadioButtonFemale = findViewById(R.id.setup_radio_female)

    }

    override fun onResume(){
        super.onResume()

        profileImgViewModel.userImage.observe(this) {
            if(profileImgViewModel.hasImage.value == true) {
                imageView.setImageBitmap(profileImgViewModel.userImage.value)
            }
        }

        profileImgViewModel.imgUri.observe(this) {
            if (profileImgViewModel.newImage.value == true) {
                imageView.setImageURI(profileImgViewModel.imgUri.value)
            }
        }
    }

    fun checkPermissions(activity: Activity?) {
        if (Build.VERSION.SDK_INT < 23) return
        if (ContextCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA),
                0
            )
        }
    }

    @Override
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.save_profile_menu, menu)
        return true
    }

    fun saveProfile(item: MenuItem) {
        val age = ageEditText.text.toString().trim()
        val height = heightEditText.text.toString().trim()
        val phone = phoneNumberEditText.text.toString().trim()

        if (validateAge(age) == -1) {
            return
        }

        if(validateSex() == -1) {
            return
        }

        if(validateHeight(height) == -1) {
            return
        }

        if(validatePhone(phone) == -1) {
            return
        }

        val db = Firebase.database.getReference("Users").child(userID)
        db.child("userAge").setValue("${ageEditText.text}")
        val selectedSex = sexRadioGroup.checkedRadioButtonId
        val radioButton = findViewById<RadioButton>(selectedSex)
        db.child("userSex").setValue("${radioButton.text}")
        db.child("userHeight").setValue(heightEditText.text.toString().toInt())
        db.child("userPhone").setValue("${phoneNumberEditText.text}")
        db.child("userDescription").setValue("${userDescriptionEditText.text}")
        if (profileImgViewModel.newImage.value == true) {
            val displayPhotoRef = storageReference.child("images/${userID}")
            displayPhotoRef.putFile(profileImgViewModel.imgUri.value!!)
        }

        val userAuth = mAuth.currentUser
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("user", userAuth)
        }
        startActivity(intent)
    }

    private val galleryResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if(it.resultCode == Activity.RESULT_OK) {
            imageUri = it.data?.data!!
//            imageView.setImageURI(imageUri)
//            newPhotoFlag = 0
            profileImgViewModel.imgUri.value = imageUri
            profileImgViewModel.newImage.value = true
        }
    }

    fun uploadPicture() {
        val displayPhotoRef = storageReference.child("images/${userID}")
        displayPhotoRef.putFile(imageUri)
    }

    fun validateAge(age: String): Int {
        if(age.isEmpty()) {
            ageEditText.requestFocus()
            ageEditText.setError("Age is required")
            return -1
        }
        return 0
    }

    fun validateSex(): Int {
        if(!sexRadioButtonFemale.isChecked && !sexRadioButtonMale.isChecked) {
            Toast.makeText(this, "Please select your sex.", Toast.LENGTH_SHORT).show()
            return -1
        }
        return 0
    }

    fun validateHeight(age: String): Int {
        if(age.isEmpty()) {
            heightEditText.requestFocus()
            heightEditText.setError("Height is required")
            return -1
        }
        return 0
    }

    fun validatePhone(phone: String): Int {
        if(phone.isEmpty() || phone.length != 10) {
            phoneNumberEditText.requestFocus()
            phoneNumberEditText.setError("Enter a valid phone number")
            return -1
        }
        return 0
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
}