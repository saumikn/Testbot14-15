/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bt.storage;

import bt.storage.components.BTAnalogPot;
import bt.storage.components.BTDigitalInput;
import bt.storage.components.BTEncoder;
import bt.storage.components.BTSonar;
import bt.storage.components.BTPiston;
import bt.storage.components.motor.BTCanJaguar;
import bt.storage.components.motor.BTJaguar;
import bt.storage.components.motor.BTMotor;
import bt.storage.components.motor.BTMotorFactory;
import edu.wpi.first.wpilibj.AnalogChannel;
import edu.wpi.first.wpilibj.AnalogPotentiometer;
import edu.wpi.first.wpilibj.Encoder;

public class BTData {
    
    public BTMotorFactory motorFactDT;
    public BTMotorFactory motorFactManip;
    public BTMotor MOTOR_R; //BTJaguar
    public BTMotor MOTOR_L; //BTJaguar
    
    public BTMotor MOTOR_FR;
    public BTMotor MOTOR_FL;
    public BTMotor MOTOR_BR;
    public BTMotor MOTOR_BL;
    
    public BTMotor MOTOR_Winch;
    public BTMotor MOTOR_Collector;
    public BTMotor MOTOR_Hinge;
    public BTAnalogPot POT_Hinge;
    public BTAnalogPot POT_Collector;
    public BTSonar SONAR_Drive;
    public BTDigitalInput DI_maxLimitSwitch;
    public BTDigitalInput DI_minLimitSwitch;
    public BTDigitalInput DI_winchLimit;
    public BTDigitalInput ballDetection;
    public AnalogChannel AC_PressureSensor;
    
    public BTEncoder ENCODER_LeftDrive;
    public BTEncoder ENCODER_RightDrive;
    
    public BTEncoder ENCODER_FL_Tank;
    public BTEncoder ENCODER_FR_Tank;
    public BTEncoder ENCODER_BL_Tank;
    public BTEncoder ENCODER_BR_Tank;
    public BTEncoder ENCODER_FL_Mecanum;
    public BTEncoder ENCODER_FR_Mecanum;
    public BTEncoder ENCODER_BL_Mecanum;
    public BTEncoder ENCODER_BR_Mecanum;
    public Encoder ENCODER_ManipWinch;
    
    public BTPiston PISTON_ManipRelease;
    public BTPiston PISTON_LockAngle;
    public BTPiston PISTON_Collector;
    
    public BTPiston PISTON_FR;
    public BTPiston PISTON_FL;
    public BTPiston PISTON_BR;
    public BTPiston PISTON_BL;
    
    public BTData(BTDebugger debug)
    {
        //DRIVETRAIN MOTORS
        if (Constants.IS_DT_CANBUS)
        {
            motorFactDT = new BTCanJaguar.Factory(Constants.IS_DT_VOLTAGE,debug);
        }
        else
        {
            motorFactDT = new BTJaguar.Factory(debug);
        }
        if (Constants.IS_SHOOTER_CANBUS)
        {
            motorFactManip = new BTCanJaguar.Factory(Constants.IS_SHOOTER_VOLTAGE, debug);
        }
        else
        {
            motorFactManip = new BTJaguar.Factory(debug);
        }
        MOTOR_R = motorFactDT.makeMotor(Constants.RIGHT_MOTOR_PORT);
        MOTOR_L = motorFactDT.makeMotor(Constants.LEFT_MOTOR_PORT);
        
        //SHOOTER MOTORS
        MOTOR_Winch = motorFactManip.makeMotor(Constants.WINCH_MOTOR_PORT);
        MOTOR_Collector = motorFactManip.makeMotor(Constants.COLLECTOR_MOTOR_PORT);
        MOTOR_Hinge = motorFactManip.makeMotor(Constants.HINGE_MOTOR_PORT);
        //DRIVETRAIN ENCODERS
        SONAR_Drive = new BTSonar(Constants.SONAR_PORT, 1024, true);
        ENCODER_LeftDrive = new BTEncoder(Constants.LEFT_ENCODE_A, Constants.LEFT_ENCODE_B);
        ENCODER_RightDrive = new BTEncoder(Constants.RIGHT_ENCODE_A, Constants.RIGHT_ENCODE_B); //(86.4/128.0)
        ENCODER_LeftDrive.setDistancePerPulse((Constants.WHEEL_CIRCUMFERENCE/Constants.ENCODER_TEETH));
        ENCODER_RightDrive.setDistancePerPulse(Constants.WHEEL_CIRCUMFERENCE/Constants.ENCODER_TEETH);
        
        ENCODER_ManipWinch = new BTEncoder(Constants.MANIP_SHOOT_ENCODE_A, Constants.MANIP_SHOOT_ENCODE_B);
        ENCODER_ManipWinch.setDistancePerPulse(Constants.MANIP_WHEEL_CIRCUMFERENCE/Constants.ENCODER_TEETH);
        DI_maxLimitSwitch = new BTDigitalInput(Constants.MAX_LIMIT_SWITCH_PORT);
        DI_minLimitSwitch = new BTDigitalInput(Constants.MIN_LIMIT_SWITCH_PORT);
        DI_winchLimit = new BTDigitalInput(Constants.WINCH_LIMIT_SWITCH_PORT);
        //ballDetection = new DigitalInput(Constants.BALL_DETECTION_PORT);
        
        POT_Hinge = new BTAnalogPot(Constants.HINGE_POT_CHANNEL);
        //POT_Collector = new BTAnalogPot(Constants.COLLECTOR_POT_CHANNEL);
        //SHOOTER PISTON
        PISTON_ManipRelease = new BTPiston(Constants.MANIP_RELEASE_EXTEND_PORT,Constants.MANIP_RELEASE_RETRACT_PORT);
        PISTON_Collector = new BTPiston(Constants.COLLECTOR_PISTON_EXTEND_PORT,Constants.COLLECTOR_PISTON_RETRACT_PORT);
        PISTON_LockAngle = new BTPiston(Constants.LOCK_PISTON_EXTEND_PORT, Constants.LOCK_PISTON_RETRACT_PORT);
        
        AC_PressureSensor = new AnalogChannel(Constants.PRESSURE_SENSOR_PORT);
        
        debug.write(Constants.DebugLocation.BTData, Constants.Severity.INFO, "Data Inited");
    }
    public void encoderReset()
    {
        //ENCODER_ManipWinch.reset();
    }
    
}
