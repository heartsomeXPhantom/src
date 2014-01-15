/*
 * Created on 20.03.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.models.applicationmodel.guimodel.editor.datasourceeditor;

import java.io.File;
import java.util.Collections;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import org.eclipse.swt.SWT;
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

import de.folt.models.applicationmodel.guimodel.support.ProgressDialogSupport;
import de.folt.models.datamodel.DataSourceConfigurations;
import de.folt.util.OpenTMSProperties;

public class ChooseDataSourceDialog extends Dialog
{
	public class ExportObserver implements Observer
	{
		private ProgressDialogSupport pdSupport;

		/**
		 * @param pdSupport
		 */
		public ExportObserver(ProgressDialogSupport pdSupport)
		{
			super();
			this.pdSupport = pdSupport;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Observer#update(java.util.Observable,
		 * java.lang.Object)
		 */
		@SuppressWarnings("unchecked")
		@Override
		public void update(Observable arg0, Object arg1)
		{
			Vector<Integer> vec = (Vector<Integer>) arg1;
			int iPos = vec.get(1);
			int iSize = vec.get(0);
			pdSupport.updateProgressIndication(iPos, iSize);
		}
	}

	private Shell shell;

	private boolean bChooseMultiple = false;

	private String dataSource = null;

	@SuppressWarnings("unused")
	private String dataSourceDisplayType = null;

	private Vector<String> dataSources = null;

	private Display display;

	private de.folt.util.Messages message;

	private final List table;

	public ChooseDataSourceDialog(Shell parent, boolean bChooseMultiple)
	{
		super(parent);
		dataSources = new Vector<String>();
		this.bChooseMultiple = bChooseMultiple;
		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setLayout(new GridLayout(1, false));
		message = de.folt.util.Messages
				.getInstance(
						"de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.datasourceeditor",
						"en");
		display = parent.getDisplay();
		shell.setText(message.getString("chooseDataSource"));

		Label promptdbs = new Label(shell, SWT.NONE);
		GridData layoutDataTmx = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL);
		layoutDataTmx.horizontalSpan = 1;
		promptdbs.setLayoutData(layoutDataTmx);
		promptdbs.setText(message.getString("OpenTMS_database_list")
				+ "                                                       ");

		if (bChooseMultiple)
			table = new List(shell, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		else
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
						dataSource = dataSource.replaceAll("(.*) \\(.*$", "$1");
						dataSources.add(dataSource);
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

		final Vector<String[]> tmxDatabases = de.folt.models.datamodel.DataSourceConfigurations
				.getOpenTMSDatabasesWithType();
		int size = 0;
		if (tmxDatabases != null)
		{
			size = tmxDatabases.size();
		}
		if (size > 0)
		{

		}
		else
		{
			table.setEnabled(false);
		}

		Composite bottom = new Composite(shell, SWT.BORDER);
		bottom.setLayout(new GridLayout(2, true));
		bottom.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Button select = new Button(bottom, SWT.PUSH);
		select.setText(message.getString("chooseDataSource"));
		if (size == 0)
		{
			select.setEnabled(false);
		}

		select.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		select.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				try
				{
					int selected = table.getSelectionIndex();
					if (selected >= 0)
					{
						dataSource = (String) table.getItem(selected);
						dataSource = dataSource.replaceAll("(.*) \\(.*$", "$1");
						dataSources.add(dataSource);
					}

					if (table.getSelectionIndices().length > 0)
					{
						int[] indexes = table.getSelectionIndices();
						for (int i = 0; i < indexes.length; i++)
						{
							String xName = table.getItem(indexes[i]);
							xName = xName.replaceAll("(.*) \\(.*$", "$1");
							if (!dataSources.contains(xName))
								dataSources.add(xName);
						}
					}
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
				shell.close();
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

		int iMaxShow = 15;

		if (tmxDatabases != null)
		{
			size = tmxDatabases.size();
			iMaxShow = Math.min(size, 15);
			if (size > 0)
			{
				for (int i = 0; i < iMaxShow; i++)
				{
					String name = tmxDatabases.get(i)[0];
					String type = tmxDatabases.get(i)[1];
					String sync = "false";
					if (tmxDatabases.size() > 2)
						sync = tmxDatabases.get(i)[2];
					table.add(name + " (" + type + " - Sync: " + sync + ")");
				}
			}
		}
		else
		{
			table.setEnabled(false);
		}

		shell.pack();

		if (tmxDatabases != null)
		{
			size = tmxDatabases.size();
			if (size > 0)
			{
				for (int i = iMaxShow; i < size; i++)
				{
					String name = tmxDatabases.get(i)[0];
					String type = tmxDatabases.get(i)[1];
					String sync = "false";
					if (tmxDatabases.size() > 2)
						sync = tmxDatabases.get(i)[2];
					table.add(name + " (" + type + " - Sync: " + sync + ")");
				}
			}
		}

		if (size > 0)
		{
			table.select(0);
			table.setSelection(0);
		}
	}

	/**
	 * @return the dataSource
	 */
	public String getDataSource()
	{
		return dataSource;
	}

	/**
	 * @return the dataSources
	 */
	public Vector<String> getDataSources()
	{
		return dataSources;
	}

	@SuppressWarnings("unused")
	private Vector<String> getOpenTMSDatabases()
	{
		Vector<String> vec = new Vector<String>();
		try
		{
			// just for the configuration
			String configfile = OpenTMSProperties.getInstance()
					.getOpenTMSProperty("dataSourceConfigurationsFile");
			if (configfile != null)
			{
				DataSourceConfigurations dsconfig = new DataSourceConfigurations(
						configfile);
				String names[] = dsconfig.getDataSources();
				for (int i = 0; i < names.length; i++)
				{
					vec.add(names[i]);
				}
			}
			Collections.sort(vec);
			return vec;
		}
		catch (Exception ex)
		{
			return null;
		}
	}

	/**
	 * @return the bChooseMultiple
	 */
	public boolean isBChooseMultiple()
	{
		return bChooseMultiple;
	}

	/**
	 * @param chooseMultiple
	 *            the bChooseMultiple to set
	 */
	public void setBChooseMultiple(boolean chooseMultiple)
	{
		bChooseMultiple = chooseMultiple;
	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(String dataSource)
	{
		this.dataSource = dataSource;
	}

	/**
	 * setDataSourceDisplayType
	 * 
	 * @param name
	 */
	@SuppressWarnings("unused")
	public void setDataSourceDisplayType(String dataSourceDisplayType)
	{
		this.dataSourceDisplayType = dataSourceDisplayType;
		// ok remove all from the list which are not of this type
		String[] items = table.getItems();
		String configFile = DataSourceConfigurations
				.getDefaultDataSourceConfigurationsFileName();
		if (configFile == null)
		{
			return;
		}

		File f = new File(configFile);
		if (!f.exists())
		{
			return;
		}
		DataSourceConfigurations config = new DataSourceConfigurations(
				configFile);
		if (config == null)
			return;

		for (int i = 0; i < items.length; i++)
		{
			String ds = items[i];
			String entry = items[i];
			ds = ds.replaceAll("(.*) \\(.*$", "$1");
			if (dataSourceDisplayType == null)
				continue;
			if (config.getDataSourceType(ds) == null)
				continue;
			if (!(config.getDataSourceType(ds).equals(dataSourceDisplayType)))
			{
				table.remove(entry);
				continue;
			}
		}
	}

	/**
	 * show
	 */
	public void show()
	{
		if (shell == null)
			return;
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
