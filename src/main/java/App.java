
import fi.kivibot.misc.Node;
import fi.kivibot.pallo.PalloApp;
import fi.kivibot.pallo.assets.AssetManager;
import fi.kivibot.pallo.audio.AudioListener;
import fi.kivibot.pallo.audio.AudioSource;
import fi.kivibot.pallo.rendering.light.PointLight;
import fi.kivibot.pallo.rendering.Material;
import fi.kivibot.pallo.rendering.Mesh;
import fi.kivibot.pallo.rendering.ParticleEmitter;
import fi.kivibot.pallo.rendering.Spatial;
import fi.kivibot.pallo.rendering.VertexBuffer;
import java.io.BufferedInputStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.WaveData;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

/**
 *
 * @author kivi
 */
public class App extends PalloApp {

    public static void main(String[] args) {
        new App().start();
    }

    private Spatial s;
    private Node n;
    private float x = 0, y = 0;

    private int ic = 1;
    private PointLight l;
    private Node ln;

    private AudioListener al;

    @Override
    protected void Init() {
        AssetManager.add("/assets/");
        Material ma = AssetManager.getMaterial("apina");
        ma.setSpecularColor(new Vector3f(1, 1, 1));

        //Mesh me = new Mesh(new float[]{x - 0.5f, y - 0.5f, x - 0.5f, y + 0.5f, x + 0.5f, y + 0.5f, x + 0.5f, y - 0.5f}, new int[]{0, 1, 2, 0, 2, 3}, new float[]{0, 1, 0, 0, 1, 0, 1, 1});
        s = new Spatial(AssetManager.getMesh("mesh"), ma);
        s.getTransform().setScale(800, 600);

        n = new Node();
        n.addChild(s);
        s.getTransform().translate(new Vector2f(0, 0));

        rootNode.addChild(n);

        for (int i = 0; i < 0; i++) {
            l = new PointLight(new Vector3f((float) Math.random(), (float) Math.random(), (float) Math.random()));
            l.getTransform().setLocalPosition(new Vector2f((float) (Math.random() * 2 - 1), (float) (Math.random() * 2 - 1)));
            l.setHeight(1);
            l.genMesh();
            rootNode.addChild(l);
        }

        ln = new Node();
        rootNode.addChild(ln);
        /*
         l = new PointLight(new Vector3f(1, 1, 1));
         l.getTransform().setLocalPosition(new Vector2f(0, 0));
         l.genMesh();
         ln.addChild(l);
         */
        float rc = 1;
        float ra = 0f;
        for (int i = 0; i < rc; i++) {
            float a = (float) (2f * Math.PI / rc * i);
            float s = (float) (Math.sin(a) * ra);
            float c = (float) (Math.cos(a) * ra);
            //l = new PointLight(new Vector3f(0.68f / rc, 0.88f / rc, 1f / rc));
            l = new PointLight(new Vector3f(0.01f / rc, 0.8f / rc, 0.8f / rc));
            l.getTransform().setLocalPosition(new Vector2f(c, s));
            l.setQuadraticAttenuation(l.getQuadraticAttenuation()/2f);
            l.setLinearAttenuation(0.001f);
            l.setRange(800);
            l.genMesh();
            ln.addChild(l);
        }

        rootNode.addChild(new ParticleEmitter());

        al = new AudioListener(true);

        rootNode.addChild(al);

        this.getAudioEngine().setActiveListener(al);
        for (int i = 0; i < 1; i++) {
            AudioSource as = new AudioSource(WaveData.create(new BufferedInputStream(App.class.getResourceAsStream("assets/test3.wav"))));

            as.getTransform().setLocalPosition(new Vector2f(i, 0f));

//            as.play();
            rootNode.addChild(as);
        }

        rootNode.addChild(new PointLight(new Vector3f(1f, 0.675f, 0.380f)));

        //rootNode.addChild(new AmbientLight(new Vector3f(0.1f, 0.3f, 0.1f)));
    }

    @Override
    protected void Update() {

        al.getTransform().setLocalPosition(new Vector2f((float) Math.cos(System.currentTimeMillis() / 1000.0) * 1.5f, (float) Math.sin(System.currentTimeMillis() / 1000.0) * 1.5f));

        x = Mouse.getX() - 400f;
        y = Mouse.getY() - 300f;

        if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
            for (int ib = 0; ib < 1; ib++) {

                ic++;
                FloatBuffer fb = BufferUtils.createFloatBuffer(8 + s.getMesh().getBuffer(VertexBuffer.Type.Position).getData().capacity());
                FloatBuffer fb2 = (FloatBuffer) s.getMesh().getBuffer(VertexBuffer.Type.Position).getData();
                fb.put(fb2);
                fb.put(new float[]{x, y, x, y + .1f, x + .1f, y + .1f, x + .1f, y});
                fb.flip();
                s.getMesh().getBuffer(VertexBuffer.Type.Position).setData(fb);

                IntBuffer fb3 = BufferUtils.createIntBuffer(ic * 6);
                FloatBuffer fb4 = BufferUtils.createFloatBuffer(ic * 8);

                for (int i = 0; i < ic * 4; i += 4) {
                    fb3.put(new int[]{i, i + 1, i + 2, i, i + 2, i + 3});
                    fb4.put(new float[]{0, 1, 0, 0, 1, 0, 1, 1});
                }
                fb3.flip();
                fb4.flip();
                s.getMesh().getBuffer(VertexBuffer.Type.Index).setData(fb3);
                s.getMesh().getBuffer(VertexBuffer.Type.TexCoord).setData(fb4);

            }
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_B)) {

            ic = 0;
            Mesh me = new Mesh();//new float[]{x, y, x, y + 0.05f, x + 0.05f, y + 0.05f, x + 0.05f, y}, new int[]{0, 1, 2, 0, 2, 3}, new float[]{0, 1, 0, 0, 1, 0, 1, 1});

            s = new Spatial(me, s.getMaterial());
            rootNode.addChild(s);

            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_N)) {
            ln.getTransform().setLocalPosition(new Vector2f(x, y));
            System.out.println(ln.getTransform().getWorldPosition());
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_O)) {
            l.setHeight(l.getHeight() + 0.01f);
            System.out.println(l.getHeight());
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_I)) {
            l.setHeight(l.getHeight() - 0.01f);
            System.out.println(l.getHeight());
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_K)) {
            s.getMaterial().setShininess(s.getMaterial().getShininess() - 0.5f);
            System.out.println(s.getMaterial().getShininess());
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_L)) {
            s.getMaterial().setShininess(s.getMaterial().getShininess() + 0.5f);
            System.out.println(s.getMaterial().getShininess());
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_M)) {
            //this.getRenderer().getAppCam().getTransform().setRotation((float) (System.currentTimeMillis() % 2000 * Math.PI / 1000f));
            //s.getTransform().setRotation((float) (System.currentTimeMillis() % 2000 * Math.PI / 1000f));
            ln.getParent().getTransform().setRotation((float) (System.currentTimeMillis() % 2000 * Math.PI / 1000f));
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
            s.getTransform().translate(new Vector2f(0, 0.01f));
            System.out.println(s.getTransform().getWorldMatrix());
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
            s.getTransform().translate(new Vector2f(0, -0.01f));
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_G)) {
            rootNode.addChild(new ParticleEmitter());
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD4)) {
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD5)) {
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_H)) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            }
            l = new PointLight(new Vector3f((float) Math.random(), (float) Math.random(), (float) Math.random()));
            l.getTransform().setLocalPosition(new Vector2f((float) (Math.random() * 2 - 1), (float) (Math.random() * 2 - 1)));
            l.setHeight(1);
            l.genMesh();
            rootNode.addChild(l);
        }
    }
}
