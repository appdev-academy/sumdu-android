package igor.contentparce;


import java.util.HashMap;

public class ListObject {

    public String id = "";
    public String title = "";
    ListObject() {}

    @Override
    public String toString() {
        return this.title;
    }

//    public HashMap<String, String> titleIdMap() {
//
//        HashMap<String, String> tmpHashMap = new HashMap<String, String>();
//        tmpHashMap.put(id, id);
//        tmpHashMap.put(title, title);
//        return tmpHashMap;
//    }
}
