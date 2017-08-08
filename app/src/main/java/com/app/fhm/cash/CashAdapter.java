package com.app.fhm.cash;

import android.content.Context;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.fhm.R;


import java.text.SimpleDateFormat;
import java.util.Calendar;

import java.util.List;


public class CashAdapter extends RecyclerView.Adapter<CashAdapter.ViewHolder> {

    private List<Cash> _lc;
    private Context context;
    private View.OnClickListener edit;

    private Calendar calendar = Calendar.getInstance();
    private SimpleDateFormat DateFormat = new SimpleDateFormat("dd.MM.yyyy");

    public CashAdapter(List<Cash> lc, View.OnClickListener e, Context c) {
        _lc = lc;
        edit = e;
        context = c;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout mRelativeLayout;

        public ViewHolder(RelativeLayout v1) {
            super(v1);
            mRelativeLayout = v1;
        }
    }

    @Override
    public CashAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RelativeLayout cv = (RelativeLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cash_item, parent, false);

        ViewHolder vh = new ViewHolder(cv);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        LinearLayout llPosition = (LinearLayout) holder.mRelativeLayout.findViewById(R.id.llCash);
        TextView Beschreibung = (TextView) holder.mRelativeLayout.findViewById(R.id.tvTitle);
        TextView Summe = (TextView) holder.mRelativeLayout.findViewById(R.id.tvSummary);
        TextView Datum = (TextView) holder.mRelativeLayout.findViewById(R.id.tvDateButton);
        TextView Repeat = (TextView) holder.mRelativeLayout.findViewById(R.id.tvRepeat);

        Cash c = _lc.get(position);
        holder.mRelativeLayout.setTag(c);
        holder.mRelativeLayout.setOnClickListener(edit);
        Beschreibung.setText(c.getContent());
        Summe.setText(String.format("%.2f", c.getTotal()));
        Datum.setText(DateFormat.format(c.getCreateDate()));
        String stringArray[] = context.getResources().getStringArray(R.array.repeat_arrays);
        Repeat.setText(stringArray[c.getRepeat()]);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return _lc.size();
    }
}

