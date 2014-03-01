/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.kivibot.pallo.rendering.light;

import fi.kivibot.misc.Node;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.FixtureDef;

/**
 *
 * @author kivi
 */
public class ShadowCaster extends Node {

    private Body b;
    private FixtureDef fd;

    public ShadowCaster(FixtureDef b) {
        fd = b;
    }

    

}
