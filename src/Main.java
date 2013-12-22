
import fi.kivibot.pallo.PalloApp;
import fi.kivibot.pallo.assets.AssetManager;
import fi.kivibot.pallo.render.Material;
import fi.kivibot.pallo.render.Mesh;
import fi.kivibot.pallo.render.Spatial;
import java.io.File;
import org.lwjgl.opengl.GL11;

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
        new Main().start();
    }

    @Override
    protected void Init() {
        AssetManager.addDir(new File("assets/"));
        Material ma = AssetManager.getMaterial("mat0");
        System.out.println(ma);
        
        Mesh me = new Mesh(new float[]{0,0,0,1,1,1,1,0},new int[]{0,1,2,0,2,3},new float[]{0,0,0,1,1,1,1,0});
        
        Spatial s = new Spatial(me,ma);
        
        GL11.glOrtho(1, 1, 1, 1, 1, 1);
        
        rootNode.addChild(s);
    }

    @Override
    protected void Update() {
        
    }
}
