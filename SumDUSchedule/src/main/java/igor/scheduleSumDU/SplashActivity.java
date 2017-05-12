package igor.scheduleSumDU;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import static com.android.volley.VolleyLog.TAG;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean tabletSize = getResources().getBoolean(R.bool.isTablet);
        if (tabletSize) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                setContentView(R.layout.splash_screen_landscape);
            } else setContentView(R.layout.splash_screen_portrait);
        } else {
            setContentView(R.layout.splash_screen_portrait);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        }
        new ParseAuditoriumsGroupsTeachers().execute();
    }

    // Parsing, serializing and saving content gained from server
    private class ParseAuditoriumsGroupsTeachers extends AsyncTask<Void, Void, Boolean> {

        final String GROUPS_KEY = "groups";
        final String TEACHERS_KEY = "teachers";
        final String AUDITORIUMS_KEY = "auditoriums";
        SharedPreferences sharedPreferences;
        String result = null;

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                // Download HTML document and parse `select` objects
                Document document = Jsoup.connect("http://schedule.sumdu.edu.ua/").get();
                Element auditoriums = document.select("#auditorium").first();
                Element groups = document.select("#group").first();
                Element teachers = document.select("#teacher").first();

                // Serialize options of `select` DOM objects
                String serializedAuditoriums = parseListObjects(auditoriums);
                String serializedGroups = parseListObjects(groups);
                String serializedTeachers = parseListObjects(teachers);

                DataManager dataManager = DataManager.getInstance();
                dataManager.context = getApplicationContext();

                // Save lists of Auditoriums, Groups and Teachers to SharedPreferences
                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString(AUDITORIUMS_KEY, serializedAuditoriums);
                editor.putString(GROUPS_KEY, serializedGroups);
                editor.putString(TEACHERS_KEY, serializedTeachers);
                editor.apply();

                result = "Success";
                return true;

            } catch (IOException e) {
                e.printStackTrace();
                result = null;

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                if (result != null) {
                    intent.putExtra("Connection", true);
                } else {
                    intent.putExtra("Connection", false);
                }
                startActivity(intent);
                finish();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            if (result != null) {
                intent.putExtra("Connection", true);
            } else {
                intent.putExtra("Connection", false);
            }
            startActivity(intent);
            finish();
        }

        // Validating then adding elements; serializing ArrayList<ListObject> to string
        private String parseListObjects(Element element) {
            // Loops through options of HTML select element and map entries to ListObjects
            ArrayList<ListObject> records = new ArrayList<ListObject>();
            for (Element option : element.children()) {
                // Validate pairTitleAndType on import
                String title = option.text().trim();
                if (title.length() > 1) {
                    ListObject newObject = new ListObject();
                    newObject.id = option.attr("value");
                    newObject.title = title;
                    records.add(newObject);
                }
            }
            Collections.sort(records, new ListObjectTitleComparator());

            // Serialize ArrayList<ListObject> to string
            Gson gson = new Gson();
            String jsonString = gson.toJson(records);
            return jsonString;
        }
    }
}