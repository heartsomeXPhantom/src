/*
 * Created on 30.09.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package net.docliff.models.applicationmodel.guimodel.editor;

import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.docliff.models.applicationmodel.guimodel.editor.AskAddTranslationMessageBox.AskResult;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.jdom.Element;
import org.jdom.Namespace;

import de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.OpenTMSXMLStyledText;
import de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.TagDescriptor;
import de.folt.models.applicationmodel.guimodel.support.OpenTMSStyleRangeProperty;
import de.folt.models.applicationmodel.guimodel.support.ProgressDialog;
import de.folt.models.applicationmodel.guimodel.support.ProgressDialogSupport;
import de.folt.models.datamodel.DataSource;
import de.folt.models.datamodel.MonoLingualObject;
import de.folt.models.datamodel.MultiLingualObject;
import de.folt.models.datamodel.TranslationCheckResult;
import de.folt.models.datamodel.phrasetranslate.PhraseTranslateResult;
import de.folt.models.documentmodel.document.XmlDocument;
import de.folt.models.documentmodel.xliff.XliffDocument;
import de.folt.util.ColorTable;
import de.folt.util.OpenTMSException;

/**
 * @author klemens
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class XliffEditorWindow extends OpenTMSXMLStyledText
{

	public class PhrasePosition
	{
		private int iEnd = -1;

		private int iStart = -1;

		private PhraseTranslateResult phraseTranslateResult = null;

		/**
		 * @param position
		 * @param i
		 */
		public PhrasePosition(int iStart, int iEnd)
		{
			this.iStart = iStart;
			this.iEnd = iEnd;
		}

		/**
		 * @param phraseTranslateResult
		 * @param position
		 * @param i
		 */
		public PhrasePosition(PhraseTranslateResult phraseTranslateResult, int iStart, int iEnd)
		{
			this.iStart = iStart;
			this.iEnd = iEnd;
			this.setPhraseTranslateResult(phraseTranslateResult);
		}

		/**
		 * @return the iEnd
		 */
		public int getIEnd()
		{
			return iEnd;
		}

		/**
		 * @return the iStart
		 */
		public int getIStart()
		{
			return iStart;
		}

		/**
		 * @return the phraseTranslateResult
		 */
		public PhraseTranslateResult getPhraseTranslateResult()
		{
			return phraseTranslateResult;
		}

		/**
		 * @param end
		 *            the iEnd to set
		 */
		public void setIEnd(int end)
		{
			iEnd = end;
		}

		/**
		 * @param start
		 *            the iStart to set
		 */
		public void setIStart(int start)
		{
			iStart = start;
		}

		/**
		 * @param phraseTranslateResult
		 *            the phraseTranslateResult to set
		 */
		public void setPhraseTranslateResult(PhraseTranslateResult phraseTranslateResult)
		{
			this.phraseTranslateResult = phraseTranslateResult;
		}
	}

	private static de.folt.util.Messages message;

	private static String userLanguage = "en";

	private static XliffEditor xliffEditor = new XliffEditor();

	/**
	 * main
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		message = de.folt.util.Messages.getInstance("net.docliff.models.applicationmodel.guimodel.editor.XliffEditor", userLanguage);

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
		String version = xliffEditor.getXliffEditorVersion();

		shell.setText(message.getString("OpenTMSXliffEditorWindow") + " " + version);
		shell.setSize(600, 400);
		Image logo = new Image(mydisplay, "images/opentms_16x16.png");
		shell.setImage(logo);

		GridLayout shellLayout = new GridLayout(1, true);
		shellLayout.horizontalSpacing = 0;
		shellLayout.verticalSpacing = 1;
		shellLayout.marginWidth = 0;
		shell.setLayout(shellLayout);

		XliffEditorWindow xliffTestWindow = new XliffEditorWindow(shell, shell, SWT.MULTI | SWT.V_SCROLL | SWT.WRAP | SWT.BORDER);
		int iGridData = GridData.GRAB_VERTICAL | GridData.VERTICAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL;
		GridData textgridentry = new GridData(iGridData);
		xliffTestWindow.setLayoutData(textgridentry);

		String xliffile = args[0];

		Element file = null;

		XliffDocument xliff = new XliffDocument();
		xliff.loadXmlFile(xliffile);
		file = xliff.getFiles().get(0);
		xliffTestWindow.setXliffDocument(xliff);
		xliffTestWindow.loadXliffFile(file, "sourcetarget");

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

	private boolean[] bApprovedSegments = null;

	private boolean[] bChangedSegments;

	/**
	 * @return the bChangedSegments
	 */
	public boolean[] getBChangedSegments()
	{
		return bChangedSegments;
	}

	/**
	 * @param changedSegments
	 *            the bChangedSegments to set
	 */
	public void setBChangedSegments(boolean[] changedSegments)
	{
		bChangedSegments = changedSegments;
	}

	private boolean bCheckIfSourceTargetCombinationExists = false;

	private boolean bDisplayAllTransUnits = false;

	private boolean[] bFuzzyMatchSegments = null;

	private boolean[] bMultiple100PercentMatchSegments;

	private boolean[] bTranslatedSegments = null;

	protected Display display;

	private String endSourceTextNumber = ">";

	private String endTargetTextNumber = ">";

	private Element file;

	private String glossary = "";

	private int iOldSegmentPosition = -1;

	private int iOverallSegmentNumber = 0;

	int iSimilarity = 70;

	private Vector<PhrasePosition> phrasePositions = new Vector<PhrasePosition>();

	private PreferencesContainer preferencesContainer = new PreferencesContainer();

	private ProgressDialog progressDialog;

	private Shell shell;

	private String sourceLanguage;

	private String startSourceApprovedTextNumber = "<sa "; // (char)0x25E8 + "";

	private String startSourceTextNumber = "<sn "; // (char)0x25E8 + "";

	private String startTargetApprovedTextNumber = "<ta "; // (char)0x25E8 + "";

	private String startTargetTextNumber = "<tn "; // (char)0x25E8 + "";

	private String targetLanguage;

	private List<Element> transUnits = null;

	private Vector<Element> transUnitsAsVector = null;

	private XliffDocument xliffDocument;

	private XmlDocument xmlDocumentTemplate;

	private List<Element> groups;

	private String[] stateInformation = null;

	private int[] segmentLengthInformation;

	private String[] sizeunit = null;

	/**
	 * @return the stateInformation
	 */
	public String[] getStateInformation()
	{
		return stateInformation;
	}

	/**
	 * @param stateInformation
	 *            the stateInformation to set
	 */
	public void setStateInformation(String[] stateInformation)
	{
		this.stateInformation = stateInformation;
	}

	/**
	 * @return the segmentLengthInformation
	 */
	public int[] getSegmentLengthInformation()
	{
		return segmentLengthInformation;
	}

	/**
	 * @param segmentLengthInformation
	 *            the segmentLengthInformation to set
	 */
	public void setSegmentLengthInformation(int[] segmentLengthInformation)
	{
		this.segmentLengthInformation = segmentLengthInformation;
	}

	/**
	 * @return the sizeunit
	 */
	public String[] getSizeunit()
	{
		return sizeunit;
	}

	/**
	 * @param sizeunit
	 *            the sizeunit to set
	 */
	public void setSizeunit(String[] sizeunit)
	{
		this.sizeunit = sizeunit;
	}

	/**
	 * @param parent
	 * @param style
	 */
	public XliffEditorWindow(Shell shell, Composite parent, int style)
	{
		super(parent, style);
		setBChangeBackGroundOnChange(false);
		display = this.getDisplay();
		this.shell = shell;
		message = de.folt.util.Messages.getInstance("net.docliff.models.applicationmodel.guimodel.editor.XliffEditor", userLanguage);

		Font font = this.getFont();
		Font font1 = new Font(font.getDevice(), font.getFontData()[0].getName(), font.getFontData()[0].getHeight() - 1, font.getFontData()[0]
				.getStyle());
		// tmx
		TagDescriptor tagDescriptorPh = new TagDescriptor("ph", ColorTable.getInstance(getDisplay(), "black"), ColorTable.getInstance(getDisplay(),
				"darkorange"), font1);
		TagDescriptor tagDescriptorUt = new TagDescriptor("ut", ColorTable.getInstance(getDisplay(), "black"), ColorTable.getInstance(getDisplay(),
				"orange"), font1);
		TagDescriptor tagDescriptorIt = new TagDescriptor("it", ColorTable.getInstance(getDisplay(), "black"), ColorTable.getInstance(getDisplay(),
				"orangered"), font1);
		TagDescriptor tagDescriptorHi = new TagDescriptor("hi", ColorTable.getInstance(getDisplay(), "black"), ColorTable.getInstance(getDisplay(),
				"purple"), font1);
		TagDescriptor tagDescriptorEpt = new TagDescriptor("ept", ColorTable.getInstance(getDisplay(), "black"), ColorTable.getInstance(getDisplay(),
				"palevioletred"), font1);
		TagDescriptor tagDescriptorBpt = new TagDescriptor("bpt", ColorTable.getInstance(getDisplay(), "black"), ColorTable.getInstance(getDisplay(),
				"indianred"), font1);
		TagDescriptor tagDescriptorSub = new TagDescriptor("sub", ColorTable.getInstance(getDisplay(), "black"), ColorTable.getInstance(getDisplay(),
				"mediumvioletred"), font1, false);

		// xliff
		TagDescriptor tagDescriptorG = new TagDescriptor("g", ColorTable.getInstance(getDisplay(), "black"), ColorTable.getInstance(getDisplay(),
				"cadetblue"), font1);
		TagDescriptor tagDescriptorX = new TagDescriptor("x", ColorTable.getInstance(getDisplay(), "black"), ColorTable.getInstance(getDisplay(),
				"blue"), font1);
		TagDescriptor tagDescriptorBx = new TagDescriptor("bx", ColorTable.getInstance(getDisplay(), "black"), ColorTable.getInstance(getDisplay(),
				"blueviolet"), font1);
		TagDescriptor tagDescriptorEx = new TagDescriptor("ex", ColorTable.getInstance(getDisplay(), "black"), ColorTable.getInstance(getDisplay(),
				"cadetblue"), font1);
		TagDescriptor tagDescriptorMrk = new TagDescriptor("mrk", ColorTable.getInstance(getDisplay(), "black"), ColorTable.getInstance(getDisplay(),
				"cornflowerblue"), font1);

		TagDescriptor tagDescriptorSn = new TagDescriptor("sn", ColorTable.getInstance(getDisplay(), "white"), ColorTable.getInstance(getDisplay(),
				"blue"));
		TagDescriptor tagDescriptorTn = new TagDescriptor("tn", ColorTable.getInstance(getDisplay(), "white"), ColorTable.getInstance(getDisplay(),
				"red"));

		TagDescriptor tagDescriptorSa = new TagDescriptor("sa", ColorTable.getInstance(getDisplay(), "white"), ColorTable.getInstance(getDisplay(),
				"blue"));
		TagDescriptor tagDescriptorTa = new TagDescriptor("ta", ColorTable.getInstance(getDisplay(), "white"), ColorTable.getInstance(getDisplay(),
				"pink"));

		tagDescriptorSa.setFontStyle(SWT.BOLD);
		tagDescriptorSn.setFontStyle(SWT.BOLD);
		tagDescriptorTa.setFontStyle(SWT.BOLD);
		tagDescriptorTn.setFontStyle(SWT.BOLD);

		addTagDescriptor(tagDescriptorG);
		addTagDescriptor(tagDescriptorPh);
		addTagDescriptor(tagDescriptorUt);
		addTagDescriptor(tagDescriptorIt);
		addTagDescriptor(tagDescriptorHi);
		addTagDescriptor(tagDescriptorEpt);
		addTagDescriptor(tagDescriptorBpt);
		addTagDescriptor(tagDescriptorSub);
		addTagDescriptor(tagDescriptorX);
		addTagDescriptor(tagDescriptorBx);
		addTagDescriptor(tagDescriptorEx);
		addTagDescriptor(tagDescriptorMrk);
		addTagDescriptor(tagDescriptorSn);
		addTagDescriptor(tagDescriptorTn);
		addTagDescriptor(tagDescriptorSa);
		addTagDescriptor(tagDescriptorTa);

		this.addMouseListener(new MouseListener()
		{
			public void mouseDoubleClick(MouseEvent e)
			{

			}

			public void mouseDown(MouseEvent e)
			{

			}

			public void mouseUp(MouseEvent e)
			{
				((OpenTMSXMLStyledText) e.widget).notifyObservers();
			}

		});

		this.setDefaultBackGroundColor(ColorTable.getInstance(getDisplay(), "white"));
		this.setDefaultForeGroundColor(ColorTable.getInstance(getDisplay(), "black"));

		addMouseMoveListener(new MouseMoveListener()
		{
			public void mouseMove(MouseEvent e)
			{
				String oldToolTipText = getToolTipText();
				if (oldToolTipText == null)
					return;
				oldToolTipText = ""; // oldToolTipText.replaceAll("(.*?) x:.*",
				// "$1");
				Point p = new Point(e.x, e.y);
				// ((OpenTMSXMLStyledText)e.widget).notifyObservers();
				try
				{
					int offset = getOffsetAtLocation(p);
					StyleRange style = (StyleRange) getStyleRangeAtOffset(offset);
					if (style == null)
					{
						setToolTipText(oldToolTipText + " x:" + e.x + " y:" + e.y);
						return;
					}

					XliffEditorWindow widget = (XliffEditorWindow) e.widget;
					int iPosition = widget.getOffsetAtLocation(p);
					TransUnitInformationData trans = getCurrentTransUnitInformation(iPosition);
					oldToolTipText = oldToolTipText + " id: " + trans.getId() + " translate: " + trans.isbTranslate();
					if (style.fontStyle == SWT.BOLD)
					{
						// this is a phrase match below - we should get the
						// words and show the results

						for (int i = 0; i < phrasePositions.size(); i++)
						{
							PhrasePosition pos = phrasePositions.get(i);
							if ((offset >= pos.iStart) && (offset < pos.iEnd))
							{
								oldToolTipText = oldToolTipText + pos.getPhraseTranslateResult().getSourcePhrase() + " / "
										+ pos.getPhraseTranslateResult().getTargetPhrase() + "\n";
							}
						}
					}

					setToolTipText(oldToolTipText + " x:" + e.x + " y:" + e.y);
					return;
				}
				catch (Exception e1)
				{

				}
				setToolTipText(oldToolTipText + " x:" + e.x + " y:" + e.y);
			}
		});

		addVerifyKeyListener(new VerifyKeyListener()
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
					XliffEditorWindow widget = (XliffEditorWindow) event.widget;
					if (widget.getSelectionText().length() > 0)
					{
						// System.out.println(widget.getSelectionText());
						String seltext = widget.getSelectionText();
						if (seltext.indexOf("<sn") > 0)
						{
							event.doit = false;
							return;
						}
						if (seltext.indexOf("<tn") > 0)
						{
							event.doit = false;
							return;
						}
						if (seltext.indexOf("<sa") > 0)
						{
							event.doit = false;
							return;
						}
						if (seltext.indexOf("<ta") > 0)
						{
							event.doit = false;
							return;
						}
					}

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
						// case SWT.DEL:
						// return;
					case SWT.ESC:
						return;
						// case SWT.BS:
						// return;
					}
					int iPosition = widget.getCaretOffset();
					if ((iKey == SWT.BS) && (iPosition > 0))
						iPosition = iPosition - 1;
					TransUnitInformationData trans = getCurrentTransUnitInformation(iPosition);
					// add some checks for length here....
					if ((trans.isBEditable() == false) || trans.isBInSourceText() || !trans.isbTranslate())
					{
						event.doit = false;
						return;
					}

					int result = XliffEditorForm.segmentLengthCheck(trans.getTargetText(), widget.shell, trans, message);
					if ((result == SWT.NO) || (result == SWT.OK))
					{
						event.doit = false;
						return;
					}
					// if (result == SWT.YES)
					// widget.notifyObservers();
					event.doit = true;
				}
				catch (Exception ex)
				{

				}
			}
		});

		addKeyListener(new KeyListener()
		{
			public void keyPressed(KeyEvent e)
			{
				int iKey = e.keyCode;
				// String key = Character.toString(e.character);
				// here we should check if it is not a function key...

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
				}

				XliffEditorWindow win = (XliffEditorWindow) e.widget;
				int iPosition = win.getCaretOffset();
				TransUnitInformationData trans = getCurrentTransUnitInformation(iPosition);

				int iSegmentNumber = trans.getISegmentNumber();
				bChangedSegments[iSegmentNumber] = true;
				bChanged = true;
			}

			public void keyReleased(KeyEvent e)
			{
				((OpenTMSXMLStyledText) e.widget).notifyObservers();
			}
		});
	}

	/**
	 * approveAllSegments approve all translations
	 * 
	 * @param approve
	 *            true = approved="yes", false approved="no",
	 * @param datasource
	 *            if not null save translation pair to data source
	 * @return the approve status
	 */
	public boolean approveAllSegments(boolean approve, DataSource datasource)
	{
		for (int i = 0; i < this.getIOverallSegmentNumber(); i++)
		{
			approveSegment(i, approve, datasource);
		}
		return approve;
	}

	/**
	 * approveSegment approve or disapprove a segment, depending on bDoNotStore
	 * store the segment in the selected data source
	 * 
	 * @param iSegmentNumber
	 *            the segment to approve/disapprove
	 * @param approve
	 *            true = approved="yes", false approved="no"
	 * @param dataSource
	 *            if not null save translation pair to data source
	 * @return the approve status; if a data source is specified the data source
	 *         save operation
	 */
	public boolean approveSegment(int iSegmentNumber, boolean approve, DataSource dataSource)
	{
		TransUnitInformationData transData = this.getSegmentTransUnitInformation(iSegmentNumber);
		String app = startSourceApprovedTextNumber;
		if (approve == false)
			app = this.startSourceTextNumber;

		if (approve == true)
		{
			String targettext = transData.getTargetText();
			if (targettext.equals(""))
				return false;
		}
		this.setSelection(transData.getISStartPosition(), transData.getISStartPosition() + app.length());
		this.insert(app);
		this.setSelection(transData.getISStartPosition(), transData.getISStartPosition());
		app = startTargetApprovedTextNumber;
		if (approve == false)
			app = this.startTargetTextNumber;
		this.setSelection(transData.getITStartPosition(), transData.getITStartPosition() + app.length());
		this.insert(app);
		this.setSelection(transData.getITStartPosition(), transData.getITStartPosition());
		this.gotoSegment(iSegmentNumber, true);

		this.bApprovedSegments[iSegmentNumber] = approve;

		bChanged = true;

		if (dataSource == null || !approve)
		{
			if (approve == true)
				this.transUnits.get(iSegmentNumber).setAttribute("approved", "yes"); // ,
			// this.getXliffDocument().getNamespace());
			else
				this.transUnits.get(iSegmentNumber).setAttribute("approved", "no"); // ,
			// this.getXliffDocument().getNamespace());
			this.gotoSegment(iSegmentNumber + 1, true);
			return approve;
		}

		// store the entry in the selected data source
		// check if we have source or target existing
		AskResult bSuccess = saveSourceTargetToDataSource(dataSource, transData);

		if ((approve == true) && (bSuccess == AskResult.TRUE))
		{
			this.transUnits.get(iSegmentNumber).setAttribute("approved", "yes"); // ,
			// this.getXliffDocument().getNamespace());
			// we should search again now to find the match
		}
		this.gotoSegment(iSegmentNumber + 1, true);
		if (bSuccess == AskResult.TRUE)
			return true;
		return false;
	}

	public boolean bIsApproved(int iSegmentNumber)
	{
		return bApprovedSegments[iSegmentNumber];
	}

	public boolean bIsChangedSegment(int iSegmentNumber)
	{
		return bChangedSegments[iSegmentNumber];
	}

	public boolean bIsFuzzyMatch(int iSegmentNumber)
	{
		return bFuzzyMatchSegments[iSegmentNumber];
	}

	public boolean bIsMultiple100PercentMatchSegments(int iSegmentNumber)
	{
		return bMultiple100PercentMatchSegments[iSegmentNumber];
	}

	public boolean bIsTranslated(int iSegmentNumber)
	{
		return getBTranslatedSegments()[iSegmentNumber];
	}

	public int copySourceToTarget(int iSegmentNumber, boolean queryIfTranslationExists)
	{
		TransUnitInformationData transData = this.getSegmentTransUnitInformation(iSegmentNumber);
		if (transData.isBApproved())
			return SWT.NO;
		String translation = transData.getSourceText();
		return setTranslation(iSegmentNumber, translation, queryIfTranslationExists);
	}

	public int countApprovedSegments()
	{
		int count = 0;
		for (int i = 0; i < this.getIOverallSegmentNumber(); i++)
		{
			if (this.bApprovedSegments[i])
				count = count + 1;
		}
		return count;
	}

	public int countChangedSegments()
	{
		int count = 0;
		for (int i = 0; i < this.getIOverallSegmentNumber(); i++)
		{
			if (this.bChangedSegments[i])
				count = count + 1;
		}
		return count;
	}

	public int countTranslatedSegments()
	{
		int count = 0;
		for (int i = 0; i < this.getIOverallSegmentNumber(); i++)
		{
			if (this.getBTranslatedSegments()[i])
				count = count + 1;
		}
		return count;
	}

	String fillString(char fillChar, int count)
	{
		// creates a string of 'x' repeating characters
		char[] chars = new char[count];
		while (count > 0)
			chars[--count] = fillChar;
		return new String(chars);
	}

	/**
	 * @return the bTranslatedSegments
	 */
	public boolean[] getBTranslatedSegments()
	{
		return bTranslatedSegments;
	}

	/**
	 * getCurrentTransUnitInformation returns information about the current
	 * segment the cursor is located on
	 * 
	 * @return the TransUnitInformationData associated with the segment
	 */
	public TransUnitInformationData getCurrentTransUnitInformation()
	{
		int iPosition = this.getCaretOffset();
		return getCurrentTransUnitInformation(iPosition);
	}

	/**
	 * getCurrentTransUnitInformation returns information about the current
	 * segment at character position i
	 * 
	 * @param iPosition
	 *            the character position
	 * @return the TransUnitInformationData associated with the segment
	 */
	public TransUnitInformationData getCurrentTransUnitInformation(int iPosition)
	{
		if (iPosition == -1)
			return null;
		TransUnitInformationData transInfo = new TransUnitInformationData();

		transInfo.setIPosition(iPosition);

		// search for the segment number and tag we are in
		String text = this.getText();

		int tl = text.length();
		int iNextSPosition = indexOfSTag(text, iPosition);
		// <sn or <sa otherwise error
		int iCurrentSPosition = lastIndexOfOfSTag(text, iPosition);

		if (iNextSPosition == iCurrentSPosition)
			iNextSPosition = indexOfSTag(text, iPosition + 1);

		transInfo.setISStartPosition(iCurrentSPosition);
		int iCurrentTPosition = indexOfTTag(text, iCurrentSPosition);
		transInfo.setITStartPosition(iCurrentTPosition);

		Pattern psource = Pattern.compile("^\\<(s[na])([ ]+?)(\\d+?)\\>.*", Pattern.DOTALL | Pattern.MULTILINE); // [0-9]+?
		// Pattern ptarget = Pattern.compile("^\\<(t[na])[ ]+?(\\d+?)\\>.*",
		// Pattern.DOTALL); // [0-9]+?

		int iL = iCurrentSPosition + 15;
		if (iL > tl)
			iL = tl - 1;
		String subString = text.substring(iCurrentSPosition, iL);
		Matcher m = psource.matcher(subString);
		String num = "";
		String blanks = "";
		String type = "";
		if (m.matches())
		{
			num = m.group(3);
			blanks = m.group(2);
			try
			{
				transInfo.setISegmentNumber(Integer.parseInt(num));
			}
			catch (NumberFormatException e)
			{
				e.printStackTrace();
			}
			type = m.group(1);
			if (type.equals("sa"))
				transInfo.setBApproved(true);
			else
				transInfo.setBApproved(false);
		}
		int iIndLen = (num + blanks + type).length() + 2;

		String sourceText = text.substring(iCurrentSPosition + iIndLen, iCurrentTPosition);
		sourceText = sourceText.replaceAll("\\n$", ""); // remove the final text
		transInfo.setSourceText(sourceText);

		sourceText = text.substring(iCurrentSPosition, iCurrentTPosition);
		transInfo.setFullSourceText(sourceText);

		String targetText = "";
		if (iNextSPosition != -1)
		{
			if ((iCurrentTPosition + iIndLen) <= iNextSPosition)
			{
				targetText = text.substring(iCurrentTPosition + iIndLen, iNextSPosition);
			}
			else
			{
				targetText = text.substring(iCurrentTPosition);
				// System.out.println("Error: " + iCurrentTPosition + " < " +
				// iNextSPosition + "\nsourceText\n" + sourceText +
				// "\ntargetText\n" + targetText);
			}
		}
		else
			targetText = text.substring(iCurrentTPosition + iIndLen);
		targetText = targetText.replaceAll("\\n$", ""); // remove the final text
		transInfo.setTargetText(targetText);

		if (iCurrentTPosition <= iNextSPosition)
		{
			if (iNextSPosition != -1)
				targetText = text.substring(iCurrentTPosition, iNextSPosition);
			else
				targetText = text.substring(iCurrentTPosition);
		}
		else
		{
			targetText = text.substring(iCurrentTPosition);
			// System.out.println("Error: " + iCurrentTPosition + " < " +
			// iNextSPosition + "\nsourceText\n" + sourceText + "\ntargetText\n"
			// + targetText);
		}
		transInfo.setFullTargetText(targetText);

		if (iPosition >= iCurrentTPosition)
			transInfo.setBInSourceText(false);
		else
			transInfo.setBInSourceText(true);
		boolean bStyle = false;

		String comp = (de.folt.util.ColorTable.getRGBByName("white")).toString();
		String rgb = comp;
		StyleRange style = (StyleRange) getStyleRangeAtOffset(iPosition);
		if (style != null)
		{
			bStyle = getOpenTMSStyleRangeProperties().containsKey(style);
			rgb = style.background.getRGB().toString();
		}
		transInfo.setBInKnowStyle(bStyle);
		if (rgb.equals(comp))
			transInfo.setBInTag(false);
		else
			transInfo.setBInTag(true);

		boolean bEditable = true;
		if (bStyle)
			bEditable = getOpenTMSStyleRangeProperties().get(style).isBEditable();

		transInfo.setBEditable(bEditable);
		transInfo.setSegmentLengthInformation(this.segmentLengthInformation[transInfo.getISegmentNumber()]);
		transInfo.setStateInformation(this.stateInformation[transInfo.getISegmentNumber()]);
		transInfo.setSizeunit(this.sizeunit[transInfo.getISegmentNumber()]);
		transInfo.setbTranslate(true);
		try
		{
			Element el = this.transUnitsAsVector.get(transInfo.getISegmentNumber());
			transInfo.setTransUnit(el);
			String translate = el.getAttributeValue("translate");
			if ((translate != null) && translate.equals("no"))
			{
				transInfo.setbTranslate(false);
			}
			transInfo.setId(el.getAttributeValue("id"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return transInfo;
	}

	private int lastIndexOfOfSTag(String string, int iPosition)
	{
		int iPos1 = string.lastIndexOf("<sa", iPosition);
		int iPos2 = string.lastIndexOf("<sn", iPosition);
		if (iPos1 == -1)
			return iPos2;
		else if (iPos2 == -1)
			return iPos1;
		else if (iPos1 < iPos2)
			return iPos2;

		return iPos1;
	}

	@SuppressWarnings("unused")
	private int lastIndexOfOfTTag(String string, int iPosition)
	{
		int iPos1 = string.lastIndexOf("<ta", iPosition);
		int iPos2 = string.lastIndexOf("<tn", iPosition);
		if (iPos1 == -1)
			return iPos2;
		else if (iPos2 == -1)
			return iPos1;
		else if (iPos1 < iPos2)
			return iPos2;

		return iPos1;
	}

	private int indexOfSTag(String string, int iPosition)
	{
		int iPos1 = string.indexOf("<sa", iPosition);
		int iPos2 = string.indexOf("<sn", iPosition);
		if (iPos1 == -1)
			return iPos2;
		else if (iPos2 == -1)
			return iPos1;
		else if (iPos1 < iPos2)
			return iPos1;

		return iPos2;
	}

	private int indexOfTTag(String string, int iPosition)
	{
		int iPos1 = string.indexOf("<ta", iPosition);
		int iPos2 = string.indexOf("<tn", iPosition);
		if (iPos1 == -1)
			return iPos2;
		else if (iPos2 == -1)
			return iPos1;
		else if (iPos1 < iPos2)
			return iPos1;

		return iPos2;
	}

	/**
	 * @return the endSourceTextNumer
	 */
	public String getEndSourceTextNumber()
	{
		return endSourceTextNumber;
	}

	/**
	 * @return the endTargetTextNumer
	 */
	public String getEndTargetTextNumber()
	{
		return endTargetTextNumber;
	}

	/**
	 * @return the file
	 */
	public Element getFile()
	{
		return file;
	}

	/**
	 * @return the glossary
	 */
	public String getGlossary()
	{
		return glossary;
	}

	/**
	 * @return the iOldSegmentPosition
	 */
	public int getIOldSegmentPosition()
	{
		return iOldSegmentPosition;
	}

	/**
	 * @return the iOverallSegmentNumber
	 */
	public int getIOverallSegmentNumber()
	{
		return iOverallSegmentNumber;
	}

	/**
	 * @return the iSimilarity
	 */
	public int getISimilarity()
	{
		return iSimilarity;
	}

	/**
	 * @return the optionsContainer
	 */
	public PreferencesContainer getPreferencesContainer()
	{
		return preferencesContainer;
	}

	/**
	 * getSegmentTransUnitInformation returns information about the segment
	 * number supplied
	 * 
	 * @param iSegmentNumber
	 *            the segment number
	 * @return the TransUnitInformationData associated with the segment
	 */
	public TransUnitInformationData getSegmentTransUnitInformation(int iSegmentNumber)
	{
		String text = this.getText();
		String prefix = " " + iSegmentNumber + ">";
		int iPosition = text.indexOf(prefix);
		if (iPosition > -1)
		{
			// ok - more complicated must determine the start s part of the
			// character positions
			int iPos1 = text.lastIndexOf("<s", iPosition);
			if (iPos1 > -1)
			{
				iPosition = iPos1;
			}
		}
		return getCurrentTransUnitInformation(iPosition);
	}

	/**
	 * @return the sourceLanguage
	 */
	public String getSourceLanguage()
	{
		return sourceLanguage;
	}

	/**
	 * @return the startSourceApprovedTextNumber
	 */
	public String getStartSourceApprovedTextNumber()
	{
		return startSourceApprovedTextNumber;
	}

	/**
	 * @return the startSourceTextNumber
	 */
	public String getStartSourceTextNumber()
	{
		return startSourceTextNumber;
	}

	/**
	 * @return the startTargetApprovedTextNumber
	 */
	public String getStartTargetApprovedTextNumber()
	{
		return startTargetApprovedTextNumber;
	}

	/**
	 * @return the startTargetTextNumber
	 */
	public String getStartTargetTextNumber()
	{
		return startTargetTextNumber;
	}

	/**
	 * @return the targetLanguage
	 */
	public String getTargetLanguage()
	{
		return targetLanguage;
	}

	/**
	 * @return the transUnits
	 */
	public List<Element> getTransUnits()
	{
		return transUnits;
	}

	/**
	 * @return the xliffDocument
	 */
	public XliffDocument getXliffDocument()
	{
		return xliffDocument;
	}

	/**
	 * @return the xliffEditor
	 */
	public XliffEditor getXliffEditor()
	{
		return xliffEditor;
	}

	/**
	 * @return the xmlDocumentTemplate
	 */
	public XmlDocument getXmlDocumentTemplate()
	{
		return xmlDocumentTemplate;
	}

	/**
	 * gotoSegment
	 * 
	 * @param iSegnum
	 */
	public void gotoSegment(int iSegnum)
	{
		gotoSegment(iSegnum, false);
	}

	/**
	 * gotoSegment
	 * 
	 * @param iSegnum
	 */
	public void gotoSegment(int iSegnum, boolean bUpdateStyle)
	{
		// search for <s... + num>
		String text = this.getText();
		String prefix = " " + iSegnum + ">";
		int iPos = text.indexOf(prefix);
		if (iPos > -1)
		{
			// ok - more complicated must determine the next character positions
			int iPos1 = text.indexOf(prefix, iPos + 1);
			if (iPos1 > -1)
			{
				iPos = iPos1;
				int ipos2 = text.indexOf(">", iPos + 1);
				if (ipos2 > 0)
					iPos = ipos2;
			}

			this.setCaretOffset(iPos + 1);
			this.setSelection(iPos + 1);
			this.forceFocus();
			if (bUpdateStyle && (getIOldSegmentPosition() > -1))
				setXliffEditorStyleRange(getIOldSegmentPosition());
			if (bUpdateStyle)
				this.setIOldSegmentPosition(iSegnum);
			if (bUpdateStyle)
				setCaretStyleRange(iSegnum);
		}
	}

	public void gotoSegment(String num)
	{
		int iNum = 0;

		try
		{
			iNum = Integer.parseInt(num);
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace();
		}
		gotoSegment(iNum);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seede.folt.models.applicationmodel.guimodel.editor.datasourceeditor.
	 * OpenTMSStyledText#handleRightMouseClick(org.eclipse.swt.widgets.Widget)
	 */
	@Override
	protected Menu handleRightMouseClick(Widget w, MouseEvent eMouseEvent)
	{
		Menu popupmenu = super.handleRightMouseClick(w, eMouseEvent);

		Point p = new Point(eMouseEvent.x, eMouseEvent.y);
		// ((OpenTMSXMLStyledText)e.widget).notifyObservers();
		try
		{
			int offset = getOffsetAtLocation(p);
			StyleRange style = (StyleRange) getStyleRangeAtOffset(offset);
			if (style == null)
			{
				return popupmenu;
			}

			if (style.fontStyle == SWT.BOLD)
			{
				// this is a phrase match below - we should get the words and
				// show the results
				MenuItem addItem = new MenuItem(popupmenu, SWT.SEPARATOR);

				for (int i = 0; i < phrasePositions.size(); i++)
				{
					PhrasePosition pos = phrasePositions.get(i);
					if ((offset >= pos.iStart) && (offset < pos.iEnd))
					{
						addItem = new MenuItem(popupmenu, SWT.PUSH);
						addItem.setText(pos.getPhraseTranslateResult().getSourcePhrase() + " / " + pos.getPhraseTranslateResult().getTargetPhrase());
						addItem.setData("targetPhrase", pos.getPhraseTranslateResult().getTargetPhrase());
						addItem.addListener(SWT.Selection, new Listener()
						{
							public void handleEvent(Event e)
							{
								int caretOffset = getCaretOffset();
								StyleRange style = (StyleRange) getStyleRangeAtOffset(caretOffset);
								if ((openTMSStyleRangeProperties != null) && openTMSStyleRangeProperties.containsKey(style))
								{
									if (!openTMSStyleRangeProperties.get(style).isBEditable())
									{
										return;
									}
								}
								// must insert text at caret position
								String targetPhrase = (String) e.widget.getData("targetPhrase");
								if (targetPhrase != null)
									insert(targetPhrase);
							}
						});
					}
				}
			}
			// here we could check if it is a xliff edit in-line tag - depends
			// on the color Info available
			/*
			 * else { TagDescriptor tagDescriptor = checkIfInLineTag(style); if
			 * (tagDescriptor != null) {
			 * 
			 * } }
			 */
		}
		catch (Exception e1)
		{

		}

		return popupmenu;
	}

	/**
	 * checkIfInLineTag
	 * 
	 * @param style
	 * @return
	 */
	@SuppressWarnings("unused")
	private TagDescriptor checkIfInLineTag(StyleRange style)
	{
		if (getOpenTMSStyleRangeProperties().containsKey(style))
		{
			Enumeration<TagDescriptor> enums = this.tags.elements();
			RGB rgbb = style.background.getRGB();
			RGB rgbf = style.foreground.getRGB();
			while (enums.hasMoreElements())
			{
				TagDescriptor tag = enums.nextElement();
				if (tag.getTagBackGroundColor().getRGB().equals(rgbb) && tag.getTagForeGroundColor().getRGB().equals(rgbf))
				{
					return tag;
				}
			}
		}
		return null;
	}

	private void initSegmentsInformation()
	{
		bApprovedSegments = new boolean[this.getIOverallSegmentNumber()];
		bFuzzyMatchSegments = new boolean[this.getIOverallSegmentNumber()];
		setBTranslatedSegments(new boolean[this.getIOverallSegmentNumber()]);
		bChangedSegments = new boolean[this.getIOverallSegmentNumber()];
		bMultiple100PercentMatchSegments = new boolean[this.getIOverallSegmentNumber()];
		stateInformation = new String[this.getIOverallSegmentNumber()];
		segmentLengthInformation = new int[this.getIOverallSegmentNumber()];
		this.sizeunit = new String[this.getIOverallSegmentNumber()];
	}

	/**
	 * @return the bChanged
	 */
	public boolean isBChanged()
	{
		return bChanged;
	}

	/**
	 * @return the bCheckIfSourceTargetCombinationExists
	 */
	public boolean isBCheckIfSourceTargetCombinationExists()
	{
		return bCheckIfSourceTargetCombinationExists;
	}

	/**
	 * @return the bDisplayAllTransUnits
	 */
	public boolean isBDisplayAllTransUnits()
	{
		return bDisplayAllTransUnits;
	}

	/**
	 * loadXliffFile loads all the specified elements contents specified by
	 * whichElement into the window
	 * 
	 * @param file
	 *            the file to load
	 * @param whichElement
	 *            Value: "source" or "target"
	 */
	@SuppressWarnings("unchecked")
	public void loadXliffFile(Element file, String whichElement)
	{
		// load all the transunits
		if (file == null)
			return;
		if (xliffDocument == null)
			return;

		this.file = file;
		xmlDocumentTemplate = new XmlDocument();
		Element header = xliffDocument.getHeader(this.file);
		if (header != null)
			glossary = xliffDocument.getGlossary(header);
		Element body = xliffDocument.getBody(this.file);
		if (body == null)
			return;

		transUnitsAsVector = new Vector<Element>();
		transUnits = (List<Element>) body.getChildren("trans-unit", xliffDocument.getNamespace());
		groups = (List<Element>) body.getChildren("group", xliffDocument.getNamespace());
		if (transUnits == null)
			return;

		for (int i = 0; i < transUnits.size(); i++)
		{
			transUnitsAsVector.add(transUnits.get(i));
		}

		if (groups.size() > 0)
		{
			try
			{
				for (int i = 0; i < groups.size(); i++)
				{
					List<Element> groupTransUnits = (List<Element>) groups.get(i).getChildren("trans-unit", xliffDocument.getNamespace());
					for (int k = 0; k < groupTransUnits.size(); k++)
					{
						transUnitsAsVector.add(groupTransUnits.get(k));
						// Element el = (Element)
						// groupTransUnits.get(k).clone();
						// transUnits.add(el);
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		if (transUnits.size() == 0)
			return;

		String windowtext = "";
		this.setIOverallSegmentNumber(transUnitsAsVector.size()); // transUnits.size());
		int padLength = (this.getIOverallSegmentNumber() + "").length();

		progressDialog = new ProgressDialog(shell, message.getString("LoadXliffFile"), message.getString("LoadXliffFile"), ProgressDialog.SINGLE_BAR);
		progressDialog.open();
		progressDialog.updateProgressMessage(message.getString("LoadXliffFile"));
		progressDialog.setPdSupport(new ProgressDialogSupport(progressDialog));

		sourceLanguage = file.getAttributeValue("source-language"); // ,
		// this.getXliffDocument().getNamespace());
		targetLanguage = file.getAttributeValue("target-language"); // ,
		// this.getXliffDocument().getNamespace());

		initSegmentsInformation();

		for (int i = 0; i < this.getIOverallSegmentNumber(); i++)
		{
			if (progressDialog.getPdSupport() != null)
				progressDialog.getPdSupport().updateProgressIndication(i + 1, this.getIOverallSegmentNumber());
			Element transUnit = transUnitsAsVector.get(i); // transUnits.get(i);
			bChangedSegments[i] = false;
			String text = "";
			String paddedI = i + "";
			int iLength = (i + "").length();
			int iDiff = padLength - iLength;
			if (iDiff > 0)
			{
				paddedI = fillString(' ', iDiff) + paddedI;
			}
			if (whichElement.equals("sourcetarget"))
			{
				Element elem = transUnit.getChild("source", xliffDocument.getNamespace());
				String stext = xmlDocumentTemplate.elementToString(elem);
				stext = stext.replaceAll("<" + "source" + ".*?>", "");
				stext = stext.replaceAll("</" + "source" + ">", "");
				if (transUnit.getAttributeValue("approved", "no").equals("no"))
					text = text + getStartSourceTextNumber() + paddedI + endSourceTextNumber + stext + "\n";
				else
					text = text + startSourceApprovedTextNumber + paddedI + endSourceTextNumber + stext + "\n";

				// maxwidth="255" size-unit="char">
				String maxwidth = transUnit.getAttributeValue("maxwidth");
				if (maxwidth != null)
				{
					try
					{
						String sizeunit = transUnit.getAttributeValue("size-unit");
						if (sizeunit != null)
							this.sizeunit[i] = sizeunit;
						else
							this.sizeunit[i] = "char";
						this.segmentLengthInformation[i] = Integer.parseInt(maxwidth);
					}
					catch (Exception ex)
					{
						this.segmentLengthInformation[i] = -1;
					}
				}
				else
				{
					this.segmentLengthInformation[i] = -1;
					this.sizeunit[i] = "char";
				}

				elem = transUnit.getChild("target", xliffDocument.getNamespace());
				if (elem != null)
				{
					String ttext = xmlDocumentTemplate.elementToString(elem);
					ttext = ttext.replaceAll("<" + "target" + ".*?>", "");
					ttext = ttext.replaceAll("</" + "target" + ">", "");
					if (transUnit.getAttributeValue("approved", "no").equals("no"))
					{
						text = text + startTargetTextNumber + paddedI + endTargetTextNumber + ttext + "\n";
						bApprovedSegments[i] = false;
					}
					else
					{
						text = text + startTargetApprovedTextNumber + paddedI + endSourceTextNumber + ttext + "\n";
						bApprovedSegments[i] = true;
					}

					if (ttext.length() > 0)
						getBTranslatedSegments()[i] = true;
					else
						getBTranslatedSegments()[i] = false;

					// state="needs-review-translation"
					String state = elem.getAttributeValue("state");
					if (state != null)
					{
						this.stateInformation[i] = state;
					}
					else
						this.stateInformation[i] = "";
				}
				else
				{
					text = text + startTargetTextNumber + paddedI + endTargetTextNumber + "\n";
					getBTranslatedSegments()[i] = false;
				}

				setSegmentStatus(transUnit, i);
			}
			else
			{
				Element elem = transUnit.getChild(whichElement, xliffDocument.getNamespace());
				text = xmlDocumentTemplate.elementToString(elem);
				text = text.replaceAll("<" + whichElement + ".*?>", "");
				text = text.replaceAll("</" + whichElement + ">", "");
				text = text + getStartSourceTextNumber() + paddedI + endSourceTextNumber + text + "\n";
			}

			windowtext = windowtext + text;
		}
		progressDialog.setTitle(message.getString("LoadXliffFileSetText"));
		this.setText(windowtext);
		this.setCaretOffset(0);
		this.setIOldSegmentPosition(0);
		progressDialog.setPdSupport(null);
		progressDialog.close();
		progressDialog = null;
		this.bChanged = false; // to avoid message because of extended change
		// modifier...
	}

	/**
	 * markupPhraseMatches
	 * 
	 * @param iSegmentNumber
	 * @return
	 */
	public int markupPhraseMatches(int iSegmentNumber)
	{

		try
		{
			Element transunit = getTransUnits().get(iSegmentNumber);
			Vector<PhraseTranslateResult> phrasesElement = xliffDocument.getTransUnitPhraseEntries(transunit);
			if (phrasesElement.size() == 0)
				return SWT.NO;
			TransUnitInformationData transData = null;

			if (iSegmentNumber == -1)
			{
				transData = this.getCurrentTransUnitInformation();
				iSegmentNumber = transData.getISegmentNumber();
			}
			else
				transData = this.getSegmentTransUnitInformation(iSegmentNumber);

			if (transData.isBApproved())
				return SWT.NO;

			return markupPhraseMatches(transData);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return SWT.NO;
		}
	}

	/**
	 * markupPhraseMatches
	 * 
	 * @param trans
	 */
	public int markupPhraseMatches(TransUnitInformationData transData)
	{
		try
		{
			int iSegmentNumber = transData.getISegmentNumber();
			Element transunit = getTransUnits().get(iSegmentNumber);
			Vector<PhraseTranslateResult> phrasesElement = xliffDocument.getTransUnitPhraseEntries(transunit);
			if (phrasesElement.size() == 0)
				return SWT.NO;

			int iStart = this.getText().indexOf(">", transData.getISStartPosition());
			if (iStart < 0)
				return SWT.NO;
			iStart = iStart + 1;
			int textLen = transData.getSourceText().length();
			int iEnd = iStart + textLen;
			// search for matches
			StyleRange[] ranges = null;

			phrasePositions.clear();

			for (int i = 0; i < phrasesElement.size(); i++)
			{
				String sourcePhrase = phrasesElement.get(i).getSourcePhrase();
				int iPosition = this.getText().indexOf(sourcePhrase, iStart);
				while (iPosition != -1)
				{
					if ((iPosition == -1) || (iPosition > iEnd))
					{
						break;
					}
					// show the matchings in bold
					// must get the current stylerange

					PhrasePosition phrasePosition = new PhrasePosition(phrasesElement.get(i), iPosition, sourcePhrase.length() + iPosition);
					phrasePositions.add(phrasePosition);
					ranges = this.getStyleRanges(iPosition, sourcePhrase.length());
					for (int j = 0; j < ranges.length; j++)
					{
						StyleRange range = (StyleRange) ranges[j].clone();
						if (range.fontStyle == SWT.BOLD)
						{
							range.underline = true;
						}
						else
						{
							range.fontStyle = SWT.BOLD;
						}
						range.start = iPosition;
						range.length = sourcePhrase.length();
						this.setStyleRange(range);
					}
					iPosition = this.getText().indexOf(sourcePhrase, iPosition + 1);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return SWT.YES;

	}

	/**
	 * saveSourceTargetToDataSource saves a translation to the specified data
	 * source
	 * 
	 * @param dataSource
	 * @param transData
	 * @return
	 */
	public AskResult saveSourceTargetToDataSource(DataSource dataSource, TransUnitInformationData transData)
	{
		if (transData.getTargetText().equals(""))
			return AskResult.FALSE;

		if (transData.getSourceText().equals(""))
			return AskResult.FALSE;

		bCheckIfSourceTargetCombinationExists = this.preferencesContainer.isQuerySourceMatch() || this.preferencesContainer.isQueryTargetMatch();

		TranslationCheckResult translationCheckResult = dataSource.checkIfTranslationExistsInDataSource(transData.getSourceText(), this
				.getSourceLanguage(), transData.getTargetText(), this.getTargetLanguage());

		boolean bSourceFound = translationCheckResult.getStatus().equals(TranslationCheckResult.TranslationCheckStatus.SOURCEFOUND);

		boolean bTargetFound = translationCheckResult.getStatus().equals(TranslationCheckResult.TranslationCheckStatus.TARGETFOUND);

		boolean bSourceTargetFound = translationCheckResult.getStatus().equals(TranslationCheckResult.TranslationCheckStatus.SOURCEANDTARGETFOUND);

		MonoLingualObject sourceMono = null;
		MonoLingualObject targetMono = null;
		MultiLingualObject multi = null;

		if (translationCheckResult.getStatus() == TranslationCheckResult.TranslationCheckStatus.NEW)
		{
			if (this.preferencesContainer.isQueryNewTranslation())
			{
				int style = SWT.PRIMARY_MODAL | SWT.ICON_QUESTION | SWT.YES | SWT.NO;
				MessageBox messageBox = new MessageBox(shell, style);
				String messagestring = message.getString("DataSource") + " " + dataSource.getDataSourceName() + "\n";
				messagestring = messagestring + this.getSourceLanguage() + ": " + transData.getSourceText() + "\n";
				messagestring = messagestring + this.getTargetLanguage() + ": " + transData.getTargetText() + "\n";
				messageBox.setMessage(messagestring);
				messageBox.setText(message.getString("AddNewSourceTargetTranslation"));
				int result = messageBox.open();
				if (result == SWT.NO)
				{
					return AskResult.NO;
				}
			}
		}
		else if ((bSourceFound == this.preferencesContainer.isQuerySourceMatch()) || (bTargetFound == this.preferencesContainer.isQueryTargetMatch()))
		{
			AskAddTranslationMessageBox checkTranslation = new AskAddTranslationMessageBox(shell, transData.getSourceText(), transData
					.getTargetText(), translationCheckResult, this.getFont(), this.getSourceLanguage(), this.getTargetLanguage(), transData
					.getISegmentNumber(), dataSource);
			checkTranslation.show();
			AskResult iButton = checkTranslation.getButton();
			if ((iButton == AskResult.CANCEL) || (iButton == AskResult.NO))
				return iButton;
			// we do not add existing source / target combinations
			// later this could be improved by checking also the criteria
			// informations and add new properties
			if (translationCheckResult.getStatus().equals(TranslationCheckResult.TranslationCheckStatus.SOURCEANDTARGETFOUND))
			{
				return AskResult.NO;
			}

			if (bCheckIfSourceTargetCombinationExists == true)
			{
				// here we should come up with a dialogue showing the
				// alternatives
				if (translationCheckResult.getStatus() != TranslationCheckResult.TranslationCheckStatus.NEW)
				{
					// if both exist ignore the translation - wk 11.09.2010
					if (bSourceTargetFound)
					{
						return AskResult.NO;
					}
					// just add the target mono
					if (bSourceFound)
					{
						Vector<MultiLingualObject> multivector = translationCheckResult.getSourceSegmentMatches();
						if (multivector.size() > 0)
						{
							multi = multivector.get(0);
							targetMono = new MonoLingualObject(transData.getTargetText(), this.getTargetLanguage());
							multi.addMonoLingualObject(targetMono);
							if (dataSource.addMonoLingualObject(targetMono, true))
								return AskResult.TRUE;
							return iButton;
						}
						return AskResult.FALSE;
					}
					// just add the source mono
					if (bTargetFound)
					{
						Vector<MultiLingualObject> multivector = translationCheckResult.getTargetSegmentMatches();
						if (multivector.size() > 0)
						{
							multi = multivector.get(0);
							sourceMono = new MonoLingualObject(transData.getSourceText(), this.getSourceLanguage());
							multi.addMonoLingualObject(sourceMono);
							if (dataSource.addMonoLingualObject(sourceMono, true))
								return AskResult.TRUE;
							return iButton;
						}
						return AskResult.FALSE;
					}
				}
			}
		}
		// here we have a totally new multi or no check for
		// bCheckIfSourceTargetCombinationExists was set
		sourceMono = new MonoLingualObject(transData.getSourceText(), this.getSourceLanguage());
		targetMono = new MonoLingualObject(transData.getTargetText(), this.getTargetLanguage());
		multi = new MultiLingualObject();
		multi.addMonoLingualObject(sourceMono);
		multi.addMonoLingualObject(targetMono);
		if (dataSource.addMultiLingualObject(multi, true))
			return AskResult.TRUE;
		return AskResult.FALSE;
	}

	/**
	 * saveXliffDocument save the xliff document associated with the xliff
	 * editor form
	 */
	public void saveXliffDocument()
	{
		saveXliffDocument(this.xliffDocument.getXmlDocumentName());
	}

	/**
	 * saveXliffDocument save the xliff document associated with the xliff
	 * editor form to a new file
	 * 
	 * @param filename
	 *            the filename to save the file too
	 */
	public void saveXliffDocument(String filename)
	{
		try
		{
			// for (int i = 0; i < this.getTransUnits().size(); i++)
			for (int i = 0; i < this.transUnitsAsVector.size(); i++)
			{
				Element transUnit = transUnitsAsVector.get(i); // this.getTransUnits().get(i);
				TransUnitInformationData transdata = this.getSegmentTransUnitInformation(i);
				@SuppressWarnings("unused")
				String sourcetext = transdata.getSourceText();
				String targettext = transdata.getTargetText();
				Element elem = transUnit.getChild("target", xliffDocument.getNamespace());
				Element sourceElement = transUnit.getChild("source", xliffDocument.getNamespace());
				int iSourceIndex = transUnit.indexOf(sourceElement);
				Element sourceSegElement = transUnit.getChild("seg-source", xliffDocument.getNamespace());
				if (sourceSegElement != null)
				{
					int iSourceSegIndex = transUnit.indexOf(sourceSegElement);
					if (iSourceSegIndex > iSourceIndex)
						iSourceIndex = iSourceSegIndex;
				}
				String ttext = "";
				String language = "";
				if (elem != null)
				{
					ttext = this.getXmlDocumentTemplate().elementToString(elem);
					language = elem.getAttributeValue("lang", Namespace.XML_NAMESPACE);
				}
				if (language == null)
					language = this.xliffDocument.getTargetLanguage();
				ttext = ttext.replaceAll("<" + "target" + ".*?>", "");
				ttext = ttext.replaceAll("</" + "target" + ">", "");
				if (!targettext.equals(ttext))
				{
					targettext = "<target>" + targettext + "</target>";
					try
					{
						this.getXmlDocumentTemplate();
						Element newTarget = xliffDocument.buildElement(targettext);
						newTarget.setAttribute("lang", language, Namespace.XML_NAMESPACE);
						transUnit.removeChild("target", this.getXliffDocument().getNamespace());
						transUnit.addContent(iSourceIndex + 1, newTarget);
					}
					catch (OpenTMSException e)
					{
						e.printStackTrace();
					}
				}

				if (transdata.isBApproved())
				{
					transUnit.setAttribute("approved", "yes"); // ,
					// this.getXliffDocument().getNamespace());
				}
				else
				{
					transUnit.setAttribute("approved", "no"); // ,
					// this.getXliffDocument().getNamespace());
				}
			}

			if (filename.equals(this.xliffDocument.getXmlDocumentName()))
				this.xliffDocument.saveToXmlFile();
			else
				this.xliffDocument.saveToXmlFile(filename);

			this.bChanged = false;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	/**
	 * @param bChanged
	 *            the bChanged to set
	 */
	public void setBChanged(boolean bChanged)
	{
		this.bChanged = bChanged;
	}

	/**
	 * @param checkIfSourceTargetCombinationExists
	 *            the bCheckIfSourceTargetCombinationExists to set
	 */
	public void setBCheckIfSourceTargetCombinationExists(boolean checkIfSourceTargetCombinationExists)
	{
		bCheckIfSourceTargetCombinationExists = checkIfSourceTargetCombinationExists;
	}

	/**
	 * @param displayAllTransUnits
	 *            the bDisplayAllTransUnits to set
	 */
	public void setBDisplayAllTransUnits(boolean displayAllTransUnits)
	{
		bDisplayAllTransUnits = displayAllTransUnits;
	}

	/**
	 * @param bTranslatedSegments
	 *            the bTranslatedSegments to set
	 */
	public void setBTranslatedSegments(boolean[] bTranslatedSegments)
	{
		this.bTranslatedSegments = bTranslatedSegments;
	}

	/**
	 * setCaretStyleRange
	 * 
	 * @param iSegmentNumber
	 */
	public void setCaretStyleRange(int iSegmentNumber)
	{
		TransUnitInformationData trans = this.getSegmentTransUnitInformation(iSegmentNumber);
		Color tagForeGroundColor = this.getDefaultForeGroundColor();
		Color tagBackGroundColor = ColorTable.getInstance(getDisplay(), "yellow");
		StyleRange range = new StyleRange(trans.getISStartPosition(), trans.getFullSourceText().length(), tagForeGroundColor, tagBackGroundColor);
		OpenTMSStyleRangeProperty property = new OpenTMSStyleRangeProperty(false);
		this.openTMSStyleRangeProperties.put(range, property);
		setStyleRange(range);

	}

	/**
	 * @param endSourceTextNumer
	 *            the endSourceTextNumer to set
	 */
	public void setEndSourceTextNumber(String endSourceTextNumber)
	{
		this.endSourceTextNumber = endSourceTextNumber;
	}

	/**
	 * @param endTargetTextNumer
	 *            the endTargetTextNumer to set
	 */
	public void setEndTargetTextNumber(String endTargetTextNumber)
	{
		this.endTargetTextNumber = endTargetTextNumber;
	}

	/**
	 * @param file
	 *            the file to set
	 */
	public void setFile(Element file)
	{
		this.file = file;
	}

	/**
	 * @param glossary
	 *            the glossary to set
	 */
	public void setGlossary(String glossary)
	{
		this.glossary = glossary;
	}

	/**
	 * @param oldSegmentPosition
	 *            the iOldSegmentPosition to set
	 */
	public void setIOldSegmentPosition(int oldSegmentPosition)
	{
		iOldSegmentPosition = oldSegmentPosition;
	}

	/**
	 * @param iOverallSegmentNumber
	 *            the iOverallSegmentNumber to set
	 */
	public void setIOverallSegmentNumber(int iOverallSegmentNumber)
	{
		this.iOverallSegmentNumber = iOverallSegmentNumber;
	}

	/**
	 * @param similarity
	 *            the iSimilarity to set
	 */
	public void setISimilarity(int similarity)
	{
		iSimilarity = similarity;
	}

	/**
	 * @param optionsContainer
	 *            the optionsContainer to set
	 */
	public void setPreferencesContainer(PreferencesContainer optionsContainer)
	{
		this.preferencesContainer = optionsContainer;
	}

	@SuppressWarnings("unchecked")
	public void setSegmentStatus(Element transUnit, int iSegnum)
	{
		List<Element> altTrans = transUnit.getChildren("alt-trans", xliffDocument.getNamespace());
		if ((altTrans.size() == 0) || bApprovedSegments[iSegnum])
		{
			bFuzzyMatchSegments[iSegnum] = false;
			bMultiple100PercentMatchSegments[iSegnum] = false;
		}
		else
		{
			bFuzzyMatchSegments[iSegnum] = true;
			bMultiple100PercentMatchSegments[iSegnum] = false;
			int i100Matches = 0;
			for (int j = 0; j < altTrans.size(); j++)
			{
				Element alt = altTrans.get(j);
				String qual = alt.getAttributeValue("match-quality"); // ,
				// this.getXliffDocument().getNamespace());
				int match = 0;
				try
				{
					match = Integer.parseInt(qual);
				}
				catch (Exception ex)
				{

				}
				if (match == 100)
				{
					bFuzzyMatchSegments[iSegnum] = false;

					if (alt.getChildren("target", xliffDocument.getNamespace()).size() > 1)
					{
						this.bMultiple100PercentMatchSegments[iSegnum] = true;
						break;
					}
					i100Matches = i100Matches + 1;
					if (i100Matches > 1)
					{
						this.bMultiple100PercentMatchSegments[iSegnum] = true;
						break;
					}
				}
			}
		}
	}

	public int setSelectedText(String text)
	{
		TransUnitInformationData transData = this.getCurrentTransUnitInformation();
		if (transData.isBInSourceText())
			return SWT.NO;
		if (transData.isBInTag())
			return SWT.NO;
		this.insert(text);
		this.bChanged = true;
		return SWT.YES;
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
	 * @param startSourceApprovedTextNumber
	 *            the startSourceApprovedTextNumber to set
	 */
	public void setStartSourceApprovedTextNumber(String startSourceApprovedTextNumber)
	{
		this.startSourceApprovedTextNumber = startSourceApprovedTextNumber;
	}

	/**
	 * @param startSourceTextNumber
	 *            the startSourceTextNumber to set
	 */
	public void setStartSourceTextNumber(String startSourceTextNumber)
	{
		this.startSourceTextNumber = startSourceTextNumber;
	}

	/**
	 * @param startTargetApprovedTextNumber
	 *            the startTargetApprovedTextNumber to set
	 */
	public void setStartTargetApprovedTextNumber(String startTargetApprovedTextNumber)
	{
		this.startTargetApprovedTextNumber = startTargetApprovedTextNumber;
	}

	/**
	 * @param startTargetTextNumber
	 *            the startTargetTextNumber to set
	 */
	public void setStartTargetTextNumber(String startTargetTextNumber)
	{
		this.startTargetTextNumber = startTargetTextNumber;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seede.folt.models.applicationmodel.guimodel.editor.datasourceeditor.
	 * OpenTMSXMLStyledText#setStyleRange(java.lang.String)
	 */
	@Override
	public void setStyleRange(String text)
	{
		super.setStyleRange(text);
		// now check alle the s-tags and apply Stylerange
		setXliffEditorStyleRange();
	}

	public void setStyleRange(String text, int iStartPosition, int iEndPostion)
	{
		super.setStyleRange(text, iStartPosition, iEndPostion);
		// now check alle the s-tags and apply Stylerange
		setXliffEditorStyleRange(iStartPosition, iEndPostion);
	}

	/**
	 * @param targetLanguage
	 *            the targetLanguage to set
	 */
	public void setTargetLanguage(String targetLanguage)
	{
		this.targetLanguage = targetLanguage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seede.folt.models.applicationmodel.guimodel.editor.datasourceeditor.
	 * OpenTMSXMLStyledText#setText(java.lang.String)
	 */
	@Override
	public void setText(String text)
	{
		super.setText(text);
		// progressDialog.updateProgressMessage("LoadXliffFileStyles");
		if (progressDialog != null)
			progressDialog.setTitle(message.getString("LoadXliffFileStyles"));
		setStyleRange(text);
	}

	public int setText(String text, int iSegmentNumber)
	{
		TransUnitInformationData transData = null;

		if (iSegmentNumber == -1)
		{
			transData = this.getCurrentTransUnitInformation();
			iSegmentNumber = transData.getISegmentNumber();
		}
		else
			transData = this.getSegmentTransUnitInformation(iSegmentNumber);

		if (transData.isBApproved())
			return SWT.NO;

		int iStart = this.getText().indexOf(">", transData.getITStartPosition());
		if (iStart < 0)
			return SWT.NO;
		iStart = iStart + 1;
		this.setSelectionRange(iStart, transData.getTargetText().length());
		this.insert(text);
		int textLen = text.length();
		this.setSelectionRange(iStart + textLen, 0);
		this.setStyleRange(this.getText(), iStart, iStart + textLen - 1);

		this.getBTranslatedSegments()[iSegmentNumber] = true;
		bChanged = true;
		return SWT.YES;
	}

	/**
	 * setTranslation set a new translation
	 * 
	 * @param iSegmentNumber
	 *            the segment number of the translation
	 * @param translation
	 *            the translation string
	 * @param queryIfTranslationExists
	 *            if true and a translation exists a dialoge asks if the
	 *            translation should be replaced
	 * @return
	 */
	public int setTranslation(int iSegmentNumber, String translation, boolean queryIfTranslationExists)
	{
		TransUnitInformationData transData = this.getSegmentTransUnitInformation(iSegmentNumber);
		if (transData.isBApproved())
			return SWT.NO;
		if (!transData.getTargetText().equals("") && queryIfTranslationExists)
		{
			int style = SWT.PRIMARY_MODAL | SWT.ICON_QUESTION | SWT.YES | SWT.NO | SWT.CANCEL;
			MessageBox messageBox = new MessageBox(shell, style);
			messageBox.setMessage(message.getString("Translations_exists") + "\n" + message.getString("Segment") + ": " + iSegmentNumber); //$NON-NLS-1$
			int result = messageBox.open();
			if (result == SWT.CANCEL)
			{
				return SWT.CANCEL;
			}
			if (result == SWT.NO)
			{
				return SWT.NO;
			}
		}

		int iStart = this.getText().indexOf(">", transData.getITStartPosition());
		if (iStart < 0)
			return SWT.NO;
		iStart = iStart + 1;
		this.setSelection(iStart, iStart + transData.getTargetText().length());
		this.insert(translation);
		this.setSelection(iStart, iStart);
		this.gotoSegment(iSegmentNumber, true);

		this.getBTranslatedSegments()[iSegmentNumber] = true;
		bChanged = true;
		return SWT.YES;
	}

	/**
	 * @param transUnits
	 *            the transUnits to set
	 */
	public void setTransUnits(List<Element> transUnits)
	{
		this.transUnits = transUnits;
	}

	/**
	 * @param xliffDocument
	 *            the xliffDocument to set
	 */
	public void setXliffDocument(XliffDocument xliffDocument)
	{
		this.xliffDocument = xliffDocument;
	}

	/**
	 * @param xliffEditor
	 *            the xliffEditor to set
	 */
	public void setXliffEditor(XliffEditor xliffEditor)
	{
		XliffEditorWindow.xliffEditor = xliffEditor;
	}

	/**
	 * setSourceStyleRange
	 */
	public void setXliffEditorStyleRange()
	{
		setXliffEditorStyleRange(0, this.getText().length());
	}

	/**
	 * setXliffEditorStyleRange set the standard style range for a given segment
	 * 
	 * @param iSegmentNumber
	 *            the segment number
	 */
	public void setXliffEditorStyleRange(int iSegmentNumber)
	{
		TransUnitInformationData trans = this.getSegmentTransUnitInformation(iSegmentNumber);
		setXliffEditorStyleRange(trans.getISStartPosition(), trans.getISStartPosition() + trans.getFullSourceText().length()
				+ trans.getFullTargetText().length());
	}

	/**
	 * setXliffEditorStyleRange
	 * 
	 * @param startPosition
	 * @param endPostion
	 */
	public void setXliffEditorStyleRange(int startPosition, int endPostion)
	{
		if (progressDialog != null)
			progressDialog.setTitle(message.getString("LoadXliffFileStylesMarkTags"));
		super.setStyleRange(this.getText(), startPosition, endPostion);
		if (progressDialog != null)
			progressDialog.setTitle(message.getString("LoadXliffFileStylesMarkLines"));
		Color tagForeGroundColor = this.getDefaultForeGroundColor();
		Color tagBackGroundColor = ColorTable.getInstance(getDisplay(), "grey");
		Color tagApproveBackGroundColor = ColorTable.getInstance(getDisplay(), "pink");
		Color tagCaretBackGroundColor = ColorTable.getInstance(getDisplay(), "yellow");
		String text = this.getText();
		int tl = text.length();

		String caretColor = tagCaretBackGroundColor.toString();

		int iSourceStartPosition = text.indexOf("<s", startPosition);
		int iTargetStartPosition = 0;

		Pattern psource = Pattern.compile("^\\<(s[na])[ ]+?(\\d+?)\\>.*", Pattern.DOTALL); // [0-9]+?
		Pattern ptarget = Pattern.compile("^\\<(t[na])[ ]+?(\\d+?)\\>.*", Pattern.DOTALL); // [0-9]+?

		while (iSourceStartPosition != -1)
		{
			if (iSourceStartPosition > endPostion)
				break;
			int iL = iSourceStartPosition + 15;
			if (iL > tl)
				iL = tl - 1;
			String subString = text.substring(iSourceStartPosition, iL);

			Matcher m = psource.matcher(subString);

			if (m.matches())
			{
				iTargetStartPosition = text.indexOf("<t", iSourceStartPosition);
				String num = m.group(2);
				int numI = 0;

				try
				{
					numI = Integer.parseInt(num) + 1;
				}
				catch (NumberFormatException e)
				{
					e.printStackTrace();
				}
				@SuppressWarnings("unused")
				String type = m.group(1);

				if ((progressDialog != null) && (progressDialog.getPdSupport() != null))
					progressDialog.getPdSupport().updateProgressIndication(numI, this.getIOverallSegmentNumber());

				int iLastStylePostion = iSourceStartPosition;
				boolean bNewStyle = false;
				for (int j = iSourceStartPosition; j < iTargetStartPosition; j++)
				{
					StyleRange currentRange = this.getStyleRangeAtOffset(j);
					String currentColor = "";
					if (currentRange != null)
						currentColor = currentRange.background.toString();
					if ((currentRange != null) && (!caretColor.equals(currentColor)))
					{
						if (bNewStyle)
						{
							StyleRange range = new StyleRange(iLastStylePostion, j - iLastStylePostion, tagForeGroundColor, tagBackGroundColor);
							OpenTMSStyleRangeProperty property = new OpenTMSStyleRangeProperty(false);
							this.openTMSStyleRangeProperties.put(range, property);
							setStyleRange(range);
							iLastStylePostion = j;
						}
						bNewStyle = false;
						iLastStylePostion = j;
						continue;
					}

					if (bNewStyle == false)
						iLastStylePostion = j;
					bNewStyle = true;
				}
				if (bNewStyle)
				{
					StyleRange range = new StyleRange(iLastStylePostion, iTargetStartPosition - iLastStylePostion, tagForeGroundColor,
							tagBackGroundColor);
					OpenTMSStyleRangeProperty property = new OpenTMSStyleRangeProperty(false);
					this.openTMSStyleRangeProperties.put(range, property);
					setStyleRange(range);
				}
			}
			else
			// curious case - anyway got to next <s.
			{
				iTargetStartPosition = iSourceStartPosition + 1;
				iSourceStartPosition = text.indexOf("<s", iTargetStartPosition);
				continue;
			}

			iSourceStartPosition = text.indexOf("<s", iTargetStartPosition);

			iL = iTargetStartPosition + 15;
			if (iL > tl)
				iL = tl - 1;
			subString = text.substring(iTargetStartPosition, iL);
			Matcher mtarget = ptarget.matcher(subString);
			if (mtarget.matches())
			{
				int iEndTargetPosition = iSourceStartPosition;
				if (iEndTargetPosition == -1)
				{
					iEndTargetPosition = tl;
				}
				@SuppressWarnings("unused")
				String num = mtarget.group(2);
				String type = mtarget.group(1);
				Color color = null;
				if (type.equals("ta"))
				{
					color = tagApproveBackGroundColor;
				}
				else
				{
					color = this.getDefaultBackGroundColor();
				}
				int iLastStylePostion = iTargetStartPosition;
				boolean bNewStyle = false;
				for (int j = iTargetStartPosition; j < iEndTargetPosition; j++)
				{
					StyleRange currentRange = this.getStyleRangeAtOffset(j);
					String currentColor = "";
					if (currentRange != null)
						currentColor = currentRange.background.toString();
					boolean bMMatchRange = currentColor.equals(tagApproveBackGroundColor.toString())
							|| currentColor.equals(getDefaultBackGroundColor().toString());
					if ((currentRange != null) && !bMMatchRange)
					{
						if (bNewStyle)
						{
							StyleRange range = null;
							range = new StyleRange(iLastStylePostion, j - iLastStylePostion, tagForeGroundColor, color);
							if (type.equals("ta"))
							{
								OpenTMSStyleRangeProperty property = new OpenTMSStyleRangeProperty(false);
								this.openTMSStyleRangeProperties.put(range, property);
							}
							setStyleRange(range);
						}
						bNewStyle = false;
						iLastStylePostion = j;
						continue;
					}
					if (bNewStyle == false)
						iLastStylePostion = j;
					bNewStyle = true;
				}
				if (bNewStyle)
				{
					StyleRange range = null;
					range = new StyleRange(iLastStylePostion, iEndTargetPosition - iLastStylePostion, tagForeGroundColor, color);
					if (type.equals("ta"))
					{
						OpenTMSStyleRangeProperty property = new OpenTMSStyleRangeProperty(false);
						this.openTMSStyleRangeProperties.put(range, property);
					}
					setStyleRange(range);
				}
			}
		}

	}

	/**
	 * @param xmlDocumentTemplate
	 *            the xmlDocumentTemplate to set
	 */
	public void setXmlDocumentTemplate(XmlDocument xmlDocumentTemplate)
	{
		this.xmlDocumentTemplate = xmlDocumentTemplate;
	}

	/**
	 * showNextChanged
	 */
	public void showNextChanged()
	{
		for (int i = iOldSegmentPosition + 1; i < this.getIOverallSegmentNumber(); i++)
		{
			if (this.bIsChangedSegment(i))
			{
				this.gotoSegment(i);
				return;
			}
		}
	}

	/**
	 * showNextFuzzy
	 */
	public void showNextFuzzy()
	{
		for (int i = iOldSegmentPosition + 1; i < this.getIOverallSegmentNumber(); i++)
		{
			if (this.bIsFuzzyMatch(i))
			{
				this.gotoSegment(i);
				return;
			}
		}
	}

	/**
	 * showNextNotTranslatedOrFuzzySegment
	 */
	public void showNextNotTranslatedOrFuzzySegment()
	{
		for (int i = iOldSegmentPosition + 1; i < this.getIOverallSegmentNumber(); i++)
		{
			if ((this.bIsFuzzyMatch(i)) || !this.bIsTranslated(i))
			{
				this.gotoSegment(i);
				return;
			}
		}
	}

	/**
	 * showNextSegment
	 */
	public void showNextSegment()
	{
		if (iOldSegmentPosition < (this.getIOverallSegmentNumber() - 1))
		{
			this.gotoSegment(iOldSegmentPosition + 1);
		}
	}

	/**
	 * showNextToCheck
	 */
	public void showNextToCheck()
	{
		for (int i = iOldSegmentPosition + 1; i < this.getIOverallSegmentNumber(); i++)
		{
			if (this.bIsMultiple100PercentMatchSegments(i))
			{
				this.gotoSegment(i);
				return;
			}
		}

	}

	/**
	 * showNextUnapprovedSegment
	 */
	public void showNextUnapprovedSegment()
	{
		for (int i = iOldSegmentPosition + 1; i < this.getIOverallSegmentNumber(); i++)
		{
			if (!this.bIsApproved(i))
			{
				this.gotoSegment(i);
				return;
			}
		}
	}

	/**
	 * showNextUntranslatedSegment
	 */
	public void showNextUntranslatedSegment()
	{
		for (int i = iOldSegmentPosition + 1; i < this.getIOverallSegmentNumber(); i++)
		{
			if (!this.bIsTranslated(i))
			{
				this.gotoSegment(i);
				return;
			}
		}
	}

	/**
	 * showPreviousChanged
	 */
	public void showPreviousChanged()
	{
		for (int i = iOldSegmentPosition - 1; i >= 0; i--)
		{
			if (this.bIsChangedSegment(i))
			{
				this.gotoSegment(i);
				return;
			}
		}
	}

	/**
	 * showPreviousFuzzy
	 */
	public void showPreviousFuzzy()
	{
		for (int i = iOldSegmentPosition - 1; i >= 0; i--)
		{
			if (this.bIsFuzzyMatch(i))
			{
				this.gotoSegment(i);
				return;
			}
		}
	}

	/**
	 * showPreviousSegment
	 */
	public void showPreviousSegment()
	{
		if (iOldSegmentPosition > 0)
		{
			this.gotoSegment(iOldSegmentPosition - 1);
		}
	}

	/**
	 * showPreviousToCheck
	 */
	public void showPreviousToCheck()
	{
		for (int i = iOldSegmentPosition - 1; i >= 0; i--)
		{
			if (this.bIsMultiple100PercentMatchSegments(i))
			{
				this.gotoSegment(i);
				return;
			}
		}
	}

	/**
	 * showPreviousUnapprovedSegment
	 */
	/**
	 * showPreviousUnapprovedSegment
	 */
	public void showPreviousUnapprovedSegment()
	{
		for (int i = iOldSegmentPosition - 1; i >= 0; i--)
		{
			if (!this.bIsApproved(i))
			{
				this.gotoSegment(i);
				return;
			}
		}
	}

	/**
	 * showPreviousUntranslatedSegment
	 */
	public void showPreviousUntranslatedSegment()
	{
		for (int i = iOldSegmentPosition - 1; i >= 0; i--)
		{
			if (!this.bIsTranslated(i))
			{
				this.gotoSegment(i);
				return;
			}
		}
	}

}
