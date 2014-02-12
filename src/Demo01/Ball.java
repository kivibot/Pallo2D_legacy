package Demo01;

import fi.kivibot.engine.game.GameObject;
import fi.kivibot.pallo.assets.AssetManager;
import fi.kivibot.pallo.render.Light;
import fi.kivibot.pallo.render.Spatial;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

/**
 *
 * @author kivi
 */
public class Ball extends GameObject {

    private Spatial gfx;
    private Light li;

    private Vector2f velo = new Vector2f((float) Math.random() * 10, (float) Math.random() * 10);

    @Override
    public boolean Init() {
        gfx = new Spatial(AssetManager.getMesh("ball"), AssetManager.getMaterial("ball"));
        gfx.getTransform().setScale(10, 10);
        this.addChild(gfx);
        li = new Light(new Vector3f(0f, 1f, 1f));
        li.setCS(true);
        li.setRange(800f);
        li.genMesh();
        li.setRC(400);
        li.setHeight(120);
        this.addChild(li);
        return true;
    }

    @Override
    public boolean Update() {
        velo.x += (float) (Math.random() - 0.5) * Math.random() * 0.1;
        velo.y += (float) (Math.random() - 0.5) * Math.random() * 0.1;
        this.getTransform().translate(velo);

        if (this.getTransform().getLocalPosition().x < -400) {
            velo.x = Math.abs(velo.x);
        }
        if (this.getTransform().getLocalPosition().y < -300) {
            velo.y = Math.abs(velo.y);
        }
        if (this.getTransform().getLocalPosition().x > 400) {
            velo.x = -Math.abs(velo.x);
        }
        if (this.getTransform().getLocalPosition().y > 300) {
            velo.y = -Math.abs(velo.y);
        }
        return true;
    }

}
