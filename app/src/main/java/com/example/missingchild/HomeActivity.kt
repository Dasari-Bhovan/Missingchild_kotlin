package com.example.missingchild

import ProfileFragment
import RecordFragment
import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit


class HomeActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
//    private val LOCATION_PERMISSION_REQUEST_CODE = 1001
    private lateinit var userAddress:TextView
    private val imagePicker =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                handleImageSelection(data)
            }
        }

    private val selectedImages = mutableListOf<File>()
    private lateinit var nameEditText: EditText
    private lateinit var ageEditText: EditText
    private val PICK_IMAGES_REQUEST = 1
    private lateinit var  imageCount:TextView
    private lateinit var heightEditText: EditText
    private lateinit var weightEditText: EditText
    private lateinit var DescriptionEditText: EditText

    private lateinit var MolesEditText: EditText

    private lateinit var LastSeenTimeEditText: EditText

    private lateinit var LastSeenAddressEditText: EditText
    private lateinit var ClothingDescriptionEditText: EditText
    private lateinit var PhysicalFeaturesEditText: EditText
    private lateinit var BehaviourCharacteristicsEditText: EditText
    private lateinit var MedicalInformationEditText: EditText
    private lateinit var ContactInformationEditText: EditText


    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
        private const val CAMERA_PERMISSION_REQUEST_CODE = 1002
        private const val STORAGE_PERMISSION_REQUEST_CODE = 1003
        private const val PERMISSION_REQUEST_CODE = 123
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.temp_home)
//        userAddress=findViewById(R.id.user_address)
        bottomNavigationView=findViewById(R.id.bottomNavigationView)
        val fab: FloatingActionButton = findViewById(R.id.fab)
        supportActionBar?.hide()
        
//        fetchLocation()

        checkAndRequestPermissions()

        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
//                    fetchLocation()
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
                R.id.profile -> {
                    replaceFragment(ProfileFragment()) // Replace with your CameraFragment
                    true
                }
                // Add handling for other menu items as needed

                else -> false
            }
        }
        fab.setOnClickListener {
            // Your onClick logic here
            showBottomDialog()
        }

        // Initially display the HomeFragment
        replaceFragment(HomeFragment()) // Replace with your default fragment
    }


    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            // Permissions already granted, proceed to get location
            fetchLocation()
        }
    }
    private fun buildAlertMessageNoGps() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton("No") { dialog, _ -> dialog.cancel() }
        val alert = builder.create()
        alert.show()
    }
private fun showBottomDialog() {
    val dialog = Dialog(this)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(R.layout.addsheet)

//    val videoLayout = dialog.findViewById<LinearLayout>(R.id.layoutVideo)
//    val shortsLayout = dialog.findViewById<LinearLayout>(R.id.layoutShorts)
//    val liveLayout = dialog.findViewById<LinearLayout>(R.id.layoutLive)
    val cancelButton = dialog.findViewById<ImageView>(R.id.cancelButton)
//
//    videoLayout.setOnClickListener {
//        dialog.dismiss()
//        Toast.makeText(this, "Upload a Video is clicked", Toast.LENGTH_SHORT).show()
//    }
//
//    shortsLayout.setOnClickListener {
//        dialog.dismiss()
//        Toast.makeText(this, "Create a short is Clicked", Toast.LENGTH_SHORT).show()
//    }
//
//    liveLayout.setOnClickListener {
//        dialog.dismiss()
//        Toast.makeText(this, "Go live is Clicked", Toast.LENGTH_SHORT).show()
//    }

    cancelButton.setOnClickListener {
        dialog.dismiss()
    }

    dialog.show()
    dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//    dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
    dialog.window?.setGravity(Gravity.BOTTOM)
    imageCount= dialog.findViewById(R.id.imageCount)
    nameEditText = dialog.findViewById(R.id.editTextName)
    ageEditText = dialog.findViewById(R.id.editTextAge)

    weightEditText = dialog.findViewById(R.id.editTextWeight)
    heightEditText = dialog.findViewById(R.id.editTextHeight)
    DescriptionEditText = dialog.findViewById(R.id.editTextDescription)
    MolesEditText = dialog.findViewById(R.id.editTextMoles)
    LastSeenTimeEditText = dialog.findViewById(R.id.editLastSeenTime)
    LastSeenAddressEditText = dialog.findViewById(R.id.editLastSeenAddress)
    ClothingDescriptionEditText= dialog.findViewById(R.id.editClothingDescriptiom)
    PhysicalFeaturesEditText= dialog.findViewById(R.id.editPhysicalFeatures)
    BehaviourCharacteristicsEditText = dialog.findViewById(R.id.editBehaviourFeatures)
    MedicalInformationEditText = dialog.findViewById(R.id.editMedicalInformation)
    ContactInformationEditText=dialog.findViewById(R.id.editAlternateMobileNumber)


    // Initialize other fields as needed
    val selectImageButton: Button = dialog.findViewById(R.id.selectImageButton)
    selectImageButton.setOnClickListener {
        selectImages()



    }

    val submitButton: Button = dialog.findViewById(R.id.submitButton)
    submitButton.setOnClickListener {
        if (selectedImages.size<4){
            imageCount.text="Select at least 4 images"
            imageCount.setTextColor(Color.RED)
        }else {
            // Handle the click event on the submit button

            submitForm()
        }
    }
}
    private fun selectImages() {
        val mimeTypes = arrayOf("image/*")

        // Create an intent to open the file picker or gallery
        val intent = Intent(Intent.ACTION_GET_CONTENT)
//        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)

        imagePicker.launch(intent)
    }
    private fun handleImageSelection(data: Intent?) {
        if (data != null) {
            val images: List<Uri> = if (data.clipData != null) {
                // Multiple images selected
                (0 until data.clipData!!.itemCount).map {
                    data.clipData!!.getItemAt(it).uri
                }
            } else {
                // Single image selected
                listOf(data.data!!)
            }

            // Convert URIs to File objects and add them to the list
            images.forEach { uri ->
                uriToFile(this, uri).let { file ->
                    selectedImages.add(file)
                }
            }


            if (selectedImages.size<4){
                imageCount.text="Select at least 4 images : Only "+selectedImages.size+" Selected"
                imageCount.setTextColor(Color.RED)
            }
            else {
                imageCount.text = selectedImages.size.toString() + " images are selected"
                imageCount.setTextColor(Color.BLUE)
            }


            // Now, selectedImages contains a list of File objects representing the selected images
        }
    }






    private fun uriToFile(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = createTempFile(context)
        inputStream?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return file
    }

    private fun createTempFile(context: Context): File {
        return File(context.cacheDir, "temp_image_file_${System.currentTimeMillis()}")
    }


    private fun submitForm() {
        // Retrieve data from UI components
        val name = nameEditText.text.toString()
        val age = ageEditText.text.toString()
        val height=heightEditText.text.toString()

        val description=DescriptionEditText.text.toString()

        val weight=weightEditText.text.toString()

        val moles=MolesEditText.text.toString()

        val last_seen_time=LastSeenTimeEditText.text.toString()
        val last_seen_address=LastSeenAddressEditText.text.toString()
        val clothing_description=ClothingDescriptionEditText.text.toString()
        val physical_features=PhysicalFeaturesEditText.text.toString()
        val behaviourCharacteristics=BehaviourCharacteristicsEditText.text.toString()
        val medical_information=MedicalInformationEditText.text.toString()
        val alternate_mobile=ContactInformationEditText.text.toString()



        val url= Constants.FLASK_BASE_URL +"/report-missing-child"
        // Retrieve other fields as needed

        val sharedPreferences = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val sessionToken = sharedPreferences?.getString("sessionToken", null)
        var token=sessionToken?:"default"
        // Create an OkHttpClient instance
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        // Create a MultipartBody for the file upload
        val requestbody =  MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("token", token)  // Replace with your authentication token
            .addFormDataPart("name", name)
            .addFormDataPart("age", age)

            .addFormDataPart("height", height)

            .addFormDataPart("weight", weight)

            .addFormDataPart("description", description)

            .addFormDataPart("moles", moles)

            .addFormDataPart("last_seen_time", last_seen_time)

            .addFormDataPart("last_seen_location",last_seen_address )
            .addFormDataPart("clothing_description", clothing_description)
            .addFormDataPart("physical_features", physical_features)
            .addFormDataPart("behavioral_characteristics", behaviourCharacteristics)
            .addFormDataPart("medical_information", medical_information)
            .addFormDataPart("alternate_mobile", alternate_mobile)
            .apply {
                // Add image files to the request body
                selectedImages.forEach { imageFile: File ->
                    addFormDataPart(
                        "images",
                        imageFile.name,
                        imageFile.asRequestBody("image/*".toMediaType())
                    )
                }
            }
            .build()

        // Add other form fields as needed



// Build the final MultipartBody
//        val multipartBody = builder.build()
        // Create a request with the server URL and method
        val request = Request.Builder()
            .url(url)
            .post(requestbody)
            .build()

        // Make the network request asynchronously
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                // Handle the server response
                if (response.isSuccessful) {
                    // Process the successful response, e.g., show a success message

                        runOnUiThread{
                            Toast.makeText(
                                this@HomeActivity,
                                "Missing child report submitted successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                        }


                } else {
                    // Handle the unsuccessful response, e.g., show an error message
                        runOnUiThread {
                            Toast.makeText(
                                this@HomeActivity,
                                "Failed to submit missing child report",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                }
            }

            override fun onFailure(call: Call, e: IOException) {
                // Handle the network request failure, e.g., show an error message
                runOnUiThread {
                    Toast.makeText(
                        this@HomeActivity,
                        "Network request failed",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }
        })
    }



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

//                        userAddress.text = latitude.toString() + "," + longitude.toString()
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
//                                        userAddress.text=formatted_address.toString()
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
