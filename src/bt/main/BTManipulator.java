/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bt.main;

import bt.storage.BTStorage;
import bt.storage.Constants;
import bt.storage.health.BTStatGroup;
import bt.storage.health.BTStatistic.BTStatBoolean;
import bt.storage.health.BTStatistic.BTStatNum;
import bt.storage.health.BTStatistic.BTStatString;

/**
 *
 * @author Programming Team GURUS
 */
public class BTManipulator {

    private final BTStorage storage;
    
    private static final double fudgeFactorDpad = .05;
    private static final double fudgeFactorRightStick = .1;
    private boolean isAtPosition;
    private boolean isReloading;
    private boolean isShooting;
    private boolean closeCollector;
    private boolean ballIN;
    private boolean isCollectorOpen;
    private boolean dpadPressed;
    private double timeOfShot;
    private double goToDistance;
    private double curDistance;
    private double gotoHingeAngle;
    private boolean hingeLimitSwitch;
    private boolean isHingeMoving;

    private final String HealthName;
    private final BTStatGroup manipStats;
    private final BTStatNum curDisStat;
    private final BTStatNum goToDisStat;
    private final BTStatNum curHingeAngleStat;
    private final BTStatNum goToHingeAngleStat;
    private final BTStatString shotPowerStat;
    private final BTStatBoolean canShootStat;
    private final BTStatNum hingeAngleStat;
    private final BTStatNum timeToReloadStat;
    private final BTStatBoolean isAtPositionStat;

    public BTManipulator(BTStorage storage) {
        this.storage = storage;
        HealthName = "BTManipulator";
        manipStats = new BTStatGroup(HealthName);
        
        curDisStat = manipStats.newNumStat("Current Distance ", 0, true);
        curHingeAngleStat = manipStats.newNumStat("Current Hinge Angle", 0, true);
        goToHingeAngleStat = manipStats.newNumStat("GoTo Hinge Angle", 0, true);
        shotPowerStat = manipStats.newStringStat("Shot Power", "OFF", true);
        canShootStat = manipStats.newBoolStat("Can Shoot", false, true);
        hingeAngleStat = manipStats.newNumStat("Hinge Angle", 0, true);
        timeToReloadStat = manipStats.newNumStat("Time To Reload", 0, true);
        goToDisStat = manipStats.newNumStat("GoTo Distance", 0, true);
        isAtPositionStat = manipStats.newBoolStat("Is Retracted", false, true);
        isReloading = false;
        isShooting = false;
        ballIN = false;
        isCollectorOpen = false;
        isAtPosition = false;
        dpadPressed = false;
        closeCollector = false;
        timeOfShot = 0;
        curDistance = 0;
        goToDistance = 0;

        gotoHingeAngle = Constants.HINGE_LIMIT_MAX;
        hingeLimitSwitch = true;
        isHingeMoving = false;

        storage.data.PISTON_ManipRelease.extend();
        storage.data.PISTON_LockAngle.retract();
        storage.data.PISTON_Collector.retract();
        storage.debug.write(Constants.DebugLocation.BTManipulator, Constants.Severity.INFO, "BTManipulator Inited");
    }

    public void update() {
        ballIN = false;
        //ballIN = storage.data.ballDetection.get();
        updateManipView();
        updateCollector();
        updateShooter();
        updateKickerLimit();
        updateShotPower();
        
    }

    private void updateManipView() {
        double curHingeAngle = -(storage.data.POT_Hinge.get() - 5);
        double conVal = storage.con.RIGHT_STICK_UP_DOWN.getVal();
        double hingeSpeed = 0;

        if (storage.con.LEFT_BUMPER_BUTTON.getLeadingEdge()) {
            gotoHingeAngle = Constants.PITCH_SHOOTING;
            hingeLimitSwitch = false;
            isHingeMoving = true;
        }
        if (storage.con.TRIGGER.getVal() == Constants.LEFT_TRIGGER) {
            hingeLimitSwitch = true;
            gotoHingeAngle = Constants.HINGE_LIMIT_MIN;
            isHingeMoving = true;
        }
        if (storage.con.BACK_BUTTON.getLeadingEdge()) {
            gotoHingeAngle = Constants.HINGE_LIMIT_MAX;
            hingeLimitSwitch = true;
            isHingeMoving = true;
        }
        if (isHingeMoving) {
            storage.data.PISTON_LockAngle.retract();
            if (hingeLimitSwitch) {
                if (gotoHingeAngle == Constants.HINGE_LIMIT_MIN && storage.data.DI_minLimitSwitch.get()) {
                    hingeSpeed = .14;
                } else if (gotoHingeAngle == Constants.HINGE_LIMIT_MAX && storage.data.DI_maxLimitSwitch.get()) {
                    hingeSpeed = -.5;
                } else {
                    isHingeMoving = false;
                }
            } else if (gotoHingeAngle > Constants.HINGE_LIMIT_MIN && gotoHingeAngle < Constants.HINGE_LIMIT_MAX) {
                if (gotoHingeAngle < curHingeAngle - Constants.HINGE_FUDGE_FACTOR && storage.data.DI_minLimitSwitch.get()) {
                    hingeSpeed = .14;
                } else if (gotoHingeAngle > curHingeAngle + Constants.HINGE_FUDGE_FACTOR && storage.data.DI_maxLimitSwitch.get()) {
                    hingeSpeed = -.5;
                } else {
                    storage.data.PISTON_LockAngle.extend();
                    isHingeMoving = false;
                }
            }
        }

        //TODO: set motors to hingeSpeed
        if (isHingeMoving) {
            storage.data.MOTOR_Hinge.setX(hingeSpeed);
        } else {
            storage.data.MOTOR_Hinge.setX(0);
        }

//        if (curHingeAngle < HINGE_LIMIT_MAX+Constants.HINGE_FUDGE_FACTOR && curHingeAngle > HINGE_LIMIT_MIN-Constants.HINGE_FUDGE_FACTOR && !isReloading && !isShooting)
//        {
//            if (conVal < fudgeFactorRightStick  && conVal > -fudgeFactorRightStick)
//            {
//                storage.data.MOTOR_Hinge.setX(0);
//            }
//            else
//            {
//                storage.data.MOTOR_Hinge.setX(-conVal/4);
//            }
//        }
//        else
//        {
//            storage.data.MOTOR_Hinge.setX(0);
//        }
        
        storage.debug.HingeMove.update("" + isHingeMoving);
        curHingeAngleStat.updateVal(curHingeAngle);
        goToHingeAngleStat.updateVal(gotoHingeAngle);
    }

    private void updateCollector() {
        if (storage.con.RIGHT_BUMPER_BUTTON.getVal()) {
            storage.data.MOTOR_Collector.setX(-Constants.COLLECT_SPEED);
        } else if (storage.con.TRIGGER.getVal() == Constants.RIGHT_TRIGGER && !isShooting && !isReloading) {
            storage.data.MOTOR_Collector.setX(Constants.PASS_SPEED);
        } else {
            storage.data.MOTOR_Collector.setX(0);
        }
        if (storage.con.A_BUTTON.getLeadingEdge()) {
            isCollectorOpen = true;
        }
        if (storage.con.B_BUTTON.getLeadingEdge()) {
            isCollectorOpen = false;
        }
        if (isCollectorOpen) {
            storage.data.PISTON_Collector.extend();
        } else if (!isShooting) {
            storage.data.PISTON_Collector.retract();
        }
        if (closeCollector)
        {
            storage.data.PISTON_Collector.retract();
            closeCollector = false;
        }
        storage.debug.isCollecting.update("" + isCollectorOpen);

    }

    private void updateShooter() {
        boolean canFire = canShoot();
        if (isShooting) {
            timeToReloadStat.updateVal(timeOfShot + Constants.RELOAD_TIME - System.currentTimeMillis());
        } else {
            timeToReloadStat.updateVal(0);
        }
        if (canFire && storage.con.START_BUTTON.getLeadingEdge()) {
            storage.data.PISTON_ManipRelease.retract();
            isShooting = true;
            timeOfShot = System.currentTimeMillis();
        }
        if (isShooting && System.currentTimeMillis() - Constants.RELOAD_TIME > timeOfShot) {
            storage.data.PISTON_ManipRelease.extend();
            isShooting = false;
            isReloading = true;
            closeCollector = true;
        }
        canShootStat.updateVal(canFire);
    }

    public boolean canShoot() {
        if (isCollectorOpen && !isShooting && !isReloading && isAtPosition) {
            return true;
        }
        return false;
    }

    private void updateKickerPositionEncode() {
        boolean isAtEnd = false;
        boolean isAtBeginning = false;
        double dpadVal;

        curDistance = storage.data.ENCODER_ManipWinch.getDistance();
        dpadVal = storage.con.DPAD_LEFT_RIGHT.getVal();
        if (curDistance < Constants.RELOADING_DISTANCE) {
            isAtBeginning = true;
            isReloading = true;
        }
        if (curDistance > Constants.MANIP_WINCH_MAX) {
            isAtEnd = true;
        }

        if (dpadVal == Constants.LEFT_DPAD && !isAtBeginning && !dpadPressed) {
            goToDistance -= Constants.DPAD_ADJUSTMENTS;
            dpadPressed = true;
        } else if (dpadVal == Constants.RIGHT_DPAD && !isAtEnd && !dpadPressed) {
            goToDistance += Constants.DPAD_ADJUSTMENTS;
            dpadPressed = true;
        } else if (dpadVal <= fudgeFactorDpad && dpadVal >= -fudgeFactorDpad) {
            dpadPressed = false;
        }

        if (storage.con.Y_BUTTON.getVal()) {
            goToDistance = Constants.SWEET_SPOT_POSITION;
        }

        if (isReloading) {
            goToDistance = Constants.RELOADING_DISTANCE;
        }
    }

    private void updateKickerMotorsEncode() {
        //TODO: add fudge factor
        if (curDistance < goToDistance && !isShooting) {
            storage.data.MOTOR_Winch.setX(Constants.MANIP_SPEED);
            isAtPosition = false;
        } else if (isShooting) {
            storage.data.MOTOR_Winch.setX(Constants.MANIP_LOW_SPEED);
        } else {
            storage.data.MOTOR_Winch.setX(0);
            isAtPosition = true;
            if (isReloading) {
                isReloading = false;
                storage.data.PISTON_Collector.retract();
            }
        }
        curDisStat.updateVal(curDistance);
        goToDisStat.updateVal(goToDistance);
    }

    private void updateKickerLimit() {
        if (storage.con.Y_BUTTON.getLeadingEdge()) {
            isReloading = true;
        }
        if (storage.con.LEFT_STICK_BUTTON.getLeadingEdge())
        {
            isReloading = false;
        }
        curDistance = storage.data.ENCODER_ManipWinch.getDistance();
        if (isReloading) {
            if (storage.data.DI_winchLimit.get()) {
                storage.data.MOTOR_Winch.setX(Constants.MANIP_SPEED);
            } else {
                storage.data.MOTOR_Winch.setX(0);
                isReloading = false;
            }
        }
        if (storage.data.DI_winchLimit.get()) {
            isAtPosition = false;
        } else {
            isAtPosition = true;
        }
        if (isShooting)
        {
            storage.data.MOTOR_Winch.setX(Constants.MANIP_LOW_SPEED);
        }
        isAtPositionStat.updateVal(isAtPosition);
        storage.debug.isShooting.update("" + isShooting);
        storage.debug.isReloading.update("" + isReloading);
        storage.debug.isAtPosition.update("" + isAtPosition);
        curDisStat.updateVal(curDistance);
    }

    private void updateShotPower() {
        if (curDistance > Constants.RELOADING_DISTANCE && curDistance <= Constants.TRUSS_POSITION) {
            shotPowerStat.updateVal("Low");
        } else if (curDistance > Constants.TRUSS_POSITION && curDistance <= Constants.SWEET_SPOT_POSITION) {
            shotPowerStat.updateVal("Medium");
        } else if (curDistance > Constants.TRUSS_POSITION && curDistance <= Constants.MANIP_WINCH_MAX) {
            shotPowerStat.updateVal("High");
        } else {
            shotPowerStat.updateVal("NOT BALLIN");
        }
    }
}
