package igor.contentparce;

/**
 * Created by Igor on 1/18/17.
 */

public class ListContentObject {

    public String dayOfTheWeek = "";
    public String date = "";
    public String pairNumber = "";
    public String pairTime = "";
    public String lecturer = "";
    public String auditorium = "";
    public String group = "";
    public String pairType = "";
    public String pairTitle = "";

    ListContentObject() {}

    @Override
    public String toString() {
        return this.pairTitle;
    }

}
