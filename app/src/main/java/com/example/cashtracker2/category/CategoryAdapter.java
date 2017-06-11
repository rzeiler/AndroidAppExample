package com.example.cashtracker2.category;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.cashtracker2.R;

import java.text.DateFormat;
import java.util.List;
import java.util.Locale;


public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<Category> _lc;
    private Context context;
    private View.OnClickListener edit, open;
    private DateFormat f = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault());

    public CategoryAdapter(List<Category> lc, View.OnClickListener e, View.OnClickListener o, Context c) {
        _lc = lc;
        edit = e;
        open = o;
        context = c;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout mLinearLayout;

        public ViewHolder(LinearLayout v1) {
            super(v1);
            mLinearLayout = v1;
        }
    }

    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout cv = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_item, parent, false);
        ViewHolder vh = new ViewHolder(cv);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TextView tvBigletter = (TextView) holder.mLinearLayout.findViewById(R.id.tvBigletter);
        TextView tvTitle = (TextView) holder.mLinearLayout.findViewById(R.id.tvTitle);
        TextView tvSum = (TextView) holder.mLinearLayout.findViewById(R.id.tvSum);
        Category c = _lc.get(position);

        String bl = c.getTitle();

        if (bl.length() > 0) {
            bl = c.getTitle().substring(0, 1).toUpperCase();
            tvBigletter.setText(bl);
        }
        tvTitle.setText(String.valueOf(c.getTitle()));
        tvSum.setText(String.format("%.2f €", c.getTotal()));
        LinearLayout llLeft = (LinearLayout) holder.mLinearLayout.findViewById(R.id.llLeft);
        llLeft.setTag(c);
        llLeft.setOnClickListener(open);
        tvBigletter.setTag(c);
        tvBigletter.setOnClickListener(edit);

        if (bl.length() > 0) {
            /* color */
            String packageName = context.getPackageName();
            int resId = context.getResources().getIdentifier(bl.toLowerCase(), "color", packageName);
            int iColor = context.getResources().getColor(resId);
            tvTitle.setTextColor(iColor);
            GradientDrawable bgShape = (GradientDrawable) tvBigletter.getBackground();
            bgShape.setColor(iColor);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return _lc.size();
    }
}

