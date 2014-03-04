/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.kivibot.pallo.scene.light;

import fi.kivibot.pallo.scene.Node;
import fi.kivibot.pallo.scene.Spatial;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.FixtureDef;

/**
 *
 * @author kivi
 */
public class ShadowCaster extends Spatial {

    private Body b;
    private FixtureDef fd;

    public ShadowCaster(FixtureDef b) {
        fd = b;
    }
    
    public Body getBody(){
        return b;
    }

    

}
