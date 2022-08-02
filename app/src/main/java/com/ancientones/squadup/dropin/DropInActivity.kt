package com.ancientones.squadup.dropin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ancientones.squadup.databinding.ActivityDropInBinding

class DropInActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDropInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDropInBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}