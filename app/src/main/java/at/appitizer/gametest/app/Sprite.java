package at.appitizer.gametest.app;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

import java.util.LinkedList;

/**
 * Created by michael on 25/03/14.
 *
 * spritesheet
 * 0 1 2 frames
 * 0 down animation
 * 1 left
 * 2 right
 * 3 up
 *
 * currSpeed to spritesheet mapping
 * (http://www.edu4java.com/en/androidgame/androidgame5.html)
 * 	x	 y	 atan2(x,y)	 atan2(x,y)/(PI/2)	 (atan2(x,y)/(PI/2)+2)%4	 bmp row (from 0)
 * 	up	 0	 -1	 PI or -PI	 2 or -2	 4 or 0	 3
 * 	right	 1	 0	 PI/2	 1	 3	 2
 * 	down	 0	 1	 0	 0	 2	 0
 * 	left	 -1	 0	 -PI/2	 -1	 1	 1
 */
public class Sprite {

    private static final String TAG = "Sprite";
    private GameView gameView;
    private Bitmap bmp;

    private static final int BMP_ROWS = 4;
    private static final int BMP_COLUMNS = 3;
    private int currFrame = 0;
    private int width;
    private int height;

    private Point currPosition = new Point(0, 0);
    private Point currSpeed = new Point(1, 1);
    private Paint trajectoryPaint;
    {
        trajectoryPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        trajectoryPaint.setStyle(Paint.Style.STROKE);
        trajectoryPaint.setColor(Color.WHITE);
    }
    private static final int TRAJECTORY_POSITION_SIZE = 10000;
    private LinkedList<Point> trajectoryPosition = new LinkedList<Point>();

    private int currTime = 0;

    public Sprite(GameView gameView, Bitmap bmp) {
        this.gameView=gameView;
        this.bmp=bmp;

        currPosition.x = (int) (Math.random() * (gameView.getWidth() - width));
        currPosition.y = (int) (Math.random() * (gameView.getHeight() - height));

        this.width = bmp.getWidth() / BMP_COLUMNS;
        this.height = bmp.getHeight() / BMP_ROWS;
    }

    private void update() {
        currTime++;
        if (currTime % 30 == 0) {
            currSpeed.x = (int) Math.round(Math.random()*2 - 1);
            currSpeed.y = (int) Math.round(Math.random()*2 - 1);
        }

        currPosition.x += currSpeed.x;
        currPosition.y += currSpeed.y;
        currFrame = ++currFrame % BMP_COLUMNS;

        if (trajectoryPosition.size() > TRAJECTORY_POSITION_SIZE) {
            trajectoryPosition.removeFirst();
        }
        trajectoryPosition.addLast(new Point(currPosition.x, currPosition.y));

        //Log.d(TAG, "\ncurrPosition = " + currPosition.x + ", " + currPosition.y + ", " +
        //        "\ncurrSpeed = " + currSpeed.x + ", " + currSpeed.y);
    }

    public void onDraw(Canvas canvas) {
        update();

        Path trajectoryPath = new Path();
        Point start = trajectoryPosition.getFirst();
        trajectoryPath.moveTo(start.x, start.y);
        for (Point p: trajectoryPosition) {
            trajectoryPath .lineTo(p.x, p.y);
        }
        canvas.drawPath(trajectoryPath, trajectoryPaint);

        int srcX = currFrame * width;
        int srcY = getAnimationRow() * height;
        Rect src = new Rect(srcX, srcY, srcX + width, srcY + height);
        Rect dst = new Rect(currPosition.x-width/2, currPosition.y-height/2, currPosition.x + width, currPosition.y + height);
        canvas.drawBitmap(bmp, src, dst, null);
    }

    // direction = 0 up, 1 left, 2 down, 3 right,
    // animation = 3 up, 1 left, 0 down, 2 right
    int[] DIRECTION_TO_ANIMATION_MAP = { 3, 1, 0, 2 };

    private int getAnimationRow() {
        double dirDouble = (Math.atan2(currSpeed.x, currSpeed.y) / (Math.PI / 2) + 2);
        int direction = (int) Math.round(dirDouble) % BMP_ROWS;
        return DIRECTION_TO_ANIMATION_MAP[direction];
    }
}
