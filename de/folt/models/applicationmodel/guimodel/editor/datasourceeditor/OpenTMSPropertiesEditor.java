package de.folt.models.applicationmodel.guimodel.editor.datasourceeditor;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;

import de.folt.models.datamodel.GeneralLinguisticObject;
import de.folt.models.datamodel.LinguisticProperties;
import de.folt.models.datamodel.LinguisticProperty;
import de.folt.models.documentmodel.tmx.TmxProp;
import de.folt.models.documentmodel.tmx.TmxProp.PropType;
import de.folt.util.ColorTable;

public class OpenTMSPropertiesEditor
{
    public static void main(String[] args)
    {
        Shell shell = new Shell();
        OpenTMSPropertiesEditor prop = new OpenTMSPropertiesEditor(shell, 0, null, null);
        prop.show();
    }

    private String[] codepages;

    private String[] combinedLanguages;

    private Display display;

    private GeneralLinguisticObject generalLinguisticObject = null;

    private de.folt.util.Messages message;

    private Shell shell;

    private Table table;

    private OpenTMSXMLStyledTextWithPropertyEditor text;

    private String userLanguage = "en";

    private boolean bChangesMade = false;
    
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

    public OpenTMSPropertiesEditor(Composite parent, int style, OpenTMSXMLStyledTextWithPropertyEditor text, final GeneralLinguisticObject generalLinguisticObject)
    {

        shell = new Shell(SWT.BORDER | SWT.MODELESS | SWT.TITLE | SWT.RESIZE | SWT.CLOSE);
        message = de.folt.util.Messages.getInstance("de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.datasourceeditor", userLanguage);
        display = parent.getDisplay();
        GridLayout gridLayout = new GridLayout();
        shell.setLayout(gridLayout);
        if (generalLinguisticObject.getClass().getName().indexOf("MultiLingualObject") > -1)
        {
            shell.setText("Property Editor" + " " + "MultiLingualObject" + generalLinguisticObject.getId() + " " + generalLinguisticObject.getStUniqueID());
        }
        else
        {
            shell.setText("Property Editor" + " " + "MonoLingualObject" + +generalLinguisticObject.getId() + " " + generalLinguisticObject.getStUniqueID());
        }

        shell.addKeyListener(new KeyListener()
        {
            public void keyPressed(KeyEvent e)
            {
                char charpressed = e.character;
                if ((int) charpressed == SWT.F1)
                {
                    // ok call help
                    dataSourceEditor.displayOpenTMSHelp(OpenTMSPropertiesEditor.class.getName());
                }
            }

            public void keyReleased(KeyEvent e)
            {
                ;
            }
        });

        this.text = text;

        bChangesMade = false;

        combinedLanguages = de.folt.util.LanguageHandling.getCombinedLanguages();
        codepages = de.folt.util.CodePageHandling.getCodePages();

        this.setGeneralLinguisticObject(generalLinguisticObject);

        // Action.
        Action actionAddNew = new Action(message.getString("NewProperty"))
        {
            public void run()
            {
                // Append.
                TableItem item = new TableItem(table, SWT.NULL);
                String propname = UUID.randomUUID().toString();
                item.setText(new String[]
                    {
                            "null", "", "", "", TmxProp.PropType.PROP + "", propname
                    });
                item.setData("new", "true");
                table.select(table.getItemCount() - 1);
                item.setBackground(ColorTable.getInstance(display, "red"));
                TmxProp tmxProp = new TmxProp();
                tmxProp.setType(propname);
                LinguisticProperty ling = new LinguisticProperty(propname, LinguisticProperty.PropStatus.NEW, tmxProp);
                item.setData("tmxProp", tmxProp);
                item.setData("ling", ling);
                bChangesMade = true;
            }
        };

        Action actionDelete = new Action(message.getString("Deleteselectedproperty"))
        {
            public void run()
            {
                int index = table.getSelectionIndex();
                if (index < 0)
                {
                    System.out.println(message.getString("Pleaseselectanitemfirst. "));
                    return;
                }
                int[] selectedIndex = table.getSelectionIndices();
                MessageBox messageBox = new MessageBox(shell, SWT.YES | SWT.NO);
                messageBox.setText(message.getString("Confirmation"));
                messageBox.setMessage(message.getString("Areyousuretoremovethepropertywithid") + selectedIndex.length);
                if (messageBox.open() == SWT.YES)
                {
                    for (int i = 0; i < selectedIndex.length; i++)
                    {
                        // remove it from the GLO too...
                        TmxProp tmxProp = (TmxProp) table.getItem(selectedIndex[i]).getData("tmxProp");
                        if (tmxProp != null)
                        {
                            LinguisticProperty linguisticProperty = (LinguisticProperty) table.getItem(selectedIndex[i]).getData("ling");
                            if (linguisticProperty != null)
                                linguisticProperty.setPropStatus(LinguisticProperty.PropStatus.DELETED);
                        }
                    }
                    bChangesMade = true;
                    table.remove(selectedIndex);
                }
            }
        };

        Action actionSave = new Action(message.getString("SaveProperty"))
        {
            public void run()
            {
                saveProperties();
            }
        };

        Action actionClose = new Action(message.getString("ClosePropertyEditor"))
        {
            public void run()
            {
                closeEditor();
            }
        };

        ToolBar toolBar = new ToolBar(shell, SWT.RIGHT | SWT.FLAT);

        ToolBarManager manager = new ToolBarManager(toolBar);
        manager.add(actionAddNew);
        manager.add(new Separator());
        manager.add(actionDelete);
        manager.add(new Separator());
        manager.add(actionSave);
        manager.add(new Separator());
        manager.add(actionClose);
        manager.update(true);

        table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
        table.setLayoutData(new GridData(GridData.FILL_BOTH));

        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        
        table.addKeyListener(new KeyListener()
        {
            public void keyPressed(KeyEvent e)
            {
                if (e.keyCode == SWT.F1)
                {
                    // ok call help
                    dataSourceEditor.displayOpenTMSHelp(OpenTMSPropertiesEditor.class.getName());
                }
            }

            public void keyReleased(KeyEvent e)
            {
                ;
            }
        });

        TableColumn tcID = new TableColumn(table, SWT.LEFT);
        tcID.setText(message.getString("ID"));

        TableColumn tcContent = new TableColumn(table, SWT.NULL);
        tcContent.setText(message.getString("content"));

        TableColumn tcLanguage = new TableColumn(table, SWT.NULL);
        tcLanguage.setText(message.getString("language"));

        TableColumn tco_encoding = new TableColumn(table, SWT.NULL);
        tco_encoding.setText(message.getString("o_encoding"));

        TableColumn tcPropType = new TableColumn(table, SWT.NULL);
        tcPropType.setText(message.getString("propType"));

        TableColumn tcType = new TableColumn(table, SWT.NULL);
        tcType.setText(message.getString("type"));

        tcID.setWidth(120);
        tcContent.setWidth(250);
        tcLanguage.setWidth(120);
        tco_encoding.setWidth(120);
        tcPropType.setWidth(80);
        tcType.setWidth(120);

        loadProperties(generalLinguisticObject.getLinguisticProperties());

        table.pack();

        // Table editor.
        final TableEditor editor = new TableEditor(table);

        table.addListener(SWT.MouseDown, new Listener()
        {
            public void handleEvent(Event event)
            {
                try
                {
                    // Locate the cell position.
                    Point point = new Point(event.x, event.y);
                    final TableItem item = table.getItem(point);
                    if (item == null)
                        return;
                    int column = -1;
                    for (int i = 0; i < table.getColumnCount(); i++)
                    {
                        Rectangle rect = item.getBounds(i);
                        if (rect.contains(point))
                            column = i;
                    }

                    if (item.getData("new").equals("true"))
                    {
                        if ((column == 0) || (column == 1) || (column == 4) || (column == 5))
                            return;
                    }
                    else
                        return;

                    // Cell position located, now open the table editor.
                    // final Button button = new Button(table, SWT.CHECK);

                    final CCombo comboSimple = new CCombo(table, SWT.DROP_DOWN | SWT.BORDER);
                    if (column == 2)
                        comboSimple.setItems(combinedLanguages);

                    if (column == 3)
                        comboSimple.setItems(codepages);
                    // button.setSelection(item.getText(column).equalsIgnoreCase("YES"));

                    editor.horizontalAlignment = SWT.LEFT;
                    editor.grabHorizontal = true;

                    editor.setEditor(comboSimple, item, column);

                    final int selectedColumn = column;
                    Listener buttonListener = new Listener()
                    {
                        public void handleEvent(final Event e)
                        {
                            switch (e.type)
                            {
                                case SWT.FocusOut:
                                    if (comboSimple.getSelectionIndex() == -1)
                                        break;
                                    String text = comboSimple.getItem(comboSimple.getSelectionIndex());
                                    item.setText(selectedColumn, text);
                                    TmxProp tmxprop = (TmxProp) item.getData("tmxProp");
                                    if (selectedColumn == 2)
                                        tmxprop.setLang(text);
                                    else if (selectedColumn == 3)
                                        tmxprop.setO_encoding(text);
                                    LinguisticProperty ling = (LinguisticProperty) item.getData("ling");
                                    if (ling != null)
                                        ling.setPropStatus(LinguisticProperty.PropStatus.CHANGED);
                                    comboSimple.dispose();
                                    break;
                                case SWT.Traverse:
                                    switch (e.detail)
                                    {
                                        case SWT.TRAVERSE_RETURN:
                                            if (comboSimple.getSelectionIndex() == -1)
                                                break;
                                            String text1 = comboSimple.getItem(comboSimple.getSelectionIndex());
                                            item.setText(selectedColumn, text1);
                                            TmxProp tmxprop1 = (TmxProp) item.getData("tmxProp");
                                            if (selectedColumn == 2)
                                                tmxprop1.setLang(text1);
                                            else if (selectedColumn == 3)
                                                tmxprop1.setO_encoding(text1);
                                            LinguisticProperty ling2 = (LinguisticProperty) item.getData("ling");
                                            if (ling2 != null)
                                                ling2.setPropStatus(LinguisticProperty.PropStatus.CHANGED);
                                            //FALL THROUGH
                                        case SWT.TRAVERSE_ESCAPE:
                                            comboSimple.dispose();
                                            e.doit = false;
                                    }
                                    break;
                            }
                        }
                    };

                    comboSimple.addListener(SWT.FocusOut, buttonListener);
                    comboSimple.addListener(SWT.Traverse, buttonListener);
                    comboSimple.setFocus();
                    item.setBackground(column, ColorTable.getInstance(display, "green"));
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }

            }
        });

        table.addListener(SWT.MouseDown, new Listener()
        {
            public void handleEvent(Event event)
            {
                // Locate the cell position.
                Point point = new Point(event.x, event.y);
                final TableItem item = table.getItem(point);
                if (item == null)
                    return;
                int column = -1;
                for (int i = 0; i < table.getColumnCount(); i++)
                {
                    Rectangle rect = item.getBounds(i);
                    if (rect.contains(point))
                        column = i;
                }

                if (item.getData("new").equals("false"))
                    if ((column != 1)) // only change content if content selected and old item
                        return;

                if (item.getData("new").equals("true"))
                    if ((column == 0)) // id no change allowed 
                        return;
                if (item.getData("new").equals("true"))
                    if ((column == 4)) // no change allowed on props 
                        return;
                if (item.getData("new").equals("true"))
                    if ((column == 3)) // change o_encoding with drop down  if new
                        return;

                if (item.getData("new").equals("true"))
                    if ((column == 2)) // change language with drop down if new
                        return;

                // Cell position located, now open the table editor.

                final Text text = new Text(table, SWT.NONE);
                text.setText(item.getText(column));

                editor.horizontalAlignment = SWT.LEFT;
                editor.grabHorizontal = true;

                editor.setEditor(text, item, column);

                final int selectedColumn = column;
                Listener textListener = new Listener()
                {
                    public void handleEvent(final Event e)
                    {
                        switch (e.type)
                        {
                            case SWT.FocusOut:
                                item.setText(selectedColumn, text.getText());
                                TmxProp tmxprop = (TmxProp) item.getData("tmxProp");

                                if (selectedColumn == 1)
                                    tmxprop.setContent(item.getText(selectedColumn));
                                else if (selectedColumn == 5)
                                    tmxprop.setType(item.getText(selectedColumn));

                                LinguisticProperty ling = (LinguisticProperty) item.getData("ling");
                                if (ling != null)
                                    ling.setPropStatus(LinguisticProperty.PropStatus.CHANGED);
                                text.dispose();
                                bChangesMade = true;
                                break;
                            case SWT.Traverse:
                                switch (e.detail)
                                {
                                    case SWT.TRAVERSE_RETURN:
                                        item.setText(selectedColumn, text.getText());
                                        tmxprop = (TmxProp) item.getData("tmxProp");
                                        if (selectedColumn == 1)
                                            tmxprop.setContent(item.getText(selectedColumn));
                                        else if (selectedColumn == 5)
                                            tmxprop.setType(item.getText(selectedColumn));
                                        LinguisticProperty ling2 = (LinguisticProperty) item.getData("ling");
                                        if (ling2 != null)
                                            ling2.setPropStatus(LinguisticProperty.PropStatus.CHANGED);
                                        //FALL THROUGH
                                    case SWT.TRAVERSE_ESCAPE:
                                        text.dispose();
                                        e.doit = false;
                                }
                                bChangesMade = true;
                                break;
                        }
                    }
                };

                text.addListener(SWT.FocusOut, textListener);
                text.addListener(SWT.Traverse, textListener);

                text.setFocus();

                item.setBackground(1, ColorTable.getInstance(display, "green"));
            }

        });

        table.addListener(SWT.Selection, new Listener()
        {
            public void handleEvent(Event event)
            {
                // System.out.println("Selected: " + table.getSelection()[0]);
            }
        });

        Listener sortListener = new Listener()
        {
            public void handleEvent(Event event)
            {
                if (!(event.widget instanceof TableColumn))
                    return;
                TableColumn tc = (TableColumn) event.widget;
                sortTable(table, table.indexOf(tc));
                // System.out.println("The table is sorted by column #" + table.indexOf(tc));
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++)
            ((TableColumn) table.getColumn(i)).addListener(SWT.Selection, sortListener);

        shell.pack();

    }

    /**
     * close 
     */
    public void close()
    {
        shell.dispose();
    }

    /**
     * closeEditor 
     */
    protected void closeEditor()
    {
        if (bChangesMade)
        {
            MessageBox messageBox = new MessageBox(shell, SWT.YES | SWT.NO);
            messageBox.setText(message.getString("Confirmation"));
            messageBox.setMessage(message.getString("Areyousuretosavechangesmade"));
            if (messageBox.open() == SWT.YES)
            {
                this.saveProperties();
            }
        }

        close();
    }

    /**
     * @return the generalLinguisticObject
     */
    public GeneralLinguisticObject getGeneralLinguisticObject()
    {
        return generalLinguisticObject;
    }

    @SuppressWarnings("unchecked")
    public void loadProperties(LinguisticProperties linguisticProperties)
    {
        if (linguisticProperties == null)
            return;
        LinguisticProperty ling = null;
        Set<String> enumprop = linguisticProperties.keySet();
        Iterator<String> it = enumprop.iterator();
        while (it.hasNext())
        {
            String key = (String) it.next();
            Object value = (Object) linguisticProperties.get(key);
            TmxProp tmxProp = null;
            if (value.getClass().getName().equals("java.util.Vector"))
            {
                for (int i = 0; i < ((Vector<LinguisticProperty>) value).size(); i++)
                {
                    ling = ((Vector<LinguisticProperty>) value).get(i);
                    Object lingValue = ling.getValue();
                    String classname = lingValue.getClass().getName();
                    if (classname.equals("de.folt.models.documentmodel.tmx.TmxProp"))
                    {
                        tmxProp = (TmxProp) lingValue;
                        if ((ling != null) && (tmxProp != null) && !ling.getPropStatus().equals(LinguisticProperty.PropStatus.DELETED))
                        {
                            String id = tmxProp.getId() + "";
                            String content = tmxProp.getContent();
                            String language = tmxProp.getLang();
                            String o_encoding = tmxProp.getO_encoding();
                            PropType propType = tmxProp.getPropType();
                            String type = tmxProp.getType();

                            TableItem item = new TableItem(table, SWT.NULL);
                            item.setText(new String[]
                                {
                                        id, content, language, o_encoding, propType + "", type
                                });
                            item.setBackground(0, ColorTable.getInstance(display, "grey"));
                            item.setBackground(2, ColorTable.getInstance(display, "grey"));
                            item.setBackground(3, ColorTable.getInstance(display, "grey"));
                            item.setBackground(4, ColorTable.getInstance(display, "grey"));
                            item.setBackground(5, ColorTable.getInstance(display, "grey"));
                            item.setData("new", "false");
                            item.setData("tmxProp", tmxProp);
                            item.setData("ling", ling);
                        }
                    }
                }
            }
            else
            {
                ling = (LinguisticProperty) linguisticProperties.get(key);
                Object lingValue = ling.getValue();
                String classname = lingValue.getClass().getName();
                if (classname.equals("de.folt.models.documentmodel.tmx.TmxProp"))
                {
                    tmxProp = (TmxProp) lingValue;
                }
            }
            if ((ling != null) && (tmxProp != null) && !ling.getPropStatus().equals(LinguisticProperty.PropStatus.DELETED))
            {
                String id = tmxProp.getId() + "";
                String content = tmxProp.getContent();
                String language = tmxProp.getLang();
                String o_encoding = tmxProp.getO_encoding();
                PropType propType = tmxProp.getPropType();
                String type = tmxProp.getType();
                ling.setPropStatus(LinguisticProperty.PropStatus.OLD);

                TableItem item = new TableItem(table, SWT.NULL);
                item.setText(new String[]
                    {
                            id, content, language, o_encoding, propType + "", type
                    });
                item.setBackground(0, ColorTable.getInstance(display, "grey"));
                item.setBackground(2, ColorTable.getInstance(display, "grey"));
                item.setBackground(3, ColorTable.getInstance(display, "grey"));
                item.setBackground(4, ColorTable.getInstance(display, "grey"));
                item.setBackground(5, ColorTable.getInstance(display, "grey"));
                item.setData("new", "false");
                item.setData("tmxProp", tmxProp);
                item.setData("ling", ling);
            }
        }
    }

    private void saveProperties()
    {
        if (!bChangesMade)
        {
            MessageBox messageBox = new MessageBox(shell);
            String string = message.getString("Nochangesmade");
            messageBox.setText(string);
            string = message.getString("Nochangesmadetext");
            messageBox.setMessage(string);
            messageBox.open();
            return;
        }

        // we must move thru all items and check if changed or new
        Map<String, Object> linguisticProperties = generalLinguisticObject.getLinguisticProperties();
        if (linguisticProperties == null)
        {
            generalLinguisticObject.setLinguisticProperties(new LinguisticProperties());
        }

        // now add the new ones
        TableItem[] items = table.getItems();
        for (int i = 0; i < items.length; i++)
        {
            if (items[i].getData("new").equals("false"))
                continue;

            TmxProp tmxProp = (TmxProp) items[i].getData("tmxProp");
            LinguisticProperty ling = (LinguisticProperty) items[i].getData("ling");
            linguisticProperties.put(tmxProp.getType(), ling);
        }
        text.setChanged(true);
        if (generalLinguisticObject.getClass().getName().equals("de.folt.models.datamodel.MonoLingualObject"))
            text.setBackground(display.getSystemColor(SWT.COLOR_DARK_YELLOW));
        else
            text.setBackground(display.getSystemColor(SWT.COLOR_MAGENTA));

        bChangesMade = false;
    }

    /**
     * @param generalLinguisticObject the generalLinguisticObject to set
     */
    public void setGeneralLinguisticObject(GeneralLinguisticObject generalLinguisticObject)
    {
        this.generalLinguisticObject = generalLinguisticObject;
    }

    public void show()
    {
        shell.open();
        //textUser.forceFocus();

        // Set up the event loop.
        while (!shell.isDisposed())
        {
            if (!display.readAndDispatch())
            {
                // If no more entries in event queue
                display.sleep();
            }
        }

        // display.dispose();
    }

    /**
     * Sorts the given table by the specified column.
     * @param columnIndex
     */
    @SuppressWarnings("unchecked")
    public void sortTable(Table table, int columnIndex)
    {
        if (table == null || table.getColumnCount() <= 1)
            return;
        if (columnIndex < 0 || columnIndex >= table.getColumnCount())
            throw new IllegalArgumentException(message.getString("Thespecifiedcolumndoesnotexist."));

        final int colIndex = columnIndex;
        @SuppressWarnings("rawtypes")
		Comparator comparator = new Comparator()
        {
            public int compare(Object o1, Object o2)
            {
                return ((TableItem) o1).getText(colIndex).compareTo(((TableItem) o2).getText(colIndex));
            }

            public boolean equals(Object obj)
            {
                return false;
            }
        };

        TableItem[] tableItems = table.getItems();
        Arrays.sort(tableItems, comparator);

        for (int i = 0; i < tableItems.length; i++)
        {
            TableItem item = new TableItem(table, SWT.NULL);
            for (int j = 0; j < table.getColumnCount(); j++)
            {
                item.setText(j, tableItems[i].getText(j));
                item.setData("tmxProp", tableItems[i].getData("tmxProp"));
                item.setData("ling", tableItems[i].getData("ling"));
                item.setData("new", tableItems[i].getData("new"));
                item.setBackground(0, tableItems[i].getBackground(0));
                item.setBackground(2, tableItems[i].getBackground(1));
                item.setBackground(2, tableItems[i].getBackground(2));
                item.setBackground(3, tableItems[i].getBackground(3));
                item.setBackground(4, tableItems[i].getBackground(4));
                item.setBackground(5, tableItems[i].getBackground(5));
            }
            tableItems[i].dispose();
        }
    }

    /**
     * toTop 
     */
    public void toTop()
    {
        shell.forceFocus();

    }
}
