package com.ancientones.squadup.ui.profile

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import com.ancientones.squadup.R

class EditProfileActivity : AppCompatActivity() {
    lateinit var imageView: ImageView
    lateinit var fullNameEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        imageView = findViewById(R.id.display_picture)
        imageView.setImageResource(R.drawable.temp_display_photo)
        fullNameEditText = findViewById(R.id.edit_profile_age)
        val fullName = intent.getStringExtra("full_name")
        fullNameEditText.setText(fullName)
    }

    @Override
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.save_profile_menu, menu)
        return true
    }

    fun saveProfile(item: MenuItem) {
        // TODO: Save new info to database
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