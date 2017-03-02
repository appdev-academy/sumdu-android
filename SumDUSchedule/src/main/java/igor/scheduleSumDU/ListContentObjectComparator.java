package igor.scheduleSumDU;

import java.util.Comparator;


public class ListContentObjectComparator implements Comparator<ListContentObject> {

    String TAG = "MainActivity";

    @Override
    public int compare(ListContentObject object1, ListContentObject object2) {

        return object1.fullDate.compareTo(object2.fullDate);
    }
}
