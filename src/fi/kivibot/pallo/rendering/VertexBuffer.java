/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.kivibot.pallo.rendering;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 *
 * @author kivi
 */
public class VertexBuffer extends GLObject implements Cloneable {

    public static enum Target {

        Vertex, Index, Uniform
    }

    public static enum Usage {

        Static, Dynamic, Stream
    }

    public static enum Type {

        Float, Integer
    }

    private Buffer data;
    private boolean sizeChanged;
    private boolean dataChanged;
    private Usage usage;
    private Type type;
    private Target target;

    public VertexBuffer(Type t, Usage u) {
        this(t, u, Target.Vertex);
    }

    public VertexBuffer(Type t, Usage u, Target ta) {
        this.usage = u;
        this.type = t;
        this.target = ta;
    }

    public boolean hasSizeChanged() {
        return this.sizeChanged;
    }

    public boolean hasDataChanged() {
        return this.dataChanged;
    }

    public boolean updateNeeded() {
        return this.hasSizeChanged() || this.hasDataChanged();
    }

    public void forceUpload() {
        this.sizeChanged = true;
    }

    public void clearUpdateNeeded() {
        this.dataChanged = false;
        this.sizeChanged = false;
    }

    public void setData(Buffer data) {
        if (this.data == null || data.capacity() != this.data.capacity()) {
            this.sizeChanged = true;
        } else {
            this.dataChanged = true;
        }
        this.data = data;
    }

    public Buffer getData() {
        return this.data;
    }

    public void setUsageHint(Usage usage) {
        if (this.usage != usage) {
            this.sizeChanged = true;
        }
        this.usage = usage;
    }

    public Usage getUsageHint() {
        return usage;
    }

    public Type getType() {
        return this.type;
    }

    public Target getTarget() {
        return target;
    }

    @Override
    public VertexBuffer clone() {
        VertexBuffer vb = new VertexBuffer(type, usage, target);
        vb.setData(data);
        return vb;
    }

    @Override
    public String toString() {
        String dat = new String();
        String typ = null;
        switch (type) {
            case Float:
                typ = "Float";
                for (int i = 0; i < data.capacity(); i++) {
                    dat += ((FloatBuffer) data).get(i) + ", ";
                }
                break;
            case Integer:
                typ = "Integer";
                for (int i = 0; i < data.capacity(); i++) {
                    dat += ((IntBuffer) data).get(i) + ", ";
                }
                break;
        }
        return "VBO{ id: " + this.getID() + ", type: " + typ + ", data: [" + dat + "] }";
    }

}
