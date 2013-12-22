/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.kivibot.pallo.render;

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
public class Mesh {

    private FloatBuffer vertices, texcoords;
    private IntBuffer indices;
    
    private int vid, iid, tcid, vaoid;

    public Mesh(float[] v_, int[] ind, float[] tc) {

        float[] v = new float[(int) (v_.length * 1.5)];
        int j = 0;
        for (int i = 0; i < v_.length; i += 2) {
            v[j++] = v_[i];
            v[j++] = v_[i+1];
            v[j++] = 0;
        }

        vertices = BufferUtils.createFloatBuffer(v.length);
        indices = BufferUtils.createIntBuffer(ind.length);
        texcoords = BufferUtils.createFloatBuffer(tc.length);

        vertices.put(v);
        vertices.flip();
        indices.put(ind);
        indices.flip();
        texcoords.put(tc);
        texcoords.flip();
        
        vaoid = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoid);
        
        vid = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vid);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);
        
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);
        
        iid = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, iid);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, indices, GL15.GL_STATIC_DRAW);
        
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

    }

    public int getVerticeBufferID() {
        return vid;
    }

    public int getIndiceBufferID() {
        return iid;
    }

    public int getTextureCoordBufferID() {
        return tcid;
    }

    public int getVertexArrayID() {
        return vaoid;
    }

    public FloatBuffer getVerticeBuffer() {
        return vertices;
    }

    public IntBuffer getIndiceBuffer() {
        return indices;
    }

    public FloatBuffer getTexCoordsBuffer() {
        return texcoords;
    }

}
