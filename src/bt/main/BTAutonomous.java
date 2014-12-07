/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bt.main;

import bt.storage.BTStorage;
import bt.storage.Constants;
import bt.storage.health.BTStatGroup;
import bt.storage.health.BTStatistic.BTStatNum;
import bt.storage.health.BTStatistic.BTStatString;

/**
 *
 * @author Dlock
 */
public class BTAutonomous {
    private final BTStorage storage;
    private double curDistance;
    private double startDistance;
    private double goToDistance = 50;
    private int time = 1000;
    private final double fudgeFactor = .1;
    private final double speed = .5;
    private boolean onStart = false;
    private boolean isManipMoving = true;
    private boolean isMoving = false;
    private boolean isShooting = false;
    private boolean isReloading = false;
    private long startTime;
    private BTStatGroup btAuto;
    private BTStatNum distance;
    private BTStatNum curHingeStat;
    private BTStatString stage;
    private int waitRatio = 500;
            
    private double gotoHingeAngle = Constants.PITCH_SHOOTING ;
    private double curHingeAngle;
    
    private BTMain btr;
    public BTAutonomous(BTStorage storage, BTMain btr)
    {
        this.storage = storage;
        this.btr = btr;
        startTime = 0;
        btAuto = new BTStatGroup("Autonomous");
        distance = btAuto.newNumStat("Distance of Robot: ", 0, true);
        stage = btAuto.newStringStat("AUTO MODE: ", "", true);
        curHingeStat = btAuto.newNumStat("Autonoumous Hinge Angle: ",0,true);
    }
    public void runWSleep()
    {
        storage.data.ENCODER_LeftDrive.reset();
        storage.data.PISTON_LockAngle.retract();
        sleep(1000);
        curHingeAngle = -(storage.data.POT_Hinge.get() - 5);
        while(gotoHingeAngle < curHingeAngle - Constants.HINGE_FUDGE_FACTOR && storage.data.DI_minLimitSwitch.get() && btr.isEnabled() && btr.isAutonomous()) {
                curHingeAngle = -(storage.data.POT_Hinge.get() - 5);
                storage.data.MOTOR_Hinge.setX(.14);
        } 
        storage.data.MOTOR_Hinge.setX(0);
        storage.data.PISTON_LockAngle.extend();
        sleep(1000);
        storage.data.PISTON_Collector.extend();
        sleep(2000);
        storage.data.PISTON_ManipRelease.retract();
        sleep(2000);
        storage.data.PISTON_ManipRelease.extend();
        storage.data.MOTOR_L.setX(-.45);
        storage.data.MOTOR_R.setX(.55);
        wait(750);
//        curDistance = storage.data.ENCODER_LeftDrive.getDistance();
//        while (curDistance < goToDistance - fudgeFactor && btr.isAutonomous() && btr.isEnabled())
//        {
//            curDistance = storage.data.ENCODER_LeftDrive.getDistance()-startDistance;
//            storage.data.MOTOR_L.setX(-.45);
//            storage.data.MOTOR_R.setX(speed);
//            distance.updateVal(curDistance);
//        }
        storage.data.MOTOR_L.setX(0);
        storage.data.MOTOR_R.setX(0);
        
    }
    public void update()
    {
        curDistance = storage.data.ENCODER_LeftDrive.getDistance()-startDistance;
        
        updateManipView();
        updateTimePosition();
        //updatePosition();
        updateShooting();
    }
    public void updateManipView() {
        double hingeSpeed = 0;
        curHingeAngle = -(storage.data.POT_Hinge.get() - 5);
        if (isManipMoving) {
            stage.updateVal("Manip Moving");
            if (gotoHingeAngle < curHingeAngle - Constants.HINGE_FUDGE_FACTOR && storage.data.DI_minLimitSwitch.get()) {
                hingeSpeed = .14;
            } else if (gotoHingeAngle > curHingeAngle + Constants.HINGE_FUDGE_FACTOR && storage.data.DI_maxLimitSwitch.get()) {
                hingeSpeed = -.3;
            } else {
                storage.data.PISTON_LockAngle.extend();
                isManipMoving = false;
                isMoving = true;
            }
            
            storage.data.MOTOR_Hinge.setX(hingeSpeed);
        }
        curHingeStat.updateVal(curHingeAngle);
    }
    public void updatePosition()
    {
        if (isMoving) {
            stage.updateVal("Drive Moving");
            if (curDistance < goToDistance - fudgeFactor) {
                storage.data.MOTOR_L.setX(-speed);
                storage.data.MOTOR_R.setX(speed);
            }
                storage.data.MOTOR_L.setX(0);
                storage.data.MOTOR_R.setX(0);
                isMoving = false;
                isShooting = true;
            
        }
        distance.updateVal(curDistance);
    }
    public void updateShooting()
    {
        if (isShooting && startTime == 0)
        {
            startTime = System.currentTimeMillis();
        }
        if (isShooting)
        {
            stage.updateVal("Shooting");
            storage.data.PISTON_Collector.retract();
            if (System.currentTimeMillis()-startTime % 3000 == 0)
            {
                storage.data.PISTON_ManipRelease.retract();
            }
            storage.data.PISTON_ManipRelease.extend();
            if (System.currentTimeMillis()-startTime % 5000 == 0)
            {
                storage.data.PISTON_ManipRelease.extend();
                isShooting = false;
                isReloading = true;
            }
        }
        if (isReloading)
        {
            stage.updateVal("Reloading");
            storage.data.PISTON_Collector.extend();
        }
    }
    public void updateTimePosition()
    {
        if (isMoving)
        {
            if ((double)storage.debug.TIME / (double)time >= 1.0 && storage.debug.TIME > 1)
            {
                storage.data.MOTOR_L.setX(0);
                storage.data.MOTOR_R.setX(0);
                isMoving = false;
                isShooting = true;
            }
            else
            {
                storage.data.MOTOR_L.setX(-speed);
                storage.data.MOTOR_R.setX(speed);
            }
        }
    }
    public void sleep(int time)
    {
        int tmp = time;
        wait(time % waitRatio);
        for (int i = 0; i < (int)((double)time/(double)waitRatio); i++)
        {
            wait(waitRatio);
            if (!btr.isEnabled() || !btr.isAutonomous())
                return;
        }
    }
    public void wait(int time){if(btr.isEnabled() && btr.isAutonomous()){try{Thread.sleep((long)time);}catch(Exception e){}}}
}
