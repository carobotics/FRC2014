/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.carobotics;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SimpleRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.camera.AxisCamera;
import edu.wpi.first.wpilibj.image.BinaryImage;
import edu.wpi.first.wpilibj.image.ColorImage;
import edu.wpi.first.wpilibj.image.EllipseMatch;
import edu.wpi.first.wpilibj.image.EllipseDescriptor;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the SimpleRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */

public class RobotMain extends SimpleRobot {
    
    public static final double WIDTH = 320;
    public static final double HEIGHT = 240;

    RobotDrive drive = new RobotDrive(1, 2);
    RobotDrive arms = new RobotDrive(3, 4);
    Joystick rightStick = new Joystick(1);
    Joystick leftStick = new Joystick(2);
    JoystickButton armsUp = new JoystickButton(leftStick, 3);
    JoystickButton armsDown = new JoystickButton(leftStick, 2);
    JoystickButton leftUp = new JoystickButton(rightStick, 5);
    JoystickButton leftDown = new JoystickButton(rightStick, 3);
    JoystickButton rightUp = new JoystickButton(rightStick, 6);
    JoystickButton rightDown = new JoystickButton(rightStick, 4);
    JoystickButton launch = new JoystickButton(rightStick, 2);
    JoystickButton compressOn = new JoystickButton(leftStick, 11);
    JoystickButton compressOff = new JoystickButton(leftStick, 10);
    boolean compresser = false;
    Relay relay = new Relay(1);
    AxisCamera camera;
    
    public void robotInit() {
        camera = AxisCamera.getInstance();
    }

    public void autonomous() {
        System.out.println("Autonomous mode.");
        while (isAutonomous() && isEnabled()) {
            SmartDashboard.putString("Name", "GLaDOS");
            try {
                ColorImage image = camera.getImage();
                //BinaryImage thresholdImage = image.thresholdRGB(25, 255, 0, 47, 0, 47);   // keep only red objects
                BinaryImage thresholdImage = image.thresholdRGB(0, 60, 0, 60, 10, 255);   // keep only red objects
                BinaryImage bigObjectsImage = thresholdImage.removeSmallObjects(false, 2);// remove small artifacts
                BinaryImage convexHullImage = bigObjectsImage.convexHull(false);          // fill in occluded rectangles

                double min = HEIGHT / 20.0;
                double max = HEIGHT;
                EllipseMatch[] matches = convexHullImage.detectEllipses(new EllipseDescriptor(min, max, min, max));
                double midX = WIDTH / 2.0;
                double midY = HEIGHT / 2.0;
                if (matches.length > 0) {
                EllipseMatch ball = matches[0];
                    for (int i = 1; i < matches.length; i++) {
                        EllipseMatch myBall = matches[i];
                        if (myBall.m_majorRadius > ball.m_majorRadius) ball = myBall;
                    }
                    double x = ball.m_xPos - midX;
                    double y = ball.m_yPos - midY;
                    System.out.println("x: " + x + ", y: " + y);
                } else {
                    System.out.println("No balls found!");
                }

                convexHullImage.free();
                bigObjectsImage.free();
                thresholdImage.free();
                image.free();

        //            } catch (AxisCameraException ex) {        // this is needed if the camera.getImage() is called
        //                ex.printStackTrace();
            } catch (Throwable e) {
                System.out.println("Error!");
                e.printStackTrace();
            }
        }
    }

    public void operatorControl() {
        System.out.println("Operator control.");
        while (isOperatorControl() && isEnabled()) {
            
            //main driving
            double mult = rightStick.getThrottle();
            if (leftStick.getTrigger() || rightStick.getTrigger()) mult = 0.5;
            double rightDrive = rightStick.getY();
            double leftDrive = leftStick.getY();
            if (leftStick.getThrottle() < 0.5) {
                double tmp = rightDrive;
                rightDrive = leftDrive * -1;
                leftDrive = tmp * -1;
            }
            drive.tankDrive(rightDrive * mult, leftDrive * mult);
            
            //arm control
            double fullArm = 1.0;
            double leftArm = 0.0;
            double rightArm = 0.0;
            if (armsUp.get()) { leftArm = rightArm = fullArm; }
            else if (armsDown.get()) { leftArm = rightArm = fullArm * -1; }
            else {
                if (leftUp.get()) leftArm = fullArm;
                if (leftDown.get()) leftArm = fullArm * -1;
                if (rightUp.get()) rightArm = fullArm;
                if (rightDown.get()) rightArm = fullArm * -1;
            }
            arms.setLeftRightMotorOutputs(leftArm, rightArm);
            
            //compresser/launching control
            if (compressOn.get()) { compresser = true; }
            else if (compressOff.get()) { compresser = false; }
            boolean forward = launch.get();
            boolean backwards = compresser;
            Relay.Value value = Relay.Value.kOff;
            if (forward && backwards) {
                value = Relay.Value.kOn;
            } else if (forward) {
                value = Relay.Value.kForward;
            } else if (backwards) {
                value = Relay.Value.kReverse;
            }
            relay.set(value);
            
            Timer.delay(0.005); //do not delete
        }
    }
}