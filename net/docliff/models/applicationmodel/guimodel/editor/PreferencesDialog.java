/*
 * Created on 27.10.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package net.docliff.models.applicationmodel.guimodel.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import de.folt.util.Messages;

/**
 * @author klemens
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class PreferencesDialog extends Dialog
{

	private Button currentDataSource;

	private Display display;

	private de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.EditorConfiguration editorConfiguration = null;

	private Messages message;

	private PreferencesContainer preferencesContainer = new PreferencesContainer();

	private Button queryAddTranslation;

	private Button queryNewTranslation;

	private Button querySourceMatch;

	private Button queryTargetMatch;

	private Shell shell;

	private String userLanguage = "en";

	private XliffEditorForm xliffEditorForm = null;

	private Button segmentDictionaryOnTop;

	private Button globalDictionaryOnTop;

	private Button searchInApprovedSegments;

	/**
	 * @param parent
	 * @param xliffEditorForm
	 */
	public PreferencesDialog(Shell parent, PreferencesContainer optionsContainer,
			de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.EditorConfiguration editorConfiguration, XliffEditorForm xliffEditorForm)
	{
		super(parent);
		this.editorConfiguration = editorConfiguration;
		this.preferencesContainer = optionsContainer;
		this.xliffEditorForm = xliffEditorForm;
		message = de.folt.util.Messages.getInstance("net.docliff.models.applicationmodel.guimodel.editor.XliffEditor", userLanguage);
		int iGridData = GridData.GRAB_VERTICAL | GridData.VERTICAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL;

		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL | SWT.CLOSE);
		display = shell.getDisplay();
		shell.setText(message.getString("OptionsDialog")); //$NON-NLS-1$
		shell.setLayout(new GridLayout(1, true));

		Group checkMatchGroup = new Group(shell, SWT.NONE);
		checkMatchGroup.setText(message.getString("matchQueryPrefs"));
		checkMatchGroup.setLayout(new GridLayout(2, true));
		checkMatchGroup.setLayoutData(new GridData(iGridData));

		queryNewTranslation = new Button(checkMatchGroup, SWT.CHECK);
		queryNewTranslation.setText(message.getString("queryNewTranslation"));
		queryNewTranslation.setLayoutData(new GridData(iGridData));
		queryNewTranslation.setSelection(preferencesContainer.isQueryNewTranslation());
		queryNewTranslation.setToolTipText(message.getString("requiresRestart"));

		queryAddTranslation = new Button(checkMatchGroup, SWT.CHECK);
		queryAddTranslation.setText(message.getString("queryAddTranslation"));
		queryAddTranslation.setLayoutData(new GridData(iGridData));
		queryAddTranslation.setSelection(preferencesContainer.isQueryAddTranslation());
		queryAddTranslation.setToolTipText(message.getString("requiresRestart"));

		querySourceMatch = new Button(checkMatchGroup, SWT.CHECK);
		querySourceMatch.setText(message.getString("querySourceMatch"));
		querySourceMatch.setLayoutData(new GridData(iGridData));
		querySourceMatch.setSelection(preferencesContainer.isQuerySourceMatch());
		querySourceMatch.setToolTipText(message.getString("requiresRestart"));

		queryTargetMatch = new Button(checkMatchGroup, SWT.CHECK);
		queryTargetMatch.setText(message.getString("queryTargetMatch"));
		queryTargetMatch.setLayoutData(new GridData(iGridData));
		queryTargetMatch.setSelection(preferencesContainer.isQueryTargetMatch());
		queryTargetMatch.setToolTipText(message.getString("requiresRestart"));

		queryNewTranslation = new Button(checkMatchGroup, SWT.CHECK);
		queryNewTranslation.setText(message.getString("queryNewTranslation"));
		queryNewTranslation.setLayoutData(new GridData(iGridData));
		queryNewTranslation.setSelection(preferencesContainer.isQueryNewTranslation());
		queryNewTranslation.setToolTipText(message.getString("requiresRestart"));

		Group searchOptionGroup = new Group(shell, SWT.NONE);
		searchOptionGroup.setText(message.getString("searchPrefs"));
		searchOptionGroup.setLayout(new GridLayout(2, true));
		searchOptionGroup.setLayoutData(new GridData(iGridData));

		searchInApprovedSegments = new Button(searchOptionGroup, SWT.CHECK);
		searchInApprovedSegments.setText(message.getString("searchInApprovedSegments"));
		searchInApprovedSegments.setLayoutData(new GridData(iGridData));
		searchInApprovedSegments.setSelection(preferencesContainer.isbSearchIfApproved());
		searchInApprovedSegments.setToolTipText(message.getString("searchInApprovedSegments"));

		// default Data Sources for tm and pt

		currentDataSource = new Button(shell, SWT.CHECK);
		currentDataSource.setText(message.getString("saveCurrentDataSourceAsPreferredDataSources"));
		currentDataSource.setLayoutData(new GridData(iGridData));
		if (xliffEditorForm == null)
		{
			currentDataSource.setEnabled(false);
			currentDataSource.setSelection(false);
		}
		currentDataSource.setToolTipText(message.getString("requiresRestart"));

		Group dictionaryViewerGroup = new Group(shell, SWT.NONE);
		dictionaryViewerGroup.setText(message.getString("dictionaryViewer"));
		dictionaryViewerGroup.setLayout(new GridLayout(2, true));
		dictionaryViewerGroup.setLayoutData(new GridData(iGridData));

		segmentDictionaryOnTop = new Button(dictionaryViewerGroup, SWT.CHECK);
		segmentDictionaryOnTop.setText(message.getString("segmentDictionaryOnTop"));
		segmentDictionaryOnTop.setLayoutData(new GridData(iGridData));
		segmentDictionaryOnTop.setSelection(preferencesContainer.isSegmentDictionaryOnTop());
		segmentDictionaryOnTop.setToolTipText(message.getString("requiresRestart"));

		globalDictionaryOnTop = new Button(dictionaryViewerGroup, SWT.CHECK);
		globalDictionaryOnTop.setText(message.getString("globalDictionaryOnTop"));
		globalDictionaryOnTop.setLayoutData(new GridData(iGridData));
		globalDictionaryOnTop.setSelection(preferencesContainer.isGlobalDictionaryOnTop());
		globalDictionaryOnTop.setToolTipText(message.getString("requiresRestart"));

		Button fontButton = new Button(shell, SWT.PUSH);
		fontButton.setText(message.getString("setFont")); //$NON-NLS-1$
		fontButton.setLayoutData(new GridData(iGridData));
		fontButton.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent ev)
			{

				FontDialog fd = new FontDialog(shell);
				if (preferencesContainer.getDefaultFont() == null)
				{
					preferencesContainer.setDefaultFont(shell.getFont());
				}
				FontData[] currentFont = preferencesContainer.getDefaultFont().getFontData();
				fd.setFontList(currentFont);
				FontData fontData = fd.open();
				if (fontData == null)
				{
					fd = null;
					return;
				}

				try
				{
					Font aFont = new Font(display, fontData);
					preferencesContainer.setDefaultFont(aFont);
					preferencesContainer.setDefaultFont(preferencesContainer.getDefaultFont());

				}
				catch (Exception e)
				{
					MessageBox box = new MessageBox(shell, SWT.PRIMARY_MODAL | SWT.ICON_ERROR | SWT.OK);
					box.setMessage(e.getLocalizedMessage());
					box.setMessage(e.toString());
					box.open();
				}
			}

		});

		Composite buttonComposite = new Composite(shell, SWT.NONE);
		GridLayout toolsLayout = new GridLayout(3, true);
		toolsLayout.marginWidth = 1;
		toolsLayout.marginHeight = 1;
		toolsLayout.horizontalSpacing = 0;
		buttonComposite.setLayout(toolsLayout);
		buttonComposite.setLayoutData(new GridData(iGridData));

		Button savePreferences = new Button(buttonComposite, SWT.PUSH);
		savePreferences.setText(message.getString("savePreferences")); //$NON-NLS-1$
		savePreferences.setLayoutData(new GridData(iGridData));
		savePreferences.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent ev)
			{
				savePreferences();
			}
		});

		Button saveandexit = new Button(buttonComposite, SWT.PUSH);
		saveandexit.setText(message.getString("saveandexit")); //$NON-NLS-1$
		saveandexit.setLayoutData(new GridData(iGridData));
		saveandexit.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent ev)
			{
				savePreferences();
				shell.close();
			}
		});

		Button exit = new Button(buttonComposite, SWT.PUSH);
		exit.setText(message.getString("exitwithoutsave")); //$NON-NLS-1$
		exit.setLayoutData(new GridData(iGridData));
		exit.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent ev)
			{
				shell.close();
			}
		});

		shell.pack();
	}

	/**
	 * @return the optionsContainer
	 */
	public PreferencesContainer getPreferencesContainer()
	{
		return preferencesContainer;
	}

	private void savePreferences()
	{
		preferencesContainer.setQueryNewTranslation(queryNewTranslation.getSelection());
		preferencesContainer.setQueryAddTranslation(queryAddTranslation.getSelection());
		preferencesContainer.setQuerySourceMatch(querySourceMatch.getSelection());
		preferencesContainer.setQueryTargetMatch(queryTargetMatch.getSelection());
		preferencesContainer.setGlobalDictionaryOnTop(globalDictionaryOnTop.getSelection());
		preferencesContainer.setSegmentDictionaryOnTop(segmentDictionaryOnTop.getSelection());
		preferencesContainer.setbSearchIfApproved(searchInApprovedSegments.getSelection());

		// get now the currently choosen data sources
		if ((xliffEditorForm != null) && currentDataSource.getSelection())
		{
			org.eclipse.swt.widgets.List ptList = xliffEditorForm.getPtDataSourceFormComposite().getDataSources();
			String datasources = "";
			for (int i = 0; i < ptList.getItems().length; i++)
			{
				if (i == 0)
					datasources = ptList.getItem(i);
				else
					datasources = datasources + ";" + ptList.getItem(i);
			}
			preferencesContainer.setPtDataSources(datasources);
			ptList = xliffEditorForm.getTmDataSourceFormComposite().getDataSources();
			datasources = "";
			for (int i = 0; i < ptList.getItems().length; i++)
			{
				if (i == 0)
					datasources = ptList.getItem(i);
				else
					datasources = datasources + ";" + ptList.getItem(i);
			}
			preferencesContainer.setTmDataSources(datasources);
		}

		preferencesContainer.savePreferences(editorConfiguration);
	}

	/**
	 * @param optionsContainer
	 *            the optionsContainer to set
	 */
	public void setPreferencesContainer(PreferencesContainer preferencesContainer)
	{
		this.preferencesContainer = preferencesContainer;
	}

	public void show()
	{
		shell.open();
		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch())
			{
				display.sleep();
			}
		}
	}

}
