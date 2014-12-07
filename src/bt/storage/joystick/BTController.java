/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bt.storage.joystick;

import bt.storage.joystick.modules.BTConAxis;
import bt.storage.joystick.modules.BTConButton;

/**
 *
 * @author Dlock
 */
public interface BTController {
    
    public BTConAxis getAxis(int port);
    public BTConButton getButton(int port);
    public BTConButton[] getAllButtons();
    public BTConAxis[] getAllAxes();
}
