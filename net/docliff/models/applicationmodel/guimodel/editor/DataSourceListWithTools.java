/*
 * Created on 10.10.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package net.docliff.models.applicationmodel.guimodel.editor;

import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.ChooseDataSourceDialog;
import de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.DataSourceEditor;
import de.folt.models.applicationmodel.guimodel.support.ProgressDialog;
import de.folt.models.applicationmodel.guimodel.support.ProgressDialogSupport;
import de.folt.models.datamodel.DataSource;
import de.folt.models.datamodel.DataSourceInstance;
import de.folt.util.OpenTMSException;

/**
 * @author klemens
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class DataSourceListWithTools extends Composite
{
	public class DataSourceObserver implements Observer
	{
		@SuppressWarnings("unused")
		private DataSourceListWithTools dataSourceListWithTools = null;

		private ProgressDialog progressDialog = null;

		public DataSourceObserver(DataSourceListWithTools dataSourceListWithTools, ProgressDialog progressDialog)
		{
			super();
			this.dataSourceListWithTools = dataSourceListWithTools;
			this.progressDialog = progressDialog;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Observer#update(java.util.Observable,
		 * java.lang.Object)
		 */
		public void update(Observable arg0, Object arg1)
		{
			try
			{
				if (progressDialog != null)
				{
					if (arg1.getClass().equals(Integer[].class))
					{
						Integer[] pos = (Integer[]) arg1;

						if (progressDialog.getPdSupport() != null)
							progressDialog.getPdSupport().updateProgressIndication(pos[0] + 1, pos[1]);
					}

					else if (arg1.getClass().equals(String.class))
					{
						progressDialog.setTitle((String) arg1);
					}
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}

		}
	}

	private static String logfile;

	public static void main(String[] args)
	{

		Display mydisplay = new Display();
		Shell shell = new Shell(mydisplay, SWT.SHELL_TRIM);

		shell.addListener(SWT.Close, new Listener()
		{
			public void handleEvent(Event event)
			{
				event.widget.dispose();
			}
		});

		shell.addListener(SWT.Resize, new Listener()
		{
			public void handleEvent(Event event)
			{
			}
		});

		shell.setText("Test DataSourceListWithTools");

		logfile = "log/" + de.folt.util.OpenTMSSupportFunctions.getCurrentUser() + "." + de.folt.util.OpenTMSSupportFunctions.getDateStringFine()
				+ ".log";
		de.folt.util.OpenTMSLogger.setLogFile(logfile);
		GridLayout shellLayout = new GridLayout(1, true);
		shellLayout.horizontalSpacing = 0;
		shellLayout.verticalSpacing = 1;
		shellLayout.marginWidth = 0;
		shell.setLayout(shellLayout);

		shell.setSize(300, 800);
		new DataSourceListWithTools(shell, SWT.BORDER, "A Test");

		shell.open();
		while (!shell.isDisposed())
		{
			if (!mydisplay.isDisposed())
			{
				if (!mydisplay.readAndDispatch())
				{
					mydisplay.sleep();
				}
			}
		}

		if (!mydisplay.isDisposed())
		{
			mydisplay.dispose();
		}

	}

	private ToolItem closeAllDataSource;

	private ToolItem closeDataSource;

	private Vector<DataSource> dataSourceInstances = null;

	private org.eclipse.swt.widgets.List dataSourcesList;

	private de.folt.util.Messages message;

	private String nameDataSourceType = "";

	private ToolItem openDataSource;

	private ToolItem openDataSourceEditor;

	@SuppressWarnings("unused")
	private Composite parent = null;

	private StyledText statusLine;

	private String userLanguage = "en";

	private ProgressDialog progressDialog;

	private Menu menuCoolbar;

	/**
	 * @param name
	 */
	public DataSourceListWithTools(Composite parent, int style, String name)
	{
		super(parent, style);
		this.nameDataSourceType = name;
		this.parent = parent;

		message = de.folt.util.Messages.getInstance("net.docliff.models.applicationmodel.guimodel.editor.XliffEditor", userLanguage);
		dataSourceInstances = new Vector<DataSource>();
		int iGridData = GridData.GRAB_VERTICAL | GridData.VERTICAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL;
		GridData screenGridData = new GridData(iGridData);

		this.setLayout(new GridLayout(1, true));
		this.setLayoutData(screenGridData);

		SashForm sash = new SashForm(this, SWT.NONE);
		sash.setOrientation(SWT.VERTICAL);
		sash.setLayout(new GridLayout(1, true));
		sash.setLayoutData(screenGridData);

		dataSourcesList = new org.eclipse.swt.widgets.List(sash, SWT.SINGLE | SWT.V_SCROLL | SWT.TOP | SWT.BORDER);
		GridData gridentry = new GridData(iGridData);
		dataSourcesList.setLayoutData(gridentry);
		dataSourcesList.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				openDataSourceEditor.setEnabled(true);
			}
		});
		dataSourcesList.setToolTipText(name);

		CoolBar coolBar = new CoolBar(sash, SWT.BORDER | SWT.FLAT);
		coolBar.setLayoutData(new GridData(GridData.FILL_BOTH));

		ToolBar dataSourceToolBar = new ToolBar(coolBar, SWT.FLAT); // SWT.NONE
		// |
		// SWT.BORDER);
		dataSourceToolBar.setBackground(this.getBackground());

		openDataSource = new ToolItem(dataSourceToolBar, SWT.PUSH | SWT.FLAT);
		openDataSource.setImage(new Image(this.getDisplay(), "images/Open.gif"));
		openDataSource.setToolTipText(message.getString("addDataSource"));

		SelectionAdapter openDataSourceSelectionAdapter = new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				openDataSource();
			}
		};
		openDataSource.addSelectionListener(openDataSourceSelectionAdapter);
		openDataSource.setData("SelectionAdapter", openDataSourceSelectionAdapter);

		new ToolItem(dataSourceToolBar, SWT.SEPARATOR);

		closeDataSource = new ToolItem(dataSourceToolBar, SWT.PUSH | SWT.FLAT);
		closeDataSource.setImage(new Image(this.getDisplay(), "images/closefile.gif"));
		closeDataSource.setToolTipText(message.getString("removeDataSource"));
		SelectionAdapter closeDataSourceSelectionAdapter = new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				closeDataSource();
			}
		};
		closeDataSource.addSelectionListener(closeDataSourceSelectionAdapter);
		closeDataSource.setData("SelectionAdapter", closeDataSourceSelectionAdapter);

		new ToolItem(dataSourceToolBar, SWT.SEPARATOR);

		closeAllDataSource = new ToolItem(dataSourceToolBar, SWT.PUSH | SWT.FLAT);
		closeAllDataSource.setImage(new Image(this.getDisplay(), "images/closefileall.gif"));
		closeAllDataSource.setToolTipText(message.getString("removeAllDataSources"));
		SelectionAdapter closeAllDataSourceSelectionAdapter = new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				String text = "";
				for (int i = 0; i < dataSourcesList.getItemCount(); i++)
				{
					String datasource = dataSourcesList.getItem(i);
					DataSource datsourceinstance = DataSourceInstance.getInstance(datasource);
					if (datsourceinstance != null)
					{
						try
						{
							text = text + datasource + " " + message.getString("successfullyRemoved") + "\n";
							datsourceinstance.bPersist();
							datsourceinstance.removeDataSource();

						}
						catch (Exception ex)
						{
							text = text + datasource + " " + message.getString("couldnotRemoved") + "\n";
							ex.printStackTrace();
						}
					}
				}
				dataSourcesList.removeAll();
				dataSourceInstances.removeAllElements();
				closeDataSource.setEnabled(false);
				closeAllDataSource.setEnabled(false);
				openDataSourceEditor.setEnabled(false);
				statusLine.setText(text);
			}
		};
		closeAllDataSource.addSelectionListener(closeAllDataSourceSelectionAdapter);
		closeAllDataSource.setData("SelectionAdapter", closeAllDataSourceSelectionAdapter);

		new ToolItem(dataSourceToolBar, SWT.SEPARATOR);

		openDataSourceEditor = new ToolItem(dataSourceToolBar, SWT.PUSH | SWT.FLAT);
		openDataSourceEditor.setImage(new Image(this.getDisplay(), "images/opentmsdatasourceeditor.gif"));
		openDataSourceEditor.setToolTipText(message.getString("openSelectedDataSource"));
		SelectionAdapter openDataSourceEditorSelectionAdapter = new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				editDataSource();
			}
		};
		openDataSourceEditor.addSelectionListener(openDataSourceEditorSelectionAdapter);
		openDataSourceEditor.setData("SelectionAdapter", openDataSourceEditorSelectionAdapter);

		CoolItem coolItem1 = new CoolItem(coolBar, SWT.DROP_DOWN);
		coolItem1.setControl(dataSourceToolBar);

		Point toolBar1Size = dataSourceToolBar.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		Point coolBar1Size = coolItem1.computeSize(toolBar1Size.x, toolBar1Size.y);
		coolItem1.setSize(coolBar1Size);

		class CoolBarListener extends SelectionAdapter
		{

			public void widgetSelected(SelectionEvent event)
			{
				if (event.detail == SWT.ARROW)
				{
					ToolBar toolBar = (ToolBar) ((CoolItem) event.widget).getControl();
					CoolBar coolBar = (CoolBar) toolBar.getParent();
					ToolItem[] buttons = toolBar.getItems();

					if (menuCoolbar != null)
					{
						menuCoolbar.dispose();
					}
					menuCoolbar = new Menu(coolBar);
					for (int loopIndex = 0; loopIndex < buttons.length; loopIndex++)
					{
						ToolItem toolItem = buttons[loopIndex];
						if (toolItem.getToolTipText() != null)
						{
							MenuItem menuItem = new MenuItem(menuCoolbar, SWT.PUSH);
							if (toolItem.getImage() != null)
								menuItem.setImage(toolItem.getImage());
							menuItem.setText(toolItem.getToolTipText());
							if (toolItem.getData("SelectionAdapter") != null)
							{
								menuItem.addSelectionListener((SelectionAdapter) toolItem.getData("SelectionAdapter"));
							}
						}
					}

					Point menuPoint = coolBar.toDisplay(new Point(event.x, event.y));
					menuCoolbar.setLocation(menuPoint.x, menuPoint.y);
					menuCoolbar.setVisible(true);
				}
			}
		}

		coolItem1.addSelectionListener(new CoolBarListener());

		statusLine = new StyledText(sash, SWT.WRAP);
		statusLine.setEditable(false);

		sash.setWeights(new int[] { 3, 1, 1 });

		closeDataSource.setEnabled(false);
		closeAllDataSource.setEnabled(false);
		openDataSourceEditor.setEnabled(false);
	}

	public DataSource addDataSource(String dataSource)
	{

		Cursor hglass = new Cursor(getDisplay(), SWT.CURSOR_WAIT);
		Cursor arrow = new Cursor(getDisplay(), SWT.CURSOR_ARROW);
		setCursor(hglass);
		String text = "";
		DataSource datasourceinstance = null;
		int iIndex = dataSourcesList.indexOf(dataSource);
		if (iIndex == -1)
		{

			de.folt.util.Timer timer = new de.folt.util.Timer();
			timer.startTimer();
			try
			{
				progressDialog = new ProgressDialog(this.getShell(), message.getString("LoadDataSource") + " " + dataSource, message
						.getString("LoadDataSource")
						+ " " + dataSource, ProgressDialog.SINGLE_BAR);
				progressDialog.open();
				progressDialog.updateProgressMessage(message.getString("LoadDataSource"));
				progressDialog.setPdSupport(new ProgressDialogSupport(progressDialog));

				Observer observer = new DataSourceObserver(this, progressDialog);
				datasourceinstance = DataSourceInstance.createInstance(dataSource, observer);

				progressDialog.setPdSupport(null);
				progressDialog.close();
				progressDialog = null;
			}
			catch (OpenTMSException e)
			{
				e.printStackTrace();
			}
			timer.endTimer();
			if (datasourceinstance != null)
			{
				dataSourceInstances.add(datasourceinstance);
				dataSourcesList.add(dataSource);
				closeDataSource.setEnabled(true);
				closeAllDataSource.setEnabled(true);

				text = text + dataSource + " " + message.getString("successfullyAdded") + " " + timer.timeNeeded() + " ms\n";

			}
			else
			{
				text = text + dataSource + " " + message.getString("couldnotbeAdded") + " " + timer.timeNeeded() + " ms\n";
			}
		}
		setCursor(arrow);
		statusLine.setText(text);
		return datasourceinstance;
	}

	/**
	 * closeAllDataSources
	 */
	public void closeAllDataSources()
	{
		for (int i = 0; i < dataSourcesList.getItemCount(); i++)
		{
			String datasource = dataSourcesList.getItem(i);
			DataSource datsourceinstance = DataSourceInstance.getInstance(datasource);
			try
			{
				if (datsourceinstance != null)
				{
					datsourceinstance.bPersist();
					datsourceinstance.removeDataSource();
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

	}

	/**
	 * closeAllDataSources
	 */
	public void closeAllDataSourcesWithList()
	{
		String text = "";
		for (int i = 0; i < dataSourcesList.getItemCount(); i++)
		{
			String datasource = dataSourcesList.getItem(i);
			DataSource datsourceinstance = DataSourceInstance.getInstance(datasource);
			if (datsourceinstance != null)
			{
				try
				{
					text = text + datasource + " " + message.getString("successfullyRemoved") + "\n";
					datsourceinstance.bPersist();
					datsourceinstance.removeDataSource();

				}
				catch (Exception ex)
				{
					text = text + datasource + " " + message.getString("couldnotRemoved") + "\n";
					ex.printStackTrace();
				}
			}
		}
		dataSourcesList.removeAll();
		dataSourceInstances.removeAllElements();
		closeDataSource.setEnabled(false);
		closeAllDataSource.setEnabled(false);
		openDataSourceEditor.setEnabled(false);
		statusLine.setText(text);
	}

	public void closeDataSource()
	{
		if (dataSourcesList.getSelectionIndex() > -1)
		{
			String datasource = dataSourcesList.getItem(dataSourcesList.getSelectionIndex());
			DataSource datsourceinstance = DataSourceInstance.getInstance(datasource);
			try
			{
				if (datsourceinstance != null)
				{
					datsourceinstance.bPersist();
					datsourceinstance.removeDataSource();
					if (dataSourcesList.getSelectionIndex() > -1)
					{
						dataSourceInstances.remove(dataSourcesList.getSelectionIndex());
					}
					else
					{
						statusLine.setText(datasource + " " + message.getString("couldnotRemovedNothingSelected"));
					}
					dataSourcesList.remove(dataSourcesList.getSelectionIndex());
					statusLine.setText(datasource + " " + message.getString("successfullyRemoved"));
					if (dataSourcesList.getItemCount() == 0)
					{
						closeDataSource.setEnabled(false);
						closeAllDataSource.setEnabled(false);
						openDataSourceEditor.setEnabled(false);
					}
					else
					{
						dataSourcesList.setSelection(0);
					}
				}
			}
			catch (Exception ex)
			{
				statusLine.setText(datasource + " " + message.getString("couldnotRemoved"));
				ex.printStackTrace();
			}
		}
	}

	public void editDataSource()
	{
		String text = "";

		int i = dataSourcesList.getSelectionIndex();
		if (i == -1)
			return;
		String datasource = dataSourcesList.getItem(dataSourcesList.getSelectionIndex());
		DataSource datsourceinstance = DataSourceInstance.getInstance(datasource);
		if (datsourceinstance != null)
		{
			try
			{
				DataSourceEditor.getInstance(getDisplay(), datasource);
			}
			catch (Exception ex)
			{
				text = text + datasource + " " + message.getString("couldnotbeOpened") + "\n";
				ex.printStackTrace();
				return;
			}
		}
		text = text + datasource + " " + message.getString("couldnotbeOpened") + "\n";
		statusLine.setText(text);
	}

	/**
	 * @return the dataSourceInstances
	 */
	public Vector<DataSource> getDataSourceInstances()
	{
		return dataSourceInstances;
	}

	/**
	 * @return the dataSourceInstances
	 */
	public DataSource getDataSourceInstances(int i)
	{
		if (dataSourceInstances.size() > i)
			return dataSourceInstances.get(i);
		return null;
	}

	/**
	 * @return the dataSources
	 */
	public org.eclipse.swt.widgets.List getDataSources()
	{
		return dataSourcesList;
	}

	/**
	 * @return the dataSourcesList
	 */
	public org.eclipse.swt.widgets.List getDataSourcesList()
	{
		return dataSourcesList;
	}

	/**
	 * @return the nameDataSourceType
	 */
	public String getNameDataSourceType()
	{
		return nameDataSourceType;
	}

	/**
	 * getSelectedDataSource
	 * 
	 * @return the currently selected DataSource
	 */
	public DataSource getSelectedDataSource()
	{
		if (dataSourcesList.getItemCount() == 0)
			return null;

		if (dataSourcesList.getSelectionIndex() == -1)
			return null;

		return getDataSourceInstances(dataSourcesList.getSelectionIndex());
	}

	public void openDataSource()
	{
		Cursor hglass = new Cursor(getDisplay(), SWT.CURSOR_WAIT);
		Cursor arrow = new Cursor(getDisplay(), SWT.CURSOR_ARROW);
		String datasource = "";
		String text = "";
		try
		{
			ChooseDataSourceDialog dialog = new ChooseDataSourceDialog(getShell(), true);
			dialog.show();
			setCursor(hglass);
			Vector<String> datasources = dialog.getDataSources();
			for (int i = 0; i < datasources.size(); i++)
			{
				int iIndex = dataSourcesList.indexOf(datasources.get(i));
				if (iIndex == -1)
				{
					datasource = datasources.get(i);
					addDataSource(datasource);
				}
			}

			// statusLine.setText(text);

			if (dataSourcesList.getSelectionIndex() < 0)
			{
				dataSourcesList.setSelection(0);
				closeDataSource.setEnabled(true);
				closeAllDataSource.setEnabled(true);
				openDataSourceEditor.setEnabled(true);
			}
			setCursor(arrow);
		}
		catch (Exception ex)
		{
			setCursor(arrow);
			statusLine.setText(text);
			ex.printStackTrace();
		}
	}

	/**
	 * @param nameDataSourceType
	 *            the nameDataSourceType to set
	 */
	public void setNameDataSourceType(String name)
	{
		this.nameDataSourceType = name;
	}
}
