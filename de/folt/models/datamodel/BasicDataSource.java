/*
 * Created on 04.02.2009
 * 
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.models.datamodel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.Vector;

import org.jdom.Element;

import de.folt.constants.OpenTMSConstants;
import de.folt.fuzzy.FuzzyNode;
import de.folt.fuzzy.FuzzyNodeSearchResult;
import de.folt.fuzzy.MonoLingualFuzzyNode;
import de.folt.fuzzy.MonoLingualPartitionedFuzzyNodeTree;
import de.folt.models.datamodel.TranslationCheckResult.TranslationCheckStatus;
import de.folt.models.datamodel.phrasetranslate.PhraseTranslate;
import de.folt.models.datamodel.phrasetranslate.PhraseTranslateResult;
import de.folt.models.datamodel.tbxfile.TbxFileDataSource;
import de.folt.models.datamodel.tmxfile.TmxFileDataSource;
import de.folt.models.datamodel.xlifffile.XliffFileDataSource;
import de.folt.models.documentmodel.document.XMLPrettyPrint;
import de.folt.models.documentmodel.tmx.TmxProp;
import de.folt.models.documentmodel.xliff.XliffDocument;
import de.folt.util.ObservableHashtable;
import de.folt.util.OpenTMSException;
import de.folt.util.OpenTMSProperties;
import de.folt.util.OpenTMSSupportFunctions;

/**
 * This class implements a basic data source (implements the DataSource
 * interface) and is intended to be sub classed for specific data sources like
 * TMX file data source or similar. See also the subclass {@see
 * de.folt.models.datamodel.ExtendedBasicSource}
 * 
 * @author klemens
 * 
 */
public class BasicDataSource extends Observable implements DataSource
{

	/**
	 * Class implements a simple Observer for a basic data source
	 * 
	 * @author klemens
	 * 
	 */
	public class BasicDataSourceObserver implements Observer
	{

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Observer#update(java.util.Observable,
		 * java.lang.Object)
		 */
		public void update(Observable arg0, Object arg1)
		{
			;
		}

	}

	public static void main(String[] args)
	{
		@SuppressWarnings("unused")
		String dataSourceNameTest = "C:\\eclipse\\workspace\\beosphereRecommender\\test\\beorectest\\beorec.tmx";
		try
		{
			DataSource dataSource = DataSourceInstance.createInstance(args[0]);
			dataSource.getAllAttributes(args[1]);
		}
		catch (OpenTMSException e)
		{
			e.printStackTrace();
			return;
		}
	}

	/**
	 * setOpenTMSPropertiesFile set the name of the OpenTMS properties file
	 * 
	 * @param propFile
	 *            the name of the property file
	 */
	public static void setOpenTMSPropertiesFile(String propFile)
	{
		de.folt.util.OpenTMSProperties.setPropfileName(propFile);
	}

	/**
	 * bChanged determines if the data sources has changed - added or removed
	 * entries
	 */
	protected boolean													bChanged;

	/**
	 * bLoadAttributesLazy determines if the attributes of the MULs and MOLs
	 * should be loaded on read time or just when they are really needed
	 */
	protected boolean													bLoadAttributesLazy						= false;

	protected DataSourceConfigurations									dataSourceConfigurations				= null;

	protected String													dataSourceConfigurationsFile;

	/**
	 * dataSourceProperties contains the properties attached to a data source
	 * like file names, ports, server names etc.
	 */
	protected DataSourceProperties										dataSourceProperties					= null;

	protected String													dataSourceType							= "";

	private Hashtable<String, Object>									dataTable								= new Hashtable<String, Object>();

	protected String													defaultDataSourceConfigurationsFileName	= "OpenTMSDataSource.config.xml";

	protected MonoLingualPartitionedFuzzyNodeTree						fuzzyTree								= null;

	protected int														iLogLevel								= 0;

	protected Hashtable<String, Hashtable<String, MonoLingualObject>>	langMonoLingualHashtable				= new Hashtable<String, Hashtable<String, MonoLingualObject>>();

	/**
	 * lastErrorCode contains the last error code produced by a method
	 */
	protected int														lastErrorCode							= 0;

	protected long														lastReadDate							= -1l;

	private Enumeration<MultiLingualObject>								multiEnum								= null;

	/**
	 * multiLingualObjectCache is a cache object which can be used by the data
	 * sources to store read MultiLingual Objects. The key used is the unique id
	 * of the object.
	 */
	protected ObservableHashtable<String, MultiLingualObject>	multiLingualObjectCache	= new ObservableHashtable<String, MultiLingualObject>();

	protected Hashtable<String, PhraseTranslate>				phraseTranslateVector	= new Hashtable<String, PhraseTranslate>();

	/**
	 * Standard Constructor for basic data source
	 */
	public BasicDataSource()
	{
		super();
		de.folt.util.OpenTMSProperties.getInstance(); // just init the openTMS
		// Properties file
		setILogLevel();
	}

	/**
	 * Creates a basic data source based on the supplied dataSourceProperties.
	 * The properties file is determined from the key value pair propertiesFile
	 * of dataSourceProperties.
	 * 
	 * @param dataSourceProperties
	 *            the dataSourceProperties to use for the construction
	 */
	public BasicDataSource(DataSourceProperties dataSourceProperties)
	{
		String openTMSPropertiesFile = (String) dataSourceProperties.get("propertiesFile");
		if ((openTMSPropertiesFile == null) || openTMSPropertiesFile.equals(""))
			openTMSPropertiesFile = de.folt.util.OpenTMSProperties.getPropfileName();
		de.folt.util.OpenTMSProperties.setPropfileName(openTMSPropertiesFile);
		de.folt.util.OpenTMSProperties.getInstance(openTMSPropertiesFile);
		Observer observer = (Observer) dataSourceProperties.get("observer");
		if (observer != null)
			this.addObserver(observer);
		this.dataSourceProperties = dataSourceProperties;
		this.fuzzyTree = new MonoLingualPartitionedFuzzyNodeTree();
	}

	/**
	 * @param propertiesFileName
	 */
	public BasicDataSource(String propertiesFileName)
	{
		super();
		de.folt.util.OpenTMSProperties.getInstance(propertiesFileName); // just
		// init the openTMS Properties file
		setILogLevel();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.DataSource#addData(java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public void addData(String key, Object object)
	{
		if (dataTable == null)
			dataTable = new Hashtable<String, Object>();
		dataTable.put(key, object);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.folt.models.datamodel.DataSource#addMonoLingualObject(de.folt.models
	 * .datamodel.MonoLingualObject, boolean)
	 */
	@Override
	public boolean addMonoLingualObject(MonoLingualObject monoLingualObject, boolean mergeObjects)
	{
		MultiLingualObject multi = monoLingualObject.getParentMultiLingualObject();
		multiLingualObjectCache.put(multi.getId() + "", multi);
		if (langMonoLingualHashtable.containsKey(monoLingualObject.getLanguage()))
		{
			Hashtable<String, MonoLingualObject> hashMono = langMonoLingualHashtable.get(monoLingualObject
					.getLanguage());
			hashMono.put(monoLingualObject.getUniqueID(), monoLingualObject);
		}
		else
		{
			Hashtable<String, MonoLingualObject> hashMono = new Hashtable<String, MonoLingualObject>();
			langMonoLingualHashtable.put(monoLingualObject.getLanguage(), hashMono);
			hashMono.put(monoLingualObject.getUniqueID(), monoLingualObject);
		}
		if (fuzzyTree != null)
		{
			MonoLingualFuzzyNode fuzzyNodeToAdd = new MonoLingualFuzzyNode(monoLingualObject);
			fuzzyTree.insertFuzzyNode(fuzzyNodeToAdd);
		}
		bChanged = true;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.folt.models.datamodel.DataSource#addMultiLingualObject(de.folt.models
	 * .datamodel.MultiLingualObject, boolean)
	 */
	@Override
	public boolean addMultiLingualObject(MultiLingualObject multiLingualObject, boolean mergeObjects)
	{
		this.multiLingualObjectCache.put(multiLingualObject.getId() + "", multiLingualObject);
		Vector<MonoLingualObject> monos = multiLingualObject.getMonoLingualObjectsAsVector();
		for (int j = 0; j < monos.size(); j++)
		{
			MonoLingualObject mono = monos.get(j);
			if (langMonoLingualHashtable.containsKey(mono.getLanguage()))
			{
				Hashtable<String, MonoLingualObject> hashMono = langMonoLingualHashtable.get(mono.getLanguage());
				hashMono.put(mono.getUniqueID(), mono);
			}
			else
			{
				Hashtable<String, MonoLingualObject> hashMono = new Hashtable<String, MonoLingualObject>();
				langMonoLingualHashtable.put(mono.getLanguage(), hashMono);
				hashMono.put(mono.getUniqueID(), mono);
			}
			if (fuzzyTree != null)
			{
				MonoLingualFuzzyNode fuzzyNodeToAdd = new MonoLingualFuzzyNode(mono);
				fuzzyTree.insertFuzzyNode(fuzzyNodeToAdd);
			}
		}

		bChanged = true;
		return true;
	}

	protected void addProps(LinguisticProperties lingprops, Hashtable<String, String> attributes)
	{
		Set<String> setlings = lingprops.keySet();
		Iterator<String> lings = setlings.iterator();
		while (lings.hasNext())
		{
			String key = lings.next();
			Object value = (Object) lingprops.get(key);
			String outval = "";
			if (value.getClass() == LinguisticProperty.class)
			{
				LinguisticProperty x = (LinguisticProperty) value;
				if (x.value.getClass() == TmxProp.class)
				{
					TmxProp y = (TmxProp) x.value;
					outval = y.getContent();
					key = y.getType();
				}
			}
			else if (value.getClass() == String.class)
			{
				outval = (String) value;
			}
			else
			{
				outval = value.toString();
			}
			if ((outval != null) && !outval.equals(""))
			{
				if (attributes.containsKey(key))
				{
					String currentValue = attributes.get(key);
					if (currentValue.indexOf(outval) == 0)
						continue;
					if (currentValue.indexOf(outval + ";") == 0)
						continue;
					if (currentValue.indexOf(";" + outval) == -1)
					{
						outval = currentValue + ";" + outval;
						attributes.put(key, outval);
					}
				}
				else
					attributes.put(key, outval);
			}
		}

	}

	protected void addProps(String key, String outval, Hashtable<String, String> attributes)
	{
		if ((outval != null) && !outval.equals(""))
		{
			if (attributes.containsKey(key))
			{
				String currentValue = attributes.get(key);
				if (currentValue.indexOf(outval) == 0)
					return;
				if (currentValue.indexOf(outval + ";") == 0)
					return;
				if (currentValue.indexOf(";" + outval) == -1)
				{
					outval = currentValue + ";" + outval;
					attributes.put(key, outval);
				}
			}
			else
				attributes.put(key, outval);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.DataSource#bAuthenticate(java.lang.String,
	 * java.lang.String)
	 * 
	 * @return true as default value; sub classes should implement specific
	 * methods
	 */
	@Override
	public boolean bAuthenticate(String userName, String password)
	{
		// per default we grant access
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.DataSource#bPersist()
	 */
	@Override
	public boolean bPersist()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.DataSource#bSupportMultiThreading()
	 */
	@Override
	public boolean bSupportMultiThreading()
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.DataSource#changedMonolingualObjects()
	 */
	@Override
	public Vector<MonoLingualObject> changedMonolingualObjects()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.folt.models.datamodel.DataSource#checkSourceTargetMOL(de.folt.models
	 * .datamodel.MonoLingualObject, de.folt.models.datamodel.MonoLingualObject)
	 */
	@Override
	public TranslationCheckResult checkIfTranslationExistsInDataSource(MonoLingualObject source,
			MonoLingualObject target)
	{
		TranslationCheckResult translationCheckResult = new TranslationCheckResult();

		Hashtable<String, Object> searchParametersSource = new Hashtable<String, Object>();
		Hashtable<String, Object> searchParametersTarget = new Hashtable<String, Object>();

		if (source.getLinguisticProperties().size() > 0)
			searchParametersSource.put("matchMonoLingualLinguisticProperties", "==");
		if (target.getLinguisticProperties().size() > 0)
			searchParametersTarget.put("matchMonoLingualLinguisticProperties", "==");

		Vector<MonoLingualObject> sourceResults = this.search(source, searchParametersSource);
		Vector<MonoLingualObject> targetResults = this.search(target, searchParametersTarget);

		if ((sourceResults.size() == 0) && (targetResults.size() == 0))
		{
			translationCheckResult.setStatus(TranslationCheckStatus.NEW);
		}
		else if ((sourceResults.size() != 0) && (targetResults.size() != 0))
		{
			translationCheckResult.setStatus(TranslationCheckStatus.SOURCEANDTARGETFOUND);
		}
		else if ((sourceResults.size() != 0))
		{
			translationCheckResult.setStatus(TranslationCheckStatus.SOURCEFOUND);
		}
		else if ((targetResults.size() != 0))
		{
			translationCheckResult.setStatus(TranslationCheckStatus.TARGETFOUND);
		}

		for (int i = 0; i < sourceResults.size(); i++)
		{
			for (int j = 0; j < targetResults.size(); j++)
			{
				if (sourceResults.get(i).getParentMultiLingualObject()
						.equals(targetResults.get(j).getParentMultiLingualObject()))
				{
					translationCheckResult.getSourceAndTargetSegmentMatches().add(
							sourceResults.get(i).getParentMultiLingualObject());
					targetResults.remove(targetResults.get(j));
					continue;
				}
			}
			translationCheckResult.getSourceSegmentMatches().add(sourceResults.get(i).getParentMultiLingualObject());
		}

		for (int i = 0; i < targetResults.size(); i++)
		{
			translationCheckResult.getTargetSegmentMatches().add(targetResults.get(i).getParentMultiLingualObject());
		}

		return translationCheckResult;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.folt.models.datamodel.DataSource#checkIfTranslationExistsInDataSource
	 * (java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public TranslationCheckResult checkIfTranslationExistsInDataSource(String sourceSegment, String sourceLanguage,
			String targetSegment, String targetLanguage)
	{

		TranslationCheckResult translationCheckResult = new TranslationCheckResult();
		translationCheckResult.setStatus(TranslationCheckResult.TranslationCheckStatus.NEW);

		MonoLingualObject sourceMono = new MonoLingualObject();
		sourceMono.setLanguage(sourceLanguage);
		sourceMono.setFormattedSegment(sourceSegment);
		Hashtable<String, Object> searchParameters = new Hashtable<String, Object>();
		Vector<MonoLingualObject> sourceResults = this.search(sourceMono, searchParameters);

		MonoLingualObject targetMono = new MonoLingualObject();
		targetMono.setLanguage(targetLanguage);
		targetMono.setFormattedSegment(targetSegment);
		Vector<MonoLingualObject> targetResults = this.search(targetMono, searchParameters);

		if ((sourceResults != null) && (targetResults != null))
		{
			translationCheckResult.setStatus(TranslationCheckResult.TranslationCheckStatus.NEW);
			return translationCheckResult;
		}

		try
		{
			if ((sourceResults.size() > 0) && (targetResults.size() > 0))
				translationCheckResult.setStatus(TranslationCheckResult.TranslationCheckStatus.SOURCEANDTARGETFOUND);
			else if (sourceResults.size() > 0)
				translationCheckResult.setStatus(TranslationCheckResult.TranslationCheckStatus.SOURCEFOUND);
			else if (targetResults.size() > 0)
				translationCheckResult.setStatus(TranslationCheckResult.TranslationCheckStatus.TARGETFOUND);

			for (int i = 0; i < sourceResults.size(); i++)
			{
				for (int j = 0; j < targetResults.size(); j++)
				{
					if (sourceResults.get(i).getParentMultiLingualObject()
							.equals(targetResults.get(j).getParentMultiLingualObject()))
					{
						translationCheckResult.getSourceAndTargetSegmentMatches().add(
								sourceResults.get(i).getParentMultiLingualObject());
						targetResults.remove(targetResults.get(j));
						continue;
					}
				}
				translationCheckResult.getSourceSegmentMatches()
						.add(sourceResults.get(i).getParentMultiLingualObject());
			}

			for (int i = 0; i < targetResults.size(); i++)
			{
				translationCheckResult.getTargetSegmentMatches()
						.add(targetResults.get(i).getParentMultiLingualObject());
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return translationCheckResult;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.DataSource#cleanDataSource()
	 */
	@Override
	public void cleanDataSource()
	{

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.DataSource#clearDataSource()
	 */
	@Override
	public boolean clearDataSource() throws OpenTMSException
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.DataSource#containsKey(java.lang.String)
	 */
	@Override
	public boolean containsKey(String key)
	{
		// TODO Auto-generated method stub
		if (dataTable != null)
		{
			return dataTable.containsKey(key);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.DataSource#containsObject(java.lang.Object)
	 */
	@Override
	public boolean containsValue(Object object)
	{
		// TODO Auto-generated method stub
		if (dataTable != null)
		{
			return dataTable.containsValue(object);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.folt.models.datamodel.DataSource#copyFrom(de.folt.models.datamodel
	 * .DataSource)
	 */
	@Override
	public int copyFrom(DataSource dataSource)
	{
		dataSource.initEnumeration();
		int i = 0;
		while (dataSource.hasMoreElements())
		{
			MultiLingualObject multi = dataSource.nextElement();
			addMultiLingualObject(multi, true);
			// System.out.println(multi.format());
			i++;
		}
		bPersist();
		return i;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seede.folt.models.datamodel.DataSource#copyTo(de.folt.models.datamodel.
	 * DataSource)
	 */
	@Override
	public int copyTo(DataSource dataSource)
	{
		initEnumeration();
		int i = 0;
		while (this.hasMoreElements())
		{
			MultiLingualObject multi = this.nextElement();
			dataSource.addMultiLingualObject(multi, true);
			// System.out.println(multi.format());
			i++;
		}
		dataSource.bPersist();
		return i;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.folt.models.datamodel.DataSource#createDataSource(de.folt.models.datamodel
	 * .DataSourceProperties)
	 */
	@Override
	public boolean createDataSource(DataSourceProperties dataModelProperties) throws OpenTMSException
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.DataSource#getCurrentTime()
	 */
	@Override
	public long currentTimeMillis()
	{
		return System.currentTimeMillis();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.folt.models.datamodel.DataSource#deleteDataSource(de.folt.models.datamodel
	 * .DataSourceProperties)
	 */
	@Override
	public boolean deleteDataSource(DataSourceProperties dataModelProperties) throws OpenTMSException
	{
		String filename = (String) dataModelProperties.get("dataSourceName");
		if (filename == null)
			filename = (String) dataSourceProperties.get("dataSourceName");
		String dataSourceConfigurationFile = (String) dataModelProperties.get("dataSourceConfigurationsFile");
		File fhd = new File(dataSourceConfigurationFile);
		if (!fhd.exists())
		{
			dataSourceConfigurationFile = this.getDefaultDataSourceConfigurationsFileName();
			File f = new File(dataSourceConfigurationFile);
			if (!f.exists())
			{
				DataSourceConfigurations.createDataSourceConfiguration(dataSourceConfigurationFile);
				return false;
			}
		}

		dataSourceProperties.put("dataSourceConfigurationsFile", dataSourceConfigurationFile);
		DataSourceConfigurations config = new DataSourceConfigurations(dataSourceConfigurationFile);
		dataSourceConfigurations = config;
		if (!config.bDataSourceExistsInConfiguration(filename))
		{
			System.out.println("File " + filename + " not found in " + dataSourceConfigurationFile);
			return false;
		}
		if (!config.removeConfiguration(filename))
			return false;
		if (!config.saveToXmlFile())
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.DataSource#exportTmxFile(java.lang.String)
	 */
	@Override
	public int exportTmxFile(String tmxFile)
	{
		DataSourceProperties dataSourceProperties = new DataSourceProperties();
		dataSourceProperties.put("tmxfile", tmxFile);
		TmxFileDataSource tmxdatasource = new TmxFileDataSource(dataSourceProperties);
		try
		{
			tmxdatasource.createDataSource(dataSourceProperties);
		}
		catch (OpenTMSException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
		this.copyTo(tmxdatasource);
		return tmxdatasource.exportTmxFile(tmxFile);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.folt.models.datamodel.DataSource#exportXliffFile(java.lang.String)
	 */
	@Override
	public int exportXliffFile(String xliffFile)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.folt.models.datamodel.DataSource#getAllAttributes(java.lang.String)
	 */
	public void getAllAttributes(String outputfile)
	{
		Hashtable<String, String> attributes = new Hashtable<String, String>();
		this.initEnumeration();
		while (this.hasMoreElements())
		{
			MultiLingualObject multi = this.nextElement();

			LinguisticProperties lingprops = multi.getLinguisticProperties();
			addProps(lingprops, attributes);
			Vector<MonoLingualObject> monos = multi.getMonoLingualObjectsAsVector();
			for (int i = 0; i < monos.size(); i++)
			{
				MonoLingualObject mono = monos.get(i);
				addProps("xml:lang", mono.getLanguage(), attributes);
				lingprops = mono.getLinguisticProperties();
				addProps(lingprops, attributes);
			}
		}

		FileOutputStream write = null;
		OutputStreamWriter writer = null;
		// create the new file;
		try
		{
			write = new FileOutputStream(outputfile);
			writer = new OutputStreamWriter(write, "UTF-8");

			Enumeration<String> enumhash = attributes.keys();
			while (enumhash.hasMoreElements())
			{
				String key = enumhash.nextElement();
				String value = (String) attributes.get(key);
				writer.write(key + "=" + value + "\n");
			}

			writer.close();
			write.close();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.DataSource#getChangedIds()
	 */
	@Override
	public Vector<Integer> getChangedIds()
	{
		lastReadDate = java.lang.System.currentTimeMillis();
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.DataSource#getData(java.lang.String)
	 */
	@Override
	public Object getData(String key)
	{
		if (dataTable != null)
			return dataTable.get(key);
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.DataSource#getDataSourceConfigurations()
	 */
	public DataSourceConfigurations getDataSourceConfigurations()
	{
		return dataSourceConfigurations;
	}

	public String getDataSourceConfigurationsFile()
	{
		return dataSourceConfigurationsFile;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.DataSource#getDataSourceName()
	 */
	@Override
	public String getDataSourceName()
	{
		String name = (String) dataSourceProperties.get("dataSourceName");
		if (name != null)
			return name;
		name = (String) dataSourceProperties.get("datasource");
		if (name != null)
			return name;
		name = (String) dataSourceProperties.get("datasourcename");
		if (name != null)
			return name;
		return this.getClass().getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.DataSource#getDataSourceProperties()
	 */
	@Override
	public DataSourceProperties getDataSourceProperties() throws OpenTMSException
	{
		return dataSourceProperties;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.DataSource#getDataSourceType()
	 */
	@Override
	public String getDataSourceType()
	{
		return this.dataSourceType;
	}

	/**
	 * @return the defaultDataSourceConfigurationsFileName; if the default file
	 *         does not exist it is created
	 */
	public String getDefaultDataSourceConfigurationsFileName()
	{
		de.folt.util.OpenTMSProperties inst = de.folt.util.OpenTMSProperties.getInstance();
		String name = "";
		if (inst != null)
			name = inst.getOpenTMSProperty("dataSourceConfigurationsFile");
		// System.out.println("getDefaultDataSourceConfigurationsFileName=" +
		// name);
		if ((name != null) && (!name.equals("")))
		{
			File f = new File(name);
			if (f.exists())
				defaultDataSourceConfigurationsFileName = name;
		}
		else
			name = "database/OpenTMSDataSource.config.xml";
		defaultDataSourceConfigurationsFileName = name;
		return defaultDataSourceConfigurationsFileName;
	}

	/**
	 * @return the fuzzyTree
	 */
	public MonoLingualPartitionedFuzzyNodeTree getFuzzyTree()
	{
		return fuzzyTree;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.DataSource#getIds()
	 */
	@Override
	public Vector<Integer> getIds()
	{
		Vector<Integer> result = new Vector<Integer>();
		Enumeration<MultiLingualObject> enumel = multiLingualObjectCache.elements();
		while (enumel.hasMoreElements())
		{
			MultiLingualObject multi = enumel.nextElement();
			result.add(multi.getId());
		}
		Collections.sort(result);
		return result;
	}

	/**
	 * @return the iLogLevel
	 */
	public int getILogLevel()
	{
		return iLogLevel;
	}

	/**
	 * getLastErrorCode Method returns the last error code for an operation done
	 * by the data source
	 * 
	 * @return an OpenTMS Error code describing the error which occurred for the
	 *         last operation
	 */
	public int getLastErrorCode()
	{
		return lastErrorCode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.folt.models.datamodel.DataSource#getMonoLingualObjectFromId(java.lang
	 * .String)
	 */
	@Override
	public MonoLingualObject getMonoLingualObjectFromId(String uniqueID)
	{
		Integer iid = 0;
		try
		{
			iid = Integer.parseInt(uniqueID);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
		Enumeration<MultiLingualObject> enumel = multiLingualObjectCache.elements();
		while (enumel.hasMoreElements())
		{
			MultiLingualObject multi = enumel.nextElement();
			Vector<MonoLingualObject> monos = multi.getMonoLingualObjectsAsVector();
			for (int i = 0; i < monos.size(); i++)
			{
				MonoLingualObject mono = monos.get(i);
				if ((mono != null) && (mono.getId() == iid))
					return mono;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.folt.models.datamodel.DataSource#getMonoLingualObjectFromUniqueId(
	 * java.lang.String)
	 */
	@Override
	public MonoLingualObject getMonoLingualObjectFromUniqueId(String uniqueID)
	{
		Enumeration<MultiLingualObject> enumel = multiLingualObjectCache.elements();
		while (enumel.hasMoreElements())
		{
			MultiLingualObject multi = enumel.nextElement();
			Vector<MonoLingualObject> monos = multi.getMonoLingualObjectsAsVector();
			for (int i = 0; i < monos.size(); i++)
			{
				MonoLingualObject mono = monos.get(i);
				if ((mono != null) && mono.getUniqueID().equals(uniqueID))
					return mono;
			}
		}
		return null;
	}

	/**
	 * multiLingualObjectCache is a cache object which can be used by the data
	 * sources to store read MultiLingual Objects. The key used is the unique id
	 * of the object.
	 * 
	 * @return the multiLingualObjectCache
	 */
	public ObservableHashtable<String, MultiLingualObject> getMultiLingualObjectCache()
	{
		return multiLingualObjectCache;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.folt.models.datamodel.DataSource#getMultiLingualObjectFromId(java.
	 * lang.String)
	 */
	@Override
	public MultiLingualObject getMultiLingualObjectFromId(String id)
	{
		return multiLingualObjectCache.get(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.folt.models.datamodel.DataSource#getMultiLingualObjectFromUniqueId
	 * (java.lang.String)
	 */
	@Override
	public MultiLingualObject getMultiLingualObjectFromUniqueId(String id)
	{
		Enumeration<MultiLingualObject> enumel = multiLingualObjectCache.elements();
		while (enumel.hasMoreElements())
		{
			MultiLingualObject multi = enumel.nextElement();
			if (multi.getStUniqueID().equals(id))
				return multi;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.DataSource#getUniqueIds()
	 */
	@Override
	public Vector<String> getUniqueIds()
	{
		Vector<String> result = new Vector<String>();
		Enumeration<MultiLingualObject> enumel = multiLingualObjectCache.elements();
		while (enumel.hasMoreElements())
		{
			MultiLingualObject multi = enumel.nextElement();
			result.add(multi.getStUniqueID());
		}
		Collections.sort(result);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Enumeration#hasMoreElements()
	 */
	@Override
	public boolean hasMoreElements()
	{
		if (multiEnum != null)
			return multiEnum.hasMoreElements();
		else
			return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.DataSource#importTbxFile(java.lang.String)
	 */
	@Override
	public int importTbxFile(String tbxFile)
	{
		DataSourceProperties dataSourceProperties = new DataSourceProperties();
		dataSourceProperties.put("tbxfile", tbxFile);
		TbxFileDataSource tbxdatasource = new TbxFileDataSource(dataSourceProperties);
		return this.copyFrom(tbxdatasource);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.DataSource#importTmxFile(java.lang.String)
	 */
	@Override
	public int importTmxFile(String tmxFile)
	{
		DataSourceProperties dataSourceProperties = new DataSourceProperties();
		dataSourceProperties.put("tmxfile", tmxFile);
		TmxFileDataSource tmxdatasource = new TmxFileDataSource(dataSourceProperties);
		return this.copyFrom(tmxdatasource);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.folt.models.datamodel.DataSource#importXliffFile(java.lang.String)
	 */
	@Override
	public int importXliffFile(String xliffFile)
	{
		DataSourceProperties dataSourceProperties = new DataSourceProperties();
		dataSourceProperties.put("xliffFile", xliffFile);
		XliffFileDataSource tmxdatasource = new XliffFileDataSource(dataSourceProperties);
		return this.copyFrom(tmxdatasource);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.DataSource#initEnumeration()
	 */
	@Override
	public void initEnumeration()
	{
		if (multiLingualObjectCache != null)
			multiEnum = multiLingualObjectCache.elements();
		else
			multiEnum = null;
	}

	/**
	 * @return the bChanged determines if the data sources has changed - added
	 *         or removed entries
	 */
	public boolean isBChanged()
	{
		return bChanged;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.DataSource#isBLoadAttributesLazy()
	 */
	@Override
	public boolean isBLoadAttributesLazy()
	{
		return bLoadAttributesLazy;
	}

	@Override
	public boolean isSyncDataSource()
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Enumeration#nextElement()
	 */
	@Override
	public MultiLingualObject nextElement()
	{
		if (multiEnum != null)
			return multiEnum.nextElement();
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.DataSource#removeData(java.lang.String)
	 */
	@Override
	public Object removeData(String key)
	{
		if (dataTable != null)
		{
			Object obj = dataTable.get(key);
			dataTable.remove(key);
			return obj;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.DataSource#removeDataSource()
	 */
	@Override
	public void removeDataSource()
	{
		dataSourceProperties = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.folt.models.datamodel.DataSource#removeMonoLingualObject(de.folt.models
	 * .datamodel.MonoLingualObject)
	 */
	@Override
	public boolean removeMonoLingualObject(MonoLingualObject monoLingualObject)
	{
		MultiLingualObject multi = monoLingualObject.getParentMultiLingualObject();
		if (multi == null)
			return false;
		bChanged = true;
		return multi.removeMonoLingualObject(monoLingualObject);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.folt.models.datamodel.DataSource#removeMultiLingualObject(de.folt.
	 * models.datamodel.MultiLingualObject)
	 */
	@Override
	public boolean removeMultiLingualObject(MultiLingualObject multiLingualObject)
	{
		if (multiLingualObject == null)
			return false;
		multiLingualObject.removeMonoLingualObjects();
		this.multiLingualObjectCache.remove(multiLingualObject.getId());
		bChanged = true;
		return true;
	}

	/**
	 * Run a filter on the transUnit results
	 * 
	 * @param translationParameters
	 * @param transUnit
	 * @return the modified transUnit
	 */
	public Element runFilterMethod(Hashtable<String, Object> translationParameters, Element transUnit)
	{
		// 25.04.2012: the new filter Functionality; sorting for the reults of
		// transunits
		if (translationParameters.containsKey(":filter"))
		{
			Filter openTMSFilter = (Filter) translationParameters.get(":filter");
			transUnit = openTMSFilter.run(transUnit, (Element) translationParameters.get(":file"),
					(XliffDocument) translationParameters.get(":xliffDocument"),
					(String) translationParameters.get(":sourceLanguage"),
					(String) translationParameters.get(":targetLanguage"),
					(int) (Integer) translationParameters.get(":matchSimilarity"), translationParameters,
					(DataSource) translationParameters.get(":dataSource"),
					(OpenTMSProperties) translationParameters.get(":instanceOpenTMSProperties"));
		}
		return transUnit;
	}

	/**
	 * Save a MonolingualObject in a tmx file as a deleted MonoLingualObject
	 * (this means adding the property action = deleted")
	 * 
	 * @param monoLingualObject
	 *            the monolingual object to save as a delete monolingual object
	 * @return true if saved
	 */
	protected boolean saveDeletedMonoLingualObject(MonoLingualObject monoLingualObject)
	{
		// here we could save the delete entry to a tmx file for later reusage
		try
		{
			monoLingualObject.addStringLinguisticProperty("action", "delete");
			monoLingualObject.addStringLinguisticProperty("deletetime", System.currentTimeMillis() + "");
			String deletedMonoLingualObject = monoLingualObject.mapToTuv();
			String multicoreproperties = " tui=\"1\"";
			if (monoLingualObject.getParentMultiLingualObject() != null)
			{
				// multId =
				// monoLingualObject.getParentMultiLingualObject().getStUniqueID();
				multicoreproperties = monoLingualObject.getParentMultiLingualObject().mapToTuWithoutTUV();
			}
			else
			{
				multicoreproperties = "<tu tuid=\"1\"></tu>";
			}

			multicoreproperties = multicoreproperties.replaceAll("</tu>", deletedMonoLingualObject + "\t\t</tu>");
			deletedMonoLingualObject = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<tmx version=\"1.4\">\n\t<header>\n\t\t<prop type=\"datasource\">"
					+ this.getDataSourceName()
					+ "</prop>\n\t</header>\n\t<body>\n\t"
					+ multicoreproperties
					+ "\t</body>\n</tmx>";

			deletedMonoLingualObject = XMLPrettyPrint.formatXml(deletedMonoLingualObject);
			String filename = de.folt.util.OpenTMSProperties.getInstance().getOpenTMSProperty("OpenTMS.dir")
					+ "/database/deleted/" + monoLingualObject.getStUniqueID() + "." + this.getDataSourceName() + "."
					+ System.currentTimeMillis() + ".tmx";
			OpenTMSSupportFunctions.simpleCopyStringToFile(deletedMonoLingualObject, filename);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		// end saving the object
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.folt.models.datamodel.DataSource#saveModifiedMonoLingualObject(de.
	 * folt.models.datamodel.MonoLingualObject)
	 */
	@Override
	public boolean saveModifiedMonoLingualObject(MonoLingualObject monoLingualObject)
	{
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.folt.models.datamodel.DataSource#saveModifiedMultiLingualObject(de
	 * .folt.models.datamodel.MultiLingualObject)
	 */
	@Override
	public boolean saveModifiedMultiLingualObject(MultiLingualObject mul)
	{
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seede.folt.models.datamodel.DataSource#search(de.folt.models.datamodel.
	 * MonoLingualObject)
	 */
	@Override
	public Vector<MonoLingualObject> search(MonoLingualObject searchMonoLingualObject,
			Hashtable<String, Object> searchParameters)
	{
		this.initEnumeration();
		Vector<MonoLingualObject> retVec = new Vector<MonoLingualObject>();
		String criteriaCondition = null;
		if (searchParameters.contains("matchMultiLingualLinguisticProperties"))
		{
			criteriaCondition = (String) searchParameters.get("matchMultiLingualLinguisticProperties");
		}

		while (this.hasMoreElements())
		{
			MultiLingualObject multi = this.nextElement();
			if (multi != null)
			{
				if (criteriaCondition != null)
				{
					if (multi.matchLinguisticProperties(criteriaCondition) == false)
						continue;
				}
				Vector<MonoLingualObject> vecmono = multi.search(searchMonoLingualObject, searchParameters);
				if (vecmono != null)
					retVec.addAll(vecmono);
			}
		}
		return retVec;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.folt.models.datamodel.DataSource#searchRegExp(de.folt.models.datamodel
	 * .MonoLingualObject, java.util.Hashtable)
	 */
	@Override
	public Vector<MonoLingualObject> searchRegExp(MonoLingualObject searchMonoLingualObject,
			Hashtable<String, Object> searchParameters)
	{
		this.initEnumeration();
		Vector<MonoLingualObject> retVec = new Vector<MonoLingualObject>();
		searchParameters.put("regexp", searchMonoLingualObject.getFormattedSegment());
		while (this.hasMoreElements())
		{
			MultiLingualObject multi = this.nextElement();
			if (multi != null)
			{
				Vector<MonoLingualObject> vecmono = multi.search(searchMonoLingualObject, searchParameters);
				if (vecmono != null)
					retVec.addAll(vecmono);
			}
		}
		return retVec;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.folt.models.datamodel.DataSource#searchWordBased(de.folt.models.datamodel
	 * .MonoLingualObject, java.util.Hashtable)
	 */
	@Override
	public Vector<MonoLingualObject> searchWordBased(MonoLingualObject searchMonoLingualObject,
			Hashtable<String, Object> searchParameters)
	{
		searchParameters.put("wordbased", "true");
		this.initEnumeration();
		Vector<MonoLingualObject> retVec = new Vector<MonoLingualObject>();

		while (this.hasMoreElements())
		{
			MultiLingualObject multi = this.nextElement();
			if (multi != null)
			{
				Vector<MonoLingualObject> vecmono = multi.search(searchMonoLingualObject, searchParameters);
				if (vecmono != null)
					retVec.addAll(vecmono);
			}
		}
		return retVec;
	}

	/**
	 * @param changed
	 *            the bChanged to set determines if the data sources has changed
	 *            - added or removed entries
	 */
	public void setBChanged(boolean changed)
	{
		bChanged = changed;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.DataSource#setBLoadAttributesLazy(boolean)
	 */
	@Override
	public void setBLoadAttributesLazy(boolean loadAttributesLazy)
	{
		bLoadAttributesLazy = loadAttributesLazy;
	}

	public void setDataSourceConfigurations(DataSourceConfigurations dataSourceConfigurations)
	{
		this.dataSourceConfigurations = dataSourceConfigurations;
	}

	public void setDataSourceConfigurationsFile(String dataSourceConfigurationsFile)
	{
		this.dataSourceConfigurationsFile = dataSourceConfigurationsFile;
	}

	@Override
	public void setDataSourceProperties(DataSourceProperties dataProps)
	{
		this.dataSourceConfigurations.setDataModelProperties(this.getDataSourceName(), dataProps);
		this.dataSourceConfigurations.saveToXmlFile();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.DataSource#setDataSourceType()
	 */
	@Override
	public void setDataSourceType()
	{
		dataSourceType = BasicDataSource.class.getName();
	}

	/**
	 * @param defaultDataSourceConfigurationsFileName
	 *            the defaultDataSourceConfigurationsFileName to set
	 */
	public void setDefaultDataSourceConfigurationsFileName(String defaultDataSourceConfigurationsFileName)
	{
		this.defaultDataSourceConfigurationsFileName = defaultDataSourceConfigurationsFileName;
	}

	/**
	 * @param logLevel
	 *            the iLogLevel - set to the value in OpenTMSProperties
	 * 
	 *            <pre>
	 * iLogLevel = de.folt.util.OpenTMSProperties.getInstance().getLogLevel(this.getClass().getCanonicalName());
	 * </pre>
	 */
	public void setILogLevel()
	{
		iLogLevel = 0;
		String classname = this.getClass().getCanonicalName();
		if (classname != null)
		{
			de.folt.util.OpenTMSProperties inst = de.folt.util.OpenTMSProperties.getInstance();
			if (inst != null)
				iLogLevel = inst.getLogLevel(classname);
		}
	}

	/**
	 * @param logLevel
	 *            the iLogLevel to set
	 */
	public void setILogLevel(int logLevel)
	{
		iLogLevel = logLevel;
		// iLogLevel =
		// de.folt.util.OpenTMSProperties.getInstance().getLogLevel(this.getClass().getCanonicalName());
	}

	/**
	 * @param lastErrorCode
	 *            the lastErrorCode to set
	 */
	public void setLastErrorCode(int lastErrorCode)
	{
		this.lastErrorCode = lastErrorCode;
	}

	/**
	 * multiLingualObjectCache is a cache object which can be used by the data
	 * sources to store read MultiLingual Objects. The key used is the unique id
	 * of the object.
	 * 
	 * @param multiLingualObjectCache
	 *            the multiLingualObjectCache to set
	 */
	public void setMultiLingualObjectCache(ObservableHashtable<String, MultiLingualObject> multiLingualObjectCache)
	{
		this.multiLingualObjectCache = multiLingualObjectCache;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.DataSource#subSegmentResultsToGlossary()
	 */
	@Override
	public Element[] subSegmentResultsToGlossary(String sourceLangauge, String targetLanguage)
	{
		if (phraseTranslateVector.containsKey(sourceLangauge + ":" + targetLanguage))
		{
			return (phraseTranslateVector.get(sourceLangauge + ":" + targetLanguage))
					.phraseTranslateResultsToGlossary();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.folt.models.datamodel.DataSource#subSegmentTranslate(org.jdom.Element,
	 * de.folt.models.documentmodel.document.XmlDocument, java.lang.String,
	 * java.lang.String, java.util.Hashtable)
	 */
	@Override
	public Element subSegmentTranslate(Element transUnit, XliffDocument xliffDocument, String sourceLanguage,
			String targetLanguage, Hashtable<String, Object> translationParameters) throws OpenTMSException
	{
		// this implements a default behavior based on PhraseTranslate
		String key = sourceLanguage + ":" + targetLanguage;
		PhraseTranslate phraseTranslate = null;
		if (phraseTranslateVector.containsKey(key))
		{
			phraseTranslate = phraseTranslateVector.get(key);
		}
		else
		{
			phraseTranslate = new PhraseTranslate(sourceLanguage, targetLanguage);
			phraseTranslate.bAddPhrases(this, sourceLanguage, targetLanguage);
			phraseTranslateVector.put(key, phraseTranslate);
		}
		String segment = transUnit.getChildText("source", xliffDocument.getNamespace());
		Vector<PhraseTranslateResult> result = phraseTranslate.findTranslation(segment);
		transUnit = phraseTranslate.addToTransUnit(transUnit, result, sourceLanguage, targetLanguage, this);

		return transUnit;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.BasicDataSource#translate(org.jdom.Element,
	 * java.lang.String, java.lang.String, int)
	 */
	@Override
	public Element translate(Element transUnit, Element file, XliffDocument xliffDocument, String sourceLanguage,
			String targetLanguage, int matchSimilarity, Hashtable<String, Object> translationParameters)
			throws OpenTMSException
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
			throw new OpenTMSException("translate", "simpleComputePlainText",
					OpenTMSConstants.OpenTMS_TRANSLATE_PLAINTEXTMETHOD_NOTFOUND_ERROR, (Object) this, ex);
		}

		MonoLingualObject mono = new MonoLingualObject(segment, sourceLanguage, MonoLingualObject.class, method, null);
		MonoLingualFuzzyNode fuzzyCompareKey = new MonoLingualFuzzyNode(mono);
		Vector<FuzzyNodeSearchResult<String, MonoLingualObject>> fuzzyresult = fuzzyTree.search(fuzzyCompareKey,
				matchSimilarity);

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
				if (targetmonos == null)
					continue;
				if (targetmonos.size() == 0)
					continue;
				// we must check the Levenshtein similarity now
				if (fzresult.getLevenDistance()[j] < matchSimilarity)
					continue;
				Element alttrans = xliffDocument.addAltTrans(transUnit, sourceMono, targetmonos,
						(int) (fzresult.getLevenDistance()[j]), translationParameters);
				if ((alttrans != null) && (dataSourceProperties != null))
					alttrans.setAttribute("origin", (String) dataSourceProperties.get("dataSourceName"));
			}
		}

		translationParameters.put(":file", file);
		translationParameters.put(":xliffDocument", xliffDocument);
		translationParameters.put(":sourceLanguage", sourceLanguage);
		translationParameters.put(":targetLanguage", targetLanguage);
		translationParameters.put(":matchSimilarity", (Integer) matchSimilarity);
		translationParameters.put(":dataSource", this);
		translationParameters.put(":instanceOpenTMSProperties", de.folt.util.OpenTMSProperties.getInstance());
		transUnit = runFilterMethod(translationParameters, transUnit);

		return transUnit;
	}

	@Override
	public void update()
	{
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable arg0, Object arg1)
	{
		System.out.println("Something has changed: " + arg0 + " " + arg1);
	}

	@Override
	public void updateDataSourceProperty(String key, String value)
	{
		this.dataSourceConfigurations.updateDataModelProperties(this.getDataSourceName(), key, value);
		dataSourceConfigurations.saveToXmlFile();
	}
}