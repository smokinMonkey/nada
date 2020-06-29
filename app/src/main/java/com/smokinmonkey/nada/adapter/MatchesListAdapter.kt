package com.smokinmonkey.nada.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.smokinmonkey.nada.R
import com.smokinmonkey.nada.activities.ChatActivity
import com.smokinmonkey.nada.util.Chat
import com.smokinmonkey.nada.util.loadUrl

class MatchesListAdapter(private var matches: ArrayList<Chat>):
    RecyclerView.Adapter<MatchesListAdapter.ChatsViewHolder>() {

    class ChatsViewHolder(private val v: View): RecyclerView.ViewHolder(v) {
        private var layout = v.findViewById<ViewGroup>(R.id.conlay_matches_item)
        private var image = v.findViewById<ImageView>(R.id.iv_matches_item_match_image)
        private var name = v.findViewById<TextView>(R.id.tv_matches_item_match_name)

        fun bind(chat: Chat) {
            // binds name with match's name
            name.text = chat.username
            // binds image with match's image
            if(image != null) {
                image.loadUrl(chat.otherImageUrl)
            }
            // set onClick listener for starting chat
            layout.setOnClickListener {
                // pass all the necessary information for chat activity
                val i = ChatActivity.newIntent(it.context, chat.chatId, chat.userId, chat.imageUrl,
                    chat.username, chat.otherImageUrl, chat.otherUserId)
                it.context.startActivity(i)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ChatsViewHolder(LayoutInflater
        .from(parent.context).inflate(R.layout.matches_item, parent, false))

    override fun getItemCount(): Int = matches.size

    override fun onBindViewHolder(holder: ChatsViewHolder, position: Int) {
        holder.bind(matches[position])
    }

    // add chat to database
    fun addElement(chat: Chat) {
        matches.add(chat)
        notifyDataSetChanged()
    }
}