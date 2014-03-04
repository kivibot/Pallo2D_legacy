/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.kivibot.pallo.scene;

import fi.kivibot.pallo.scene.Node;
import fi.kivibot.pallo.scene.Spatial;

/**
 *
 * @author kivi
 */
public class Camera extends Spatial {

    private final int screen_width, screen_height;
    private final float ratio;

    public Camera(int sw, int sh) {
        screen_width = sw;
        screen_height = sh;
        ratio = (float) sw / (float) sh;
        this.setScale(1, ratio);
        this.setViewSize(sw, sh);
    }

    public final void setViewSize(int w, int h) {
        this.setScale((float) 2f / (float) w, 2f / (float) h);
    }

}
