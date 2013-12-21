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
public class FBO {

    private int id;

    public FBO(int id) {
        this.id = id;
    }

    public int getID() {
        return id;
    }

    public void bind() {
    }
}
