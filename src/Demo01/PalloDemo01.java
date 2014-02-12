package Demo01;

import fi.kivibot.misc.Node;
import fi.kivibot.pallo.PalloApp;
import fi.kivibot.pallo.assets.AssetManager;
import fi.kivibot.pallo.rendering.Spatial;
import fi.kivibot.pallo.rendering.light.AmbientLight;
import fi.kivibot.pallo.rendering.light.DirectionalLight;
import java.io.File;
import java.util.List;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

/**
 *
 * @author kivi
 */
public class PalloDemo01 extends PalloApp {

    private enum State {

        GAME,
    }

    private State state = State.GAME;

    private List<Brick> bricks;
    private List<Ball> balls;

    private Node ln;

    @Override
    protected void Init() {
        this.getRenderer().getMainCam().setViewSize(800, 600);
        AssetManager.addDir(new File("assets_001"));

        Spatial s = new Spatial(AssetManager.getMesh("mesh"), AssetManager.getMaterial("apina"));
        s.getTransform().setScale(800, 600);
        rootNode.addChild(s);
        this.initGame();
    }

    @Override
    protected void Update() {
        switch (state) {
            case GAME:
                break;
        }
        float x = Mouse.getX() - 400f;
        float y = Mouse.getY() - 300f;
        if (Keyboard.isKeyDown(Keyboard.KEY_N)) {
            ln.getTransform().setLocalPosition(new Vector2f(x, y));
            System.out.println(ln.getTransform().getWorldPosition());
        }
    }

    private void initGame() {
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 8; j++) {
                Brick b = new Brick();
                b.getTransform().translate(new Vector2f(-300 + i * 120, 200 - j * 40));
                rootNode.addChild(b);
            }
        }
        for (int i = 0; i < 40; i++) {
            ln = new Ball();
            rootNode.addChild(ln);
        }
        rootNode.addChild(new DirectionalLight(new Vector3f(0.3f, 0.3f, 0.3f), new Vector3f(-1, 1, -1)));
    }

}
