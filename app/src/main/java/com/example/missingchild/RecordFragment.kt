import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.Spinner
import android.widget.TextView
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.text.font.FontWeight
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.missingchild.Constants.FLASK_BASE_URL
import com.example.missingchild.MapActivity
import com.example.missingchild.R
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.FileNotFoundException
import java.io.IOException
import java.net.URL
import java.security.AccessController.getContext

class RecordFragment : Fragment() {

    private lateinit var reportAdapter: ReportAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_record, container, false)

        // Initialize RecyclerView
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        reportAdapter = ReportAdapter()
        recyclerView.adapter = reportAdapter

        // Fetch and display reports
        fetchReports()
        val search:SearchView=view.findViewById(R.id.searchView)
        val searchButton:Button=view.findViewById(R.id.searchButton)
        val minEditText:EditText=view.findViewById(R.id.minEditText)
        val maxEditText:EditText=view.findViewById(R.id.maxEditText)
        var searchQuery:CharSequence?=""
        var minValue:String
        var maxValue:String
        val valueSpinner: Spinner = view.findViewById(R.id.valueSpinner)
        val sortOrderSpinner: Spinner = view.findViewById(R.id.sortOrderSpinner)
        val sortItemSpinner: Spinner = view.findViewById(R.id.sortItemSpinner)
        val genderSpinner: Spinner = view.findViewById(R.id.genderSpinner)
        val recordSpinner: Spinner = view.findViewById(R.id.recordSpinner)
        var sortOrder:String=""
        var selectedItem:String=""
        var valueSelectedItem:String=""
        var recordType:String=""
        var gender:String=""
        valueSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {

                valueSelectedItem = parent.getItemAtPosition(position).toString()
                if ((minEditText.text.toString().trim().isEmpty() || maxEditText.text.toString().trim().isEmpty())&&valueSelectedItem!="--None--"){
                    minEditText.error="Min or Max is required"
                }
                // Do something with the selected item
                Log.d("SpinnerSelection", "Selected item: $valueSelectedItem")
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }
        sortOrderSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                sortOrder = parent.getItemAtPosition(position).toString()
                // Do something with the selected item
                Log.d("SpinnerSelection", "Sort Order: $sortOrder")
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }
        sortItemSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedItem = parent.getItemAtPosition(position).toString()
                // Do something with the selected item
                Log.d("SpinnerSelection", "Selected Item : $selectedItem")
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }
        recordSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                recordType = parent.getItemAtPosition(position).toString()
                // Do something with the selected item
                Log.d("SpinnerSelection", "Record type: $recordType")
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }
        genderSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                gender = parent.getItemAtPosition(position).toString()
                // Do something with the selected item
                Log.d("SpinnerSelection", "Selected item: $gender")
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }



        searchButton.setOnClickListener {
            var minAge=""
            var maxAge=""
            var minHeight=""
            var maxHeight=""
            var minWeight=""
            var maxWeight=""
            var minLastSeenTime=""
            var maxLastSeenTime=""
            if (!search.query.isNullOrEmpty()){
                searchQuery=search.query
            }
            if (search.query.isEmpty()){
                searchQuery=""
            }
            if(valueSelectedItem.equals("Age")){
                if (!minEditText.text.toString().trim().isEmpty()){
                    minAge = minEditText.text.toString()
                }

                maxAge=maxEditText.text.toString()
            }
            if(valueSelectedItem.equals("Height")){
                if (!minEditText.text.toString().trim().isEmpty()){
                     minHeight = minEditText.text.toString()

                }

                 maxHeight=maxEditText.text.toString()
                println("modifed height"+maxHeight)
            }
            if(valueSelectedItem.equals("Weight")){
                if (!minEditText.text.toString().trim().isEmpty()){
                     minWeight = minEditText.text.toString()
                }

                 maxWeight=maxEditText.text.toString()
            }
            if(valueSelectedItem.equals("Last Seen Time")){
                if (!minEditText.text.toString().trim().isEmpty()){
                     minLastSeenTime = minEditText.text.toString()
                }

                maxLastSeenTime=maxEditText.text.toString()
            }
            fetchReports(searchQuery=searchQuery, minAge = minAge, maxAge = maxAge, minHeight = minHeight, maxHeight = maxHeight, minLastSeenTime = minLastSeenTime, maxLastSeenTime = maxLastSeenTime)



        }

        return view
    }

    private fun fetchReports(
        searchQuery: CharSequence? =null,
        gender: String?=null,
        minAge: String?=null,
        maxAge: String?=null,
        minHeight:String?=null,
        maxHeight:String?=null,
        minWeight: String?=null,
        maxWeight: String?=null,
        minLastSeenTime: String?=null,
        maxLastSeenTime: String?=null,
        recordType:String?=null,
        isMatched: Boolean?=null,
        sortField: String?=null,
        sortOrder: String?=null
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            val apiUrl = FLASK_BASE_URL+"/get_reports" // Replace with your actual API endpoint
            val urlBuilder = StringBuilder(apiUrl)
            urlBuilder.append("?")
            println("request sent")
            // Append query parameters for search and filtering
            if (!searchQuery.isNullOrEmpty()) {
                urlBuilder.append("search_query=$searchQuery&")
            }
            if (!gender.isNullOrEmpty()) {
                urlBuilder.append("gender=$gender&")
            }
            if (!minAge.isNullOrEmpty()) {
                urlBuilder.append("min_age=$minAge&")
            }
            if (!maxAge.isNullOrEmpty()) {
                urlBuilder.append("max_age=$maxAge&")
            }
            if (!minHeight.isNullOrEmpty()) {
                urlBuilder.append("min_height=$minHeight&")
            }
            if (!maxHeight.isNullOrEmpty()) {
                urlBuilder.append("max_height=$maxHeight&")
            }
            if (!minWeight.isNullOrEmpty()) {
                urlBuilder.append("min_weight=$minWeight&")
            }
            if (!maxWeight.isNullOrEmpty()) {
                urlBuilder.append("max_weight=$maxWeight&")
            }
            if (!minLastSeenTime.isNullOrEmpty()) {
                urlBuilder.append("min_last_seen_time=$minLastSeenTime&")
            }
            if (!maxLastSeenTime.isNullOrEmpty()) {
                urlBuilder.append("max_last_seen_time=$maxLastSeenTime&")
            }
            if (!recordType.isNullOrEmpty()) {
                urlBuilder.append("record_type=$recordType&")
            }



            // Append other query parameters similarly

            // Remove trailing '&' if present
            if (urlBuilder.endsWith("&")) {
                urlBuilder.deleteCharAt(urlBuilder.length - 1)
            }

            try {
                val response = URL(urlBuilder.toString()).readText()
                val jsonResponse= JSONObject(response)
                val jsonArray=jsonResponse.getJSONArray("reports")
                val reports = mutableListOf<Report>()
                System.out.println(response)
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    // Parse JSON object to create Report objects
                    val name = jsonObject.getString("name")
                    val age=jsonObject.getInt("age")
                    val description=jsonObject.getString("description")
                    val alternate_mobile = jsonObject.optString("alternate_mobile", "")
                    val behavioral_characteristics = jsonObject.optString("behavioral_characteristics", "")
                    val clothing_description = jsonObject.optString("clothing_description", "")
                    val weight = jsonObject.optInt("weight", 0)
                    val email = jsonObject.optString("email", "")
                    val height = jsonObject.optInt("height", 0)
                    val images = jsonObject.optString("images", "")
                    var matched_location=jsonObject.optJSONArray("matched_location")
                    val last_seen_location = jsonObject.optString("last_seen_location", "")
                    val last_seen_time = jsonObject.optString("last_seen_time", "")
                    val matched = jsonObject.optBoolean("matched", false)
                    val medical_information = jsonObject.optString("medical_information", "")
                    val moles = jsonObject.optString("moles", "")
                    val physical_features = jsonObject.optString("physical_features", "")
//                    val base64 = jsonObject.optString("base64", "")
                    if(matched_location==null){
                        matched_location= JSONArray()
                    }

//                    "helper_full_name": "Mahesh",
//                    "helper_phone": "9398241099",
//                    "helper_profile_email": "mahesh@gmail.com",

                        val helper_fullname = jsonObject.optString("helper_full_name", "")
                        val helper_phone = jsonObject.optString("helper_phone", "")
                        val helper_profile_email = jsonObject.optString("helper_profile_email", "")

                    // Parse other fields similarly
                    val report = Report(name=name,age=age,description=description, alternateMobile = alternate_mobile, email = email,
                        clothingDescription = clothing_description, moles = moles, physical_features = physical_features,
                        medicalInformation = medical_information, height = height, weight = weight, last_seen_location = last_seen_location, last_seen_time = last_seen_time,
                        matched = matched, images = images, helper_email = helper_profile_email, helper_fullname = helper_fullname, helper_phonenumber = helper_phone, matched_Location = matched_location)
                    reports.add(report)
                }
                GlobalScope.launch(Dispatchers.Main) {
                    reportAdapter.reports = reports
                    reportAdapter.notifyDataSetChanged()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

}

//    private fun parseReports(responseData: String): List<Report> {
//        val reports = mutableListOf<Report>()
//        try {
//            val jsonObject = JSONObject(responseData)
//            val reportArray = jsonObject.getJSONArray("user_profiles")
//            for (i in 0 until reportArray.length()) {
//                val reportObject = reportArray.getJSONObject(i)
//                // Parse report fields and create Report objects
//                val report = Report(
//                    "hi"
//                    // Populate fields accordingly
//                )
//                reports.add(report)
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//        return reports
//    }
//}

class ReportAdapter : RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {

    var reports = mutableListOf<Report>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_report, parent, false)

        return ReportViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val report = reports[position]
        // Bind report data to the ViewHolder
        holder.bind(report)
    }

    override fun getItemCount(): Int {
        return reports.size
    }

    fun updateReports(newReports: List<Report>) {
        reports.clear()
        reports.addAll(newReports)
        notifyDataSetChanged()
    }

    inner class ReportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Define views from item_report layout
        // Example: val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val intialCard:LinearLayout=itemView.findViewById(R.id.initialcard)
        val nameEditText: TextView = itemView.findViewById(R.id.textName)
        val ageEditText: TextView = itemView.findViewById(R.id.textAge)
        val LastSeenEditText: TextView = itemView.findViewById(R.id.textLastSeenTime)
        val LastSeenLocEditText: TextView = itemView.findViewById(R.id.textLastSeenLocation)
        val matchedEditText: TextView = itemView.findViewById(R.id.textMatched)

        val expandedView = itemView.findViewById<LinearLayout>(R.id.expandedView)
        val clothingDescriptionTextView: TextView = itemView.findViewById(R.id.textCloth)
        val descriptionTextView: TextView = itemView.findViewById(R.id.textDescription)
        val parentMobileTextView: TextView = itemView.findViewById(R.id.textParentPh)
        val heightTextView: TextView = itemView.findViewById(R.id.textHeight)
        val weightTextView: TextView = itemView.findViewById(R.id.textWeight)
        val medicalInformationTextView: TextView = itemView.findViewById(R.id.textMedicalInfo)
        val molesTextView: TextView = itemView.findViewById(R.id.textMoles)
        val physicalFeaturesTextView: TextView = itemView.findViewById(R.id.textPhysicalFeatures)

        val helperNameTextView: TextView = itemView.findViewById(R.id.textHelper_name)
        val helperMailTextView: TextView = itemView.findViewById(R.id.textHelperEmail)
        val helperPhoneTextView: TextView = itemView.findViewById(R.id.textHelperPhone)
        val shapeableImageView:ShapeableImageView = itemView.findViewById(R.id.shapableImageViewreport);
        val textMatchedLocation:TextView = itemView.findViewById(R.id.textMatchedLocation)



        // Base64 encoded string representing your image




        // Define a function to create and add a TextView


// Add TextViews for each field


        @SuppressLint("SetTextI18n")
        fun bind(report: Report) {

            // Bind report data to views
            // Example: titleTextView.text = report.title
            nameEditText.text="Name: "+report.name
            ageEditText.text= "Age: "+report.age.toString()
            LastSeenEditText.text="Last seen time: "+report.last_seen_time
            LastSeenLocEditText.text="Last Seen Location:"+report.last_seen_location

            matchedEditText.text="Matched:"+report.matched
            parentMobileTextView.text = "Parent Mobile: ${report.alternateMobile}"
            clothingDescriptionTextView.text = "Clothing Description: ${report.clothingDescription}"
            descriptionTextView.text = "Description: ${report.description}"
            heightTextView.text = "Height: ${report.height}"
            weightTextView.text="Weight:${report.weight}"
            medicalInformationTextView.text = "Medical Information: ${report.medicalInformation}"
            molesTextView.text = "Moles: ${report.moles}"
            physicalFeaturesTextView.text = "Physical Features: ${report.physical_features}"
            try {
                val url =
                    URL("https://res.cloudinary.com/dognhm5vd/image/upload/v1707997728/training/${report.images}/1.png.jpg")
                val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                val circularBitmap = getCircularBitmap(bmp)
                shapeableImageView.setImageBitmap(circularBitmap)
//            if (report.base64String!="") {
//                val base64String = "your_base64_encoded_image_here"
//
//                // Decode the base64 string to a bitmap
//                val decodedString: ByteArray = Base64.decode(base64String, Base64.DEFAULT)
//                val decodedByte =
//                    BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
//
//                // Set the bitmap to the ShapeableImageView
//                shapeableImageView.setImageBitmap(decodedByte)
//            }
                // Set the bitmap to the ShapeableImageView
            }
            catch (e:IOException){
                val x="no url"
            }
            catch (e:FileNotFoundException){
                val x="no"
            }
            finally {
                val i="Not found"
            }
            if (report.matched){
                helperNameTextView.text="Helper Full name:${report.helper_fullname}"

                helperMailTextView.text="Helper Mail:${report.helper_email}"

                helperPhoneTextView.text="Helper Mobile Number:${report.helper_phonenumber}"
                textMatchedLocation.text="Matched Location:${report.matched_Location}"
                textMatchedLocation.setOnClickListener{
                    val intent = Intent(itemView.context , MapActivity::class.java)

                        intent.putExtra("lat", report.matched_Location.get(0).toString().toDouble())
                        intent.putExtra("lon", report.matched_Location.get(1).toString().toDouble())

                    itemView.context.startActivity(intent)
                }

            }
            else{
                helperPhoneTextView.visibility=View.GONE

                helperMailTextView.visibility=View.GONE
                helperNameTextView.visibility=View.GONE
                textMatchedLocation.visibility=View.GONE

            }


//            addTextView("Description", report.description)
//            addTextView("Height", report.height.toString())
//            addTextView("Weight",report.weight.toString())
//            addTextView("Parent Ph number",report.alternateMobile)
//            addTextView("Medical Information",report.medical_information)
//            addTextView("Physical Features",report.physical_features)
//            addTextView("Moles",report.moles)
//            if(report.matched){
//                addTextView("Helper Name",report.helper_fullname)
//                addTextView("Helper Email",report.helper_email)
//                addTextView("Helper Phone Number",report.helper_phonenumber)
//
//            }

            intialCard.setOnClickListener {
                if (expandedView.visibility == View.VISIBLE) {
                    // If the expanded view is visible, hide it
                    expandedView.visibility = View.GONE
                } else {
                    // If the expanded view is not visible, show it
                    expandedView.visibility = View.VISIBLE
                }
            }



        }
//        private fun addTextView(label: String, value: String?) {
//            if (value.isNullOrEmpty()) return // Skip if value is null or empty
//
//            val textView = TextView(itemView.context)
//            textView.layoutParams = ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT
//            )
//            textView.text = "$label: $value"
//            textView.textSize = 16f
//            expandedView.addView(textView)
//        }
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
    }
}

data class Report(
    // Define report fields here
    // Example: val title: String,
    //          val description: String,
    //          ...
    val name:String,
    val age: Int,
    val email: String,
    val description:String,
    val clothingDescription: String,
    val alternateMobile:String,
    val height: Int,
    val weight:Int,
    val images: String,
    val last_seen_location: String,
    val matched_Location:JSONArray,
    val last_seen_time: String,
    val matched: Boolean,
    val medicalInformation: String,
    val moles: String,
    val physical_features: String,
    val helper_fullname:String,
    val helper_phonenumber:String,
    val helper_email:String,

)
