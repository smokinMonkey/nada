package com.smokinmonkey.nada.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.smokinmonkey.nada.R
import com.smokinmonkey.nada.util.Post
import com.smokinmonkey.nada.util.getDate
import com.smokinmonkey.nada.util.loadUrl

class PotentialMatchPostListAdapter(val userId: String, val posts:ArrayList<Post>):
    RecyclerView.Adapter<PotentialMatchPostListAdapter.MatchPostViewHolder>() {

    fun updatePosts(newPosts: List<Post>) {
        posts.clear()
        posts.addAll(newPosts)
        notifyDataSetChanged()
    }

    class MatchPostViewHolder(v: View): RecyclerView.ViewHolder(v) {
        private val container = v.findViewById<ViewGroup>(R.id.conlay_potential_match_post)
        private val postImage = v.findViewById<ImageView>(R.id.iv_potential_match_post_image)
        private val postTitle = v.findViewById<TextView>(R.id.tv_potential_match_item_post_title)
        private val postEntry = v.findViewById<TextView>(R.id.tv_potential_match_item_post_entry)
        private val postDate = v.findViewById<TextView>(R.id.tv_potential_match_item_post_date)

        fun bind(userId: String, post: Post) {
            postImage.visibility = View.GONE

            post.imageUrl?.let {
                postImage.visibility = View.VISIBLE
                postImage.loadUrl(post.imageUrl)
            }
            postTitle.text = post.title
            postEntry.text = post.text
            postDate.text = getDate(post.timestamp)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MatchPostViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.potential_match_item_post, parent,
            false)
    )

    override fun getItemCount() = posts.size

    override fun onBindViewHolder(holder: MatchPostViewHolder, position: Int) {
        holder.bind(userId, posts[position])
    }
}