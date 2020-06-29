package com.smokinmonkey.nada.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.ContextMenu
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.smokinmonkey.nada.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    // firebase instances
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseAuthListener = FirebaseAuth.AuthStateListener {
        val user = firebaseAuth.currentUser?.uid
        user?.let {
            startActivity(MainActivity.newIntent(this))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setTextChangeListener(et_login_email, til_login_email)
        setTextChangeListener(et_login_password, til_login_password)

//        TestFairy.begin(this, "SDK-0oV5VwPg")
    }
    // reset the error when edit text changed
    fun setTextChangeListener(et: EditText, til: TextInputLayout) {
        et.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) { }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                til.isErrorEnabled = false
            }
        })
    }
    // when user clicked on login button
    fun onLogin(v: View) {
        var proceed = true
        // check if email is null or empty
        if(et_login_email.text.isNullOrEmpty()) {
            til_login_email.error = "Email is required..."
            til_login_email.isErrorEnabled = true
            proceed = false
        }
        // check if password is null or empty or greater than 6 characters
        if(et_login_password.text.isNullOrEmpty()) {
            til_login_password.error = "Password is required..."
            til_login_password.isErrorEnabled = true
            proceed = false
        }
        // check if there is any errors
        if(proceed) {
            linlay_login_pb.visibility = View.VISIBLE
            firebaseAuth.signInWithEmailAndPassword(et_login_email.text.toString(),
                et_login_password.text.toString()).addOnCompleteListener { task ->
                    if(!task.isSuccessful) {
                        linlay_login_pb.visibility = View.GONE
                        Toast.makeText(this, "Login error: ${task.exception?.localizedMessage}",
                            Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                    linlay_login_pb.visibility = View.GONE
                }
        }
        linlay_login_pb.visibility = View.GONE
    }
    // when register text view clicked and go to register activity
    fun onRegister(v: View) {
        startActivity(RegisterActivity.newIntent(this))
    }
    // when forgot password text view clicked and go to forgot password activity
    fun onForgotPassword(v: View) {
        Toast.makeText(this, "Forgot password clicked, implement later", Toast.LENGTH_SHORT).show()
    }
    // activity lifecycle on start to add auth state listener
    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener(firebaseAuthListener)
    }
    // activity lifecycle on stop remove auth state listener
    override fun onStop() {
        super.onStop()
        firebaseAuth.removeAuthStateListener(firebaseAuthListener)
    }

    companion object {
        fun newIntent(context: Context) = Intent(context, LoginActivity::class.java)
    }
}
