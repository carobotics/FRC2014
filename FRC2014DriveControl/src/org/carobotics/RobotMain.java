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
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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
    boolean oneStickDrive = false;
    String oneStickDriveKey = "OneStickDrive";
    
    protected void robotInit() {
        super.robotInit();
        SmartDashboard.putBoolean(oneStickDriveKey, false);
    }

    public void autonomous() {
        //temporary randomness
        for (int i = 0; i < 4; i++) {
            drive.drive(0.5, 0.0); //.drive(percent_forward, percent_turn)
            Timer.delay(0.5);
            drive.drive(-0.5, 0.0);
            Timer.delay(0.5);
        }
        drive.drive(0.0, 0.0);
    }

    public void operatorControl() {
        while (isOperatorControl() && isEnabled()) {
            try {
                oneStickDrive = SmartDashboard.getBoolean(oneStickDriveKey);
                //oneStickDrive = prefs.getString("OneStickDrive", "false").equalsIgnoreCase("true");
            } catch (Throwable t) {
                t.printStackTrace();
            }
            oneStickDrive = leftStick.getThrottle() > 0.5;
            //drive.arcadeDrive(stick);
            double mult = 1.0;
            if(rightStick.getTrigger() || leftStick.getTrigger()) { mult = 0.5; }
            if (oneStickDrive) {
                double turnAmt = leftStick.getZ();
                if (Math.abs(leftStick.getX()) > Math.abs(turnAmt)) turnAmt = leftStick.getX();
                drive.drive(leftStick.getY() * mult, turnAmt * -1);
            } else {
                drive.tankDrive(leftStick.getY() * mult, rightStick.getY() * mult);
            }
            Timer.delay(0.005); //do not delete
        }
    }
}