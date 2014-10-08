/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.carobotics;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 * @author Developer
 */
public class UpdateVals extends Command {
    private boolean isFinished = false;
    private RobotMain controller;
    public UpdateVals(RobotMain controller) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
        this.controller = controller;
        this.controller.print("Constructed");
    }

    // Called just before this Command runs the first time
    protected void initialize() {
        controller.print("Initialized");
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
        controller.print("Command!");
        finish();
    }
    
    public void finish() {
        controller.print("Finishing");
        isFinished = true;
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        controller.print("is finished?");
        return isFinished;
    }

    // Called once after isFinished returns true
    protected void end() {
        controller.print("End");
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
        controller.print("Interrupt");
    }
}
