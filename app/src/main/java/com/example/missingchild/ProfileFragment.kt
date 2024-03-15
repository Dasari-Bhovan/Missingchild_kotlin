import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.compose.ui.Modifier
import androidx.fragment.app.Fragment
import com.example.missingchild.Constants
import com.example.missingchild.LoginActivity
import com.example.missingchild.R
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.handleCoroutineException
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.net.URL

class ProfileFragment : Fragment() {

    private lateinit var client: OkHttpClient
    private lateinit var updateProfileButton: Button
    private lateinit var shapableImage: ShapeableImageView
    private lateinit var shapableImage1: ShapeableImageView
    val IMAGE_PICK_REQUEST_CODE=2
    var fullName:String=""
    var age:String=""
    var pheight:String=""
    var weight:String=""
    var phoneNumber:String=""
    var profile_url:String=""
    lateinit var base64:String
    var email:String=""
    var address:String=""
    var pincode:String=""
    var occupation:String=""
    private var uid:String=""
    var gender1:String=""

    private lateinit var editFullName:EditText

    private lateinit var editAge:EditText

    private lateinit var editHeight:EditText
    private lateinit var editWeight:EditText
    private lateinit var editPhone:EditText
    private lateinit var editGender: Spinner
    private lateinit var editAddress:EditText
    private lateinit var editPincode:EditText
    private lateinit var editOccupation:EditText

    private lateinit var viewEmail:TextView
    lateinit var genderSpinner:Spinner

    lateinit var logoutButton: Button






    private lateinit var changePictureButton: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.temp_profile, container, false)

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri: Uri? = data.data
            if (selectedImageUri != null) {
                uploadImage(selectedImageUri)
            }
        }

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        client = OkHttpClient()
        genderSpinner=view.findViewById(R.id.genderSpinner)
        logoutButton=view.findViewById(R.id.logoutButton)
        // Fetch user profile data

        fetchUserProfile()

        genderSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                gender1 = parent.getItemAtPosition(position).toString()
                // Do something with the selected item
                Log.d("SpinnerSelection", "Selected item: $gender1")
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        shapableImage=view.findViewById(R.id.shapableImageViewreport)
//        shapableImage1=view.findViewById(R.id.shapableImageViewreport1)
//        https://res.cloudinary.com/dognhm5vd/image/upload/v1708113375/profile/NKfotAlsifZ1eqa5QhRmfBkoOMV2.png.jpg
//        val url = URL("https://res.cloudinary.com/dognhm5vd/image/upload/v1708113375/profile/${email}.png.jpg")
//        val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
//        val circularBitmap = getCircularBitmap(bmp)

//        shapableImage.setImageBitmap(circularBitmap)
        updateProfileButton=view.findViewById(R.id.updateButton)
        changePictureButton=view.findViewById(R.id.changePictureButton)

        editFullName=view.findViewById(R.id.editName)
        viewEmail=view.findViewById(R.id.editEmail)
        editAge=view.findViewById(R.id.editAge)
        editHeight=view.findViewById(R.id.editHeight)
        editWeight=view.findViewById(R.id.editWeight)
        editGender=view.findViewById(R.id.genderSpinner)
        editPhone=view.findViewById(R.id.editPhone)
        editAddress=view.findViewById(R.id.editAddress)
        editPincode=view.findViewById(R.id.editPincode)
        editOccupation=view.findViewById(R.id.editOccupation)


        // Set click listener for update profile button
        updateProfileButton.setOnClickListener {
            fullName=editFullName.text.toString()
            age=editAge.text.toString()
            pincode=editPincode.text.toString()
            occupation=editOccupation.text.toString()
            address=editAddress.text.toString()
            pheight=editHeight.text.toString()
            phoneNumber=editPhone.text.toString()
            weight=editWeight.text.toString()




            updateProfile(fullName=fullName, age = age, pincode = pincode,
                occupation = occupation, address = address, phoneNumber = phoneNumber, weight = weight,
                height = pheight, gender = gender1)

        }

        changePictureButton.setOnClickListener {
            openImagePicker()
        }

        logoutButton.setOnClickListener{
            val sharedPreferences = context?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val sessionToken = sharedPreferences?.getString("sessionToken", null) ?: "default"
            makeLogoutRequest(sessionToken.toString())
        }

    }
    private fun makeLogoutRequest(sessionToken:String) {
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
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

                activity?.runOnUiThread {
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
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()

    }

    private fun getCircularBitmap(bitmap: Bitmap): Bitmap {
        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(output)
        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)

        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = Color.BLACK
        canvas.drawCircle(bitmap.width / 2f, bitmap.height / 2f, bitmap.width / 0.5f, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)

        return output
    }


    private fun fetchUserProfile() {
        val url = Constants.FLASK_BASE_URL + "/profile"
        val sharedPreferences = context?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val sessionToken = sharedPreferences?.getString("sessionToken", null) ?: "default"

        val request = Request.Builder()
            .url(url)
            .put(FormBody.Builder()
                .add("token", sessionToken)
                .build())
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    println(response)
                    val responseBody = response.body?.string()
                    responseBody?.let {
                        val jsonObject = JSONObject(it)
                        fullName=jsonObject.getString("full_name")
                        uid=jsonObject.getString("uid")
                        age=jsonObject.optString("age")
                        email=jsonObject.getString("email")
                        phoneNumber=jsonObject.optString("phone_number")
//                        age=jsonObject.optString("age").toInt()
                        pheight=jsonObject.optString("height")
                        weight=jsonObject.optString("weight")
                        profile_url=jsonObject.optString("profile_picture_url")

                        occupation=jsonObject.optString("occupation")
                        address=jsonObject.optString("address")
                        pincode=jsonObject.optString("pincode")
                        var gender = jsonObject.optString("gender")

                        activity?.runOnUiThread{
                            editFullName.setText(fullName)
                            viewEmail.text=email
                            editPhone.setText(phoneNumber)
                            editOccupation.setText(occupation)
                            if (gender=="Male"){
                                editGender.setSelection(1)
                            }
                            else if(gender=="Female"){
                                editGender.setSelection(2)
                            }
//                            editAge.setText(age)
                            editAge.setText(age.toString())
                            editHeight.setText(pheight)
                            editWeight.setText(weight)
                            editPincode.setText(pincode)
                            editAddress.setText(address)
//        https://res.cloudinary.com/dognhm5vd/image/upload/v1708113375/profile/NKfotAlsifZ1eqa5QhRmfBkoOMV2.png.jpg
                            if(profile_url!="") {
                                val url = URL(profile_url)
                                val bmp = BitmapFactory.decodeStream(
                                    url.openConnection().getInputStream()
                                )
                                val circularBitmap = getCircularBitmap(bmp)


                                shapableImage.setImageBitmap(circularBitmap)

                            }
                    }

                    }
                    // Parse the JSON response body
                // and update UI with user profile data
                } else {
                    // Handle unsuccessful response
                }

            }

            override fun onFailure(call: Call, e: IOException) {
                // Handle failure
            }
        })
    }

//    private fun updateProfile() {
//        val sharedPreferences = context?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
//        val token = sharedPreferences?.getString("sessionToken", null) ?: "default" // Replace with the user token obtained during login
////        val photoFile = File("path_to_your_photo_file") // Replace with the actual path to the photo file
//        val request = Request.Builder()
//            .url("your_update_profile_url")
//            .post(MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//                .addFormDataPart("token", token)
////                .addFormDataPart("photo", photoFile.name, photoFile.asRequestBody())
//                .addFormDataPart("other_parameters", "parameter_values") // Add other parameters as needed
//                .build())
//            .build()
//
//        client.newCall(request).enqueue(object : Callback {
//            override fun onResponse(call: Call, response: Response) {
//                if (response.isSuccessful) {
//                    // Profile updated successfully
//                } else {
//                    // Handle unsuccessful response
//                }
//            }
//
//            override fun onFailure(call: Call, e: IOException) {
//                // Handle failure
//            }
//        })
//    }
//}
private fun updateProfile(phoneNumber: String, fullName: String,gender:String,height:String,pincode:String
,weight:String,age:String,address:String,occupation:String) {
    val url = Constants.FLASK_BASE_URL +"/update_profile" // Replace with your Flask register endpoint URL
    val sharedPreferences = context?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    val sessionToken = sharedPreferences?.getString("sessionToken", null) ?: "default"
//    val apiUrl = Constants.FLASK_BASE_URL +"/get_reports" // Replace with your actual API endpoint
    val urlBuilder = StringBuilder(url)
    urlBuilder.append("?")
    println("request sent")
    urlBuilder.append("token=$sessionToken&")
    // Append query parameters for search and filtering
    if (!phoneNumber.isNullOrEmpty()) {
        urlBuilder.append("phone_number=$phoneNumber&")
    }
    if (!gender.isNullOrEmpty()) {
        urlBuilder.append("gender=$gender&")
    }
    if (!fullName.isNullOrEmpty()) {
        urlBuilder.append("full_name=$fullName&")
    }
    if (!age.toString().isNullOrEmpty()) {
        urlBuilder.append("age=$age&")
    }
    if (!pincode.toString().isNullOrEmpty()) {
        urlBuilder.append("pincode=$pincode&")
    }
    if (!address.toString().isNullOrEmpty()) {
        urlBuilder.append("address=$address&")
    }
    if (!height.toString().isNullOrEmpty()) {
        urlBuilder.append("height=$pheight&")
    }
    if (!weight.toString().isNullOrEmpty()) {
        urlBuilder.append("weight=$weight&")
    }
    if (!occupation.isNullOrEmpty()) {
        urlBuilder.append("occupation=$occupation&")
    }




    // Append other query parameters similarly

    // Remove trailing '&' if present
    if (urlBuilder.endsWith("&")) {
        urlBuilder.deleteCharAt(urlBuilder.length - 1)
    }
    val response = URL(urlBuilder.toString()).readText()
    if (!response.isNullOrEmpty()){
        fetchUserProfile()
        Toast.makeText(requireContext(),"Profile updated successfully",Toast.LENGTH_SHORT).show()
    }

//    val formBody = FormBody.Builder()
//        .add("token", sessionToken)
//        .add("phone_number", phoneNumber)
//        .add("full_name", fullName)
//        .add("pincode", pincode.toString())
//        .add("gender",gender)
//        .add("height",height)
//        .add("weight",weight)
//        .add("age", age.toString())
//        .add("address",address)
//        .add("occupation",occupation)
//        .build()
//
//    val client = OkHttpClient()
//
//    val request = Request.Builder()
//        .url(url)
//        .post(formBody)
//        .addHeader("Content-Type", "application/x-www-form-urlencoded")
//        .build()
//
//    client.newCall(request).enqueue(object : Callback {
//        override fun onFailure(call: Call, e: IOException) {
//            e.printStackTrace()
//
//        }
//
//        override fun onResponse(call: Call, response: Response) {
//            val responseData = response.body?.string()
//
//            // Process the response
//            // Display a Toast or update UI based on registration success or failure
//            if (response.isSuccessful) {
//                Log.d(TAG, "Profile uploaded successfully")
////                val responseBody = response.body?.string()
////                responseBody?.let {
////                    val jsonObject = JSONObject(it)
////                    base64 = jsonObject.getString("base64")
////                }
//
////
//                fetchUserProfile()
             else {
                Toast.makeText(requireContext(),"Profile not updated",Toast.LENGTH_SHORT).show()
            }
//        }
//    })
}
    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//        uploadImage(intent)
        startActivityForResult(intent, IMAGE_PICK_REQUEST_CODE)

    }

    private fun uploadImage(imageUri: Uri) {
        val file = File(getRealPathFromURI(imageUri))
        val sharedPreferences = context?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val sessionToken = sharedPreferences?.getString("sessionToken", null)
        var token=sessionToken?:"default"
        val url=Constants.FLASK_BASE_URL+"/profile_img"
       val requestBody: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("photo", file.name, file.asRequestBody("image/*".toMediaTypeOrNull()))
            .addFormDataPart("token", token)
            .build()
        val request: Request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle failure
//                Log.e(TAG, "Failed to upload image: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                // Handle successful response
                Log.d(TAG, "Image uploaded successfully")
                fetchUserProfile()
                // You can update the UI or perform any other actions here
            }
        })
    }

    private fun getRealPathFromURI(uri: Uri): String {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = requireActivity().contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            it.moveToFirst()
            return it.getString(columnIndex)
        }
        return ""
    }

}
