package com.saxman4.newsviewer;

import android.app.Service;
import android.content.Intent;
import android.icu.text.IDNA;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by saxman4 on 11/15/17.
 *
 * OBSOLETE: This Service class runs on the main thread so this is not a good class to use for
 * starting the internet radio stations.  We will use the MusicServiceTask instead, which extends
 * the IntentService class, and runs in a separate thread.
 *
 */

public class MusicService extends Service {

    private MediaPlayer myPlayer;
    String TAG = "saxman4";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // stop radio if already playing
        if (myPlayer != null) {
            myPlayer.stop();
            myPlayer.release();
            myPlayer = null;
        }

        // now restart stream using new myUri
        if (intent != null && intent.getExtras() != null) {
            //myPlayer = MediaPlayer.create(this, Uri.parse(myRadio));
            myPlayer = MediaPlayer.create(this, Uri.parse(intent.getExtras().getString("radio_uri")));
            myPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            myPlayer.start();
            return START_STICKY;
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (myPlayer != null) {
            myPlayer.stop();
            myPlayer.release();
            myPlayer = null;
        }
    }
}
