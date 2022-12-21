package edu.byuh.cis.cs203.MiniGame.ui;

import androidx.preference.PreferenceManager;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;

import edu.byuh.cis.cs203.MiniGame.R;

public class MainActivity extends Activity {

    private GameView gv;
    private MediaPlayer soundtrack;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        soundtrack = MediaPlayer.create(this, R.raw.background_music);
        soundtrack.setLooping(true);
        gv = new GameView(this);
        setContentView(gv);
    }
    /**
     * These for the music playing
     * added for time stop function when it is off screen
     */
    @Override
    protected void onResume(){
        super.onResume();
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Prefs.PLAY_SOUNDTRACK,true)){
            soundtrack.start();
        }
        gv.resumeGame();
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Prefs.PLAY_SOUNDTRACK, true)) {
            soundtrack.pause();
        }
        gv.pauseGame();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Prefs.PLAY_SOUNDTRACK,true)) {
            soundtrack.release();
        }

    }


}