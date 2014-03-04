/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.kivibot.pallo.audio;

import fi.kivibot.pallo.scene.Node;
import fi.kivibot.pallo.scene.Spatial;
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

/**
 *
 * @author kivi
 */
public class AudioListener extends Spatial {

    private FloatBuffer pos, vel, ori;
    private boolean side = false;

    public AudioListener(boolean side) {
        this.side = side;
        pos = (FloatBuffer) BufferUtils.createFloatBuffer(3).rewind();
        vel = (FloatBuffer) BufferUtils.createFloatBuffer(3).put(0).rewind();
        ori = (FloatBuffer) BufferUtils.createFloatBuffer(6).rewind();
    }

    public FloatBuffer getPositionBuffer() {
        Vector2f p = this.getWorldPosition();
        pos.clear();
        pos.put(new float[]{p.x, p.y, 0});
        pos.rewind();
        return pos;
    }

    public FloatBuffer getVelocityBuffer() {
        return (FloatBuffer) vel.rewind();
    }

    public FloatBuffer getOrientationBuffer() {
        Vector3f p = new Vector3f();
        Matrix3f m = this.getWorldMatrix();
        ori.clear();
        if (side) {
            ori.put(new float[]{0, 0, -1, 0, 1, 0});
        } else {
            Matrix3f.transform(m, new Vector3f(1, 0, 0), p);
            ori.put(new float[]{p.x, p.y, 0, 0, 0, 1});
        }
        ori.rewind();
        return ori;
    }
}
