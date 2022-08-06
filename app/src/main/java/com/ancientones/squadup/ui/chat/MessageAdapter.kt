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
        return MessageViewHolder( MessageBinding.inflate(inflater, parent, false) );
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
                println("debugx: sent message styling enabled")
                binding.message.setBackgroundResource(R.drawable.message_sent);

                val layout: ConstraintLayout = binding.messageLayout;
                val set = ConstraintSet();
                set.clone(layout);
                set.clear(binding.message.id, ConstraintSet.START);
                set.connect(binding.message.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);
                set.applyTo(layout);    
                binding.messageFrom.visibility = View.GONE;
            }
        }
    }

}
