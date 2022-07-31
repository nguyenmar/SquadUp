package com.ancientones.squadup.auth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ancientones.squadup.databinding.ActivityAuthSigninBinding

class AuthSignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthSigninBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAuthSigninBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}