package com.ancientones.squadup.ui.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ancientones.squadup.R
import com.ancientones.squadup.database.models.Message
import com.ancientones.squadup.databinding.MessageBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class MessageAdapter(private val options: FirestoreRecyclerOptions<Message>,
                     private val currentUserName: String?
                     ): FirestoreRecyclerAdapter<Message, ViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context);
        return MessageViewHolder( MessageBinding.inflate(inflater) );
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Message) {
        (holder as MessageViewHolder).bind( model );
    }

    // Inner class for viewholder
    inner class MessageViewHolder(private val binding: MessageBinding): ViewHolder(binding.root) {
        fun bind(item: Message){
            binding.message.text = item.message;
            binding.messageFrom.text = item.from;
            println("debugx ${item.from} == ${currentUserName}")

            if( item.from == currentUserName ) {
                binding.message.setBackgroundResource(R.drawable.message_blue);
                // todo: adjust the constraints to be right aligned

                val layout1: ConstraintLayout = binding.messageLayout;
                val set1 = ConstraintSet();
                set1.clone(layout1);
                set1.connect(binding.message.id, ConstraintSet.END, layout1.id, ConstraintSet.END);
                set1.applyTo(layout1)
                binding.messageFrom.visibility = View.INVISIBLE;
            }
        }
    }

}
