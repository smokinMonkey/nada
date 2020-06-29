package com.smokinmonkey.nada.fragments

import android.content.Context
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.smokinmonkey.nada.adapter.PostListAdapter
import com.smokinmonkey.nada.adapter.PotentialMatchListAdapter
import com.smokinmonkey.nada.util.CustomCallback
import com.smokinmonkey.nada.util.DATA_CHATS
import com.smokinmonkey.nada.util.DATA_USERS
import com.smokinmonkey.nada.util.User
import java.lang.RuntimeException

/**
 * A simple [Fragment] subclass.
 * Implements all the custom methods and data for all fragments
 */
abstract class CustomFragment : Fragment() {

    protected var currentUser: User? = null
    protected var currentUserPrefGender: String? = null
    protected var callback: CustomCallback? = null
    protected val firebasedb = FirebaseDatabase.getInstance().reference
    protected val usersDb = FirebaseDatabase.getInstance().reference.child(DATA_USERS)
    protected val chatsDb = FirebaseDatabase.getInstance().reference.child(DATA_CHATS)
    protected val userId = FirebaseAuth.getInstance().currentUser?.uid

    protected var postsAdapter: PostListAdapter? = null
    protected var potenteialMatchesAdapter: PotentialMatchListAdapter? = null
//    protected var matchPostAdapter: PotentialMatchPostListAdapter? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is CustomCallback) {
            callback = context
        } else {
            throw RuntimeException(context.toString() + " must implement CustomCallback")
        }
    }

    abstract fun updateList()

    fun setUser(user: User) {
        this.currentUser = user
        this.currentUserPrefGender = user.preferredGender
    }

    override fun onResume() {
        super.onResume()
        updateList()
    }
}
