package com.smokinmonkey.nada.fragments

import android.os.Bundle
import android.renderscript.Sampler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

import com.smokinmonkey.nada.R
import com.smokinmonkey.nada.adapter.PostListAdapter
import com.smokinmonkey.nada.util.*
import kotlinx.android.synthetic.main.fragment_my_posts.*
import kotlinx.android.synthetic.main.my_activity_item_post.*

/**
 * A simple [Fragment] subclass.
 */
class MyPostsFragment : CustomFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_posts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postsAdapter = PostListAdapter(userId!!, arrayListOf())
        rv_my_post_list.apply{
            layoutManager = LinearLayoutManager(context)
            adapter = postsAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

    }

    override fun updateList() {
        rv_my_post_list?.visibility = View.GONE
        val posts = arrayListOf<Post>()
        // get the list of posts from user id
        val postQuery = firebasedb.child(DATA_POSTS).orderByChild(DATA_POST_USER_ID).equalTo(userId!!)
        postQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) { }

            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach { child ->
                    val post = child.getValue(Post::class.java)
                    if(post != null) {
                        posts.add(post)
                        postsAdapter?.updatePosts(posts)
                        rv_my_post_list.visibility = View.VISIBLE
                    }
                }
            }
        })

    }

}
