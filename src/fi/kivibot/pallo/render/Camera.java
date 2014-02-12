/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.kivibot.pallo.render;

import fi.kivibot.misc.Node;

/**
 *
 * @author kivi
 */
public class Camera extends Node {

    private int screen_width, screen_height;
    private float ratio;

    public Camera(int sw, int sh) {
        screen_width = sw;
        screen_height = sh;
        ratio = (float) sw / (float) sh;
        this.getTransform().setScale(1, ratio);
    }

    public void setViewSize(int w, int h) {
        this.getTransform().setScale((float) 2f / (float) w, 2f / (float) h);
    }

}
