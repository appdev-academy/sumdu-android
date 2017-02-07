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

        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy hh:mm");

        char[] temp1 = new char[5];
        char[] temp2 = new char[5];
        object1.pairTime.getChars(0, 5, temp1, 0);
        object2.pairTime.getChars(0, 5, temp2, 0);
        String dateBuilder1 = object1.date + " " + new String(temp1);
        String dateBuilder2 = object2.date + " " + new String(temp2);

        try {
            Date date1 = formatter.parse(dateBuilder1);
            Date date2 = formatter.parse(dateBuilder2);

            return date1.compareTo(date2);

        } catch (ParseException e) {
            e.printStackTrace();
            Log.d(TAG, "ParseException");
        }



        return 0;
    }
}
