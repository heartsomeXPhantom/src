/*
 * Created on 20.03.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.models.applicationmodel.guimodel.editor.datasourceeditor;

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.araya.eaglememex.util.EMXProperties;

import de.folt.util.CodePageHandling;
import de.folt.util.LanguageHandling;
import de.folt.util.OpenTMSProperties;

public class CreateOpenTMSDataSource extends Dialog
{
	private Button chooseFileName;

	private Text databaseText;

	private String dataSourceName = null;

	private Combo dataSourcetype;

	private OpenTMSDataSourceTypes dbtypes;

	private Display display;

	private Combo encCombo;

	private String encoding = "";

	private de.folt.util.Messages message;

	private Text passText;

	private Text portText;

	private Text serverText;

	private Shell shell;

	private List table;

	private Combo type;

	private String[] types;

	private String userLanguage = "en";

	private Text userText;

	private Button select;

	private Combo sourceLangCombo;

	protected String sourceLanguage = "";

	protected String targetLanguage = "";

	private Combo targetLangCombo;

	private Button syncButton;

	private Text	creationUser;

	private Text	userList;

	/**
	 * Display a Create Open TMS Data Source Dialog
	 * 
	 * @param parent
	 *            the parent shell
	 * @param bAllowChoose
	 *            allow choosing a data source (choose or create one if set to
	 *            true)
	 */
	public CreateOpenTMSDataSource(Shell parent, final boolean bAllowChoose)
	{
		super(parent);
		message = de.folt.util.Messages
				.getInstance(
						"de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.datasourceeditor",
						userLanguage);
		shell = new Shell(parent, SWT.DIALOG_TRIM);
		shell.setLayout(new GridLayout(1, false));
		if (bAllowChoose)
		{
			shell.setText(message.getString("createopenchooseTMSDatabase"));
		}
		else
		{
			shell.setText(message.getString("createopenTMSDatabase"));
		}

		dbtypes = new OpenTMSDataSourceTypes();
		types = dbtypes.sqlDataSourceTypes();

		display = parent.getDisplay();

		Label promptdbs = new Label(shell, SWT.NONE);
		GridData layoutDataTmx = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL);
		layoutDataTmx.horizontalSpan = 1;
		promptdbs.setLayoutData(layoutDataTmx);
		promptdbs.setText(message.getString("OpenTMS_database_list")
				+ "                                                       ");

		table = new List(shell, SWT.SINGLE | SWT.READ_ONLY | SWT.V_SCROLL);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 1;
		table.setLayoutData(data);

		final Vector<String[]> tmxDatabases = de.folt.models.datamodel.DataSourceConfigurations
				.getOpenTMSDatabasesWithType();
		int size = 0;

		table.setEnabled(true);
		if (!bAllowChoose)
		{
			table.setBackground(de.folt.util.ColorTable.getInstance(
					shell.getDisplay(), "lightgray"));
		}
		if (bAllowChoose)
		{
			table.addMouseListener(new MouseListener()
			{
				public void mouseDoubleClick(MouseEvent e)
				{
					try
					{
						int selected = table.getSelectionIndex();
						if (selected >= 0)
						{
							dataSourceName = (String) table.getItem(selected);
						}
					}
					catch (Exception ex)
					{
						ex.printStackTrace();
					}
					shell.close();
				}

				@Override
				public void mouseDown(MouseEvent arg0)
				{

				}

				@Override
				public void mouseUp(MouseEvent arg0)
				{

				}
			});
		}

		Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));

		Label databaseLabel = new Label(composite, SWT.NONE);
		databaseLabel.setText(message.getString("Database_Name"));
		databaseText = new Text(composite, SWT.BORDER);
		databaseText.addModifyListener(new ModifyListener()
		{
			public void modifyText(ModifyEvent arg0)
			{
				String datasource = databaseText.getText();
				File f = new File(datasource);

				if (f.exists())
					select.setEnabled(true);
				else
				{
					select.setEnabled(false);
					if (dataSourcetype
							.getText()
							.equals(de.folt.models.datamodel.sql.OpenTMSSQLDataSource.class
									.getName()))
					{
						select.setEnabled(true);
					}

					if (dataSourcetype.getText().equals(
							de.folt.models.datamodel.db4o.DB4O.class.getName()))
					{
						select.setEnabled(true);
					}

					if (dataSourcetype
							.getText()
							.equals(de.folt.models.datamodel.googletranslate.GoogleTranslate.class
									.getName()))
					{
						select.setEnabled(true);
					}

					if (dataSourcetype
							.getText()
							.equals(de.folt.models.datamodel.microsofttranslate.MicrosoftTranslate.class
									.getName()))
					{
						select.setEnabled(true);
					}

					if (dataSourcetype.getText().equals(
							de.folt.models.datamodel.mtmoses.MTMoses.class
									.getName()))
					{
						select.setEnabled(true);
					}

					if (dataSourcetype
							.getText()
							.equals(de.folt.models.datamodel.parallelcorpus.ParallelCorpus.class
									.getName()))
					{
						encCombo.setEnabled(true);
						select.setEnabled(true);
					}

					if (dataSourcetype.getText().equals(
							de.folt.models.datamodel.csv.Csv.class.getName()))
					{
						encCombo.setEnabled(true);
						select.setEnabled(true);
					}
				}

			}
		});
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = 200;
		databaseText.setLayoutData(data);
		// need to add a browse button...
		Label browseLabel = new Label(composite, SWT.NONE);
		browseLabel.setText("");
		chooseFileName = new Button(composite, SWT.PUSH);
		chooseFileName.setText(message.getString("chooseFileName"));
		chooseFileName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		chooseFileName.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				String dataSourceGenericType = dataSourcetype.getText();
				if (dataSourceGenericType
						.equals(com.araya.OpenTMS.ArayaDataSource.class
								.getName()))
				{
					// here we must call now a dialog to choose the araya data
					// source to add
					String propertiesFile = OpenTMSProperties.getInstance()
							.getOpenTMSProperty("ArayaPropertiesFile");
					EMXProperties.setPropfileName(propertiesFile);
					String arayaDatabaseListFile = EMXProperties.getInstance()
							.getEMXProperty("database.list");
					com.araya.tm.DatabaseSelector chooser = new com.araya.tm.DatabaseSelector(
							arayaDatabaseListFile, shell);
					chooser.show(shell);
					String data = chooser.getDatabase();
					if (data != null)
					{
						databaseText.setText(data);
						select.setEnabled(true);
					}
					return;
				}

				if (dataSourceGenericType
						.equals(de.folt.models.datamodel.db4o.DB4O.class
								.getName()))
				{
					select.setEnabled(true);
				}

				if (dataSourceGenericType
						.equals(de.folt.models.datamodel.parallelcorpus.ParallelCorpus.class
								.getName()))
				{
					encCombo.setEnabled(true);
					select.setEnabled(true);
				}

				FileDialog fd = new FileDialog(shell, SWT.OPEN);
				String extensions[] = null;

				if (dataSourceGenericType
						.equals(de.folt.models.datamodel.parallelcorpus.ParallelCorpus.class
								.getName()))
				{
					extensions = new String[] { "*.csv*;*" };
				}
				else if (dataSourceGenericType
						.equals(de.folt.models.datamodel.db4o.DB4O.class
								.getName()))
				{
					extensions = new String[] { "*.yap", "*.*;*" };
				}
				else if (dataSourceGenericType
						.equals(de.folt.models.datamodel.csv.Csv.class
								.getName()))
				{
					extensions = new String[] { "*.*;*" };
				}
				else if (dataSourceGenericType
						.equals(de.folt.models.datamodel.multipledatasource.MultipleDataSource.class
								.getName()))
				{
					extensions = new String[] { "*.multds;*" };
				}
				else
				{
					extensions = new String[] {
							"*.tmx;*.xlf;*.xliff;*.txt;*.csv", "*.*" };
				}

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
					String extension = ".xlf";
					if (dataSourcetype
							.getText()
							.equals(de.folt.models.datamodel.tmxfile.TmxFileDataSource.class
									.getName()))
						extension = ".tmx";
					if (dataSourceGenericType
							.equals(de.folt.models.datamodel.db4o.DB4O.class
									.getName()))
					{
						extension = ".yap";
					}

					if (dataSourceGenericType
							.equals(de.folt.models.datamodel.parallelcorpus.ParallelCorpus.class
									.getName()))
					{
						extension = "";
					}

					if (dataSourceGenericType
							.equals(de.folt.models.datamodel.multipledatasource.MultipleDataSource.class
									.getName()))
					{
						extension = "multds";
					}

					String filename = fd.getFileName();
					if (filename.endsWith(".xlf"))
						;
					else if (filename.endsWith(".xliff"))
						;
					else if (filename.endsWith(".tmx"))
						;
					else if (filename.endsWith(".tbx"))
						;
					else if (filename.endsWith(".txt"))
						;
					else if (filename.endsWith(".yap"))
						;
					else if (filename.endsWith(".csv"))
						;
					else if (filename.endsWith(".multds"))
						;
					else
						filename = filename + extension;
					databaseText.setText(fd.getFilterPath()
							+ System.getProperty("file.separator") + filename);
					select.setEnabled(true);
				}
			}
		});

		Label encLabel = new Label(composite, SWT.NONE);
		encLabel.setText(message.getString("Code_Page"));

		encCombo = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
		encCombo.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				encoding = encCombo.getText();
			}
		});
		encCombo.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.GRAB_HORIZONTAL));
		String scodepage[] = CodePageHandling.getCodePages();
		encCombo.setItems(scodepage);

		int ipos2 = 0;
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

		encCombo.setEnabled(false);

		Label sourceLangLabel = new Label(composite, SWT.NONE);
		sourceLangLabel.setText(message.getString("SourceLanguage"));

		String slangnames[] = LanguageHandling.getCombinedLanguages();

		sourceLangCombo = new Combo(composite, SWT.DROP_DOWN);
		sourceLangCombo.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				sourceLanguage = LanguageHandling
						.getShortLanguageCodeFromCombinedTable(sourceLangCombo
								.getText());
			}
		});
		sourceLangCombo.setLayoutData(new GridData(
				GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		sourceLangCombo.setItems(slangnames);

		Label targetLangLabel = new Label(composite, SWT.NONE);
		targetLangLabel.setText(message.getString("TargetLanguage"));

		targetLangCombo = new Combo(composite, SWT.DROP_DOWN);
		targetLangCombo.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				targetLanguage = LanguageHandling
						.getShortLanguageCodeFromCombinedTable(targetLangCombo
								.getText());
			}
		});
		targetLangCombo.setLayoutData(new GridData(
				GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		targetLangCombo.setItems(slangnames);

		Label dataSourcetypeLabel = new Label(composite, SWT.NONE);
		dataSourcetypeLabel.setText(message.getString("Database_Source_Type"));
		dataSourcetype = new Combo(composite, SWT.SINGLE | SWT.BORDER);

		dataSourcetype.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		dataSourcetype.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				if (dataSourcetype.getText().equals(
						de.folt.models.datamodel.sql.OpenTMSSQLDataSource.class
								.getName()))
				{
					type.setEnabled(true);
					serverText.setEnabled(true);
					portText.setEnabled(true);
					userText.setEnabled(true);
					passText.setEnabled(true);
					chooseFileName.setEnabled(false);
					encCombo.setEnabled(false);
					syncButton.setEnabled(true);
				}
				else
				{
					type.setEnabled(false);
					serverText.setEnabled(false);
					portText.setEnabled(false);
					userText.setEnabled(false);
					passText.setEnabled(false);
					chooseFileName.setEnabled(true);
					encCombo.setEnabled(true);
					syncButton.setEnabled(false);
				}
			}
		});

		Vector<String> dataModels = de.folt.models.datamodel.DataSourceInstance
				.getKnownDataSourceModels();
		for (int i = 0; i < dataModels.size(); i++)
		{
			dataSourcetype.add(dataModels.get(i));
		}

		dataSourcetype.select(0);

		Label typeLabel = new Label(composite, SWT.NONE);
		typeLabel.setText(message.getString("Database_Type"));
		type = new Combo(composite, SWT.SINGLE | SWT.BORDER);

		if (types != null)
		{
			type.setItems(types);
			type.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			type.addSelectionListener(new SelectionAdapter()
			{
				public void widgetSelected(SelectionEvent e)
				{
					setDefaultServerPort();
				}
			});
		}

		Label serverLabel = new Label(composite, SWT.NONE);
		serverLabel.setText(message.getString("Server_Name"));
		serverText = new Text(composite, SWT.BORDER);
		serverText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		serverText.setText("localhost");

		Label portLabel = new Label(composite, SWT.NONE);
		portLabel.setText(message.getString("Port_number"));
		portText = new Text(composite, SWT.BORDER);
		portText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label userLabel = new Label(composite, SWT.NONE);
		userLabel.setText(message.getString("User_Name"));
		userText = new Text(composite, SWT.BORDER);
		userText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label passLabel = new Label(composite, SWT.NONE);
		passLabel.setText(message.getString("Password"));
		passText = new Text(composite, SWT.BORDER | SWT.PASSWORD);
		passText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Label creationUserLabel = new Label(composite, SWT.NONE);
		creationUserLabel.setText(message.getString("creationUserCreateDataSource"));
		creationUser = new Text(composite, SWT.BORDER);
		creationUser.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		creationUser.setText(System.getProperty("user.name"));
		
		Label userListLabel = new Label(composite, SWT.NONE);
		userListLabel.setText(message.getString("userListCreateDataSource"));
		userList = new Text(composite, SWT.BORDER);
		userList.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		userList.setText(System.getProperty("user.name"));

		Label syncLabel = new Label(composite, SWT.NONE);
		syncLabel.setText(message.getString("syncLabel"));
		syncButton = new Button(composite, SWT.CHECK);
		syncButton.setEnabled(false);

		if (dataSourcetype.getText().equals(
				de.folt.models.datamodel.sql.OpenTMSSQLDataSource.class
						.getName()))
			syncButton.setEnabled(true);

		Composite bottom = new Composite(shell, SWT.BORDER);
		bottom.setLayout(new GridLayout(2, true));
		bottom.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		select = new Button(bottom, SWT.PUSH);
		if (bAllowChoose)
		{
			select.setText(message.getString("createopenchooseTMSDatabase"));
		}
		else
		{
			select.setText(message.getString("createopenTMSDatabase"));
		}

		select.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		select.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				try
				{
					String dataSourceNameNew = (String) databaseText.getText();

					if (bAllowChoose && dataSourceNameNew.equals(""))
					{
						if (table.getSelectionIndex() > -1)
						{
							dataSourceName = table.getItem(table
									.getSelectionIndex());
							return;
						}
					}

					if (dataSourceNameNew.equals(""))
						return;

					Cursor hglass = new Cursor(shell.getDisplay(),
							SWT.CURSOR_WAIT);
					Cursor arrow = new Cursor(shell.getDisplay(),
							SWT.CURSOR_ARROW);
					shell.setCursor(hglass);

					dataSourceNameNew = dataSourceNameNew.replaceAll(
							" \\(.*\\)", "");
					if (tmxDatabases != null)
					{
						for (int i = 0; i < tmxDatabases.size(); i++)
						{
							if (tmxDatabases.get(i)[0]
									.equals(dataSourceNameNew))
							{
								shell.setCursor(arrow);
								MessageBox messageBox = new MessageBox(shell);
								String string = message
										.getString("DataSourceExists");
								messageBox.setText(string);
								string = message.getString("DataSourceExists")
										+ " " + dataSourceNameNew;
								messageBox.setMessage(string);
								messageBox.open();

								return;
							}
						}
					}

					String dataSourceType = type.getText();
					String dataSourceGenericType = dataSourcetype.getText();
					Hashtable<String, Object> param = new Hashtable<String, Object>();
					
					param.put("user-id", creationUser.getText());
					param.put("user-id-list", userList.getText());
					
					if (dataSourceGenericType
							.equals(de.folt.models.datamodel.tmxfile.TmxFileDataSource.class
									.getName()))
					{
						dataSourceType = "tmx";
						param.put("dataSourceType", dataSourceType);
					}
					else if (dataSourceGenericType
							.equals(de.folt.models.datamodel.xlifffile.XliffFileDataSource.class
									.getName()))
					{
						dataSourceType = "xliff";
						param.put("dataSourceType", dataSourceType);
					}
					else if (dataSourceGenericType
							.equals(de.folt.models.datamodel.tbxfile.TbxFileDataSource.class
									.getName()))
					{
						dataSourceType = "tbx";
						param.put("dataSourceType", dataSourceType);
					}
					else if (dataSourceGenericType
							.equals(de.folt.models.datamodel.trados.TradosTMDataSource.class
									.getName()))
					{
						dataSourceType = "trados";
						param.put("dataSourceType", dataSourceType);
						encoding = encCombo.getText();
						param.put("codepage", encoding);
					}
					else if (dataSourceGenericType
							.equals(de.folt.models.datamodel.multipledatasource.MultipleDataSource.class
									.getName()))
					{
						dataSourceType = "multiple";
						param.put("dataSourceType", dataSourceType);
						// here we must call now a dialog to choose all the data
						// source to add
						shell.setCursor(arrow);
						ChooseDataSourceDialog chooser = new ChooseDataSourceDialog(
								shell, true);
						chooser.show();
						Vector<String> data = chooser.getDataSources();
						if (data != null)
							param.put("dataSources", data);
						shell.setCursor(hglass);
					}
					else if (dataSourceGenericType
							.equals(com.araya.OpenTMS.ArayaDataSource.class
									.getName()))
					{
						dataSourceType = "Araya";
						param.put("dataSourceType", dataSourceType);
						// here we must call now a dialog to choose all the data
						// source to add
						shell.setCursor(arrow);
						// String propertiesFile =
						// OpenTMSProperties.getInstance().getOpenTMSProperty("ArayaPropertiesFile");
						// EMXProperties.setPropfileName(propertiesFile);
						// String arayaDatabaseListFile =
						// EMXProperties.getInstance().getEMXProperty("database.list");
						// com.araya.tm.DatabaseSelector chooser = new
						// com.araya.tm.DatabaseSelector(arayaDatabaseListFile);
						// chooser.show();
						// String data = chooser.getText();
						if (databaseText.getText() != null)
						{
							param.put("dataSourceName", databaseText.getText());
						}
						shell.setCursor(hglass);
					}
					else
					{
						@SuppressWarnings("rawtypes")
						Class dataSourceClass = null;
						try
						{
							dataSourceClass = Class
									.forName(dataSourceGenericType);

						}
						catch (Exception ex)
						{
							dataSourceClass = null;
						}

						if ((dataSourceClass != null)
								&& !dataSourceGenericType
										.equals("de.folt.models.datamodel.sql.OpenTMSSQLDataSource"))
						{
							param.put("dataModelClass", dataSourceGenericType);
							param.put("dataSourceType", dataSourceGenericType);
						}
						else
						{

							String directory = OpenTMSProperties.getInstance()
									.getOpenTMSProperty(
											"hibernateConfigurationsDirectory");
							param.put("hibernateConfigFile", directory + "/"
									+ dataSourceType);
							param.put("dataSourceType", directory + "/"
									+ dataSourceType); // MySQL
							String dataSourceServer = serverText.getText();
							String dataSourcePort = portText.getText();
							String dataSourceUser = userText.getText();
							String dataSourcePassword = passText.getText();
							param.put("dataSourceServer", dataSourceServer); // localhost
							param.put("dataSourcePort", dataSourcePort); // 2341
							param.put("dataSourceUser", dataSourceUser); // sa
							param.put("dataSourcePassword", dataSourcePassword); // my
							param.put("sync", syncButton.getSelection() + "");
							// password
						}
					}

					param.put("dataSourceName", dataSourceNameNew); // folttm
					param.put("targetLanguage", targetLanguage);
					param.put("sourceLanguage", sourceLanguage);
					param.put("codepage", encCombo.getText());

					Enumeration<String> paramenum = param.keys();
					while (paramenum.hasMoreElements())
					{
						String key = paramenum.nextElement();
						System.out.println("Key: \"" + key + "\" value: \""
								+ param.get(key) + "\"");
					}

					Vector<String> result = de.folt.rpc.connect.Interface
							.runCreateDB(param);
					if (result
							.get(0)
							.equals(de.folt.constants.OpenTMSConstants.OpenTMS_ID_FAILURE
									+ ""))
					{
						MessageBox messageBox = new MessageBox(shell);
						String string = message.getString("Error_Creating");
						messageBox.setText(string);
						string = message
								.getString("OpenTMS_database_not_created")
								+ " " + dataSourceNameNew;
						messageBox.setMessage(string);
						messageBox.open();
						dataSourceName = null;
					}
					else
					{
						MessageBox messageBox = new MessageBox(shell);
						String string = message.getString("Success_Creating");
						messageBox.setText(string);
						string = message.getString("OpenTMS_database_created")
								+ " " + dataSourceNameNew;
						messageBox.setMessage(string);
						messageBox.open();
						dataSourceName = dataSourceNameNew;
					}
					shell.setCursor(arrow);
					shell.close();
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
					shell.close();
				}
			}
		});

		if (types == null)
			select.setEnabled(false);

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

		if ((types != null) && types.length > 0)
		{
			type.select(0);
			setDefaultServerPort();
		}

		int iMaxShow = 15;

		if (tmxDatabases != null)
		{
			size = tmxDatabases.size();
			iMaxShow = Math.min(size, 15);
			if (size > 0)
			{
				for (int i = 0; i < iMaxShow; i++)
				{
					String name = tmxDatabases.get(i)[0];
					String type = tmxDatabases.get(i)[1];
					table.add(name + " (" + type + ")");
				}
			}
		}

		shell.pack();

		if (tmxDatabases != null)
		{
			size = tmxDatabases.size();
			if (size > 0)
			{
				for (int i = iMaxShow; i < size; i++)
				{
					String name = tmxDatabases.get(i)[0];
					String type = tmxDatabases.get(i)[1];
					table.add(name + " (" + type + ")");
				}
			}
		}
	}

	/**
	 * @return the dataSourceName
	 */
	public String getDataSourceName()
	{
		if (dataSourceName != null)
			dataSourceName = dataSourceName.replaceAll(" .*?\\(.*", "");
		return dataSourceName;
	}

	/**
	 * @param dataSourceName
	 *            the dataSourceName to set
	 */
	public void setDataSourceName(String dataSourceName)
	{
		this.dataSourceName = dataSourceName;
	}

	private void setDefaultServerPort()
	{
		try
		{
			int index = type.getSelectionIndex();
			if (index == -1)
			{
				return;
			}

			String port = dbtypes.getPort(types[index]);
			if (port == null)
				port = "";
			portText.setText(port);
			String server = dbtypes.getServer(types[index]);
			if (server == null)
				server = "";
			serverText.setText(server);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		chooseFileName.setEnabled(false);
	}

	/**
	 * show
	 */
	public void show()
	{
		shell.open();
		shell.forceActive();
		while (!shell.isDisposed())
		{
			if (display.readAndDispatch())
			{
				display.sleep();
			}
		}
	}
}
