/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.kivibot.engine.game;

import fi.kivibot.misc.Node;

/**
 *
 * @author kivi
 */
public abstract class GameObject extends Node {

    private boolean initialized = false;

    public boolean isInitialized() {
        return initialized;
    }

    public void setAsInitialized() {
        initialized = true;
    }

    public abstract boolean Init();

    public abstract boolean Update();

}
