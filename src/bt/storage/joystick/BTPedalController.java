/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bt.storage.joystick;

import bt.storage.joystick.BTController;
import bt.storage.joystick.modules.BTConAxis;
import bt.storage.joystick.modules.BTConButton;
import bt.storage.joystick.modules.BTJoystickButton;
import bt.storage.joystick.modules.BTJoystickAxis;
import edu.wpi.first.wpilibj.Joystick;

/**
 *
 * @author Dlock
 */
public class BTPedalController implements BTController {

    private final Joystick joy;

    public BTPedalController(int joyport) {
        joy = new Joystick(joyport);
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

    public BTConButton[] getAllButtons() {
        BTJoystickButton[] buttons = new BTJoystickButton[BTJoystickButton.BUTTON_AMOUNT_MAX];
        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = new BTJoystickButton(joy, i);
        }
        return buttons;
    }

    public BTConAxis[] getAllAxes() {
        BTJoystickAxis[] axes = new BTJoystickAxis[BTJoystickAxis.AXIS_AMOUNT_MAX];
        for (int i = 0; i < axes.length; i++) {
            axes[i] = new BTJoystickAxis(joy, i);
        }
        return axes;
    }
}
