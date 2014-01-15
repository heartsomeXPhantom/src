/*
 * Created on 22.10.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package net.docliff.models.applicationmodel.guimodel.editor;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import com.araya.eaglememex.util.LogPrint;

import de.folt.models.datamodel.DataSource;
import de.folt.models.datamodel.MonoLingualObject;
import de.folt.models.datamodel.MultiLingualObject;
import de.folt.models.datamodel.TranslationCheckResult;

public class AskAddTranslationMessageBox extends Dialog
{

	public static void main(String[] args)
	{
		try
		{
			Shell shell = new Shell(SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
			Font font = shell.getFont();
			DataSource dataSource = null;
			TranslationCheckResult translationCheckResult = null;
			AskAddTranslationMessageBox test = new AskAddTranslationMessageBox(shell, "Das ist ein Segment", "This is a segment",
					translationCheckResult, font, "de", "en", 10, dataSource);
			test.show();

			System.exit(0);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			System.exit(1);
		}

	}

	/**
	 * @author klemens
	 * 
	 *         Result types for AskAddTranslationMessageBox
	 */
	public enum AskResult
	{
		/**
		 * approve, but do not save
		 */
		NO, /**
		 * Add to chosen MUL
		 */
		ADD, /**
		 * REPLACE in chosen MUL
		 */
		REPLACE, /**
		 * no action
		 */
		CANCEL,
		/**
		 * if nothing was done or the method not called
		 */
		FALSE,
		/**
		 * if success
		 */
		TRUE
	}

	private String userLanguage = "en";

	private de.folt.util.Messages message;

	private String doc_s = "";

	private String doc_t = "";

	private String s_tmx_sl = "";

	private String s_tmx_tl = "";

	private String t_tmx_tl = "";

	private String t_tmx_sl = "";

	private String doc_s_replace = "doc_s_replace";

	private String doc_t_replace = "doc_t_replace";

	private String s_tmx_sl_replace = "s_tmx_sl_replace";

	private String s_tmx_tl_replace = "s_tmx_tl_replace";

	private String t_tmx_tl_replace = "t_tmx_tl_replace";

	private String t_tmx_sl_replace = "t_tmx_sl_replace";

	private Color red = null;

	private String sourceLanguage;

	@SuppressWarnings("unused")
	private DataSource dataSource;

	private int segmentNumber;

	private String targetLanguage;

	private TranslationCheckResult translationCheckResult;

	private String sourceSegment;

	private String targetSegment;

	private Shell shell;

	private Display display;

	private AskResult button;

	private StyledText styledText;

	private List sourceAndTargetResults;

	private List sourceResults;

	private List targetResults;

	private Button addToMultiLingualObject = null;

	private MultiLingualObject selectedMultiLingualEntry = null;

	private Button replaceInMultiLingualObject;

	private String dataSourceName;

	public AskAddTranslationMessageBox(Shell parent, String sourceSegment, String targetSegment, TranslationCheckResult translationCheckResult,
			Font font, String sourceLanguage, String targetLanguage, int segmentNumber, DataSource dataSource)
	{
		super(parent, SWT.NONE);
		message = de.folt.util.Messages.getInstance("net.docliff.models.applicationmodel.guimodel.editor.XliffEditor", userLanguage);

		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
		display = shell.getDisplay();
		shell.setText(""); //$NON-NLS-1$
		shell.setLayout(new GridLayout(1, true));

		this.translationCheckResult = translationCheckResult;
		red = new Color(parent.getDisplay(), 0xFF, 0x00, 0x00);

		button = AskResult.CANCEL;

		shell.addListener(SWT.Close, new Listener()
		{
			public void handleEvent(Event event)
			{
				// button = SWT.CANCEL;
			}
		});

		int iGridData = GridData.GRAB_VERTICAL | GridData.VERTICAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL;
		int iGridDatattributeFilter = GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL;

		Composite checkResult = new Composite(shell, SWT.BORDER);
		checkResult.setLayout(new GridLayout(2, false));
		checkResult.setLayoutData(new GridData(iGridData));

		SashForm checkResultListHolder = new SashForm(checkResult, SWT.NONE);
		checkResultListHolder.setOrientation(SWT.VERTICAL);
		GridLayout toolsLayout = new GridLayout(1, true);
		toolsLayout.marginWidth = 1;
		toolsLayout.marginHeight = 1;
		toolsLayout.horizontalSpacing = 0;
		checkResultListHolder.setLayout(toolsLayout);
		checkResultListHolder.setLayoutData(new GridData(iGridData));

		if (translationCheckResult != null)
		{
			if (translationCheckResult.getSourceAndTargetSegmentMatches().size() > 0)
			{
				sourceAndTargetResults = new org.eclipse.swt.widgets.List(checkResultListHolder, SWT.SINGLE | SWT.V_SCROLL | SWT.TOP | SWT.BORDER);
				GridData gridentry = new GridData(iGridData);
				sourceAndTargetResults.setLayoutData(gridentry);
				sourceAndTargetResults.setData("translationCheckResult", this.translationCheckResult);
				sourceAndTargetResults.setData("styledText", styledText);
				sourceAndTargetResults.addSelectionListener(new SelectionAdapter()
				{
					public void widgetSelected(SelectionEvent e)
					{
						handleSelection(sourceAndTargetResults, ((TranslationCheckResult) (sourceAndTargetResults.getData("translationCheckResult")))
								.getSourceAndTargetSegmentMatches(), "sourcetarget");
					}
				});
				sourceAndTargetResults.setToolTipText(message.getString("SourceAndTargetMatches"));
				for (int i = 0; i < translationCheckResult.getSourceAndTargetSegmentMatches().size(); i++)
				{
					sourceAndTargetResults.add(translationCheckResult.getSourceAndTargetSegmentMatches().get(i).getUniqueID());
					sourceAndTargetResults.select(0);
				}
			}
			else
				sourceAndTargetResults = null;

			if (translationCheckResult.getSourceSegmentMatches().size() > 0)
			{
				sourceResults = new org.eclipse.swt.widgets.List(checkResultListHolder, SWT.SINGLE | SWT.V_SCROLL | SWT.TOP | SWT.BORDER);
				GridData gridentry = new GridData(iGridData);
				sourceResults.setLayoutData(gridentry);
				sourceResults.setData("translationCheckResult", this.translationCheckResult);
				sourceResults.setData("styledText", styledText);
				sourceResults.addSelectionListener(new SelectionAdapter()
				{
					public void widgetSelected(SelectionEvent e)
					{
						handleSelection(sourceResults, ((TranslationCheckResult) (sourceResults.getData("translationCheckResult")))
								.getSourceSegmentMatches(), "source");
					}
				});
				sourceResults.setToolTipText(message.getString("SourceMatches"));
				for (int i = 0; i < translationCheckResult.getSourceSegmentMatches().size(); i++)
				{
					sourceResults.add(translationCheckResult.getSourceSegmentMatches().get(i).getUniqueID());
					sourceResults.select(0);
				}
			}
			else
				sourceResults = null;

			if (translationCheckResult.getTargetSegmentMatches().size() > 0)
			{
				targetResults = new org.eclipse.swt.widgets.List(checkResultListHolder, SWT.SINGLE | SWT.V_SCROLL | SWT.TOP | SWT.BORDER);
				GridData gridentry = new GridData(iGridData);
				targetResults.setLayoutData(gridentry);
				targetResults.setData("translationCheckResult", this.translationCheckResult);
				targetResults.setData("styledText", styledText);
				targetResults.addSelectionListener(new SelectionAdapter()
				{
					public void widgetSelected(SelectionEvent e)
					{
						handleSelection(targetResults, ((TranslationCheckResult) (targetResults.getData("translationCheckResult")))
								.getTargetSegmentMatches(), "target");
					}
				});
				targetResults.setToolTipText(message.getString("TargetMatches"));
				for (int i = 0; i < translationCheckResult.getTargetSegmentMatches().size(); i++)
				{
					targetResults.add(translationCheckResult.getTargetSegmentMatches().get(i).getUniqueID());
					targetResults.select(0);
				}
			}
			else
				targetResults = null;
		}

		styledText = new StyledText(checkResult, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.READ_ONLY | SWT.RESIZE);
		styledText.setFont(font);
		styledText
				.setText("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
		styledText.setLayoutData(new GridData(iGridData));

		if (sourceAndTargetResults != null)
			sourceAndTargetResults.setData("styledText", styledText);
		if (sourceResults != null)
			sourceResults.setData("styledText", styledText);
		if (targetResults != null)
			targetResults.setData("styledText", styledText);

		Composite saveBarComposite = new Composite(shell, SWT.BORDER);
		saveBarComposite.setLayout(new GridLayout(3, false));
		saveBarComposite.setLayoutData(new GridData(iGridData));

		GridData gridttributeFilter = new GridData(iGridDatattributeFilter);

		Button no = new Button(saveBarComposite, SWT.PUSH);
		no.setText(message.getString("NO")); //$NON-NLS-1$
		no.setLayoutData(gridttributeFilter);
		no.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent ev)
			{
				button = AskResult.NO;
				shell.close();
			}
		});

		addToMultiLingualObject = new Button(saveBarComposite, SWT.PUSH);
		addToMultiLingualObject.setText(message.getString("ADD")); //$NON-NLS-1$
		addToMultiLingualObject.setLayoutData(gridttributeFilter);
		addToMultiLingualObject.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent ev)
			{
				button = AskResult.ADD;
				shell.close();
			}
		});
		if (this.getSelectedMultiLingualEntry() == null)
			addToMultiLingualObject.setEnabled(false);
		else
		{
			addToMultiLingualObject.setEnabled(true);
			addToMultiLingualObject.setText(message.getString("ADD") + " " + this.getSelectedMultiLingualEntry().getUniqueID());
		}

		replaceInMultiLingualObject = new Button(saveBarComposite, SWT.PUSH);
		replaceInMultiLingualObject.setText(message.getString("REPLACE")); //$NON-NLS-1$
		replaceInMultiLingualObject.setLayoutData(gridttributeFilter);
		replaceInMultiLingualObject.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent ev)
			{
				button = AskResult.REPLACE;
				shell.close();
			}
		});
		if (this.getSelectedMultiLingualEntry() == null)
			replaceInMultiLingualObject.setEnabled(false);
		else
		{
			replaceInMultiLingualObject.setEnabled(true);
			replaceInMultiLingualObject.setText(message.getString("ADD") + " " + this.getSelectedMultiLingualEntry().getUniqueID());
		}

		Button cancel = new Button(saveBarComposite, SWT.PUSH);
		cancel.setText(message.getString("CANCEL")); //$NON-NLS-1$
		cancel.setLayoutData(gridttributeFilter);
		cancel.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent ev)
			{
				button = AskResult.CANCEL;
				shell.close();
			}
		});

		shell.setDefaultButton(no);
		shell.pack();
		styledText.setText("");

		doc_s = message.getString("doc_s");
		doc_t = message.getString("doc_t");
		s_tmx_sl = message.getString("s_tmx_sl");
		s_tmx_tl = message.getString("s_tmx_tl");
		t_tmx_tl = message.getString("t_tmx_tl");
		t_tmx_sl = message.getString("t_tmx_sl");

		this.sourceLanguage = sourceLanguage;
		this.targetLanguage = targetLanguage;
		this.segmentNumber = segmentNumber;
		this.dataSource = dataSource;
		this.translationCheckResult = translationCheckResult;
		this.sourceSegment = sourceSegment;
		this.targetSegment = targetSegment;

		dataSourceName = "";
		if (dataSource != null)
			dataSourceName = dataSource.getDataSourceName();
		String header = message.getString("Save_To_DataSource") + " \"" + dataSourceName + "\" " + message.getString("Save_To_DataSource_Segment");

		shell.setText(header);

		if (sourceAndTargetResults != null)
		{
			handleSelection(sourceAndTargetResults, translationCheckResult.getSourceAndTargetSegmentMatches(), "sourcetarget");
		}
		else if (sourceResults != null)
		{
			if (this.getSelectedMultiLingualEntry() == null)
				handleSelection(sourceResults, translationCheckResult.getSourceSegmentMatches(), "source");
			else if (targetResults != null)
			{
				if (this.getSelectedMultiLingualEntry() == null)
					handleSelection(targetResults, translationCheckResult.getTargetSegmentMatches(), "target");
			}
		}

		// createTranslationCheckResultString();
	}

	private String adaptMessage(String message)
	{
		message = message.replaceAll(doc_s_replace, doc_s);
		message = message.replaceAll(doc_t_replace, doc_t);
		message = message.replaceAll(s_tmx_sl_replace, s_tmx_sl);
		message = message.replaceAll(s_tmx_tl_replace, s_tmx_tl);
		message = message.replaceAll(t_tmx_tl, t_tmx_tl);
		message = message.replaceAll(t_tmx_tl_replace, t_tmx_tl);
		message = message.replaceAll(t_tmx_sl_replace, t_tmx_sl);

		return message;
	}

	/**
	 * createTranslationCheckResultString
	 * 
	 * @param translationCheckResult
	 * @return
	 */
	private String createTranslationCheckResultString(String sourceSegment, String targetSegment, MultiLingualObject multi, String type)
	{
		String formattedMessage = message.getString("saveTranslationToDataSource") + " " + dataSourceName + " " + message.getString("forSegment")
				+ " " + segmentNumber + "?\n\n" + message.getString("MultiObjectsStartString") + "\n" + message.getString("PleaseRecheck") + "\n"
				+ message.getString("DoYouReallyWantToAdd") + "\n\n";

		formattedMessage = formattedMessage + message.getString("doc_s") + "\"" + sourceSegment + "\"\n\n";

		String typeMessageS = "";
		String typeMessageT = "";
		if (type.equals("source"))
		{
			typeMessageS = s_tmx_sl;
			typeMessageT = s_tmx_tl;
		}
		else if (type.equals("target"))
		{
			typeMessageS = t_tmx_sl;
			typeMessageT = t_tmx_tl;
		}
		else if (type.equals("sourcetarget"))
		{
			typeMessageS = s_tmx_sl;
			typeMessageT = t_tmx_tl;
		}

		Vector<MonoLingualObject> monos = multi.getMonoLingualObjectsAsVector(this.sourceLanguage);
		for (int j = 0; j < monos.size(); j++)
		{
			formattedMessage = formattedMessage + typeMessageS + message.getString("idex") + " " + monos.get(j).getStUniqueID() + ":"
					+ this.sourceLanguage + ":\n\"" + monos.get(j).getFormattedSegment() + "\"\n\n";
		}
		monos = multi.getMonoLingualObjectsAsVector(this.targetLanguage);
		for (int j = 0; j < monos.size(); j++)
		{
			formattedMessage = formattedMessage + typeMessageT + message.getString("idex") + monos.get(j).getStUniqueID() + ":" + this.targetLanguage
					+ ":\n\"" + monos.get(j).getFormattedSegment() + "\"\n\n";
		}

		formattedMessage = formattedMessage + message.getString("doc_t") + "\"" + targetSegment + "\"\n";
		formattedMessage = adaptMessage(formattedMessage);
		this.styledText.setText(formattedMessage);

		LogPrint.println(formattedMessage);

		String multiObjectsStartString = message.getString("MultiObjectsStartString");
		// String formattedMessage = this.styledText.getText();
		try
		{
			formatStyleRange(formattedMessage, doc_s, SWT.BOLD);
			formatStyleRange(formattedMessage, doc_t, SWT.BOLD);
			formatStyleRange(formattedMessage, s_tmx_sl, SWT.BOLD);
			formatStyleRange(formattedMessage, s_tmx_tl, SWT.BOLD);
			formatStyleRange(formattedMessage, t_tmx_sl, SWT.BOLD);
			formatStyleRange(formattedMessage, t_tmx_tl, SWT.BOLD);
			formatStyleRange(formattedMessage, multiObjectsStartString, SWT.BOLD);
			formatTranslations(formattedMessage, s_tmx_tl);
			formatTranslations(formattedMessage, t_tmx_tl);
			formatTranslations(formattedMessage, doc_t);
			formatIdStyleRange(formattedMessage);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			this.styledText.setText(formattedMessage);
		}
		return formattedMessage;
	}

	private void formatIdStyleRange(String mytext)
	{
		String idmatch = "ID:";
		String endidmatch = ":\n";
		int iStart = mytext.indexOf(idmatch);
		while (iStart > -1)
		{
			int iEnd = mytext.indexOf(endidmatch, iStart);
			if (iEnd > -1)
			{
				StyleRange multiObjectsStartStringStyleRange = new StyleRange();
				multiObjectsStartStringStyleRange.start = iStart;
				multiObjectsStartStringStyleRange.length = iEnd - iStart;
				multiObjectsStartStringStyleRange.fontStyle = SWT.ITALIC;
				this.styledText.setStyleRange(multiObjectsStartStringStyleRange);
				iStart = mytext.indexOf(idmatch, iEnd);
			}
			else
				break;
		}
	}

	private void formatStyleRange(String mytext, String formatText, int fontStyle)
	{
		int iStart = mytext.indexOf(formatText);
		while (iStart > -1)
		{
			StyleRange multiObjectsStartStringStyleRange = new StyleRange();
			multiObjectsStartStringStyleRange.start = iStart;
			multiObjectsStartStringStyleRange.length = formatText.length();
			LogPrint.println("formatStyleRange\n" + mytext + "\n" + formatText + "\n" + multiObjectsStartStringStyleRange.start + " "
					+ multiObjectsStartStringStyleRange.length);
			multiObjectsStartStringStyleRange.fontStyle = fontStyle;
			this.styledText.setStyleRange(multiObjectsStartStringStyleRange);
			iStart = mytext.indexOf(formatText, iStart + formatText.length());
		}
	}

	private void formatTranslations(String mytext, String formatText)
	{
		int iStart = mytext.indexOf(formatText);
		if (iStart == -1)
			return;
		iStart = mytext.indexOf("\"", iStart);
		while (iStart > -1)
		{
			int iEnd = mytext.indexOf("\"\n", iStart + 1);
			if (iEnd > -1)
			{
				StyleRange multiObjectsStartStringStyleRange = new StyleRange();
				multiObjectsStartStringStyleRange.start = iStart + 1;
				multiObjectsStartStringStyleRange.length = iEnd - iStart - 1;
				LogPrint.println(multiObjectsStartStringStyleRange.start + " " + multiObjectsStartStringStyleRange.length);
				multiObjectsStartStringStyleRange.foreground = red;
				this.styledText.setStyleRange(multiObjectsStartStringStyleRange);
			}
			else
				break;
			iStart = mytext.indexOf(formatText, iEnd + 1);
			if (iStart > -1)
				iStart = mytext.indexOf("\"\n", iStart);
		}
	}

	/**
	 * @return the button
	 */
	public AskResult getButton()
	{
		return button;
	}

	/**
	 * @return the selectedMultiLingualEntry
	 */
	public MultiLingualObject getSelectedMultiLingualEntry()
	{
		return selectedMultiLingualEntry;
	}

	private void handleSelection(List resultList, Vector<MultiLingualObject> matches, String type)
	{
		String mulid = resultList.getItem(resultList.getSelectionIndex());
		MultiLingualObject multi = null;
		for (int i = 0; i < matches.size(); i++)
		{
			if (matches.get(i).getUniqueID().equals(mulid))
			{
				multi = matches.get(i);
				// String mulString = multi.format();
				// StyledText styledText = (StyledText)
				// resultList.getData("styledText");
				createTranslationCheckResultString(sourceSegment, targetSegment, multi, type);
				setSelectedMultiLingualEntry(multi);
			}
		}
		if (addToMultiLingualObject != null)
		{
			addToMultiLingualObject.setEnabled(true);
			addToMultiLingualObject.setText(message.getString("ADD") + " " + mulid);
		}
	}

	/**
	 * @param button
	 *            the button to set
	 */
	public void setButton(AskResult button)
	{
		this.button = button;
	}

	/**
	 * @param selectedMultiLingualEntry
	 *            the selectedMultiLingualEntry to set
	 */
	public void setSelectedMultiLingualEntry(MultiLingualObject selectedMultiLingualEntry)
	{
		this.selectedMultiLingualEntry = selectedMultiLingualEntry;
	}

	public void show()
	{
		shell.open();
		shell.forceActive();
		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch())
			{
				display.sleep();
			}
		}
	}

}
