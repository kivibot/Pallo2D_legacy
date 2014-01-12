/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.kivibot.pallo.render;

import fi.kivibot.misc.Node;
import org.lwjgl.util.vector.Vector3f;

/**
 *
 * @author kivi
 */
public class Light extends Node {

    private Vector3f color;

    private Mesh m;

    private float fol = (float) (Math.PI * 2);
    private int rays = 8;
    private float height = 0.5f;

    public Light(Vector3f c) {
        color = c;
    }

    public Vector3f getColor() {
        return color;
    }

    public void setColor(Vector3f c) {
        color = c;
    }

    public Mesh getMesh() {
        return m;
    }

    public void setMesh(Mesh me) {
        m = me;
    }

    public float getFOL() {
        return fol;
    }

    public void setFOL(float f) {
        fol = f;
    }

    public float getHeight() {
        return this.height;
    }

    public void setHeight(float f) {
        this.height = f;
    }

}
