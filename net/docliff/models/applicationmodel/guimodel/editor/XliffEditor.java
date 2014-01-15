/*
 * Created on 30.09.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package net.docliff.models.applicationmodel.guimodel.editor;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
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
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.araya.eaglememex.util.EMXProperties;

import de.folt.constants.OpenTMSVersionConstants;
import de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.ChooseDataSourceDialog;
import de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.CopyFromToOpenTMSDataSource;
import de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.CreateOpenTMSDataSource;
import de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.DataSourceEditor;
import de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.DeleteOpenTMSDataSource;
import de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.DisplayLog;
import de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.ExportOpenTMSDataSource;
import de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.ExportXliffInternalTerminology;
import de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.ImportOpenTMSDataSource;
import de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.ManageMultipleDataSource;
import de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.OpenTMSTranslate;
import de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.ReverseConversion;
import de.folt.models.applicationmodel.guimodel.support.ProgressDialog;
import de.folt.models.applicationmodel.guimodel.support.ProgressDialogSupport;
import de.folt.models.datamodel.DataSource;
import de.folt.models.datamodel.DataSourceConfigurations;
import de.folt.models.datamodel.DataSourceInstance;
import de.folt.models.datamodel.DataSourceProperties;
import de.folt.models.datamodel.sql.OpenTMSSQLDataSource;
import de.folt.models.documentmodel.document.XmlDocument;
import de.folt.util.ColorTable;
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
 * Class implements the docliff xliff editor for openTMS. It is derived in parts
 * from Heartsome Europe Araya xliff editor (www.heartsome,de)
 * 
 * @author klemens
 * 
 */
public class XliffEditor
{
	public class DictionarySelectionObserver implements Observer
	{

		@SuppressWarnings("unused")
		private XliffEditor xliffEditor = null;

		/**
		 * @param xliffEditorFormWindow
		 */
		public DictionarySelectionObserver(XliffEditor xliffEditor)
		{
			super();
			this.xliffEditor = xliffEditor;
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
				int iTab = xliffEditorWindowsTabs.getSelectionIndex();
				if (iTab >= 0)
				{
					CTabItem tab = xliffEditorWindowsTabs.getItem(iTab);
					XliffEditorForm form = (XliffEditorForm) tab.getData("XliffEditorForm");
					if (form != null)
					{
						XliffEditorWindow win = ((XliffEditorForm) tab.getData("XliffEditorForm"))
								.getXliffEditorWindow();
						SimpleXliffEditorWindow simpleXliffEditorWindow = form.getTargetTextWindow();
						String targetTerm = (String) arg1;
						if (win.getLostFocus() > simpleXliffEditorWindow.getLostFocus())
						{
							win.setSelectedText(targetTerm);
							TransUnitInformationData trans = win.getCurrentTransUnitInformation();
							simpleXliffEditorWindow.setText(trans.getTargetText());
							simpleXliffEditorWindow.setBackground(win.getDisplay().getSystemColor(SWT.COLOR_WHITE));
							simpleXliffEditorWindow.setStyleRange(trans.getTargetText());
							simpleXliffEditorWindow.setToolTipText(message.getString("targetTextWindow") + "\n"
									+ trans.getFullSourceText());
						}
						else
						{
							simpleXliffEditorWindow.insert(targetTerm);
							win.setText(simpleXliffEditorWindow.getText(), -1);
							win.setBChanged(true);
						}
					}
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}

		}
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
			new XliffEditor(args);
			System.exit(0);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			System.exit(1);
		}

	}

	@SuppressWarnings("unused")
	private boolean bConvert = false;

	protected boolean bFirstSyncDone = false;

	private String configfile;

	private String curruser;

	private Font defaultFont = null;

	private Display display;

	private de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.EditorConfiguration editorConfiguration = null;

	private int editorHeight;

	private int editorWidth;

	private int editorxLocation;

	private int editoryLocation;

	protected long lClientUpdateCounter = -1l;

	protected Object lDiffClientWebServerUpdateCounter = -1l;

	private String logfile;

	protected long lWebServerUpdateCounter = -1l;

	private Menu menubar;

	private Hashtable<String, MenuItem> menuItems = new Hashtable<String, MenuItem>();

	private de.folt.util.Messages message;

	private PreferencesContainer preferencesContainer = new PreferencesContainer();

	private String propertiesFile = "";

	private Shell shell;

	@SuppressWarnings("unused")
	private Color standardBackground;

	private DataSource syncLocalDataSource;

	private String syncLocalDataSourceName;

	private String syncServerDataSource;

	private String userLanguage = "en";

	private String webServerUpdateCounter = "";

	private XliffEditorDictionaryViewer xliffEditorDictionaryViewer;

	private XliffEditorDictionaryViewer xliffEditorSegmentDictionaryViewer;

	private String xliffEditorVersion = de.folt.constants.OpenTMSVersionConstants.getFullVersionString();

	private CTabFolder xliffEditorWindowsTabs;

	/**
     * 
     */
	public XliffEditor()
	{
		// TODO Auto-generated constructor stub
	}

	public XliffEditor(String[] args)
	{
		try
		{

			String webserverClasspath = "docliffclasspath";
			if (new File(webserverClasspath).exists())
				new de.folt.util.JarFileLoader(webserverClasspath, true);

			Hashtable<String, String> arguments = de.folt.util.OpenTMSSupportFunctions.argumentReader(args);
			propertiesFile = arguments.get("propertiesFile");

			logfile = "log/" + de.folt.util.OpenTMSSupportFunctions.getCurrentUser() + "."
					+ de.folt.util.OpenTMSSupportFunctions.getDateStringFine() + ".log";

			de.folt.util.OpenTMSLogger.setLogFile(logfile);

			File flog = new File(logfile);
			if (flog.exists())
			{
				logfile = flog.getCanonicalPath();
			}
			System.out.println("docliff Xliff Editor");
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

			configfile = OpenTMSProperties.getInstance().getOpenTMSProperty(
					"OpenTMS.XliffEditor.EditorConfigurationDirectory");
			String propfileName = OpenTMSProperties.getInstance().getOpenTMSProperty("ArayaPropertiesFile");
			EMXProperties.getInstance(propfileName);

			curruser = System.getProperty("user.name").toLowerCase();
			editorConfiguration = new de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.EditorConfiguration(
					shell, configfile, "docliffEditor", curruser);

			preferencesContainer.getPreferences(editorConfiguration);

			createShell();
			initShell();
			openXliffFile(args);
			shell.pack();

			shell.setSize(editorWidth, editorHeight);
			shell.setLocation(editorxLocation, editoryLocation);

			runShell();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

	}

	private void aboutDialog()
	{
		About about = new About(shell);
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
				if (fileTransfer.isSupportedType(event.currentDataType))
				{
					String[] files = (String[]) event.data;
					if (files.length > 0)
					{
						String xliffFile = files[0];
						// check for the file type
						String doctype = XmlDocument.getRootElementName(xliffFile);
						if (doctype.equals("xliff"))
							openXliffFile(xliffFile);
						else
						{
							OpenTMSTranslate trans = new OpenTMSTranslate(shell, "", "", xliffFile);
							trans.show();
							xliffFile = trans.getXliffDocument();
							File f = new File(xliffFile);
							if (f.exists())
								openXliffFile(xliffFile);
							trans = null;
						}
					}
				}
			}

			public void dropAccept(DropTargetEvent event)
			{
				;
			}
		});
	}

	private void chooseDataSourceAction()
	{
		@SuppressWarnings("unused")
		OpenTMSWebServiceConstants consts = new OpenTMSWebServiceConstants();
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
			JOptionPane.showMessageDialog(null, "Error: Web Service not running!", "openTMS Update Counter Values",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		/*
		 * if (server != null) consts = server.getOpenTMSWebServiceConstants();
		 * if (url != null) consts.setOpenTMSWebServerURL(url);
		 * 
		 * OpenTMSWebService service = new OpenTMSWebService(consts);
		 */
		OpenTMSWebServiceInterface openTMSService = service.getOpenTMSPort();
		String parameters = "";
		HashMap<String, String> paramHash = new HashMap<String, String>();
		paramHash.put("method", "getSyncDataSources");
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
			// this.syncLocalDataSource =
			// DataSourceInstance.createInstance(syncServerDataSource);
			// if (this.syncLocalDataSource == null)
			// {
			// JOptionPane.showMessageDialog(null,
			// "Error: SyncServerDataSource " + syncServerDataSource +
			// " could not be loaded!", "Sync DataSource Error",
			// JOptionPane.ERROR_MESSAGE);
			// syncServerDataSource = null;
			// }
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
			syncServerDataSource = null;
			// syncLocalDataSource = null;
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
			OpenTMSWebServiceInterface openTMSService = service.getOpenTMSPort();
			String updateCounter = "";

			MenuItem connectWebService = menuItems.get("connectWebService");
			connectWebService.setData("WebService", service);

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

			JOptionPane.showMessageDialog(null, "Successfully conntected to Sync Webservice\n" + message
					+ "\nUpdate Counter = " + updateCounter, "openTMS Sync Web Service Connection",
					JOptionPane.INFORMATION_MESSAGE);

			MenuItem stop = menuItems.get("stopWebService");
			stop.setEnabled(false);
			MenuItem start = menuItems.get("startWebService");
			start.setEnabled(false);

			MenuItem chooseSyncDataSource = menuItems.get("chooseSyncDataSource");
			chooseSyncDataSource.setEnabled(true);

			MenuItem sync = menuItems.get("sync");
			sync.setEnabled(true);

			MenuItem upload = menuItems.get("upload");
			upload.setEnabled(true);

			MenuItem download = menuItems.get("download");
			download.setEnabled(true);

			MenuItem updatecounter = menuItems.get("updatecounter");
			updatecounter.setEnabled(true);

			connectWebService.setEnabled(false);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error when conntecting to Sync Web Service\n" + url,
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
				CTabItem actItem = xliffEditorWindowsTabs.getSelection();
				if (actItem == null)
					return;
				XliffEditorForm form = (XliffEditorForm) actItem.getData("XliffEditorForm");
				if (form.getXliffEditorWindow().isFocusControl())
					form.getXliffEditorWindow().cut();
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
				CTabItem actItem = xliffEditorWindowsTabs.getSelection();
				if (actItem == null)
					return;
				XliffEditorForm form = (XliffEditorForm) actItem.getData("XliffEditorForm");
				if (form.getXliffEditorWindow().isFocusControl())
					form.getXliffEditorWindow().copy();

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
				CTabItem actItem = xliffEditorWindowsTabs.getSelection();
				if (actItem == null)
					return;
				XliffEditorForm form = (XliffEditorForm) actItem.getData("XliffEditorForm");
				if (form.getXliffEditorWindow().isFocusControl())
					form.getXliffEditorWindow().paste();

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
				openXliffFile();
			}
		});
		menuItems.put("fileNew", fileOpen);

		MenuItem fileClose = new MenuItem(fmenu, SWT.PUSH);
		fileClose.setText(message.getString("Close_File")); //$NON-NLS-1$
		fileClose.setAccelerator(SWT.CONTROL | 'W');
		fileClose.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				int iTab = xliffEditorWindowsTabs.getSelectionIndex();
				if (iTab >= 0)
				{
					CTabItem dataSourceTemp = xliffEditorWindowsTabs.getItem(iTab);
					@SuppressWarnings("unused")
					String ds = (String) dataSourceTemp.getData("xliffFile");
					try
					{
						if (dataSourceTemp.getData("XliffEditorForm") != null)
						{
							XliffEditorForm xliffEditorForm = (XliffEditorForm) dataSourceTemp
									.getData("XliffEditorForm");
							if (xliffEditorForm.getXliffEditorWindow().isBChanged())
							{
								// ask if to be saved when changed
								int style = SWT.PRIMARY_MODAL | SWT.ICON_QUESTION | SWT.YES | SWT.NO | SWT.CANCEL;
								MessageBox messageBox = new MessageBox(shell, style);
								messageBox.setMessage(message.getString("Save_Changes")); //$NON-NLS-1$
								int result = messageBox.open();
								if (result == SWT.CANCEL)
								{
									return;
								}
								else
								{
									if (result == SWT.YES)
									{
										((XliffEditorForm) dataSourceTemp.getData("XliffEditorForm"))
												.saveXliffDocument();
									}
								}
							}

							xliffEditorForm.closeAllDataSources();
						}
					}
					catch (Exception ex)
					{
						ex.printStackTrace();
					}
					dataSourceTemp.dispose();
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
				int iTab = xliffEditorWindowsTabs.getSelectionIndex();
				if (iTab >= 0)
				{
					CTabItem tab = xliffEditorWindowsTabs.getItem(iTab);
					if (tab.getData("XliffEditorForm") != null)
						((XliffEditorForm) tab.getData("XliffEditorForm")).saveXliffDocument();
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
				int iTab = xliffEditorWindowsTabs.getSelectionIndex();
				if (iTab >= 0)
				{
					CTabItem tab = xliffEditorWindowsTabs.getItem(iTab);
					if (tab.getData("XliffEditorForm") != null)
					{
						boolean bNowSave = true;
						while (bNowSave)
						{
							FileDialog fd = new FileDialog(shell, SWT.OPEN);
							String extensions[] = { "*.xlf;*.xliff;*.xml*.*" };
							fd.setFilterExtensions(extensions);
							if (System.getProperty("file.separator").equals("/"))
							{
								fd.setFilterPath(System.getProperty("user.home"));
							}
							fd.open();
							if (fd.getFileName() == "")
							{ //$NON-NLS-1$
								fd = null;
								extensions = null;
								return;
							}
							else
							{

								String separator = System.getProperty("file.separator");
								String filename = fd.getFilterPath() + separator + fd.getFileName();
								fd = null;
								File fx = new File(filename);
								if (fx.exists())
								{
									// ask if to be overwritten
									int style = SWT.PRIMARY_MODAL | SWT.ICON_QUESTION | SWT.YES | SWT.NO | SWT.CANCEL;
									MessageBox messageBox = new MessageBox(shell, style);
									messageBox.setMessage(message.getString("File_exists")); //$NON-NLS-1$
									int result = messageBox.open();
									if (result == SWT.CANCEL)
									{
										bNowSave = true;
										continue;
									}
									else
									{
										if (result == SWT.YES)
										{
											((XliffEditorForm) tab.getData("XliffEditorForm"))
													.saveXliffDocument(filename);
										}
									}
									fx = null;
									return;
								}
								else
								{
									((XliffEditorForm) tab.getData("XliffEditorForm")).saveXliffDocument(filename);
									return;
								}
							}
						}
					}
				}
			}
		});

		menuItems.put("fileSaveAs", fileSaveAs);

		MenuItem recentFilesMenu = new MenuItem(fmenu, SWT.CASCADE);
		recentFilesMenu.setText(message.getString("recentFiles")); //$NON-NLS-1$
		recentFilesMenu.setAccelerator(SWT.CONTROL | 'R');

		recentFilesMenu.addListener(SWT.Selection, new Listener()
		{
			public void handleEvent(Event e)
			{

			}
		});

		Menu recentFMenu = new Menu(shell, SWT.DROP_DOWN);
		recentFilesMenu.setMenu(recentFMenu);
		String recentFilesString = editorConfiguration.loadValueForKey("recentXliffFiles");
		int iMaxFiles = 10;
		if ((recentFilesString != null) && !recentFilesString.equals(""))
		{
			String[] recentFiles = recentFilesString.split(";");
			int iStart = Math.max(0, recentFiles.length - iMaxFiles);
			for (int i = iStart; i < recentFiles.length; i++)
			{
				File f = new File(recentFiles[i]);
				if (!f.exists())
				{
					continue;
				}
				MenuItem menuitem = new MenuItem(recentFMenu, SWT.PUSH);
				menuitem.setText((i - iStart + 1) + ": " + recentFiles[i]);
				menuitem.setData(recentFiles[i]);
				menuitem.addListener(SWT.Selection, new Listener()
				{
					public void handleEvent(Event e)
					{
						String file = (String) ((MenuItem) e.widget).getData();
						openXliffFile(file);
					}
				});
			}
		}

		new MenuItem(fmenu, SWT.SEPARATOR);

		MenuItem fileExit = new MenuItem(fmenu, SWT.PUSH);
		fileExit.setText(message.getString("E&xit_tAlt_+_X_108")); //$NON-NLS-1$
		fileExit.setAccelerator(SWT.ALT | 'X');
		fileExit.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				for (int i = 0; i < xliffEditorWindowsTabs.getItemCount(); i++)
				{
					CTabItem dataSourceTemp = xliffEditorWindowsTabs.getItem(i);
					try
					{
						if (dataSourceTemp.getData("XliffEditorForm") == null)
							continue;

						if (((XliffEditorForm) dataSourceTemp.getData("XliffEditorForm")).getXliffEditorWindow()
								.isBChanged())
						{
							// ask if to be saved when changed
							int style = SWT.PRIMARY_MODAL | SWT.ICON_QUESTION | SWT.YES | SWT.NO | SWT.CANCEL;
							MessageBox messageBox = new MessageBox(shell, style);
							messageBox.setMessage(message.getString("Save_Changes") + "\n" + dataSourceTemp.getText());
							int result = messageBox.open();
							if (result == SWT.CANCEL)
							{
								return;
							}
							else if (result == SWT.YES)
							{
								((XliffEditorForm) dataSourceTemp.getData("XliffEditorForm")).saveXliffDocument();
							}

							((XliffEditorForm) dataSourceTemp.getData("XliffEditorForm")).closeAllDataSources();
						}
					}
					catch (Exception ex)
					{
						ex.printStackTrace();
					}
				}
				editorConfiguration.saveKeyValuePair("editorWidth", shell.getSize().x + "");
				editorConfiguration.saveKeyValuePair("editorHeight", shell.getSize().y + "");
				editorConfiguration.saveKeyValuePair("editorxLocation", shell.getLocation().x + "");
				editorConfiguration.saveKeyValuePair("editoryLocation", shell.getLocation().y + "");
				xliffEditorDictionaryViewer.savePosition();
				xliffEditorSegmentDictionaryViewer.savePosition();
				display.dispose();
			}
		});
		menuItems.put("fileExit", fileExit);
	}

	private void createGotoMenu(Menu menubar)
	{

		MenuItem gotoMenu = new MenuItem(menubar, SWT.CASCADE);
		gotoMenu.setText(message.getString("Goto")); //$NON-NLS-1$
		Menu emenu = new Menu(gotoMenu);
		gotoMenu.setMenu(emenu);

		MenuItem editNext = new MenuItem(emenu, SWT.PUSH);
		editNext.setText(message.getString("Next_segment")); //$NON-NLS-1$
		editNext.setAccelerator(SWT.PAGE_DOWN);
		editNext.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				int iTab = xliffEditorWindowsTabs.getSelectionIndex();
				if (iTab >= 0)
				{
					CTabItem tab = xliffEditorWindowsTabs.getItem(iTab);
					if (tab.getData("XliffEditorForm") != null)
					{
						((XliffEditorForm) tab.getData("XliffEditorForm")).getXliffEditorWindow().showNextSegment();
						((XliffEditorForm) tab.getData("XliffEditorForm")).getXliffEditorObserver().update(null, null);
					}
				}
			}
		});

		MenuItem editPrevious = new MenuItem(emenu, SWT.PUSH);
		editPrevious.setText(message.getString("Previous_segment")); //$NON-NLS-1$
		editPrevious.setAccelerator(SWT.PAGE_UP);
		editPrevious.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				int iTab = xliffEditorWindowsTabs.getSelectionIndex();
				if (iTab >= 0)
				{
					CTabItem tab = xliffEditorWindowsTabs.getItem(iTab);
					if (tab.getData("XliffEditorForm") != null)
					{
						((XliffEditorForm) tab.getData("XliffEditorForm")).getXliffEditorWindow().showPreviousSegment();
						((XliffEditorForm) tab.getData("XliffEditorForm")).getXliffEditorObserver().update(null, null);
					}
				}
			}
		});

		new MenuItem(emenu, SWT.SEPARATOR);
		MenuItem firstSegment = new MenuItem(emenu, SWT.PUSH);
		firstSegment.setText(message.getString("Editor.936")); //$NON-NLS-1$
		firstSegment.setAccelerator(SWT.CTRL | SWT.HOME);
		firstSegment.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				int iTab = xliffEditorWindowsTabs.getSelectionIndex();
				if (iTab >= 0)
				{
					CTabItem tab = xliffEditorWindowsTabs.getItem(iTab);
					if (tab.getData("XliffEditorForm") != null)
					{
						((XliffEditorForm) tab.getData("XliffEditorForm")).gotoSegment(0);
						((XliffEditorForm) tab.getData("XliffEditorForm")).getXliffEditorObserver().update(null, null);
					}
				}
			}
		});

		MenuItem lastSegment = new MenuItem(emenu, SWT.PUSH);
		lastSegment.setText(message.getString("Editor.937")); //$NON-NLS-1$
		lastSegment.setAccelerator(SWT.CTRL | SWT.END);
		lastSegment.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				int iTab = xliffEditorWindowsTabs.getSelectionIndex();
				if (iTab >= 0)
				{
					CTabItem tab = xliffEditorWindowsTabs.getItem(iTab);
					if (tab.getData("XliffEditorForm") != null)
					{
						((XliffEditorForm) tab.getData("XliffEditorForm")).gotoSegment(((XliffEditorForm) tab
								.getData("XliffEditorForm")).getXliffEditorWindow().getIOverallSegmentNumber() - 1);
						((XliffEditorForm) tab.getData("XliffEditorForm")).getXliffEditorObserver().update(null, null);
					}
				}
			}
		});

		new MenuItem(emenu, SWT.SEPARATOR);

		MenuItem editJump = new MenuItem(emenu, SWT.PUSH);
		editJump.setText(message.getString("&Jump_to_segment..._tCtrl_+_G_58")); //$NON-NLS-1$
		editJump.setAccelerator(SWT.CTRL | 'G');
		editJump.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				int iTab = xliffEditorWindowsTabs.getSelectionIndex();
				if (iTab >= 0)
				{
					CTabItem tab = xliffEditorWindowsTabs.getItem(iTab);
					if (tab.getData("XliffEditorForm") != null)
					{
						((XliffEditorForm) tab.getData("XliffEditorForm")).gotoSegment((((XliffEditorForm) tab
								.getData("XliffEditorForm")).getJumpText()).getText());
						((XliffEditorForm) tab.getData("XliffEditorForm")).getXliffEditorObserver().update(null, null);
					}
				}
			}
		});

		new MenuItem(emenu, SWT.SEPARATOR);

		MenuItem editNextFuzzy = new MenuItem(emenu, SWT.PUSH);
		editNextFuzzy.setText(message.getString("Next_fuzzy")); //$NON-NLS-1$
		editNextFuzzy.setAccelerator(SWT.CONTROL | 'N');
		editNextFuzzy.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				int iTab = xliffEditorWindowsTabs.getSelectionIndex();
				if (iTab >= 0)
				{
					CTabItem tab = xliffEditorWindowsTabs.getItem(iTab);
					if (tab.getData("XliffEditorForm") != null)
					{
						((XliffEditorForm) tab.getData("XliffEditorForm")).getXliffEditorWindow().showNextFuzzy();
						((XliffEditorForm) tab.getData("XliffEditorForm")).getXliffEditorObserver().update(null, null);
					}
				}
			}
		});

		MenuItem editPreviousFuzzy = new MenuItem(emenu, SWT.PUSH);
		editPreviousFuzzy.setText(message.getString("Previous_fuzzy")); //$NON-NLS-1$
		editPreviousFuzzy.setAccelerator(SWT.CONTROL | 'P');
		editPreviousFuzzy.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				int iTab = xliffEditorWindowsTabs.getSelectionIndex();
				if (iTab >= 0)
				{
					CTabItem tab = xliffEditorWindowsTabs.getItem(iTab);
					if (tab.getData("XliffEditorForm") != null)
					{
						((XliffEditorForm) tab.getData("XliffEditorForm")).getXliffEditorWindow().showPreviousFuzzy();
						((XliffEditorForm) tab.getData("XliffEditorForm")).getXliffEditorObserver().update(null, null);
					}
				}
			}
		});

		new MenuItem(emenu, SWT.SEPARATOR);

		MenuItem editNextToCheck = new MenuItem(emenu, SWT.PUSH);
		editNextToCheck.setText(message.getString("Next_To_Check")); //$NON-NLS-1$
		editNextToCheck.setAccelerator(SWT.CONTROL | '1');
		editNextToCheck.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				int iTab = xliffEditorWindowsTabs.getSelectionIndex();
				if (iTab >= 0)
				{
					CTabItem tab = xliffEditorWindowsTabs.getItem(iTab);
					if (tab.getData("XliffEditorForm") != null)
					{
						((XliffEditorForm) tab.getData("XliffEditorForm")).getXliffEditorWindow().showNextToCheck();
						((XliffEditorForm) tab.getData("XliffEditorForm")).getXliffEditorObserver().update(null, null);
					}
				}
			}
		});

		MenuItem editPreviousToCheck = new MenuItem(emenu, SWT.PUSH);
		editPreviousToCheck.setText(message.getString("Previous_To_Check")); //$NON-NLS-1$
		editPreviousToCheck.setAccelerator(SWT.CONTROL | '2');
		editPreviousToCheck.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				int iTab = xliffEditorWindowsTabs.getSelectionIndex();
				if (iTab >= 0)
				{
					CTabItem tab = xliffEditorWindowsTabs.getItem(iTab);
					if (tab.getData("XliffEditorForm") != null)
					{
						((XliffEditorForm) tab.getData("XliffEditorForm")).getXliffEditorWindow().showPreviousToCheck();
						((XliffEditorForm) tab.getData("XliffEditorForm")).getXliffEditorObserver().update(null, null);
					}
				}
			}
		});

		new MenuItem(emenu, SWT.SEPARATOR);

		MenuItem editNextUntranslated = new MenuItem(emenu, SWT.PUSH);
		editNextUntranslated.setText(message.getString("Next_untranslated")); //$NON-NLS-1$
		editNextUntranslated.setAccelerator(SWT.CONTROL | 'U');
		editNextUntranslated.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				int iTab = xliffEditorWindowsTabs.getSelectionIndex();
				if (iTab >= 0)
				{
					CTabItem tab = xliffEditorWindowsTabs.getItem(iTab);
					if (tab.getData("XliffEditorForm") != null)
					{
						((XliffEditorForm) tab.getData("XliffEditorForm")).getXliffEditorWindow()
								.showNextUntranslatedSegment();
						((XliffEditorForm) tab.getData("XliffEditorForm")).getXliffEditorObserver().update(null, null);
					}
				}
			}
		});

		MenuItem editPreviousUntranslated = new MenuItem(emenu, SWT.PUSH);
		editPreviousUntranslated.setText(message.getString("Editor.940")); //$NON-NLS-1$
		editPreviousUntranslated.setAccelerator(SWT.SHIFT | SWT.CONTROL | 'U');
		editPreviousUntranslated.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				int iTab = xliffEditorWindowsTabs.getSelectionIndex();
				if (iTab >= 0)
				{
					CTabItem tab = xliffEditorWindowsTabs.getItem(iTab);
					if (tab.getData("XliffEditorForm") != null)
					{
						((XliffEditorForm) tab.getData("XliffEditorForm")).getXliffEditorWindow()
								.showPreviousUntranslatedSegment();
						((XliffEditorForm) tab.getData("XliffEditorForm")).getXliffEditorObserver().update(null, null);
					}
				}
			}
		});

		new MenuItem(emenu, SWT.SEPARATOR);

		MenuItem editNextUnapproved = new MenuItem(emenu, SWT.PUSH);
		editNextUnapproved.setText(message.getString("Next_unapproved")); //$NON-NLS-1$
		editNextUnapproved.setAccelerator(SWT.CONTROL | 'Q');
		editNextUnapproved.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				int iTab = xliffEditorWindowsTabs.getSelectionIndex();
				if (iTab >= 0)
				{
					CTabItem tab = xliffEditorWindowsTabs.getItem(iTab);
					if (tab.getData("XliffEditorForm") != null)
					{
						((XliffEditorForm) tab.getData("XliffEditorForm")).getXliffEditorWindow()
								.showNextUnapprovedSegment();
						((XliffEditorForm) tab.getData("XliffEditorForm")).getXliffEditorObserver().update(null, null);
					}
				}
			}
		});

		MenuItem editPreviousUnapproved = new MenuItem(emenu, SWT.PUSH);
		editPreviousUnapproved.setText(message.getString("Editor.941")); //$NON-NLS-1$
		editPreviousUnapproved.setAccelerator(SWT.SHIFT | SWT.CONTROL | 'Q');
		editPreviousUnapproved.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				int iTab = xliffEditorWindowsTabs.getSelectionIndex();
				if (iTab >= 0)
				{
					CTabItem tab = xliffEditorWindowsTabs.getItem(iTab);
					if (tab.getData("XliffEditorForm") != null)
					{
						((XliffEditorForm) tab.getData("XliffEditorForm")).getXliffEditorWindow()
								.showPreviousUnapprovedSegment();
						((XliffEditorForm) tab.getData("XliffEditorForm")).getXliffEditorObserver().update(null, null);
					}
				}
			}
		});

	}

	/**
	 * Method createHelpMenu.
	 * 
	 * @param menubar
	 */
	private void createHelpMenu(Menu menubar)
	{

		MenuItem helpMenu = new MenuItem(menubar, SWT.CASCADE);
		helpMenu.setText(message.getString("Menu_Help"));
		helpMenu.setAccelerator(SWT.ALT | 'H');
		Menu hmenu = new Menu(helpMenu);
		helpMenu.setMenu(hmenu);

		MenuItem showlog = new MenuItem(hmenu, SWT.PUSH);
		showlog.setText(message.getString("ShowLog.0") + " " + logfile + "\tAlt + L");
		showlog.setAccelerator(SWT.ALT | 'L');
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
		homepage.setText(message.getString("OpenTMSHomepage") + "\tF3");
		homepage.setAccelerator(SWT.F3);
		homepage.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				displayOpenTMSHomepage();
			}
		});
		menuItems.put("homepage", homepage);

		MenuItem helpAbout = new MenuItem(hmenu, SWT.PUSH);
		helpAbout.setText(message.getString("&About...") + "\tF6");
		helpAbout.setAccelerator(SWT.F6);
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
	@SuppressWarnings("unused")
	private void createOpenTMSMenu(Menu menubar)
	{

		MenuItem pluginMenu = new MenuItem(menubar, SWT.CASCADE);
		pluginMenu.setText(message.getString("Menu_OpenTMS"));
		pluginMenu.setAccelerator(SWT.ALT | 'o');
		Menu pmenu = new Menu(pluginMenu);
		pluginMenu.setMenu(pmenu);

		MenuItem openDataSource = new MenuItem(pmenu, SWT.PUSH);
		openDataSource.setText(message.getString("openDataSource"));
		openDataSource.setAccelerator(SWT.CTRL | SWT.SHIFT | 'O');
		openDataSource.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				int iTab = xliffEditorWindowsTabs.getSelectionIndex();
				if (iTab >= 0)
				{
					CTabItem dataSourceTemp = xliffEditorWindowsTabs.getItem(iTab);
					if (dataSourceTemp != null)
					{
						SashForm sash = ((XliffEditorForm) dataSourceTemp.getData("XliffEditorForm"))
								.getDataSourceHolders();
						CTabFolder ctab = (CTabFolder) sash.getData("dataSourceWindowsTabs");
						CTabItem ctabitem = ctab.getSelection();
						DataSourceListWithTools dataSourceFormComposite = (DataSourceListWithTools) ctabitem
								.getData("dataSourceFormComposite");
						dataSourceFormComposite.openDataSource();
					}
				}
			}
		});
		menuItems.put("openDataSource", openDataSource);

		MenuItem closeDataSource = new MenuItem(pmenu, SWT.PUSH);
		closeDataSource.setText(message.getString("closeDataSource"));
		openDataSource.setAccelerator(SWT.CTRL | SWT.SHIFT | 'C');
		closeDataSource.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				int iTab = xliffEditorWindowsTabs.getSelectionIndex();
				if (iTab >= 0)
				{
					CTabItem dataSourceTemp = xliffEditorWindowsTabs.getItem(iTab);
					if (dataSourceTemp != null)
					{
						SashForm sash = ((XliffEditorForm) dataSourceTemp.getData("XliffEditorForm"))
								.getDataSourceHolders();
						CTabFolder ctab = (CTabFolder) sash.getData("dataSourceWindowsTabs");
						CTabItem ctabitem = ctab.getSelection();
						DataSourceListWithTools dataSourceFormComposite = (DataSourceListWithTools) ctabitem
								.getData("dataSourceFormComposite");
						dataSourceFormComposite.closeDataSource();
					}
				}
			}
		});
		menuItems.put("closeDataSource", closeDataSource);

		MenuItem closeAllDataSources = new MenuItem(pmenu, SWT.PUSH);
		closeAllDataSources.setText(message.getString("closeAllDataSources"));
		openDataSource.setAccelerator(SWT.CTRL | SWT.SHIFT | 'A');
		closeAllDataSources.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				int iTab = xliffEditorWindowsTabs.getSelectionIndex();
				if (iTab >= 0)
				{
					CTabItem dataSourceTemp = xliffEditorWindowsTabs.getItem(iTab);
					if (dataSourceTemp != null)
					{
						SashForm sash = ((XliffEditorForm) dataSourceTemp.getData("XliffEditorForm"))
								.getDataSourceHolders();
						CTabFolder ctab = (CTabFolder) sash.getData("dataSourceWindowsTabs");
						CTabItem ctabitem = ctab.getSelection();
						DataSourceListWithTools dataSourceFormComposite = (DataSourceListWithTools) ctabitem
								.getData("dataSourceFormComposite");
						dataSourceFormComposite.closeAllDataSourcesWithList();
					}
				}
			}
		});
		menuItems.put("closeAllDataSources", closeAllDataSources);

		MenuItem editDataSource = new MenuItem(pmenu, SWT.PUSH);
		editDataSource.setText(message.getString("editDataSource"));
		openDataSource.setAccelerator(SWT.CTRL | SWT.SHIFT | 'E');
		editDataSource.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				int iTab = xliffEditorWindowsTabs.getSelectionIndex();
				if (iTab >= 0)
				{
					CTabItem dataSourceTemp = xliffEditorWindowsTabs.getItem(iTab);
					if (dataSourceTemp != null)
					{
						SashForm sash = ((XliffEditorForm) dataSourceTemp.getData("XliffEditorForm"))
								.getDataSourceHolders();
						CTabFolder ctab = (CTabFolder) sash.getData("dataSourceWindowsTabs");
						CTabItem ctabitem = ctab.getSelection();
						DataSourceListWithTools dataSourceFormComposite = (DataSourceListWithTools) ctabitem
								.getData("dataSourceFormComposite");
						dataSourceFormComposite.editDataSource();
					}
				}
			}
		});
		menuItems.put("editDataSource", editDataSource);

		new MenuItem(pmenu, SWT.SEPARATOR);

		MenuItem openTMSTranslate = new MenuItem(pmenu, SWT.PUSH);
		openTMSTranslate.setText(message.getString("openTMSTranslate"));
		openTMSTranslate.setAccelerator(SWT.ALT | 'O');
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
		openTMSRevConvert.setText(message.getString("openTMSRevConvert"));
		openTMSRevConvert.setAccelerator(SWT.ALT | 'R');
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
		createopenTMSDatabase.setText(message.getString("createopenTMSDatabase"));
		createopenTMSDatabase.setAccelerator(SWT.ALT | 'C');
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
		deleteopenTMSDatabase.setText(message.getString("deleteopenTMSDatabase"));
		deleteopenTMSDatabase.setAccelerator(SWT.ALT | 'D');
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
		manageMultipleDataSource.setText(message.getString("manageMultipleDataSource"));
		manageMultipleDataSource.setAccelerator(SWT.ALT | 'M');
		manageMultipleDataSource.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				Vector<String[]> tmxDatabases = de.folt.models.datamodel.DataSourceConfigurations
						.getOpenTMSDatabasesWithType();
				boolean bMultipleDataSources = false;
				for (int i = 0; i < tmxDatabases.size(); i++)
				{
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

		MenuItem importopenTMSDatabase = new MenuItem(pmenu, SWT.PUSH);
		importopenTMSDatabase.setText(message.getString("importopenTMSDatabase"));
		importopenTMSDatabase.setAccelerator(SWT.ALT | 'I');
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
		exportopenTMSDatabase.setText(message.getString("exportopenTMSDatabase"));
		exportopenTMSDatabase.setAccelerator(SWT.ALT | 'E');
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
		exportXliffInternalTerminology.setText(message.getString("exportXliffInternalTerminology"));
		exportXliffInternalTerminology.setAccelerator(SWT.ALT | 'T');
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

		MenuItem copyFromToOpenTMSDataSource = new MenuItem(pmenu, SWT.PUSH);
		copyFromToOpenTMSDataSource.setText(message.getString("CopyFromToOpenTMSDataSource"));
		copyFromToOpenTMSDataSource.setAccelerator(SWT.ALT | 'F');
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

		MenuItem openDataSourceEditor = new MenuItem(pmenu, SWT.PUSH);
		openDataSourceEditor.setText(message.getString("openDataSourceEditor"));
		copyFromToOpenTMSDataSource.setAccelerator(SWT.ALT | 'S');
		openDataSourceEditor.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				try
				{
					// display.asyncExec(new Runnable()
					// {
					// public void run()
					// {

					DataSourceEditor.getInstance(display);

					// }
					// });

				}
				catch (Exception e2)
				{
					e2.printStackTrace();
				}
			}
		});
		menuItems.put("openDataSourceEditor", openDataSourceEditor);

		if (1 == 2) // moved to Sync Service
		{
			final MenuItem startWebService = new MenuItem(pmenu, SWT.PUSH);
			startWebService.setText(message.getString("startWebService")); //$NON-NLS-1$
			startWebService.addSelectionListener(new SelectionAdapter()
			{
				public void widgetSelected(SelectionEvent e)
				{
					String url = de.folt.util.OpenTMSProperties.getInstance().getOpenTMSProperty(
							"OpenTMS.WebService.url");
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

					startWebService.setData("WebService", server);

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
						OpenTMSWebServiceInterface openTMSService = service.getOpenTMSPort();

						HashMap<String, String> paramHash = new HashMap<String, String>();
						String parameters = "";
						webServerUpdateCounter = "";
						try
						{
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

							message = message + "\nWeb Server Update Counter: " + webServerUpdateCounter
									+ "\nClient Update Counter: " + lClientUpdateCounter + "\nDifference: "
									+ lDiffClientWebServerUpdateCounter;
							message = message + "\nUpdate Counter: " + webServerUpdateCounter;
						}
						catch (Exception ex)
						{
							// TODO Auto-generated catch block
							ex.printStackTrace();
						}

						// return a list of available sync data sources
						parameters = "";
						paramHash = new HashMap<String, String>();
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

						JOptionPane.showMessageDialog(null, message, "openTMS WebServices Server started",
								JOptionPane.INFORMATION_MESSAGE);
						MenuItem stop = menuItems.get("stopWebService");
						stop.setEnabled(true);
						MenuItem start = menuItems.get("startWebService");
						start.setEnabled(false);
					}
					else
					{
						JOptionPane.showMessageDialog(null, "Error: openTMS WebServices Server could not be started\n",
								"openTMS WebServices Server not started", JOptionPane.ERROR_MESSAGE);
					}
				}
			});

			menuItems.put("startWebService", startWebService);

			final MenuItem stopWebService = new MenuItem(pmenu, SWT.PUSH);
			stopWebService.setText(message.getString("stopWebService")); //$NON-NLS-1$
			stopWebService.addSelectionListener(new SelectionAdapter()
			{
				public void widgetSelected(SelectionEvent e)
				{
					try
					{
						OpenTMSWebServiceServer server = (OpenTMSWebServiceServer) startWebService
								.getData("WebService");
						if (server != null)
						{
							server.shutdownServer();
							JOptionPane.showMessageDialog(null, "openTMS WebServices Server shutdown\n",
									"openTMS WebServices Server shutdown", JOptionPane.INFORMATION_MESSAGE);
							MenuItem stop = menuItems.get("stopWebService");
							stop.setEnabled(false);
							MenuItem start = menuItems.get("startWebService");
							start.setEnabled(true);
						}
						else
							JOptionPane.showMessageDialog(null, "Error: openTMS WebServices Server was not started",
									"openTMS WebServices Server not running", JOptionPane.ERROR_MESSAGE);
					}
					catch (Exception e2)
					{
						e2.printStackTrace();
						JOptionPane.showMessageDialog(null, "Error: openTMS WebServices Server could not be shutdown\n"
								+ e2.getMessage(), "openTMS WebServices Server Exception", JOptionPane.ERROR_MESSAGE);
					}
				}

			});
			stopWebService.setEnabled(false);
			menuItems.put("stopWebService", stopWebService);
		}
	}

	private void createOptionsMenu(Menu menubar)
	{

		MenuItem editMenu = new MenuItem(menubar, SWT.CASCADE);
		editMenu.setText(message.getString("Menu_Options")); //$NON-NLS-1$
		editMenu.setAccelerator(SWT.ALT | 'E');
		Menu emenu = new Menu(editMenu);
		editMenu.setMenu(emenu);

		MenuItem setFont = new MenuItem(emenu, SWT.PUSH);
		setFont.setText(message.getString("setFont")); //$NON-NLS-1$
		setFont.setAccelerator(SWT.CTRL | 'Z');
		setFont.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				selectFontDialog();
				// change all the fonts
				CTabItem[] tabs = xliffEditorWindowsTabs.getItems();
				for (int i = 0; i < tabs.length; i++)
				{
					CTabItem actItem = xliffEditorWindowsTabs.getItem(i);
					if (actItem == null)
						return;
					XliffEditorForm form = (XliffEditorForm) actItem.getData("XliffEditorForm");
					form.setFont(defaultFont);
				}
			}
		});

		menuItems.put("setFont", setFont);

		MenuItem bringGlobalDictionaryToTop = new MenuItem(emenu, SWT.PUSH);
		bringGlobalDictionaryToTop.setText(message.getString("bringGlobalDictionaryToTop")); //$NON-NLS-1$
		bringGlobalDictionaryToTop.setAccelerator(SWT.F4);
		bringGlobalDictionaryToTop.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				xliffEditorDictionaryViewer.show();
			}
		});
		menuItems.put("bringGlobalDictionaryToTop", bringGlobalDictionaryToTop);

		MenuItem bringSegmentDictionaryToTop = new MenuItem(emenu, SWT.PUSH);
		bringSegmentDictionaryToTop.setText(message.getString("bringSegmentDictionaryToTop")); //$NON-NLS-1$
		bringSegmentDictionaryToTop.setAccelerator(SWT.F5);
		bringSegmentDictionaryToTop.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				xliffEditorSegmentDictionaryViewer.show();
			}
		});
		menuItems.put("bringSegmentDictionaryToTop", bringSegmentDictionaryToTop);

		MenuItem setPreferences = new MenuItem(emenu, SWT.PUSH);
		setPreferences.setText(message.getString("setPreferencest")); //$NON-NLS-1$
		setPreferences.setAccelerator(SWT.CTRL | SWT.SHIFT | 'P');
		setPreferences.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				CTabItem tab = xliffEditorWindowsTabs.getSelection();
				XliffEditorForm form = null;
				if (tab != null)
				{
					form = (XliffEditorForm) tab.getData("XliffEditorForm");
				}
				new PreferencesDialog(shell, preferencesContainer, editorConfiguration, form).show();
			}
		});

		menuItems.put("setPreferences", setPreferences);
	}

	/**
	 * createShell
	 */
	private void createShell()
	{
		message = de.folt.util.Messages.getInstance("net.docliff.models.applicationmodel.guimodel.editor.XliffEditor",
				userLanguage);

		display = new Display();
		shell = new Shell(display, SWT.SHELL_TRIM);
		String version = this.getXliffEditorVersion();
		shell.setText(message.getString("Docliff_Editor") + " " + version);

		Image logo = new Image(display, "images/docliff.png");
		shell.setImage(logo);

		editorWidth = 1000;
		editorHeight = 800;
		editorxLocation = 100;
		editoryLocation = 100;
		try
		{
			editorWidth = Integer.parseInt(editorConfiguration.loadValueForKey("editorWidth"));
			editorHeight = Integer.parseInt(editorConfiguration.loadValueForKey("editorHeight"));

			editorxLocation = Integer.parseInt(editorConfiguration.loadValueForKey("editorxLocation"));
			editoryLocation = Integer.parseInt(editorConfiguration.loadValueForKey("editoryLocation"));
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		shell.setSize(editorWidth, editorHeight);
		shell.setLocation(editorxLocation, editoryLocation);

		GridLayout shellLayout = new GridLayout(1, true);
		shellLayout.horizontalSpacing = 0;
		shellLayout.verticalSpacing = 1;
		shellLayout.marginWidth = 0;
		shell.setLayout(shellLayout);

		shell.addListener(SWT.Close, new Listener()
		{
			public void handleEvent(Event event)
			{
				for (int i = 0; i < xliffEditorWindowsTabs.getItemCount(); i++)
				{
					CTabItem dataSourceTemp = xliffEditorWindowsTabs.getItem(i);
					try
					{
						if (dataSourceTemp.getData("XliffEditorForm") == null)
							continue;

						if (((XliffEditorForm) dataSourceTemp.getData("XliffEditorForm")).getXliffEditorWindow()
								.isBChanged())
						{
							// ask if to be saved when changed
							int style = SWT.PRIMARY_MODAL | SWT.ICON_QUESTION | SWT.YES | SWT.NO | SWT.CANCEL;
							MessageBox messageBox = new MessageBox(shell, style);
							messageBox.setMessage(message.getString("Save_Changes") + "\n" + dataSourceTemp.getText());
							int result = messageBox.open();
							if (result == SWT.CANCEL)
							{
								event.doit = false;
								return;
							}
							else if (result == SWT.YES)
							{
								((XliffEditorForm) dataSourceTemp.getData("XliffEditorForm")).saveXliffDocument();
							}
							((XliffEditorForm) dataSourceTemp.getData("XliffEditorForm")).closeAllDataSources();

						}
					}
					catch (Exception ex)
					{
						ex.printStackTrace();
					}
				}

				editorConfiguration.saveKeyValuePair("editorWidth", shell.getSize().x + "");
				editorConfiguration.saveKeyValuePair("editorHeight", shell.getSize().y + "");
				editorConfiguration.saveKeyValuePair("editorxLocation", shell.getLocation().x + "");
				editorConfiguration.saveKeyValuePair("editoryLocation", shell.getLocation().y + "");
				xliffEditorDictionaryViewer.savePosition();
				xliffEditorSegmentDictionaryViewer.savePosition();
				display.dispose();
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

		MenuItem chooseLocalSyncDataSource = new MenuItem(pmenu, SWT.PUSH);
		chooseLocalSyncDataSource.setText(message.getString("Choose_LocalSyncDataSource")); //$NON-NLS-1$
		chooseLocalSyncDataSource.setAccelerator(SWT.ALT | SWT.SHIFT | 'L');
		chooseLocalSyncDataSource.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				chooseLocalDataSourceAction();
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
				downloadAction();
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

	}

	protected void chooseLocalDataSourceAction()
	{
		// Vector<String[]> synDataSources =
		// DataSourceInstance.getDataSourcesWithType();
		String[] synDataSources = DataSourceInstance
				.getDataSourcesWithType(de.folt.models.datamodel.sql.OpenTMSSQLDataSource.class.getName());
		try
		{
			Object[] possibleValues = new Object[synDataSources.length];
			for (int i = 0; i < synDataSources.length; i++)
			{
				possibleValues[i] = synDataSources[i];
			}

			String message = "\nLocal Sync Data Sources available:\n" + synDataSources;
			System.out.println(message);

			syncLocalDataSourceName = (String) JOptionPane.showInputDialog(null, "Choose a Local Sync Data Source",
					"Local Sync Data Source", JOptionPane.INFORMATION_MESSAGE, null, possibleValues, possibleValues[0]);
			this.syncLocalDataSource = DataSourceInstance.createInstance(syncLocalDataSourceName);
			if (this.syncLocalDataSource == null)
			{
				JOptionPane.showMessageDialog(null, "Error: SyncLocalDataSource " + syncLocalDataSourceName
						+ " could not be loaded!", "Sync Local DataSource Error", JOptionPane.ERROR_MESSAGE);
				syncLocalDataSourceName = null;
			}
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
			syncLocalDataSourceName = null;
			syncLocalDataSource = null;
		}
	}

	private void createTasksMenu(Menu menubar)
	{
		MenuItem tasksMenu = new MenuItem(menubar, SWT.CASCADE);
		tasksMenu.setText(message.getString("Menu_Tasks")); //$NON-NLS-1$
		tasksMenu.setAccelerator(SWT.ALT | 'T');
		Menu tmenu = new Menu(tasksMenu);
		tasksMenu.setMenu(tmenu);

		MenuItem editApprove = new MenuItem(tmenu, SWT.PUSH);
		editApprove.setText(message.getString("Approve_Segment_tCtrl_+_E_59"));
		editApprove.setAccelerator(SWT.CONTROL | 'E');
		editApprove.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent el)
			{
				int iTab = xliffEditorWindowsTabs.getSelectionIndex();
				if (iTab >= 0)
				{
					CTabItem tab = xliffEditorWindowsTabs.getItem(iTab);
					if (tab.getData("XliffEditorForm") != null)
					{
						XliffEditorWindow win = ((XliffEditorForm) tab.getData("XliffEditorForm"))
								.getXliffEditorWindow();
						DataSource dataSource = ((XliffEditorForm) tab.getData("XliffEditorForm"))
								.getTmDataSourceFormComposite().getSelectedDataSource();
						boolean bApprove = win.approveSegment(win.getIOldSegmentPosition(), true, dataSource);
						if ((dataSource != null) && bApprove)
						{
							Cursor hglass = new Cursor(shell.getDisplay(), SWT.CURSOR_WAIT);
							Cursor arrow = new Cursor(shell.getDisplay(), SWT.CURSOR_ARROW);
							Hashtable<String, Object> transParam = new Hashtable<String, Object>();
							transParam.put("ignoreApproveAttribute", "yes");
							shell.setCursor(hglass);
							((XliffEditorForm) tab.getData("XliffEditorForm")).translateSegment(
									win.getIOldSegmentPosition(), transParam);
							((XliffEditorForm) tab.getData("XliffEditorForm")).showAltTrans(
									win.getIOldSegmentPosition(), 0, 0);
							shell.setCursor(arrow);

						}
					}
				}
			}
		});

		MenuItem editDisApprove = new MenuItem(tmenu, SWT.PUSH);
		editDisApprove.setText(message.getString("Disapprove_Segment_tCtrl_+_E_59"));
		editDisApprove.setAccelerator(SWT.CONTROL | 'F');
		editDisApprove.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent el)
			{
				int iTab = xliffEditorWindowsTabs.getSelectionIndex();
				if (iTab >= 0)
				{
					CTabItem tab = xliffEditorWindowsTabs.getItem(iTab);
					if (tab.getData("XliffEditorForm") != null)
					{
						XliffEditorWindow win = ((XliffEditorForm) tab.getData("XliffEditorForm"))
								.getXliffEditorWindow();
						win.approveSegment(win.getIOldSegmentPosition(), false, null);
					}
				}
			}
		});

		new MenuItem(tmenu, SWT.SEPARATOR);

		MenuItem editApproveAll = new MenuItem(tmenu, SWT.PUSH);
		editApproveAll.setText(message.getString("Approve_All"));
		editApproveAll.setAccelerator(SWT.CONTROL | 'G');
		editApproveAll.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent el)
			{
				int iTab = xliffEditorWindowsTabs.getSelectionIndex();
				if (iTab >= 0)
				{
					CTabItem tab = xliffEditorWindowsTabs.getItem(iTab);
					if (tab.getData("XliffEditorForm") != null)
					{
						XliffEditorWindow win = ((XliffEditorForm) tab.getData("XliffEditorForm"))
								.getXliffEditorWindow();
						DataSource datasource = ((XliffEditorForm) tab.getData("XliffEditorForm"))
								.getTmDataSourceFormComposite().getSelectedDataSource();
						win.approveAllSegments(true, datasource);
					}
				}
			}
		});

		MenuItem editDisApproveAll = new MenuItem(tmenu, SWT.PUSH);
		editDisApproveAll.setText(message.getString("Disapprove_All"));
		editDisApproveAll.setAccelerator(SWT.CONTROL | 'H');
		editDisApproveAll.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent el)
			{
				int iTab = xliffEditorWindowsTabs.getSelectionIndex();
				if (iTab >= 0)
				{
					CTabItem tab = xliffEditorWindowsTabs.getItem(iTab);
					if (tab.getData("XliffEditorForm") != null)
					{
						XliffEditorWindow win = ((XliffEditorForm) tab.getData("XliffEditorForm"))
								.getXliffEditorWindow();
						win.approveAllSegments(false, null);
					}
				}
			}
		});

		new MenuItem(tmenu, SWT.SEPARATOR);

		MenuItem editAcceptTranslation = new MenuItem(tmenu, SWT.PUSH);
		editAcceptTranslation.setText(message.getString("AcceptMatchTranslation"));
		editAcceptTranslation.setAccelerator(SWT.CONTROL | 'A');
		editAcceptTranslation.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent el)
			{
				int iTab = xliffEditorWindowsTabs.getSelectionIndex();
				if (iTab >= 0)
				{
					CTabItem tab = xliffEditorWindowsTabs.getItem(iTab);
					if (tab.getData("XliffEditorForm") != null)
					{
						XliffEditorForm form = (XliffEditorForm) tab.getData("XliffEditorForm");
						form.acceptTranslation();
					}
				}
			}
		});

		MenuItem editAcceptAll100Translation = new MenuItem(tmenu, SWT.PUSH);
		editAcceptAll100Translation.setText(message.getString("AcceptAll100Translation"));
		editAcceptAll100Translation.setAccelerator(SWT.CONTROL | 'A');
		editAcceptAll100Translation.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent el)
			{
				int iTab = xliffEditorWindowsTabs.getSelectionIndex();
				if (iTab >= 0)
				{
					CTabItem tab = xliffEditorWindowsTabs.getItem(iTab);
					if (tab.getData("XliffEditorForm") != null)
					{
						XliffEditorForm form = (XliffEditorForm) tab.getData("XliffEditorForm");
						form.acceptAll100Translation(false);
					}
				}
			}
		});

		MenuItem editAcceptAll100NewTranslation = new MenuItem(tmenu, SWT.PUSH);
		editAcceptAll100NewTranslation.setText(message.getString("AcceptAll100NewTranslation"));
		editAcceptAll100NewTranslation.setAccelerator(SWT.CONTROL | 'L');
		editAcceptAll100NewTranslation.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent el)
			{
				int iTab = xliffEditorWindowsTabs.getSelectionIndex();
				if (iTab >= 0)
				{
					CTabItem tab = xliffEditorWindowsTabs.getItem(iTab);
					if (tab.getData("XliffEditorForm") != null)
					{
						XliffEditorForm form = (XliffEditorForm) tab.getData("XliffEditorForm");
						form.acceptAll100Translation(true);
					}
				}
			}
		});

		MenuItem editAcceptAllNewTranslations = new MenuItem(tmenu, SWT.PUSH);
		editAcceptAllNewTranslations.setText(message.getString("AcceptAllTranslations"));
		editAcceptAllNewTranslations.setAccelerator(SWT.CONTROL | 'I');
		editAcceptAllNewTranslations.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent el)
			{
				int iTab = xliffEditorWindowsTabs.getSelectionIndex();
				if (iTab >= 0)
				{
					CTabItem tab = xliffEditorWindowsTabs.getItem(iTab);
					if (tab.getData("XliffEditorForm") != null)
					{
						XliffEditorForm form = (XliffEditorForm) tab.getData("XliffEditorForm");
						form.acceptAllTranslations(true);
					}
				}
			}
		});

		new MenuItem(tmenu, SWT.SEPARATOR);

		MenuItem editRemoveTranslation = new MenuItem(tmenu, SWT.PUSH);
		editRemoveTranslation.setText(message.getString("RemoveTranslation"));
		editRemoveTranslation.setAccelerator(SWT.CONTROL | 'P');
		editRemoveTranslation.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent el)
			{
				int iTab = xliffEditorWindowsTabs.getSelectionIndex();
				if (iTab >= 0)
				{
					CTabItem tab = xliffEditorWindowsTabs.getItem(iTab);
					if (tab.getData("XliffEditorForm") != null)
					{
						XliffEditorForm form = (XliffEditorForm) tab.getData("XliffEditorForm");
						form.removeTranslation(form.getIOldSegmentPosition());
					}
				}
			}
		});

		MenuItem editRemoveAllTranslations = new MenuItem(tmenu, SWT.PUSH);
		editRemoveAllTranslations.setText(message.getString("RemoveAllTranslations"));
		editRemoveAllTranslations.setAccelerator(SWT.CONTROL | 'R');
		editRemoveAllTranslations.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent el)
			{
				int iTab = xliffEditorWindowsTabs.getSelectionIndex();
				if (iTab >= 0)
				{
					CTabItem tab = xliffEditorWindowsTabs.getItem(iTab);
					if (tab.getData("XliffEditorForm") != null)
					{
						XliffEditorForm form = (XliffEditorForm) tab.getData("XliffEditorForm");
						form.removeAllTranslations();
					}
				}
			}
		});

		new MenuItem(tmenu, SWT.SEPARATOR);

		MenuItem translateSegment = new MenuItem(tmenu, SWT.PUSH);
		translateSegment.setText(message.getString("translateSegment"));
		translateSegment.setAccelerator(SWT.CONTROL | 'T');
		translateSegment.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent el)
			{
				int iTab = xliffEditorWindowsTabs.getSelectionIndex();
				if (iTab >= 0)
				{
					CTabItem tab = xliffEditorWindowsTabs.getItem(iTab);
					if (tab.getData("XliffEditorForm") != null)
					{
						XliffEditorForm form = (XliffEditorForm) tab.getData("XliffEditorForm");
						Cursor hglass = new Cursor(display, SWT.CURSOR_WAIT);
						Cursor arrow = new Cursor(display, SWT.CURSOR_ARROW);
						shell.setCursor(hglass);
						form.translateSegment(form.getIOldSegmentPosition());
						form.showAltTrans(form.getIOldSegmentPosition(), 0, 0);
						shell.setCursor(arrow);
					}
				}
			}
		});

		MenuItem translateAllSegments = new MenuItem(tmenu, SWT.PUSH);
		translateAllSegments.setText(message.getString("translateAllSegments"));
		translateAllSegments.setAccelerator(SWT.CONTROL | SWT.SHIFT | 'T');
		translateAllSegments.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent el)
			{
				int iTab = xliffEditorWindowsTabs.getSelectionIndex();
				if (iTab >= 0)
				{
					CTabItem tab = xliffEditorWindowsTabs.getItem(iTab);
					if (tab.getData("XliffEditorForm") != null)
					{
						XliffEditorForm form = (XliffEditorForm) tab.getData("XliffEditorForm");
						form.translateAllSegments();
					}
				}
			}
		});
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
				openXliffFile();
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
				CTabItem actItem = xliffEditorWindowsTabs.getSelection();
				if (actItem == null)
					return;
				XliffEditorForm form = (XliffEditorForm) actItem.getData("XliffEditorForm");
				if (form != null)
				{
					if (form.getXliffEditorWindow().isFocusControl())
						form.getXliffEditorWindow().cut();
				}
			}
		});

		ToolItem eCopy = new ToolItem(toolBar, SWT.PUSH | SWT.FLAT);
		eCopy.setImage(new Image(display, "images/editcopy.gif")); //$NON-NLS-1$
		eCopy.setToolTipText(message.getString("Copy")); //$NON-NLS-1$
		eCopy.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				CTabItem actItem = xliffEditorWindowsTabs.getSelection();
				if (actItem == null)
					return;
				XliffEditorForm form = (XliffEditorForm) actItem.getData("XliffEditorForm");
				if (form != null)
				{
					if (form.getXliffEditorWindow().isFocusControl())
						form.getXliffEditorWindow().copy();
				}
			}
		});

		ToolItem ePaste = new ToolItem(toolBar, SWT.PUSH | SWT.FLAT);
		ePaste.setImage(new Image(display, "images/editpaste.gif")); //$NON-NLS-1$
		ePaste.setToolTipText(message.getString("Paste")); //$NON-NLS-1$
		ePaste.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				CTabItem actItem = xliffEditorWindowsTabs.getSelection();
				if (actItem == null)
					return;
				XliffEditorForm form = (XliffEditorForm) actItem.getData("XliffEditorForm");
				if (form != null)
				{
					if (form.getXliffEditorWindow().isFocusControl())
						form.getXliffEditorWindow().paste();
				}
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
		String homepage = OpenTMSProperties.getInstance().getOpenTMSProperty("DocliffXliffEditorHelp");
		if ((homepage == null) || homepage.equals(""))
		{
			homepage = "http://www.opentms.de";
		}
		Program.launch(homepage);
	}

	/**
	 * displayOpenTMSHelp
	 */
	protected void displayOpenTMSHelp(String linkReference)
	{
		String homepage = OpenTMSProperties.getInstance().getOpenTMSProperty("DocliffXliffEditorHelp");
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
	private void downloadAction()
	{

		if (syncLocalDataSource == null)
		{
			JOptionPane.showMessageDialog(null, "Sync Local Data Source is null", "openTMS Download Result Error",
					JOptionPane.ERROR_MESSAGE);
			return;
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
			OpenTMSWebServiceConstants consts = new OpenTMSWebServiceConstants();
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
				JOptionPane.showMessageDialog(null, "Error: Web Service not running!", "openTMS Update Counter Values",
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
			// long lCC = getlClientUpdateCounter();
			// long lDV = getlDiffClientWebServerUpdateCounter();
			updateCounter = getlWebServerUpdateCounter() + 1l + "";
			if (isbFirstSyncDone() == false)
				updateCounter = -1l + "";
			paramHash.put("method", "download");
			paramHash.put("user-id", System.getProperty("user.name"));
			paramHash.put("password", "");
			if (getSyncServerDataSource() == null)
			{
				progressDialog.close();
				shell.setCursor(arrow);
				JOptionPane.showMessageDialog(null, "Sync Data Source is null/not defined",
						"openTMS Download Result Error", JOptionPane.ERROR_MESSAGE);
				return;
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
					JOptionPane.showMessageDialog(null, "TMX Document is null", "openTMS Download Result Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				Hashtable<String, Object> param = new Hashtable<String, Object>();
				param.put("dataSourceName", syncLocalDataSource.getDataSourceName());
				param.put("sourceDocument", tmx_document);
				param.put("inputDocumentType", "String");
				param.put("dataSourceConfigurationsFile", "");
				// param.put("update-counter", update_counter);
				param.put("inputDocumentType", "String");
				param.put("sync", "download-import");
				// param.put("sync", "upload");
				String encoding = "UTF-8";
				param.put("encoding", encoding);

				Vector<String> resultimport = de.folt.rpc.connect.Interface.runImportOpenTMSDataSource(param);

				// now we need to update the fuzzy index...
				this.syncLocalDataSource.changedMonolingualObjects();

				pdSupport.updateProgressIndication(3, 4);

				String message = resultimport.toString();

				webServerUpdateCounter = paramHash.get("update-counter");
				try
				{
					setlWebServerUpdateCounter(Long.parseLong(webServerUpdateCounter));
					String dataSourceConfigurationsFile = syncLocalDataSource
							.getDefaultDataSourceConfigurationsFileName();
					DataSourceConfigurations config = new DataSourceConfigurations(dataSourceConfigurationsFile);
					DataSourceProperties dataModelProperties = new DataSourceProperties();
					dataModelProperties.remove("WebServerUpdateCounter");
					dataModelProperties.put("WebServerUpdateCounter", getlWebServerUpdateCounter() + ":"
							+ getSyncServerDataSource());
					config.addPropertiesToDataModelProperties(syncLocalDataSource.getDataSourceName(),
							dataModelProperties);
					config.saveToXmlFile();
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}

				message = message + "\nNew Web Server Update Counter: " + webServerUpdateCounter;
				pdSupport.updateProgressIndication(4, 4);
				// form.fillIdList();
				progressDialog.close();
				shell.setCursor(arrow);
				JOptionPane
						.showMessageDialog(null, message, "openTMS Download Result", JOptionPane.INFORMATION_MESSAGE);

				setbFirstSyncDone(true);
			}
			else
			{
				progressDialog.close();
				shell.setCursor(arrow);
			}
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
			progressDialog.close();
			shell.setCursor(arrow);
			JOptionPane.showMessageDialog(null, e1.getMessage(), "openTMS Download Result Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void getDefaultFont()
	{
		try
		{
			String fontName = editorConfiguration.loadValueForKey("defaultFontName");
			if (fontName == null)
			{
				defaultFont = shell.getFont();
				return;
			}
			String fontHeight = editorConfiguration.loadValueForKey("defaultFontHeight");
			if (fontHeight == null)
			{
				defaultFont = shell.getFont();
				return;
			}
			String fontStyle = editorConfiguration.loadValueForKey("defaultFontStyle");
			if (fontStyle == null)
			{
				defaultFont = shell.getFont();
				return;
			}

			int iFontHeight = 10;
			try
			{
				iFontHeight = Integer.parseInt(fontHeight);
			}
			catch (Exception e)
			{
			}
			int iFontStyle = 0;
			try
			{
				iFontStyle = Integer.parseInt(fontStyle);
			}
			catch (Exception e)
			{
			}
			defaultFont = new Font(display, new FontData(fontName, iFontHeight, iFontStyle));
		}
		catch (NumberFormatException e)
		{
			defaultFont = shell.getFont();
			e.printStackTrace();
		}
		catch (Exception e)
		{
			defaultFont = shell.getFont();
			e.printStackTrace();
		}
	}

	public long getlClientUpdateCounter()
	{
		return lClientUpdateCounter;
	}

	public Object getlDiffClientWebServerUpdateCounter()
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
	 * @return the optionsContainer
	 */
	public PreferencesContainer getPreferencesContainerContainer()
	{
		return preferencesContainer;
	}

	public DataSource getSyncDataSource()
	{
		return syncLocalDataSource;
	}

	public String getSyncServerDataSource()
	{
		return syncServerDataSource;
	}

	public String getUpdateCounter()
	{
		return webServerUpdateCounter;
	}

	/**
	 * @return the xliffEditorVersion
	 */
	public String getXliffEditorVersion()
	{
		return xliffEditorVersion;
	}

	/**
	 * adaptShell
	 */
	private void initShell()
	{
		int iGridData = GridData.GRAB_VERTICAL | GridData.VERTICAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL
				| GridData.FILL_HORIZONTAL;

		Composite toolsHolder = new Composite(shell, SWT.NONE);
		GridLayout toolsLayout = new GridLayout(1, false);
		toolsLayout.marginWidth = 1;
		toolsLayout.marginHeight = 1;
		toolsLayout.horizontalSpacing = 0;
		toolsHolder.setLayout(toolsLayout);
		toolsHolder.setLayoutData(new GridData(iGridData));

		ToolBar toolBar = new ToolBar(toolsHolder, SWT.NONE);
		createToolBar(toolBar);
		// final Menu
		menubar = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menubar);
		createFileMenu(menubar);
		createEditMenu(menubar);
		createGotoMenu(menubar);
		createOptionsMenu(menubar);
		createTasksMenu(menubar);
		createSyncMenu(menubar);
		createOpenTMSMenu(menubar);
		createHelpMenu(menubar);

		@SuppressWarnings("unused")
		GridData screenGridData = new GridData(iGridData);

		xliffEditorWindowsTabs = new CTabFolder(toolsHolder, SWT.BORDER);
		xliffEditorWindowsTabs.setLayoutData(new GridData(GridData.FILL_BOTH)); // (screenGridData);
		xliffEditorWindowsTabs.setLayout(new GridLayout(1, true));
		xliffEditorWindowsTabs.addCTabFolder2Listener(new CTabFolder2Adapter()
		{
			public void close(CTabFolderEvent event)
			{
				CTabItem dataSourceTemp = (CTabItem) event.item;
				try
				{
					if (dataSourceTemp.getData("XliffEditorForm") != null)
					{
						if (((XliffEditorForm) dataSourceTemp.getData("XliffEditorForm")).getXliffEditorWindow()
								.isBChanged())
						{
							// ask if to be saved when changed
							int style = SWT.PRIMARY_MODAL | SWT.ICON_QUESTION | SWT.YES | SWT.NO | SWT.CANCEL;
							MessageBox messageBox = new MessageBox(shell, style);
							messageBox.setMessage(message.getString("Save_Changes")); //$NON-NLS-1$
							int result = messageBox.open();
							if (result == SWT.CANCEL)
							{
								event.doit = false;
								return;
							}
							else
							{
								if (result == SWT.YES)
								{
									((XliffEditorForm) dataSourceTemp.getData("XliffEditorForm")).saveXliffDocument();
								}
							}
						}
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
		xliffEditorWindowsTabs.addListener(SWT.Selection, new Listener()
		{
			public void handleEvent(Event event)
			{
				CTabItem dataSourceTemp = (CTabItem) event.item;
				try
				{
					XliffEditorForm form = (XliffEditorForm) dataSourceTemp.getData("XliffEditorForm");
					if (form != null)
					{
						xliffEditorDictionaryViewer.setTerms((form).getXliffEditorWindow().getGlossary());
						xliffEditorSegmentDictionaryViewer.setTerms((form).getPhrases(form.getIOldSegmentPosition()));
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});

		getDefaultFont();

		AddDropSupport(xliffEditorWindowsTabs);

		boolean bGlobalDictionaryOnTop = editorConfiguration.loadBooleanValueForKey("globalDictionaryOnTop");
		boolean bSegmentDictionaryOnTop = editorConfiguration.loadBooleanValueForKey("segmentDictionaryOnTop");

		int style = SWT.RESIZE;
		if (bSegmentDictionaryOnTop)
			style = style | SWT.ON_TOP;
		xliffEditorSegmentDictionaryViewer = new XliffEditorDictionaryViewer(shell,
				message.getString("XliffEditorDictionary"), "", "", style);
		xliffEditorSegmentDictionaryViewer.show();

		style = SWT.RESIZE;
		if (bGlobalDictionaryOnTop)
			style = style | SWT.ON_TOP;
		xliffEditorDictionaryViewer = new XliffEditorDictionaryViewer(shell,
				message.getString("XliffEditorGlobalDictionary"), "", "", style);
		xliffEditorDictionaryViewer.show();

		xliffEditorDictionaryViewer.addObserver(new DictionarySelectionObserver(this));
		xliffEditorSegmentDictionaryViewer.addObserver(new DictionarySelectionObserver(this));
	}

	public boolean isbFirstSyncDone()
	{
		return bFirstSyncDone;
	}

	/**
	 * openDataSource
	 */
	protected void openXliffFile()
	{
		try
		{
			FileDialog fd = new FileDialog(shell, SWT.OPEN);
			String extensions[] = { "*.xlf;*.xliff", "*.xml", "*.*" };
			fd.setFilterExtensions(extensions);
			if (System.getProperty("file.separator").equals("/"))
			{
				fd.setFilterPath(System.getProperty("user.home"));
			}
			String xlifffile = fd.open();
			if (xlifffile == null)
			{
				fd = null;
				extensions = null;
				return;
			}

			if (xlifffile != null)
			{
				openXliffFile(xlifffile);
			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();

		}
	}

	/**
	 * openDataSource
	 * 
	 * @param xliffFile
	 */
	protected void openXliffFile(String xliffFile)
	{
		Cursor hglass = new Cursor(shell.getDisplay(), SWT.CURSOR_WAIT);
		Cursor arrow = new Cursor(shell.getDisplay(), SWT.CURSOR_ARROW);
		shell.setCursor(hglass);
		try
		{
			if (xliffFile != null)
			{
				CTabItem[] tabs = xliffEditorWindowsTabs.getItems();
				for (int i = 0; i < tabs.length; i++)
				{
					if (tabs[i].getText().equals(xliffFile))
						return;
				}

				CTabItem xliffFileTabItem = new CTabItem(xliffEditorWindowsTabs, SWT.BORDER | SWT.CLOSE);
				xliffFileTabItem.setData("xliffFile", xliffFile);
				xliffFileTabItem.addListener(SWT.Close, new Listener()
				{
					public void handleEvent(Event event)
					{
						CTabItem dataSourceTemp = (CTabItem) event.item;
						try
						{
							if (dataSourceTemp.getData("XliffEditorForm") != null)
							{
								if (((XliffEditorForm) dataSourceTemp.getData("XliffEditorForm"))
										.getXliffEditorWindow().isBChanged())
								{
									// ask if to be saved when changed
									int style = SWT.PRIMARY_MODAL | SWT.ICON_QUESTION | SWT.YES | SWT.NO | SWT.CANCEL;
									MessageBox messageBox = new MessageBox(shell, style);
									messageBox.setMessage(message.getString("Save_Changes")); //$NON-NLS-1$
									int result = messageBox.open();
									if (result == SWT.CANCEL)
									{
										event.doit = false;
										return;
									}
									else
									{
										if (result == SWT.YES)
										{
											((XliffEditorForm) dataSourceTemp.getData("XliffEditorForm"))
													.saveXliffDocument();
										}
									}
								}

								((XliffEditorForm) dataSourceTemp.getData("XliffEditorForm")).closeAllDataSources();

							}
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
				});
				xliffFileTabItem.setText(xliffFile);

				XliffEditorForm form = new XliffEditorForm(shell, xliffEditorWindowsTabs, SWT.NONE, xliffFile,
						configfile);
				if (!form.isBXliffEditorFormCreated())
				{
					shell.setCursor(arrow);
					int style = SWT.PRIMARY_MODAL | SWT.ICON_ERROR | SWT.CANCEL;
					MessageBox messageBox = new MessageBox(shell, style);
					messageBox.setMessage(message.getString("ErrorCreatingXliffEditorForm") + "\n" + xliffFile);
					@SuppressWarnings("unused")
					int result = messageBox.open();
					form = null;
					xliffFileTabItem.dispose();
					return;
				}
				form.setXliffEditorDictionaryViewer(xliffEditorDictionaryViewer);
				form.setXliffEditorSegmentDictionaryViewer(xliffEditorSegmentDictionaryViewer);
				form.getXliffEditorDictionaryViewer().setTerms(form.getXliffEditorWindow().getGlossary());
				xliffFileTabItem.setControl(form);
				xliffEditorWindowsTabs.setSelection(xliffFileTabItem);
				xliffFileTabItem.setData("XliffEditorForm", form);
				xliffFileTabItem.setData("xliffFile", form.getXliffFile());
				form.setFont(defaultFont);
				form.setPreferencesContainer(preferencesContainer);
				form.loadDefaultDataSources(this.syncLocalDataSourceName);
				// add to recent files
				String recentFiles = editorConfiguration.loadValueForKey("recentXliffFiles");
				File f = new File(xliffFile);
				if (f.exists())
				{
					if ((recentFiles == null) || recentFiles.equals(""))
					{
						editorConfiguration.saveKeyValuePair("recentXliffFiles", f.getAbsolutePath());
					}
					else if (recentFiles.indexOf(f.getAbsolutePath()) == -1)
					{
						editorConfiguration.saveKeyValuePair("recentXliffFiles",
								recentFiles + ";" + f.getAbsolutePath());
					}
				}
			}
			shell.setCursor(arrow);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			shell.setCursor(arrow);
		}
	}

	protected void openXliffFile(String[] xliffFiles)
	{
		if (xliffFiles == null)
			return;
		for (int i = 0; i < xliffFiles.length; i++)
		{
			openXliffFile(xliffFiles[i]);
		}
	}

	/**
	 * runShell
	 */
	private void runShell()
	{
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

		ColorTable.removeInstance();
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
	 * Method selectFontDialog.
	 */
	private void selectFontDialog()
	{
		FontDialog fd = new FontDialog(shell);
		if (defaultFont == null)
		{
			defaultFont = shell.getFont();
		}
		FontData[] currentFont = defaultFont.getFontData();
		fd.setFontList(currentFont);
		FontData fontData = fd.open();
		if (fontData == null)
		{
			fd = null;
			return;
		}

		try
		{
			Font aFont = new Font(display, fontData);
			defaultFont = aFont;
			preferencesContainer.setDefaultFont(defaultFont);
			int height = fontData.getHeight();
			int style = fontData.getStyle();
			String name = fontData.getName();

			editorConfiguration.saveKeyValuePair("defaultFontName", name);
			editorConfiguration.saveKeyValuePair("defaultFontHeight", height + "");
			editorConfiguration.saveKeyValuePair("defaultFontStyle", style + "");

		}
		catch (Exception e)
		{
			MessageBox box = new MessageBox(shell, SWT.PRIMARY_MODAL | SWT.ICON_ERROR | SWT.OK);
			box.setMessage(e.getLocalizedMessage());
			box.setMessage(e.toString());
			box.open();
		}
	}

	public void setbFirstSyncDone(boolean bFirstSyncDone)
	{
		this.bFirstSyncDone = bFirstSyncDone;
	}

	public void setlClientUpdateCounter(long lClientUpdateCounter)
	{
		this.lClientUpdateCounter = lClientUpdateCounter;
	}

	public void setlDiffClientWebServerUpdateCounter(Object lDiffClientWebServerUpdateCounter)
	{
		this.lDiffClientWebServerUpdateCounter = lDiffClientWebServerUpdateCounter;
	}

	public void setlWebServerUpdateCounter(long lWebServerUpdateCounter)
	{
		this.lWebServerUpdateCounter = lWebServerUpdateCounter;
	}

	/**
	 * @param preferencesContainer
	 *            the preferencesContainer to set
	 */
	public void setPreferencesContainer(PreferencesContainer preferencesContainer)
	{
		this.preferencesContainer = preferencesContainer;
	}

	public void setSyncDataSource(DataSource syncDataSource)
	{
		this.syncLocalDataSource = syncDataSource;
	}

	public void setSyncServerDataSource(String syncServerDataSource)
	{
		this.syncServerDataSource = syncServerDataSource;
	}

	public void setUpdateCounter(String updateCounter)
	{
		this.webServerUpdateCounter = updateCounter;
	}

	/**
	 * @param xliffEditorVersion
	 *            the xliffEditorVersion to set
	 */
	public void setXliffEditorVersion(String xliffEditorVersion)
	{
		this.xliffEditorVersion = xliffEditorVersion;
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

		if (serverimp != null)
		{
			System.out.println("openTMS WebService Server started");
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
			OpenTMSWebServiceInterface openTMSService = service.getOpenTMSPort();
			HashMap<String, String> paramHash = new HashMap<String, String>();
			String parameters = "";
			webServerUpdateCounter = "";
			try
			{
				paramHash.put("method", "getUpdateCounter");
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

			JOptionPane.showMessageDialog(null, message, "openTMS WebServices Server started",
					JOptionPane.INFORMATION_MESSAGE);
			MenuItem stop = menuItems.get("stopWebService");
			stop.setEnabled(true);
			MenuItem start = menuItems.get("startWebService");
			start.setEnabled(false);

			MenuItem chooseSyncDataSource = menuItems.get("chooseSyncDataSource");
			chooseSyncDataSource.setEnabled(true);

			MenuItem sync = menuItems.get("sync");
			sync.setEnabled(true);

			MenuItem upload = menuItems.get("upload");
			upload.setEnabled(true);

			MenuItem download = menuItems.get("download");
			download.setEnabled(true);

			MenuItem updatecounter = menuItems.get("updatecounter");
			updatecounter.setEnabled(true);
			
			MenuItem connectWebService = menuItems.get("connectWebService");
			connectWebService.setEnabled(false);

		}
		else
		{
			JOptionPane.showMessageDialog(null, "Error: openTMS WebServices Server could not be started\n",
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
				JOptionPane.showMessageDialog(null, "openTMS WebServices Server shutdown\n",
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
			}
			else
			{
				MenuItem connectWebService = menuItems.get("connectWebService");
				OpenTMSWebService webService = (OpenTMSWebService) connectWebService.getData("WebService");
				if (webService != null)
				{
					JOptionPane.showMessageDialog(null,
							"Error: Trying to close openTMS WebServices which was not started by this application! Shutdown denied!",
							"openTMS WebServices Server not started by this application", JOptionPane.ERROR_MESSAGE);
				}
				else
				{
					JOptionPane.showMessageDialog(null,
							"Error: openTMS WebServices was not started!",
							"openTMS WebServices Server not running", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		catch (Exception e2)
		{
			e2.printStackTrace();
			JOptionPane.showMessageDialog(null,
					"Error: openTMS WebServices Server could not be shutdown\n" + e2.getMessage(),
					"openTMS WebServices Server Exception", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void updateCounterAction()
	{
		OpenTMSWebServiceConstants consts = new OpenTMSWebServiceConstants();
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
			JOptionPane.showMessageDialog(null, "Error: Web Service not running!", "openTMS Update Counter Values",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		/*
		 * if (server != null) consts = server.getOpenTMSWebServiceConstants();
		 * if (url != null) consts.setOpenTMSWebServerURL(url);
		 * 
		 * OpenTMSWebService service = new OpenTMSWebService(consts);
		 */
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

			JOptionPane.showMessageDialog(null, message, "openTMS Update Counter Values",
					JOptionPane.INFORMATION_MESSAGE);
		}
		catch (Exception ex)
		{
			// TODO Auto-generated catch block
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error: Web Service Error!", "openTMS Update Counter Values",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void uploadAction()
	{
		// get active chosen database

		if (syncLocalDataSource == null)
		{
			JOptionPane.showMessageDialog(null, "Sync Local Data Source is null", "openTMS Upload Result Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		HashMap<String, String> paramHash = new HashMap<String, String>();

		try
		{
			OpenTMSWebServiceConstants consts = new OpenTMSWebServiceConstants();
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
				JOptionPane.showMessageDialog(null, "Error: Web Service not running!", "openTMS Update Counter Values",
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
			param.put("dataSourceName", syncLocalDataSource.getDataSourceName());
			param.put("dataSourceConfigurationsFile", "");
			long updateCounterLocalDatSource = getlWebServerUpdateCounter() + 1;
			param.put("update-counter", updateCounterLocalDatSource + "");
			param.put("encoding", "UTF-8");

			Vector<String> resultexport = de.folt.rpc.connect.Interface.runExportOpenTMSDataSource(param);
			if (resultexport.size() != 4)
			{
				JOptionPane.showMessageDialog(null, "Error: Export local datasource!", "openTMS Upload",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			String iNumExported = resultexport.get(3);
			try
			{
				int iNum = Integer.parseInt(iNumExported);
				if (iNum == 0)
				{
					JOptionPane.showMessageDialog(null, "Info: No changes in data source; no upload initiated!",
							"openTMS Upload", JOptionPane.INFORMATION_MESSAGE);
					return;
				}
			}
			catch (Exception ex)
			{

			}
			String tmx_document = resultexport.get(1);

			paramHash.put("method", "upload");
			paramHash.put("user-id", System.getProperty("user.name"));
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
				JOptionPane.showMessageDialog(null, message, "openTMS Upload Result", JOptionPane.INFORMATION_MESSAGE);

				webServerUpdateCounter = paramHash.get("update-counter");
				try
				{
					setlWebServerUpdateCounter(Long.parseLong(webServerUpdateCounter));
					String dataSourceConfigurationsFile = syncLocalDataSource
							.getDefaultDataSourceConfigurationsFileName();
					DataSourceConfigurations config = new DataSourceConfigurations(dataSourceConfigurationsFile);
					DataSourceProperties dataModelProperties = new DataSourceProperties();
					dataModelProperties.remove("WebServerUpdateCounter");
					dataModelProperties.put("WebServerUpdateCounter", getlWebServerUpdateCounter() + ":"
							+ getSyncServerDataSource());
					config.addPropertiesToDataModelProperties(syncLocalDataSource.getDataSourceName(),
							dataModelProperties);
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
				JOptionPane.showMessageDialog(null, message, "openTMS Upload Result", JOptionPane.ERROR_MESSAGE);
			}
		}
		catch (Exception ex)
		{
			JOptionPane.showMessageDialog(null, ex.getMessage(), "openTMS Upload Result Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public String getSyncLocalDataSourceName()
	{
		return syncLocalDataSourceName;
	}

	public void setSyncLocalDataSourceName(String syncLocalDataSourceName)
	{
		this.syncLocalDataSourceName = syncLocalDataSourceName;
	}

}
