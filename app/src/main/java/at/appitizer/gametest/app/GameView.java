package at.appitizer.gametest.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.LinkedList;

/**
 * Created by michael on 25/03/14.
 */
public class GameView extends SurfaceView {
    private static final String TAG = "GameView";
    private static final int TRAJECTORY_SIZE = 10000;
    private final Bitmap bmp;
    private SurfaceHolder holder;
    private GameLoopThread gameLoopThread;
    //private int x = 0;
    //private int y = 0;
    private Point currPosition = new Point(0, 0);
    private Paint trajectoryPaint;
    private LinkedList<Point> trajectory = new LinkedList<Point>();

    public GameView(Context context) {
        super(context);
        gameLoopThread = new GameLoopThread(this);
        holder = getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                boolean retry = true;
                gameLoopThread.setRunning(false);
                while (retry) {
                    try {
                        gameLoopThread.join();
                        retry = false;
                    }
                    catch (InterruptedException e) {
                    }
                }
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                currPosition.x = getWidth() / 2;
                currPosition.y = getHeight() / 2;
                gameLoopThread.setRunning(true);
                gameLoopThread.start();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format,
                                       int width, int height) {
            }
        });

        trajectoryPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        trajectoryPaint.setStyle(Paint.Style.STROKE);
        trajectoryPaint.setColor(Color.WHITE);

        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.god1);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.BLACK);
        int dx = (int) Math.signum(Math.random() - 0.5);
        int dy = (int) Math.signum(Math.random() - 0.5);

        // the god may walk out of sight
        currPosition.x += dx;
        currPosition.y += dy;

        if (trajectory.size() > TRAJECTORY_SIZE) {
            trajectory.removeFirst();
        }
        trajectory.addLast(new Point(currPosition.x, currPosition.y));

        Path trajectoryPath = new Path();
        Point start = trajectory.getFirst();
        trajectoryPath.moveTo(start.x, start.y);
        for (Point p: trajectory) {
            trajectoryPath .lineTo(p.x, p.y);
        }
        canvas.drawPath(trajectoryPath, trajectoryPaint);

        float left = currPosition.x - bmp.getWidth() / 2;
        float right = currPosition.y - bmp.getHeight() / 2;
        canvas.drawBitmap(bmp, left, right, null);
    }
}
