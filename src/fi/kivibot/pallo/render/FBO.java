/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.kivibot.pallo.render;

import java.util.ArrayList;
import java.util.List;
import org.lwjgl.util.vector.Vector2f;

/**
 *
 * @author kivi
 */
public class FBO extends GLObject {

    public static final FBO DEFAULT = new FBO(0);
    
    private VertexBuffer targets = new VertexBuffer(VertexBuffer.Type.Integer, VertexBuffer.Usage.Static);
    private List<Texture> textures = new ArrayList<>();
    private Vector2f dim;
    
    private FBO(int id){
        dim = new Vector2f(0,0);
        this.setID(id);
    }

    public FBO(int w, int h) {
        dim = new Vector2f(w, h);
    }

    public List<Texture> getTextureList() {
        return textures;
    }

    public VertexBuffer getTargetBuffer() {
        return targets;
    }

    public Vector2f getDimensions() {
        return dim;
    }

}
