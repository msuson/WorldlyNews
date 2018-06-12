package com.example.android.worldlynews;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * Created by masus on 3/15/2018.
 */

public class StoryLoader extends AsyncTaskLoader<List<Story>> {

    private static final String LOG_TAG = StoryLoader.class.getSimpleName();

    private String url;

    public StoryLoader(Context context, String url) {
        super(context);
        this.url = url;
    }

    @Override
    protected void onStartLoading() {
        Log.i(LOG_TAG, "onStartLoading method");
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public List<Story> loadInBackground() {
        Log.i(LOG_TAG, "loadInBackground method");
        if(url == null) {
            return null;
        }

        List<Story> stories = QueryUtils.fetchStoryData(url);

        return stories;
    }
}
