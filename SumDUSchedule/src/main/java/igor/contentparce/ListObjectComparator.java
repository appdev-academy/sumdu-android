package igor.contentparce;

import java.util.Comparator;

/**
 * Created by Igor on 12/22/16.
 */

public class ListObjectComparator implements Comparator<ListObject> {
    
    @Override
    public int compare(ListObject object1, ListObject object2) {
        return object1.title.compareToIgnoreCase(object2.title);
    }
}