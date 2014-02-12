/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.kivibot.pallo.rendering;

import fi.kivibot.pallo.rendering.light.Light;
import fi.kivibot.pallo.rendering.light.LightShadower;
import fi.kivibot.math.Rect;
import fi.kivibot.misc.Node;
import fi.kivibot.pallo.assets.AssetManager;
import fi.kivibot.pallo.rendering.VertexBuffer.Target;
import fi.kivibot.pallo.rendering.VertexBuffer.Type;
import fi.kivibot.pallo.rendering.VertexBuffer.Usage;
import fi.kivibot.pallo.rendering.light.DirectionalLight;
import fi.kivibot.pallo.rendering.light.PointLight;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

/**
 *
 * @author kivi
 */
public class Renderer {

    private List<VertexBuffer> vbs = new ArrayList<>();
    private List<Mesh> vaos = new ArrayList<>();
    private Rect screenBounds = new Rect(-10.75f, -10.75f, 111.5f, 111.5f);
    private int objc;
    private Spatial screen, screen2;
    private Shader pass1_point_shader, pass1_directional_shader, pass1_ambient_shader, pass2_shader;
    private VertexBuffer lightbuf = new VertexBuffer(Type.Float, Usage.Dynamic, Target.Uniform);

    private FBO pass0_fbo;

    private List<Material> matList = new ArrayList<Material>();

    private Camera main_cam;

    private LightShadower ls = new LightShadower();

    private FloatBuffer[] matbufs = new FloatBuffer[9];

    public Renderer(int w, int h) {
        AssetManager.addDir(new File("assets"));

        pass0_fbo = new FBO(w, h);

        main_cam = new Camera(w, h);

        IntBuffer pass0_trgs = BufferUtils.createIntBuffer(3);
        pass0_trgs.put(new int[]{GL30.GL_COLOR_ATTACHMENT0, GL30.GL_COLOR_ATTACHMENT1, GL30.GL_COLOR_ATTACHMENT2});
        pass0_trgs.flip();
        pass0_fbo.getTargetBuffer().setData(pass0_trgs);

        Mesh m = new Mesh(new float[]{-1, -1, -1, 1, 1, 1, 1, -1}, new int[]{0, 1, 2, 0, 2, 3}, new float[]{0, 0, 0, 1, 1, 1, 1, 0});
        Mesh m2 = new Mesh(new float[]{-1, -1, -1, 1, 1, 1, 1, -1}, new int[]{0, 1, 2, 0, 2, 3}, new float[]{0, 1, 0, 0, 1, 0, 1, 1});

        Material ma = new Material("renderer_screen", Texture.DEFAULT, AssetManager.getShader("shader0"));
        Material ma2 = new Material("renderer_screen", Texture.DEFAULT, AssetManager.getShader("light_preview"));

        this.screen = new Spatial(m, ma);
        this.screen2 = new Spatial(m, ma2);

        pass1_point_shader = AssetManager.getShader("pass1_point");
        pass1_directional_shader = AssetManager.getShader("pass1_directional");
        pass1_ambient_shader = AssetManager.getShader("pass1_ambient");

        pass2_shader = AssetManager.getShader("pass2");

        System.out.println(this.pass2_shader);

        System.out.println(this.pass1_point_shader);
        System.out.println(this.pass1_directional_shader);
        System.out.println(this.pass1_ambient_shader);

        for (int i = 0; i < matbufs.length; i++) {
            matbufs[i] = BufferUtils.createFloatBuffer(9);
        }

    }

    public void renderTree(Node n_) {

        objc = 0;

        List<Node> no = new LinkedList<>();
        List<Light> li = new LinkedList<>();
        Queue<Node> q = new LinkedList<>();
        q.add(n_);
        Node c;
        while ((c = q.poll()) != null) {
            if (c instanceof Light) {
                li.add((Light) c);
            } else {
                no.add(c);
            }
            for (Node n : c.getChildren()) {
                q.add(n);
            }
        }
        renderLists(no, li);
    }

    private void renderLists(List<Node> list, List<Light> lights) {

        GL11.glDisable(GL11.GL_BLEND);

        this.updateMesh(screen.getMesh());
        this.updateMesh(screen2.getMesh());

        this.updateFBO(pass0_fbo);

        if (!this.bindFBO(pass0_fbo)) {
            System.out.println("Could not bind fbo");
            return;
        }

        GL20.glDrawBuffers((IntBuffer) pass0_fbo.getTargetBuffer().getData());
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

        this.matList.clear();

        for (Node n : list) {
            renderNodePass0(n);
        }

        this.bindFBO(FBO.DEFAULT);

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD0)) {
            screen.getMaterial().setDiffuseMap(pass0_fbo.getTextureList().get(0));
            this.renderSpatial_old(screen);
        } else if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD1)) {
            screen.getMaterial().setDiffuseMap(pass0_fbo.getTextureList().get(1));
            this.renderSpatial_old(screen);
        } else if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD2)) {
            screen.getMaterial().setDiffuseMap(pass0_fbo.getTextureList().get(2));
            this.renderSpatial_old(screen);
        } else {

            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

            this.bindTexture(pass0_fbo.getTextureList().get(0), GL13.GL_TEXTURE0);
            this.bindTexture(pass0_fbo.getTextureList().get(1), GL13.GL_TEXTURE1);
            this.bindTexture(pass0_fbo.getTextureList().get(2), GL13.GL_TEXTURE2);

            int shader_max_materials = 100;

            FloatBuffer md = BufferUtils.createFloatBuffer(shader_max_materials * (3 + 1));

            for (Material m : this.matList) {
                md.put(new float[]{m.getSpecularColor().x, m.getSpecularColor().y,
                    m.getSpecularColor().z, m.getShininess()});
            }

            md.flip();

            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

            for (Light l : lights) {
                if (l.getCS()) {
                    ls.updateLight(l);
                }
                renderPass1(l, md);
            }

            GL11.glDisable(GL11.GL_BLEND);

            //this.updateVertexBuffer(lightbuf);
            //GL15.glBindBuffer(GL31.GL_UNIFORM_BUFFER, lightbuf.getID());
            //GL30.glBindBufferBase(GL31.GL_UNIFORM_BUFFER, 0, lightbuf.getID());
            //GL31.glUniformBlockBinding(pass1_shader.getID(), lightbuf.getID(), 1);
            //System.out.println(GL20.glGetUniformLocation(pass1_shader.getID(), "asd[1].col"));

            /*
             GL11.glEnable(GL11.GL_BLEND);
             GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

             for (Light l : lights) {
             this.renderLight(l);
             }

             GL11.glDisable(GL11.GL_BLEND);
             */
        }

    }

    private void renderNodePass0(Node n) {
        if (n instanceof Spatial) {
            renderSpatialPass0((Spatial) n);
        }
    }

    private void renderPass1(Light l, FloatBuffer md) {
        //BINDING

        Shader s = null;

        switch (l.getType()) {
            case POINT:
                s = this.pass1_point_shader;
                break;
            case DIRECTIONAL:
                s = this.pass1_directional_shader;
                break;
            case AMBIENT:
                s = this.pass1_ambient_shader;
                break;
            default:
                System.out.println("No such type");
                return;
        }

        if (!this.bindShader(s)) {
            //NOPE
            System.out.println("Could not bind shader");
            return;
        }

        this.updateMesh(l.getMesh());

        switch (l.getType()) {
            case POINT:
                FloatBuffer ld = (FloatBuffer) this.matbufs[0].clear();
                Vector3f col = l.getColor();
                Vector2f pos = l.getTransform().getWorldPosition();
                ld.put(new float[]{col.x, col.y, col.z, pos.x, pos.y, ((PointLight) l).getHeight(), col.x, col.y, col.z});
                ld.flip();
                GL20.glUniform1(GL20.glGetUniformLocation(s.getID(), "li"), ld);
                GL20.glUniform1(GL20.glGetUniformLocation(s.getID(), "mat"), md);
                FloatBuffer mat0b = (FloatBuffer) this.matbufs[0].clear();
                Matrix3f ma0 = l.getTransform().getWorldMatrix();
                ma0.m10 = 0;
                ma0.m01 = 0;
                ma0.m00 = 1; //cos 0
                ma0.m11 = 1; //cos 0
                ma0.store(mat0b);
                mat0b.flip();
                FloatBuffer matcb = (FloatBuffer) this.matbufs[1].clear();
                this.main_cam.getTransform().getWorldMatrix().store(matcb);
                matcb.flip();
                GL20.glUniformMatrix3(GL20.glGetUniformLocation(s.getID(), "mat0"), false, mat0b);
                GL20.glUniformMatrix3(GL20.glGetUniformLocation(s.getID(), "matc"), false, matcb);
                GL20.glUniform1f(GL20.glGetUniformLocation(s.getID(), "attenuationSquare"), 0.000100f);
                GL20.glUniform1f(GL20.glGetUniformLocation(s.getID(), "attenuationLinear"), 0.000000f);
                break;
            case DIRECTIONAL:
                ld = (FloatBuffer) this.matbufs[0].clear();
                col = l.getColor();
                Vector3f dir = ((DirectionalLight)l).getDirection();
                ld.put(new float[]{col.x, col.y, col.z, dir.x, dir.y, dir.z, col.x, col.y, col.z});
                ld.flip();
                GL20.glUniform1(GL20.glGetUniformLocation(s.getID(), "li"), ld);
                break;
            case AMBIENT:
                ld = (FloatBuffer) this.matbufs[0].clear();
                col = l.getColor();
                ld.put(new float[]{col.x, col.y, col.z, 0, 0, 0, 0, 0, 0});
                ld.flip();
                GL20.glUniform1(GL20.glGetUniformLocation(s.getID(), "li"), ld);
                break;
        }

        //set sampler pointers
        GL20.glUniform1i(GL20.glGetUniformLocation(s.getID(), "samp[0]"), 0);
        GL20.glUniform1i(GL20.glGetUniformLocation(s.getID(), "samp[1]"), 1);
        GL20.glUniform1i(GL20.glGetUniformLocation(s.getID(), "samp[2]"), 2);

        //RENDERING
        GL30.glBindVertexArray(l.getMesh().getID());
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, l.getMesh().getIndiceBuffer().getID());
        GL11.glDrawElements(GL11.GL_TRIANGLES, l.getMesh().getIndiceBuffer().getData().capacity(), GL11.GL_UNSIGNED_INT, 0);
        
    }

    private void renderSpatialPass0(Spatial s) {

        Material mat = s.getMaterial();
        Mesh mesh = s.getMesh();
        Shader pass0 = mat.getShader();

        //BINDING
        if (!this.bindShader(pass0)) {
            //NOPE
            System.out.println("Could not bind shader");
            return;
        }

        if (!this.bindTexture(mat.getDiffuseMap(), GL13.GL_TEXTURE0)) {
            //NOPE
            System.out.println("Could not bind texture");
            return;
        }

        if (!this.bindTexture(mat.getNormalMap(), GL13.GL_TEXTURE1)) {
            //NOPE
            System.out.println("Could not bind texture");
            return;
        }

        if (!this.bindTexture(mat.getGlossMap(), GL13.GL_TEXTURE2)) {
            //NOPE
            System.out.println("Could not bind texture");
            return;
        }

        int id = matList.indexOf(mat);

        if (id == -1) {
            this.matList.add(mat);
            id = matList.size() - 1;
        }

        this.updateMesh(mesh);

        GL20.glUniform1i(GL20.glGetUniformLocation(pass0.getID(), "matID"), id);

        FloatBuffer mat0b = (FloatBuffer) this.matbufs[0].clear();
        s.getTransform().getWorldMatrix().store(mat0b);
        mat0b.flip();
        FloatBuffer matcb = (FloatBuffer) this.matbufs[1].clear();
        this.main_cam.getTransform().getWorldMatrix().store(matcb);
        matcb.flip();

        GL20.glUniformMatrix3(GL20.glGetUniformLocation(pass0.getID(), "mat0"), false, mat0b);
        GL20.glUniformMatrix3(GL20.glGetUniformLocation(pass0.getID(), "matc"), false, matcb);

        Vector3f ta = new Vector3f(), bita = new Vector3f();

        Matrix3f.transform(s.getTransform().getWorldMatrix(), new Vector3f(1, 0, 0), ta);
        Matrix3f.transform(s.getTransform().getWorldMatrix(), new Vector3f(0, 1, 0), bita);

        float talen = (float) Math.sqrt(Math.pow(ta.x, 2) + Math.pow(ta.y, 2));
        float bitalen = (float) Math.sqrt(Math.pow(bita.x, 2) + Math.pow(bita.y, 2));

        Matrix3f matt = new Matrix3f();
        matt.m00 = ta.x / talen;
        matt.m01 = ta.y / talen;
        matt.m02 = ta.z / talen;
        matt.m10 = bita.x / bitalen;
        matt.m11 = bita.y / bitalen;
        matt.m12 = bita.z / bitalen;
        matt.m20 = 0;
        matt.m21 = 0;
        matt.m22 = 1;

        FloatBuffer mattb = (FloatBuffer) this.matbufs[2].clear();

        matt.store(mattb);

        mattb.flip();

        GL20.glUniformMatrix3(GL20.glGetUniformLocation(pass0.getID(), "matt"), false, mattb);
        GL20.glUniform1i(GL20.glGetUniformLocation(pass0.getID(), "samp[0]"), 0);
        GL20.glUniform1i(GL20.glGetUniformLocation(pass0.getID(), "samp[1]"), 1);
        GL20.glUniform1i(GL20.glGetUniformLocation(pass0.getID(), "samp[2]"), 2);

        //RENDERING
        GL30.glBindVertexArray(s.getMesh().getID());
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, s.getMesh().getIndiceBuffer().getID());
        GL11.glDrawElements(GL11.GL_TRIANGLES, s.getMesh().getIndiceBuffer().getData().capacity(), GL11.GL_UNSIGNED_INT, 0);

    }

    @Deprecated
    private void renderSpatial_old(Spatial s) {
        objc++;

        Material mat = s.getMaterial();

        this.updateMesh(s.getMesh());

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, mat.getDiffuseMap().getID());
        GL20.glUseProgram(mat.getShader().getID());

        // Bind to the VAO that has all the information about the vertices
        GL30.glBindVertexArray(s.getMesh().getID());
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

        int trg = getBufferTarget(vb.getTarget());

        if (vb.updateNeeded()) {
            GL15.glBindBuffer(trg, id);
            if (vb.hasSizeChanged()) {
                switch (vb.getType()) {
                    case Float:
                        GL15.glBufferData(trg, (FloatBuffer) vb.getData(), UsgeHintToGL(vb.getUsageHint()));
                        break;
                    case Integer:
                        GL15.glBufferData(trg, (IntBuffer) vb.getData(), UsgeHintToGL(vb.getUsageHint()));
                        break;
                }

            } else if (vb.hasDataChanged()) {
                switch (vb.getType()) {
                    case Float:
                        GL15.glBufferSubData(trg, 0, (FloatBuffer) vb.getData());
                        break;
                    case Integer:
                        GL15.glBufferSubData(trg, 0, (IntBuffer) vb.getData());
                        break;
                }

            }
            vb.clearUpdateNeeded();
        }
        return ret;
    }

    private int getBufferTarget(VertexBuffer.Target t) {
        switch (t) {
            case Vertex:
                return GL15.GL_ARRAY_BUFFER;
            case Index:
                return GL15.GL_ELEMENT_ARRAY_BUFFER;
            case Uniform:
                return GL31.GL_UNIFORM_BUFFER;
        }
        return 0;
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

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo.getID());

        if (this.updateVertexBuffer(fbo.getTargetBuffer())) {
            for (int i = fbo.getTextureList().size(); i < fbo.getTargetBuffer().getData().capacity(); i++) {
                int tid = GL11.glGenTextures();
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, tid);

                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

                GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_RGB32F,
                        (int) fbo.getDimensions().x, (int) fbo.getDimensions().y,
                        0, GL11.GL_RGBA, GL11.GL_INT, (ByteBuffer) null);

                GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, ((IntBuffer) fbo.getTargetBuffer().getData()).get(i), GL11.GL_TEXTURE_2D, tid, 0);
                fbo.getTextureList().add(new Texture(tid));
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
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

    private boolean bindShader(Shader s) {
        if (s.getID() == -1) {
            return false;
        }
        GL20.glUseProgram(s.getID());
        return true;
    }

    private boolean bindTexture(Texture t, int pos) {
        if (t.getID() == -1) {
            return false;
        }
        GL13.glActiveTexture(pos);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, t.getID());
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        return true;
    }

    private boolean bindFBO(FBO f) {
        if (f.getID() == -1) {
            return false;
        }
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, f.getID());
        return true;
    }

    public Camera getMainCam() {
        return main_cam;
    }

}
