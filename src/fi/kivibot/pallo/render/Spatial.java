/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.kivibot.pallo.render;

import fi.kivibot.math.Transform;
import fi.kivibot.misc.Node;

/**
 *
 * @author kivi
 */
public class Spatial extends Node implements RenderAble {

    private Mesh mesh;
    private Material material;

    public Spatial(Mesh me, Material ma) {
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

    @Override
    public void onRender() {
        
    }

}
