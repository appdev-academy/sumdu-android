package igor.contentparce;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.ListView;
//import android.widget.TextView;
//
//import java.util.ArrayList;
//
//public class ListObjectArrayAdapter extends ArrayAdapter<MainActivity> {
//
//    private static class ViewHolder {
//        private TextView itemView;
//    }
//
//    public MyClassAdapter(Context context, int textViewResourceId, ArrayList<MainActivity> items) {
//        super(context, textViewResourceId, items);
//        return;
//    }
//
//    public View getView(int position, View convertView, ViewGroup parent) {
//
//        if (convertView == null) {
//            convertView = LayoutInflater.from(this.getContext())
//                    .inflate(R.layout.main, parent, false);
//
//           ViewHolder viewHolder = new ViewHolder();
//            viewHolder.itemView = (ListView) convertView.findViewById(R.id.lvContent);
//
//            convertView.setTag(viewHolder);
//        } else {
//            viewHolder = (ViewHolder) convertView.getTag();
//        }
//
//        MainActivity item = getItem(position);
//        if (item!= null) {
//            // My layout has only one TextView
//            // do whatever you want with your string and long
//            viewHolder.itemView.setText(String.format("%s %d", item.reason, item.long_val));
//        }
//
//        return convertView;
//    }
//}

class ListObjectArrayAdapter
{
    @Override
    public String toString() {
        return "Hello, world.";
    }
}
