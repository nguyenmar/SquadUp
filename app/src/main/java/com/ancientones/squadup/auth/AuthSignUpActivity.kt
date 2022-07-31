package com.ancientones.squadup.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ancientones.squadup.databinding.ActivityAuthSignupBinding

class AuthSignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthSignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAuthSignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}