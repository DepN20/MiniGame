package edu.byuh.cis.cs203.MiniGame.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageView;

import edu.byuh.cis.cs203.MiniGame.R;

public class SplashActivity extends Activity {

    private ImageView iv;

    @Override
    protected void onCreate(Bundle b){
        super.onCreate(b);
        iv = new ImageView(this);
        iv.setImageResource(R.drawable.splash);
        iv.setScaleType(ImageView.ScaleType.FIT_XY);
        setContentView(iv);
    }
    /**
     * Touch Zone for each button
     * About box, setting, play buttons are there
     */
    @Override
    public boolean onTouchEvent(MotionEvent m) {
        var w = iv.getWidth();
        var h = iv.getHeight();

        System.out.println(w);
        System.out.println(h);
        System.out.println(m.getX());
        System.out.println(m.getY());
        if (m.getAction() == MotionEvent.ACTION_DOWN) {
            var x = m.getX();
            var y = m.getY();
            if (x> w*0.94f && y<h*0.17f) {//about box
//                Intent tent = new Intent(this, MyPrefsActivity.class);
//                startActivity(tent);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("About Box")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                var box = builder.create();
                box.show();
            }
            else if (x <w*0.05f && y < h * (0.16f)) {//Setting
                Intent tent = new Intent(this, MyPrefsActivity.class);
                startActivity(tent);

            }
            else if(x>w*0.40f && y>h*0.90f && x<w*0.6f && y<h*0.99f){//Play button
                Intent tent = new Intent(this, MainActivity.class);
                startActivity(tent);
            }
        }
        return true;
    }
}
