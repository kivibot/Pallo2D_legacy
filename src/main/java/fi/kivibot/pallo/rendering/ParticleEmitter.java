/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.kivibot.pallo.rendering;

import fi.kivibot.engine.game.GameObject;
import fi.kivibot.misc.Node;
import fi.kivibot.pallo.rendering.light.PointLight;
import fi.kivibot.pallo.rendering.light.PointLightArray;
import fi.kivibot.util.TimeUtils;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

/**
 *
 * @author kivi
 */
public class ParticleEmitter extends GameObject {

    private class Particle extends Node {

        public float age;
        public Vector2f velo;

        public Particle(Vector2f v) {
            velo = v;
        }
    }

    private int max_count;
    private float max_age;
    private float emit_speed;
    private float size_s;
    private float size_e;
    private Vector2f gravity;
    private float angle;
    private float fos;
    private float min_speed;
    private float max_speed;
    private Vector3f color_s;
    private Vector3f color_e;
    private float height = 30;

    private Vector2f ep = new Vector2f();

    private List<Particle> particles = new LinkedList<>();
    private Queue<Particle> buffer = new LinkedList<>();

    private PointLightArray pla = new PointLightArray();

    public ParticleEmitter() {
        this(300, 0.7f, 300, 10, 5, new Vector2f(0, 7), (float) Math.PI / 2, (float) -Math.PI / 3 * 2, -1.0f, 1.5f, new Vector3f(1f, 0.5f, 0.1f), new Vector3f(0.3f, -0.0f, 0f));
    }

    public ParticleEmitter(int mc, float ma, float es, float ss, float se, Vector2f g, float a, float f, float mis, float mas, Vector3f cs, Vector3f ce) {
        this.max_count = mc;
        this.max_age = ma;
        this.emit_speed = es;
        this.size_s = ss;
        this.size_e = se;
        this.gravity = g;
        this.angle = a;
        this.fos = f;
        this.min_speed = mis;
        this.max_speed = mas;
        this.color_s = cs;
        this.color_e = ce;
    }

    public void setMaximumAmount(int a) {
        this.max_count = a;
    }

    public int getMaximumAmount() {
        return this.max_count;
    }

    public void setMaximumAge(int a) {
        this.max_age = a;
    }

    public float getMaximumAge() {
        return this.max_age;
    }

    public void setEmittingSpeed(float f) {
        this.emit_speed = f;
    }

    public float getEmittingSpeed() {
        return this.emit_speed;
    }

    public void setStartSize(float f) {
        this.size_s = f;
    }

    public float getStartSize() {
        return this.size_s;
    }

    public void setEndSize(float f) {
        this.size_e = f;
    }

    public float getEndSize() {
        return this.size_e;
    }

    public void setGravity(Vector2f v) {
        this.gravity = v;
    }

    public Vector2f getGravity() {
        return this.gravity;
    }

    public void setStartingAngle(float a) {
        this.angle = a;
    }

    public float getStartingAngle() {
        return this.angle;
    }

    public void setFieldOfStarting(float f) {
        this.fos = f;
    }

    public float getFieldOfStarting() {
        return this.fos;
    }

    public void setMaxStartingSpeed(float f) {
        this.max_speed = f;
    }

    public float getMaxStartingSpeed() {
        return this.max_speed;
    }

    public void setMinStartingSpeed(float f) {
        this.min_speed = f;
    }

    public float getMinStartingSpeed() {
        return this.min_speed;
    }

    public void setStartColor(Vector3f c) {
        this.color_s = c;
    }

    public Vector3f getStartColor() {
        return this.color_s;
    }

    public void setEndColor(Vector3f c) {
        this.color_e = c;
    }

    public Vector3f getEndColor() {
        return this.color_e;
    }

    public void setEmittingPoint(Vector2f v) {
        this.ep = v;
    }

    public Vector2f getEmittingPoint() {
        return this.ep;
    }

    private float toadd;

    @Override
    public boolean Init() {
        this.addChild(pla);
        return true;
    }

    @Override
    public boolean Update(float delta) {
        if (delta > 0.02) {
            System.out.println(delta);
        }
        for (int i = 0; i < this.max_count - (buffer.size() + particles.size()); i++) {
            Particle p = new Particle(new Vector2f());
            buffer.add(p);
        }

        List<Particle> tbr = new LinkedList<>();
        int ind = -1;
        for (Particle p : particles) {
            ind++;
            p.age += delta;
            if (p.age > this.max_age) {
                tbr.add(p);
            } else {
                p.getTransform().translate(p.velo);

                Vector2f asdf = p.getTransform().getLocalPosition();

                pla.setPosition(ind, new Vector3f(asdf.x, asdf.y, this.height));

                float r = p.age / (float) this.max_age * (this.size_e - this.size_s) + this.size_s;

                pla.setRadius(ind, r);

                float cr = (this.color_e.x * (float) p.age + this.color_s.x * (float) (this.max_age - p.age)) / (float) this.max_age;
                float cg = (this.color_e.y * (float) p.age + this.color_s.y * (float) (this.max_age - p.age)) / (float) this.max_age;
                float cb = (this.color_e.z * (float) p.age + this.color_s.z * (float) (this.max_age - p.age)) / (float) this.max_age;
                pla.setColor(ind, new Vector3f(cr, cg, cb));
                p.velo.x += gravity.x * delta;
                p.velo.y += gravity.y * delta;

            }
        }
        for (Particle p : tbr) {
            pla.removeFirst();
            particles.remove(p);
            buffer.add(p);
        }
        if (particles.size() >= this.max_count) {
            this.toadd = 0;
        } else {
            toadd += this.emit_speed * delta;
            for (int i = 0; i < Math.floor(toadd) && !buffer.isEmpty(); i++) {
                toadd--;
                Particle p = buffer.poll();
                p.age = 0;
                p.getTransform().setLocalPosition(new Vector2f(ep.x, ep.y));
                float vel = (float) ((this.max_speed - this.min_speed) * Math.random() + this.min_speed);
                float a = this.angle + (float) (Math.random() - 0.5) * this.fos;
                p.velo.x = (float) (Math.cos(a) * vel);
                p.velo.y = (float) (Math.sin(a) * vel);

                pla.addLight(color_s, new Vector3f(ep.x, ep.y, this.height), this.size_s);
                particles.add(p);
            }
        }
        return true;
    }

}
