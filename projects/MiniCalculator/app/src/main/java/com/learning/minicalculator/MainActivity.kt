package com.learning.minicalculator;

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private var num1: EditText? = null
    private var num2: EditText? = null
    private var result: EditText? = null
    private var calculateButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        num1 = findViewById<EditText>(R.id.num1)
        num2 = findViewById<EditText>(R.id.num2)
        result = findViewById<EditText>(R.id.result)
        calculateButton = findViewById<Button>(R.id.calculateButton)

        // Set click listener for the button
        calculateButton?.setOnClickListener(View.OnClickListener { calculateSum() })
    }

    private fun calculateSum() {
        // Get input values
        val input1 = num1!!.text.toString()
        val input2 = num2!!.text.toString()

        // Check if inputs are not empty
        if (input1.isEmpty() || input2.isEmpty()) {
            result!!.setText("Please enter both numbers !")
            return
        }

        // Parse inputs to integers
        val number1 = input1.toDoubleOrNull()
        val number2 = input2.toDoubleOrNull()

        // Calculate sum
        val sum = (number1 ?: 0.0) + (number2 ?: 0.0)

        // Display result
        result!!.setText(sum.toString())
    }
}