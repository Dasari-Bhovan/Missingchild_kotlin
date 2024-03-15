package com.example.missingchild;
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import java.io.IOException
import com.example.missingchild.Constants.FLASK_BASE_URL;

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val editTextEmail = findViewById<EditText>(R.id.editTextEmail)
        val editTextPhoneNumber = findViewById<EditText>(R.id.editTextPhoneNumber)
        val editTextFullName = findViewById<EditText>(R.id.editTextFullName)
        val editTextPassword = findViewById<EditText>(R.id.editTextPassword)
        val editTextConfirmPassword = findViewById<EditText>(R.id.editTextConfirmPassword)
        val buttonRegister = findViewById<ImageView>(R.id.buttonRegister)
        val backToLogin=findViewById<TextView>(R.id.go_to_login)

        backToLogin.setOnClickListener{
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        buttonRegister.setOnClickListener {
            val email = editTextEmail.text.toString().trim()
            val phoneNumber = editTextPhoneNumber.text.toString().trim()
            val fullName = editTextFullName.text.toString().trim()
            val password = editTextPassword.text.toString().trim()
            val confirmPassword = editTextConfirmPassword.text.toString().trim()




         if (email.isEmpty()) {
                // Show an error message indicating both fields are required
                // For example, you can show a Toast message or set an error on the EditTexts
                editTextEmail.error = "Email is required"

            }
             if (password.isEmpty()){
                editTextPassword.error = "Password is required"
            }
             if (confirmPassword.isEmpty()){
                editTextConfirmPassword.error="Confirm Password is required"
            }
             if (phoneNumber.isEmpty()){
                editTextPhoneNumber.error="Phone Number is required"
            }
            if(fullName.isEmpty()){
                editTextFullName.error="Full name is required"
            }



            if (email.isEmpty() && phoneNumber.isEmpty() && fullName.isEmpty() && password.isEmpty() && confirmPassword.isEmpty()) {
                Toast.makeText(this@RegisterActivity, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else if (password != confirmPassword) {
                Toast.makeText(this@RegisterActivity, "Passwords do not match", Toast.LENGTH_SHORT).show()
                editTextConfirmPassword.error="Password donot match"
                return@setOnClickListener
            }

            else {

                sendRegistrationRequest(email, phoneNumber, fullName, password)
//                System.out.println("Registration Successful ")

            }
        }

    }

    private fun sendRegistrationRequest(email: String, phoneNumber: String, fullName: String, password: String) {
        val url = FLASK_BASE_URL+"/register" // Replace with your Flask register endpoint URL

        val formBody = FormBody.Builder()
            .add("email", email)
            .add("phone", phoneNumber)
            .add("full_name", fullName)
            .add("password", password)
            .build()

        val client = OkHttpClient()

        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                println("Registration Failed")
                runOnUiThread {
                    Toast.makeText(this@RegisterActivity, "Registration failed. Please try again later.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()

                // Process the response
                // Display a Toast or update UI based on registration success or failure
                if (response.isSuccessful) {
                    runOnUiThread {
                        Toast.makeText(this@RegisterActivity, "Registration successful", Toast.LENGTH_SHORT).show()
                        System.out.println("Registration Successful ")
                        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                        // Optionally, navigate to another activity or perform further actions upon successful registration
                    }

                } else {
                    runOnUiThread {
                        Toast.makeText(this@RegisterActivity, "Registration failed. Please try again later.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}
