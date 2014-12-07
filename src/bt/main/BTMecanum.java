    /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bt.main;

import bt.storage.BTStorage;
import com.sun.squawk.util.MathUtils;

/**
 *
 * 
 */
public class BTMecanum {
    // Set values for these using storage.con
    double frontRightMotor;
    double frontLeftMotor;
    double backRightMotor;
    double backLeftMotor;
    
    boolean frontRightPiston;
    boolean frontLeftPiston;
    boolean backRightPiston;
    boolean backLeftPiston;
    
    
    
    boolean isTankDrive;
    
    private final BTStorage storage;
    
    public BTMecanum(BTStorage storage)
    {
        this.storage = storage;
    }
    public void update()
    {
        if(storage.con.A_BUTTON.getLeadingEdge())
        {
            changeDrive();
        }
        if(isTankDrive)
        {
            mecanum();
        }
        else
        {
            tankDrive();
        }
    }
    
    public void mecanum()
    {
        double lr = storage.con.RIGHT_STICK_LEFT_RIGHT.getVal();
        double ud = storage.con.RIGHT_STICK_UP_DOWN.getVal();
               
        double mag = Math.sqrt(lr * lr + ud * ud);
        double angle = MathUtils.atan(ud / lr);
        double rotation = storage.con.LEFT_STICK_LEFT_RIGHT.getVal();
        
        double fl = mag * Math.sin(angle + Math.PI / 4) + rotation;
        double fr = mag * Math.cos(angle + Math.PI / 4) - rotation;
        double bl = mag * Math.cos(angle + Math.PI / 4) + rotation;
        double br = mag * Math.sin(angle + Math.PI / 4) - rotation;
        
        double max = 1;
        
        if(Math.abs(fl) > 1 || Math.abs(fr) > 1 || Math.abs(bl) > 1 || Math.abs(br) > 1)
        {
            max = Math.max(Math.abs(fl),Math.abs(fr));
            max = Math.max(max, Math.abs(bl));
            max = Math.max(max, Math.abs(br));
        }
        
        storage.data.MOTOR_FL.setX(fl / max);
        storage.data.MOTOR_FR.setX(fr / max);
        storage.data.MOTOR_BL.setX(bl / max);
        storage.data.MOTOR_BR.setX(br / max);
        
    }
    
    public void tankDrive()
    {        
        storage.data.MOTOR_FL.setX(storage.con.RIGHT_STICK_UP_DOWN.getVal());
        storage.data.MOTOR_FR.setX(storage.con.RIGHT_STICK_UP_DOWN.getVal());
        storage.data.MOTOR_BL.setX(storage.con.RIGHT_STICK_UP_DOWN.getVal());
        storage.data.MOTOR_BR.setX(storage.con.RIGHT_STICK_UP_DOWN.getVal());
    }
    
    public void changeDrive()
    {
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
    
}
