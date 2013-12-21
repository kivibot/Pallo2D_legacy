/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.kivibot.math;

/**
 *
 * @author kivi
 */
public class Transform {
    
    private Vector2 pos;
    
    public Transform(Vector2 p){
        pos = p;
    }
    
    public Vector2 getPosition(){
        return pos;
    }
    
    public void setPosition(Vector2 v){
        pos = v;
    }
    
}
