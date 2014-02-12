/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.kivibot.pallo.rendering.light;

import fi.kivibot.misc.Node;
import fi.kivibot.pallo.rendering.Mesh;
import org.lwjgl.util.vector.Vector3f;

/**
 *
 * @author kivi
 */
public abstract class Light extends Node {

    public enum Type {

        DIRECTIONAL,
        POINT,
        AMBIENT
    }

    private Vector3f color;

    protected Mesh m;

    private boolean cs = true;
    private Type type;

    public Light(Vector3f c, Type t) {
        color = c;
        this.type = t;
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

    public void setCS(boolean b) {
        cs = b;
    }

    public boolean getCS() {
        return cs;
    }

    public Type getType() {
        return type;
    }

}
