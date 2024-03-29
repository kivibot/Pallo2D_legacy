package fi.kivibot.pallo.scene.light;

import fi.kivibot.pallo.scene.VertexBuffer;
import java.nio.FloatBuffer;
import java.util.LinkedList;
import java.util.List;
import org.jbox2d.callbacks.RayCastCallback;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Vector3f;

/**
 *
 * @author kivi
 */
public class LightShadower implements RayCastCallback {

    private World w;
    private Vec2 rcep;

    private List<ShadowCaster> shadowCasters = new LinkedList<>();

    public LightShadower() {
        w = new World(new Vec2());

        FixtureDef fd = new FixtureDef();
        PolygonShape sd = new PolygonShape();
        sd.setAsBox(25, 25);
        fd.shape = sd;
    }

    public World getWorld() {
        return w;
    }

    public void updateShadowCasters(List<ShadowCaster> scs) {
        this.shadowCasters = scs;
        for (ShadowCaster sc : scs) {
            if (sc.getBody() == null) {
                FixtureDef fd = new FixtureDef();
                fd.shape = sc.getShape();
                Body b = w.createBody(new BodyDef());
                b.createFixture(fd);
                sc.setBody(b);
            }
        }
    }

    public void updateLight(Light l_) {
        if (l_.getType() != Light.Type.POINT) {
            return;
        }
        PointLight l = (PointLight) l_;

        FloatBuffer fb = BufferUtils.createFloatBuffer(2 * l.getRC() + 2);

        Vec2 pos = new Vec2(l.getWorldPosition().getX(), l.getWorldPosition().getY());
        fb.put(new float[]{0, 0});
        for (int i = 0; i < l.getRC(); i++) {
            float a = (l.getFOL() / (float) (l.getRC() - 1)) * i;
            Vec2 ret = this.calcRay(pos, new Vec2((float) Math.cos(a), (float) Math.sin(a)).mul(l.getRange()));
            Vector3f cb = new Vector3f(), s = new Vector3f(ret.x, ret.y, 0);
            Matrix3f.transform(l.getLocalMatrix(), s, cb);
            fb.put(new float[]{ret.x, ret.y});
        }
        fb.flip();
        l.genMesh();
        l.getMesh().getBuffer(VertexBuffer.Type.Position).setData(fb);
    }

    private Vec2 calcRay(Vec2 pos, Vec2 dir) {
        of = 0;
        w.raycast(this, pos.add(dir), pos);
        //w.raycast(this, pos, pos.add(dir));
        return dir.mul(1 - of);
    }

    private float of;

    @Override
    public float reportFixture(Fixture fix, Vec2 point, Vec2 normal, float frac) {
        if (of < frac) {
            of = frac;
        }
        return 1;
    }

}
