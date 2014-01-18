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

    public Camera(int w, int h) {
        float a = (float) w / (float) h;
        this.getTransform().scale(1, a);
    }

}
