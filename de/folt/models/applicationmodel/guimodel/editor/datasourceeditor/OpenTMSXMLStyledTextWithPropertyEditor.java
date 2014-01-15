/*
 * Created on 23.06.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.models.applicationmodel.guimodel.editor.datasourceeditor;

import java.util.Hashtable;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Widget;

import de.folt.models.datamodel.GeneralLinguisticObject;
import de.folt.models.datamodel.MonoLingualObject;
import de.folt.models.datamodel.MultiLingualObject;
import de.folt.models.documentmodel.document.XmlDocument;

/**
 * @author klemens
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class OpenTMSXMLStyledTextWithPropertyEditor extends OpenTMSXMLStyledText
{

    OpenTMSPropertiesEditor propertyEditor = null;
    
    private DataSourceEditor dataSourceEditor = new DataSourceEditor();

    /**
     * @return the dataSourceEditor
     */
    public DataSourceEditor getDataSourceEditor()
    {
        return dataSourceEditor;
    }

    /**
     * @param dataSourceEditor the dataSourceEditor to set
     */
    public void setDataSourceEditor(DataSourceEditor dataSourceEditor)
    {
        this.dataSourceEditor = dataSourceEditor;
    }

    /**
     * @return the propertyEditor
     */
    public OpenTMSPropertiesEditor getPropertyEditor()
    {
        return propertyEditor;
    }

    /**
     * @param parent
     * @param style
     */
    public OpenTMSXMLStyledTextWithPropertyEditor(Composite parent, int style)
    {
        super(parent, style);
        AddDropSupport();
    }

    /**
     * displayMOLProperties 
     */
    protected void displayMOLProperties()
    {
        if (propertyEditor != null)
            propertyEditor.close();
        propertyEditor = null;
        if (propertyEditor == null)
        {
            MonoLingualObject mono = (MonoLingualObject) this.getData("MOL");
            if (mono != null)
            {
                propertyEditor = new OpenTMSPropertiesEditor(this.getParent(), SWT.MULTI | SWT.V_SCROLL | SWT.WRAP | SWT.BORDER, this, (GeneralLinguisticObject) mono);
                propertyEditor.show();
            }
            return;
        }
        propertyEditor.toTop();
    }

    /**
     * displayMULProperties 
     */
    protected void displayMULProperties()
    {
        if (propertyEditor != null)
            propertyEditor.close();
        propertyEditor = null;
        if (propertyEditor == null)
        {
            MultiLingualObject multi = (MultiLingualObject) this.getData("MUL");
            if (multi != null)
            {
                propertyEditor = new OpenTMSPropertiesEditor(this.getParent(), SWT.MULTI | SWT.V_SCROLL | SWT.WRAP | SWT.BORDER, this, (GeneralLinguisticObject)multi);
                propertyEditor.show();
            }
            return;
        }
        propertyEditor.toTop();

    }

    /* (non-Javadoc)
     * @see de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.OpenTMSStyledText#handleRightMouseClick(org.eclipse.swt.widgets.Widget)
     */
    @Override
    protected Menu handleRightMouseClick(Widget w, MouseEvent eMouse)
    {
        Menu popUpMenu = super.handleRightMouseClick(w, eMouse);
        new MenuItem(popUpMenu, SWT.SEPARATOR);
        MenuItem tmxMOLPropItem = new MenuItem(popUpMenu, SWT.PUSH);
        tmxMOLPropItem.setText(message.getString("ShowMOLProperties"));
        tmxMOLPropItem.addListener(SWT.Selection, new Listener()
        {
            public void handleEvent(Event e)
            {
                displayMOLProperties();
            }
        });

        MenuItem tmxMULPropItem = new MenuItem(popUpMenu, SWT.PUSH);
        tmxMULPropItem.setText(message.getString("ShowMULProperties"));
        tmxMULPropItem.addListener(SWT.Selection, new Listener()
        {
            public void handleEvent(Event e)
            {
                displayMULProperties();
            }
        });

        return popUpMenu;
    }

    private void AddDropSupport()
    {
        // final Shell shell = new Shell();
        // final Table dropTable = new Table(shell, SWT.BORDER);
        int operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT;
        DropTarget dragdropsupportwindow = new DropTarget(this, operations);

        // Receive data in Text or File format
        final TextTransfer textTransfer = TextTransfer.getInstance();
        final FileTransfer fileTransfer = FileTransfer.getInstance();
        Transfer[] types = new Transfer[]
            {
                    fileTransfer, textTransfer
            };
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

                    try
                    {
                        String dataSourceName = files[0];
                        dataSourceName = dataSourceName.replaceAll(" \\(.*\\)", "");
                        Cursor hglass = new Cursor(getDisplay(), SWT.CURSOR_WAIT);
                        Cursor arrow = new Cursor(getDisplay(), SWT.CURSOR_ARROW);
                        getShell().setCursor(hglass);
                        String dataSourceType = XmlDocument.getRootElementName(dataSourceName);

                        System.out.println("DataSourceType Drop-File = \"" + dataSourceType + "\"");
                        Hashtable<String, Object> param = new Hashtable<String, Object>();

                        if (dataSourceType.equals("xliff") || dataSourceType.equals("tmx"))
                        {
                            final Vector<String[]> tmxDatabases = de.folt.models.datamodel.DataSourceConfigurations.getOpenTMSDatabasesWithType();
                            // check if exists ...
                            if (tmxDatabases != null)
                            {
                                for (int i = 0; i < tmxDatabases.size(); i++)
                                {
                                    if (tmxDatabases.get(i)[0].equals(dataSourceName))
                                    {
                                        // ok here we should open it...
                                        getShell().setCursor(arrow);
                                        dataSourceEditor.openDataSource(dataSourceName);
                                        return;
                                    }
                                }
                            }
                            param.put("dataSourceType", dataSourceType);
                            param.put("dataSourceName", dataSourceName);
                            Vector<String> result = de.folt.rpc.connect.Interface.runCreateDB(param);
                            if (result.get(0).equals(de.folt.constants.OpenTMSConstants.OpenTMS_ID_FAILURE + ""))
                            {
                                MessageBox messageBox = new MessageBox(getShell());
                                String string = message.getString("Error_Creating");
                                messageBox.setText(string);
                                string = message.getString("OpenTMS_database_not_created") + " " + dataSourceName;
                                messageBox.setMessage(string);
                                messageBox.open();
                                getShell().setCursor(arrow);
                                dataSourceName = null;
                            }
                            else
                            {
                                MessageBox messageBox = new MessageBox(getShell());
                                String string = message.getString("Success_Creating");
                                messageBox.setText(string);
                                string = message.getString("OpenTMS_database_created") + " " + dataSourceName;
                                messageBox.setMessage(string);
                                messageBox.open();
                                getShell().setCursor(arrow);
                                dataSourceEditor.openDataSource(dataSourceName);
                            }
                            getShell().setCursor(arrow);
                        }
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }
            }

            public void dropAccept(DropTargetEvent event)
            {
                ;
            }
        });
    }
}
