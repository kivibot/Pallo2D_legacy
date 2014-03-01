/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.kivibot.pallo.rendering.light;

import fi.kivibot.pallo.rendering.Mesh;
import fi.kivibot.pallo.rendering.VertexBuffer;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector3f;

/**
 *
 * @author kivi
 */
public class PointLightArray extends Light {

    private class ArrayLight {

        public Vector3f col, pos;
        public float rad;
        public int points = 9;
        public boolean updateNeeded = true;

        public float[] pb, cb, lpd;
        public int[] ib;

        public ArrayLight(Vector3f c, Vector3f p, float r) {
            col = c;
            pos = p;
            rad = r;
        }

    }

    private List<ArrayLight> als = new ArrayList<>();
    private boolean updateNeeded = false;

    public PointLightArray() {
        super(new Vector3f(1, 1, 0), Type.ARRAY);
        FloatBuffer fb_p = BufferUtils.createFloatBuffer(0);
        FloatBuffer fb_c = BufferUtils.createFloatBuffer(0);
        FloatBuffer fb_d = BufferUtils.createFloatBuffer(0);
        IntBuffer ib = BufferUtils.createIntBuffer(0);
        VertexBuffer vpb = new VertexBuffer(VertexBuffer.Type.Position, VertexBuffer.Usage.Dynamic, VertexBuffer.Format.Float);
        VertexBuffer vcb = new VertexBuffer(VertexBuffer.Type.Color, VertexBuffer.Usage.Dynamic, VertexBuffer.Format.Float);
        VertexBuffer vlpd = new VertexBuffer(VertexBuffer.Type.Lpos, VertexBuffer.Usage.Dynamic, VertexBuffer.Format.Float);
        VertexBuffer vib = new VertexBuffer(VertexBuffer.Type.Index, VertexBuffer.Usage.Dynamic, VertexBuffer.Format.Integer);
        vpb.setData(fb_p);
        vcb.setData(fb_c);
        vcb.setVertexSize(3);
        vlpd.setData(fb_d);
        vlpd.setVertexSize(3);
        vib.setData(ib);
        this.m = new Mesh(vpb, vib, vcb, vlpd);
    }

    public void addLight(Vector3f color, Vector3f position, float len) {
        als.add(new ArrayLight(color, position, len));
        updateNeeded = true;
    }

    @Deprecated
    public void removeFirst() {
        als.remove(0);
        this.updateNeeded = true;
    }

    @Deprecated
    public void setColor(int i, Vector3f c) {
        als.get(i).col = c;
        als.get(i).updateNeeded = true;
        this.updateNeeded = true;
    }

    @Deprecated
    public void setRadius(int i, float r) {
        als.get(i).rad = r;
        als.get(i).updateNeeded = true;
        this.updateNeeded = true;
    }

    @Deprecated
    public void setPosition(int i, Vector3f p) {
        als.get(i).pos = p;
        als.get(i).updateNeeded = true;
        this.updateNeeded = true;
    }

    public void update() {
        if (!updateNeeded) {
            return;
        }
        updateNeeded = false;
        int pbs = 0, ibs = 0, cbs = 0, lpds = 0, asdf = 0;
        for (ArrayLight al : als) {
            if (al.updateNeeded) {
                this.genMesh(al, asdf);
            }
            asdf += al.points;
            pbs += al.pb.length;
            cbs += al.cb.length;
            ibs += al.ib.length;
            lpds += al.lpd.length;
        }
        FloatBuffer pb = (FloatBuffer) m.getBuffer(VertexBuffer.Type.Position).getData();
        FloatBuffer cb = (FloatBuffer) m.getBuffer(VertexBuffer.Type.Color).getData();
        FloatBuffer lpd = (FloatBuffer) m.getBuffer(VertexBuffer.Type.Lpos).getData();
        IntBuffer ib = (IntBuffer) m.getBuffer(VertexBuffer.Type.Index).getData();
        if (pb.capacity() >= pbs) {
            pb = (FloatBuffer) pb.clear();
        } else {
            pb = BufferUtils.createFloatBuffer(pbs);
        }
        if (cb.capacity() >= cbs) {
            cb = (FloatBuffer) cb.clear();
        } else {
            cb = BufferUtils.createFloatBuffer(cbs);
        }
        if (lpd.capacity() >= lpds) {
            lpd = (FloatBuffer) lpd.clear();
        } else {
            lpd = BufferUtils.createFloatBuffer(lpds);
        }
        if (ib.capacity() >= ibs) {
            ib = (IntBuffer) ib.clear();
        } else {
            ib = BufferUtils.createIntBuffer(ibs);
        }
        for (ArrayLight al : als) {
            pb.put(al.pb);
            cb.put(al.cb);
            ib.put(al.ib);
            lpd.put(al.lpd);
        }
        ib.flip();
        pb.flip();
        lpd.flip();
        cb.flip();
        m.getBuffer(VertexBuffer.Type.Index).setData(ib);
        m.getBuffer(VertexBuffer.Type.Position).setData(pb);
        m.getBuffer(VertexBuffer.Type.Lpos).setData(lpd);
        m.getBuffer(VertexBuffer.Type.Color).setData(cb);
    }

    private void genMesh(ArrayLight al, int ibase) {
        float[] cb = new float[al.points * 3];
        float[] pb = new float[al.points * 2];
        float[] lpd = new float[al.points * 3];
        int[] ib = new int[(al.points - 1) * 3];

        for (int i = 0; i < al.points; i++) {
            cb[i * 3 + 0] = al.col.x;
            cb[i * 3 + 1] = al.col.y;
            cb[i * 3 + 2] = al.col.z;
        }

        pb[0] = al.pos.x;
        pb[1] = al.pos.y;
        lpd[0] = al.pos.x;
        lpd[1] = al.pos.y;
        lpd[2] = al.pos.z;

        for (int i = 0; i < al.points - 1; i++) {
            double a = ((Math.PI * 2) / (float) (al.points - 1)) * i;
            float x = (float) (al.rad * Math.cos(a));
            float y = (float) (al.rad * Math.sin(a));
            pb[(i + 1) * 2 + 0] = x + al.pos.x;
            pb[(i + 1) * 2 + 1] = y + al.pos.y;
            ib[i * 3 + 0] = ibase;
            ib[i * 3 + 1] = ibase + i + 1;
            int t = (i + 2) % (al.points);
            ib[i * 3 + 2] = ibase + (t != 0 ? t : 1);
            lpd[(i + 1) * 3 + 0] = al.pos.x;
            lpd[(i + 1) * 3 + 1] = al.pos.y;
            lpd[(i + 1) * 3 + 2] = al.pos.z;
        }
        al.ib = ib;
        al.cb = cb;
        al.pb = pb;
        al.lpd = lpd;
    }

}
