package com.ancientones.squadup.ui.dropin

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import com.ancientones.squadup.AuthActivity
import com.ancientones.squadup.R
import com.ancientones.squadup.databinding.FragmentHistoryListBinding
import com.ancientones.squadup.dropin.DropInMembersActivity
import com.ancientones.squadup.ui.profile.EditProfileActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class HistoryFragment : Fragment() {
    private var _binding: FragmentHistoryListBinding? = null
    private val binding get() = _binding!!
    private lateinit var dropinListAdapter: DropinListAdapter
    private lateinit var historyViewModel: HistoryViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        historyViewModel = ViewModelProvider(this).get(HistoryViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        _binding = FragmentHistoryListBinding.inflate(inflater, container, false)
        val root: View = binding.root
        historyViewModel.getCompletedDropins()
        historyViewModel.documentList.observe(viewLifecycleOwner) {
            println("change in documents id list")
            println(it)
            dropinListAdapter = DropinListAdapter(requireActivity(), it)
            binding.listView.adapter = dropinListAdapter
        }
        return root
    }

}