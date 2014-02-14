/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.kivibot.pallo.rendering;

import fi.kivibot.engine.game.GameObject;
import fi.kivibot.pallo.rendering.light.PointLight;
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

    private class Particle {

        public PointLight pl;
        public int age;
        public Vector2f velo;

        public Particle(PointLight a, Vector2f v) {
            pl = a;
            velo = v;
        }
    }

    private int max_count;
    private int max_age;
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

    public ParticleEmitter() {
        this(300, 700, 300, 3, 15, new Vector2f(0, 7), (float) Math.PI / 2, (float) -Math.PI / 3 * 2, 0, 1.0f, new Vector3f(1f, 0.5f, 0.1f), new Vector3f(0.2f, -0.0f, 0f));
    }

    public ParticleEmitter(int mc, int ma, float es, float ss, float se, Vector2f g, float a, float f, float mis, float mas, Vector3f cs, Vector3f ce) {
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

    public int getMaximumAge() {
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

    public void setEmittingPoint(Vector2f v){
        this.ep = v;
    }
    
    public Vector2f getEmittingPoint(){
        return this.ep;
    }
    
    private long last_time;
    private float toadd;

    @Override
    public boolean Init() {
        last_time = TimeUtils.getTime();
        return true;
    }

    @Override
    public boolean Update() {
        for (int i = 0; i < this.max_count - (buffer.size() + particles.size()); i++) {
            Particle p = new Particle(new PointLight(this.color_s), new Vector2f());
            p.pl.setCS(false);
            p.pl.setRC(129);
            p.pl.setRange(1);
            p.pl.genMesh();
            buffer.add(p);
        }

        long cur_time = TimeUtils.getTime();
        int passed = (int) (cur_time - last_time);
        float step = passed / 1000f;
        this.last_time = cur_time;
        List<Particle> tbr = new LinkedList<>();
        for (Particle p : particles) {
            p.age += passed;
            if (p.age > this.max_age) {
                tbr.add(p);
            } else {
                p.pl.getTransform().translate(p.velo);
                float r = p.age / (float) this.max_age * (this.size_e - this.size_s) + this.size_s;
                
                
                if(r > 50){
                    p.pl.setCS(true);
                    p.pl.setRange(r);
                    p.pl.getTransform().setScale(1, 1);
                }else{
                    if(p.pl.getRange() != 1){
                        p.pl.setRange(1);
                        p.pl.genMesh();
                    }
                    p.pl.getTransform().setScale(r, r);
                }
                
                float cr = (this.color_e.x * (float) p.age + this.color_s.x * (float) (this.max_age - p.age)) / (float) this.max_age;
                float cg = (this.color_e.y * (float) p.age + this.color_s.y * (float) (this.max_age - p.age)) / (float) this.max_age;
                float cb = (this.color_e.z * (float) p.age + this.color_s.z * (float) (this.max_age - p.age)) / (float) this.max_age;
                p.pl.setColor(new Vector3f(cr, cg, cb));
                p.velo.x += gravity.x * step;
                p.velo.y += gravity.y * step;

            }
        }
        for (Particle p : tbr) {
            this.removeChild(p.pl);
            particles.remove(p);
            buffer.add(p);
        }
        if (particles.size() >= this.max_count) {
            this.toadd = 0;
        } else {
            toadd += this.emit_speed * step;
            for (int i = 0; i < Math.floor(toadd) && !buffer.isEmpty(); i++) {
                toadd--;
                Particle p = buffer.poll();
                p.pl.getTransform().setScale(1, 1);
                p.pl.genMesh();
                p.pl.setHeight(this.height);
                p.pl.getTransform().setLocalPosition(ep);
                p.age = 0;
                float vel = (float) ((this.max_speed - this.min_speed) * Math.random() + this.min_speed);
                float a = this.angle + (float) (Math.random() - 0.5) * this.fos;
                p.velo.x = (float) (Math.cos(a) * vel);
                p.velo.y = (float) (Math.sin(a) * vel);
                this.addChild(p.pl);
                particles.add(p);
            }
        }
        return true;
    }

}
