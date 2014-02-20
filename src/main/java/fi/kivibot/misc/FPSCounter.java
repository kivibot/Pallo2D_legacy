/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.kivibot.misc;

import java.util.LinkedList;
import org.lwjgl.Sys;

/**
 *
 * @author kivi
 */
public class FPSCounter {

    private int time, count, steps;

    private float avgtime, rate;

    private LinkedList<Integer> times = new LinkedList<>();

    public FPSCounter(int s) {
        steps = s;
        time = (int) ((Sys.getTime() * 1000) / Sys.getTimerResolution());
        for(int i=0; i<s; i++){
            times.add(0);
        }
    }

    public void update() {
        long time2 = (int) ((Sys.getTime() * 1000) / Sys.getTimerResolution()) - time;
        times.add(Integer.valueOf((int) time2));
        time = (int) ((Sys.getTime() * 1000) / Sys.getTimerResolution());
        if (times.size() > getFrameRate()) {
            times.removeFirst();
        }
        long t = 0;
        for (Integer i : times) {
            t += i;
        }
        avgtime = (float) t / (float) (times.size());
        rate = 1000.0f / avgtime;
    }

    public float getFrameRate() {
        return (int) (rate * 100) / 100f;
    }

    public float getAverageTime() {
        return (int) (avgtime * 100) / 100f;
    }

}
