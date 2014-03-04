/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.kivibot.pallo.scene.light;

import fi.kivibot.pallo.scene.Mesh;
import fi.kivibot.pallo.scene.VertexBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector3f;

/**
 *
 * @author kivi
 */
public class AmbientLight extends Light {

    public AmbientLight(Vector3f c) {
        super(c, Type.AMBIENT);

        FloatBuffer fb = BufferUtils.createFloatBuffer(8);
        IntBuffer ib = BufferUtils.createIntBuffer(6);
        fb.put(new float[]{-1, -1});
        fb.put(new float[]{-1, 1});
        fb.put(new float[]{1, 1});
        fb.put(new float[]{1, -1});
        ib.put(new int[]{0, 1, 2});
        ib.put(new int[]{0, 2, 3});

        fb.flip();
        ib.flip();

        VertexBuffer vb = new VertexBuffer(VertexBuffer.Type.Position, VertexBuffer.Usage.Dynamic, VertexBuffer.Format.Float);
        VertexBuffer vib = new VertexBuffer(VertexBuffer.Type.Index, VertexBuffer.Usage.Dynamic, VertexBuffer.Format.Integer);
        vb.setData(fb);
        vib.setData(ib);
        this.m = new Mesh(vb, vib);
    }

}
