package de.folt.test;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class WebBrowser
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub
		Browser browser;
		Shell shell = new Shell();
		Display display =shell.getDisplay();
		shell.setText("Test WebBrowser");
		try
		{
			browser = new Browser(shell, SWT.NONE);
			browser.setUrl("http://www.waldhor.com");
			browser.setSize(500, 600);
			browser.pack();

		}
		catch (SWTError e)
		{
			MessageBox messageBox = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
			messageBox.setMessage("Browser cannot be initialized.");
			messageBox.setText("Exit");
			messageBox.open();
			System.exit(-1);
		}
		shell.open();
		while (!shell.isDisposed())
		{
			if (!display.isDisposed())
			{
				if (!display.readAndDispatch())
				{
					display.sleep();
				}
			}
		}

		if (!display.isDisposed())
		{
			display.dispose();
		}
	}

}
