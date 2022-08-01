package com.ancientones.squadup.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ancientones.squadup.databinding.FragmentNotificationsBinding
import com.google.firebase.analytics.FirebaseAnalytics


class ProfileFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private var mFirebaseAnalytics: FirebaseAnalytics? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textNotifications
        notificationsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(requireActivity())
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "12345")
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "hello_world_name")
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "test_string")
        mFirebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
