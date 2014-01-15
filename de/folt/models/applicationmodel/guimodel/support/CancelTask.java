/*
 * Created on 10.12.2005
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.models.applicationmodel.guimodel.support;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * @author klemens.waldhoer
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class CancelTask implements Runnable
{
	private static Shell inShell;
	private static CancelTaskDialog taskdialog = null;
	private boolean bCancelled = false;
	private boolean bHalted = false;
	private boolean bTerminateStatus = false;
	@SuppressWarnings("unused")
	private Button cancel;
	@SuppressWarnings("unused")
	private Display display;

	@SuppressWarnings("unused")
	private Button halt;
	private String message;

	private String title;

	public CancelTask(Shell parent, String title, String message)
	{
		inShell = parent;
		this.message = message;
		this.title = title;
		bTerminateStatus = false;
		display = inShell.getDisplay();
		bCancelled = false;
		bTerminateStatus = false;
		bHalted = false;
	}

	public void close()
	{
		inShell.getDisplay().asyncExec(new Runnable()
		{
			public void run()
			{
				taskdialog.close();
			}
		});
	}

	public boolean isCancelled()
	{
		if (taskdialog != null)
			bCancelled = taskdialog.isCancelled();
		return bCancelled;
	}

	public boolean isHalted()
	{
		if (taskdialog != null)
			bHalted = taskdialog.isHalted();
		return bHalted;
	}

	public boolean isTerminated()
	{
		if (taskdialog != null)
			bTerminateStatus = taskdialog.isTerminated();
		return bTerminateStatus;
	}

	public void run()
	{
		bTerminateStatus = false;
		inShell.getDisplay().syncExec(new Runnable()
		{
			public void run()
			{
				taskdialog = null;
				taskdialog = new CancelTaskDialog(inShell, title, message);
				taskdialog.show();
			}
		});

		bTerminateStatus = true;
		return;
	}

	public void setCancelled(boolean b)
	{
		bCancelled = b;
	}

	public void setTerminated(boolean b)
	{
		bTerminateStatus = b;
	}
}
