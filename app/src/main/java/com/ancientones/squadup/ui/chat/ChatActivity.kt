package com.ancientones.squadup.ui.chat

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ancientones.squadup.R
import com.ancientones.squadup.databinding.FragmentChatBinding
import com.ancientones.squadup.databinding.FragmentDashboardBinding

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewModel: ChatViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(ChatViewModel::class.java);

        _binding = FragmentChatBinding.inflate(inflater, container, false);
        val root: View = binding.root;
        return root;
    }

}
