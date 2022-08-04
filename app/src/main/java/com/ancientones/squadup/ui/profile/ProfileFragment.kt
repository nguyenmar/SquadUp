package com.ancientones.squadup.ui.profile

import android.annotation.SuppressLint
import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import com.ancientones.squadup.AuthActivity
import com.ancientones.squadup.R
import com.ancientones.squadup.databinding.FragmentProfileBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

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

    @SuppressLint("SetTextI18n")
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
                    intent.putExtra("firstName", "${profileViewModel.firstName.value}")
                    intent.putExtra("lastName", "${profileViewModel.lastName.value}")
                    intent.putExtra("userAge", "${profileViewModel.userAge.value}")
                    intent.putExtra("userSex", "${profileViewModel.userSex.value}")
                    intent.putExtra("userHeight", "${profileViewModel.userHeight.value}")
                    intent.putExtra("userPhone", "${profileViewModel.userPhone.value}")
                    intent.putExtra("userDescription", "${profileViewModel.userDescription.value}")
                    intent.putExtra("userID", "${Firebase.auth.currentUser!!.uid}")

                    startActivity(intent)
                }
                else if (menuItem.itemId == R.id.logoutBtn) {
                    val intent = Intent(requireActivity(), AuthActivity::class.java)
                    Firebase.auth.signOut()
                    startActivity(intent)
                    requireActivity().finish()
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        val usernameView = view.findViewById<TextView>(R.id.username)
        val userAgeView = view.findViewById<TextView>(R.id.userAge)
        val userHeightView = view.findViewById<TextView>(R.id.userHeight)
        val userSexView = view.findViewById<TextView>(R.id.userSex)
        val userPhoneView = view.findViewById<TextView>(R.id.userPhone)
        val userDescriptionView = view.findViewById<TextView>(R.id.userDescription)
        val userPictureView = view.findViewById<ImageView>(R.id.display_picture)
        userPictureView.setImageResource(R.drawable.temporary_display_photo)

        profileViewModel.firstName.observe(requireActivity()) {
            usernameView.text = "${profileViewModel.firstName.value} ${profileViewModel.lastName.value}"
        }

        profileViewModel.lastName.observe(requireActivity()) {
            usernameView.text = "${profileViewModel.firstName.value} ${profileViewModel.lastName.value}"
        }

        profileViewModel.userAge.observe(requireActivity()) {
            userAgeView.text = "${profileViewModel.userAge.value}"
        }

        profileViewModel.userSex.observe(requireActivity()) {
            userSexView.text = "${profileViewModel.userSex.value}"
        }

        profileViewModel.userHeight.observe(requireActivity()) {
            userHeightView.text = "${profileViewModel.userHeight.value} cm"
        }

        profileViewModel.userPhone.observe(requireActivity()) {
            userPhoneView.text = "${profileViewModel.userPhone.value}"
        }

        profileViewModel.userDescription.observe(requireActivity()) {
            userDescriptionView.text = "${profileViewModel.userDescription.value}"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
