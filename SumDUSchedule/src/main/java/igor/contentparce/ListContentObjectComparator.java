package igor.contentparce;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;


public class ListContentObjectComparator implements Comparator<ListContentObject> {

    String TAG = "MainActivity";

    @Override
    public int compare(ListContentObject object1, ListContentObject object2) {

        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");

        try {
            Date date1 = formatter.parse(object1.date);
            Date date2 = formatter.parse(object2.date);
            return date1.compareTo(date2);

        } catch (ParseException e) {
            e.printStackTrace();
            Log.d(TAG, "ParseException");
        }

        return 0;
    }
}
