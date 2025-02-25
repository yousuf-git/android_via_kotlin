package com.learning.login_signup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LoginActivity : AppCompatActivity() {
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var signupTextView: TextView
    private lateinit var dateTimeTextView: TextView
    private var user: User? = null

//    lateinit means that the variable will be initialized later

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        signupTextView = findViewById(R.id.signupTextView)
        dateTimeTextView = findViewById(R.id.dateTimeTextView)

        // Set up date and time
        updateDateTime()

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
//            For Debug....
//            Toast.makeText(this,email, Toast.LENGTH_SHORT).show()
//            Toast.makeText(this,password, Toast.LENGTH_SHORT).show()

//            ============== Login using SharedPreferences ==============
//            val sharedPref = getSharedPreferences("UserData", Context.MODE_PRIVATE)
//            val savedEmail = sharedPref.getString("email", "yousuf@gmail.com") ?: R.string.default_email
//            val savedPassword = sharedPref.getString("password", "yousuf") ?: R.string.default_password
//          For Debug....
//            Toast.makeText(this,savedEmail, Toast.LENGTH_SHORT).show()
//            Toast.makeText(this,savedPassword, Toast.LENGTH_SHORT).show()
//          Default:
//            if (email == "yousuf@gmail.com" && password == "yousuf") {
//            if (email == savedEmail && password == savedPassword) {
//                showSuccessDialog()
//            } else {
//                Toast.makeText(this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show()
//            }
//            ============== Login Using SQLite ==============
            val db = DBHelper(this, null)
            user = db.login(email, password)
            if (user != null) {
                showSuccessDialog()
            } else {
                Toast.makeText(this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show()
            }
        }

        signupTextView.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    private fun updateDateTime() {
//        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
//        Time with AM and PM (12 hr format)
        val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.getDefault())
        val currentDateAndTime = sdf.format(Date())
        dateTimeTextView.text = currentDateAndTime

        // Updating every second
        Handler(Looper.getMainLooper()).postDelayed({ updateDateTime() }, 1000)
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.login_success))
//            .setMessage(getString(R.string.welcome))
            .setPositiveButton("NEXT") { _, _ ->

//                startActivity(Intent(this, HomeActivity::class.java))
//                start the home activity and send the user there
                val intent = Intent(this, HomeActivity::class.java)

                intent.putExtra("name", user?.getName())
                intent.putExtra("email", user?.getEmail())
                startActivity(intent)
                finish()
            }
            .show()
    }
}

