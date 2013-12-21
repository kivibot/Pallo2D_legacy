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

    public void bind() {
    }

    @Override
    public String toString() {
        return "Texture{ name: " + name + ", id: " + id + " }";
    }

}
