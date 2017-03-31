package vcs.com.demoall;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class PagerAdapter extends BaseAdapter {

    Context mContext;
    LayoutInflater inflater;
    ArrayList<PageModel> data;
    //private static final int NOT_SELECTED = -1;
    //private int selectedPos = NOT_SELECTED;

    public PagerAdapter(Context c, ArrayList<PageModel> m) {
        data = m;
        mContext = c;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        TextView no;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //change
        /*View itemView = inflater.inflate(R.layout.horizontal_list_itm, null);
        no = (TextView) itemView.findViewById(R.id.xyz);
        no.setText("" + data.get(position).getPageNo());

        if (data.get(position).getisSelected()) {
            no.setBackgroundColor(Color.parseColor(Content.Appcolor));
            //no.setBackgroundResource(R.drawable.b_field_green);
            no.setTextColor(Color.parseColor("#ffffff"));
            no.requestFocus();
        } else {
            no.setBackgroundResource(R.drawable.b_field_white);
            no.setTextColor(Color.parseColor("#000000"));
        }

        return itemView;*/

        return convertView;
    }

}
