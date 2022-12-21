package edu.byuh.cis.cs203.MiniGame.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.media.MediaPlayer;
import android.view.MotionEvent;
import android.view.View;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import edu.byuh.cis.cs203.MiniGame.R;
import edu.byuh.cis.cs203.MiniGame.graphics.Airplane;
import edu.byuh.cis.cs203.MiniGame.graphics.Battleship;
import edu.byuh.cis.cs203.MiniGame.graphics.DepthCharge;
import edu.byuh.cis.cs203.MiniGame.graphics.Missile;
import edu.byuh.cis.cs203.MiniGame.graphics.Sprite;
import edu.byuh.cis.cs203.MiniGame.graphics.Submarine;
import edu.byuh.cis.cs203.MiniGame.misc.Direction;
import edu.byuh.cis.cs203.MiniGame.misc.TickListener;
import edu.byuh.cis.cs203.MiniGame.misc.Timer;

/**
 * It all happens here: the drawing, the tapping, the animation.
 */
public class GameView extends View implements TickListener {

    private Bitmap water;
    private Bitmap pop;
    private Battleship battleship;
    private List<Airplane> planes;
    private List<Submarine> subs;
    private boolean init;
    private Timer tim;
    private List<DepthCharge> bombs;
    private List<Missile> missiles;
    private float w,h;
    private Paint missilePaint;
    private boolean leftPop, rightPop;
    private int score;
    private Paint scorePaint;
    private int timeLeft;
    private Paint timePaint;
    long timeNow;
    long timeBefore;
    int minute;
    int second;
    int highscore;
    public static Context MYCON;




    /**
     * Constructor for our View subclass. Loads all the images
     * @param context a reference to our main Activity class
     */
    public GameView(Context context) {
        super(context);
        reader();
        MYCON = context;
        pop = BitmapFactory.decodeResource(getResources(), R.drawable.star);
        water = BitmapFactory.decodeResource(getResources(), R.drawable.water);
        bombs = new ArrayList<>();
        missiles = new ArrayList<>();
        missilePaint = new Paint();
        missilePaint.setColor(Color.DKGRAY);
        missilePaint.setStyle(Paint.Style.STROKE);
        planes = new ArrayList<>();
        subs = new ArrayList<>();
        scorePaint = new Paint();
        scorePaint.setColor(Color.BLACK);
        scorePaint.setStyle(Paint.Style.FILL);
        scorePaint.setTextAlign(Paint.Align.LEFT);
        timePaint = new Paint();
        timePaint.setColor(Color.BLACK);
        timePaint.setStyle(Paint.Style.FILL);
        timePaint.setTextAlign(Paint.Align.RIGHT);

        init = false;
        leftPop = false;
        rightPop = false;
        score = 0;
        timeLeft = 180;

    }

    /**
     * Scales, positions, and renders the scene
     * @param c the Canvas object, provided by system
     */
    @Override
    public void onDraw(Canvas c) {
        if (init == false) {
            w = getWidth();
            h = getHeight();
            scorePaint.setTextSize(h/20);

            //scale the water
            final int waterWidth = (int)(w/50);
            water = Bitmap.createScaledBitmap(water, waterWidth,waterWidth, true);

            //scale the "pop"
            final int popWidth = (int)(w*0.03f);
            pop = Bitmap.createScaledBitmap(pop, popWidth, popWidth, true);

            //load and scale the battleship
            battleship = Battleship.getInstance(getResources(), w);
            missilePaint.setStrokeWidth(w*0.0025f);

            //position sprites
            final float battleshipX = w/2; //center the ship
            final float battleshipY = h/2-battleship.getHeight()*0.04f; //put the ship above the water line
            battleship.setLocation(battleshipX, battleshipY);

            //DIRTY HACK: inform Airplane class of acceptable upper/lower limits of flight
            final float battleshipTop = battleship.getTop()+battleship.getHeight()*0.4f;
            Airplane.setSkyLimits(0, battleshipTop);

            //DIRTY HACK: inform Submarine class of acceptable upper/lower limits of depth
            Submarine.setWaterDepth(h/2 + waterWidth*2, h);

            //load and scale the enemies
            float speed = Prefs.speed(getContext());
            for (int i=0; i<Prefs.numPlane(getContext()); i++) {
                planes.add( new Airplane(getResources(),w,speed));
            }
            for (int i=0; i<Prefs.numSub(getContext()); i++) {
                subs.add(new Submarine(getResources(),w,speed));
            }

            //Once everything is in place, start the animation loop!
            tim = new Timer();
            //Using "method reference" syntax here, just for fun
            planes.forEach(tim::subscribe);
            subs.forEach(tim::subscribe);
            tim.subscribe(this);
            init = true;
        }

        //now draw everything
        c.drawColor(Color.WHITE);

        float waterX = 0;
        while (waterX < w) {
            c.drawBitmap(water, waterX, h/2, null);
            waterX += water.getWidth();
        }

        battleship.draw(c);
        planes.forEach(p -> p.draw(c));
        subs.forEach(s -> s.draw(c));
        missiles.forEach(m -> m.draw(c));
        bombs.forEach(d -> d.draw(c));

        if (leftPop) {
            final PointF popLocation = battleship.getLeftCannonPosition();
            c.drawBitmap(pop, popLocation.x-pop.getWidth(), popLocation.y-pop.getHeight(), null);
            leftPop = false;
        }
        if (rightPop) {
            final PointF popLocation = battleship.getRightCannonPosition();
            c.drawBitmap(pop, popLocation.x, popLocation.y-pop.getHeight(), null);
            rightPop = false;
        }
        c.drawText("SCORE: " + score, 100, h*0.6f, scorePaint);
        if(second<10){
            c.drawText("Timer: "+minute +":0"+second,1500,h*0.6f,scorePaint);
        }
        else {
            c.drawText("Timer: " + minute + ":" + second, 1500, h * 0.6f, scorePaint);
        }


    }

    /**
     * launch depth charges and missiles based on the user's taps.
     * @param m an object encapsulating the (x,y) location of the user's tap
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent m) {
        if (m.getAction() == MotionEvent.ACTION_DOWN) {
            float x = m.getX();
            float y = m.getY();
            //did the user tap the bottom half of the screen? Depth Charge!

            if (y > h/2) {
                if(Prefs.rapidDepthCharge(getContext())==true || bombs.size()==0 ){
                    var dc = new DepthCharge(getResources(), w);
                    dc.setCentroid(w/2, h/2);
                    bombs.add(dc);
                    tim.subscribe(dc);
                    Context c = getContext();
                    MediaPlayer media = MediaPlayer.create(c,R.raw.depth_cahrge);
                    media.start();
                }
            } else {
                if(Prefs.rapidMissile(getContext())==true || missiles.size()==0){
                //did the user tap the top half of the screen? missile!
                    Missile miss = null;
                    if (x < w/2) {
                        miss = new Missile(Direction.LEFT_FACING, w, missilePaint);
                        miss.setBottomRight(battleship.getLeftCannonPosition());
                        leftPop = true;
                        Context c = getContext();
                        MediaPlayer media = MediaPlayer.create(c,R.raw.missile);
                        media.start();
                    } else {
                        miss = new Missile(Direction.RIGHT_FACING, w, missilePaint);
                        miss.setBottomLeft(battleship.getRightCannonPosition());
                        rightPop = true;
                        Context c = getContext();
                        MediaPlayer media = MediaPlayer.create(c,R.raw.missile);
                        media.start();
                    }
                    missiles.add(miss);
                    tim.subscribe(miss);
                }


            }

            //clean up depth charges that go off-screen
            List<Sprite> doomed = bombs.stream().filter(dc -> dc.getTop() > getHeight()).collect(Collectors.toList());
            doomed.forEach(tim::unsubscribe);
            bombs.removeAll(doomed);

            //clean up missiles that go off-screen
            doomed = missiles.stream().filter(miss -> miss.getBottom() < 0).collect(Collectors.toList());
            doomed.forEach(tim::unsubscribe);
            missiles.removeAll(doomed);

        }
        return true;
    }

    private void detectCollisions() {
        for (Submarine s : subs) {
            for (DepthCharge d : bombs) {
                if (d.overlaps(s)) {
                    s.explode();
                    score += s.getPointValue();
                    //hide the depth charge off-screen; it will get cleaned
                    //up at the next touch event.
                    d.setLocation(0,getHeight());
                    Context c = getContext();
                    MediaPlayer media = MediaPlayer.create(c,R.raw.sub_explode);
                    media.start();
                }
            }
        }

        for (Airplane p : planes) {
            for (Missile m : missiles) {
                if (p.overlaps(m)) {
                    p.explode();
                    score += p.getPointValue();
                    //hide the missile charge off-screen; it will get cleaned
                    //up at the next touch event.
                    m.setLocation(0,-getHeight());
                    Context c = getContext();
                    MediaPlayer media = MediaPlayer.create(c,R.raw.plane_explode);
                    media.start();
                }
            }
        }
    }
    public void reader(){
        try(Scanner s = new Scanner(getContext().openFileInput("score.txt"))){
            highscore = s.nextInt();

        } catch (FileNotFoundException e) {
            highscore=0;
        }
    }
    @Override
    public void tick() {
        timeNow = System.currentTimeMillis();

        if(timeNow -timeBefore>1){
            timeLeft--;
            minute = timeLeft/60;
            second = timeLeft%60;
            timeBefore=timeNow;
        }
        if(timeLeft==0){
            tim.stop();
            if(score<highscore) {
                AlertDialog.Builder ab = new AlertDialog.Builder(getContext());
                ab.setTitle("GameOver")
                        .setMessage("Do you want to play again? ")
                        .setCancelable(false)
                        .setPositiveButton("Play Again!", (d, i) -> restart())
                        .setNegativeButton("Quit.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                try (var fos = getContext().openFileOutput("score.txt", Context.MODE_PRIVATE)){

                                    fos.write((""+highscore).getBytes());

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                ((Activity) getContext()).finish();
                            }
                        });
                var box = ab.create();
                box.show();
            }
            else{
                AlertDialog.Builder ab = new AlertDialog.Builder(getContext());
                ab.setTitle("GameOver")
                        .setMessage("Congrats! You got Hight score! Do you want to play again? ")
                        .setCancelable(false)
                        .setPositiveButton("Play Again!", (d, i) -> restart())
                        .setNegativeButton("Quit.", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                try {
                                    var fos = getContext().openFileOutput("score.txt", Context.MODE_PRIVATE);
                                    fos.write((""+highscore).getBytes());
                                    fos.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                ((Activity) getContext()).finish();
                            }
                        });
                var box = ab.create();
                box.show();
                highscore=score;
            }
        }

        invalidate();
        detectCollisions();
    }

    public void restart(){
        score = 0;
        timeLeft = 180;
        tim.resume();
    }

    public void pauseGame(){
        tim.pause();
    }
    public void resumeGame(){
        if(tim!=null){
        tim.resume();
    }}

}


