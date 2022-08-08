package com.ancientones.squadup.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.ancientones.squadup.MainActivity
import com.ancientones.squadup.R
import com.ancientones.squadup.User
import com.ancientones.squadup.databinding.ActivityAuthSignupBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.math.ln

class AuthSignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthSignupBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var emailAddressEditText: EditText
    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAuthSignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = Firebase.auth
        emailAddressEditText = findViewById(R.id.sign_up_email_address)
        firstNameEditText = findViewById(R.id.sign_up_first_name)
        lastNameEditText = findViewById(R.id.sign_up_last_name)
        passwordEditText = findViewById(R.id.sign_up_password)
        confirmPasswordEditText = findViewById(R.id.sign_up_password_confirm)
        progressBar = findViewById(R.id.sign_up_progress_bar)
    }

    fun registerUser(view: View) {
        val email = emailAddressEditText.text.toString().trim()
        val fName = firstNameEditText.text.toString().trim()
        val lName = lastNameEditText.text.toString().trim()
        val pword = passwordEditText.text.toString().trim()
        val pwordConfirm = confirmPasswordEditText.toString().trim()

        if(validateEmail(email) == -1) {
            return
        }
        if(validateName(fName, lName) == -1) {
            return
        }
        if(validatePassword(pword, pwordConfirm) == -1) {
            return
        }

        progressBar.visibility = View.VISIBLE
        mAuth.createUserWithEmailAndPassword(email, pword)
            .addOnCompleteListener(this) { task ->
                    if(task.isSuccessful) {
                        val user = User(email, fName, lName)
                        Firebase.database.getReference("Users")
                            .child(FirebaseAuth.getInstance().currentUser!!.uid)
                            .setValue(user).addOnCompleteListener(OnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(this, "User successfully registered!", Toast.LENGTH_SHORT).show()
                                        progressBar.visibility = View.VISIBLE
                                        val userAuth = mAuth.currentUser
                                        val intent = Intent(this, MainActivity::class.java).apply {
                                            putExtra("user", userAuth)
                                        }
                                        startActivity(intent)
                                    } else {
                                        Toast.makeText(this, "User failed to register.", Toast.LENGTH_SHORT).show()
                                        progressBar.visibility = View.GONE
                                    }
                                })
                    } else {
                        Toast.makeText(this, "User failed to register2.", Toast.LENGTH_SHORT).show()
                        progressBar.visibility = View.GONE
                    }
            }

    }

    fun validateEmail(email: String): Int {
        if(email.isEmpty()) {
            emailAddressEditText.requestFocus()
            emailAddressEditText.setError("E-mail is required!")
            return -1
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailAddressEditText.requestFocus()
            emailAddressEditText.setError("E-mail is invalid!")
            return -1
        }
        return 0
    }

    fun validateName(fName: String, lName: String): Int {
        if(fName.isEmpty()) {
            firstNameEditText.requestFocus()
            firstNameEditText.setError("Please provide your first name.")
            return -1
        }

        if(lName.isEmpty()) {
            lastNameEditText.requestFocus()
            lastNameEditText.setError("Please provide your last name.")
            return -1
        }

        return 0
    }

    fun validatePassword(pword: String, pwordConfirm: String): Int {
        if(pword.isEmpty()) {
            passwordEditText.requestFocus()
            passwordEditText.setError("Please input your personal password.")
            return -1
        }

        if(pword.length < 6) {
            passwordEditText.requestFocus()
            passwordEditText.setError("Please input a password 6 characters or longer.")
        }

        if(pword.equals(pwordConfirm)) {
            confirmPasswordEditText.requestFocus()
            confirmPasswordEditText.setError("Your passwords do not match!")
            return -1
        }

        return 0
    }

}