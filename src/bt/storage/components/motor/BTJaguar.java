/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bt.storage.components.motor;

import bt.storage.BTDebugger;
import bt.storage.Constants;
import bt.storage.health.BTStatGroup;
import bt.storage.health.BTStatistic.BTStatNum;
import edu.wpi.first.wpilibj.Jaguar;

/**
 *
 * @author Dlock
 */
public class BTJaguar implements BTMotor {

    Jaguar jag;
    double val;
    public BTStatGroup motorG;
    private BTStatNum debugSpeed;
    public BTJaguar(int port, BTDebugger debug)
    {
        jag = new Jaguar(port);
        val = 0;
        motorG = new BTStatGroup("MotorGroup "+port);
        debugSpeed = motorG.newNumStat("DEBUG: BTJaguar: Port: "+port+" ", 0, Constants.DEBUGMODE);
    }
    public void setX(double x) {
        val = x;
        jag.set(x);
        debugSpeed.updateVal(val);
    }
    public double getX()
    {
        return val;
    }

    public static class Factory implements BTMotorFactory
    {
        BTDebugger debug;
        public Factory(BTDebugger debug)
        {
            this.debug = debug;
        }
        public BTJaguar makeJagMotor(int port) {
            return new BTJaguar(port, debug);
        }

        public BTMotor makeMotor(int port) {
            return new BTJaguar(port, debug);
        }

        public BTCanJaguar makeCanMotor(int port) {
            return null;
        }
    }
}
