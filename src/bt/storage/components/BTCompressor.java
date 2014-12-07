/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bt.storage.components;

import edu.wpi.first.wpilibj.Compressor;

/**
 *
 * @author Dlock
 */
public class BTCompressor extends Compressor{

    private boolean isStarted;
    public BTCompressor(int pressureSwitchChannel, int compressorRelayChannel) {
        super(pressureSwitchChannel, compressorRelayChannel);
        isStarted = false;
    }
    public boolean startComp()
    {
        this.start();
        isStarted = true;
        return isStarted;
    }
    public boolean isStarted()
    {
        return isStarted;
    }
    public boolean stopComp()
    {
        this.stop();
        isStarted = false;
        return isStarted;
    }
}
