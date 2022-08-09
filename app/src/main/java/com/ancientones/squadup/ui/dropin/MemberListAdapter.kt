package com.ancientones.squadup.ui.dropin

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.ancientones.squadup.R
import com.ancientones.squadup.ui.profile.RateProfileActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.HashMap

class MemberListAdapter(private val context: Context, private var memberList: List<String>) : BaseAdapter() {
    override fun getItem(pos: Int): Any {
        return memberList[pos]
    }

    override fun getItemId(pos: Int): Long {
        return pos.toLong()
    }

    override fun getCount(): Int {
        return memberList.size
    }

    override fun getView(pos: Int, view: View?, parent: ViewGroup?): View? {
        val view = View.inflate(context, R.layout.adapter_member, null)

        val usernameView = view.findViewById<TextView>(R.id.username)
        val userImage = view.findViewById<ImageView>(R.id.imageView) // TODO: use profileImgViewModel

        var firstName: String

        Firebase.database.getReference("Users")
            .child(memberList[pos])
            .get()
            .addOnSuccessListener {
                val userMap: HashMap<String, String> = it.value as HashMap<String, String>
                var userHasRated = false

                // Check if current user has already rated this user
                if(userMap["hasRated"] != null) {
                    val hasRated = userMap["hasRated"] as List<String>
                    hasRated.forEach{ uid ->
                        if(uid == Firebase.auth.currentUser!!.uid){
                            userHasRated = true
                        }
                    }
                }

                // Setup basic list details
                firstName = userMap["firstName"].toString()
                usernameView.text = "$firstName ${userMap["lastName"]}"

                // Attach click listener to start a RateProfile intent, if user has not be rated
                if (!userHasRated) {
                    view.setOnClickListener{
                        val intent = Intent(context, RateProfileActivity::class.java)
                        intent.putExtra("userID", memberList[pos])
                        intent.putExtra("firstName", firstName)
                        context.startActivity(intent)
                    }
                }
                else {
                    view.setOnClickListener {
                        Toast.makeText(parent!!.context, "You have already rated ${firstName}.", Toast.LENGTH_LONG)
                            .show()
                    }
                }

            }
            .addOnFailureListener{
                Log.e("firebase", "error getting data", it)
            }
        return view
    }
}