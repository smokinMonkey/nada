package com.smokinmonkey.nada.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.smokinmonkey.nada.R
import com.smokinmonkey.nada.fragments.CustomFragment
import com.smokinmonkey.nada.fragments.HomeFragment
import com.smokinmonkey.nada.fragments.MessagesFragment
import com.smokinmonkey.nada.fragments.MyPostsFragment
import com.smokinmonkey.nada.util.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), CustomCallback {
    // firebase instances
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebasedb = FirebaseDatabase.getInstance().getReference()

    private var userId = firebaseAuth.currentUser?.uid
    private var user: User? = null

    // fragment instances
    private val homeFrag = HomeFragment()
    private val messagesFrag = MessagesFragment()
    private val myPostsFrag = MyPostsFragment()
    private var currentFrag: CustomFragment = homeFrag

    private var pageAdapter: PageAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pageAdapter = PageAdapter(supportFragmentManager)

        // setup container for fragments
        vp_fragment_container.adapter = pageAdapter
        vp_fragment_container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tablay_home))
        tablay_home.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(vp_fragment_container))

        tablay_home.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) { }
            override fun onTabUnselected(p0: TabLayout.Tab?) { }

            @SuppressLint("RestrictedApi")
            override fun onTabSelected(p0: TabLayout.Tab?) {
                when(p0?.position) {
                    0 -> {
                        tv_main_navbar_title.text = "Home"
                        fab_add_post.visibility = View.GONE
                        currentFrag = homeFrag
                    }
                    1 -> {
                        tv_main_navbar_title.text = "Messages"
                        fab_add_post.visibility = View.VISIBLE
                        currentFrag = messagesFrag
                    }
                    2 -> {
                        tv_main_navbar_title.text = "My Posts"
                        fab_add_post.visibility = View.VISIBLE
                        currentFrag = myPostsFrag
                    }
                }
            }
        })
        // go to profile activity or setup user profile
        iv_main_navbar_profile_icon.setOnClickListener { view ->
            startActivity(ProfileActivity.newIntent(this))
        }
        // floating action button start new post activity
        fab_add_post.setOnClickListener {
            startActivity(PostJournalActivity.newIntent(this, userId, user?.username, user?.imageUrl))
        }
    }

    override fun onResume() {
        super.onResume()
        userId = FirebaseAuth.getInstance().currentUser?.uid
        if(userId == null) {
            startActivity(LoginActivity.newIntent(this))
            finish()
        } else {
            populate()
        }
    }

    fun populate() {
        linlay_main_pb.visibility = View.VISIBLE
        firebasedb.child(DATA_USERS).child(userId!!).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onCancelled(p0: DatabaseError) { }

            override fun onDataChange(p0: DataSnapshot) {
                user = p0.getValue(User::class.java)
                user?.let {
                    iv_main_navbar_profile_icon.loadUrl(it.imageUrl)
                    linlay_main_pb.visibility = View.GONE
                    updateFragmentUser()
                }
            }
        })
        linlay_main_pb.visibility = View.GONE
    }

    // bind user with the corresponding fragment
    fun updateFragmentUser() {
        homeFrag.setUser(user!!)
        messagesFrag.setUser(user!!)
        myPostsFrag.setUser(user!!)
        currentFrag.updateList()
    }

    inner class PageAdapter(fm: FragmentManager):
        FragmentPagerAdapter(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getItem(position: Int): Fragment {
            return when(position) {
                0 -> homeFrag
                1 -> messagesFrag
                else -> myPostsFrag
            }
        }
        override fun getCount() = 3
    }

    companion object {
        fun newIntent(context: Context) = Intent(context, MainActivity::class.java)
    }

    override fun onUserUpdated() { populate() }
    override fun onRefresh() { currentFrag.updateList() }
}
