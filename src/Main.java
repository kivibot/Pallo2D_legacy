
import fi.kivibot.pallo.PalloApp;
import fi.kivibot.pallo.assets.AssetManager;
import fi.kivibot.pallo.render.Material;
import java.io.File;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author kivi
 */
public class Main extends PalloApp{

    public static void main(String[] args) {
        AssetManager.addDir(new File("assets/"));
        Material m = AssetManager.getMaterial("mat0");
        System.out.println(m);
        new Main().start();
    }

    @Override
    protected void Init() {
        
    }

    @Override
    protected void Update() {
        
    }
}
