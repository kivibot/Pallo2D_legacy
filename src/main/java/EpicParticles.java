import fi.kivibot.engine.game.GameObject;
import fi.kivibot.pallo.rendering.light.PointLight;
import org.jbox2d.common.Vec2;
import org.jbox2d.common.Vec3;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
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
public class EpicParticles extends GameObject {

    private PointLight[] lights;
    private Vec2[] velos;
    private Vec2 lol = new Vec2(0, 0);
    private Vec2 g = new Vec2(0, -0.0005f);

    public EpicParticles(int n) {
        this.lights = new PointLight[n];
        this.velos = new Vec2[n];
        for (int i = 0; i < n; i++) {
            Vec3 v = new Vec3((float) Math.random(), (float) Math.random(), (float) Math.random());
            v.mul(1.0f / (float) Math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z));
            this.lights[i] = new PointLight(new Vector3f(v.x, v.y, v.z));
            this.lights[i].setRange(0.005f);
            this.lights[i].setRC(8);
            this.lights[i].genMesh();
            this.lights[i].setCS(false);
            this.velos[i] = new Vec2((float) Math.random() / 100f - 0.005f, (float) Math.random() / 100f - 0.005f);
            this.addChild(this.lights[i]);
        }
    }

    @Override
    public boolean Init() {
        return true;
    }

    @Override
    public boolean Update() {
        for (int i = 0; i < lights.length; i++) {
            lights[i].getTransform().translate(new Vector2f(this.velos[i].x, this.velos[i].y));
            velos[i].x *= 0.9925;
            velos[i].y *= 0.9925;
            velos[i] = velos[i].add(g);
            if (lights[i].getTransform().getLocalPosition().x < -0.5) {
                velos[i].x = Math.abs(velos[i].x) * 0.5f;
            }
            if (lights[i].getTransform().getLocalPosition().y < -0.5) {
                velos[i].y = Math.abs(velos[i].y) * 0.5f;
            }
            if (lights[i].getTransform().getLocalPosition().x > 0.5) {
                velos[i].x = -Math.abs(velos[i].x) * 0.5f;
            }
            if (lights[i].getTransform().getLocalPosition().y > 0.5) {
                velos[i].y = -Math.abs(velos[i].y) * 0.5f;
            }
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_Y)) {
            for (int i = 0; i < lights.length; i++) {
                Vec2 v = new Vec2();
                v.x -= (lights[i].getTransform().getLocalPosition().x - (Mouse.getX() / 400.f - 1.0f));
                v.y -= (lights[i].getTransform().getLocalPosition().y - (Mouse.getY() / 300.f - 1.0f));
                double f = Math.sqrt(v.x * v.x + v.y * v.y);

                velos[i] = velos[i].add(v.mul((float) (1.0 / (f * 400.0))));
            }
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_R)) {
            for (int i = 0; i < lights.length; i++) {
                Vec2 v = new Vec2();
                v.x -= (lights[i].getTransform().getLocalPosition().x - lol.x);
                v.y -= (lights[i].getTransform().getLocalPosition().y - lol.y);
                double f = Math.sqrt(v.x * v.x + v.y * v.y);

                velos[i] = velos[i].add(v.mul((float) (1.0 / (f * 400.0))));
            }
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_E)) {
            for (int i = 0; i < lights.length; i++) {
                Vec2 v = new Vec2();
                v.x -= (lights[i].getTransform().getLocalPosition().x - lol.x - 1.25f);
                v.y -= (lights[i].getTransform().getLocalPosition().y - lol.y - 1.25f);
                double f = Math.sqrt(v.x * v.x + v.y * v.y);

                velos[i] = velos[i].add(v.mul((float) (1.0 / (f * 400.0))));

                v = new Vec2();
                v.x -= (lights[i].getTransform().getLocalPosition().x - lol.x);
                v.y -= (lights[i].getTransform().getLocalPosition().y - lol.y);
                f = Math.sqrt(v.x * v.x + v.y * v.y);

                velos[i] = velos[i].add(v.mul((float) (1.0 / (f * 400.0))));

                v = new Vec2();
                v.x -= (lights[i].getTransform().getLocalPosition().x - lol.x + 1.25f);
                v.y -= (lights[i].getTransform().getLocalPosition().y - lol.y - 1.25f);
                f = Math.sqrt(v.x * v.x + v.y * v.y);

                velos[i] = velos[i].add(v.mul((float) (1.0 / (f * 400.0))));
            }
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_T)) {
            for (int i = 0; i < lights.length; i++) {
                velos[i] = new Vec2(0, 0);
            }
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_U)) {
            for (int i = 0; i < lights.length; i++) {
                velos[i] = new Vec2(0, 0);
                lights[i].getTransform().setLocalPosition(new Vector2f((float) (Math.random() / 100.0), (float) (Math.random() / 100.0)));
            }
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
            lol.y += 0.01;
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
            lol.y += 0.01;
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
            lol.y -= 0.01;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
            lol.x += 0.01333;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
            lol.x -= 0.01333;
        }
        return true;
    }

}
