/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package bt.main;


import bt.storage.components.BTCompressor;
import bt.storage.BTStorage;
import bt.storage.Constants;
import bt.storage.health.BTStatGroup;
import bt.storage.health.BTStatistic.BTStatBoolean;
import bt.storage.health.BTStatistic.BTStatNum;
import bt.storage.health.BTStatistic.BTStatString;
import edu.wpi.first.wpilibj.SimpleRobot;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the SimpleRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class BTMain extends SimpleRobot {
    BTStorage storage;
    BTDriveTrain dt;
    private BTStatGroup btMain;
    private BTStatString version;
    private BTStatNum pressure;
    private BTStatNum CYCLE_TIME;
    private BTStatBoolean isPressureStat;
    private String HealthName;
    BTCompressor comp;
    BTAutonomous auto;
    BTManipulator shooter;
    BTStatNum compStat;
    String versionStr;
    
    public void robotInit() {
        storage = new BTStorage();
        versionStr = "1.1.6";
        HealthName = "BTMain";
        dt = new BTDriveTrain(storage);
        btMain = new BTStatGroup(HealthName);
        version = btMain.newStringStat("Version", versionStr, true);
        pressure = btMain.newNumStat("Pressure: ", 0, true);
        CYCLE_TIME = btMain.newNumStat("Cycle Time: ", 0, true);
        isPressureStat = btMain.newBoolStat("Is At Full Pressure", false, true);
        comp = new BTCompressor(Constants.COMP_SENSOR_PORT, Constants.COMP_RELAY_PORT);
        auto = new BTAutonomous(storage, this);
        shooter = new BTManipulator(storage);
        storage.debug.write(Constants.DebugLocation.BTMain, Constants.Severity.INFO, "Robot Inited Version "+versionStr);
    }
    
    public void autonomous() {
        comp.startComp();
        storage.data.ENCODER_LeftDrive.start();
        storage.data.ENCODER_RightDrive.start();
        storage.debug.STIME = System.currentTimeMillis();
        auto.runWSleep();
        while (isAutonomous())
        {
           //auto.update();
           storage.debug.TIME = System.currentTimeMillis()-storage.debug.STIME;
        }
    }

    public void operatorControl() {
        comp.startComp();
        storage.debug.STIME = System.currentTimeMillis();
        storage.data.ENCODER_ManipWinch.start();
        storage.data.ENCODER_LeftDrive.start();
        storage.data.ENCODER_RightDrive.start();
        storage.data.ENCODER_LeftDrive.reset();
        storage.data.ENCODER_RightDrive.reset();
        while (isOperatorControl())
        {
            comp.startComp();
            dt.update();
            shooter.update();
            pressure.updateVal(storage.data.AC_PressureSensor.getValue()*Constants.PSI_PER_VAL);
            if (storage.data.AC_PressureSensor.getValue() >= Constants.PRESSURE_ISFILLED)
            {
                isPressureStat.updateVal(true);
            }
            else
            {
                isPressureStat.updateVal(false);
            }
            storage.debug.TIME = System.currentTimeMillis()-storage.debug.STIME;
        }
    }
    public void disabled() {
        
        comp.stopComp();
        storage.data.encoderReset();
        if (Constants.DEBUG_DURING_DISABLED)
            storage.debug.printDebugWARNING();
    }
}
