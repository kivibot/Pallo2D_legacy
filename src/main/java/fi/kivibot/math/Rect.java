/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.kivibot.math;

import org.lwjgl.util.vector.Vector2f;

/**
 *
 * @author kivi
 */
public class Rect {

    private Vector2f bl, wh, tr;

    public Rect(float x, float y, float w, float h) {
        bl = new Vector2f(x, y);
        wh = new Vector2f(w, h);
        tr = new Vector2f(x + w, h + y);
    }

    public Vector2f getPosition() {
        return bl;
    }

    public Vector2f getWH() {
        return wh;
    }

    public Vector2f getTR() {
        return tr;
    }

    public boolean contains(Vector2f v) {
        if (v.x >= bl.x && v.x < tr.x) {
            if (v.y >= bl.y && v.y < tr.y) {
                return true;
            }
        }
        return false;
    }

    public boolean intersects(Rect r) {
        //IHAN HITON EPÄOPTIMAALINEN :D
        return contains(r.bl)
                || contains(r.tr)
                || contains(new Vector2f(r.bl.x, r.tr.y))
                || contains(new Vector2f(r.tr.y, r.bl.y))
                || r.contains(new Vector2f(bl))
                || r.contains(new Vector2f(tr))
                || r.contains(new Vector2f(bl.x, tr.y))
                || r.contains(new Vector2f(tr.x, bl.y));
    }

}
