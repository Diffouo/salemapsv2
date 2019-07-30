package cm.pade.formapps;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sumbang on 27/09/2016.
 */

public class CmdProduitAdapter extends ArrayAdapter<Commande> {

    public ArrayList<Commande> produitList; private Context ctx;

    public CmdProduitAdapter(Context context, int textViewResourceId, ArrayList<Commande> produitsList) {

        super(context, textViewResourceId, produitsList);

        this.produitList = new ArrayList<Commande>();
        this.produitList.addAll(produitsList);
        this.ctx = context;
    }

    private class ViewHolder {
         CheckBox name;
        EditText qte;
        Spinner type;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        Log.v("ConvertView", String.valueOf(position));

        if (convertView == null) {

            LayoutInflater vi = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = vi.inflate(R.layout.cmd_produit_list, null);

            holder = new ViewHolder();
            holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);
            holder.qte = (EditText) convertView.findViewById(R.id.editText);
            holder.type = (Spinner) convertView.findViewById(R.id.type);

            convertView.setTag(holder);

            holder.name.setOnClickListener( new View.OnClickListener() {
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v ;
                    Commande pdt = (Commande) cb.getTag();
                    // Toast.makeText(ctx, "Clicked on Checkbox: " + cb.getText() + " is " + cb.isChecked(), Toast.LENGTH_LONG).show();
                    pdt.getProduit().setSelected(cb.isChecked());
                }
            });



        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.qte.setTag(position);

        holder.qte.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                int pos = (Integer) holder.qte.getTag();

                if(charSequence.toString().isEmpty()) produitList.get(pos).setQte(0);

                else produitList.get(pos).setQte(Integer.parseInt(charSequence.toString()));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        holder.type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                 produitList.get(position).setType(i);

                Log.e("POS",""+i);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        holder.type.setTag(position);


        Commande pdt = produitList.get(position);
        holder.name.setText(pdt.getProduit().getName());
        holder.name.setChecked(pdt.getProduit().isSelected());
        holder.name.setTag(pdt);
        holder.qte.setText(""+produitList.get(position).getQte());

        return convertView;

    }
}
