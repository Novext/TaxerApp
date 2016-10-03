package com.novext.taxerapp;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.ui.IconGenerator;

import java.util.Timer;
import java.util.TimerTask;

public class Timeout {

    private int hour = 0;
    private int minute;
    private int second;
    private Timer timer;
    private boolean isTimerRunning;
    private IconGenerator iconFactory;
    private Marker marker;

    public Timeout(int _minute, int _second, Context ctx, Marker _marker) {
        timer = new Timer();
        iconFactory = new IconGenerator(ctx);
        iconFactory.setRotation(0);
        iconFactory.setStyle(IconGenerator.STYLE_BLUE);
        minute = _minute;
        second = _second;
        marker = _marker;
    }

    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            isTimerRunning = true;
            if(second > 0) {
                second--;
            } else {
                second = 59;
                if(minute > 0) minute--;
                else {
                    minute = 59;
                    if(hour > 0) hour--;
                        // si segundo = 0, minuto = 0 y hora = 0,
                        // cancelamos el timer
                    else {
                        isTimerRunning = false;
                        timer.cancel();
                        timer.purge();
                        new Handler(Looper.getMainLooper()).post(
                            new Runnable() {
                                @Override
                                public void run() {
                                    marker.remove();
                                }
                            }
                        );
                    }
                }
            }
            if(isTimerRunning){
                final String time = minute + " : " + second;

                new Handler(Looper.getMainLooper()).post(
                    new Runnable() {
                        @Override
                        public void run() {
                            marker.setIcon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(time)));
                        }
                    }
                );
            }
        }
    }; // fin timertask

    public void start(int timeout, int interval) {
        timer.schedule(task, timeout, interval);
    }

} 