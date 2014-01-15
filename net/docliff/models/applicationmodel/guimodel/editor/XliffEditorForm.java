/*
 * Created on 20.05.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package net.docliff.models.applicationmodel.guimodel.editor;

import java.util.Hashtable;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;
import org.jdom.Element;

import com.araya.eaglememex.util.EMXProperties;

import de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.DataSourceEditor;
import de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.OpenTMSPropertiesEditor;
import de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.OpenTMSXMLStyledText;
import de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.ReverseConversion;
import de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.TagDescriptor;
import de.folt.models.applicationmodel.guimodel.support.ProgressDialog;
import de.folt.models.applicationmodel.guimodel.support.ProgressDialogSupport;
import de.folt.models.datamodel.DataSource;
import de.folt.models.datamodel.MonoLingualObject;
import de.folt.models.datamodel.phrasetranslate.PhraseTranslateResult;
import de.folt.models.documentmodel.document.XmlDocument;
import de.folt.models.documentmodel.xliff.XliffDocument;
import de.folt.util.ColorTable;
import de.folt.util.OpenTMSProperties;

/**
 * Class implements a data source form editor.
 * 
 * @author klemens
 * 
 */
public class XliffEditorForm extends Composite
{

	public class SimpleXliffEditorObserver implements Observer
	{

		private XliffEditorForm xliffEditorForm = null;

		/**
		 * @param xliffEditorFormWindow
		 */
		public SimpleXliffEditorObserver(XliffEditorForm xliffEditorForm)
		{
			super();
			this.xliffEditorForm = xliffEditorForm;
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
				xliffEditorForm.getXliffEditorWindow().setText(xliffEditorForm.getTargetTextWindow().getText(), -1);

			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}

		}
	}

	public class TagWindowObserver implements Observer
	{

		private XliffEditorForm xliffEditorForm = null;

		/**
		 * @param xliffEditorFormWindow
		 */
		public TagWindowObserver(XliffEditorForm xliffEditorForm)
		{
			super();
			this.xliffEditorForm = xliffEditorForm;
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
				xliffEditorForm.getXliffEditorWindow().insert((String) arg1);
				if ((tagWindow != null) && !tagWindow.isBIsDisposed())
				{
					TransUnitInformationData trans = xliffEditorForm.getXliffEditorWindow().getCurrentTransUnitInformation();
					xliffEditorForm.getXliffEditorWindow().setStyleRange(xliffEditorForm.getXliffEditorWindow().getText(),
							trans.getITStartPosition(), trans.getFullTargetText().length() + trans.getITStartPosition() + 1);
					tagWindow.update(trans.getISegmentNumber(), trans.getFullSourceText(), trans.getFullTargetText());
				}
				xliffEditorObserver.update(null, null);
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}

		}
	}

	public class XliffEditorObserver implements Observer
	{

		private XliffEditorForm xliffEditorFormWindow = null;

		/**
		 * @param xliffEditorFormWindow
		 */
		public XliffEditorObserver(XliffEditorForm xliffEditorFormWindow)
		{
			super();
			this.xliffEditorFormWindow = xliffEditorFormWindow;
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
				TransUnitInformationData trans = xliffEditorFormWindow.getXliffEditorWindow().getCurrentTransUnitInformation();
				int iSegnum = trans.getISegmentNumber();
				if (!trans.getTargetText().equals(xliffEditorFormWindow.getTargetTextWindow().getText()))
				{
					xliffEditorFormWindow.getTargetTextWindow().setText(trans.getTargetText());
					xliffEditorFormWindow.getTargetTextWindow().setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
					xliffEditorFormWindow.getTargetTextWindow().setStyleRange(trans.getTargetText());
					String state = trans.getStateInformation();
					if ((state == null) || state.equals(""))
						state = "---";
					xliffEditorFormWindow.getTargetTextWindow().setToolTipText(
							message.getString("targetTextWindow") + "\n" + message.getString("targetTextWindowState") + " " + state + " "
									+ message.getString("targetTextWindowSize") + " " + trans.getSegmentLengthInformation() + " ("
									+ trans.getTargetText().length() + ") " + message.getString("targetTextWindowSizeUnit") + " "
									+ trans.getSizeunit() + "\n" + trans.getFullSourceText());
				}
				if ((iSegnum >= 0) && (getIOldSegmentPosition() == iSegnum))
				{
					return;
				}

				segmentNumber.setText(message.getString("_Segmentnumber") + " " + iSegnum + "/"
						+ xliffEditorFormWindow.getXliffEditorWindow().getIOverallSegmentNumber() + " ("
						+ xliffEditorFormWindow.getXliffEditorWindow().countApprovedSegments() + "/"
						+ xliffEditorFormWindow.getXliffEditorWindow().countTranslatedSegments() + ") " + sourceLanguage + "->" + targetLanguage);
				matchNumber.setText(message.getString("_matchNumber"));

				if ((iSegnum >= 0) && (getIOldSegmentPosition() != iSegnum))
				{
					if (getIOldSegmentPosition() > -1)
						xliffEditorFormWindow.getXliffEditorWindow().setXliffEditorStyleRange(getIOldSegmentPosition());
					if (autosearchdatasource.getSelection())
					{
						Cursor hglass = new Cursor(getDisplay(), SWT.CURSOR_WAIT);
						Cursor arrow = new Cursor(getDisplay(), SWT.CURSOR_ARROW);
						setCursor(hglass);
						translateSegment(iSegnum);
						translatePhrases(iSegnum);
						setCursor(arrow);
					}
					showAltTrans(iSegnum, 0, 0);
					setIOldSegmentPosition(iSegnum);
					if (xliffEditorSegmentDictionaryViewer != null)
					{
						xliffEditorSegmentDictionaryViewer.adaptDictionaryViewer(xliffFile, sourceLanguage, targetLanguage);
						xliffEditorSegmentDictionaryViewer.setTerms(getPhrases(iSegnum));
					}
					if (xliffEditorDictionaryViewer != null)
					{
						xliffEditorDictionaryViewer.adaptDictionaryViewer(sourceLanguage, targetLanguage);
					}

					if (tagWindow != null)
					{
						if (tagWindow.isBIsDisposed())
						{
							tagWindow = new TagWindow(shell, SWT.NONE, getXliffEditorWindow().getIOldSegmentPosition(), trans.getFullSourceText(),
									trans.getFullTargetText());
							tagWindow.addObserver(tagWindowObserver);
							tagWindow.show();
						}
						else
							tagWindow.update(getXliffEditorWindow().getIOldSegmentPosition(), trans.getFullSourceText(), trans.getFullTargetText());
					}

				}
				else if ((iSegnum >= 0))
				{
					if (autosearchdatasource.getSelection())
					{
						Cursor hglass = new Cursor(getDisplay(), SWT.CURSOR_WAIT);
						Cursor arrow = new Cursor(getDisplay(), SWT.CURSOR_ARROW);
						setCursor(hglass);
						translateSegment(iSegnum);
						translatePhrases(iSegnum);
						setCursor(arrow);
					}
					showAltTrans(iSegnum, 0, 0);
					setIOldSegmentPosition(iSegnum);
					if (xliffEditorSegmentDictionaryViewer != null)
					{
						xliffEditorSegmentDictionaryViewer.adaptDictionaryViewer(xliffFile, sourceLanguage, targetLanguage);
						xliffEditorSegmentDictionaryViewer.setTerms(getPhrases(iSegnum));
					}
					if (xliffEditorDictionaryViewer != null)
					{
						xliffEditorDictionaryViewer.adaptDictionaryViewer(sourceLanguage, targetLanguage);
					}
				}
				else
				{
					matchNumber.setText(message.getString("_matchNumber"));
				}

				xliffEditorFormWindow.getXliffEditorWindow().setCaretStyleRange(iSegnum);
				if (getIOldSegmentPosition() > -1)
					xliffEditorFormWindow.getXliffEditorWindow().markupPhraseMatches(trans);

			}
			catch (Exception e)
			{
				e.printStackTrace();
				xliffEditorFormWindow.getStatusDisplayWindow().setText(e.getLocalizedMessage());
				matchNumber.setText(message.getString("_matchNumber"));
			}
		}
	}

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

		shell.setText("Test Xliff Editor Form");

		shell.setSize(1200, 900);

		String configFile = OpenTMSProperties.getInstance().getOpenTMSProperty("OpenTMS.XliffEditor.EditorConfigurationDirectory");
		String propfileName = OpenTMSProperties.getInstance().getOpenTMSProperty("ArayaPropertiesFile");
		EMXProperties.getInstance(propfileName);

		@SuppressWarnings("unused")
		XliffEditorForm xliffEditorform = new XliffEditorForm(shell, shell, SWT.NONE, args[0], configFile);
		// xliffEditorform.pack();
		shell.pack();
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

	/**
	 * segmentLengthCheck checks if a target text has the correct length
	 * 
	 * @param translationText
	 *            the translation to check
	 * @param shell
	 *            the shell for the dialogue
	 * @param trans
	 *            the TransUnitInformationData containing the relevant
	 *            information
	 * @param message
	 *            the message container for the texts to be display
	 * @return -99 for not existing length information or char size, otherwise
	 *         SWT:YES or SWT.NO
	 */
	public static int segmentLengthCheck(String translationText, Shell shell, TransUnitInformationData trans, de.folt.util.Messages message)
	{
		// int iSegmentNumber = trans.getISegmentNumber();
		// add some checks for length here....
		if ((trans.getSegmentLengthInformation() != -1) && trans.getSizeunit().equals("char"))
		{
			String translation = MonoLingualObject.simpleComputePlainText(translationText);
			int iLen = translation.length() + 1; // we must add 1 for the
			// current character!
			if (iLen > trans.getSegmentLengthInformation())
			{
				int style = SWT.PRIMARY_MODAL | SWT.ICON_WARNING | SWT.OK; // SWT.YES
				// |
				// SWT.NO;
				MessageBox messageBox = new MessageBox(Display.getCurrent().getActiveShell(), style);
				messageBox.setText(message.getString("TranslationTooLong"));
				messageBox.setMessage(message.getString("TranslationTextTooLong") + "\n" + message.getString("targetTextWindowSize") + " "
						+ trans.getSegmentLengthInformation() + " < " + iLen + " " + message.getString("targetTextWindowTextLength"));
				int result = messageBox.open();
				return result;
			}
		}
		return -99;
	}

	private Combo altSourceMatchesCombo;

	private OpenTMSXMLStyledText altSourceWindow;

	private Combo altTargetMatchesCombo;

	private OpenTMSXMLStyledText altTargetWindow;

	private Button autosearchdatasource;

	private boolean bXliffEditorFormCreated = false;

	private String currentId;

	private String currentMULId;

	private String currentOrigin;

	private String curruser;

	private SashForm dataSourceHolders;

	private CTabFolder dataSourceWindowsTabs;

	private Display display;

	private de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.EditorConfiguration editorConfiguration;

	private ToolItem editTMEntry;

	private Combo fuzzysim;

	private Label gotoSeg;

	private Button jump;

	private Combo jumpText;

	private SashForm matchFileHolder;

	private Label matchNumber;

	private SashForm matchSourceFileHolder;

	private SashForm matchTargetFileHolder;

	private de.folt.util.Messages message;

	private ToolItem phraseSearch;

	private PreferencesContainer preferencesContainer = new PreferencesContainer();

	private DataSourceListWithTools ptDataSourceFormComposite;

	private Combo searchmethod;

	private Label segmentNumber;

	private Shell shell;

	private String sourceLanguage = "";

	private OpenTMSXMLStyledText statusDisplayWindow;

	private SashForm statusHolder;

	protected TagWindow tagWindow = null;

	private TagWindowObserver tagWindowObserver;

	private String targetLanguage = "";

	private SimpleXliffEditorWindow targetTextWindow;

	private SashForm tasksHolder;

	private DataSourceListWithTools tmDataSourceFormComposite;

	private ToolItem tmphraseSearch;

	private SashForm toolbarHolder;

	private SashForm toolsAreaHolder;

	private String userLanguage = "en";

	private SashForm workAreaHolder;

	private XliffDocument xliffDocument;

	private XliffEditor xliffEditor;

	private XliffEditorDictionaryViewer xliffEditorDictionaryViewer;

	private XliffEditorObserver xliffEditorObserver;

	private XliffEditorDictionaryViewer xliffEditorSegmentDictionaryViewer;

	private XliffEditorWindow xliffEditorWindow;

	private String xliffFile;

	private SashForm xliffFileHolder;

	/**
	 * Class implements a simple Observer for a basic data source
	 * 
	 * @author klemens
	 * 
	 */

	private XmlDocument xmlDoc = new XmlDocument();

	private Menu menuCoolbar;

	private int iOldSetSimilarity = 70;

	/**
	 * @param parent
	 * @param style
	 */
	public XliffEditorForm(Shell shell, Composite parent, int style, String xliffFile, String configFile)
	{
		super(parent, style);
		this.setSize(shell.getSize().x, shell.getSize().y);
		this.shell = shell;
		message = de.folt.util.Messages.getInstance("net.docliff.models.applicationmodel.guimodel.editor.XliffEditor", userLanguage);

		message.getString("languageName");
		message.getString("plainText");
		display = parent.getDisplay();
		this.xliffFile = xliffFile;
		openXliffFile(xliffFile);

		curruser = System.getProperty("user.name").toLowerCase();
		editorConfiguration = new de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.EditorConfiguration(shell, configFile,
				"docliffEditor", curruser);

		de.folt.util.Messages.getInstance("net.docliff.models.applicationmodel.guimodel.editor.XliffEditor", userLanguage);
		GridLayout shellLayout = new GridLayout(1, true);
		shellLayout.horizontalSpacing = 0;
		shellLayout.verticalSpacing = 1;
		shellLayout.marginWidth = 0;
		this.setLayout(shellLayout);

		setBXliffEditorFormCreated(createContents(parent, xliffFile));
	}

	/**
	 * acceptAll100Translation
	 * 
	 * @return
	 */
	public int acceptAll100Translation()
	{
		return acceptAll100Translation(false);
	}

	/**
	 * acceptAll100Translation accepts all matching translation when quality =
	 * 100
	 * 
	 * @param bIgenoreSegmentsWithExistingTranslations
	 *            - if true existing translation in target will be kept;
	 *            otherwise replaced (if user specifies yes)
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public int acceptAll100Translation(boolean bIgenoreSegmentsWithExistingTranslations)
	{
		this.statusDisplayWindow.setText("");
		for (int k = 0; k < this.xliffEditorWindow.getIOverallSegmentNumber(); k++)
		{
			this.xliffEditorWindow.gotoSegment(k, true);

			showAltTrans(k, 0, 0);
			TransUnitInformationData transData = this.xliffEditorWindow.getSegmentTransUnitInformation(k);
			if (transData.isBApproved())
				continue;
			if (!transData.getTargetText().equals("") && bIgenoreSegmentsWithExistingTranslations)
			{
				continue;
			}
			Element transunit = getXliffEditorWindow().getTransUnits().get(k);
			List<Element> altranslist = transunit.getChildren("alt-trans", xliffEditorWindow.getXliffDocument().getNamespace());
			if (altranslist.size() > 0)
			{
				for (int i = 0; i < altranslist.size(); i++)
				{
					Element altrans = altranslist.get(i);
					String quality = altrans.getAttributeValue("match-quality"); // ,
					// xliffEditorWindow.getXliffDocument().getNamespace());
					if (quality == null)
						continue;
					try
					{
						int iQuality = Integer.parseInt(quality);
						if (iQuality == 100)
						{
							List<Element> targetlist = altrans.getChildren("target", xliffEditorWindow.getXliffDocument().getNamespace());
							if (targetlist.size() > 0)
							{
								Element target = targetlist.get(0);
								String translation = xmlDoc.elementToString(target);
								translation = translation.replaceAll("\\<target.*?>(.*?)\\</target>", "$1");
								if (translation.equals(""))
									continue;
								int iRes = this.xliffEditorWindow.setTranslation(k, translation, true);
								if (iRes == SWT.CANCEL)
								{
									segmentNumber.setText(message.getString("_Segmentnumber") + " " + k + "/"
											+ this.getXliffEditorWindow().getIOverallSegmentNumber() + " ("
											+ this.getXliffEditorWindow().countApprovedSegments() + "/"
											+ this.getXliffEditorWindow().countTranslatedSegments() + ")");
									return iRes;
								}
								else if (iRes == SWT.YES)
								{
									this.statusDisplayWindow.setText(statusDisplayWindow.getText() + message.getString("AcceptedMatchingTarget")
											+ " " + k + "\n");
								}
								break;
							}
						}
					}
					catch (Exception e)
					{
						continue;
					}
				}
			}
		}
		segmentNumber.setText(message.getString("_Segmentnumber") + " " + (this.xliffEditorWindow.getIOverallSegmentNumber() - 1) + "/"
				+ this.getXliffEditorWindow().getIOverallSegmentNumber() + " (" + this.getXliffEditorWindow().countApprovedSegments() + "/"
				+ this.getXliffEditorWindow().countTranslatedSegments() + ")");

		return SWT.YES;
	}

	/**
	 * acceptAllTranslations accept all translations from the alt-trans
	 * regardless of quality of the match
	 * 
	 * @return YES for success
	 */
	public int acceptAllTranslations()
	{
		return acceptAllTranslations(false);
	}

	/**
	 * acceptAllTranslations accept all translations from the alt-trans
	 * regardless of quality of the match
	 * 
	 * @param bIgenoreSegmentsWithExistingTranslations
	 *            - if true existing translation in target will be kept;
	 *            otherwise replaced (if user specifies yes)
	 * @return YES for success
	 */
	@SuppressWarnings("unchecked")
	public int acceptAllTranslations(boolean bIgenoreSegmentsWithExistingTranslations)
	{
		this.statusDisplayWindow.setText("");
		for (int k = 0; k < this.xliffEditorWindow.getIOverallSegmentNumber(); k++)
		{
			this.xliffEditorWindow.gotoSegment(k, true);

			showAltTrans(k, 0, 0);
			TransUnitInformationData transData = this.xliffEditorWindow.getSegmentTransUnitInformation(k);
			if (transData.isBApproved())
				continue;
			if (!transData.getTargetText().equals("") && bIgenoreSegmentsWithExistingTranslations)
			{
				continue;
			}
			Element transunit = getXliffEditorWindow().getTransUnits().get(k);
			List<Element> altranslist = transunit.getChildren("alt-trans", xliffEditorWindow.getXliffDocument().getNamespace());
			if (altranslist.size() > 0)
			{
				for (int i = 0; i < altranslist.size(); i++)
				{
					Element altrans = altranslist.get(i);
					try
					{
						List<Element> targetlist = altrans.getChildren("target", xliffEditorWindow.getXliffDocument().getNamespace());
						if (targetlist.size() > 0)
						{
							Element target = targetlist.get(0);
							String translation = xmlDoc.elementToString(target);
							translation = translation.replaceAll("\\<target.*?>(.*?)\\</target>", "$1");
							if (translation.equals(""))
								continue;
							int iRes = this.xliffEditorWindow.setTranslation(k, translation, true);
							if (iRes == SWT.CANCEL)
							{
								segmentNumber.setText(message.getString("_Segmentnumber") + " " + k + "/"
										+ this.getXliffEditorWindow().getIOverallSegmentNumber() + " ("
										+ this.getXliffEditorWindow().countApprovedSegments() + "/"
										+ this.getXliffEditorWindow().countTranslatedSegments() + ")");
								return iRes;
							}
							else if (iRes == SWT.YES)
							{
								this.statusDisplayWindow.setText(statusDisplayWindow.getText() + message.getString("AcceptedMatchingTarget") + " "
										+ k + "\n");
							}
							break;
						}

					}
					catch (Exception e)
					{
						continue;
					}
				}
			}
		}
		segmentNumber.setText(message.getString("_Segmentnumber") + " " + (this.xliffEditorWindow.getIOverallSegmentNumber() - 1) + "/"
				+ this.getXliffEditorWindow().getIOverallSegmentNumber() + " (" + this.getXliffEditorWindow().countApprovedSegments() + "/"
				+ this.getXliffEditorWindow().countTranslatedSegments() + ")");

		return SWT.YES;
	}

	/**
	 * acceptTranslation accepts matching translation regardless of quality of
	 * the match
	 */
	public int acceptTranslation()
	{
		// get the current translations
		String translation = this.altTargetWindow.getText();
		if (translation.equals(""))
			return SWT.NO;
		return this.xliffEditorWindow.setTranslation(this.xliffEditorWindow.getIOldSegmentPosition(), translation, true);
	}

	/**
	 * backConvertXliffDocument
	 */
	protected void backConvertXliffDocument()
	{
		if (getXliffEditorWindow().isBChanged())
		{
			// ask if to be saved when changed
			int style = SWT.PRIMARY_MODAL | SWT.ICON_QUESTION | SWT.YES | SWT.NO | SWT.CANCEL;
			MessageBox messageBox = new MessageBox(shell, style);
			messageBox.setMessage(message.getString("Save_Changes") + "\n" + this.getXliffEditorWindow().getXliffDocument().getXmlDocumentName());
			int result = messageBox.open();
			if (result == SWT.CANCEL)
			{
				// return;
			}
			else if (result == SWT.YES)
			{
				saveXliffDocument();
			}
		}

		// now run back conversion...
		new ReverseConversion(shell, this.getXliffEditorWindow().getXliffDocument().getXmlDocumentName(), targetLanguage);

	}

	/**
	 * closeDataSources
	 */
	public void closeAllDataSources()
	{
		ptDataSourceFormComposite.closeAllDataSources();
		tmDataSourceFormComposite.closeAllDataSources();
	}

	/**
	 * Creates the main window's contents
	 * 
	 * @param parent
	 *            the parent window
	 */
	private boolean createContents(Widget parent, String xliffFileName)
	{
		// Change the color used to paint the sashes
		int iGridData = GridData.GRAB_VERTICAL | GridData.VERTICAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL;

		this.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		SashForm toolsHolder = new SashForm(this, SWT.NONE);
		toolsHolder.setOrientation(SWT.HORIZONTAL);
		GridLayout toolsLayout = new GridLayout(1, true);
		toolsLayout.marginWidth = 1;
		toolsLayout.marginHeight = 1;
		toolsLayout.horizontalSpacing = 0;
		toolsHolder.setLayout(toolsLayout);
		toolsHolder.setLayoutData(new GridData(iGridData));

		// SashForm dataSourceForms = createDataSourceForm(toolsHolder);
		// setDataSourceHolders(dataSourceForms);

		xliffFileHolder = createXliffEditorWithToolBar(toolsHolder);

		// 1 : 5 | | | changed to one sash now and moved the
		// createXliffEditorWithToolBar(toolsHolder); to the inner form
		toolsHolder.setWeights(new int[] { 1
		/* , 5 */
		});

		this.layout();

		boolean bSuccess = loadXliffFile(xliffFileName);

		if (bSuccess)
			xliffEditorObserver.update(null, null);

		return bSuccess;
	}

	/**
	 * createDataSourceForm
	 * 
	 * @param toolsHolder
	 */
	private SashForm createDataSourceForm(SashForm parentHolder)
	{
		int iGridData = GridData.GRAB_VERTICAL | GridData.VERTICAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL;

		SashForm toolsHolder = new SashForm(parentHolder, SWT.NONE);
		toolsHolder.setOrientation(SWT.VERTICAL);
		GridLayout toolsLayout = new GridLayout(1, true);
		toolsLayout.marginWidth = 1;
		toolsLayout.marginHeight = 1;
		toolsLayout.horizontalSpacing = 0;
		toolsHolder.setLayout(toolsLayout);
		toolsHolder.setLayoutData(new GridData(iGridData));

		dataSourceWindowsTabs = new CTabFolder(toolsHolder, SWT.BORDER);
		dataSourceWindowsTabs.setLayoutData(new GridLayout(1, true));
		dataSourceWindowsTabs.setLayoutData(new GridData(iGridData));
		toolsHolder.setData("dataSourceWindowsTabs", dataSourceWindowsTabs);

		CTabItem tmTabItem = new CTabItem(dataSourceWindowsTabs, SWT.BORDER);
		tmTabItem.setText(message.getString("tmDataSources"));
		tmDataSourceFormComposite = new DataSourceListWithTools(dataSourceWindowsTabs, SWT.BORDER, message.getString("tmDataSources"));
		tmTabItem.setControl(tmDataSourceFormComposite);
		toolsHolder.setData("tmDataSourceFormComposite", tmDataSourceFormComposite);
		tmTabItem.setData("dataSourceFormComposite", tmDataSourceFormComposite);

		CTabItem ptTabItem = new CTabItem(dataSourceWindowsTabs, SWT.BORDER);
		ptTabItem.setText(message.getString("phraseDataSources"));
		ptDataSourceFormComposite = new DataSourceListWithTools(dataSourceWindowsTabs, SWT.BORDER, message.getString("phraseDataSources"));
		ptTabItem.setControl(ptDataSourceFormComposite);
		toolsHolder.setData("ptDataSourceFormComposite", ptDataSourceFormComposite);
		ptTabItem.setData("dataSourceFormComposite", ptDataSourceFormComposite);
		dataSourceWindowsTabs.setSelection(tmTabItem);

		toolsHolder.addKeyListener(new KeyListener()
		{
			public void keyPressed(KeyEvent e)
			{
				if (e.keyCode == SWT.F1)
				{
					// ok call help
					xliffEditor.displayOpenTMSHelp(OpenTMSPropertiesEditor.class.getName());
				}
			}

			public void keyReleased(KeyEvent e)
			{
				;
			}
		});

		toolsHolder.setWeights(new int[] { 1 });

		// toolsHolder.pack();

		return toolsHolder;
	}

	private void createDataSourceToolBar(CoolBar coolBar, ToolBar toolBar)
	{
		ToolItem fSearch = new ToolItem(toolBar, SWT.PUSH | SWT.FLAT);
		fSearch.setImage(new Image(display, "images/searchtmx.gif"));
		fSearch.setToolTipText(message.getString("SearchSegment"));
		SelectionAdapter fSearchSelectionAdapter = new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				// run the search on the databases in the list of data sources
				if (tmDataSourceFormComposite.getDataSourceInstances() == null)
					return;
				Cursor hglass = new Cursor(getDisplay(), SWT.CURSOR_WAIT);
				Cursor arrow = new Cursor(getDisplay(), SWT.CURSOR_ARROW);
				setCursor(hglass);
				translateSegment(getIOldSegmentPosition());
				showAltTrans(getIOldSegmentPosition(), 0, 0);
				setCursor(arrow);
			}
		};
		fSearch.addSelectionListener(fSearchSelectionAdapter);
		fSearch.setData("SelectionAdapter", fSearchSelectionAdapter);

		ToolItem sep1 = new ToolItem(toolBar, SWT.SEPARATOR);

		searchmethod = new Combo(toolBar, SWT.READ_ONLY);
		searchmethod.setToolTipText(message.getString("ChooseSearchMethod"));
		SelectionAdapter searchmethodSelectionAdapter = new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				if (searchmethod.getSelectionIndex() == 0)
				{
					fuzzysim.setEnabled(true);
					fuzzysim.removeAll();
					fuzzysim.add("100", 0);
					fuzzysim.add("90", 1);
					fuzzysim.add("80", 2);
					fuzzysim.add("70", 3);
					fuzzysim.add("60", 4);
					fuzzysim.select(0);
					fuzzysim.setToolTipText(message.getString("ChooseFuzzy"));
				}
				else if (searchmethod.getSelectionIndex() == 2)
				{
					fuzzysim.setEnabled(true);
					fuzzysim.removeAll();
					fuzzysim.add("OR", 0);
					fuzzysim.add("AND", 1);
					fuzzysim.setToolTipText(message.getString("ChooseOrAnd") + " (" + new de.folt.util.WordHandling().getDefaultSplitString() + ")");
					fuzzysim.select(0);
				}
				else
				{
					fuzzysim.setEnabled(false);
				}
			}
		};
		searchmethod.addSelectionListener(searchmethodSelectionAdapter);

		searchmethod.add("FUZZY", 0);
		searchmethod.add("REGEXP", 1);
		searchmethod.add("WORD", 2);
		searchmethod.pack();
		sep1.setWidth(searchmethod.getSize().x);
		sep1.setControl(searchmethod);
		searchmethod.select(0);
		searchmethod.setData("SelectionAdapter", searchmethodSelectionAdapter);

		ToolItem sep2 = new ToolItem(toolBar, SWT.SEPARATOR);

		fuzzysim = new Combo(toolBar, SWT.READ_ONLY);
		fuzzysim.setToolTipText(message.getString("ChooseFuzzy"));
		SelectionAdapter fuzzysimSelectionAdapter = new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				editorConfiguration.saveKeyValuePair("recentQuality", fuzzysim.getText());
				String sim = fuzzysim.getText();
				int similarity = 70;
				try
				{
					similarity = Integer.parseInt(sim);
					iOldSetSimilarity = similarity;
				}
				catch (Exception ex)
				{
					similarity = iOldSetSimilarity;
				}
				xliffEditorWindow.setISimilarity(similarity);
			}
		};
		fuzzysim.addSelectionListener(fuzzysimSelectionAdapter);
		fuzzysim.add("100", 0);
		fuzzysim.add("90", 1);
		fuzzysim.add("80", 2);
		fuzzysim.add("70", 3);
		fuzzysim.add("60", 4);
		fuzzysim.setEnabled(true);
		fuzzysim.pack();
		sep2.setWidth(fuzzysim.getSize().x);
		sep2.setControl(fuzzysim);
		fuzzysim.select(0);
		fuzzysim.setData("SelectionAdapter", fuzzysimSelectionAdapter);

		ToolItem sep1a = new ToolItem(toolBar, SWT.SEPARATOR);
		Label autosearchlabel = new Label(toolBar, SWT.LEFT);
		autosearchlabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL));
		autosearchlabel.setText(message.getString("autosearchdatasourcelabel"));
		autosearchlabel.setToolTipText(message.getString("autosearchdatasource"));
		autosearchlabel.pack();
		sep1a.setWidth(autosearchlabel.getSize().x + 10);
		sep1a.setControl(autosearchlabel);

		ToolItem sep1b = new ToolItem(toolBar, SWT.SEPARATOR);
		autosearchdatasource = new Button(toolBar, SWT.CHECK);
		autosearchdatasource.setToolTipText(message.getString("autosearchdatasource"));
		SelectionAdapter autosearchdatasourceSelectionAdapter = new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{

			}
		};
		autosearchdatasource.addSelectionListener(autosearchdatasourceSelectionAdapter);
		autosearchdatasource.pack();
		sep1b.setWidth(autosearchdatasource.getSize().x + 20);
		sep1b.setControl(autosearchdatasource);
		autosearchdatasource.setData("SelectionAdapter", autosearchdatasourceSelectionAdapter);

		autosearchdatasource.setSelection(true);

		String recentQuality = editorConfiguration.loadValueForKey("recentQuality");
		if ((recentQuality == null) || recentQuality.equals(""))
			recentQuality = "70";
		fuzzysim.setText(recentQuality);

		phraseSearch = new ToolItem(toolBar, SWT.PUSH | SWT.FLAT);
		phraseSearch.setImage(new Image(display, "images/searchterm.gif"));
		phraseSearch.setToolTipText(message.getString("SearchPhrase"));
		SelectionAdapter phraseSearchSelectionAdapter = new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				// run the search on the databases in the list of data sources
				translatePhrases(getIOldSegmentPosition());
				showAltTrans(getIOldSegmentPosition(), 0, 0);
				if (xliffEditorSegmentDictionaryViewer != null)
				{
					xliffEditorSegmentDictionaryViewer.adaptDictionaryViewer(xliffFile, sourceLanguage, targetLanguage);
					xliffEditorSegmentDictionaryViewer.setTerms(getPhrases(getIOldSegmentPosition()));
				}
				if (xliffEditorDictionaryViewer != null)
				{
					xliffEditorDictionaryViewer.adaptDictionaryViewer(sourceLanguage, targetLanguage);
				}
			}

		};
		phraseSearch.addSelectionListener(phraseSearchSelectionAdapter);
		phraseSearch.setData("SelectionAdapter", phraseSearchSelectionAdapter);

		tmphraseSearch = new ToolItem(toolBar, SWT.PUSH | SWT.FLAT);
		tmphraseSearch.setImage(new Image(display, "images/searchtmxterm.gif"));
		tmphraseSearch.setToolTipText(message.getString("SearchTMAndPhrase"));
		SelectionAdapter tmphraseSearchSelectionAdapter = new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				// run the search on the databases in the list of data sources
				if (ptDataSourceFormComposite.getDataSourceInstances() == null)
					return;
				for (int i = 0; i < ptDataSourceFormComposite.getDataSourceInstances().size(); i++)
				{
					try
					{
						DataSource datasource = ptDataSourceFormComposite.getDataSourceInstances().get(i);
						Element altrans = datasource.subSegmentTranslate(getXliffEditorWindow().getTransUnits().get(getIOldSegmentPosition()),
								xliffDocument, sourceLanguage, targetLanguage, null);
						if (altrans != null)
						{
							String text = "";
							Vector<PhraseTranslateResult> elements = getPhrases(getIOldSegmentPosition());
							for (int j = 0; j < elements.size(); j++)
							{
								text = text + elements.get(j).getSourcePhrase() + " : " + elements.get(j).getTargetPhrase() + "\n";
							}
							statusDisplayWindow.setText(text);
						}
					}
					catch (Exception e2)
					{
						e2.printStackTrace();
					}
				}
				showAltTrans(getIOldSegmentPosition(), 0, 0);
			}

		};
		tmphraseSearch.addSelectionListener(tmphraseSearchSelectionAdapter);
		tmphraseSearch.setData("SelectionAdapter", tmphraseSearchSelectionAdapter);

		new ToolItem(toolBar, SWT.SEPARATOR);

		editTMEntry = new ToolItem(toolBar, SWT.PUSH | SWT.FLAT);
		editTMEntry.setImage(new Image(display, "images/editmatching.gif"));
		editTMEntry.setToolTipText(message.getString("editTMEntry"));
		SelectionAdapter editTMEntrySelectionAdapter = new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				if (currentId.equals(""))
					return;
				try
				{
					statusDisplayWindow.setText(message.getString("EditDataSourceEntry") + currentOrigin + " " + currentId);
					String searchDataSource = currentOrigin; // adapt to correct
					// name in data
					// source editor
					searchDataSource = searchDataSource.replaceAll(".*?database=(.*?)", "$1");
					searchDataSource = searchDataSource.replaceAll("jdbc\\:.*/", "");
					DataSourceEditor.getInstance(shell.getDisplay(), searchDataSource, currentId);
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
					return;
				}
			}

		};
		editTMEntry.addSelectionListener(editTMEntrySelectionAdapter);
		tmphraseSearch.setData("SelectionAdapter", editTMEntrySelectionAdapter);

		new ToolItem(toolBar, SWT.SEPARATOR);

		ToolItem approve = new ToolItem(toolBar, SWT.NONE);
		approve.setImage(new Image(display, "images/approve.gif"));
		approve.setToolTipText(message.getString("Approve_Segment_tCtrl_+_E_59"));
		SelectionAdapter approveSelectionAdapter = new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				DataSource dataSource = tmDataSourceFormComposite.getSelectedDataSource();
				boolean bApprove = getXliffEditorWindow().approveSegment(getIOldSegmentPosition(), true, dataSource);
				if ((dataSource != null) && bApprove)
				{
					Cursor hglass = new Cursor(getDisplay(), SWT.CURSOR_WAIT);
					Cursor arrow = new Cursor(getDisplay(), SWT.CURSOR_ARROW);
					Hashtable<String, Object> transParam = new Hashtable<String, Object>();
					transParam.put("ignoreApproveAttribute", "yes");
					setCursor(hglass);
					translateSegment(getIOldSegmentPosition(), transParam);
					showAltTrans(getIOldSegmentPosition(), 0, 0);
					setCursor(arrow);

				}
				xliffEditorObserver.update(null, null);
			}
		};
		approve.addSelectionListener(approveSelectionAdapter);
		approve.setData("SelectionAdapter", approveSelectionAdapter);

		ToolItem approvenostore = new ToolItem(toolBar, SWT.NONE);
		approvenostore.setImage(new Image(display, "images/approvedonotstore.gif"));
		approvenostore.setToolTipText(message.getString("Approve_DoNotStoreSegment_tCtrl_+_E_59"));
		SelectionAdapter approvenostoreSelectionAdapter = new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				DataSource dataSource = null;
				getXliffEditorWindow().approveSegment(getIOldSegmentPosition(), true, dataSource);
				xliffEditorObserver.update(null, null);
			}
		};
		approvenostore.addSelectionListener(approvenostoreSelectionAdapter);
		approvenostore.setData("SelectionAdapter", approvenostoreSelectionAdapter);

		ToolItem disapprove = new ToolItem(toolBar, SWT.NONE);
		disapprove.setImage(new Image(display, "images/disapprove.gif"));
		disapprove.setToolTipText(message.getString("Disapprove_Segment_tCtrl_+_E_59"));
		SelectionAdapter disapproveSelectionAdapter = new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				getXliffEditorWindow().approveSegment(getIOldSegmentPosition(), false, null);
				xliffEditorObserver.update(null, null);
			}
		};
		disapprove.addSelectionListener(disapproveSelectionAdapter);
		disapprove.setData("SelectionAdapter", disapproveSelectionAdapter);

		ToolItem copysourecetottarget = new ToolItem(toolBar, SWT.NONE);
		copysourecetottarget.setImage(new Image(display, "images/copytotarget.gif")); //$NON-NLS-1$
		copysourecetottarget.setToolTipText(message.getString("CopySourceToTarget")); //$NON-NLS-1$
		SelectionAdapter copysourecetottargetSelectionAdapter = new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				TransUnitInformationData trans = getXliffEditorWindow().getCurrentTransUnitInformation();
				getXliffEditorWindow().copySourceToTarget(trans.getISegmentNumber(), true);
				xliffEditorObserver.update(null, null);
				if ((tagWindow != null) && !tagWindow.isBIsDisposed())
				{
					trans = getXliffEditorWindow().getCurrentTransUnitInformation();
					tagWindow.update(trans.getISegmentNumber(), trans.getFullSourceText(), trans.getFullTargetText());
				}
			}
		};
		copysourecetottarget.addSelectionListener(copysourecetottargetSelectionAdapter);
		copysourecetottarget.setData("SelectionAdapter", copysourecetottargetSelectionAdapter);

		ToolItem accepttranslation = new ToolItem(toolBar, SWT.NONE);
		accepttranslation.setImage(new Image(display, "images/accepttranslation.gif"));
		accepttranslation.setToolTipText(message.getString("AcceptTranslation"));
		SelectionAdapter accepttranslationSelectionAdapter = new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				acceptTranslation();
				xliffEditorObserver.update(null, null);
			}
		};
		accepttranslation.addSelectionListener(accepttranslationSelectionAdapter);
		accepttranslation.setData("SelectionAdapter", accepttranslationSelectionAdapter);

		CoolItem coolItem1 = new CoolItem(coolBar, SWT.DROP_DOWN);
		coolItem1.setControl(toolBar);
		Point toolBar1Size = toolBar.computeSize(SWT.DEFAULT, SWT.DEFAULT);
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

					MenuItem searchmethodLocal = new MenuItem(menuCoolbar, SWT.CASCADE);
					searchmethodLocal.setText(message.getString("ChooseSearchMethod"));

					searchmethodLocal.addListener(SWT.Selection, new Listener()
					{
						public void handleEvent(Event e)
						{

						}
					});

					Menu searchFMenu = new Menu(shell, SWT.DROP_DOWN);

					MenuItem menuitemFuzzy = new MenuItem(searchFMenu, SWT.CASCADE);
					menuitemFuzzy.setText("FUZZY");

					Menu fuzzyFMenu = new Menu(shell, SWT.DROP_DOWN);
					menuitemFuzzy.setMenu(fuzzyFMenu);
					Listener menuitemFuzzyListener = new Listener()
					{
						public void handleEvent(Event e)
						{
							Integer index = (Integer) ((MenuItem) e.widget).getData();
							searchmethod.select(index);
							fuzzysim.setEnabled(true);
							fuzzysim.removeAll();
							fuzzysim.add("100", 0);
							fuzzysim.add("90", 1);
							fuzzysim.add("80", 2);
							fuzzysim.add("70", 3);
							fuzzysim.add("60", 4);
							Integer fIndex = (Integer) ((MenuItem) e.widget).getData("pos");
							fuzzysim.select(fIndex);
							try
							{
								iOldSetSimilarity = Integer.parseInt(fuzzysim.getText());
							}
							catch (Exception ex)
							{

							}
							fuzzysim.setToolTipText(message.getString("ChooseFuzzy"));
							xliffEditorWindow.setISimilarity(iOldSetSimilarity);
						}
					};

					MenuItem menuitemfuzzysim100 = new MenuItem(fuzzyFMenu, SWT.PUSH);
					menuitemfuzzysim100.setText("100");
					menuitemfuzzysim100.setData("fuzzy", "100");
					menuitemfuzzysim100.setData("pos", (Integer) 0);
					menuitemfuzzysim100.setData("type", "fuzzy");
					menuitemfuzzysim100.setData((Integer) 0);
					menuitemfuzzysim100.addListener(SWT.Selection, menuitemFuzzyListener);

					MenuItem menuitemfuzzysim90 = new MenuItem(fuzzyFMenu, SWT.PUSH);
					menuitemfuzzysim90.setText("90");
					menuitemfuzzysim90.setData("fuzzy", "90");
					menuitemfuzzysim90.setData("pos", (Integer) 1);
					menuitemfuzzysim90.setData("type", "fuzzy");
					menuitemfuzzysim90.setData((Integer) 0);
					menuitemfuzzysim90.addListener(SWT.Selection, menuitemFuzzyListener);

					MenuItem menuitemfuzzysim80 = new MenuItem(fuzzyFMenu, SWT.PUSH);
					menuitemfuzzysim80.setText("80");
					menuitemfuzzysim80.setData("fuzzy", "80");
					menuitemfuzzysim80.setData("pos", (Integer) 2);
					menuitemfuzzysim80.setData("type", "fuzzy");
					menuitemfuzzysim80.setData((Integer) 0);
					menuitemfuzzysim80.addListener(SWT.Selection, menuitemFuzzyListener);

					MenuItem menuitemfuzzysim70 = new MenuItem(fuzzyFMenu, SWT.PUSH);
					menuitemfuzzysim70.setText("70");
					menuitemfuzzysim70.setData("fuzzy", "70");
					menuitemfuzzysim70.setData("pos", (Integer) 3);
					menuitemfuzzysim70.setData("type", "fuzzy");
					menuitemfuzzysim70.setData((Integer) 0);
					menuitemfuzzysim70.addListener(SWT.Selection, menuitemFuzzyListener);

					MenuItem menuitemfuzzysim60 = new MenuItem(fuzzyFMenu, SWT.PUSH);
					menuitemfuzzysim60.setText("60");
					menuitemfuzzysim60.setData("fuzzy", "60");
					menuitemfuzzysim60.setData("pos", (Integer) 3);
					menuitemfuzzysim60.setData("type", "fuzzy");
					menuitemfuzzysim60.setData((Integer) 0);
					menuitemfuzzysim60.addListener(SWT.Selection, menuitemFuzzyListener);

					menuitemFuzzy.setData((Integer) 0);

					Listener menuitemListener = new Listener()
					{
						public void handleEvent(Event e)
						{
							Integer index = (Integer) ((MenuItem) e.widget).getData();
							searchmethod.select(index);
							if (index == 1)
							{
								fuzzysim.setEnabled(false);
							}
							else
							{
								fuzzysim.setEnabled(true);
							}
						}
					};
					menuitemFuzzy.addListener(SWT.Selection, menuitemListener);

					MenuItem menuitemRegExp = new MenuItem(searchFMenu, SWT.PUSH);
					menuitemRegExp.addListener(SWT.Selection, menuitemListener);
					menuitemRegExp.setText("REGEXP");
					menuitemRegExp.setData((Integer) 1);

					MenuItem menuitemWord = new MenuItem(searchFMenu, SWT.CASCADE);
					menuitemWord.addListener(SWT.Selection, menuitemListener);
					menuitemWord.setText("WORD");
					menuitemWord.setData((Integer) 2);

					Menu wordAndORMenu = new Menu(shell, SWT.DROP_DOWN);
					menuitemWord.setMenu(wordAndORMenu);
					Listener WordParamsListener = new Listener()
					{
						public void handleEvent(Event e)
						{
							Integer index = (Integer) ((MenuItem) e.widget).getData();
							searchmethod.select(index);
							fuzzysim.setEnabled(true);
							fuzzysim.removeAll();
							fuzzysim.add("OR", 0);
							fuzzysim.add("AND", 1);
							Integer fIndex = (Integer) ((MenuItem) e.widget).getData("pos");
							fuzzysim.select(fIndex);
							fuzzysim.setToolTipText(message.getString("ChooseFuzzy"));
						}
					};

					MenuItem menuitemWordOR = new MenuItem(wordAndORMenu, SWT.PUSH);
					menuitemWordOR.setText("OR");
					menuitemWordOR.setData("pos", (Integer) 0);
					menuitemWordOR.setData("type", "WORD");
					menuitemWordOR.setData("method", "OR");
					menuitemWordOR.setData((Integer) 2);
					menuitemWordOR.addListener(SWT.Selection, WordParamsListener);

					MenuItem menuitemWordAND = new MenuItem(wordAndORMenu, SWT.PUSH);
					menuitemWordAND.setText("AND");
					menuitemWordAND.setData("pos", (Integer) 1);
					menuitemWordAND.setData("type", "WORD");
					menuitemWordAND.setData("method", "AND");
					menuitemWordAND.setData((Integer) 2);
					menuitemWordAND.addListener(SWT.Selection, WordParamsListener);

					searchmethodLocal.setMenu(searchFMenu);

					Point menuPoint = coolBar.toDisplay(new Point(event.x, event.y));
					menuCoolbar.setLocation(menuPoint.x, menuPoint.y);
					menuCoolbar.setVisible(true);
				}
			}
		}

		coolItem1.addSelectionListener(new CoolBarListener());
	}

	private void createNavigationToolBar(CoolBar coolBar, ToolBar toolBar)
	{

		ToolItem saveXliffDocument = new ToolItem(toolBar, SWT.PUSH | SWT.FLAT);
		saveXliffDocument.setImage(new Image(display, "images/savemol.gif")); //$NON-NLS-1$
		saveXliffDocument.setToolTipText(message.getString("SaveFile")); //$NON-NLS-1$
		SelectionAdapter saveXliffDocumentSelectionAdapter = new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				saveXliffDocument();
			}
		};
		saveXliffDocument.addSelectionListener(saveXliffDocumentSelectionAdapter);
		saveXliffDocument.setData("SelectionAdapter", saveXliffDocumentSelectionAdapter);

		ToolItem backConvertXliffDocument = new ToolItem(toolBar, SWT.PUSH | SWT.FLAT);
		backConvertXliffDocument.setImage(new Image(display, "images/document_out.gif")); //$NON-NLS-1$
		backConvertXliffDocument.setToolTipText(message.getString("backConvertXliffDocument")); //$NON-NLS-1$
		SelectionAdapter backConvertXliffDocumentSelectionAdapter = new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				backConvertXliffDocument();
			}
		};
		backConvertXliffDocument.addSelectionListener(backConvertXliffDocumentSelectionAdapter);
		backConvertXliffDocument.setData("SelectionAdapter", backConvertXliffDocumentSelectionAdapter);

		new ToolItem(toolBar, SWT.SEPARATOR);

		ToolItem previous = new ToolItem(toolBar, SWT.NONE);
		previous.setImage(new Image(display, "images/grayleftarrow.gif")); //$NON-NLS-1$
		previous.setToolTipText(message.getString("Previous_Segment")); //$NON-NLS-1$
		SelectionAdapter previoustSelectionAdapter = new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				getXliffEditorWindow().showPreviousSegment();
				xliffEditorObserver.update(null, null);
			}
		};
		previous.addSelectionListener(previoustSelectionAdapter);
		previous.setData("SelectionAdapter", previoustSelectionAdapter);

		ToolItem next = new ToolItem(toolBar, SWT.NONE);
		next.setImage(new Image(display, "images/grayrightarrow.gif")); //$NON-NLS-1$
		next.setToolTipText(message.getString("Next_Segment")); //$NON-NLS-1$
		SelectionAdapter nextSelectionAdapter = new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				getXliffEditorWindow().showNextSegment();
				xliffEditorObserver.update(null, null);
			}
		};
		next.addSelectionListener(nextSelectionAdapter);
		next.setData("SelectionAdapter", nextSelectionAdapter);

		new ToolItem(toolBar, SWT.SEPARATOR);

		ToolItem previousFuzzy = new ToolItem(toolBar, SWT.NONE);
		previousFuzzy.setImage(new Image(display, "images/blueleftarrow.gif")); //$NON-NLS-1$
		previousFuzzy.setToolTipText(message.getString("Previous_Fuzzy")); //$NON-NLS-1$
		SelectionAdapter previousFuzzySelectionAdapter = new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				getXliffEditorWindow().showPreviousFuzzy();
				xliffEditorObserver.update(null, null);
			}
		};
		previousFuzzy.addSelectionListener(previousFuzzySelectionAdapter);
		previousFuzzy.setData("SelectionAdapter", previousFuzzySelectionAdapter);

		ToolItem nextFuzzy = new ToolItem(toolBar, SWT.NONE);
		nextFuzzy.setImage(new Image(display, "images/bluerightarrow.gif")); //$NON-NLS-1$
		nextFuzzy.setToolTipText(message.getString("Next_Fuzzy")); //$NON-NLS-1$
		SelectionAdapter nextFuzzySelectionAdapter = new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				getXliffEditorWindow().showNextFuzzy();
				xliffEditorObserver.update(null, null);
			}
		};
		nextFuzzy.addSelectionListener(nextFuzzySelectionAdapter);
		nextFuzzy.setData("SelectionAdapter", nextFuzzySelectionAdapter);

		new ToolItem(toolBar, SWT.SEPARATOR);

		ToolItem previousChanged = new ToolItem(toolBar, SWT.NONE);
		previousChanged.setImage(new Image(display, "images/greenleftarrow.gif")); //$NON-NLS-1$
		previousChanged.setToolTipText(message.getString("Previous_Changed")); //$NON-NLS-1$
		SelectionAdapter previousChangedSelectionAdapter = new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				getXliffEditorWindow().showPreviousChanged();
				xliffEditorObserver.update(null, null);
			}
		};
		previousChanged.addSelectionListener(previousChangedSelectionAdapter);
		previousChanged.setData("SelectionAdapter", previousChangedSelectionAdapter);

		ToolItem nextChanged = new ToolItem(toolBar, SWT.NONE);
		nextChanged.setImage(new Image(display, "images/greenrightarrow.gif")); //$NON-NLS-1$
		nextChanged.setToolTipText(message.getString("Next_Changed")); //$NON-NLS-1$
		SelectionAdapter nextChangedSelectionAdapter = new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				getXliffEditorWindow().showNextChanged();
				xliffEditorObserver.update(null, null);
			}
		};
		nextChanged.addSelectionListener(nextChangedSelectionAdapter);
		nextChanged.setData("SelectionAdapter", nextChangedSelectionAdapter);

		new ToolItem(toolBar, SWT.SEPARATOR);

		ToolItem previousToCheck = new ToolItem(toolBar, SWT.NONE);
		previousToCheck.setImage(new Image(display, "images/redback.gif")); //$NON-NLS-1$
		previousToCheck.setToolTipText(message.getString("Previous_To_Check")); //$NON-NLS-1$
		SelectionAdapter previousToCheckSelectionAdapter = new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				getXliffEditorWindow().showPreviousToCheck();
				xliffEditorObserver.update(null, null);
			}
		};
		previousToCheck.addSelectionListener(previousToCheckSelectionAdapter);
		previousToCheck.setData("SelectionAdapter", previousToCheckSelectionAdapter);

		ToolItem nextToCheck = new ToolItem(toolBar, SWT.NONE);
		nextToCheck.setImage(new Image(display, "images/redforward.gif")); //$NON-NLS-1$
		nextToCheck.setToolTipText(message.getString("Next_To_Check")); //$NON-NLS-1$
		SelectionAdapter nextToCheckSelectionAdapter = new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				getXliffEditorWindow().showNextToCheck();
				xliffEditorObserver.update(null, null);
			}
		};
		nextToCheck.addSelectionListener(nextToCheckSelectionAdapter);
		nextToCheck.setData("SelectionAdapter", nextToCheckSelectionAdapter);

		new ToolItem(toolBar, SWT.SEPARATOR);

		ToolItem nextUntranslated = new ToolItem(toolBar, SWT.NONE);
		nextUntranslated.setImage(new Image(display, "images/greenforward.gif")); //$NON-NLS-1$
		nextUntranslated.setToolTipText(message.getString("Next_Untranslated")); //$NON-NLS-1$
		SelectionAdapter nextUntranslatedSelectionAdapter = new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				getXliffEditorWindow().showNextUntranslatedSegment();
				xliffEditorObserver.update(null, null);
			}
		};
		nextUntranslated.addSelectionListener(nextUntranslatedSelectionAdapter);
		nextUntranslated.setData("SelectionAdapter", nextUntranslatedSelectionAdapter);

		new ToolItem(toolBar, SWT.SEPARATOR);

		ToolItem nextUnapproved = new ToolItem(toolBar, SWT.NONE);
		nextUnapproved.setImage(new Image(display, "images/yellowforward.gif")); //$NON-NLS-1$
		nextUnapproved.setToolTipText(message.getString("Next_Unapproved")); //$NON-NLS-1$
		SelectionAdapter nextUnapprovedSelectionAdapter = new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				getXliffEditorWindow().showNextUnapprovedSegment();
				xliffEditorObserver.update(null, null);
			}
		};

		nextUnapproved.addSelectionListener(nextUnapprovedSelectionAdapter);
		nextUnapproved.setData("SelectionAdapter", nextUnapprovedSelectionAdapter);

		new ToolItem(toolBar, SWT.SEPARATOR);

		SelectionAdapter nextNonTransFuzzySelectionAdapter = new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				getXliffEditorWindow().showNextNotTranslatedOrFuzzySegment();
				xliffEditorObserver.update(null, null);
			}
		};
		ToolItem nextNonTransFuzzy = new ToolItem(toolBar, SWT.NONE);
		nextNonTransFuzzy.setImage(new Image(display, "images/nextnontransfuzzy.gif")); //$NON-NLS-1$
		nextNonTransFuzzy.setToolTipText(message.getString("Next_NonTrans")); //$NON-NLS-1$
		nextNonTransFuzzy.addSelectionListener(nextNonTransFuzzySelectionAdapter);
		nextNonTransFuzzy.setData("SelectionAdapter", nextNonTransFuzzySelectionAdapter);

		new ToolItem(toolBar, SWT.SEPARATOR);

		ToolItem showTags = new ToolItem(toolBar, SWT.NONE);
		showTags.setImage(new Image(display, "images/tags.gif")); //$NON-NLS-1$
		showTags.setToolTipText(message.getString("showTags")); //$NON-NLS-1$

		SelectionAdapter showTagsSelectionAdapter = new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				// getXliffEditorWindow().showNextNotTranslatedOrFuzzySegment();
				// xliffEditorObserver.update(null, null);
				TransUnitInformationData transData = getXliffEditorWindow().getCurrentTransUnitInformation();
				if ((tagWindow == null) || (tagWindow.isBIsDisposed()))
				{
					tagWindow = new TagWindow(shell, SWT.NONE, getXliffEditorWindow().getIOldSegmentPosition(), transData.getFullSourceText(),
							transData.getFullTargetText());
					tagWindow.addObserver(tagWindowObserver);
				}
				else
					tagWindow.update(getXliffEditorWindow().getIOldSegmentPosition(), transData.getFullSourceText(), transData.getFullTargetText());
				tagWindow.show();
			}
		};
		showTags.addSelectionListener(showTagsSelectionAdapter);
		showTags.setData("SelectionAdapter", showTagsSelectionAdapter);

		toolBar.pack();

		CoolItem coolItem1 = new CoolItem(coolBar, SWT.DROP_DOWN);
		coolItem1.setControl(toolBar);

		Point toolBar1Size = toolBar.computeSize(SWT.DEFAULT, SWT.DEFAULT);
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
	}

	@SuppressWarnings("unused")
	private void createNavigationToolBar(ToolBar toolBar)
	{

		ToolItem saveXliffDocument = new ToolItem(toolBar, SWT.PUSH | SWT.FLAT);
		saveXliffDocument.setImage(new Image(display, "images/savemol.gif")); //$NON-NLS-1$
		saveXliffDocument.setToolTipText(message.getString("SaveFile")); //$NON-NLS-1$
		saveXliffDocument.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				saveXliffDocument();
			}
		});

		ToolItem backConvertXliffDocument = new ToolItem(toolBar, SWT.PUSH | SWT.FLAT);
		backConvertXliffDocument.setImage(new Image(display, "images/document_out.gif")); //$NON-NLS-1$
		backConvertXliffDocument.setToolTipText(message.getString("backConvertXliffDocument")); //$NON-NLS-1$
		backConvertXliffDocument.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				backConvertXliffDocument();
			}
		});

		new ToolItem(toolBar, SWT.SEPARATOR);

		ToolItem previous = new ToolItem(toolBar, SWT.NONE);
		previous.setImage(new Image(display, "images/grayleftarrow.gif")); //$NON-NLS-1$
		previous.setToolTipText(message.getString("Previous_Segment")); //$NON-NLS-1$
		previous.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				getXliffEditorWindow().showPreviousSegment();
				xliffEditorObserver.update(null, null);
			}
		});

		ToolItem next = new ToolItem(toolBar, SWT.NONE);
		next.setImage(new Image(display, "images/grayrightarrow.gif")); //$NON-NLS-1$
		next.setToolTipText(message.getString("Next_Segment")); //$NON-NLS-1$
		next.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				getXliffEditorWindow().showNextSegment();
				xliffEditorObserver.update(null, null);
			}
		});

		new ToolItem(toolBar, SWT.SEPARATOR);

		ToolItem previousFuzzy = new ToolItem(toolBar, SWT.NONE);
		previousFuzzy.setImage(new Image(display, "images/blueleftarrow.gif")); //$NON-NLS-1$
		previousFuzzy.setToolTipText(message.getString("Previous_Fuzzy")); //$NON-NLS-1$
		previousFuzzy.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				getXliffEditorWindow().showPreviousFuzzy();
				xliffEditorObserver.update(null, null);
			}
		});

		ToolItem nextFuzzy = new ToolItem(toolBar, SWT.NONE);
		nextFuzzy.setImage(new Image(display, "images/bluerightarrow.gif")); //$NON-NLS-1$
		nextFuzzy.setToolTipText(message.getString("Next_Fuzzy")); //$NON-NLS-1$
		nextFuzzy.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				getXliffEditorWindow().showNextFuzzy();
				xliffEditorObserver.update(null, null);
			}
		});

		new ToolItem(toolBar, SWT.SEPARATOR);

		ToolItem previousChanged = new ToolItem(toolBar, SWT.NONE);
		previousChanged.setImage(new Image(display, "images/greenleftarrow.gif")); //$NON-NLS-1$
		previousChanged.setToolTipText(message.getString("Previous_Changed")); //$NON-NLS-1$
		previousChanged.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				getXliffEditorWindow().showPreviousChanged();
				xliffEditorObserver.update(null, null);
			}
		});

		ToolItem nextChanged = new ToolItem(toolBar, SWT.NONE);
		nextChanged.setImage(new Image(display, "images/greenrightarrow.gif")); //$NON-NLS-1$
		nextChanged.setToolTipText(message.getString("Next_Changed")); //$NON-NLS-1$
		nextChanged.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				getXliffEditorWindow().showNextChanged();
				xliffEditorObserver.update(null, null);
			}
		});

		new ToolItem(toolBar, SWT.SEPARATOR);

		ToolItem previousToCheck = new ToolItem(toolBar, SWT.NONE);
		previousToCheck.setImage(new Image(display, "images/redback.gif")); //$NON-NLS-1$
		previousToCheck.setToolTipText(message.getString("Previous_To_Check")); //$NON-NLS-1$
		previousToCheck.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				getXliffEditorWindow().showPreviousToCheck();
				xliffEditorObserver.update(null, null);
			}
		});

		ToolItem nextToCheck = new ToolItem(toolBar, SWT.NONE);
		nextToCheck.setImage(new Image(display, "images/redforward.gif")); //$NON-NLS-1$
		nextToCheck.setToolTipText(message.getString("Next_To_Check")); //$NON-NLS-1$
		nextToCheck.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				getXliffEditorWindow().showNextToCheck();
				xliffEditorObserver.update(null, null);
			}
		});

		new ToolItem(toolBar, SWT.SEPARATOR);

		ToolItem nextUntranslated = new ToolItem(toolBar, SWT.NONE);
		nextUntranslated.setImage(new Image(display, "images/greenforward.gif")); //$NON-NLS-1$
		nextUntranslated.setToolTipText(message.getString("Next_Untranslated")); //$NON-NLS-1$
		nextUntranslated.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				getXliffEditorWindow().showNextUntranslatedSegment();
				xliffEditorObserver.update(null, null);
			}
		});

		new ToolItem(toolBar, SWT.SEPARATOR);

		ToolItem nextUnapproved = new ToolItem(toolBar, SWT.NONE);
		nextUnapproved.setImage(new Image(display, "images/yellowforward.gif")); //$NON-NLS-1$
		nextUnapproved.setToolTipText(message.getString("Next_Unapproved")); //$NON-NLS-1$
		nextUnapproved.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				getXliffEditorWindow().showNextUnapprovedSegment();
				xliffEditorObserver.update(null, null);
			}
		});

		new ToolItem(toolBar, SWT.SEPARATOR);

		ToolItem nextNonTransFuzzy = new ToolItem(toolBar, SWT.NONE);
		nextNonTransFuzzy.setImage(new Image(display, "images/nextnontransfuzzy.gif")); //$NON-NLS-1$
		nextNonTransFuzzy.setToolTipText(message.getString("Next_NonTrans")); //$NON-NLS-1$
		nextNonTransFuzzy.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				getXliffEditorWindow().showNextNotTranslatedOrFuzzySegment();
				xliffEditorObserver.update(null, null);
			}
		});

		new ToolItem(toolBar, SWT.SEPARATOR);

		ToolItem showTags = new ToolItem(toolBar, SWT.NONE);
		showTags.setImage(new Image(display, "images/tags.gif")); //$NON-NLS-1$
		showTags.setToolTipText(message.getString("showTags")); //$NON-NLS-1$
		showTags.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				// getXliffEditorWindow().showNextNotTranslatedOrFuzzySegment();
				// xliffEditorObserver.update(null, null);
				TransUnitInformationData transData = getXliffEditorWindow().getCurrentTransUnitInformation();
				if ((tagWindow == null) || (tagWindow.isBIsDisposed()))
				{
					tagWindow = new TagWindow(shell, SWT.NONE, getXliffEditorWindow().getIOldSegmentPosition(), transData.getFullSourceText(),
							transData.getFullTargetText());
					tagWindow.addObserver(tagWindowObserver);
				}
				else
					tagWindow.update(getXliffEditorWindow().getIOldSegmentPosition(), transData.getFullSourceText(), transData.getFullTargetText());
				tagWindow.show();
			}
		});

		// toolBar.pack();
	}

	/**
	 * createSegmentBar
	 * 
	 * @param toolbarHolder2
	 */
	private void createSegmentBar(SashForm toolbarHolder2)
	{
		// Composite nextleftBar = new Composite(toolbarHolder2, SWT.BORDER);
		// nextleftBar.setLayout(new GridLayout(5, false));

		CoolBar nextleftBar = new CoolBar(toolbarHolder2, SWT.BORDER | SWT.FLAT);
		nextleftBar.setLayoutData(new GridData(GridData.FILL_BOTH));
		ToolBar segmentToolBar = new ToolBar(nextleftBar, SWT.FLAT);

		ToolItem sep1a = new ToolItem(segmentToolBar, SWT.SEPARATOR);
		gotoSeg = new Label(segmentToolBar, SWT.RIGHT);
		gotoSeg.setText(message.getString("Go_to__20"));
		gotoSeg.setToolTipText(message.getString("gotoSeg"));
		gotoSeg.pack();
		sep1a.setWidth(gotoSeg.getSize().x + 10);
		sep1a.setControl(gotoSeg);

		ToolItem sep1b = new ToolItem(segmentToolBar, SWT.SEPARATOR);
		jumpText = new Combo(segmentToolBar, SWT.DROP_DOWN);
		jumpText.setText("0");
		jumpText.setEnabled(true);
		jumpText.setToolTipText(message.getString("jumpText"));

		jumpText.addKeyListener(new KeyListener()
		{

			public void keyPressed(KeyEvent key)
			{
				if (key.keyCode == 13)
				{
					gotoSegment(jumpText.getText());
					jumpText.setToolTipText(message.getString("jumpText") + " :" + jumpText.getSelectionIndex() + "");
				}
			}

			public void keyReleased(KeyEvent arg0)
			{
				// do nothing
			}
		});
		jumpText.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				gotoSegment(jumpText.getSelectionIndex());
				jumpText.setToolTipText(message.getString("jumpText") + " :" + jumpText.getSelectionIndex() + "");
			}
		});

		GridData jumpData = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
		jumpData.widthHint = 40;
		jumpText.setLayoutData(jumpData);
		jumpText.pack();
		sep1b.setWidth(jumpText.getSize().x + 10);
		sep1b.setControl(jumpText);

		ToolItem sep1c = new ToolItem(segmentToolBar, SWT.SEPARATOR);
		jump = new Button(segmentToolBar, SWT.CENTER);
		jump.setText(message.getString("&Go_22"));
		jump.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				gotoSegment(jumpText.getSelectionIndex());
				jumpText.setToolTipText(message.getString("jumpText") + " :" + jumpText.getSelectionIndex() + "");
			}
		});
		jump.setToolTipText(message.getString("jump"));
		jump.pack();
		sep1c.setWidth(jump.getSize().x + 20);
		sep1c.setControl(jump);

		@SuppressWarnings("unused")
		ToolItem sep1y = new ToolItem(segmentToolBar, SWT.SEPARATOR);

		ToolItem sep1d = new ToolItem(segmentToolBar, SWT.SEPARATOR);
		segmentNumber = new Label(segmentToolBar, SWT.NONE);
		segmentNumber.setText(message.getString("_Segmentnumber") + 0 + "      ");
		segmentNumber.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
		// segmentNumber.setLayout(new GridLayout());
		segmentNumber.setToolTipText(message.getString("segmentNumber"));
		segmentNumber.pack();
		sep1d.setWidth(segmentNumber.getSize().x + 100);
		sep1d.setControl(segmentNumber);

		@SuppressWarnings("unused")
		ToolItem sep1x = new ToolItem(segmentToolBar, SWT.SEPARATOR);

		ToolItem sep1e = new ToolItem(segmentToolBar, SWT.SEPARATOR);
		matchNumber = new Label(segmentToolBar, SWT.NONE);
		matchNumber.setText(message.getString("_matchNumber") + "-----");
		matchNumber.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
		matchNumber.setToolTipText(message.getString("matchNumber"));
		matchNumber.pack();
		sep1e.setWidth(matchNumber.getSize().x + 100);
		sep1e.setControl(matchNumber);
		jumpText.setToolTipText(message.getString("jumpText") + " :" + segmentNumber.getText());

		segmentToolBar.pack();

		CoolItem coolItem1 = new CoolItem(nextleftBar, SWT.DROP_DOWN);
		coolItem1.setControl(segmentToolBar);

		Point toolBar1Size = segmentToolBar.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		Point coolBar1Size = coolItem1.computeSize(toolBar1Size.x, toolBar1Size.y);
		coolItem1.setSize(coolBar1Size);

		class CoolBarListenerSegment extends SelectionAdapter
		{
			public void widgetSelected(SelectionEvent event)
			{
				if (event.detail == SWT.ARROW)
				{
					ToolBar toolBar = (ToolBar) ((CoolItem) event.widget).getControl();
					CoolBar coolBar = (CoolBar) toolBar.getParent();

					if (menuCoolbar != null)
					{
						menuCoolbar.dispose();
					}
					menuCoolbar = new Menu(coolBar);

					MenuItem menuItem = new MenuItem(menuCoolbar, SWT.PUSH);
					menuItem.setText(segmentNumber.getText());
					menuItem = new MenuItem(menuCoolbar, SWT.PUSH);
					menuItem.setText(matchNumber.getText());

					Point menuPoint = coolBar.toDisplay(new Point(event.x, event.y));
					menuCoolbar.setLocation(menuPoint.x, menuPoint.y);
					menuCoolbar.setVisible(true);
				}
			}
		}

		coolItem1.addSelectionListener(new CoolBarListenerSegment());
	}

	/**
	 * createXliffEditorWithToolBar
	 * 
	 * @return
	 */
	private SashForm createXliffEditorWithToolBar(SashForm parentHolder)
	{
		int iGridData = GridData.GRAB_VERTICAL | GridData.VERTICAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL;

		GridLayout toolsLayout = new GridLayout(1, true);
		toolsLayout.marginWidth = 1;
		toolsLayout.marginHeight = 1;
		toolsLayout.horizontalSpacing = 0;

		xliffFileHolder = new SashForm(parentHolder, SWT.NONE);
		xliffFileHolder.setOrientation(SWT.VERTICAL);
		GridLayout segmentToolsLayout = new GridLayout(1, true);
		segmentToolsLayout.marginWidth = 1;
		segmentToolsLayout.marginHeight = 1;
		segmentToolsLayout.horizontalSpacing = 0;
		xliffFileHolder.setLayout(toolsLayout);
		xliffFileHolder.setLayoutData(new GridData(iGridData));

		xliffEditorWindow = new XliffEditorWindow(shell, this.xliffFileHolder, SWT.MULTI | SWT.V_SCROLL | SWT.WRAP | SWT.BORDER);
		// xliffEditorWindow.setLayout(new GridLayout(1, true));
		int iGridDataxliffEditorWindow = GridData.GRAB_VERTICAL | GridData.VERTICAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL;
		GridData textgridentry = new GridData(iGridDataxliffEditorWindow);
		xliffEditorWindow.setLayoutData(textgridentry);
		xliffEditorWindow.setToolTipText(message.getString("xliffEditorWindow"));

		xliffEditorObserver = new XliffEditorObserver(this);
		xliffEditorWindow.addObserver(xliffEditorObserver);
		xliffEditorWindow.setPreferencesContainer(preferencesContainer);

		targetTextWindow = new SimpleXliffEditorWindow(this.xliffFileHolder, SWT.MULTI | SWT.V_SCROLL | SWT.WRAP | SWT.BORDER);
		targetTextWindow.setLayoutData(textgridentry);
		targetTextWindow.setBackground(ColorTable.getInstance(getDisplay(), "white"));
		targetTextWindow.setBChangeBackGroundOnChange(true);
		targetTextWindow.setToolTipText(message.getString("targetTextWindow"));

		targetTextWindow.addObserver(new SimpleXliffEditorObserver(this));
		targetTextWindow.addVerifyKeyListener(new VerifyKeyListener()
		{
			public void verifyKey(VerifyEvent event)
			{
				try
				{
					// int keyCode = event.keyCode;
					// int caretOffset = getCaretOffset();
					// StyleRange style = (StyleRange)
					// getStyleRangeAtOffset(caretOffset);
					// System.out.println(keyCode + " / " + caretOffset + " / "
					// + (int)SWT.BS + " / " + style);
					// we must check if the <s ...> is in the selected text - if
					// any is selected
					int iKey = event.keyCode;
					// String key = Character.toString(e.character);
					// here we should check if it is not a function key...
					event.doit = true;
					switch (iKey)
					{
					case SWT.F1:
						return;
					case SWT.F2:
						return;
					case SWT.F3:
						return;
					case SWT.F4:
						return;
					case SWT.F5:
						return;
					case SWT.F6:
						return;
					case SWT.F7:
						return;
					case SWT.F8:
						return;
					case SWT.F9:
						return;
					case SWT.F10:
						return;
					case SWT.F11:
						return;
					case SWT.F12:
						return;
					case SWT.ARROW_DOWN:
						return;
					case SWT.ARROW_UP:
						return;
					case SWT.ARROW_LEFT:
						return;
					case SWT.ARROW_RIGHT:
						return;
					case SWT.PAGE_DOWN:
						return;
					case SWT.PAGE_UP:
						return;
					case SWT.INSERT:
						return;
					case SWT.SHIFT:
						return;
					case SWT.DEL:
						return;
					case SWT.ESC:
						return;
					case SWT.BS:
						return;
					}
					SimpleXliffEditorWindow widget = (SimpleXliffEditorWindow) event.widget;

					int iPosition = xliffEditorWindow.getCaretOffset();
					TransUnitInformationData trans = xliffEditorWindow.getCurrentTransUnitInformation(iPosition);
					// add some checks for length here....
					int result = XliffEditorForm.segmentLengthCheck(targetTextWindow.getText(), widget.getShell(), trans, message);
					if ((result == SWT.NO) || (result == SWT.OK))
					{
						event.doit = false;
						return;
					}
					event.doit = true;
					xliffEditorWindow.getBChangedSegments()[trans.getISegmentNumber()] = true;
					xliffEditorWindow.setBChanged(true);
					// if (result == SWT.YES)
					// xliffEditorWindow.notifyObservers();
				}
				catch (Exception ex)
				{

				}
			}
		});

		targetTextWindow.addKeyListener(new KeyListener()
		{
			public void keyPressed(KeyEvent e)
			{
				@SuppressWarnings("unused")
				int iKey = e.keyCode;
				return;
			}

			public void keyReleased(KeyEvent e)
			{
				xliffEditorWindow.notifyObservers();
			}
		});

		workAreaHolder = new SashForm(xliffFileHolder, SWT.NONE);
		workAreaHolder.setOrientation(SWT.HORIZONTAL);
		GridData workAreaHoldergridentry = new GridData(iGridDataxliffEditorWindow);
		workAreaHolder.setLayoutData(workAreaHoldergridentry);

		// now all the following tools go to the workAreaHolder
		// this is a dataSource chooser form (dataSourceForms) and right the
		// other tools

		SashForm dataSourceForms = createDataSourceForm(workAreaHolder);
		setDataSourceHolders(dataSourceForms);

		toolsAreaHolder = new SashForm(workAreaHolder, SWT.NONE);
		toolsAreaHolder.setOrientation(SWT.VERTICAL);
		GridData toolsAreaHoldergridentry = new GridData(iGridDataxliffEditorWindow);
		toolsAreaHolder.setLayoutData(toolsAreaHoldergridentry);

		tasksHolder = new SashForm(toolsAreaHolder, SWT.NONE);
		tasksHolder.setOrientation(SWT.HORIZONTAL);
		GridData gridentry3 = new GridData(iGridDataxliffEditorWindow);
		// tasksHolder.setLayout(new GridLayout(1, true));
		tasksHolder.setLayoutData(gridentry3);

		toolbarHolder = new SashForm(tasksHolder, SWT.NONE);
		toolbarHolder.setOrientation(SWT.VERTICAL);
		GridData gridentry2 = new GridData(iGridDataxliffEditorWindow);
		// toolbarHolder.setLayout(new GridLayout(1, true));
		toolbarHolder.setLayoutData(gridentry2);

		// changes to be made here
		// ToolBar navigationToolBar = new ToolBar(toolbarHolder, SWT.NONE |
		// SWT.BORDER);
		// createNavigationToolBar(navigationToolBar);

		CoolBar coolBar = new CoolBar(toolbarHolder, SWT.BORDER | SWT.FLAT);
		coolBar.setLayoutData(new GridData(GridData.FILL_BOTH));
		// ToolBar navigationToolBar = new ToolBar(toolbarHolder, SWT.NONE |
		// SWT.BORDER)
		ToolBar navigationToolBar = new ToolBar(coolBar, SWT.FLAT); // SWT.NONE
		// |
		// SWT.BORDER);
		// createNavigationToolBar(navigationToolBar);
		createNavigationToolBar(coolBar, navigationToolBar);

		CoolBar coolBar1 = new CoolBar(toolbarHolder, SWT.BORDER | SWT.FLAT);
		coolBar.setLayoutData(new GridData(GridData.FILL_BOTH));
		ToolBar dataSourceToolBar = new ToolBar(coolBar1, SWT.NONE | SWT.BORDER);
		this.createDataSourceToolBar(coolBar1, dataSourceToolBar);

		String sim = fuzzysim.getText();
		int similarity = 70;
		try
		{
			similarity = Integer.parseInt(sim);
		}
		catch (Exception e)
		{
		}
		xliffEditorWindow.setISimilarity(similarity);

		toolbarHolder.setWeights(new int[] { 1, 1 });

		statusHolder = new SashForm(tasksHolder, SWT.NONE);
		statusHolder.setOrientation(SWT.VERTICAL);
		// statusHolder.setLayout(new GridLayout(1, true));
		statusHolder.setLayoutData(gridentry2);

		createSegmentBar(statusHolder);

		statusDisplayWindow = new OpenTMSXMLStyledText(this.statusHolder, SWT.MULTI | SWT.V_SCROLL | SWT.WRAP | SWT.BORDER | SWT.READ_ONLY);
		// statusDisplayWindow.setLayout(new GridLayout(1, true));
		statusDisplayWindow.setLayoutData(textgridentry);
		statusDisplayWindow.setBackground(ColorTable.getInstance(getDisplay(), "lightgray"));
		statusDisplayWindow.setBChangeBackGroundOnChange(false);
		statusDisplayWindow.setToolTipText(message.getString("statusDisplayWindow"));

		statusHolder.setWeights(new int[] { 1, 1 });

		matchFileHolder = new SashForm(toolsAreaHolder, SWT.NONE);
		matchFileHolder.setOrientation(SWT.HORIZONTAL);
		GridLayout matchFileHolderLayout = new GridLayout(2, false);
		matchFileHolderLayout.marginWidth = 1;
		matchFileHolderLayout.marginHeight = 1;
		matchFileHolderLayout.horizontalSpacing = 0;
		// matchFileHolder.setLayout(new GridLayout(1, true)); //
		// matchFileHolderLayout);
		matchFileHolder.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));

		// add the selection list for matches
		matchSourceFileHolder = new SashForm(matchFileHolder, SWT.NONE);
		matchSourceFileHolder.setOrientation(SWT.VERTICAL);
		altSourceMatchesCombo = new Combo(matchSourceFileHolder, SWT.DROP_DOWN | SWT.READ_ONLY);
		altSourceMatchesCombo.setToolTipText(message.getString("altSourceMatchesCombo"));
		altSourceMatchesCombo.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0)
			{
				if (getIOldSegmentPosition() == -1)
					return;
				int iIndex = altSourceMatchesCombo.getSelectionIndex();
				showAltTrans(getIOldSegmentPosition(), iIndex, 0);
			}

			public void widgetSelected(SelectionEvent e)
			{
				if (getIOldSegmentPosition() == -1)
					return;
				int iIndex = altSourceMatchesCombo.getSelectionIndex();
				showAltTrans(getIOldSegmentPosition(), iIndex, 0);
			}
		});

		altSourceWindow = new OpenTMSXMLStyledText(matchSourceFileHolder, SWT.MULTI | SWT.V_SCROLL | SWT.WRAP | SWT.BORDER | SWT.READ_ONLY);
		altSourceWindow.setBChangeBackGroundOnChange(false);
		altSourceWindow.setToolTipText(message.getString("altSourceWindow"));
		matchSourceFileHolder.setWeights(new int[] { 1, 4 });

		matchTargetFileHolder = new SashForm(matchFileHolder, SWT.NONE);
		matchTargetFileHolder.setOrientation(SWT.VERTICAL);
		altTargetMatchesCombo = new Combo(matchTargetFileHolder, SWT.DROP_DOWN | SWT.READ_ONLY);
		altTargetMatchesCombo.setToolTipText(message.getString("altTargetMatchesCombo"));
		altTargetMatchesCombo.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0)
			{
				if (getIOldSegmentPosition() == -1)
					return;
				int iIndexSource = altSourceMatchesCombo.getSelectionIndex();
				int iIndexTarget = altTargetMatchesCombo.getSelectionIndex();
				showAltTrans(getIOldSegmentPosition(), iIndexSource, iIndexTarget);
			}

			public void widgetSelected(SelectionEvent e)
			{
				if (getIOldSegmentPosition() == -1)
					return;
				int iIndexSource = altSourceMatchesCombo.getSelectionIndex();
				int iIndexTarget = altTargetMatchesCombo.getSelectionIndex();
				showAltTrans(getIOldSegmentPosition(), iIndexSource, iIndexTarget);
			}
		});

		altTargetWindow = new OpenTMSXMLStyledText(matchTargetFileHolder, SWT.MULTI | SWT.V_SCROLL | SWT.WRAP | SWT.BORDER | SWT.READ_ONLY);
		altTargetWindow.setBChangeBackGroundOnChange(false);
		altTargetWindow.setToolTipText(message.getString("altTargetWindow"));
		matchTargetFileHolder.setWeights(new int[] { 1, 4 });

		setAltTransTagDescriptors();

		tasksHolder.setWeights(new int[] { 1, 1 });

		toolsAreaHolder.setWeights(new int[] { 2, 3 });

		workAreaHolder.setWeights(new int[] { 1, 5 });

		xliffFileHolder.setWeights(new int[] { 15, 1, 5 });

		// xliffFileHolder.pack();
		xliffFileHolder.layout();

		tagWindowObserver = new TagWindowObserver(this);

		return xliffFileHolder;

	}

	/**
	 * @return the dataSourceHolders
	 */
	public SashForm getDataSourceHolders()
	{
		return dataSourceHolders;
	}

	/**
	 * @return the iOldSegmentPosition
	 */
	public int getIOldSegmentPosition()
	{
		return xliffEditorWindow.getIOldSegmentPosition();
	}

	/**
	 * @return the jumpText
	 */
	public Combo getJumpText()
	{
		return jumpText;
	}

	private Color getMatchQualityColor(String quality)
	{
		int iQuality = 0;

		if (quality.equals("MT"))
			return ColorTable.getInstance(getDisplay(), "lightgrey");
		if (quality.equals("IATE"))
			return ColorTable.getInstance(getDisplay(), "darkgray");

		try
		{
			iQuality = Integer.parseInt(quality);
		}
		catch (NumberFormatException e)
		{
			// e.printStackTrace();
			return ColorTable.getInstance(getDisplay(), "slategray");
		}

		if (iQuality == 100)
		{
			return ColorTable.getInstance(getDisplay(), "lightblue0");
		}
		if (iQuality >= 90)
		{
			return ColorTable.getInstance(getDisplay(), "lightblue1");
		}
		if (iQuality >= 80)
		{
			return ColorTable.getInstance(getDisplay(), "lightblue2");
		}
		if (iQuality >= 70)
		{
			return ColorTable.getInstance(getDisplay(), "lightblue3");
		}
		if (iQuality >= 60)
		{
			return ColorTable.getInstance(getDisplay(), "lightblue4");
		}

		return ColorTable.getInstance(getDisplay(), "white");
	}

	public Vector<PhraseTranslateResult> getPhrases(int iSegNum)
	{
		try
		{
			Element transunit = this.getXliffEditorWindow().getTransUnits().get(iSegNum);
			Vector<PhraseTranslateResult> phrasesElement = xliffDocument.getTransUnitPhraseEntries(transunit);

			return phrasesElement;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @return the optionsContainer
	 */
	public PreferencesContainer getPreferencesContainer()
	{
		return preferencesContainer;
	}

	/**
	 * @return the ptDataSourceFormComposite
	 */
	public DataSourceListWithTools getPtDataSourceFormComposite()
	{
		return ptDataSourceFormComposite;
	}

	/**
	 * @return the sourceLanguage
	 */
	public String getSourceLanguage()
	{
		return sourceLanguage;
	}

	/**
	 * @return the statusDisplayWindow
	 */
	public OpenTMSXMLStyledText getStatusDisplayWindow()
	{
		return statusDisplayWindow;
	}

	/**
	 * @return the targetLanguage
	 */
	public String getTargetLanguage()
	{
		return targetLanguage;
	}

	/**
	 * @return the statusWindow
	 */
	public SimpleXliffEditorWindow getTargetTextWindow()
	{
		return targetTextWindow;
	}

	/**
	 * @return the tmDataSourceFormComposite
	 */
	public DataSourceListWithTools getTmDataSourceFormComposite()
	{
		return tmDataSourceFormComposite;
	}

	/**
	 * @return the xliffEditor
	 */
	public XliffEditor getXliffEditor()
	{
		return xliffEditor;
	}

	/**
	 * @return the xliffEditorDictionaryViewer
	 */
	public XliffEditorDictionaryViewer getXliffEditorDictionaryViewer()
	{
		return xliffEditorDictionaryViewer;
	}

	/**
	 * @return the xliffEditorObserver
	 */
	public XliffEditorObserver getXliffEditorObserver()
	{
		return xliffEditorObserver;
	}

	/**
	 * @return the xliffEditorSegmentDictionaryViewer
	 */
	public XliffEditorDictionaryViewer getXliffEditorSegmentDictionaryViewer()
	{
		return xliffEditorSegmentDictionaryViewer;
	}

	/**
	 * @return the xliffEditorWindow
	 */
	public XliffEditorWindow getXliffEditorWindow()
	{
		return xliffEditorWindow;
	}

	/**
	 * @return the xliffFile
	 */
	public String getXliffFile()
	{
		return xliffFile;
	}

	/**
	 * gotoSegment
	 * 
	 * @param num
	 */
	protected void gotoSegment(int num)
	{
		this.xliffEditorWindow.gotoSegment(num);
		xliffEditorObserver.update(null, null);
	}

	protected void gotoSegment(String num)
	{
		this.xliffEditorWindow.gotoSegment(num);
		xliffEditorObserver.update(null, null);
	}

	/**
	 * @return the bXliffEditorFormCreated
	 */
	public boolean isBXliffEditorFormCreated()
	{
		return bXliffEditorFormCreated;
	}

	/**
	 * loadDefaultDataSources
	 * @param syncServerDataSource 
	 */
	public void loadDefaultDataSources(String syncServerDataSource)
	{
		if (preferencesContainer == null)
			return;

		String ptSources = preferencesContainer.getPtDataSources();
		if (!ptSources.equals(""))
		{
			String[] sources = ptSources.split(";");
			for (int i = 0; i < sources.length; i++)
			{
				this.ptDataSourceFormComposite.addDataSource(sources[i]);
				if (i == 0)
					this.ptDataSourceFormComposite.getDataSourcesList().setSelection(0);
			}
		}
		
		// check if we have sync server data source 
		if (syncServerDataSource != null)
		{
			this.tmDataSourceFormComposite.addDataSource(syncServerDataSource);
		}
		
		String tmSources = preferencesContainer.getTmDataSources();
		if (!tmSources.equals(""))
		{
			String[] sources = tmSources.split(";");
			for (int i = 0; i < sources.length; i++)
			{
				this.tmDataSourceFormComposite.addDataSource(sources[i]);
				if (i == 0)
					this.tmDataSourceFormComposite.getDataSourcesList().setSelection(0);
			}
		}
		

	}

	/**
	 * loadXliffFile
	 * 
	 * @param xliffFileName
	 */
	public boolean loadXliffFile(String xliffFileName)
	{
		// there are several checks in here if the source / target language is
		// really set correct - must bei improved in the future...
		Element file = null;
		xliffDocument = new XliffDocument();
		xliffDocument.loadXmlFile(xliffFileName);
		List<Element> files = xliffDocument.getFiles();
		if (files.size() == 0)
		{
			System.out.println("No <file> element found for " + xliffFileName);
			return false;
		}
		file = files.get(0);
		sourceLanguage = file.getAttributeValue("source-language"); // ,
		// xliffDocument.getNamespace());
		targetLanguage = file.getAttributeValue("target-language"); // ,
		// xliffDocument.getNamespace());

		boolean bSourceTargetLanguageChanged = false;

		if ((sourceLanguage == null) || sourceLanguage.equals(""))
		{
			// display target window
			LanguageRequest request = new LanguageRequest(display, true);
			request.show();
			sourceLanguage = request.getLanguage();
			if (!(sourceLanguage == null) && !sourceLanguage.equals(""))
			{
				file.setAttribute("source-language", sourceLanguage); // ,
				// xliffDocument.getNamespace());
				bSourceTargetLanguageChanged = true;
			}
		}

		if ((targetLanguage == null) || targetLanguage.equals(""))
		{
			// display target window
			LanguageRequest request = new LanguageRequest(display, false);
			request.show();
			targetLanguage = request.getLanguage();
			if (!(targetLanguage == null) && !targetLanguage.equals(""))
			{
				file.setAttribute("target-language", targetLanguage); // ,
				// xliffDocument.getNamespace());
				bSourceTargetLanguageChanged = true;
			}
		}

		xliffEditorWindow.setXliffDocument(xliffDocument);
		xliffDocument.setSourceLanguage(sourceLanguage);
		xliffDocument.setTargetLanguage(targetLanguage);
		// xliffEditorWindow.pack();
		xliffEditorWindow.loadXliffFile(file, "sourcetarget");
		sourceLanguage = xliffEditorWindow.getSourceLanguage();
		targetLanguage = xliffEditorWindow.getTargetLanguage();

		if ((xliffDocument.getSourceLanguage() == null) || xliffDocument.getSourceLanguage().equals(""))
		{
			xliffDocument.setSourceLanguage(sourceLanguage);
		}
		if ((xliffDocument.getTargetLanguage() == null) || xliffDocument.getTargetLanguage().equals(""))
		{
			xliffDocument.setTargetLanguage(targetLanguage);
		}
		for (int i = 0; i < xliffEditorWindow.getTransUnits().size(); i++)
		{
			jumpText.add("" + i);
		}

		if (bSourceTargetLanguageChanged) // must do it here because
		// xliffEditorWindow.loadXliffFile(file,
		// "sourcetarget") changes the
		// bChanged status!
		{
			this.getXliffEditorWindow().setBChanged(true);
		}
		return true;
	}

	/**
	 * openXliffFile
	 * 
	 * @param xliffFile
	 * 
	 */
	private void openXliffFile(String xliffFile)
	{
		try
		{
			this.setXliffFile(xliffFile);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	/**
	 * removeAllTranslations remove all translation in document
	 */
	public void removeAllTranslations()
	{
		for (int k = 0; k < this.xliffEditorWindow.getIOverallSegmentNumber(); k++)
		{
			this.xliffEditorWindow.gotoSegment(k, true);
			int iRes = this.xliffEditorWindow.setTranslation(k, "", false);
			if (iRes == SWT.YES)
				this.xliffEditorWindow.getBTranslatedSegments()[k] = false;
		}
		this.showAltTrans(this.xliffEditorWindow.getIOverallSegmentNumber() - 1, 0, 0);
		segmentNumber.setText(message.getString("_Segmentnumber") + " " + (this.xliffEditorWindow.getIOverallSegmentNumber() - 1) + "/"
				+ this.getXliffEditorWindow().getIOverallSegmentNumber() + " (" + this.getXliffEditorWindow().countApprovedSegments() + "/"
				+ this.getXliffEditorWindow().countTranslatedSegments() + ")");

	}

	/**
	 * removeTranslation remove translation of segment
	 * 
	 * @param iSegNum
	 *            the segment to remove the translation for
	 */
	public void removeTranslation(int iSegNum)
	{
		this.xliffEditorWindow.gotoSegment(iSegNum, true);
		int iRes = this.xliffEditorWindow.setTranslation(iSegNum, "", false);
		if (iRes == SWT.YES)
			this.xliffEditorWindow.getBTranslatedSegments()[iSegNum] = false;

		this.showAltTrans(iSegNum, 0, 0);
		segmentNumber.setText(message.getString("_Segmentnumber") + " " + iSegNum + "/" + this.getXliffEditorWindow().getIOverallSegmentNumber()
				+ " (" + this.getXliffEditorWindow().countApprovedSegments() + "/" + this.getXliffEditorWindow().countTranslatedSegments() + ")");

	}

	protected void saveXliffDocument()
	{
		saveXliffDocument(this.xliffDocument.getXmlDocumentName());
	}

	/**
	 * saveXliffDocument
	 */

	protected void saveXliffDocument(String filename)
	{
		this.xliffEditorWindow.saveXliffDocument(filename);
	}

	private void setAltTransTagDescriptors()
	{
		TagDescriptor tagDescriptorPh = new TagDescriptor("ph", ColorTable.getInstance(getDisplay(), "black"), ColorTable.getInstance(getDisplay(),
				"darkorange"));

		TagDescriptor tagDescriptorUt = new TagDescriptor("ut", ColorTable.getInstance(getDisplay(), "black"), ColorTable.getInstance(getDisplay(),
				"orange"));
		TagDescriptor tagDescriptorIt = new TagDescriptor("it", ColorTable.getInstance(getDisplay(), "black"), ColorTable.getInstance(getDisplay(),
				"orangered"));
		TagDescriptor tagDescriptorHi = new TagDescriptor("hi", ColorTable.getInstance(getDisplay(), "black"), ColorTable.getInstance(getDisplay(),
				"purple"));
		TagDescriptor tagDescriptorEpt = new TagDescriptor("ebt", ColorTable.getInstance(getDisplay(), "black"), ColorTable.getInstance(getDisplay(),
				"palevioletred"));
		TagDescriptor tagDescriptorBpt = new TagDescriptor("bpt", ColorTable.getInstance(getDisplay(), "black"), ColorTable.getInstance(getDisplay(),
				"indianred"));
		TagDescriptor tagDescriptorSub = new TagDescriptor("sub", ColorTable.getInstance(getDisplay(), "black"), ColorTable.getInstance(getDisplay(),
				"mediumvioletred"));

		// xliff
		TagDescriptor tagDescriptorG = new TagDescriptor("g", ColorTable.getInstance(getDisplay(), "black"), ColorTable.getInstance(getDisplay(),
				"cadetblue"));
		TagDescriptor tagDescriptorX = new TagDescriptor("x", ColorTable.getInstance(getDisplay(), "black"), ColorTable.getInstance(getDisplay(),
				"blue"));
		TagDescriptor tagDescriptorBx = new TagDescriptor("bx", ColorTable.getInstance(getDisplay(), "black"), ColorTable.getInstance(getDisplay(),
				"blueviolet"));
		TagDescriptor tagDescriptorEx = new TagDescriptor("ex", ColorTable.getInstance(getDisplay(), "black"), ColorTable.getInstance(getDisplay(),
				"cadetblue"));
		TagDescriptor tagDescriptorMrk = new TagDescriptor("mrk", ColorTable.getInstance(getDisplay(), "black"), ColorTable.getInstance(getDisplay(),
				"cornflowerblue"));

		altSourceWindow.addTagDescriptor(tagDescriptorG);
		altSourceWindow.addTagDescriptor(tagDescriptorPh);
		altSourceWindow.addTagDescriptor(tagDescriptorUt);
		altSourceWindow.addTagDescriptor(tagDescriptorIt);
		altSourceWindow.addTagDescriptor(tagDescriptorHi);
		altSourceWindow.addTagDescriptor(tagDescriptorEpt);
		altSourceWindow.addTagDescriptor(tagDescriptorBpt);
		altSourceWindow.addTagDescriptor(tagDescriptorSub);
		altSourceWindow.addTagDescriptor(tagDescriptorX);
		altSourceWindow.addTagDescriptor(tagDescriptorBx);
		altSourceWindow.addTagDescriptor(tagDescriptorEx);
		altSourceWindow.addTagDescriptor(tagDescriptorMrk);

		altTargetWindow.addTagDescriptor(tagDescriptorG);
		altTargetWindow.addTagDescriptor(tagDescriptorPh);
		altTargetWindow.addTagDescriptor(tagDescriptorUt);
		altTargetWindow.addTagDescriptor(tagDescriptorIt);
		altTargetWindow.addTagDescriptor(tagDescriptorHi);
		altTargetWindow.addTagDescriptor(tagDescriptorEpt);
		altTargetWindow.addTagDescriptor(tagDescriptorBpt);
		altTargetWindow.addTagDescriptor(tagDescriptorSub);
		altTargetWindow.addTagDescriptor(tagDescriptorX);
		altTargetWindow.addTagDescriptor(tagDescriptorBx);
		altTargetWindow.addTagDescriptor(tagDescriptorEx);
		altTargetWindow.addTagDescriptor(tagDescriptorMrk);
	}

	/**
	 * @param bXliffEditorFormCreated
	 *            the bXliffEditorFormCreated to set
	 */
	public void setBXliffEditorFormCreated(boolean bXliffEditorFormCreated)
	{
		this.bXliffEditorFormCreated = bXliffEditorFormCreated;
	}

	/**
	 * @param dataSourceHolders
	 *            the dataSourceHolders to set
	 */
	public void setDataSourceHolders(SashForm dataSourceHolders)
	{
		this.dataSourceHolders = dataSourceHolders;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.widgets.Control#setFont(org.eclipse.swt.graphics.Font)
	 */
	public void setFont(Font font)
	{
		super.setFont(font);
		this.xliffEditorWindow.setFont(font);
	}

	/**
	 * @param oldSegmentPosition
	 *            the iOldSegmentPosition to set
	 */
	public void setIOldSegmentPosition(int oldSegmentPosition)
	{
		xliffEditorWindow.setIOldSegmentPosition(oldSegmentPosition);
	}

	/**
	 * @param PreferencesContainer
	 *            the optionsContainer to set
	 */
	public void setPreferencesContainer(PreferencesContainer preferencesContainer)
	{
		if (xliffEditorWindow != null)
			xliffEditorWindow.setPreferencesContainer(preferencesContainer);
		this.preferencesContainer = preferencesContainer;
	}

	/**
	 * @param ptDataSourceFormComposite
	 *            the ptDataSourceFormComposite to set
	 */
	public void setPtDataSourceFormComposite(DataSourceListWithTools ptDataSourceFormComposite)
	{
		this.ptDataSourceFormComposite = ptDataSourceFormComposite;
	}

	/**
	 * @param sourceLanguage
	 *            the sourceLanguage to set
	 */
	public void setSourceLanguage(String sourceLanguage)
	{
		this.sourceLanguage = sourceLanguage;
	}

	/**
	 * @param statusDisplayWindow
	 *            the statusDisplayWindow to set
	 */
	public void setStatusDisplayWindow(OpenTMSXMLStyledText statusDisplayWindow)
	{
		this.statusDisplayWindow = statusDisplayWindow;
	}

	/**
	 * @param targetLanguage
	 *            the targetLanguage to set
	 */
	public void setTargetLanguage(String targetLanguage)
	{
		this.targetLanguage = targetLanguage;
	}

	/**
	 * @param targetTextWindow
	 *            the statusWindow to set
	 */
	public void setTargetTextWindow(SimpleXliffEditorWindow targetTextWindow)
	{
		this.targetTextWindow = targetTextWindow;
	}

	/**
	 * @param tmDataSourceFormComposite
	 *            the tmDataSourceFormComposite to set
	 */
	public void setTmDataSourceFormComposite(DataSourceListWithTools tmDataSourceFormComposite)
	{
		this.tmDataSourceFormComposite = tmDataSourceFormComposite;
	}

	/**
	 * @param xliffEditor
	 *            the xliffEditor to set
	 */
	public void setXliffEditor(XliffEditor xliffEditor)
	{
		this.xliffEditor = xliffEditor;
	}

	/**
	 * @param xliffEditorDictionaryViewer
	 *            the xliffEditorDictionaryViewer to set
	 */
	public void setXliffEditorDictionaryViewer(XliffEditorDictionaryViewer xliffEditorDictionaryViewer)
	{
		this.xliffEditorDictionaryViewer = xliffEditorDictionaryViewer;
	}

	/**
	 * @param xliffEditorObserver
	 *            the xliffEditorObserver to set
	 */
	public void setXliffEditorObserver(XliffEditorObserver xliffEditorObserver)
	{
		this.xliffEditorObserver = xliffEditorObserver;
	}

	/**
	 * @param xliffEditorSegmentDictionaryViewer
	 *            the xliffEditorSegmentDictionaryViewer to set
	 */
	public void setXliffEditorSegmentDictionaryViewer(XliffEditorDictionaryViewer xliffEditorSegmentDictionaryViewer)
	{
		this.xliffEditorSegmentDictionaryViewer = xliffEditorSegmentDictionaryViewer;
	}

	/**
	 * @param xliffEditorWindow
	 *            the xliffEditorWindow to set
	 */
	public void setXliffEditorWindow(XliffEditorWindow xliffEditorWindow)
	{
		this.xliffEditorWindow = xliffEditorWindow;
	}

	/**
	 * @param xliffFile
	 *            the xliffFile to set
	 */
	public void setXliffFile(String xliffFile)
	{
		this.xliffFile = xliffFile;
	}

	/**
	 * showAltTrans show the alt-trans element information of a segment for the
	 * ith altr-trans element with target i
	 * 
	 * @param iSegnum
	 *            the segment number
	 * @param iAltTransSource
	 *            the i-the al trans
	 * @param iAltTransTarget
	 *            the j-th target of the it-th alttrans
	 */
	@SuppressWarnings("unchecked")
	public void showAltTrans(int iSegnum, int iAltTransSource, int iAltTransTarget)
	{
		altSourceMatchesCombo.removeAll();
		altTargetMatchesCombo.removeAll();
		altSourceWindow.setText("");
		altTargetWindow.setText("");
		getStatusDisplayWindow().setText("");
		matchNumber.setText("");

		Pattern ptarget = Pattern.compile("\\<target.*?>(.*?)\\</target>", Pattern.DOTALL | Pattern.MULTILINE);
		Pattern psource = Pattern.compile("\\<source.*?>(.*?)\\</source>", Pattern.DOTALL | Pattern.MULTILINE);

		Element transunit = getXliffEditorWindow().getTransUnits().get(iSegnum);
		List<Element> altranslist = transunit.getChildren("alt-trans", this.xliffDocument.getNamespace());
		currentMULId = "";
		currentId = "";
		if (altranslist.size() > 0)
		{
			for (int i = 0; i < altranslist.size(); i++)
			{
				Element altrans = altranslist.get(i);
				String quality = altrans.getAttributeValue("match-quality"); // ,
				// this.xliffDocument.getNamespace());
				String origin = altrans.getAttributeValue("origin"); // ,
				// this.xliffDocument.getNamespace());
				if (origin != null)
				{
					currentOrigin = origin.replaceAll(".*?database=(.*?)", "$1");
					currentOrigin = currentOrigin.replaceAll("jdbc\\:.*/", "");
				}
				else
					origin = "";
				// remove the url if necessary

				if (quality == null)
					quality = "";

				Color color = getMatchQualityColor(quality);
				Element source = altrans.getChild("source", this.xliffDocument.getNamespace());

				String text = xmlDoc.elementToString(source);

				Matcher m = psource.matcher(text);
				if (m.matches())
				{
					text = m.group(1);
				}
				else
					text = text.replaceAll("\\<source.*?>(.*?)\\</source>", "$1");

				if (i == iAltTransSource)
				{
					altSourceWindow.setText(text);
					altSourceWindow.setStyleRange(text);
					altSourceWindow.setBackground(color);
					matchNumber.setText(message.getString("_matchNumber") + " " + quality + " " + message.getString("_matchActualNumber")
							+ (iAltTransSource + 1) + "/" + altranslist.size());
					// ok store the multi ID
					currentMULId = xliffDocument.getAltTransMultiCreationId(altrans);
					if (currentMULId == null)
						currentMULId = "currentMULIdNoId";
					currentId = xliffDocument.getAltTransId(altrans);
					statusDisplayWindow.setText(message.getString("datasourceName") + origin + "\n" + message.getString("creationID") + currentMULId
							+ "\n" + message.getString("uniqueID") + currentId);
					
					String alt_trans = this.xliffDocument.elementToString(altrans);
					alt_trans = alt_trans.replaceAll("(<.*?>.*?</.*?>)", "$1\n");
					statusDisplayWindow.setToolTipText(alt_trans);
				}

				String ctext = text.replaceAll("\\<(\\w+?).*?>.*?\\</$1>", "");
				ctext = ctext.replaceAll("\\</?\\w+?.*?>", "");

				altSourceMatchesCombo.add(quality + ": " + ctext);
				if (i == iAltTransSource)
				{
					List<Element> targetlist = altrans.getChildren("target", this.xliffDocument.getNamespace());
					for (int j = 0; j < targetlist.size(); j++)
					{
						Element target = targetlist.get(j);
						text = xmlDoc.elementToString(target);

						m = ptarget.matcher(text);
						if (m.matches())
						{
							text = m.group(1);
						}
						else
							text = text.replaceAll("\\<target.*?>(.*?)\\</target>", "$1");
						if (j == iAltTransTarget)
						{
							altTargetWindow.setText(text);
							altTargetWindow.setStyleRange(text);
							altTargetWindow.setBackground(color);
							matchNumber.setText(message.getString("_matchNumber") + " " + quality + " " + message.getString("_matchActualNumber")
									+ " " + (iAltTransSource + 1) + "/" + altranslist.size() + " " + (iAltTransTarget + 1) + "/" + targetlist.size());
						}
						ctext = text.replaceAll("\\<(\\w+?).*?>.*?\\</$1>", "");
						ctext = ctext.replaceAll("\\</?\\w+?.*?>", "");
						altTargetMatchesCombo.add(ctext);

						origin = origin.replaceAll(".*?database=(.*?)", "$1");
					}
				}
				altSourceMatchesCombo.select(iAltTransSource);
				altTargetMatchesCombo.select(iAltTransTarget);
			}
		}
		else
		{
			altSourceWindow.setBackground(ColorTable.getInstance(getDisplay(), "lightgray"));
			altTargetWindow.setBackground(ColorTable.getInstance(getDisplay(), "lightgray"));
		}
	}

	/**
	 * translateAllSegments translate all segments displaying a progress dialog
	 */
	public void translateAllSegments()
	{
		Cursor hglass = new Cursor(display, SWT.CURSOR_WAIT);
		Cursor arrow = new Cursor(display, SWT.CURSOR_ARROW);
		shell.setCursor(hglass);
		ProgressDialog progressDialog = new ProgressDialog(shell, message.getString("translateAllSegmentsMessage"), message
				.getString("translateAllSegmentsMessage"), ProgressDialog.SINGLE_BAR);
		progressDialog.open();
		progressDialog.updateProgressMessage(message.getString("translateAllSegmentsMessage"));
		progressDialog.setPdSupport(new ProgressDialogSupport(progressDialog));
		for (int i = 0; i < this.getXliffEditorWindow().getIOverallSegmentNumber(); i++)
		{
			if (progressDialog.getPdSupport() != null)
				progressDialog.getPdSupport().updateProgressIndication(i + 1, this.getXliffEditorWindow().getIOverallSegmentNumber());
			translateSegment(i);
			// this.gotoSegment(i);
		}

		this.gotoSegment(getIOldSegmentPosition());

		progressDialog.setPdSupport(null);
		progressDialog.close();
		progressDialog = null;
		shell.setCursor(arrow);
		return;
	}

	/**
	 * runPhraseSearch
	 * 
	 * @param iSegmentPosition
	 */
	public void translatePhrases(int iSegmentPosition)
	{
		if (ptDataSourceFormComposite.getDataSourceInstances() == null)
			return;
		int iNumPhrases = getPhrases(iSegmentPosition).size();
		Vector<String> sourceTerms = new Vector<String>();
		Vector<String> targetTerms = new Vector<String>();
		for (int i = 0; i < ptDataSourceFormComposite.getDataSourceInstances().size(); i++)
		{
			try
			{
				DataSource datasource = ptDataSourceFormComposite.getDataSourceInstances().get(i);
				Element altrans = datasource.subSegmentTranslate(getXliffEditorWindow().getTransUnits().get(iSegmentPosition), xliffDocument,
						sourceLanguage, targetLanguage, null);
				if (altrans != null)
				{
					String text = "";
					Vector<PhraseTranslateResult> elements = getPhrases(iSegmentPosition);
					for (int j = 0; j < elements.size(); j++)
					{
						text = text + elements.get(j).getSourcePhrase() + " : " + elements.get(j).getTargetPhrase() + "\n";
						sourceTerms.add(elements.get(j).getSourcePhrase());
						targetTerms.add(elements.get(j).getTargetPhrase());
					}
					statusDisplayWindow.setText(text);
				}
			}
			catch (Exception e2)
			{
				e2.printStackTrace();
			}
		}

		if (getPhrases(iSegmentPosition).size() != iNumPhrases)
		{
			XliffDocument xliff = new XliffDocument();
			xliff.addSubSegmentTranslationToGlossary(getXliffEditorWindow().getFile(), sourceTerms, targetTerms);
			xliffEditorDictionaryViewer.setTerms(xliff.getGlossary(getXliffEditorWindow().getFile()));
			getXliffEditorWindow().setBChanged(true);
		}
	}

	/**
	 * translateSegment translate a segment based on the chosen openTMS data
	 * sources
	 * 
	 * @param iSegnum
	 *            the segment number
	 * @return the resulting (possible) modified trans-unit element
	 */
	public Element translateSegment(int iSegnum)
	{
		Hashtable<String, Object> transParam = new Hashtable<String, Object>();
		if (this.preferencesContainer.isbSearchIfApproved())
			transParam.put("ignoreApproveAttribute", "yes");
		else
			transParam.put("ignoreApproveAttribute", "no");
		return translateSegment(iSegnum, transParam);
	}

	/**
	 * translateSegment translate a segment based on the chosen openTMS data
	 * sources
	 * 
	 * @param iSegnum
	 *            the segment number
	 * @return the resulting (possible) modified trans-unit element
	 */
	public Element translateSegment(int iSegnum, Hashtable<String, Object> transParam)
	{
		if (tmDataSourceFormComposite.getDataSourceInstances() == null)
			return getXliffEditorWindow().getTransUnits().get(iSegnum);
		Element translationUnit = getXliffEditorWindow().getTransUnits().get(iSegnum);
		int iNumaltransUnits = translationUnit.getChildren("alt-trans", xliffEditorWindow.getXliffDocument().getNamespace()).size();

		for (int i = 0; i < tmDataSourceFormComposite.getDataSourceInstances().size(); i++)
		{
			DataSource datasource = tmDataSourceFormComposite.getDataSourceInstances().get(i);
			// Element altrans =
			// getXliffEditorWindow().getTransUnits().get(getIOldSegmentPosition());
			int iSimilarity = 70;
			try
			{
				try
				{
					iSimilarity = Integer.parseInt(fuzzysim.getText());
				}
				catch (Exception ex)
				{
					iSimilarity = iOldSetSimilarity;
				}
				translationUnit = getXliffEditorWindow().getTransUnits().get(iSegnum);
				@SuppressWarnings("unused")
				Element transunit = datasource.translate(translationUnit, xliffDocument.getFiles().get(0), xliffDocument, sourceLanguage,
						targetLanguage, iSimilarity, transParam);
				// String altres =
				// xliffDocument.elementContentToString(translationUnit);
				// statusDisplayWindow.setText(altres);
				this.getXliffEditorWindow().setSegmentStatus(translationUnit, i);
			}
			catch (Exception e2)
			{
				e2.printStackTrace();
			}
		}

		if (iNumaltransUnits != translationUnit.getChildren("alt-trans", xliffEditorWindow.getXliffDocument().getNamespace()).size())
			getXliffEditorWindow().setBChanged(true);
		return translationUnit;
	}

}
