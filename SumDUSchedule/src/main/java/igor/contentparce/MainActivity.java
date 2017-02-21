package igor.contentparce;

import android.app.Fragment;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TabHost;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MainActivity extends TabActivity {
// TabActivity

    String TAG = "MainActivity";

    SharedPreferences sharedPreferences;

    // Static variable for history cleaning
    final int CLEAN = 1;

    private int connectionStatus = 1;

    // Special keys for values
    final String GROUPS_KEY = "groups";
    final String TEACHERS_KEY = "teachers";
    final String AUDITORIUMS_KEY = "auditoriums";
    final String HISTORY_KEY = "history";

    private SearchView searchView;
    private ListView listView;
    private TabHost tabHost;
    private String searchQuery = "";
    private String content_title = null;

    // ArrayLists for getting from sharedPreferences unfiltered elements
    private ArrayList<ListObject> history;
    private ArrayList<ListObject> auditoriums;
    private ArrayList<ListObject> groups;
    private ArrayList<ListObject> teachers;

    // ArrayLists for filtered with query elements
    private ArrayList<ListObject> filteredHistory;
    private ArrayList<ListObject> filteredAuditoriums;
    private ArrayList<ListObject> filteredGroups;
    private ArrayList<ListObject> filteredTeachers;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        listView = (ListView) findViewById(R.id.lvContent);
        registerForContextMenu(listView);
        setOnItemClickListener();

        checkConnection();
        if (connectionStatus != 0) {
            new ParseAuditoriumsGroupsTeachers().execute();
        }

        readDataFromSharedPreferences();
        setupTabBar();
        filterDataWithQuery(searchQuery);
        setAdapterByContent();
    }

    // Adding and setting up SearchView
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);
        MenuItem item = menu.findItem(R.id.menu_search);
        searchView = (SearchView) item.getActionView();

        if (searchView == null) {
            Log.d(TAG, "Search view is null");
        } else {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    searchQuery = newText;
                    filterDataWithQuery(searchQuery);

                    return false;
                }
            });
        }
        return super.onCreateOptionsMenu(menu);
    }

    // Creating context menu for deleting history from shared preferences
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        if (tabHost.getCurrentTabTag().equals("history")) {
            switch (view.getId()) {
                case R.id.lvContent:
                    menu.setHeaderTitle("History");
                    menu.add(0, CLEAN, 0, "Очистить историю");
                    break;
            }
        }
    }

    @Override
    public boolean onContextItemSelected (MenuItem item){
        switch (item.getItemId()) {
            case CLEAN:
                cleanHistoryInSharedPreferences();
                checkConnection();
                if(connectionStatus != 0) {
                    new ParseAuditoriumsGroupsTeachers().execute();
                }
                    setAdapterByContent();
                break;
        }

        return super.onContextItemSelected(item);
    }

    // Adding tab bar
    public void setupTabBar() {
        tabHost = (TabHost) findViewById(android.R.id.tabhost);
        // initialization
        tabHost.setup();

        TabHost.TabSpec tabSpec;

        tabSpec = tabHost.newTabSpec("history");
        tabSpec.setIndicator("", getResources().getDrawable(R.drawable.tab_icon_selector));
        tabSpec.setContent(R.id.lvContent);
        tabHost.addTab(tabSpec);

        // adding tab and defining tag
        tabSpec = tabHost.newTabSpec("teachers");
        // tab pairTitleAndType
        tabSpec.setIndicator("Викладач");
        // specifying component id from FrameLayout to put as filling
        tabSpec.setContent(R.id.lvContent);
        // adding root element
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("groups");
        tabSpec.setIndicator("Група");
        tabSpec.setContent(R.id.lvContent);
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("auditoriums");
        tabSpec.setIndicator("Аудиторія");
        tabSpec.setContent(R.id.lvContent);
        tabHost.addTab(tabSpec);

        tabHost.getTabWidget().getChildAt(0).getLayoutParams().width = 5;


        // This tab will be chosen as default
        tabHost.setCurrentTabByTag("groups");

        // handler of tab change
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            public void onTabChanged(String tabId) {

                filterDataWithQuery(searchQuery);
                setAdapterByContent();
                checkConnection();
                if(connectionStatus != 0) {
                    new ParseAuditoriumsGroupsTeachers().execute();
                }
            }
        });
    }

    // Filtering data with entered query
    private void filterDataWithQuery(String query) {
        if (query == null || query == "") {
            filteredAuditoriums = auditoriums;
            filteredGroups = groups;
            filteredTeachers = teachers;
            filteredHistory = history;
            setAdapterByContent();
            return;
        } else

            // Filter Auditoriums, Groups, Teachers and History
            filteredAuditoriums = filterArrayListWithQuery(auditoriums, query);
        filteredGroups = filterArrayListWithQuery(groups, query);
        filteredTeachers = filterArrayListWithQuery(teachers, query);
        filteredHistory =  filterArrayListWithQuery(history, query);
        setAdapterByContent();
    }

    // OnItemClickListener for listView elements
    private void setOnItemClickListener () {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String contentID = "";
                String chosenID = "";

                checkConnection();

                if (tabHost.getCurrentTabTag().equals("auditoriums")) {
                    try {
                        ListObject auditoriums = filteredAuditoriums.get(position);
                        auditoriums.objectType = "id_aud";
                        contentID = auditoriums.objectType;
                        chosenID = auditoriums.id;
                        content_title = auditoriums.title;
                        if (!history.toString().contains(auditoriums.title) && connectionStatus != 0) {
                            saveHistoryToSharedPreferences(auditoriums.id, auditoriums.title, auditoriums.objectType);
                        }
                    } catch (Exception e) {
                    }
                } else if (tabHost.getCurrentTabTag().equals("groups")) {
                    try {
                        ListObject groups = filteredGroups.get(position);
                        groups.objectType = "id_grp";
                        contentID = groups.objectType;
                        chosenID = groups.id;
                        content_title = groups.title;
                        if (!history.toString().contains(groups.title) && connectionStatus != 0) {
                            saveHistoryToSharedPreferences(groups.id, groups.title, groups.objectType);
                        }
                    } catch (Exception e) {
                    }
                } else if  (tabHost.getCurrentTabTag().equals("teachers")) {
                    try {
                        ListObject teachers = filteredTeachers.get(position);
                        teachers.objectType = "id_fio";
                        contentID = teachers.objectType;
                        chosenID = teachers.id;
                        content_title = teachers.title;
                        if (!history.toString().contains(teachers.title) && connectionStatus != 0) {
                            saveHistoryToSharedPreferences(teachers.id, teachers.title, teachers.objectType);
                        }
                    } catch (Exception e) {
                    }
                } else if (tabHost.getCurrentTabTag().equals("history"))     {
                    try {
                        ListObject history = filteredHistory.get(position);
                        contentID = history.objectType;
                        chosenID = history.id;
                        content_title = history.title;
                    } catch (Exception e) {
                    }
                }

                // Get start fullDate
                Date startDate = new Date();

                // Get end fullDate = start fullDate + 30 days
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(startDate);
                calendar.add(Calendar.DATE, 30);
                Date endDate = calendar.getTime();

                String downloadURL = scheduleURLFor(contentID, chosenID, startDate, endDate);
                if (downloadURL != null) {
                   intentToContentActivity(contentID, chosenID, startDate, endDate);
                }
            }
        });
    }

    // Checking internet connection
    public void checkConnection() {
        // запускаем проверку в новом потоке
        new Thread(new Runnable() {
            @Override
            public void run(){
                if (checkInternet()) {
                    connectionStatus = 1;
                    Log.d(TAG, "Есть подключение");
                } else {
                    connectionStatus = 0;
                    Log.d(TAG, "Нет подключения");
                }
            }
        }).start();
    }

    // Main internet connection check method
    public boolean checkInternet() {

        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        // проверка подключения
        if (activeNetwork != null && activeNetwork.isConnected()) {
            try {
                // тест доступности внешнего ресурса
                URL url = new URL("http://schedule.sumdu.edu.ua");
                HttpURLConnection urlc = (HttpURLConnection)url.openConnection();
                urlc.setRequestProperty("User-Agent", "test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1000); // Timeout в секундах
                urlc.connect();
                // статус ресурса OK
                if (urlc.getResponseCode() == 200) {
                    return true;
                }
                // иначе проверка провалилась
                return false;

            } catch (IOException e) {
                Log.d(TAG, "Ошибка проверки подключения к интернету", e);
                return false;
            }
        }

        return false;
    }

    // Intent to new Activity
    private void intentToContentActivity(String contentID, String chosenID, Date startDate, Date endDate) {

//        if (connectionStatus == 0 && tabHost.getCurrentTabTag().equals("history") || connectionStatus != 0) {
            String downloadURL = scheduleURLFor(contentID, chosenID, startDate, endDate);
            Intent intent = new Intent(this, ContentActivity.class);
            intent.putExtra("downloadURL", downloadURL);
            intent.putExtra("content_title", content_title);
            intent.putExtra("connectionStatus", connectionStatus);
            startActivity(intent);
            Log.d(TAG, "Status: " + "GO!");
//        } else {
//            Toast.makeText(getApplicationContext(),
//                    "No connection to server. You only able to use items from History tab.", Toast.LENGTH_LONG).show();
//        }
    }

    // Setting up fullDate
    private String dateToString(Date date) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
        String dateString = dateFormatter.format(date);
        return dateString;
    }

    // Building up URL for server connectionStatus
    private String scheduleURLFor(String contentID, String chosenID, Date startDate, Date endDate) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("schedule.sumdu.edu.ua")
                .appendPath("index")
                .appendPath("json")
                .appendQueryParameter(contentID, chosenID)
                .appendQueryParameter("date_beg", dateToString(startDate))
                .appendQueryParameter("date_end", dateToString(endDate));
        return builder.build().toString();
    }

    // Performing comparison element with query and getting if it's similar
    private ArrayList<ListObject> filterArrayListWithQuery(ArrayList<ListObject> array, String query) {
        ArrayList<ListObject> filteredArray = new ArrayList<ListObject>();
        for (ListObject record : array) {
            if (record.title.toLowerCase().contains(query.toLowerCase())) {
                filteredArray.add(record);
            }
        }
        return filteredArray;

    }

    // Reading data from shared preferences by special keys considering internet connection status
    private void readDataFromSharedPreferences() {
        sharedPreferences = getPreferences(MODE_PRIVATE);
        if (sharedPreferences.contains(AUDITORIUMS_KEY) && connectionStatus == 1) {
            String fetchResult = sharedPreferences.getString(AUDITORIUMS_KEY, "");
            auditoriums = parseStringToArrayList(fetchResult);
            Log.d(TAG, "1 case" );
        } else

        if (sharedPreferences.contains(AUDITORIUMS_KEY) && connectionStatus == 0) {
            String fetchResult = sharedPreferences.getString(AUDITORIUMS_KEY, "");
            String fetchCompareResult = sharedPreferences.getString(HISTORY_KEY, "");
            ArrayList<ListObject> fetchResultListObject = parseStringToArrayList(fetchResult);
            ArrayList<ListObject> fetchCompareResultListObject = parseStringToArrayList(fetchCompareResult);
            auditoriums = compareLists(fetchCompareResultListObject, fetchResultListObject);
            Log.d(TAG, "GROUPS: " + auditoriums );
        } else {
            auditoriums = new ArrayList<ListObject>();
        }

        if (sharedPreferences.contains(GROUPS_KEY) && connectionStatus == 1) {
            String fetchResult = sharedPreferences.getString(GROUPS_KEY, "");
            groups = parseStringToArrayList(fetchResult);
            Log.d(TAG, "1 case" );
        } else

            if (sharedPreferences.contains(GROUPS_KEY) && connectionStatus == 0) {
            String fetchResult = sharedPreferences.getString(GROUPS_KEY, "");
            String fetchCompareResult = sharedPreferences.getString(HISTORY_KEY, "");
            ArrayList<ListObject> fetchResultListObject = parseStringToArrayList(fetchResult);
            ArrayList<ListObject> fetchCompareResultListObject = parseStringToArrayList(fetchCompareResult);
                groups = compareLists(fetchCompareResultListObject, fetchResultListObject);
                Log.d(TAG, "GROUPS: " + groups );

                } else {
            groups = new ArrayList<ListObject>();
        }

        if (sharedPreferences.contains(TEACHERS_KEY) && connectionStatus == 1) {
            String fetchResult = sharedPreferences.getString(TEACHERS_KEY, "");
            teachers = parseStringToArrayList(fetchResult);
            Log.d(TAG, "1 case" );
        } else

        if (sharedPreferences.contains(TEACHERS_KEY) && connectionStatus == 0) {
            String fetchResult = sharedPreferences.getString(TEACHERS_KEY, "");
            String fetchCompareResult = sharedPreferences.getString(HISTORY_KEY, "");
            ArrayList<ListObject> fetchResultListObject = parseStringToArrayList(fetchResult);
            ArrayList<ListObject> fetchCompareResultListObject = parseStringToArrayList(fetchCompareResult);
            teachers = compareLists(fetchCompareResultListObject, fetchResultListObject);
            Log.d(TAG, "GROUPS: " + teachers );
        } else {
            teachers = new ArrayList<ListObject>();
        }

        if (sharedPreferences.contains(HISTORY_KEY)) {
            String fetchResult = sharedPreferences.getString(HISTORY_KEY, "");
            history = parseStringToArrayList(fetchResult);
        } else {
            history = new ArrayList<ListObject>();
        }
    }

    // Comparing objects saved in sharedpreferences and linked in history tab with with their native tabs for displaing there
    public ArrayList<ListObject> compareLists(ArrayList<ListObject> prevList, ArrayList<ListObject> modelList) {
        ArrayList<ListObject> temp = new ArrayList<ListObject>();
        for (ListObject modelListdata : modelList) {
                for (ListObject prevListdata : prevList) {
                    if (prevListdata.getTitle().equals(modelListdata.getTitle())) {
                        temp.add(prevListdata);
                    }
                }
            }
            return temp;
    }

    // Parsing Json string to arrayList Gson
    private ArrayList<ListObject> parseStringToArrayList(String stringToParse) {
        Type itemsListType = new TypeToken<List<ListObject>>(){}.getType();
        ArrayList<ListObject> records = new Gson().fromJson(stringToParse, itemsListType);
        return records;
    }

    // Setting adapter equal to content of selected tab
    private void setAdapterByContent() {
        ListView listView = (ListView)findViewById(R.id.lvContent);

        if (tabHost.getCurrentTabTag().equals("auditoriums")) {
            ArrayAdapter<ListObject> contentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filteredAuditoriums);
            listView.setAdapter(contentAdapter);
        } else if (tabHost.getCurrentTabTag().equals("groups")) {
            ArrayAdapter<ListObject> contentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filteredGroups);
            listView.setAdapter(contentAdapter);
        } else if (tabHost.getCurrentTabTag().equals("teachers")){
            ArrayAdapter<ListObject> contentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filteredTeachers);
            listView.setAdapter(contentAdapter);
        } else if (tabHost.getCurrentTabTag().equals("history")){
            ArrayAdapter<ListObject> contentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filteredHistory);
            listView.setAdapter(contentAdapter);
        }
    }

    // Saving elements added to history in sharedPreferences
    private String saveHistoryToSharedPreferences (String historySaveID, String historySaveTitle, String  historySaveObjectType) {

        Gson gson = new Gson();
        sharedPreferences = getPreferences(MODE_PRIVATE);

        ListObject historyObject = new ListObject();
        historyObject.id = historySaveID;
        historyObject.title = historySaveTitle;
        historyObject.objectType = historySaveObjectType;
        history.add(historyObject);

        String jsonHistoryString = gson.toJson(history);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(HISTORY_KEY, jsonHistoryString);
//        editor.putString(historySaveTitle, jsonHistoryString);
        editor.apply();

        return jsonHistoryString;
    }

    // Clean whole history in sharedPreferences
    private void cleanHistoryInSharedPreferences() {
        sharedPreferences = getPreferences(MODE_PRIVATE);
        Log.d(TAG, "HISTORY_KEY" + sharedPreferences.getString(HISTORY_KEY, ""));
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(HISTORY_KEY);
        editor.apply();
        Log.d(TAG, "HISTORY_KEY" + sharedPreferences.getString(HISTORY_KEY, ""));
    }

    // Parsing, serializing and saving content gained from server
    class ParseAuditoriumsGroupsTeachers extends AsyncTask<Void, Void, Boolean> {

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

                // Save lists of Auditoriums, Groups and Teachers to SharedPreferences
                sharedPreferences = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(AUDITORIUMS_KEY, serializedAuditoriums);
                editor.putString(GROUPS_KEY, serializedGroups);
                editor.putString(TEACHERS_KEY, serializedTeachers);
                editor.apply();

                return true;

            } catch (IOException e) {
                e.printStackTrace();

                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            readDataFromSharedPreferences();
            filterDataWithQuery(searchQuery);
            setAdapterByContent();

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
            Collections.sort(records, new ListObjectComparator());

            // Serialize ArrayList<ListObject> to string
            Gson gson = new Gson();
            String jsonString = gson.toJson(records);
            return jsonString;
        }

    }
}