/*
 * Created on Jan 1, 2004
 */
package de.folt.models.applicationmodel.guimodel.editor.datasourceeditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


public class Login extends Dialog
{

	private static Shell	shell;

	private static String	user;

	public Login(Shell parent, String currentUser, de.folt.util.Messages message)
	{
		super(parent, SWT.NONE);

		user = currentUser;

		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setLayout(new GridLayout(1, false));
		shell.setText(message.getString("Log_as_Different_User"));

		Composite top = new Composite(shell, SWT.NONE);
		top.setLayout(new GridLayout(2, false));

		Label label = new Label(top, SWT.NONE);
		label.setText(message.getString("User_Name"));

		final Text text = new Text(top, SWT.BORDER);
		GridData data = new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL);
		data.widthHint = 150;
		text.setLayoutData(data);
		text.setText(currentUser);

		//
		// Buttons
		//
		Composite bottom = new Composite(shell, SWT.BORDER);
		bottom.setLayout(new GridLayout(2, true));
		bottom.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL));

		Button okButton = new Button(bottom, SWT.PUSH);
		okButton.setText(message.getString("&Accept_104"));
		okButton.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL));
		okButton.addSelectionListener(new SelectionListener()
		{

			public void widgetDefaultSelected(SelectionEvent arg0)
			{
				; // do nothing
			}

			public void widgetSelected(SelectionEvent arg0)
			{
				if (text.getText().length() > 0)
				{
					user = text.getText();
					shell.close();
				}
			}
		});

		Button cancel = new Button(bottom, SWT.PUSH);
		cancel.setText(message.getString("_&Cancel_10"));
		cancel.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL));
		cancel.addSelectionListener(new SelectionListener()
		{

			public void widgetDefaultSelected(SelectionEvent arg0)
			{
				; // do nothing
			}

			public void widgetSelected(SelectionEvent arg0)
			{
				shell.close();
			}
		});

		shell.pack();
	}

	/**
	 * @return Returns the user.
	 */
	public String getUser()
	{
		return user;
	}

	public void show()
	{
		shell.open();
		while (!shell.isDisposed())
		{
			if (!shell.getDisplay().isDisposed())
			{
				if (!shell.getDisplay().readAndDispatch())
				{
					shell.getDisplay().sleep();
				}
			}
		}
	}
}
