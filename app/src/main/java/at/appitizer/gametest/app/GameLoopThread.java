package at.appitizer.gametest.app;

import android.graphics.Canvas;
import android.util.Log;

/**
 * Created by michael on 25/03/14.
 */
public class GameLoopThread extends Thread {
    private static final String TAG = "GameLoopThread";

    static final long FPS = 10;
    private GameView view;
    private boolean running = false;

    public GameLoopThread(GameView view) {
        this.view = view;
    }

    public void setRunning(boolean run) {
        running = run;
    }

    @Override
    public void run() {
        long ticksPS = 1000 / FPS;
        long startTime;
        long sleepTime;
        while (running) {
            Canvas c = null;
            try {
                c = view.getHolder().lockCanvas();
                startTime = System.currentTimeMillis();
                synchronized (view.getHolder()) {
                    view.onDraw(c);
                }
            }
            finally {
                if (c != null) {
                    view.getHolder().unlockCanvasAndPost(c);
                }
            }
            sleepTime = ticksPS-(System.currentTimeMillis() - startTime);
            try {
                if (sleepTime > 0)
                    sleep(sleepTime);
                else
                    sleep(10);
            }
            catch (Exception e) {
            }
        }
    }
}
