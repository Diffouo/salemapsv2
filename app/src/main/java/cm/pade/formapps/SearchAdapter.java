package cm.pade.formapps;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by christian.sumbang on 17/01/2017.
 */

public class SearchAdapter extends CursorAdapter {

    private List<String> items;
    private TextView text;

    public SearchAdapter(Context context, Cursor cursor, List<String> items) {
        super(context, cursor, false);
        this.items = items;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {

       // LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.search_adapter, viewGroup, false);

        text = (TextView) view.findViewById(R.id.textViewName);

        return  view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        text.setText((CharSequence) this.items.get(cursor.getPosition()));
    }
}
