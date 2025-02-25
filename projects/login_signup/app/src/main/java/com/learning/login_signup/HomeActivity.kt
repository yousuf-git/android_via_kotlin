package com.learning.login_signup;

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class HomeActivity : AppCompatActivity() {
    private lateinit var nameTextView: TextView
    private lateinit var emailTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        nameTextView = findViewById(R.id.nameTextView)
        emailTextView = findViewById(R.id.emailTextView)

        // Load user data from SharedPreferences
//        val sharedPref = getSharedPreferences("UserData", Context.MODE_PRIVATE)
//        val fullName = sharedPref.getString("fullName", "Yousuf") ?: "Yousuf"
//        val email = sharedPref.getString("email", "yousuf@gmail.com") ?: "yousuf@gmail.com"
        
//        Get user data from intent
        val fullName = intent.getStringExtra("name") ?: "Yousuf"
        val email = intent.getStringExtra("email") ?: "yousuf@gmail.com"

        nameTextView.text = "Name: $fullName"
        emailTextView.text = "Email: $email"
    }
}

