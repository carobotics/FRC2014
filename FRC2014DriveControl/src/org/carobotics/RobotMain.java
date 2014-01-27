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

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the SimpleRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class RobotMain extends SimpleRobot {

    RobotDrive drive = new RobotDrive(1, 2);
    Joystick rightStick = new Joystick(1);
    Joystick leftStick = new Joystick(2);

    public void autonomous() {
    }

    public void operatorControl() {
        while (isOperatorControl() && isEnabled()) {
            double mult = rightStick.getThrottle();
            drive.tankDrive(rightStick.getY() * mult, leftStick.getY() * mult);
            Timer.delay(0.005); //do not delete
        }
    }
}