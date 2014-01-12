/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.kivibot.pallo;

import fi.kivibot.engine.game.GameObject;
import fi.kivibot.misc.FPSCounter;
import fi.kivibot.misc.Node;
import fi.kivibot.pallo.assets.AssetManager;
import fi.kivibot.pallo.render.Renderer;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

/**
 *
 * @author kivi
 */
public abstract class PalloApp {

    private boolean running = true;
    private int frame_rate = 120;
    private FPSCounter fps = new FPSCounter(frame_rate / 2);
    private Renderer renderer;

    protected Node rootNode = new Node();

    protected abstract void Init();

    protected abstract void Update();

    public void start() {
        initDisplay(800, 600);
        initOGL();
        Init();
        this.renderer = new Renderer(800, 600);
        do {
            fps.update();
            Display.setTitle("FPS: " + (int) fps.getFrameRate() + " ("
                    + fps.getAverageTime() + "ms) " + AssetManager.status()
                    + ", " + renderer.status());
        } while (loop());
        cleanUp();
    }

    protected void exit() {
        running = false;
    }

    private boolean initDisplay(int w, int h) {
        try {
            Display.setDisplayMode(new DisplayMode(w, h));
            Display.create(new PixelFormat(), new ContextAttribs(3, 1).withForwardCompatible(true));
        } catch (LWJGLException ex) {
            Logger.getLogger(PalloApp.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    private void initOGL() {

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
    }

    private boolean loop() {
        handleEvents();
        this.handleGameLogic();
        this.Update();
        handleRendering();
        Display.update();
        Display.sync(frame_rate);
        //   Display.sync(1);
        return running;
    }

    private void handleEvents() {
        if (Display.isCloseRequested()) {
            running = false;
        }
    }

    private void handleGameLogic() {
        Queue<Node> q = new LinkedList<>();
        q.add(rootNode);
        Node c;
        while ((c = q.poll()) != null) {
            for (Node n : c.getChildren()) {
                q.add(n);
            }
            if (c instanceof GameObject) {
                ((GameObject) c).Update();
            }
        }
    }

    private void handleRendering() {
        renderer.renderTree(rootNode);
    }

    private void cleanUp() {
    }

}
