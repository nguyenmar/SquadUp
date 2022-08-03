package com.ancientones.squadup.ui.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ancientones.squadup.database.models.Message
import com.ancientones.squadup.databinding.ActivityChatBinding
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ancientones.squadup.BuildConfig

class ChatActivity : AppCompatActivity() {
    companion object {
        val CHAT_ID_KEY = "chat_id_key";
        val CHAT_COLLECTION_NAME = "chats"
        val MESSAGE_COLLECTION_NAME = "messages"
    }

    private lateinit var binding: ActivityChatBinding;
    private lateinit var viewModel: ChatViewModel;

    // Firebase
    private lateinit var db: FirebaseFirestore;
    private lateinit var auth: FirebaseAuth;
    private lateinit var manager: LinearLayoutManager;
    private lateinit var messageAdapter: MessageAdapter;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ChatViewModel::class.java);

        binding = ActivityChatBinding.inflate(layoutInflater);
        setContentView(binding.root);

        // emulator stuff here

        // auth
//        auth = Firebase.auth;
//        if(auth.currentUser == null ){
//            // todo: launch something, for now comment out
//        }

        // db setup

        // for local only, todo: comment out later
        println("debugx ${BuildConfig.DEBUG}")
        Firebase.firestore.useEmulator("10.0.2.2", 8080);
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(false).build();
        Firebase.firestore.firestoreSettings = settings;

        db = Firebase.firestore;

        // chat setup
        val id:String = "1";
        // chats -> *chat room they belong to* ->
        val query = db.collection(CHAT_COLLECTION_NAME).document(id)
            .collection(MESSAGE_COLLECTION_NAME).orderBy("timestamp")
            .limit(50);

//        db.collection(CHAT_COLLECTION_NAME).document(id).get().addOnSuccessListener {
//            val chat: Chat? = it.toObject<Chat>();
//            println("debugx: chat: ${it.get("messages")}")
//            title = "testing";
//        }

        val options = FirestoreRecyclerOptions.Builder<Message>()
            .setQuery(query, Message::class.java)
            .build();

        messageAdapter = MessageAdapter(options, "titanvj"); // todo: figure out how to get name here?

        manager = LinearLayoutManager(this);
        manager.stackFromEnd = true;

        binding.chatRecyclerView.layoutManager = manager;
        binding.chatRecyclerView.adapter = messageAdapter;
        messageAdapter.startListening()
        binding.sendButton.setOnClickListener {
            if(binding.messageEditBox.text.isNotEmpty()) {
                val message = Message(
                    "titanvj",
                    "xxxxxxxxx",
                    binding.messageEditBox.text.toString()
                );
                db.collection(CHAT_COLLECTION_NAME).document(id)
                    .collection(MESSAGE_COLLECTION_NAME).add(message).addOnSuccessListener {
                        println("DEBUGx: it rwk")
                    }.addOnFailureListener {
                        println("DEBUGx fail")
                    };
                binding.messageEditBox.text.clear();
            }
        }

    }
}
