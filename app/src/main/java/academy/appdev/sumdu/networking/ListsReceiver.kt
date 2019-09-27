package academy.appdev.sumdu.networking

import academy.appdev.sumdu.AsynkHandler
import academy.appdev.sumdu.MainActivity
import academy.appdev.sumdu.objects.ListObject
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.tab_list_layout.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.io.IOException
import java.util.*


private const val baseUrl = "http://schedule.sumdu.edu.ua/"

// Special keys for sharedPreferences values
val GROUPS_KEY = "academy.appdev.sumdu.groups"
val TEACHERS_KEY = "academy.appdev.sumdu.teachers"
val AUDITORIUMS_KEY = "academy.appdev.sumdu.auditoriums"
val HISTORY_KEY = "academy.appdev.sumdu.history"

fun MainActivity.getLists(handler: () -> Unit) {
    AsynkHandler {
        // todo: swipeRefresh.isRefreshing = true
//        val swipeRefresh = this.containerLayout.<SwipeRefreshLayout>(R.id.swipeRefreshLayout)
//        runOnUiThread { swipeRefresh.isRefreshing = true }

        try {
            // Download HTML document and parse `select` objects
            val document = Jsoup.connect(baseUrl).get()
            val auditoriums = document.select("#auditorium").first()
            val groups = document.select("#group").first()
            val teachers = document.select("#teacher").first()

            // Serialize options of `select` DOM objects
            val serializedAuditoriums = parseListObjects(auditoriums)
            val serializedGroups = parseListObjects(groups)
            val serializedTeachers = parseListObjects(teachers)

            // Save lists of Auditoriums, Groups and Teachers to SharedPreferences
            sharedPreferences?.edit()?.apply {
                putString(AUDITORIUMS_KEY, serializedAuditoriums)
                putString(GROUPS_KEY, serializedGroups)
                putString(TEACHERS_KEY, serializedTeachers)
                apply()
            }

            handler()

//            runOnUiThread { swipeRefreshLayout.isRefreshing = false }
        } catch (e: IOException) {
            e.printStackTrace()

            // todo: set swipeRefresh.isRefreshing = false
//            this.runOnUiThread { swipeRefresh.isRefreshing = false }
        }
    }.execute()
}

fun parseListObjects(element: Element): String {
    // Loops through options of HTML select element and map entries to ListObjects
    val records = ArrayList<ListObject>()
    for (option in element.children()) {
        // Validate pairTitleAndType on import
        val title = option.text().trim { it <= ' ' }
        if (title.length > 1) {
            records.add(ListObject().apply {
                this.id = option.attr("value")
                this.title = title
            })
        }
    }
    return Gson().toJson(records.sortedBy { it.title })
}

fun parseStringToArrayList(stringToParse: String): ArrayList<ListObject>? {
    val itemsListType = object : TypeToken<List<ListObject>>() {}.type
    return Gson().fromJson<ArrayList<ListObject>>(stringToParse, itemsListType)
}