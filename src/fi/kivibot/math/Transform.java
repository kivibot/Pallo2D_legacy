/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.kivibot.math;

import org.lwjgl.util.vector.Vector2f;

/**
 *
 * @author kivi
 */
public class Transform {
    
    private Vector2f pos;
    
    public Transform(Vector2f p){
        pos = p;
    }
    
    public Vector2f getPosition(){
        return pos;
    }
    
    public void setPosition(Vector2f v){
        pos = v;
    }
    
}
