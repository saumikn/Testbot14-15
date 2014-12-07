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
public class BTJoystickAxis implements BTConAxis {

    int port;
    Joystick joy;
    public static final int AXIS_AMOUNT_MAX = 6;

    public BTJoystickAxis(Joystick joy, int port) {
        this.port = port;
        this.joy = joy;
    }

    public double getVal() {
        return joy.getRawAxis(port);
    }

   
}
