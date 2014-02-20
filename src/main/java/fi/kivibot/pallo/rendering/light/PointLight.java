/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.kivibot.pallo.rendering.light;

import fi.kivibot.pallo.rendering.Mesh;
import fi.kivibot.pallo.rendering.VertexBuffer;
import fi.kivibot.util.TimeUtils;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector3f;

/**
 *
 * @author kivi
 */
public class PointLight extends Light {

    private float fol = (float) (Math.PI * 2);
    private int rays = 2560;
    private float height = 0.5f;
    private float range = 01.7f;

    public PointLight(Vector3f c) {
        super(c, Type.POINT);
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

    public void setRC(int rc) {
        rays = rc;
    }

    public int getRC() {
        return rays;
    }

    public void setRange(float r) {
        range = r;
    }

    public float getRange() {
        return range;
    }

    @Deprecated
    public void genMesh() {
        FloatBuffer fb = null;
        if (m != null && m.getBuffer("position") != null && m.getBuffer("position").getData().capacity() == 2 + this.rays * 2) {
            fb = (FloatBuffer) m.getBuffer("position").getData().clear();
        } else {
            fb = BufferUtils.createFloatBuffer(2 + this.rays * 2);
        }
        IntBuffer ib = null;
        if (m != null && m.getBuffer("index") != null && m.getBuffer("index").getData().capacity() == this.rays * 3) {
            ib = (IntBuffer) m.getBuffer("index").getData().clear();
        } else {
            ib = BufferUtils.createIntBuffer(3 * rays);
        }

        fb.put(new float[]{0, 0});
        float l = range;
        for (int i = 0; i < this.rays; i++) {
            double a = (fol / (float) (this.rays - 1)) * i;
            float x = (float) (l * Math.cos(a));
            float y = (float) (l * Math.sin(a));
            fb.put(new float[]{x, y});

            if (i != this.rays - 1) {
                ib.put(0);
                ib.put(i + 1);
                ib.put(i + 2);
            }
        }
        fb.flip();
        ib.flip();
        if (this.m == null) {
            VertexBuffer vb = new VertexBuffer(VertexBuffer.Type.Float, VertexBuffer.Usage.Dynamic);
            VertexBuffer vib = new VertexBuffer(VertexBuffer.Type.Integer, VertexBuffer.Usage.Dynamic);
            vb.setData(fb);
            vib.setData(ib);

            this.m = new Mesh().addBuffer("position", vb).addBuffer("index", vib);
        } else {
            VertexBuffer vb = m.getBuffer("position");
            VertexBuffer vib = m.getBuffer("index");
            vb.setData(fb);
            vib.setData(ib);
        }

    }

}
