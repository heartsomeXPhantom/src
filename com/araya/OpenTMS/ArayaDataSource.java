/*
 * Created on 21.10.2009
 * 
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.araya.OpenTMS;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Vector;

import org.jdom.Element;

import com.araya.eaglememex.util.EMXException;
import com.araya.eaglememex.util.EMXProperties;
import com.araya.eaglememex.webclient.EagleMemexClient;
import com.araya.tm.DatabaseLists;
import com.araya.tmx.TmxObject;
import com.araya.tmx.XliffTranslationObject;

import de.folt.models.datamodel.BasicDataSource;
import de.folt.models.datamodel.DataSource;
import de.folt.models.datamodel.DataSourceConfigurations;
import de.folt.models.datamodel.DataSourceInstance;
import de.folt.models.datamodel.DataSourceProperties;
import de.folt.models.datamodel.LinguisticProperty;
import de.folt.models.datamodel.MonoLingualObject;
import de.folt.models.datamodel.MultiLingualObject;
import de.folt.models.datamodel.GeneralLinguisticObject.LinguisticTypes;
import de.folt.models.documentmodel.tmx.TmxProp;
import de.folt.models.documentmodel.xliff.XliffDocument;
import de.folt.util.OpenTMSCrypter;
import de.folt.util.OpenTMSException;
import de.folt.util.OpenTMSProperties;

public class ArayaDataSource extends BasicDataSource
{

	public static void main(String[] args)
	{
		DataSourceProperties dataSourceProperties = new DataSourceProperties();
		dataSourceProperties.put("dataSourceName", args[0]);
		ArayaDataSource arayaDataSource = new ArayaDataSource(dataSourceProperties);
		System.out.println(arayaDataSource.getDataSourceName());
		arayaDataSource.cleanDataSource();

		try
		{
			System.out.println(arayaDataSource.createDataSource(dataSourceProperties));
		}
		catch (OpenTMSException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		dataSourceProperties.put("dataModelClass", arayaDataSource.getDataSourceType());
		try
		{
			DataSource datasource = DataSourceInstance.createInstance(arayaDataSource.getDataSourceType() + ":" + args[0], dataSourceProperties);
			System.out.println(datasource.getDataSourceName());
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		// just for the configuration
		BasicDataSource bas = new BasicDataSource();
		DataSourceConfigurations dsconfig = new DataSourceConfigurations(bas.getDefaultDataSourceConfigurationsFileName());
		String names[] = dsconfig.getDataSources();
		if (names != null)
		{
			for (int i = 0; i < names.length; i++)
			{
				System.out.println("Data source " + i + ": " + names[i]);
			}
		}
		bas = null;
	}

	private String			arayaDatabaseListFile;

	private String			arrayaDatabasePath;

	private String			dataSourceName;

	private int				initEnumerationId;

	private TmxObject		internalTM;

	private String			propertiesFile;

	private Vector<String>	uniqueIdVector;

	/**
     * 
     */
	public ArayaDataSource()
	{
		super();
	}

	/**
	 * @param dataSourceProperties
	 */
	@SuppressWarnings("unchecked")
	public ArayaDataSource(DataSourceProperties dataSourceProperties)
	{
		super(dataSourceProperties);
		dataSourceName = (String) dataSourceProperties.get("database");
		if (dataSourceName == null)
			dataSourceName = (String) dataSourceProperties.get("dataSourceName");
		if (dataSourceName == null)
			dataSourceName = (String) dataSourceProperties.get("datasourcename");
		if (dataSourceName == null)
			dataSourceName = (String) dataSourceProperties.get("datasource");

		String sourceLanguage = (String) dataSourceProperties.get("sourceLanguage");
		propertiesFile = OpenTMSProperties.getInstance().getOpenTMSProperty("ArayaPropertiesFile");
		EMXProperties.setPropfileName(propertiesFile);
		arrayaDatabasePath = EMXProperties.getInstance().getEMXProperty("database.path");
		arayaDatabaseListFile = EMXProperties.getInstance().getEMXProperty("database.list");
		Hashtable<String, String> param = DatabaseLists.getDatabaseParam(dataSourceName, arayaDatabaseListFile);
		if (param.get("filepath") != null)
		{
			arrayaDatabasePath = (String) param.get("filepath");
		}
		try
		{
			internalTM = TmxObject.getInstance((String) param.get("name"), (String) param.get("user"), (String) param.get("password"), (String) param.get("type"), (String) param.get("server"),
					(String) param.get("port"), arrayaDatabasePath, sourceLanguage, false);

			// internalTM.fuzzyIndexTmxObjectForLanguage(sourceLanguage);
			internalTM.setWordIndexing(false, false, false);
			internalTM.setBUseTmxVariables(false);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			internalTM = null;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.BasicDataSource#addMonoLingualObject(de.folt.models.datamodel.MonoLingualObject, boolean)
	 */
	@Override
	public boolean addMonoLingualObject(MonoLingualObject monoLingualObject, boolean mergeObjects)
	{
		com.araya.tmx.MonoLingualObject arayaMono = mapFrom(monoLingualObject);
		int iOldNum = internalTM.getTableIdRefs().size();
		int iRes = internalTM.addMonoLingualObject(arayaMono, true);
		return !(iOldNum == iRes);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.BasicDataSource#addMultiLingualObject(de.folt.models.datamodel.MultiLingualObject, boolean)
	 */
	@Override
	public boolean addMultiLingualObject(MultiLingualObject multiLingualObject, boolean mergeObjects)
	{
		com.araya.tmx.MultiLingualObject arayaMulti = mapFrom(multiLingualObject);
		int iOldNum = internalTM.getTableIdRefs().size();
		int iRes = internalTM.addMultilingualObject(arayaMulti, mergeObjects);
		return !(iOldNum == iRes);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.BasicDataSource#bPersist()
	 */
	@Override
	public boolean bPersist()
	{
		// TODO Auto-generated method stub
		return super.bPersist();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.BasicDataSource#cleanDataSource()
	 */
	@Override
	public void cleanDataSource()
	{
		if (this.internalTM == null)
			return;
		boolean bSuccess = TmxObject.removeInstance(dataSourceName);
		if (bSuccess)
			internalTM = null;
		return;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.BasicDataSource#clearDataSource()
	 */
	@Override
	public boolean clearDataSource() throws OpenTMSException
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.BasicDataSource#copyFrom(de.folt.models.datamodel.DataSource)
	 */
	@Override
	public int copyFrom(de.folt.models.datamodel.DataSource dataSource)
	{
		// TODO Auto-generated method stub
		return super.copyFrom(dataSource);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.BasicDataSource#copyTo(de.folt.models.datamodel.DataSource)
	 */
	@Override
	public int copyTo(de.folt.models.datamodel.DataSource dataSource)
	{
		// TODO Auto-generated method stub
		return super.copyTo(dataSource);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.BasicDataSource#createDataSource(de.folt.models.datamodel.DataSourceProperties)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean createDataSource(DataSourceProperties dataModelProperties) throws OpenTMSException
	{
		try
		{
			propertiesFile = OpenTMSProperties.getInstance().getOpenTMSProperty("ArayaPropertiesFile");
			EMXProperties.setPropfileName(propertiesFile);
			arrayaDatabasePath = EMXProperties.getInstance().getEMXProperty("database.path");
			arayaDatabaseListFile = EMXProperties.getInstance().getEMXProperty("database.list");
			Hashtable<String, String> param = (Hashtable<String, String>) DatabaseLists.getDatabaseParam(dataSourceName, arayaDatabaseListFile);
			if (param.get("filepath") != null)
			{
				arrayaDatabasePath = (String) param.get("filepath");
			}

			dataSourceName = (String) dataSourceProperties.get("database");
			if (dataSourceName == null)
				dataSourceName = (String) dataSourceProperties.get("dataSourceName");
			if (dataSourceName == null)
				dataSourceName = (String) dataSourceProperties.get("datasourcename");
			if (dataSourceName == null)
				dataSourceName = (String) dataSourceProperties.get("datasource");

			String user = (String) param.get("user");
			String password = (String) param.get("password");
			String server = (String) param.get("server");
			String port = (String) param.get("port");
			String type = (String) param.get("type");
			String dataSourceConfigurationFile = (String) dataModelProperties.get("dataSourceConfigurationsFile");
			if ((dataSourceConfigurationFile == null) || (dataSourceConfigurationFile.equals("")))
				dataSourceConfigurationFile = this.getDefaultDataSourceConfigurationsFileName();
			File f = new File(dataSourceConfigurationFile);
			if (!f.exists())
			{
				DataSourceConfigurations.createDataSourceConfiguration(dataSourceConfigurationFile);
			}

			DataSourceConfigurations config = new DataSourceConfigurations(dataSourceConfigurationFile);
			DataSourceProperties props = new DataSourceProperties();
			props.put("dataSourceConfigurationsFile", dataSourceConfigurationFile);
			props.put("database", dataSourceName);
			props.put("connection.server", server);
			props.put("connection.port", port);
			props.put("connection.type", type);
			props.put("connection.username", user);
			props.put("connection.arrayaDatabasePath", arrayaDatabasePath);
			OpenTMSCrypter crypt = new OpenTMSCrypter();
			props.put("connection.password", crypt.encryptString(password));
			config.addConfiguration(dataSourceName, this.getDataSourceType(), props);
			config.saveToXmlFile();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.BasicDataSource#deleteDataSource(de.folt.models.datamodel.DataSourceProperties)
	 */
	@Override
	public boolean deleteDataSource(DataSourceProperties dataModelProperties) throws OpenTMSException
	{
		String datasource = (String) dataModelProperties.get("datasource");
		if (datasource == null)
		{
			datasource = (String) dataModelProperties.get("dataSourceName");
			if (datasource == null)
			{
				datasource = (String) dataModelProperties.get("dataSource");
			}
			if (datasource == null)
			{
				return false;
			}
		}

		String configFile = this.getDefaultDataSourceConfigurationsFileName();
		File f = new File(configFile);
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
			e.printStackTrace();
			return false;
		}
		if (!config.bDataSourceExistsInConfiguration(datasource))
		{
			System.out.println("File " + datasource + " not found in " + configFile);
			return false;
		}

		config.removeConfiguration(datasource);
		config.saveToXmlFile();
		return true;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.BasicDataSource#exportTmxFile(java.lang.String)
	 */
	@Override
	public int exportTmxFile(String tmxFile)
	{
		// TODO Auto-generated method stub
		return super.exportTmxFile(tmxFile);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.BasicDataSource#exportXliffFile(java.lang.String)
	 */
	@Override
	public int exportXliffFile(String xliffFile)
	{
		// TODO Auto-generated method stub
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
		return com.araya.OpenTMS.ArayaDataSource.class.getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.BasicDataSource#getMonoLingualObjectFromId(java.lang.String)
	 */
	@Override
	public MonoLingualObject getMonoLingualObjectFromId(String uniqueID)
	{
		com.araya.tmx.MonoLingualObject arayaMono = internalTM.getMonoLingualObjectOnId(uniqueID);
		if (arayaMono != null)
		{
			return this.mapTo(arayaMono);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.BasicDataSource#getMonoLingualObjectFromUniqueId(java.lang.String)
	 */
	@Override
	public MonoLingualObject getMonoLingualObjectFromUniqueId(String uniqueID)
	{
		com.araya.tmx.MonoLingualObject arayaMono = internalTM.getMonoLingualObjectOnId(uniqueID);
		if (arayaMono != null)
		{
			return this.mapTo(arayaMono);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.BasicDataSource#getMultiLingualObjectFromId(java.lang.String)
	 */
	@Override
	public MultiLingualObject getMultiLingualObjectFromId(String id)
	{
		com.araya.tmx.MultiLingualObject arayaMulti = internalTM.getMultLingualObjectForRefId(id);
		if (arayaMulti != null)
		{
			return this.mapTo(arayaMulti);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.BasicDataSource#getMultiLingualObjectFromUniqueId(java.lang.String)
	 */
	@Override
	public MultiLingualObject getMultiLingualObjectFromUniqueId(String id)
	{
		com.araya.tmx.MultiLingualObject arayaMulti = internalTM.getMultLingualObjectForRefId(id);
		if (arayaMulti != null)
		{
			return this.mapTo(arayaMulti);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.BasicDataSource#getUniqueIds()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Vector<String> getUniqueIds()
	{
		if (internalTM == null)
			return new Vector<String>();
		return (Vector<String>) internalTM.getAllIDs();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.BasicDataSource#hasMoreElements()
	 */
	@Override
	public boolean hasMoreElements()
	{
		if (initEnumerationId < internalTM.getAllIDsNumber())
			return true;
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.BasicDataSource#importTmxFile(java.lang.String)
	 */
	@Override
	public int importTmxFile(String tmxFile)
	{
		// TODO Auto-generated method stub
		return super.importTmxFile(tmxFile);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.BasicDataSource#initEnumeration()
	 */
	@Override
	public void initEnumeration()
	{
		uniqueIdVector = getUniqueIds();
		initEnumerationId = 0;
	}

	/**
	 * mapFrom
	 * 
	 * @param monoLingualObject
	 * @return
	 */
	private com.araya.tmx.MonoLingualObject mapFrom(MonoLingualObject monoLingualObject)
	{
		com.araya.tmx.MonoLingualObject arayaMono = null;
		if (monoLingualObject.getLinguisticProperties().size() > 0)
		{
			arayaMono = (com.araya.tmx.MonoLingualObject) monoLingualObject.getLinguisticProperties().get("MonoLingualObject");
			if (arayaMono != null)
			{
				if (monoLingualObject.getFormattedSegment().equals(arayaMono.getSegmentfull()))
				{
					return arayaMono; // no real changes done to segment
				}
				else
				{
					// change to segment must be delete
					com.araya.tmx.MonoLingualObject newArayaMono = arayaMono.cloneMono();
					internalTM.bRemoveMonoLingualObject(arayaMono);
					arayaMono = newArayaMono;
					arayaMono.setIProperty(0);
				}
			}
		}
		// a new MonoLingualObject...
		if (arayaMono == null)
		{
			arayaMono = new com.araya.tmx.MonoLingualObject();
		}
		// for the moment we just use the main properties to save in Araya
		arayaMono.setLanguage(monoLingualObject.getLanguage());

		arayaMono.setPlainsegmentfull(monoLingualObject.getPlainTextSegment());
		arayaMono.setSegmentfull(monoLingualObject.getFormattedSegment());
		arayaMono.setCreationauthor(monoLingualObject.getStOwner());
		arayaMono.setLastaccess((int) (long) monoLingualObject.getLastAccessTime());
		return arayaMono;
	}

	/**
	 * mapFrom
	 * 
	 * @param multiLingualObject
	 * @return
	 */
	private com.araya.tmx.MultiLingualObject mapFrom(MultiLingualObject multiLingualObject)
	{
		com.araya.tmx.MultiLingualObject arayaMulti = null;
		if (multiLingualObject.getLinguisticProperties().size() > 0)
		{
			arayaMulti = (com.araya.tmx.MultiLingualObject) multiLingualObject.getLinguisticProperties().get("MultiLingualObject");
		}
		else
		{
			arayaMulti = new com.araya.tmx.MultiLingualObject();
			arayaMulti.setIdno(multiLingualObject.getStUniqueID());
			arayaMulti.setIdref(multiLingualObject.getStUniqueID());
			arayaMulti.setId(internalTM.getAllIDsNumber() + 1);
		}
		Vector<MonoLingualObject> monos = multiLingualObject.getMonoLingualObjectsAsVector();
		for (int i = 0; i < monos.size(); i++)
		{
			com.araya.tmx.MonoLingualObject arayaMono = mapFrom(monos.get(i));
			arayaMono.setIdref(multiLingualObject.getStUniqueID()); // changed
			arayaMono.setIdno(multiLingualObject.getStUniqueID() + "." + arayaMono.getLanguage()); // changed
			arayaMono.setId(internalTM.getAllIDsNumber() + 1);
			arayaMulti.bAddMonoLingualObject(arayaMono); // maybe we need to check if duplicate
		}
		return arayaMulti;
	}

	/**
	 * mapTo
	 * 
	 * @param arayaMono
	 * @return
	 */
	private MonoLingualObject mapTo(com.araya.tmx.MonoLingualObject arayaMono)
	{
		MonoLingualObject openTMSMonoLingualObject = new MonoLingualObject();
		openTMSMonoLingualObject.setLanguage(arayaMono.getLanguage());
		openTMSMonoLingualObject.setFormattedSegment(arayaMono.getSegmentfull());
		openTMSMonoLingualObject.setPlainTextSegment(arayaMono.getPlainsegmentfull());
		openTMSMonoLingualObject.setLastAccessTime(arayaMono.getLastaccess());
		openTMSMonoLingualObject.setLingType(LinguisticTypes.TMX);
		openTMSMonoLingualObject.setId(arayaMono.getId());
		openTMSMonoLingualObject.setUniqueID(arayaMono.getIdno() + "." + arayaMono.getId()); // getIdref());
		openTMSMonoLingualObject.setStOwner(arayaMono.getCreationauthor());
		setLinguisticProperties(openTMSMonoLingualObject, arayaMono);
		return openTMSMonoLingualObject;
	}

	/**
	 * mapTo
	 * 
	 * @param arayaMulti
	 * @return
	 */
	private MultiLingualObject mapTo(com.araya.tmx.MultiLingualObject arayaMulti)
	{
		MultiLingualObject openTMSMultiLingualObject = new MultiLingualObject();
		setLinguisticProperties(openTMSMultiLingualObject, arayaMulti);

		openTMSMultiLingualObject.setLastAccessTime(arayaMulti.getLastaccess());
		openTMSMultiLingualObject.setLingType(LinguisticTypes.TMX);
		openTMSMultiLingualObject.setId(arayaMulti.getId());
		openTMSMultiLingualObject.setUniqueID(arayaMulti.getIdref());
		openTMSMultiLingualObject.setStOwner(arayaMulti.getCreationauthor());
		Vector<com.araya.tmx.MonoLingualObject> arayaMonos = arayaMulti.getAllMonoLingualObjectsAsVector();
		for (int i = 0; i < arayaMonos.size(); i++)
		{
			com.araya.tmx.MonoLingualObject arayaMono = arayaMonos.get(i);
			MonoLingualObject openTMSMonoLingualObject = mapTo(arayaMono);
			openTMSMultiLingualObject.addMonoLingualObject(openTMSMonoLingualObject);
		}
		return openTMSMultiLingualObject;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.BasicDataSource#nextElement()
	 */
	@Override
	public MultiLingualObject nextElement()
	{
		com.araya.tmx.MultiLingualObject arayaMulti = internalTM.getMultLingualObjectForRefId(uniqueIdVector.get(initEnumerationId));
		MultiLingualObject multi = mapTo(arayaMulti);
		initEnumerationId++;
		return multi;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.BasicDataSource#removeDataSource()
	 */
	@Override
	public void removeDataSource()
	{
		// TODO Auto-generated method stub
		super.removeDataSource();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.BasicDataSource#removeMonoLingualObject(de.folt.models.datamodel.MonoLingualObject)
	 */
	@Override
	public boolean removeMonoLingualObject(MonoLingualObject monoLingualObject)
	{
		// TODO Auto-generated method stub
		return super.removeMonoLingualObject(monoLingualObject);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.BasicDataSource#removeMultiLingualObject(de.folt.models.datamodel.MultiLingualObject)
	 */
	@Override
	public boolean removeMultiLingualObject(MultiLingualObject multiLingualObject)
	{
		// TODO Auto-generated method stub
		return super.removeMultiLingualObject(multiLingualObject);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.BasicDataSource#search(de.folt.models.datamodel.MonoLingualObject, java.util.Hashtable)
	 */
	@Override
	public Vector<MonoLingualObject> search(MonoLingualObject searchMonoLingualObject, Hashtable<String, Object> searchParameters)
	{
		Vector<MonoLingualObject> matchMonoVector = new Vector<MonoLingualObject>();
		try
		{

			Vector<com.araya.tmx.MultiLingualObject> arayaMatchMulti = null;
			if (internalTM.isBServerSidedTmxObject())
			{
				// arayaMatchMulti = EagleMemexClient.searchSegment(searchMonoLingualObject.getFormattedSegment(), searchMonoLingualObject.getLanguage(), internalTM.getDATABASENAME());
			}
				else
			{
				internalTM.setBNoSyncWithDatabase(internalTM.isBNoSyncWithDatabase());

				arayaMatchMulti = internalTM.searchSegment(searchMonoLingualObject.getFormattedSegment(), searchMonoLingualObject.getLanguage());
				internalTM.setBNoSyncWithDatabase(false);
			}
			if (arayaMatchMulti != null)
			{
				// search the monolingual object
				for (int j = 0; j < arayaMatchMulti.size(); j++)
				{
					MultiLingualObject multi = mapTo(arayaMatchMulti.get(j));
					Vector<MonoLingualObject> allmonos = multi.getMonoLingualObjectsAsVector(searchMonoLingualObject.getLanguage());
					for (int i = 0; i < allmonos.size(); i++)
					{
						if (allmonos.get(i).getFormattedSegment().equals(searchMonoLingualObject.getFormattedSegment()))
						{
							matchMonoVector.add(allmonos.get(i));
							break;
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return matchMonoVector;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.BasicDataSource#setDataSourceType()
	 */
	@Override
	public void setDataSourceType()
	{
		getDataSourceType();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.BasicDataSource#setILogLevel()
	 */
	@Override
	public void setILogLevel()
	{
		// TODO Auto-generated method stub
		super.setILogLevel();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.BasicDataSource#setILogLevel(int)
	 */
	@Override
	public void setILogLevel(int logLevel)
	{
		// TODO Auto-generated method stub
		super.setILogLevel(logLevel);
	}

	private void setLinguisticProperties(MonoLingualObject openTMSMonoLingualObject, com.araya.tmx.MonoLingualObject arayaMono)
	{
		int idVal = 0;
		TmxProp tmxProp = new TmxProp(arayaMono.getChangedate(), "", "", TmxProp.PropType.CORE, "getChangedate", idVal);
		LinguisticProperty lingProp = new LinguisticProperty(tmxProp.toString(), tmxProp);
		openTMSMonoLingualObject.addLinguisticProperty(lingProp);

		tmxProp = new TmxProp(arayaMono.getCreationauthor(), "", "", TmxProp.PropType.CORE, "getCreationauthor", idVal++);
		lingProp = new LinguisticProperty(tmxProp.toString(), tmxProp);
		openTMSMonoLingualObject.addLinguisticProperty(lingProp);

		tmxProp = new TmxProp(arayaMono.getCreationdate(), "", "", TmxProp.PropType.CORE, "getCreationdate", idVal++);
		lingProp = new LinguisticProperty(tmxProp.toString(), tmxProp);
		openTMSMonoLingualObject.addLinguisticProperty(lingProp);

		tmxProp = new TmxProp(arayaMono.getModificationauthor(), "", "", TmxProp.PropType.CORE, "getModificationauthor", idVal++);
		lingProp = new LinguisticProperty(tmxProp.toString(), tmxProp);
		openTMSMonoLingualObject.addLinguisticProperty(lingProp);

		tmxProp = new TmxProp(arayaMono.getModificationdate(), "", "", TmxProp.PropType.CORE, "getModificationdate", idVal++);
		lingProp = new LinguisticProperty(tmxProp.toString(), tmxProp);
		openTMSMonoLingualObject.addLinguisticProperty(lingProp);

		tmxProp = new TmxProp(arayaMono.getDocument(), "", "", TmxProp.PropType.CORE, "getDocument", idVal++);
		lingProp = new LinguisticProperty(tmxProp.toString(), tmxProp);
		openTMSMonoLingualObject.addLinguisticProperty(lingProp);

		tmxProp = new TmxProp(arayaMono.getDocumentid(), "", "", TmxProp.PropType.CORE, "getDocumentid", idVal++);
		lingProp = new LinguisticProperty(tmxProp.toString(), tmxProp);
		openTMSMonoLingualObject.addLinguisticProperty(lingProp);

		tmxProp = new TmxProp(arayaMono.getDomain(), "", "", TmxProp.PropType.CORE, "getDomain", idVal++);
		lingProp = new LinguisticProperty(tmxProp.toString(), tmxProp);
		openTMSMonoLingualObject.addLinguisticProperty(lingProp);

		tmxProp = new TmxProp(arayaMono.getStProperty(), "", "", TmxProp.PropType.CORE, "getStProperty", idVal++);
		lingProp = new LinguisticProperty(tmxProp.toString(), tmxProp);
		openTMSMonoLingualObject.addLinguisticProperty(lingProp);

		tmxProp = new TmxProp(arayaMono.getTmuser(), "", "", TmxProp.PropType.CORE, "getTmuser", idVal++);
		lingProp = new LinguisticProperty(tmxProp.toString(), tmxProp);
		openTMSMonoLingualObject.addLinguisticProperty(lingProp);

		tmxProp = new TmxProp(arayaMono.getAttributes(), "", "", TmxProp.PropType.CORE, "getAttributes", idVal++);
		lingProp = new LinguisticProperty(tmxProp.toString(), tmxProp);
		openTMSMonoLingualObject.addLinguisticProperty(lingProp);

		tmxProp = new TmxProp(arayaMono.getIdno(), "", "", TmxProp.PropType.CORE, "getIdno", idVal++);
		lingProp = new LinguisticProperty(tmxProp.toString(), tmxProp);
		openTMSMonoLingualObject.addLinguisticProperty(lingProp);

		tmxProp = new TmxProp(arayaMono.getIdref(), "", "", TmxProp.PropType.CORE, "getIdref", idVal++);
		lingProp = new LinguisticProperty(tmxProp.toString(), tmxProp);
		openTMSMonoLingualObject.addLinguisticProperty(lingProp);

		tmxProp = new TmxProp(arayaMono.getTimer() + "", "", "", TmxProp.PropType.CORE, "getTimer", idVal++);
		lingProp = new LinguisticProperty(tmxProp.toString(), tmxProp);
		openTMSMonoLingualObject.addLinguisticProperty(lingProp);

		lingProp = new LinguisticProperty("MonoLingualObject", arayaMono);
		openTMSMonoLingualObject.addLinguisticProperty(lingProp);
	}

	private void setLinguisticProperties(MultiLingualObject openTMSMultiLingualObject, com.araya.tmx.MultiLingualObject arayaMulti)
	{
		int idVal = 0;
		TmxProp tmxProp = new TmxProp(arayaMulti.getChangedate(), "", "", TmxProp.PropType.CORE, "getChangedate", idVal);
		LinguisticProperty lingProp = new LinguisticProperty(tmxProp.toString(), tmxProp);
		openTMSMultiLingualObject.addLinguisticProperty(lingProp);

		tmxProp = new TmxProp(arayaMulti.getCreationauthor(), "", "", TmxProp.PropType.CORE, "getCreationauthor", idVal++);
		lingProp = new LinguisticProperty(tmxProp.toString(), tmxProp);
		openTMSMultiLingualObject.addLinguisticProperty(lingProp);

		tmxProp = new TmxProp(arayaMulti.getCreationdate(), "", "", TmxProp.PropType.CORE, "getCreationdate", idVal++);
		lingProp = new LinguisticProperty(tmxProp.toString(), tmxProp);
		openTMSMultiLingualObject.addLinguisticProperty(lingProp);

		tmxProp = new TmxProp(arayaMulti.getModificationauthor(), "", "", TmxProp.PropType.CORE, "getModificationauthor", idVal++);
		lingProp = new LinguisticProperty(tmxProp.toString(), tmxProp);
		openTMSMultiLingualObject.addLinguisticProperty(lingProp);

		tmxProp = new TmxProp(arayaMulti.getModificationdate(), "", "", TmxProp.PropType.CORE, "getModificationdate", idVal++);
		lingProp = new LinguisticProperty(tmxProp.toString(), tmxProp);
		openTMSMultiLingualObject.addLinguisticProperty(lingProp);

		tmxProp = new TmxProp(arayaMulti.getDocument(), "", "", TmxProp.PropType.CORE, "getDocument", idVal++);
		lingProp = new LinguisticProperty(tmxProp.toString(), tmxProp);
		openTMSMultiLingualObject.addLinguisticProperty(lingProp);

		tmxProp = new TmxProp(arayaMulti.getDocumentid(), "", "", TmxProp.PropType.CORE, "getDocumentid", idVal++);
		lingProp = new LinguisticProperty(tmxProp.toString(), tmxProp);
		openTMSMultiLingualObject.addLinguisticProperty(lingProp);

		tmxProp = new TmxProp(arayaMulti.getDomain(), "", "", TmxProp.PropType.CORE, "getDomain", idVal++);
		lingProp = new LinguisticProperty(tmxProp.toString(), tmxProp);
		openTMSMultiLingualObject.addLinguisticProperty(lingProp);

		tmxProp = new TmxProp(arayaMulti.getStProperty(), "", "", TmxProp.PropType.CORE, "getStProperty", idVal++);
		lingProp = new LinguisticProperty(tmxProp.toString(), tmxProp);
		openTMSMultiLingualObject.addLinguisticProperty(lingProp);

		tmxProp = new TmxProp(arayaMulti.getTmuser(), "", "", TmxProp.PropType.CORE, "getTmuser", idVal++);
		lingProp = new LinguisticProperty(tmxProp.toString(), tmxProp);
		openTMSMultiLingualObject.addLinguisticProperty(lingProp);

		tmxProp = new TmxProp(arayaMulti.getAttributes(), "", "", TmxProp.PropType.CORE, "getAttributes", idVal++);
		lingProp = new LinguisticProperty(tmxProp.toString(), tmxProp);
		openTMSMultiLingualObject.addLinguisticProperty(lingProp);

		tmxProp = new TmxProp(arayaMulti.getIdno(), "", "", TmxProp.PropType.CORE, "getIdno", idVal++);
		lingProp = new LinguisticProperty(tmxProp.toString(), tmxProp);
		openTMSMultiLingualObject.addLinguisticProperty(lingProp);

		tmxProp = new TmxProp(arayaMulti.getIdref(), "", "", TmxProp.PropType.CORE, "getIdref", idVal++);
		lingProp = new LinguisticProperty(tmxProp.toString(), tmxProp);
		openTMSMultiLingualObject.addLinguisticProperty(lingProp);

		tmxProp = new TmxProp(arayaMulti.getTimer() + "", "", "", TmxProp.PropType.CORE, "getTimer", idVal++);
		lingProp = new LinguisticProperty(tmxProp.toString(), tmxProp);
		openTMSMultiLingualObject.addLinguisticProperty(lingProp);

		lingProp = new LinguisticProperty("MultiLingualObject", arayaMulti);
		openTMSMultiLingualObject.addLinguisticProperty(lingProp);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.BasicDataSource#translate(org.jdom.Element, de.folt.models.documentmodel.xliff.XliffDocument, java.lang.String, java.lang.String, int, java.util.Hashtable)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Element translate(Element transUnit, Element file, XliffDocument xliffDocument, String sourceLanguage, String targetLanguage, int matchSimilarity,
			Hashtable<String, Object> translationParameters)
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
			if (translationParameters.containsKey("ignoreApproveAttribute") && ((String) (translationParameters.get("ignoreApproveAttribute"))).equals("yes"))
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

		Element source = transUnit.getChild("source");
		String searchsegment = xliffDocument.elementContentToString(source);

		try
		{
			Vector translations = null;
			if (internalTM.isBServerSidedTmxObject())
				translations = EagleMemexClient.searchTMXEntry(searchsegment, sourceLanguage, targetLanguage, "", matchSimilarity + "", 100 + "", internalTM.getDATABASENAME(), null, internalTM
						.isBNoSyncWithDatabase());
			else
			{
				internalTM.setBNoSyncWithDatabase(internalTM.isBNoSyncWithDatabase());
				translations = internalTM.SearchSimilarSegment(searchsegment, sourceLanguage, targetLanguage, (int) matchSimilarity, 100);
				internalTM.setBNoSyncWithDatabase(false);
			}
			if (translations.size() == 0)
				return transUnit;

			if ((translations != null) && (translations.size() > 0))
			{
				int iTranslationsFound = translations.size();

				for (int i = 0; i < iTranslationsFound; i++)
				{
					XliffTranslationObject translated = (XliffTranslationObject) translations.get(i);
					// now we must convert the XliffTranslationObject into a trans

					com.araya.tmx.MonoLingualObject mono = (com.araya.tmx.MonoLingualObject) translated.getSourceMonoLingualObject();
					MonoLingualObject sourceMono = this.mapTo(mono);
					MultiLingualObject multi = new MultiLingualObject(sourceMono.getUniqueID(), null, LinguisticTypes.TMX);
					multi.addMonoLingualObject(sourceMono);
					@SuppressWarnings("unused")
					boolean bReplComputed = false;
					if (mono != null)
						bReplComputed = mono.isReplClassComputed();
					Vector<MonoLingualObject> targetmonos = new Vector<MonoLingualObject>();
					Enumeration targetenum = ((Hashtable) translated.getTranslations()).keys();
					// here we should sort the targets with regard to their date...
					// problem is we need it in vector sort it...
					Vector targetsorter = new Vector();
					while (targetenum.hasMoreElements())
					{
						String segment = (String) targetenum.nextElement();
						com.araya.tmx.MonoLingualObject targetmono = (com.araya.tmx.MonoLingualObject) translated.getTranslations().get(segment);
						targetsorter.add(targetmono);
					}
					// now run the sort operation
					if (targetsorter.size() > 1)
					{
						Collections.sort(targetsorter, new Comparator()
						{
							public int compare(Object mono1o, Object mono2o)
							{
								com.araya.tmx.MonoLingualObject mono1 = (com.araya.tmx.MonoLingualObject) mono1o;
								com.araya.tmx.MonoLingualObject mono2 = (com.araya.tmx.MonoLingualObject) mono2o;
								long mono1moddate = mono1.getModificationdateInt();
								long mono2moddate = mono2.getModificationdateInt();

								long compdate1 = mono1moddate;
								long compdate2 = mono2moddate;
								long mono1credate = mono1.getCreationdateInt();
								long mono2credate = mono2.getCreationdateInt();

								if (mono1moddate == 0)
									compdate1 = mono1credate;
								if (mono2moddate == 0)
									compdate2 = mono2credate;

								if (compdate1 > compdate2)
									return -1;
								else if (compdate1 < compdate2)
									return 1;
								return 0;
							}
						});
					}

					for (int k = 0; k < targetsorter.size(); k++)
					{
						MonoLingualObject targetmono = this.mapTo((com.araya.tmx.MonoLingualObject) targetsorter.get(k));
						targetmonos.add(targetmono);
						multi.addMonoLingualObject(targetmono);
					}

					Element alttrans = xliffDocument.addAltTrans(transUnit, sourceMono, targetmonos, (int) translated.getMatchQuality(), translationParameters);
					if ((alttrans != null) && (dataSourceProperties != null))
						alttrans.setAttribute("origin", (String) dataSourceProperties.get("dataSourceName"));
				}

			}
		}
		catch (EMXException e)
		{
			e.printStackTrace();
		}

		return transUnit;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.BasicDataSource#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable arg0, Object arg1)
	{
		// TODO Auto-generated method stub
		super.update(arg0, arg1);
	}

}
