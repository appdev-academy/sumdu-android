package igor.contentparce;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ContentActivity extends Activity {

    String TAG = "ContentActivity";

    SharedPreferences sharedPreferencesContent;

    private ListView contentListView;

    private ArrayList<ListContentObject> content;

    final String CONTENT_KEY = "CONTENT_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        setContentAdapter();
        new ParseTask().execute();
        readDataFromSharedPreferences();



    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
        return true;

    }

    private void readDataFromSharedPreferences() {
        sharedPreferencesContent = getPreferences(MODE_PRIVATE);
//        if (sharedPreferencesContent.contains(CONTENT_KEY)) {
//            String fetchResult = sharedPreferencesContent.getString(CONTENT_KEY, "");
//            content = parseStringToArrayList(fetchResult);
//            Log.d(TAG, "fetchResult1:" + fetchResult);
//        } else {
            new ParseTask().execute();
            String fetchResult = sharedPreferencesContent.getString(CONTENT_KEY, "");
            content = parseStringToArrayList(fetchResult);
            Log.d(TAG, "fetchResult2:" + fetchResult);

//        }
    }

    private ArrayList<ListContentObject> parseStringToArrayList(String stringToParse) {
        Type itemsListType = new TypeToken<List<ListContentObject>>(){}.getType();
        ArrayList<ListContentObject> contentRecords = new Gson().fromJson(stringToParse, itemsListType);
        return contentRecords;
    }

    private void setContentAdapter() {
        ListView contentListView = (ListView)findViewById(R.id.contentListView);

        readDataFromSharedPreferences();

        ArrayAdapter<ListContentObject> contentListViewAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, content);
        contentListView.setAdapter(contentListViewAdapter);


    }

    class ParseTask extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";
        String jsonString = "";

        @Override
        protected String doInBackground(Void... params) {

            try {

                Intent intent = getIntent();
                String downloadURL = intent.getStringExtra("downloadURL");
                URL url = new URL(downloadURL);
                Log.d(TAG, "url:" + url);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                resultJson = buffer.toString();



            } catch(Exception e){
                e.printStackTrace();
            }

            sharedPreferencesContent = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferencesContent.edit();
            Log.d(TAG, "sharedPreferencesContent:" + sharedPreferencesContent.getString(CONTENT_KEY, ""));

            try {
                JSONArray jsonArray = new JSONArray(resultJson);
                Log.d(TAG, "jsonArray:" + jsonArray);
                ArrayList<ListContentObject> contentRecords = new ArrayList<ListContentObject>();

                for (int i = 0; i < jsonArray.length(); i++)
                {
                    ListContentObject newContentObject = new ListContentObject();
                    newContentObject.date = jsonArray.getJSONObject(i).getString("DATE_REG");
                    newContentObject.dayOfTheWeek = jsonArray.getJSONObject(i).getString("NAME_WDAY");
                    newContentObject.pairNumber = jsonArray.getJSONObject(i).getString("NAME_PAIR");
                    newContentObject.pairTime = jsonArray.getJSONObject(i).getString("TIME_PAIR");
                    newContentObject.lecturer = jsonArray.getJSONObject(i).getString("NAME_FIO");
                    newContentObject.auditorium = jsonArray.getJSONObject(i).getString("NAME_AUD");
                    newContentObject.group = jsonArray.getJSONObject(i).getString("NAME_GROUP");
                    newContentObject.pairType = jsonArray.getJSONObject(i).getString("NAME_STUD");
                    newContentObject.pairTitle = jsonArray.getJSONObject(i).getString("ABBR_DISC");
                    contentRecords.add(newContentObject);
                }

                Gson gson = new Gson();
                String jsonContentString = gson.toJson(contentRecords);

                editor.putString(CONTENT_KEY, jsonContentString);
                editor.apply();
                Log.d(TAG, "sharedPreferencesContent:" + sharedPreferencesContent.getString(CONTENT_KEY, ""));

            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "JSONException:");

            }

            return resultJson;
        }



        @Override
        protected void onPostExecute(String stringJson) {
            super.onPostExecute(stringJson);


        }
    }


}