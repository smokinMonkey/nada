package com.smokinmonkey.nada.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.smokinmonkey.nada.R
import com.smokinmonkey.nada.util.Message
import com.smokinmonkey.nada.util.loadUrl

class MessageAdapter(private var messages: ArrayList<Message>, val userId: String):
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    companion object {
        val CURRENT_USER = 1
        val OTHER_USER = 2
    }

    class MessageViewHolder(val v: View): RecyclerView.ViewHolder(v) {
        // set on click listener
        val layout = v.findViewById<View>(R.id.container_message_item_other_user)

        fun bind(message: Message) {
            v.findViewById<TextView>(R.id.tv_messages_item).text = message.message
            v.findViewById<ImageView>(R.id.iv_messages_item).loadUrl(message.sentByImageUrl)

//            layout.setOnClickListener {
//                Toast.makeText(it.context, "User profile clicked from messages, implement later", Toast.LENGTH_SHORT).show()
//            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        if(viewType == CURRENT_USER) {
            return MessageViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.messages_item_user, parent, false))
        } else {
            return MessageViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.messages_item_other_user, parent, false))
        }
    }

    override fun getItemCount(): Int = messages.size

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemViewType(position: Int): Int {
        if(messages[position].sentById.equals(userId)) {
            return CURRENT_USER
        } else {
            return OTHER_USER
        }
    }

    // add message
    fun addMessage(message: Message) {
        messages.add(message)
        notifyDataSetChanged()
    }
}