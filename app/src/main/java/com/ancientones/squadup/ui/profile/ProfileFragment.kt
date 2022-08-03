package com.ancientones.squadup.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import com.ancientones.squadup.R
import com.ancientones.squadup.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        profileViewModel.fetchUser()

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Adds an edit button in toolbar
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.profile_menu, menu)
            }

            // Handle the menu selection
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.editMenuBtn) {
                    val intent = Intent(requireActivity(), EditProfileActivity::class.java)
                    intent.putExtra("full_name", "${profileViewModel.firstName.value} ${profileViewModel.lastName.value}")
                    startActivity(intent)
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        val usernameView = view.findViewById<TextView>(R.id.username)

        profileViewModel.firstName.observe(requireActivity()) {
            usernameView.text = "${profileViewModel.firstName.value} ${profileViewModel.lastName.value}"
        }

        profileViewModel.lastName.observe(requireActivity()) {
            usernameView.text = "${profileViewModel.firstName.value} ${profileViewModel.lastName.value}"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
