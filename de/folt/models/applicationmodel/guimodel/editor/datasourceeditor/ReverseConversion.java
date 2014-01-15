/*
 * Created on Jul 17, 2003
 *  
 */
package de.folt.models.applicationmodel.guimodel.editor.datasourceeditor;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.xml.sax.SAXException;

import com.araya.eaglememex.doc.XliffDocument;
import com.araya.eaglememex.util.EMXProperties;
import com.araya.eaglememex.util.FormatDetector;
import com.araya.eaglememex.util.TTConstants;
import com.araya.utilities.DocumentProperties;

import de.folt.util.CodePageHandling;
import de.folt.util.LanguageHandling;
import de.folt.util.OpenTMSProperties;

public class ReverseConversion
{

    private String configfile = "";

    @SuppressWarnings("unused")
    private String configFile;

    private String curruser = "";

    private de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.EditorConfiguration editorConfiguration = null;

    private Combo encCombo;

    private String encoding;

    private Combo langCombo;

    private String language;

    private de.folt.util.Messages message;

    protected boolean plaintext;

    private String separator;

    private Shell shell;

    private String targetDocument;

    String targetExtensions[] =
        {
            "*.*"}; //$NON-NLS-1$

    private String xliffDocument;

    /**
     * @param parent the shell for the dialog
     * @param xliffdefaultfilename the xliff file to convert back to the original document format
     */
    public ReverseConversion(Shell parent, String xliffdefaultfilename)
    {
        encoding = ""; //$NON-NLS-1$
        separator = System.getProperty("file.separator"); //$NON-NLS-1$

        message = de.folt.util.Messages.getInstance("de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.datasourceeditor", "en");

        configfile = OpenTMSProperties.getInstance().getOpenTMSProperty("OpenTMS.DataSourceEditor.EditorConfigurationDirectory");
        String propfileName = OpenTMSProperties.getInstance().getOpenTMSProperty("ArayaPropertiesFile");
        EMXProperties.getInstance(propfileName);

        curruser = System.getProperty("user.name").toLowerCase();
        editorConfiguration = new de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.EditorConfiguration(shell, configfile, curruser);

        String revXliffFile = editorConfiguration.loadValueForKey("revXliffFile");
        String revConvertedFile = editorConfiguration.loadValueForKey("revConvertedFile");
        String revLanguage = editorConfiguration.loadValueForKey("revLanguage");
        String revCodepage = editorConfiguration.loadValueForKey("revCodepage");

        if (!xliffdefaultfilename.equals(""))
        {
            revXliffFile = xliffdefaultfilename;
            // should get the extension here
            // should come from
            // <file datatype="html" original="C:\araya\test\html\entities-unknown.html" source-language="de" target-language="en">
            // here we should check the file extensions ...
            try
            {
                XliffDocument transXliff = null;
                FormatDetector fmDetect = new FormatDetector(true);
                fmDetect.setEncoding("UTF-8");
                fmDetect.detection(revXliffFile);
                transXliff = fmDetect.getXliffDocument();
                String datatype = transXliff.getDataType();
                if (datatype.equals(TTConstants.PHP2XLIFF))
                    revConvertedFile = xliffdefaultfilename + ".php";
                else if (datatype.equals(TTConstants.HTML2XLIFF))
                    revConvertedFile = xliffdefaultfilename + ".html";
                else if (datatype.equals("html"))
                    revConvertedFile = xliffdefaultfilename + ".html";
                else if (datatype.equals("rtf"))
                    revConvertedFile = xliffdefaultfilename + ".rtf";
                else if (datatype.equals("mif"))
                    revConvertedFile = xliffdefaultfilename + ".mit";
                else if (datatype.equals(TTConstants.PLAINTEXT2XLIFF))
                    revConvertedFile = xliffdefaultfilename + ".txt";
                else if (datatype.equals("plaintext"))
                    revConvertedFile = xliffdefaultfilename + ".txt";
                else
                {
                    boolean bIsOpenOffice = com.araya.openoffice.openOfficeConverter.isOpenOfficeDocument(xliffdefaultfilename);
                    if (bIsOpenOffice)
                    {
                        // ok need to determine the orginal format
                        String origformat = transXliff.getOrigFormat();
                        if (origformat.equals("OpenOffice"))
                            origformat = "odt";
                        if ((origformat != null) && !origformat.equals(""))
                        {
                            revConvertedFile = xliffdefaultfilename + "." + origformat;
                        }
                        else
                            revConvertedFile = xliffdefaultfilename + ".odt";
                    }
                    else if (com.araya.winword.winWordConverter.isWinWordDocument(xliffdefaultfilename))
                    {
                        revConvertedFile = xliffdefaultfilename + ".docx";
                    }
                    else
                        revConvertedFile = xliffdefaultfilename + ".xml";
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        shell = new Shell(parent.getDisplay(), SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
        shell.setLayout(new GridLayout(1, false));
        shell.setText(message.getString("Document_reverse_conversion")); //$NON-NLS-1$

        Composite filesComposite = new Composite(shell, SWT.NONE);
        filesComposite.setLayout(new GridLayout(3, false));
        filesComposite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));

        Label sourceNameLabel = new Label(filesComposite, SWT.NONE);
        sourceNameLabel.setText(message.getString("XLIFF_file")); //$NON-NLS-1$

        final StyledText sourceNameText = new StyledText(filesComposite, SWT.BORDER);
        sourceNameText.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
        sourceNameText.addModifyListener(new ModifyListener()
        {

            public void modifyText(ModifyEvent arg0)
            {
                String xliffDocumentNew = sourceNameText.getText();
                File xl = new File(xliffDocumentNew);
                xliffDocument = sourceNameText.getText();
                if (!xl.exists())
                {
                    return;
                }

                try
                {
                    String skl[] = com.araya.OpenTMS.Interface.getSkeleton(xliffDocument);
                    encoding = skl[2];
                    language = skl[1];
                    if (skl[3] != null)
                        plaintext = Boolean.parseBoolean(skl[3]);
                    else
                        plaintext = false;
                    if (!skl[0].equals(""))
                    {

                        if (!language.equals(""))
                        {
                            String name = LanguageHandling.getCombinedLanguageCodeFromShortLanguageCode(language);
                            langCombo.setText(name);
                        }
                        if (!encoding.equals(""))
                        {
                            encCombo.setText(encoding);
                        }
                    }
                    else
                    {
                        if (plaintext == false)
                        {
                            MessageBox box = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
                            box.setMessage(message.getString("ReverseConversion.132")); //$NON-NLS-1$
                            box.open();
                        }
                        if (!language.equals(""))
                        { //$NON-NLS-1$
                            String name = LanguageHandling.getCombinedLanguageCodeFromShortLanguageCode(language);
                            langCombo.setText(name);
                        }
                        if (!encoding.equals(""))
                        { //$NON-NLS-1$
                            encCombo.setText(encoding);
                        }
                    }
                }
                catch (SAXException e)
                {
                    e.printStackTrace();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                catch (ParserConfigurationException e)
                {
                    e.printStackTrace();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

        String slangnames[] = LanguageHandling.getCombinedLanguages();

        Button browseSource = new Button(filesComposite, SWT.PUSH);
        browseSource.setText(message.getString("Browse")); //$NON-NLS-1$
        browseSource.addSelectionListener(new SelectionAdapter()
        {

            public void widgetSelected(SelectionEvent e)
            {
                FileDialog fd = new FileDialog(shell, SWT.OPEN);
                String extensions[] =
                    {
                            "*.xlf", "*.xml", "*.*"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                fd.setFilterExtensions(extensions);
                if (System.getProperty("file.separator").equals("/")) { //$NON-NLS-1$ //$NON-NLS-2$
                    fd.setFilterPath(System.getProperty("user.home")); //$NON-NLS-1$
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
                }
            }
        });

        Label targetNameLabel = new Label(filesComposite, SWT.NONE);
        targetNameLabel.setText(message.getString("Converted_document")); //$NON-NLS-1$

        final StyledText targetNameText = new StyledText(filesComposite, SWT.BORDER);
        targetNameText.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
        if ((revConvertedFile != null) && !revConvertedFile.equals(""))
            targetNameText.setText(revConvertedFile);

        Button browseTarget = new Button(filesComposite, SWT.PUSH);
        browseTarget.setText(message.getString("Browse")); //$NON-NLS-1$
        browseTarget.addSelectionListener(new SelectionAdapter()
        {

            public void widgetSelected(SelectionEvent e)
            {
                FileDialog fd = new FileDialog(shell, SWT.SAVE);
                fd.setFilterExtensions(targetExtensions);
                if (System.getProperty("file.separator").equals("/")) { //$NON-NLS-1$ //$NON-NLS-2$
                    fd.setFilterPath(System.getProperty("user.home")); //$NON-NLS-1$
                }
                fd.open();
                if (fd.getFileName() == "") { //$NON-NLS-1$
                    fd = null;
                    return;
                }
                else
                {
                    targetNameText.setText(fd.getFilterPath() + separator + fd.getFileName());
                    fd = null;
                }
            }
        });

        Group langComposite = new Group(shell, SWT.NONE);
        langComposite.setText(message.getString("Target_Document_Properties")); //$NON-NLS-1$
        langComposite.setLayout(new GridLayout(2, false));
        langComposite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));

        Label langLabel = new Label(langComposite, SWT.NONE);
        langLabel.setText(message.getString("Language")); //$NON-NLS-1$

        langCombo = new Combo(langComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        langCombo.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));

        langCombo.setItems(slangnames);
        if ((revLanguage != null) && !revLanguage.equals(""))
        {
            langCombo.setText(revLanguage);
        }

        Label encLabel = new Label(langComposite, SWT.NONE);
        encLabel.setText(message.getString("Code_Page")); //$NON-NLS-1$

        encCombo = new Combo(langComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        encCombo.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
        String scodepage[] = CodePageHandling.getCodePages();
        encCombo.setItems(scodepage);

        if ((revCodepage != null) && !revCodepage.equals(""))
            encCombo.setText(revCodepage);

        //
        // Bottom of the screen, buttons section
        //

        Composite bottom = new Composite(shell, SWT.BORDER);
        bottom.setLayout(new GridLayout(2, true));
        bottom.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));

        Button convertButton = new Button(bottom, SWT.PUSH);
        convertButton.setText(message.getString("Reverse_conversion")); //$NON-NLS-1$
        convertButton.addSelectionListener(new SelectionAdapter()
        {

            public void widgetSelected(SelectionEvent e)
            {
                xliffDocument = sourceNameText.getText();
                targetDocument = targetNameText.getText();
                language = langCombo.getText();
                try
                {
                    language = LanguageHandling.getShortLanguageCodeFromCombinedTable(language);
                }
                catch (Exception ex)
                {
                    language = "en";
                }
                encoding = encCombo.getText();
                if (xliffDocument.equals("")) { //$NON-NLS-1$
                    MessageBox box = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
                    box.setMessage(message.getString("Please_select_a_document_for_conversion")); //$NON-NLS-1$
                    box.open();
                    return;
                }
                if (targetDocument.equals("")) { //$NON-NLS-1$
                    MessageBox box = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
                    box.setMessage(message.getString("Please_select_a_name_for_converted_document")); //$NON-NLS-1$
                    box.open();
                    return;
                }
                if (targetDocument.equals(xliffDocument))
                {
                    MessageBox box = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
                    box.setMessage(message.getString("Please_select_a_different_name_for_converted_document")); //$NON-NLS-1$
                    box.open();
                    return;
                }
                if (language.equals("")) { //$NON-NLS-1$
                    MessageBox box = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
                    box.setMessage(message.getString("Please_select_document_language")); //$NON-NLS-1$
                    box.open();
                    return;
                }
                if (encoding.equals("")) { //$NON-NLS-1$
                    MessageBox box = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
                    box.setMessage(message.getString("Please_select_document_encoding")); //$NON-NLS-1$
                    box.open();
                    return;
                }

                try
                {
                    if (com.araya.OpenTMS.Interface.convert(xliffDocument, targetDocument, language, encoding))
                    {
                        editorConfiguration.saveKeyValuePair("revXliffFile", xliffDocument);
                        editorConfiguration.saveKeyValuePair("revConvertedFile", targetDocument);
                        editorConfiguration.saveKeyValuePair("revLanguage", langCombo.getText());
                        editorConfiguration.saveKeyValuePair("revCodepage", encoding);
                        MessageBox mbox = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
                        mbox = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
                        mbox.setMessage(message.getString("Conversion_completed")); //$NON-NLS-1$
                        mbox.open();
                    }
                    else
                    {
                        editorConfiguration.saveKeyValuePair("revXliffFile", xliffDocument);
                        editorConfiguration.saveKeyValuePair("revConvertedFile", targetDocument);
                        editorConfiguration.saveKeyValuePair("revLanguage", langCombo.getText());
                        editorConfiguration.saveKeyValuePair("revCodepage", encoding);
                        MessageBox mbox = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
                        mbox = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
                        mbox.setMessage(message.getString("Error_Translating_Document")); //$NON-NLS-1$
                        mbox.open();
                    }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                    editorConfiguration.saveKeyValuePair("revXliffFile", xliffDocument);
                    editorConfiguration.saveKeyValuePair("revConvertedFile", targetDocument);
                    editorConfiguration.saveKeyValuePair("revLanguage", langCombo.getText());
                    editorConfiguration.saveKeyValuePair("revCodepage", encoding);
                    MessageBox mbox = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
                    mbox = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
                    mbox.setMessage(message.getString("Error_Translating_Document")); //$NON-NLS-1$
                    mbox.open();
                }
            }
        });

        Button close = new Button(bottom, SWT.PUSH);
        close.setText(message.getString("Cl&ose")); //$NON-NLS-1$
        close.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        close.addSelectionListener(new SelectionAdapter()
        {

            public void widgetSelected(SelectionEvent e)
            {
                shell.close();
            }
        });

        if ((revXliffFile != null) && !revXliffFile.equals(""))
            sourceNameText.setText(revXliffFile);

        shell.pack();
    }

    /**
     * @param parent the parent shell for the message dialoage
     * @param xliffFileDocument the xliff document to convert back to original format
     * @param targetLanguage the target language for the back converted document
     */
    public ReverseConversion(Shell parent, String xliffFileDocument, String targetLanguage)
    {
        message = de.folt.util.Messages.getInstance("de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.datasourceeditor", "en");

        String revConvertedFile = xliffFileDocument + ".back";
        String revXliffFile = xliffFileDocument;
        String targetEncoding = "UTF-8";
        if (xliffFileDocument.equals(""))
            return;

        // should get the extension here
        // should come from
        // <file datatype="html" original="C:\araya\test\html\entities-unknown.html" source-language="de" target-language="en">
        // here we should check the file extensions ...
        try
        {
            XliffDocument transXliff = null;
            FormatDetector fmDetect = new FormatDetector(true);
            fmDetect.setEncoding("UTF-8");
            fmDetect.detection(revXliffFile);
            transXliff = fmDetect.getXliffDocument();
            String datatype = transXliff.getDataType();
            if (datatype.equals(TTConstants.PHP2XLIFF))
                revConvertedFile = xliffFileDocument + ".php";
            else if (datatype.equals(TTConstants.HTML2XLIFF))
                revConvertedFile = xliffFileDocument + ".html";
            else if (datatype.equals("html"))
                revConvertedFile = xliffFileDocument + ".html";
            else if (datatype.equals("rtf"))
                revConvertedFile = xliffFileDocument + ".rtf";
            else if (datatype.equals("mif"))
                revConvertedFile = xliffFileDocument + ".mit";
            else if (datatype.equals(TTConstants.PLAINTEXT2XLIFF))
                revConvertedFile = xliffFileDocument + ".txt";
            else if (datatype.equals("plaintext"))
                revConvertedFile = xliffFileDocument + ".txt";
            else
            {
                boolean bIsOpenOffice = com.araya.openoffice.openOfficeConverter.isOpenOfficeDocument(xliffFileDocument);
                if (bIsOpenOffice)
                {
                    // ok need to determine the orginal format
                    String origformat = transXliff.getOrigFormat();
                    if (origformat.equals("OpenOffice"))
                        origformat = "odt";
                    if ((origformat != null) && !origformat.equals(""))
                    {
                        revConvertedFile = xliffFileDocument + "." + origformat;
                    }
                    else
                        revConvertedFile = xliffFileDocument + ".odt";
                }
                else if (com.araya.winword.winWordConverter.isWinWordDocument(xliffFileDocument))
                {
                    revConvertedFile = xliffFileDocument + ".docx";
                }
                else
                    revConvertedFile = xliffFileDocument + ".xml";
            }

            DocumentProperties prop = new DocumentProperties(xliffFileDocument);
            if (prop.getEncoding() != null)
            {
                if (!prop.getEncoding().equals(""))
                    targetEncoding = prop.getEncoding();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return;
        }

        if (com.araya.OpenTMS.Interface.convert(xliffFileDocument, revConvertedFile, targetLanguage, targetEncoding))
        {
            if (parent != null)
            {
                MessageBox mbox = new MessageBox(parent, SWT.ICON_INFORMATION | SWT.OK);
                mbox = new MessageBox(parent, SWT.ICON_INFORMATION | SWT.OK);
                mbox.setMessage(message.getString("Conversion_completed") + "\n" + revConvertedFile + " - " + targetEncoding); //$NON-NLS-1$
                mbox.open();
            }
        }
        else
        {
            if (parent != null)
            {
                MessageBox mbox = new MessageBox(parent, SWT.ICON_INFORMATION | SWT.OK);
                mbox = new MessageBox(parent, SWT.ICON_INFORMATION | SWT.OK);
                mbox.setMessage(message.getString("Error_Translating_Document") + "\n" + revConvertedFile); //$NON-NLS-1$
                mbox.open();
            }

        }
    }

    /**
     * @param xliffFileDocument the xliff document to convert back to original format
     * @param targetLanguage the target language for the back converted document
     */
    public ReverseConversion(String xliffFileDocument, String targetLanguage)
    {
        new ReverseConversion(null, xliffFileDocument, targetLanguage);
    }

    /**
     * show the back conversion dialogue
     */
    public void show()
    {
        shell.open();
        shell.forceActive();
    }

}