package igor.contentparce;

import android.app.DialogFragment;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TabHost;
import android.widget.TextView;
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

    String TAG = "MainActivity";

    SharedPreferences sharedPreferences;

    // Static variable for history cleaning
    final int CLEAN = 1;
    final int DELETE = 2;

    final int DIALOG_DELETE = 1;

    // Connection status variable
    private int connectionStatus = 0;

    private int checkForOfflineMode = 0;

    private int elementPosition = 0;

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

    DialogFragment dialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        listView = (ListView) findViewById(R.id.lvContent);
        dialogFragment = new DialogOnLongClick();

//        registerForContextMenu(listView);
        setOnItemClickListener();
        listView.setLongClickable(true);

        checkConnection();
        if (connectionStatus != 0) {
            new ParseAuditoriumsGroupsTeachers().execute();
        }

        readDataFromSharedPreferences();
        setupTabBar();
        filterDataWithQuery(searchQuery);
        setAdapterByContent();


    }

//        protected DialogOnLongClick onCreateDialog(int id) {
//            if (id == DIALOG_DELETE) {
//                AlertDialog.Builder adb = new AlertDialog.Builder(this);
//                // заголовок
//                adb.setTitle("Історія");
//                // сообщение
//                adb.setMessage("Оберіть дію над вмістом збереженим в історії:");
////                // иконка
////                adb.setIcon(android.R.drawable.ic_dialog_info);
//                // кнопка положительного ответа
//                adb.setPositiveButton(R.string.delete_element, myClickListener);
//                // кнопка отрицательного ответа
//                adb.setNegativeButton(R.string.clean_history, myClickListener);
//                // создаем диалог
//                return adb.create();
//            }
//            return super.onCreateDialog(id);
//        }
//    DialogInterface.OnClickListener myClickListener = new DialogInterface.OnClickListener() {
//        public void onClick(DialogInterface dialog, int which) {
//            switch (which) {
//                // положительная кнопка
//                case DialogOnLongClick.BUTTON_POSITIVE:
//
//                    Gson gson = new Gson();
//                    sharedPreferences = getPreferences(MODE_PRIVATE);
//
//                    filteredHistory.remove(elementPosition);
//                    Log.d(TAG, "pos: " + elementPosition);
//
//                    String jsonHistoryString = gson.toJson(filteredHistory);
//
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putString(HISTORY_KEY, jsonHistoryString);
//                    editor.apply();
//
//                    setAdapterByContent();
//                    break;
//                // негативная кнопка
//                case DialogOnLongClick.BUTTON_NEGATIVE:
//                    cleanHistoryInSharedPreferences();
//                    setAdapterByContent();
//                    break;
//            }
//        }
//    };


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

//    // Creating context menu for deleting history from shared preferences
//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
//        if (tabHost.getCurrentTabTag().equals("history")) {
//            switch (view.getId()) {
//                case R.id.lvContent:
//                    menu.setHeaderTitle("Історія");
//                    menu.add(0, CLEAN, 0, "Очистити історію");
//                    menu.add(0, DELETE, 0, "Видалити елемент");
//                    break;
//            }
//        }
//    }
//
//    @Override
//    public boolean onContextItemSelected (MenuItem item){
//        switch (item.getItemId()) {
//            case CLEAN:
//                cleanHistoryInSharedPreferences();
//                checkConnection();
//                if(connectionStatus != 0) {
//                    new ParseAuditoriumsGroupsTeachers().execute();
//                }
//                setAdapterByContent();
//                break;
////            case DELETE:
////                AdapterView.AdapterContextMenuInfo adapterContextMenuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
////
////                listHistory.remove();
////
////                checkConnection();
////                if(connectionStatus != 0) {
////                    new ParseAuditoriumsGroupsTeachers().execute();
////                }
////                setAdapterByContent();
////                break;
//        }
//
//        return super.onContextItemSelected(item);
//    }

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

        TextView textView1 = (TextView) tabHost.getTabWidget().getChildAt(1).findViewById(android.R.id.title);
        textView1.setTextSize(11);
        tabHost.getTabWidget().getChildAt(1).getLayoutParams().width = 50;

        TextView textView2 = (TextView) tabHost.getTabWidget().getChildAt(2).findViewById(android.R.id.title);
        textView2.setTextSize(11);
        tabHost.getTabWidget().getChildAt(2).getLayoutParams().width = 50;

        TextView textView3 = (TextView) tabHost.getTabWidget().getChildAt(3).findViewById(android.R.id.title);
        textView3.setTextSize(11);
        tabHost.getTabWidget().getChildAt(3).getLayoutParams().width = 50;

        // This tab will be chosen as default
        tabHost.setCurrentTabByTag("groups");

        // handler of tab change
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            public void onTabChanged(String tabId) {

                filterDataWithQuery(searchQuery);
                setAdapterByContent();
                checkConnection();
                onLongItemClickListener();
                if(connectionStatus != 0) {
                    new ParseAuditoriumsGroupsTeachers().execute();
                }
            }
        });
    }

    public void deleteElement() {

        Gson gson = new Gson();
                    sharedPreferences = getPreferences(MODE_PRIVATE);

                    filteredHistory.remove(elementPosition);
                    Log.d(TAG, "pos: " + elementPosition);

                    String jsonHistoryString = gson.toJson(filteredHistory);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(HISTORY_KEY, jsonHistoryString);
                    editor.apply();

                    setAdapterByContent();
    }

    public void cleanHistory() {

        cleanHistoryInSharedPreferences();
                    setAdapterByContent();
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
                        if (history.toString().contains(auditoriums.title) && connectionStatus == 0 || connectionStatus != 0) {
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
                        if (history.toString().contains(groups.title) && connectionStatus == 0 || connectionStatus != 0) {
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

                        if (history.toString().contains(teachers.title) && connectionStatus == 0 || connectionStatus != 0) {
                            saveHistoryToSharedPreferences(teachers.id, teachers.title, teachers.objectType);
//                       }
                        }

                    } catch (Exception e) {
                    }
                } else if (tabHost.getCurrentTabTag().equals("history")) {
                    try {
                        ListObject historyObject = filteredHistory.get(position);
                        contentID = historyObject.objectType;
                        chosenID = historyObject.id;
                        content_title = historyObject.title;
                        filteredHistory.remove(position);

                            saveHistoryToSharedPreferences(historyObject.id, historyObject.title, historyObject.objectType);
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

                if (downloadURL != null && history.toString().contains(content_title) && connectionStatus == 0 || connectionStatus != 0 && downloadURL != null) {
                    intentToContentActivity(contentID, chosenID, startDate, endDate);
                 } else Toast.makeText(getApplicationContext(),
            "Лише збережений в історії розклад доступний в offline режимі.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void onLongItemClickListener() {

        Log.d(TAG, "TabTag: " + tabHost.getCurrentTabTag());
//        if (tabHost.getCurrentTabTag().equals("groups")) {
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int pos, long id) {

                    elementPosition = pos;
                    dialogFragment.show(getFragmentManager(), "dialogFragment");
                    return false;
                }
            });
//        }

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

        String downloadURL = scheduleURLFor(contentID, chosenID, startDate, endDate);
        Intent intent = new Intent(this, ContentActivity.class);
        intent.putExtra("downloadURL", downloadURL);
        intent.putExtra("content_title", content_title);
        intent.putExtra("connectionStatus", connectionStatus);
        startActivity(intent);
        Log.d(TAG, "Status: " + "GO!");

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
        if (sharedPreferences.contains(AUDITORIUMS_KEY)) {
            String fetchResult = sharedPreferences.getString(AUDITORIUMS_KEY, "");
            auditoriums = parseStringToArrayList(fetchResult);
        } else
            auditoriums = new ArrayList<ListObject>();

        if (sharedPreferences.contains(GROUPS_KEY)) {
            String fetchResult = sharedPreferences.getString(GROUPS_KEY, "");
            groups = parseStringToArrayList(fetchResult);
        } else
            groups = new ArrayList<ListObject>();

        if (sharedPreferences.contains(TEACHERS_KEY)) {
            String fetchResult = sharedPreferences.getString(TEACHERS_KEY, "");
            teachers = parseStringToArrayList(fetchResult);
        } else
            teachers = new ArrayList<ListObject>();

        if (sharedPreferences.contains(HISTORY_KEY)) {
            String fetchResult = sharedPreferences.getString(HISTORY_KEY, "");
            history = parseStringToArrayList(fetchResult);
        } else
            history = new ArrayList<>();
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

    // Parsing Json string to ArrayList Gson
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
        Log.d(TAG, "filteredHistory:" + filteredHistory);

        ListObject historyObject = new ListObject();
        historyObject.id = historySaveID;
        historyObject.title = historySaveTitle;
        historyObject.objectType = historySaveObjectType;

        for (int i = 0; i < filteredHistory.size(); i++) {
           if (filteredHistory.toArray()[i].toString().contains(historyObject.title)) {
               filteredHistory.remove(i);
               Log.d(TAG, "!!!");
           }
            Log.d(TAG, "I:" + i);
        }

        Collections.reverse(filteredHistory);
        filteredHistory.add(historyObject);
        Collections.reverse(filteredHistory);

        Log.d(TAG, "filteredHistory:" + filteredHistory);

        String jsonHistoryString = gson.toJson(filteredHistory);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(HISTORY_KEY, jsonHistoryString);
        editor.apply();

        return jsonHistoryString;
    }

    // Clean whole history in sharedPreferences
    private void cleanHistoryInSharedPreferences() {
//        listHistory.clear();
        filteredHistory.clear();
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
            Collections.sort(records, new ListObjectTitleComparator());

            // Serialize ArrayList<ListObject> to string
            Gson gson = new Gson();
            String jsonString = gson.toJson(records);
            return jsonString;
        }

    }
}