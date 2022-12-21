package edu.byuh.cis.cs203.MiniGame.misc;

import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;
import java.util.List;

public class Timer extends Handler {

    List<TickListener> observers;
    boolean j;



    public Timer() {

        observers = new ArrayList<>();
        sendMessageDelayed(obtainMessage(), 0);

    }

    public void subscribe(TickListener t) {
        observers.add(t);
    }

    public void unsubscribe(TickListener t) {
        observers.remove(t);
    }

    private void notifyListeners() {
        for (TickListener t : observers) {
            t.tick();
        }
    }


    @Override
    public void handleMessage(Message msg) {
        if(j==false){
            notifyListeners();
            sendMessageDelayed(obtainMessage(), 100);
        }
        else{
        }
    }

    public void pause() {
        j = true;
    }

    /**
     * Restart the timer after it's been paused
     */
    public void resume() {
        j = false;
        sendMessageDelayed(obtainMessage(), 0);
    }

    /**
     * stop the timer
     */
    public void stop() {
        pause();
        observers.clear();
    }


}


