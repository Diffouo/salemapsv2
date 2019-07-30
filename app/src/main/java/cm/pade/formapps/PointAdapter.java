package cm.pade.formapps;



import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sumbang on 21/09/2016.
 */
public class PointAdapter extends BaseAdapter {

    private List<Point> annonces; private Context ctx;

    public PointAdapter(Context context, List<Point> cp) {

        this.annonces = cp; this.ctx = context;
    }


    @Override
    public int getCount() {
        return annonces.size();
    }

    @Override
    public Object getItem(int position) {
        return annonces.get(position);
    }

    @Override
    public long getItemId(int position) {
        return annonces.indexOf(getItem(position));
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final Point items = (Point) getItem(position);

        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        convertView = inflater.inflate(R.layout.point_item_list, parent, false);

        // Lookup view for data population
        TextView name = (TextView) convertView.findViewById(R.id.ptname);
        TextView id = (TextView) convertView.findViewById(R.id.ptid);

        // Populate the data into the template view using the data object
        name.setText(items.getNom());
        id.setText("0");

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.e("TAG", "Clique sur " + items.getNom());

            }
        });

        // Return the completed view to render on screen
        return convertView;
    }
}
