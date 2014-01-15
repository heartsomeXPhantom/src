/*
 * Created on 11.12.2005
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.models.applicationmodel.guimodel.support;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * @author klemens.waldhoer
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class CancelTaskDialog extends Dialog
{
	@SuppressWarnings("unused")
	private static Shell inShell = null;

	private static Shell shell = null;

	private boolean bCancelled = false;

	private boolean bHalted = false;

	private boolean bTerminateStatus = false;

	private Button cancel;

	private Display display;

	@SuppressWarnings("unused")
	private Button halt;

	private de.folt.util.Messages message;

	@SuppressWarnings("unused")
	private String messageText;

	@SuppressWarnings("unused")
	private String title;

	private String userLanguage = "en";

	public CancelTaskDialog(Shell parent, String title, String messageText)
	{
		super(parent, SWT.NONE);

		message = de.folt.util.Messages.getInstance(
				"de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.datasourceeditor", userLanguage);

		inShell = parent;
		this.messageText = messageText;
		this.title = title;
		bTerminateStatus = false;

		bCancelled = false;
		bHalted = false;

		shell = null;

		// display = new Display();

		// shell = new Shell(display, SWT.DIALOG_TRIM | SWT.MODELESS);
		shell = new Shell(SWT.DIALOG_TRIM | SWT.MODELESS);
		shell.setLayout(new GridLayout(1, false));
		shell.setText(message.getString(title));
		shell.setSize(300, 200);
		display = shell.getDisplay();

		Label prompt = new Label(shell, SWT.NONE);
		prompt.setText(message.getString(messageText));

		Composite composite = new Composite(shell, SWT.NONE);
		GridLayout grid1 = new GridLayout(2, false);
		composite.setLayout(grid1); // (new GridLayout(2, false));
		GridData gridd1 = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
		composite.setLayoutData(gridd1);

		cancel = new Button(composite, SWT.PUSH);
		cancel.setText("11111111111111111111111111111");

		cancel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		cancel.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				bCancelled = true;
				shell.close();
			}

		});

		/*
		 * halt = new Button(composite, SWT.PUSH);
		 * halt.setText("11111111111111111111111111111");
		 * 
		 * halt.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		 * halt.addSelectionListener(new SelectionAdapter() { public void
		 * widgetSelected(SelectionEvent e) { bHalted = !bHalted; if (bHalted)
		 * halt.setText(Messages.getString("Continue")); else
		 * halt.setText(Messages.getString("Halt")); } });
		 */

		shell.pack();
		// halt.setText(Messages.getString("Halt"));
		cancel.setText(message.getString("Cancel"));
	}

	public void close()
	{
		shell.close();
	}

	public boolean isCancelled()
	{
		return bCancelled;
	}

	public boolean isHalted()
	{
		return bHalted;
	}

	public boolean isTerminated()
	{
		return bTerminateStatus;
	}

	public void show()
	{
		display.asyncExec(new Runnable()
		{
			public void run()
			{
				shell.open();
				/*
				 * while (!shell.isDisposed()) { if (!display.readAndDispatch())
				 * { display.sleep(); } }
				 */
			}
		});

	}
}