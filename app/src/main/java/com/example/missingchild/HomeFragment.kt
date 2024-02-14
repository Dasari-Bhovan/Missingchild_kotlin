package com.example.missingchild

//

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.TimeUnit


class HomeFragment : Fragment(){
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
    private lateinit var heightEditText:EditText
    private lateinit var weightEditText:EditText
    private lateinit var DescriptionEditText:EditText

    private lateinit var MolesEditText:EditText

    private lateinit var LastSeenTimeEditText:EditText

    private lateinit var LastSeenAddressEditText:EditText
    private lateinit var ClothingDescriptionEditText:EditText
    private lateinit var PhysicalFeaturesEditText:EditText
    private lateinit var BehaviourCharacteristicsEditText:EditText
    private lateinit var MedicalInformationEditText:EditText
    private lateinit var ContactInformationEditText:EditText

    // Add other fields as needed

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize UI components
        nameEditText = view.findViewById(R.id.editTextName)
        ageEditText = view.findViewById(R.id.editTextAge)

        weightEditText = view.findViewById(R.id.editTextWeight)
        heightEditText = view.findViewById(R.id.editTextHeight)
        DescriptionEditText = view.findViewById(R.id.editTextDescription)
        MolesEditText = view.findViewById(R.id.editTextMoles)
        LastSeenTimeEditText = view.findViewById(R.id.editLastSeenTime)
        LastSeenAddressEditText = view.findViewById(R.id.editLastSeenAddress)
        ClothingDescriptionEditText= view.findViewById(R.id.editClothingDescriptiom)
        PhysicalFeaturesEditText= view.findViewById(R.id.editPhysicalFeatures)
        BehaviourCharacteristicsEditText = view.findViewById(R.id.editBehaviourFeatures)
        MedicalInformationEditText = view.findViewById(R.id.editMedicalInformation)
        ContactInformationEditText=view.findViewById(R.id.editAlternateMobileNumber)


        // Initialize other fields as needed
        val selectImageButton: Button = view.findViewById(R.id.selectImageButton)
        selectImageButton.setOnClickListener {
            selectImages()
        }
        val submitButton: Button = view.findViewById(R.id.submitButton)
        submitButton.setOnClickListener {
            // Handle the click event on the submit button
            submitForm()
        }

        return view
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
                uriToFile(requireContext(), uri).let { file ->
                    selectedImages.add(file)
                }
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

        val sharedPreferences = context?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
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
                    requireActivity().runOnUiThread {
                        Toast.makeText(
                            context,
                            "Missing child report submitted successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    // Handle the unsuccessful response, e.g., show an error message
                    requireActivity().runOnUiThread {
                        Toast.makeText(
                            context,
                            "Failed to submit missing child report",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                // Handle the network request failure, e.g., show an error message
                requireActivity().runOnUiThread {
                    Toast.makeText(
                        context,
                        "Network request failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }
}
