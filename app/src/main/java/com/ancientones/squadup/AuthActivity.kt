package com.ancientones.squadup

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ancientones.squadup.auth.AuthSignInActivity
import com.ancientones.squadup.auth.AuthSignUpActivity
import com.ancientones.squadup.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun onClick(view: View) {
        val intent = Intent(this, AuthSignUpActivity::class.java)
        startActivity(intent)
    }
}
