package edu.byuh.cis.cs203.MiniGame.graphics;

import android.content.res.Resources;
import android.graphics.PointF;
import edu.byuh.cis.cs203.MiniGame.R;

public class Battleship extends Sprite {

    private static Battleship uniqueInstance;
    /**
     * default cosntructor
     * @param res the Android resources object
     * @param w width of the screen, for scaling purposes
     */
    public Battleship(Resources res, float w) {
        super(w);
        image = loadAndScale(res, R.drawable.battleship, 0.4);
        bounds.set(0,0,image.getWidth(),image.getHeight());
    }
    /**
     * singleton setting
     * same as constructor parameter
     */
    public static Battleship getInstance(Resources res, float w){
        if(uniqueInstance == null){
            uniqueInstance = new Battleship(res, w);
            return uniqueInstance;
        }
        return  uniqueInstance;
    }
    /**
     * Helper method for positioning the missiles
     * @return the (x,y) position of the right cannon on the battleship
     */
    public PointF getRightCannonPosition() {
        return new PointF(bounds.left + bounds.width()*(167f/183f), bounds.top+bounds.height()*0.4f);
    }

    /**
     * Helper method for positioning the missiles
     * @return the (x,y) position of the left cannon on the battleship
     */
    public PointF getLeftCannonPosition() {
        return new PointF(bounds.left + bounds.width()*(22f/183f), bounds.top+bounds.height()*0.4f);
    }

}

