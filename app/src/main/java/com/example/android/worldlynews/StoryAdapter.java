package com.example.android.worldlynews;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by masus on 3/15/2018.
 */

public class StoryAdapter extends ArrayAdapter<Story> {

    private static final String LOG_TAG = StoryAdapter.class.getSimpleName();

    public StoryAdapter(Context context, ArrayList<Story> stories) {
        super(context, 0, stories);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.story_list_item, parent, false);
        }

        Story story = getItem(position);

        TextView titleView = listItemView.findViewById(R.id.title_view);
        titleView.setText(story.getTitle());

        TextView sectionView = listItemView.findViewById(R.id.section_view);
        sectionView.setText(story.getSection());

        TextView authorView = listItemView.findViewById(R.id.author_view);
        authorView.setText(story.getAuthor());

        TextView pubishDateView = listItemView.findViewById(R.id.publish_date_view);
        String publishDate = formatDate(story.getPublishDate());
        pubishDateView.setText(publishDate);

        return listItemView;
    }

    private String formatDate(String date) {
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        String formattedDateString = "";

        try {
            formattedDateString = dateFormat.format(originalFormat.parse(date));
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Parse exception: ", e);
        }
        return formattedDateString;
    }
}
