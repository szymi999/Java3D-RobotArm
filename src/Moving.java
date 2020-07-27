// klasa obsługująca klawiaturę

import javax.media.j3d.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Enumeration;

public class Moving extends Behavior {

    private WakeupCriterion[] theCriteria;
    private WakeupOr oredCriteria;
    private Robot robot;

    public Moving(Robot robot){
        this.robot = robot;
    }

    public void initialize(){
        theCriteria = new WakeupCriterion[2];
        WakeupOnAWTEvent wakeupPress = new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED);
        WakeupOnAWTEvent wakeupRelease = new WakeupOnAWTEvent(KeyEvent.KEY_RELEASED);
        theCriteria[0] = wakeupPress;
        theCriteria[1] = wakeupRelease;
        oredCriteria = new WakeupOr(theCriteria);
        wakeupOn(oredCriteria);

    }

    public void processStimulus(Enumeration criteria) {
        WakeupOnAWTEvent ev;
        AWTEvent[] events;

        while (criteria.hasMoreElements()) {
            WakeupCriterion theCriterion = (WakeupCriterion) criteria.nextElement();
            if (theCriterion instanceof WakeupOnAWTEvent) {
                ev  = (WakeupOnAWTEvent) theCriterion;
                events = ev.getAWTEvent();
                processAWTEvent(events);
            }
        }
        wakeupOn(oredCriteria);

    }

    private void processAWTEvent(AWTEvent[] events){
        // sprawdzamy które przyciski są trzymane a które puszczone i nadajemy wartość true lub false
        // do tablicy boolowskiej, dzięki takiemu sposobowi możemy wykonywać dwa ruchy jednocześnie
        for(int n=0; n<events.length; n++){
            if(events[n] instanceof KeyEvent){
                KeyEvent key = (KeyEvent) events[n];

                if(key.getID() == KeyEvent.KEY_PRESSED){
                    int keyCode = key.getKeyCode();

                    robot.key[keyCode] = true;

                } else if(key.getID() == KeyEvent.KEY_RELEASED){
                    int keyCode = key.getKeyCode();

                    robot.key[keyCode] = false;
                }
            }
        }
    }
}

