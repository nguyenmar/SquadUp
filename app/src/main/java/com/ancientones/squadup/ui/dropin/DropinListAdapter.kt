package com.ancientones.squadup.ui.dropin

import android.content.ContentValues
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ancientones.squadup.R
import com.ancientones.squadup.databinding.FragmentHistoryBinding
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.BaseAdapter
import androidx.lifecycle.ViewModelProvider
import com.ancientones.squadup.dropin.DropInActivity
import com.ancientones.squadup.dropin.DropInMembersActivity
import com.ancientones.squadup.dropin.DropInViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase


class DropinListAdapter(private val context: Context, private var documentsList: List<String>): BaseAdapter() {

    override fun getCount(): Int {
        return documentsList.size
    }

    override fun getItem(position: Int): Any {
        return documentsList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup?): View? {
        val view = View.inflate(context, R.layout.adapter_dropin, null)
        val dropInTitle = view.findViewById<TextView>(R.id.titleText)
        var members: ArrayList<String> = ArrayList()

        println("debug: doclist: $documentsList")

        val db = FirebaseFirestore.getInstance()

        val docRef = db.collection("dropin").document(documentsList[position])
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        println("DocumentSnapshot data: ${document.data}")
                        dropInTitle.text = "${document.get("date").toString()} ${document.get("sport").toString()} Drop-in"
                        members = document.get("members") as ArrayList<String>
                    } else {
                        println("No document")
                    }
                }
                .addOnFailureListener {
                    println("failed to get document")
                }

        view.setOnClickListener{

            val intent = Intent(context, DropInMembersActivity::class.java)
            println(members)
            members.remove(Firebase.auth.currentUser!!.uid)
            println(Firebase.auth.currentUser!!.uid)
            println(members)


            intent.putStringArrayListExtra("memberList", members)
            context.startActivity(intent)
        }
        return view
    }

}