package igor.scheduleSumDU;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class DataManager {

    private static String TAG = "DataManager";

    public Context context;

    private DataManager(){}

    private static class SingletonHelper{
        private static final DataManager INSTANCE = new DataManager();
    }

    public static DataManager getInstance(){
        return SingletonHelper.INSTANCE;
    }


    // METHODS FOR CONTENTACTIVITY NEXT

    // Setting up fullDate
    public String dateToString(Date date) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
        String dateString = dateFormatter.format(date);
        return dateString;
    }

    // Performing comparison element with query and getting if it's similar
    public static ArrayList<ListObject> filterArrayListWithQuery(ArrayList<ListObject> array, String query) {
        ArrayList<ListObject> filteredArray = new ArrayList<ListObject>();
        for (ListObject record : array) {
            if (record.title.toLowerCase().contains(query.toLowerCase())) {
                filteredArray.add(record);
            }
        }
        return filteredArray;
    }

    // Saving elements added to history in sharedPreferences
    public String saveHistoryToSharedPreferences (ArrayList<ListObject> historyToSave) {

        Gson gson = new Gson();
        MainActivity mainActivity = new MainActivity();

        mainActivity.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        String jsonHistoryString = gson.toJson(historyToSave);

        SharedPreferences.Editor editor = mainActivity.sharedPreferences.edit();
        editor.putString(mainActivity.HISTORY_KEY, jsonHistoryString);
        editor.apply();

        return jsonHistoryString;
    }


    // Saving elements added to history in sharedPreferences
    public String saveHistoryToBufferSharedPreferences (String historySaveID, String historySaveTitle, String  historySaveObjectType, ArrayList<ListObject> historyToSave) {

        Gson gson = new Gson();
        MainActivity mainActivity = new MainActivity();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        ListObject historyObject = new ListObject();
        historyObject.id = historySaveID;
        historyObject.title = historySaveTitle;
        historyObject.objectType = historySaveObjectType;

        for (int i = 0; i < historyToSave.size(); i++) {
            if (historyToSave.toArray()[i].toString().contains(historyObject.title)) {
                historyToSave.remove(i);
            }
        }

        Collections.reverse(historyToSave);
        historyToSave.add(historyObject);
        Collections.reverse(historyToSave);

        String jsonHistoryString = gson.toJson(historyToSave);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(mainActivity.BUFFER_KEY, jsonHistoryString);
        editor.apply();

        return jsonHistoryString;
    }

    // Clean whole history in sharedPreferences
    public void cleanHistoryInSharedPreferences(ArrayList<ListObject> historyToClean) {
        MainActivity mainActivity = new MainActivity();

        historyToClean.clear();
        mainActivity.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = mainActivity.sharedPreferences.edit();
        editor.remove(mainActivity.HISTORY_KEY);
        editor.apply();
    }

    // Deleting one selected element on "History" tab
    public void deleteElementFromHistory(ArrayList<ListObject> historyToDelete, int elementPosition) {
        MainActivity mainActivity = new MainActivity();

        Gson gson = new Gson();
        mainActivity.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        historyToDelete.remove(elementPosition);
        String jsonHistoryString = gson.toJson(historyToDelete);
        SharedPreferences.Editor editor = mainActivity.sharedPreferences.edit();
        editor.putString(mainActivity.HISTORY_KEY, jsonHistoryString);
        editor.apply();
    }

    public ArrayList<ListObject> filterAuditoriumsWithQuery(String query,ArrayList<ListObject> filteredAuditoriums, ArrayList<ListObject> unfilteredAuditoriums) {

        if (query == null || query == "") {
            filteredAuditoriums = unfilteredAuditoriums;
        } else {
            filteredAuditoriums = filterArrayListWithQuery(unfilteredAuditoriums, query);
        }
        return filteredAuditoriums;
    }

    public ArrayList<ListObject> filterGroupsWithQuery(String query,ArrayList<ListObject> filteredGroups, ArrayList<ListObject> unfilteredGroups) {

        if (query == null || query == "") {
            filteredGroups = unfilteredGroups;
        } else {
            filteredGroups = filterArrayListWithQuery(unfilteredGroups, query);
        }
        return filteredGroups;
    }

    public ArrayList<ListObject> filterTeachersWithQuery(String query,ArrayList<ListObject> filteredTeachers, ArrayList<ListObject> unfilteredTeachers) {

        if (query == null || query == "") {
            filteredTeachers = unfilteredTeachers;
        } else {
            filteredTeachers = filterArrayListWithQuery(unfilteredTeachers, query);
        }
        return filteredTeachers;
    }

    public ArrayList<ListObject> filterHistoryWithQuery(String query,ArrayList<ListObject> filteredHistory, ArrayList<ListObject> unfilteredHistory) {

        if (query == null || query == "") {
            filteredHistory = unfilteredHistory;
        } else {
            filteredHistory = filterArrayListWithQuery(unfilteredHistory, query);
        }
        return filteredHistory;
    }

    // Parsing Json string to ArrayList Gson
    public ArrayList<ListObject> parseStringToArrayList(String stringToParse) {
        Type itemsListType = new TypeToken<List<ListObject>>(){}.getType();
        ArrayList<ListObject> records = new Gson().fromJson(stringToParse, itemsListType);
        return records;
    }

    public ArrayList<ListObject> readAuditoriumsFromSharedPreferences() {

        MainActivity mainActivity = new MainActivity();
        mainActivity.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        ArrayList<ListObject> auditoriums;

        if (mainActivity.sharedPreferences.contains(mainActivity.AUDITORIUMS_KEY)) {
            String fetchResult = mainActivity.sharedPreferences.getString(mainActivity.AUDITORIUMS_KEY, "");
            auditoriums = parseStringToArrayList(fetchResult);
        } else {
            auditoriums = new ArrayList<ListObject>();
        }
        return auditoriums;
    }

    public ArrayList<ListObject> readGroupsFromSharedPreferences() {

        MainActivity mainActivity = new MainActivity();
        mainActivity.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        ArrayList<ListObject> groups;

        if (mainActivity.sharedPreferences.contains(mainActivity.GROUPS_KEY)) {
            String fetchResult = mainActivity.sharedPreferences.getString(mainActivity.GROUPS_KEY, "");
            groups = parseStringToArrayList(fetchResult);
        } else {
            groups = new ArrayList<ListObject>();
        }
        return groups;
    }

    public ArrayList<ListObject> readTeachersFromSharedPreferences() {

        MainActivity mainActivity = new MainActivity();
        mainActivity.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        ArrayList<ListObject> teachers;

        if (mainActivity.sharedPreferences.contains(mainActivity.TEACHERS_KEY)) {
            String fetchResult = mainActivity.sharedPreferences.getString(mainActivity.TEACHERS_KEY, "");
            teachers = parseStringToArrayList(fetchResult);
        } else {
            teachers = new ArrayList<ListObject>();
        }
        return teachers;
    }

    public ArrayList<ListObject> readHistoryFromSharedPreferences() {

        MainActivity mainActivity = new MainActivity();
        mainActivity.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        ArrayList<ListObject> history;

        if (mainActivity.sharedPreferences.contains(mainActivity.HISTORY_KEY)) {
            String fetchResult = mainActivity.sharedPreferences.getString(mainActivity.HISTORY_KEY, "");
            history = parseStringToArrayList(fetchResult);
            Log.d(TAG, "READ_HISTORY_FROM_SPREF:" + history);

        } else {
            history = new ArrayList<ListObject>();
        }
        return history;
    }

    public ArrayList<ListObject> readHistoryFromBufferSharedPreferences() {

        MainActivity mainActivity = new MainActivity();
        mainActivity.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        ArrayList<ListObject> history;

        if (mainActivity.sharedPreferences.contains(mainActivity.BUFFER_KEY)) {
            String fetchResult = mainActivity.sharedPreferences.getString(mainActivity.BUFFER_KEY, "");
            history = parseStringToArrayList(fetchResult);
        } else {
            history = new ArrayList<ListObject>();
        }
        return history;
    }


    // METHODS FOR CONTENTACTIVITY NEXT

    private ArrayList<ListContentObject> parseContentStringToArrayList(String stringToParse) {
        Type itemsListType = new TypeToken<List<ListContentObject>>(){}.getType();
        ArrayList<ListContentObject> contentRecords = new Gson().fromJson(stringToParse, itemsListType);
        Collections.sort(contentRecords, new ListContentObjectComparator());

        return contentRecords;
    }

    // Getting data from sharedpreferences by "content_title"
    public ArrayList<ListContentObject> readDataFromSharedPreferences(Intent intent) {
        ContentActivity contentActivity = new ContentActivity();
        ArrayList<ListContentObject> content;

        contentActivity.sharedPreferencesContent = PreferenceManager.getDefaultSharedPreferences(context);
        String fetchResult = contentActivity.sharedPreferencesContent.getString(intent.getStringExtra("content_title"), "");

        content = parseContentStringToArrayList(fetchResult);

        return content;
    }
}
