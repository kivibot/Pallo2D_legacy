/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.kivibot.pallo.render;

import fi.kivibot.misc.Node;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

/**
 *
 * @author kivi
 */
public class Light extends Node {

    private Vector3f color;
    

    public Light(Vector3f c) {
        color = c;
    }

    public Vector3f getColor() {
        return color;
    }

    public void setColor(Vector3f c) {
        color = c;
    }

}
