/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bt.storage.components;

import bt.storage.Constants;
import bt.storage.health.BTStatGroup;
import bt.storage.health.BTStatistic.BTStatNum;
import edu.wpi.first.wpilibj.AnalogPotentiometer;

/**
 *
 * @author Dlock
 */
public class BTAnalogPot extends AnalogPotentiometer {

    public BTStatGroup PotG;
    private BTStatNum numVal;
    private String healthName;
    public BTAnalogPot(int channel) {
        super(channel);
        healthName = "DEBUG: AnalogPot: Pot port:"+channel;
        PotG = new BTStatGroup(healthName);
        numVal = PotG.newNumStat(healthName, 0, Constants.DEBUGMODE);
    }
    public double get()
    {
        double val = super.get();
        numVal.updateVal(val);
        return val;
    }
}
