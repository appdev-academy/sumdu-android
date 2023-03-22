package academy.appdev.sumdu.networking

import academy.appdev.sumdu.objects.ContentObject
import academy.appdev.sumdu.objects.ListObject
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.ArrayList


fun parseListObjects(hashMap: Map<String, String>, objectType: String): String {
    val records = ArrayList<ListObject>()
    hashMap.forEach {
        val title = it.value.replace("\"", "")
        if (title.isNotEmpty()) records.add(ListObject(it.key, title, objectType))
    }
    return Gson().toJson(records.sortedBy { it.title })
}

fun parseStringToArrayList(stringToParse: String?): ArrayList<ListObject>? {
    val itemsListType = object : TypeToken<List<ListObject>>() {}.type
    return Gson().fromJson<ArrayList<ListObject>>(stringToParse, itemsListType)
}

fun parseStringToList(stringToParse: String?): List<ContentObject>? {
    val itemsListType = object : TypeToken<List<ContentObject>>() {}.type
    return Gson().fromJson<List<ContentObject>>(stringToParse, itemsListType)
}

fun parseListToJson(contentList: List<ContentObject>): String {
    return Gson().toJson(contentList)
}
