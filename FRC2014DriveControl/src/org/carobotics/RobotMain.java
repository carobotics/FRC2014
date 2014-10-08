/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.carobotics;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SimpleRobot;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.camera.AxisCamera;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.util.Date;

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
    Victor arm1 = new Victor(3);
    Victor arm2 = new Victor(4);
    Joystick rightStick = new Joystick(1);
    Joystick leftStick = new Joystick(2);
    JoystickButton barmsUp = new JoystickButton(leftStick, 3);
    JoystickButton barmsDown = new JoystickButton(leftStick, 2);
    JoystickButton blaunch = new JoystickButton(rightStick, 2);
    JoystickButton bcompressOn = new JoystickButton(leftStick, 11);
    JoystickButton bcompressOff = new JoystickButton(leftStick, 10);
    JoystickButton bdriveReverseToggle = new JoystickButton(leftStick, 6);
    boolean previousDriveReverseToggle = false;
    boolean launching = false;
    long launchTime = 0;
    boolean resetting = false;
    Solenoid resetter = new Solenoid(1);
    Solenoid launcher = new Solenoid(2);
    Compressor compressor = new Compressor(1, 1);
    AxisCamera camera;
    boolean driveReverse = false;
    double rightDrive = 0.0;
    double leftDrive = 0.0;
    double driveSmoothVel = 0.05;
    double drivingMult = 1.0;
    long dashValUpdateTime = 0;
    
    public void print(String msg) {
        System.out.println(msg);
    }
    
    public void robotInit() {
        camera = AxisCamera.getInstance();
        compressor.start();
        print("Initializing...");
    }

    public void autonomous() {
        System.out.println("Autonomous mode.");
        if (isAutonomous() && isEnabled()) {
            long startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() < startTime + 2000) {
                drive.tankDrive(-0.75, -0.75);
            }
            drive.tankDrive(0, 0);
        }
    }

    public void operatorControl() { 
        System.out.println("Operator control.");
        SmartDashboard.putBoolean("Compressor", true);
        SmartDashboard.putNumber("DrivingMultiplier", 1.0);
        SmartDashboard.putNumber("DriveAcceleration", 0.05);
        SmartDashboard.putBoolean("IsFiring", false);
        SmartDashboard.putBoolean("IsResetting", false);
        while (isOperatorControl() && isEnabled()) {
            long curTime = new Date().getTime();
            
            if (curTime > dashValUpdateTime + 1000) {
                drivingMult = SmartDashboard.getNumber("DrivingMultiplier");
                driveSmoothVel = SmartDashboard.getNumber("DriveAcceleration");
            } else {
                dashValUpdateTime = curTime;
            }
            
            //START: Main Driving
            //reverse toggling
            boolean reverseToggle = bdriveReverseToggle.get();
            if (reverseToggle && !previousDriveReverseToggle) {
                driveReverse = !driveReverse;
                System.out.println("Reverse toggled to: " + reverseToggle);
            }
            previousDriveReverseToggle = reverseToggle;
            //main driving
            double rightVal = rightStick.getY();
            double leftVal = leftStick.getY();
            //acceleration smoothing
            double rightDif = rightVal - rightDrive;
            double leftDif = leftVal - leftDrive;
            if (Math.abs(rightDif) > driveSmoothVel) {
                int rightDir = (int) (rightDif / Math.abs(rightDif));
                rightDrive += driveSmoothVel * rightDir;
            }
            if (Math.abs(leftDif) > driveSmoothVel) {
                int leftDir = (int) (leftDif / Math.abs(leftDif));
                leftDrive += driveSmoothVel * leftDir;
            }
            if (!driveReverse)
                drive.tankDrive(rightDrive * drivingMult, leftDrive * drivingMult);
            else
                drive.tankDrive(-leftDrive * drivingMult, -rightDrive * drivingMult);
            //END: Main Driving
            
            //START: Arm control
            double fullArm = 0.851;
            double armMult = ((rightStick.getThrottle() * -1) + 1) / 2;
            double armAmt = 0.0;
            if (barmsUp.get()) { armAmt = fullArm * -1 * armMult; }
            else if (barmsDown.get()) { armAmt = fullArm * armMult; }
            arm1.set(armAmt);
            arm2.set(armAmt);
            //END: Arm control
            
            //START: Compressor control
            if (bcompressOn.get() && !compressor.enabled()) { compressor.start(); SmartDashboard.putBoolean("Compressor", true);}
            else if (bcompressOff.get() && compressor.enabled()) { compressor.stop(); SmartDashboard.putBoolean("Compressor", false);}
            //END: Compressor control
            
            //START: Solenoid control
            if (launching) {
                if (curTime >= launchTime + 2000) {
                    launching = false;
                    resetting = true;
                    SmartDashboard.putBoolean("IsFiring", false);
                    SmartDashboard.putBoolean("IsResetting", true);
                } else if (curTime >= launchTime + 1000) {
                    resetter.set(false);
                    launcher.set(false);
                } else {
                    resetter.set(false);
                    launcher.set(true);
                }
            } else if (resetting) {
                if (curTime >= launchTime + 3000) {
                    launcher.set(false);
                    resetter.set(false);
                    resetting = false;
                    SmartDashboard.putBoolean("IsResetting", false);
                } else {
                    launcher.set(false);
                    resetter.set(true);
                }
            } else {
                if (blaunch.get()) {
                    launching = true;
                    launchTime = new Date().getTime();
                    SmartDashboard.putBoolean("IsFiring", true);
                } else {
                    resetter.set(false);
                    launcher.set(false);
                }
            }
            //END: Solenoid control
            
            Timer.delay(0.005); //do not delete
        }
    }
}
