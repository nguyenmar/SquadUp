package com.ancientones.squadup

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ancientones.squadup.auth.AuthSignInActivity
import com.ancientones.squadup.auth.AuthSignUpActivity
import com.ancientones.squadup.databinding.ActivityAuthBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private lateinit var auth: FirebaseAuth

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

}
