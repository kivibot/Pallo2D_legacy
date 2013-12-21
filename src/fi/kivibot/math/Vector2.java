/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.kivibot.math;

/**
 *
 * @author kivi
 */
public class Vector2 implements Cloneable {

    public static final Vector2 ZERO = new Vector2(0,0);
    
    public float x, y;

    private Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2 add(float a, float b) {
        x += a;
        y += b;
        return this;
    }

    public Vector2 add(Vector2 v) {
        return add(v.x, v.y);
    }

    public Vector2 sub(float a, float b) {
        x -= a;
        y -= b;
        return this;
    }

    public Vector2 sub(Vector2 v) {
        return sub(v.x, v.y);
    }

    public Vector2 mult(float f) {
        x *= f;
        y *= f;
        return this;
    }

    public float lenSq() {
        return x * x + y * y;
    }

    public float len() {
        return (float) Math.sqrt(lenSq());
    }

    public void normalize() {
        mult(1.0f / len());
    }

    public Vector2 normal() {
        return new Vector2(-x, y);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new Vector2(x, y);
    }
}
