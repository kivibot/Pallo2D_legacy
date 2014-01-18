import fi.kivibot.misc.Node;
import fi.kivibot.pallo.PalloApp;
import fi.kivibot.pallo.assets.AssetManager;
import fi.kivibot.pallo.render.Light;
import fi.kivibot.pallo.render.Material;
import fi.kivibot.pallo.render.Mesh;
import fi.kivibot.pallo.render.Spatial;
import java.io.File;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author kivi
 */
public class Main extends PalloApp {

    public static void main(String[] args) {
        new Main().start();
    }

    private Spatial s;
    private Node n;
    private float x = 0, y = 0;

    private int ic = 1;
    private Light l;

    @Override
    protected void Init() {
        AssetManager.addDir(new File("assets/"));
        Material ma = AssetManager.getMaterial("apina");
        ma.setSpecularColor(new Vector3f(1, 1, 1));

        //Mesh me = new Mesh(new float[]{x - 0.5f, y - 0.5f, x - 0.5f, y + 0.5f, x + 0.5f, y + 0.5f, x + 0.5f, y - 0.5f}, new int[]{0, 1, 2, 0, 2, 3}, new float[]{0, 1, 0, 0, 1, 0, 1, 1});
        s = new Spatial(AssetManager.getMesh("mesh"), ma);

        n = new Node();
        n.addChild(s);
        s.getTransform().translate(new Vector2f(0.3f, 0));

        rootNode.addChild(n);

        for (int i = 0; i < 20; i++) {
            l = new Light(new Vector3f((float) Math.random(), (float) Math.random(), (float) Math.random()));
            l.getTransform().setLocalPosition(new Vector2f((float) (Math.random() * 2 - 1), (float) (Math.random() * 2 - 1)));
            l.setHeight(1);
            l.genMesh();
            rootNode.addChild(l);
        }
        l = new Light(new Vector3f(1, 1, 1));
        l.getTransform().setLocalPosition(new Vector2f(0, 0));
        l.genMesh();
        rootNode.addChild(l);
    }

    @Override
    protected void Update() {

        x = Mouse.getX() / 400f - 1;
        y = Mouse.getY() / 300f - 1;

        if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
            for (int ib = 0; ib < 1; ib++) {

                ic++;
                FloatBuffer fb = BufferUtils.createFloatBuffer(8 + s.getMesh().getVerticeBuffer().getData().capacity());
                FloatBuffer fb2 = (FloatBuffer) s.getMesh().getVerticeBuffer().getData();
                fb.put(fb2);
                fb.put(new float[]{x, y, x, y + .1f, x + .1f, y + .1f, x + .1f, y});
                fb.flip();
                s.getMesh().getVerticeBuffer().setData(fb);

                IntBuffer fb3 = BufferUtils.createIntBuffer(ic * 6);
                FloatBuffer fb4 = BufferUtils.createFloatBuffer(ic * 8);

                for (int i = 0; i < ic * 4; i += 4) {
                    fb3.put(new int[]{i, i + 1, i + 2, i, i + 2, i + 3});
                    fb4.put(new float[]{0, 1, 0, 0, 1, 0, 1, 1});
                }
                fb3.flip();
                fb4.flip();
                s.getMesh().getIndiceBuffer().setData(fb3);
                s.getMesh().getTexCoordsBuffer().setData(fb4);

            }
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_B)) {

            ic = 0;
            Mesh me = new Mesh(new float[]{x, y, x, y + 0.05f, x + 0.05f, y + 0.05f, x + 0.05f, y}, new int[]{0, 1, 2, 0, 2, 3}, new float[]{0, 1, 0, 0, 1, 0, 1, 1});

            s = new Spatial(me, s.getMaterial());
            rootNode.addChild(s);

            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_N)) {
            l.getTransform().setLocalPosition(new Vector2f(x, y));
            System.out.println(l.getTransform().getWorldPosition());
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_O)) {
            l.setHeight(l.getHeight() + 0.01f);
            System.out.println(l.getHeight());
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_I)) {
            l.setHeight(l.getHeight() - 0.01f);
            System.out.println(l.getHeight());
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_K)) {
            s.getMaterial().setShininess(s.getMaterial().getShininess() - 0.5f);
            System.out.println(s.getMaterial().getShininess());
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_L)) {
            s.getMaterial().setShininess(s.getMaterial().getShininess() + 0.5f);
            System.out.println(s.getMaterial().getShininess());
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_M)) {
            this.getRenderer().getMainCam().getTransform().setRotation((float) (System.currentTimeMillis() % 2000 * Math.PI / 1000f));
            //n.getTransform().setRotation((float) (System.currentTimeMillis() % 2000 * Math.PI / 1000f));
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
            s.getTransform().translate(new Vector2f(0, 0.01f));
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
            s.getTransform().translate(new Vector2f(0, -0.01f));
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD4)) {
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD5)) {
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        }
    }
}
