package igor.contentparce;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ContentActivity extends Activity {

    String TAG = "ContentActivity";

    SharedPreferences sharedPreferencesContent;

    private ListView contentListView;

    private ArrayList<ListContentObject> content;
    private ArrayList<ListContentObject> output;

    final String CONTENT_KEY = "CONTENT_KEY";

    ProgressDialog progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_list_view);

        progressDialog();

        // Setting actionBar with "Back" button
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Getting title from pressed element for using as activity title
        Intent intent = getIntent();
        setTitle(intent.getStringExtra("content_title"));


//        setContentAdapter();

        new ParseTask().execute();
//        readDataFromSharedPreferences();
//        setContentListView();

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

        String fetchResult = sharedPreferencesContent.getString(CONTENT_KEY, "");
        content = parseStringToArrayList(fetchResult);

//        }
    }

    private void progressDialog() {
        progress = new ProgressDialog(this);
        progress.setTitle("Загрузка");
        progress.setMessage("Получение данных");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
    }

    private ArrayList<ListContentObject> parseStringToArrayList(String stringToParse) {
        Type itemsListType = new TypeToken<List<ListContentObject>>(){}.getType();
        ArrayList<ListContentObject> contentRecords = new Gson().fromJson(stringToParse, itemsListType);
        Collections.sort(contentRecords, new ListContentObjectComparator());

        return contentRecords;
    }

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
                Log.d(TAG, "DATE:" + date[i]);
                dayOfTheWeek[i] = content.get(i).dayOfTheWeek;
            } else

            if (dateFormatter.format(content.get(i).fullDate) != null && !dateFormatter.format(content.get(i).fullDate).equals(dateFormatter.format(content.get(i-1).fullDate))) {
                date[i] = dateFormatter.format(content.get(i).fullDate);
                dayOfTheWeek[i] = content.get(i).dayOfTheWeek;
                Log.d(TAG, "DATE:" + date[i]);
            }
        }

        String[] dateMatch = new String[content.size()];
        for(int i = 0; i < content.size()-1; i++){
            if(dateFormatter.format(content.get(i).fullDate) != null) {
                dateMatch[i] = dateFormatter.format(content.get(i).fullDate);
                Log.d(TAG, "DATE_MATCH:" + dateMatch[i]);
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
                dayItem.setBackgroundColor(0x749531DA);
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
                    } else tvPairTimeAndAuditorium.setText(pairTime[i] + " *  " + auditorium[i]);

                    TextView tvLecturer = (TextView) item.findViewById(R.id.tvLecturer);
                    tvLecturer.setText(lecturer[i]);
                    item.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
                    item.setBackgroundColor(0x559966CC);
                    linLayout.addView(item);
                    Log.d(TAG,"ITEM  " + i);
            }
        }


    }

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
                    Log.d(TAG, "dateBuilder:" + dateBuilder);

                    newContentObject.fullDate = parser.parse(dateBuilder);
                    Log.d(TAG, "FULLDATE:" + newContentObject.fullDate);


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

            readDataFromSharedPreferences();
            setContentListView();
            progress.dismiss();
        }
    }


}