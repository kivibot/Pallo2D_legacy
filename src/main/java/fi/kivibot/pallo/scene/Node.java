package fi.kivibot.pallo.scene;

import fi.kivibot.pallo.audio.AudioListener;
import fi.kivibot.pallo.audio.AudioSource;
import fi.kivibot.pallo.scene.light.Light;
import fi.kivibot.pallo.scene.light.ShadowCaster;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author kivi
 */
public class Node extends Spatial {

    private final List<Node> c = new LinkedList<>();
    private final List<Light> lightList = new LinkedList<>();
    private final List<ShadowCaster> shadowList = new LinkedList<>();
    private final List<Geometry> geomList = new LinkedList<>();
    private final List<AudioSource> audioSourceList = new LinkedList<>();
    private final List<AudioListener> audioListenerList = new LinkedList<>();

    public List<Node> getChildren() {
        return c;
    }

    private boolean add(Spatial s) {
        if (s.getParent() != null) {
            return false;
        }
        s.setParent(this);
        return true;
    }

    private boolean remove(Spatial s) {
        if (s.getParent() != this) {
            return false;
        }
        s.setParent(null);
        return true;
    }

    public boolean addChild(Node n) {
        return add(n) ? c.add(n) : false;
    }

    public boolean removeChild(Node n) {
        return remove(n) ? c.remove(n) : false;
    }

    public void removeAllChildren() {
        while (!c.isEmpty()) {
            removeChild(c.get(0));
        }
    }

    public boolean addLight(Light l) {
        return add(l) ? this.lightList.add(l) : false;
    }

    public boolean removeLight(Light l) {
        return remove(l) ? this.lightList.remove(l) : false;
    }

    public List<Light> getLights() {
        return this.lightList;
    }

    public void removeAllLights() {
        while (!this.lightList.isEmpty()) {
            this.removeLight(this.lightList.get(0));
        }
    }

    public boolean addShadowCaster(ShadowCaster sc) {
        return add(sc) ? this.shadowList.add(sc) : false;
    }

    public boolean removeShadowCaster(ShadowCaster sc) {
        return remove(sc) ? this.shadowList.remove(sc) : false;
    }

    public List<ShadowCaster> getShadowCasters() {
        return this.shadowList;
    }

    public void removeAllShadowCasters() {
        while (!this.shadowList.isEmpty()) {
            this.removeShadowCaster(this.shadowList.get(0));
        }
    }

    public boolean addGeometry(Geometry g) {
        return add(g) ? this.geomList.add(g) : false;
    }

    public boolean removeGeometry(Geometry g) {
        return remove(g) ? this.geomList.remove(g) : false;
    }

    public List<Geometry> getGeometries() {
        return this.geomList;
    }

    public void removeAllGeometries() {
        while (!this.geomList.isEmpty()) {
            this.removeGeometry(this.geomList.get(0));
        }
    }

    public boolean addAudioSource(AudioSource as) {
        return add(as) ? this.audioSourceList.add(as) : false;
    }

    public boolean removeAudioSource(AudioSource as) {
        return remove(as) ? this.audioSourceList.remove(as) : false;
    }

    public List<AudioSource> getAudioSources() {
        return this.audioSourceList;
    }

    public void removeAllAudioSources() {
        while (!this.audioSourceList.isEmpty()) {
            this.removeAudioSource(this.audioSourceList.get(0));
        }
    }

    public boolean addAudioListener(AudioListener as) {
        return add(as) ? this.audioListenerList.add(as) : false;
    }

    public boolean removeAudioListener(AudioListener as) {
        return remove(as) ? this.audioListenerList.remove(as) : false;
    }

    public List<AudioListener> getAudioListeners() {
        return this.audioListenerList;
    }

    public void removeAllAudioListeners() {
        while (!this.audioListenerList.isEmpty()) {
            this.removeAudioListener(this.audioListenerList.get(0));
        }
    }

}
