package cm.pade.formapps;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Sumbang on 30/10/2016.
 */

public class DistributionAdapter extends ArrayAdapter<Distribution> {

    public ArrayList<Distribution> produitList; private Context ctx;

    public DistributionAdapter(Context context, int textViewResourceId, ArrayList<Distribution> produitsList) {

        super(context, textViewResourceId, produitsList);

        this.produitList = new ArrayList<Distribution>();
        this.produitList.addAll(produitsList);
        this.ctx = context;
    }

    private class ViewHolder {
        CheckBox name;
        EditText qte;
        EditText prix;
        Spinner type;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final DistributionAdapter.ViewHolder holder;
        Log.v("ConvertView", String.valueOf(position));

        if (convertView == null) {

            LayoutInflater vi = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = vi.inflate(R.layout.distribution_pdt_list, null);

            holder = new DistributionAdapter.ViewHolder();
            holder.prix = (EditText) convertView.findViewById(R.id.editText2);
            holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);
            holder.qte = (EditText) convertView.findViewById(R.id.editText);
            holder.type = (Spinner) convertView.findViewById(R.id.type);

            convertView.setTag(holder);

            holder.name.setOnClickListener( new View.OnClickListener() {
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v ;
                    Distribution pdt = (Distribution) cb.getTag();
                    // Toast.makeText(ctx, "Clicked on Checkbox: " + cb.getText() + " is " + cb.isChecked(), Toast.LENGTH_LONG).show();
                    pdt.getProduit().setSelected(cb.isChecked());
                }
            });


        }
        else {
            holder = (DistributionAdapter.ViewHolder) convertView.getTag();
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

        holder.prix.setTag(position);

        holder.prix.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                int pos = (Integer) holder.prix.getTag();

                if(charSequence.toString().isEmpty()) produitList.get(pos).setPrice(0);

                else produitList.get(pos).setPrice(Integer.parseInt(charSequence.toString()));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        holder.type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                produitList.get(position).setUnite(i);

                Log.e("POS",""+i);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        holder.type.setTag(position);

        Distribution pdt = produitList.get(position);
        holder.prix.setText(""+produitList.get(position).getPrice());
        holder.name.setText(pdt.getProduit().getName());
        holder.name.setChecked(pdt.getProduit().isSelected());
        holder.name.setTag(pdt);
        holder.qte.setText(""+produitList.get(position).getQte());

        return convertView;

    }
}

