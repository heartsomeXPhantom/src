/*
 * Created on 10.03.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package net.docliff.models.applicationmodel.guimodel.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class MessageBox extends Dialog
{
	protected int button = SWT.CANCEL;

	protected Display display;

	protected Shell shell;

	protected StyledText text;

	protected Composite saveBarComposite;

	private String userLanguage = "en";

	private de.folt.util.Messages message;
	
	public MessageBox(Shell parent, String header, String messageText, Font font)
	{
		super(parent, SWT.NONE);
		message = de.folt.util.Messages.getInstance(
				"net.docliff.models.applicationmodel.guimodel.editor.XliffEditor", userLanguage);
		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
		display = shell.getDisplay();
		shell.setText(header); //$NON-NLS-1$
		shell.setLayout(new GridLayout(1, true));

		button = SWT.CANCEL;

		shell.addListener(SWT.Close, new Listener()
		{
			public void handleEvent(Event event)
			{
				// button = SWT.CANCEL;
			}
		});

		int iGridData = GridData.GRAB_VERTICAL | GridData.VERTICAL_ALIGN_FILL
				| GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL;

		text = new StyledText(shell, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.READ_ONLY | SWT.RESIZE);
		text.setFont(font);
		text
				.setText("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
		text.setLayoutData(new GridData(iGridData));

		Composite saveBarComposite = new Composite(shell, SWT.BORDER);
		saveBarComposite.setLayout(new GridLayout(3, false));
		saveBarComposite.setLayoutData(new GridData(iGridData));

		int iGridDatattributeFilter = GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL;
		GridData gridttributeFilter = new GridData(iGridDatattributeFilter);

		Button no = new Button(saveBarComposite, SWT.PUSH);
		no.setText(message.getString("NO")); //$NON-NLS-1$
		no.setLayoutData(gridttributeFilter);
		no.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent ev)
			{
				button = SWT.NO;
				shell.close();
			}
		});

		Button yes = new Button(saveBarComposite, SWT.PUSH);
		yes.setText(message.getString("YES")); //$NON-NLS-1$
		yes.setLayoutData(gridttributeFilter);
		yes.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent ev)
			{
				button = SWT.YES;
				shell.close();
			}
		});

		Button cancel = new Button(saveBarComposite, SWT.PUSH);
		cancel.setText(message.getString("CANCEL")); //$NON-NLS-1$
		cancel.setLayoutData(gridttributeFilter);
		cancel.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent ev)
			{
				button = SWT.CANCEL;
				shell.close();
			}
		});

		shell.setDefaultButton(no);
		shell.pack();
		text.setText(messageText);
	}

	/**
	 * @param parent
	 * @param logfilename
	 * @param displayImage
	 */
	public MessageBox(Shell parent, String header, String messageText, Font font, int style)
	{
		super(parent, SWT.NONE);
		message = de.folt.util.Messages.getInstance(
				"net.docliff.models.applicationmodel.guimodel.editor.XliffEditor", userLanguage);
		shell = new Shell(parent, style);
		display = shell.getDisplay();
		shell.setText(header); //$NON-NLS-1$
		shell.setLayout(new GridLayout(1, true));

		button = SWT.CANCEL;
		int iGridData = GridData.GRAB_VERTICAL | GridData.VERTICAL_ALIGN_FILL
				| GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL;

		shell.addListener(SWT.Close, new Listener()
		{
			public void handleEvent(Event event)
			{
				// button = SWT.CANCEL;
			}
		});

		text = new StyledText(shell, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.READ_ONLY | SWT.RESIZE);
		text.setFont(font);
		text
				.setText("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
		text.setLayoutData(new GridData(iGridData));

		int iGridData1 = GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL;

		saveBarComposite = new Composite(shell, SWT.BORDER);
		saveBarComposite.setLayout(new GridLayout(3, false));
		saveBarComposite.setLayoutData(new GridData(iGridData1));

		int iGridDatattributeFilter = GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL;
		GridData gridttributeFilter = new GridData(iGridDatattributeFilter);

		Button no = new Button(saveBarComposite, SWT.PUSH);
		no.setText(message.getString("NO")); //$NON-NLS-1$
		no.setLayoutData(gridttributeFilter);
		no.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent ev)
			{
				button = SWT.NO;
				shell.close();
			}
		});

		Button yes = new Button(saveBarComposite, SWT.PUSH);
		yes.setText(message.getString("YES")); //$NON-NLS-1$
		yes.setLayoutData(gridttributeFilter);
		yes.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent ev)
			{
				button = SWT.YES;
				shell.close();
			}
		});

		Button cancel = new Button(saveBarComposite, SWT.PUSH);
		cancel.setText(message.getString("CANCEL")); //$NON-NLS-1$
		cancel.setLayoutData(gridttributeFilter);
		cancel.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent ev)
			{
				button = SWT.CANCEL;
				shell.close();
			}
		});

		shell.setDefaultButton(no);
		shell.pack();
		text.setText(messageText);
	}

	/**
	 * @return the button
	 */
	public int getButton()
	{
		return button;
	}

	/**
	 * @return the text
	 */
	public StyledText getTextText()
	{
		return text;
	}

	public int open()
	{
		show();
		return button;
	}

	/**
	 * @param text
	 *            the text to set
	 */
	public void setTextText(StyledText text)
	{
		this.text = text;
	}

	public void setTitle(String title)
	{
		shell.setText(title);
	}
	
	public void show()
	{
		shell.open();
		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch())
			{
				display.sleep();
			}
		}
	}
}
