/*
 * Created on 03.08.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.models.applicationmodel.guimodel.editor.datasourceeditor;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

import de.folt.models.datamodel.DataSourceInstance;
import de.folt.models.datamodel.DataSourceProperties;
import de.folt.models.datamodel.multipledatasource.MultipleDataSource;
import de.folt.util.OpenTMSException;

/**
 * @author klemens
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class ManageMultipleDataSource extends Dialog
{

	/**
	 * @param parent
	 */
	public ManageMultipleDataSource(Shell parent)
	{
		super(parent);
	}

	private Shell shell = null;

	private de.folt.util.Messages message;

	private Display display;

	private List table = null;

	private String dataSource = "";

	private StyledText sourceNameText;

	/**
	 * @param shell
	 * @param datasource
	 */
	@SuppressWarnings("unused")
	public ManageMultipleDataSource(Shell parent, String dataSourceName)
	{
		super(parent);
		final MultipleDataSource ds;
		try
		{
			shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
			shell.setLayout(new GridLayout(1, false));
			message = de.folt.util.Messages.getInstance("de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.datasourceeditor", "en");
			display = parent.getDisplay();
			shell.setText(message.getString("manageMultipleDataSource"));

			DataSourceProperties dataProp = new DataSourceProperties();
			dataProp.put("loadDataSource", "false");
			dataProp.put("dataSourceName", dataSourceName);
			dataProp.setDataSourceProperty("dataModelClass", de.folt.models.datamodel.multipledatasource.MultipleDataSource.class.getName());
			ds = (MultipleDataSource) DataSourceInstance.createInstance(dataSourceName, dataProp);
			Vector<String> datasourcenames = ds.getDataSourceNames();

			Composite top = new Composite(shell, SWT.BORDER);
			top.setLayout(new GridLayout(2, true));
			top.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			Label sourceNameLabel = new Label(top, SWT.NONE);
			GridData slayoutDataTmx = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL);
			slayoutDataTmx.horizontalSpan = 2;
			sourceNameLabel.setLayoutData(slayoutDataTmx);
			sourceNameLabel.setText(message.getString("Newdatasource"));

			sourceNameText = new StyledText(top, SWT.BORDER);
			GridData solayoutDataTmx = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL);
			solayoutDataTmx.horizontalSpan = 2;
			sourceNameText.setLayoutData(solayoutDataTmx);

			sourceNameText.setEditable(false);
			sourceNameText.setEnabled(false);

			Button choose = new Button(top, SWT.PUSH);
			choose.setText(message.getString("chooseDateSource"));
			choose.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			choose.addSelectionListener(new SelectionAdapter()
			{
				public void widgetSelected(SelectionEvent e)
				{
					try
					{
						ChooseDataSourceDialog chooser = new ChooseDataSourceDialog(shell, false);
						chooser.show();
						if (chooser.getDataSource() != null)
						{
							sourceNameText.setText(chooser.getDataSource());
						}
					}
					catch (Exception ex)
					{
						ex.printStackTrace();
					}
				}
			});

			Button add = new Button(top, SWT.PUSH);
			add.setText(message.getString("addMultipleDataSource"));
			add.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			add.addSelectionListener(new SelectionAdapter()
			{
				public void widgetSelected(SelectionEvent e)
				{
					try
					{
						String name = sourceNameText.getText();
						if (name.equals(""))
							return;
						if (ds.addDataSource(name))
							table.add(name);
					}
					catch (Exception ex)
					{
						ex.printStackTrace();
					}
				}
			});

			Label promptdbs = new Label(shell, SWT.NONE);
			GridData layoutDataTmx = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL);
			layoutDataTmx.horizontalSpan = 1;
			promptdbs.setLayoutData(layoutDataTmx);
			promptdbs.setText(message.getString("OpenTMS_database_list") + "                                                       ");

			table = new List(shell, SWT.BORDER | SWT.V_SCROLL);

			GridData data = new GridData(GridData.FILL_HORIZONTAL);
			data.horizontalSpan = 1;
			table.setLayoutData(data);
			table.addSelectionListener(new SelectionAdapter()
			{
				public void widgetSelected(SelectionEvent e)
				{

				}
			});
			table.addMouseListener(new MouseListener()
			{
				public void mouseDoubleClick(MouseEvent e)
				{
					try
					{
						int selected = table.getSelectionIndex();
						if (selected >= 0)
						{
							dataSource = (String) table.getItem(selected);
						}
					}
					catch (Exception ex)
					{
						ex.printStackTrace();
					}
					shell.close();
				}

				@Override
				public void mouseDown(MouseEvent arg0)
				{

				}

				@Override
				public void mouseUp(MouseEvent arg0)
				{

				}
			});

			for (int i = 0; i < datasourcenames.size(); i++)
			{
				table.add(datasourcenames.get(i));
			}

			Composite bottom = new Composite(shell, SWT.BORDER);
			bottom.setLayout(new GridLayout(2, true));
			bottom.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			Button remove = new Button(bottom, SWT.PUSH);
			remove.setText(message.getString("removeMultipleDataSource"));
			remove.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			remove.addSelectionListener(new SelectionAdapter()
			{
				public void widgetSelected(SelectionEvent e)
				{
					try
					{
						int selected = table.getSelectionIndex();
						if (selected >= 0)
						{
							dataSource = (String) table.getItem(selected);
							ds.removeDataSource(dataSource);
							table.remove(dataSource);
						}
					}
					catch (Exception ex)
					{
						ex.printStackTrace();
					}
				}
			});

			Button close = new Button(bottom, SWT.PUSH);
			close.setText(message.getString("Cl&ose"));
			close.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			close.addSelectionListener(new SelectionAdapter()
			{
				public void widgetSelected(SelectionEvent e)
				{
					dataSource = null;
					shell.close();
				}
			});

			shell.pack();

		}
		catch (OpenTMSException e)
		{
			e.printStackTrace();
			return;
		}
		if (ds == null)
			return;
	}

	/**
	 * show
	 */
	public void show()
	{
		shell.open();
		shell.forceActive();
		while (!shell.isDisposed())
		{
			if (this.display.readAndDispatch())
			{
				this.display.sleep();
			}
		}
	}

}
