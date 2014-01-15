/*
 * Created on 04.02.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.models.datamodel.tmxfile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.jdom.Element;

import de.folt.constants.OpenTMSConstants;
import de.folt.fuzzy.FuzzyNode;
import de.folt.fuzzy.FuzzyNodeSearchResult;
import de.folt.fuzzy.MonoLingualFuzzyNode;
import de.folt.fuzzy.MonoLingualPartitionedFuzzyNodeTree;
import de.folt.models.datamodel.BasicDataSource;
import de.folt.models.datamodel.DataSource;
import de.folt.models.datamodel.DataSourceConfigurations;
import de.folt.models.datamodel.DataSourceInstance;
import de.folt.models.datamodel.DataSourceProperties;
import de.folt.models.datamodel.LinguisticProperty;
import de.folt.models.datamodel.MonoLingualObject;
import de.folt.models.datamodel.MultiLingualObject;
import de.folt.models.documentmodel.tmx.TmxDocument;
import de.folt.models.documentmodel.xliff.XliffDocument;
import de.folt.util.OpenTMSException;
import de.folt.util.Timer;

/**
 * This class implements a data source based on a TMX file.
 * 
 * @author klemens
 * 
 */
public class TmxFileDataSource extends BasicDataSource
{

	/**
	 * main
	 * 
	 * @param args
	 * <br>
	 *            -test - run some test functions <br>
	 *            -create <datasourcename> create a tmx data source; creates a
	 *            data source tmx file if it does not exist <br>
	 *            -delete <datasourcename> deletes a tmx data source; delete the
	 *            data source tmx file
	 */
	public static void main(String[] args)
	{
		if (args.length <= 0)
		{
			return;
		}
		if (args[0].equalsIgnoreCase(("-test")))
		{
			test(args);
			return;
		}
		if (args.length <= 2)
		{
			DataSourceProperties model = new DataSourceProperties();
			model.put("dataModelClass", "de.folt.models.datamodel.tmxfile.TmxFileDataSource");
			model.put("tmxfile", args[1]);
			if (args[0].equalsIgnoreCase(("-create")))
			{
				try
				{
					DataSource datasource = DataSourceInstance.createInstance("TMX:" + args[1], model);
					datasource.createDataSource(model);
					return;
				}
				catch (OpenTMSException e)
				{
					e.printStackTrace();
					return;
				}
			}
			if (args[0].equalsIgnoreCase(("-delete")))
			{
				try
				{
					DataSource datasource = DataSourceInstance.createInstance("TMX:" + args[1], model);
					datasource.deleteDataSource(model);
					return;
				}
				catch (OpenTMSException e)
				{
					e.printStackTrace();
					return;
				}
			}
		}
	}

	/**
	 * test simple test method for generating DataModelInstances
	 */
	public static void test(String[] args)
	{
		try
		{
			String tmxfile = args[1];
			DataSourceProperties model = new DataSourceProperties();
			model.put("dataModelClass", "de.folt.models.datamodel.tmxfile.TmxFileDataSource");
			model.put("tmxfile", tmxfile);
			// model.put("dataModelUrl", "openTMS.jar");
			System.out.println(model.toString());
			System.out.println("createInstance" + " TMX:" + tmxfile);
			DataSource datasource = DataSourceInstance.createInstance("TMX:" + tmxfile, model);
			System.out.println("createInstance" + " TMX:" + tmxfile + " getLastErrorCode=" + datasource.getLastErrorCode() + " >>> " + datasource);
			TmxFileDataSource tmxdatasource = (TmxFileDataSource) datasource;
			System.out.println("Number of fuzzy nodes: " + tmxdatasource.fuzzyTree.countNodes());
			System.out.println(tmxdatasource.fuzzyTree.format());

			datasource = DataSourceInstance.getInstance("TMX:" + tmxfile);
			System.out.println("getInstance" + " TMX:" + tmxfile + " >>> " + datasource);
			System.out.println("Instances: ");
			String[] inst = DataSourceInstance.getAllDataSourceInstanceNames();
			for (int i = 0; i < inst.length; i++)
			{
				System.out.println(i + ": " + inst[i]);
			}
			DataSourceInstance.removeInstance("TMX:" + tmxfile);
			// testing not existing instance ...
			DataSourceInstance.removeInstance("bla:" + tmxfile);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private TmxDocument doc = null;

	private Integer multiID = 1;

	/**
     * 
     */
	public TmxFileDataSource()
	{
		super();
	}

	/**
	 * @param dataModelProperties
	 *            the data model parameters<br>
	 *            Main Key:
	 *            dataModelProperties.getDataSourceProperty("tmxfile"); - the
	 *            tmx file to use (alternatively "tmxFile" or "dataSourceName"
	 *            can be used too)
	 */
	public TmxFileDataSource(DataSourceProperties dataModelProperties)
	{
		this.dataSourceProperties = dataModelProperties;
		String tmxFile = (String) dataModelProperties.getDataSourceProperty("tmxfile");
		if (tmxFile == null)
			tmxFile = (String) dataModelProperties.getDataSourceProperty("tmxFile");
		if (tmxFile == null)
			tmxFile = (String) dataModelProperties.getDataSourceProperty("dataSourceName");
		if (tmxFile != null)
		{
			dataModelProperties.put("dataSourceName", tmxFile);
			dataModelProperties.put("tmxFile", tmxFile);
			dataModelProperties.put("datasource", tmxFile);
			dataModelProperties.put("datasourcename", tmxFile);
		}
		if (tmxFile == null)
		{
			this.setLastErrorCode(OpenTMSConstants.OpenTMS_TMXDATASOURCE_FILE_NULL_ERROR);
			return;
		}
		fuzzyTree = new MonoLingualPartitionedFuzzyNodeTree();
		this.multiLingualObjectCache.addObserver(new BasicDataSource.BasicDataSourceObserver());
		File f = new File(tmxFile);
		if (!f.exists())
		{
			this.setLastErrorCode(OpenTMSConstants.OpenTMS_TMXDATASOURCE_FILE_NOTFOUND_ERROR);
			return;
		}
		Timer timer = new Timer();
		timer.startTimer();
		doc = new TmxDocument();
		// load the xml file
		doc.loadXmlFile(f);

		timer.stopTimer();
		System.out.println(timer.timerString("TMX file load " + tmxFile + ": Version " + doc.getTmxVersion()));
		timer = new Timer();
		timer.startTimer();
		loadTMXEntries();
		timer.stopTimer();
		this.dataSourceProperties = dataModelProperties;
		System.out.println(timer.timerString("TMX file read entries " + tmxFile + ": Version " + doc.getTmxVersion()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.folt.models.datamodel.BasicDataSource#addMultiLingualObject(de.folt
	 * .models.datamodel.MultiLingualObject, boolean)
	 */
	@Override
	public boolean addMultiLingualObject(MultiLingualObject multiLingualObject, boolean mergeObjects)
	{
		multiLingualObject.setId(this.multiID++);

		// tu must be added to body ...
		Element body = doc.getTmxBody();
		String multistring = multiLingualObject.mapToTu();
		try
		{
			Element multielem = doc.buildElement(multistring);
			body.addContent(multielem);
		}
		catch (OpenTMSException e)
		{
			e.printStackTrace();
		}
		return super.addMultiLingualObject(multiLingualObject, mergeObjects);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.BasicDataSource#bPersist()
	 */
	@Override
	public boolean bPersist()
	{
		return doc.saveToXmlFile();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.DataSource#cleanDataSource()
	 */
	@Override
	public void cleanDataSource()
	{
		doc = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.BasicDataSource#clearDataSource()
	 */
	@Override
	public boolean clearDataSource() throws OpenTMSException
	{
		return super.clearDataSource();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.folt.models.datamodel.BasicDataSource#createDataSource(de.folt.models
	 * .datamodel.DataSourceProperties)
	 */
	@Override
	public boolean createDataSource(DataSourceProperties dataModelProperties) throws OpenTMSException
	{
		if (this.dataSourceProperties == null)
			this.dataSourceProperties = dataModelProperties;
		String tmxFile = (String) dataModelProperties.get("tmxfile");
		if (tmxFile == null)
		{
			if (tmxFile == null)
				tmxFile = (String) dataModelProperties.getDataSourceProperty("tmxFile");
			tmxFile = (String) dataModelProperties.get("datasourcename");
			if (tmxFile == null)
			{
				tmxFile = (String) dataModelProperties.get("datasource");
			}
			if (tmxFile == null)
			{
				tmxFile = (String) dataModelProperties.get("dataSourceName");
			}
			if (tmxFile == null)
			{
				return false;
			}
		}

		dataModelProperties.put("dataSourceName", tmxFile);
		dataModelProperties.put("tmxFile", tmxFile);
		dataModelProperties.put("datasource", tmxFile);
		dataModelProperties.put("datasourcename", tmxFile);
		File f = new File(tmxFile);
		if (!f.exists())
		{
			FileOutputStream write = null;
			OutputStreamWriter writer = null;
			// create the new file;
			try
			{
				write = new FileOutputStream(tmxFile);
				writer = new OutputStreamWriter(write, "UTF-8");
				writer.write(tmxHeader(0));
				writer.write(tmxFooter());
				writer.close();
				write.close();
				f = new File(tmxFile);
			}
			catch (Exception ex)
			{
				try
				{
					if (writer != null)
						writer.close();
					if (write != null)
						write.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}

				ex.printStackTrace();
				f = new File(tmxFile);
				if (f.exists())
				{
					f.delete();
				}
				return false;
			}
		}

		Timer timer = new Timer();
		timer.startTimer();
		doc = new TmxDocument();
		// load the xml file
		doc.loadXmlFile(f);

		timer.stopTimer();
		System.out.println(timer.timerString("TMX file load " + tmxFile + ": Version " + doc.getTmxVersion()));
		timer = new Timer();
		timer.startTimer();
		loadTMXEntries();
		timer.stopTimer();
		System.out.println(timer.timerString("TMX file read entries " + tmxFile + ": Version " + doc.getTmxVersion()));

		String dataSourceConfigurationsFile = (String) dataModelProperties.get("dataSourceConfigurationsFile");
		BasicDataSource sqldatasource = new BasicDataSource();
		File fhd = new File(dataSourceConfigurationsFile);
		if (!fhd.exists())
		{
			File fx = new File(sqldatasource.getDefaultDataSourceConfigurationsFileName());
			if (!fx.exists())
			{
				return false;
			}

			dataSourceConfigurationsFile = sqldatasource.getDefaultDataSourceConfigurationsFileName();
		}
		dataSourceProperties.put("dataSourceConfigurationsFile", dataSourceConfigurationsFile);
		DataSourceConfigurations config = new DataSourceConfigurations(dataSourceConfigurationsFile);
		if (config.bDataSourceExistsInConfiguration(tmxFile))
			return true;
		DataSourceProperties props = new DataSourceProperties();
		props.put("dataSourceConfigurationsFile", dataSourceConfigurationsFile);
		props.put("tmxfile", tmxFile);
		// props.put("datasourcetype",
		// "de.folt.models.datamodel.tmxfile.TmxFileDataSource");

		config.addConfiguration(tmxFile, this.getDataSourceType(), props);
		config.saveToXmlFile();

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.folt.models.datamodel.BasicDataSource#deleteDataSource(de.folt.models
	 * .datamodel.DataSourceProperties)
	 */
	@Override
	public boolean deleteDataSource(DataSourceProperties dataModelProperties) throws OpenTMSException
	{
		String tmxFile = (String) dataModelProperties.get("tmxfile");
		if (tmxFile == null)
		{
			if (tmxFile == null)
				tmxFile = (String) dataModelProperties.getDataSourceProperty("tmxFile");
			tmxFile = (String) dataModelProperties.get("dataSourceName");
			if (tmxFile == null)
			{
				tmxFile = (String) dataModelProperties.get("dataSource");
			}
			if (tmxFile == null)
			{
				System.out.println("tmxFile not specified");
				return false;
			}
		}

		File f = new File(tmxFile);
		if (f.exists())
		{
			if (f.delete() == false)
			{
				System.out.println("File " + tmxFile + " could not be deleted");
				return false;
			}
			String configFile = this.getDefaultDataSourceConfigurationsFileName();
			f = new File(configFile);
			if (!f.exists())
			{
				DataSourceConfigurations.createDataSourceConfiguration(configFile);
				return false;
			}
			dataSourceProperties.put("dataSourceConfigurationsFile", configFile);
			DataSourceConfigurations config = new DataSourceConfigurations(configFile);
			if (!config.bDataSourceExistsInConfiguration(tmxFile))
			{
				System.out.println("File " + tmxFile + " not found in " + configFile);
				return false;
			}

			config.removeConfiguration(tmxFile);
			config.saveToXmlFile();
			return true;
		}
		System.out.println("File " + tmxFile + " does not exist");
		String configFile = this.getDefaultDataSourceConfigurationsFileName();
		f = new File(configFile);
		if (!f.exists())
		{
			DataSourceConfigurations.createDataSourceConfiguration(configFile);
			return false;
		}
		dataSourceProperties.put("dataSourceConfigurationsFile", configFile);
		DataSourceConfigurations config = new DataSourceConfigurations(configFile);
		config.removeConfiguration(tmxFile);
		config.saveToXmlFile();
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.folt.models.datamodel.BasicDataSource#exportTmxFile(java.lang.String)
	 */
	@Override
	public int exportTmxFile(String tmxFile)
	{
		FileOutputStream write = null;
		OutputStreamWriter writer = null;
		int iCurrentNumber = 0;
		int iNumber = 0;
		try
		{
			write = new FileOutputStream(tmxFile);
			writer = new OutputStreamWriter(write, "UTF-8");
			Timer timer = new Timer();
			timer.startTimer();

			// List<SQLMultiLingualObject> newresults = query.list();
			writer.write(tmxHeader(this.multiLingualObjectCache.size()));
			Vector<Integer> posVec = new Vector<Integer>();
			posVec.add((Integer) 0);
			posVec.add((Integer) 1);

			this.initEnumeration();
			while (this.hasMoreElements())
			{
				MultiLingualObject multi = this.nextElement();
				String multiString = multi.mapToTu();
				writer.write(multiString);

				posVec.set(0, (Integer) iCurrentNumber);
				posVec.set(1, (Integer) iNumber);
				this.setChanged();
				this.notifyObservers(posVec);
				iCurrentNumber++;
			}
			writer.write(tmxFooter());
			writer.close();
			write.close();
			timer.stopTimer();
			System.out.println(timer.timerString("exportTmxFile " + tmxFile + " >> " + iCurrentNumber));
			return iCurrentNumber;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			try
			{
				if (writer != null)
				{
					writer.write(tmxFooter());
					writer.close();
				}
				if (write != null)
					write.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			System.out.println("Error when " + tmxFile + " exporting at # " + iCurrentNumber + " of " + iNumber);
			return de.folt.constants.OpenTMSConstants.OpenTMS_TMX_EXPORT_ERROR;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.folt.models.datamodel.BasicDataSource#exportXliffFile(java.lang.String
	 * )
	 */
	@Override
	public int exportXliffFile(String xliffFile)
	{
		return super.exportXliffFile(xliffFile);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.BasicDataSource#getDataSourceType()
	 */
	@Override
	public String getDataSourceType()
	{
		return de.folt.models.datamodel.tmxfile.TmxFileDataSource.class.getName();
	}

	/**
	 * returns the basic fuzzy tree
	 * 
	 * @return the fuzzyTree
	 */
	public MonoLingualPartitionedFuzzyNodeTree getFuzzyTree()
	{
		return fuzzyTree;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.folt.models.datamodel.BasicDataSource#getMultiLingualObjectFromUniqueId
	 * (java.lang.String)
	 */
	@Override
	public MultiLingualObject getMultiLingualObjectFromUniqueId(String id)
	{
		return super.getMultiLingualObjectFromUniqueId(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.BasicDataSource#getUniqueIds()
	 */
	@Override
	public Vector<String> getUniqueIds()
	{
		return super.getUniqueIds();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.BasicDataSource#hasMoreElements()
	 */
	@Override
	public boolean hasMoreElements()
	{
		return super.hasMoreElements();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.BasicDataSource#initEnumeration()
	 */
	@Override
	public void initEnumeration()
	{
		// multiEnum = multiLingualObjectCache.elements();
		super.initEnumeration();
	}

	/**
	 * loadTMXEntries loads the tmx entries into main memory
	 * 
	 * @return the number of TUVs read
	 */
	private int loadTMXEntries()
	{
		int iDocTuListLength = doc.getTuList().size();
		System.out.println("TMX file #tuvs = " + iDocTuListLength);
		// add an observer if something changed

		for (int i = 0; i < iDocTuListLength; i++)
		{
			MultiLingualObject multi = doc.tuToMultiLingualObject(doc.getTuList().get(i));
			multi.setId(multiID++);
			super.addMultiLingualObject(multi, false); // addMultiLingualObject(multi,
			// false);
		}

		lastReadDate = java.lang.System.currentTimeMillis();

		return iDocTuListLength;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.BasicDataSource#nextElement()
	 */
	@Override
	public MultiLingualObject nextElement()
	{
		return super.nextElement();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.BasicDataSource#removeDataSource()
	 */
	@Override
	public void removeDataSource()
	{
		super.removeDataSource();
		this.bPersist();
		doc = null;
		langMonoLingualHashtable = null;
		fuzzyTree = null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.folt.models.datamodel.BasicDataSource#removeMonoLingualObject(de.folt
	 * .models.datamodel.MonoLingualObject)
	 */
	@Override
	public boolean removeMonoLingualObject(MonoLingualObject monoLingualObject)
	{
		LinguisticProperty lingtu = monoLingualObject.getObjectLinguisticProperty("tuv");
		Element tu = (Element) lingtu.getValue();
		if (tu == null)
			return false;
		List<Element> tuvs = doc.getTuList();
		for (int i = 0; i < tuvs.size(); i++)
		{
			Element tuv = tuvs.get(i);
			boolean bFound = tuv.removeContent(tu);
			if (bFound)
				return super.removeMonoLingualObject(monoLingualObject);
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.folt.models.datamodel.BasicDataSource#removeMultiLingualObject(de.
	 * folt.models.datamodel.MultiLingualObject)
	 */
	@Override
	public boolean removeMultiLingualObject(MultiLingualObject multiLingualObject)
	{
		LinguisticProperty lingtu = multiLingualObject.getObjectLinguisticProperty("tu");
		Element tu = (Element) lingtu.getValue();
		if (tu == null)
			return false;
		boolean bFound = doc.getTmxBody().removeContent(tu);
		if (bFound)
			return super.removeMultiLingualObject(multiLingualObject);
		else
			return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.folt.models.datamodel.BasicDataSource#saveModifiedMonoLingualObject
	 * (de.folt.models.datamodel.MonoLingualObject)
	 */
	@Override
	public boolean saveModifiedMonoLingualObject(MonoLingualObject monoLingualObject)
	{
		try
		{
			LinguisticProperty lingtuv = monoLingualObject.getObjectLinguisticProperty("tuv");
			Element tuv = (Element) lingtuv.getValue();
			if (tuv == null)
				return false;
			tuv.removeChildren("seg");
			Element seg;
			try
			{
				seg = doc.buildElement("<seg>" + monoLingualObject.getFormattedSegment() + "</seg>");
			}
			catch (OpenTMSException e)
			{
				e.printStackTrace();
				return false;
			}
			tuv.addContent(seg);
			// now save the LinguisticProperties

			doc.saveModifiedLinguisticProperties(monoLingualObject);

			MonoLingualFuzzyNode fuzzyNodeToAdd = new MonoLingualFuzzyNode(monoLingualObject);
			fuzzyTree.insertFuzzyNode(fuzzyNodeToAdd);
			return true; // super.saveModifiedMonoLingualObject(monoLingualObject);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.folt.models.datamodel.BasicDataSource#saveModifiedMultiLingualObject
	 * (de.folt.models.datamodel.MultiLingualObject)
	 */
	@Override
	public boolean saveModifiedMultiLingualObject(MultiLingualObject multiLingualObject)
	{
		try
		{
			LinguisticProperty lingtuv = multiLingualObject.getObjectLinguisticProperty("tu");
			Element tuv = (Element) lingtuv.getValue();
			if (tuv == null)
				return false;

			// now save the LinguisticProperties

			doc.saveModifiedLinguisticProperties(multiLingualObject);

			return true; // super.saveModifiedMonoLingualObject(monoLingualObject);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.BasicDataSource#setDataSourceType()
	 */
	@Override
	public void setDataSourceType()
	{
		this.dataSourceType = TmxFileDataSource.class.getName();
	}

	/**
	 * tmxFooter
	 * 
	 * @return
	 */
	private String tmxFooter()
	{
		return "</body>\n</tmx>";
	}

	/**
	 * tmxHeader
	 * 
	 * @return
	 */
	private String tmxHeader(int iNumberEntries)
	{
		String tmx = "";
		tmx = tmx + "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"; //$NON-NLS-1$
		tmx = tmx + "<tmx version=\"1.4\">\n"; //$NON-NLS-1$
		tmx = tmx
				+ "\t<header creationtool=\"" + this.getClass().getName() + "\" creationtoolversion=\"" //$NON-NLS-1$
				+ de.folt.constants.OpenTMSVersionConstants.de_folt_models_datamodel_tmxfile_TmxFileDataSource
				+ "\" adminlang=\"en-us\" srclang=\"en-us\" datatype=\"xml\" o-tmf=\"" + dataSourceProperties.getDataSourceProperty("tmxfile") + "\" segtype=\"paragraph\">\n"; //$NON-NLS-1$
		tmx = tmx + "\t\t<prop type=\"entrynumber\">" + iNumberEntries + "</prop>\n";
		tmx = tmx + "\t\t<prop type=\"creationDate\">" + de.folt.util.OpenTMSSupportFunctions.getDateString() + "</prop>\n";
		tmx = tmx + "\t</header>\n";
		tmx = tmx + "\t<body>\n"; //$NON-NLS-1$
		return tmx;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.BasicDataSource#translate(org.jdom.Element,
	 * java.lang.String, java.lang.String, int)
	 */
	@Override
	public Element translate(Element transUnit, Element file, XliffDocument xliffDocument, String sourceLanguage, String targetLanguage,
			int matchSimilarity, Hashtable<String, Object> translationParameters) throws OpenTMSException
	{
		if (translationParameters == null)
		{
			String approved = transUnit.getAttributeValue("approved");
			if ((approved != null) && approved.equals("yes"))
				return transUnit;
		}
		else
		{
			if (translationParameters.containsKey("ignoreApproveAttribute")
					&& ((String) (translationParameters.get("ignoreApproveAttribute"))).equals("yes"))
			{
				;
			}
			else
			{
				String approved = transUnit.getAttributeValue("approved");
				if ((approved != null) && approved.equals("yes"))
					return transUnit;
			}

		}

		Element source = transUnit.getChild("source", xliffDocument.getNamespace());
		String segment = xliffDocument.elementContentToString(source);
		if (translationParameters == null)
			translationParameters = new Hashtable<String, Object>();
		translationParameters.put("translate.SourceFormattedSegment", segment);
		// now run a fuzzy search
		@SuppressWarnings("rawtypes")
		Class[] classes = new Class[2];
		classes[0] = String.class;
		classes[1] = Object.class;
		Method method = null;
		try
		{
			method = MonoLingualObject.class.getMethod("simpleComputePlainText", classes);
		}
		catch (Exception ex)
		{
			throw new OpenTMSException("translate", "simpleComputePlainText", OpenTMSConstants.OpenTMS_TRANSLATE_PLAINTEXTMETHOD_NOTFOUND_ERROR,
					(Object) this, ex);
		}

		MonoLingualObject mono = new MonoLingualObject(segment, sourceLanguage, MonoLingualObject.class, method, null);
		MonoLingualFuzzyNode fuzzyCompareKey = new MonoLingualFuzzyNode(mono);
		Vector<FuzzyNodeSearchResult<String, MonoLingualObject>> fuzzyresult = fuzzyTree.search(fuzzyCompareKey, matchSimilarity);

		if (fuzzyresult == null)
			return transUnit;

		// System.out.println("# Fuzzy Node search results = " +
		// fuzzyresult.size());
		int iFLenght = fuzzyresult.size();

		for (int i = 0; i < iFLenght; i++)
		{
			FuzzyNodeSearchResult<String, MonoLingualObject> fzresult = fuzzyresult.get(i);
			FuzzyNode<String, MonoLingualObject> fuzzyNode = fzresult.getFuzzyNode();
			Vector<MonoLingualObject> monos = fuzzyNode.getValues();
			for (int j = 0; j < monos.size(); j++)
			{
				MonoLingualObject sourceMono = monos.get(j);
				MultiLingualObject parentMulti = sourceMono.getParentMultiLingualObject();
				if (parentMulti == null)
					continue;
				Vector<MonoLingualObject> targetmonos = parentMulti.getMonoLingualObjectsAsVector(targetLanguage);
				if ((targetmonos == null) || (targetmonos.size() == 0))
					continue;
				// we must check the Levenshtein similarity now
				if (fzresult.getLevenDistance()[j] < matchSimilarity)
					continue;
				Element alttrans = xliffDocument.addAltTrans(transUnit, sourceMono, targetmonos, (int) (fzresult.getLevenDistance()[j]),
						translationParameters);
				if ((alttrans != null) && (dataSourceProperties != null))
					alttrans.setAttribute("origin", (String) dataSourceProperties.get("dataSourceName"));
			}
		}
		
		translationParameters.put(":file", file);
		translationParameters.put(":xliffDocument", xliffDocument);
		translationParameters.put(":sourceLanguage", sourceLanguage);
		translationParameters.put(":targetLanguage", targetLanguage);
		translationParameters.put(":matchSimilarity", (Integer)matchSimilarity);
		translationParameters.put(":dataSource", this);
		translationParameters.put(":instanceOpenTMSProperties", de.folt.util.OpenTMSProperties.getInstance());
		transUnit = runFilterMethod(translationParameters, transUnit);
		return transUnit;
	}

}
