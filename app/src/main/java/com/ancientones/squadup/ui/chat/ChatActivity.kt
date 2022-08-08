package com.ancientones.squadup.ui.chat

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.MenuItem
import androidx.activity.addCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ancientones.squadup.database.models.Message
import com.ancientones.squadup.databinding.ActivityChatBinding
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ancientones.squadup.BuildConfig
import com.ancientones.squadup.database.models.Chat
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.lang.Exception
import java.util.*

class ChatActivity : AppCompatActivity() {
    companion object {
        val CHAT_ID_KEY = "chat_id_key";
        val CHAT_COLLECTION_NAME = "chats"
        val MESSAGE_COLLECTION_NAME = "messages"
    }

    private lateinit var binding: ActivityChatBinding;
    private lateinit var imageResult: ActivityResultLauncher<Intent>;
    private val MSG_KEY = "msg_key";
    
    // Firebase
    private lateinit var db: FirebaseFirestore;
    private lateinit var storage: FirebaseStorage;
    private lateinit var auth: FirebaseAuth;
    private lateinit var manager: LinearLayoutManager;
    private lateinit var messageAdapter: MessageAdapter;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatBinding.inflate(layoutInflater);
        setContentView(binding.root);

        // back button
        var actionBar = supportActionBar;
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // last state, for temp message
        if(savedInstanceState != null){
            val msg = savedInstanceState.getString(MSG_KEY);
            binding.messageEditBox.setText(msg);
        }

        // auth
//        auth = Firebase.auth;
//        if(auth.currentUser == null ){
//            // todo: launch something, for now comment out
//        }

        // db setup
        // for local only, todo: comment out later
//        if( BuildConfig.DEBUG ){
//            try{
//                Firebase.firestore.useEmulator("10.0.2.2", 8080);
//                val settings = FirebaseFirestoreSettings.Builder()
//                    .setPersistenceEnabled(false).build();
//                Firebase.firestore.firestoreSettings = settings;
//
//                // todo: setup storage firebase emulator
//                //Firebase.storage.useEmulator("10.0.2.2", 9099);
//            } catch (e: Exception){}
//        }
        db = Firebase.firestore;
        storage = Firebase.storage;

        // chat setup
        val chat_id = intent.getStringExtra( CHAT_ID_KEY )!!;

        // chat title
        db.collection(CHAT_COLLECTION_NAME).document( chat_id ).get().addOnSuccessListener {
                title = it.toObject(Chat::class.java)!!.title;
            };
        val query = db.collection( CHAT_COLLECTION_NAME ).document( chat_id )
            .collection( MESSAGE_COLLECTION_NAME ).orderBy("timestamp").limit(50);

        val options = FirestoreRecyclerOptions.Builder<Message>()
            .setQuery(query, Message::class.java)
            .build();

        messageAdapter = MessageAdapter(options, "titanvj"); // todo: figure out how to get name here?

        manager = LinearLayoutManager(this);
        manager.stackFromEnd = true;

        binding.chatRecyclerView.layoutManager = manager;
        binding.chatRecyclerView.adapter = messageAdapter;

        messageAdapter.registerAdapterDataObserver(
            NewMessageListener(binding.chatRecyclerView, messageAdapter, manager)
        );

        messageAdapter.startListening();

        binding.sendButton.setOnClickListener {
            if( binding.messageEditBox.text.isNotEmpty() ) {
                val message = Message(
                    "",
                    "titanvj",
                    "xxxxxxxxx",
                    binding.messageEditBox.text.toString().trim()
                );
                db.collection(CHAT_COLLECTION_NAME).document(chat_id)
                    .collection(MESSAGE_COLLECTION_NAME).add(message).addOnSuccessListener {
//                        println("DEBUGx: it rwk")
                    }.addOnFailureListener {
//                        println("DEBUGx fail")
                    };
                binding.messageEditBox.text.clear();
            }
        };

        // image stuff
        // set result handler
        imageResult = registerForActivityResult( ActivityResultContracts.StartActivityForResult() ) {
            result: ActivityResult ->
            val image: Uri = result.data!!.data!!;

            // get msg, with a generated id for the image
            val message = db.collection(CHAT_COLLECTION_NAME).document(chat_id)
                .collection(MESSAGE_COLLECTION_NAME).document();

            // upload the photo to storage with id of the message and get its url
            val imageRef = storage.reference.child("images/${message.id}");
            imageRef.putFile(image).addOnSuccessListener {
                it.metadata!!.reference!!.downloadUrl.addOnSuccessListener { downloadUri ->
                    val msg = Message(
                        "",
                        "titanvj",
                        "xxxx",
                        null, // leave msg null
                        downloadUri.toString()
                    );
                    message.set(msg);
                }
            }
        }

        binding.sendImageBtn.setOnClickListener {
            // lauch activity for result to get an image
            val intent = Intent(Intent.ACTION_PICK);
            intent.type = "image/*";
            intent.action = Intent.ACTION_GET_CONTENT;
            imageResult.launch(intent);
        }

        onBackPressedDispatcher.addCallback(this) {
            messageAdapter.stopListening();
            finish();
        };
    }

    // todo: add saved instance state for text in text box and scroll position? match messages app
    // todo: add the auto scroll or button to scroll down when scrolled up enough or new message(notif fab)?

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putString(MSG_KEY, binding.messageEditBox.text.toString().trim());
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if( item.itemId == android.R.id.home ){
            messageAdapter.stopListening();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    override fun onDestroy() {
        super.onDestroy();
        messageAdapter.stopListening();
    }

    override fun onPause() {
        messageAdapter.stopListening();
        super.onPause();
    }

    override fun onResume() {
        super.onResume();
        messageAdapter.startListening();
    }
}
