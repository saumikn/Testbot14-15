/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bt.storage.components;

import edu.wpi.first.wpilibj.AnalogChannel;

/**
 *
 * @author Dlock
 */
public class BTSonar extends AnalogChannel {

    private final double InchesVoltRatio = 70.0/.67;
    private final double InchesValueRatio = 70.0/124.0;;
    private final double MetersVoltRatio;
    private final double MetersValueRatio;
    private double meanValue = 0;
    private double meanVoltage = 0;
    private double SDValue;
    private double SDVoltage;
    private final int AVG_AMOUNT;
    private int arrVoltIndex = 0;
    private int arrValueIndex = 0;
    private boolean voltCompletedArr = false;
    private double[] arrVoltage;
    private int[] arrValue;
    private boolean refine;
    
    public BTSonar(int channel, boolean refine)
    {
        super(channel);
        this.MetersVoltRatio = 1/3;
        this.MetersValueRatio = 1/3;
        this.AVG_AMOUNT = 100;
        this.refine = refine;
        initAverage();
    }
    public BTSonar(int channel, int avgCount,boolean refine) 
    {
        super(channel);
        this.MetersVoltRatio = 1/3;
        this.MetersValueRatio = 1/3;
        this.AVG_AMOUNT = avgCount;
        this.refine = refine;
        initAverage();
    }
    private void initAverage()
    {
        arrVoltage = new double[AVG_AMOUNT];
        arrValue = new int[AVG_AMOUNT];
        for (int i = 0; i < arrVoltage.length; i++)
        {
            arrVoltage[i] = -1;
            arrValue[i] = -1;
        }
    }
    public double getAverageVoltage()
    {
        int total = 0;
        double sum = 0;
        double sdSum = 0;
        double val = this.getVoltage();
        arrVoltage[arrVoltIndex] = val;
        
        if (voltCompletedArr)
        {
            total = arrVoltage.length;
        }
        else
        {
            total = arrVoltIndex;
        }
        
        if (voltCompletedArr)
        {
            for (int i = 0; i<arrVoltage.length; i++)
            {
                sum += arrVoltage[i];
            }
            meanVoltage = sum/total;
        }
        if (arrVoltIndex < arrVoltage.length-1)
        {
            arrVoltIndex++;
        }
        else
        {
            arrVoltIndex = 0;
            voltCompletedArr = true;
        }
        return meanVoltage;
    }
//        if (voltCompletedArr && refine)
//        {
//            for (int i = 0; i<arrVoltage.length; i++)
//            {
//                sdSum += (arrVoltage[i]-meanVoltage) * (arrVoltage[i]-meanVoltage);
//            }
//            SDVoltage = Math.sqrt((sdSum)/(arrVoltage.length));
//        }
//        if (voltCompletedArr && refine)
//        {
//            System.out.println("Mean: "+meanVoltage+" Standard Diviation: "+SDVoltage);
//            if ((meanVoltage+3*SDVoltage) > val && val > (meanVoltage-3*SDVoltage))
//                arrVoltage[arrVoltIndex] = val;
//            else
//                return meanVoltage;
//        }
//        else
//        {
//            arrVoltage[arrVoltIndex] = val;
//        }
    public int getAverageValue()
    {
        double sum = 0;
        double count = 0;
        arrValue[arrValueIndex] = this.getValue();
        if (arrValueIndex < arrValue.length-1)
            arrValueIndex++;
        else
            arrValueIndex = 0;
        for (int i = 0; i<arrValue.length; i++)
        {
            if (arrValue[i] != -1)
            {
                sum = sum+arrValue[i];
                count++;
            }
            else
            {
                break;
            }
        }
        meanValue = sum/count;
        return (int)meanValue;
    }
    public double getDisINVolt()
    {
        return (double)this.getAverageVoltage()*(double)InchesVoltRatio;
    }
    public double getDisINValue()
    {
        return (double)this.getAverageValue()*(double)InchesValueRatio;
    }
    public double getDisCMVolt()
    {
        return this.getAverageVoltage()*MetersVoltRatio;
    }
    public double getRawDisIN()
    {
        return (double)this.getVoltage()*(double)InchesVoltRatio;
    }
    public double getDisCMValue()
    {
        return this.getAverageValue()*MetersValueRatio;
    }
}
