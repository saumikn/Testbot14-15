    /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bt.main;

import bt.storage.BTStorage;
import bt.storage.Constants;
import bt.storage.health.BTStatGroup;
import bt.storage.health.BTStatistic.BTStatNum;
import com.sun.squawk.util.MathUtils;

/**
 *
 * @author Dlock
 */
public class BTDriveTrain {
    private final BTStorage storage;
    
    
    //SmartDashboard
    private final String HealthName = "BTDriveTrain";
    private final BTStatGroup dtStats;
    private final BTStatNum frontLeftSpeed;
    private final BTStatNum frontRightSpeed;
    private final BTStatNum backLeftSpeed;
    private final BTStatNum backRightSpeed;
    
    boolean isTankDrive;
    
    public BTDriveTrain(BTStorage storage)
    {
        this.storage = storage;
        
        //SmartDashboard
        dtStats = new BTStatGroup(HealthName);        
        frontLeftSpeed = dtStats.newNumStat("Front Left Speed", 0, true);
        frontRightSpeed = dtStats.newNumStat("Front Right Speed", 0, true);
        backLeftSpeed = dtStats.newNumStat("Back Left Speed", 0, true);
        backRightSpeed = dtStats.newNumStat("Back Right Speed", 0, true);
        
        storage.debug.write(Constants.DebugLocation.BTDriveTrain, Constants.Severity.INFO, "BTDriveTrain initiated successfully");
    }
    public void update()
    {
        chooseDrive();//Choses which drivetrain to use, and changes them if the A_BUTTON is pressed
        updateSpeed();//Updates the speed of all four wheels in use to the Smart Dashboard
    }
    
    public void chooseDrive()
    {
        //If the A_BUTTON is pressed, the drivetrain in use is changed
        if(storage.con.A_BUTTON.getLeadingEdge())
        {
            changeDrive();
        }
        //Chooses which code to run based on the drivetrain in use
        if(isTankDrive)
        {
            mecanum();
        }
        else
        {
            tankDrive();
        }
    }
    
    public void changeDrive()
    {
        //The drivetrain in use changes
        if(isTankDrive)
        {
            isTankDrive = false;
            
            storage.data.PISTON_FL.extend();
            storage.data.PISTON_FR.extend();
            storage.data.PISTON_BL.extend();
            storage.data.PISTON_BR.extend();
        }
        else
        {
            isTankDrive = true;
            
            storage.data.PISTON_FL.retract();
            storage.data.PISTON_FR.retract();
            storage.data.PISTON_BL.retract();
            storage.data.PISTON_BR.retract();
        }
               
    }
    
    private void updateSpeed()
    {
        //Updates speed to SmartDashboard
        if(isTankDrive)
        {
            frontLeftSpeed.updateVal(storage.data.ENCODER_FL_Tank.getRate());
            frontRightSpeed.updateVal(storage.data.ENCODER_FR_Tank.getRate());
            backLeftSpeed.updateVal(storage.data.ENCODER_BL_Tank.getRate());
            backRightSpeed.updateVal(storage.data.ENCODER_BR_Tank.getRate());
        }
        else
        {
            frontLeftSpeed.updateVal(storage.data.ENCODER_FL_Mecanum.getRate());
            frontRightSpeed.updateVal(storage.data.ENCODER_FR_Mecanum.getRate());
            backLeftSpeed.updateVal(storage.data.ENCODER_BL_Mecanum.getRate());
            backRightSpeed.updateVal(storage.data.ENCODER_BR_Mecanum.getRate());
        }
    }
    
    private void tankDrive()
    {
        //Gets the latest value for the left and right joysticks
        double u = storage.con.RIGHT_STICK_UP_DOWN.getVal();
        double v = storage.con.LEFT_STICK_UP_DOWN.getVal();
        
        //Curves the motor speed so driving is easier
        u = u * Math.abs(u);
        v = v * Math.abs(v);
        
        //Sets the motors
        storage.data.MOTOR_FR.setX(u);
        storage.data.MOTOR_FL.setX(-v);
        storage.data.MOTOR_BR.setX(u);
        storage.data.MOTOR_BL.setX(-v);
    }
    
    public void mecanum()
    {
        //Gets the latest values of the right joysticks 
        double u = storage.con.RIGHT_STICK_LEFT_RIGHT.getVal();
        double v = storage.con.RIGHT_STICK_UP_DOWN.getVal();
        
        //Calculates the ideal speed, angle, and rotation
        double speed = Math.sqrt(u * u + v * v);
        double angle = MathUtils.atan(v / u);
        double rotation = storage.con.LEFT_STICK_LEFT_RIGHT.getVal();
        
        //Determines what to set the motor, based on the formulas in the PDF
        double fl = speed * Math.sin(angle + Math.PI / 4) + rotation;
        double fr = speed * Math.cos(angle + Math.PI / 4) - rotation;
        double bl = speed * Math.cos(angle + Math.PI / 4) + rotation;
        double br = speed * Math.sin(angle + Math.PI / 4) - rotation;
        
        double max = 1;
        
        //Finds the maximum absolute value of the motor speeds, so we know if we need to scale
        if(Math.abs(fl) > 1 || Math.abs(fr) > 1 || Math.abs(bl) > 1 || Math.abs(br) > 1)
        {
            max = Math.max(Math.abs(fl),Math.abs(fr));
            max = Math.max(max, Math.abs(bl));
            max = Math.max(max, Math.abs(br));
        }
        
        //Sets the motors, and scales if any value is outside of the range [-1,1]
        storage.data.MOTOR_FL.setX(fl / max);
        storage.data.MOTOR_FR.setX(fr / max);
        storage.data.MOTOR_BL.setX(bl / max);
        storage.data.MOTOR_BR.setX(br / max);
        
    }
}