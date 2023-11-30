# Java3D-RobotArm
Robot arm with 6 degrees of freedom programmed in Java using Java3D api

<b>I had a manipulator with six degrees of freedom to make.</b>
It was to resemble the arm shown in the picture below
<br>
![image](https://github.com/szymi999/Java3D-RobotArm/assets/52047025/3302995a-fff8-499f-93b7-076e5b104060)

In the project I created a world in the middle of which there is a hall and in it a designed manipulator. Next to it lies a ball that an arm can pick up and move. Everything looks as follows:
<br>
![image](https://github.com/szymi999/Java3D-RobotArm/assets/52047025/56f1e2c7-7f01-4098-95e1-7fda7065da18)

<b>What's in the program</b>
1. things visible in the picture
- The environment in which the robot is located
- The "Grab Object" button that allows us to grab the ball when the gripper of our manipulator touches it. BranchGroup with the ball removes from the scene and adds to our robot
- "Release object" button, making the robot let go of the ball (if it was holding it). BranchGroup with ball removes from our robot and adds to the scene
- "Start recording" and "Stop recording" buttons responsible for recording the manipulator's movement. When the first one is pressed in the GameLoop class, it writes the value of each angle to the board at a rate of 60 times per second. When the second button is pressed, it stops recording the angles. While recording, we can also catch, move and release the ball
- "Play recording" and "Stop playback" buttons responsible for motion playback. Motion playback is also done in the GameLoop class. After pressing the first one, the robot first moves to the initial position it had at the beginning of the recording and then plays back all the angle settings that were recorded. If we had an interaction with the ball during recording, then during playback the manipulator will want to play that, but if the ball is no longer in that
place then it will be written out in the console that there was no object to catch
- "Reset camera setting" button that sets the initial position of the camera, that is, as shown in the image above
2 Things you can't see:
- changed control (first I used KeyListener interface), I removed it and created new classes inheriting from Behavior - Moving and GameLoop. The first one handles the keyboard and into an array of Boolean variables (the elements of this array correspond to the given keys) writes the value true if the key is pressed or false when the key is released and not pressed. And the second class
- GameLoop refreshes 60 times per second and is responsible for performing these movements.
- The program includes collision detection, which is the CollisionDetector class inherited from Behavior. It detects a collision with the ball or with the ground. When there is a collision it blocks the movement of the manipulator in the direction in which it last moved

<b>Programme structure:</b>
<br>
![image](https://github.com/szymi999/Java3D-RobotArm/assets/52047025/eacacbda-15db-428a-8a5f-89fe4a98b2d4)
