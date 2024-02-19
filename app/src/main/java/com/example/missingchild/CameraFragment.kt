package com.example.missingchild

import android.app.ProgressDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors



import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.IOException

import java.util.concurrent.TimeUnit


class CameraFragment : Fragment() {

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var imageCapture: ImageCapture
    private lateinit var optionsLayout: LinearLayout
    private lateinit var capturedImageView: ImageView
    private  lateinit var captureButton: Button
    private lateinit var image :File

    private  lateinit var child_details:TextView
    private var lat:String=""
    private var lon:String=""



    private lateinit var cameraSelector: CameraSelector
    private var lensFacing: Int = CameraSelector.LENS_FACING_BACK

    private var loadingDialog: ProgressDialog? = null

    private lateinit var cameraPreviewView: PreviewView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_camera, container, false)
        fetchLocation()

        // Initialize views
         captureButton = view.findViewById(R.id.captureButton)
        optionsLayout = view.findViewById(R.id.optionsLayout)
        capturedImageView = view.findViewById(R.id.capturedImageView)
        val captureButton: Button = view.findViewById(R.id.captureButton)
        val switchCameraButton: Button = view.findViewById(R.id.switchCameraButton)

         cameraPreviewView = view.findViewById(R.id.cameraPreviewView)
        child_details=view.findViewById(R.id.matched_child_details)

        // Set up CameraX
        startCamera()

        // Set up capture button click listener
        captureButton.setOnClickListener {
            captureImage()
        }
        switchCameraButton.setOnClickListener {
            switchCamera()
        }

        // Set up options buttons
        val correctButton: Button = view.findViewById(R.id.predictButton)
        val wrongButton: Button = view.findViewById(R.id.recaptureButton)

        correctButton.setOnClickListener {
            // Handle the "Correct" button click
            Toast.makeText(requireContext(), "Image sent to prediction", Toast.LENGTH_SHORT).show()
            uploadPhoto(image)
//            resetCapture()
        }


        wrongButton.setOnClickListener {
            // Handle the "Wrong" button click
//            Toast.makeText(requireContext(), "", Toast.LENGTH_SHORT).show()
            resetCapture()
        }

        return view
    }

    private fun startCamera() {
        // Set up cameraExecutor
        cameraExecutor = Executors.newSingleThreadExecutor()

        // Set up imageCapture
        val outputDirectory = getOutputDirectory()
        val outputFile = File(outputDirectory, "${System.currentTimeMillis()}.jpg")
        imageCapture = ImageCapture.Builder().build()

        // Bind the camera use cases to the lifecycle
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
//            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
            try {
                // Unbind any existing use cases before rebinding
                cameraProvider.unbindAll()

                // Bind the camera use cases to the preview
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(view?.findViewById<PreviewView>(R.id.cameraPreviewView)?.surfaceProvider)
                }

                cameraProvider.bindToLifecycle(viewLifecycleOwner, cameraSelector, preview, imageCapture)

            } catch (exc: Exception) {
                Log.e("CameraFragment", "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }
    private fun showLoadingDialog() {
        loadingDialog = ProgressDialog(requireContext())
        loadingDialog?.setMessage("Processing...")
        loadingDialog?.setCancelable(false)
        loadingDialog?.show()
    }

    private fun dismissLoadingDialog() {
        loadingDialog?.dismiss()
    }
    private fun captureImage() {
        val imageCapture = imageCapture ?: return

        // Create a timestamped file to save the image
        val outputDirectory = getOutputDirectory()
        val photoFile = File(outputDirectory, "${System.currentTimeMillis()}.jpg")

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            cameraExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e("CameraFragment", "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    // Show the options layout after capturing the image
//                    optionsLayout.visibility = View.VISIBLE

                    // Display the captured image in the ImageView
//                    capturedImageView.setImageURI(photoFile.toUri())
                    activity?.runOnUiThread {
                        // Show the options layout after capturing the image
                        optionsLayout.visibility = View.VISIBLE
                        capturedImageView.visibility=View.VISIBLE
                        cameraPreviewView.visibility=View.GONE
                        captureButton.visibility=View.GONE


                        // Load the captured image into the ImageView
                        image=photoFile
                        capturedImageView.setImageURI(Uri.fromFile(photoFile))
                    }
                }
            }
        )
    }

    private fun uploadPhoto(imageFile: File) {
        showLoadingDialog()

        // Set up OkHttp client
        val client = OkHttpClient.Builder()
            .connectTimeout(300, TimeUnit.SECONDS)
            .readTimeout(300, TimeUnit.SECONDS)
            .writeTimeout(300, TimeUnit.SECONDS)
            .build()
        val sharedPreferences = context?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val sessionToken = sharedPreferences?.getString("sessionToken", null)
        var token=sessionToken?:"default"

//        fetchLocation()
        // Request body containing the image file
        val requestBody: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("photo", imageFile.name, imageFile.asRequestBody("image/*".toMediaTypeOrNull()))
            .addFormDataPart("token", token)
            .addFormDataPart("lat",lat)
            .addFormDataPart("lon",lon)
            .build()

        val url1= Constants.FLASK_BASE_URL +"/predict"

        // Request object
        val request: Request = Request.Builder()
            .url(url1) // Replace with your server URL
            .post(requestBody)
            .build()

        // Asynchronous call using OkHttp
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
//                dismissLoadingDialog()
//                resetCapture()

                // Handle failure (e.g., network error)
                CoroutineScope(Dispatchers.Main).launch {
                    dismissLoadingDialog()
                    println(e.message)
                    Toast.makeText(requireContext(), "Failed to upload photo", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                // Handle successful response
                val responseBody = response.body?.string()
//                                val hi=JSONObject(responseBody)
//                                userAddress.text
                responseBody?.let {
                    val jsonObject = JSONObject(it)
                    val message=jsonObject.getString("message")
                    if (message.equals("No Face matched with the records")){
                        child_details.text="Image not Found \n Try again in better lighting conditions\n Make sure photo is clear and not blur"
                    }
                    else {
                        val name = jsonObject.getString("name")
                        val mobile = jsonObject.getString("parent_mobile")
                        val description = jsonObject.getString("child_description")
                        val age = jsonObject.getString("age")
//                                        println("Address Line 1: $formatted_address")
                        child_details.text =
                            "Miissing child details :\n\tChild Name : " + name + "\n\tChild age : " + age + "\n\tDescription : " + description + "\n\tContact their parents at   " + mobile
                    }
//
                CoroutineScope(Dispatchers.Main).launch {
                    dismissLoadingDialog()
                    Toast.makeText(requireContext(), "Photo recognized success", Toast.LENGTH_SHORT).show()
                }

            }}
        }
        )
    }

    private fun resetCapture() {
        // Hide the options layout
        optionsLayout.visibility = View.GONE
        cameraPreviewView.visibility=View.VISIBLE
        captureButton.visibility=View.VISIBLE
        // Clear the captured image in the ImageView
        capturedImageView.setImageURI(null)

        // Restart the camera preview
        startCamera()
    }
    private fun switchCamera() {
        lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
            CameraSelector.LENS_FACING_FRONT
        } else {
            CameraSelector.LENS_FACING_BACK
        }
        startCamera()
    }
    private fun fetchLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    // Got last known location. In some rare situations, this can be null.
                    if (location != null) {
                        lat= location.latitude.toString()
                        lon = location.longitude.toString()
                    }
                }.addOnFailureListener { e ->
                    // Handle failure to get location
                    e.printStackTrace()
                }
        } catch (ex: SecurityException) {
            ex.printStackTrace()
        }
    }


    private fun getOutputDirectory(): File {
        val mediaDir = requireContext().externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else requireContext().filesDir
    }
}
