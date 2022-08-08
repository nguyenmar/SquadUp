package com.ancientones.squadup.ui.profile

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.Image
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import com.ancientones.squadup.AuthActivity
import com.ancientones.squadup.R
import com.ancientones.squadup.databinding.FragmentProfileBinding
import com.ancientones.squadup.dropin.DropInMembersActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var storageReference: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        firebaseStorage = Firebase.storage
        storageReference = firebaseStorage.reference
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
                    intent.putExtra("userID", Firebase.auth.currentUser!!.uid)

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
        val userRatingView = view.findViewById<RatingBar>(R.id.userRating)

        val userPictureView = view.findViewById<ImageView>(R.id.display_picture)
        val imageRef = storageReference.child("images/${Firebase.auth.currentUser!!.uid}")
        imageRef.getBytes(1024 * 1024).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
            userPictureView.setImageBitmap(bitmap)
        }.addOnFailureListener {
            println("DEBUG: User does not currently have a set display photo.")
            userPictureView.setImageResource(R.drawable.temporary_display_photo)
        }

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

        profileViewModel.userRating.observe(requireActivity()) {
            userRatingView.rating = profileViewModel.userRating.value!!.toFloat()
        }

        // TODO: remove after implementing DropInMembersActivity
        val testBtn = view.findViewById<Button>(R.id.testRateMembers)
        testBtn.setOnClickListener{
            val intent = Intent(requireActivity(), DropInMembersActivity::class.java)
            val mockList = arrayListOf<String>("0GyqfvkvTpNgAc05dhbPKBDTMZw2", "x2Ukx8ngkOQZVyhkBtvUK1QgAqp2",
            "uv5yBD7hv5WzQ0kTtfm83IfXCFt1", "9bFEiZwOLmNGYD3qrqsx4ZgdjXP2", "jIBpeZvvdeX9MrLonJiJbKUlC2S2",
            "Ulz6KTXTOOMfHdaMhNM3K9bd6Zq2", "BjMo3n3oPBhPpYDaQMPYWwjiBY02", "9CcVNt8rSnSjiMM7mE344wsvLCB2")

            // Prepare memberList by removing currentUser
            mockList.filterTo(arrayListOf()) { it != Firebase.auth.currentUser!!.uid }

            intent.putStringArrayListExtra("memberList", mockList)
            startActivity(intent)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
