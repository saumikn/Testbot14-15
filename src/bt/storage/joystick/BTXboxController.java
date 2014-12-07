/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bt.storage.joystick;

import bt.storage.joystick.modules.BTConAxis;
import bt.storage.joystick.modules.BTConButton;
import bt.storage.joystick.modules.BTJoystickButton;
import bt.storage.joystick.modules.BTJoystickAxis;
import edu.wpi.first.wpilibj.Joystick;

/**
 *
 * @author Dlock
 */
public class BTXboxController implements BTController {

    private final Joystick joy;
    public final BTJoystickButton A_BUTTON;
    public final BTJoystickButton B_BUTTON;
    public final BTJoystickButton X_BUTTON;
    public final BTJoystickButton Y_BUTTON;
    public final BTJoystickButton LEFT_BUMPER_BUTTON;
    public final BTJoystickButton RIGHT_BUMPER_BUTTON;
    public final BTJoystickButton BACK_BUTTON;
    public final BTJoystickButton START_BUTTON;
    public final BTJoystickButton LEFT_STICK_BUTTON;
    public final BTJoystickButton RIGHT_STICK_BUTTON;
    
    public final BTJoystickAxis LEFT_STICK_LEFT_RIGHT;
    public final BTJoystickAxis LEFT_STICK_UP_DOWN;
    public final BTJoystickAxis TRIGGER;
    public final BTJoystickAxis RIGHT_STICK_LEFT_RIGHT;
    public final BTJoystickAxis RIGHT_STICK_UP_DOWN;
    public final BTJoystickAxis DPAD_LEFT_RIGHT;
    public static final double XBOX_LEFT_TRIGGER = 1.0;
    public static final double XBOX_RIGHT_TRIGGER = -1.0;
    public static final double XBOX_LEFT_DPAD = -1.0;
    public static final double XBOX_RIGHT_DPAD = 1.0;
    private BTJoystickButton[] buttons;
    private BTJoystickAxis[] axes;

    /**
     *
     * @param joyport
     */
    public BTXboxController(int joyport) {
        joy = new Joystick(joyport);
        A_BUTTON = new BTJoystickButton(joy, 1);
        B_BUTTON = new BTJoystickButton(joy, 2);
        X_BUTTON = new BTJoystickButton(joy, 3);
        Y_BUTTON = new BTJoystickButton(joy, 4);
        LEFT_BUMPER_BUTTON = new BTJoystickButton(joy, 5);
        RIGHT_BUMPER_BUTTON = new BTJoystickButton(joy, 6);
        BACK_BUTTON = new BTJoystickButton(joy, 7);
        START_BUTTON = new BTJoystickButton(joy, 8);
        LEFT_STICK_BUTTON = new BTJoystickButton(joy, 9);
        RIGHT_STICK_BUTTON = new BTJoystickButton(joy, 10);
        
        LEFT_STICK_LEFT_RIGHT = new BTJoystickAxis(joy, 1);
        LEFT_STICK_UP_DOWN = new BTJoystickAxis(joy, 2);
        TRIGGER = new BTJoystickAxis(joy, 3);
        RIGHT_STICK_LEFT_RIGHT = new BTJoystickAxis(joy, 4);
        RIGHT_STICK_UP_DOWN = new BTJoystickAxis(joy, 5);
        DPAD_LEFT_RIGHT = new BTJoystickAxis(joy, 6);
        
        buttons = new BTJoystickButton[BTJoystickButton.BUTTON_AMOUNT_MAX];
        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = new BTJoystickButton(joy, i);
        }
        axes = new BTJoystickAxis[BTJoystickAxis.AXIS_AMOUNT_MAX];
        for (int i = 0; i < axes.length; i++) {
            axes[i] = new BTJoystickAxis(joy, i);
        }
    }

    public BTConAxis getAxis(int port) {
        if (port > BTJoystickAxis.AXIS_AMOUNT_MAX || port < 1)
            return null;
        return new BTJoystickAxis(joy, port);
    }

    public BTConButton getButton(int port) {
        if (port > BTJoystickButton.BUTTON_AMOUNT_MAX || port < 1)
            return null;
        return new BTJoystickButton(joy, port);
    }
    

    public Joystick getJoystick() {
        return joy;
    }

    public BTConButton[] getAllButtons() {
        return buttons;
    }

    public BTConAxis[] getAllAxes() {
        return axes;
    }
}
