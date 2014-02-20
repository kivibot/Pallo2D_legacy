/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.kivibot.pallo.rendering;

import org.lwjgl.util.vector.Vector3f;

/**
 *
 * @author kivi
 */
public class Material {

    public static final Material DEFAULT = new Material("DEFAULT", Texture.DEFAULT, Shader.DEFAULT);

    private Texture diffuse, normal, gloss;
    private Shader shader;
    private String name;
    private float shi;
    private Vector3f specular;

    public Material(String n, Texture diff, Shader s) {
        this(n, diff, Texture.DEFAULT, Texture.DEFAULT, s);
    }

    public Material(String n, Texture diff, Texture norm, Texture glo, Shader s) {
        this(n, diff, norm, glo, s, 5, new Vector3f(1, 1, 1));
    }

    public Material(String n, Texture diff, Texture norm, Texture glo, Shader s, float sh, Vector3f spec) {
        diffuse = diff;
        normal = norm;
        gloss = glo;
        shader = s;
        name = n;
        shi = sh;
        specular = spec;
    }

    public Texture getDiffuseMap() {
        return diffuse;
    }

    public Texture getNormalMap() {
        return normal;
    }

    public void setDiffuseMap(Texture t) {
        diffuse = t;
    }

    public void setNormalMap(Texture t) {
        normal = t;
    }

    public Texture getGlossMap() {
        return gloss;
    }

    public void setGlossMap(Texture t) {
        gloss = t;
    }

    public Shader getShader() {
        return shader;
    }

    public void setShader(Shader s) {
        shader = s;
    }

    public float getShininess() {
        return this.shi;
    }

    public void setShininess(float f) {
        shi = f;
    }

    public Vector3f getSpecularColor() {
        return this.specular;
    }

    public void setSpecularColor(Vector3f c) {
        this.specular = c;
    }

    @Override
    public String toString() {
        return "Material\n\tname: " + name + "\n\tDiffuse: " + diffuse
                + "\n\tNormal: " + normal + "\n\tGloss: " + gloss
                + "\n\tShader: " + shader;
    }

}
