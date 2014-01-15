/*
 * Created on 20.05.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.models.applicationmodel.guimodel.editor.datasourceeditor;

import java.util.Hashtable;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;

import de.folt.models.applicationmodel.guimodel.support.ProgressDialog;
import de.folt.models.applicationmodel.guimodel.support.ProgressDialogSupport;
import de.folt.models.datamodel.DataSource;
import de.folt.models.datamodel.DataSourceInstance;
import de.folt.models.datamodel.MonoLingualObject;
import de.folt.models.datamodel.MultiLingualObject;
import de.folt.util.ColorTable;

/**
 * Class implements a data source form editor.
 * 
 * @author klemens
 * 
 */
public class DataSourceForm extends SashForm
{

	private DataSource dataSource;

	private DataSourceEditor dataSourceEditor = new DataSourceEditor();

	@SuppressWarnings("unused")
	private String dataSourceName;

	private Display display;

	private Combo fuzzysim;

	private Label idlabel;

	private Combo idselector;

	private String languageName = "Language: ";

	private Combo languages;

	private de.folt.util.Messages message;

	private Label monoidlabel;

	private List monoIdList;

	private String plainTextName;

	private OpenTMSSearchStyledText search;

	private Label searchidlabel;

	private Combo searchmethod;

	private List searchResultIdList;

	private SashForm segmentHolder;

	private OpenTMSXMLStyledTextWithPropertyEditor text[] = null;

	private org.eclipse.swt.widgets.List uniqueIdList;

	private String userLanguage = "en";

	/**
	 * @param parent
	 * @param style
	 * @param dataSourceEditor
	 */
	public DataSourceForm(Composite parent, int style, String datasource, DataSourceEditor dataSourceEditor)
	{
		super(parent, style);
		this.dataSourceEditor = dataSourceEditor;
		message = de.folt.util.Messages.getInstance(
				"de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.datasourceeditor", userLanguage);

		languageName = message.getString("languageName");
		plainTextName = message.getString("plainText");
		display = parent.getDisplay();
		dataSourceName = datasource;
		openDataSource(datasource);
		de.folt.util.Messages.getInstance(
				"de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.datasourceeditor", userLanguage);
		createContents(parent);
	}

	protected void bRemoveAllMULEntry()
	{
		MultiLingualObject multi = null;
		if (uniqueIdList.getItemCount() == 0)
			return;
		boolean bSuccess = false;
		
		Vector<Integer> succRemoved = new Vector<Integer>();
        Cursor hglass = new Cursor(this.dataSourceEditor.getShell().getDisplay(), SWT.CURSOR_WAIT);
        Cursor arrow = new Cursor(this.dataSourceEditor.getShell().getDisplay(), SWT.CURSOR_ARROW);
        
        this.dataSourceEditor.getShell().setCursor(hglass);
        ProgressDialog progressDialog = new ProgressDialog(this.dataSourceEditor.getShell(), message.getString("DeleteAllMUL"), message.getString("DeleteAllMUL"), ProgressDialog.SINGLE_BAR);
        progressDialog.open();
        progressDialog.updateProgressMessage("Export_TMX_Document");
        
        ProgressDialogSupport pdSupport = null;
        pdSupport = new ProgressDialogSupport(progressDialog);
		
		for (int index = 0; index < uniqueIdList.getItemCount(); index++)
		{
			String uniqueID = uniqueIdList.getItem(index);
			pdSupport.updateProgressIndication(index, uniqueIdList.getItemCount());
			if (idselector.getSelectionIndex() == 0)
			{
				multi = dataSource.getMultiLingualObjectFromUniqueId(uniqueID);
			}
			else
			{
				multi = dataSource.getMultiLingualObjectFromId(uniqueID);
			}

			if (multi != null)
			{
				bSuccess = dataSource.removeMultiLingualObject(multi);
				if (bSuccess)
				{
					succRemoved.add(index);
				}
				else
				{
					break;
				}
			}
		}

		if (bSuccess)
		{
			uniqueIdList.removeAll();
			searchResultIdList.removeAll();
			this.monoIdList.removeAll();
		}
		else
		{
			for (int i = succRemoved.size(); i >= 0; i--)
			{
				uniqueIdList.remove(succRemoved.get(i));
			}
			searchResultIdList.removeAll();
			this.monoIdList.removeAll();
		}

        progressDialog.close();
        this.dataSourceEditor.getShell().setCursor(arrow);
        
		displayEntry();
	}

	/**
	 * bRemoveMOLEntry
	 */
	protected void bRemoveMOLEntry()
	{
		MonoLingualObject mono = null;
		if (monoIdList.getItemCount() == 0)
			return;
		int index = monoIdList.getSelectionIndex();
		if (index == -1)
			return;

		String uniqueID = monoIdList.getItem(index);
		if (idselector.getSelectionIndex() == 0)
		{
			mono = dataSource.getMonoLingualObjectFromUniqueId(uniqueID);
		}
		else
		{
			mono = dataSource.getMonoLingualObjectFromId(uniqueID);
		}

		if (mono != null)
		{
			boolean bSuccess = dataSource.removeMonoLingualObject(mono);
			if (bSuccess)
			{
				monoIdList.remove(index);
				if (monoIdList.getItemCount() == 0)
				{
					index = uniqueIdList.getSelectionIndex();
					uniqueIdList.remove(index);
					searchResultIdList.removeAll();
					if (uniqueIdList.getItemCount() > 0)
					{
						if (index < uniqueIdList.getItemCount())
							uniqueIdList.select(index);
						else if (index == uniqueIdList.getItemCount())
							uniqueIdList.select(index - 1);
					}

				}
			}
			displayEntry();
		}

	}

	/**
	 * bRemoveMULEntry
	 */
	protected void bRemoveMULEntry()
	{
		MultiLingualObject multi = null;
		if (uniqueIdList.getItemCount() == 0)
			return;
		int index = uniqueIdList.getSelectionIndex();
		if (index == -1)
			return;
		String uniqueID = uniqueIdList.getItem(index);
		if (idselector.getSelectionIndex() == 0)
		{
			multi = dataSource.getMultiLingualObjectFromUniqueId(uniqueID);
		}
		else
		{
			multi = dataSource.getMultiLingualObjectFromId(uniqueID);
		}

		if (multi != null)
		{
			boolean bSuccess = dataSource.removeMultiLingualObject(multi);
			if (bSuccess)
			{
				uniqueIdList.remove(index);
				if (searchResultIdList.getItemCount() > 0)
				{
					for (int i = 0; i < searchResultIdList.getItemCount(); i++)
					{
						if (searchResultIdList.getItem(i).equals(uniqueID))
						{
							searchResultIdList.remove(i);
							break;
						}
					}
				}
				this.monoIdList.removeAll();
				if (uniqueIdList.getItemCount() > 0)
				{
					if (index < uniqueIdList.getItemCount())
						uniqueIdList.select(index);
					else if (index == uniqueIdList.getItemCount())
						uniqueIdList.select(index - 1);
				}
				displayEntry();
			}
		}
	}

	/**
	 * bSaveModifiedMols
	 */
	protected void bSaveModifiedMols()
	{
		if (text != null)
		{
			boolean bAnyThingChanged = false;
			for (int i = 0; i < text.length; i++)
			{
				if (text[i].isChanged())
				{
					MonoLingualObject mol = (MonoLingualObject) text[i].getData("MOL");
					String newSegment = text[i].getText();
					mol.setFormattedSegment(newSegment);
					mol.setPlainTextSegment(MonoLingualObject.simpleComputePlainText(newSegment));
					boolean bSaved = dataSource.saveModifiedMonoLingualObject(mol);
					if (bSaved)
					{
						text[i].setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
						text[i].setToolTipText(languageName + " " + mol.getLanguage() + "\nMOL ID: "
								+ mol.getStUniqueID() + " (" + mol.getLastAccessTime() + ")\nMUL ID:"
								+ mol.getParentMultiLingualObject().getStUniqueID() + " ("
								+ mol.getParentMultiLingualObject().getLastAccessTime() + ")\n" + plainTextName + "\n"
								+ mol.getPlainTextSegment());
						bAnyThingChanged = true;
					}
				}
				/*
				 * if (i == 0) { if (text[i].isChanged()) { MultiLingualObject
				 * mul = (MultiLingualObject) text[i].getData("MUL"); boolean
				 * bSaved = dataSource.saveModifiedMultiLingualObject(mul); if
				 * (bSaved) { bAnyThingChanged = true; } } }
				 */
			}
			if (bAnyThingChanged)
				dataSource.bPersist();
		}

	}

	protected void bSaveModifiedMul()
	{
		// get the MUL - just take the first one
		if ((text == null) || (text.length < 1))
			return;
		MultiLingualObject mul = (MultiLingualObject) text[0].getData("MUL");
		boolean bSaved = dataSource.saveModifiedMultiLingualObject(mul);
		if (bSaved)
			dataSource.bPersist();
	}

	/**
	 * Creates the main window's contents
	 * 
	 * @param parent
	 *            the parent window
	 */
	private void createContents(Widget parent)
	{
		try
		{
			// Change the color used to paint the sashes
			this.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
			int iGridData = GridData.GRAB_VERTICAL | GridData.VERTICAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL
					| GridData.FILL_HORIZONTAL;
			// the list menu
			SashForm toolsHolder = new SashForm(this, SWT.NONE);
			toolsHolder.setOrientation(SWT.VERTICAL);
			GridLayout toolsLayout = new GridLayout(1, false);
			toolsLayout.marginWidth = 1;
			toolsLayout.marginHeight = 1;
			toolsLayout.horizontalSpacing = 0;
			toolsHolder.setLayout(toolsLayout);
			toolsHolder.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));

			toolsHolder.addKeyListener(new KeyListener()
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

			idselector = new Combo(toolsHolder, SWT.DROP_DOWN | SWT.READ_ONLY);
			int iGridDataIdSelector = GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL;
			GridData gridentry = new GridData(iGridDataIdSelector);
			idselector.setLayoutData(gridentry);
			idselector.add("stUniqueId");
			idselector.add("id");
			idselector.select(0);
			idselector.addSelectionListener(new SelectionAdapter()
			{
				public void widgetSelected(SelectionEvent e)
				{
					fillIdList();
				}
			});

			Composite composite = new Composite(toolsHolder, SWT.NONE);
			composite.setLayout(new GridLayout(2, false));

			idlabel = new Label(composite, SWT.NONE);
			idlabel.setText(message.getString("AllIds"));

			Button update = new Button(composite, SWT.PUSH);
			update.setText(message.getString("UpdateUniquidlist"));
			update.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			update.addSelectionListener(new SelectionAdapter()
			{
				public void widgetSelected(SelectionEvent e)
				{
					fillIdList();
				}
			});
			update.setImage(new Image(display, "images/update.gif"));

			/*
			 * ToolBar toolbarcomposite = new ToolBar(composite, SWT.NONE);
			 * 
			 * ToolItem fUpdate = new ToolItem(toolbarcomposite, SWT.PUSH |
			 * SWT.FLAT); fUpdate.setImage(new Image(display,
			 * "images/update.gif"));
			 * fUpdate.setToolTipText(message.getString("UpdateUniquidlist"));
			 * fUpdate.addSelectionListener(new SelectionAdapter() { public void
			 * widgetSelected(SelectionEvent e) { fillIdList(); }
			 * 
			 * });
			 */

			uniqueIdList = new org.eclipse.swt.widgets.List(toolsHolder, SWT.SINGLE | SWT.V_SCROLL | SWT.TOP
					| SWT.BORDER);
			gridentry = new GridData(iGridData);
			uniqueIdList.setLayoutData(gridentry);
			uniqueIdList.addSelectionListener(new SelectionAdapter()
			{
				public void widgetSelected(SelectionEvent e)
				{
					displayEntry();
				}
			});
			uniqueIdList.setToolTipText(message.getString("uniqueIdList"));

			monoidlabel = new Label(toolsHolder, SWT.NONE);
			monoidlabel.setText(message.getString("monoidlabel"));

			monoIdList = new org.eclipse.swt.widgets.List(toolsHolder, SWT.SINGLE | SWT.V_SCROLL | SWT.TOP | SWT.BORDER);
			gridentry = new GridData(iGridData);
			monoIdList.setLayoutData(gridentry);
			monoIdList.addSelectionListener(new SelectionAdapter()
			{
				public void widgetSelected(SelectionEvent e)
				{
					int iSel = monoIdList.getSelectionIndex();
					if (text != null)
					{
						for (int i = 0; i < text.length; i++)
						{
							if (iSel == i)
							{
								text[i].setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
							}
							else
							{
								text[i].setBackground(getDisplay().getSystemColor(SWT.COLOR_TITLE_INACTIVE_FOREGROUND));
							}
						}
					}
				}
			});
			monoIdList.setToolTipText(message.getString("monoIdList"));

			searchidlabel = new Label(toolsHolder, SWT.NONE);
			searchidlabel.setText(message.getString("SearchIDs"));

			searchResultIdList = new org.eclipse.swt.widgets.List(toolsHolder, SWT.SINGLE | SWT.V_SCROLL | SWT.TOP
					| SWT.BORDER);
			gridentry = new GridData(iGridData);
			searchResultIdList.setLayoutData(gridentry);
			searchResultIdList.addSelectionListener(new SelectionAdapter()
			{
				public void widgetSelected(SelectionEvent e)
				{
					displaySearchEntry();
				}
			});

			toolsHolder.setWeights(new int[] { 1, 2, 15, 1, 5, 1, 7 });
			// get all ids from the data source
			if (dataSource != null)
			{
				Vector<String> unis = dataSource.getUniqueIds();
				if (unis != null)
				{
					int iL = unis.size();
					for (int i = 0; i < iL; i++)
					{
						uniqueIdList.add(unis.get(i));
					}
					uniqueIdList.setSelection(0);
				}
			}
			else
				return;

			idlabel.setText(message.getString("AllIds") + " (" + uniqueIdList.getItemCount() + ")");

			SashForm searchAndTextHolder = new SashForm(this, SWT.NONE);
			searchAndTextHolder.setOrientation(SWT.VERTICAL);
			searchAndTextHolder.setLayout(toolsLayout);
			searchAndTextHolder.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));

			segmentHolder = new SashForm(searchAndTextHolder, SWT.NONE);
			segmentHolder.setOrientation(SWT.VERTICAL);
			GridLayout segmentToolsLayout = new GridLayout(1, false);
			segmentToolsLayout.marginWidth = 1;
			segmentToolsLayout.marginHeight = 1;
			segmentToolsLayout.horizontalSpacing = 0;
			segmentHolder.setLayout(toolsLayout);
			segmentHolder.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));

			GridData textgridentry = new GridData(iGridData);
			ToolBar toolBar = new ToolBar(searchAndTextHolder, SWT.NONE | SWT.BORDER);
			createToolBar(toolBar);

			search = new OpenTMSSearchStyledText(searchAndTextHolder, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL
					| SWT.BORDER);
			search.setLayoutData(textgridentry);
			search.setData("DataSourceForm", this);

			searchAndTextHolder.setWeights(new int[] { 15, 1, 3 });

			this.setWeights(new int[] { 1, 4 });

			this.displayEntry();
		}
		catch (Exception ex)
		{
			dataSource = null;
			return;
		}
	}

	private void createToolBar(ToolBar toolBar)
	{

		ToolItem fSearch = new ToolItem(toolBar, SWT.PUSH | SWT.FLAT);
		fSearch.setImage(new Image(display, "images/searchtmx.gif"));
		fSearch.setToolTipText(message.getString("SearchSegment"));
		fSearch.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				search();
			}

		});

		ToolItem sep = new ToolItem(toolBar, SWT.SEPARATOR);

		languages = new Combo(toolBar, SWT.READ_ONLY);
		languages.setToolTipText(message.getString("ChooseLanguage"));
		languages.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{

			}
		});

		languages.setItems(de.folt.util.LanguageHandling.getCombinedLanguages());
		languages.setItem(0, "---------");
		languages.pack();
		sep.setWidth(languages.getSize().x);
		sep.setControl(languages);
		languages.select(0);

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
				else if (searchmethod.getSelectionIndex() == 3)
				{
					fuzzysim.setEnabled(true);
					fuzzysim.removeAll();
					fuzzysim.add("OR", 0);
					fuzzysim.add("AND", 1);
					fuzzysim.setToolTipText(message.getString("ChooseOrAnd") + " ("
							+ new de.folt.util.WordHandling().getDefaultSplitString() + ")");
					fuzzysim.select(0);
				}
				else
				{
					fuzzysim.setEnabled(false);
				}
			}
		});
		searchmethod.add("FUZZY", 0);
		searchmethod.add("EXACT", 1);
		searchmethod.add("REGEXP", 2);
		searchmethod.add("WORD", 3);
		searchmethod.add("LIKE", 4);
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

		new ToolItem(toolBar, SWT.SEPARATOR);

		ToolItem fDeleteSelectedMUL = new ToolItem(toolBar, SWT.PUSH | SWT.FLAT);
		fDeleteSelectedMUL.setImage(new Image(display, "images/deletemul.gif")); //$NON-NLS-1$
		fDeleteSelectedMUL.setToolTipText(message.getString("DeleteSelectedMUL")); //$NON-NLS-1$
		fDeleteSelectedMUL.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				try
				{
					bRemoveMULEntry();
				}
				catch (Exception e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		ToolItem fDeleteAllMUL = new ToolItem(toolBar, SWT.PUSH | SWT.FLAT);
		fDeleteAllMUL.setImage(new Image(display, "images/deleteAllmul.gif")); //$NON-NLS-1$
		fDeleteAllMUL.setToolTipText(message.getString("DeleteAllMUL")); //$NON-NLS-1$
		fDeleteAllMUL.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				try
				{
					bRemoveAllMULEntry();
				}
				catch (Exception e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		ToolItem fDeleteSelectedMOL = new ToolItem(toolBar, SWT.PUSH | SWT.FLAT);
		fDeleteSelectedMOL.setImage(new Image(display, "images/deletemol.gif")); //$NON-NLS-1$
		fDeleteSelectedMOL.setToolTipText(message.getString("DeleteSelectedMOL")); //$NON-NLS-1$
		fDeleteSelectedMOL.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				bRemoveMOLEntry();
			}

		});

		new ToolItem(toolBar, SWT.SEPARATOR);

		ToolItem fSaveModifiedMOLs = new ToolItem(toolBar, SWT.PUSH | SWT.FLAT);
		fSaveModifiedMOLs.setImage(new Image(display, "images/savemol.gif")); //$NON-NLS-1$
		fSaveModifiedMOLs.setToolTipText(message.getString("SaveModifiedMOLs")); //$NON-NLS-1$
		fSaveModifiedMOLs.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				bSaveModifiedMols();
				bSaveModifiedMul();
			}
		});

		toolBar.pack();
	}

	/**
	 * displayEntry
	 */
	protected void displayEntry()
	{
		if (uniqueIdList.getItemCount() <= 0)
			return;

		int index = uniqueIdList.getSelectionIndex();
		String uniqueID = uniqueIdList.getItem(index);
		displayEntry(uniqueID);
	}

	/**
	 * displayEntry
	 */
	public void displayEntry(String uniqueID)
	{
		if (text != null)
		{
			for (int i = 0; i < text.length; i++)
			{
				if (text[i].getPropertyEditor() != null)
					text[i].getPropertyEditor().close();
				text[i].dispose();
			}
		}

		if (searchResultIdList != null)
		{
			int iPos = uniqueIdList.indexOf(uniqueID);
			if (iPos != -1)
			{
				uniqueIdList.setSelection(iPos);
			}
		}

		int iGridData = GridData.GRAB_VERTICAL | GridData.VERTICAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL
				| GridData.FILL_HORIZONTAL;

		MultiLingualObject multi = null;
		if (uniqueIdList.isFocusControl())
		{
			if (idselector.getSelectionIndex() == 0)
			{
				multi = dataSource.getMultiLingualObjectFromUniqueId(uniqueID);
			}
			else
			{
				multi = dataSource.getMultiLingualObjectFromId(uniqueID);
			}
		}
		else
			multi = dataSource.getMultiLingualObjectFromUniqueId(uniqueID);

		if (multi != null)
		{
			Vector<MonoLingualObject> monos = multi.getMonoLingualObjectsAsVector();
			if (monos.size() != 0)
			{
				text = new OpenTMSXMLStyledTextWithPropertyEditor[monos.size()];
				monoIdList.removeAll();
				for (int i = 0; i < monos.size(); i++)
				{
					text[i] = new OpenTMSXMLStyledTextWithPropertyEditor(segmentHolder, SWT.MULTI | SWT.V_SCROLL
							| SWT.WRAP | SWT.BORDER);
					GridData textgridentry = new GridData(iGridData);
					text[i].setLayoutData(textgridentry);

					// tmx
					TagDescriptor tagDescriptorPh = new TagDescriptor("ph", ColorTable.getInstance(getDisplay(),
							"black"), ColorTable.getInstance(getDisplay(), "darkorange"));
					TagDescriptor tagDescriptorUt = new TagDescriptor("ut", ColorTable.getInstance(getDisplay(),
							"black"), ColorTable.getInstance(getDisplay(), "orange"));
					TagDescriptor tagDescriptorIt = new TagDescriptor("it", ColorTable.getInstance(getDisplay(),
							"black"), ColorTable.getInstance(getDisplay(), "orangered"));
					TagDescriptor tagDescriptorHi = new TagDescriptor("hi", ColorTable.getInstance(getDisplay(),
							"black"), ColorTable.getInstance(getDisplay(), "purple"));
					TagDescriptor tagDescriptorEpt = new TagDescriptor("ebt", ColorTable.getInstance(getDisplay(),
							"black"), ColorTable.getInstance(getDisplay(), "palevioletred"));
					TagDescriptor tagDescriptorBpt = new TagDescriptor("bpt", ColorTable.getInstance(getDisplay(),
							"black"), ColorTable.getInstance(getDisplay(), "indianred"));
					TagDescriptor tagDescriptorSub = new TagDescriptor("sub", ColorTable.getInstance(getDisplay(),
							"black"), ColorTable.getInstance(getDisplay(), "mediumvioletred"));

					// xliff
					TagDescriptor tagDescriptorG = new TagDescriptor("g",
							ColorTable.getInstance(getDisplay(), "black"), ColorTable.getInstance(getDisplay(),
									"cadetblue"));
					TagDescriptor tagDescriptorX = new TagDescriptor("x",
							ColorTable.getInstance(getDisplay(), "black"), ColorTable.getInstance(getDisplay(), "blue"));
					TagDescriptor tagDescriptorBx = new TagDescriptor("bx", ColorTable.getInstance(getDisplay(),
							"black"), ColorTable.getInstance(getDisplay(), "blueviolet"));
					TagDescriptor tagDescriptorEx = new TagDescriptor("ex", ColorTable.getInstance(getDisplay(),
							"black"), ColorTable.getInstance(getDisplay(), "cadetblue"));
					TagDescriptor tagDescriptorMrk = new TagDescriptor("mrk", ColorTable.getInstance(getDisplay(),
							"black"), ColorTable.getInstance(getDisplay(), "cornflowerblue"));

					text[i].addTagDescriptor(tagDescriptorG);
					text[i].addTagDescriptor(tagDescriptorPh);
					text[i].addTagDescriptor(tagDescriptorUt);
					text[i].addTagDescriptor(tagDescriptorIt);
					text[i].addTagDescriptor(tagDescriptorHi);
					text[i].addTagDescriptor(tagDescriptorEpt);
					text[i].addTagDescriptor(tagDescriptorBpt);
					text[i].addTagDescriptor(tagDescriptorSub);
					text[i].addTagDescriptor(tagDescriptorX);
					text[i].addTagDescriptor(tagDescriptorBx);
					text[i].addTagDescriptor(tagDescriptorEx);
					text[i].addTagDescriptor(tagDescriptorMrk);

					text[i].setText(monos.get(i).getFormattedSegment());
					text[i].setStyleRange(text[i].getText());

					String properties = "MOL Properties for " + i + ":\n" + monos.get(i).getLastAccessTime() + "\n"
							+ monos.get(i).getLinguisticProperties().simpleFormat();
					String mulproperties = "MUL Properties:\n" + multi.getLastAccessTime() + "\n"
							+ multi.getLinguisticProperties().simpleFormat();
					String toolTip = languageName + " " + monos.get(i).getLanguage() + "\nMOL ID: "
							+ monos.get(i).getStUniqueID() + "\nMUL ID:" + multi.getStUniqueID() + " ("
							+ multi.getLastAccessTime() + ")\n" + plainTextName + "\n"
							+ monos.get(i).getPlainTextSegment() + "\n\n" + properties + "\n" + mulproperties;
					text[i].setToolTipText(toolTip);
					if (idselector.getSelectionIndex() == 0)
					{
						monoIdList.add(monos.get(i).getStUniqueID());
					}
					else
					{
						monoIdList.add(monos.get(i).getId().toString());
					}
					text[i].setData("MOL.stUniqueID", monos.get(i).getStUniqueID());
					text[i].setData("MUL.stUniqueID", multi.getStUniqueID());
					text[i].setData("index", (Integer) i);
					text[i].setData("MOL", monos.get(i));
					text[i].setData("MUL", multi);
					text[i].setData("datasource", dataSource);
				}
			}
			monoidlabel.setText(message.getString("monoidlabel") + " " + uniqueID + " (" + monos.size() + ")");
		}

		segmentHolder.layout();
	}

	/**
	 * displaySearchEntry
	 */
	protected void displaySearchEntry()
	{
		try
		{
			int index = searchResultIdList.getSelectionIndex();
			String uniqueId = searchResultIdList.getItem(index);
			displayEntry(uniqueId);
		}
		catch (Exception e)
		{
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	public void fillIdList()
	{
		if (idselector.getSelectionIndex() == 0)
		{
			// get all ids from the data source
			uniqueIdList.removeAll();
			Vector<String> unis = dataSource.getUniqueIds();
			if (unis != null)
			{
				int iL = unis.size();
				for (int i = 0; i < iL; i++)
				{
					uniqueIdList.add(unis.get(i));
				}
			}
		}
		else
		{
			// get all ids from the data source
			uniqueIdList.removeAll();
			Vector<Integer> unis = dataSource.getIds();
			if (unis != null)
			{
				int iL = unis.size();
				for (int i = 0; i < iL; i++)
				{
					uniqueIdList.add(unis.get(i) + "");
				}
			}
		}

		uniqueIdList.select(0);
		uniqueIdList.setSelection(0);
	}

	/**
	 * @return the dataSource
	 */
	public DataSource getDataSource()
	{
		return dataSource;
	}

	/**
	 * @return the dataSourceEditor
	 */
	public DataSourceEditor getDataSourceEditor()
	{
		return dataSourceEditor;
	}

	/**
	 * @return the search
	 */
	public OpenTMSStyledText getSearch()
	{
		return search;
	}

	/**
	 * @return the text
	 */
	public OpenTMSStyledText getText()
	{
		OpenTMSStyledText x = null;
		if (text != null)
		{
			for (int i = 0; i < text.length; i++)
			{
				x = text[i];
				if (x.isFocusControl())
					return x;
			}
		}
		return x;
	}

	/**
	 * openDataSource
	 * 
	 * @param datasource2
	 */
	private void openDataSource(String datasource)
	{
		try
		{
			dataSource = DataSourceInstance.createInstance(datasource);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	public void search()
	{
		if (search.getText().equals(""))
			return;
		searchResultIdList.removeAll();
		monoIdList.removeAll();
		searchidlabel.setText(message.getString("SearchIDs"));
		Hashtable<String, Object> searchParameters = new Hashtable<String, Object>();
		int iLangIndex = languages.getSelectionIndex();
		String lancode = de.folt.util.LanguageHandling.getShortLanguageCodeFromCombinedTable(languages
				.getItem(iLangIndex));
		if (iLangIndex > 0)
		{
			searchParameters.put("sourceLanguage", lancode);
		}

		MonoLingualObject searchMonoLingualObject = new MonoLingualObject();
		searchMonoLingualObject.setFormattedSegment(search.getText());
		searchMonoLingualObject.setLanguage("");
		if (iLangIndex > 0)
		{
			if (lancode.equals(""))
				searchMonoLingualObject.setLanguage(null);
			else
				searchMonoLingualObject.setLanguage(lancode);
		}
		else
		{
			searchMonoLingualObject.setLanguage(null);
		}

		int iTypeIndex = searchmethod.getSelectionIndex();
		String method = searchmethod.getItem(iTypeIndex);
		Vector<MonoLingualObject> searchresult = null;
		if (method.equalsIgnoreCase("fuzzy"))
		{
			searchParameters.put("fuzzy", "true");
			int iFuzzyIndex = fuzzysim.getSelectionIndex();
			String fuzzyvalue = fuzzysim.getItem(iFuzzyIndex);
			searchParameters.put("similarity", fuzzyvalue);
			searchresult = dataSource.search(searchMonoLingualObject, searchParameters);

		}
		else if (method.equalsIgnoreCase("REGEXP"))
		{
			searchresult = dataSource.searchRegExp(searchMonoLingualObject, searchParameters);
		}
		else if (method.equalsIgnoreCase("WORD"))
		{
			searchParameters.put("wordbased", "true");
			int iFuzzyIndex = fuzzysim.getSelectionIndex();
			String fuzzyvalue = fuzzysim.getItem(iFuzzyIndex);
			if (fuzzyvalue.equalsIgnoreCase("OR"))
				searchParameters.put("orbased", "true");
			else
				searchParameters.put("orbased", "false");
			searchresult = dataSource.searchWordBased(searchMonoLingualObject, searchParameters);
		}
		else if (method.equalsIgnoreCase("EXACT"))
		{
			searchParameters.put("fuzzy", "false");
			searchParameters.put("wordbased", "false");
			searchresult = dataSource.search(searchMonoLingualObject, searchParameters);
		}
		else if (method.equalsIgnoreCase("LIKE"))
		{
			searchParameters.put("fuzzy", "false");
			searchParameters.put("wordbased", "false");
			searchParameters.put("like", "true");
			searchresult = dataSource.search(searchMonoLingualObject, searchParameters);
		}

		if (searchresult != null)
		{

			for (int i = 0; i < searchresult.size(); i++)
			{
				String id = searchresult.get(i).getParentMultiLingualObject().getStUniqueID();
				if (searchResultIdList.indexOf(id) == -1)
					searchResultIdList.add(id);
			}
			if (searchResultIdList.getItemCount() > 0)
				searchResultIdList.setSelection(0);
			searchidlabel.setText(message.getString("SearchIDs") + "(" + searchResultIdList.getItemCount() + ")");
		}
		else
			searchidlabel.setText(message.getString("SearchIDs") + "(0)");

	}

	/**
	 * selectMULId - select the uniqueID and display the entry
	 * 
	 * @param uniqueID
	 *            the uniqueID to display
	 * @return true if found and displayed, otherwise false
	 */

	public boolean selectUniqueId(String uniqueID)
	{
		fillIdList();
		int index = uniqueIdList.indexOf(uniqueID);
		if (index != -1)
		{
			idselector.select(0);
			uniqueIdList.select(index);
			uniqueIdList.setSelection(index);
			this.displayEntry(uniqueID);
			return true;
		}

		return false;

	}

	/**
	 * @param dataSourceEditor
	 *            the dataSourceEditor to set
	 */
	public void setDataSourceEditor(DataSourceEditor dataSourceEditor)
	{
		this.dataSourceEditor = dataSourceEditor;
	}

}
