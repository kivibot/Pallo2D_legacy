package fi.kivibot.pallo.rendering.light;

import fi.kivibot.pallo.rendering.VertexBuffer;
import java.nio.FloatBuffer;
import java.util.LinkedList;
import java.util.List;
import org.jbox2d.callbacks.RayCastCallback;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
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

    private List<Fixture> fixs = new LinkedList<>();

    public LightShadower() {
        w = new World(new Vec2());

        FixtureDef fd = new FixtureDef();
        PolygonShape sd = new PolygonShape();
        sd.setAsBox(25, 25);
        fd.shape = sd;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 5; j++) {

                BodyDef bd = new BodyDef();
                bd.position = new Vec2((float)Math.random() * 700f - 350f, (float)Math.random() * 500f - 250f);

                fixs.add(w.createBody(bd).createFixture(fd));
                fixs.get(fixs.size() - 1).m_userData = i + "_" + j;
            }
        }

    }

    public World getWorld() {
        return w;
    }

    public void updateShadowCaster(ShadowCaster sc) {

    }

    public void updateLight(Light l_) {
        if (l_.getType() != Light.Type.POINT) {
            return;
        }
        PointLight l = (PointLight) l_;

        FloatBuffer fb = BufferUtils.createFloatBuffer(2 * l.getRC() + 2);

        Vec2 pos = new Vec2(l.getTransform().getWorldPosition().getX(), l.getTransform().getWorldPosition().getY());
        fb.put(new float[]{0, 0});
        for (int i = 0; i < l.getRC(); i++) {
            float a = (l.getFOL() / (float) (l.getRC() - 1)) * i;
            Vec2 ret = this.calcRay(pos, new Vec2((float) Math.cos(a), (float) Math.sin(a)).mul(l.getRange()));
            Vector3f cb = new Vector3f(), s = new Vector3f(ret.x, ret.y, 0);
            Matrix3f.transform(l.getTransform().getLocalMatrix(), s, cb);
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
