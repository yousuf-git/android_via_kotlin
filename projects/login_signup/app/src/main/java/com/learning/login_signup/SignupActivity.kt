package com.learning.login_signup;

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class SignupActivity : AppCompatActivity() {
//    'lateinit' allows initializing a not-null property outside of a constructor
    private lateinit var fullNameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var signupButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        fullNameEditText = findViewById(R.id.fullNameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        signupButton = findViewById(R.id.signupButton)

        signupButton.setOnClickListener {
            val fullName = fullNameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (fullName.trim().isNotEmpty() && email.trim().isNotEmpty() && password.trim().isNotEmpty()) {
                // Save user data
                saveUserData(fullName, email, password)

//                Toast.makeText(this, getString(R.string.signup_success), Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveUserData(fullName: String, email: String, password: String) {
//        Using SharedPreferences to save user data
//        val sharedPref = getSharedPreferences("UserData", Context.MODE_PRIVATE)
//        with(sharedPref.edit()) {
//            putString("fullName", fullName)
//            putString("email", email)
//            putString("password", password)
//            apply()
//        }
//        Storing data in SQLite database
        val db = DBHelper(this, null)
        if (db.signup(fullName, email, password)) {
            Toast.makeText(this, getString(R.string.signup_success), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, getString(R.string.signup_failed), Toast.LENGTH_SHORT).show()
        }
    }
}

