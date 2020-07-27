// ta klasa odpowiada za stworzenie świata, obiektu do podnoszenia, otoczenia i klas typu CollisionDetector

import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.universe.SimpleUniverse;

import javax.media.j3d.*;
import javax.swing.*;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import java.awt.*;
public class World extends JFrame {

    private Robot robot;
    private SimpleUniverse simpleU;

    // definicja przycisków
    public JButton catchObject;
    public JButton letGoObject;
    public JButton startRecording;
    public JButton stopRecording;
    public JButton playRecording;
    public JButton stopPlayRecording;
    public JButton resetCameraView;

    // obiekt, polozenie i jego transformacje
    public Sphere object;
    public Vector3f objPos = new Vector3f(1.5f, 0.5f, 0.0f);
    public Transform3D objectTransform;
    public TransformGroup objectTg;
    public TransformGroup world;
    public BranchGroup objectGroup;

    // zmienna mówiąca czy jest kolizja na obiekcie
    public boolean objColl = false;

    // zmienna mówiąca czy obiekt jest trzymany przez nasz manipulator
    public boolean objHold = false;

    // zmienna mówiąca czy właśnie nagrywamy
    public boolean recording = false;
    public int recordingCount = 0;

    // zmienna mówiąca czy odtwarzamy nagranie
    public boolean recordingPlay = false;
    public int playingRecordCount = 0;

    // zmienna mówiąca że ustawiamy położenie manipulatora do początkowego położenia podczas nagrywania
    public boolean setting = false;
    public int settingCount = 0;

    // zmienna dajaca dostep do zmiennych klasy GameLoop
    public GameLoop loop;

    World(){
        // tworzenie okienka
        super("Projekt");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());

        robot = new Robot();

        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();

        // tworzenie płótna
        Canvas3D canvas3d = new Canvas3D(config);
        canvas3d.setPreferredSize(new Dimension(800, 600));

        add("Center", canvas3d);
        pack();
        setVisible(true);

        // panel z instrukcją i przyciskami
        JPanel p = new JPanel();
        p.setPreferredSize(new Dimension(130, 200));
        p.setLayout(new FlowLayout());

        p.add(new Label("Instrukcja:"));
        p.add(new Label("a, d - Arm sweep"));
        p.add(new Label("w, s - Shoulder swivel"));
        p.add(new Label("q, e - Elbow"));
        p.add(new Label("j, l - Yaw"));
        p.add(new Label("i, k - Pitch"));
        p.add(new Label("u, o - Roll"));

        // przyciski
        catchObject = new JButton("Chwyć obiekt");
        catchObject.addActionListener(new ButtonsAction(this, robot));
        p.add(catchObject);

        letGoObject = new JButton("Puść obiekt");
        letGoObject.addActionListener(new ButtonsAction(this, robot));
        p.add(letGoObject);

        startRecording = new JButton("Zacznij nagrywać");
        startRecording.addActionListener(new ButtonsAction(this, robot));
        p.add(startRecording);

        stopRecording = new JButton("Przestań nagrywać");
        stopRecording.addActionListener(new ButtonsAction(this, robot));
        p.add(stopRecording);

        playRecording = new JButton("Odtwórz nagranie");
        playRecording.addActionListener(new ButtonsAction(this, robot));
        p.add(playRecording);

        stopPlayRecording = new JButton("Zatrzymaj odtwarzanie");
        stopPlayRecording.addActionListener(new ButtonsAction(this, robot));
        p.add(stopPlayRecording);

        resetCameraView = new JButton("Zresetuj widok kamery");
        resetCameraView.addActionListener(new ButtonsAction(this, robot));
        p.add(resetCameraView);

        add("East", p);

        pack();

        // simpleUniverse
        simpleU = new SimpleUniverse(canvas3d);

        Transform3D observerPos = new Transform3D();
        observerPos.set(new Vector3f(0.0f,0.5f,5.0f));
        simpleU.getViewingPlatform().getViewPlatformTransform().setTransform(observerPos);

        // obsługa myszy, żeby można było obracać kamerą
        OrbitBehavior orbit = new OrbitBehavior(canvas3d, OrbitBehavior.REVERSE_ROTATE);
        orbit.setSchedulingBounds(new BoundingSphere());
        simpleU.getViewingPlatform().setViewPlatformBehavior(orbit);

        simpleU.addBranchGraph(getScene());
    }

    private BranchGroup getScene(){
        // branch grooup calej sceny
        BranchGroup scene = new BranchGroup();

        // transformgroup zawierający robota oraz podłoże
        world = new TransformGroup();
        world.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        world.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
        world.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
        world.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
        TransformGroup robotTg;

        // nasz robot pobrany z klasy Robot
        robotTg = robot.getGroup();
        robotTg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        world.addChild(robotTg);

        // tworzenie scian
        float szerokoscScian = 10f;
        makeWalls(szerokoscScian);

        // tworzenie podłoża
        TransformGroup ground = new TransformGroup();
        Shape3D gr = new MyShapes().makeGround(new Point3f(szerokoscScian, 0f, szerokoscScian),
                new Point3f(szerokoscScian, 0f, -szerokoscScian), new Point3f(-szerokoscScian, 0f, -szerokoscScian),
                new Point3f(-szerokoscScian, 0f, szerokoscScian));
        gr.setUserData(new String("ground"));
        Appearance appGround = new Appearance();
        appGround.setTexture(createTexture("grafika/floor.jpg"));
        gr.setAppearance(appGround);

        // detekcja kolizji dla ziemi
        CollisionDetector collisionGround = new CollisionDetector(gr, new BoundingSphere(), this, robot);
        ground.addChild(gr);
        world.addChild(collisionGround);
        world.addChild(ground);

        // światła
        Color3f light1Color = new Color3f(1.0f, 1.0f, 1.0f);
        Vector3f light1Direction = new Vector3f(4.0f, -7.0f, -12.0f);
        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);

        DirectionalLight light1 = new DirectionalLight(light1Color, light1Direction);
        light1.setInfluencingBounds(bounds);
        scene.addChild(light1);

        // obiekt do podnoszenia
        object = new Sphere(0.15f, robot.createAppearance(new Color3f(Color.ORANGE)));
        object.getShape().setUserData(new String("object"));
        objectTransform = new Transform3D();
        objectTransform.setTranslation(objPos);
        objectTg = new TransformGroup(objectTransform);
        objectTg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        objectTg.addChild(object);
        objectGroup = new BranchGroup();
        objectGroup.setCapability(BranchGroup.ALLOW_DETACH);
        objectGroup.addChild(objectTg);
        world.addChild(objectGroup);

        // klasa behavior która służy do obsługiwania klawiatury
        Moving moving = new Moving(robot);
        moving.setSchedulingBounds(bounds);
        world.addChild(moving);

        // klasa behavior która odświeża się 60 razy na sekundę i odpowiada ona np. za nagrywanie, odtworzenie nagrania
        // symulowanie grawitacji
        loop = new GameLoop(this, robot);
        loop.setSchedulingBounds(bounds);
        world.addChild(loop);

        // detekcja kolizji dla obiektu
        CollisionDetector collisionObject = new CollisionDetector(object.getShape(), new BoundingSphere(new Point3d(), 0.15), this, robot);
        world.addChild(collisionObject);

        scene.addChild(world);

        scene.compile();
        return scene;
    }

    private Texture createTexture(String path) {
        // Załadowanie tekstury
        TextureLoader loader = new TextureLoader(path, null);
        ImageComponent2D image = loader.getImage();

        if (image == null) {
            System.out.println("Nie udało się załadować tekstury: " + path);
        }

        Texture2D texture = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA, image.getWidth(), image.getHeight());

        texture.setImage(0, image);
        texture.setBoundaryModeS(Texture.WRAP);
        texture.setBoundaryModeT(Texture.WRAP);

        return texture;
    }

    public SimpleUniverse getSimpleU() {
        return simpleU;
    }

    private void makeWalls(float szerokoscScian){
        // metoda tworząca ściany
        Appearance appWall = new Appearance();
        appWall.setTexture(createTexture("grafika/walls.jpg"));

        // sciana 1
        TransformGroup wall1Tg = new TransformGroup();
        Shape3D wall1 = new MyShapes().makeGround(new Point3f(szerokoscScian, szerokoscScian, szerokoscScian),
                new Point3f(szerokoscScian, 0f, szerokoscScian), new Point3f(szerokoscScian, 0f, -szerokoscScian),
                new Point3f(szerokoscScian, szerokoscScian, -szerokoscScian));
        wall1.setAppearance(appWall);

        // sciana 2
        Transform3D transformWall2 = new Transform3D();
        transformWall2.setTranslation(new Vector3f(-2*szerokoscScian, 0f, 0f));
        TransformGroup wall2Tg = new TransformGroup(transformWall2);
        Shape3D wall2 = new MyShapes().makeGround(new Point3f(szerokoscScian, szerokoscScian, szerokoscScian),
                new Point3f(szerokoscScian, 0f, szerokoscScian), new Point3f(szerokoscScian, 0f, -szerokoscScian),
                new Point3f(szerokoscScian, szerokoscScian, -szerokoscScian));
        wall2.setAppearance(appWall);

        // sciana 3
        Transform3D transformWall3 = new Transform3D();
        transformWall3.rotY(Math.PI/2);
        TransformGroup wall3Tg = new TransformGroup(transformWall3);
        Shape3D wall3 = new MyShapes().makeGround(new Point3f(szerokoscScian, szerokoscScian, szerokoscScian),
                new Point3f(szerokoscScian, 0f, szerokoscScian), new Point3f(szerokoscScian, 0f, -szerokoscScian),
                new Point3f(szerokoscScian, szerokoscScian, -szerokoscScian));
        wall3.setAppearance(appWall);

        // sciana 4
        Transform3D transformWall4 = new Transform3D();
        transformWall4.rotY(-Math.PI/2);
        TransformGroup wall4Tg = new TransformGroup(transformWall4);
        Shape3D wall4 = new MyShapes().makeGround(new Point3f(szerokoscScian, szerokoscScian, szerokoscScian),
                new Point3f(szerokoscScian, 0f, szerokoscScian), new Point3f(szerokoscScian, 0f, -szerokoscScian),
                new Point3f(szerokoscScian, szerokoscScian, -szerokoscScian));
        wall4.setAppearance(appWall);

        // sufit
        Transform3D transformWall5 = new Transform3D();
        transformWall5.setTranslation(new Vector3f(0.0f, szerokoscScian, 0.0f));
        TransformGroup wall5Tg = new TransformGroup(transformWall5);
        Shape3D wall5 = new MyShapes().makeGround(new Point3f(szerokoscScian, 0f, szerokoscScian),
                new Point3f(szerokoscScian, 0f, -szerokoscScian), new Point3f(-szerokoscScian, 0f, -szerokoscScian),
                new Point3f(-szerokoscScian, 0f, szerokoscScian));
        wall5.setAppearance(appWall);

        wall1Tg.addChild(wall1);
        world.addChild(wall1Tg);
        wall2Tg.addChild(wall2);
        world.addChild(wall2Tg);
        wall3Tg.addChild(wall3);
        world.addChild(wall3Tg);
        wall4Tg.addChild(wall4);
        world.addChild(wall4Tg);
        wall5Tg.addChild(wall5);
        world.addChild(wall5Tg);
    }
}
