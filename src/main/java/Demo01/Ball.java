package Demo01;

import fi.kivibot.engine.game.GameObject;
import fi.kivibot.pallo.assets.AssetManager;
import fi.kivibot.pallo.rendering.ParticleEmitter;
import fi.kivibot.pallo.rendering.light.Light;
import fi.kivibot.pallo.rendering.Spatial;
import fi.kivibot.pallo.rendering.light.PointLight;
import org.jbox2d.common.Vec2;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

/**
 *
 * @author kivi
 */
public class Ball extends GameObject {

    private Spatial gfx;
    private PointLight li;

    private ParticleEmitter fire, fire2;

    private Vector2f velo = new Vector2f((float) Math.random() * 20 - 10, (float) Math.random() * 20 - 10);

    @Override
    public boolean Init() {
        gfx = new Spatial(AssetManager.getMesh("ball"), AssetManager.getMaterial("ball"));
        gfx.getTransform().setScale(10, 10);
        this.addChild(gfx);
        li = new PointLight(new Vector3f(1f, 0.6f, 0.1f));
        li.setCS(true);
        li.setRange(800f);
        li.genMesh();
        li.setRC(400);
        li.setHeight(120);
        this.addChild(li);
        fire = new ParticleEmitter();
        this.addChild(fire);
        fire2 = new ParticleEmitter();
        this.addChild(fire2);
        fire2.setMaximumAmount(10);
        fire2.setEmittingSpeed(10);
        fire2.setStartSize(0);
        fire2.setEndSize(400);
        fire2.setStartColor(new Vector3f(0.2f, 0.1f, 0f));
        fire2.setEndColor(new Vector3f(0.1f, 0f, 0f));
        return true;
    }

    @Override
    public boolean Update() {

        velo.x *= 0.998;
        velo.y *= 0.998;
        
        if (Keyboard.isKeyDown(Keyboard.KEY_K)) {
            Vector2f v = new Vector2f();
            v.x -= (getTransform().getLocalPosition().x - (Mouse.getX() - 400.0f));
            v.y -= (getTransform().getLocalPosition().y - (Mouse.getY() - 300.0f));
            double f = 1.0 / ( 2 * Math.sqrt(v.x * v.x + v.y * v.y));

            velo.x += v.x * f;
            velo.y += v.y * f;
        }
        this.getTransform().translate(velo);

        fire.setEmittingPoint((Vector2f) this.getTransform().getWorldPosition());
        fire.getTransform().setLocalPosition((Vector2f) this.getTransform().getWorldPosition().negate());
        fire2.setEmittingPoint((Vector2f) this.getTransform().getWorldPosition());
        fire2.getTransform().setLocalPosition((Vector2f) this.getTransform().getWorldPosition().negate());

        if (this.getTransform().getLocalPosition().x < -400) {
            velo.x = Math.abs(velo.x);
            this.getTransform().translate(new Vector2f(-400 - this.getTransform().getLocalPosition().x, 0));
        }
        if (this.getTransform().getLocalPosition().y < -300) {
            velo.y = Math.abs(velo.y);
            this.getTransform().translate(new Vector2f(0, -300 - this.getTransform().getLocalPosition().y));
        }
        if (this.getTransform().getLocalPosition().x > 400) {
            velo.x = -Math.abs(velo.x);
            this.getTransform().translate(new Vector2f(400 - this.getTransform().getLocalPosition().x, 0));
        }
        if (this.getTransform().getLocalPosition().y > 300) {
            velo.y = -Math.abs(velo.y);
            this.getTransform().translate(new Vector2f(0, 300 - this.getTransform().getLocalPosition().y));
        }
        return true;
    }

}
