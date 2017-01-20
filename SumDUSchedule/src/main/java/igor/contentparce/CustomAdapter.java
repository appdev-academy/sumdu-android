//package igor.contentparce;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.TextView;
//
//import java.util.ArrayList;
//
//public class CustomAdapter extends BaseAdapter {
//    private static ArrayList<SearchResults> searchArrayList;
//
//    private LayoutInflater mInflater;
//
//    public CustomAdapter(Context context, ArrayList<SearchResults> results) {
//        searchArrayList = results;
//        mInflater = LayoutInflater.from(context);
//    }
//
//    public int getCount() {
//        return searchArrayList.size();
//    }
//
//    public Object getItem(int pairTime) {
//        return searchArrayList.get(pairTime);
//    }
//
//    public long getItemId(int pairTime) {
//        return pairTime;
//    }
//
//    public View getView(int pairTime, View convertView, ViewGroup parent) {
//        ViewHolder holder;
//        if (convertView == null) {
//            convertView = mInflater.inflate(R.layout.item, null);
//            holder = new ViewHolder();
//            holder.txtName = (TextView) convertView.findViewById(R.id.pairTitleAndType);
//            holder.txtCityState = (TextView) convertView.findViewById(R.id.cityState);
//            holder.txtPhone = (TextView) convertView.findViewById(R.id.phone);
//
//            convertView.setTag(holder);
//        } else {
//            holder = (ViewHolder) convertView.getTag();
//        }
//
//        holder.txtName.setText(searchArrayList.get(pairTime).getName());
//        holder.txtCityState.setText(searchArrayList.get(pairTime).getCityState());
//        holder.txtPhone.setText(searchArrayList.get(pairTime).getPhone());
//
//        return convertView;
//    }
//
//    static class ViewHolder {
//        TextView txtName;
//        TextView txtCityState;
//        TextView txtPhone;
//    }
//}