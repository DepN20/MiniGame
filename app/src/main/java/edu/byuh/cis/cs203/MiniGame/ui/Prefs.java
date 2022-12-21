package edu.byuh.cis.cs203.MiniGame.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

import edu.byuh.cis.cs203.MiniGame.R;

public class Prefs extends PreferenceFragmentCompat {

    public static final String PLAY_SOUNDTRACK = "PLAY_SOUNDTRACK";
    public static final String RAPID_MISSILE = "RAPID MISSILE";
    public static final String RAPID_DEPCHARGE = "RAPID DEPTH CHARGE";
    public static final String SPEED_OBJECT = "SPEED OBJECT";
    public static final String DIR_PLANE = "DIRECTION PLANE";
    private static final String OPT_NUM_SUBS = "num_subs";
    private static final String OPT_NUM_PLANES = "num_planes";

    /**
     * All the preferences on setting button
     */
    @Override
    public void onCreatePreferences(Bundle b, String s) {
        Context context = getPreferenceManager().getContext();
        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(context);

        //TODO add preference widgets here
        SwitchPreference musicPref = new SwitchPreference(context);
        musicPref.setTitle(R.string.music);
        musicPref.setSummaryOff(R.string.summary_offm);
        musicPref.setSummaryOn(R.string.summary_onm);
        musicPref.setKey(PLAY_SOUNDTRACK);

        SwitchPreference rapidfire = new SwitchPreference(context);
        rapidfire.setTitle(R.string.title);
        rapidfire.setSummaryOff(R.string.summary_off);
        rapidfire.setSummaryOn(R.string.summary_on);
        rapidfire.setKey(RAPID_MISSILE);

        SwitchPreference rapidDepthCharge = new SwitchPreference(context);
        rapidDepthCharge.setTitle(R.string.title1);
        rapidDepthCharge.setSummaryOff(R.string.summary_off1);
        rapidDepthCharge.setSummaryOn(R.string.summary_on1);
        rapidDepthCharge.setKey(RAPID_DEPCHARGE);

        ListPreference speedPlane = new ListPreference(context);
        speedPlane.setTitle(R.string.title_speed);
        speedPlane.setSummary(R.string.summary_speed);
        speedPlane.setKey(SPEED_OBJECT);
        speedPlane.setEntries(R.array.speed);
        speedPlane.setEntryValues(new String[] {"0.005", "0.02", "0.1"});

        ListPreference dirPlane = new ListPreference(context);
        dirPlane.setTitle(R.string.title_dir);
        dirPlane.setSummary(R.string.summary_dir);
        dirPlane.setKey(DIR_PLANE);
        dirPlane.setEntries(R.array.dir);
        dirPlane.setEntryValues(new String[]{"1", "2", "3"});

        ListPreference numPlanes = new ListPreference(context);
        numPlanes.setTitle(R.string.num_plane);
        numPlanes.setSummary(R.string.summary_numplane);
        numPlanes.setEntries(R.array.num_plane);
        numPlanes.setEntryValues(new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"});
        numPlanes.setKey(OPT_NUM_PLANES);


        ListPreference numSubs = new ListPreference(context);
        numSubs.setTitle(R.string.num_sub);
        numSubs.setSummary(R.string.summary_numsub);
        numSubs.setEntries(R.array.num_sub);
        numSubs.setEntryValues(new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"});
        numSubs.setKey(OPT_NUM_SUBS);


        screen.addPreference(musicPref);
        screen.addPreference(rapidfire);
        screen.addPreference(rapidDepthCharge);
        screen.addPreference(speedPlane);
        screen.addPreference(dirPlane);
        screen.addPreference(numPlanes);
        screen.addPreference(numSubs);


        setPreferenceScreen(screen);
    }

    /**
     *
     * all the preferences to use
     */
    public static boolean rapidMissile(Context c){
        return PreferenceManager.getDefaultSharedPreferences(c).getBoolean(RAPID_MISSILE,true);
    }
    public static boolean rapidDepthCharge(Context c){
        return PreferenceManager.getDefaultSharedPreferences(c).getBoolean(RAPID_DEPCHARGE,true);
    }
    public static float speed (Context c){
        String tmp = PreferenceManager.getDefaultSharedPreferences(c).getString(SPEED_OBJECT, "0.02");
        return Float.parseFloat(tmp);
    }
    public static String dir (Context c){
        return PreferenceManager.getDefaultSharedPreferences(c).getString(DIR_PLANE,"3");
    }

    public static int numPlane(Context context) {
        String tmp = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(OPT_NUM_PLANES, "3");
        return Integer.parseInt(tmp);
    }

    public static int numSub(Context context) {
        String tmp = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(OPT_NUM_SUBS, "3");
        return Integer.parseInt(tmp);
    }

}
