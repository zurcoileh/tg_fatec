package com.easy_ride.app.controller;

/**
 * Created by Helio on 5/1/2016.
 */
import android.app.Activity;
import android.os.Handler;
import android.os.HandlerThread;

public class ERController<M> {
    private M model;
    private Activity activity;
    private Handler handler;
    private HandlerThread handlerThread;

    public ERController(M model, Activity activity)   {
        this.activity = activity;
        this.model = model;
        this.handlerThread = new HandlerThread(getClass().getSimpleName() +  " Thread");
        this.handlerThread.start();
        this.handler = new Handler(handlerThread.getLooper());
    }

    public void dispose() {
        handlerThread.getLooper().quit();
    }

    public Activity getActivity() {
        return activity;
    }

    public M getModel() {
        return model;
    }

    // handle event
    public void handle(final Message message, final Object... data) {
        handler.post(new Runnable() {
            @SuppressWarnings("unchecked")
            public void run() {
                message.getTask().run(ERController.this, data);
            }
        });
    }

    protected interface Message<T> {
        MessageTask getTask();
    }

    protected interface MessageTask<T> {
        void run(T sender, Object data[]);
    }
}

