/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.kivibot.pallo.render;

import fi.kivibot.math.Rect;
import fi.kivibot.misc.Node;
import fi.kivibot.pallo.render.VertexBuffer.Type;
import fi.kivibot.pallo.render.VertexBuffer.Usage;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

/**
 *
 * @author kivi
 */
public class Renderer {

    private List<VertexBuffer> vbs = new ArrayList<>();
    private List<Mesh> vaos = new ArrayList<>();
    private Rect screenBounds = new Rect(-10.75f, -10.75f, 111.5f, 111.5f);
    private int objc;

    public void renderTree(Node n_) {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

        objc = 0;

        Queue<Node> q = new LinkedList<>();
        q.add(n_);
        Node c;
        while ((c = q.poll()) != null) {
            for (Node n : c.getChildren()) {
                q.add(n);
            }
            renderNode(c);
        }
    }

    private void renderNode(Node n) {
        if (n instanceof Spatial) {
            renderSpatial((Spatial) n);
        }
    }

    private void renderSpatial(Spatial s) {
        objc++;

        Material mat = s.getMaterial();

        this.updateMesh(s.getMesh());

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, mat.getDiffuseMap().getID());
        GL20.glUseProgram(mat.getShader().getID());

        // Bind to the VAO that has all the information about the vertices
        // GL30.glBindVertexArray(s.getMesh().getID());
        // Bind to the index VBO that has all the information about the order of the vertices
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, s.getMesh().getIndiceBuffer().getID());

        // Draw the vertices
        GL11.glDrawElements(GL11.GL_TRIANGLES, s.getMesh().getIndiceBuffer().getData().capacity(), GL11.GL_UNSIGNED_INT, 0);

        // Put everything back to default (deselect)
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

        //GL30.glBindVertexArray(0);
    }

    private void updateMesh(Mesh m) {
        boolean created = false;
        if (m.getID() == -1) {
            m.setID(GL30.glGenVertexArrays());
            created = true;
            vaos.add(m);
        }

        GL30.glBindVertexArray(m.getID());

        if (created) {
            GL20.glEnableVertexAttribArray(0);
            GL20.glEnableVertexAttribArray(1);
        }

        boolean a = updateVertexBuffer(m.getVerticeBuffer());

        if (created || a) {
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, m.getVerticeBuffer().getID());
            GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, true, 0, 0);
        }

        a = updateVertexBuffer(m.getTexCoordsBuffer());

        if (created || a) {
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, m.getTexCoordsBuffer().getID());
            GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, 0);
        }

        updateVertexBuffer(m.getIndiceBuffer());

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    private boolean updateVertexBuffer(VertexBuffer vb) {
        boolean ret = false;
        if (vb.getID() == -1) {
            int id = GL15.glGenBuffers();
            vb.setID(id);
            vbs.add(vb);
            ret = true;
        }

        int id = vb.getID();

        if (vb.updateNeeded()) {
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, id);
            if (vb.hasSizeChanged()) {
                switch (vb.getType()) {
                    case Float:
                        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, (FloatBuffer) vb.getData(), UsgeHintToGL(vb.getUsageHint()));
                        break;
                    case Integer:
                        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, (IntBuffer) vb.getData(), UsgeHintToGL(vb.getUsageHint()));
                        break;
                }

            } else if (vb.hasDataChanged()) {
                switch (vb.getType()) {
                    case Float:
                        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, (FloatBuffer) vb.getData());
                        break;
                    case Integer:
                        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, (IntBuffer) vb.getData());
                        break;
                }

            }
            vb.clearUpdateNeeded();
        }
        return ret;
    }

    private int UsgeHintToGL(Usage u) {
        switch (u) {
            case Static:
                return GL15.GL_STATIC_DRAW;
            case Dynamic:
                return GL15.GL_DYNAMIC_DRAW;
            case Stream:
                return GL15.GL_STATIC_DRAW;
        }
        return -1;
    }

    public String status() {
        int vertc = 0;
        for (VertexBuffer vb : vbs) {
            vertc += vb.getData().capacity();
        }
        return vbs.size() + " VBOs [" + vertc + "], " + vaos.size() + " VAOs, " + objc + " rendered";
    }

    private void updateFBO(FBO fbo) {
        if (fbo.getID() == -1) {
            fbo.setID(GL30.glGenFramebuffers());
        }

        if (this.updateVertexBuffer(fbo.getTargetBuffer())) {
            for (int i = fbo.getTextureList().size(); i < fbo.getTargetBuffer().getData().capacity(); i++) {
                int tid = GL11.glGenTextures();

                GL11.glBindTexture(GL11.GL_TEXTURE_2D, tid);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

                GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8,
                        (int) fbo.getDimensions().x, (int) fbo.getDimensions().y,
                        0, GL11.GL_RGBA, GL11.GL_INT, (ByteBuffer) null);

                GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, getCA(i), tid, 0);

            }
        }
    }

    private int getCA(int i) {
        switch (i) {
            case 0:
                return GL30.GL_COLOR_ATTACHMENT0;
            case 1:
                return GL30.GL_COLOR_ATTACHMENT1;
            case 2:
                return GL30.GL_COLOR_ATTACHMENT2;
            case 3:
                return GL30.GL_COLOR_ATTACHMENT3;
            case 4:
                return GL30.GL_COLOR_ATTACHMENT4;
            case 5:
                return GL30.GL_COLOR_ATTACHMENT5;
            case 6:
                return GL30.GL_COLOR_ATTACHMENT6;
            case 7:
                return GL30.GL_COLOR_ATTACHMENT7;
            case 8:
                return GL30.GL_COLOR_ATTACHMENT8;
            case 9:
                return GL30.GL_COLOR_ATTACHMENT9;
            case 10:
                return GL30.GL_COLOR_ATTACHMENT10;
            case 11:
                return GL30.GL_COLOR_ATTACHMENT11;
            case 12:
                return GL30.GL_COLOR_ATTACHMENT12;
            case 13:
                return GL30.GL_COLOR_ATTACHMENT13;
            case 14:
                return GL30.GL_COLOR_ATTACHMENT14;
            case 15:
                return GL30.GL_COLOR_ATTACHMENT15;
        }
        return -1;
    }

}
