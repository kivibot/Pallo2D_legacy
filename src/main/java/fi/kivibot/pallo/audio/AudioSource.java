/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.kivibot.pallo.audio;

import fi.kivibot.pallo.scene.Node;
import fi.kivibot.pallo.scene.Spatial;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import org.lwjgl.util.WaveData;
import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

/**
 *
 * @author kivi
 */
public class AudioSource extends Spatial {

    private FloatBuffer pos, vel;
    private int buffer, source;
    private boolean startRequested, stopRequested;

    public AudioSource(WaveData wd) {

        pos = (FloatBuffer) BufferUtils.createFloatBuffer(3).rewind();
        vel = (FloatBuffer) BufferUtils.createFloatBuffer(3).rewind();

        buffer = AL10.alGenBuffers();

        AL10.alBufferData(buffer, wd.format, wd.data, wd.samplerate);

        wd.dispose();

        source = AL10.alGenSources();

        AL10.alSourcei(source, AL10.AL_BUFFER, buffer);
        AL10.alSourcef(source, AL10.AL_PITCH, 1.0f);
        AL10.alSourcef(source, AL10.AL_GAIN, 1.0f);
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

    public void play() {
        this.startRequested = true;
    }

    public void stop() {
        this.stopRequested = true;
    }

    public int getSourceID() {
        return this.source;
    }

    public boolean isStartRequested() {
        return this.startRequested;
    }

    public boolean isStopRequested() {
        return this.stopRequested;
    }

    public void clearRequests() {
        this.startRequested = false;
        this.stopRequested = false;
    }
}
