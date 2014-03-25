package at.appitizer.gametest.app;

import android.graphics.Canvas;
import android.util.Log;

/**
 * Created by michael on 25/03/14.
 */
public class GameLoopThread extends Thread {
    private static final String TAG = "GameLoopThread";
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
        Log.d(TAG, "run");
        while (running) {
            Canvas c = null;
            try {
                c = view.getHolder().lockCanvas();
                synchronized (view.getHolder()) {
                    view.onDraw(c);
                }
            }
            finally {
                if (c != null) {
                    view.getHolder().unlockCanvasAndPost(c);
                }
            }
        }
    }
}
