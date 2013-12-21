/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.kivibot.pallo.render;

import java.nio.FloatBuffer;

/**
 *
 * @author kivi
 */
public class Mesh {

    private FloatBuffer vertices, indices, texcoords;

    private int vid, iid, tcid, vaoid;

    public Mesh(float[] v, float[] i, float[] tc) {
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
    
    public FloatBuffer getVerticeBuffer(){
        return vertices;
    }
    
    public FloatBuffer getIndiceBuffer(){
        return indices;
    }
    
    public FloatBuffer getTexCoordsBuffer(){
        return texcoords;
    }
    
    
    public void update(){
        
    }

}
