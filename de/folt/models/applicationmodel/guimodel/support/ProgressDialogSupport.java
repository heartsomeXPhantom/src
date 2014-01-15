/*
 * Created on 10.12.2005
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.models.applicationmodel.guimodel.support;

import java.lang.reflect.Method;
import java.util.Date;

import org.eclipse.swt.widgets.Shell;

/**
 * @author klemens.waldhoer
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class ProgressDialogSupport
{
	private static Object[] actargs;

	private static Date actTime;

	private static long lstart;

	private CancelTask cancelTask = null;

	private de.folt.util.Messages message;

	private Object ProgressDisplay = null;

	private Method ProgressMethod = null;

	private Method ProgressMethodString = null;

	private Shell shell;

	private String userLanguage = "en";

	@SuppressWarnings("unchecked")
	public ProgressDialogSupport(ProgressDialog progressDialog)
	{
		if (progressDialog == null)
			return;

		message = de.folt.util.Messages.getInstance(
				"de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.datasourceeditor", userLanguage);

		actTime = new Date();
		shell = progressDialog.returnShell();
		lstart = actTime.getTime();
		ProgressDisplay = progressDialog;
		@SuppressWarnings("rawtypes")
		Class prClass = progressDialog.getClass();
		@SuppressWarnings("rawtypes")
		Class[] formparams = new Class[1];
		formparams[0] = Integer.TYPE;
		@SuppressWarnings("rawtypes")
		Class[] formparams1 = new Class[1];
		String x = "";
		formparams1[0] = x.getClass();
		try
		{
			ProgressMethod = prClass.getMethod("updateProgress", formparams);
			ProgressMethodString = prClass.getMethod("updateProgressMessage", formparams1);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			progressDialog.close();
			return;
		}
	}

	/**
	 * @return the cancelTask
	 */
	public CancelTask getCancelTask()
	{
		return cancelTask;
	}

	public Shell returnShell()
	{
		return shell;
	}

	/**
	 * @param cancelTask
	 *            the cancelTask to set
	 */
	public void setCancelTask(CancelTask cancelTask)
	{
		this.cancelTask = cancelTask;
	}

	public void updateProgressIndication(int iCurrentNumber, int iOverallNumbers)
	{
		// now send a message to the update object
		try
		{
			if (ProgressMethod != null)
			{
				actargs = new Object[1];
				String entry = "# " + iCurrentNumber;
				int inum = iCurrentNumber;
				if (inum == 0)
					inum = 1;
				iCurrentNumber = iCurrentNumber % 100;
				if (iCurrentNumber < 0)
					iCurrentNumber = 1;
				else if (iCurrentNumber > 100)
					iCurrentNumber = 100;
				actargs[0] = new Integer(iCurrentNumber);
				actTime = new Date();
				long lact = actTime.getTime();
				lact = lact - lstart;
				String stCurrent = de.folt.util.OpenTMSSupportFunctions.convertToDuration(lact / 1000);
				ProgressMethod.invoke(ProgressDisplay, actargs);
				if (ProgressMethodString != null)
				{
					if (iOverallNumbers > 0)
					{
						// estimate time needed
						long ltimeperentry = lact / inum;
						long rementries = iOverallNumbers - inum;
						long overalltime = ltimeperentry * rementries;
						if (overalltime < 0)
							overalltime = 0;
						overalltime = overalltime / 1000;
						String stOverall = de.folt.util.OpenTMSSupportFunctions.convertToDuration(overalltime);
						entry = entry + "/" + iOverallNumbers + " (" + stCurrent + "/" + stOverall + ")";
					}
					else
					{
						entry = entry + " (" + stCurrent + ")";
					}
					actargs[0] = entry;

					ProgressMethodString.invoke(ProgressDisplay, actargs);
				}
			}

			return;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return;
		}
	}

	public void updateProgressIndication(String entry)
	{
		// now send a message to the update object
		try
		{
			String outmessage = message.getString(entry);
			if (ProgressMethodString != null)
			{
				actargs = new Object[1];
				actargs[0] = outmessage;
				ProgressMethodString.invoke(ProgressDisplay, actargs);
			}

			return;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return;
		}
	}

	public void updateProgressIndicationNoMessage(String entry)
	{
		// now send a message to the update object
		try
		{
			String outmessage = entry;
			if (ProgressMethodString != null)
			{
				actargs = new Object[1];
				actargs[0] = outmessage;
				ProgressMethodString.invoke(ProgressDisplay, actargs);
			}

			return;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return;
		}
	}

}