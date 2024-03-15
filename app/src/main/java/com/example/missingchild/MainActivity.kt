package com.example.missingchild

import android.content.Context
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import android.os.StrictMode
import android.provider.Settings
import androidx.appcompat.app.AlertDialog


class MainActivity : AppCompatActivity() {

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
        private const val CAMERA_PERMISSION_REQUEST_CODE = 1002
        private const val STORAGE_PERMISSION_REQUEST_CODE = 1003
        private const val PERMISSION_REQUEST_CODE = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        val loggedInText:TextView = findViewById(R.id.loggedInText)

        val logoutButton: Button = findViewById(R.id.logoutButton)

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val sessionToken = sharedPreferences.getString("sessionToken", null)
//        checkPermissions()
        checkAndRequestPermissions()
        if (!isNetworkAvailable()) {
            buildAlertMessageNoInternet()
        }
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        if (sessionToken.isNullOrEmpty()) {
            // No session token found, navigate to LoginActivity
            val intent = Intent(this, LoginActivity::class.java)

            startActivity(intent)
            finish() // Optionally, finish MainActivity
        } else {
            // User is logged in, display a message and setup logout functionality
            checkAndRequestPermissions()
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
//            logoutButton.setOnClickListener {
//                // Perform logout functionality
//                makeLogoutRequest(sessionToken)
//            }
        }
    }
    private fun checkAndRequestPermissions() {
        val permissionsNeeded = mutableListOf<String>()

        if (!hasLocationPermission()) {
            permissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (!hasCameraPermission()) {
            permissionsNeeded.add(Manifest.permission.CAMERA)
        }
        if (!hasStoragePermission()) {
            permissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            permissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if (permissionsNeeded.isNotEmpty()) {
            requestPermissions(
                permissionsNeeded.toTypedArray(),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun buildAlertMessageNoInternet() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Active internet connection is required. Please enable internet and try again.")
            .setCancelable(false)
            .setPositiveButton("Settings") { _, _ ->
                startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

        val alert = builder.create()
        alert.show()
    }

    private fun isNetworkAvailable() =
        (this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).run {
            getNetworkCapabilities(activeNetwork)?.run {
                hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                        || hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                        || hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
            } ?: false
        }


    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun hasStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST_CODE
        )
    }

    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            STORAGE_PERMISSION_REQUEST_CODE
        )
    }


    private fun makeLogoutRequest(sessionToken:String) {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("sessionToken")
        editor.apply()

        val url = Constants.FLASK_BASE_URL +"/logout"

        val client = OkHttpClient()

        val formBody = FormBody.Builder()
            .add("token", sessionToken)
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
                System.out.println("failed");
                // Handle failure
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                Log.d("LogoutActivity", "Response: $responseData") // Log response for debugging

                runOnUiThread {
                    try {
                        val jsonResponse = JSONObject(responseData)
                        val message = jsonResponse.getString("message")

                        Log.d("LogoutActivity", "Response message: $message")
                        System.out.println(message)
                        // Assume `token` is the received token from the server




                        // Handle response message accordingly (e.g., show a toast or navigate to another activity)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }
        })
        val intent = Intent(this@MainActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
