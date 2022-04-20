package com.example.explorejournal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class AttemptAdapter  extends BaseAdapter {

    private Context context;
    private ArrayList<Attempt> attempts;

    public AttemptAdapter(Context context, ArrayList<Attempt> attempts) {
        this.context = context;
        this.attempts = attempts;
    }

    @Override
    public int getCount() {

        if (attempts == null) {
            return 0;
        } else {
            return attempts.size();
        }
    }

    @Override
    public Attempt getItem(int i) {
        if (attempts != null && attempts.size() > 0) {
            return attempts.get(i);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int i) {
        if (attempts != null && attempts.size() > 0) {
            return i;
        } else {
            return -1;
        }
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder holder;

        if (view == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.attempt_list_item, null, true);

            holder.attemptDate = (TextView) view.findViewById(R.id.attempt_date);
            holder.attemptRating = (TextView) view.findViewById(R.id.attempt_rating);
            holder.attemptNote = (TextView) view.findViewById(R.id.attempt_note);



            view.setTag(holder);
        }else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = (ViewHolder)view.getTag();
        }

        holder.attemptDate.setText(String.valueOf(attempts.get(i).getDate()));
        holder.attemptRating.setText(String.valueOf(attempts.get(i).getRating()));
        holder.attemptNote.setText(String.valueOf(attempts.get(i).getNote()));

        return view;

    }

    private class ViewHolder {

        protected TextView attemptDate, attemptRating, attemptNote;

    }
}
