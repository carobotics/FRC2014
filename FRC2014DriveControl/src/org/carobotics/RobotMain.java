/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.carobotics;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SimpleRobot;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.camera.AxisCamera;

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
    Jaguar arms = new Jaguar(3);
    Joystick rightStick = new Joystick(1);
    Joystick leftStick = new Joystick(2);
    JoystickButton barmsUp = new JoystickButton(leftStick, 3);
    JoystickButton barmsDown = new JoystickButton(leftStick, 2);
    JoystickButton blaunch = new JoystickButton(rightStick, 2);
    JoystickButton bcompressOn = new JoystickButton(leftStick, 11);
    JoystickButton bcompressOff = new JoystickButton(leftStick, 10);
    JoystickButton bdriveNormal = new JoystickButton(leftStick, 6);
    JoystickButton bdriveReverse = new JoystickButton(leftStick, 7);
    Solenoid launcher = new Solenoid(1);
    Compressor compressor = new Compressor(1, 1);
    AxisCamera camera;
    boolean driveReverse = false;
    double rightDrive = 0.0;
    double leftDrive = 0.0;
    double driveSmoothVel = 0.1;
    
    public void robotInit() {
        camera = AxisCamera.getInstance();
        compressor.start();
    }

    public void autonomous() {
        System.out.println("Autonomous mode.");
        if (isAutonomous() && isEnabled()) {
            drive.arcadeDrive(0.5, 0.0);
            try {
                Thread.sleep(1 * 1000);
            } catch (InterruptedException e) {
                System.out.println("ERROR: Sleep was interrupted!");
            }
            drive.arcadeDrive(0.0, 0.0);
        }
    }

    public void operatorControl() {
        System.out.println("Operator control.");
        while (isOperatorControl() && isEnabled()) {
            
            //START: Main Driving
            if (bdriveNormal.get()) { driveReverse = false; System.out.println("Normal"); }
            if (bdriveReverse.get()) { driveReverse = true; System.out.println("Reverse"); }
            double mult = (rightStick.getThrottle() + 1) / 2;
            if (leftStick.getTrigger() || rightStick.getTrigger()) mult = 0.0;
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
            //drive reverse
            if (driveReverse) {
                double tmp = rightDrive;
                rightDrive = leftDrive * -1;
                leftDrive = tmp * -1;
            }
            drive.tankDrive(leftDrive * mult, rightDrive * mult);
            //END: Main Driving
            
            //START: Arm control
            double fullArm = 1.0;
            double armAmt = 0.0;
            if (barmsUp.get()) { armAmt = fullArm; }
            else if (barmsDown.get()) { armAmt = fullArm * -1; }
            arms.set(armAmt);
            //END: Arm control
            
            //START: Compresser/launching control
            if (bcompressOn.get() && !compressor.enabled()) { compressor.start(); }
            else if (bcompressOff.get() && compressor.enabled()) { compressor.stop(); }
            launcher.set(blaunch.get());
            //END: Compresser/launching control
            
            Timer.delay(0.005); //do not delete
        }
    }
}