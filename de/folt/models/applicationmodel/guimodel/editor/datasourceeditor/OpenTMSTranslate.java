/*
 * Created on 15.02.2009
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
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.jdom.Element;

import com.araya.converters.Converters;
import com.araya.eaglememex.util.EMXConstants;
import com.araya.eaglememex.util.EMXProperties;
import com.araya.eaglememex.util.ErrorHandler;
import com.araya.eaglememex.util.FormatDetector;
import com.araya.eaglememex.util.LogPrint;
import com.araya.util.TextUtil;
import com.araya.utilities.DocumentProperties;

import de.folt.models.applicationmodel.guimodel.support.CancelTask;
import de.folt.models.applicationmodel.guimodel.support.ProgressDialog;
import de.folt.models.applicationmodel.guimodel.support.ProgressDialogSupport;
import de.folt.models.datamodel.DataSource;
import de.folt.models.datamodel.DataSourceConfigurations;
import de.folt.models.datamodel.DataSourceInstance;
import de.folt.models.datamodel.DataSourceProperties;
import de.folt.models.datamodel.sql.OpenTMSSQLDataSource;
import de.folt.models.documentmodel.document.XmlDocument;
import de.folt.models.documentmodel.xliff.XliffDocument;
import de.folt.util.CodePageHandling;
import de.folt.util.LanguageHandling;
import de.folt.util.OpenTMSProperties;
import de.folt.util.ZipArchive;

public class OpenTMSTranslate extends Dialog
{

    public class TranslateObserver implements Observer
    {

        private ProgressDialogSupport pdSupport;

        /**
         * @param pdSupport
         */
        public TranslateObserver(ProgressDialogSupport pdSupport)
        {
            super();
            this.pdSupport = pdSupport;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
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

    private Button alltargetsTranslate;

    private Button alltargetsTranslateTM;

    private Button alltransTranslate;

    private Button alltransTranslateTM;

    private boolean bNotResolveEntities;

    private String configfile = "";

    private Button conversionOptionDialog;

    private Button convertButton;

    @SuppressWarnings("unused")
    private Shell currentshell = null;

    private String curruser = "";

    private Button dataSourceTranslate;

    private Button dataSubSegmentSourceTranslate;

    private DocumentProperties documentProperties;

    private String donotresolveentities = "no";

    private String donotresolveentitiesfile;

    private de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.EditorConfiguration editorConfiguration = null;

    private Combo encCombo;

    private String encoding;

    private StyledText formatText;

    private Button googleTranslate = null;

    private Button iateTranslate = null;

    int iLogLevel = 0;

    private Combo langCombo;

    private String language;

    String logfilename = "";

    private Combo maxMatch;

    private de.folt.util.Messages message;

    private Combo minMatch;

    private String number = "1";

    private boolean segByCR;

    private boolean segByElem;

    private String separator;

    private Shell shell;

    private String sourceDocument;

    private StyledText sourceNameText;

    private Combo subtable;

    private Combo table;

    private Combo tgtLangCombo;

    private String tgtLanguage;

    private StyledText tmxFile;

    private String xliffDocument;

    /**
     * @return the xliffDocument
     */
    public String getXliffDocument()
    {
        return xliffDocument;
    }

    private StyledText xliffNameText;

    public OpenTMSTranslate()
    {
        super(null, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL | SWT.CLOSE);
    }

    @SuppressWarnings("unused")
	public OpenTMSTranslate(Shell parent, String location, String defaultDatabase, String document)
    {
        super(parent, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL | SWT.CLOSE);
        message = de.folt.util.Messages.getInstance("de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.datasourceeditor", "en");

        currentshell = parent;

        configfile = OpenTMSProperties.getInstance().getOpenTMSProperty("OpenTMS.DataSourceEditor.EditorConfigurationDirectory");
        String propfileName = OpenTMSProperties.getInstance().getOpenTMSProperty("ArayaPropertiesFile");
        EMXProperties.getInstance(propfileName);

        curruser = System.getProperty("user.name").toLowerCase();
        editorConfiguration = new de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.EditorConfiguration(shell, configfile, curruser);

        documentProperties = null;
        tgtLanguage = "";

        separator = System.getProperty("file.separator"); //$NON-NLS-1$
        rewriteResources(separator);

        shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.CLOSE);
        shell.setLayout(new GridLayout(1, false));
        shell.setText(message.getString("openTMSTranslate"));
        shell.getDisplay();

        String recentDocument = editorConfiguration.loadValueForKey("recentDocument");
        if ((document != null) && !document.equals(""))
            recentDocument = document;

        String recentXLF = editorConfiguration.loadValueForKey("recentXLF");
        if ((document != null) && !document.equals(""))
            recentXLF = document + ".xlf";
        String recentSourceLanguage = editorConfiguration.loadValueForKey("recentSourceLanguage");
        String recentTargetLanguage = editorConfiguration.loadValueForKey("recentTargetLanguage");

        String recentTMXDocument = editorConfiguration.loadValueForKey("recenttTMXFile");

        // boolean brecentSegment = StringToBoolean(editorConfiguration.loadValueForKey("recentSegment"));
        // boolean brecentCRLF = StringToBoolean(editorConfiguration.loadValueForKey("recentCRLF"));
        // String recentCRLFCount = editorConfiguration.loadValueForKey("recentCRLFCount");
        String recentMinMatch = editorConfiguration.loadValueForKey("recentMinMatch");
        String recentMaxMatch = editorConfiguration.loadValueForKey("recentMaxMatch");

        boolean btmxTranslateOpenTMS = StringToBoolean(editorConfiguration.loadValueForKey("tmxTranslateOpenTMS"));

        boolean bsubTranslateOpenTMS = StringToBoolean(editorConfiguration.loadValueForKey("subTranslateOpenTMS"));

        defaultDatabase = editorConfiguration.loadValueForKey("recentOpenTMSDatabase");
        if (defaultDatabase == null)
            defaultDatabase = "";

        String subdefaultDatabase = editorConfiguration.loadValueForKey("recentSubOpenTMSDatabase");
        if (subdefaultDatabase == null)
            subdefaultDatabase = "";

        if ((recentMinMatch == null) || recentMinMatch.equals(""))
            recentMinMatch = "80";
        if ((recentMaxMatch == null) || recentMaxMatch.equals(""))
            recentMaxMatch = "5";

        //
        // Files selection
        //

        Group filesComposite = new Group(shell, SWT.NONE);
        filesComposite.setLayout(new GridLayout(3, false));
        filesComposite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
        filesComposite.setText(message.getString("filesComposite"));

        Label sourceNameLabel = new Label(filesComposite, SWT.NONE);
        sourceNameLabel.setText(message.getString("Source_document"));

        sourceNameText = new StyledText(filesComposite, SWT.BORDER);
        sourceNameText.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));

        if (recentDocument != null)
            sourceNameText.setText(recentDocument);

        Button browseSource = new Button(filesComposite, SWT.PUSH);
        browseSource.setText(message.getString("Browse"));
        browseSource.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                FileDialog fd = new FileDialog(shell, SWT.OPEN);
                String extensions[] =
                    {
                        "*.*"
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
                    sourceNameText.setText(fd.getFilterPath() + separator + fd.getFileName());
                    fd = null;
                    // if (xliffNameText.getText().length() == 0)
                    {
                        xliffNameText.setText(sourceNameText.getText() + ".xlf");
                    }
                }
                SetFormatInfo();
            }
        });

        Label xliffNameLabel = new Label(filesComposite, SWT.NONE);
        xliffNameLabel.setText(message.getString("XLIFF_file"));

        xliffNameText = new StyledText(filesComposite, SWT.BORDER);
        xliffNameText.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
        if (recentXLF != null)
            xliffNameText.setText(recentXLF);
        Button browseXliff = new Button(filesComposite, SWT.PUSH);
        browseXliff.setText(message.getString("Browse"));
        browseXliff.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                FileDialog fd = new FileDialog(shell, SWT.SAVE);
                String extensions[] =
                    {
                            "*.xlf", "*.xml", "*.*"
                    };
                fd.setFilterExtensions(extensions);
                if (xliffNameText.getText().length() > 0)
                {
                    fd.setFileName(xliffNameText.getText());
                }
                else
                {
                    if (sourceNameText.getText().length() > 0)
                    {
                        fd.setFileName(sourceNameText.getText() + ".xlf");
                    }
                }
                fd.open();
                if (fd.getFileName() == "") { //$NON-NLS-1$
                    fd = null;
                    extensions = null;
                    return;
                }
                else
                {
                    xliffNameText.setText(fd.getFilterPath() + separator + fd.getFileName());
                    langCombo.setEnabled(true);
                    tgtLangCombo.setEnabled(true);
                    fd = null;
                }
            }
        });

        Group formatComposite = new Group(shell, SWT.NONE);
        formatComposite.setLayout(new GridLayout(2, false));
        formatComposite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
        formatComposite.setText(message.getString("formatComposite"));

        Button checkFormat = new Button(formatComposite, SWT.PUSH);
        checkFormat.setText(message.getString("CheckFormat"));
        checkFormat.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                SetFormatInfo();
            }
        });

        formatText = new StyledText(formatComposite, SWT.BORDER | SWT.READ_ONLY);
        formatText.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
        formatText.setEditable(false);
        formatText.setEnabled(false);

        //
        // Source document properties
        //

        Group langComposite = new Group(shell, SWT.NONE);
        langComposite.setText(message.getString("Source_Document_Properties"));
        langComposite.setLayout(new GridLayout(4, false));
        langComposite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));

        Label langLabel = new Label(langComposite, SWT.NONE);
        langLabel.setText(message.getString("Language"));

        String slangnames[] = LanguageHandling.getCombinedLanguages();

        langCombo = new Combo(langComposite, SWT.DROP_DOWN);
        langCombo.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                language = LanguageHandling.getShortLanguageCodeFromCombinedTable(langCombo.getText());
            }
        });
        langCombo.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));

        langCombo.setItems(slangnames);

        int ipos2 = 0;

        // recentSourceLanguage
        boolean bPosChanged = false;
        for (int i = 0; i < slangnames.length; i++)
        {
            if (slangnames[i].startsWith(recentSourceLanguage))
            {
                ipos2 = i;
                bPosChanged = true;
                break;
            }
        }

        if (bPosChanged == false)
        {

            for (int i = 0; i < slangnames.length; i++)
            {
                if (slangnames[i].startsWith("de"))
                {
                    ipos2 = i;
                    break;
                }
            }
        }

        if (langCombo.getItemCount() > 0)
        {
            langCombo.select(ipos2);
            language = LanguageHandling.getShortLanguageCodeFromCombinedTable(langCombo.getText());
        }

        Label encLabel = new Label(langComposite, SWT.NONE);
        encLabel.setText(message.getString("Code_Page"));

        encCombo = new Combo(langComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        encCombo.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                encoding = encCombo.getText();
            }
        });
        encCombo.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
        String scodepage[] = CodePageHandling.getCodePages();
        encCombo.setItems(scodepage);

        ipos2 = 0;
        for (int i = 0; i < scodepage.length; i++)
        {
            if (scodepage[i].startsWith("UTF-8"))
            {
                ipos2 = i;
                break;
            }
        }

        if (encCombo.getItemCount() > 0)
            encCombo.select(ipos2);

        //
        // Conversion options
        //

        segByElem = StringToBoolean(editorConfiguration.loadValueForKey("recentSegment"));
        segByCR = StringToBoolean(editorConfiguration.loadValueForKey("recentCRLF"));
        number = editorConfiguration.loadValueForKey("recentCRLFCount");

        bNotResolveEntities = StringToBoolean(editorConfiguration.loadValueForKey("doNotResolveEntities"));
        donotresolveentitiesfile = editorConfiguration.loadValueForKey("donotresolveentitiesfile");

        conversionOptionDialog = new Button(shell, SWT.PUSH);
        conversionOptionDialog.setText(message.getString("Conversion_options"));
        conversionOptionDialog.setToolTipText(segByCR + " " + segByElem + " " + number + " " + donotresolveentities + " " + donotresolveentitiesfile);

        conversionOptionDialog.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        conversionOptionDialog.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                String file = sourceNameText.getText();

                ConversionOptions conversionOptions = new ConversionOptions(shell, "", file);
                boolean bIsCandel = conversionOptions.open();
                if (bIsCandel == false)
                {
                    documentProperties = conversionOptions.getDocumentProperties();
                    donotresolveentitiesfile = conversionOptions.getDoNotResolveEntitiesFile();
                    number = conversionOptions.getStNumber();

                    bNotResolveEntities = conversionOptions.isBdoNotResolveEntities();
                    if (conversionOptions.isBdoNotResolveEntities())
                        donotresolveentities = "no";
                    else
                        donotresolveentities = "yes";
                    segByCR = conversionOptions.isBSegCRLF();
                    segByElem = conversionOptions.isBSegType();
                    conversionOptionDialog.setToolTipText(segByCR + " " + segByElem + " " + number + " " + donotresolveentities + " " + donotresolveentitiesfile);
                }
            }
        });

        //
        // Translation options
        //

        Group tgtComposite = new Group(shell, SWT.NONE);
        tgtComposite.setText(message.getString("Translation_Options"));
        tgtComposite.setLayout(new GridLayout(4, false));
        tgtComposite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));

        GridData layoutData;

        Group dataTMComposite = new Group(tgtComposite, SWT.NONE);
        dataTMComposite.setLayout(new GridLayout(4, false));
        GridData dataTMCompositegd = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
        dataTMCompositegd.horizontalSpan = 4;
        dataTMComposite.setLayoutData(dataTMCompositegd);
        dataTMComposite.setText(message.getString("tmTranslation"));

        Label prompt = new Label(dataTMComposite, SWT.NONE);
        GridData layoutDataTmx = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
        layoutDataTmx.horizontalSpan = 2;
        prompt.setLayoutData(layoutDataTmx);
        prompt.setText(message.getString("TMX_File_for_translation"));

        tmxFile = new StyledText(dataTMComposite, SWT.BORDER);
        layoutDataTmx = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
        layoutDataTmx.horizontalSpan = 3;
        tmxFile.setLayoutData(layoutDataTmx);

        if (recentTMXDocument != null)
            tmxFile.setText(recentTMXDocument);

        Button browsetmxFile = new Button(dataTMComposite, SWT.PUSH);
        browsetmxFile.setText(message.getString("Browse"));
        browsetmxFile.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                FileDialog fd = new FileDialog(shell, SWT.OPEN);
                String extensions[] =
                    {
                        "*.tmx,*.tmx;*.tmx;*.xlf;*.*.*"
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
                    tmxFile.setText(fd.getFilterPath() + separator + fd.getFileName());
                    fd = null;
                    // if (xliffNameText.getText().length() == 0)
                    {
                        xliffNameText.setText(sourceNameText.getText() + ".xlf");
                    }
                }
                SetFormatInfo();
            }
        });

        Label promptdbs = new Label(dataTMComposite, SWT.NONE);
        GridData layoutDataTmxPrompt = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        layoutDataTmxPrompt.horizontalSpan = 2;
        promptdbs.setLayoutData(layoutDataTmxPrompt);
        promptdbs.setText(message.getString("OpenTMS_database_list_for_TM"));

        dataSourceTranslate = new Button(dataTMComposite, SWT.CHECK);
        dataSourceTranslate.setEnabled(true);
        GridData layoutDataDSTranslate = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
        layoutDataDSTranslate.horizontalSpan = 2;
        dataSourceTranslate.setLayoutData(layoutDataDSTranslate);

        if (btmxTranslateOpenTMS)
            dataSourceTranslate.setSelection(btmxTranslateOpenTMS);

        table = new Combo(dataTMComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        data.horizontalSpan = 4;
        table.setLayoutData(data);

        Vector<String> tmxDatabases = getOpenTMSDatabases();

        if (tmxDatabases != null)
        {
            int size = tmxDatabases.size();
            if (size > 0)
            {
                int ipos = 0;
                for (int i = 0; i < size; i++)
                {
                    String name = (String) tmxDatabases.get(i);
                    if (name.equals(defaultDatabase))
                        ipos = i;
                    table.add(name);
                }

                if ((size >= 0))
                    table.select(ipos);
            }
        }
        else
        {
            table.setEnabled(false);
            dataSourceTranslate.setEnabled(false);
        }

        if (dataSourceTranslate.getSelection())
            table.setEnabled(true);
        else
            table.setEnabled(false);

        dataSourceTranslate.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                table.setEnabled(dataSourceTranslate.getSelection());
            }
        });

        Label alltargetsTM = new Label(dataTMComposite, SWT.NONE);
        GridData alltargetsdTM = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        alltargetsdTM.horizontalSpan = 1;
        alltargetsTM.setLayoutData(alltargetsdTM);
        alltargetsTM.setText(message.getString("useAllTargets"));

        alltargetsTranslateTM = new Button(dataTMComposite, SWT.CHECK);
        alltargetsTranslateTM.setEnabled(true);
        GridData alltargetsdTMAT = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        alltargetsdTMAT.horizontalSpan = 1;
        alltargetsTranslateTM.setLayoutData(alltargetsdTMAT);

        Label alltransTM = new Label(dataTMComposite, SWT.NONE);
        GridData alltransdtm = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        alltransdtm.horizontalSpan = 1;
        alltransTM.setLayoutData(alltransdtm);
        alltransTM.setText(message.getString("useAllTrans"));

        alltransTranslateTM = new Button(dataTMComposite, SWT.CHECK);
        alltransTranslateTM.setEnabled(true);
        GridData alltargetsdTMATAL = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        alltargetsdTMATAL.horizontalSpan = 1;
        alltransTranslateTM.setLayoutData(alltargetsdTMATAL);

        Label minMatchLabel = new Label(dataTMComposite, SWT.NONE);
        minMatchLabel.setText(message.getString("Minimum_Match_Percentage"));
        layoutData = new GridData();
        layoutData.horizontalSpan = 1;
        minMatchLabel.setLayoutData(layoutData);

        minMatch = new Combo(dataTMComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        minMatch.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
        layoutData = new GridData();
        layoutData.horizontalSpan = 1;
        minMatch.setLayoutData(layoutData);
        String[] minData =
            {
                    "100", "95", "90", "85", "80", "75", "70", "65", "60", "55", "50", "45", "40", "35", "30"
            };
        minMatch.setItems(minData);
        if ((recentMinMatch != null) || !recentMinMatch.equals(""))
            minMatch.setText(recentMinMatch);
        else
            minMatch.setText("70");

        Label maxMatchLabel = new Label(dataTMComposite, SWT.CENTER);
        maxMatchLabel.setText(message.getString("Maximum_Match_Count"));
        layoutData = new GridData();
        layoutData.horizontalSpan = 1;
        maxMatchLabel.setLayoutData(layoutData);

        maxMatch = new Combo(dataTMComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        maxMatch.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
        layoutData = new GridData();
        layoutData.horizontalSpan = 1;
        maxMatch.setLayoutData(layoutData);
        String[] maxData =
            {
                    "-1", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"
            };
        maxMatch.setItems(maxData);

        if ((recentMaxMatch != null) || !recentMaxMatch.equals(""))
            maxMatch.setText(recentMaxMatch);
        else
            maxMatch.setText("5");

        Group dataSubComposite = new Group(tgtComposite, SWT.NONE);
        dataSubComposite.setLayout(new GridLayout(4, false));
        GridData dataSubCompositegd = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
        dataSubCompositegd.horizontalSpan = 4;
        dataSubComposite.setLayoutData(dataSubCompositegd);
        dataSubComposite.setText(message.getString("subsegmentTranslation"));

        Label promptsubdbs = new Label(dataSubComposite, SWT.NONE);
        GridData layoutDataTmxSub = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        layoutDataTmxSub.horizontalSpan = 2;
        promptsubdbs.setLayoutData(layoutDataTmxSub);
        promptsubdbs.setText(message.getString("OpenTMS_database_list_for_Subsegment"));

        dataSubSegmentSourceTranslate = new Button(dataSubComposite, SWT.CHECK);
        dataSubSegmentSourceTranslate.setEnabled(true);
        GridData layoutData1 = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
        layoutData1.horizontalSpan = 2;
        dataSubSegmentSourceTranslate.setLayoutData(layoutData1);

        if (bsubTranslateOpenTMS)
            dataSubSegmentSourceTranslate.setSelection(bsubTranslateOpenTMS);

        subtable = new Combo(dataSubComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        GridData datasub = new GridData(GridData.FILL_HORIZONTAL);
        datasub.horizontalSpan = 4;
        subtable.setLayoutData(datasub);

        if (tmxDatabases != null)
        {
            int size = tmxDatabases.size();
            if (size > 0)
            {
                int ipos = 0;
                for (int i = 0; i < size; i++)
                {
                    String name = (String) tmxDatabases.get(i);
                    if (name.equals(subdefaultDatabase))
                        ipos = i;
                    subtable.add(name);
                }

                if ((size >= 0))
                    subtable.select(ipos);
            }
        }
        else
        {
            subtable.setEnabled(false);
            dataSubSegmentSourceTranslate.setEnabled(false);
        }

        if (dataSubSegmentSourceTranslate.getSelection())
            subtable.setEnabled(true);
        else
            subtable.setEnabled(false);

        dataSubSegmentSourceTranslate.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                subtable.setEnabled(dataSubSegmentSourceTranslate.getSelection());
            }
        });

        Label alltargets = new Label(dataSubComposite, SWT.NONE);
        GridData alltargetsd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        alltargetsd.horizontalSpan = 1;
        alltargets.setLayoutData(alltargetsd);
        alltargets.setText(message.getString("useAllTargets"));

        alltargetsTranslate = new Button(dataSubComposite, SWT.CHECK);
        alltargetsTranslate.setEnabled(true);
        GridData layoutDataST = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        layoutDataST.horizontalSpan = 1;
        alltargetsTranslate.setLayoutData(layoutDataST);

        Label alltrans = new Label(dataSubComposite, SWT.NONE);
        GridData alltransd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        alltransd.horizontalSpan = 1;
        alltrans.setLayoutData(alltransd);
        alltrans.setText(message.getString("useAllTrans"));

        alltransTranslate = new Button(dataSubComposite, SWT.CHECK);
        alltransTranslate.setEnabled(true);
        GridData layoutDatAAT = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        layoutDatAAT.horizontalSpan = 1;
        alltransTranslate.setLayoutData(layoutDatAAT);

        String gt = de.folt.util.OpenTMSProperties.getInstance().getOpenTMSProperty("Google.Translate");
        boolean bgt = false;
        try
        {
            bgt = Boolean.parseBoolean(gt);
        }
        catch (Exception e1)
        {
        }

        if (bgt == true)
        {
            Group machineComposite = new Group(tgtComposite, SWT.NONE);
            machineComposite.setLayout(new GridLayout(4, false));
            GridData machinegd = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
            machinegd.horizontalSpan = 4;
            machineComposite.setLayoutData(machinegd);
            machineComposite.setText(message.getString("machineTranslation"));

            Label google = new Label(machineComposite, SWT.NONE);
            GridData layoutDataGoogle = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
            layoutDataGoogle.horizontalSpan = 2;
            google.setLayoutData(layoutDataGoogle);
            google.setText(message.getString("GoogleTranslate"));

            googleTranslate = new Button(machineComposite, SWT.CHECK);
            googleTranslate.setEnabled(true);
            GridData layoutData3 = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_BEGINNING);
            layoutData3.horizontalSpan = 1;
            googleTranslate.setLayoutData(layoutData3);

            googleTranslate.setEnabled(bgt);
        }

        gt = de.folt.util.OpenTMSProperties.getInstance().getOpenTMSProperty("Iate.Translate");
        bgt = false;
        try
        {
            bgt = Boolean.parseBoolean(gt);
        }
        catch (Exception e1)
        {
        }

        if (bgt == true)
        {
            Label iate = new Label(tgtComposite, SWT.NONE);
            GridData iategd = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
            iategd.horizontalSpan = 1;
            iate.setLayoutData(layoutDataTmx);
            iate.setText(message.getString("IateTranslate"));

            iateTranslate = new Button(tgtComposite, SWT.CHECK);
            iateTranslate.setEnabled(true);
            layoutData = new GridData();
            layoutData.horizontalSpan = 1;
            iateTranslate.setLayoutData(layoutData);

            iateTranslate.setEnabled(bgt);
        }

        Label tgtLabel = new Label(tgtComposite, SWT.NONE);
        tgtLabel.setText(message.getString("Target_Language_1"));
        layoutData = new GridData();
        layoutData.horizontalSpan = 2;
        tgtLabel.setLayoutData(layoutData);

        tgtLangCombo = new Combo(tgtComposite, SWT.DROP_DOWN); //  | SWT.READ_ONLY);
        tgtLangCombo.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                tgtLanguage = LanguageHandling.getShortLanguageCodeFromCombinedTable(tgtLangCombo.getText());
            }
        });
        tgtLangCombo.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL));
        layoutData = new GridData();
        layoutData.horizontalSpan = 2;
        tgtLangCombo.setItems(slangnames);
        tgtLangCombo.setLayoutData(layoutData);
        int ipos1 = 0;
        for (int i = 0; i < slangnames.length; i++)
        {
            // if (tlangnames[i].startsWith("en")) // recentTargetLanguage
            if (slangnames[i].startsWith(recentTargetLanguage))
            {
                ipos1 = i;
                break;
            }
        }
        if (tgtLangCombo.getItemCount() > 0)
        {
            tgtLangCombo.select(ipos1);
            tgtLangCombo.setEnabled(true);
            tgtLanguage = LanguageHandling.getShortLanguageCodeFromCombinedTable(tgtLangCombo.getText());
        }

        Composite bottom = new Composite(shell, SWT.BORDER);
        bottom.setLayout(new GridLayout(2, true));
        bottom.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));

        convertButton = new Button(bottom, SWT.PUSH);
        convertButton.setText(message.getString("Convert_to_xliff"));
        convertButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        convertButton.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                sourceDocument = sourceNameText.getText();
                xliffDocument = xliffNameText.getText();
                language = TextUtil.getLanguageCode(langCombo.getText());
                encoding = encCombo.getText();
                if (sourceDocument.equals(""))
                {
                    MessageBox box = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
                    box.setMessage(message.getString("Please_select_a_document_for_conversion"));
                    box.open();
                    return;
                }
                if (xliffDocument.equals(""))
                {
                    MessageBox box = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
                    box.setMessage(message.getString("Please_select_a_name_for_converted_document"));
                    box.open();
                    return;
                }
                if (sourceDocument.equals(xliffDocument))
                {
                    MessageBox box = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
                    box.setMessage(message.getString("Please_select_a_different_name_for_converted_document"));
                    box.open();
                    return;
                }
                if (language.equals(""))
                {
                    MessageBox box = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
                    box.setMessage(message.getString("Please_select_document_language"));
                    box.open();
                    return;
                }
                if (encoding.equals(""))
                {
                    MessageBox box = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
                    box.setMessage(message.getString("Please_select_document_encoding"));
                    box.open();
                    return;
                }

                editorConfiguration.saveKeyValuePair("recenttTMXFile", tmxFile.getText());
                editorConfiguration.saveKeyValuePair("recentDocument", sourceNameText.getText());
                editorConfiguration.saveKeyValuePair("recentXLF", xliffDocument);
                editorConfiguration.saveKeyValuePair("recentSourceLanguage", langCombo.getText());
                editorConfiguration.saveKeyValuePair("recentTargetLanguage", tgtLangCombo.getText());
                editorConfiguration.saveKeyValuePair("recenttCodePage", encCombo.getText());
                editorConfiguration.saveKeyValuePair("recentSegment", BooleanToString(segByElem));
                editorConfiguration.saveKeyValuePair("recentCRLF", BooleanToString(segByCR));
                editorConfiguration.saveKeyValuePair("recentCRLFCount", number);
                editorConfiguration.saveKeyValuePair("recentMinMatch", minMatch.getText());
                editorConfiguration.saveKeyValuePair("recentMaxMatch", maxMatch.getText());
                editorConfiguration.saveKeyValuePair("doNotResolveEntities", BooleanToString(bNotResolveEntities));
                editorConfiguration.saveKeyValuePair("donotresolveentitiesfile", donotresolveentitiesfile);
                editorConfiguration.saveKeyValuePair("tmxTranslateOpenTMS", dataSourceTranslate.getSelection() + "");
                editorConfiguration.saveKeyValuePair("recentOpenTMSDatabase", table.getText() + "");
                editorConfiguration.saveKeyValuePair("recentSubOpenTMSDatabase", subtable.getText() + "");
                editorConfiguration.saveKeyValuePair("subTranslateOpenTMS", dataSubSegmentSourceTranslate.getSelection() + "");

                // Cursor cursor = new Cursor(display, SWT.CURSOR_WAIT);
                // shell.setCursor(cursor);
                convert();
                // cursor = new Cursor(display, SWT.CURSOR_ARROW);
                // shell.setCursor(cursor);
            }
        });

        Button close = new Button(bottom, SWT.PUSH);
        close.setText(message.getString("Cl&ose"));
        close.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        close.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                shell.close();
            }
        });

        langCombo.setEnabled(true);
        tgtLangCombo.setEnabled(true);

        if ((sourceNameText.getText() != null) && !sourceNameText.getText().equals(""))
            SetFormatInfo();

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
     * convert
     * 
     * @param filename
     */
    private void convert()
    {
        try
        {
            boolean bIsArchive = ZipArchive.isArchive(sourceDocument);
            if (bIsArchive)
            {
                LogPrint.println("Zip File detected: " + sourceDocument);
                // we must check for open office now!

                boolean bUseExtensions = false;
                FormatDetector fmDetect = new FormatDetector(bUseExtensions);
                String format = fmDetect.detection(sourceDocument);

                if (!(format.equals("OpenOffice") || format.equals("WinWord")))
                {
                    Vector<String> fileNames = ZipArchive.getArchiveFileNames(sourceDocument);
                    if (fileNames.size() > 0)
                    {
                        String destdirname = EMXProperties.getInstance().getEMXProperty("eaglememex.tmp.dir");
                        ZipArchive.extractArchive(sourceDocument, destdirname);
                        for (int j = 0; j < fileNames.size(); j++)
                        {
                            String filename = destdirname + fileNames.get(j);
                            File fi = new File(filename);
                            if (fi.isDirectory())
                                continue;
                            if (!fi.exists())
                                continue;
                            if (fi.length() <= 0)
                                continue;

                            LogPrint.println("Extracted " + j + " " + fileNames.get(j) + "(" + filename + ") ->" + destdirname + fileNames.get(j));
                            convert(filename, filename + ".xlf");
                            xliffDocument = filename + ".xlf";
                        }
                        MessageBox mbox = null;
                        mbox = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
                        mbox.setMessage(message.getString("Conversion_completed"));
                        mbox.open();
                    }
                }
                else
                {
                    convert(sourceDocument, xliffDocument);
                    MessageBox mbox = null;
                    mbox = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
                    mbox.setMessage(message.getString("Conversion_completed"));
                    mbox.open();
                }
            }
            else
            {
                convert(sourceDocument, xliffDocument);
                MessageBox mbox = null;
                mbox = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
                mbox.setMessage(message.getString("Conversion_completed"));
                mbox.open();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * convert
     * 
     */
    @SuppressWarnings(
        {
                "deprecation"
        })
    private void convert(String sourceDocumentLocal, String xliffDocumentLocal)
    {

        File file = new File(sourceDocumentLocal);
        if (!file.exists())
            return;

        // setLog();
        File log = null;
        logfilename = de.folt.util.OpenTMSLogger.returnLogFile();
        if ((logfilename == null) || (logfilename.equals("")))
        {
            try
            {
                log = File.createTempFile("temp", ".log", new File("log"));
                logfilename = log.getAbsolutePath();
                // log.deleteOnExit();
            }
            catch (IOException e1)
            {
                MessageBox box = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
                box.setMessage(e1.getLocalizedMessage());
                box.open();
                return;
            }
        }

        Hashtable<String, String> h = new Hashtable<String, String>();
        h.put("message", "TranslateDocument");
        h.put("sourcefile", sourceDocumentLocal);

        // Conversion data
        h.put("transfile", xliffDocumentLocal);
        h.put("xlifffile", xliffDocumentLocal + ".temp");
        h.put("segfile", xliffDocumentLocal);
        h.put("phase", "CONVSEG");
        h.put("replclass", "no");
        h.put("matchquality", "40");
        h.put("matchmaximum", "5n");
        h.put("sklfile", sourceDocumentLocal + ".skl");
        h.put("backfile", "");
        h.put("sourcelang", language);
        h.put("targetlang", tgtLanguage);
        h.put("sourcecharset", encoding);
        h.put("targetcharset", "UTF-8");

        h.put("donotresolveentities", donotresolveentities); // 08.02.2005
        h.put("donotresolveentitiesfile", donotresolveentitiesfile);// 11.02.2005

        if (segByCR == true)
        {
            String n = number;
            if (n.equals("1"))
            {
                h.put("breakoncrlf", "yes");
            }
            else
            {
                try
                {
                    Integer i = new Integer(n);
                    h.put("breakoncrlf", i.intValue() + "n");
                }
                catch (java.lang.NumberFormatException nfe)
                {
                    MessageBox box = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
                    box.setMessage(("Invalid_number"));
                    box.open();
                    h.put("breakoncrlf", "yes");
                }
            }
            n = null;
        }
        else
        {
            h.put("breakoncrlf", "no");
        }

        // wk 26.03.2005
        boolean bTranslateAtt = true;
        if (bTranslateAtt)
            h.put("useTranslateAttribute", "yes");
        else
            h.put("useTranslateAttribute", "no");
        // end wk

        if (segByElem == true)
        {
            h.put("paraseg", "yes");
        }
        else
        {
            h.put("paraseg", "no");
        }

        h.put("logfile", logfilename);
        // h.put("logfile", logfilename);

        h.put("useextensions", "yes");

        ProgressDialog progressDialog = new ProgressDialog(shell, message.getString("Convert_to_xliff"), message.getString("Convert_Process"), ProgressDialog.SINGLE_BAR);
        progressDialog.open();

        // Convert and section
        try
        {
            @SuppressWarnings("rawtypes")
			Vector resultConvert = Converters.run(h);

            String resultcode = "error";
            if (resultConvert.size() > 0)
                resultcode = (String) resultConvert.get(0);
            else
                LogPrint.println("Converter error occured");
            if (resultcode.equals("0"))
            {
                if (tmxFile.getText().equals("") && (dataSourceTranslate.getSelection() == false) && (dataSubSegmentSourceTranslate.getSelection() == false)
                        && ((googleTranslate != null) && (googleTranslate.getSelection() == false)) && ((iateTranslate != null) && (iateTranslate.getSelection() == false)))
                {
                    progressDialog.close();
                    return;
                }

                progressDialog.updateProgressMessage(message.getString("DataSourceTranslation"));

                ProgressDialogSupport pdSupport = null;
                pdSupport = new ProgressDialogSupport(progressDialog);
                CancelTask cancel = null;
                Thread cancelthread = null;
                if (pdSupport != null)
                {
                    pdSupport.updateProgressIndication("TMX_File_for_translation");
                    cancel = new CancelTask(pdSupport.returnShell(), "Cancel_TM_Translation", "caTM_Translation");
                    cancelthread = new Thread(cancel);
                    cancelthread.run();
                }

                pdSupport.updateProgressIndication(1, 2);

                de.folt.models.documentmodel.xliff.XliffDocument doc = new de.folt.models.documentmodel.xliff.XliffDocument();
                doc.loadXmlFile(xliffDocumentLocal);

                String simstring = minMatch.getText();
                int sim = 80;
                try
                {
                    sim = Integer.parseInt(simstring);
                }
                catch (Exception ex)
                {

                }

                try
                {
                    DataSourceProperties model = new DataSourceProperties();
                    Hashtable<String, Object> translationParameters = new Hashtable<String, Object>();
                    // check the type of the data source
                    String dataSourceFileName = tmxFile.getText();
                    if ((dataSourceFileName != null) && !dataSourceFileName.equals(""))
                    {
                        XmlDocument docSource = new XmlDocument();
                        docSource.loadXmlFile(dataSourceFileName);
                        String type = docSource.getRootElementName();
                        docSource = null;

                        String typedoc = "TMX:";
                        if (type.equals("xliff"))
                        {
                            model.put("dataModelClass", "de.folt.models.datamodel.xlifffile.XliffFileDataSource");
                            model.put("xlifffile", tmxFile.getText());
                            typedoc = "XLIFF:";
                        }
                        if (type.equals("tbx"))
                        {
                            model.put("dataModelClass", "de.folt.models.datamodel.tbxfile.TbxFileDataSource");
                            model.put("tbxfile", tmxFile.getText());
                            typedoc = "XLIFF:";
                        }
                        else
                        {
                            model.put("dataModelClass", "de.folt.models.datamodel.tmxfile.TmxFileDataSource");
                            model.put("tmxfile", tmxFile.getText());
                        }

                        LogPrint.println("Run " + typedoc + " ---> " + dataSourceFileName);

                        DataSource datasource = DataSourceInstance.createInstance(typedoc + dataSourceFileName, model);
                        TranslateObserver transObserver = this.new TranslateObserver(pdSupport);
                        doc.addObserver(transObserver);
                        doc.translate(datasource, language, tgtLanguage, sim, -1, translationParameters);
                        doc.saveToXmlFile();
                    }
                    if ((googleTranslate != null) && (googleTranslate.getSelection() == true))
                    {
                        model.put("dataModelClass", "de.folt.models.datamodel.googletranslate.GoogleTranslate");

                        DataSource datasource = DataSourceInstance.createInstance("GoogleTranslate:translate", model);

                        doc.translate(datasource, language, tgtLanguage, 100, -1, translationParameters);
                        doc.saveToXmlFile();
                    }
                    if ((iateTranslate != null) && (iateTranslate.getSelection() == true))
                    {
                        model.put("dataModelClass", "de.folt.models.datamodel.iate.IateTerminology");

                        DataSource datasource = DataSourceInstance.createInstance("IateTranslate:translate", model);

                        doc.translate(datasource, language, tgtLanguage, 100, -1, translationParameters);
                        doc.saveToXmlFile();
                    }
                    if (dataSourceTranslate.getSelection() == true)
                    {
                        model.put("dataModelClass", "de.folt.models.datamodel.sql.OpenTMSSQLDataSource");
                        OpenTMSSQLDataSource sqldatasource = new OpenTMSSQLDataSource();
                        String dataSourceConfigurationsFile = sqldatasource.getDefaultDataSourceConfigurationsFileName();
                        model.put("dataSourceConfigurationsFile", dataSourceConfigurationsFile);
                        DataSourceConfigurations dataSourceConfiguration = new DataSourceConfigurations(dataSourceConfigurationsFile);
                        String datasourcename = table.getText();
                        // get the model for the datasourcename
                        String dataModelClass = dataSourceConfiguration.getDataSourceType(datasourcename);
                        model.put("dataModelClass", dataModelClass);
                        model.put("database", datasourcename);
                        model.put("dataSourceName", datasourcename);
                        LogPrint.println("Run " + dataModelClass + " ---> " + datasourcename);
                        model.put("sourceLanguage", language);
                        model.put("targetLanguage", tgtLanguage);
                        model.put("similarity", sim);
                        model.put("loadAllTargets", alltargetsTranslateTM.getSelection() + "");
                        model.put("loadAltTrans", alltransTranslateTM.getSelection() + "");
                        DataSource datasqlsource = DataSourceInstance.createInstance(dataModelClass + ":" + datasourcename, model);
                        TranslateObserver transObserver = new TranslateObserver(pdSupport);
                        doc.addObserver(transObserver);
                        doc.translate(datasqlsource, language, tgtLanguage, sim, -1, translationParameters);
                        doc.saveToXmlFile();
                    }

                    if (dataSubSegmentSourceTranslate.getSelection() == true)
                    {
                        model.put("dataModelClass", "de.folt.models.datamodel.sql.OpenTMSSQLDataSource");
                        OpenTMSSQLDataSource sqldatasource = new OpenTMSSQLDataSource();
                        String dataSourceConfigurationsFile = sqldatasource.getDefaultDataSourceConfigurationsFileName();
                        model.put("dataSourceConfigurationsFile", dataSourceConfigurationsFile);
                        DataSourceConfigurations dataSourceConfiguration = new DataSourceConfigurations(dataSourceConfigurationsFile);
                        String datasourcename = subtable.getText();
                        // get the model for the datasource name
                        String dataModelClass = dataSourceConfiguration.getDataSourceType(datasourcename);
                        model.put("dataModelClass", dataModelClass);
                        model.put("database", datasourcename);
                        model.put("dataSourceName", datasourcename);
                        LogPrint.println("Run " + dataModelClass + " ---> " + datasourcename);
                        model.put("sourceLanguage", language);
                        model.put("targetLanguage", tgtLanguage);

                        model.put("loadAllTargets", alltargetsTranslate.getSelection() + "");
                        model.put("loadAltTrans", alltransTranslate.getSelection() + "");

                        DataSource datasqlsource = DataSourceInstance.createInstance(dataModelClass + ":" + datasourcename, model);
                        TranslateObserver transObserver = new TranslateObserver(pdSupport);
                        doc.addObserver(transObserver);
                        doc.subSegmentTranslate(datasqlsource, language, tgtLanguage, translationParameters);
                        doc.saveToXmlFile();
                    }

                    @SuppressWarnings("unused")
                    ErrorHandler err = new ErrorHandler(EMXConstants.ID_TM);
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                    progressDialog.close();
                    pdSupport = null;
                    MessageBox box = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
                    box.setMessage(message.getString("Error_Translating_Document") + " " + xliffDocumentLocal);
                    box.open();
                    return;
                }
                if (pdSupport != null)
                {
                    if (!cancel.isCancelled())
                        cancel.close();
                    if (cancelthread.isAlive())
                        cancelthread.stop();
                    pdSupport = null;
                }
                pdSupport = null;
            }

            if ("0".equals((String) resultConvert.get(0)))
            {
                if (documentProperties != null)
                {
                    DocumentProperties.addXLFDocumentProperties(xliffDocumentLocal, documentProperties);
                }
                // Conversion OK, no PT
                progressDialog.close();

            }
            else
            {
                progressDialog.close();
                DisplayLog disp = new DisplayLog(shell, logfilename);
                disp.setTitle(message.getString("Error_Converting_Document"));
                disp.show();
                disp = null;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            progressDialog.close();
            DisplayLog disp = new DisplayLog(shell, logfilename);
            disp.setTitle(message.getString("Error_Converting_Document"));
            disp.show();
            disp = null;
        }
    }

    private Vector<String> getOpenTMSDatabases()
    {
        Vector<String> vec = new Vector<String>();
        try
        {
            // just for the configuration
            String configfile = de.folt.util.OpenTMSProperties.getInstance().getOpenTMSProperty("OpenTMS.OpenTMSDataSourceConfigFile");
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

    public void SetFormatInfo()
    {
        String filename = sourceNameText.getText();
        if (!filename.equals(""))
        {
            File f = new File(filename);
            if (f.exists())
            {
                FormatDetector formatDect = new FormatDetector(true);
                String myencoding = null;
                LogPrint.println("formatDect: Use encoding \"" + myencoding + "\"");
                formatDect.setEncoding(myencoding); // previous encoding
                String args[] = new String[1];
                args[0] = filename;
                @SuppressWarnings("rawtypes")
				Vector vector = formatDect.run(args);
                if (vector.size() >= 4)
                {
                    int formatCode = Integer.parseInt((String) vector.get(0));
                    String formatName = (String) vector.get(3);
                    myencoding = formatDect.getEncoding();
                    if (vector.size() >= 6)
                        myencoding = (String) vector.get(5);
                    formatText.setText(formatCode + ": " + formatName);
                    if (myencoding != null)
                        encCombo.setText(myencoding);
                    langCombo.setEnabled(true);
                    tgtLangCombo.setEnabled(true);
                    if ((formatName.indexOf("XLIFF") > -1) || (formatName.indexOf("xliff") > -1))
                    {
                        // open xliff file
                        XliffDocument xliff = new XliffDocument();
                        xliff.loadXmlFile(filename);
                        List<Element> files = xliff.getFiles();
                        if (files.size() > 0)
                        {
                            String sourceLanguage = xliff.getFileSourceLanguage(files.get(0));
                            String targetLanguage = xliff.getFileTargetLanguage(files.get(0));
                            if (sourceLanguage != null)
                            {
                                if (LanguageHandling.getCombinedLanguageCodeFromShortLanguageCode(sourceLanguage) != null)
                                    sourceLanguage = LanguageHandling.getCombinedLanguageCodeFromShortLanguageCode(sourceLanguage);
                                langCombo.setText(sourceLanguage);
                                langCombo.setEnabled(false);
                            }
                            if (targetLanguage != null)
                            {
                                if (LanguageHandling.getCombinedLanguageCodeFromShortLanguageCode(targetLanguage) != null)
                                    targetLanguage = LanguageHandling.getCombinedLanguageCodeFromShortLanguageCode(targetLanguage);
                                tgtLangCombo.setText(targetLanguage);
                                tgtLangCombo.setEnabled(false);
                            }
                        }
                        xliff = null;
                    }
                }
                f = null;
            }
            else
            {
                formatText.setText(message.getString("CheckFormatNoFile"));
            }
        }
        else
        {
            formatText.setText(message.getString("CheckFormatEmptyFile"));
        }
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
