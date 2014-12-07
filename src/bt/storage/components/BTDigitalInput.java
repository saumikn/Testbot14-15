/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bt.storage.components;

import bt.storage.Constants;
import bt.storage.health.BTStatGroup;
import bt.storage.health.BTStatistic.BTStatBoolean;
import edu.wpi.first.wpilibj.DigitalInput;

/**
 *
 * @author Dlock
 */
public class BTDigitalInput extends DigitalInput {

    public BTStatGroup DiG;
    private BTStatBoolean boolval;
    private String healthName;
    public BTDigitalInput(int channel) {
        super(channel);
        healthName = "DEBUG: Digital Input: DI port:"+channel;
        DiG = new BTStatGroup(healthName);
        boolval = DiG.newBoolStat(healthName, false, Constants.DEBUGMODE);
    }
    public boolean get()
    {
        boolean val = super.get();
        boolval.updateVal(val);
        return val;
    }
}
