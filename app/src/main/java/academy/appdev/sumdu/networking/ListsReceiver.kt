package academy.appdev.sumdu.networking

import academy.appdev.sumdu.*
import academy.appdev.sumdu.networking.retrofit.Api.baseUrl
import org.jsoup.Jsoup
import java.io.IOException


fun MainActivity.getLists(handler: () -> Unit) {
    AsynkHandler {
        try {
            // Download HTML document and parse `select` objects
            val document = Jsoup.connect(baseUrl(this)).get()
            val auditoriums = document.select("#auditorium").first()
            val groups = document.select("#group").first()
            val teachers = document.select("#teacher").first()

            // Serialize options of `select` DOM objects
            val serializedAuditoriums = parseListObjects(auditoriums, "id_aud")
            val serializedGroups = parseListObjects(groups, "id_grp")
            val serializedTeachers = parseListObjects(teachers, "id_fio")

            // Save lists of Auditoriums, Groups and Teachers to SharedPreferences
            sharedPreferences.edit()?.apply {
                putString(AUDITORIUMS_KEY, serializedAuditoriums)
                putString(GROUPS_KEY, serializedGroups)
                putString(TEACHERS_KEY, serializedTeachers)
                apply()
            }

            this.runOnUiThread { handler() }

        } catch (e: IOException) {
            e.printStackTrace()

            this.runOnUiThread {
                handler()
                applicationContext?.makeToast(R.string.can_not_load_list)
            }
        }
    }.execute()
}
