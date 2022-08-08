package com.ancientones.squadup.ui.chat

import android.os.Message
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class NewMessageListener(
    private val recycler: RecyclerView,
    private val adapter: MessageAdapter,
    private val manager: LinearLayoutManager
): RecyclerView.AdapterDataObserver() {
    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
        // todo: new message fab popup, add a scroll position listener in chatactivity
        println("debugx: listener $positionStart $itemCount")
        if(positionStart > 0) {
            val msg = adapter.getItem(positionStart - 1);
            println("Debugx: ${msg.message}")
        }
        super.onItemRangeInserted(positionStart, itemCount)
        val count = adapter.itemCount
        val lastVisiblePosition = manager.findLastCompletelyVisibleItemPosition()
        // If the recycler view is initially being loaded or the
        // user is at the bottom of the list, scroll to the bottom
        // of the list to show the newly added message.
        val loading = lastVisiblePosition == -1
        val atBottom = positionStart >= count - 1 && lastVisiblePosition == positionStart - 1
        println("debugx: scroll conditions: $loading $atBottom")
        if (loading || !atBottom) {
            recycler.scrollToPosition(positionStart)
        }
    }

}
