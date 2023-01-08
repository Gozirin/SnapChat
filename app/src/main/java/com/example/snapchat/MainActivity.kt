package com.example.snapchat

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    var emailEditText: EditText? = null
    var passwordEditText: EditText? = null
    val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)

        if (mAuth.currentUser != null) {
            logIn()
        }
    }
    fun goClicked(view: View) {
        // CHECK IF WE CAN LOGIN USER
        mAuth.signInWithEmailAndPassword(emailEditText?.text.toString(), passwordEditText?.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    task.result.user?.let {
                        FirebaseDatabase.getInstance().getReference().child("users").child(
                            it.uid)
                    }
                    logIn()
                } else {
                    // SIGNUP THE USER
                    mAuth.createUserWithEmailAndPassword(emailEditText?.text.toString(), passwordEditText?.text.toString())
                        .addOnCompleteListener(this) { task -> }
                    if (task.isSuccessful) {
                        // ADD DATABASE
                        logIn()
                    } else {
                        Toast.makeText(this, "Login Failed.Try Again.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    fun logIn() {
        // MOVE TO NEXT ACTIVITY
        val intent = Intent(this, SnapsActivity::class.java)
        startActivity(intent)
    }
}
