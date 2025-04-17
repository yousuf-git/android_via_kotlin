package  com.learning.text_file_handling

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream
import java.io.FileInputStream

class MainActivity : AppCompatActivity() {

    private lateinit var textView: TextView
    private val fileName = "my_data_file.txt"
    private val defaultContent = "I love my parents"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView = findViewById(R.id.textView)

        // Check and handle the file
        val fileContent = checkAndHandleFile()
        textView.text = fileContent
    }

    private fun checkAndHandleFile(): String {
        val file = File(filesDir, fileName)

        return if (file.exists()) {
            // File exists - read its content
            readFileContent(file)
        } else {
            // File doesn't exist - create it with default content
            createFileWithContent(file, defaultContent)
            defaultContent
        }
    }

    private fun readFileContent(file: File): String {
        return try {
            FileInputStream(file).bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            "Error reading file: ${e.message}"
        }
    }

    private fun createFileWithContent(file: File, content: String) {
        try {
            FileOutputStream(file).use { fos ->
                fos.write(content.toByteArray())
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error creating file: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}