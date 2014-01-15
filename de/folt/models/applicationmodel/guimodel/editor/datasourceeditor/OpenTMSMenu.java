/*
 * Created on 04.12.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.models.applicationmodel.guimodel.editor.datasourceeditor;

import java.util.Hashtable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import de.folt.webservices.OpenTMSWebServiceServer;

/**
 * @author klemens
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class OpenTMSMenu
{

    /**
     * Add all openTMS menus
     */
    public final static int allMenus = 1;

    /**
     * Add copy from one data source to another data source menu
     */
    public final static int copyFromToOpenTMSDataSource = 512;

    /**
     * Add create data source menu
     */
    public final static int createDataSource = 8;

    /**
     * Add delete data source menu
     */
    public final static int deleteDataSource = 16;

    /**
     * Add export from data source menu
     */
    public final static int exportFromDataSource = 128;

    /**
     * Add export xliff terminology menu
     */
    public final static int exportXliffInternalTerminology = 256;

    /**
     * Add import into data source menu
     */
    public final static int importIntoDataSource = 64;

    /**
     * Add manage multiple data source menu
     */
    public final static int manageMultipleDataSource = 32;

    /**
     * Add reverse conversion menu
     */
    public final static int reverseConversion = 4;

    /**
     * Add Web Service Start menu item
     */
    public static final int startWebService = 0;

	/**
     * Add Translate (incl. conversion) menu
     */
    public final static int translate = 2;

    private Hashtable<String, MenuItem> menuItems = new Hashtable<String, MenuItem>();

    private de.folt.util.Messages message;

    private Shell shell;
    
    private OpenTMSWebServiceServer server;

    private String userLanguage = "en";

    /**
     * @param shell
     */
    public OpenTMSMenu(Shell shell)
    {
        super();
        this.shell = shell;
    }

    /**
     * Add the openTMS menu items to a given menu bar
     * @param menubar the menu bar where the various openTMS items re added to
     */
    public void createOpenTMSMenu(Menu menubar)
    {
        createOpenTMSMenu(menubar, OpenTMSMenu.allMenus);
    }

    /**
     * Add the openTMS menu items to a given menu bar
     * @param menubar the menu bar where the various openTMS items re added to
     * @param options these options define the menu items to be displayed
     */
    public void createOpenTMSMenu(Menu menubar, int options)
    {
        message = de.folt.util.Messages.getInstance("de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.datasourceeditor", userLanguage);

        MenuItem pluginMenu = new MenuItem(menubar, SWT.CASCADE);
        pluginMenu.setText(message.getString("Menu_OpenTMS_External")); //$NON-NLS-1$
        pluginMenu.setAccelerator(SWT.ALT | 'o');
        Menu pmenu = new Menu(pluginMenu);
        pluginMenu.setMenu(pmenu);

        int valall = options & OpenTMSMenu.allMenus;
        boolean bAll = (valall == 1);

        if (bAll || ((options & OpenTMSMenu.translate) == OpenTMSMenu.translate))
        {
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
        }

        if (bAll || ((options & OpenTMSMenu.reverseConversion) == OpenTMSMenu.reverseConversion))
        {
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
        }

        if (bAll || ((options & OpenTMSMenu.createDataSource) == OpenTMSMenu.createDataSource))
        {
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
        }

        if (bAll || ((options & OpenTMSMenu.deleteDataSource) == OpenTMSMenu.deleteDataSource))
        {
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
        }

        if (bAll || ((options & OpenTMSMenu.manageMultipleDataSource) == OpenTMSMenu.manageMultipleDataSource))
        {
            MenuItem manageMultipleDataSource = new MenuItem(pmenu, SWT.PUSH);
            manageMultipleDataSource.setText(message.getString("manageMultipleDataSource")); //$NON-NLS-1$
            manageMultipleDataSource.addSelectionListener(new SelectionAdapter()
            {
                public void widgetSelected(SelectionEvent e)
                {
                    ChooseDataSourceDialog chooser = new ChooseDataSourceDialog(shell, false);
                    chooser.setDataSourceDisplayType(de.folt.models.datamodel.multipledatasource.MultipleDataSource.class.getName());
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
        }

        if (bAll || ((options & OpenTMSMenu.importIntoDataSource) == OpenTMSMenu.importIntoDataSource))
        {
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
        }

        if (bAll || ((options & OpenTMSMenu.exportFromDataSource) == OpenTMSMenu.exportFromDataSource))
        {
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
        }

        if (bAll || ((options & OpenTMSMenu.exportXliffInternalTerminology) == OpenTMSMenu.exportXliffInternalTerminology))
        {
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
        }

        if (bAll || ((options & OpenTMSMenu.copyFromToOpenTMSDataSource) == OpenTMSMenu.copyFromToOpenTMSDataSource))
        {
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
    }

    /**
     * @return the menuItems
     */
    public Hashtable<String, MenuItem> getMenuItems()
    {
        return menuItems;
    }

    /**
     * @return the message
     */
    public de.folt.util.Messages getMessage()
    {
        return message;
    }

    /**
     * @return the shell
     */
    public Shell getShell()
    {
        return shell;
    }

    /**
     * @return the userLanguage
     */
    public String getUserLanguage()
    {
        return userLanguage;
    }

    /**
     * @param menuItems the menuItems to set
     */
    public void setMenuItems(Hashtable<String, MenuItem> menuItems)
    {
        this.menuItems = menuItems;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(de.folt.util.Messages message)
    {
        this.message = message;
    }

    /**
     * @param shell the shell to set
     */
    public void setShell(Shell shell)
    {
        this.shell = shell;
    }

    /**
     * @param userLanguage the userLanguage to set
     */
    public void setUserLanguage(String userLanguage)
    {
        this.userLanguage = userLanguage;
    }

	public OpenTMSWebServiceServer getServer()
	{
		return server;
	}

	public void setServer(OpenTMSWebServiceServer server)
	{
		this.server = server;
	}

}
