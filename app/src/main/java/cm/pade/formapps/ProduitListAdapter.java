package cm.pade.formapps;

import android.content.Context;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sumbang on 27/09/2016.
 */

public class ProduitListAdapter extends ArrayAdapter<Produit> {

    public ArrayList<Produit> produitList; private Context ctx;

    public ProduitListAdapter(Context context, int textViewResourceId, ArrayList<Produit> produitsList) {

        super(context, textViewResourceId, produitsList);

        this.produitList = new ArrayList<Produit>();
        this.produitList.addAll(produitsList);
        this.ctx = context;
    }

    private class ViewHolder {
        TextView code;
        CheckBox name;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        Log.v("ConvertView", String.valueOf(position));

        if (convertView == null) {

            LayoutInflater vi = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = vi.inflate(R.layout.produit_list, null);

            holder = new ViewHolder();
            //holder.code = (TextView) convertView.findViewById(R.id.code);
            holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);
            convertView.setTag(holder);

            holder.name.setOnClickListener( new View.OnClickListener() {
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v ;
                    Produit pdt = (Produit) cb.getTag();
                   // Toast.makeText(ctx, "Clicked on Checkbox: " + cb.getText() + " is " + cb.isChecked(), Toast.LENGTH_LONG).show();
                    pdt.setSelected(cb.isChecked());
                }
            });
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        Produit pdt = produitList.get(position);
        //holder.code.setText(" (" +  pdt.getId() + ")");
        holder.name.setText(pdt.getName());
        holder.name.setChecked(pdt.isSelected());
        holder.name.setTag(pdt);

        return convertView;

    }
}
