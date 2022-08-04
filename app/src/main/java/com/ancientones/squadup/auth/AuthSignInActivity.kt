package com.ancientones.squadup.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ancientones.squadup.MainActivity
import com.ancientones.squadup.databinding.ActivityAuthSigninBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthSignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthSigninBinding
    private lateinit var auth: FirebaseAuth

    companion object {
        private const val TAG = "EmailPasswordSignIn"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        binding = ActivityAuthSigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signInBtn.setOnClickListener{
            val userEmail = binding.signInEmailAddress.text
            val userPassword = binding.signInPassword.text
            if(userEmail.isNotEmpty() && userPassword.isNotEmpty()) {
                signIn(userEmail.toString(), userPassword.toString())
            }
            else {
                Toast.makeText(baseContext, "Please enter a valid email and password.",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    Log.d(TAG, user.toString())
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }

            }
    }
}