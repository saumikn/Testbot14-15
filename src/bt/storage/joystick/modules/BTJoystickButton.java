/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bt.storage.joystick.modules;

import edu.wpi.first.wpilibj.Joystick;

/**
 *
 * @author Dlock
 */
public class BTJoystickButton implements BTConButton {

    int port;
    Joystick joy;
    public boolean prevVal = false;
    public boolean currentVal = false;
    public static final int BUTTON_AMOUNT_MAX = 12;

    public BTJoystickButton(Joystick joy, int port) {
        this.port = port;
        this.joy = joy;
        
    }

    public boolean getVal() {
        currentVal = joy.getRawButton(port);
        prevVal = currentVal;
        return prevVal;
    }

    public boolean getLeadingEdge() {
        currentVal = joy.getRawButton(port);
        boolean state = false;
        if (!prevVal && currentVal) {
            state = true;
        }
        prevVal = currentVal;
        return state;
    }

    public boolean getBackEdge() {
        currentVal = joy.getRawButton(port);
        boolean state = false;
        if (prevVal && !currentVal) {
            state = true;
        }
        prevVal = currentVal;
        return state;
    }

    public boolean getContinousEdge() {
        currentVal = joy.getRawButton(port);
        boolean state = false;
        if (prevVal && currentVal) {
            state = true;
        }
        prevVal = currentVal;
        return state;
    }
}
