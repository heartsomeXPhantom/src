/*
 * Created on 04.09.2006
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.util;

/**
 * This class implements a timer for measuring the time need for operations. A timer can be started, stopped an continued.<br>
 * 
 * <pre>
 * Timer timer = new Timer();
 * timer.start();
 * timer.stop();
 * timer.continue();
 * 
 * ...
 * </pre>
 * 
 * @author klemens
 */
public class Timer
{
    private long continueTime = 0l;

    private long deductTime = 0;

    private long endTime = 0l;

    protected int iLogLevel = 0;

    protected int iProperty = 0;

    private long startTime = 0l;

    private long stopTime = 0l;

    /**
     * creates a times
     */
    public Timer()
    {
        continueTime = 0l;
        deductTime = 0;
        endTime = 0l;
        iLogLevel = 0;
        iProperty = 0;

        startTime = 0l;

        stopTime = 0l;
    }

    /**
     * continueTimer continues a stopped timer
     */
    public void continueTimer()
    {
        if (startTime == 0)
        	this.startTimer();
    	continueTime = System.currentTimeMillis();
        endTime = System.currentTimeMillis();
        stopTime = continueTime - stopTime;
        deductTime = deductTime + stopTime;
    }

    /**
     * endTimer sets the end time of timer at the current time.
     */
	public void endTimer()
    {
        endTime = System.currentTimeMillis() - deductTime;
    }

    /**
     * @return Returns the continueTime.
     */
    public long getContinueTime()
    {
        return continueTime;
    }

    /**
     * @return Returns the deductTime.
     */
    public long getDeductTime()
    {
        return deductTime;
    }

    /**
     * @return Returns the endTime.
     */
    public long getEndTime()
    {
        return endTime;
    }

    /**
     * @return Returns the startTime.
     */
    public long getStartTime()
    {
        return startTime;
    }

    /**
     * @return Returns the stopTime.
     */
    public long getStopTime()
    {
        return stopTime;
    }

    /**
     * @param continueTime
     *            The continueTime to set.
     */
    public void setContinueTime(long continueTime)
    {
        this.continueTime = continueTime;
    }

    /**
     * @param deductTime
     *            The deductTime to set.
     */
    public void setDeductTime(long deductTime)
    {
        this.deductTime = deductTime;
    }

    /**
     * @param endTime
     *            The endTime to set.
     */
    public void setEndTime(long endTime)
    {
        this.endTime = endTime;
    }

    /**
     * @param startTime
     *            The startTime to set.
     */
    public void setStartTime(long startTime)
    {
        this.startTime = startTime;
    }

    /**
     * @param stopTime
     *            The stopTime to set.
     */
    public void setStopTime(long stopTime)
    {
        this.stopTime = stopTime;
    }

    /**
     * startTimer starts a timers
     */
    public void startTimer()
    {
        startTime = System.currentTimeMillis();
        stopTime = System.currentTimeMillis();
    }

    /**
     * stopTimer stops a timer
     */
    public void stopTimer()
    {
        stopTime = System.currentTimeMillis();
    }

    /**
     * timeNeeded
     * 
     * @return
     */
    public long timeNeeded()
    {
        return (endTime - startTime);
    }

    /**
     * timerString returns a formatted timer string:<br>
     * Format produced:<br>
     * 
     * <pre>
     * str = &quot;Time needed for &quot; + string + &quot; &quot; + timeneeded + &quot; ms (&quot; + seconds + &quot; sec/&quot; + minutes + &quot; min)&quot;;
     * </pre>
     * 
     * @param string
     *            description string to add
     * @return the formatted timer string:
     */
    public String timerString(String string)
    {
        String str = "";
        long endTime = System.currentTimeMillis();
        long timeneeded = (endTime - startTime);
        float minutes = (float) timeneeded / (float) 60000;
        float seconds = (float) timeneeded / (float) 1000;

        str = "Time needed for " + string + " " + timeneeded + " ms (" + seconds + " sec/" + minutes + " min)";
        return str;
    }

    /**
     * timerString returns a formatted timer string. The parameter iCount is used to determine the average time a number (iCount) of operations needed per operation (e.g. for determining the average
     * time for loading 1000 MonoLingualObjects from a database.<br>
     * Format produced:<br>
     * 
     * <pre>
     * str = &quot;Time needed for &quot; + string + &quot; &quot; + timeneeded + &quot; ms (&quot; + seconds + &quot; sec/&quot; + minutes + &quot; min) for &quot; + iCount + &quot; operations. Average was &quot; + average + &quot; ms. &quot;;
     * </pre>
     * 
     * @param string
     *            the description string to add
     * @param iCount
     *            the number of operations for which the mean operation time should be computed.
     * @return the formatted timer string
     */
    public String timerString(String string, long iCount)
    {
        String str = "";
        long endTime = System.currentTimeMillis();
        long timeneeded = (endTime - startTime);
        float average = 0;
        if (iCount != 0)
            average = (float) timeneeded / (float) iCount;
        float minutes = (float) timeneeded / (float) 60000;
        float seconds = (float) timeneeded / (float) 1000;

        str = "Time needed for " + string + " " + timeneeded + " ms (" + seconds + " sec/" + minutes + " min) for " + iCount + " operations. Average was " + average + " ms. ";
        return str;
    }

}
