package igor.contentparce;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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
    private ArrayList<ListContentObject> output;

    final String CONTENT_KEY = "CONTENT_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_list_view);

        // Setting actionBar with "Back" button
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Getting pairTitleAndType from pressed element for using as activity pairTitleAndType
        Intent intent = getIntent();
        setTitle(intent.getStringExtra("content_title"));


//        ArrayList<SearchResults> searchResults = GetSearchResults();

//        final ListView lv1 = (ListView) findViewById(R.id.ListView01);
//        lv1.setAdapter(new CustomAdapter(this, searchResults));

//        setContentAdapter();
        new ParseTask().execute();
        readDataFromSharedPreferences();
        setContentListView();

    }

//    private ArrayList<SearchResults> GetSearchResults(){
//        ArrayList<SearchResults> results = new ArrayList<SearchResults>();
//
//
//        SearchResults sr1 = new SearchResults();
//        sr1.setName("output");
//        sr1.setCityState("Dallas, TX");
//        sr1.setPhone("214-555-1234");
//        results.add(sr1);
//
//        sr1 = new SearchResults();
//        sr1.setName("Jane Doe");
//        sr1.setCityState("Atlanta, GA");
//        sr1.setPhone("469-555-2587");
//        results.add(sr1);
//
//        sr1 = new SearchResults();
//        sr1.setName("Steve Young");
//        sr1.setCityState("Miami, FL");
//        sr1.setPhone("305-555-7895");
//        results.add(sr1);
//
//        sr1 = new SearchResults();
//        sr1.setName("Fred Jones");
//        sr1.setCityState("Las Vegas, NV");
//        sr1.setPhone("612-555-8214");
//        results.add(sr1);
//
//        return results;
//    }

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
        Log.d(TAG, "CONTENT:" + content);


//        }
    }

    private ArrayList<ListContentObject> parseStringToArrayList(String stringToParse) {
        Type itemsListType = new TypeToken<List<ListContentObject>>(){}.getType();
        ArrayList<ListContentObject> contentRecords = new Gson().fromJson(stringToParse, itemsListType);
        return contentRecords;
    }

    private void setContentListView() {


        String[] pairTitle = new String[content.size()];
        for(int i = 0; i < content.size(); i++){
            if (content.get(i).pairTitle != null) {
                pairTitle[i] = content.get(i).pairTitle;
                i++;
            }
        }

        String[] pairType = new String[content.size()];
        for(int i = 0; i < content.size(); i++){
            if (content.get(i).pairType != null) {
                pairType[i] = content.get(i).pairType;
                i++;
            }
        }

        String[] pairTime = new String[content.size()];
        for(int i = 0; i < content.size(); i++){
            if (content.get(i).pairTime != null) {
                pairTime[i] = content.get(i).pairTime;
                i++;
            }
        }

        String[] auditorium = new String[content.size()];
        for(int i = 0; i < content.size(); i++){
            if (content.get(i).auditorium != null) {
                auditorium[i] = content.get(i).auditorium;
                i++;
            }
        }

        String[] lecturer = new String[content.size()];
        for(int i = 0; i < content.size(); i++){
            if (content.get(i).lecturer != null) {
                lecturer[i] = content.get(i).lecturer;
                i++;
            }
        }



        String[] dayOfTheWeek = new String[content.size()];
        String[] date = new String[content.size()];
        for(int i = 0; i < content.size(); i++){
            if(content.get(i).date != null && i == 0) {
                date[i] = content.get(i).date;
                dayOfTheWeek[i] = content.get(i).dayOfTheWeek;
                i++;
            } else

            if (content.get(i).date != null && !content.get(i).date.equals(content.get(i-1).date)) {
                date[i] = content.get(i).date;
                dayOfTheWeek[i] = content.get(i).dayOfTheWeek;
                i++;
            }
        }


        LinearLayout linLayout = (LinearLayout) findViewById(R.id.linLayout);

        LayoutInflater ltInflater = getLayoutInflater();


        for (int i = 0; i < date.length; i++) {

            View dayItem = ltInflater.inflate(R.layout.day, linLayout, false);
            TextView tvDate = (TextView) dayItem.findViewById(R.id.tvDate);

                tvDate.setText(date[i]);
                TextView tvDayOfTheWeek = (TextView) dayItem.findViewById(R.id.tvDayOfTheWeek);
                tvDayOfTheWeek.setText(dayOfTheWeek[i]);
                dayItem.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
                dayItem.setBackgroundColor(0x749531DA);
                linLayout.addView(dayItem);

            if (content.get(i).date.equals(date[i]) && pairTitle[i] != null) {

                View item = ltInflater.inflate(R.layout.item, linLayout, false);
                TextView tvPairTitleAndType = (TextView) item.findViewById(R.id.tvPairTitleAndPairType);


                tvPairTitleAndType.setText(pairTitle[i] + " (" + pairType[i] + ")");
                TextView tvPairTimeAndAuditorium = (TextView) item.findViewById(R.id.tvPairTimeAndAuditorium);
                tvPairTimeAndAuditorium.setText(pairTime[i] + " *  " + auditorium[i]);
                TextView tvLecturer = (TextView) item.findViewById(R.id.tvLecturer);
                tvLecturer.setText(lecturer[i]);
                item.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
                item.setBackgroundColor(0x559966CC);
                linLayout.addView(item);

            }
        }


//        for (int i = 0; i < date.length; i++) {
//
//            View dayItem = ltInflater.inflate(R.layout.day, linLayout, false);
//            TextView tvDate = (TextView) dayItem.findViewById(R.id.tvDate);
//
//
//
//            if (i == 0 && date[i] != null) {
//
//                tvDate.setText(date[i]);
//                Log.d(TAG, "[i] = " + i);
//                TextView tvDayOfTheWeek = (TextView) dayItem.findViewById(R.id.tvDayOfTheWeek);
//                tvDayOfTheWeek.setText(dayOfTheWeek[i]);
//                dayItem.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
//                dayItem.setBackgroundColor(0x749531DA);
//                linLayout.addView(dayItem);
//            } else
//
//            if (date[i] != null) {
//
////                temp = date[i];
//                tvDate.setText(date[i]);
//                Log.d(TAG, "DATE[i]" + date[i]);
//                TextView tvDayOfTheWeek = (TextView) dayItem.findViewById(R.id.tvDayOfTheWeek);
//                tvDayOfTheWeek.setText(dayOfTheWeek[i]);
//                dayItem.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
//                dayItem.setBackgroundColor(0x749531DA);
//                linLayout.addView(dayItem);
//            }
//
//
//                for (int e = 0; e < pairTitle.length; e++) {
//
//                        View item = ltInflater.inflate(R.layout.item, linLayout, false);
//                        TextView tvPairTitleAndType = (TextView) item.findViewById(R.id.tvPairTitleAndPairType);
//
//                    if (pairTitle[e] != null) {
//                        tvPairTitleAndType.setText(pairTitle[e] + " (" + pairType[e] + ")");
//                        TextView tvPairTimeAndAuditorium = (TextView) item.findViewById(R.id.tvPairTimeAndAuditorium);
//                        tvPairTimeAndAuditorium.setText(pairTime[e] + " *  " + auditorium[e]);
//                        TextView tvLecturer = (TextView) item.findViewById(R.id.tvLecturer);
//                        tvLecturer.setText(lecturer[e]);
//                        item.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
//                        item.setBackgroundColor(0x559966CC);
//                        linLayout.addView(item);
//                    }
//                }
//        }

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