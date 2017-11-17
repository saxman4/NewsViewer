package com.saxman4.newsviewer;

import android.app.IntentService;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Looper;
import android.support.annotation.Nullable;

/**
 * Created by saxman4 on 11/16/17.
 *
 * The MusicServiceTask InentService class replaces the MusicService Service class because it
 * runs in a separate thread, therefore will not disturb the main thread performance.
 * 
 */

public class MusicServiceTask extends IntentService {

    private MediaPlayer myPlayer;
    String TAG = "saxman4";

    public MusicServiceTask(String name, MediaPlayer myPlayer) {
        super(name);
        this.myPlayer = myPlayer;
    }

    public MusicServiceTask(String name) {
        super(name);
    }

    public MusicServiceTask() {
        super("MusicServiceTask");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
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
        }
        Looper.loop();
        return;
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
