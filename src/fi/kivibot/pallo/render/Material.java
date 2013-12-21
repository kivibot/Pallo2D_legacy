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
public class Material {

    private Texture diffuse;
    private Shader shader;
    private String name;

    public Material(String n, Texture diff, Shader s) {
        diffuse = diff;
        shader = s;
        name = n;
    }

    public Texture getDiffuseMap() {
        return diffuse;
    }

    public void setDiffuseMap(Texture t) {
        diffuse = t;
    }

    public Shader getShader() {
        return shader;
    }

    public void setShader(Shader s) {
        shader = s;
    }

    @Override
    public String toString() {
        return "Material\n\tname: " + name + "\n\tDiffuse: " + diffuse + "\n\tShader: " + shader;
    }

}
