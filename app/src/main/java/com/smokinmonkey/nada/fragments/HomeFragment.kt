package com.smokinmonkey.nada.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lorentzos.flingswipe.SwipeFlingAdapterView

import com.smokinmonkey.nada.R
import com.smokinmonkey.nada.adapter.PotentialMatchListAdapter
import com.smokinmonkey.nada.util.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : CustomFragment() {

    private var matches = ArrayList<User>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // initialize custom adapter for list of potential matches
        potenteialMatchesAdapter = PotentialMatchListAdapter(context, R.layout.match_item, matches)

        swipe_main_container.adapter = potenteialMatchesAdapter
        swipe_main_container.setFlingListener(object : SwipeFlingAdapterView.onFlingListener{
            override fun removeFirstObjectInAdapter() {
                matches.removeAt(0)
                potenteialMatchesAdapter?.notifyDataSetChanged()
            }

            override fun onLeftCardExit(p0: Any?) {
                val user = p0 as User
                usersDb.child(user.uid.toString()).child(DATA_MATCH_UNLIKES)
                    .child(userId!!).setValue(true)
            }

            override fun onRightCardExit(p0: Any?) {
                val selectedUser = p0 as User
                val selectedUserId = selectedUser.uid
                if(!selectedUserId.isNullOrEmpty()) {
                    usersDb.child(userId!!).child(DATA_MATCH_LIKES)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) { }

                            override fun onDataChange(p0: DataSnapshot) {
                                // checks if there is a match
                                if(p0.hasChild(selectedUserId)) {
                                    Toast.makeText(context, "Match!!!", Toast.LENGTH_SHORT).show()
                                    // chat implement
                                    val chatKey = firebasedb.child(DATA_CHATS).push().key
                                    if(chatKey != null) {
                                        // remove selected user id from current user's like list into match list
                                        usersDb.child(userId).child(DATA_MATCH_LIKES)
                                            .child(selectedUserId).removeValue()
                                        // add selected user id into matched list
                                        usersDb.child(userId).child(DATA_MATCHES)
                                            .child(selectedUserId).setValue(chatKey)
                                        // update selected user's matched list as well
                                        usersDb.child(selectedUserId).child(DATA_MATCHES)
                                            .child(userId).setValue(chatKey)

                                        // current user's chat information
                                        chatsDb.child(chatKey).child(userId)
                                            .child(DATA_CHAT_NAME).setValue(currentUser?.username)
                                        chatsDb.child(chatKey).child(userId)
                                            .child(DATA_CHAT_IMAGE_URL).setValue(currentUser?.imageUrl)
                                        // match's chat information
                                        chatsDb.child(chatKey).child(selectedUserId)
                                            .child(DATA_CHAT_NAME).setValue(selectedUser.username)
                                        chatsDb.child(chatKey).child(selectedUserId)
                                            .child(DATA_CHAT_IMAGE_URL).setValue(selectedUser.imageUrl)
                                    }
                                } else {
                                    usersDb.child(selectedUserId).child(DATA_MATCH_LIKES)
                                        .child(userId).setValue(true)
                                }
                            }
                        })
                }
            }

            override fun onAdapterAboutToEmpty(p0: Int) { }
            override fun onScroll(p0: Float) { }
        })

        // query for all preferred gender
        val prefGenderQuery = firebasedb.child(DATA_USERS).orderByChild(DATA_USER_GENDER)
            .equalTo(currentUser?.preferredGender)

        prefGenderQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) { }

            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach { child ->
                    val user = child.getValue(User::class.java)
                    if(user != null) {
                        if(!child.child(DATA_MATCH_LIKES).hasChild(userId!!) &&
                            !child.child(DATA_MATCH_UNLIKES).hasChild(userId!!) &&
                            !child.child(DATA_MATCHES).hasChild(userId!!)) {
                            matches.add(user)
                            potenteialMatchesAdapter?.notifyDataSetChanged()
                        }
                    }
                }
            }

        })

        ib_dislike.setOnClickListener {
            if(!matches.isEmpty()) {
                swipe_main_container.topCardListener.selectLeft()
            }
        }

        ib_like.setOnClickListener {
            if(!matches.isEmpty()) {
                swipe_main_container.topCardListener.selectRight()
            }
        }

        // click picture to go to potential match detail activity
        swipe_main_container.setOnItemClickListener { i, any ->
            val user = potenteialMatchesAdapter!!.getItem(i)
            Toast.makeText(context, "Potential match clicked, implement go to profile detail " + user?.username,
                Toast.LENGTH_SHORT).show()
        }

    }

    override fun updateList() {

    }
}
