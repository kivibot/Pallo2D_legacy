/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.kivibot.pallo.render;

import fi.kivibot.misc.Node;
import java.util.LinkedList;
import java.util.Queue;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

/**
 *
 * @author kivi
 */
public class Renderer {

    public void renderTree(Node n_) {
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
        if(n instanceof Spatial){
            renderSpatial((Spatial)n);
        }
    }

    private void renderSpatial(Spatial s) {
        Material mat = s.getMaterial();
        mat.getDiffuseMap().bind();
        mat.getShader().bind();

        // Bind to the VAO that has all the information about the vertices
        GL30.glBindVertexArray(s.getMesh().getVertexArrayID());
        GL20.glEnableVertexAttribArray(0);

        // Bind to the index VBO that has all the information about the order of the vertices
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, s.getMesh().getVerticeBufferID());

        // Draw the vertices
        GL11.glDrawElements(GL11.GL_TRIANGLES, s.getMesh().getIndiceBufferID(), GL11.GL_UNSIGNED_BYTE, 0);

        // Put everything back to default (deselect)
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);

    }

}
