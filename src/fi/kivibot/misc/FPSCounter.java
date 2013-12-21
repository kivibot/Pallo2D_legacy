/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.kivibot.misc;

import org.lwjgl.Sys;

/**
 *
 * @author kivi
 */
public class FPSCounter {

    private int time, count, steps;

    private float avgtime, rate;

    public FPSCounter(int s) {
        steps = s;
        time = (int) ((Sys.getTime() * 1000) / Sys.getTimerResolution());
    }

    public void update() {
        if (count == steps) {
            time = (int) ((Sys.getTime() * 1000) / Sys.getTimerResolution()) - time;
            avgtime = (float) time / (float) (steps);
            rate = 1000.0f / avgtime;
            time = (int) ((Sys.getTime() * 1000) / Sys.getTimerResolution());
            count = 0;
        }
        count++;
    }

    public float getFrameRate() {
        return (int) (rate * 100) / 100f;
    }

    public float getAverageTime() {
        return (int) (avgtime * 100) / 100f;
    }

}
