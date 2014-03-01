package fi.kivibot.pallo;

import fi.kivibot.engine.game.GameObject;
import fi.kivibot.misc.FPSCounter;
import fi.kivibot.misc.Node;
import fi.kivibot.pallo.assets.AssetManager;
import fi.kivibot.pallo.audio.AudioEngine;
import fi.kivibot.pallo.rendering.Renderer;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
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
    private int frame_rate = 60;
    private FPSCounter fps = new FPSCounter(frame_rate);
    private Renderer renderer;
    private AudioEngine audio;

    protected Node rootNode = new Node();

    protected Renderer getRenderer() {
        return renderer;
    }

    protected AudioEngine getAudioEngine() {
        return this.audio;
    }

    protected abstract void Init();

    protected abstract void Update();

    public void start() {
        initDisplay(800, 600);
        initOGL();
        initOAL();
        this.renderer = new Renderer(800, 600);
        Init();
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
        GL11.glDisable(GL11.GL_DEPTH_TEST);
    }

    private boolean initOAL() {
        try {
            AL.create();
        } catch (LWJGLException ex) {
            Logger.getLogger(PalloApp.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        audio = new AudioEngine();
        return true;
    }

    private boolean loop() {
        handleEvents();
        this.handleGameLogic();
        this.Update();
        handleRendering();
        handleAudio();
        Display.update();
        renderer.gc();
        Display.sync(frame_rate);
        //Display.sync(1);
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
                if (((GameObject) c).isInitialized()) {
                    ((GameObject) c).Update();
                } else {
                    ((GameObject) c).Init();
                    ((GameObject) c).setAsInitialized();
                }
            }
        }
    }

    private void handleRendering() {
        renderer.renderTree(rootNode);
    }

    private void handleAudio() {
        audio.updateTree(rootNode);
    }

    private void cleanUp() {
    }

}
