// WeatherArrayAdapter.java
// An ArrayAdapter for displaying a List<Weather>'s elements in a ListView
package com.saxman4.newsviewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class NewsArrayAdapter extends ArrayAdapter<News> {
    // class for reusing views as list items scroll off and onto the screen
    private static class ViewHolder {
        TextView titleTextView;
    }

    // constructor to initialize superclass inherited members
    public NewsArrayAdapter(Context context, List<News> forecast) {
        super(context, -1, forecast);
    }

    // creates the custom views for the ListView's items
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // get news object for this specified ListView position
        News day = getItem(position);

        ViewHolder viewHolder; // object that reference's list item's views

        // create a new ViewHolder
        viewHolder = new ViewHolder();
        LayoutInflater inflater = LayoutInflater.from(getContext());
        convertView = inflater.inflate(R.layout.list_item, parent, false);

        viewHolder.titleTextView = (TextView) convertView.findViewById(R.id.titleTextView);

        // get other data from news object and place into views
        Context context = getContext(); // for loading String resources

        //viewHolder.titleTextView.setText(day.title + "\n\n" + day.url + "\n");

        // Use fromHtml ad LinkMovementMethd to replace long url text into clickable "link"
        viewHolder.titleTextView.setText(Html.fromHtml(day.title + "<a href=" + day.url + ">" + " link" + "</a>" + "<br>"));
        viewHolder.titleTextView.setMovementMethod(LinkMovementMethod.getInstance());

        return convertView; // return completed list item to display
    }
}
