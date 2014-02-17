/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.kivibot.pallo.assets;

import fi.kivibot.pallo.rendering.Material;
import fi.kivibot.pallo.rendering.Mesh;
import fi.kivibot.pallo.rendering.Shader;
import fi.kivibot.pallo.rendering.Texture;
import fi.kivibot.pallo.rendering.VertexBuffer;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.json.JSONArray;
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
    private static final HashMap<String, Material> materials_ready = new HashMap<>();
    private static final HashMap<String, Shader> shaders = new HashMap<>();
    private static final HashMap<String, Texture> textures = new HashMap<>();
    private static final HashMap<String, Mesh> meshes = new HashMap<>();

    private static final HashMap<String, String> shader_parts = new HashMap<>();
    private static final HashMap<String, JSONObject> shader_prog = new HashMap<>();

    private static final HashMap<String, InputStream> tex_stream = new HashMap<>();

    private static final int minVer = 1, maxVer = 4;

    public static void addFile(File f) {
        if (f.isDirectory()) {
            addDir(f);
            return;
        }

        if (f.getName().endsWith(".json")) {
            processJSON(new JSONObject(readFileToString(f)));
            System.out.println("loaded " + f);
        } else if (f.getName().endsWith(".png")) {
            loadAsTexture(f);
            System.out.println("loaded " + f);
        } else if (f.getName().endsWith(".glsl")) {
            loadShader(readFileToString(f), f.getName().split("\\.")[0], f.getName().split("\\.")[1]);
            System.out.println("loaded " + f);
        } else if (f.getName().endsWith(".p2dme")) {
            loadMesh(readFileToString(f), f.getName().split("\\.")[0]);
            System.out.println("loaded " + f);
        } else {
            System.out.println("did not load " + f);
        }
    }

    public static void addDir(File f) {
        if (f.isFile()) {
            addFile(f);
            return;
        }
        for (File fi : f.listFiles()) {
            addFile(fi);
        }
    }

    public static Mesh getMesh(String key) {
        return meshes.get(key);
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
            if (jso != null) {
                String ve = shader_parts.get(jso.getString("vertex"));
                String fr = shader_parts.get(jso.getString("fragment"));

                if (ve != null && fr != null) {

                    String[] defines;

                    if (jso.getInt("version") >= 4) {
                        JSONArray jsoa = jso.getJSONArray("defines");
                        defines = new String[jsoa.length()];
                        for (int i = 0; i < jsoa.length(); i++) {
                            defines[i] = jsoa.getString(i);
                        }
                    } else {
                        defines = new String[0];
                    }

                    int veid = AssetManager.compileShader(ve, "vert", defines);
                    if (veid == -1) {
                        System.out.println("Could not compile shader :\\");
                        return null;
                    }
                    int frid = AssetManager.compileShader(fr, "frag", defines);
                    if (frid == -1) {
                        System.out.println("Could not compile shader :/");
                        return null;
                    }

                    int sid = GL20.glCreateProgram();

                    GL20.glAttachShader(sid, veid);
                    GL20.glAttachShader(sid, frid);

                    if (jso.getInt("version") >= 3) {
                        JSONArray jsa = jso.getJSONArray("varloc");
                        for (int i = 0; i < jsa.length(); i++) {
                            JSONObject vlo = jsa.getJSONObject(i);
                            String name = vlo.getString("name");
                            int id = vlo.getInt("id");
                            String typ = vlo.getString("type");
                            switch (typ) {
                                case "frag":
                                    GL30.glBindFragDataLocation(sid, id, name);
                                    break;
                                case "vert":
                                    GL20.glBindAttribLocation(sid, id, name);
                            }
                        }
                    }

                    GL20.glLinkProgram(sid);
                    GL20.glValidateProgram(sid);

                    s = new Shader(key, sid);
                    shaders.put(key, s);
                    return s;
                }
            }

        }
        return Shader.DEFAULT;
    }

    public static Material getMaterial(String key) {
        Material m = materials_ready.get(key);
        if (m != null) {
            return m;
        }
        JSONObject jso = materials.get(key);
        if (jso == null) {
            return Material.DEFAULT;
        }
        Texture diffuse = getTexture(jso.getString("diffuse"));
        Texture normal = Texture.DEFAULT;
        Texture gloss = Texture.DEFAULT;
        if (jso.getInt("version") >= 2) {
            normal = getTexture(jso.getString("normal"));
        }
        if (jso.getInt("version") >= 3) {
            gloss = getTexture(jso.getString("gloss"));
        }
        Shader shader = getShader(jso.getString("shader"));
        Material mat = new Material(key, diffuse, normal, gloss, shader);
        materials_ready.put(key, mat);
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
            Logger.getLogger(AssetManager.class
                    .getName()).log(Level.SEVERE, null, ex);

            return "";
        }
        return file;
    }

    private static void loadAsTexture(File f) {
        try {
            tex_stream.put(f.getName().split("\\.")[0], new FileInputStream(f));

        } catch (FileNotFoundException ex) {
            Logger.getLogger(AssetManager.class
                    .getName()).log(Level.SEVERE, null, ex);
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
    }

    private static void wipeAllCPUAssets() {
        materials.clear();
        shaders.clear();
        textures.clear();

        shader_parts.clear();
        shader_prog.clear();
        tex_stream.clear();
    }

    public static void clean() {
        wipeAllGPUAssets();
        wipeAllCPUAssets();
    }

    private static int compileShader(String raw_, String type, String[] defines) {
        int sid;
        switch (type) {
            case "frag":
                sid = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
                break;
            case "vert":
                sid = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
                break;
            default:
                System.out.println("No such type: " + type);
                return -1;
        }
        String raw = "";
        for (String s : defines) {
            raw += "#define " + s + "\n";
        }
        raw += raw_;
        GL20.glShaderSource(sid, raw);
        GL20.glCompileShader(sid);
        int err = GL20.glGetShaderi(sid, GL20.GL_COMPILE_STATUS);
        if (err == GL11.GL_FALSE) {
            int ls = GL20.glGetShaderi(sid, GL20.GL_INFO_LOG_LENGTH);
            String str = GL20.glGetShaderInfoLog(sid, ls);
            System.out.println(">>error");
            System.err.println(str);
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(AssetManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            return -1;
        }
        return sid;
    }

    private static void loadShader(String raw, String name, String type) {
        shader_parts.put(name + "." + type, raw);
    }

    private static void loadMesh(String instr, String name) {
        String[] pts = instr.split("\n");
        int vc = 0;
        int fc = 0;
        int tc = 0;
        for (String s : pts) {
            if (s.length() == 0) {
                continue;
            }
            switch (s.charAt(0)) {
                case 'v':
                    vc++;
                    break;
                case 'f':
                    fc++;
                case 't':
                    tc++;
                    break;
                default:
                    break;
            }
        }
        FloatBuffer ver = BufferUtils.createFloatBuffer(vc * 2);
        IntBuffer ind = BufferUtils.createIntBuffer(fc * 3);
        FloatBuffer tec = BufferUtils.createFloatBuffer(tc * 2);
        for (String s : pts) {
            String[] sp = s.split(" ");
            switch (sp[0]) {
                case "v":
                    float x = Float.parseFloat(sp[1]);
                    float y = Float.parseFloat(sp[2]);
                    ver.put(new float[]{x, y});
                    break;
                case "f":
                    ind.put(Integer.parseInt(sp[1]));
                    ind.put(Integer.parseInt(sp[2]));
                    ind.put(Integer.parseInt(sp[3]));
                    break;
                case "t":
                    float u = Float.parseFloat(sp[1]);
                    float v = Float.parseFloat(sp[2]);
                    tec.put(new float[]{u, v});
                    break;
                default:
                    break;
            }
        }
        ver.flip();
        ind.flip();
        tec.flip();
        VertexBuffer v = new VertexBuffer(VertexBuffer.Type.Float, VertexBuffer.Usage.Dynamic);
        VertexBuffer t = new VertexBuffer(VertexBuffer.Type.Float, VertexBuffer.Usage.Dynamic);
        VertexBuffer i = new VertexBuffer(VertexBuffer.Type.Integer, VertexBuffer.Usage.Dynamic);
        v.setData(ver);
        t.setData(tec);
        i.setData(ind);
        Mesh m = new Mesh().addBuffer("position", v).addBuffer("texcoord", t).addBuffer("index", i);
        meshes.put(name, m);
    }

    public static String status() {
        return textures.size() + "/" + tex_stream.size() + " Textures, "
                + shaders.size() + "/" + shader_prog.size() + " Shaders ["
                + shader_parts.size() + "], " + materials.size() + " Materials";
    }

    private static Texture loadTex(InputStream is, String name) {
        try {
            BufferedImage buf = ImageIO.read(is);
            return loadTex(buf, name);

        } catch (IOException ex) {
            Logger.getLogger(AssetManager.class
                    .getName()).log(Level.SEVERE, null, ex);
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
