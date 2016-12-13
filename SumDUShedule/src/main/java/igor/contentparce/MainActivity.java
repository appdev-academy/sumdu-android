package igor.contentparce;

import android.app.TabActivity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

public class MainActivity extends TabActivity {

    String TAG = "MainActivity";

    SharedPreferences sharedPreferences;
    final String GROUPS_KEY = "groups";
    final String TEACHERS_KEY = "teachers";
    final String AUDITORIUMS_KEY = "auditoriums";

    SearchView searchView;
    ListView listView;
    TabHost tabHost;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        listView = (ListView) findViewById(R.id.lvContent);

        adapter = new ArrayAdapter<> (
                MainActivity.this,
                android.R.layout.simple_list_item_1);
        listView.setAdapter(adapter);

        setupTabBar();

        new ParseAuditoriumsGroupsTeachers().execute();
        reloadData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);
        MenuItem item = menu.findItem(R.id.menu_search);
        searchView = (SearchView)item.getActionView();

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
                    Log.d(TAG, "Query: " + newText);
                    adapter.getFilter().filter(newText);
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
        tabSpec.setIndicator("" ,getResources().getDrawable(R.drawable.tab_icon_selector));
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

        // первая вкладка будет выбрана по умолчанию
        tabHost.setCurrentTabByTag("groups");

        // обработчик переключения вкладок
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            public void onTabChanged(String tabId) {
                reloadData();
                new ParseAuditoriumsGroupsTeachers().execute();
            }
        });
    }

    private void reloadData() {
        Log.d(TAG, "Reload data");
        sharedPreferences = getPreferences(MODE_PRIVATE);
        String tabTag = tabHost.getCurrentTabTag();
        Log.d(TAG, "Tab tag: " + tabTag);
        if (sharedPreferences.contains(tabTag)) {
            String fetchResult = sharedPreferences.getString(tabTag, "");
            Log.d(TAG, fetchResult);
            Type itemsListType = new TypeToken<List<ListObject>>(){}.getType();
            List<ListObject> records = new Gson().fromJson(fetchResult, itemsListType);

            // Prepare list of Whatever names
            ArrayList<String> recordNames = new ArrayList<String>();
            for (ListObject record: records) {
                if (record.title.trim().length() > 1) {
                    recordNames.add(record.title);
                }
            }

            ArrayAdapter<String> listObjectsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, recordNames);
            listView.setAdapter(listObjectsAdapter);
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

            } catch(IOException e) {
                e.printStackTrace();

                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            reloadData();
        }

        private String parseListObjects(Element element) {
            // Loops through options of HTML select element and map entries to ListObjects
            ArrayList<ListObject> records = new ArrayList<ListObject>();
            for (Element option: element.children()) {
                ListObject newObject = new ListObject();
                newObject.id = option.attr("value");
                newObject.title = option.text();
                records.add(newObject);
            }

            // Serialize ArrayList<ListObject> to string
            Gson gson = new Gson();
            String jsonString = gson.toJson(records);
            return jsonString;
        }
    }
}