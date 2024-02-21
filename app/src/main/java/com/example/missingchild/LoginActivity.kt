package com.example.missingchild

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaTypeOrNull


import com.example.missingchild.Constants.FLASK_BASE_URL

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val usernameEditText: EditText = findViewById(R.id.username_edit_text)
        val passwordEditText: EditText = findViewById(R.id.password_edit_text)
        val loginButton: ImageView = findViewById(R.id.login_button)
        val goToRegisterTextView = findViewById<TextView>(R.id.go_to_register_text_view)

        val goToForgotPassword = findViewById<TextView>(R.id.textViewForgotPassword)

        goToForgotPassword.setOnClickListener{
            // Handle click action to navigate to the registration activity
            val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
            startActivity(intent)

        }
        goToRegisterTextView.setOnClickListener{
            // Handle click action to navigate to the registration activity
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            if (username.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
                // Show an error message indicating both fields are required
                // For example, you can show a Toast message or set an error on the EditTexts
                usernameEditText.error = "Username is required"

            }
            else if (password.isEmpty()){
                passwordEditText.error = "Password is required"
            }
            else {
                makeLoginRequest(username, password)
            }

        }
    }

    private fun makeLoginRequest(username: String, password: String) {

        val url = FLASK_BASE_URL+"/login"

        val client = OkHttpClient()

        val formBody = FormBody.Builder()
            .add("email", username)
            .add("password", password)
            .build()


//        val body = RequestBody.create(mediaType, formBody.toString())
//        System.out.println(body)
        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                System.out.println("failed")
                // Handle failure
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                Log.d("LoginActivity", "Response: $responseData") // Log response for debugging

                runOnUiThread {
                    try {
                        val jsonResponse = JSONObject(responseData)
                        val message = jsonResponse.getString("message")

                        val token = jsonResponse.getString("token")
                        Log.d("LoginActivity", "Response message: $message")
                        System.out.println(message)
                        // Assume `token` is the received token from the server

// Get the SharedPreferences instance
                        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

// Save the token in SharedPreferences
                        val editor = sharedPreferences.edit()
                        editor.putString("sessionToken", token)
                        editor.apply()


                        Toast.makeText( this@LoginActivity,message, Toast.LENGTH_SHORT,).show()// Log response message
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                        // Handle response message accordingly (e.g., show a toast or navigate to another activity)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }
        })
    }
}
