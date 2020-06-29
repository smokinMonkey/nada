package com.smokinmonkey.nada.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.smokinmonkey.nada.R
import com.smokinmonkey.nada.util.*
import kotlinx.android.synthetic.main.activity_profile.*
import java.util.*

class ProfileActivity : AppCompatActivity() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebasedb = FirebaseDatabase.getInstance().getReference()
    private val firebaseStorage = FirebaseStorage.getInstance().reference
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
//    private var profileImageUrl: String? = null
    private var userBirthdate: String? = null
    private var userAge: String? = null
    private var userCalendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // check if user is logged in, if user is null, should not be here
        if(userId == null) { finish() }

        // set onclick listener for when photo is clicked
        // have user pick an image, later implement camera capability
        iv_profile_user.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE_PHOTO)
        }

//        val datePickerDialog = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
//            userCalendar.set(Calendar.YEAR, year)
//            userCalendar.set(Calendar.MONTH, month)
//            userCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
//            val df = "MM/dd/yyyy"
//            val sdf = SimpleDateFormat(df)
//            userBirthdate = sdf.format(userCalendar)
//        }
        // initialize radio buttons at initial state
        rb_profile_woman.isChecked = true
        rb_profile_look_woman.isChecked = true

        // populate information from firebase store and storage
        populateInfo()
    }

//    fun calculateAge() {
//        val today = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            LocalDate.now()
//        } else {
//            null
//        }
//        val bd = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            LocalDate.of(userCalendar.get(Calendar.YEAR), userCalendar.get(Calendar.MONTH), userCalendar.get(Calendar.DAY_OF_MONTH))
//        } else {
//            null
//        }
//        val p = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            Period.between(bd, today)
//        } else {
//            null
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            userAge = Integer.toString(p!!.years)
//            tv_profile_age.setText(userAge)
//        }
//    }

    // method to run when activity returned a result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_PHOTO) {
            // store image into firebase storage
            storeImage(data?.data)
        }
    }

    // store image into storage
    fun storeImage(imageUrl: Uri?) {
        imageUrl?.let {
            Toast.makeText(this, "Uploading image...", Toast.LENGTH_SHORT).show()
            linlay_profile_pb.visibility = View.VISIBLE
            val filepath = firebaseStorage.child(DATA_USER_IMAGES).child(userId!!)
            filepath.putFile(imageUrl).addOnSuccessListener {
                filepath.downloadUrl.addOnSuccessListener { uri ->
                    val url = uri.toString()
                    firebasedb.child(DATA_USERS).child(userId!!).child(DATA_USER_IMAGEURL).setValue(url)
                        .addOnSuccessListener {
                            iv_profile_user.loadUrl(url)
                        }
                    linlay_profile_pb.visibility = View.GONE
                }.addOnFailureListener { onUploadFailure() }

            }.addOnFailureListener { onUploadFailure() }
        }

    }

    // upload image fails
    fun onUploadFailure() {
        Toast.makeText(this, "Image upload failed, please try again.", Toast.LENGTH_SHORT).show()
        linlay_profile_pb.visibility = View.GONE
    }

    // grabs information from firestore and storage and bind them to view
    fun populateInfo() {
        linlay_profile_pb.visibility = View.VISIBLE
        firebasedb.child(DATA_USERS).child(userId!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) { }

                override fun onDataChange(p0: DataSnapshot) {
                    // get data from firestore
                    val user = p0.getValue(User::class.java)
                    // bind corresponding data to view
                    et_profile_username.setText(user?.username, TextView.BufferType.EDITABLE)
                    // get birthday string and calculate age
                    et_profile_birthday.setText(user?.birthday, TextView.BufferType.EDITABLE)

                    // populate radio buttons
                    if (user?.gender == DATA_GENDER_MALE) {
                        rb_profile_man.isChecked = true
                    } else {
                        rb_profile_woman.isChecked = true
                    }
                    if (user?.preferredGender == DATA_GENDER_MALE) {
                        rb_profile_look_man.isChecked = true
                    } else {
                        rb_profile_look_woman.isChecked = true
                    }
                    user?.imageUrl.let {
                        iv_profile_user.loadUrl(user?.imageUrl)
                    }
                    linlay_profile_pb.visibility = View.GONE
                }
            })
        linlay_profile_pb.visibility = View.GONE
                // set date string to calendar object
//                if(!user?.birthday.isNullOrEmpty()) {
//                    val sdf = SimpleDateFormat("MM/dd/yyyy")
//                    userCalendar.setTime(sdf.parse(user?.birthday))
//                }

                // checks if age is already in database
//                calculateAge()

//                tv_profile_age.setText(user?.age)

    }

    // when user click apply button, save information to firestore
    fun onApply(v: View) {
//        linlay_profile_pb.visibility = View.VISIBLE
        val username = et_profile_username.text.toString()
//        val userBirthdate = et_profile_birthday.text.toString()
        val gender = if(rb_profile_man.isChecked) DATA_GENDER_MALE else DATA_GENDER_FEMALE
        val preferredGender = if(rb_profile_look_man.isChecked) DATA_GENDER_MALE else DATA_GENDER_FEMALE
//
//        val map = HashMap<String, Any>()
//        map[DATA_USER_USERNAME] = username
//        map[DATA_USER_BDAY] = userBirthdate
//        if(userAge.isNullOrEmpty()) {
//            map[DATA_USER_AGE] = "0"
//        } else {
//            map[DATA_USER_AGE] = userAge!!
//        }
//        map[DATA_USER_GENDER] = gender
//        map[DATA_USER_GENDER_PREF] = preferredGender
//
//        firebasedb.child(DATA_USERS).child(userId!!).setValue(map)
//            .addOnSuccessListener {
//                Toast.makeText(this, "Update successful", Toast.LENGTH_SHORT).show()
//                finish()
//            }
//            .addOnFailureListener { e ->
//                e.printStackTrace()
//                Toast.makeText(this, "Update failed, please try again.", Toast.LENGTH_SHORT).show()
//                linlay_profile_pb.visibility = View.GONE
//            }
        firebasedb.child(DATA_USERS).child(userId!!).child(DATA_USER_USERNAME).setValue(username)
        firebasedb.child(DATA_USERS).child(userId!!).child(DATA_USER_GENDER).setValue(gender)
        firebasedb.child(DATA_USERS).child(userId!!).child(DATA_USER_GENDER_PREF).setValue(preferredGender)

        startActivity(MainActivity.newIntent(this))
    }

    // when user click signout button
    fun onSignout(v: View) {
        firebaseAuth.signOut()
        startActivity(LoginActivity.newIntent(this))
        finish()
    }

    companion object {
        fun newIntent(context: Context) = Intent(context, ProfileActivity::class.java)
    }
}
