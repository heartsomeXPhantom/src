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
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;
import org.jdom.Element;

import com.araya.eaglememex.util.EMXProperties;

import de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.DataSourceEditor;
import de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.OpenTMSPropertiesEditor;
import de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.OpenTMSXMLStyledText;
import de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.TagDescriptor;
import de.folt.models.applicationmodel.guimodel.support.ProgressDialog;
import de.folt.models.applicationmodel.guimodel.support.ProgressDialogSupport;
import de.folt.models.datamodel.DataSource;
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
public class XliffEditorWithToolBars extends Composite
{

	public class XliffEditorObserver implements Observer
	{

		private XliffEditorWithToolBars xliffEditorFormWindow = null;

		/**
		 * @param xliffEditorFormWindow
		 */
		public XliffEditorObserver(XliffEditorWithToolBars xliffEditorFormWindow)
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
				xliffEditorFormWindow.getStatusWindow().setText(trans.toString());
				int iSegnum = trans.getISegmentNumber();

				segmentNumber.setText(message.getString("_Segmentnumber") + " " + iSegnum + "/"
						+ xliffEditorFormWindow.getXliffEditorWindow().getIOverallSegmentNumber() + " ("
						+ xliffEditorFormWindow.getXliffEditorWindow().countApprovedSegments() + "/"
						+ xliffEditorFormWindow.getXliffEditorWindow().countTranslatedSegments() + ") " + sourceLanguage + "->" + targetLanguage);
				matchNumber.setText(message.getString("_matchNumber"));

				if ((iSegnum >= 0) && (getIOldSegmentPosition() != iSegnum))
				{
					if (getIOldSegmentPosition() > -1)
						xliffEditorFormWindow.getXliffEditorWindow().setXliffEditorStyleRange(getIOldSegmentPosition());
					showAltTrans(iSegnum, 0, 0);
					setIOldSegmentPosition(iSegnum);
					if (xliffEditorDictionaryViewer != null)
					{
						xliffEditorDictionaryViewer.adaptDictionaryViewer(xliffFile, sourceLanguage, targetLanguage);
						xliffEditorDictionaryViewer.setTerms(getPhrases(iSegnum));
					}
				}
				else if ((iSegnum >= 0))
				{
					showAltTrans(iSegnum, 0, 0);
					setIOldSegmentPosition(iSegnum);
					xliffEditorDictionaryViewer.setTerms(getPhrases(iSegnum));
				}
				else
				{
					matchNumber.setText(message.getString("_matchNumber"));
				}

				xliffEditorFormWindow.getXliffEditorWindow().setCaretStyleRange(iSegnum);

			}
			catch (Exception e)
			{
				xliffEditorFormWindow.getStatusWindow().setText(e.getLocalizedMessage());
				matchNumber.setText(message.getString("_matchNumber"));
			}
		}
	}

	private Combo altSourceMatchesCombo;

	private OpenTMSXMLStyledText altSourceWindow;

	private Combo altTargetMatchesCombo;

	private OpenTMSXMLStyledText altTargetWindow;

	private String currentId;

	private String currentMULId;

	private String currentOrigin;

	private String curruser;

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

	private DataSourceListWithTools ptDataSourceFormComposite;

	private Combo searchmethod;

	private Label segmentNumber;

	private Shell shell;

	private String sourceLanguage = "";

	private OpenTMSXMLStyledText statusDisplayWindow;

	private SashForm statusHolder;

	private OpenTMSXMLStyledText statusLineWindow;

	private String targetLanguage = "";

	private SashForm tasksHolder;

	private DataSourceListWithTools tmDataSourceFormComposite;

	private SashForm toolbarHolder;

	private String userLanguage = "en";

	private XliffDocument xliffDocument;

	private XliffEditor xliffEditor;

	private XliffEditorDictionaryViewer xliffEditorDictionaryViewer;

	private XliffEditorObserver xliffEditorObserver;

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

	private SashForm dataSourceHolders;

	/**
	 * @param parent
	 * @param style
	 */
	public XliffEditorWithToolBars(Shell shell, Composite parent, int style, String xliffFile, String configFile)
	{
		super(parent, style);
		this.setSize(shell.getSize().x / 2, shell.getSize().y / 2);
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

		createContents(parent, xliffFile);
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
		this.statusLineWindow.setText("");
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
			List<Element> altranslist = transunit.getChildren("alt-trans");
			if (altranslist.size() > 0)
			{
				for (int i = 0; i < altranslist.size(); i++)
				{
					Element altrans = altranslist.get(i);
					String quality = altrans.getAttributeValue("match-quality");
					if (quality == null)
						continue;
					try
					{
						int iQuality = Integer.parseInt(quality);
						if (iQuality == 100)
						{
							List<Element> targetlist = altrans.getChildren("target");
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
									this.statusLineWindow.setText(this.statusLineWindow.getText() + message.getString("AcceptedMatchingTarget") + " "
											+ k + "\n");
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
	private void createContents(Widget parent, String xliffFileName)
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

		setDataSourceHolders(createDataSourceForm(toolsHolder));

		xliffFileHolder = createXliffEditorWithToolBar(toolsHolder, xliffFileName);

		// 1 : 4 | | |
		toolsHolder.setWeights(new int[] { 1, 5 });

		this.pack();

		this.layout();

		xliffEditorObserver.update(null, null);
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

		CTabItem tmTabItem = new CTabItem(dataSourceWindowsTabs, SWT.BORDER);
		tmTabItem.setText(message.getString("tmDataSources"));

		tmDataSourceFormComposite = new DataSourceListWithTools(dataSourceWindowsTabs, SWT.BORDER, message.getString("tmDataSources"));
		tmTabItem.setControl(tmDataSourceFormComposite);

		CTabItem ptTabItem = new CTabItem(dataSourceWindowsTabs, SWT.BORDER);
		ptTabItem.setText(message.getString("phraseDataSources"));

		ptDataSourceFormComposite = new DataSourceListWithTools(dataSourceWindowsTabs, SWT.BORDER, message.getString("phraseDataSources"));
		ptTabItem.setControl(ptDataSourceFormComposite);

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

		toolsHolder.pack();

		return toolsHolder;
	}

	private void createDataSourceToolBar(ToolBar toolBar)
	{
		ToolItem fSearch = new ToolItem(toolBar, SWT.PUSH | SWT.FLAT);
		fSearch.setImage(new Image(display, "images/searchtmx.gif"));
		fSearch.setToolTipText(message.getString("SearchSegment"));
		fSearch.addSelectionListener(new SelectionAdapter()
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

		});

		ToolItem sep1 = new ToolItem(toolBar, SWT.SEPARATOR);

		searchmethod = new Combo(toolBar, SWT.READ_ONLY);
		searchmethod.setToolTipText(message.getString("ChooseSearchMethod"));
		searchmethod.addSelectionListener(new SelectionAdapter()
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
		});

		searchmethod.add("FUZZY", 0);
		searchmethod.add("REGEXP", 1);
		searchmethod.add("WORD", 2);
		searchmethod.pack();
		sep1.setWidth(searchmethod.getSize().x);
		sep1.setControl(searchmethod);
		searchmethod.select(0);

		ToolItem sep2 = new ToolItem(toolBar, SWT.SEPARATOR);
		fuzzysim = new Combo(toolBar, SWT.READ_ONLY);
		fuzzysim.setToolTipText(message.getString("ChooseFuzzy"));
		fuzzysim.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				editorConfiguration.saveKeyValuePair("recentQuality", fuzzysim.getText());
				String sim = fuzzysim.getText();
				int similarity = 70;
				try
				{
					similarity = Integer.parseInt(sim);
				}
				catch (Exception ex)
				{
				}
				xliffEditorWindow.setISimilarity(similarity);
			}
		});
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

		String recentQuality = editorConfiguration.loadValueForKey("recentQuality");
		if ((recentQuality == null) || recentQuality.equals(""))
			recentQuality = "70";
		fuzzysim.setText(recentQuality);

		phraseSearch = new ToolItem(toolBar, SWT.PUSH | SWT.FLAT);
		phraseSearch.setImage(new Image(display, "images/searchterm.gif"));
		phraseSearch.setToolTipText(message.getString("SearchPhrase"));
		phraseSearch.addSelectionListener(new SelectionAdapter()
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
							statusLineWindow.setText(text);
						}
					}
					catch (Exception e2)
					{
						e2.printStackTrace();
					}
				}
				showAltTrans(getIOldSegmentPosition(), 0, 0);
			}

		});

		new ToolItem(toolBar, SWT.SEPARATOR);

		editTMEntry = new ToolItem(toolBar, SWT.PUSH | SWT.FLAT);
		editTMEntry.setImage(new Image(display, "images/editmatching.gif"));
		editTMEntry.setToolTipText(message.getString("editTMEntry"));
		editTMEntry.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				if (currentId.equals(""))
					return;
				try
				{
					statusLineWindow.setText(message.getString("EditDataSourceEntry") + currentOrigin + " " + currentId);
					DataSourceEditor.getInstance(shell.getDisplay(), currentOrigin, currentId);
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
					return;
				}
			}

		});

		new ToolItem(toolBar, SWT.SEPARATOR);

		ToolItem approve = new ToolItem(toolBar, SWT.NONE);
		approve.setImage(new Image(display, "images/approve.gif"));
		approve.setToolTipText(message.getString("Approve_Segment_tCtrl_+_E_59"));
		approve.addSelectionListener(new SelectionAdapter()
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
		});

		ToolItem approvenostore = new ToolItem(toolBar, SWT.NONE);
		approvenostore.setImage(new Image(display, "images/approvedonotstore.gif"));
		approvenostore.setToolTipText(message.getString("Approve_DoNotStoreSegment_tCtrl_+_E_59"));
		approvenostore.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				DataSource dataSource = null;
				getXliffEditorWindow().approveSegment(getIOldSegmentPosition(), true, dataSource);
				xliffEditorObserver.update(null, null);
			}
		});

		ToolItem disapprove = new ToolItem(toolBar, SWT.NONE);
		disapprove.setImage(new Image(display, "images/disapprove.gif"));
		disapprove.setToolTipText(message.getString("Disapprove_Segment_tCtrl_+_E_59"));
		disapprove.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				getXliffEditorWindow().approveSegment(getIOldSegmentPosition(), false, null);
				xliffEditorObserver.update(null, null);
			}
		});

		ToolItem copysourecetottarget = new ToolItem(toolBar, SWT.NONE);
		copysourecetottarget.setImage(new Image(display, "images/copytotarget.gif")); //$NON-NLS-1$
		copysourecetottarget.setToolTipText(message.getString("CopySourceToTarget")); //$NON-NLS-1$
		copysourecetottarget.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{

				getXliffEditorWindow().copySourceToTarget(getIOldSegmentPosition(), true);
				xliffEditorObserver.update(null, null);
			}
		});

		ToolItem accepttranslation = new ToolItem(toolBar, SWT.NONE);
		accepttranslation.setImage(new Image(display, "images/accepttranslation.gif")); //$NON-NLS-1$
		accepttranslation.setToolTipText(message.getString("AcceptTranslation")); //$NON-NLS-1$
		accepttranslation.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				acceptTranslation();
				xliffEditorObserver.update(null, null);
			}
		});
	}

	Menu menuCoolbar;

	// private void createNavigationToolBar(ToolBar toolBar)
	private void createNavigationToolBar(CoolBar coolBar, ToolBar toolBar)
	{

		// coolBar = new CoolBar(shell, SWT.BORDER | SWT.FLAT);
		// coolBar.setLayoutData(new GridData(GridData.FILL_BOTH));

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

	/**
	 * createSegmentBar
	 * 
	 * @param toolbarHolder2
	 */
	private void createSegmentBar(SashForm toolbarHolder2)
	{
		Composite nextleftBar = new Composite(toolbarHolder2, SWT.BORDER);
		nextleftBar.setLayout(new GridLayout()); // new GridLayout(5, false));
		// //
		// GridData nextleftBarData = new GridData(GridData.FILL_HORIZONTAL |
		// GridData.GRAB_HORIZONTAL);
		// nextleftBar.setLayoutData(nextleftBarData);

		segmentNumber = new Label(nextleftBar, SWT.NONE);
		segmentNumber.setText(message.getString("_Segmentnumber") + 0 + "      ");
		segmentNumber.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
		// segmentNumber.setLayout(new GridLayout());
		segmentNumber.setToolTipText(segmentNumber.getText());

		matchNumber = new Label(nextleftBar, SWT.NONE);
		matchNumber.setText(message.getString("_matchNumber") + "-----");
		matchNumber.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
		matchNumber.setToolTipText(matchNumber.getText());

		gotoSeg = new Label(nextleftBar, SWT.RIGHT);
		gotoSeg.setText(message.getString("Go_to__20")); //$NON-NLS-1$

		jumpText = new Combo(nextleftBar, SWT.DROP_DOWN);
		jumpText.setText("0"); //$NON-NLS-1$
		jumpText.setEnabled(true);

		for (int i = 0; i < xliffEditorWindow.getTransUnits().size(); i++)
		{
			jumpText.add("" + i);
		}

		jumpText.setToolTipText(segmentNumber.getText());

		jumpText.addKeyListener(new KeyListener()
		{

			public void keyPressed(KeyEvent key)
			{
				if (key.keyCode == 13)
				{
					gotoSegment(jumpText.getSelectionIndex());
					jumpText.setToolTipText(jumpText.getSelectionIndex() + "");
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
				jumpText.setToolTipText(jumpText.getSelectionIndex() + "");
			}
		});

		GridData jumpData = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
		jumpData.widthHint = 30;
		jumpText.setLayoutData(jumpData);

		jump = new Button(nextleftBar, SWT.CENTER);
		jump.setText(message.getString("&Go_22")); //$NON-NLS-1$
		jump.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				gotoSegment(jumpText.getSelectionIndex());
				jumpText.setToolTipText(jumpText.getSelectionIndex() + "");
			}
		});

	}

	/**
	 * createXliffEditorWithToolBar
	 * 
	 * @return
	 */
	private SashForm createXliffEditorWithToolBar(SashForm parentHolder, String xliffFileName)
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
		xliffEditorWindow.setLayout(new GridLayout(1, true));
		int iGridDataxliffEditorWindow = GridData.GRAB_VERTICAL | GridData.VERTICAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL;
		GridData textgridentry = new GridData(iGridDataxliffEditorWindow);
		xliffEditorWindow.setLayoutData(textgridentry);

		xliffEditorObserver = new XliffEditorObserver(this);
		xliffEditorWindow.addObserver(xliffEditorObserver);

		Element file = null;
		xliffDocument = new XliffDocument();
		xliffDocument.loadXmlFile(xliffFileName);
		file = xliffDocument.getFiles().get(0);
		sourceLanguage = file.getAttributeValue("source-language");
		targetLanguage = file.getAttributeValue("target-language");
		xliffEditorWindow.setXliffDocument(xliffDocument);
		xliffEditorWindow.pack();
		xliffEditorWindow.loadXliffFile(file, "sourcetarget");
		sourceLanguage = xliffEditorWindow.getSourceLanguage();
		targetLanguage = xliffEditorWindow.getTargetLanguage();

		tasksHolder = new SashForm(xliffFileHolder, SWT.NONE);
		tasksHolder.setOrientation(SWT.HORIZONTAL);
		GridData gridentry3 = new GridData(iGridDataxliffEditorWindow);
		tasksHolder.setLayout(new GridLayout(1, true));
		tasksHolder.setLayoutData(gridentry3);

		toolbarHolder = new SashForm(tasksHolder, SWT.NONE);
		toolbarHolder.setOrientation(SWT.VERTICAL);
		GridData gridentry2 = new GridData(iGridDataxliffEditorWindow);
		toolbarHolder.setLayout(new GridLayout(1, true));
		toolbarHolder.setLayoutData(gridentry2);

		CoolBar coolBar = new CoolBar(toolbarHolder, SWT.BORDER | SWT.FLAT);
		coolBar.setLayoutData(new GridData(GridData.FILL_BOTH));
		// ToolBar navigationToolBar = new ToolBar(toolbarHolder, SWT.NONE |
		// SWT.BORDER)
		ToolBar navigationToolBar = new ToolBar(coolBar, SWT.FLAT); // SWT.NONE
		// |
		// SWT.BORDER);
		// createNavigationToolBar(navigationToolBar);
		createNavigationToolBar(coolBar, navigationToolBar);
		ToolBar dataSourceToolBar = new ToolBar(toolbarHolder, SWT.NONE | SWT.BORDER);
		this.createDataSourceToolBar(dataSourceToolBar);

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
		statusHolder.setLayout(new GridLayout(1, true));
		statusHolder.setLayoutData(gridentry2);

		createSegmentBar(statusHolder);

		statusDisplayWindow = new OpenTMSXMLStyledText(this.statusHolder, SWT.MULTI | SWT.V_SCROLL | SWT.WRAP | SWT.BORDER | SWT.READ_ONLY);
		statusDisplayWindow.setLayout(new GridLayout(1, true));
		statusDisplayWindow.setLayoutData(textgridentry);
		statusDisplayWindow.setBackground(ColorTable.getInstance(getDisplay(), "lightgray"));
		statusDisplayWindow.setBChangeBackGroundOnChange(false);

		statusHolder.setWeights(new int[] { 1, 1 });

		matchFileHolder = new SashForm(xliffFileHolder, SWT.NONE);
		matchFileHolder.setOrientation(SWT.HORIZONTAL);
		GridLayout matchFileHolderLayout = new GridLayout(2, false);
		matchFileHolderLayout.marginWidth = 1;
		matchFileHolderLayout.marginHeight = 1;
		matchFileHolderLayout.horizontalSpacing = 0;
		matchFileHolder.setLayout(new GridLayout(1, true)); // matchFileHolderLayout);
		matchFileHolder.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));

		// add the selection list for matches
		matchSourceFileHolder = new SashForm(matchFileHolder, SWT.NONE);
		matchSourceFileHolder.setOrientation(SWT.VERTICAL);
		altSourceMatchesCombo = new Combo(matchSourceFileHolder, SWT.DROP_DOWN | SWT.READ_ONLY);

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
		matchSourceFileHolder.setWeights(new int[] { 1, 4 });

		matchTargetFileHolder = new SashForm(matchFileHolder, SWT.NONE);
		matchTargetFileHolder.setOrientation(SWT.VERTICAL);
		altTargetMatchesCombo = new Combo(matchTargetFileHolder, SWT.DROP_DOWN | SWT.READ_ONLY);

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
		matchTargetFileHolder.setWeights(new int[] { 1, 4 });

		setAltTransTagDescriptors();

		statusLineWindow = new OpenTMSXMLStyledText(this.xliffFileHolder, SWT.MULTI | SWT.V_SCROLL | SWT.WRAP | SWT.BORDER | SWT.READ_ONLY);
		statusLineWindow.setLayoutData(textgridentry);
		statusLineWindow.setLayout(new GridLayout(1, true));
		statusLineWindow.setBackground(ColorTable.getInstance(getDisplay(), "lightgray"));
		statusLineWindow.setBChangeBackGroundOnChange(false);

		tasksHolder.setWeights(new int[] { 1, 1 });

		xliffFileHolder.setWeights(new int[] { 15, 2, 3, 1 });

		xliffFileHolder.pack();
		xliffFileHolder.layout();

		return xliffFileHolder;

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

		try
		{
			iQuality = Integer.parseInt(quality);
		}
		catch (NumberFormatException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
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

	private Vector<PhraseTranslateResult> getPhrases(int iSegNum)
	{
		Element transunit = this.getXliffEditorWindow().getTransUnits().get(iSegNum);
		Vector<PhraseTranslateResult> phrasesElement = xliffDocument.getTransUnitPhraseEntries(transunit);

		return phrasesElement;
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
	 * @return the statusWindow
	 */
	public OpenTMSXMLStyledText getStatusWindow()
	{
		return statusLineWindow;
	}

	/**
	 * @return the targetLanguage
	 */
	public String getTargetLanguage()
	{
		return targetLanguage;
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
	 * @param statusWindow
	 *            the statusWindow to set
	 */
	public void setStatusWindow(OpenTMSXMLStyledText statusWindow)
	{
		this.statusLineWindow = statusWindow;
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

		Pattern ptarget = Pattern.compile("\\<target.*?>(.*?)\\</target>", Pattern.DOTALL);
		Pattern psource = Pattern.compile("\\<source.*?>(.*?)\\</source>", Pattern.DOTALL);

		Element transunit = getXliffEditorWindow().getTransUnits().get(iSegnum);
		List<Element> altranslist = transunit.getChildren("alt-trans");
		currentMULId = "";
		currentId = "";
		if (altranslist.size() > 0)
		{
			for (int i = 0; i < altranslist.size(); i++)
			{
				Element altrans = altranslist.get(i);
				String quality = altrans.getAttributeValue("match-quality");
				String origin = altrans.getAttributeValue("origin");
				currentOrigin = origin.replaceAll(".*?database=(.*?)", "$1");
				if (quality == null)
					quality = "";

				Color color = getMatchQualityColor(quality);
				Element source = altrans.getChild("source");

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
					currentId = xliffDocument.getAltTransId(altrans);
					statusDisplayWindow.setText(message.getString("datasourceName") + origin + "\n" + message.getString("creationID") + currentMULId
							+ "\n" + message.getString("uniqueID") + currentId);
				}

				String ctext = text.replaceAll("\\<(\\w+?).*?>.*?\\</$1>", "");
				ctext = ctext.replaceAll("\\</?\\w+?.*?>", "");

				altSourceMatchesCombo.add(quality + ": " + ctext);
				if (i == iAltTransSource)
				{
					List<Element> targetlist = altrans.getChildren("target");
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
		ProgressDialog progressDialog = new ProgressDialog(shell, message.getString("translateSegment"), message.getString("translateSegment"),
				ProgressDialog.SINGLE_BAR);
		progressDialog.open();
		progressDialog.updateProgressMessage(message.getString("translateSegment"));
		progressDialog.setPdSupport(new ProgressDialogSupport(progressDialog));
		for (int i = 0; i < this.getXliffEditorWindow().getIOverallSegmentNumber(); i++)
		{
			if (progressDialog.getPdSupport() != null)
				progressDialog.getPdSupport().updateProgressIndication(i + 1, this.getXliffEditorWindow().getIOverallSegmentNumber());
			// translateSegment(i);
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
	 * translateSegment translate a segment based on the chosen openTMS data
	 * sources
	 * 
	 * @param iSegnum
	 *            the segment number
	 * @return the resulting (possible) modified trans-unit element
	 */
	public Element translateSegment(int iSegnum)
	{
		return translateSegment(iSegnum, null);
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
		Element translationUnit = null;
		for (int i = 0; i < tmDataSourceFormComposite.getDataSourceInstances().size(); i++)
		{
			DataSource datasource = tmDataSourceFormComposite.getDataSourceInstances().get(i);
			// Element altrans =
			// getXliffEditorWindow().getTransUnits().get(getIOldSegmentPosition());
			int iSimilarity = 70;
			try
			{
				iSimilarity = Integer.parseInt(fuzzysim.getText());
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

		getXliffEditorWindow().setBChanged(true);
		return translationUnit;
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

		shell.setSize(500, 400);

		String configFile = OpenTMSProperties.getInstance().getOpenTMSProperty("OpenTMS.XliffEditor.EditorConfigurationDirectory");
		String propfileName = OpenTMSProperties.getInstance().getOpenTMSProperty("ArayaPropertiesFile");
		EMXProperties.getInstance(propfileName);

		XliffEditorWithToolBars xliffEditorform = new XliffEditorWithToolBars(shell, shell, SWT.NONE, args[0], configFile);
		xliffEditorform.pack();
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
	 * @param dataSourceHolders
	 *            the dataSourceHolders to set
	 */
	public void setDataSourceHolders(SashForm dataSourceHolders)
	{
		this.dataSourceHolders = dataSourceHolders;
	}

	/**
	 * @return the dataSourceHolders
	 */
	public SashForm getDataSourceHolders()
	{
		return dataSourceHolders;
	}

}
