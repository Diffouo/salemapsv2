package cm.pade.formapps;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * Created by Sumbang on 21/09/2016.
 */

public class MenuItemAdapter extends ArrayAdapter<MenuItems> {

    Context mContext;
    int layoutResourceId;
    MenuItems data[] = null;

    public MenuItemAdapter(Context mContext, int layoutResourceId,MenuItems[] data) {

        super(mContext, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItem = convertView;

        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        listItem = inflater.inflate(layoutResourceId, parent, false);

        ImageView imageViewIcon = (ImageView) listItem.findViewById(R.id.imageViewIcon);
        TextView textViewName = (TextView) listItem.findViewById(R.id.textViewName);

        MenuItems folder = data[position];

        imageViewIcon.setImageResource(folder.getIcone());
        textViewName.setText(folder.getLabel());

        return listItem;
    }



}