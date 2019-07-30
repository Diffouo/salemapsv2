package cm.pade.formapps;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Sumbang on 22/09/2016.
 */

public class GridAdapter extends BaseAdapter {

    private Context mContext;

    public GridAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.grid_item, parent, false);

        TextView text1 = (TextView) rowView.findViewById(R.id.titre);
        TextView text2 = (TextView) rowView.findViewById(R.id.key);
        ImageView img =  (ImageView) rowView.findViewById(R.id.img);

        if(position == 0 ) text1.setText("Nouveau point de vente");

        else if(position == 1 ) text1.setText("Modifier un point de vente");

        else if(position == 2 ) text1.setText("Synchronisation");

        else if(position == 3 ) text1.setText("Nos produits");

        else if(position == 4 ) text1.setText("Produits Concurrent");

        else if(position == 5 ) text1.setText("Prospection");

        else if(position == 6 ) text1.setText("Commande");

        else if(position == 7 ) text1.setText("Sortie Distributeur");

        else if(position == 8 ) text1.setText("Livraison");

        else if(position == 9 ) text1.setText("Recouvrement Client");

        else if(position == 10 ) text1.setText("Versement Distributeur");

        else if(position == 11 ) text1.setText("Inventaire");
        //  text1.setText(iconLabel[position]);

        img.setImageResource(mThumbIds[position]);
        return rowView;
    }

    // references to our images
    private Integer[] mThumbIds = {
            R.drawable.ic_add_location, R.drawable.ic_edit_location,
            R.drawable.ic_synchronisation,
            R.drawable.ic_presence_okf, R.drawable.ic_concurent,
            R.drawable.ic_prospection, R.drawable.ic_command,
            R.drawable.ic_sortie_distrib, R.drawable.ic_livraison,
            R.drawable.ic_recouvre_clt, R.drawable.ic_versement_distrib,
            R.drawable.ic_invent
    };
    private Integer[] iconKey={ 0, 1, 2,3, 4, 5, 6, 7, 8, 9, 10,11};
}
