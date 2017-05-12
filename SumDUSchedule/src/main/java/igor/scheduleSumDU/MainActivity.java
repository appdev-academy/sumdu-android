package igor.scheduleSumDU;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import static android.R.id.list;


public class MainActivity extends TabActivity {

    String TAG = "MainActivity";

    SharedPreferences sharedPreferences;

    private int elementPosition = 0;

    // Special keys for sharedPreferences values
    final String GROUPS_KEY = "groups";
    final String TEACHERS_KEY = "teachers";
    final String AUDITORIUMS_KEY = "auditoriums";
    final String HISTORY_KEY = "history";
    final String BUFFER_KEY = "buffer";

    private SearchView searchView;
    private ListView listView;
    private ListView historyListView;
    private TabHost tabHost;
    private SwipeRefreshLayout swipeRefreshLayout;

    public String searchQuery = "";
    private String content_title = null;

    // ArrayLists for getting from sharedPreferences unfiltered elements
    public ArrayList<ListObject> history;
    public ArrayList<ListObject> auditoriums;
    public ArrayList<ListObject> groups;
    public ArrayList<ListObject> teachers;

    // ArrayLists for filtered with query elements
    public ArrayList<ListObject> filteredHistory;
    public ArrayList<ListObject> filteredAuditoriums;
    public ArrayList<ListObject> filteredGroups;
    public ArrayList<ListObject> filteredTeachers;

    public Context mainActivityContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean tabletSize = getResources().getBoolean(R.bool.isTablet);
        if (tabletSize) {
            setContentView(R.layout.tablet_main);
        } else {
            setContentView(R.layout.main);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        }

        listView = (ListView) findViewById(R.id.lvContent);
        historyListView = (ListView) findViewById(R.id.lvHistory);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_main);

        DataManager dataManager = DataManager.getInstance();
        dataManager.context = getApplicationContext();
        mainActivityContext = getApplicationContext();

        setOnItemClickListener();
        historyListView.setLongClickable(true);

        getActionBar().setIcon(
                new ColorDrawable(ContextCompat.getColor(this, android.R.color.transparent)));

        Intent intent = getIntent();
        Bundle data = intent.getExtras();
        if (data != null) {
            if (!data.getBoolean("Connection")) {
                new ParseAuditoriumsGroupsTeachers().execute();
            }
        }

        setupTabBar();
        filterDataWithQuery();
        refreshListener();
        onLongItemClickListener();
        setAdapterByContent();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
        } else {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    searchQuery = newText;
                    filterDataWithQuery();
                    setAdapterByContent();

                    return false;
                }
            });
        }
        return super.onCreateOptionsMenu(menu);
    }

    // Adding tab bar
    public void setupTabBar() {

        ImageView historyImage = (ImageView) findViewById(R.id.history_image);
        TextView emptyHistoryText1 = (TextView) findViewById(R.id.empty_history_text1);
        TextView emptyHistoryText2 = (TextView) findViewById(R.id.empty_history_text2);

        tabHost = (TabHost) findViewById(android.R.id.tabhost);
        // initialization
        tabHost.setup();

        TabHost.TabSpec tabSpec;

        tabSpec = tabHost.newTabSpec("history");
        tabSpec.setIndicator("", getResources().getDrawable(R.drawable.tab_icon_selector));

        tabSpec.setContent(R.id.lvHistory);

        historyImage.setVisibility(View.GONE);
        historyListView.setEmptyView(historyImage);
        historyListView.setEmptyView(emptyHistoryText1);
        historyListView.setEmptyView(emptyHistoryText2);

        tabHost.addTab(tabSpec);

        // adding tab and defining tag
        tabSpec = tabHost.newTabSpec("teachers");
        // tab pairTitleAndType.
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

        boolean tabletSize = getResources().getBoolean(R.bool.isTablet);
        if (tabletSize) {
            TextView textView1 = (TextView) tabHost.getTabWidget().getChildAt(1).findViewById(android.R.id.title);
            textView1.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.tab_text_size_tablet));
            tabHost.getTabWidget().getChildAt(1).getLayoutParams().width = 185;

            TextView textView2 = (TextView) tabHost.getTabWidget().getChildAt(2).findViewById(android.R.id.title);
            textView2.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.tab_text_size_tablet));
            tabHost.getTabWidget().getChildAt(2).getLayoutParams().width = 185;

            TextView textView3 = (TextView) tabHost.getTabWidget().getChildAt(3).findViewById(android.R.id.title);
            textView3.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.tab_text_size_tablet));
            tabHost.getTabWidget().getChildAt(3).getLayoutParams().width = 185;
        } else {
            TextView textView1 = (TextView) tabHost.getTabWidget().getChildAt(1).findViewById(android.R.id.title);
            textView1.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.tab_text_size_phone));
            tabHost.getTabWidget().getChildAt(1).getLayoutParams().width = 50;

            TextView textView2 = (TextView) tabHost.getTabWidget().getChildAt(2).findViewById(android.R.id.title);
            textView2.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.tab_text_size_phone));
            tabHost.getTabWidget().getChildAt(2).getLayoutParams().width = 50;

            TextView textView3 = (TextView) tabHost.getTabWidget().getChildAt(3).findViewById(android.R.id.title);
            textView3.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.tab_text_size_phone));
            tabHost.getTabWidget().getChildAt(3).getLayoutParams().width = 50;
        }

        // This tab will be chosen as default
        tabHost.setCurrentTab(1);
        tabHost.setCurrentTab(0);

        // Handler of tab change
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            public void onTabChanged(String tabId) {

                filterDataWithQuery();
                setAdapterByContent();
                refreshListener();
                onLongItemClickListener();
            }
        });
    }

    private void refreshListener () {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!tabHost.getCurrentTabTag().equals("history")) {
                    Toast.makeText(getApplicationContext(),
                            "Оновлення списків...", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(true);
                    new ParseAuditoriumsGroupsTeachers().execute();
                } else swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    // Building and calling dialog for "History"
    public void dialog() {
        final DataManager dataManager = DataManager.getInstance();

        AlertDialog.Builder dialogAlert = new AlertDialog.Builder(MainActivity.this);
        dialogAlert.setTitle(R.string.history);
        dialogAlert.setMessage(R.string.body_text);
        dialogAlert.setPositiveButton(R.string.delete_element, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                dataManager.deleteElementFromHistory(filteredHistory, elementPosition);
                setAdapterByContent();
            }
        });

        dialogAlert.setNegativeButton(R.string.clean_history, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                dataManager.cleanHistoryInSharedPreferences(filteredHistory);
                setAdapterByContent();
            }
        });

        dialogAlert.show();
    }


    private void filterDataWithQuery() {
        DataManager dataManager = DataManager.getInstance();

        readDataFromSharedPreferences();
        filteredAuditoriums = dataManager.filterAuditoriumsWithQuery(searchQuery, filteredAuditoriums, auditoriums);
        filteredGroups = dataManager.filterGroupsWithQuery(searchQuery, filteredGroups, groups);
        filteredTeachers = dataManager.filterTeachersWithQuery(searchQuery, filteredTeachers, teachers);
        filteredHistory = dataManager.filterHistoryWithQuery(searchQuery, filteredHistory, history);
    }

    private void readDataFromSharedPreferences () {
        DataManager dataManager = DataManager.getInstance();

        auditoriums = dataManager.readAuditoriumsFromSharedPreferences();
        groups = dataManager.readGroupsFromSharedPreferences();
        teachers = dataManager.readTeachersFromSharedPreferences();
        history = dataManager.readHistoryFromSharedPreferences();
    }

    // OnItemClickListener for listView elements
    private void setOnItemClickListener () {

        if (!swipeRefreshLayout.isRefreshing()) {
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    DataManager dataManager = DataManager.getInstance();
                    String contentID = "";
                    String chosenID = "";

                    if (tabHost.getCurrentTab() == 3) {
                        try {
                            ListObject auditoriums = filteredAuditoriums.get(position);
                            auditoriums.objectType = "id_aud";
                            contentID = auditoriums.objectType;
                            chosenID = auditoriums.id;
                            content_title = auditoriums.title;

                            dataManager.saveHistoryToBufferSharedPreferences(auditoriums.id, auditoriums.title, auditoriums.objectType, history);

                        } catch (Exception e) {
                        }
                    } else if (tabHost.getCurrentTab() == 2) {
                        try {
                            ListObject groups = filteredGroups.get(position);
                            groups.objectType = "id_grp";
                            contentID = groups.objectType;
                            chosenID = groups.id;
                            content_title = groups.title;

                            dataManager.saveHistoryToBufferSharedPreferences(groups.id, groups.title, groups.objectType, history);

                        } catch (Exception e) {
                        }
                    } else if (tabHost.getCurrentTab() == 1) {
                        try {
                            ListObject teachers = filteredTeachers.get(position);
                            teachers.objectType = "id_fio";
                            contentID = teachers.objectType;
                            chosenID = teachers.id;
                            content_title = teachers.title;

                            dataManager.saveHistoryToBufferSharedPreferences(teachers.id, teachers.title, teachers.objectType, history);

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
                    } else Toast.makeText(getApplicationContext(),
                            "Невірне посилання на об'єкт", Toast.LENGTH_LONG).show();
                }
            });
        }

                historyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        DataManager dataManager = DataManager.getInstance();
                        String contentID = "";
                        String chosenID = "";

                            try {
                                ListObject historyObject = filteredHistory.get(position);
                                contentID = historyObject.objectType;
                                chosenID = historyObject.id;
                                content_title = historyObject.title;
                                filteredHistory.remove(position);

                                dataManager.saveHistoryToBufferSharedPreferences(historyObject.id, historyObject.title, historyObject.objectType, history);

                            } catch (Exception e) {
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
                        } else Toast.makeText(getApplicationContext(),
                                "Невірне посилання на об'єкт", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void onLongItemClickListener() {

        historyListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int pos, long id) {
                    elementPosition = pos;
                    dialog();

                return true;
            }
        });
    }

    // Intent to new Activity
    private void intentToContentActivity(String contentID, String chosenID, Date startDate, Date endDate) {

        String downloadURL = scheduleURLFor(contentID, chosenID, startDate, endDate);
        Intent intent = new Intent(this, ContentActivity.class);
        intent.putExtra("downloadURL", downloadURL);
        intent.putExtra("content_title", content_title);
        intent.putExtra("content_type", contentID);
        intent.putExtra("content_id", chosenID);
        startActivity(intent);
    }

    // Building up URL for server connectionStatus
    private String scheduleURLFor(String contentID, String chosenID, Date startDate, Date endDate) {
        DataManager dataManager = DataManager.getInstance();
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("schedule.sumdu.edu.ua")
                .appendPath("index")
                .appendPath("json")
                .appendQueryParameter(contentID, chosenID)
                .appendQueryParameter("date_beg", dataManager.dateToString(startDate))
                .appendQueryParameter("date_end", dataManager.dateToString(endDate));
        return builder.build().toString();
    }

    // Setting adapter equal to content of selected tab
    public void setAdapterByContent() {

        if (tabHost.getCurrentTab() == 3) {
            ArrayAdapter<ListObject> contentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filteredAuditoriums);
            listView.setAdapter(contentAdapter);
        } else if (tabHost.getCurrentTab() == 2) {
            ArrayAdapter<ListObject> contentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filteredGroups);
            listView.setAdapter(contentAdapter);
        } else if (tabHost.getCurrentTab() == 1) {
            ArrayAdapter<ListObject> contentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filteredTeachers);
            listView.setAdapter(contentAdapter);
        } else if (tabHost.getCurrentTab() == 0) {
            ArrayAdapter<ListObject> contentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filteredHistory);
            historyListView.setAdapter(contentAdapter);
        }
    }

    // Parsing, serializing and saving content gained from server
    private class ParseAuditoriumsGroupsTeachers extends AsyncTask<Void, Void, Boolean> {

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
                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mainActivityContext);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString(AUDITORIUMS_KEY, serializedAuditoriums);
                editor.putString(GROUPS_KEY, serializedGroups);
                editor.putString(TEACHERS_KEY, serializedTeachers);
                editor.apply();

                return true;

            } catch (IOException e) {
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean refresh) {
            swipeRefreshLayout.setRefreshing(false);

            filterDataWithQuery();
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
            Collections.sort(records, new ListObjectTitleComparator());

            // Serialize ArrayList<ListObject> to string
            Gson gson = new Gson();
            String jsonString = gson.toJson(records);
            return jsonString;
        }
    }
}