package fi.kivibot.pallo.render;

import java.nio.FloatBuffer;
import org.jbox2d.callbacks.RayCastCallback;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.lwjgl.BufferUtils;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author kivi
 */
public class LightShadower implements RayCastCallback {

    private World w;
    private Vec2 rcep;
    
    public LightShadower() {
        w = new World(new Vec2());

        for (int i = 0; i < 10; i++) {
            FixtureDef fd = new FixtureDef();
            PolygonShape sd = new PolygonShape();
            sd.setAsBox(0.025f, 0.025f);
            fd.shape = sd;

            BodyDef bd = new BodyDef();
            bd.position = new Vec2(0.1f*i, 0.0f);
            w.createBody(bd).createFixture(fd);
        }
    }

    public World getWorld() {
        return w;
    }

    public void updateLight(Light l) {
        FloatBuffer fb = BufferUtils.createFloatBuffer(2 * l.getRC() + 2);
        
        Vec2 pos = new Vec2(l.getTransform().getWorldPosition().getX(), l.getTransform().getWorldPosition().getY());
        fb.put(new float[]{0, 0});
        for (int i = 0; i < l.getRC(); i++) {
            float a = (l.getFOL() / (float) (l.getRC() - 1)) * i;
            Vec2 ret = this.calcRay(pos, new Vec2((float) Math.cos(a), (float) Math.sin(a)).mul(l.getRange()));
            fb.put(new float[]{ret.x, ret.y});
        }
        fb.flip();
        l.getMesh().getVerticeBuffer().setData(fb);
    }

    private Vec2 calcRay(Vec2 pos, Vec2 dir) {
        rcep = null;
        w.raycast(this, pos, pos.add(dir));
        return rcep != null ? rcep.sub(pos) : dir;
    }

    @Override
    public float reportFixture(Fixture fix, Vec2 point, Vec2 normal, float frac) {
        rcep = point;
        return frac;
    }

}
