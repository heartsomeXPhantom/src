package de.folt.models.applicationmodel.guimodel.support;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

import de.folt.util.ColorTable;

public class ProgressDialog
{

	public static short DOUBLE_BARS = 2;

	private static String message1;

	public static short NO_BARS = 0;

	public static short SINGLE_BAR = 1;

	private static int value1;

	private Display display;

	private ProgressBar mainBar;

	private Label mainLabel;

	private de.folt.util.Messages message;

	private ProgressDialogSupport pdSupport = null;

	private ProgressBar progressBar;

	private Label progressLabel;

	Shell proShell;

	Shell shell;

	private Label titleLabel;

	private String userLanguage = "en";

	public ProgressDialog(Shell currentShell, String title, String progressMessage, short style)
	{

		message = de.folt.util.Messages.getInstance(
				"de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.datasourceeditor", userLanguage);

		display = currentShell.getDisplay();

		proShell = new Shell(display, SWT.MODELESS /* SWT.APPLICATION_MODAL */| SWT.TITLE); // SWT.ON_TOP);
		proShell.setCursor(new Cursor(proShell.getDisplay(), SWT.CURSOR_WAIT));
		proShell.setLayout(new GridLayout());
		proShell.setText(message.getString("progress.dialog.title"));

		Point loc = currentShell.getLocation();
		loc.x = loc.x + 20;
		loc.y = loc.y + 20;
		proShell.setLocation(loc);

		shell = currentShell;
		shell.setCursor(new Cursor(proShell.getDisplay(), SWT.CURSOR_WAIT));

		Composite holder = new Composite(proShell, SWT.BORDER);
		holder.setLayout(new GridLayout());
		holder.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));

		titleLabel = new Label(holder, SWT.BOLD);
		titleLabel.setText(title);
		titleLabel.setBackground(ColorTable.getInstance(display, "darkgrey")); // dull
		// green
		titleLabel.setForeground(ColorTable.getInstance(display, "white")); // white
		titleLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));

		if (style == DOUBLE_BARS)
		{
			mainLabel = new Label(holder, SWT.NONE);
			mainLabel.setText(progressMessage);

			mainBar = new ProgressBar(holder, SWT.NONE);
			GridData data = new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL);
			data.widthHint = 500;
			mainBar.setLayoutData(data);
			mainBar.setMinimum(0);
			mainBar.setMaximum(100);
		}
		else
		{
			mainLabel = null;
			mainBar = null;
		}

		progressLabel = new Label(holder, SWT.NONE);
		GridData data = new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL);
		data.widthHint = 500;
		progressLabel.setLayoutData(data);
		progressLabel.setText(progressMessage);

		if (style != NO_BARS)
		{
			progressBar = new ProgressBar(holder, SWT.NONE);
			data = new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL);
			data.widthHint = 500;
			progressBar.setLayoutData(data);
			progressBar.setMinimum(0);
			progressBar.setMaximum(100);
		}
		proShell.pack();
	}

	public void close()
	{
		display.syncExec(new Runnable()
		{
			public void run()
			{
				setPdSupport(null);
				shell.setCursor(new Cursor(proShell.getDisplay(), SWT.CURSOR_ARROW));
				proShell.close();
				proShell.dispose();
			}
		});
	}

	/**
	 * @return the pdSupport
	 */
	public ProgressDialogSupport getPdSupport()
	{
		return pdSupport;
	}

	/**
	 * getTitle returns the title text of the title label
	 * 
	 * @return the title text
	 */
	public String getTitle()
	{
		return titleLabel.getText();
	}

	/**
	 * @return the titleLabel
	 */
	public Label getTitleLabel()
	{
		return titleLabel;
	}

	public void open()
	{
		proShell.open();
		display.update();
	}

	public Shell returnShell()
	{
		return proShell;
	}

	/**
	 * @param pdSupport
	 *            the pdSupport to set
	 */
	public void setPdSupport(ProgressDialogSupport pdSupport)
	{
		this.pdSupport = pdSupport;
	}

	/**
	 * setTitle set the title text of the title label
	 * 
	 * @param titleLabel
	 *            the new text for the title label
	 */
	public void setTitle(String titleLabel)
	{
		this.titleLabel.setText(titleLabel);
	}

	/**
	 * @param titleLabel
	 *            the titleLabel to set
	 */
	public void setTitleLabel(Label titleLabel)
	{
		this.titleLabel = titleLabel;
	}

	public void showFinish(String finishMessage)
	{
		message1 = finishMessage;
		display.syncExec(new Runnable()
		{
			public void run()
			{
				progressLabel.setText(message1);
				display.update();
				shell.setCursor(new Cursor(proShell.getDisplay(), SWT.CURSOR_ARROW));
				proShell.setCursor(new Cursor(proShell.getDisplay(), SWT.CURSOR_ARROW));
			}
		});
	}

	public void updateMain(int value)
	{
		value1 = value;
		display.syncExec(new Runnable()
		{
			public void run()
			{
				if (mainBar == null)
					return;
				mainBar.setSelection(value1);
				display.update();
				while (display.readAndDispatch())
				{
					// do nothing
				}
			}
		});
	}

	public void updateMainMessage(String message)
	{
		message1 = message;
		display.syncExec(new Runnable()
		{
			public void run()
			{
				if (mainLabel == null)
					return;
				mainLabel.setText(message1);
				display.update();
				while (display.readAndDispatch())
				{
					// do nothing
				}
			}
		});
	}

	public void updateProgress(int value)
	{
		value1 = value;
		display.syncExec(new Runnable()
		{
			public void run()
			{
				progressBar.setSelection(value1);
				display.update();
				while (display.readAndDispatch())
				{
					// do nothing
				}
			}
		});
	}

	public void updateProgress(int value, String message)
	{
		message1 = message;
		value1 = value;
		display.syncExec(new Runnable()
		{
			public void run()
			{
				progressBar.setSelection(value1);
				progressLabel.setText(message1);
				display.update();
				while (display.readAndDispatch())
				{
					// do nothing
				}
			}
		});
	}

	public void updateProgressMessage(String message)
	{
		message1 = message;
		display.syncExec(new Runnable()
		{
			public void run()
			{
				progressLabel.setText(message1);
				display.update();
				while (display.readAndDispatch())
				{
					// do nothing
				}
			}
		});
	}
}