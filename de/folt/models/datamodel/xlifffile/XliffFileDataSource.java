/*
 * Created on 15.02.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.models.datamodel.xlifffile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.JDOMParseException;

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
import de.folt.models.datamodel.tmxfile.TmxFileDataSource;
import de.folt.models.documentmodel.xliff.XliffDocument;
import de.folt.rpc.webserver.OpenTMSServer;
import de.folt.util.OpenTMSException;
import de.folt.util.Timer;

/**
 * This class uses an XLIFF File as a data source. Only the approved source and
 * target elements of the trans-unit are used (read into the fuzzy index). <br>
 * Special Features: <br>
 * loadAllTargets - if true; loads all targets independently if approved or not<br>
 * loadAltTrans - if true; loads all alt-trans found in trans-unit<br>
 * 
 * @author klemens
 * 
 */
public class XliffFileDataSource extends BasicDataSource
{

	/**
	 * main
	 * 
	 * @param args
	 * <br>
	 *            -test - run some test functions <br>
	 *            -create <datasourcename> create a xliff data source; creates a
	 *            data source xliff file if it does not exist <br>
	 *            -delete <datasourcename> deletes a xliff data source; delete
	 *            the data source xliff file
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
			model.put("dataModelClass", "de.folt.models.datamodel.tmxfile.XliffFileDataSource");
			model.put("xlifffile", args[1]);
			if (args[0].equalsIgnoreCase(("-create")))
			{
				try
				{
					DataSource datasource = DataSourceInstance.createInstance("XLIFF:" + args[1], model);
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
					DataSource datasource = DataSourceInstance.createInstance("XLIFF:" + args[1], model);
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
			String tmxfile = args[0];
			DataSourceProperties model = new DataSourceProperties();
			model.put("dataModelClass", "de.folt.models.datamodel.xlifffile.XliffFileDataSource");
			model.put("tmxfile", tmxfile);
			// model.put("dataModelUrl", "openTMS.jar");
			System.out.println(model.toString());
			System.out.println("createInstance" + " XLIFF:" + tmxfile);
			DataSource datasource = DataSourceInstance.createInstance("TMX:" + tmxfile, model);
			System.out.println("createInstance" + " XLIFF:" + tmxfile + " getLastErrorCode=" + datasource.getLastErrorCode() + " >>> " + datasource);
			XliffFileDataSource tmxdatasource = (XliffFileDataSource) datasource;
			System.out.println("Number of fuzzy nodes: " + tmxdatasource.fuzzyTree.countNodes());
			System.out.println(tmxdatasource.fuzzyTree.format());

			datasource = DataSourceInstance.getInstance("XLIFF:" + tmxfile);
			System.out.println("getInstance" + " XLIFF:" + tmxfile + " >>> " + datasource);
			System.out.println("Instances: ");
			String[] inst = DataSourceInstance.getAllDataSourceInstanceNames();
			for (int i = 0; i < inst.length; i++)
			{
				System.out.println(i + ": " + inst[i]);
			}
			DataSourceInstance.removeInstance("XLIFF:" + tmxfile);
			// testing not existing instance ...
			DataSourceInstance.removeInstance("bla:" + tmxfile);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private boolean bLoadAllTargets = false;

	private boolean bLoadAltTrans = false;

	private XliffDocument doc = null;

	private MonoLingualPartitionedFuzzyNodeTree fuzzyTree = null;

	private Integer multiID = 1;

	/**
     * 
     */
	public XliffFileDataSource()
	{
		super();
	}

	/**
	 * @param dataModelProperties
	 *            the data model parameters<br>
	 *            Main Key:
	 *            dataModelProperties.getDataSourceProperty("xlifffile")
	 *            (equivalently one can use "xliffFile" or "dataSourceName" or
	 *            "dataSource") - the xliff file to use
	 * @throws JDOMParseException
	 */
	public XliffFileDataSource(DataSourceProperties dataModelProperties)
	{
		this.dataSourceProperties = dataModelProperties;
		String xliffFile = (String) dataModelProperties.getDataSourceProperty("xlifffile");
		if (xliffFile == null)
		{
			xliffFile = (String) dataModelProperties.get("xliffFile");
			if (xliffFile == null)
			{
				xliffFile = (String) dataModelProperties.get("dataSource");
			}
			xliffFile = (String) dataModelProperties.get("dataSourceName");
			if (xliffFile == null)
			{
				xliffFile = (String) dataModelProperties.get("dataSource");
			}
			if (xliffFile == null)
			{
				this.setLastErrorCode(OpenTMSConstants.OpenTMS_TMXDATASOURCE_FILE_NOTFOUND_ERROR);
				return;
			}
		}
		if (xliffFile != null)
		{
			dataModelProperties.put("dataSourceName", xliffFile);
			dataModelProperties.put("xlifffile", xliffFile);
			dataModelProperties.put("datasource", xliffFile);
			dataModelProperties.put("datasourcename", xliffFile);
		}
		File f = new File(xliffFile);
		if (!f.exists())
		{
			this.setLastErrorCode(OpenTMSConstants.OpenTMS_TMXDATASOURCE_FILE_NOTFOUND_ERROR);
			return;
		}

		String whichTargets = (String) dataModelProperties.getDataSourceProperty("loadAllTargets");
		if (whichTargets == null)
		{
			bLoadAllTargets = false;
		}
		else
		{
			try
			{
				bLoadAllTargets = Boolean.parseBoolean(whichTargets);
			}
			catch (Exception e)
			{
				bLoadAllTargets = false;
			}
		}

		String altTransTargets = (String) dataModelProperties.getDataSourceProperty("loadAltTrans");
		if (altTransTargets == null)
		{
			bLoadAltTrans = false;
		}
		else
		{
			try
			{
				bLoadAltTrans = Boolean.parseBoolean(altTransTargets);
			}
			catch (Exception e)
			{
				bLoadAltTrans = false;
			}
		}

		Timer timer = new Timer();
		timer.startTimer();
		doc = new XliffDocument();
		doc.loadXmlFile(f);
		fuzzyTree = new MonoLingualPartitionedFuzzyNodeTree();
		timer.stopTimer();
		System.out.println(timer.timerString("XLIFF file load " + xliffFile + ": Version " + doc.getXliffVersion()));
		timer = new Timer();
		timer.startTimer();
		int iRes = loadXliffEntries();
		timer.stopTimer();
		System.out.println(timer.timerString("XLIFF file read entries " + xliffFile + ": Version " + doc.getXliffVersion() + " / " + iRes));

		String dataSourceConfigurationsFile = (String) dataModelProperties.get("dataSourceConfigurationsFile");
		dataSourceConfigurationsFile = DataSourceConfigurations.getConfigurationFileName(dataSourceConfigurationsFile);
		if (dataSourceConfigurationsFile == null)
		{
			return;
		}

		DataSourceConfigurations config = new DataSourceConfigurations(dataSourceConfigurationsFile);
		if (config.bDataSourceExistsInConfiguration(xliffFile))
			return;
		DataSourceProperties props = new DataSourceProperties();
		props.put("dataSourceConfigurationsFile", dataSourceConfigurationsFile);
		props.put("xlifffile", xliffFile);

		config.addConfiguration(xliffFile, this.getDataSourceType(), props);
		config.saveToXmlFile();
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
		Element file = doc.getFiles().get(0);
		Element body = doc.getXliffBody(file);
		String multistring = multiLingualObject.mapToTransUnit();
		try
		{
			Element multielem = doc.buildElement(multistring);
			body.addContent(multielem);
		}
		catch (OpenTMSException e)
		{
			// TODO Auto-generated catch block
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
		// TODO Auto-generated method stub
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
		String xlifffile = (String) dataModelProperties.get("xlifffile");
		if (xlifffile == null)
		{
			xlifffile = (String) dataModelProperties.get("xliffFile");
			if (xlifffile == null)
			{
				xlifffile = (String) dataModelProperties.get("dataSource");
			}
			xlifffile = (String) dataModelProperties.get("dataSourceName");
			if (xlifffile == null)
			{
				xlifffile = (String) dataModelProperties.get("dataSource");
			}
			if (xlifffile == null)
			{
				return false;
			}
		}

		dataModelProperties.put("dataSourceName", xlifffile);
		dataModelProperties.put("xlifffile", xlifffile);
		dataModelProperties.put("datasource", xlifffile);
		dataModelProperties.put("datasourcename", xlifffile);

		File f = new File(xlifffile);
		if (!f.exists())
		{
			// create the new file;
			try
			{
				FileOutputStream write = new FileOutputStream(xlifffile);
				OutputStreamWriter writer = new OutputStreamWriter(write, "UTF-8");
				String sL = (String) dataModelProperties.get("sourceLanguage");
				if ((sL == null) || (sL.equals("")))
					sL = "de";
				String tL = (String) dataModelProperties.get("targetLanguage");
				if ((tL == null) || (tL.equals("")))
					tL = "en";
				writer.write(xliffHeader(0, xlifffile, sL, tL));
				writer.write(xliffFooter());
				writer.close();
				write.close();
				f = new File(xlifffile);
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
				return false;
			}
		}

		Timer timer = new Timer();
		timer.startTimer();
		doc = new XliffDocument();
		// load the xml file
		try
		{
			doc.loadXmlFile(f);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		fuzzyTree = new MonoLingualPartitionedFuzzyNodeTree();
		timer.stopTimer();
		System.out.println(timer.timerString("XLIFF file load " + xlifffile + ": Version " + doc.getXliffVersion()));
		timer = new Timer();
		timer.startTimer();
		loadXliffEntries();
		timer.stopTimer();
		System.out.println(timer.timerString("XLIFF file read entries " + xlifffile + ": Version " + doc.getXliffVersion()));

		String dataSourceConfigurationsFile = (String) dataModelProperties.get("dataSourceConfigurationsFile");
		dataSourceConfigurationsFile = DataSourceConfigurations.getConfigurationFileName(dataSourceConfigurationsFile);
		if (dataSourceConfigurationsFile == null)
		{
			return false;
		}

		dataSourceProperties.put("dataSourceConfigurationsFile", dataSourceConfigurationsFile);
		DataSourceConfigurations config = null;
		try
		{
			config = new DataSourceConfigurations(dataSourceConfigurationsFile);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (config.bDataSourceExistsInConfiguration(xlifffile))
			return true;
		DataSourceProperties props = new DataSourceProperties();
		props.put("dataSourceConfigurationsFile", dataSourceConfigurationsFile);
		props.put("xlifffile", xlifffile);
		// props.put("datasourcetype",
		// "de.folt.models.datamodel.xlifffile.XliffFileDataSource");

		config.addConfiguration(xlifffile, this.getDataSourceType(), props);
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
		String xlifffile = (String) dataModelProperties.get("xlifffile");
		if (xlifffile == null)
		{
			xlifffile = (String) dataModelProperties.get("xliffFile");
			if (xlifffile == null)
			{
				xlifffile = (String) dataModelProperties.get("datasource");
			}
			xlifffile = (String) dataModelProperties.get("dataSourceName");
			if (xlifffile == null)
			{
				xlifffile = (String) dataModelProperties.get("dataSource");
			}
			if (xlifffile == null)
			{
				return false;
			}
		}

		File f = new File(xlifffile);
		if (f.exists())
		{
			if (f.delete() == false)
			{
				System.out.println("File " + xlifffile + " could not be deleted");
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
			DataSourceConfigurations config = null;
			try
			{
				config = new DataSourceConfigurations(configFile);
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
			if (!config.bDataSourceExistsInConfiguration(xlifffile))
			{
				System.out.println("File " + xlifffile + " not found in " + configFile);
				return false;
			}

			config.removeConfiguration(xlifffile);
			config.saveToXmlFile();
			return true;

		}
		System.out.println("File " + xlifffile + " does not exist");
		String configFile = this.getDefaultDataSourceConfigurationsFileName();
		f = new File(configFile);
		if (!f.exists())
		{
			DataSourceConfigurations.createDataSourceConfiguration(configFile);
			return false;
		}
		dataSourceProperties.put("dataSourceConfigurationsFile", configFile);
		DataSourceConfigurations config = new DataSourceConfigurations(configFile);
		config.removeConfiguration(xlifffile);
		config.saveToXmlFile();
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.BasicDataSource#getDataSourceType()
	 */
	@Override
	public String getDataSourceType()
	{
		// TODO Auto-generated method stub
		return de.folt.models.datamodel.xlifffile.XliffFileDataSource.class.getName();
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
		// TODO Auto-generated method stub
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
	 * @return the bLoadAllTargets
	 */
	public boolean isBLoadAllTargets()
	{
		return bLoadAllTargets;
	}

	/**
	 * @return the bLoadAltTrans
	 */
	public boolean isBLoadAltTrans()
	{
		return bLoadAltTrans;
	}

	/**
	 * loadXliffEntries loads the xliff entries into main memory
	 * 
	 * @return the number of TUVs read
	 */
	@SuppressWarnings("unchecked")
	private int loadXliffEntries()
	{
		List<Element> filelist = doc.getFiles();
		int filelistLength = filelist.size();
		System.out.println("XLIFF file # = " + filelistLength);
		// add an observer if something changed
		this.multiLingualObjectCache.addObserver(new BasicDataSource.BasicDataSourceObserver());
		int iNumEntries = 0;
		for (int k = 0; k < filelistLength; k++)
		{
			Element file = filelist.get(k);
			List<Element> transUnits = doc.getTransUnitList(doc.getXliffBody(file));
			for (int i = 0; i < transUnits.size(); i++)
			{
				MultiLingualObject multi = doc.transUnitToMultiLingualObject(transUnits.get(i), this.bLoadAllTargets);
				if (multi != null)
				{
					iNumEntries++;
					multi.setId(multiID++);
					super.addMultiLingualObject(multi, false); // addMultiLingualObject(multi,
																// false);
				}
				if (bLoadAltTrans)
				{
					List<Element> alttrans = (List<Element>) (transUnits.get(i)).getChildren("alt-trans");
					if (alttrans == null)
						continue;
					for (int l = 0; l < alttrans.size(); l++)
					{
						multi = doc.transUnitToMultiLingualObject(alttrans.get(i), true);
						if (multi != null)
						{
							iNumEntries++;
							multi.setId(multiID++);
							super.addMultiLingualObject(multi, false); // addMultiLingualObject(multi,
																		// false);
						}
					}
				}
			}
		}

		lastReadDate = java.lang.System.currentTimeMillis();

		return iNumEntries;
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
	 * In XLIFF Documents no MOLs can be removed, only MULs. Fucntion always
	 * returns false. @see
	 * de.folt.models.datamodel.BasicDataSource#removeMonoLingualObject
	 * (de.folt.models.datamodel.MonoLingualObject)
	 */
	@Override
	public boolean removeMonoLingualObject(MonoLingualObject monoLingualObject)
	{
		// either remove source or target
		String language = monoLingualObject.getLanguage();
		MultiLingualObject multi = monoLingualObject.getParentMultiLingualObject();
		if (multi == null)
			return false;
		LinguisticProperty lingtransunit = multi.getObjectLinguisticProperty("trans-unit");
		Element transunit = (Element) lingtransunit.getValue();
		if (transunit == null)
			return false;
		Element source = transunit.getChild("source", doc.getNamespace());
		String sourcesegment = doc.elementContentToString(source);
		String elanguage = source.getAttributeValue("lang", Namespace.XML_NAMESPACE);
		if (language.equals(elanguage) && sourcesegment.equals(monoLingualObject.getFormattedSegment()))
		{
			// here we need to remove the whole trans-unit as otherwise the
			// xliff file gets invalid!
			if (this.removeMultiLingualObject(multi))
				return super.removeMultiLingualObject(multi);
			else
				return false;
		}
		else
		{
			Element target = transunit.getChild("target", doc.getNamespace());
			elanguage = target.getAttributeValue("lang", Namespace.XML_NAMESPACE);
			String targetsegment = doc.elementContentToString(target);
			if (language.equals(elanguage) && targetsegment.equals(monoLingualObject.getFormattedSegment()))
			{
				if (!transunit.removeContent(target))
					return false;
			}
		}
		return super.removeMonoLingualObject(monoLingualObject);
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
		LinguisticProperty lingtransunit = multiLingualObject.getObjectLinguisticProperty("trans-unit");
		Element transunit = (Element) lingtransunit.getValue();
		if (transunit == null)
			return false;
		Element file0 = doc.getFiles().get(0);
		Element body = file0.getChild("body", doc.getNamespace());
		if (body.removeContent(transunit))
			return super.removeMultiLingualObject(multiLingualObject);
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
			String language = monoLingualObject.getLanguage();
			MultiLingualObject multi = monoLingualObject.getParentMultiLingualObject();
			if (multi == null)
				return false;
			Element newContent = null;
			LinguisticProperty lingtransunit = multi.getObjectLinguisticProperty("trans-unit");
			Element transunit = (Element) lingtransunit.getValue();

			LinguisticProperty sourceProp = multi.getObjectLinguisticProperty("trans-unit-source");
			Element source = (Element) sourceProp.getValue();
			String elanguage = source.getAttributeValue("lang", Namespace.XML_NAMESPACE);
			if (language.equals(elanguage))
			{
				try
				{
					newContent = doc.buildElement("<source xml:lang=\"" + language + "\">" + monoLingualObject.getFormattedSegment() + "</source>");
				}
				catch (OpenTMSException e)
				{
					e.printStackTrace();
					return false;
				}
				transunit.removeChild("source", doc.getNamespace());
				transunit.addContent(newContent);
				MonoLingualFuzzyNode fuzzyNodeToAdd = new MonoLingualFuzzyNode(monoLingualObject);
				fuzzyTree.insertFuzzyNode(fuzzyNodeToAdd);
				return true;
			}

			LinguisticProperty targetProp = multi.getObjectLinguisticProperty("trans-unit-target");
			Element target = (Element) targetProp.getValue();
			elanguage = target.getAttributeValue("lang", Namespace.XML_NAMESPACE);
			if (language.equals(elanguage))
			{
				try
				{
					newContent = doc.buildElement("<target xml:lang=\"" + language + "\">" + monoLingualObject.getFormattedSegment() + "</target>");
				}
				catch (OpenTMSException e)
				{
					e.printStackTrace();
					return false;
				}
				transunit.removeChild("target", doc.getNamespace());
				transunit.addContent(newContent);
				MonoLingualFuzzyNode fuzzyNodeToAdd = new MonoLingualFuzzyNode(monoLingualObject);
				fuzzyTree.insertFuzzyNode(fuzzyNodeToAdd);
				return true;
			}

			return true;
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
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

		Element source = transUnit.getChild("source", doc.getNamespace());
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

	/**
	 * tmxFooter
	 * 
	 * @return
	 */
	private String xliffFooter()
	{
		return "</body>\n</file>\n</xliff>";
	}

	/**
	 * tmxHeader
	 * 
	 * @return
	 */
	private String xliffHeader(int iNumberEntries, String filename, String sourcelanguage, String targetLanguage)
	{
		String tmx = "";
		tmx = tmx + "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"; //$NON-NLS-1$
		tmx = tmx + "<xliff version=\"1.0\">\n"; //$NON-NLS-1$
		tmx = tmx + "<file datatype=\"XML\" original=\"" + filename + "\" source-language=\"" + sourcelanguage + "\" target-language=\""
				+ targetLanguage + "\">\n";
		tmx = tmx + "\t<header>\n"; //$NON-NLS-1$
		tmx = tmx + "\t\t<phase company-name=\"Open TMS\" date=\"" + de.folt.util.OpenTMSSupportFunctions.getDateString()
				+ "\" phase-name=\"1\" process-name=\"pre-process\" tool=\"" + this.getClass().getName() + " " + OpenTMSServer.getVersion()
				+ "\" />\n";
		tmx = tmx + "\t</header>\n";
		tmx = tmx + "\t<body>\n"; //$NON-NLS-1$
		return tmx;
	}
}
