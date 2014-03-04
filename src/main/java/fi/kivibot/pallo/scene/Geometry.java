package fi.kivibot.pallo.scene;

import fi.kivibot.pallo.rendering.Material;

/**
 *
 * @author kivi
 */
public class Geometry extends Spatial{

    private Mesh mesh;
    private Material material;

    public Geometry(Mesh me, Material ma) {
        mesh = me;
        material = ma;
    }

    public void setMesh(Mesh m) {
        mesh = m;
    }

    public Mesh getMesh() {
        return mesh;
    }

    public void setMaterial(Material m) {
        material = m;
    }

    public Material getMaterial() {
        return material;
    }

}
