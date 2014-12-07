/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bt.storage;

import bt.storage.joystick.BTXboxController;

/**
 *
 * @author Dlock
 */
public class BTStorage {
    
    public BTDebugger debug;
    public BTData data;
    public BTXboxController con;
    
    public BTStorage()
    {
        debug = new BTDebugger();
        data = new BTData(debug);
        con = new BTXboxController(Constants.XBOX_CONTROLLER_PORT);
    }
    
}
