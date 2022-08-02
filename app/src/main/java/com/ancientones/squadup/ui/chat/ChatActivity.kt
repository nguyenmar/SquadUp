package com.ancientones.squadup.ui.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.ancientones.squadup.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {
    companion object {
        val CHAT_ID_KEY = "chat_id_key";
    }

    private lateinit var binding: ActivityChatBinding;
    private lateinit var viewModel: ChatViewModel;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ChatViewModel::class.java);

        binding = ActivityChatBinding.inflate(layoutInflater);
        setContentView(binding.root);
    }
}
