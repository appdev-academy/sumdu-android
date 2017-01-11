package igor.contentparce;


public class ListObject {

    public String id = "";
    public String title = "";
    public String objectType = "";
    ListObject() {}

    @Override
    public String toString() {
        return this.title;
    }

}
