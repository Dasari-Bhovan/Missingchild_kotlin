package com.example.missingchild

//

import android.app.Activity
import android.app.Dialog
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.location.LocationManager
import android.net.Uri

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import java.net.URL
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.android.gms.location.LocationServices
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.TimeUnit
import android.provider.Settings
import androidx.appcompat.app.AlertDialog

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.view.Gravity
import android.view.Window
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.Spinner
import androidx.core.content.getSystemService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.material.imageview.ShapeableImageView

import android.content.ContentValues.TAG

import android.content.Context
import android.widget.LinearLayout
import androidx.camera.core.impl.utils.ContextUtil.getApplicationContext
import androidx.compose.ui.text.android.TextLayout
import androidx.compose.ui.text.toLowerCase
import com.github.mikephil.charting.charts.BarChart
import java.io.FileNotFoundException
import java.lang.reflect.InvocationTargetException



import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;
import org.json.JSONArray


class HomeFragment : Fragment() {
    private lateinit var locationText: TextView
    private lateinit var lineChart: LineChart
    private lateinit var yearbarChart: BarChart
    private lateinit var pieChart: PieChart
    private lateinit var client: OkHttpClient
    private lateinit var updateProfileButton: Button
    private lateinit var shapableImage: ShapeableImageView
    private lateinit var totalCountText: TextView
    private lateinit var nameEditText1: EditText
    private lateinit var mailEditText1: EditText
    private lateinit var phoneEditText1: EditText
    lateinit var relationSpinner1: Spinner
    private lateinit var nameEditText2: EditText
    private lateinit var mailEditText2: EditText

    private lateinit var imageCount: TextView
    private lateinit var phoneEditText2: EditText
    lateinit var relationSpinner2: Spinner

    lateinit var totalSpinner: Spinner
    lateinit var yearSpinner: Spinner

    lateinit var pieOptionSpinner:Spinner
     var latitude:Double = 0.0
    var longitude:Double = 0.0

    lateinit var locationSpinner:Spinner

    lateinit var legendyearLayout: LinearLayout

    var pieOption:String=""


    var name1: String = ""

    var email1: String = ""
    var phone1: String = ""
    var relation1: String = ""

    var name2: String = ""
    var email2: String = ""
    var phone2: String = ""
    var relation2: String = ""

    var graph1:String=""
    var graph_year:String=""

    var countArray = mutableListOf<Int>()
    var yArray= mutableListOf<String>()

    var countyearArray = mutableListOf<Int>()
    var yyearArray= mutableListOf<String>()

    private lateinit var foundCountText: TextView

    private lateinit var addEmergencyText: TextView

    private lateinit var profileNameText: TextView
    private lateinit var notFoundCountText: TextView
    val IMAGE_PICK_REQUEST_CODE = 2
    var fullName: String = ""
    var totalCount: String = ""
    var profile_url: String = ""
    var foundCount: String = ""


//    private val imagePicker =
//        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//            if (result.resultCode == Activity.RESULT_OK) {
//                val data: Intent? = result.data
//                handleImageSelection(data)
//            }
//        }
//
//    private val selectedImages = mutableListOf<File>()
//    private lateinit var nameEditText: EditText
//    private lateinit var ageEditText: EditText
//    private val PICK_IMAGES_REQUEST = 1
//    private lateinit var heightEditText:EditText
//    private lateinit var weightEditText:EditText
//    private lateinit var DescriptionEditText:EditText
//
//    private lateinit var MolesEditText:EditText
//
//    private lateinit var LastSeenTimeEditText:EditText
//
//    private lateinit var LastSeenAddressEditText:EditText
//    private lateinit var ClothingDescriptionEditText:EditText
//    private lateinit var PhysicalFeaturesEditText:EditText
//    private lateinit var BehaviourCharacteristicsEditText:EditText
//    private lateinit var MedicalInformationEditText:EditText
//    private lateinit var ContactInformationEditText:EditText

    // Add other fields as needed


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        client = OkHttpClient()
        val view = inflater.inflate(R.layout.temp_home_fragment, container, false)
        locationText = view.findViewById(R.id.location)

        foundCountText = view.findViewById(R.id.FoundCount)
        shapableImage = view.findViewById(R.id.shapableImageViewreport)

        totalCountText = view.findViewById(R.id.TotalCount)

        notFoundCountText = view.findViewById(R.id.NotFoundCount)
        profileNameText = view.findViewById(R.id.profileName)
        addEmergencyText = view.findViewById(R.id.addEmergencyContactText)
        addEmergencyText.setOnClickListener {
            showAddEmergencyDialog()
        }

        locationText.setOnClickListener{
            if (latitude!=0.0 && longitude!=0.0) {
                val intent = Intent(requireContext(), MapActivity::class.java)

                intent.putExtra("lat", latitude)
                intent.putExtra("lon", longitude)
                startActivity(intent)
                println(latitude.toString() + " " + longitude.toString())
            }
            else{
                buildAlertMessageNoGps()
            }
        }


        totalSpinner=view.findViewById(R.id.totalSpinner)
        yearSpinner=view.findViewById(R.id.yearSpinner)
        pieChart=view.findViewById(R.id.pieMatchingChart)
        pieOptionSpinner=view.findViewById(R.id.categorySpinner)
        pieOptionSpinner.setSelection(0)
        pieOption="all"
        totalSpinner.setSelection(1)
        graph1="Year"
        totalSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                graph1 = parent.getItemAtPosition(position).toString()
                fetchGraphData()
                // Do something with the selected item
                Log.d("SpinnerSelection", "Selected option : $graph1")
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        fetchpieData()

        pieOptionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                pieOption = parent.getItemAtPosition(position).toString()
                fetchpieData()
                // Do something with the selected item
                Log.d("SpinnerSelection", "Selected option : $pieOption")
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        yearSpinner.setSelection(1)
        graph_year="2020"
        yearSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                graph_year = parent.getItemAtPosition(position).toString()
                fetchGraphYearData()
                // Do something with the selected item
                Log.d("SpinnerSelection", "Selected option : $graph_year")
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }


        lineChart = view.findViewById(R.id.lineChart);
        lineChart.setTouchEnabled(true);
        lineChart.setPinchZoom(true);
        fetchGraphData()


        yearbarChart=view.findViewById(R.id.barChart)
        yearbarChart.setTouchEnabled(true)
        yearbarChart.setPinchZoom(true)
        fetchGraphYearData()
//        renderData()



        val manager = requireActivity().getSystemService(LOCATION_SERVICE) as LocationManager
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps()
            fetchLocation()
        }
        if (!isNetworkAvailable()) {
            buildAlertMessageNoInternet()
        }


        fetchLocation()
        fetchProfile_Count()
        var text: TextView
        // Initialize UI components
//        nameEditText = view.findViewById(R.id.editTextName)
//        ageEditText = view.findViewById(R.id.editTextAge)
//
//        weightEditText = view.findViewById(R.id.editTextWeight)
//        heightEditText = view.findViewById(R.id.editTextHeight)
//        DescriptionEditText = view.findViewById(R.id.editTextDescription)
//        MolesEditText = view.findViewById(R.id.editTextMoles)
//        LastSeenTimeEditText = view.findViewById(R.id.editLastSeenTime)
//        LastSeenAddressEditText = view.findViewById(R.id.editLastSeenAddress)
//        ClothingDescriptionEditText= view.findViewById(R.id.editClothingDescriptiom)
//        PhysicalFeaturesEditText= view.findViewById(R.id.editPhysicalFeatures)
//        BehaviourCharacteristicsEditText = view.findViewById(R.id.editBehaviourFeatures)
//        MedicalInformationEditText = view.findViewById(R.id.editMedicalInformation)
//        ContactInformationEditText=view.findViewById(R.id.editAlternateMobileNumber)
//
//
//        // Initialize other fields as needed
//        val selectImageButton: Button = view.findViewById(R.id.selectImageButton)
//        selectImageButton.setOnClickListener {
//            selectImages()
//        }
//        val submitButton: Button = view.findViewById(R.id.submitButton)
//        submitButton.setOnClickListener {
//            // Handle the click event on the submit button
//            submitForm()
//        }

        return view
    }
    private fun renderData(){
        val entries = arrayListOf<Entry>()
        for(i in 0 until countArray.size){
//            println(i.toFloat())
            entries.add(Entry(i.toFloat(), countArray.get(i).toFloat()))
        }
//        entries.add(Entry(1f, 20f))
//        entries.add(Entry(2f, 30f))
//        entries.add(Entry(3f, 25f))
//        entries.add(Entry(4f, 35f))
//        entries.add(Entry(5f, 40f))
//
//        entries.add(Entry(6f, 48f))
//
//        entries.add(Entry(8f, 40f))
//        entries.add(Entry(9f, 30f))
//        entries.add(Entry(10f, 25f))
//        entries.add(Entry(11f, 20f))
//        entries.add(Entry(12f, 20f))
//        entries.add(Entry(13f, 10f))


        val dataSet = LineDataSet(entries, "Total children registered")
        dataSet.color = Color.BLUE
        dataSet.valueTextColor = Color.BLACK
        dataSet.label

        val lineData = LineData(dataSet)
        lineChart.data = lineData

        lineChart.description.isEnabled = false

        val xAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.axisMinimum= 0F
        xAxis.axisMaximum=yArray.size.toFloat()-1

        xAxis.setLabelCount(yArray.size,true)
        xAxis.valueFormatter = LabelFormatterX(yArray)


//        val yAxis=lineChart.yAxis

        val leftAxis = lineChart.axisLeft
        leftAxis.enableGridDashedLine(10f, 10f, 0f)

        val rightAxis = lineChart.axisRight
        rightAxis.isEnabled = false

        lineChart.invalidate()
    }

    private fun renderBarData(){

            val barEntries = arrayListOf<BarEntry>()
            for(i in 0 until countyearArray.size) {
//            println(i.toFloat())
                barEntries.add(BarEntry(i.toFloat(), countyearArray.get(i).toFloat()))
            }
//            val barDataSet = BarDataSet(barEntries, "Year Wise distribution")
//        barDataSet.color = Color.BLUE


        yearbarChart.description.isEnabled = false
        val colorNames = listOf(
            "Red", "Green", "Blue", "Yellow", "Gray",
            "Cyan", "Magenta", "Black", "Dark Gray", "Light Gray",
            "White", "Orange"
        )
        val colors = listOf(
            Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.GRAY,
            Color.CYAN, Color.MAGENTA, Color.BLACK, Color.DKGRAY, Color.LTGRAY,
            Color.WHITE, Color.rgb(255, 165, 0) // Orange color
        )

        val barDataSets = ArrayList<IBarDataSet>()
        for (i in barEntries.indices) {
            val barDataSet = BarDataSet(arrayListOf(barEntries[i]), yyearArray.get(i))
            barDataSet.color = colors[i]
            barDataSet.valueTextColor = Color.BLACK
            barDataSets.add(barDataSet)
        }

        val barData = BarData(barDataSets)
        yearbarChart.data = barData

//        val barDataSet = BarDataSet(barEntries, "Year Wise distribution")
//        barDataSet.color = Color.BLUE
//        activity?.runOnUiThread {
//
//            legendyearLayout.removeAllViews()
//        }
//            for (i in yyearArray.indices) {
//                activity?.runOnUiThread {
//
//                val colorView = TextView(requireContext())
//                colorView.width = 50 // Adjust the width of the colored rectangle as needed
//                colorView.height = 50 // Adjust the height of the colored rectangle as needed
//                colorView.setBackgroundColor(colors[i])
//
//                val textView = TextView(requireContext())
//                textView.text = yyearArray[i]
//                textView.setTextColor(Color.BLACK)
//
//                val legendItemLayout = LinearLayout(requireContext())
//
//
//
//                legendItemLayout.orientation = LinearLayout.HORIZONTAL
//                legendItemLayout.addView(colorView)
//                legendItemLayout.addView(textView)
//
//                legendyearLayout.addView(legendItemLayout)
//            }
//        }


        val xAxis = yearbarChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
//        xAxis.granularity = 1f;
//        xAxis.isGranularityEnabled = true;
        xAxis.setDrawGridLines(false)
        xAxis.setLabelCount(yyearArray.size,true)
//        xAxis.setCenterAxisLabels(true);
        xAxis.valueFormatter = IndexAxisValueFormatter(yyearArray)


        val leftAxis = yearbarChart.axisLeft
        leftAxis.enableGridDashedLine(10f, 10f, 0f)

        val rightAxis = yearbarChart.axisRight
        rightAxis.isEnabled = false

        yearbarChart.invalidate()
          }

    private fun renderPieData(){

        val pieEntries = arrayListOf<PieEntry>()
        for(i in 0 until countArray.size) {
//            println(i.toFloat())
            pieEntries.add(PieEntry(countArray.get(i).toFloat(),yArray.get(i)))
        }
//
        val colorNames = listOf(
            "Red", "Green", "Blue", "Yellow", "Gray",
            "Cyan", "Magenta", "Black", "Dark Gray", "Light Gray",
            "White", "Orange"
        )
        val colors = listOf(
            Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.GRAY,
            Color.CYAN, Color.MAGENTA, Color.BLACK, Color.DKGRAY, Color.LTGRAY,
            Color.WHITE, Color.rgb(255, 165, 0) // Orange color
        )

   val pieDataSet = PieDataSet(pieEntries, "Pie Chart")
        pieDataSet.colors = colors
        pieDataSet.valueTextColor = Color.BLACK
        pieDataSet.valueTextSize = 12f

        val pieData = PieData(pieDataSet)
        pieChart.data = pieData
        pieChart.setTouchEnabled(true)




        pieChart.description.isEnabled = false
        pieChart.isDrawHoleEnabled = true
        pieChart.setHoleColor(Color.TRANSPARENT)
        pieChart.setTransparentCircleColor(Color.WHITE)
        pieChart.setTransparentCircleAlpha(110)
        pieChart.setHoleRadius(50f)
        pieChart.setTransparentCircleRadius(55f)
        pieChart.setDrawEntryLabels(false)
        pieChart.legend.isEnabled = true
//        pieChart.animateY(1400)
        pieChart.invalidate()
    }
    private fun showAddEmergencyDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.addemergencysheet)

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
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//    dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)
        nameEditText1 = dialog.findViewById(R.id.editTextName1)
        mailEditText1 = dialog.findViewById(R.id.editEmail1)

        phoneEditText1 = dialog.findViewById(R.id.editMobileNumber1)
        relationSpinner1 = dialog.findViewById(R.id.relationSpinner1)

        nameEditText2 = dialog.findViewById(R.id.editTextName2)
        mailEditText2 = dialog.findViewById(R.id.editEmail2)



        phoneEditText2 = dialog.findViewById(R.id.editMobileNumber2)
        relationSpinner2 = dialog.findViewById(R.id.relationSpinner2)



        relationSpinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                relation1 = parent.getItemAtPosition(position).toString()
                // Do something with the selected item
                Log.d("SpinnerSelection", "Selected relation: $relation1")
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }
        relationSpinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                relation2 = parent.getItemAtPosition(position).toString()
                // Do something with the selected item
                Log.d("SpinnerSelection", "Selected relation: $relation2")
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }




        // Initialize other fields as needed

        val submitButton: Button = dialog.findViewById(R.id.submitButton)
        submitButton.setOnClickListener {
            // Handle the click event on the submit button
            try {
                // Your code that may throw exceptions
                submitForm() // Example function call that may throw exceptions
            } catch (e: RuntimeException) {
                // Handle RuntimeException
                e.printStackTrace() // Example: printing stack trace
            } catch (e: InvocationTargetException) {
                // Handle InvocationTargetException
                e.printStackTrace() // Example: printing stack trace
            } catch (e: FileNotFoundException) {
                // Handle FileNotFoundException
                e.printStackTrace() // Example: printing stack trace
            } catch (e: Exception) {
                // Catch any other exceptions not caught above
                e.printStackTrace() // Example: printing stack trace
            }
        }
        fetchUserEmergencyContacts()
    }

    //    private fun selectImages() {
//        val mimeTypes = arrayOf("image/*")
//
//        // Create an intent to open the file picker or gallery
//        val intent = Intent(Intent.ACTION_GET_CONTENT)
////        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
//        intent.type = "image/*"
//        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
//
//        imagePicker.launch(intent)
//    }
//    private fun handleImageSelection(data: Intent?) {
//        if (data != null) {
//            val images: List<Uri> = if (data.clipData != null) {
//                // Multiple images selected
//                (0 until data.clipData!!.itemCount).map {
//                    data.clipData!!.getItemAt(it).uri
//                }
//            } else {
//                // Single image selected
//                listOf(data.data!!)
//            }
//
//            // Convert URIs to File objects and add them to the list
//            images.forEach { uri ->
//                uriToFile(requireContext(), uri).let { file ->
//                    selectedImages.add(file)
//                }
//            }
//
//            // Now, selectedImages contains a list of File objects representing the selected images
//        }
//    }
//
//
//
//
//
//
//    private fun uriToFile(context: Context, uri: Uri): File {
//        val inputStream = context.contentResolver.openInputStream(uri)
//        val file = createTempFile(context)
//        inputStream?.use { input ->
//            file.outputStream().use { output ->
//                input.copyTo(output)
//            }
//        }
//        return file
//    }
//
//    private fun createTempFile(context: Context): File {
//        return File(context.cacheDir, "temp_image_file_${System.currentTimeMillis()}")
//    }
//
//
//    private fun submitForm() {
//        // Retrieve data from UI components
//        val name = nameEditText.text.toString()
//        val age = ageEditText.text.toString()
//        val height=heightEditText.text.toString()
//
//        val description=DescriptionEditText.text.toString()
//
//        val weight=weightEditText.text.toString()
//
//        val moles=MolesEditText.text.toString()
//
//        val last_seen_time=LastSeenTimeEditText.text.toString()
//        val last_seen_address=LastSeenAddressEditText.text.toString()
//        val clothing_description=ClothingDescriptionEditText.text.toString()
//        val physical_features=PhysicalFeaturesEditText.text.toString()
//        val behaviourCharacteristics=BehaviourCharacteristicsEditText.text.toString()
//        val medical_information=MedicalInformationEditText.text.toString()
//        val alternate_mobile=ContactInformationEditText.text.toString()
//
//
//
//        val url= Constants.FLASK_BASE_URL +"/report-missing-child"
//        // Retrieve other fields as needed
//
//        val sharedPreferences = context?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
//        val sessionToken = sharedPreferences?.getString("sessionToken", null)
//        var token=sessionToken?:"default"
//        // Create an OkHttpClient instance
//        val client = OkHttpClient.Builder()
//            .connectTimeout(30, TimeUnit.SECONDS)
//            .readTimeout(30, TimeUnit.SECONDS)
//            .writeTimeout(30, TimeUnit.SECONDS)
//            .build()
//
//        // Create a MultipartBody for the file upload
//        val requestbody =  MultipartBody.Builder()
//            .setType(MultipartBody.FORM)
//            .addFormDataPart("token", token)  // Replace with your authentication token
//            .addFormDataPart("name", name)
//            .addFormDataPart("age", age)
//
//            .addFormDataPart("height", height)
//
//            .addFormDataPart("weight", weight)
//
//            .addFormDataPart("description", description)
//
//            .addFormDataPart("moles", moles)
//
//            .addFormDataPart("last_seen_time", last_seen_time)
//
//            .addFormDataPart("last_seen_location",last_seen_address )
//            .addFormDataPart("clothing_description", clothing_description)
//            .addFormDataPart("physical_features", physical_features)
//            .addFormDataPart("behavioral_characteristics", behaviourCharacteristics)
//            .addFormDataPart("medical_information", medical_information)
//            .addFormDataPart("alternate_mobile", alternate_mobile)
//            .apply {
//                // Add image files to the request body
//                selectedImages.forEach { imageFile: File ->
//                    addFormDataPart(
//                        "images",
//                        imageFile.name,
//                        imageFile.asRequestBody("image/*".toMediaType())
//                    )
//                }
//            }
//            .build()
//
//            // Add other form fields as needed
//
//
//
//// Build the final MultipartBody
////        val multipartBody = builder.build()
//        // Create a request with the server URL and method
//        val request = Request.Builder()
//            .url(url)
//            .post(requestbody)
//            .build()
//
//        // Make the network request asynchronously
//        client.newCall(request).enqueue(object : Callback {
//            override fun onResponse(call: Call, response: Response) {
//                // Handle the server response
//                if (response.isSuccessful) {
//                    // Process the successful response, e.g., show a success message
//                    requireActivity().runOnUiThread {
//                        Toast.makeText(
//                            context,
//                            "Missing child report submitted successfully",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                } else {
//                    // Handle the unsuccessful response, e.g., show an error message
//                    requireActivity().runOnUiThread {
//                        Toast.makeText(
//                            context,
//                            "Failed to submit missing child report",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                }
//            }
//
//            override fun onFailure(call: Call, e: IOException) {
//                // Handle the network request failure, e.g., show an error message
//                requireActivity().runOnUiThread {
//                    Toast.makeText(
//                        context,
//                        "Network request failed",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//        })
//    }
    private fun buildAlertMessageNoInternet() {
        val builder = AlertDialog.Builder(requireContext())
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
        (requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).run {
            getNetworkCapabilities(activeNetwork)?.run {
                hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                        || hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                        || hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
            } ?: false
        }

    private fun buildAlertMessageNoGps() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton("No") { dialog, _ -> dialog.cancel() }
        val alert = builder.create()
        alert.show()
    }

    private fun fetchLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    // Got last known location. In some rare situations, this can be null.
                    location?.let {
                         latitude = it.latitude
                         longitude = it.longitude
                        val formattedAddress = "$latitude, $longitude"
                        locationText.text = formattedAddress
                        println(formattedAddress)

                        val client = OkHttpClient.Builder()
                            .connectTimeout(300, TimeUnit.SECONDS)
                            .readTimeout(300, TimeUnit.SECONDS)
                            .writeTimeout(300, TimeUnit.SECONDS)
                            .build()

                        val url =
                            "https://api.geoapify.com/v1/geocode/reverse?lat=$latitude&lon=$longitude&apiKey=e47723ed0b064c3fab486307f31e2ae5"

                        val request: Request = Request.Builder()
                            .url(url)
                            .get()
                            .build()

                        client.newCall(request).enqueue(object : Callback {
                            override fun onFailure(call: Call, e: IOException) {
                                println("Failed to execute request")
                            }

                            override fun onResponse(call: Call, response: Response) {
                                val responseBody = response.body?.string()
                                responseBody?.let {
                                    val jsonObject = JSONObject(it)
                                    val featuresArray = jsonObject.getJSONArray("features")
                                    if (featuresArray.length() > 0) {
                                        val firstFeature = featuresArray.getJSONObject(0)
                                        val properties = firstFeature.getJSONObject("properties")
                                        val formattedAddress =
                                            properties.getString("formatted")

                                            locationText.text = formattedAddress

                                        println("Address: $formattedAddress")
                                    } else {
                                        println("No features found in the response.")
                                    }
                                }
                            }
                        })
                    } ?: run {
                        println("Last known location is null")
                    }
                }
                .addOnFailureListener { e ->
                    println("Error getting last known location: ${e.message}")
                }
        } catch (ex: SecurityException) {
            ex.printStackTrace()
            println(ex)
        }
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

    private fun fetchProfile_Count() {
        val url = Constants.FLASK_BASE_URL + "/count"
        val sharedPreferences = context?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val sessionToken = sharedPreferences?.getString("sessionToken", null) ?: "default"

        val request = Request.Builder()
            .url(url)
            .put(
                FormBody.Builder()
                    .add("token", sessionToken)
                    .build()
            )
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    println(response)
                    val responseBody = response.body?.string()
                    responseBody?.let {
                        val jsonObject = JSONObject(it)
                        fullName = jsonObject.getString("name")

                        profile_url = jsonObject.optString("profile_picture_url")

                        totalCount = jsonObject.optString("total")
                        foundCount = jsonObject.optString("matched")


                        if (foundCount.toInt() <= totalCount.toInt()) {
                            notFoundCountText.text=((totalCount.toInt() - foundCount.toInt()).toString())
                            totalCountText.text = totalCount
                            foundCountText.text=(foundCount)
                            profileNameText.text=(fullName)

                        }

                        activity?.runOnUiThread {

//        https://res.cloudinary.com/dognhm5vd/image/upload/v1708113375/profile/NKfotAlsifZ1eqa5QhRmfBkoOMV2.png.jpg
                            if (profile_url != "") {
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

    private fun fetchGraphData(){
        if (graph1=="Week"){
            graph1="day"
        }
        else if (graph1=="Day"){
            graph1="date"
        }
        else{
            graph1=graph1.lowercase()
        }
        val url = Constants.FLASK_BASE_URL + "/incident_"+graph1+"_distribution"
        val request = Request.Builder()
            .url(url)
            .get()
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    println(response)
                    val responseBody = response.body?.string()

                    responseBody?.let {
                        val jsonObject = JSONObject(it)
                        val res=jsonObject.getJSONArray("res")
                        val jsonObjectList = List(res.length()) { res.getJSONObject(it) }

                        // Sort the list of JSONObjects based on the "year" field
                        val sortedJsonObjectList = jsonObjectList.sortedBy { it.getInt(graph1) }
                        val sortedJsonArray = JSONArray(sortedJsonObjectList)
                        println(sortedJsonArray)
                        countArray= mutableListOf<Int>()
                        yArray= mutableListOf<String>()
//                        for (i :  )

                        for (i in 0 until sortedJsonArray.length()) {




                            val element = sortedJsonArray.getJSONObject(i)
                            // Now you can access properties of each element using element.get<Type>("key")
                            // For example:
                            val ele = element.getInt(graph1)
                            val count = element.getInt("count")


                            val months= arrayOf("Jan","Feb","Mar","Apr","May","Jun","July","Aug","Sept","Oct","Nov","Dec")
                            val weeks= arrayOf("Sun","Mon","Tue","Wed","Thur","Fri","Sat")
                            if (graph1=="month"){
                                countArray.add(count)
                                yArray.add(months.get(ele-1))
                            }

                            else if(graph1=="day"){
                                countArray.add(count)
                                yArray.add(weeks.get(ele))
                            }
                            else{
                                countArray.add(count)
                                yArray.add(ele.toString())


                            }

                            // Process the properties as needed
                        }
                        println(countArray)
                        println(yArray)
                        renderData()
                    }

                }
                // Parse the JSON response body
                // and update UI with user profile data
                else {
                    // Handle unsuccessful response
                }

            }

            override fun onFailure(call: Call, e: IOException) {
                // Handle failure
            }
        })
    }

    private fun fetchpieData(){

        val url = Constants.FLASK_BASE_URL + "/age_distribution"
        val request = Request.Builder()
            .url(url)
            .get()
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    println(response)
                    val responseBody = response.body?.string()

                    responseBody?.let {
                        val jsonObject = JSONObject(it)
                        val res=jsonObject.getJSONArray(pieOption.lowercase())
//                        val jsonObjectList = List(res.length()) { res.getJSONObject(it) }

                        // Sort the list of JSONObjects based on the "year" field
//                        val sortedJsonObjectList = jsonObjectList.sortedBy { it.getString("age_group") }
//                        val sortedJsonArray = JSONArray(sortedJsonObjectList)
//                        println(sortedJsonArray)
                        countArray= mutableListOf<Int>()
                        yArray= mutableListOf<String>()
//                        for (i :  )

                        for (i in 0 until res.length()) {




                            val element = res.getJSONObject(i)
                            // Now you can access properties of each element using element.get<Type>("key")
                            // For example:
                            val ele = element.getString("age_group")
                            val count = element.getInt("count")



                            countArray.add(count)
                            yArray.add(ele.toString())




                            // Process the properties as needed
                        }
                        println(countArray)
                        println(yArray)
                        renderPieData()
                    }

                }
                // Parse the JSON response body
                // and update UI with user profile data
                else {
                    // Handle unsuccessful response
                }

            }

            override fun onFailure(call: Call, e: IOException) {
                // Handle failure
            }
        })
    }

    private fun fetchGraphYearData(){
//        if (graph1=="Week"){
//            graph1="day"
//        }
//        else if (graph1=="Day"){
//            graph1="date"
//        }
//        else{
//            graph1=graph1.lowercase()
//        }
        val url = Constants.FLASK_BASE_URL + "/incident_month_distribution_year"
        val request = Request.Builder()
            .url(url)
            .put(FormBody.Builder()
                .add("year", graph_year)
                .build())
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    println(response)
                    val responseBody = response.body?.string()

                    responseBody?.let {
                        val jsonObject = JSONObject(it)
                        val res=jsonObject.getJSONArray("res")
                        val jsonObjectList = List(res.length()) { res.getJSONObject(it) }

                        // Sort the list of JSONObjects based on the "year" field
                        val sortedJsonObjectList = jsonObjectList.sortedBy { it.getInt("month") }
                        val sortedJsonArray = JSONArray(sortedJsonObjectList)
                        println(sortedJsonArray)
                        countyearArray= mutableListOf<Int>()
                        yyearArray= mutableListOf<String>()
//                        for (i :  )

                        for (i in 0 until sortedJsonArray.length()) {




                            val element = sortedJsonArray.getJSONObject(i)
                            // Now you can access properties of each element using element.get<Type>("key")
                            // For example:
                            val ele = element.getInt("month")
                            val count = element.getInt("count")


                            val months= arrayOf("January","February","March","April","May","June","July","August","September","October","November","December")
//                            val weeks= arrayOf("Sun","Mon","Tue","Wed","Thur","Fri","Sat")

                            countyearArray.add(count)
                            yyearArray.add(months.get(ele-1))




                            // Process the properties as needed
                        }
                        println(countyearArray)
                        println(yyearArray)
                        renderBarData()
                    }

                }
                // Parse the JSON response body
                // and update UI with user profile data
                else {
                    // Handle unsuccessful response
                }

            }

            override fun onFailure(call: Call, e: IOException) {
                // Handle failure
            }
        })
    }




    private fun submitForm() {
        // Retrieve data from UI components
        name1 = nameEditText1.text.toString()
        email1 = mailEditText1.text.toString()
        phone1 = phoneEditText1.text.toString()
        name2 = nameEditText2.text.toString()
        email2 = mailEditText2.text.toString()
        phone2 = phoneEditText2.text.toString()
        val url =
            Constants.FLASK_BASE_URL + "/update_emergency_contacts" // Replace with your Flask register endpoint URL
        val sharedPreferences = context?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val sessionToken = sharedPreferences?.getString("sessionToken", null) ?: "default"
//    val apiUrl = Constants.FLASK_BASE_URL +"/get_reports" // Replace with your actual API endpoint
        val urlBuilder = StringBuilder(url)
        urlBuilder.append("?")
        println("request sent")
        urlBuilder.append("token=$sessionToken&")
        // Append query parameters for search and filtering
        if (!name1.isNullOrEmpty()) {
            urlBuilder.append("name1=$name1&")
        }
        if (!phone1.isNullOrEmpty()) {
            urlBuilder.append("phone_number1=$phone1&")
        }
        if (!email1.isNullOrEmpty()) {
            urlBuilder.append("email1=$email1&")
        }
        if (!relation1.isNullOrEmpty()) {
            urlBuilder.append("relation1=$relation1&")
        }

        if (!name2.isNullOrEmpty()) {
            urlBuilder.append("name2=$name2&")
        }
        if (!phone2.isNullOrEmpty()) {
            urlBuilder.append("phone_number2=$phone2&")
        }
        if (!email2.isNullOrEmpty()) {
            urlBuilder.append("email2=$email2&")
        }
        if (!relation2.isNullOrEmpty()) {
            urlBuilder.append("relation2=$relation2&")
        }



        // Append other query parameters similarly

        // Remove trailing '&' if present
        if (urlBuilder.endsWith("&")) {
            urlBuilder.deleteCharAt(urlBuilder.length - 1)
        }
        val response = URL(urlBuilder.toString()).readText()
        if (!response.isNullOrEmpty()) {
            fetchUserEmergencyContacts()
            Toast.makeText(requireContext(), "Emergency Contacts  updated successfully", Toast.LENGTH_SHORT)
                .show()
        } else {
            Toast.makeText(requireContext(), "Emergency Contacts not updated", Toast.LENGTH_SHORT).show()
        }




        // Add other form fields as needed


// Build the final MultipartBody
//        val multipartBody = builder.build()
        // Create a request with the server URL and method

    }

//    private fun updateEmergencyContacts(
//        name1: String,
//        email1: String,
//        phone1: String,
//        relation1: String,
//        name2: String,
//        email2: String,
//        phone2: String,
//        relation2: String
//    ) {
//        val url =
//            Constants.FLASK_BASE_URL + "/update_profile" // Replace with your Flask register endpoint URL
//        val sharedPreferences = context?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
//        val sessionToken = sharedPreferences?.getString("sessionToken", null) ?: "default"
////    val apiUrl = Constants.FLASK_BASE_URL +"/get_reports" // Replace with your actual API endpoint
//        val urlBuilder = StringBuilder(url)
//        urlBuilder.append("?")
//        println("request sent")
//        urlBuilder.append("token=$sessionToken&")
//        // Append query parameters for search and filtering
//        if (!name1.isNullOrEmpty()) {
//            urlBuilder.append("name1=$name1&")
//        }
//        if (!phone1.isNullOrEmpty()) {
//            urlBuilder.append("phone_number1=$phone1&")
//        }
//        if (!email1.isNullOrEmpty()) {
//            urlBuilder.append("email1=$email1&")
//        }
//        if (!relation1.isNullOrEmpty()) {
//            urlBuilder.append("relation1=$relation1&")
//        }
//
//        if (!name2.isNullOrEmpty()) {
//            urlBuilder.append("name2=$name2&")
//        }
//        if (!phone2.isNullOrEmpty()) {
//            urlBuilder.append("phone_number2=$phone2&")
//        }
//        if (!email2.isNullOrEmpty()) {
//            urlBuilder.append("email2=$email2&")
//        }
//        if (!relation2.isNullOrEmpty()) {
//            urlBuilder.append("relation2=$relation2&")
//        }
//
//
//
//        // Append other query parameters similarly
//
//        // Remove trailing '&' if present
//        if (urlBuilder.endsWith("&")) {
//            urlBuilder.deleteCharAt(urlBuilder.length - 1)
//        }
//        val response = URL(urlBuilder.toString()).readText()
//        if (!response.isNullOrEmpty()) {
//            fetchUserEmergencyContacts()
//            Toast.makeText(requireContext(), "Emergency Contacts  updated successfully", Toast.LENGTH_SHORT)
//                .show()
//        } else {
//            Toast.makeText(requireContext(), "Emergency Contacts not updated", Toast.LENGTH_SHORT).show()
//        }
////        }
////    })
//    }

    private fun fetchUserEmergencyContacts() {
        val url = Constants.FLASK_BASE_URL + "/emergency_contacts"
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
                        name1=jsonObject.getString("name1")
//                        uid=jsonObject.getString("uid")
                        phone1=jsonObject.optString("phone_number1")
                        email1=jsonObject.getString("email1")
                        relation1=jsonObject.optString("relation1")
//                        age=jsonObject.optString("age").toInt()
                        name2=jsonObject.getString("name2")
//                        uid=jsonObject.getString("uid")
                        phone2=jsonObject.optString("phone_number2")
                        email2=jsonObject.getString("email2")
                        relation2=jsonObject.optString("relation2")

                        activity?.runOnUiThread{
                            nameEditText1.setText(name1)
                            mailEditText1.setText(email1)
                            phoneEditText1.setText(phone1)
                            if (relation1.equals("Parent")) {
                                relationSpinner1.setSelection(1);
                            } else if (relation1.equals("Child")) {
                                relationSpinner1.setSelection(2);
                            } else if (relation1.equals("Sibling")) {
                                relationSpinner1.setSelection(3);
                            } else if (relation1.equals("Grandparent")) {
                                relationSpinner1.setSelection(4);
                            } else if (relation1.equals("Grandchild")) {
                                relationSpinner1.setSelection(5);
                            } else if (relation1.equals("Aunt/Uncle")) {
                                relationSpinner1.setSelection(6);
                            } else if (relation1.equals("Niece/Nephew")) {
                                relationSpinner1.setSelection(7);
                            } else if (relation1.equals("Cousin")) {
                                relationSpinner1.setSelection(8);
                            } else if (relation1.equals("Guardian")) {
                                relationSpinner1.setSelection(9);
                            } else if (relation1.equals("Other")) {
                                relationSpinner1.setSelection(10);
                            } else {
                                relationSpinner1.setSelection(0); // Default selection if none matches
                            }

                            nameEditText2.setText(name2)
                            mailEditText2.setText(email2)
                            phoneEditText2.setText(phone2)
                            
                            
                            if (relation2.equals("Parent")) {
                                relationSpinner2.setSelection(1);
                            } else if (relation2.equals("Child")) {
                                relationSpinner2.setSelection(2);
                            } else if (relation2.equals("Sibling")) {
                                relationSpinner2.setSelection(3);
                            } else if (relation2.equals("Grandparent")) {
                                relationSpinner2.setSelection(4);
                            } else if (relation2.equals("Grandchild")) {
                                relationSpinner2.setSelection(5);
                            } else if (relation2.equals("Aunt/Uncle")) {
                                relationSpinner2.setSelection(6);
                            } else if (relation2.equals("Niece/Nephew")) {
                                relationSpinner2.setSelection(7);
                            } else if (relation2.equals("Cousin")) {
                                relationSpinner2.setSelection(8);
                            } else if (relation2.equals("Guardian")) {
                                relationSpinner2.setSelection(9);
                            } else if (relation2.equals("Other")) {
                                relationSpinner2.setSelection(10);
                            } else {
                                relationSpinner2.setSelection(0); // Default selection if none matches
                            }
//                            editAge.setText(age)

                            }
                        }

                    }
                    // Parse the JSON response body
                    // and update UI with user profile data
                else {
                    // Handle unsuccessful response
                }

            }

            override fun onFailure(call: Call, e: IOException) {
                // Handle failure
            }
        })
    }

}

class LabelFormatterX(private val labels: MutableList<String>) : com.github.mikephil.charting.formatter.ValueFormatter() {

    override fun getFormattedValue(value: Float): String {
        println("x"+value)
        println("f"+value.toInt())
        return if (value.toInt() >= 0 && value.toInt() < labels.size)
            labels[value.toInt()]
        else
            ""
    }
}