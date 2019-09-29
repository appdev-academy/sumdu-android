package academy.appdev.sumdu.activities

import academy.appdev.sumdu.AsynkHandler
import academy.appdev.sumdu.MainActivity
import academy.appdev.sumdu.networking.*
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import org.jsoup.Jsoup
import java.io.IOException

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getLists()
    }

    private fun navigateNext() {
        startActivity(Intent(this, MainActivity::class.java))
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }

    private fun getLists() {
        AsynkHandler {
            try {
                // Download HTML document and parse `select` objects
                val document = Jsoup.connect(baseUrl).get()
                val auditoriums = document.select("#auditorium").first()
                val groups = document.select("#group").first()
                val teachers = document.select("#teacher").first()

                // Serialize options of `select` DOM objects
                val serializedAuditoriums = parseListObjects(auditoriums, "id_aud")
                val serializedGroups = parseListObjects(groups, "id_grp")
                val serializedTeachers = parseListObjects(teachers, "id_fio")

                // Save lists of Auditoriums, Groups and Teachers to SharedPreferences
                PreferenceManager.getDefaultSharedPreferences(this@SplashActivity).edit()?.apply {
                    putString(AUDITORIUMS_KEY, serializedAuditoriums)
                    putString(GROUPS_KEY, serializedGroups)
                    putString(TEACHERS_KEY, serializedTeachers)
                    apply()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

            navigateNext()
        }.execute()
    }
}