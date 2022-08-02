package com.ancientones.squadup.dropin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ancientones.squadup.databinding.ActivityAddDropInBinding

class AddDropInActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddDropInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddDropInBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}