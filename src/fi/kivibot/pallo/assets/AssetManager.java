/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.kivibot.pallo.assets;

import fi.kivibot.pallo.render.Material;
import fi.kivibot.pallo.render.Shader;
import fi.kivibot.pallo.render.Texture;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.json.JSONObject;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

/**
 *
 * @author kivi
 */
public class AssetManager {

    private static final HashMap<String, JSONObject> materials = new HashMap<>();
    private static final HashMap<String, Shader> shaders = new HashMap<>();
    private static final HashMap<String, Texture> textures = new HashMap<>();

    private static final HashMap<String, Integer> shader_part = new HashMap<>();
    private static final HashMap<String, JSONObject> shader_prog = new HashMap<>();

    private static final HashMap<String, InputStream> tex_stream = new HashMap<>();

    private static final int minVer = 1, maxVer = 1;

    public static void addFile(File f) {
        if (f.isDirectory()) {
            return;
        }

        if (f.getName().endsWith(".json")) {
            processJSON(new JSONObject(readFileToString(f)));
        } else if (f.getName().endsWith(".png")) {
            loadAsTexture(f);
        } else if (f.getName().endsWith(".glsl")) {
            loadShader(readFileToString(f), f.getName().split("\\.")[0], f.getName().split("\\.")[1]);
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
        Texture tex = textures.get(key);
        if (tex != null) {
            return tex;
        } else {
            InputStream is = tex_stream.get(key);
            if (is != null) {
                tex = loadAsTexture(is, key);
                textures.put(key, tex);
                return tex;
            }
        }
        return Texture.DEFAULT;
    }

    public static Shader getShader(String key) {
        Shader s = shaders.get(key);
        if (s != null) {
            return s;
        } else {
            //try to make
            JSONObject jso = shader_prog.get(key);

            Integer ve = shader_part.get(jso.getString("vertex"));
            Integer fr = shader_part.get(jso.getString("fragment"));
            if (ve != null && fr != null) {
                int sid = GL20.glCreateProgram();

                GL20.glAttachShader(sid, ve.intValue());
                GL20.glAttachShader(sid, fr.intValue());

                GL20.glLinkProgram(sid);
                GL20.glValidateProgram(sid);

                s = new Shader(key, sid);
                shaders.put(key, s);
                return s;
            }

        }
        return Shader.DEFAULT;
    }

    public static Material getMaterial(String key) {
        JSONObject jso = materials.get(key);
        if (jso == null) {
            return Material.DEFAULT;
        }
        Texture diffuse = getTexture(jso.getString("diffuse"));
        Shader shader = getShader(jso.getString("shader"));
        Material mat = new Material(key, diffuse, shader);
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
                shader_prog.put(jso.getString("name"), jso);
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

    private static void loadAsTexture(File f) {
        try {
            tex_stream.put(f.getName().split("\\.")[0], new FileInputStream(f));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(AssetManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static Texture loadAsTexture(InputStream is, String name) {
        return loadTex(is, name);
    }

    private static void wipeAllGPUAssets() {
        for (Texture t : textures.values()) {
            GL11.glDeleteTextures(t.getID());
        }
        for (Shader s : shaders.values()) {
            GL20.glDeleteProgram(s.getID());
        }
        for (Integer i : shader_part.values()) {
            GL20.glDeleteShader(i.intValue());
        }
    }

    private static void wipeAllCPUAssets() {
        materials.clear();
        shaders.clear();
        textures.clear();

        shader_part.clear();
        shader_prog.clear();
        tex_stream.clear();
    }

    public static void clean() {
        wipeAllGPUAssets();
        wipeAllCPUAssets();
    }

    private static void loadShader(String raw, String name, String type) {
        int sid;
        switch (type) {
            case "frag":
                sid = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
                break;
            case "vert":
                sid = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
                break;
            default:
                return;
        }
        GL20.glShaderSource(sid, raw);
        GL20.glCompileShader(sid);
        if (GL20.glGetShader(sid, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            System.err.println("Could not compile shader.");
            return;
        }
        shader_part.put(name + "." + type, new Integer(sid));
    }

    public static String status() {
        return textures.size() + "/" + tex_stream.size() + " Textures, "
                + shaders.size() + "/" + shader_prog.size() + " Shaders ["
                + shader_part.size() + "], " + materials.size() + " Materials";
    }

    private static Texture loadTex(InputStream is, String name) {
        try {
            BufferedImage buf = ImageIO.read(is);
            return loadTex(buf, name);
        } catch (IOException ex) {
            Logger.getLogger(AssetManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private static Texture loadTex(BufferedImage image, String name) {
        int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

        ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4);
        
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int pixel = pixels[y * image.getWidth() + x];
                buffer.put((byte) ((pixel >> 16) & 0xFF));
                buffer.put((byte) ((pixel >> 8) & 0xFF));
                buffer.put((byte) (pixel & 0xFF));
                buffer.put((byte) ((pixel >> 24) & 0xFF));
            }
        }

        buffer.flip();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        int tid = GL11.glGenTextures();

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, tid);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, image.getWidth(), image.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);

        Texture ret = new Texture(name, tid);

        textures.put(name, ret);

        return ret;
    }
}
