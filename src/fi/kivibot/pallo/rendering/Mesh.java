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

    private VertexBuffer vertices, texcoords, indices;

    private Rect b;

    public Mesh(VertexBuffer v, VertexBuffer t, VertexBuffer i) {
        vertices = v;
        texcoords = t;
        indices = i;
    }

    public Mesh(float[] v, int[] ind, float[] tc) {

        ve = BufferUtils.createFloatBuffer(v.length);
        ib = BufferUtils.createIntBuffer(ind.length);
        te = BufferUtils.createFloatBuffer(tc.length);

        ve.put(v);
        ve.flip();
        ib.put(ind);
        ib.flip();
        te.put(tc);
        te.flip();

        vertices = new VertexBuffer(VertexBuffer.Type.Float, VertexBuffer.Usage.Dynamic);
        vertices.setData(ve);

        texcoords = new VertexBuffer(VertexBuffer.Type.Float, VertexBuffer.Usage.Dynamic);
        texcoords.setData(te);

        indices = new VertexBuffer(VertexBuffer.Type.Integer, VertexBuffer.Usage.Dynamic);
        indices.setData(ib);
    }

    public VertexBuffer getVerticeBuffer() {
        return vertices;
    }

    public VertexBuffer getIndiceBuffer() {
        return indices;
    }

    public VertexBuffer getTexCoordsBuffer() {
        return texcoords;
    }

    public Rect getBoundingBox() {
        if (this.getVerticeBuffer().updateNeeded()) {
            b = this.calcBoundingBox();
        }
        return this.b;
    }

    private Rect calcBoundingBox() {
        float mix = 99999, miy = 99999, max = -99999, may = -99999;

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
        return new Mesh(vertices.clone(), texcoords.clone(), indices.clone());
    }

    @Override
    public String toString() {
        return "Mesh\n\tid: " + this.getID()
                + "\n\t" + "Vertices: " + vertices
                + "\n\tTexcoords: " + texcoords
                + "\n\tIndices: " + indices;
    }

}
