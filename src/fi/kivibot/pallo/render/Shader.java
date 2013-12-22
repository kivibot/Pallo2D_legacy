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
public class Shader {

    public static final Shader DEFAULT = new Shader("DEFAULT",0);

    private int id;
    private String name;

    public Shader(String name, int id) {
        this.id = id;
        this.name = name;
    }

    public int getID() {
        return id;
    }

    @Override
    public String toString() {
        return "Shader{ name: " + name + ", id: " + id + " }";
    }

}
