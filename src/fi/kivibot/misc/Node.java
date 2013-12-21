/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.kivibot.misc;

import fi.kivibot.math.Transform;
import fi.kivibot.math.Vector2;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author kivi
 */
public class Node {
    
    private List<Node> c = new ArrayList<>();
    private Node p;
    private Transform t;
    
    public Node(){
        this(new Transform(Vector2.ZERO));
    }
    
    public Node(Transform tr){
        t = tr;
    }
    
    public List<Node> getChildren(){
        return c;
    }
    
    public boolean addChild(Node n){
        if(n.getParent() != null){
            return false;
        }
        n.setParent(this);
        return c.add(n);
    }
    
    public boolean removeChild(Node n){
        if(n.getParent() != this){
            return false;
        }
        n.setParent(null);
        return c.remove(n);
    }
    
    public void removeAllChildren(){
        for(Node n : c){
            removeChild(n);
        }
    }
    
    public Node getParent(){
        return p;
    }
    
    public void setParent(Node n){
        p = n;
    }
    
    
    public void setTransform(Transform m) {
        t = m;
    }

    public Transform getTransform() {
        return t;
    }
    
}
