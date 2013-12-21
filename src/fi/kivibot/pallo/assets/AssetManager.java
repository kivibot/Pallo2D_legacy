/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.kivibot.pallo.assets;

import fi.kivibot.pallo.render.Material;
import fi.kivibot.pallo.render.Shader;
import fi.kivibot.pallo.render.Texture;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

/**
 *
 * @author kivi
 */
public class AssetManager {

    private static final HashMap<String, JSONObject> materials = new HashMap<>();
    private static final HashMap<String, Shader> shaders = new HashMap<>();
    private static final HashMap<String, Texture> textures = new HashMap<>();

    private static final HashMap<String, Integer> shader_progs = new HashMap<>();

    private static final int minVer = 1, maxVer = 1;

    public static void addFile(File f) {
        if (f.isDirectory()) {
            return;
        }
        
        if (f.getName().endsWith(".json")) {
            processJSON(new JSONObject(readFileToString(f)));
        }else if(f.getName().endsWith(".png")){
            loadAsTexture(f);
        }
    }

    public static void addDir(File f) {
        if (f.isFile()) {
            return;
        }
        for (File fi : f.listFiles()) {
            addFile(fi);
        }
    }

    public static Texture getTexture(String key) {
        return textures.get(key);
    }

    public static Shader getShader(String key) {
        Shader s = shaders.get(key);
        if(s!=null){
            return s;
        }else{
            //try to make
        }
        return Shader.DEFAULT;
    }

    public static Material getMaterial(String key) {
        JSONObject jso = materials.get(key);
        Texture diffuse = getTexture(jso.getString("diffuse"));
        Shader shader = getShader(jso.getString("shader"));
        Material mat = new Material(key,diffuse,shader);
        return mat;
    }

    private static void processJSON(JSONObject jso) {
        if (jso.getInt("version") > maxVer || jso.getInt("version") < minVer) {
            return;
        }
        switch (jso.getString("type")) {
            case "material":
                materials.put(jso.getString("name"), jso);
                break;
            case "shader":
                shaders.put(jso.getString("name"), Shader.DEFAULT);
                break;
        }
    }

    private static String readFileToString(File f) {
        String file = "";
        try {
            BufferedReader in = new BufferedReader(new FileReader(f));
            String ln;
            while ((ln = in.readLine()) != null) {
                file += ln + "\n";
            }
        } catch (IOException ex) {
            Logger.getLogger(AssetManager.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
        return file;
    }

    private static void loadAsTexture(File f){
        textures.put(f.getName().split("\\.")[0], new Texture(0));
    }
}
