/*
 * Created on 03.11.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.models.applicationmodel.guimodel.editor.datasourceeditor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import com.araya.editor.DocumentPropertiesDialog;
import com.araya.utilities.DocumentProperties;

import de.folt.util.OpenTMSProperties;

/**
 * @author klemens
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class ConversionOptions  extends Dialog
{

    private boolean bdoNotResolveEntities = false;

    private boolean bIcCancel = true;

    private boolean bSegCRLF = false;

    private boolean bSegType = false;

    private Button ButtondoNotResolveEntities;

    private String configfile = "";

    private String curruser = "";

    private DocumentProperties documentProperties;

    private String doNotResolveEntitiesFile = "";

    private StyledText donotresolveentitiesfileNameText;

    private de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.EditorConfiguration editorConfiguration = null;

    private de.folt.util.Messages message;

    private StyledText number;

    private Button segCRLF;

    private Button segType;

    private String separator;

    private Shell shell;

    private String sourceFileName = "";
    
    private String stNumber = "1";

    public ConversionOptions(Shell parent, String location, String sourceXliffFileName)
    {
        super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.CLOSE);
        message = de.folt.util.Messages.getInstance("de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.datasourceeditor", "en");

        this.sourceFileName = sourceXliffFileName;

        configfile = OpenTMSProperties.getInstance().getOpenTMSProperty("OpenTMS.DataSourceEditor.EditorConfigurationDirectory");

        curruser = System.getProperty("user.name").toLowerCase();
        editorConfiguration = new de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.EditorConfiguration(shell, configfile, curruser);

        documentProperties = null;

        boolean brecentSegment = StringToBoolean(editorConfiguration.loadValueForKey("recentSegment"));
        boolean brecentCRLF = StringToBoolean(editorConfiguration.loadValueForKey("recentCRLF"));
        String recentCRLFCount = editorConfiguration.loadValueForKey("recentCRLFCount");

        bdoNotResolveEntities = StringToBoolean(editorConfiguration.loadValueForKey("doNotResolveEntities"));
        String donotresolveentitiesfile = editorConfiguration.loadValueForKey("donotresolveentitiesfile");

        separator = System.getProperty("file.separator"); //$NON-NLS-1$
        rewriteResources(separator);
        //
        // Conversion options
        //

        shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.CLOSE);
        shell.setLayout(new GridLayout(1, false));
        shell.setText(message.getString("Conversion_options"));

        Group conversionOptions = new Group(shell, SWT.NONE);
        conversionOptions.setText(message.getString("Conversion_options"));
        conversionOptions.setLayout(new GridLayout(3, true));
        conversionOptions.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));

        segCRLF = new Button(conversionOptions, SWT.CHECK);
        segCRLF.setSelection(brecentCRLF);
        segCRLF.setText(message.getString("Segmentation_by_CRLF"));
        segCRLF.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                number.setEnabled(segCRLF.getSelection());
            }
        });

        segType = new Button(conversionOptions, SWT.CHECK);
        segType.setText(message.getString("Segmentation_by_Element"));
        segType.setSelection(brecentSegment);

        ButtondoNotResolveEntities = new Button(conversionOptions, SWT.CHECK);
        ButtondoNotResolveEntities.setSelection(bdoNotResolveEntities);

        if (bdoNotResolveEntities == true)
            doNotResolveEntitiesFile = "yes";
        else
            doNotResolveEntitiesFile = "no";

        ButtondoNotResolveEntities.setText(message.getString("doNotResolveEntities"));
        ButtondoNotResolveEntities.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                // doNotResolveEntities.setSelection(doNotResolveEntities.getSelection());
                if (ButtondoNotResolveEntities.getSelection() == true)
                    doNotResolveEntitiesFile = "yes";
                else
                    doNotResolveEntitiesFile = "no";
            }
        });

        Composite optionsBottom = new Composite(conversionOptions, SWT.NONE);
        optionsBottom.setLayout(new GridLayout(2, false));
        optionsBottom.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));

        Label numCRLFLabel = new Label(optionsBottom, SWT.NONE);
        numCRLFLabel.setText(message.getString("Number_of_CRLF"));
        number = new StyledText(optionsBottom, SWT.BORDER);
        GridData numberData = new GridData();
        numberData.widthHint = 40;
        number.setLayoutData(numberData);

        if ((recentCRLFCount != null) && !recentCRLFCount.equals(""))
            number.setText(recentCRLFCount);
        else
            number.setText("1");

        if (segCRLF.getSelection() == true)
            number.setEnabled(true);
        else
            number.setEnabled(false);

        Button docProps = new Button(conversionOptions, SWT.PUSH);
        docProps.setText(message.getString("Set_Properties"));

        docProps.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        docProps.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                if (!sourceFileName.equals(""))
                {
                    if (documentProperties == null)
                    {
                        documentProperties = new DocumentProperties(sourceFileName);
                    }
                    DocumentPropertiesDialog propertiesDialog = new DocumentPropertiesDialog(shell.getDisplay(), "", documentProperties, "", null, null);
                    propertiesDialog.show();
                }
            }
        });

        // Group optionsEntFileBottom = new Group(shell, SWT.NONE);
        // optionsEntFileBottom.setLayout(new GridLayout(2, false));
        // optionsEntFileBottom.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
        // optionsEntFileBottom.setText(message.getString("NoEntity_document"));

        @SuppressWarnings("unused")
        Label dummy = new Label(conversionOptions, SWT.NONE);

        Label donotresolveentitiesfileNameLabel = new Label(conversionOptions, SWT.NONE);
        donotresolveentitiesfileNameLabel.setText(message.getString("NoEntity_document"));

        donotresolveentitiesfileNameText = new StyledText(conversionOptions, SWT.BORDER);
        donotresolveentitiesfileNameText.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));

        if (donotresolveentitiesfileNameText != null)
            donotresolveentitiesfileNameText.setText(donotresolveentitiesfile);

        Button donotresolveentitiesfileSource = new Button(conversionOptions, SWT.PUSH);
        donotresolveentitiesfileSource.setText(message.getString("Browse"));
        donotresolveentitiesfileSource.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                FileDialog fd = new FileDialog(shell, SWT.OPEN);
                String extensions[] =
                    {
                        "*.txt; *.*"
                    };
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
                    donotresolveentitiesfileNameText.setText(fd.getFilterPath() + separator + fd.getFileName());
                    fd = null;

                }
            }
        });
        
        Composite bottom = new Composite(shell, SWT.BORDER);
        bottom.setLayout(new GridLayout(2, true));
        bottom.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));


        Button close = new Button(bottom, SWT.PUSH);
        close.setText(message.getString("Cl&ose"));
        close.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        close.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                bdoNotResolveEntities = ButtondoNotResolveEntities.getSelection();

                bSegCRLF = segCRLF.getSelection();

                bSegType = segType.getSelection();
                
                doNotResolveEntitiesFile = donotresolveentitiesfileNameText.getText();
                
                stNumber = number.getText();
                
                editorConfiguration.saveKeyValuePair("recentSegment", BooleanToString(segType.getSelection()));
                editorConfiguration.saveKeyValuePair("recentCRLF", BooleanToString(segCRLF.getSelection()));
                editorConfiguration.saveKeyValuePair("recentCRLFCount", number.getText());
                editorConfiguration.saveKeyValuePair("doNotResolveEntities", BooleanToString(ButtondoNotResolveEntities.getSelection()));
                editorConfiguration.saveKeyValuePair("donotresolveentitiesfile", donotresolveentitiesfileNameText.getText());
                bIcCancel = false;
                shell.close();
            }
        });
        
        Button cancel = new Button(bottom, SWT.PUSH);
        cancel.setText(message.getString("Cancel"));
        cancel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        cancel.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                bIcCancel = true;
                shell.close();
            }
        });

        shell.pack();
    }

    private String BooleanToString(boolean b)
    {
        if (b)
            return "true";
        else
            return "false";
    }

    /**
     * @return the documentProperties
     */
    public DocumentProperties getDocumentProperties()
    {
        return documentProperties;
    }

    /**
     * @return the donotresolveentities
     */
    public String getDoNotResolveEntitiesFile()
    {
        return doNotResolveEntitiesFile;
    }

    /**
     * @return the stNumber
     */
    public String getStNumber()
    {
        return stNumber;
    }

    /**
     * @return the bdoNotResolveEntities
     */
    public boolean isBdoNotResolveEntities()
    {
        return bdoNotResolveEntities;
    }

    /**
     * @return the bIcCancel
     */
    public boolean isBIcCancel()
    {
        return bIcCancel;
    }

    /**
     * @return the bSegCRLF
     */
    public boolean isBSegCRLF()
    {
        return bSegCRLF;
    }

    /**
     * @return the bSegType
     */
    public boolean isBSegType()
    {
        return bSegType;
    }

    public boolean open()
    {
        show();
        return isBIcCancel();
    }

    /**
     * rewriteResources
     * 
     * @param sep
     */
    public void rewriteResources(String sep)
    {

        FileReader reader = null;
        String line = "";

        try
        {
            String propfile = "lib/eaglememex.properties";

            File pr = new File(propfile);
            if (!pr.exists())
                return;

            // propfile = EMXProperties.getPropfileName();

            reader = new FileReader(propfile); // adapted 12.09.2006
            FileOutputStream properties = new FileOutputStream("eaglememex.properties");

            int i;
            char c;
            i = reader.read();
            while (i != -1)
            {
                c = (char) i;
                line = line + c;
                if (c == '\n')
                {
                    if (line.indexOf("eaglememex.ini.dir") > -1)
                    {
                        String _line = "eaglememex.ini.dir=" + System.getProperty("user.dir") + sep + "ini" + sep + "\r\n";
                        line = "";
                        for (int j = 0; j < _line.length(); j++)
                        {
                            line = line + _line.charAt(j);
                            if (_line.charAt(j) == '\\')
                            {
                                line = line + '\\';
                            }
                        }
                    }
                    if (line.indexOf("database.path") > -1)
                    {
                        String _line = "database.path=" + System.getProperty("user.dir") + sep + "database" + sep + "\r\n";
                        line = "";
                        for (int j = 0; j < _line.length(); j++)
                        {
                            line = line + _line.charAt(j);
                            if (_line.charAt(j) == '\\')
                            {
                                line = line + '\\';
                            }
                        }
                    }
                    if (line.indexOf("mapfile") > -1)
                    {
                        String _line = "mapfile=" + System.getProperty("user.dir") + sep + "lib" + sep + "tbxmap.dat" + "\r\n";
                        line = "";
                        for (int j = 0; j < _line.length(); j++)
                        {
                            line = line + _line.charAt(j);
                            if (_line.charAt(j) == '\\')
                            {
                                line = line + '\\';
                            }
                        }
                    }
                    if (line.indexOf("irregularfile") > -1)
                    {
                        String _line = "irregularfile=" + System.getProperty("user.dir") + sep + "ini" + sep + "irregular.xml" + "\r\n";
                        line = "";
                        for (int j = 0; j < _line.length(); j++)
                        {
                            line = line + _line.charAt(j);
                            if (_line.charAt(j) == '\\')
                            {
                                line = line + '\\';
                            }
                        }
                    }
                    if (line.indexOf("eaglememex.stopwordfile") > -1)
                    {
                        String _line = "eaglememex.stopwordfile=" + System.getProperty("user.dir") + sep + "ini" + sep + "stopword.txt" + "\r\n";
                        line = "";
                        for (int j = 0; j < _line.length(); j++)
                        {
                            line = line + _line.charAt(j);
                            if (_line.charAt(j) == '\\')
                            {
                                line = line + '\\';
                            }
                        }
                    }
                    if (line.indexOf("ext.jarfiles") > -1)
                    {
                        String _line = "ext.jarfiles=" + System.getProperty("user.dir") + sep + "lib" + sep + "sqlconvert.jar" + ";" + System.getProperty("user.dir") + sep + "lib" + sep
                                + "converters.jar" + "\r\n";
                        line = "";
                        for (int j = 0; j < _line.length(); j++)
                        {
                            line = line + _line.charAt(j);
                            if (_line.charAt(j) == '\\')
                            {
                                line = line + '\\';
                            }
                        }
                    }

                    properties.write(line.getBytes());
                    line = "";
                }
                i = reader.read();
            }
            properties.close();
        }
        catch (FileNotFoundException e)
        {
            MessageBox box = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
            box.setMessage(e.getLocalizedMessage());
            box.open();
        }
        catch (IOException e)
        {
            MessageBox box = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
            box.setMessage(e.getLocalizedMessage());
            box.open();
        }

    }

    /**
     * @param bdoNotResolveEntities the bdoNotResolveEntities to set
     */
    public void setBdoNotResolveEntities(boolean bdoNotResolveEntities)
    {
        this.bdoNotResolveEntities = bdoNotResolveEntities;
    }

    /**
     * @param bIcCancel the bIcCancel to set
     */
    public void setBIcCancel(boolean bIcCancel)
    {
        this.bIcCancel = bIcCancel;
    }

    /**
     * @param segCRLF the bSegCRLF to set
     */
    public void setBSegCRLF(boolean segCRLF)
    {
        bSegCRLF = segCRLF;
    }

    /**
     * @param segType the bSegType to set
     */
    public void setBSegType(boolean segType)
    {
        bSegType = segType;
    }

    /**
     * @param documentProperties the documentProperties to set
     */
    public void setDocumentProperties(DocumentProperties documentProperties)
    {
        this.documentProperties = documentProperties;
    }
    
    /**
     * @param donotresolveentities the donotresolveentities to set
     */
    public void setDoNotResolveEntitiesFile(String doNotResolveEntitiesFile)
    {
        this.doNotResolveEntitiesFile = doNotResolveEntitiesFile;
    }

    /**
     * @param stNumber the stNumber to set
     */
    public void setStNumber(String stNumber)
    {
        this.stNumber = stNumber;
    }

    public void show()
    {
        shell.open();
        Display display = getParent().getDisplay();
        while (!shell.isDisposed())
        {
            if (!display.readAndDispatch())
            {
                display.sleep();
            }
        }
    }
    
    private boolean StringToBoolean(String b)
    {
        if (b.equalsIgnoreCase("true"))
            return true;
        else
            return false;
    }
}
