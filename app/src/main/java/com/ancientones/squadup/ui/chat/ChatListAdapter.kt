package com.ancientones.squadup.ui.chat

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.ancientones.squadup.R
import com.ancientones.squadup.database.models.Chat

class ChatListAdapter(private val ctx: Context, private var chatsList: List<Chat> )
    : ArrayAdapter<Chat> (ctx, 0, chatsList) {

    fun replace(newList: List<Chat>?) {
        chatsList = newList!!;
    }

    override fun getCount(): Int {
        return chatsList.size;
    }

    override fun getItem(position: Int): Chat? {
        return chatsList.get( position );
    }

    override fun getItemId(position: Int): Long {
        return position.toLong();
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        println("debugx: getView")
        val view: View = View.inflate( ctx, R.layout.chat_list_item, null );

        val name: TextView = view.findViewById( R.id.chat_name );

        val chat: Chat? = getItem(position);
        if( chat != null ) {
            name.text = chat.title;
        }
        return view;
    }
}
