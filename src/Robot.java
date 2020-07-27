// klasa w której tworzymy naszego robota

import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.Sphere;

import javax.media.j3d.*;
import javax.vecmath.Color3f;
import javax.vecmath.Color4f;
import javax.vecmath.Vector3f;
import java.awt.*;

public class Robot {

    // transformgroup całego robota
    public TransformGroup group;

    // kąty każdego ramienia
    public float angles[];

    // transformgroupy każdych części manipulatora
    public TransformGroup[] tgArm;
    public TransformGroup[] tgJoint;
    public Transform3D[] transformArm;
    public Transform3D[] transformJoint;

    // obiekty z których składa się robot
    private Sphere[] joint;
    private Shape3D[] arms;
    private Shape3D[] yawShapes;
    private Shape3D[] pitchShapes;
    private Shape3D[] rollShapes;
    private Cylinder[] yawCylinder;
    private Cylinder[] pitchCylinder;
    private Cylinder[] rollCylinder;

    // pomocna zmienna do zmiany transformacji
    private Transform3D tmp;

    // predkosc z jaka się obraca ramię - 1/180
    public float robotSpeed = 180;

    public char lastMove;
    public Boolean[] key;
    public Boolean[] notAllow;

    Robot(){
        group = makeRobot();
        key = new Boolean[256];
        notAllow = new Boolean[12];
        for(int i=0; i<256; i++){
            key[i] = false;
        }
        for(int i=0; i<12; i++){
            notAllow[i] = false;
        }
    }

    private TransformGroup makeRobot(){
        TransformGroup bg = new TransformGroup();

        angles = new float[6];
        tgArm = new TransformGroup[6];
        tgJoint = new TransformGroup[3];
        transformArm = new Transform3D[6];
        transformJoint = new Transform3D[3];
        arms = new Shape3D[3];
        yawShapes = new Shape3D[2];
        pitchShapes = new Shape3D[2];
        rollShapes = new Shape3D[2];
        yawCylinder = new Cylinder[2];
        pitchCylinder = new Cylinder[1];
        rollCylinder = new Cylinder[1];
        joint = new Sphere[3];
        tmp = new Transform3D();

        // podstawa
        Shape3D podst = new MyShapes().makeCustomCylinder(0.4f, 0.35f, 0.15f, 100);
        podst.setAppearance(createAppearance(new Color3f(Color.GRAY)));
        Transform3D transformPodst = new Transform3D();
        TransformGroup tgPodst = new TransformGroup(transformPodst);
        tgPodst.addChild(podst);
        bg.addChild(tgPodst);

        // kształty
        arms[0] = new MyShapes().makeCustomCylinder(0.35f, 0.35f, 0.05f, 100);
        arms[0].setAppearance(createAppearance(new Color3f(Color.ORANGE)));

        Shape3D arm0part0 = new MyShapes().makeCustomCylinder(0.1f, 0.1f, 0.05f, 100);
        arm0part0.setAppearance(createAppearance(new Color3f(Color.ORANGE)));

        // ustawienie kształtów aby utworzyły dany obiekt
        Transform3D transformPart0 = new Transform3D();
        transformPart0.setTranslation(new Vector3f(0.0f, -0.05f, 0.0f));
        TransformGroup tgPart0 = new TransformGroup(transformPart0);
        tgPart0.addChild(arm0part0);

        Shape3D arm0part1 = new MyShapes().makeTriangularShape(0.3f, 0.1f, 0.3f, 0.03f);
        arm0part1.setAppearance(createAppearance(new Color3f(Color.ORANGE)));
        Shape3D arm0part2 = new MyShapes().makeTriangularShape(0.3f, 0.1f, 0.3f, 0.03f);
        arm0part2.setAppearance(createAppearance(new Color3f(Color.ORANGE)));

        Transform3D transformPart1 = new Transform3D();
        transformPart1.setTranslation(new Vector3f(0.0f, 0.05f, 0.25f));
        TransformGroup tgPart1 = new TransformGroup(transformPart1);
        tgPart1.addChild(arm0part1);

        Transform3D transformPart2 = new Transform3D();
        transformPart2.setTranslation(new Vector3f(0.0f, 0.05f, -0.25f));
        TransformGroup tgPart2 = new TransformGroup(transformPart2);
        tgPart2.addChild(arm0part2);

        // ramię
        // początkowy kąt ramienia
        angles[0] = 0;
        transformArm[0] = new Transform3D();
        transformArm[0].setTranslation(new Vector3f(0.0f, 0.2f, 0.0f));
        tgArm[0] = new TransformGroup(transformArm[0]);
        tgArm[0].setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        tgArm[0].addChild(arms[0]);
        tgArm[0].addChild(tgPart0);
        tgArm[0].addChild(tgPart1);
        tgArm[0].addChild(tgPart2);
        bg.addChild(tgArm[0]);

        // połączenie ramienia i barku
        joint[0] = new Sphere(0.25f);
        joint[0].setAppearance(createAppearance(new Color3f(Color.ORANGE)));

        // ustawienia położenia kształtów
        Shape3D joint0Part0 = new MyShapes().makeCustomCylinder(0.1f, 0.1f, 0.6f, 100);
        joint0Part0.setAppearance(createAppearance(new Color3f(Color.ORANGE)));
        Transform3D rotationJoint0Part0 = new Transform3D();
        rotationJoint0Part0.rotX(Math.PI/2);
        TransformGroup tgRotJoint0Part0 = new TransformGroup(rotationJoint0Part0);
        tgRotJoint0Part0.addChild(joint0Part0);

        Transform3D transformJoint0Part0 = new Transform3D();
        transformJoint0Part0.setTranslation(new Vector3f(0.0f, 0.0f, -0.3f));
        TransformGroup tgJoint0Part0 = new TransformGroup(transformJoint0Part0);
        tgJoint0Part0.addChild(tgRotJoint0Part0);

        // bark
        // początkowy kąt barku
        angles[1] = -(float)(Math.PI/4);
        transformJoint[0] = new Transform3D();
        transformJoint[0].setTranslation(new Vector3f(0.0f, 0.25f, 0.0f));
        tmp.rotZ(-Math.PI/4);
        transformJoint[0].mul(tmp);
        tgJoint[0] = new TransformGroup(transformJoint[0]);
        tgJoint[0].setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        tgJoint[0].addChild(joint[0]);
        tgJoint[0].addChild(tgJoint0Part0);
        tgArm[0].addChild(tgJoint[0]);

        arms[1] = new MyShapes().makeCustomCylinder(0.1f, 0.08f, 0.5f, 100);
        arms[1].setAppearance(createAppearance(new Color3f(Color.ORANGE)));
        transformArm[1] = new Transform3D();
        transformArm[1].setTranslation(new Vector3f(0.0f, 0.2f, 0.0f));
        tgArm[1] = new TransformGroup(transformArm[1]);
        tgArm[1].addChild(arms[1]);
        tgJoint[0].addChild(tgArm[1]);

        // połączenie między barkiem i łokciem ramienia
        angles[2] = -(float)(Math.PI/4);
        joint[1] = new Sphere(0.15f);
        joint[1].setAppearance(createAppearance(new Color3f(Color.ORANGE)));
        transformJoint[1] = new Transform3D();
        transformJoint[1].setTranslation(new Vector3f(0.0f, 0.55f, 0.0f));
        tmp.rotZ(-Math.PI/4);
        transformJoint[1].mul(tmp);
        tgJoint[1] = new TransformGroup(transformJoint[1]);
        tgJoint[1].setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        tgJoint[1].addChild(joint[1]);
        tgArm[1].addChild(tgJoint[1]);

        // łokieć
        arms[2] = new MyShapes().makeCustomCylinder(0.08f, 0.07f, 0.5f, 100);
        arms[2].setAppearance(createAppearance(new Color3f(Color.WHITE)));
        transformArm[2] = new Transform3D();
        transformArm[2].setTranslation(new Vector3f(0.0f, 0.05f, 0.0f));
        tgArm[2] = new TransformGroup(transformArm[2]);
        tgArm[2].addChild(arms[2]);
        tgJoint[1].addChild(tgArm[2]);

        // roll
        angles[3] = 0;
        yawCylinder[0] = new Cylinder(0.1f, 0.2f);
        yawCylinder[0].setAppearance(createAppearance(new Color3f(Color.ORANGE)));
        yawCylinder[1] = new Cylinder(0.1f, 0.2f);
        yawCylinder[1].setAppearance(createAppearance(new Color3f(Color.ORANGE)));

        yawShapes[0] = new MyShapes().makeTriangularShape(0.15f, 0.05f, 0.2f, 0.01f);
        yawShapes[0].setAppearance(createAppearance(new Color3f(Color.ORANGE)));
        yawShapes[1] = new MyShapes().makeTriangularShape(0.15f, 0.05f, 0.2f, 0.01f);
        yawShapes[1].setAppearance(createAppearance(new Color3f(Color.ORANGE)));

        // ustawienia położenia kształtów
        Transform3D yawRotation = new Transform3D();
        yawRotation.rotX(Math.PI/2);
        TransformGroup yawRotTg1 = new TransformGroup(yawRotation);
        yawRotTg1.addChild(yawCylinder[0]);
        Transform3D yawTransform1 = new Transform3D();
        yawTransform1.setTranslation(new Vector3f(0.0f, 0.1f, 0.0f));
        TransformGroup yawTg1 = new TransformGroup(yawTransform1);
        yawTg1.addChild(yawRotTg1);

        Transform3D yawTransform2 = new Transform3D();
        yawTransform2.setTranslation(new Vector3f(0.0f, 0.1f, 0.1f));
        TransformGroup yawTg2 = new TransformGroup(yawTransform2);
        yawTg2.addChild(yawShapes[0]);

        Transform3D yawTransform3 = new Transform3D();
        yawTransform3.setTranslation(new Vector3f(0.0f, 0.1f, -0.1f));
        TransformGroup yawTg3 = new TransformGroup(yawTransform3);
        yawTg3.addChild(yawShapes[1]);

        Transform3D yawTransform4 = new Transform3D();
        yawTransform4.setTranslation(new Vector3f(0.0f, 0.0f, 0.0f));
        TransformGroup yawTg4 = new TransformGroup(yawTransform4);
        yawTg4.addChild(yawCylinder[1]);

        transformArm[3] = new Transform3D();
        transformArm[3].setTranslation(new Vector3f(0.0f, 0.6f, 0.0f));
        tgArm[3] = new TransformGroup(transformArm[3]);
        tgArm[3].setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        tgArm[3].addChild(yawTg1);
        tgArm[3].addChild(yawTg2);
        tgArm[3].addChild(yawTg3);
        tgArm[3].addChild(yawTg4);
        tgArm[2].addChild(tgArm[3]);

        // pitch
        angles[4] = 0;
        pitchShapes[0] = new MyShapes().makeCuboid(0.2f, 0.025f, 0.15f);
        pitchShapes[0].setAppearance(createAppearance(new Color3f(Color.ORANGE)));
        pitchShapes[1] = new MyShapes().makeCuboid(0.1f, 0.05f, 0.1f);
        pitchShapes[1].setAppearance(createAppearance(new Color3f(Color.ORANGE)));
        pitchCylinder[0] = new Cylinder(0.05f, 0.2f);
        pitchCylinder[0].setAppearance(createAppearance(new Color3f(Color.ORANGE)));

        // ustawienia położenia kształtów
        Transform3D pitchTransform1 = new Transform3D();
        pitchTransform1.rotX(Math.PI/2);
        TransformGroup pitchTg1 = new TransformGroup(pitchTransform1);
        pitchTg1.addChild(pitchCylinder[0]);

        Transform3D pitchTransform2 = new Transform3D();
        pitchTransform2.rotY(Math.PI/2);
        TransformGroup pitchTg2 = new TransformGroup(pitchTransform2);
        pitchTg2.addChild(pitchShapes[0]);

        Transform3D pitchRotation3 = new Transform3D();
        pitchRotation3.rotX(Math.PI/2);
        tmp.rotZ(Math.PI/2);
        pitchRotation3.mul(tmp);
        TransformGroup pitchRotTg = new TransformGroup(pitchRotation3);
        pitchRotTg.addChild(pitchShapes[1]);
        Transform3D pitchTransform3 = new Transform3D();
        pitchTransform3.setTranslation(new Vector3f(0.05f, 0.125f, 0.0f));
        TransformGroup pitchTg3 = new TransformGroup(pitchTransform3);
        pitchTg3.addChild(pitchRotTg);

        transformArm[4] = new Transform3D();
        transformArm[4].setTranslation(new Vector3f(0.0f, 0.25f, 0.0f));
        tgArm[4] = new TransformGroup(transformArm[4]);
        tgArm[4].setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        tgArm[4].addChild(pitchTg1);
        tgArm[4].addChild(pitchTg2);
        tgArm[4].addChild(pitchTg3);
        tgArm[3].addChild(tgArm[4]);

        // yaw
        angles[5] = 0;
        joint[2] = new Sphere(0.06f);
        joint[2].setAppearance(createAppearance(new Color3f(Color.ORANGE)));
        transformJoint[2] = new Transform3D();
        transformJoint[2].setTranslation(new Vector3f(0.0f, 0.15f, 0.0f));
        tgJoint[2] = new TransformGroup(transformJoint[2]);
        tgJoint[2].setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        tgJoint[2].addChild(joint[2]);
        tgArm[4].addChild(tgJoint[2]);

        rollCylinder[0] = new Cylinder(0.05f, 0.1f);
        rollCylinder[0].setAppearance(createAppearance(new Color3f(Color.ORANGE)));
        rollShapes[0] = new MyShapes().makeTriangularShape(0.05f, 0.02f, 0.05f, 0.01f);
        rollShapes[0].setAppearance(createAppearance(new Color3f(Color.ORANGE)));
        rollShapes[1] = new MyShapes().makeTriangularShape(0.05f, 0.02f, 0.05f, 0.01f);
        rollShapes[1].setAppearance(createAppearance(new Color3f(Color.ORANGE)));

        // ustawienia położenia kształtów
        Transform3D rollTransform1 = new Transform3D();
        rollTransform1.setTranslation(new Vector3f(0.0f, 0.05f, 0.02f));
        TransformGroup rollTg1 = new TransformGroup(rollTransform1);
        rollTg1.addChild(rollShapes[0]);

        Transform3D rollTransform2 = new Transform3D();
        rollTransform2.setTranslation(new Vector3f(0.0f, 0.05f, -0.02f));
        TransformGroup rollTg2 = new TransformGroup(rollTransform2);
        rollTg2.addChild(rollShapes[1]);

        transformArm[5] = new Transform3D();
        transformArm[5].setTranslation(new Vector3f(0.0f, 0.1f, 0.0f));
        tgArm[5] = new TransformGroup(transformArm[5]);
        tgArm[5].setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        tgArm[5].setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
        tgArm[5].setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
        tgArm[5].setCapability(TransformGroup.ALLOW_CHILDREN_READ);
        tgArm[5].addChild(rollCylinder[0]);
        tgArm[5].addChild(rollTg1);
        tgArm[5].addChild(rollTg2);

        tgJoint[2].addChild(tgArm[5]);

        return bg;
    }

    public Appearance createAppearance(Color3f color){
        // metoda która zwraca wygląd z kolorem który podamy
        Appearance appearance = new Appearance();
        Color3f black = new Color3f(0.0f, 0.0f, 0.0f);
        Color3f white = new Color3f(1.0f, 1.0f, 1.0f);
        Texture texture = new Texture2D();
        TextureAttributes texAttr = new TextureAttributes();
        texAttr.setTextureMode(TextureAttributes.MODULATE);
        texture.setBoundaryModeS(Texture.WRAP);
        texture.setBoundaryModeT(Texture.WRAP);
        texture.setBoundaryColor(new Color4f(1.0f, 1.0f, 0.0f, 0.0f));
        Material mat = new Material(color, black, color, white, 70f);
        appearance.setTextureAttributes(texAttr);
        appearance.setMaterial(mat);
        appearance.setTexture(texture);

        return appearance;
    }

    public TransformGroup getGroup(){
        return group;
    }

    public Vector3f handPos(){
        // metoda zwracająca pozycję końcówki chwytaka
        Vector3f pos;

        tmp.setTranslation(new Vector3f(0.0f, 0.25f, 0.0f));
        transformArm[5].mul(tmp);
        tgArm[5].setTransform(transformArm[5]);
        Transform3D tr = new Transform3D();
        Vector3f v = new Vector3f();
        rollShapes[0].getLocalToVworld(tr);
        tr.get(v);
        pos = v;
        transformArm[5].mulInverse(tmp);
        tgArm[5].setTransform(transformArm[5]);

        return pos;
    }
}
