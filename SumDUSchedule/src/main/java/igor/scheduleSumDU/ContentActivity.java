package igor.scheduleSumDU;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

public class ContentActivity extends Activity {

    String TAG = "ContentActivity";

    public SharedPreferences sharedPreferencesContent;

    public ArrayList<ListContentObject> content;

    private int connectionStatus;

    public Context contentContext;

    ProgressDialog progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_list_view);

        DataManager dataManager = DataManager.getInstance();
        dataManager.context = getApplicationContext();
        contentContext = getApplicationContext();

        progressDialog();

        // Setting actionBar with "Back" button
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        getActionBar().setIcon(
                new ColorDrawable(ContextCompat.getColor(this, android.R.color.transparent)));

        // Getting title from pressed element for using as activity title
        Intent intent = getIntent();
        setTitle(intent.getStringExtra("content_title"));
        Log.d(TAG, "INTENT:" + intent);

//        if (checkConnection() == 0) {

        new ParseTask().execute();

//            content = dataManager.readDataFromSharedPreferences(intent);
//            setContentListView();
//            progress.dismiss();

//        } else {


//        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.search, menu);
//        MenuItem item = menu.findItem(R.id.menu_search);

        getMenuInflater().inflate(R.menu.menu_content_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Activating "Back" button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:

                Intent homeIntent = new Intent(this, MainActivity.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
                return true;

            case R.id.refresh_button:

                    progressDialog();
                    new ParseTask().execute();

                return true;


        }
        return super.onOptionsItemSelected(item);
    }


    // Setting up and starting progress dialog
    public void progressDialog() {
        progress = new ProgressDialog(this);
        progress.setTitle("Загрузка");
        progress.setMessage("Получение данных");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
    }


    // Setting listview considering to chosen element
    private void setContentListView() {

        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat timeFormatter = new SimpleDateFormat("kk:mm");

        String[] pairTitle = new String[content.size()];
        for(int i = 0; i < content.size()-1; i++){
            if (content.get(i).pairTitle != null) {
                pairTitle[i] = content.get(i).pairTitle;
//                Log.d(TAG, "PAIR_TITLE:" + content.get(i).pairTitle);
            }
        }

        String[] pairType = new String[content.size()];
        for(int i = 0; i < content.size()-1; i++){
            if (content.get(i).pairType != null) {
                pairType[i] = content.get(i).pairType;
            }
        }

        String[] pairTime = new String[content.size()];
        for(int i = 0; i < content.size()-1; i++){
            if (timeFormatter.format(content.get(i).fullDate) != null) {
                try {
                    Date date = timeFormatter.parse(timeFormatter.format(content.get(i).fullDate));
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.add(Calendar.MINUTE, 80);
                    String pairBeginingAndEnding = (timeFormatter.format(content.get(i).fullDate) + " - " + timeFormatter.format(calendar.getTime()));
                    pairTime[i] = pairBeginingAndEnding;

                } catch (ParseException e) {
                    Log.d(TAG, "ParseException");
                }
            }
        }

        String[] auditorium = new String[content.size()];
        for(int i = 0; i < content.size()-1; i++){
            if (content.get(i).auditorium != null) {
                auditorium[i] = content.get(i).auditorium;
            }
        }

        String[] lecturer = new String[content.size()];
        for(int i = 0; i < content.size()-1; i++){
            if (content.get(i).lecturer != null) {
                lecturer[i] = content.get(i).lecturer;
            }
        }

        String[] dayOfTheWeek = new String[content.size()];
        String[] date = new String[content.size()];
        for(int i = 0; i < content.size()-1; i++){
            if(dateFormatter.format(content.get(i).fullDate) != null && i == 0) {
                date[i] = dateFormatter.format(content.get(i).fullDate);
//                Log.d(TAG, "DATE:" + date[i]);
                dayOfTheWeek[i] = content.get(i).dayOfTheWeek;
            } else

            if (dateFormatter.format(content.get(i).fullDate) != null && !dateFormatter.format(content.get(i).fullDate).equals(dateFormatter.format(content.get(i-1).fullDate))) {
                date[i] = dateFormatter.format(content.get(i).fullDate);
                dayOfTheWeek[i] = content.get(i).dayOfTheWeek;
//                Log.d(TAG, "DATE:" + date[i]);
            }
        }


        String[] dateMatch = new String[content.size()];
        for(int i = 0; i < content.size()-1; i++){
            if(dateFormatter.format(content.get(i).fullDate) != null) {
                dateMatch[i] = dateFormatter.format(content.get(i).fullDate);
//                Log.d(TAG, "DATE_MATCH:" + dateMatch[i]);
            }
        }

        LinearLayout linLayout = (LinearLayout) findViewById(R.id.linLayout);
        LayoutInflater ltInflater = getLayoutInflater();

        for (int i = 0; i < date.length; i++) {

            View dayItem = ltInflater.inflate(R.layout.day, linLayout, false);
            TextView tvDate = (TextView) dayItem.findViewById(R.id.tvDate);

            if (i == 0 || date[i] != null) {

                tvDate.setText(date[i]);
                TextView tvDayOfTheWeek = (TextView) dayItem.findViewById(R.id.tvDayOfTheWeek);
                tvDayOfTheWeek.setText(dayOfTheWeek[i]);
                dayItem.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
                dayItem.setBackgroundColor(Color.LTGRAY);
                linLayout.addView(dayItem);
            }

            if (dateFormatter.format(content.get(i).fullDate).equals(dateMatch[i]) && pairTitle[i] != null) {

                View item = ltInflater.inflate(R.layout.item, linLayout, false);
                TextView tvPairTitleAndType = (TextView) item.findViewById(R.id.tvPairTitleAndPairType);

                if (pairType[i].trim().length() <= 1) {
                    tvPairTitleAndType.setText(pairTitle[i]);
                } else tvPairTitleAndType.setText(pairTitle[i] + " (" + pairType[i] + ")");

                TextView tvPairTimeAndAuditorium = (TextView) item.findViewById(R.id.tvPairTimeAndAuditorium);

                if (auditorium[i].trim().length() <= 1) {
                    tvPairTimeAndAuditorium.setText(pairTime[i]);
                } else tvPairTimeAndAuditorium.setText(pairTime[i] + "  *  " + auditorium[i]);

                TextView tvLecturer = (TextView) item.findViewById(R.id.tvLecturer);
                tvLecturer.setText(lecturer[i]);
                item.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
                item.setBackgroundColor(Color.WHITE);
                linLayout.addView(item);
//                    Log.d(TAG,"ITEM  " + i);
            }
        }
    }


    // Parsing and saving gained data into shared preferences
    class ParseTask extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        @Override
        protected String doInBackground(Void... params) {

            try {

                Intent intent = getIntent();
                String downloadURL = intent.getStringExtra("downloadURL");
                URL url = new URL(downloadURL);
                Log.d(TAG, "downloadURL: " + downloadURL);

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
                Log.d(TAG, "WTF!");

                e.printStackTrace();
            }

            sharedPreferencesContent = PreferenceManager.getDefaultSharedPreferences(contentContext);
            SharedPreferences.Editor editor = sharedPreferencesContent.edit();


            try {
                JSONArray jsonArray = new JSONArray(resultJson);
                ArrayList<ListContentObject> contentRecords = new ArrayList<ListContentObject>();

                DateFormat parser = new SimpleDateFormat("dd.MM.yyyy kk:mm");

                for (int i = 0; i < jsonArray.length(); i++)
                {
                    ListContentObject newContentObject = new ListContentObject();
//                    newContentObject.pairTime = jsonArray.getJSONObject(i).getString("TIME_PAIR");

                    char[] temp1 = new char[5];
                    jsonArray.getJSONObject(i).getString("TIME_PAIR").getChars(0, 5, temp1, 0);
                    String dateBuilder = jsonArray.getJSONObject(i).getString("DATE_REG") + " " + new String(temp1);
//                    Log.d(TAG, "dateBuilder:" + dateBuilder);

                    newContentObject.fullDate = parser.parse(dateBuilder);
//                    Log.d(TAG, "FULLDATE:" + newContentObject.fullDate);

                    newContentObject.dayOfTheWeek = jsonArray.getJSONObject(i).getString("NAME_WDAY");
                    newContentObject.lecturer = jsonArray.getJSONObject(i).getString("NAME_FIO");
                    newContentObject.auditorium = jsonArray.getJSONObject(i).getString("NAME_AUD");
                    newContentObject.group = jsonArray.getJSONObject(i).getString("NAME_GROUP");
                    newContentObject.pairType = jsonArray.getJSONObject(i).getString("NAME_STUD");
                    newContentObject.pairTitle = jsonArray.getJSONObject(i).getString("ABBR_DISC");
                    contentRecords.add(newContentObject);
                }

                Gson gson = new Gson();
                String jsonContentString = gson.toJson(contentRecords);

                Intent intent = getIntent();
                editor.putString(intent.getStringExtra("content_title"), jsonContentString);
                editor.apply();

            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "JSONException:");

            }

            return resultJson;
        }



        @Override
        protected void onPostExecute(String stringJson) {
            super.onPostExecute(stringJson);
            DataManager dataManager = DataManager.getInstance();
            Intent intent = getIntent();

            content = dataManager.readDataFromSharedPreferences(intent);
            setContentListView();
            progress.dismiss();
            Toast.makeText(getApplicationContext(),
                            "Розклад оновлено.", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Refreshed");
        }
    }


}