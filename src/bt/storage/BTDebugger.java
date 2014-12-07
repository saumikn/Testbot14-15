/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bt.storage;

import bt.storage.Constants.DebugLocation;
import bt.storage.health.BTStatGroup;
import bt.storage.health.BTStatistic.BTStatString;
import com.sun.squawk.microedition.io.FileConnection;
import java.io.DataOutputStream;
import java.io.PrintStream;
import javax.microedition.io.Connector;

/**
 *
 * @author Dlock
 */
public class BTDebugger {
    public String[][] DEBUGARR;
    private String healthName= "DEBUG: ";
    private BTStatGroup debugG = new BTStatGroup(healthName);
        public BTDebugStat LStick = new BTDebugStat(Constants.DebugLocation.BTDriveTrain,"Left Stick", "");
        public BTDebugStat RStick = new BTDebugStat(Constants.DebugLocation.BTDriveTrain,"Right Stick", "");
        public BTDebugStat LeftVelocity = new BTDebugStat(Constants.DebugLocation.BTDriveTrain,"Left Side Calculated Velocity", "");
        public BTDebugStat RightVelocity = new BTDebugStat(Constants.DebugLocation.BTDriveTrain,"Right Side Calculated Velocity", "");
        
        public BTDebugStat HingeMove = new BTDebugStat(Constants.DebugLocation.BTManipulator,"Is Hinge Moving: ", "");
        public BTDebugStat isShooting = new BTDebugStat(Constants.DebugLocation.BTManipulator,"isShooting", "");
        public BTDebugStat isReloading = new BTDebugStat(Constants.DebugLocation.BTManipulator,"isReloading", "");
        public BTDebugStat isCollecting = new BTDebugStat(Constants.DebugLocation.BTManipulator,"isCollecting", "");
        public BTDebugStat isAtPosition = new BTDebugStat(Constants.DebugLocation.BTManipulator,"isAtPosition", "");
    boolean debugMode;
    int debugCount = 0;
    double timeCount = 0;
    public double CYCLE = 0;
    public long TIME = 0;
    public long STIME = 0;
    
    PrintStream out;
    DataOutputStream theFile;
    FileConnection fc;
    public BTDebugger()
    {
        DEBUGARR = new String[Constants.DEBUG_CAP_LIMIT][5];
        this.debugMode = Constants.DEBUGMODE;
        STIME = System.currentTimeMillis();
    }
    public String getLatestMsg()
    {
        return "["+DEBUGARR[debugCount][0]+"]["+DEBUGARR[debugCount][1]+"]["+DEBUGARR[debugCount][2]+"]: "+DEBUGARR[debugCount][3];
    }
    public void write(Constants.DebugLocation location, Constants.Severity severity, String txt)
    {
        
        String time;
        time = getCurrentSysTime();
        int printCycle = 0;
        
        DEBUGARR[debugCount][0] = time;
        DEBUGARR[debugCount][1] = location.toString();
        DEBUGARR[debugCount][2] = severity.toString();
        DEBUGARR[debugCount][3] = txt;
        DEBUGARR[debugCount][4] = Integer.toString(printCycle);
        
        if (debugMode && printCycle == 0)
            System.out.println(getLatestMsg());
        
        if (debugCount < DEBUGARR.length)
            debugCount++;
        else
            debugCount = 0;
    }
    public void write(Constants.DebugLocation location, Constants.Severity severity, String txt, int printCycle)
    {
        
        String time;
        time = getCurrentSysTime();
        
        if (printCycle < 0)
        {
            printCycle = 0;
        }
        
        DEBUGARR[debugCount][0] = time;
        DEBUGARR[debugCount][1] = location.toString();
        DEBUGARR[debugCount][2] = severity.toString();
        DEBUGARR[debugCount][3] = txt;
        DEBUGARR[debugCount][4] = Integer.toString(printCycle);
        
        if (debugMode && printCycle == 0)
            System.out.println(getLatestMsg());
        if (debugCount < DEBUGARR.length)
            debugCount++;
        else
            debugCount = 0;
    }
    
    public void printDebugWARNING()
    {
        for (int x = 0; x <=debugCount; x++)
        {
            if (DEBUGARR[x][0] != null)
                System.out.println("["+DEBUGARR[x][0]+"]["+DEBUGARR[x][1]+"]["+DEBUGARR[x][2]+"]: "+DEBUGARR[x][3]);
            else
                return;
        }
    }
    
    public String getCurrentSysTime()
    {
        long milliseconds = System.currentTimeMillis()-STIME;
        
        int tsecs= (int) (milliseconds % 1000);
        int secs = (int) (milliseconds / 1000) % 60;
        int mins = (int) ((milliseconds / (1000*60)) % 60);
         
        
        
        return mins+":"+secs+":"+tsecs;
    }
    public void updateCycles()
    {
        CYCLE++;
        TIME = System.currentTimeMillis()-STIME;
        for (int i = 0; i<DEBUGARR.length; i++)
        {
//            if (debugMode && !(Integer.parseInt(DEBUGARR[i][4]) <= 0) && CYCLE % Integer.parseInt(DEBUGARR[i][4]) == 0)
//            {
//                System.out.println("["+DEBUGARR[debugCount][0]+"]["+DEBUGARR[debugCount][1]+"]["+DEBUGARR[debugCount][2]+"]: "+DEBUGARR[debugCount][3]);
//            }
        }
    }
    public double getCyclesperSecond()
    {
        long tempTime = TIME;
        updateCycles();
        double cperS = CYCLE/TIME;
        return cperS;
    }
    public void intoFile()
    {
        try
        {
            fc = (FileConnection)Connector.open("file:///Debugoutput.txt",Connector.WRITE);
            fc.create();
            theFile = fc.openDataOutputStream();
        }
        catch(Exception e){
            write(Constants.DebugLocation.BTDebug,Constants.Severity.WARNING,"Debug unable to write to file");
        }
    }
    public class BTDebugStat
    {
        
        private String val;
        private String startVal;
        private String name;
        private DebugLocation loc;
        private BTStatString output;
        private BTDebugStat(DebugLocation loc, String name, String startVal)
        {
            this.name = name;
            this.loc = loc;
            this.startVal = startVal;
//            if (Constants.DEBUGMODE)
//            {
//                SmartDashboard.putString(healthName+" ("+loc.toString()+") ["+name+"] ", startVal);
//            }
            output = debugG.newStringStat(healthName+" ("+loc.toString()+") ["+name+"] ", startVal, Constants.DEBUGMODE);
        }
        public void update(String val)
        {
            output.updateVal(val);
//            if (Constants.DEBUGMODE)
//            {
//                SmartDashboard.putString(healthName+" ("+loc.toString()+") ["+name+"] ", startVal);
//            }
        }
    }
}
