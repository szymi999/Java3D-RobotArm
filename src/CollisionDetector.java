// klasa wykrywająca kolizje

import javax.media.j3d.*;
import java.util.Enumeration;

public class CollisionDetector extends Behavior {
    private Shape3D collidingShape;

    private WakeupCriterion[] theCriteria;

    private WakeupOr oredCriteria;

    private World world;

    private Robot robot;

    public CollisionDetector(Shape3D theShape, Bounds theBounds, World world, Robot robot){
        collidingShape = theShape;
        setSchedulingBounds(theBounds);
        this.world = world;
        this.robot = robot;
    }

    public void initialize(){
        theCriteria = new WakeupCriterion[3];
        WakeupOnCollisionEntry startsCollision = new WakeupOnCollisionEntry(collidingShape);
        WakeupOnCollisionExit endsCollision = new WakeupOnCollisionExit(collidingShape);
        WakeupOnCollisionMovement moveCollision = new WakeupOnCollisionMovement(collidingShape);
        theCriteria[0] = startsCollision;
        theCriteria[1] = endsCollision;
        theCriteria[2] = moveCollision;
        oredCriteria = new WakeupOr(theCriteria);
        wakeupOn(oredCriteria);
    }

    public void processStimulus(Enumeration criteria) {
        while (criteria.hasMoreElements()) {
            WakeupCriterion theCriterion = (WakeupCriterion) criteria.nextElement();
            if(theCriterion instanceof WakeupOnCollisionEntry){
                toNotAllow();
            } else if(theCriterion instanceof WakeupOnCollisionExit){
                toAllow();
            }
            wakeupOn(oredCriteria);
        }
    }

    private void toNotAllow(){
        // jeśli wystąpiła kolizja to blokujemy ruch w tym kierunku
        if(robot.lastMove == 'a'){
            robot.notAllow[0] = true;
        }
        if(robot.lastMove == 'd'){
            robot.notAllow[1] = true;
        }
        if(robot.lastMove == 'w'){
            robot.notAllow[2] = true;
        }
        if(robot.lastMove == 's'){
            robot.notAllow[3] = true;
        }
        if(robot.lastMove == 'q'){
            robot.notAllow[4] = true;
        }
        if(robot.lastMove == 'e'){
            robot.notAllow[5] = true;
        }
        if(robot.lastMove == 'u'){
            robot.notAllow[6] = true;
        }
        if(robot.lastMove == 'o'){
            robot.notAllow[7] = true;
        }
        if(robot.lastMove == 'i'){
            robot.notAllow[8] = true;
        }
        if(robot.lastMove == 'k'){
            robot.notAllow[9] = true;
        }
        if(robot.lastMove == 'j'){
            robot.notAllow[10] = true;
        }
        if(robot.lastMove == 'l'){
            robot.notAllow[11] = true;
        }
        if(collidingShape.getUserData() != "object"){
            world.objColl = true;
        }
    }

    private void toAllow(){
        // jesli koniec kolizji to odblokowujemy wszystkie ruchy
        for(int i=0; i<12; i++){
            robot.notAllow[i] = false;
        }
        if(collidingShape.getUserData() != "object"){
            world.objColl = false;
        }
    }
}