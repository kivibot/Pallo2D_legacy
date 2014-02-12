/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Demo01;

import fi.kivibot.engine.game.GameObject;
import fi.kivibot.pallo.assets.AssetManager;
import fi.kivibot.pallo.render.Light;
import fi.kivibot.pallo.render.Spatial;
import org.lwjgl.util.vector.Vector3f;

/**
 *
 * @author kivi
 */
public class Brick extends GameObject {

    private Spatial gfx;

    @Override
    public boolean Init() {
        gfx = new Spatial(AssetManager.getMesh("brick"), AssetManager.getMaterial("brick"));
        this.addChild(gfx);
        return true;
    }

    @Override
    public boolean Update() {
        return true;
    }

}
