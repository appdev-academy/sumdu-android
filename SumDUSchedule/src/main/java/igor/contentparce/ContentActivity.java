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


//        String[] temp = new String[content.size()];
//        int index = 0;
//        for (Object value : content) {
//            temp[index] = (String) value;
//            index++;
//        }
//
//        Log.d(TAG, "temp:" + temp);

//        }
    }

    private ArrayList<ListContentObject> parseStringToArrayList(String stringToParse) {
        Type itemsListType = new TypeToken<List<ListContentObject>>(){}.getType();
        ArrayList<ListContentObject> contentRecords = new Gson().fromJson(stringToParse, itemsListType);
        return contentRecords;
    }

    private void setContentListView() {

//        String[] pairTitle = { "Пристр. цифр. електр. (лабораторна робота)", "Марья", "Петр", "Антон", "Даша", "Борис",
//                "Костя", "Игорь", "Иван", "Марья", "Петр", "Антон" };;
//        String[] pairTime = { "11:25 - 12:45", "Бухгалтер", "Программер",
//                "Программер", "Бухгалтер", "Директор", "Программер", "Охранник", "Программер", "Бухгалтер", "Программер",
//                "Программер" };
//        String[] auditorium = { "ЕТ314", "Бухгалтер1", "Программер1",
//                "Программер1", "Бухгалтер1", "Директор1", "Программер1", "Охранник1", "Программер1", "Бухгалтер1", "Программер1",
//                "Программер1" };
//        String[] lecturer = { "Дрозденко Олексій Олександрович", "Марья", "Петр", "Антон", "Даша", "Борис",
//                "Костя", "Игорь", "Иван", "Марья", "Петр", "Антон" };


        String[] pairTitle = new String[content.size()];
        for(int i = 0; i < content.size(); i++){
            pairTitle[i] = content.get(i).pairTitle;
            i++;
        }

        String[] pairType = new String[content.size()];
        for(int i = 0; i < content.size(); i++){
            pairType[i] = content.get(i).pairType;
            i++;
        }

        String[] pairTime = new String[content.size()];
        for(int i = 0; i < content.size(); i++){
            pairTime[i] = content.get(i).pairTime;
            i++;
        }

        String[] auditorium = new String[content.size()];
        for(int i = 0; i < content.size(); i++){
            auditorium[i] = content.get(i).auditorium;
            i++;
        }

        String[] lecturer = new String[content.size()];
        for(int i = 0; i < content.size(); i++){
            lecturer[i] = content.get(i).lecturer;
            i++;
        }


        int[] colors = new int[2];

        colors[0] = Color.parseColor("#559966CC");
        colors[1] = Color.parseColor("#55336699");

        LinearLayout linLayout = (LinearLayout) findViewById(R.id.linLayout);

        LayoutInflater ltInflater = getLayoutInflater();

        for (int i = 0; i < pairTitle.length; i++) {
            Log.d("myLogs", "i = " + i);
            View item = ltInflater.inflate(R.layout.item, linLayout, false);
            TextView tvName = (TextView) item.findViewById(R.id.tvPairTitle_pairType);
            tvName.setText(pairTitle[i] + "(" + pairType[i] + ")");
            TextView tvPosition = (TextView) item.findViewById(R.id.tvPairTimeAndAuditorium);
            tvPosition.setText(pairTime[i] + "  *  " + auditorium[i]);
            TextView tvSalary = (TextView) item.findViewById(R.id.tvLecturer);
            tvSalary.setText(String.valueOf(lecturer[i]));
            item.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
            item.setBackgroundColor(colors[i % 2]);
            linLayout.addView(item);
        }

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