/*
 * Created on 05.01.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.models.datamodel;

import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Observer;
import java.util.Random;
import java.util.Vector;

import de.folt.constants.OpenTMSConstants;
import de.folt.util.OpenTMSException;
import de.folt.util.OpenTMSProperties;

/**
 * This class maintains a list of active data sources (instances of data
 * sources). Data source are added by using <b>createInstance</b>. The class
 * itself cannot be instantiated.<br>
 * The main idea is to have only one active instance for each named data source
 * . This e.g. avoids loading the same data sources several times (in different
 * threads).<br>
 * This implements a (enhanced) singleton pattern; not in its classical meaning
 * where one has really only one instance. In contrasts it pools several
 * instances of data sources in one hash table. Each data source is stored in
 * the hash table with a key which identifies a given dat source. As a singleton
 * there is no method to create a DataSourceInstance. The method
 * <b>getInstace</b> returns a named data source. getInstance creates one if it
 * does not exist otherwise returns the one found. getAllDataSourceInstanceNames
 * returns all active instances.
 * 
 * The data sources themselves are stores in @see
 * de.folt.models.datamodel.DataSourceStorage, which basically is a class
 * consisting of the data source and a counter indicating how often the
 * createInstance method was called. createInstance increase the counter,
 * removeInsance decreases the counter. The data source is removed from the list
 * when the counter decrements to zero. The data source remove method is only
 * called if the removeIfCounterZero boolean of DataSourceStorage is set to true
 * (which it is per default).
 * 
 * @author klemens
 * 
 */
public class DataSourceInstance
{

	private static Hashtable<String, DataSourceStorage> dataSource = new Hashtable<String, DataSourceStorage>();

	/**
	 * createInstance create an instance of a data source based on its name.
	 * Method requires that it can determine the parameters of the data source
	 * for an existing data source some how.
	 * 
	 * @param name
	 *            of the data source
	 * @return the data source or null in case of not found or when an Exception
	 *         occurred
	 * @throws de.folt.util.OpenTMSException
	 */
	public static synchronized DataSource createInstance(String name) throws de.folt.util.OpenTMSException
	{
		if (dataSource.containsKey(name))
		{
			dataSource.get(name).incrementCounter();
			return dataSource.get(name).getDataSource();
		}
		String configfile = OpenTMSProperties.getInstance().getOpenTMSProperty("dataSourceConfigurationsFile");
		String configfileFixed = OpenTMSProperties.getInstance()
				.getOpenTMSProperty("dataSourceConfigurationsFileFixed");
		if ((configfile == null) && (configfileFixed == null))
		{
			OpenTMSProperties.getInstance();
			System.out.println("No DataSource Configuration Files defined in OpenTMSProperties " + OpenTMSProperties.getPropfileName());
			return null;
		}
		if (configfile != null)
		{
			DataSourceConfigurations dsconfig = new DataSourceConfigurations(configfile);

			String dataModelClass = dsconfig.getDataSourceType(name);

			if (dataModelClass == null)
			{
				// now let's try the fixed config file
				if (configfileFixed != null)
				{
					dsconfig = new DataSourceConfigurations(configfileFixed);

					dataModelClass = dsconfig.getDataSourceType(name);
					if (dataModelClass == null)
					{
						System.out.println("No DataSourceType found in \"" + configfileFixed + "\" for data source \""
								+ name + "\".");
						return null;
					}
				}
				else
				{
					System.out.println("No DataSourceType found in \"" + configfile + "\" for data source \"" + name
							+ "\".");
					return null;
				}
			}

			DataSourceProperties dataModelProperties = new DataSourceProperties();
			dataModelProperties.put("dataModelClass", dataModelClass);
			dataModelProperties.put("dataSourceName", name);

			dataModelProperties.put("sourceLanguage", "");
			dataModelProperties.put("targetLanguage", "");
			dsconfig.addPropertiesToDataModelProperties(name, dataModelProperties);
			// ok - we need to add all the other necessary parameters
			// we can do this by getting them from the configuration file...
			try
			{
				DataSource thisDataSource = DataSourceInstance.createInstance(name, dataModelProperties);
				thisDataSource.setDataSourceConfigurations(dsconfig);
				return thisDataSource;
			}
			catch (OpenTMSException e)
			{
				e.printStackTrace();
				return null;
			}
		}
		else
		{
			System.out.println("No DataSource Configuration Files defined in OpenTMSProperties " + OpenTMSProperties.getPropfileName());
		}
		return null;
	}

	/**
	 * createInstance create an instance of a data source based on its name and
	 * model a parameters.
	 * 
	 * @param name
	 *            the name for the new instance
	 * @param dataModelProperties
	 *            the properties associated with the new instance
	 * @return the instance generated
	 */
	@SuppressWarnings("unchecked")
	public static synchronized DataSource createInstance(String name, DataSourceProperties dataModelProperties)
			throws de.folt.util.OpenTMSException
	{
		try
		{
			if (dataSource.containsKey(name))
			{
				dataSource.get(name).incrementCounter();
				System.out.println("createInstance: " + name + " reused " + dataSource.get(name).getCounter());
				return dataSource.get(name).getDataSource();
			}
			String datamodelclass = (String) dataModelProperties.getDataSourceProperty("dataModelClass");
			URLClassLoader ucl = null;
			URL[] urls = new URL[1];
			// the url of the class / jar file for the DataModel used
			urls[0] = (URL) dataModelProperties.getDataSourceProperty("dataModelUrl");
			Class<DataSource> exeClass = null;
			if (urls[0] != null)
			{
				ucl = new URLClassLoader(urls);
				Object objconv = ucl.loadClass(datamodelclass).newInstance();
				exeClass = (Class<DataSource>) objconv.getClass();
			}
			else
			// get the class from the current classes - somewhere...
			{
				exeClass = (Class<DataSource>) Class.forName(datamodelclass);
			}

			if (exeClass == null)
			{
				return null;
			}

			// define the Constructor to be used
			// Example: datamodel.sql ... public sql(DataModelProperties
			// dataModelProperties)
			@SuppressWarnings("rawtypes")
			Class[] classparams = new Class[1];
			classparams[0] = DataSourceProperties.class;
			@SuppressWarnings("rawtypes")
			Constructor cons = exeClass.getConstructor(classparams);
			Object param[] = new Object[1];
			param[0] = dataModelProperties;
			// create a new DataModel
			DataSource datamodel = (DataSource) cons.newInstance(param);
			// save it in case of success
			if (datamodel.getLastErrorCode() == 0)
			{
				DataSourceStorage dataSourceStorage = new DataSourceStorage(datamodel);
				dataSource.put(name, dataSourceStorage);
				System.out.println("createInstance: " + name + " new " + dataSource.get(name).getCounter());
			}
			else
			{
				throw new de.folt.util.OpenTMSException("createInstance", "failure", datamodel.getLastErrorCode(),
						datamodel);
			}
			// end return it
			return datamodel;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}

	}

	/**
	 * createInstance creates an instance of a data source with and attaches an
	 * observer
	 * 
	 * @param name
	 *            the data source to create
	 * @param observer
	 *            an observer for the data source
	 * @return the data source
	 */
	public static DataSource createInstance(String name, Observer observer) throws de.folt.util.OpenTMSException
	{
		if (dataSource.containsKey(name))
		{
			dataSource.get(name).incrementCounter();
			return dataSource.get(name).getDataSource();
		}
		String configfile = OpenTMSProperties.getInstance().getOpenTMSProperty("dataSourceConfigurationsFile");
		if (configfile != null)
		{
			DataSourceConfigurations dsconfig = new DataSourceConfigurations(configfile);

			String dataModelClass = dsconfig.getDataSourceType(name);

			DataSourceProperties dataModelProperties = new DataSourceProperties();
			dataModelProperties.put("dataModelClass", dataModelClass);
			dataModelProperties.put("dataSourceName", name);

			dataModelProperties.put("sourceLanguage", "");
			dataModelProperties.put("targetLanguage", "");
			dataModelProperties.put("observer", observer);
			dsconfig.addPropertiesToDataModelProperties(name, dataModelProperties);
			// ok - we need to add all the other necessary parameters
			// we can do this by getting them from the configuration file...
			try
			{
				return DataSourceInstance.createInstance(name, dataModelProperties);
			}
			catch (OpenTMSException e)
			{
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	/**
	 * createInstance create an instance of a data source based on its name.
	 * Method requires that it can determine the parameters of the data source
	 * for an existing data source some how.
	 * 
	 * @param name
	 *            of the data source
	 * @return the data source or null in case of not found or when an Exception
	 *         occured
	 * @throws de.folt.util.OpenTMSException
	 */
	public static synchronized DataSource createInstance(String name, String configfile)
			throws de.folt.util.OpenTMSException
	{
		if (dataSource.containsKey(name))
			return dataSource.get(name).getDataSource();

		if (configfile != null)
		{
			DataSourceConfigurations dsconfig = new DataSourceConfigurations(configfile);

			String dataModelClass = dsconfig.getDataSourceType(name);

			DataSourceProperties dataModelProperties = new DataSourceProperties();
			dataModelProperties.put("dataModelClass", dataModelClass);
			dataModelProperties.put("dataSourceName", name);

			dataModelProperties.put("sourceLanguage", "");
			dataModelProperties.put("targetLanguage", "");

			String sourceLanguage = (String) dsconfig.getProperty(configfile, "sourceLanguage");
			if (sourceLanguage != null)
				dataModelProperties.put("sourceLanguage", sourceLanguage);
			String targetLanguage = (String) dsconfig.getProperty(configfile, "targetLanguage");
			if (targetLanguage != null)
				dataModelProperties.put("targetLanguage", targetLanguage);
			String encoding = (String) dsconfig.getProperty(configfile, "encoding");
			if (targetLanguage != null)
				dataModelProperties.put("encoding", encoding);

			dsconfig.addPropertiesToDataModelProperties(name, dataModelProperties);
			// ok - we need to add all the other necessary parameters
			// we can do this by getting them from the configuration file...
			try
			{
				return DataSourceInstance.createInstance(name, dataModelProperties);
			}
			catch (OpenTMSException e)
			{
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	/**
	 * getAllDataSourceInstanceNames returns all data source instances
	 * 
	 * @return the names of the data source instances
	 */
	public static String[] getAllDataSourceInstanceNames()
	{
		int iLen = dataSource.size();
		String[] instances = new String[iLen];
		Enumeration<String> inst = dataSource.keys();
		int i = 0;
		while (inst.hasMoreElements())
		{
			instances[i] = inst.nextElement();
			i++;
		}

		return instances;
	}

	/**
	 * getCounter get the counter (usage) value for the data source
	 * 
	 * @param name
	 *            the data source name
	 * @return the counter associated with the instance or -1 in case of error
	 */
	public static int getCounter(String name)
	{
		DataSourceStorage ds = getDataSourceStorageInstance(name);
		if (ds != null)
			return ds.getCounter();
		return -1;
	}

	/**
	 * getDataSourceStorageInstance get the data storage instance
	 * 
	 * @param name
	 *            the name of the instance
	 * @return the DataSourceStorage associated with the instance
	 */
	public static DataSourceStorage getDataSourceStorageInstance(String name)
	{
		return dataSource.get(name);
	}

	/**
	 * getInstance
	 * 
	 * @param name
	 *            the name of the data source instance to be retrieved
	 * @return a data source based on name
	 */
	public static DataSource getInstance(String name)
	{
		DataSourceStorage ds = getDataSourceStorageInstance(name);
		if (ds != null)
			return dataSource.get(name).getDataSource();
		else
			return null;
	}

	/**
	 * getKnownDataSourceModels return all the known data source model classes
	 * 
	 * @return a vector with the names of the know data source model class names
	 */
	public static Vector<String> getKnownDataSourceModels()
	{
		Vector<String> datasourceclasses = new Vector<String>();
		datasourceclasses.add(de.folt.models.datamodel.sql.OpenTMSSQLDataSource.class.getName());
		datasourceclasses.add(com.araya.OpenTMS.ArayaDataSource.class.getName());
		datasourceclasses.add(de.folt.models.datamodel.tmxfile.TmxFileDataSource.class.getName());
		datasourceclasses.add(de.folt.models.datamodel.xlifffile.XliffFileDataSource.class.getName());
		datasourceclasses.add(de.folt.models.datamodel.tbxfile.TbxFileDataSource.class.getName());
		datasourceclasses.add(de.folt.models.datamodel.csv.Csv.class.getName());
		datasourceclasses.add(de.folt.models.datamodel.trados.TradosTMDataSource.class.getName());
		datasourceclasses.add(de.folt.models.datamodel.multipledatasource.MultipleDataSource.class.getName());
		datasourceclasses.add(de.folt.models.datamodel.parallelcorpus.ParallelCorpus.class.getName());
		datasourceclasses.add(de.folt.models.datamodel.db4o.DB4O.class.getName());
		datasourceclasses.add(de.folt.models.datamodel.googletranslate.GoogleTranslate.class.getName());
		datasourceclasses.add(de.folt.models.datamodel.microsofttranslate.MicrosoftTranslate.class.getName());
		datasourceclasses.add(de.folt.models.datamodel.mtmoses.MTMoses.class.getName());
		return datasourceclasses;
	}

	/**
	 * getOpenTMSDatabases get a vector of the openTMS Data Sources
	 * 
	 * @return a Vector containing the openTMS Data Sources
	 */
	public static Vector<String> getOpenTMSDatabases()
	{
		Vector<String> vec = new Vector<String>();
		try
		{
			// just for the configuration
			String configfile = de.folt.util.OpenTMSProperties.getInstance().getOpenTMSProperty(
					"OpenTMS.OpenTMSDataSourceConfigFile");
			if (configfile != null)
			{
				DataSourceConfigurations dsconfig = new DataSourceConfigurations(configfile);
				String names[] = dsconfig.getDataSources();
				for (int i = 0; i < names.length; i++)
				{
					vec.add(names[i]);
				}
			}
			Collections.sort(vec);
			return vec;
		}
		catch (Exception ex)
		{
			return null;
		}
	}
	
	public static Vector<String[]> getDataSourcesWithType()
	{
		try
		{
			// just for the configuration
			String configfile = de.folt.util.OpenTMSProperties.getInstance().getOpenTMSProperty(
					"OpenTMS.OpenTMSDataSourceConfigFile");
			if (configfile != null)
			{
				DataSourceConfigurations dsconfig = new DataSourceConfigurations(configfile);
				Vector<String[]> names = dsconfig.getDataSourcesWithType();
				return names;
			}
			return null;
		}
		catch (Exception ex)
		{
			return null;
		}
	}
	
	public static String[] getDataSourcesWithType(String type)
	{
		try
		{
			// just for the configuration
			String configfile = de.folt.util.OpenTMSProperties.getInstance().getOpenTMSProperty(
					"OpenTMS.OpenTMSDataSourceConfigFile");
			if (configfile != null)
			{
				DataSourceConfigurations dsconfig = new DataSourceConfigurations(configfile);
				String[] names = dsconfig.getDataSources(type);
				return names;
			}
			return null;
		}
		catch (Exception ex)
		{
			return null;
		}
	}

	/**
	 * main
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		test();
	}

	/**
	 * removeInstance
	 * 
	 * @param name
	 *            the name of the data model to remove; usually the data model
	 *            then closes connections etc.
	 * @return the removed data model
	 * @throws OpenTMSException
	 */
	public static DataSource removeInstance(String name) throws OpenTMSException
	{
		if (!dataSource.containsKey(name))
		{
			throw new OpenTMSException(name, "Data source does not exist",
					OpenTMSConstants.OpenTMS_DATASOURCE_NOTFOUND_ERROR);
		}
		DataSourceStorage dataSourceStorage = dataSource.get(name);
		if (dataSourceStorage != null)
		{
			DataSource dataModel = dataSource.get(name).getDataSource();
			// now totally remove the dataModel
			if (dataSourceStorage.getCounter() == 1)
			{
				dataSourceStorage.decrementCounter();
				// we only remove if the corresponding remove indicator is true
				// (which it is per default)
				// in special cases this should avoid unloading the database
				// when the counter comes to 0
				if (dataSourceStorage.isRemoveIfCounterZero())
					dataModel.removeDataSource();
				dataSource.remove(name);
				dataSourceStorage = null;
				dataModel = null;
			}
			else
			{
				dataSourceStorage.decrementCounter();
			}
			return dataModel;
		}
		else
		{
			throw new OpenTMSException(name, "Data model does not exist",
					OpenTMSConstants.OpenTMS_DATAMODEL_NOTFOUND_ERROR);
		}
	}

	/**
	 * test simple test method for generating DataModelInstances
	 */
	public static void test()
	{
		try
		{

			final Vector<String> tmxDatabases = de.folt.models.datamodel.DataSourceConfigurations.getOpenTMSDatabases();// getOpenTMSDatabasesWithType();
			int size = 0;
			if (tmxDatabases != null)
			{
				size = tmxDatabases.size();
			}
			else
				return;

			Random random = new Random();
			System.out.println("Open Data Source Instances");
			for (int i = 0; i < size; i++)
			{
				int irand = random.nextInt(5);
				String datasourcename = tmxDatabases.get(i);
				if (irand == 1)
				{
					DataSource datasource = DataSourceInstance.createInstance(datasourcename);
					if (datasource == null)
					{
						System.out.println(i + ": " + "Error: " + datasourcename);
						continue;
					}
					System.out.println(i + ": " + datasource.getDataSourceName() + datasource.getDataSourceType() + " "
							+ DataSourceInstance.getCounter(datasourcename));
				}
				else
				{
					System.out.println(i + ": " + "Not used: " + datasourcename);
				}
			}

			System.out.println("Re Open Data Source Instances");
			String[] inst = DataSourceInstance.getAllDataSourceInstanceNames();

			for (int k = 0; k < 10; k++)
			{
				for (int i = 0; i < inst.length; i++)
				{
					System.out.println(i + ": " + inst[i]);
					if (random.nextBoolean())
					{
						DataSource datasource = DataSourceInstance.createInstance(inst[i]);
						System.out.println(k + ": " + i + ": " + datasource.getDataSourceName()
								+ datasource.getDataSourceType() + " " + DataSourceInstance.getCounter(inst[i]));
					}
				}
			}

			System.out.println("Remove Data Source Instances");
			for (int i = 0; i < inst.length; i++)
			{
				DataSource datasource = DataSourceInstance.removeInstance(inst[i]);
				System.out.println(i + ": " + datasource.getDataSourceName() + datasource.getDataSourceType() + " "
						+ DataSourceInstance.getCounter(inst[i]));
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
     * 
     */
	private DataSourceInstance()
	{
		super();
	}
}
