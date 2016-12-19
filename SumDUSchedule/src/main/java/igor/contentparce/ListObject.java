package igor.contentparce;


public class ListObject {
    public String id = "";
    public String title = "";
    ListObject() {}

    @Override
    public String toString() {
        return this.title;
    }
}