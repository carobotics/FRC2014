/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.carobotics;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SimpleRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.camera.AxisCamera;
import edu.wpi.first.wpilibj.image.BinaryImage;
import edu.wpi.first.wpilibj.image.ColorImage;
import edu.wpi.first.wpilibj.image.EllipseMatch;
import edu.wpi.first.wpilibj.image.EllipseDescriptor;

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
    Joystick rightStick = new Joystick(1);
    Joystick leftStick = new Joystick(2);
    AxisCamera camera;
    
    public void robotInit() {
        camera = AxisCamera.getInstance();
    }

    public void autonomous() {
        while (isAutonomous() && isEnabled()) {
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
        while (isOperatorControl() && isEnabled()) {
            System.out.println("Operator control.");
            double mult = rightStick.getThrottle();
            drive.tankDrive(rightStick.getY() * mult, leftStick.getY() * mult);
            Timer.delay(0.005); //do not delete
        }
    }
}