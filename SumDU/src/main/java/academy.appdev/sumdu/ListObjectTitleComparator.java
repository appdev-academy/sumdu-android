package academy.appdev.sumdu;

import java.util.Comparator;


public class ListObjectTitleComparator implements Comparator<ListObject> {

    @Override
    public int compare(ListObject object1, ListObject object2) {
        return object1.title.compareToIgnoreCase(object2.title);
    }
}