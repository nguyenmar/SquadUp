package com.ancientones.squadup.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.*
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

    // TODO: add rest of ProfileViewModel user details
    // User Profile details
    private lateinit var profileViewModel: ProfileViewModel
    private var firstName: String = ""
    private var lastName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        profileViewModel.fetchUser()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        profileViewModel.firstName.observe(requireActivity()) {
            firstName = it
            if(firstName.isNotEmpty() && lastName.isNotEmpty()) {
                binding.username.text = "$firstName $lastName"
            }
        }

        profileViewModel.lastName.observe(requireActivity()) {
            lastName = it
            if(firstName.isNotEmpty() && lastName.isNotEmpty()) {
                binding.username.text = "$firstName $lastName"
            }
        }

        return root
    }

    // Adds an edit button in toolbar
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.profile_menu, menu)
            }

            // Handle the menu selection
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.editMenuBtn) {
                    // TODO: uncomment when EditProfileActivity is ready
//                    startActivity(Intent(requireActivity(), EditProfileActivity::class.java))
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
