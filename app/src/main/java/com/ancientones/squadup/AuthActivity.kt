package com.ancientones.squadup

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ancientones.squadup.auth.AuthSignInActivity
import com.ancientones.squadup.auth.AuthSignUpActivity
import com.ancientones.squadup.databinding.ActivityAuthBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var videoView: VideoView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAuthBinding.inflate(layoutInflater)
        auth = Firebase.auth

        binding.signUpBtn.setOnClickListener{
            val intent = Intent(this, AuthSignUpActivity::class.java)
            startActivity(intent)
        }

        binding.signInBtn.setOnClickListener{
            val intent = Intent(this, AuthSignInActivity::class.java)
            startActivity(intent)
        }
        setContentView(binding.root)

        videoView = findViewById(R.id.videoView)
        val uri: Uri = Uri.parse("android.resource://"+packageName+"/"+R.raw.auth_video)
        videoView.setVideoURI(uri)
        videoView.start()
        videoView.setOnPreparedListener {
            it.isLooping = true
        }

        checkMapPermissions()


    }

    override fun onStart() {
        super.onStart()
        if(auth.currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            Toast.makeText(baseContext, "Welcome back!",
                Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        videoView.start()
    }

    private fun checkMapPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0)
    }

}
