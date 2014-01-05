/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.kivibot.math;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

/**
 *
 * @author kivi
 */
public class Transform {
    
    private Vector3f pos;
    
    public Transform(Vector3f p){
        pos = p;
    }
    
    public Vector3f getPosition(){
        return pos;
    }
    
    public void setPosition(Vector3f v){
        pos = v;
    }
    
}
