// News.java
// Maintains one day's news information
package com.saxman4.newsviewer;

import android.util.Log;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

class News {
    public final String title;
    public final String url;

    // constructor
    public News(String title, String url) {
        this.title = title;
        this.url = url;
    }

}
