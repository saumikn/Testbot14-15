/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bt.storage.components;

import bt.storage.Constants;
import bt.storage.health.BTStatGroup;
import bt.storage.health.BTStatistic.BTStatNum;
import edu.wpi.first.wpilibj.Encoder;

/**
 *
 * @author Dlock
 */
public class BTEncoder extends Encoder {

    public BTStatGroup encodeG;
    private BTStatNum rawVal;
    private String healthName;
    public BTEncoder(int aChannel, int bChannel) {
        super(aChannel, bChannel);
        healthName = "DEBUG: Encoder: A:"+aChannel+" B:"+bChannel+" ";
        encodeG = new BTStatGroup(healthName);
        rawVal = encodeG.newNumStat(healthName, 0, Constants.DEBUGMODE);
    }
    public int getRaw()
    {
        int val = super.getRaw();
        rawVal.updateVal(val);
        return val;
    }
    public double getDistance()
    {
        double val = super.getDistance();
        getRaw();
        return val;
    }
    public double getRate()
    {
        double val = super.getRate();
        getRaw();
        return val;
    }
}
