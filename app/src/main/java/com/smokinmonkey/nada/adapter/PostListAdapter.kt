package com.smokinmonkey.nada.adapter

import android.text.Layout
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

class PostListAdapter(val userId: String, val posts: ArrayList<Post>):
    RecyclerView.Adapter<PostListAdapter.PostViewHolder>() {

    fun updatePosts(newPosts: List<Post>) {
        posts.clear()
        posts.addAll(newPosts)
        notifyDataSetChanged()
    }

    class PostViewHolder(v: View): RecyclerView.ViewHolder(v) {
        private val layout = v.findViewById<ViewGroup>(R.id.conlay_item_post)
//        private val username = v.findViewById<TextView>(R.id.tv_item_post_username)
        private val title = v.findViewById<TextView>(R.id.tv_item_post_title)
        private val date = v.findViewById<TextView>(R.id.tv_item_post_date)
        private val entry = v.findViewById<TextView>(R.id.tv_item_post_entry)
//        private val userProfile = v.findViewById<ImageView>(R.id.iv_item_post_user_image)
        private val image = v.findViewById<ImageView>(R.id.iv_item_post_image)
        private val likeCount = v.findViewById<TextView>(R.id.tv_item_post_like_count)
//        private val commentCount = v.findViewById<TextView>(R.id.tv_item_post_comment_count)
        // buttons
        private val heartButton = v.findViewById<ImageView>(R.id.iv_item_post_like)
//        private val commentButton = v.findViewById<ImageView>(R.id.iv_item_post_comment)

        fun bind(userId: String, post: Post) {
            // bind post with view
//            userProfile.loadUrl(post.userImage)
//            username.text = post.username
            title.text = post.title
            date.text = getDate(post.timestamp)
            entry.text = post.text
            if(post.imageUrl.isNullOrEmpty()) {
                image.visibility = View.GONE
            } else {
                image.loadUrl(post.imageUrl)
            }
            likeCount.text = post.likes?.size.toString()
//            commentCount.text = post.comments?.size.toString()

            // check if it is user's own post, if it is like button disabled

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PostViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.my_activity_item_post, parent, false)
    )

    override fun getItemCount(): Int = posts.size

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(userId, posts[position])
    }
}