import fi.kivibot.misc.Node;
import fi.kivibot.pallo.PalloApp;
import fi.kivibot.pallo.assets.AssetManager;
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

    @Override
    protected void Init() {
        AssetManager.addDir(new File("assets/"));
        Material ma = AssetManager.getMaterial("mat0");
        System.out.println(ma);

        Mesh me = new Mesh(new float[]{x, y, x, y + 0.05f, x + 0.05f, y + 0.05f, x + 0.05f, y}, new int[]{0, 1, 2, 0, 2, 3}, new float[]{0, 1, 0, 0, 1, 0, 1, 1});

        s = new Spatial(me, ma);

        rootNode.addChild(s);
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

    }
}
