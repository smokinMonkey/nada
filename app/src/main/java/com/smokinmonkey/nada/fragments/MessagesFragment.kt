package com.smokinmonkey.nada.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

import com.smokinmonkey.nada.R
import com.smokinmonkey.nada.adapter.MatchesListAdapter
import com.smokinmonkey.nada.util.Chat
import com.smokinmonkey.nada.util.CustomCallback
import com.smokinmonkey.nada.util.DATA_MATCHES
import com.smokinmonkey.nada.util.User
import kotlinx.android.synthetic.main.fragment_messages.*

/**
 * A simple [Fragment] subclass.
 */
class MessagesFragment : CustomFragment() {

    private val matchesListAdapter = MatchesListAdapter(ArrayList())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_messages, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // bind recyclerview with layout manager
        rv_messages_frag.apply{
            layoutManager = LinearLayoutManager(context)
            adapter = matchesListAdapter
        }
    }

    override fun updateList() {
        // live data observer for matches branch
        usersDb.child(userId!!).child(DATA_MATCHES).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) { }

            override fun onDataChange(p0: DataSnapshot) {
                // live data observer for new matches
                if(p0.hasChildren()) {
                    p0.children.forEach { child ->
                        val matchId = child.key
                        val chatId = child.value.toString()
                        if(!matchId.isNullOrEmpty()) {
                            usersDb.child(matchId).addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onCancelled(p0: DatabaseError) { }

                                override fun onDataChange(p0: DataSnapshot) {
                                    val user = p0.getValue(User::class.java)
                                    if(user != null) {
                                        val chat = Chat(chatId, userId, currentUser?.imageUrl,
                                            user.username, user.imageUrl, user.uid)
                                        matchesListAdapter.addElement(chat)
                                    }
                                }
                            })
                        }
                    }
                }
            }

        })

    }
}
