package com.smokinmonkey.nada.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.smokinmonkey.nada.R
import com.smokinmonkey.nada.adapter.MessageAdapter
import com.smokinmonkey.nada.util.*
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.messages_item_other_user.*
import java.util.*
import kotlin.collections.ArrayList

class ChatActivity : AppCompatActivity() {

    private var chatId: String? = null
    private var userId: String? = null
    private var imageUrl: String? = null
    private var username: String? = null
    private var otherImageUrl: String? = null
    private var otherUserId: String? = null

    private lateinit var chatDb: DatabaseReference
    private lateinit var messageAdapter: MessageAdapter

    // live data observer
    private val chatMessageListener = object : ChildEventListener {
        override fun onCancelled(p0: DatabaseError) { }

        override fun onChildMoved(p0: DataSnapshot, p1: String?) { }

        override fun onChildChanged(p0: DataSnapshot, p1: String?) { }

        override fun onChildAdded(p0: DataSnapshot, p1: String?) {
            val message = p0.getValue(Message::class.java)
            if(message != null) {
                messageAdapter.addMessage(message)
                rv_chat_messages.post {
                    rv_chat_messages.smoothScrollToPosition(messageAdapter.itemCount - 1)
                }
            }
        }

        override fun onChildRemoved(p0: DataSnapshot) { }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        chatId = intent.extras?.getString(PARAM_CHAT_ID)
        userId = intent.extras?.getString(PARAM_USER_ID)
        imageUrl = intent.extras?.getString(PARAM_IMAGE_URL)
        username = intent.extras?.getString(PARAM_USERNAME)
        otherImageUrl = intent.extras?.getString(PARAM_OTHER_IMAGE_URL)
        otherUserId = intent.extras?.getString(PARAM_OTHER_USER_ID)

        if(chatId.isNullOrEmpty() || userId.isNullOrEmpty() || imageUrl.isNullOrEmpty() ||
            username.isNullOrEmpty() || otherUserId.isNullOrEmpty() || otherImageUrl.isNullOrEmpty()) {
            Toast.makeText(this, "Chat room error...", Toast.LENGTH_SHORT).show()
            finish()
        }

        chatDb = FirebaseDatabase.getInstance().reference.child(DATA_CHATS)

        messageAdapter = MessageAdapter(ArrayList(), userId!!)
        rv_chat_messages.apply {
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(context)
            adapter = messageAdapter
        }

        chatDb.child(chatId!!).child(DATA_MESSAGES).addChildEventListener(chatMessageListener)
        chatDb.child(chatId!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) { }

            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach { value ->
                    val key = value.key
                    val user = value.getValue(User::class.java)
                    if(!key.equals(userId)) {
                        tv_chat_navbar_title.text = user?.username
                    }
                }
            }
        })

    }

    fun onSend(v: View) {
        if(!et_chat.text.toString().isNullOrEmpty()) {

            val message = Message(userId, imageUrl, et_chat.text.toString(),
                Calendar.getInstance().time.toString())

            val key = chatDb.child(chatId!!).child(DATA_MESSAGES).push().key
            if(!key.isNullOrEmpty()) {
                chatDb.child(chatId!!).child(DATA_MESSAGES).child(key).setValue(message)
            }
            // clear text in edit text
            et_chat.setText("", TextView.BufferType.EDITABLE)
        }
    }

    companion object {
        // constants for chat data
        private val PARAM_CHAT_ID = "chatId"
        private val PARAM_USER_ID = "userId"
        private val PARAM_IMAGE_URL = "imageUrl"
        private val PARAM_USERNAME = "username"
        private val PARAM_OTHER_IMAGE_URL = "otherImageUrl"
        private val PARAM_OTHER_USER_ID = "otherUserId"

        fun newIntent(context: Context?, chatId: String?, userId: String?, imageUrl: String?,
                      username: String?, otherImageUrl: String?, otherUserId: String?): Intent {
            val i = Intent(context, ChatActivity::class.java)
            i.putExtra(PARAM_CHAT_ID, chatId)
            i.putExtra(PARAM_USER_ID, userId)
            i.putExtra(PARAM_IMAGE_URL, imageUrl)
            i.putExtra(PARAM_USERNAME, username)
            i.putExtra(PARAM_OTHER_IMAGE_URL, otherImageUrl)
            i.putExtra(PARAM_OTHER_USER_ID, otherUserId)

            return i
        }
    }
}