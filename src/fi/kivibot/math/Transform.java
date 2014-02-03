/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.kivibot.math;

import org.lwjgl.util.vector.Matrix2f;
import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

/**
 *
 * @author kivi
 */
public class Transform {

    private Vector2f pos, sca = new Vector2f(1, 1);
    private float rot = 0;

    private Matrix3f mat = new Matrix3f();

    private Transform parent;

    
    public Transform(Vector2f p) {
        pos = p;
        mat.m22 = 1;
        mat.m00 = 1; //cos 0
        mat.m11 = 1; //cos 0
    }

    public Vector2f getLocalPosition() {
        return new Vector2f(pos.x, pos.y);
    }

    public void setLocalPosition(Vector2f v) {
        pos = new Vector2f(v.x, v.y);
        updateTranslationPart();
    }

    public void translate(Vector2f v) {
        Vector2f tmp = new Vector2f();
        Vector2f.add(pos, v, tmp);
        pos = tmp;
        updateTranslationPart();
    }

    private void updateTranslationPart() {
        
        //LWJGL
        mat.m20 = pos.x;
        mat.m21 = pos.y;
    }

    public Vector2f getWorldPosition() {
        Vector3f pp = new Vector3f(0, 0, 1);
        Vector3f ret = new Vector3f();
        Matrix3f m = this.getWorldMatrix();
        Matrix3f.transform(m, pp, ret);
        return new Vector2f(ret.x, ret.y);
    }

    public Matrix3f getLocalMatrix() {
        return mat;
    }

    public Matrix3f getWorldMatrix() {
        Matrix3f ret = new Matrix3f();
        if (this.parent != null) {
            Matrix3f.mul(this.getLocalMatrix(), this.parent.getWorldMatrix(), ret);
        } else {
            ret = this.getLocalMatrix();
        }
        return ret;
    }

    public void setParent(Transform t) {
        parent = t;
    }

    public void setRotation(float r) {
        rot = r;

        float cos = (float) Math.cos(r);
        float sin = (float) Math.sin(r);

        mat.m00 = cos * sca.x;
        mat.m10 = sin * sca.x;
        mat.m01 = -sin * sca.y;
        mat.m11 = cos * sca.y;
    }

    public void scale(float x, float y) {
        sca.x = x;
        sca.y = y;

        this.setRotation(this.rot);
    }

}
