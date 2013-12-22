/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.kivibot.pallo.render;

/**
 *
 * @author kivi
 */
public class Texture {

    public static Texture DEFAULT = new Texture("DEFAULT",0);
    
    private static int tnamid = 0;

    private int id;
    private String name;

    public Texture(int id) {
        this("__T-" + tnamid++, id);
    }

    public Texture(String name, int id) {
        this.id = id;
        this.name = name;
    }

    public int getID() {
        return id;
    }
    
    @Override
    public String toString() {
        return "Texture{ name: " + name + ", id: " + id + " }";
    }

}
