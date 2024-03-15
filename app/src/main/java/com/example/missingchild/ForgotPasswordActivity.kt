package com.example.missingchild
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.*

import com.example.missingchild.Constants.FLASK_BASE_URL;
import java.io.IOException

class ForgotPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.new_activity_forgot_password)


        val editTextEmail = findViewById<EditText>(R.id.editTextEmail)
        val buttonResetPassword = findViewById<Button>(R.id.buttonResetPassword)
        val loginButton=findViewById<TextView>(R.id.go_to_login)

        loginButton.setOnClickListener{
            val intent = Intent(this@ForgotPasswordActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        buttonResetPassword.setOnClickListener {
            val email = editTextEmail.text.toString().trim()

            if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                // Show an error if email is empty or not in a valid format
//                Toast.makeText(this@ForgotPasswordActivity, "Please enter a valid email", Toast.LENGTH_SHORT).show()
                editTextEmail.error="Enter a valid email"
                return@setOnClickListener
            }
            else{
                sendForgotPasswordRequest(email);
                System.out.println(email+"  Success");
                val intent = Intent(this@ForgotPasswordActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }

            // TODO: Implement your logic to send reset password instructions to the entered email
            // You can use the provided email address (variable: email) to send a reset password link

            // For demonstration purposes, just show a message
//            Toast.makeText(this@ForgotPasswordActivity, "Reset password instructions sent to $email", Toast.LENGTH_SHORT).show()
        }
    }
    private fun sendForgotPasswordRequest(email: String) {
        val url = FLASK_BASE_URL+"/reset_password" // Replace with your Flask reset password endpoint URL

        val formBody = FormBody.Builder()
            .add("email", email)
            .build()

        val client = OkHttpClient()

        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@ForgotPasswordActivity, "Failed to reset password", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()

                runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(this@ForgotPasswordActivity, "Password reset instructions sent to $email", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@ForgotPasswordActivity, "Failed to reset password", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}
