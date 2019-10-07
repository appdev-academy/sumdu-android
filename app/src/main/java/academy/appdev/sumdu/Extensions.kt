package academy.appdev.sumdu

import academy.appdev.sumdu.fragments.TabFragment
import academy.appdev.sumdu.networking.parseStringToArrayList
import academy.appdev.sumdu.objects.ListObject
import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.tab_list_layout.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


fun TabFragment.refreshData() {
    val filteredArray = ArrayList<ListObject>()
    val generalArrayList =
        parseStringToArrayList(mainActivity?.sharedPreferences?.getString(key, ""))
            ?: arrayListOf()
    generalArrayList.forEach {
        if (it.title?.toLowerCase()?.contains(searchQuery.toLowerCase()) == true) {
            filteredArray.add(it)
        }
    }
    data = filteredArray
    setNewData(filteredArray)

    swipeRefreshLayout.isRefreshing = false
}

fun TabFragment.saveToHistory(
    newHistoryObject: ListObject
): String {
    val history =
        parseStringToArrayList(mainActivity?.sharedPreferences?.getString(HISTORY_KEY, ""))
            ?: arrayListOf()

    var doesHistoryContainsObject = false
    history.forEach {
        if (it.id == newHistoryObject.id) doesHistoryContainsObject = true
    }

    if (!doesHistoryContainsObject) {
        history.add(history.size, newHistoryObject)
    }

    val jsonHistoryString = Gson().toJson(history)

    mainActivity?.sharedPreferences?.edit()?.apply {
        putString(HISTORY_KEY, jsonHistoryString)
        apply()
    }

    return jsonHistoryString
}

fun String.toDate(): Date? {
    return try {
        SimpleDateFormat("dd.MM.yyyy").parse(this)
    } catch (e: ParseException) {
        null
    }
}

fun String.formatDateString(): String? {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy")
    return try {
        val date = dateFormat.parse(this)
        dateFormat.format(date)
    } catch (e: ParseException) {
        null
    }
}

fun String.formatDayMonth(context: Context?): String? {
    return try {
        val date = SimpleDateFormat("dd.MM.yyyy").parse(this)
        SimpleDateFormat("d MMMM", context?.appLocale).format(date)
    } catch (e: ParseException) {
        null
    }
}

fun Context.makeToast(stringId: Int) {
    Toast.makeText(this, stringId, Toast.LENGTH_SHORT).show()
}

val Context.sharedPreferences: SharedPreferences
    get() = PreferenceManager.getDefaultSharedPreferences(
        this
    )

val Date.stringValue: String get() = run { SimpleDateFormat("dd.MM.yyyy").format(this) }
