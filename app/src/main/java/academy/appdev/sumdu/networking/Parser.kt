package academy.appdev.sumdu.networking

import academy.appdev.sumdu.objects.ContentObject
import academy.appdev.sumdu.objects.ListObject
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.jsoup.nodes.Element
import java.util.ArrayList


fun parseListObjects(element: Element, objectType: String): String {
    // Loops through options of HTML select element and map entries to ListObjects
    val records = ArrayList<ListObject>()
    for (option in element.children()) {
        // Validate pairTitleAndType on import
        val title = option.text().trim { it <= ' ' }
        if (title.length > 1) {
            records.add(ListObject().apply {
                this.id = option.attr("value")
                this.objectType = objectType
                this.title = title
            })
        }
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