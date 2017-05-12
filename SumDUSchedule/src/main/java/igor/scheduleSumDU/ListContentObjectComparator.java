package igor.scheduleSumDU;

import java.util.Comparator;


public class ListContentObjectComparator implements Comparator<ListContentObject> {

    @Override
    public int compare(ListContentObject object1, ListContentObject object2) {
        return object1.fullDate.compareTo(object2.fullDate);
    }
}
