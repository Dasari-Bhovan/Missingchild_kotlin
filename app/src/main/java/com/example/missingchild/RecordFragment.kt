import com.example.missingchild.R

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.missingchild.Constants.FLASK_BASE_URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.URL

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

        return view
    }

    private fun fetchReports(
        searchQuery: String?=null,
        gender: String?=null,
        minAge: String?=null,
        maxAge: String?=null,
        minLastSeenTime: String?=null,
        maxLastSeenTime: String?=null,
        isMatched: Boolean?=null,
        sortField: String?=null,
        sortOrder: String?=null
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            val apiUrl = FLASK_BASE_URL+"/get_user_profiles" // Replace with your actual API endpoint
            val urlBuilder = StringBuilder(apiUrl)
            urlBuilder.append("?")

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
                    val last_seen_location = jsonObject.optString("last_seen_location", "")
                    val last_seen_time = jsonObject.optString("last_seen_time", "")
                    val matched = jsonObject.optBoolean("matched", false)
                    val medical_information = jsonObject.optString("medical_information", "")
                    val moles = jsonObject.optString("moles", "")
                    val physical_features = jsonObject.optString("physical_features", "")
//                    "helper_full_name": "Mahesh",
//                    "helper_phone": "9398241099",
//                    "helper_profile_email": "mahesh@gmail.com",

                        val helper_fullname = jsonObject.optString("helper_fullname", "")
                        val helper_phone = jsonObject.optString("helper_phone", "")
                        val helper_profile_email = jsonObject.optString("helper_profile_email", "")

                    // Parse other fields similarly
                    val report = Report(name=name,age=age,description=description, alternateMobile = alternate_mobile, email = email,
                        clothing_description = clothing_description, moles = moles, physical_features = physical_features,
                        medical_information = medical_information, height = height, weight = weight, last_seen_location = last_seen_location, last_seen_time = last_seen_time,
                        matched = matched, images = images, helper_email = helper_profile_email, helper_fullname = helper_fullname, helper_phonenumber = helper_phone)
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

        // Define a function to create and add a TextView


// Add TextViews for each field


        fun bind(report: Report) {

            // Bind report data to views
            // Example: titleTextView.text = report.title
            nameEditText.text="Name: "+report.name
            ageEditText.text= "Age: "+report.age.toString()
            LastSeenEditText.text="Last seen time: "+report.last_seen_time
            LastSeenLocEditText.text="Last Seen Location:"+report.last_seen_location
            matchedEditText.text="Matched:"+report.matched
            addTextView("Description", report.description)
            addTextView("Height", report.height.toString())
            addTextView("Weight",report.weight.toString())
            addTextView("Parent Ph number",report.alternateMobile)
            addTextView("Medical Information",report.medical_information)
            addTextView("Physical Features",report.physical_features)
            addTextView("Moles",report.moles)
            if(report.matched){
                addTextView("Helper Name",report.helper_fullname)
                addTextView("Helper Email",report.helper_email)
                addTextView("Helper Phone Number",report.helper_phonenumber)

            }

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
        private fun addTextView(label: String, value: String?) {
            if (value.isNullOrEmpty()) return // Skip if value is null or empty

            val textView = TextView(itemView.context)
            textView.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            textView.text = "$label: $value"
            textView.textSize = 16f
            expandedView.addView(textView)
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
    val clothing_description: String,
    val alternateMobile:String,
    val height: Int,
    val weight:Int,
    val images: String,
    val last_seen_location: String,
    val last_seen_time: String,
    val matched: Boolean,
    val medical_information: String,
    val moles: String,
    val physical_features: String,
    val helper_fullname:String,
    val helper_phonenumber:String,
    val helper_email:String

)
