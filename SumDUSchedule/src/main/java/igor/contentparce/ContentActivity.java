package igor.contentparce;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

public class ContentActivity extends Activity {

    String TAG = "ContentActivity";

    SharedPreferences sPreferences;

    final String NAME_WDAY_KEY = "NAME_WDAY";
    final String NAME_PAIR_KEY = "NAME_PAIR";
    final String TIME_PAIR_KEY = "TIME_PAIR";
    final String NAME_FIO_KEY = "NAME_FIO";
    final String NAME_AUD_KEY = "NAME_AUD";
    final String NAME_GROUP_KEY = "NAME_GROUP";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        new ParseTask().execute();


        Intent intent = getIntent();
        String objectType = intent.getStringExtra("objectType");


//        setContentAdapter();

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
        return true;

    }


//    private void setContentAdapter() {
//        ListView contentListView = (ListView)findViewById(R.id.contentListView);
//
//        ArrayAdapter<ListObject> contentListViewAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, temp);
//        contentListView.setAdapter(contentListViewAdapter);
//    }

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

            return resultJson;
        }



        @Override
        protected void onPostExecute(String stringJson) {
            super.onPostExecute(stringJson);
            Log.d(TAG, "strJson:" + stringJson);

            sPreferences = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = sPreferences.edit();

            try {

            JSONArray jsonArray = new JSONArray(stringJson);
            Log.d(TAG, "jsonArray:" + jsonArray);
                ArrayList<ListObject> contentRecords = new ArrayList<ListObject>();

                for (int i = 0; i < jsonArray.length(); i++)
                {
                    ListObject newContentObject = new ListObject();
                    newContentObject.title = jsonArray.getJSONObject(i).getString("NAME_WDAY");;
                    contentRecords.add(newContentObject);

                }

                Gson gson = new Gson();
                String jsonContentString = gson.toJson(contentRecords);

                editor.putString(NAME_WDAY_KEY, jsonContentString);
                editor.apply();
                Log.d(TAG, "sPreferences:" + sPreferences.getString(NAME_WDAY_KEY, ""));



            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "JSONException:");

            }
        }
    }


}