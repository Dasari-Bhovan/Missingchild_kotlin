package com.example.missingchild

import RecordFragment
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import android.Manifest
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.android.gms.location.LocationServices

import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.lang.Error
import java.util.concurrent.TimeUnit

class HomeActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
//    private val LOCATION_PERMISSION_REQUEST_CODE = 1001
    private lateinit var userAddress:TextView

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
        private const val CAMERA_PERMISSION_REQUEST_CODE = 1002
        private const val STORAGE_PERMISSION_REQUEST_CODE = 1003
        private const val PERMISSION_REQUEST_CODE = 123
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        userAddress=findViewById(R.id.user_address)
        bottomNavigationView = findViewById(R.id.bottomnav)
        supportActionBar?.hide()
        
        fetchLocation()

        checkAndRequestPermissions()

        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    fetchLocation()
                    replaceFragment(HomeFragment()) // Replace with your HomeFragment
                    true
                }
                R.id.camera -> {
                    fetchLocation()
                    replaceFragment(CameraFragment()) // Replace with your CameraFragment
                    true
                }
                R.id.reports -> {
                    replaceFragment(RecordFragment()) // Replace with your CameraFragment
                    true
                }
                // Add handling for other menu items as needed

                else -> false
            }
        }

        // Initially display the HomeFragment
        replaceFragment(HomeFragment()) // Replace with your default fragment
    }

//    private fun checkLocationPermission() {
//        if (ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//                LOCATION_PERMISSION_REQUEST_CODE
//            )
//        } else {
//            // Permissions already granted, proceed to get location
//            fetchLocation()
//        }
//    }
    private fun fetchLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    // Got last known location. In some rare situations, this can be null.
                    if (location != null) {
                        val latitude = location.latitude
                        val longitude = location.longitude
                        var formatted_address=latitude.toString() + "," + longitude.toString()

                        userAddress.text = latitude.toString() + "," + longitude.toString()
                        println(latitude.toString() + "," + longitude.toString())
                        val client = OkHttpClient.Builder()
                            .connectTimeout(300, TimeUnit.SECONDS)
                            .readTimeout(300, TimeUnit.SECONDS)
                            .writeTimeout(300, TimeUnit.SECONDS)
                            .build()

                        // Request body containing the image file


                        val url1 = "https://api.geoapify.com/v1/geocode/reverse?lat="+latitude.toString()+"&lon="+longitude.toString()+"&apiKey=e47723ed0b064c3fab486307f31e2ae5"

                        // Request object
                        val request: Request = Request.Builder()
                            .url(url1) // Replace with your server URL
                            .get()
                            .build()

                        // Asynchronous call using OkHttp
                        client.newCall(request).enqueue(object : Callback {
                            override fun onFailure(call: Call, e: IOException) {
//

                                // Handle failure (e.g., network error)

                            }

                            override fun onResponse(call: Call, response: Response) {
                                // Handle successful response
//                                println(response)
                                val responseBody = response.body?.string()
//                                val hi=JSONObject(responseBody)
//                                userAddress.text
                                responseBody?.let {
                                    val jsonObject = JSONObject(it)
                                    val featuresArray = jsonObject.getJSONArray("features")
//                                    println(featuresArray.length())
                                    if (featuresArray.length() > 0) {
                                        val firstFeature = featuresArray.getJSONObject(0)
                                        println(firstFeature.toString())
                                        val properties = firstFeature.getJSONObject("properties")
                                        try {
                                            formatted_address =
                                                properties.getString("formatted")
                                        }catch (ex:Error){
                                            println("Error fetching location")
                                        }
//                                        println("Address Line 1: $formatted_address")
                                        userAddress.text=formatted_address.toString()
                                    } else {
                                        println("No features found in the response.")
                                    }
//                                println(hi)

                            }
                            }
                        })

                        println("Location recognized success")
                        // Store the latitude and longitude in your user profile or wherever needed
                    }
                }
                .addOnFailureListener { e ->
                    // Handle failure to get location
                    e.printStackTrace()
                }
        } catch (ex: SecurityException) {
            ex.printStackTrace()
        }
    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//
//        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
//             if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
//                // Permission granted, proceed to get location
//                fetchLocation()
//            } else {
////                requestPermission()
//                // Permission denied, handle accordingly
//            }
//        }
//    }
private fun checkAndRequestPermissions() {
    val permissionsNeeded = mutableListOf<String>()

    if (!hasLocationPermission()) {
        permissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION)
    }
    else{
        fetchLocation()
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


    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
