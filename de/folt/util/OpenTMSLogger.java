/*
 * Created on 25.09.2008
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Calendar;
import java.util.Locale;

public class OpenTMSLogger
{
    
    private static boolean bDisplayCaller = true;

    private static final PrintStream conStderr = System.err;
    
    private static final PrintStream conStdout = System.out;

    private static String currentLogFileName = null;

    private static int iLogLevel = 0;
    
    private static int iOpenNumber = 0;
    
    private static OutputStream outStream = null;
    
    private static PrintStream printst = null;
    
    private static PrintStream printst1 = null;
    
    /**
     * close - close the log file and resets to standard output stream
     *  
     */
    public static void close()
    {
        if (iOpenNumber == 1)
        {
            if (System.out != conStdout)
            {
                OpenTMSLogger.println("Closing LogFile #=" + iOpenNumber);
                System.setOut(conStdout);
                System.setErr(conStderr);
                if (outStream != null)
                {
                    try
                    {
                        printst.close();
                        printst1.close();
                        outStream.close();
                        outStream = null;
                        printst = null;
                        printst1 = null;
                        // currentLogFileName = null;
                        iOpenNumber--;
                    }
                    catch (IOException e)
                    {
                        System.out.println(e.getMessage());
                        outStream = null;
                        // currentLogFileName = null;
                    }
                }
            }
        }
        else
        {
            // LogPrint.println("Closing LogFileLevel #=" + iOpenNumber);
            iOpenNumber--;
        }
    }

    /**
     * createLogFileName
     * 
     * @return
     */
    /**
     * createLogFileName - creates a standard log file names "OpenTMS" using createLogFileName(String basicname)
     * @return the log file path
     */
    public static String createLogFileName()
    {
        return createLogFileName("OpenTMS");
    }

    /**
     * createLogFileName - create a (new) log file in the OpenTMS log directory )Property "OpenTMS.log.dir")
     * 
     * @param basicname - the file name of the log file to be created
     * @return the log file path
     */
    public static String createLogFileName(String basicname)
    {
        String name = "";
        String logdir = OpenTMSProperties.getInstance().getOpenTMSProperty("OpenTMS.log.dir");
        @SuppressWarnings("unused")
        String checkfile = logdir + "checkfile";
        if (logdir == null)
            logdir = "";
        if (basicname == null)
            name = "araya";
        else
            name = basicname;

        String curuser = System.getProperty("user.name");
        if (curuser == null)
            curuser = "";

        try
        {
            File file = File.createTempFile(name + "." + curuser + ".", ".log", new File(logdir));
            name = file.getAbsolutePath();
        }
        catch (Exception ex)
        {
            name = logdir + name + curuser;
        }

        currentLogFileName = name;

        return name;
    }

    /**
     * println prints a line to the current log file woth calling hierarchy and date/time information
     * @param string the string to print
     */
    public static void println(String str)
    {
        if ((iOpenNumber < 1) && (currentLogFileName != null))
            setLogFile(currentLogFileName);
        String caller = "";
        if (bDisplayCaller == true)
            caller = OpenTMSSupportFunctions.getCallingMethod(2);
        // Date startTime = Calendar.getInstance().getTime(); // Calendar.getInstance(Locale.US).getTime();
        Calendar cal = Calendar.getInstance(Locale.GERMAN);

        // String logdate = startTime.getDay() + "." + (startTime.getMonth()+1) + "." + (startTime.getYear()+1900) + " " + startTime.getHours() + ":" + startTime.getMinutes() + ":" +
        // startTime.getSeconds();
        String logdate = cal.get(Calendar.DAY_OF_MONTH) + "." + (cal.get(Calendar.MONTH) + 1) + "." + cal.get(Calendar.YEAR) + " " + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE)
                + ":" + cal.get(Calendar.SECOND) + "," + cal.get(Calendar.MILLISECOND);

        System.out.println(iOpenNumber + "/" + logdate + "/" + caller + "/  " + str);
        if ((iOpenNumber < 1) && (currentLogFileName != null))
            close();
        
    }

    /**
     * returnLogFile - returns the current log file name
     * 
     * @return the current log file name
     */
    public static String returnLogFile()
    {
        return currentLogFileName;
    }

    /**
     * returnLogFileName - returns the current log file name
     * 
     * @return the current log file name using returnLogFile()
     */
    public static String returnLogFileName()
    {
        return returnLogFile();
    }
    
    /**
     * returnLogLevel the current log level is returned
     * 
     * @return the current log leven
     */
    public static int returnLogLevel()
    {
        return iLogLevel;
    }
    
    /**
     * setLogFile - set the log file to the standard OpenTMS log file (OpenTMS) using createLogFileName()
     *  
     */
    public static void setLogFile()
    {
        // String logdir = EMXProperties.getInstance().getEMXProperty("eaglememex.log.dir");
        String loglevel = OpenTMSProperties.getInstance().getOpenTMSProperty("opentms.log.loglevel");

        currentLogFileName = createLogFileName();
        if (currentLogFileName == null)
        {
            currentLogFileName = OpenTMSProperties.getInstance().getOpenTMSProperty("opentms.log.file");
            String curuser = System.getProperty("user.name");
            if (curuser != null)
                currentLogFileName = currentLogFileName + "." + curuser;
        }

        setLogFile(currentLogFileName);
        if (loglevel != null)
            iLogLevel = Integer.parseInt(loglevel);
        else
            iLogLevel = 0;
    }
    
    /**
     * setLogFile - sets the log file to a given file name
     * 
     * @param logFileName - the log file name
     */
    public static void setLogFile(String logFileName)
    {
        if (logFileName != null && !logFileName.equals(""))
        {
            try
            {
                File f = new File(logFileName);
                f.createNewFile();
                
                if (outStream == null)
                {
                    outStream = new FileOutputStream(logFileName, true);
                    printst = new PrintStream(outStream);
                    // System.setOut(new PrintStream(outStream));
                    // System.setErr(new PrintStream(outStream));
                    System.setOut(printst);
                    printst1 = new PrintStream(outStream);
                    System.setErr(printst1);

                    currentLogFileName = logFileName;
                    iOpenNumber++;
                    OpenTMSLogger.println("Opening LogFileLevel #=" + iOpenNumber + " with logfile \"" + currentLogFileName + "\"");
                }
                else
                    iOpenNumber++; 
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
                System.out.println("Cannot write log to file " + logFileName);
                outStream = null;
                logFileName = null;
            }
        }
    }
    
    /**
     * setLogLevel - sets the log level to a specific value (int)
     * 
     * @param iLog - the log level to use
     */
    public static void setLogLevel(int iLog)
    {
        iLogLevel = iLog;
    }
}
