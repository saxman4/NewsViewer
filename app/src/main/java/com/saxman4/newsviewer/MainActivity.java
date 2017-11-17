// MainActivity.java
// Displays a top 10 articles for the specified news source
package com.saxman4.newsviewer;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRouter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    // List of news objects
    private List<News> newsList = new ArrayList<>();

    // ArrayAdapter for binding News objects to a ListView
    private NewsArrayAdapter newsArrayAdapter;
    private ListView newsListView; // displays news info

    private MediaPlayer myPlayer;  // define for background music
    String TAG = "saxman4";

    // configure Toolbar, ListView
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // autogenerated code to inflate layout and configure Toolbar
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // hide keyboard - This can be used to suppress the soft-keyboard until the user
        // actually touches the editText View.
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // create main menu
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    public String myUriString;
    private boolean musicInMainActivity = false; // start background music in MainActivity or MusicServiceTask

    private void musicOn() {
        if (musicInMainActivity == true) { // start in MMainActivity
            // stop radio if already playing
            Log.d(TAG, "usingMainActivity");
            if (myPlayer != null) {
                myPlayer.stop();
                myPlayer.release();
                myPlayer = null;
            }

            // now restart stream using new myUri
            myPlayer = MediaPlayer.create(this, Uri.parse(myUriString));
            myPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            myPlayer.start();
        }
        else { // start in MusicServiceTask
            Log.d(TAG, "using MusicService");
            Intent intent = new Intent(this, MusicServiceTask.class);
            intent.putExtra("radio_uri", myUriString);
            startService(intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        URL url;

        // create ArrayAdapter to bind newsList to the newsListView
        newsListView = (ListView) findViewById(R.id.newsListView);
        newsArrayAdapter = new NewsArrayAdapter(this, newsList);
        newsListView.setAdapter(newsArrayAdapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // Handle item selection
        switch (item.getItemId()) {
            case R.id.cnn_news:
                Log.d(TAG, "onOptionsItemSelected - cnn\n");
                url = createURL(R.id.cnn_news);
                toolbar.setTitle(R.string.cnn_news);
                break;
            case R.id.engadget_news:
                Log.d(TAG, "onOptionsItemSelected - engadget");
                url = createURL(R.id.engadget_news);
                toolbar.setTitle(R.string.engadget_news);
                break;
            case R.id.ew_news:
                Log.d(TAG, "onOptionsItemSelected - em\n");
                url = createURL(R.id.ew_news);
                toolbar.setTitle(R.string.ew_news);
                break;
            case R.id.hacker_news:
                Log.d(TAG, "onOptionsItemSelected - hacker\n");
                url = createURL(R.id.hacker_news);
                toolbar.setTitle(R.string.hacker_news);
                break;
            case R.id.mtv_news:
                Log.d(TAG, "onOptionsItemSelected - mtv\n");
                url = createURL(R.id.mtv_news);
                toolbar.setTitle(R.string.mtv_news);
                break;
            /*
            // test only - obsolete (never called)
            case R.id.music_on: // setup media player for background music
                if (myPlayer == null) {
                    // Play mp3 song...
                    //myPlayer = MediaPlayer.create(this, R.raw.oh_good_grief);
                    //myPlayer.setLooping(true);
                    //myPlayer.start();

                    // Play streaming radio
                    myUri = Uri.parse(getString(R.string.radio_url3));
                    myPlayer = MediaPlayer.create(this, myUri);
                    myPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    myPlayer.start();
                }
                return true;
            */
            case  R.id.music1:
                myUriString = getString(R.string.radio_url1);
                musicOn();
                return true;
            case R.id.music2:
                myUriString = getString(R.string.radio_url2);
                musicOn();
                return true;
            case R.id.music3:
                myUriString = getString(R.string.radio_url3);
                musicOn();
                return true;
            case R.id.music6:
                myUriString = getString(R.string.radio_url6);
                musicOn();
                return true;
            case R.id.music_off: // stop background music if currently playing
                if (musicInMainActivity == true) {
                    // use myPlaer from MainActivity
                    if (myPlayer != null) {
                        myPlayer.stop();
                        myPlayer.release();
                        myPlayer = null;
                    }
                }
                else {
                    stopService(new Intent(this, MusicServiceTask.class));
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

        if (url != null) {
            GetNewsTask getLocalNewsTask = new GetNewsTask();
            getLocalNewsTask.execute(url);
        }
        else {
            Snackbar.make(findViewById(R.id.coordinatorLayout),
                    R.string.invalid_url, Snackbar.LENGTH_LONG).show();
        }
        return true;
    }

    // create news web service URL using id number
    private URL createURL(int id) {
        String apiKey = getString(R.string.api_key);
        String baseUrl;

        switch (id) {
            case R.id.cnn_news:
                baseUrl = getString(R.string.cnn_web_service_url);
                break;
            case R.id.engadget_news:
                baseUrl = getString(R.string.engadget_web_service_url);
                break;
            case R.id.ew_news:
                baseUrl = getString(R.string.ew_web_service_url);
                break;
            case R.id.hacker_news:
                baseUrl = getString(R.string.hacker_web_service_url);
                break;
            case R.id.mtv_news:
                baseUrl = getString(R.string.mtv_web_service_url);
                break;
            default:
                baseUrl = getString(R.string.cnn_web_service_url);
                break;
        }

        try {
            String urlString = baseUrl + "&sortBy=top&apiKey=" + apiKey;
            return new URL(urlString);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null; // URL was malformed
    }

    // makes the REST web service call to get news data and
    // saves the data to a local HTML file
    private class GetNewsTask
            extends AsyncTask<URL, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(URL... params) {
            HttpURLConnection connection = null;

            try {
                connection = (HttpURLConnection) params[0].openConnection();
                int response = connection.getResponseCode();

                if (response == HttpURLConnection.HTTP_OK) {
                    StringBuilder builder = new StringBuilder();

                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream()))) {

                        String line;

                        while ((line = reader.readLine()) != null) {
                            builder.append(line);
                        }
                    }
                    catch (IOException e) {
                        Snackbar.make(findViewById(R.id.coordinatorLayout),
                                R.string.read_error, Snackbar.LENGTH_LONG).show();
                        e.printStackTrace();
                    }

                    return new JSONObject(builder.toString());
                }
                else {
                    Snackbar.make(findViewById(R.id.coordinatorLayout),
                            R.string.connect_error, Snackbar.LENGTH_LONG).show();
                }
            }
            catch (Exception e) {
                Snackbar.make(findViewById(R.id.coordinatorLayout),
                        R.string.connect_error, Snackbar.LENGTH_LONG).show();
                e.printStackTrace();
            }
            finally {
                connection.disconnect(); // close the HttpURLConnection
            }

            return null;
        }

        // process JSON response and update ListView
        @Override
        protected void onPostExecute(JSONObject news) {
            convertJSONtoArrayList(news); // repopulate newsList
            newsArrayAdapter.notifyDataSetChanged(); // rebind to ListView
            newsListView.smoothScrollToPosition(0); // scroll to top
        }
    }

    // create News objects from JSONObject containing the forecast
    private void convertJSONtoArrayList(JSONObject forecast) {
        newsList.clear(); // clear old news data

        try {
            // get forecast's "list" JSONArray
            JSONArray list = forecast.getJSONArray("articles");

            // convert each element of list to a News object
            for (int i = 0; i < list.length(); ++i) {
                JSONObject day = list.getJSONObject(i); // get one day's data

                // add new News object to newsList
                newsList.add(new News(
                        day.getString("title"),
                        day.getString("url")));
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
