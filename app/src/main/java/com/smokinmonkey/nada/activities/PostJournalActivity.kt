package com.smokinmonkey.nada.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.smokinmonkey.nada.R
import com.smokinmonkey.nada.util.*
import kotlinx.android.synthetic.main.activity_post_journal.*

class PostJournalActivity : AppCompatActivity() {

    private val firebasePostsdb = FirebaseDatabase.getInstance().reference.child(DATA_POSTS)
    private val firebaseStorage = FirebaseStorage.getInstance().reference

    // user information passed form intent
    private var userId: String? = null
    private var username: String? = null
    private var userImage: String? = null
    // journal post image url
    private var imageUrl: String? = null

    companion object {
        val PARAM_USER_ID = "UserId"
        val PARAM_USER_NAME = "Username"
        val PARAM_USER_IMAGE = "UserImage"

        fun newIntent(context: Context, userId: String?, username: String?, userImage: String?): Intent {
            var i = Intent(context, PostJournalActivity::class.java)
            i.putExtra(PARAM_USER_ID, userId)
            i.putExtra(PARAM_USER_NAME, username)
            i.putExtra(PARAM_USER_IMAGE, userImage)
            return i
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_journal)

        if(intent.hasExtra(PARAM_USER_ID) && intent.hasExtra(PARAM_USER_NAME)) {
            userId = intent.getStringExtra(PARAM_USER_ID)
            username = intent.getStringExtra(PARAM_USER_NAME)
            if(intent.hasExtra(PARAM_USER_IMAGE)) {
                userImage = intent.getStringExtra(PARAM_USER_IMAGE)
            }
        } else {
            Toast.makeText(this, "ERROR creating journal post", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
    // start intent to pick an image for journal post with callback
    fun addPostImage(v: View) {
        var i = Intent(Intent.ACTION_PICK)
        i.type = "image/*"
        startActivityForResult(i, REQUEST_CODE_PHOTO)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_PHOTO) {
            storeImage(data?.data)
        }
    }

    fun storeImage(url: Uri?) {
        url?.let {
            Toast.makeText(this, "Uploading journal post image...", Toast.LENGTH_SHORT).show()
            linlay_post_pb.visibility = View.VISIBLE

            val filepath = firebaseStorage.child(DATA_POST_IMAGES).child(userId!!)
            filepath.putFile(url)
                .addOnSuccessListener {
                    filepath.downloadUrl.addOnSuccessListener { uri ->
                        imageUrl = uri.toString()
                        iv_post_journal_image.loadUrl(imageUrl)
                        linlay_post_pb.visibility = View.GONE
                    }.addOnFailureListener {
                        onUploadFailure()
                    }
                }
                .addOnFailureListener {
                    onUploadFailure()
                }
        }
    }

    fun onUploadFailure() {
        Toast.makeText(this, "Image upload failed, please try again later.", Toast.LENGTH_SHORT).show()
        linlay_post_pb.visibility = View.GONE
    }

    fun postJournalEntry(v: View) {
        linlay_post_pb.visibility = View.VISIBLE
        if(!et_post_title.text.isNullOrEmpty()) {
            val postId = firebasePostsdb.push().key
            val title = et_post_title.text.toString()
            val entry = et_post_journal_entry.text.toString()

            val post = Post(
                postId,
                userId,
                arrayListOf(userId!!),
                userImage,
                username,
                title,
                entry,
                imageUrl,
                System.currentTimeMillis(),
                arrayListOf(),
                arrayListOf()
            )

            firebasePostsdb.child(userId!!).setValue(post).addOnSuccessListener {
                Toast.makeText(this, "Journal post save to database success",
                    Toast.LENGTH_SHORT).show()
                finish()
            }.addOnFailureListener { e ->
                e.printStackTrace()
                Toast.makeText(this, "Journal post save to database failed",
                    Toast.LENGTH_SHORT).show()
            }
            linlay_post_pb.visibility = View.GONE
        } else {
            linlay_post_pb.visibility = View.GONE
            Toast.makeText(this, "Journal post title cannot be blank or empty.",
                Toast.LENGTH_SHORT).show()
        }
    }

}

