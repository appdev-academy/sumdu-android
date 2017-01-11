package igor.contentparce;

import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TabHost;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MainActivity extends TabActivity {

    String TAG = "MainActivity";

    SharedPreferences sharedPreferences;

    // Static variable for history cleaning
    final int CLEAN = 1;

    // Special keys for values
    final String GROUPS_KEY = "groups";
    final String TEACHERS_KEY = "teachers";
    final String AUDITORIUMS_KEY = "auditoriums";
    final String HISTORY_KEY = "history";

    private SearchView searchView;
    private ListView listView;
    private TabHost tabHost;
    private String searchQuery = "";

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
        setupTabBar();

        new ParseAuditoriumsGroupsTeachers().execute();
        readDataFromSharedPreferences();
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
                new ParseAuditoriumsGroupsTeachers().execute();
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
        // tab title
        tabSpec.setIndicator("Teachers");
        // specifying component id from FrameLayout to put as filling
        tabSpec.setContent(R.id.lvContent);
        // adding root element
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("groups");
        tabSpec.setIndicator("Groups");
        tabSpec.setContent(R.id.lvContent);
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("auditoriums");
        tabSpec.setIndicator("Auditorium");
        tabSpec.setContent(R.id.lvContent);
        tabHost.addTab(tabSpec);

        // This tab will be chosen as default
        tabHost.setCurrentTabByTag("auditoriums");

        // handler of tab change
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            public void onTabChanged(String tabId) {
                filterDataWithQuery(searchQuery);
                setAdapterByContent();
                new ParseAuditoriumsGroupsTeachers().execute();
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
        filteredHistory = filterArrayListWithQuery(history, query);
        setAdapterByContent();
    }

    // OnItemClickListener for listView elements
    private void setOnItemClickListener () {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String contentID = "id_aud";
                String chosenID = "";

                if (tabHost.getCurrentTabTag().equals("auditoriums")) {
                    try {
                        ListObject auditoriums = filteredAuditoriums.get(position);
                        auditoriums.objectType = "id_aud";
                        contentID = auditoriums.objectType;
                        chosenID = auditoriums.id;
                        contentActivity();
                        if (!history.toString().contains(auditoriums.title)) {
                            saveHistoryToSharedPreferences(auditoriums.id, auditoriums.title, auditoriums.objectType);
                            Log.d(TAG, "History:" + sharedPreferences.getString(HISTORY_KEY, ""));
                        }
                    } catch (Exception e) {
                    }
                } else if (tabHost.getCurrentTabTag().equals("groups")) {
                    try {
                        ListObject groups = filteredGroups.get(position);
                        groups.objectType = "id_grp";
                        contentID = groups.objectType;
                        chosenID = groups.id;
                        contentActivity();
                        if (!history.toString().contains(groups.title)) {
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
                        contentActivity();
                        if (!history.toString().contains(teachers.title)) {
                            saveHistoryToSharedPreferences(teachers.id, teachers.title, teachers.objectType);
                        }
                    } catch (Exception e) {
                    }
                } else if (tabHost.getCurrentTabTag().equals("history"))     {
                    try {
                        ListObject history = filteredHistory.get(position);
                        contentID = history.objectType;
                        Log.d(TAG, "CONTENTID:" + contentID);
                        chosenID = history.id;
                        contentActivity();
                    } catch (Exception e) {
                    }
                }

                // Get start date
                Date startDate = new Date();

                // Get end date = start date + 30 days
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(startDate);
                calendar.add(Calendar.DATE, 30);
                Date endDate = calendar.getTime();

                String downloadURL = scheduleURLFor(contentID, chosenID, startDate, endDate);
                if (downloadURL != null) {
//                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(downloadURL));
//                    startActivity(browserIntent);
                }
            }
        });
    }

    // Setting up date
    private String dateToString(Date date) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
        String dateString = dateFormatter.format(date);
        return dateString;
    }

    // Building up URL for server connection
    private String scheduleURLFor(String contentID, String chosenID, Date startDate, Date endDate) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("schedule.sumdu.edu.ua")
                .appendPath("index")
                .appendPath("json")
                .appendQueryParameter(contentID, chosenID)
                .appendQueryParameter("date_beg", dateToString(startDate))
                .appendQueryParameter("date_end", dateToString(endDate));
        Log.d(TAG, "builder: " + builder.build().toString());
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

    // Reading data from shared preferences by special keys
    private void readDataFromSharedPreferences() {
        sharedPreferences = getPreferences(MODE_PRIVATE);
        if (sharedPreferences.contains(AUDITORIUMS_KEY)) {
            String fetchResult = sharedPreferences.getString(AUDITORIUMS_KEY, "");
            auditoriums = parseStringToArrayList(fetchResult);
        } else {
            auditoriums = new ArrayList<ListObject>();
        }
        if (sharedPreferences.contains(GROUPS_KEY)) {
            String fetchResult = sharedPreferences.getString(GROUPS_KEY, "");
            groups = parseStringToArrayList(fetchResult);
        } else {
            groups = new ArrayList<ListObject>();
        }
        if (sharedPreferences.contains(TEACHERS_KEY)) {
            String fetchResult = sharedPreferences.getString(TEACHERS_KEY, "");
            teachers = parseStringToArrayList(fetchResult);
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

    // Parsing string Json to arrayList Gson
    private ArrayList<ListObject> parseStringToArrayList(String stringToParse) {
        Type itemsListType = new TypeToken<List<ListObject>>(){}.getType();
        ArrayList<ListObject> records = new Gson().fromJson(stringToParse, itemsListType);
        return records;
    }

    // Setting adapter equal to content of selected tab
    private void setAdapterByContent() {
        ListView listView = (ListView)findViewById(R.id.lvContent);

        if (tabHost.getCurrentTabTag().equals("auditoriums")) {
            ArrayAdapter<ListObject> contentAdapter = new ArrayAdapter<ListObject>(this, android.R.layout.simple_list_item_1, filteredAuditoriums);
            listView.setAdapter(contentAdapter);
        } else if (tabHost.getCurrentTabTag().equals("groups")) {
            ArrayAdapter<ListObject> contentAdapter = new ArrayAdapter<ListObject>(this, android.R.layout.simple_list_item_1, filteredGroups);
            listView.setAdapter(contentAdapter);
        } else if (tabHost.getCurrentTabTag().equals("teachers")){
            ArrayAdapter<ListObject> contentAdapter = new ArrayAdapter<ListObject>(this, android.R.layout.simple_list_item_1, filteredTeachers);
            listView.setAdapter(contentAdapter);
        } else if (tabHost.getCurrentTabTag().equals("history")){
            ArrayAdapter<ListObject> contentAdapter = new ArrayAdapter<ListObject>(this, android.R.layout.simple_list_item_1, filteredHistory);
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
        editor.apply();

        return jsonHistoryString;
    }



    // Intent to new Activity
    private void contentActivity () {
        Intent intent = new Intent(this, ContentActivity.class);
        startActivity(intent);
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
                // Validate title on import
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