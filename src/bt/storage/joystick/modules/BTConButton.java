/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bt.storage.joystick.modules;

/**
 *
 * @author Dlock
 */
public interface BTConButton {
    public boolean getVal();
    public boolean getLeadingEdge();
    public boolean getBackEdge();
    public boolean getContinousEdge();
}
