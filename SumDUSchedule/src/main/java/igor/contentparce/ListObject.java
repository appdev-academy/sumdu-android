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

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }
}
