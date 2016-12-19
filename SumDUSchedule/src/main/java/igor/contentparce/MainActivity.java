package igor.contentparce;

import android.app.TabActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class MainActivity extends TabActivity {

    String TAG = "MainActivity";

    SharedPreferences sharedPreferences;
    final String GROUPS_KEY = "groups";
    final String TEACHERS_KEY = "teachers";
    final String AUDITORIUMS_KEY = "auditoriums";

    private SearchView searchView;
    private ListView listView;
    private TabHost tabHost;
    private String searchQuery = "";

    private ArrayAdapter<String> adapter;
    private ArrayAdapter<ListObject> contentAdapter;

    private ArrayList<ListObject> auditoriums;
    private ArrayList<ListObject> groups;
    private ArrayList<ListObject> teachers;

    private ArrayList<ListObject> filteredAuditoriums;
    private ArrayList<ListObject> filteredGroups;
    private ArrayList<ListObject> filteredTeachers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        listView = (ListView) findViewById(R.id.lvContent);
        setupTabBar();

        new ParseAuditoriumsGroupsTeachers().execute();
        readDataFromSharedPreferences();
        filterDataWithQuery(searchQuery);
        setAdapterByContent();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Log.d(TAG, "itemClick: position = " + position + ", id = " + id);
            }
        });
    }

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

    public void setupTabBar() {
        tabHost = (TabHost) findViewById(android.R.id.tabhost);
        // инициализация
        tabHost.setup();

        TabHost.TabSpec tabSpec;

        tabSpec = tabHost.newTabSpec("history");
        tabSpec.setIndicator("", getResources().getDrawable(R.drawable.tab_icon_selector));
        tabSpec.setContent(R.id.lvContent);
        tabHost.addTab(tabSpec);

        // создаем вкладку и указываем тег
        tabSpec = tabHost.newTabSpec("teachers");
        // название вкладки
        tabSpec.setIndicator("Teachers");
        // указываем id компонента из FrameLayout, он и станет содержимым
        tabSpec.setContent(R.id.lvContent);
        // добавляем в корневой элемент
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
        tabHost.setCurrentTabByTag("history");

        // обработчик переключения вкладок
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            public void onTabChanged(String tabId) {
                filterDataWithQuery(searchQuery);
                setAdapterByContent();
                new ParseAuditoriumsGroupsTeachers().execute();
            }
        });
    }

    private void filterDataWithQuery(String query) {
        if (query == null || query == "") {
            filteredAuditoriums = auditoriums;
            filteredGroups = groups;
            filteredTeachers = teachers;
            setAdapterByContent();
            return;
        } else

            // Filter Auditoriums, Groups and Teachers
            filteredAuditoriums = filterArrayListWithQuery(auditoriums, query);
        filteredGroups = filterArrayListWithQuery(groups, query);
        filteredTeachers = filterArrayListWithQuery(teachers, query);
        setAdapterByContent();
    }

    private ArrayList<ListObject> filterArrayListWithQuery(ArrayList<ListObject> array, String query) {
        ArrayList<ListObject> filteredArray = new ArrayList<ListObject>();
        for (ListObject record : array) {
            if (record.title.toLowerCase().contains(query.toLowerCase())) {
                filteredArray.add(record);
            }
        }
        return filteredArray;
    }

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
    }

    private ArrayList<ListObject> parseStringToArrayList(String stringToParse) {
        Type itemsListType = new TypeToken<List<ListObject>>() {
        }.getType();
        ArrayList<ListObject> records = new Gson().fromJson(stringToParse, itemsListType);
        return records;
    }

    private void setAdapterByContent() {
        Log.d(TAG, "0");

        ListView listView = (ListView)findViewById(R.id.lvContent);

        if (tabHost.getCurrentTabTag().equals("auditoriums")) {
            ArrayAdapter<ListObject> contentAdapter = new ArrayAdapter<ListObject>(this, android.R.layout.simple_list_item_1, filteredAuditoriums);
            listView.setAdapter(contentAdapter);
            Log.d(TAG, "1");
        } else if (tabHost.getCurrentTabTag().equals("groups")) {
            ArrayAdapter<ListObject> contentAdapter = new ArrayAdapter<ListObject>(this, android.R.layout.simple_list_item_1, filteredGroups);
            listView.setAdapter(contentAdapter);
            Log.d(TAG, "2");
        } else {
            ArrayAdapter<ListObject> contentAdapter = new ArrayAdapter<ListObject>(this, android.R.layout.simple_list_item_1, filteredTeachers);
            listView.setAdapter(contentAdapter);
            Log.d(TAG, "3");
        }
    }


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
                editor.commit();

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

            // Serialize ArrayList<ListObject> to string
            Gson gson = new Gson();
            String jsonString = gson.toJson(records);
            return jsonString;
        }
    }
}