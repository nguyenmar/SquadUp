package com.ancientones.squadup.ui.chat

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ancientones.squadup.database.models.Chat
import com.ancientones.squadup.databinding.FragmentChatListBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class ChatListFragment : Fragment() {

    private var _binding: FragmentChatListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var db: FirebaseFirestore;
    private lateinit var storage: FirebaseStorage;
    private lateinit var userId: String;

    private lateinit var chatAdapter: ChatListAdapter;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatListBinding.inflate(inflater, container, false);
        val root: View = binding.root;

        /** TODO:
         *      Get user object by auth id, then get list of all chats
         *      with list of dropin the user has joined.
         * */

        db = Firebase.firestore;
        userId = Firebase.auth.currentUser!!.uid;


        // todo: get list of dropins for  user for the list values
        val query = db.collection( "dropin" )
            .whereArrayContains("members", userId);

        val chatsList: ArrayList<Chat> = arrayListOf();
        chatAdapter = ChatListAdapter( requireContext(), chatsList );

        // update lists of chats
        query.addSnapshotListener { dropins, error ->
            if( error != null || dropins?.documents?.isEmpty() ?: true){
                binding.chatList.visibility = View.INVISIBLE;
                binding.chatListEmpty.visibility = View.VISIBLE;
                return@addSnapshotListener;
            }

            val newChats = ArrayList<Chat>();
            val dropIds = arrayListOf<String>();

            for( dropin in dropins!! ){
                dropIds.add( dropin.id );
            }

            db.collection( ChatActivity.CHAT_COLLECTION_NAME )
                .whereIn("dropIn_id", dropIds ).get().addOnSuccessListener {
                    for( chat in it.documents ){
                        newChats.add( chat.toObject(Chat::class.java)!! );
                    }
                    chatAdapter.replace( newChats );
                    chatAdapter.notifyDataSetChanged();
            };
        };

        binding.chatList.adapter = chatAdapter;
        binding.chatList.setOnItemClickListener { parent, view, position, id ->
            val chat = chatAdapter.getItem(position);
            val intent = Intent(requireActivity(), ChatActivity::class.java);
            intent.putExtra(ChatActivity.CHAT_ID_KEY, chat!!.dropIn_id);
            requireActivity().startActivity(intent);
        }

        return root;
    }

    override fun onDestroyView() {
        super.onDestroyView();
        _binding = null;
    }

}
