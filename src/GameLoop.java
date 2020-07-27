// ta klasa odświeża się 60 razy na sekundę i ona jest odpowiedzialna od ruszania się robota, nagrywania ruchu,
// odtwarzania, grawitacji, dźwięków

import javax.media.j3d.*;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.vecmath.Matrix3d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Enumeration;

public class GameLoop extends Behavior {

    private WakeupOr oredCriteria;
    private World world;
    private Robot robot;

    // dzwieki
    private File dzwiek1;
    private File dzwiek2;
    private File dzwiek3;

    // tablica do przetrzymywania kątów robota podczas nagrywania ruchów
    private float angles[][];

    // ile sekund maksymalnie mozna nagrywac
    private int recordingLimit = 60;

    // zmienna w której bedzie różnica kątów aktualnego położenia manipulatora i położenia początkowego
    // podczas nagrywania
    private float[] differenceInAngles = new float[6];
    // ile sekund trwa ustawianie położenia manipulatora do odtworzenia nagrania
    private int timeOfSetting = 120;

    // zmienna która pozwala nam policzyć różnice kątów tylko w pierwszej iteracji
    public boolean countDifference = false;

    // zmienne mówiące w której klatce złapaliśmy obiekt i puściliśmy
    public int whenCatch = -1;
    public int whenLetGo = -1;

    public GameLoop(World world, Robot robot){
        this.world = world;
        this.robot = robot;
        angles = new float[60*recordingLimit][6];
        dzwiek1 = new File("sounds/duzy_dzwiek.wav");
        dzwiek2 = new File("sounds/sredni_dzwiek.wav");
        dzwiek3 = new File("sounds/maly_dzwiek.wav");
    }

    // funkcja do odtwarzania dźwięków przy ruchu robota
    static void PlaySound(File Sound){
        try{
            Clip clip= AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(Sound));
            clip.start();
        }catch (Exception e){}
    }

    public void initialize(){
        WakeupCriterion[] theCriteria = new WakeupCriterion[1];
        theCriteria[0] = new WakeupOnElapsedTime(1000/60);
        oredCriteria = new WakeupOr(theCriteria);
        wakeupOn(oredCriteria);
    }

    public void processStimulus(Enumeration criteria) {
        while (criteria.hasMoreElements()) {
            WakeupCriterion theCriterion = (WakeupCriterion) criteria.nextElement();
            if (theCriterion instanceof WakeupOnElapsedTime) {
                robot.handPos();
                if(world.objPos.y > 0.16f && !world.objHold) {
                    world.objPos.y -= 0.01f;
                    world.objectTransform.setTranslation(world.objPos);
                    world.objectTg.setTransform(world.objectTransform);
                }
                if(!world.recordingPlay) {
                    move();
                }
                if(world.recording){
                    if(world.recordingCount == 0){
                        whenCatch = -1;
                        whenLetGo = -1;
                    }
                    for(int i=0; i<6; i++){
                        angles[world.recordingCount][i] = robot.angles[i];
                    }
                    world.recordingCount++;
                    if(world.recordingCount == recordingLimit*60){
                        world.recording = false;
                        System.out.println("Koniec nagrwania");

                    }
                }
                if(world.recordingPlay){
                    if(countDifference){
                        differenceInAngles[0] = (robot.angles[0] - angles[0][0]) / timeOfSetting;
                        differenceInAngles[1] = (robot.angles[1] - angles[0][1]) / timeOfSetting;
                        differenceInAngles[2] = (robot.angles[2] - angles[0][2]) / timeOfSetting;
                        differenceInAngles[3] = (robot.angles[3] - angles[0][3]) / timeOfSetting;
                        differenceInAngles[4] = (robot.angles[4] - angles[0][4]) / timeOfSetting;
                        differenceInAngles[5] = (robot.angles[5] - angles[0][5]) / timeOfSetting;
                        world.setting = true;
                        countDifference = false;
                    }
                    else if(world.setting) {
                        startingPosition();
                        world.settingCount++;
                        if(world.settingCount == timeOfSetting){
                            world.setting = false;
                        }
                    }
                    else if(world.recordingPlay){
                        if(world.playingRecordCount == 0){
                            System.out.println("Odtwarzanie nagrania");
                        }
                        if(world.playingRecordCount == whenCatch && world.objColl){
                            world.objectGroup.detach();
                            world.objectTransform.setTranslation(new Vector3f(0.0f, 0.25f, 0.0f));
                            world.objectTg.setTransform(world.objectTransform);
                            robot.tgArm[5].addChild(world.objectGroup);
                            world.objHold = true;
                        }
                        else if(world.playingRecordCount == whenCatch){
                            System.out.println("Nie bylo obiektu do zlapania");
                        }
                        if(world.playingRecordCount == whenLetGo && world.objHold){
                            world.objectGroup.detach();
                            world.objPos = robot.handPos();
                            world.objectTransform.setTranslation(world.objPos);
                            world.objectTg.setTransform(world.objectTransform);

                            CollisionDetector collisionObject = new CollisionDetector(world.object.getShape(), new BoundingSphere(new Point3d(), 0.15), world, robot);
                            BranchGroup tmpCollGroup = new BranchGroup();
                            tmpCollGroup.addChild(collisionObject);
                            world.world.addChild(tmpCollGroup);

                            world.world.addChild(world.objectGroup);
                            world.objHold = false;
                        }
                        else if(world.playingRecordCount == whenLetGo){
                            System.out.println("Nie bylo obiektu do puszczenia");
                        }
                        playRecord(world.playingRecordCount);
                        world.playingRecordCount++;
                        if (world.playingRecordCount == world.recordingCount) {
                            world.recordingPlay = false;
                            System.out.println("koniec odtwarzania");
                        }
                    }
                }
            }
        }
        wakeupOn(oredCriteria);
    }

    private void move(){
        // metoda która porusza manipulatorem
        if (robot.key[KeyEvent.VK_A] && !robot.notAllow[0]) {
            robot.angles[0] += Math.PI / robot.robotSpeed;
            robot.transformArm[0].setRotation(new Matrix3d(Math.cos(robot.angles[0]), 0, Math.sin(robot.angles[0]),
                    0, 1, 0,
                    -Math.sin(robot.angles[0]), 0, Math.cos(robot.angles[0])));
            robot.tgArm[0].setTransform(robot.transformArm[0]);
            // PlaySoun(dzwiek1);
            robot.lastMove = 'a';
        }
        if (robot.key[KeyEvent.VK_D] && !robot.notAllow[1]) {
            robot.angles[0] -= Math.PI / robot.robotSpeed;
            robot.transformArm[0].setRotation(new Matrix3d(Math.cos(robot.angles[0]), 0, Math.sin(robot.angles[0]),
                    0, 1, 0,
                    -Math.sin(robot.angles[0]), 0, Math.cos(robot.angles[0])));
            robot.tgArm[0].setTransform(robot.transformArm[0]);
            // PlaySound(dzwiek1);
            robot.lastMove = 'd';
        }
        // sterowanie położeniem barku
        if (robot.key[KeyEvent.VK_W] && !robot.notAllow[2]) {
            robot.angles[1] += Math.PI / robot.robotSpeed;
            if (robot.angles[1] > Math.PI / 6) robot.angles[1] = (float) (30 * Math.PI / 180);
            else{
                robot.transformJoint[0].setRotation(new Matrix3d(Math.cos(robot.angles[1]), -Math.sin(robot.angles[1]), 0,
                        Math.sin(robot.angles[1]), Math.cos(robot.angles[1]), 0,
                        0, 0, 1));
                robot.tgJoint[0].setTransform(robot.transformJoint[0]);
            }
            // PlaySound(dzwiek1);
            robot.lastMove = 'w';
        }
        if (robot.key[KeyEvent.VK_S] && !robot.notAllow[3]) {
            robot.angles[1] -= Math.PI / robot.robotSpeed;
            if (robot.angles[1] < -Math.PI / 2) robot.angles[1] = (float) (-Math.PI / 2);
            else {
                robot.transformJoint[0].setRotation(new Matrix3d(Math.cos(robot.angles[1]), -Math.sin(robot.angles[1]), 0,
                        Math.sin(robot.angles[1]), Math.cos(robot.angles[1]), 0,
                        0, 0, 1));
                robot.tgJoint[0].setTransform(robot.transformJoint[0]);
            }
            // PlaySound(dzwiek1);
            robot.lastMove = 's';
        }
        // sterowanie położeniem łokcia
        if (robot.key[KeyEvent.VK_Q] && !robot.notAllow[4]) {
            robot.angles[2] += Math.PI / robot.robotSpeed;
            if(robot.angles[2]>0) robot.angles[2]=0;
            else {
                robot.transformJoint[1].setRotation(new Matrix3d(Math.cos(robot.angles[2]), -Math.sin(robot.angles[2]), 0,
                        Math.sin(robot.angles[2]), Math.cos(robot.angles[2]), 0,
                        0, 0, 1));
                robot.tgJoint[1].setTransform(robot.transformJoint[1]);
            }
            // PlaySound(dzwiek2);
            robot.lastMove = 'q';
        }
        if (robot.key[KeyEvent.VK_E] && !robot.notAllow[5]) {
            robot.angles[2] -= Math.PI / robot.robotSpeed;
            if(robot.angles[2]<-105*Math.PI/180) robot.angles[2]=(float)(-105*Math.PI/180);
            else {
                robot.transformJoint[1].setRotation(new Matrix3d(Math.cos(robot.angles[2]), -Math.sin(robot.angles[2]), 0,
                        Math.sin(robot.angles[2]), Math.cos(robot.angles[2]), 0,
                        0, 0, 1));
                robot.tgJoint[1].setTransform(robot.transformJoint[1]);
            }
            // PlaySound(dzwiek2);
            robot.lastMove = 'e';
        }
        // sterowaniem obrotem roll
        if (robot.key[KeyEvent.VK_U] && !robot.notAllow[6]) {
            robot.angles[3] -= Math.PI / robot.robotSpeed;
            robot.transformArm[3].setRotation(new Matrix3d(Math.cos(robot.angles[3]), 0, Math.sin(robot.angles[3]),
                    0, 1, 0,
                    -Math.sin(robot.angles[3]), 0, Math.cos(robot.angles[3])));
            robot.tgArm[3].setTransform(robot.transformArm[3]);
            // PlaySound(dzwiek2);
            robot.lastMove = 'u';
        }
        if (robot.key[KeyEvent.VK_O] && !robot.notAllow[7]) {
            robot.angles[3] += Math.PI / robot.robotSpeed;
            robot.transformArm[3].setRotation(new Matrix3d(Math.cos(robot.angles[3]), 0, Math.sin(robot.angles[3]),
                    0, 1, 0,
                    -Math.sin(robot.angles[3]), 0, Math.cos(robot.angles[3])));
            robot.tgArm[3].setTransform(robot.transformArm[3]);
            // PlaySound(dzwiek2);
            robot.lastMove = 'o';
        }
        // sterowanie pitch
        if (robot.key[KeyEvent.VK_I] && !robot.notAllow[8]) {
            robot.angles[4] += Math.PI / robot.robotSpeed;
            if(robot.angles[4]>Math.PI/2) robot.angles[4]=(float)(Math.PI/2);
            else {
                robot.transformArm[4].setRotation(new Matrix3d(Math.cos(robot.angles[4]), -Math.sin(robot.angles[4]), 0,
                        Math.sin(robot.angles[4]), Math.cos(robot.angles[4]), 0,
                        0, 0, 1));
                robot.tgArm[4].setTransform(robot.transformArm[4]);
            }
            // PlaySound(dzwiek3);
            robot.lastMove = 'i';
        }
        if (robot.key[KeyEvent.VK_K] && !robot.notAllow[9]) {
            robot.angles[4] -= Math.PI / robot.robotSpeed;
            if(robot.angles[4]<-Math.PI/2) robot.angles[4]=(float)(-Math.PI/2);
            else {
                robot.transformArm[4].setRotation(new Matrix3d(Math.cos(robot.angles[4]), -Math.sin(robot.angles[4]), 0,
                        Math.sin(robot.angles[4]), Math.cos(robot.angles[4]), 0,
                        0, 0, 1));
                robot.tgArm[4].setTransform(robot.transformArm[4]);
            }
            // PlaySound(dzwiek3);
            robot.lastMove = 'k';
        }
        // sterowanie yaw
        if (robot.key[KeyEvent.VK_J] && !robot.notAllow[10]) {
            robot.angles[5] -= Math.PI / robot.robotSpeed;
            if(robot.angles[5]<-Math.PI/4) robot.angles[5]=(float)(-Math.PI/4);
            else {
                robot.transformJoint[2].setRotation(new Matrix3d(1, 0, 0,
                        0, Math.cos(robot.angles[5]), -Math.sin(robot.angles[5]),
                        0, Math.sin(robot.angles[5]), Math.cos(robot.angles[5])));
                robot.tgJoint[2].setTransform(robot.transformJoint[2]);
            }
            // PlaySound(dzwiek3);
            robot.lastMove = 'j';
        }
        if (robot.key[KeyEvent.VK_L] && !robot.notAllow[11]) {
            robot.angles[5] += Math.PI / robot.robotSpeed;
            if(robot.angles[5]>Math.PI/4)robot.angles[5]=(float)(Math.PI/4);
            else {
                robot.transformJoint[2].setRotation(new Matrix3d(1, 0, 0,
                        0, Math.cos(robot.angles[5]), -Math.sin(robot.angles[5]),
                        0, Math.sin(robot.angles[5]), Math.cos(robot.angles[5])));
                robot.tgJoint[2].setTransform(robot.transformJoint[2]);
            }
            // PlaySound(dzwiek3);
            robot.lastMove = 'l';
        }
    }

    private void playRecord(int n){
        // metoda odtwarzająca nagrany ruch
        robot.angles[0] = angles[n][0];
        robot.transformArm[0].setRotation(new Matrix3d(Math.cos(robot.angles[0]), 0, Math.sin(robot.angles[0]),
                                                            0, 1, 0,
                                                                -Math.sin(robot.angles[0]), 0, Math.cos(robot.angles[0])));
        robot.tgArm[0].setTransform(robot.transformArm[0]);

        robot.angles[1] = angles[n][1];
        robot.transformJoint[0].setRotation(new Matrix3d(Math.cos(robot.angles[1]), -Math.sin(robot.angles[1]), 0,
                                                         Math.sin(robot.angles[1]), Math.cos(robot.angles[1]), 0,
                                                         0, 0, 1));
        robot.tgJoint[0].setTransform(robot.transformJoint[0]);

        robot.angles[2] = angles[n][2];
        robot.transformJoint[1].setRotation(new Matrix3d(Math.cos(robot.angles[2]), -Math.sin(robot.angles[2]), 0,
                                                         Math.sin(robot.angles[2]), Math.cos(robot.angles[2]), 0,
                                                        0, 0, 1));
        robot.tgJoint[1].setTransform(robot.transformJoint[1]);

        robot.angles[3] = angles[n][3];
        robot.transformArm[3].setRotation(new Matrix3d(Math.cos(robot.angles[3]), 0, Math.sin(robot.angles[3]),
                                                                0, 1, 0,
                                                                -Math.sin(robot.angles[3]), 0, Math.cos(robot.angles[3])));
        robot.tgArm[3].setTransform(robot.transformArm[3]);

        robot.angles[4] = angles[n][4];
        robot.transformArm[4].setRotation(new Matrix3d(Math.cos(robot.angles[4]), -Math.sin(robot.angles[4]), 0,
                                                       Math.sin(robot.angles[4]), Math.cos(robot.angles[4]), 0,
                                                   0, 0, 1));
        robot.tgArm[4].setTransform(robot.transformArm[4]);

        robot.angles[5] = angles[n][5];
        robot.transformJoint[2].setRotation(new Matrix3d(1, 0, 0,
                                                         0, Math.cos(robot.angles[5]), -Math.sin(robot.angles[5]),
                                                         0, Math.sin(robot.angles[5]), Math.cos(robot.angles[5])));
        robot.tgJoint[2].setTransform(robot.transformJoint[2]);
    }

    private void startingPosition(){
        // metoda ustawiająca położenie manipulatora do początkowego przy nagrywaniu
        robot.angles[0] -= differenceInAngles[0];
        robot.transformArm[0].setRotation(new Matrix3d(Math.cos(robot.angles[0]), 0, Math.sin(robot.angles[0]),
                0, 1, 0,
                -Math.sin(robot.angles[0]), 0, Math.cos(robot.angles[0])));
        robot.tgArm[0].setTransform(robot.transformArm[0]);

        robot.angles[1] -= differenceInAngles[1];
        robot.transformJoint[0].setRotation(new Matrix3d(Math.cos(robot.angles[1]), -Math.sin(robot.angles[1]), 0,
                Math.sin(robot.angles[1]), Math.cos(robot.angles[1]), 0,
                0, 0, 1));
        robot.tgJoint[0].setTransform(robot.transformJoint[0]);

        robot.angles[2] -= differenceInAngles[2];
        robot.transformJoint[1].setRotation(new Matrix3d(Math.cos(robot.angles[2]), -Math.sin(robot.angles[2]), 0,
                Math.sin(robot.angles[2]), Math.cos(robot.angles[2]), 0,
                0, 0, 1));
        robot.tgJoint[1].setTransform(robot.transformJoint[1]);

        robot.angles[3] -= differenceInAngles[3];
        robot.transformArm[3].setRotation(new Matrix3d(Math.cos(robot.angles[3]), 0, Math.sin(robot.angles[3]),
                0, 1, 0,
                -Math.sin(robot.angles[3]), 0, Math.cos(robot.angles[3])));
        robot.tgArm[3].setTransform(robot.transformArm[3]);

        robot.angles[4] -= differenceInAngles[4];
        robot.transformArm[4].setRotation(new Matrix3d(Math.cos(robot.angles[4]), -Math.sin(robot.angles[4]), 0,
                Math.sin(robot.angles[4]), Math.cos(robot.angles[4]), 0,
                0, 0, 1));
        robot.tgArm[4].setTransform(robot.transformArm[4]);

        robot.angles[5] -= differenceInAngles[5];
        robot.transformJoint[2].setRotation(new Matrix3d(1, 0, 0,
                0, Math.cos(robot.angles[5]), -Math.sin(robot.angles[5]),
                0, Math.sin(robot.angles[5]), Math.cos(robot.angles[5])));
        robot.tgJoint[2].setTransform(robot.transformJoint[2]);
    }
}
