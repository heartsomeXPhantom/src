/*
 * Created on 18.05.2009
 * 
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.models.applicationmodel.guimodel.editor.datasourceeditor;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import de.folt.constants.OpenTMSVersionConstants;
import de.folt.models.applicationmodel.guimodel.support.ProgressDialog;
import de.folt.models.applicationmodel.guimodel.support.ProgressDialogSupport;
import de.folt.models.datamodel.DataSource;
import de.folt.models.datamodel.DataSourceConfigurations;
import de.folt.models.datamodel.DataSourceInstance;
import de.folt.models.datamodel.DataSourceProperties;
import de.folt.models.datamodel.sql.OpenTMSSQLDataSource;
import de.folt.models.documentmodel.document.XmlDocument;
import de.folt.util.OpenTMSException;
import de.folt.util.OpenTMSInitialJarFileLoader;
import de.folt.util.OpenTMSProperties;
import de.folt.webservices.OpenTMSWebService;
import de.folt.webservices.OpenTMSWebServiceConstants;
import de.folt.webservices.OpenTMSWebServiceImplementation;
import de.folt.webservices.OpenTMSWebServiceInterface;
import de.folt.webservices.OpenTMSWebServiceResult;
import de.folt.webservices.OpenTMSWebServiceServer;
import flexjson.JSONDeserializer;

/**
 * This class implements an editor for OpenTMS data sources. It provides basic
 * functionalities to remove, change and create entries in a OpenTMS data
 * source.
 * 
 * @author klemens
 * 
 */
public class DataSourceEditor
{

	private static DataSourceEditor	dataSourceEditorInstance	= null;

	/**
	 * getInstance return an instance of a data source editor for sharing
	 * between other applications
	 * 
	 * @param display
	 *            the display to use
	 * @return
	 */
	public static DataSourceEditor getInstance(Display display)
	{
		try
		{
			if (dataSourceEditorInstance == null)
			{
				dataSourceEditorInstance = new DataSourceEditor(null, display);
				dataSourceEditorInstance.setBExternalShell(true);
				dataSourceEditorInstance.run(null, display);
			}
			else
			{
				dataSourceEditorInstance.getShell().setVisible(true);
				dataSourceEditorInstance.getShell().forceFocus();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return dataSourceEditorInstance;
	}

	/**
	 * getInstance return an instance of a data source editor for sharing
	 * between other applications
	 * 
	 * @param display
	 *            the display to use
	 * @param dataSource
	 *            the name of the data source to use
	 * @return
	 */
	public static DataSourceEditor getInstance(Display display, String dataSource)
	{
		try
		{
			if (dataSourceEditorInstance == null)
			{
				dataSourceEditorInstance = new DataSourceEditor(null, display);
				dataSourceEditorInstance.setBExternalShell(true);
				// new Thread()
				// {
				// public void run()
				// {
				// dataSourceEditorInstance.getDisplay().asyncExec(new
				// Runnable()
				// {
				// public void run()
				// {
				String[] args = new String[1];
				args[0] = dataSource;
				dataSourceEditorInstance.run(args, dataSourceEditorInstance.getDisplay());
				dataSourceEditorInstance.getShell().setVisible(true);
				dataSourceEditorInstance.getShell().forceFocus();
				dataSourceEditorInstance.openDataSource(dataSource);
				return dataSourceEditorInstance;
				// }
				// });
				// }
				// }.start();
			}

			dataSourceEditorInstance.getShell().setVisible(true);
			dataSourceEditorInstance.getShell().forceFocus();
			dataSourceEditorInstance.openDataSource(dataSource);

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return dataSourceEditorInstance;
	}

	/**
	 * getInstance return an instance of a data source editor for sharing
	 * between other applications
	 * 
	 * @param display
	 *            the display to use
	 * @param dataSource
	 *            the name of the data source to use
	 * @param uniqueId
	 *            the unique Id to display in the data source editor
	 * @return
	 */
	public static DataSourceEditor getInstance(Display display, String dataSource, String uniqueId)
	{
		try
		{
			if (dataSourceEditorInstance == null)
			{
				dataSourceEditorInstance = new DataSourceEditor(null, display);
				dataSourceEditorInstance.setBExternalShell(true);
				// new Thread()
				// {
				// public void run()
				// {
				// dataSourceEditorInstance.getDisplay().asyncExec(new
				// Runnable()
				// {
				// public void run()
				// {
				String[] args = new String[1];
				args[0] = dataSource;
				dataSourceEditorInstance.run(args, dataSourceEditorInstance.getDisplay());
				dataSourceEditorInstance.getShell().setVisible(true);
				dataSourceEditorInstance.getShell().forceFocus();
				DataSourceForm dataForm = dataSourceEditorInstance.openDataSource(dataSource);
				dataForm.selectUniqueId(uniqueId);
				return dataSourceEditorInstance;
				// }
				// });
				// }
				// }.start();
			}

			dataSourceEditorInstance.getShell().setVisible(true);
			dataSourceEditorInstance.getShell().forceFocus();
			DataSourceForm dataForm = dataSourceEditorInstance.openDataSource(dataSource);
			dataForm.selectUniqueId(uniqueId);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return dataSourceEditorInstance;
	}

	/**
	 * main start the data source editor
	 * 
	 * @param args
	 *            propertiesFile <filename> the properties to use (not used at
	 *            the moment)
	 */
	public static void main(String[] args)
	{
		try
		{
			new DataSourceEditor(args);
			System.exit(0);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			System.exit(1);
		}

	}

	@SuppressWarnings("unused")
	private boolean						bConvert							= false;

	private boolean						bDisplayFromOtherApplictions		= false;

	private boolean						bExternalShell						= false;

	protected boolean					bFirstSyncDone						= false;

	private boolean						bRunning							= false;

	protected String					currentUser							= System.getProperty("user.name");

	private String						dataSourceEditorVersion				= de.folt.constants.OpenTMSVersionConstants.getFullVersionString();

	private CTabFolder					dataSourceTabs;

	private Display						display								= null;

	private JFrame						frmOpt								= null;

	protected long						lClientUpdateCounter				= -1l;

	protected long						lDiffClientWebServerUpdateCounter	= -1l;

	private String						logfile;

	private long						lWebServerUpdateCounter				= -1l;

	private Menu						menubar;

	private Hashtable<String, MenuItem>	menuItems							= new Hashtable<String, MenuItem>();

	private de.folt.util.Messages		message;

	private String						propertiesFile						= "";

	private Shell						shell;

	@SuppressWarnings("unused")
	private Color						standardBackground;

	protected String					syncServerDataSource				= null;

	private String						userLanguage						= "en";

	private String						webServerUpdateCounter				= "";

	/**
     * 
     */
	public DataSourceEditor()
	{
		// TODO Auto-generated constructor stub
	}

	/**
     * 
     */
	public DataSourceEditor(String[] args)
	{
		new DataSourceEditor(args, null).run(args, null);
	}

	/**
     * 
     */
	public DataSourceEditor(String[] args, Display display)
	{
		super();
		this.display = display;
	}

	private void aboutDialog()
	{
		About about = new About(shell, currentUser);
		about.show();
	}

	private void AddDropSupport(Control control)
	{
		// final Shell shell = new Shell();
		// final Table dropTable = new Table(shell, SWT.BORDER);
		int operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT;
		DropTarget dragdropsupportwindow = new DropTarget(control, operations);

		// Receive data in Text or File format
		final TextTransfer textTransfer = TextTransfer.getInstance();
		final FileTransfer fileTransfer = FileTransfer.getInstance();
		Transfer[] types = new Transfer[] { fileTransfer, textTransfer };
		dragdropsupportwindow.setTransfer(types);
		dragdropsupportwindow.addDropListener(new DropTargetListener()
		{
			public void dragEnter(DropTargetEvent event)
			{
				if (event.detail == DND.DROP_DEFAULT)
				{
					if ((event.operations & DND.DROP_COPY) != 0)
					{
						event.detail = DND.DROP_COPY;
					}
					else
					{
						event.detail = DND.DROP_NONE;
					}
				}
				// will accept text but prefer to have files dropped
				for (int i = 0; i < event.dataTypes.length; i++)
				{
					if (fileTransfer.isSupportedType(event.dataTypes[i]))
					{
						event.currentDataType = event.dataTypes[i];
						// files should only be copied
						if (event.detail != DND.DROP_COPY)
						{
							event.detail = DND.DROP_NONE;
						}
						break;
					}
				}
			}

			public void dragLeave(DropTargetEvent event)
			{
				;
			}

			public void dragOperationChanged(DropTargetEvent event)
			{
				if (event.detail == DND.DROP_DEFAULT)
				{
					if ((event.operations & DND.DROP_COPY) != 0)
					{
						event.detail = DND.DROP_COPY;
					}
					else
					{
						event.detail = DND.DROP_NONE;
					}
				}
				// allow text to be moved but files should only be copied
				if (fileTransfer.isSupportedType(event.currentDataType))
				{
					if (event.detail != DND.DROP_COPY)
					{
						event.detail = DND.DROP_NONE;
					}
				}
			}

			public void dragOver(DropTargetEvent event)
			{
				event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL;
				if (textTransfer.isSupportedType(event.currentDataType))
				{
					// NOTE: on unsupported platforms this will return null
					Object o = textTransfer.nativeToJava(event.currentDataType);
					@SuppressWarnings("unused")
					String t = (String) o;
				}
			}

			public void drop(DropTargetEvent event)
			{
				if (textTransfer.isSupportedType(event.currentDataType))
				{
					// do nothing in this case
					@SuppressWarnings("unused")
					String text = (String) event.data;
				}
				Cursor hglass = new Cursor(shell.getDisplay(), SWT.CURSOR_WAIT);
				Cursor arrow = new Cursor(shell.getDisplay(), SWT.CURSOR_ARROW);
				if (fileTransfer.isSupportedType(event.currentDataType))
				{
					String[] files = (String[]) event.data;

					try
					{
						String dataSourceName = files[0];
						dataSourceName = dataSourceName.replaceAll(" \\(.*\\)", "");

						shell.setCursor(hglass);
						String dataSourceType = XmlDocument.getRootElementName(dataSourceName);

						System.out.println("DataSourceType Drop-File = \"" + dataSourceType + "\"");
						Hashtable<String, Object> param = new Hashtable<String, Object>();

						if (dataSourceType.equals("xliff") || dataSourceType.equals("tmx"))
						{
							final Vector<String[]> tmxDatabases = de.folt.models.datamodel.DataSourceConfigurations
									.getOpenTMSDatabasesWithType();
							// check if exists ...
							if (tmxDatabases != null)
							{
								for (int i = 0; i < tmxDatabases.size(); i++)
								{
									if (tmxDatabases.get(i)[0].equals(dataSourceName))
									{
										// ok here we should open it...
										shell.setCursor(arrow);
										openDataSource(dataSourceName);
										return;
									}
								}
							}
							param.put("dataSourceType", dataSourceType);
							param.put("dataSourceName", dataSourceName);
							Vector<String> result = de.folt.rpc.connect.Interface.runCreateDB(param);
							if (result.get(0).equals(de.folt.constants.OpenTMSConstants.OpenTMS_ID_FAILURE + ""))
							{
								MessageBox messageBox = new MessageBox(shell);
								String string = message.getString("Error_Creating");
								messageBox.setText(string);
								string = message.getString("OpenTMS_database_not_created") + " " + dataSourceName;
								messageBox.setMessage(string);
								messageBox.open();
								shell.setCursor(arrow);
								dataSourceName = null;
							}
							else
							{
								MessageBox messageBox = new MessageBox(shell);
								String string = message.getString("Success_Creating");
								messageBox.setText(string);
								string = message.getString("OpenTMS_database_created") + " " + dataSourceName;
								messageBox.setMessage(string);
								messageBox.open();
								shell.setCursor(arrow);
								openDataSource(dataSourceName);
							}
							shell.setCursor(arrow);
						}
						shell.setCursor(arrow);
					}
					catch (Exception ex)
					{
						ex.printStackTrace();
						shell.setCursor(arrow);
					}
				}
			}

			public void dropAccept(DropTargetEvent event)
			{
				;
			}
		});
	}

	protected void addUser()
	{
		@SuppressWarnings("unused")
		OpenTMSWebServiceConstants consts = new OpenTMSWebServiceConstants();
		@SuppressWarnings("unused")
		String url = de.folt.util.OpenTMSProperties.getInstance().getOpenTMSProperty("OpenTMS.WebService.url");

		MenuItem startWebService = menuItems.get("startWebService");
		OpenTMSWebService service = (OpenTMSWebService) startWebService.getData("WebService");

		if (service == null)
		{
			MenuItem connectWebService = menuItems.get("connectWebService");
			service = (OpenTMSWebService) connectWebService.getData("WebService");
		}

		if (service == null)
		{
			showDataSourceEditorMessage("Error: Web Service not running!", "openTMS Update Counter Values",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		OpenTMSWebServiceInterface openTMSService = service.getOpenTMSPort();

		HashMap<String, String> paramHash = new HashMap<String, String>();
		String parameters = "";
		webServerUpdateCounter = "";
		try
		{
			String newuser = "";

			newuser = JOptionPane.showInputDialog("New User");
			if ((newuser == null) || (newuser.equals("")))
				return;
			paramHash.put("method", "adduser");
			paramHash.put("newuser", newuser);
			paramHash.put("user-id", currentUser);
			paramHash.put("name", this.syncServerDataSource);
			parameters = OpenTMSWebServiceResult.jsonSerialise(paramHash);
			String result = openTMSService.synchronize(parameters);
			System.out.println("result: " + result);
			System.out.println(OpenTMSWebServiceResult.deserializeToString(result));
			@SuppressWarnings({ "rawtypes" })
			HashMap rethash = (HashMap) new JSONDeserializer().deserialize(result);

			String message = "User added: " + newuser;

			// "result":"error"
			if (rethash.containsKey("result") && (rethash.get("result").equals("error")))
			{
				message = (String) rethash.get("errorstring");
			}

			showDataSourceEditorMessage(message, "openTMS Add User",
					JOptionPane.INFORMATION_MESSAGE);
		}
		catch (Exception ex)
		{
			// TODO Auto-generated catch block
			ex.printStackTrace();
			showDataSourceEditorMessage("Error: Web Service Error!", "openTMS Update Counter Values",
					JOptionPane.ERROR_MESSAGE);
		}

	}

	private void chooseDataSourceAction()
	{
		@SuppressWarnings("unused")
		OpenTMSWebServiceConstants consts = new OpenTMSWebServiceConstants();
		@SuppressWarnings("unused")
		String url = de.folt.util.OpenTMSProperties.getInstance().getOpenTMSProperty("OpenTMS.WebService.url");

		MenuItem startWebService = menuItems.get("startWebService");
		OpenTMSWebService service = (OpenTMSWebService) startWebService.getData("WebService");

		if (service == null)
		{
			MenuItem connectWebService = menuItems.get("connectWebService");
			service = (OpenTMSWebService) connectWebService.getData("WebService");
		}

		if (service == null)
		{
			showDataSourceEditorMessage(null, "Error: Web Service not running!", "openTMS Update Counter Values",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		OpenTMSWebServiceInterface openTMSService = service.getOpenTMSPort();
		String parameters = "";
		HashMap<String, String> paramHash = new HashMap<String, String>();
		paramHash.put("method", "getSyncDataSources");
		paramHash.put("user-id", this.currentUser);
		if (paramHash.containsKey("user-id"))
		{
			paramHash.put("user-id", paramHash.get("user-id"));
		}
		parameters = OpenTMSWebServiceResult.jsonSerialise(paramHash);
		String result = openTMSService.synchronize(parameters);
		System.out.println("result: " + result);
		System.out.println(OpenTMSWebServiceResult.deserializeToString(result));

		@SuppressWarnings("rawtypes")
		HashMap rethash = (HashMap) new JSONDeserializer().deserialize(result);
		String synDataSources = (String) rethash.get("syncDataSources");
		try
		{
			Object[] possibleValues = synDataSources.split("\\;");
			String message = "\nSync Data Sources available:\n" + synDataSources;
			System.out.println(message);

			syncServerDataSource = (String) JOptionPane.showInputDialog(null, "Choose a Web Server Sync Data Source",
					"Sync Data Source", JOptionPane.INFORMATION_MESSAGE, null, possibleValues, possibleValues[0]);

			// we should run a test message if the data source is really available
			paramHash.put("method", "listuser");
			paramHash.put("name", this.syncServerDataSource);
			paramHash.put("user-id", currentUser);
			parameters = OpenTMSWebServiceResult.jsonSerialise(paramHash);
			result = openTMSService.synchronize(parameters);
			System.out.println("result: " + result);
			System.out.println(OpenTMSWebServiceResult.deserializeToString(result));
			@SuppressWarnings("rawtypes")
			HashMap rethash1 = (HashMap) new JSONDeserializer().deserialize(result);
			String userList = (String) rethash1.get("userlist");

			message = "User list: " + userList;

			// "result":"error"
			if (rethash1.containsKey("result") && (rethash1.get("result").equals("error")))
			{
				message = (String) rethash1.get("errorstring");
				showDataSourceEditorMessage(message, "openTMS Choose Sync Datasource Error", JOptionPane.ERROR_MESSAGE);
				syncServerDataSource = null;
				return;
			}
			else
			{
				message = "Successfully loaded " + syncServerDataSource;
				showDataSourceEditorMessage(message, "openTMS Choose Sync Datasource", JOptionPane.INFORMATION_MESSAGE);
			}
			//

			MenuItem updatecounter = menuItems.get("updatecounter");
			updatecounter.setEnabled(true);

			if (syncServerDataSource != null)
			{
				MenuItem sync = menuItems.get("sync");
				sync.setEnabled(true);

				MenuItem upload = menuItems.get("upload");
				upload.setEnabled(true);

				MenuItem download = menuItems.get("download");
				download.setEnabled(true);

				MenuItem connectWebService = menuItems.get("connectWebService");
				connectWebService.setEnabled(false);

				MenuItem listUser = menuItems.get("listUser");
				listUser.setEnabled(true);

				MenuItem getCreator = menuItems.get("getCreator");
				getCreator.setEnabled(true);

				MenuItem addUser = menuItems.get("addUser");
				addUser.setEnabled(true);

				MenuItem removeUser = menuItems.get("removeUser");
				removeUser.setEnabled(true);
			}
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
			syncServerDataSource = null;
		}
	}

	@SuppressWarnings("rawtypes")
	protected void connectWebServiceAction()
	{
		OpenTMSWebServiceConstants consts = new OpenTMSWebServiceConstants();
		String url = de.folt.util.OpenTMSProperties.getInstance().getOpenTMSProperty("OpenTMS.WebService.url");
		if (url != null)
			consts.setOpenTMSWebServerURL(url);

		try
		{
			OpenTMSWebService service = new OpenTMSWebService(consts);
			MenuItem connectWebService = menuItems.get("connectWebService");
			connectWebService.setData("WebService", service);
			// connectWebService.setData("WebServer", service);
			MenuItem startWebService = menuItems.get("startWebService");
			startWebService.setData("WebService", service);
			// startWebService.setData("WebServer", service);

			OpenTMSWebServiceInterface openTMSService = service.getOpenTMSPort();
			String updateCounter = "";
			String parameters = "";
			HashMap<String, String> paramHash = new HashMap<String, String>();
			paramHash.put("method", "getUpdateCounter");
			parameters = OpenTMSWebServiceResult.jsonSerialise(paramHash);
			String result = openTMSService.synchronize(parameters);
			System.out.println("result: " + result);
			System.out.println(OpenTMSWebServiceResult.deserializeToString(result));
			HashMap rethash = (HashMap) new JSONDeserializer().deserialize(result);
			updateCounter = (String) rethash.get("update-counter");

			String message = service.getOpenTMSWebServiceConstants().openTMSWebServerURL + "\n"
					+ service.getOpenTMSWebServiceConstants().openTMSWebServerNameSpace + "\n"
					+ service.getOpenTMSWebServiceConstants().openTMSWebServerService + "\n"
					+ service.getOpenTMSWebServiceConstants().openTMSWebServerWSDL;

			webServerUpdateCounter = (String) rethash.get("update-counter");

			bFirstSyncDone = false;

			try
			{
				lWebServerUpdateCounter = Long.parseLong(webServerUpdateCounter);
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}

			OpenTMSSQLDataSource otemp = new OpenTMSSQLDataSource();
			lClientUpdateCounter = otemp.currentTimeMillis();
			lDiffClientWebServerUpdateCounter = lClientUpdateCounter - lWebServerUpdateCounter;

			message = message + "\nWeb Server Update Counter: " + webServerUpdateCounter + "\nClient Update Counter: "
					+ lClientUpdateCounter + "\nDifference: " + lDiffClientWebServerUpdateCounter;

			// return a list of available sync data sources
			parameters = "";
			paramHash = new HashMap<String, String>();
			paramHash.put("method", "getSyncDataSources");
			parameters = OpenTMSWebServiceResult.jsonSerialise(paramHash);
			result = openTMSService.synchronize(parameters);
			System.out.println("result: " + result);
			System.out.println(OpenTMSWebServiceResult.deserializeToString(result));

			rethash = (HashMap) new JSONDeserializer().deserialize(result);
			String synDataSources = (String) rethash.get("syncDataSources");
			synDataSources = synDataSources.replaceAll("\\;", "\n");

			message = message + "\nSync Data Sources available:\n" + synDataSources;

			showDataSourceEditorMessage(null, "Successfully conntected to Sync Webservice\n" + message
					+ "\nUpdate Counter = " + updateCounter, "openTMS Sync Web Service Connection",
					JOptionPane.INFORMATION_MESSAGE);

			MenuItem stop = menuItems.get("stopWebService");
			stop.setEnabled(false);
			MenuItem start = menuItems.get("startWebService");
			start.setEnabled(false);

			MenuItem chooseSyncDataSource = menuItems.get("chooseSyncDataSource");
			chooseSyncDataSource.setEnabled(true);

			MenuItem updatecounter = menuItems.get("updatecounter");
			updatecounter.setEnabled(true);

			if (syncServerDataSource != null)
			{
				MenuItem sync = menuItems.get("sync");
				sync.setEnabled(true);

				MenuItem upload = menuItems.get("upload");
				upload.setEnabled(true);

				MenuItem download = menuItems.get("download");
				download.setEnabled(true);

				connectWebService = menuItems.get("connectWebService");
				connectWebService.setEnabled(false);

				MenuItem listUser = menuItems.get("listUser");
				listUser.setEnabled(true);

				MenuItem getCreator = menuItems.get("getCreator");
				getCreator.setEnabled(true);

				MenuItem addUser = menuItems.get("addUser");
				addUser.setEnabled(true);

				MenuItem removeUser = menuItems.get("removeUser");
				removeUser.setEnabled(true);
			}

			connectWebService = menuItems.get("connectWebService");
			connectWebService.setEnabled(false);

			start.setEnabled(false);

			connectWebService.setEnabled(false);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			showDataSourceEditorMessage(null, "Error when conntecting to Sync Web Service\n" + url,
					"openTMS Sync Web Service Connection", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void createEditMenu(Menu menubar)
	{

		MenuItem editMenu = new MenuItem(menubar, SWT.CASCADE);
		editMenu.setText(message.getString("Menu_Edit")); //$NON-NLS-1$
		editMenu.setAccelerator(SWT.ALT | 'E');
		Menu emenu = new Menu(editMenu);
		editMenu.setMenu(emenu);

		MenuItem editUndo = new MenuItem(emenu, SWT.PUSH);
		editUndo.setText(message.getString("Undo")); //$NON-NLS-1$
		editUndo.setAccelerator(SWT.CTRL | 'Z');
		editUndo.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				// undo();
			}
		});
		menuItems.put("editUndo", editUndo);

		editUndo.setEnabled(false);

		MenuItem editRedo = new MenuItem(emenu, SWT.PUSH);
		editRedo.setText(message.getString("Redo")); //$NON-NLS-1$
		editRedo.setAccelerator(SWT.CTRL | 'Y');
		editRedo.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				// redo();
			}
		});
		menuItems.put("editRedo", editRedo);

		new MenuItem(emenu, SWT.SEPARATOR);

		editRedo.setEnabled(false);

		MenuItem editCut = new MenuItem(emenu, SWT.PUSH);
		editCut.setText(message.getString("Cut")); //$NON-NLS-1$
		editCut.setAccelerator(SWT.CTRL | 'X');
		editCut.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				CTabItem actItem = dataSourceTabs.getSelection();
				if (actItem == null)
					return;
				DataSourceForm form = (DataSourceForm) actItem.getData("DataSourceForm");
				if (form.getText().isFocusControl())
					form.getText().cut();
				else if (form.getSearch().isFocusControl())
					form.getSearch().cut();
			}
		});
		menuItems.put("editCut", editCut);

		MenuItem editCopy = new MenuItem(emenu, SWT.PUSH);
		editCopy.setText(message.getString("Copy")); //$NON-NLS-1$
		editCopy.setAccelerator(SWT.CTRL | 'C');
		editCopy.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				CTabItem actItem = dataSourceTabs.getSelection();
				if (actItem == null)
					return;
				DataSourceForm form = (DataSourceForm) actItem.getData("DataSourceForm");
				if (form.getText().isFocusControl())
					form.getText().copy();
				else if (form.getSearch().isFocusControl())
					form.getSearch().copy();
			}
		});
		menuItems.put("editCopy", editCopy);

		MenuItem editPaste = new MenuItem(emenu, SWT.PUSH);
		editPaste.setText(message.getString("Paste")); //$NON-NLS-1$
		editPaste.setAccelerator(SWT.CTRL | 'V');
		editPaste.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				CTabItem actItem = dataSourceTabs.getSelection();
				if (actItem == null)
					return;
				DataSourceForm form = (DataSourceForm) actItem.getData("DataSourceForm");
				if (form.getText().isFocusControl())
					form.getText().paste();
				else if (form.getSearch().isFocusControl())
					form.getSearch().paste();
			}
		});
		menuItems.put("editPaste", editPaste);

	} // end createEditMenu

	private void createFileMenu(Menu menubar)
	{

		Menu fmenu;
		MenuItem fileMenu = new MenuItem(menubar, SWT.CASCADE);
		fileMenu.setText(message.getString("Menu_File")); //$NON-NLS-1$
		fileMenu.setAccelerator(SWT.ALT | 'F');
		fmenu = new Menu(fileMenu);
		fileMenu.setMenu(fmenu);

		MenuItem fileOpen = new MenuItem(fmenu, SWT.PUSH);
		fileOpen.setText(message.getString("&Open_tCtrl_+_O_105")); //$NON-NLS-1$
		fileOpen.setAccelerator(SWT.CONTROL | 'O');
		fileOpen.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				openDataSource();
			}
		});
		menuItems.put("fileNew", fileOpen);

		MenuItem fileClose = new MenuItem(fmenu, SWT.PUSH);
		fileClose.setText(message.getString("Close_File")); //$NON-NLS-1$
		fileClose.setAccelerator(SWT.CONTROL | SWT.F4);
		fileClose.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				int iTab = dataSourceTabs.getSelectionIndex();
				if (iTab >= 0)
				{
					CTabItem tab = dataSourceTabs.getItem(iTab);
					tab.dispose();
				}
			}
		});
		menuItems.put("fileClose", fileClose);

		MenuItem fileSave = new MenuItem(fmenu, SWT.PUSH);
		fileSave.setText(message.getString("&Save_tCtrl_+_S_106")); //$NON-NLS-1$
		fileSave.setAccelerator(SWT.CONTROL | 'S');
		fileSave.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				int iTab = dataSourceTabs.getSelectionIndex();
				if (iTab >= 0)
				{
					@SuppressWarnings("unused")
					CTabItem tab = dataSourceTabs.getItem(iTab);
				}
			}
		});
		menuItems.put("fileSave", fileSave);
		MenuItem fileSaveAs = new MenuItem(fmenu, SWT.PUSH);
		fileSaveAs.setText(message.getString("&Save_as..._107") + "\tAlt + F6"); //$NON-NLS-1$
		fileSaveAs.setAccelerator(SWT.ALT | SWT.F6);
		fileSaveAs.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				int iTab = dataSourceTabs.getSelectionIndex();
				if (iTab >= 0)
				{
					CreateOpenTMSDataSource create = new CreateOpenTMSDataSource(shell, true);
					create.show();
					String newDataSourcename = create.getDataSourceName();
					if ((newDataSourcename != null) && !newDataSourcename.equals(""))
					{
						CTabItem tab = dataSourceTabs.getItem(iTab);
						DataSource dataSource = (DataSource) tab.getData("DataSource");
						if (dataSource != null)
						{
							Cursor hglass = new Cursor(shell.getDisplay(), SWT.CURSOR_WAIT);
							Cursor arrow = new Cursor(shell.getDisplay(), SWT.CURSOR_ARROW);
							shell.setCursor(hglass);
							boolean bSuccess = saveDataSourceAs(dataSource, newDataSourcename);
							shell.setCursor(arrow);
							if (!bSuccess)
							{
								MessageBox messageBox = new MessageBox(shell);
								String string = message.getString("Error_Copying");
								messageBox.setText(string);
								string = message.getString("OpenTMS_database_not_copied") + " " + newDataSourcename;
								messageBox.setMessage(string);
								messageBox.open();
							}
						}
					}
				}
			}
		});

		menuItems.put("fileSaveAs", fileSaveAs);

		new MenuItem(fmenu, SWT.SEPARATOR);

		MenuItem fileExit = new MenuItem(fmenu, SWT.PUSH);
		fileExit.setText(message.getString("E&xit_tAlt_+_X_108")); //$NON-NLS-1$
		fileExit.setAccelerator(SWT.ALT | 'X');
		fileExit.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				if (bDisplayFromOtherApplictions == false)
					display.dispose();
				else
					shell.setVisible(false);
			}
		});
		menuItems.put("fileExit", fileExit);
	}

	/**
	 * Method createHelpMenu.
	 * 
	 * @param menubar
	 */
	private void createHelpMenu(Menu menubar)
	{

		MenuItem helpMenu = new MenuItem(menubar, SWT.CASCADE);
		helpMenu.setText(message.getString("Menu_Help")); //$NON-NLS-1$
		helpMenu.setAccelerator(SWT.ALT | 'H');
		Menu hmenu = new Menu(helpMenu);
		helpMenu.setMenu(hmenu);

		MenuItem showlog = new MenuItem(hmenu, SWT.PUSH);
		showlog.setText(message.getString("ShowLog.0") + " " + logfile); //$NON-NLS-1$
		showlog.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				DisplayLog disp = new DisplayLog(shell, logfile, true, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL
						| SWT.READ_ONLY);
				disp.setTitle(message.getString("ShowLog.0") + " " + logfile);
				disp.show();
				disp = null;
			}
		});

		MenuItem help = new MenuItem(hmenu, SWT.PUSH);
		help.setText(message.getString("OpenTMSHelp")); //$NON-NLS-1$
		// helpUpdate.setAccelerator(SWT.F1);
		help.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				displayOpenTMSHelp();
			}
		});
		help.setAccelerator(SWT.F1);
		help.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				displayOpenTMSHelp();
			}
		});
		menuItems.put("homepage", help);

		MenuItem homepage = new MenuItem(hmenu, SWT.PUSH);
		homepage.setText(message.getString("OpenTMSHomepage")); //$NON-NLS-1$
		// helpUpdate.setAccelerator(SWT.F1);
		homepage.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				displayOpenTMSHomepage();
			}
		});
		menuItems.put("homepage", homepage);

		MenuItem helpAbout = new MenuItem(hmenu, SWT.PUSH);
		helpAbout.setText(message.getString("&About...")); //$NON-NLS-1$
		helpAbout.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				aboutDialog();
			}
		});
		menuItems.put("helpAbout", helpAbout);
	}

	/**
	 * @param menubar
	 */
	private void createOpenTMSMenu(Menu menubar)
	{

		MenuItem pluginMenu = new MenuItem(menubar, SWT.CASCADE);
		pluginMenu.setText(message.getString("Menu_OpenTMS")); //$NON-NLS-1$
		pluginMenu.setAccelerator(SWT.ALT | 'o');
		Menu pmenu = new Menu(pluginMenu);
		pluginMenu.setMenu(pmenu);

		MenuItem openTMSTranslate = new MenuItem(pmenu, SWT.PUSH);
		openTMSTranslate.setText(message.getString("openTMSTranslate")); //$NON-NLS-1$
		openTMSTranslate.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				OpenTMSTranslate trans = new OpenTMSTranslate(shell, "", "", "");
				trans.show();
				trans = null;
			}
		});
		menuItems.put("openTMSTranslate", openTMSTranslate);

		MenuItem openTMSRevConvert = new MenuItem(pmenu, SWT.PUSH);
		openTMSRevConvert.setText(message.getString("openTMSRevConvert")); //$NON-NLS-1$
		openTMSRevConvert.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				ReverseConversion trans = new ReverseConversion(shell, "");
				trans.show();
				trans = null;
			}
		});
		menuItems.put("openTMSRevConvert", openTMSRevConvert);

		new MenuItem(pmenu, SWT.SEPARATOR);

		MenuItem createopenTMSDatabase = new MenuItem(pmenu, SWT.PUSH);
		createopenTMSDatabase.setText(message.getString("createopenTMSDatabase")); //$NON-NLS-1$
		createopenTMSDatabase.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				CreateOpenTMSDataSource create = new CreateOpenTMSDataSource(shell, false);
				create.show();
				create = null;
			}
		});
		menuItems.put("createopenTMSDatabase", createopenTMSDatabase);
		MenuItem deleteopenTMSDatabase = new MenuItem(pmenu, SWT.PUSH);
		deleteopenTMSDatabase.setText(message.getString("deleteopenTMSDatabase")); //$NON-NLS-1$
		deleteopenTMSDatabase.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				DeleteOpenTMSDataSource delete = new DeleteOpenTMSDataSource(shell);
				delete.show();
				delete = null;
			}

		});
		menuItems.put("deleteopenTMSDatabase", deleteopenTMSDatabase);

		MenuItem manageMultipleDataSource = new MenuItem(pmenu, SWT.PUSH);
		manageMultipleDataSource.setText(message.getString("manageMultipleDataSource")); //$NON-NLS-1$
		manageMultipleDataSource.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				Vector<String[]> tmxDatabases = de.folt.models.datamodel.DataSourceConfigurations
						.getOpenTMSDatabasesWithType();
				boolean bMultipleDataSources = false;
				for (int i = 0; i < tmxDatabases.size(); i++)
				{
					@SuppressWarnings("unused")
					String name = tmxDatabases.get(i)[0];
					String type = tmxDatabases.get(i)[1];
					if (type.equals(de.folt.models.datamodel.multipledatasource.MultipleDataSource.class.getName()))
					{
						bMultipleDataSources = true;
						break;
					}
				}
				if (bMultipleDataSources == false)
				{
					MessageBox mess = new MessageBox(shell, SWT.OK | SWT.ICON_WARNING);
					mess.setText(message.getString("noMultipleDateSourceTitle"));
					mess.setMessage(message.getString("noMultipleDateSource"));
					mess.open();
					return;
				}

				ChooseDataSourceDialog chooser = new ChooseDataSourceDialog(shell, false);
				chooser.setDataSourceDisplayType(de.folt.models.datamodel.multipledatasource.MultipleDataSource.class
						.getName());
				chooser.show();
				String datasource = chooser.getDataSource();
				if (datasource != null)
				{
					ManageMultipleDataSource manag = new ManageMultipleDataSource(shell, datasource);
					manag.show();
				}
				chooser = null;

			}
		});
		menuItems.put("manageMultipleDataSource", manageMultipleDataSource);

		new MenuItem(pmenu, SWT.SEPARATOR);

		MenuItem importopenTMSDatabase = new MenuItem(pmenu, SWT.PUSH);
		importopenTMSDatabase.setText(message.getString("importopenTMSDatabase")); //$NON-NLS-1$
		importopenTMSDatabase.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				ImportOpenTMSDataSource importer = new ImportOpenTMSDataSource(shell);
				importer.show();
				importer = null;
			}
		});
		menuItems.put("importopenTMSDatabase", importopenTMSDatabase);

		MenuItem exportopenTMSDatabase = new MenuItem(pmenu, SWT.PUSH);
		exportopenTMSDatabase.setText(message.getString("exportopenTMSDatabase")); //$NON-NLS-1$
		exportopenTMSDatabase.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				ExportOpenTMSDataSource exporter = new ExportOpenTMSDataSource(shell);
				exporter.show();
				exporter = null;
			}
		});
		menuItems.put("exportopenTMSDatabase", exportopenTMSDatabase);

		MenuItem exportXliffInternalTerminology = new MenuItem(pmenu, SWT.PUSH);
		exportXliffInternalTerminology.setText(message.getString("exportXliffInternalTerminology")); //$NON-NLS-1$
		exportXliffInternalTerminology.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				ExportXliffInternalTerminology exporter = new ExportXliffInternalTerminology(shell, "");
				exporter.show();
				exporter = null;
			}
		});
		menuItems.put("exportXliffInternalTerminology", exportXliffInternalTerminology);

		new MenuItem(pmenu, SWT.SEPARATOR);

		MenuItem copyFromToOpenTMSDataSource = new MenuItem(pmenu, SWT.PUSH);
		copyFromToOpenTMSDataSource.setText(message.getString("CopyFromToOpenTMSDataSource")); //$NON-NLS-1$
		copyFromToOpenTMSDataSource.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				CopyFromToOpenTMSDataSource exporter = new CopyFromToOpenTMSDataSource(shell);
				exporter.show();
				exporter = null;
			}
		});
		menuItems.put("copyFromToOpenTMSDataSource", copyFromToOpenTMSDataSource);
	}

	/**
	 * createShell
	 */
	private void createShell()
	{
		message = de.folt.util.Messages.getInstance(
				"de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.datasourceeditor", userLanguage);

		if (display == null)
			display = new Display();
		shell = new Shell(display, SWT.SHELL_TRIM);
		String version = this.getDataSourceEditorVersion();
		shell.setText(message.getString("OpenTMSDataSource_Editor") + " " + version);

		Image logo = new Image(display, "images/opentms_16x16.png");
		shell.setImage(logo);

		GridLayout shellLayout = new GridLayout(1, true);
		shellLayout.horizontalSpacing = 0;
		shellLayout.verticalSpacing = 1;
		shellLayout.marginWidth = 0;
		shell.setLayout(shellLayout);
		// shell.setImage(logo);

		shell.addListener(SWT.Close, new Listener()
		{
			public void handleEvent(Event event)
			{
				if (bDisplayFromOtherApplictions == false)
					display.dispose();
				else
				{
					shell.setVisible(false);
					event.doit = false;
				}
			}
		});

		shell.addListener(SWT.Resize, new Listener()
		{
			public void handleEvent(Event event)
			{
			}
		});

		standardBackground = shell.getBackground();
	}

	/**
	 * @param menubar
	 */
	private void createSyncMenu(Menu menubar)
	{

		MenuItem pluginMenu = new MenuItem(menubar, SWT.CASCADE);
		pluginMenu.setText(message.getString("Menu_Sync")); //$NON-NLS-1$
		pluginMenu.setAccelerator(SWT.ALT | 's');
		Menu pmenu = new Menu(pluginMenu);
		pluginMenu.setMenu(pmenu);

		MenuItem userLogin = new MenuItem(pmenu, SWT.PUSH);
		userLogin.setText(message.getString("Log_as_Different_User"));
		userLogin.setData("name", "Log_as_Different_User");
		userLogin.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				Login login = new Login(shell, currentUser, message);
				login.show();
				if (!login.getUser().equals(""))
					currentUser = login.getUser();
			}
		});

		MenuItem chooseSyncDataSource = new MenuItem(pmenu, SWT.PUSH);
		chooseSyncDataSource.setText(message.getString("Choose_SyncDataSource")); //$NON-NLS-1$
		chooseSyncDataSource.setAccelerator(SWT.ALT | 'C');
		chooseSyncDataSource.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				chooseDataSourceAction();
			}
		});
		menuItems.put("chooseSyncDataSource", chooseSyncDataSource);
		chooseSyncDataSource.setEnabled(false);

		new MenuItem(pmenu, SWT.SEPARATOR);

		MenuItem sync = new MenuItem(pmenu, SWT.PUSH);
		sync.setText(message.getString("Choose_Sync"));
		sync.setAccelerator(SWT.ALT | 'Y');
		sync.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				boolean bSuccess = downloadAction();
				if (bSuccess)
					uploadAction();
			}
		});
		menuItems.put("sync", sync);
		sync.setEnabled(false);

		MenuItem upload = new MenuItem(pmenu, SWT.PUSH);
		upload.setText(message.getString("Choose_Upload"));
		upload.setAccelerator(SWT.ALT | 'U');
		upload.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				uploadAction();
			}
		});
		menuItems.put("upload", upload);
		upload.setEnabled(false);

		MenuItem download = new MenuItem(pmenu, SWT.PUSH);
		download.setText(message.getString("Choose_Download"));
		download.setAccelerator(SWT.ALT | 'D');
		download.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				downloadAction();
			}
		});
		menuItems.put("download", download);
		download.setEnabled(false);

		MenuItem updatecounter = new MenuItem(pmenu, SWT.PUSH);
		updatecounter.setText(message.getString("Choose_Updatecounter"));
		download.setAccelerator(SWT.ALT | 'P');
		updatecounter.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				updateCounterAction();
			}
		});
		menuItems.put("updatecounter", updatecounter);
		updatecounter.setEnabled(false);

		new MenuItem(pmenu, SWT.SEPARATOR);

		final MenuItem connectWebService = new MenuItem(pmenu, SWT.PUSH);
		connectWebService.setText(message.getString("connectToWebService")); //$NON-NLS-1$
		connectWebService.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				connectWebServiceAction();
			}
		});
		menuItems.put("connectWebService", connectWebService);

		final MenuItem startWebService = new MenuItem(pmenu, SWT.PUSH);
		startWebService.setText(message.getString("startWebService")); //$NON-NLS-1$
		startWebService.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				startWebServiceAction();
			}
		});
		menuItems.put("startWebService", startWebService);

		final MenuItem stopWebService = new MenuItem(pmenu, SWT.PUSH);
		stopWebService.setText(message.getString("stopWebService")); //$NON-NLS-1$
		stopWebService.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				stopWebServiceAction();
			}

		});
		stopWebService.setEnabled(false);
		menuItems.put("stopWebService", stopWebService);

		final MenuItem listUser = new MenuItem(pmenu, SWT.PUSH);
		listUser.setText(message.getString("listUser")); //$NON-NLS-1$
		listUser.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				listUser();
			}

		});
		listUser.setEnabled(false);
		menuItems.put("listUser", listUser);

		final MenuItem getCreator = new MenuItem(pmenu, SWT.PUSH);
		getCreator.setText(message.getString("getCreator")); //$NON-NLS-1$
		getCreator.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				getCreator();
			}

		});
		getCreator.setEnabled(false);
		menuItems.put("getCreator", getCreator);

		final MenuItem addUser = new MenuItem(pmenu, SWT.PUSH);
		addUser.setText(message.getString("adduser")); //$NON-NLS-1$
		addUser.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				addUser();
			}
		});
		addUser.setEnabled(false);
		menuItems.put("addUser", addUser);

		final MenuItem removeUser = new MenuItem(pmenu, SWT.PUSH);
		removeUser.setText(message.getString("removeuser")); //$NON-NLS-1$
		removeUser.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				removeUser();
			}
		});
		removeUser.setEnabled(false);
		menuItems.put("removeUser", removeUser);

	}

	/**
	 * createToolBar
	 * 
	 * @param toolBar
	 */
	private void createToolBar(ToolBar toolBar)
	{

		ToolItem fOpen = new ToolItem(toolBar, SWT.PUSH | SWT.FLAT);
		fOpen.setImage(new Image(display, "images/Open.gif")); //$NON-NLS-1$
		fOpen.setToolTipText(message.getString("&Open_tCtrl_+_O_105")); //$NON-NLS-1$
		fOpen.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				openDataSource();
			}
		});

		new ToolItem(toolBar, SWT.SEPARATOR);

		ToolItem eCut = new ToolItem(toolBar, SWT.PUSH | SWT.FLAT);
		eCut.setImage(new Image(display, "images/editcut.gif")); //$NON-NLS-1$
		eCut.setToolTipText(message.getString("Cut")); //$NON-NLS-1$
		eCut.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				CTabItem actItem = dataSourceTabs.getSelection();
				if (actItem == null)
					return;
				DataSourceForm form = (DataSourceForm) actItem.getData("DataSourceForm");
				if (form.getText().isFocusControl())
					form.getText().cut();
				else if (form.getSearch().isFocusControl())
					form.getSearch().cut();
			}
		});

		ToolItem eCopy = new ToolItem(toolBar, SWT.PUSH | SWT.FLAT);
		eCopy.setImage(new Image(display, "images/editcopy.gif")); //$NON-NLS-1$
		eCopy.setToolTipText(message.getString("Copy")); //$NON-NLS-1$
		eCopy.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				CTabItem actItem = dataSourceTabs.getSelection();
				if (actItem == null)
					return;
				DataSourceForm form = (DataSourceForm) actItem.getData("DataSourceForm");
				if (form.getText().isFocusControl())
					form.getText().copy();
				else if (form.getSearch().isFocusControl())
					form.getSearch().copy();
			}
		});

		ToolItem ePaste = new ToolItem(toolBar, SWT.PUSH | SWT.FLAT);
		ePaste.setImage(new Image(display, "images/editpaste.gif")); //$NON-NLS-1$
		ePaste.setToolTipText(message.getString("Paste")); //$NON-NLS-1$
		ePaste.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				CTabItem actItem = dataSourceTabs.getSelection();
				if (actItem == null)
					return;
				DataSourceForm form = (DataSourceForm) actItem.getData("DataSourceForm");
				if (form.getText().isFocusControl())
					form.getText().paste();
				else if (form.getSearch().isFocusControl())
					form.getSearch().paste();
			}
		});

		new ToolItem(toolBar, SWT.SEPARATOR);

		ToolItem fHelp = new ToolItem(toolBar, SWT.PUSH | SWT.FLAT);
		fHelp.setImage(new Image(display, "images/Help.gif")); //$NON-NLS-1$
		fHelp.setToolTipText(message.getString("Menu_Help")); //$NON-NLS-1$
		fHelp.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				displayOpenTMSHelp();
			}
		});

		ToolItem fAbout = new ToolItem(toolBar, SWT.PUSH | SWT.FLAT);
		fAbout.setImage(new Image(display, "images/About.gif")); //$NON-NLS-1$
		fAbout.setToolTipText(message.getString("About")); //$NON-NLS-1$
		fAbout.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				aboutDialog();
			}
		});
	}

	/**
	 * displayOpenTMSHelp
	 */
	protected void displayOpenTMSHelp()
	{
		String homepage = OpenTMSProperties.getInstance().getOpenTMSProperty("OpenTMSHelp");
		if ((homepage == null) || homepage.equals(""))
		{
			homepage = "http://www.opentms.de";
		}
		Program.launch(homepage);
	}

	/**
	 * displayOpenTMSHelp
	 */
	public void displayOpenTMSHelp(String linkReference)
	{
		String homepage = OpenTMSProperties.getInstance().getOpenTMSProperty("OpenTMSHelp");
		if ((homepage == null) || homepage.equals(""))
		{
			homepage = "http://www.opentms.de";
		}
		homepage = homepage + "#" + linkReference;
		Program.launch(homepage);
	}

	private void displayOpenTMSHomepage()
	{
		String homepage = OpenTMSProperties.getInstance().getOpenTMSProperty("OpenTMSHomepage");
		if ((homepage == null) || homepage.equals(""))
		{
			homepage = "http://www.opentms.de";
		}
		Program.launch(homepage);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private boolean downloadAction()
	{
		// get active chosen database
		CTabItem actItem = dataSourceTabs.getSelection();
		if (actItem == null)
			return false;
		DataSourceForm form = (DataSourceForm) actItem.getData("DataSourceForm");
		DataSource dataSource = (DataSource) actItem.getData("DataSource");
		if (dataSource == null)
		{
			showDataSourceEditorMessage(null, "Data Source is null", "openTMS Download Result Error",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}

		HashMap<String, String> paramHash = new HashMap<String, String>();
		Cursor hglass = new Cursor(shell.getDisplay(), SWT.CURSOR_WAIT);
		Cursor arrow = new Cursor(shell.getDisplay(), SWT.CURSOR_ARROW);

		shell.setCursor(hglass);
		ProgressDialog progressDialog = new ProgressDialog(shell, message.getString("Download"),
				message.getString("Download"), ProgressDialog.SINGLE_BAR);
		progressDialog.open();
		progressDialog.updateProgressMessage("Export_TMX_Document");

		ProgressDialogSupport pdSupport = null;
		pdSupport = new ProgressDialogSupport(progressDialog);

		try
		{
			pdSupport.updateProgressIndication(0, 4);
			@SuppressWarnings("unused")
			OpenTMSWebServiceConstants consts = new OpenTMSWebServiceConstants();
			@SuppressWarnings("unused")
			String url = de.folt.util.OpenTMSProperties.getInstance().getOpenTMSProperty("OpenTMS.WebService.url");

			MenuItem startWebService = menuItems.get("startWebService");
			OpenTMSWebService service = (OpenTMSWebService) startWebService.getData("WebService");

			if (service == null)
			{
				MenuItem connectWebService = menuItems.get("connectWebService");
				service = (OpenTMSWebService) connectWebService.getData("WebService");
				if (service == null)
				{
					showDataSourceEditorMessage(null, "Web Service not running", "openTMS Download Result Error",
							JOptionPane.ERROR_MESSAGE);
					return false;
				}
			}

			OpenTMSWebServiceInterface openTMSService = service.getOpenTMSPort();

			String updateCounter = "";
			// long lCC = getlClientUpdateCounter();
			// long lDV = getlDiffClientWebServerUpdateCounter();
			updateCounter = getlWebServerUpdateCounter() + 1l + "";
			if (isbFirstSyncDone() == false)
				updateCounter = -1l + "";
			paramHash.put("method", "download");
			paramHash.put("user-id", currentUser);
			paramHash.put("password", "");
			if (getSyncServerDataSource() == null)
			{
				progressDialog.close();
				shell.setCursor(arrow);
				showDataSourceEditorMessage(null, "Sync Data Source is null/not defined",
						"openTMS Download Result Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}
			paramHash.put("name", getSyncServerDataSource());
			paramHash.put("parameter", "empty");
			// paramHash.put("user-id-list", "klemens,michael,stefan");
			paramHash.put("update-counter", updateCounter);
			String parameters = OpenTMSWebServiceResult.jsonSerialise(paramHash);
			pdSupport.updateProgressIndication(1, 4);
			String result = openTMSService.synchronize(parameters);
			pdSupport.updateProgressIndication(2, 4);
			System.out.println("result: " + result);
			System.out.println(OpenTMSWebServiceResult.deserializeToString(result));
			paramHash = (HashMap) new JSONDeserializer().deserialize(result);
			String resultweb = paramHash.get("result");
			if (resultweb.equals("ok"))
			{
				String tmx_document = paramHash.get("tmx-document");
				if (tmx_document == null)
				{
					showDataSourceEditorMessage(null, "TMX Document is null", "openTMS Download Result Error",
							JOptionPane.ERROR_MESSAGE);
					return false;
				}
				Hashtable<String, Object> param = new Hashtable<String, Object>();
				param.put("dataSourceName", dataSource.getDataSourceName());
				param.put("sourceDocument", tmx_document);
				param.put("inputDocumentType", "String");
				param.put("dataSourceConfigurationsFile", "");
				// param.put("update-counter", update_counter);
				param.put("inputDocumentType", "String");
				param.put("sync", "download-import");
				param.put("user-id", currentUser);
				// param.put("sync", "upload");
				String encoding = "UTF-8";
				param.put("encoding", encoding);

				Vector<String> resultimport = de.folt.rpc.connect.Interface.runImportOpenTMSDataSource(param);

				pdSupport.updateProgressIndication(3, 4);

				String message = resultimport.toString();

				webServerUpdateCounter = paramHash.get("update-counter");
				try
				{
					setlWebServerUpdateCounter(Long.parseLong(webServerUpdateCounter));
					String dataSourceConfigurationsFile = dataSource.getDefaultDataSourceConfigurationsFileName();
					DataSourceConfigurations config = new DataSourceConfigurations(dataSourceConfigurationsFile);
					DataSourceProperties dataModelProperties = new DataSourceProperties();
					dataModelProperties.remove("WebServerUpdateCounter");
					dataModelProperties.put("WebServerUpdateCounter", getlWebServerUpdateCounter() + ":"
							+ getSyncServerDataSource());
					config.addPropertiesToDataModelProperties(dataSource.getDataSourceName(), dataModelProperties);
					config.saveToXmlFile();
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}

				message = message + "\nNew Web Server Update Counter: " + webServerUpdateCounter;
				pdSupport.updateProgressIndication(4, 4);
				form.fillIdList();
				progressDialog.close();
				shell.setCursor(arrow);
				this.showDataSourceEditorMessage(null, message, "openTMS Download Result", JOptionPane.INFORMATION_MESSAGE);

				setbFirstSyncDone(true);
				return true;
			}
			else
			{
				progressDialog.close();
				shell.setCursor(arrow);
				showDataSourceEditorMessage(null, paramHash.get("errorstring"), "openTMS Download Result Error", JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
			progressDialog.close();
			shell.setCursor(arrow);
			showDataSourceEditorMessage(null, e1.getMessage(), "openTMS Download Result Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}

	/**
	 * @return
	 */
	public DataSource getActiveDataSource()
	{
		try
		{
			// get active chosen database
			CTabItem actItem = dataSourceTabs.getSelection();
			if (actItem == null)
				return null;
			DataSource dataSource = (DataSource) actItem.getData("DataSource");
			return dataSource;
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	private void getCreator()
	{
		@SuppressWarnings("unused")
		OpenTMSWebServiceConstants consts = new OpenTMSWebServiceConstants();
		@SuppressWarnings("unused")
		String url = de.folt.util.OpenTMSProperties.getInstance().getOpenTMSProperty("OpenTMS.WebService.url");

		MenuItem startWebService = menuItems.get("startWebService");
		OpenTMSWebService service = (OpenTMSWebService) startWebService.getData("WebService");

		if (service == null)
		{
			MenuItem connectWebService = menuItems.get("connectWebService");
			service = (OpenTMSWebService) connectWebService.getData("WebService");
		}

		if (service == null)
		{
			showDataSourceEditorMessage(null, "Error: Web Service not running!", "openTMS Update Counter Values",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		OpenTMSWebServiceInterface openTMSService = service.getOpenTMSPort();

		HashMap<String, String> paramHash = new HashMap<String, String>();
		String parameters = "";
		webServerUpdateCounter = "";
		try
		{
			paramHash.put("method", "getcreator");
			paramHash.put("name", this.syncServerDataSource);
			paramHash.put("user-id", currentUser);
			parameters = OpenTMSWebServiceResult.jsonSerialise(paramHash);
			String result = openTMSService.synchronize(parameters);
			System.out.println("result: " + result);
			System.out.println(OpenTMSWebServiceResult.deserializeToString(result));
			@SuppressWarnings("rawtypes")
			HashMap rethash = (HashMap) new JSONDeserializer().deserialize(result);
			String userList = (String) rethash.get("creator");
			String message = "Creator: " + userList + "\nDatabase Creator: " + (String) rethash.get("database-creator");

			// "result":"error"
			if (rethash.containsKey("result") && (rethash.get("result").equals("error")))
			{
				message = (String) rethash.get("errorstring");
			}

			showDataSourceEditorMessage(null, message, "openTMS Creator",
					JOptionPane.INFORMATION_MESSAGE);
		}
		catch (Exception ex)
		{
			// TODO Auto-generated catch block
			ex.printStackTrace();
			showDataSourceEditorMessage(null, "Error: Web Service Error!", "openTMS Update Counter Values",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public String getCurrentUser()
	{
		return currentUser;
	}

	/**
	 * @return the dataSourceEditorVersion
	 */
	public String getDataSourceEditorVersion()
	{
		return dataSourceEditorVersion;
	}

	/**
	 * @return the display
	 */
	public Display getDisplay()
	{
		return display;
	}

	public long getlClientUpdateCounter()
	{
		return lClientUpdateCounter;
	}

	public long getlDiffClientWebServerUpdateCounter()
	{
		return lDiffClientWebServerUpdateCounter;
	}

	public long getlWebServerUpdateCounter()
	{
		return lWebServerUpdateCounter;
	}

	@SuppressWarnings("unused")
	private Vector<String> getOpenTMSDatabases()
	{
		Vector<String> vec = new Vector<String>();
		try
		{
			// just for the configuration
			String configfile = OpenTMSProperties.getInstance().getOpenTMSProperty("dataSourceConfigurationsFile");
			if (configfile != null)
			{
				DataSourceConfigurations dsconfig = new DataSourceConfigurations(configfile);
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
	 * @return the shell
	 */
	public Shell getShell()
	{
		return shell;
	}

	public String getSyncServerDataSource()
	{
		return syncServerDataSource;
	}

	public String getUpdateCounter()
	{
		return webServerUpdateCounter;
	}

	public String getWebServerUpdateCounter()
	{
		return webServerUpdateCounter;
	}

	/**
	 * adaptShell
	 */
	private void initShell()
	{
		Composite toolsHolder = new Composite(shell, SWT.NONE);
		GridLayout toolsLayout = new GridLayout(1, false);
		toolsLayout.marginWidth = 1;
		toolsLayout.marginHeight = 1;
		toolsLayout.horizontalSpacing = 0;
		toolsHolder.setLayout(toolsLayout);
		toolsHolder.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));

		ToolBar toolBar = new ToolBar(toolsHolder, SWT.NONE);
		createToolBar(toolBar);
		// final Menu
		menubar = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menubar);
		createFileMenu(menubar);
		createEditMenu(menubar);
		createSyncMenu(menubar);
		createOpenTMSMenu(menubar);
		createHelpMenu(menubar);

		int iGridData = GridData.GRAB_VERTICAL | GridData.VERTICAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL
				| GridData.FILL_HORIZONTAL;

		GridData screenGridData = new GridData(iGridData);

		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.numColumns = 1;
		gridLayout.makeColumnsEqualWidth = true;
		shell.setLayout(gridLayout);
		GridData shellentry = new GridData(iGridData);
		shell.setLayoutData(shellentry);

		dataSourceTabs = new CTabFolder(shell, SWT.BORDER);
		dataSourceTabs.setLayoutData(screenGridData);
		dataSourceTabs.addCTabFolder2Listener(new CTabFolder2Adapter()
		{
			public void close(CTabFolderEvent event)
			{
				String ds = (String) event.item.getData("datasource");
				try
				{
					DataSourceInstance.removeInstance(ds);
				}
				catch (OpenTMSException e)
				{
					e.printStackTrace();
				}
			}
		});
		AddDropSupport(dataSourceTabs);
	}

	/**
	 * @return the bExternalShell
	 */
	public boolean isBExternalShell()
	{
		return bExternalShell;
	}

	public boolean isbFirstSyncDone()
	{
		return bFirstSyncDone;
	}

	/**
	 * @return the bRunning
	 */
	public boolean isBRunning()
	{
		return bRunning;
	}

	private void listUser()
	{
		@SuppressWarnings("unused")
		OpenTMSWebServiceConstants consts = new OpenTMSWebServiceConstants();
		@SuppressWarnings("unused")
		String url = de.folt.util.OpenTMSProperties.getInstance().getOpenTMSProperty("OpenTMS.WebService.url");

		MenuItem startWebService = menuItems.get("startWebService");
		OpenTMSWebService service = (OpenTMSWebService) startWebService.getData("WebService");

		if (service == null)
		{
			MenuItem connectWebService = menuItems.get("connectWebService");
			service = (OpenTMSWebService) connectWebService.getData("WebService");
		}

		if (service == null)
		{
			showDataSourceEditorMessage(null, "Error: Web Service not running!", "openTMS Update Counter Values",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		OpenTMSWebServiceInterface openTMSService = service.getOpenTMSPort();

		HashMap<String, String> paramHash = new HashMap<String, String>();
		String parameters = "";
		webServerUpdateCounter = "";
		try
		{
			paramHash.put("method", "listuser");
			paramHash.put("name", this.syncServerDataSource);
			paramHash.put("user-id", currentUser);
			parameters = OpenTMSWebServiceResult.jsonSerialise(paramHash);
			String result = openTMSService.synchronize(parameters);
			System.out.println("result: " + result);
			System.out.println(OpenTMSWebServiceResult.deserializeToString(result));
			@SuppressWarnings("rawtypes")
			HashMap rethash = (HashMap) new JSONDeserializer().deserialize(result);
			String userList = (String) rethash.get("userlist");

			String message = "User list: " + userList;

			// "result":"error"
			if (rethash.containsKey("result") && (rethash.get("result").equals("error")))
			{
				message = (String) rethash.get("errorstring");
			}

			showDataSourceEditorMessage(null, message, "openTMS User List",
					JOptionPane.INFORMATION_MESSAGE);
		}
		catch (Exception ex)
		{
			// TODO Auto-generated catch block
			ex.printStackTrace();
			showDataSourceEditorMessage(null, "Error: Web Service Error!", "openTMS Update Counter Values",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * openDataSource
	 */
	public void openDataSource()
	{
		try
		{
			ChooseDataSourceDialog chooseDataSourceDialog = new ChooseDataSourceDialog(shell, false);
			chooseDataSourceDialog.show();
			String datasource = chooseDataSourceDialog.getDataSource();

			if (datasource != null)
			{
				openDataSource(datasource);
			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();

		}
	}

	/**
	 * openDataSource open a data source in a data source form editor
	 * 
	 * @param datasource
	 *            the data source to open
	 * @return the data source form created
	 */
	public DataSourceForm openDataSource(String datasource)
	{
		Cursor hglass = new Cursor(shell.getDisplay(), SWT.CURSOR_WAIT);
		Cursor arrow = new Cursor(shell.getDisplay(), SWT.CURSOR_ARROW);
		DataSourceForm form = null;
		shell.setCursor(hglass);
		try
		{
			if (datasource != null)
			{
				CTabItem[] tabs = dataSourceTabs.getItems();
				for (int i = 0; i < tabs.length; i++)
				{
					if (tabs[i].getText().equals(datasource))
					{
						shell.setCursor(arrow);
						return (DataSourceForm) tabs[i].getData("DataSourceForm");
					}
				}

				CTabItem dataSource = new CTabItem(dataSourceTabs, SWT.BORDER | SWT.CLOSE);
				dataSource.setData("datasource", datasource);
				dataSource.addListener(SWT.Close, new Listener()
				{
					public void handleEvent(Event event)
					{
						CTabItem dataSourceTemp = (CTabItem) event.item;
						String ds = (String) dataSourceTemp.getData("datasource");
						try
						{
							DataSourceInstance.removeInstance(ds);
						}
						catch (OpenTMSException e)
						{
							e.printStackTrace();
						}
					}
				});

				dataSource.setText(datasource);
				Composite formComposite = new Composite(dataSourceTabs, SWT.BORDER);
				formComposite.setLayout(new FillLayout());
				form = new DataSourceForm(formComposite, SWT.NONE, datasource, this);
				form.setData("DataSourceEditor", this);
				if (form.getDataSource() != null)
				{
					dataSource.setControl(formComposite);
					dataSourceTabs.setSelection(dataSource);
					dataSource.setData("DataSourceForm", form);
					dataSource.setData("DataSource", form.getDataSource());
				}
				else
				{
					dataSource.dispose();
					MessageBox messageBox = new MessageBox(shell);
					String string = message.getString("Error_Opening_Datasource");
					messageBox.setText(string);
					string = message.getString("Data_Source_Form_Not_Opened") + " " + datasource;
					messageBox.setMessage(string);
					messageBox.open();
				}
			}
			shell.setCursor(arrow);
			return form;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			shell.setCursor(arrow);
			return form;
		}
	}

	protected void removeUser()
	{
		@SuppressWarnings("unused")
		OpenTMSWebServiceConstants consts = new OpenTMSWebServiceConstants();
		@SuppressWarnings("unused")
		String url = de.folt.util.OpenTMSProperties.getInstance().getOpenTMSProperty("OpenTMS.WebService.url");

		MenuItem startWebService = menuItems.get("startWebService");
		OpenTMSWebService service = (OpenTMSWebService) startWebService.getData("WebService");

		if (service == null)
		{
			MenuItem connectWebService = menuItems.get("connectWebService");
			service = (OpenTMSWebService) connectWebService.getData("WebService");
		}

		if (service == null)
		{
			showDataSourceEditorMessage(null, "Error: Web Service not running!", "openTMS Update Counter Values",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		OpenTMSWebServiceInterface openTMSService = service.getOpenTMSPort();

		HashMap<String, String> paramHash = new HashMap<String, String>();
		String parameters = "";
		webServerUpdateCounter = "";
		try
		{
			String newuser = "";

			newuser = JOptionPane.showInputDialog("Remove User");
			if ((newuser == null) || (newuser.equals("")))
				return;
			paramHash.put("method", "removeuser");
			paramHash.put("removeuser", newuser);
			paramHash.put("name", this.syncServerDataSource);
			paramHash.put("user-id", currentUser);
			parameters = OpenTMSWebServiceResult.jsonSerialise(paramHash);
			String result = openTMSService.synchronize(parameters);
			System.out.println("result: " + result);
			System.out.println(OpenTMSWebServiceResult.deserializeToString(result));
			@SuppressWarnings({ "rawtypes" })
			HashMap rethash = (HashMap) new JSONDeserializer().deserialize(result);

			String message = "User removed: " + newuser;

			// "result":"error"
			if (rethash.containsKey("result") && (rethash.get("result").equals("error")))
			{
				message = (String) rethash.get("errorstring");
			}

			showDataSourceEditorMessage(null, message, "openTMS Remove User",
					JOptionPane.INFORMATION_MESSAGE);
		}
		catch (Exception ex)
		{
			// TODO Auto-generated catch block
			ex.printStackTrace();
			showDataSourceEditorMessage(null, "Error: Web Service Error!", "openTMS Update Counter Values",
					JOptionPane.ERROR_MESSAGE);
		}

	}

	public void run(String[] args, Display display)
	{
		try
		{

			String webserverClasspath = "datasourceclasspath";
			if (new File(webserverClasspath).exists())
				new de.folt.util.JarFileLoader(webserverClasspath, true);

			this.display = display;
			if (display != null)
			{
				bDisplayFromOtherApplictions = true;
			}
			Hashtable<String, String> arguments = de.folt.util.OpenTMSSupportFunctions.argumentReader(args);
			propertiesFile = arguments.get("propertiesFile");

			if (bDisplayFromOtherApplictions)
			{
				logfile = de.folt.util.OpenTMSLogger.returnLogFile();
			}
			else
			{
				logfile = "log/" + de.folt.util.OpenTMSSupportFunctions.getCurrentUser() + "."
						+ de.folt.util.OpenTMSSupportFunctions.getDateStringFine() + ".log";
			}
			de.folt.util.OpenTMSLogger.setLogFile(logfile);

			File flog = new File(logfile);
			if (flog.exists())
			{
				logfile = flog.getCanonicalPath();
			}
			System.out.println("OpenTMS Data Source Editor");
			System.out.println("Version Info: " + de.folt.constants.OpenTMSVersionConstants.getFullVersionString());
			System.out.println("Full Version Info:\n"
					+ de.folt.constants.OpenTMSVersionConstants.getAllVersionsAsString());

			@SuppressWarnings("rawtypes")
			Vector<Class> classes = OpenTMSVersionConstants.getAllLoadedClasses();
			for (int i = 0; i < classes.size(); i++)
			{
				System.out.println(classes.get(i).getName() + " "
						+ de.folt.util.OpenTMSSupportFunctions.getCompileDate(classes.get(i)) + " "
						+ OpenTMSVersionConstants.getVersionString(classes.get(i)));
			}

			if ((propertiesFile != null) && !propertiesFile.equals(""))
			{
				File f = new File(propertiesFile);
				if (f.exists())
				{
					OpenTMSProperties.setPropfileName(propertiesFile);
					OpenTMSProperties.getInstance(propertiesFile);
				}
			}

			String propstring = OpenTMSProperties.getInstance().getOpenTMSPropertiesAsString();
			System.out.println("OpenTMS Properties " + OpenTMSProperties.getPropfileName() + ":\n" + propstring);

			@SuppressWarnings("unused")
			OpenTMSInitialJarFileLoader openTMSInitialJarFileLoader = new OpenTMSInitialJarFileLoader();

			createShell();
			initShell();
			if ((args != null) && args.length > 0)
			{
				String datasource = args[0];
				this.openDataSource(datasource);
			}
			runShell();

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	/**
	 * runShell
	 */
	private void runShell()
	{
		shell.open();

		setBRunning(true);

		if (bExternalShell)
			return;

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
			if (bDisplayFromOtherApplictions == false)
				display.dispose();
			else
				shell.setVisible(false);
		}
	}

	/**
	 * saveDataSourceAs
	 * 
	 * @param dataSource
	 * @param newDataSourcename
	 * @return
	 */
	protected boolean saveDataSourceAs(DataSource dataSource, String newDataSourcename)
	{
		try
		{
			DataSource newDataSource = DataSourceInstance.createInstance(newDataSourcename);
			if (newDataSource == null)
				return false;
			// int iResult = dataSource.copyTo(newDataSource);
			int iResult = newDataSource.copyFrom(dataSource);
			if (iResult >= 0)
				return true;
			return false;
		}
		catch (OpenTMSException e)
		{
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * @param externalShell
	 *            the bExternalShell to set
	 */
	public void setBExternalShell(boolean externalShell)
	{
		bExternalShell = externalShell;
	}

	public void setbFirstSyncDone(boolean bFirstSyncDone)
	{
		this.bFirstSyncDone = bFirstSyncDone;
	}

	/**
	 * @param bRunning
	 *            the bRunning to set
	 */
	public void setBRunning(boolean bRunning)
	{
		this.bRunning = bRunning;
	}

	public void setCurrentUser(String currentUser)
	{
		this.currentUser = currentUser;
	}

	/**
	 * @param dataSourceEditorVersion
	 *            the dataSourceEditorVersion to set
	 */
	public void setDataSourceEditorVersion(String dataSourceEditorVersion)
	{
		this.dataSourceEditorVersion = dataSourceEditorVersion;
	}

	/**
	 * @param display
	 *            the display to set
	 */
	public void setDisplay(Display display)
	{
		this.display = display;
	}

	public void setlClientUpdateCounter(long lClientUpdateCounter)
	{
		this.lClientUpdateCounter = lClientUpdateCounter;
	}

	public void setlDiffClientWebServerUpdateCounter(long lDiffClientWebServerUpdateCounter)
	{
		this.lDiffClientWebServerUpdateCounter = lDiffClientWebServerUpdateCounter;
	}

	public void setlWebServerUpdateCounter(long iWebServerUpdateCounter)
	{
		this.lWebServerUpdateCounter = iWebServerUpdateCounter;
	}

	/**
	 * @param shell
	 *            the shell to set
	 */
	public void setShell(Shell shell)
	{
		this.shell = shell;
	}

	public void setSyncServerDataSource(String syncServerDataSource)
	{
		this.syncServerDataSource = syncServerDataSource;
	}

	public void setUpdateCounter(String updateCounter)
	{
		this.webServerUpdateCounter = updateCounter;
	}

	public void setWebServerUpdateCounter(String webServerUpdateCounter)
	{
		this.webServerUpdateCounter = webServerUpdateCounter;
	}

	public void showDataSourceEditorMessage(JFrame jFrame, String message, String value, int messageType)
	{
		showDataSourceEditorMessage(message, value, messageType);
		return;
	}

	public void showDataSourceEditorMessage(String message, String value, int messageType)
	{
		if (frmOpt == null)
			frmOpt = new JFrame();
		frmOpt.setVisible(true);
		frmOpt.setLocation(300, 300);
		frmOpt.setAlwaysOnTop(true);
		frmOpt.setVisible(false);
		JOptionPane.showMessageDialog(frmOpt, message, value, messageType);
		return;
	}

	private void startWebServiceAction()
	{
		String url = de.folt.util.OpenTMSProperties.getInstance().getOpenTMSProperty("OpenTMS.WebService.url");
		String urldialogue = de.folt.util.OpenTMSProperties.getInstance().getOpenTMSProperty(
				"OpenTMS.WebService.dialogue");
		boolean bWithDialogue = false;
		try
		{
			bWithDialogue = Boolean.parseBoolean(urldialogue);
			bWithDialogue = false;
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
		}
		OpenTMSWebServiceServer server;
		OpenTMSWebServiceImplementation serverimp;
		if ((url != null) && !url.equals(""))
		{
			server = new OpenTMSWebServiceServer(url);
			serverimp = server.createServer(url, bWithDialogue);
		}
		else
		{
			server = new OpenTMSWebServiceServer();
			serverimp = server.createServer(bWithDialogue);
		}

		MenuItem startWebService = menuItems.get("startWebService");
		startWebService.setData("WebServer", server);

		MenuItem connectWebService = menuItems.get("connectWebService");
		connectWebService.setData("WebServer", server);

		if (serverimp != null)
		{
			System.out.println("openTMS WebServices Server started");
			String message = server.getOpenTMSWebServiceConstants().openTMSWebServerURL + "\n"
					+ server.getOpenTMSWebServiceConstants().openTMSWebServerNameSpace + "\n"
					+ server.getOpenTMSWebServiceConstants().openTMSWebServerService + "\n"
					+ server.getOpenTMSWebServiceConstants().openTMSWebServerWSDL;
			System.out.println("Start OpenTMS Server: \n" + message);

			OpenTMSWebServiceConstants consts = new OpenTMSWebServiceConstants();
			if (server != null)
				consts = server.getOpenTMSWebServiceConstants();
			if (url != null)
				consts.setOpenTMSWebServerURL(url);
			OpenTMSWebService service = new OpenTMSWebService(consts);
			startWebService.setData("WebService", service);
			connectWebService.setData("WebService", service);
			OpenTMSWebServiceInterface openTMSService = service.getOpenTMSPort();
			HashMap<String, String> paramHash = new HashMap<String, String>();
			String parameters = "";
			webServerUpdateCounter = "";
			try
			{
				paramHash.put("method", "getUpdateCounter");
				paramHash.put("user-id", currentUser);
				parameters = OpenTMSWebServiceResult.jsonSerialise(paramHash);
				String result = openTMSService.synchronize(parameters);
				System.out.println("result: " + result);
				System.out.println(OpenTMSWebServiceResult.deserializeToString(result));
				@SuppressWarnings("rawtypes")
				HashMap rethash = (HashMap) new JSONDeserializer().deserialize(result);
				webServerUpdateCounter = (String) rethash.get("update-counter");

				bFirstSyncDone = false;

				try
				{
					lWebServerUpdateCounter = Long.parseLong(webServerUpdateCounter);
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}

				OpenTMSSQLDataSource otemp = new OpenTMSSQLDataSource();
				lClientUpdateCounter = otemp.currentTimeMillis();
				lDiffClientWebServerUpdateCounter = lClientUpdateCounter - lWebServerUpdateCounter;

				message = message + "\nWeb Server Update Counter: " + webServerUpdateCounter
						+ "\nClient Update Counter: " + lClientUpdateCounter + "\nDifference: "
						+ lDiffClientWebServerUpdateCounter;
			}
			catch (Exception ex)
			{
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}

			// return a list of available sync data sources
			parameters = "";
			paramHash = new HashMap<String, String>();
			paramHash.put("user-id", currentUser);
			paramHash.put("method", "getSyncDataSources");
			parameters = OpenTMSWebServiceResult.jsonSerialise(paramHash);
			String result = openTMSService.synchronize(parameters);
			System.out.println("result: " + result);
			System.out.println(OpenTMSWebServiceResult.deserializeToString(result));

			@SuppressWarnings("rawtypes")
			HashMap rethash = (HashMap) new JSONDeserializer().deserialize(result);
			String synDataSources = (String) rethash.get("syncDataSources");
			synDataSources = synDataSources.replaceAll("\\;", "\n");

			message = message + "\nSync Data Sources available:\n" + synDataSources;

			showDataSourceEditorMessage(null, message, "openTMS WebServices Server started",
					JOptionPane.INFORMATION_MESSAGE);

			MenuItem stop = menuItems.get("stopWebService");
			stop.setEnabled(true);
			MenuItem start = menuItems.get("startWebService");
			start.setEnabled(false);

			MenuItem chooseSyncDataSource = menuItems.get("chooseSyncDataSource");
			chooseSyncDataSource.setEnabled(true);

			MenuItem updatecounter = menuItems.get("updatecounter");
			updatecounter.setEnabled(true);

			connectWebService = menuItems.get("connectWebService");
			connectWebService.setEnabled(false);

			if (syncServerDataSource != null)
			{
				MenuItem sync = menuItems.get("sync");
				sync.setEnabled(true);

				MenuItem upload = menuItems.get("upload");
				upload.setEnabled(true);

				MenuItem download = menuItems.get("download");
				download.setEnabled(true);

				MenuItem listUser = menuItems.get("listUser");
				listUser.setEnabled(true);

				MenuItem getCreator = menuItems.get("getCreator");
				getCreator.setEnabled(true);

				MenuItem addUser = menuItems.get("addUser");
				addUser.setEnabled(true);

				MenuItem removeUser = menuItems.get("removeUser");
				removeUser.setEnabled(true);
			}
		}
		else
		{
			showDataSourceEditorMessage(null, "Error: openTMS WebServices Server could not be started\n",
					"openTMS WebServices Server not started", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void stopWebServiceAction()
	{
		try
		{
			MenuItem startWebService = menuItems.get("startWebService");
			OpenTMSWebServiceServer server = (OpenTMSWebServiceServer) startWebService.getData("WebServer");
			if (server != null)
			{
				server.shutdownServer();
				showDataSourceEditorMessage(null, "openTMS WebServices Server shutdown\n",
						"openTMS WebServices Server shutdown", JOptionPane.INFORMATION_MESSAGE);
				MenuItem stop = menuItems.get("stopWebService");
				stop.setEnabled(false);
				MenuItem start = menuItems.get("startWebService");
				start.setEnabled(true);

				MenuItem chooseSyncDataSource = menuItems.get("chooseSyncDataSource");
				chooseSyncDataSource.setEnabled(false);

				MenuItem sync = menuItems.get("sync");
				sync.setEnabled(false);

				MenuItem upload = menuItems.get("upload");
				upload.setEnabled(false);

				MenuItem download = menuItems.get("download");
				download.setEnabled(false);

				MenuItem updatecounter = menuItems.get("updatecounter");
				updatecounter.setEnabled(false);

				MenuItem connectWebService = menuItems.get("connectWebService");
				connectWebService.setEnabled(true);

				MenuItem listUser = menuItems.get("listUser");
				listUser.setEnabled(false);

				MenuItem getCreator = menuItems.get("getCreator");
				getCreator.setEnabled(false);

				MenuItem addUser = menuItems.get("addUser");
				addUser.setEnabled(false);

				MenuItem removeUser = menuItems.get("removeUser");
				removeUser.setEnabled(false);

			}
			else
			{
				MenuItem connectWebService = menuItems.get("connectWebService");
				OpenTMSWebService webService = (OpenTMSWebService) connectWebService.getData("WebService");
				if (webService != null)
				{
					showDataSourceEditorMessage(null,
							"Error: Trying to close openTMS WebServices which was not started by this application! Shutdown denied!",
							"openTMS WebServices Server not started by this application", JOptionPane.ERROR_MESSAGE);
				}
				else
				{
					showDataSourceEditorMessage(null,
							"Error: openTMS WebServices was not started!",
							"openTMS WebServices Server not running", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		catch (Exception e2)
		{
			e2.printStackTrace();
			showDataSourceEditorMessage(null,
					"Error: openTMS WebServices Server could not be shutdown\n" + e2.getMessage(),
					"openTMS WebServices Server Exception", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void updateCounterAction()
	{
		@SuppressWarnings("unused")
		OpenTMSWebServiceConstants consts = new OpenTMSWebServiceConstants();
		@SuppressWarnings("unused")
		String url = de.folt.util.OpenTMSProperties.getInstance().getOpenTMSProperty("OpenTMS.WebService.url");

		MenuItem startWebService = menuItems.get("startWebService");
		OpenTMSWebService service = (OpenTMSWebService) startWebService.getData("WebService");

		if (service == null)
		{
			MenuItem connectWebService = menuItems.get("connectWebService");
			service = (OpenTMSWebService) connectWebService.getData("WebService");
		}

		if (service == null)
		{
			showDataSourceEditorMessage(null, "Error: Web Service not running!", "openTMS Update Counter Values",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		OpenTMSWebServiceInterface openTMSService = service.getOpenTMSPort();

		HashMap<String, String> paramHash = new HashMap<String, String>();
		String parameters = "";
		webServerUpdateCounter = "";
		try
		{

			String message = "\nOld Web Server Update Counter: " + webServerUpdateCounter
					+ "\nOld Client Update Counter: " + lClientUpdateCounter + "\nOld Difference: "
					+ lDiffClientWebServerUpdateCounter;
			paramHash.put("method", "getUpdateCounter");
			parameters = OpenTMSWebServiceResult.jsonSerialise(paramHash);
			String result = openTMSService.synchronize(parameters);
			System.out.println("result: " + result);
			System.out.println(OpenTMSWebServiceResult.deserializeToString(result));
			@SuppressWarnings("rawtypes")
			HashMap rethash = (HashMap) new JSONDeserializer().deserialize(result);
			webServerUpdateCounter = (String) rethash.get("update-counter");

			try
			{
				lWebServerUpdateCounter = Long.parseLong(webServerUpdateCounter);
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}

			OpenTMSSQLDataSource otemp = new OpenTMSSQLDataSource();
			lClientUpdateCounter = otemp.currentTimeMillis();
			lDiffClientWebServerUpdateCounter = lClientUpdateCounter - lWebServerUpdateCounter;

			message = message + "\nWeb Server Update Counter: " + webServerUpdateCounter + "\nClient Update Counter: "
					+ lClientUpdateCounter + "\nDifference: " + lDiffClientWebServerUpdateCounter;

			showDataSourceEditorMessage(null, message, "openTMS Update Counter Values",
					JOptionPane.INFORMATION_MESSAGE);
		}
		catch (Exception ex)
		{
			// TODO Auto-generated catch block
			ex.printStackTrace();
			showDataSourceEditorMessage(null, "Error: Web Service Error!", "openTMS Update Counter Values",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void uploadAction()
	{
		// get active chosen database
		CTabItem actItem = dataSourceTabs.getSelection();
		if (actItem == null)
			return;
		@SuppressWarnings("unused")
		DataSourceForm form = (DataSourceForm) actItem.getData("DataSourceForm");
		DataSource dataSource = (DataSource) actItem.getData("DataSource");
		if (dataSource == null)
		{
			showDataSourceEditorMessage(null, "Data Source is null", "openTMS Upload Result Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		HashMap<String, String> paramHash = new HashMap<String, String>();

		try
		{
			@SuppressWarnings("unused")
			OpenTMSWebServiceConstants consts = new OpenTMSWebServiceConstants();
			@SuppressWarnings("unused")
			String url = de.folt.util.OpenTMSProperties.getInstance().getOpenTMSProperty("OpenTMS.WebService.url");

			MenuItem startWebService = menuItems.get("startWebService");
			OpenTMSWebService service = (OpenTMSWebService) startWebService.getData("WebService");

			if (service == null)
			{
				MenuItem connectWebService = menuItems.get("connectWebService");
				service = (OpenTMSWebService) connectWebService.getData("WebService");
			}

			if (service == null)
			{
				showDataSourceEditorMessage(null, "Error: Web Service not running!", "openTMS Update Counter Values",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			/*
			 * if (server != null) consts =
			 * server.getOpenTMSWebServiceConstants(); if (url != null)
			 * consts.setOpenTMSWebServerURL(url);
			 * 
			 * OpenTMSWebService service = new OpenTMSWebService(consts);
			 */
			OpenTMSWebServiceInterface openTMSService = service.getOpenTMSPort();

			String updateCounter = "";

			// here must retrieve all the changed values
			Hashtable<String, Object> param = new Hashtable<String, Object>();
			param.put("dataSourceName", dataSource.getDataSourceName());
			param.put("dataSourceConfigurationsFile", "");
			long updateCounterLocalDatSource = getlWebServerUpdateCounter() + 1;
			param.put("update-counter", updateCounterLocalDatSource + "");
			param.put("encoding", "UTF-8");
			param.put("user-id", currentUser);

			Vector<String> resultexport = de.folt.rpc.connect.Interface.runExportOpenTMSDataSource(param);
			if (resultexport.size() != 4)
			{
				showDataSourceEditorMessage(null, "Error: Export local datasource!", "openTMS Upload", JOptionPane.ERROR_MESSAGE);
				return;
			}

			String iNumExported = resultexport.get(3);
			try
			{
				int iNum = Integer.parseInt(iNumExported);
				if (iNum == 0)
				{
					showDataSourceEditorMessage("Info: No changes in data source; no upload initiated!", "openTMS Upload", JOptionPane.INFORMATION_MESSAGE);
					return;
				}
			}
			catch (Exception ex)
			{

			}
			String tmx_document = resultexport.get(1);

			paramHash.put("method", "upload");
			paramHash.put("user-id", currentUser);
			paramHash.put("password", "");
			paramHash.put("name", getSyncServerDataSource());
			paramHash.put("parameter", "empty");
			paramHash.put("tmx-document", tmx_document);
			paramHash.put("encoding", "BASE64");
			updateCounter = getlWebServerUpdateCounter() + 1l + "";
			paramHash.put("update-counter", updateCounter);
			String parameters = OpenTMSWebServiceResult.jsonSerialise(paramHash);
			String result = openTMSService.synchronize(parameters);
			System.out.println("result: " + result);
			System.out.println(OpenTMSWebServiceResult.deserializeToString(result));
			paramHash = (HashMap) new JSONDeserializer().deserialize(result);
			String resultweb = paramHash.get("result");
			if (resultweb.equals("ok"))
			{
				String message = "Upload for " + iNumExported + "entries ok ";
				showDataSourceEditorMessage(message, "openTMS Upload Result", JOptionPane.INFORMATION_MESSAGE);

				webServerUpdateCounter = paramHash.get("update-counter");
				try
				{
					setlWebServerUpdateCounter(Long.parseLong(webServerUpdateCounter));
					String dataSourceConfigurationsFile = dataSource.getDefaultDataSourceConfigurationsFileName();
					DataSourceConfigurations config = new DataSourceConfigurations(dataSourceConfigurationsFile);
					DataSourceProperties dataModelProperties = new DataSourceProperties();
					dataModelProperties.remove("WebServerUpdateCounter");
					dataModelProperties.put("WebServerUpdateCounter", getlWebServerUpdateCounter() + ":"
							+ getSyncServerDataSource());
					config.addPropertiesToDataModelProperties(dataSource.getDataSourceName(), dataModelProperties);
					config.saveToXmlFile();
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}
			else
			{
				String message = "Upload Error";
				showDataSourceEditorMessage(message + "\n" + paramHash.get("errorstring"), "openTMS Upload Result", JOptionPane.ERROR_MESSAGE);
			}
		}
		catch (Exception ex)
		{
			showDataSourceEditorMessage(ex.getMessage(), "openTMS Upload Result Error", JOptionPane.ERROR_MESSAGE);
		}
	}

}
