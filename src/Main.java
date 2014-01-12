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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
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
    private float x = 0, y = 0;

    private int ic = 1;
    private Light l;

    @Override
    protected void Init() {
        AssetManager.addDir(new File("assets/"));
        Material ma = AssetManager.getMaterial("apina");
        ma.setSpecularColor(new Vector3f(1, 1, 1));
        System.out.println(ma);

        //Mesh me = new Mesh(new float[]{x - 0.5f, y - 0.5f, x - 0.5f, y + 0.5f, x + 0.5f, y + 0.5f, x + 0.5f, y - 0.5f}, new int[]{0, 1, 2, 0, 2, 3}, new float[]{0, 1, 0, 0, 1, 0, 1, 1});
        s = new Spatial(AssetManager.getMesh("mesh"), ma);

        rootNode.addChild(s);

        for (int i = 0; i < 20; i++) {
            l = new Light(new Vector3f((float) Math.random(), (float) Math.random(), (float) Math.random()));
            l.getTransform().getPosition().x = (float) (Math.random() * 2 - 1);
            l.getTransform().getPosition().y = (float) (Math.random() * 2 - 1);
            l.getTransform().getPosition().z = 0.5f;
            rootNode.addChild(l);
        }
        l = new Light(new Vector3f(1, 1, 1));
        l.getTransform().getPosition().z = 0.5f;
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
            l.getTransform().setPosition(new Vector3f(x, y, l.getTransform().getPosition().z));
            System.out.println(l.getTransform().getPosition().x + " " + l.getTransform().getPosition().y + " " + l.getTransform().getPosition().z);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_O)) {
            l.getTransform().getPosition().z += 0.01f;
            System.out.println(l.getTransform().getPosition().z);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_I)) {
            l.getTransform().getPosition().z -= 0.01f;
            System.out.println(l.getTransform().getPosition().z);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_K)) {
            s.getMaterial().setShininess(s.getMaterial().getShininess() - 0.5f);
            System.out.println(s.getMaterial().getShininess());
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_L)) {
            s.getMaterial().setShininess(s.getMaterial().getShininess() + 0.5f);
            System.out.println(s.getMaterial().getShininess());
        }
    }
}
