package com.ancientones.squadup.ui.chat

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ancientones.squadup.databinding.FragmentChatListBinding

class ChatListFragment : Fragment() {

    private var _binding: FragmentChatListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewModel: ChatListViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(ChatListViewModel::class.java);

        _binding = FragmentChatListBinding.inflate(inflater, container, false);
        val root: View = binding.root;
        return root;
    }

    override fun onDestroyView() {
        super.onDestroyView();
        _binding = null;
    }

}
