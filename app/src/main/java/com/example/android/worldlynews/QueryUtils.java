package com.example.android.worldlynews;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by masus on 3/15/2018.
 */

public final class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private QueryUtils() {

    }

    public static List<Story> fetchStoryData(String requestUrl) {
        Log.i(LOG_TAG, "fetchStoryData method");
        URL url = createUrl(requestUrl);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        List<Story> stories = extractResultsFromJson(jsonResponse);

        return stories;
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if(url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* Time in milliseconds */);
            urlConnection.setConnectTimeout(15000 /* Time in milliseconds */);
            urlConnection.connect();
            if(urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e("QueryUtils", "Error Code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e("QueryUtils", "Problem retrieving earthquake JSON: ", e);
        } finally {
            if(urlConnection != null) {
                urlConnection.disconnect();
            }
            if(inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException{
        StringBuilder output = new StringBuilder();
        if(inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while(line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static List<Story> extractResultsFromJson(String jsonResponse) {

        ArrayList<Story> stories = new ArrayList<Story>();

        try {
            JSONObject rootObject = new JSONObject(jsonResponse);
            JSONObject responseObject = rootObject.getJSONObject("response");
            JSONArray results = responseObject.getJSONArray("results");

            for(int i = 0; i < results.length(); i++) {
                JSONObject currentStory = results.getJSONObject(i);
                JSONArray tagsArray = currentStory.getJSONArray("tags");

                String title = currentStory.getString("webTitle");
                String section = currentStory.getString("sectionName");
                String webUrl = currentStory.getString("webUrl");
                String author;
                if(tagsArray.length() != 0) {
                    JSONObject contributorTag = currentStory.getJSONArray("tags").getJSONObject(0);
                    author = contributorTag.getString("webTitle");
                } else {
                    author = "No author listed";
                }
                String publishDate = currentStory.getString("webPublicationDate");

                Story story = new Story(title, section, webUrl, author, publishDate);
                stories.add(story);
            }
        } catch(JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the Guardian JSON response: ", e);
        }
        return stories;
    }
}
