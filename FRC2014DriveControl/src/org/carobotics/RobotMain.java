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
    
    public void robotInit() {
        camera = AxisCamera.getInstance();
        compressor.start();
    }

    public void autonomous() {
        System.out.println("Autonomous mode.");
        while (isAutonomous() && isEnabled()) {
            
        }
    }

    public void operatorControl() {
        System.out.println("Operator control.");
        while (isOperatorControl() && isEnabled()) {
            
            //main driving
            if (bdriveNormal.get()) { driveReverse = false; System.out.println("Normal"); }
            if (bdriveReverse.get()) { driveReverse = true; System.out.println("Reverse"); }
            double mult = rightStick.getThrottle();
            if (leftStick.getTrigger() || rightStick.getTrigger()) mult = 0.5;
            double rightDrive = rightStick.getY() * -1;
            double leftDrive = leftStick.getY() * -1;
            if (driveReverse) {
                
                double tmp = rightDrive;
                rightDrive = leftDrive * -1;
                leftDrive = tmp * -1;
            }
            drive.tankDrive(rightDrive * mult, leftDrive * mult);
            
            //arm control
            double fullArm = 1.0;
            double armAmt = 0.0;
            if (barmsUp.get()) { armAmt = fullArm; }
            else if (barmsDown.get()) { armAmt = fullArm * -1; }
            arms.set(armAmt);
            
            //compresser/launching control
            if (bcompressOn.get() && !compressor.enabled()) { compressor.start(); }
            else if (bcompressOff.get() && compressor.enabled()) { compressor.stop(); }
            launcher.set(blaunch.get());
            
            Timer.delay(0.005); //do not delete
        }
    }
}