package igor.scheduleSumDU;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
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
import java.util.Locale;

import static igor.scheduleSumDU.R.id.tvPairTimeAndAuditorium;
import static igor.scheduleSumDU.R.layout.item;


public class ContentActivity extends Activity {

    String TAG = "ContentActivity";

    public SharedPreferences sharedPreferencesContent;

    public Context contentContext;

    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    ProgressDialog progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_list_view);

        boolean tabletSize = getResources().getBoolean(R.bool.isTablet);
        if (!tabletSize) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        }

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

        new ParseTask().execute();
//        setContentListView(dataManager.readDataFromSharedPreferences(intent));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        getMenuInflater().inflate(R.menu.menu_content_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Activating "Back" button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:

                homeIntent();
                return true;

            case R.id.refresh_button:

                    progressDialog();
                    new ParseTask().execute();

                return true;

            case R.id.import_button:


                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // Setting up and starting progress dialog
    public void progressDialog() {
//        this.runOnUiThread(new Runnable() {
//
//            @Override
//            public void run() {
                progress = new ProgressDialog(ContentActivity.this);
                progress.setTitle("Загрузка");
                progress.setMessage("Отримання даних");
                progress.setCanceledOnTouchOutside(false);
                progress.show();
//            }
//        });
    }


    // Setting listview considering to chosen element
    private void setContentListView(ArrayList<ListContentObject> inputContent) {

        Locale locale = new Locale("uk", "UK");
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat dateFormatterForMonth = new SimpleDateFormat("d MMMM", locale);
        SimpleDateFormat timeFormatter = new SimpleDateFormat("kk:mm");

        Intent intent = getIntent();
        String contentType = intent.getStringExtra("content_type");
        Log.d(TAG, "content_type: " + contentType);

        String[] pairTitle = new String[inputContent.size()];
        for(int i = 0; i < inputContent.size(); i++){
            if (inputContent.get(i).pairTitle != null) {
                pairTitle[i] = inputContent.get(i).pairTitle;
//                Log.d(TAG, "PAIR_TITLE:" + content.get(i).pairTitle);
            }
        }

        String[] pairType = new String[inputContent.size()];
        for(int i = 0; i < inputContent.size(); i++){
            if (inputContent.get(i).pairType != null) {
                pairType[i] = inputContent.get(i).pairType;
            }
        }

        String[] pairTime = new String[inputContent.size()];
        for(int i = 0; i < inputContent.size(); i++){
            if (timeFormatter.format(inputContent.get(i).fullDate) != null) {
                try {
                    Date date = timeFormatter.parse(timeFormatter.format(inputContent.get(i).fullDate));
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.add(Calendar.MINUTE, 80);
                    String pairBeginningAndEnding = (timeFormatter.format(inputContent.get(i).fullDate) + " - " + timeFormatter.format(calendar.getTime()));
                    pairTime[i] = pairBeginningAndEnding;

                } catch (ParseException e) {
                    Log.d(TAG, "ParseException");
                }
            }
        }

        String[] group = new String[inputContent.size()];
        for(int i = 0; i < inputContent.size(); i++){
            if (inputContent.get(i).group != null) {
                group[i] = inputContent.get(i).group;
            }
        }

        String[] auditorium = new String[inputContent.size()];
        for(int i = 0; i < inputContent.size(); i++){
            if (inputContent.get(i).auditorium != null) {
                auditorium[i] = inputContent.get(i).auditorium;
            }
        }

        String[] lecturer = new String[inputContent.size()];
        for(int i = 0; i < inputContent.size(); i++){
            if (inputContent.get(i).lecturer != null) {
                lecturer[i] = inputContent.get(i).lecturer;
            }
        }

        String[] dayOfTheWeek = new String[inputContent.size()];
        String[] date = new String[inputContent.size()];
        for(int i = 0; i < inputContent.size(); i++){
            if(dateFormatter.format(inputContent.get(i).fullDate) != null && i == 0) {
                date[i] = dateFormatter.format(inputContent.get(i).fullDate);
//                Log.d(TAG, "DATE:" + date[i]);
                dayOfTheWeek[i] = inputContent.get(i).dayOfTheWeek;
            } else

            if (dateFormatter.format(inputContent.get(i).fullDate) != null && !dateFormatter.format(inputContent.get(i).fullDate).equals(dateFormatter.format(inputContent.get(i-1).fullDate))) {
                date[i] = dateFormatter.format(inputContent.get(i).fullDate);
                dayOfTheWeek[i] = inputContent.get(i).dayOfTheWeek;
//                Log.d(TAG, "DATE:" + date[i]);
            }
        }


        String[] dateMatch = new String[inputContent.size()];
        for(int i = 0; i < inputContent.size(); i++){
            if(dateFormatter.format(inputContent.get(i).fullDate) != null) {
                dateMatch[i] = dateFormatter.format(inputContent.get(i).fullDate);
//                Log.d(TAG, "DATE_MATCH:" + dateMatch[i]);
            }
        }

        LinearLayout linLayout = (LinearLayout) findViewById(R.id.linLayout);
        linLayout.removeAllViews();
        LayoutInflater ltInflater = getLayoutInflater();

        for (int i = 0; i < date.length; i++) {

            View dayItem = ltInflater.inflate(R.layout.day, linLayout, false);
            TextView tvDate = (TextView) dayItem.findViewById(R.id.tvDate);

            if (i == 0 || date[i] != null) {

                Log.d(TAG, "Date[i]: " + date[i]);
                try {
                    Date dateForMonth = dateFormatter.parse(date[i]);
                    tvDate.setText(dateFormatterForMonth.format(dateForMonth));
                } catch (ParseException e) {
                    Log.d(TAG, "ERROR: " + e);
                }

                TextView tvDayOfTheWeek = (TextView) dayItem.findViewById(R.id.tvDayOfTheWeek);
                tvDayOfTheWeek.setText(dayOfTheWeek[i]);
                dayItem.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
                dayItem.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.headerColor));
                linLayout.addView(dayItem);
            }

            if (dateFormatter.format(inputContent.get(i).fullDate).equals(dateMatch[i]) && pairTitle[i] != null) {

                View item = ltInflater.inflate(R.layout.item, linLayout, false);
                TextView tvPairTitleAndType = (TextView) item.findViewById(R.id.tvPairTitleAndPairType);
                TextView tvLecturer = (TextView) item.findViewById(R.id.tvLecturer);
                TextView tvPairTimeAndAuditorium = (TextView) item.findViewById(R.id.tvPairTimeAndAuditorium);

                if (pairType[i].trim().length() <= 1) {
                        tvPairTitleAndType.setText(pairTitle[i]);
                    } else tvPairTitleAndType.setText(pairTitle[i] + "\n(" + pairType[i] + ")");

                if (contentType.equals("id_grp")) {
                    if (auditorium[i].trim().length() > 1 && pairTime[i].trim().length() > 1) {
                        tvPairTimeAndAuditorium.setText(pairTime[i] + "  *  " + auditorium[i]);
                    } else if (auditorium[i].trim().length() <= 1 && pairTime[i].trim().length() > 1) {
                        tvPairTimeAndAuditorium.setText(pairTime[i]);
                    } else if (auditorium[i].trim().length() <= 1 && pairTime[i].trim().length() <= 1) {
                        tvPairTimeAndAuditorium.setText("");
                    } else if (auditorium[i].trim().length() > 1 && pairTime[i].trim().length() <= 1) {
                        tvPairTimeAndAuditorium.setText(auditorium[i]);
                    }
                    tvLecturer.setText(lecturer[i]);
                }

                if (contentType.equals("id_aud")) {
                    if (group[i].trim().length() > 1 && pairTime[i].trim().length() > 1) {
                        tvPairTimeAndAuditorium.setText(pairTime[i] + "  *  " + group[i]);
                    } else if (group[i].trim().length() <= 1 && pairTime[i].trim().length() > 1) {
                        tvPairTimeAndAuditorium.setText(pairTime[i]);
                    } else if (group[i].trim().length() <= 1 && pairTime[i].trim().length() <= 1) {
                        tvPairTimeAndAuditorium.setText("");
                    } else if (group[i].trim().length() > 1 && pairTime[i].trim().length() <= 1) {
                        tvPairTimeAndAuditorium.setText(group[i]);
                    }
                    tvLecturer.setText(lecturer[i]);
                }

                if (contentType.equals("id_fio")) {
                    if (auditorium[i].trim().length() > 1 && pairTime[i].trim().length() > 1) {
                        tvPairTimeAndAuditorium.setText(pairTime[i] + "  *  " + auditorium[i]);
                    } else if (auditorium[i].trim().length() <= 1 && pairTime[i].trim().length() <= 1) {
                        tvPairTimeAndAuditorium.setText("");
                    } else if (auditorium[i].trim().length() <= 1 && pairTime[i].trim().length() > 1) {
                        tvPairTimeAndAuditorium.setText(pairTime[i]);
                    } else if (auditorium[i].trim().length() > 1 && pairTime[i].trim().length() <= 1) {
                        tvPairTimeAndAuditorium.setText(auditorium[i]);
                    }
                    if(group[i].trim().length() > 1) {
                        tvLecturer.setText("Для " + group[i]);
                    } else tvLecturer.setText("");
                }

                item.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
                item.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.backgroundWhite));
                linLayout.addView(item);
//                    Log.d(TAG,"ITEM  " + i);
            }
        }
    }

    private void homeIntent() {

        Intent homeIntent = new Intent(this, MainActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);

    }


    // Parsing and saving gained data into shared preferences
    class ParseTask extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";
        String result = null;

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

                result = "Success";

            } catch(Exception e){

                result = null;

                progress.dismiss();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Не вдалося оновити розклад. Немає інтернет підключення.", Toast.LENGTH_LONG).show();
                    }
                });

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

                result = "Success";

            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "JSONException:");

                result = null;

            }

            return result;
        }



        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result != null) {

                DataManager dataManager = DataManager.getInstance();
                Intent intent = getIntent();

                dataManager.saveHistoryToSharedPreferences(dataManager.readHistoryFromBufferSharedPreferences());
                setContentListView(dataManager.readDataFromSharedPreferences(intent));
                progress.dismiss();
                Toast.makeText(getApplicationContext(),
                        "Розклад оновлено", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Refreshed");
            } else {
                Log.d(TAG, "ELSE");
                MainActivity mainActivity = new MainActivity();
                mainActivity.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(contentContext);
                DataManager dataManager = DataManager.getInstance();
                Intent intent = getIntent();


                if (mainActivity.sharedPreferences.getString(mainActivity.HISTORY_KEY, "").contains(intent.getStringExtra("content_title"))) {

                    dataManager.saveHistoryToSharedPreferences(dataManager.readHistoryFromBufferSharedPreferences());
                    setContentListView(dataManager.readDataFromSharedPreferences(intent));
                    progress.dismiss();
                    
                } else {

                    homeIntent();
                    progress.dismiss();
                }
            }
        }
    }


}