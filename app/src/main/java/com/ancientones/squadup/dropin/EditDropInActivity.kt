package com.ancientones.squadup.dropin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ancientones.squadup.databinding.ActivityEditDropInBinding

class EditDropInActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditDropInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditDropInBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}