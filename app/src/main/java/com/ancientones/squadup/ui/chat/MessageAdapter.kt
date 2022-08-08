package com.ancientones.squadup.ui.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ancientones.squadup.R
import com.ancientones.squadup.database.models.Message
import com.ancientones.squadup.databinding.ImageMessageBinding
import com.ancientones.squadup.databinding.MessageBinding
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class MessageAdapter(private val options: FirestoreRecyclerOptions<Message>,
                     private val currentUserName: String?
                     ): FirestoreRecyclerAdapter<Message, ViewHolder>(options) {

    companion object {
        val VIEW_TYPE_TEXT = 1;
        val VIEW_TYPE_IMAGE = 2;
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context);
        return if( viewType == VIEW_TYPE_TEXT ){
            MessageViewHolder( MessageBinding.inflate(inflater, parent, false) );
        } else {
            ImageMessageViewHolder( ImageMessageBinding.inflate(inflater, parent, false) );
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Message) {
        if (options.snapshots[position].message != null) {
            (holder as MessageViewHolder).bind( model );
        } else {
            (holder as ImageMessageViewHolder).bind( model );
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if( options.snapshots[ position ].message != null ) {
            VIEW_TYPE_TEXT;
        } else {
            VIEW_TYPE_IMAGE;
        }
    }

    // Inner class for viewholder of text messages
    inner class MessageViewHolder(private val binding: MessageBinding): ViewHolder(binding.root) {
        fun bind(item: Message){
            binding.message.text = item.message;
            binding.messageFrom.text = item.from;
            if( item.from == currentUserName ) {
                binding.message.setBackgroundResource(R.drawable.message_sent);

                val layout: ConstraintLayout = binding.messageLayout;
                val set = ConstraintSet();
                set.clone(layout);
                set.clear(binding.message.id, ConstraintSet.START);
                set.connect(binding.message.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);
                set.applyTo(layout);
                binding.messageFrom.visibility = View.GONE;
            } else {
                // because its a recycler never know which view will
                // be used to render a received message
                binding.message.setBackgroundResource(R.drawable.message_received);

                val layout: ConstraintLayout = binding.messageLayout;
                val set = ConstraintSet();
                set.clone(layout);
                set.clear(binding.message.id, ConstraintSet.END);
                set.connect(binding.message.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
                set.applyTo(layout);
                binding.messageFrom.visibility = View.VISIBLE;
            }

        }
    }

    // inner class for viewholder of image messages
    inner class ImageMessageViewHolder(private val binding: ImageMessageBinding):
        ViewHolder(binding.root) {
            fun bind(item: Message) {
                // load the image from storage
                val imageRef = Firebase.storage.getReferenceFromUrl(item.imageUrl);
                imageRef.downloadUrl.addOnSuccessListener {
                    val downloadUri = it.toString();
                    Glide.with(binding.imgMessage.context).load(downloadUri).into(binding.imgMessage);
                }

                // set name
                binding.imgMessageFrom.text = item.from;
                if( item.from == currentUserName ) {

                    val layout: ConstraintLayout = binding.messageLayout;
                    val set = ConstraintSet();
                    set.clone(layout);
                    set.clear(binding.imgMessage.id, ConstraintSet.START);
                    set.connect(binding.imgMessage.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END, 0);
                    set.applyTo(layout);
                    binding.imgMessageFrom.visibility = View.GONE;
                } else {
                    // because its a recycler never know which view will
                    // be used to render a received message

                    val layout: ConstraintLayout = binding.messageLayout;
                    val set = ConstraintSet();
                    set.clone(layout);
                    set.clear(binding.imgMessage.id, ConstraintSet.END);
                    set.connect(binding.imgMessage.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, 0);
                    set.applyTo(layout);
                    binding.imgMessageFrom.visibility = View.VISIBLE;
                }
            }
        }

}
