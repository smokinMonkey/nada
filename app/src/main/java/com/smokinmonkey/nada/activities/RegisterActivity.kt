package com.smokinmonkey.nada.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.smokinmonkey.nada.R
import com.smokinmonkey.nada.util.*
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    // firebase instances
    private val firebasedb = FirebaseDatabase.getInstance().getReference()
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseAuthListener = FirebaseAuth.AuthStateListener {
        val user = firebaseAuth.currentUser?.uid
        user?.let {
            startActivity(MainActivity.newIntent(this))
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // set reset input text errors
        setTextChangeListener(et_register_username, til_register_username)
        setTextChangeListener(et_register_email, til_register_email)
        setTextChangeListener(et_register_password, til_register_password)
//        setTextChangeListener(et_register_confirm_password, til_register_confirm_password)
    }
    // reset the error when edit text changed
    fun setTextChangeListener(et: EditText, til: TextInputLayout) {
        et.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                til.isErrorEnabled = false
            }
        })
    }

    // method to create account with email and password
    fun register(v: View) {
        var proceed = true
        // check if username text is null or empty
        if(et_register_username.text.isNullOrEmpty()) {
            til_register_username.error = "Username is required"
            til_register_username.isErrorEnabled = true
            proceed = false
        }
        // check if email text is null or empty
        if(et_register_email.text.isNullOrEmpty()) {
            til_register_email.error = "Email is required"
            til_register_email.isErrorEnabled = true
            proceed = false
        }
        // check if password is null or empty
        if(et_register_password.text.isNullOrEmpty()) {
            til_register_password.error = "Password is required"
            til_register_password.isErrorEnabled = true
            proceed = false
        }
//        // check if confirm password is null or empty
//        if(et_register_confirm_password.text.isNullOrEmpty()) {
//            til_register_confirm_password.error = "Confirm password is required"
//            til_register_confirm_password.isErrorEnabled = true
//            proceed = false
//        }
        // if everything passes register account
        if(proceed) {
            linlay_register_pb.visibility = View.VISIBLE
            firebaseAuth.createUserWithEmailAndPassword(et_register_email.text.toString(),
                et_register_password.text.toString()).addOnCompleteListener { task ->
                if(!task.isSuccessful) {
                    Toast.makeText(this, "Register error: ${task.exception?.localizedMessage}",
                        Toast.LENGTH_SHORT).show()
                } else {
                    // save information to database
                    val uid = firebaseAuth.uid
                    val email = et_register_email.text.toString()
                    val username = et_register_username.text.toString()
//                    val user = User(uid, email, username, "", "", "", "", "")
                    firebasedb.child(DATA_USERS).child(uid!!).child(DATA_USER_ID).setValue(uid)
                    firebasedb.child(DATA_USERS).child(uid!!).child(DATA_USER_USERNAME).setValue(username)
                    firebasedb.child(DATA_USERS).child(uid!!).child(DATA_USER_EMAIL).setValue(email)
//                        .addOnSuccessListener {
//                            Toast.makeText(this, "User data saved to database success",
//                                Toast.LENGTH_SHORT).show()
//                        }.addOnFailureListener { e ->
//                            e.printStackTrace()
//                            Toast.makeText(this, "User data saved to database failed",
//                                Toast.LENGTH_SHORT).show()
//                        }
                }
                linlay_register_pb.visibility = View.GONE
            }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                    linlay_register_pb.visibility = View.GONE
                }
        }
    }
    // method to start to go back to login activity
    fun backToLogin(v: View) {
        startActivity(LoginActivity.newIntent(this))
        finish()
    }
    // activity lifecycle on start to add auth state listener
    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener(firebaseAuthListener)
    }
    // activity lifecycle on stop to remove auth state listener
    override fun onStop() {
        super.onStop()
        firebaseAuth.removeAuthStateListener(firebaseAuthListener)
    }

    companion object {
        fun newIntent(context: Context) = Intent(context, RegisterActivity::class.java)
    }
}
