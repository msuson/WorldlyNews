package com.example.android.worldlynews;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Story>> {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private final String GUARDIAN_REQUEST_URL = "https://content.guardianapis.com/search?";
    private StoryAdapter storyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView storyListView = findViewById(R.id.list);
        TextView emptyView = findViewById(R.id.empty_view);
        storyListView.setEmptyView(emptyView);

        storyAdapter = new StoryAdapter(this, new ArrayList<Story>());

        storyListView.setAdapter(storyAdapter);

        storyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Uri webpage = Uri.parse(storyAdapter.getItem(i).getWebUrl());
                Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                startActivity(intent);
            }
        });

        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            getLoaderManager().initLoader(0, null, this);
        } else {
            ProgressBar spinner = findViewById(R.id.loading_spinner);
            spinner.setVisibility(View.GONE);
            emptyView.setText(R.string.no_internet);
        }
    }

    @Override
    public Loader<List<Story>> onCreateLoader(int i, Bundle bundle) {
        Log.i(LOG_TAG, "onCreateLoader callback");

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String section = sharedPrefs.getString(
                getString(R.string.settings_sections_key),
                getString(R.string.settings_sections_label));

        Uri baseUri = Uri.parse(GUARDIAN_REQUEST_URL);

        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("q", "world news");
        if(!section.equals("all")) {
            uriBuilder.appendQueryParameter("section", section);
        }
        uriBuilder.appendQueryParameter("format", "json");
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("order-by", "newest");
        uriBuilder.appendQueryParameter("page-size", "20");
        uriBuilder.appendQueryParameter("api-key", "test");

        Log.i(LOG_TAG, "URL" + uriBuilder.toString());

        return new StoryLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Story>> loader, List<Story> stories) {
        Log.i(LOG_TAG, "onLoadFinished callback");

        ProgressBar spinner = findViewById(R.id.loading_spinner);
        spinner.setVisibility(View.GONE);

        storyAdapter.clear();

        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();

        if(stories != null && !stories.isEmpty() && isConnected) {
            storyAdapter.addAll(stories);
        } else if((stories == null || stories.isEmpty()) && isConnected) {
            TextView emptyView = findViewById(R.id.empty_view);
            emptyView.setText(R.string.no_stories);
        } else if(!isConnected) {
            TextView emptyView = findViewById(R.id.empty_view);
            emptyView.setText(R.string.no_internet);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Story>> loader) {
        Log.i(LOG_TAG, "onLoadFinished callback");
        storyAdapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
