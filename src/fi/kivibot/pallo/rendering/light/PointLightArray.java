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

    private List<PointLight> pls = new ArrayList<>();
    private boolean updateNeeded = false;

    public PointLightArray(Vector3f c) {
        super(c, Type.ARRAY);
        FloatBuffer fb_p = BufferUtils.createFloatBuffer(0);
        FloatBuffer fb_c = BufferUtils.createFloatBuffer(0);
        IntBuffer ib = BufferUtils.createIntBuffer(0);
        VertexBuffer vpb = new VertexBuffer(VertexBuffer.Type.Float, VertexBuffer.Usage.Dynamic);
        VertexBuffer vcb = new VertexBuffer(VertexBuffer.Type.Float, VertexBuffer.Usage.Dynamic);
        VertexBuffer vib = new VertexBuffer(VertexBuffer.Type.Integer, VertexBuffer.Usage.Dynamic);
        vpb.setData(fb_p);
        vcb.setData(fb_c);
        vib.setData(ib);
        this.m = new Mesh().addBuffer("position", vpb).addBuffer("index", vib).addBuffer("color", vcb);
    }

    public void addLight(PointLight pl) {
        pls.add(pl);
        updateNeeded = true;
    }

    public void removeLight(PointLight pl) {
        pls.remove(pl);
        updateNeeded = true;
    }

    public void update() {
        if (!updateNeeded) {
            return;
        }
        updateNeeded = false;
        int fbs = 0, ibs = 0;
        for (PointLight pl : pls) {
            if (pl.m == null) {
                pl.genMesh();
            }
            fbs += pl.m.getBuffer("position").getData().capacity();
            ibs += pl.m.getBuffer("position").getData().capacity();
        }
        FloatBuffer fb = (FloatBuffer) m.getBuffer("position").getData();
        IntBuffer ib = (IntBuffer) m.getBuffer("index").getData();
        if (fb.capacity() < fbs) {
            fb = (FloatBuffer) fb.clear();
        } else {
            fb = BufferUtils.createFloatBuffer(fbs);
        }
        if (ib.capacity() < ibs) {
            ib = (IntBuffer) ib.clear();
        } else {
            ib = BufferUtils.createIntBuffer(ibs);
        }
        int base = 0;
        for (PointLight pl : pls) {
            FloatBuffer plfb = (FloatBuffer) pl.m.getBuffer("position").getData();
            IntBuffer plib = (IntBuffer) pl.m.getBuffer("index").getData();
            fb.put((FloatBuffer) plfb);
            for (int i = 0; i < plib.capacity(); i++) {
                ib.put(plib.get(i) + base);
            }
            base += plfb.capacity();
        }
        m.getBuffer("index").setData(ib);
        m.getBuffer("position").setData(fb);
    }

}
