/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.kivibot.pallo.audio;

import fi.kivibot.pallo.scene.Node;
import java.util.LinkedList;
import java.util.Queue;
import org.lwjgl.openal.AL10;

/**
 *
 * @author kivi
 */
public class AudioEngine {

    private AudioListener main;

    public void setActiveListener(AudioListener al) {
        main = al;
    }

    public void updateTree(Node n, float factor) {
        Queue<Node> q = new LinkedList<>();
        q.add(n);
        if (main != null) {
            AL10.alListener(AL10.AL_POSITION, main.getPositionBuffer());
            AL10.alListener(AL10.AL_VELOCITY, main.getVelocityBuffer());
            AL10.alListener(AL10.AL_ORIENTATION, main.getOrientationBuffer());
        }
        while (!q.isEmpty()) {
            Node cur = q.poll();
            for (Node no : cur.getChildren()) {
                q.add(no);
            }
            for (AudioSource as : cur.getAudioSources()) {
                this.handleSource(as, factor);
            }
        }
    }

    private void handleSource(AudioSource as, float factor) {
        AL10.alSource(as.getSourceID(), AL10.AL_POSITION, as.getPositionBuffer());
        AL10.alSource(as.getSourceID(), AL10.AL_VELOCITY, as.getVelocityBuffer());
        AL10.alSourcef(as.getSourceID(), AL10.AL_PITCH, factor);
        if (as.isStartRequested()) {
            AL10.alSourcePlay(as.getSourceID());
        } else if (as.isStopRequested()) {
            AL10.alSourceStop(as.getSourceID());
        }
        as.clearRequests();
    }

}
