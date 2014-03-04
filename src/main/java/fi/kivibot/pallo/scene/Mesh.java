/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.kivibot.pallo.scene;

import fi.kivibot.pallo.rendering.GLObject;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author kivi
 */
public class Mesh extends GLObject implements Cloneable {

    private Map<VertexBuffer.Type, VertexBuffer> buffers = new EnumMap<>(VertexBuffer.Type.class);
    private List<VertexBuffer> bufferlist = new ArrayList<>();

    public Mesh() {
    }

    public Mesh(VertexBuffer... vbs) {
        for (VertexBuffer vb : vbs) {
            this.addBuffer(vb);
        }
    }

    public List<VertexBuffer> getBuffers() {
        return this.bufferlist;
    }

    public void addBuffer(VertexBuffer vb) {
        if(buffers.containsKey(vb.getType())){
            bufferlist.remove(buffers.get(vb.getType()));
        }
        buffers.put(vb.getType(), vb);
        bufferlist.add(vb);
    }

    public VertexBuffer getBuffer(VertexBuffer.Type t) {
        return this.buffers.get(t);
    }

    @Override
    public String toString() {
        return "Mesh\n\tid: " + this.getID();
    }

}
