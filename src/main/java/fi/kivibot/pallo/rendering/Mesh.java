/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.kivibot.pallo.rendering;

import fi.kivibot.math.Rect;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

/**
 *
 * @author kivi
 */
public class Mesh extends GLObject {

    private FloatBuffer ve, te;
    private IntBuffer ib;

    private Rect b;

    private Map<String, VertexBuffer> buffers = new HashMap<>();

    public Mesh() {
    }

    public Set<Entry<String, VertexBuffer>> getBuffers() {
        return this.buffers.entrySet();
    }

    public Mesh addBuffer(String name, VertexBuffer vb) {
        buffers.put(name, vb);
        return this;
    }

    public VertexBuffer getBuffer(String name) {
        return this.buffers.get(name);
    }

    public Rect getBoundingBox() {
        if (this.getBuffer("position").updateNeeded()) {
            b = this.calcBoundingBox();
        }
        return this.b;
    }

    private Rect calcBoundingBox() {
        float mix = 99999, miy = 99999, max = -99999, may = -99999;
        VertexBuffer vertices = this.getBuffer("position");
        for (int i = 0; i < vertices.getData().capacity(); i += 2) {
            float x = ((FloatBuffer) vertices.getData()).get(i);
            float y = ((FloatBuffer) vertices.getData()).get(i + 1);

            if (x < mix) {
                mix = x;
            }
            if (x > max) {
                max = x;
            }
            if (y < miy) {
                miy = y;
            }
            if (y > may) {
                may = y;
            }
        }

        return new Rect(mix, miy, max - mix, may - miy);
    }

    @Override
    public Mesh clone() {
        Mesh m = new Mesh();
        for (Entry<String, VertexBuffer> e : this.buffers.entrySet()) {
            m.addBuffer(e.getKey(), e.getValue().clone());
        }
        return m;
    }

    @Override
    public String toString() {
        return "Mesh\n\tid: " + this.getID();
    }

}
