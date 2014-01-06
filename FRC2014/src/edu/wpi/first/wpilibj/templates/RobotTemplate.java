/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.templates;
 
import edu.wpi.first.wpilibj.Joystick; 
import edu.wpi.first.wpilibj.RobotDrive; 
import edu.wpi.first.wpilibj.SimpleRobot; 
import edu.wpi.first.wpilibj.Timer; 



public class RobotTemplate extends SimpleRobot {
 
    RobotDrive drive = new RobotDrive(1, 2);
    Joystick rightStick = new Joystick(1);
    Joystick leftStick = new Joystick(1);
 
    public void autonomous() {
        for (int i = 0; i < 4; i++) { 
            drive.drive(0.5, 0.0);
            Timer.delay(2.0);
            drive.drive(0.0, 0.75);
        }
        drive.drive(0.0, 0.0);
    } 
 
    public void operatorControl() { 
        while (isOperatorControl() && isEnabled())
        { 
            //drive.arcadeDrive(stick);
            drive.tankDrive(leftStick, rightStick);
            Timer.delay(0.005); 
        }
    }
}