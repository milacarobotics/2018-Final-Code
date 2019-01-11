/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team5999.robot;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;



public class Robot extends IterativeRobot {
	private static final String kDefaultAuto = "Default";
	private static final String kCustomAuto = "My Auto";
	private String m_autoSelected;
	private SendableChooser<String> m_chooser = new SendableChooser<>();

	Joystick LStick = new Joystick(0);
	Joystick RStick = new Joystick(1);
	
	VictorSP FL = new VictorSP(0);
	VictorSP FR = new VictorSP(2);
	VictorSP BL = new VictorSP(1);
	VictorSP BR = new VictorSP(3); 	
	
	SpeedControllerGroup LDrive = new SpeedControllerGroup(FL, BL);
	SpeedControllerGroup RDrive = new SpeedControllerGroup(FR, BR);

	DifferentialDrive Tank = new DifferentialDrive(LDrive,RDrive);
	
	// arm
	Victor Arm1= new Victor(6);
	Victor Arm2 = new Victor(7);
	// SpeedControllerGroup Arm = new SpeedControllerGroup(Arm1, Arm2);
	
	Spark Grab1 = new Spark(4);
	Spark Grab2 = new Spark(5);
	
	// SpeedControllerGroup Grab = new SpeedControllerGroup (Grab1,Grab2);
	
	// compressor
	Compressor comp = new Compressor();
	
	// wrist
	DoubleSolenoid Wrist = new DoubleSolenoid(0,1);
	//Solenoid Wrist2 = new Solenoid(2);
	
	// toggle speed
	boolean speedTog = false;
	
	Timer BreakingBot = new Timer();
	
	
	@Override
	public void robotInit() {
		
		m_chooser.addDefault("Default Auto", kDefaultAuto);
		m_chooser.addObject("My Auto", kCustomAuto);
		SmartDashboard.putData("Auto choices", m_chooser);
		CameraServer.getInstance().startAutomaticCapture();
		/*
		Wrist1.set(Value.kForward);
		Wrist1.set(Value.kReverse);
		
		Wrist2.set(true);
		Wrist2.set(false);
	*/
	}
	
	



	@Override
	public void autonomousInit() {
		m_autoSelected = m_chooser.getSelected();
		// autoSelected = SmartDashboard.getString("Auto Selector",
		// defaultAuto);
		System.out.println("Auto selected: " + m_autoSelected);
		BreakingBot.reset();
		BreakingBot.start();
		
	}


	@Override
	public void autonomousPeriodic() {
//		switch (m_autoSelected) {
//			case kCustomAuto:
//
//				
//
//
//					/*Tank.stopMotor();
//				}
//					*/
//				break;
//			case kDefaultAuto:
				// defalut = 10, change to 5 in hopes of solving long selfdrive.
				while(BreakingBot.get() <= 7) {
					
					SmartDashboard.putNumber("Timer", BreakingBot.get());
					boolean start = BreakingBot.get() >= 0;
					boolean stop = BreakingBot.get() <= 4.5;
					
					Wrist.set(DoubleSolenoid.Value.kForward);
					
					
					if(start == true) {
						Tank.tankDrive(0.5, 0.5);
					
					}else if(stop == true) {
						Tank.stopMotor();
					}else {
						Tank.stopMotor();					
					}
					SmartDashboard.putNumber("Timer", BreakingBot.get());
				}
//			default:
//				// Put default auto code here
//				break;
//		}
	
		
	}


	@Override
	public void teleopPeriodic() {	
		comp.start();
		
		tank();
		grabber();
		arm();
		wrist();
	}
	
	public void tank() {
		if(LStick.getRawButtonPressed(3)) {
			if (speedTog == false) {
				speedTog = true;
			} else if (speedTog == true) {
				speedTog = false;
			}
		}
		if(speedTog == true) {
			Tank.tankDrive(-LStick.getY()*.50, -RStick.getY()*.50);
		} else {
			Tank.tankDrive(-LStick.getY()*.75, -RStick.getY()*.75);
		}
		SmartDashboard.putNumber("Left Stick Speed: ", -LStick.getThrottle());
		SmartDashboard.putNumber("Right Stick Speed: ", -RStick.getThrottle());
	}
	
	public void grabber() {
		// SpeedControllerGroup Grab = new SpeedControllerGroup (Grab1,Grab2);
		Grab2.setInverted(true);
		if(LStick.getRawButton(1)) {
			Grab1.setSpeed(1);
			Grab2.setSpeed(1);
		}else if (LStick.getRawButton(4)) {
			Grab1.setSpeed(-0.75);
			Grab2.setSpeed(-0.75);
		}else {
			Grab1.setSpeed(0.3);
			Grab2.setSpeed(0.3);
		}
		
		//SmartDashboard.putData("Grabber", Grab);
	}
	
	public void arm() {
		Arm2.setInverted(true);
		if(RStick.getRawButton(5)) {
			Arm1.setSpeed(0.40);
			Arm2.setSpeed(0.40);
		}else if (RStick.getRawButton(3)) {
			Arm1.setSpeed(-0.25);
			Arm2.setSpeed(-0.25);
		}else {
			Arm1.setSpeed(0.0);
			Arm2.setSpeed(0.0);
		}
	}

	public void wrist() {
		if(RStick.getRawButton(2)){
			SmartDashboard.putString("Wrist", "Going Forward");
			Wrist.set(DoubleSolenoid.Value.kForward);
			//Wrist2.set(true);
		} else if (RStick.getRawButton(1)) {
			SmartDashboard.putString("Wrist", "Going Backward");
			Wrist.set(DoubleSolenoid.Value.kReverse);
			//Wrist2.set(false);
		} else {
			SmartDashboard.putString("Wrist","nothing...");
			Wrist.set(DoubleSolenoid.Value.kOff);
		}
	}

	@Override
	public void testPeriodic() {
	
		
		//LiveWindow.add(Grab);
	}
}
