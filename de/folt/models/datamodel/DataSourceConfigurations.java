/*
 * Created on 17.03.2009
 * 
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.models.datamodel;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.JDOMParseException;
import org.jdom.output.XMLOutputter;

import de.folt.models.documentmodel.document.XmlDocument;
import de.folt.util.OpenTMSProperties;

/**
 * 
 * This class implements DataSourceConfigurations. DataSourceConfigurations
 * basically are a list of different DataSources. DataSources are added or
 * deleted when the DataSource createDataSource method is called. The class
 * supplied various methods adding, deleting, retrieving DataSources. The data
 * sources are stored in an xml file.
 * 
 * @author klemens
 * 
 */
public class DataSourceConfigurations extends XmlDocument
{

	/**
     * 
     */
	private static final long	serialVersionUID	= 1722690890847940292L;

	/**
	 * createDataSourceConfiguration create a new data source configuration file
	 * 
	 * @param pathName
	 *            the full path name of the data source configuration
	 */
	public static boolean createDataSourceConfiguration(String pathName)
	{
		File f = new File(pathName);
		if (f.exists())
			return false;

		XmlDocument doc = new XmlDocument();
		doc.setXmlDocumentName(pathName);
		Element dataSourceConfig = new Element("DataSourceConfigurations");
		dataSourceConfig.setAttribute("creator", de.folt.util.OpenTMSSupportFunctions.getCurrentUser());
		dataSourceConfig.setAttribute("creationDate", de.folt.util.OpenTMSSupportFunctions.getDateString());
		doc.setDocument(new Document());
		doc.getDocument().setRootElement(dataSourceConfig);
		doc.saveToXmlFile();

		return false;
	}

	/**
	 * createDataSourceConfiguration create a new data source configuration file
	 * 
	 * @param fileName
	 *            the file name of the data source configuration
	 * @param directory
	 *            the directory of the data source configuration
	 */
	public static boolean createDataSourceConfiguration(String fileName, String directory)
	{
		return createDataSourceConfiguration(directory + "/" + fileName);
	}

	/**
	 * getConfigurationFileName get the configuration file name based on a given
	 * string. Methods checks if file exists; if not check for
	 * getDefaultDataSourceConfigurationsFileName() and
	 * OpenTMSProperties.getInstance
	 * ().getOpenTMSProperty("dataSourceConfigurationsFile")
	 * 
	 * @param message
	 *            the message containing the proposed name as key / value pair
	 *            for message.get("dataSourceConfigurationsFile");
	 * @return the dataSourceConfigurationsFile name or null if not found
	 */
	public static String getConfigurationFileName(Hashtable<String, Object> message)
	{
		String dataSourceConfigurationsFile = (String) message.get("dataSourceConfigurationsFile");
		return getConfigurationFileName(dataSourceConfigurationsFile);
	}

	/**
	 * getConfigurationFileName get the configuration file name based on a given
	 * string. Methods checks if file exists; if not check for
	 * getDefaultDataSourceConfigurationsFileName() and
	 * OpenTMSProperties.getInstance
	 * ().getOpenTMSProperty("dataSourceConfigurationsFile")
	 * 
	 * @param dataSourceConfigurationsFile
	 *            the proposed configuration file name
	 * @return the dataSourceConfigurationsFile name or null if not found
	 */
	public static String getConfigurationFileName(String dataSourceConfigurationsFile)
	{
		BasicDataSource sqldatasource = new BasicDataSource();
		if (dataSourceConfigurationsFile == null)
			dataSourceConfigurationsFile = sqldatasource.getDefaultDataSourceConfigurationsFileName();

		File fhd = new File(dataSourceConfigurationsFile);
		if (!fhd.exists())
		{
			if (dataSourceConfigurationsFile == null)
				dataSourceConfigurationsFile = OpenTMSProperties.getInstance().getOpenTMSProperty(
						"dataSourceConfigurationsFile");
			if (dataSourceConfigurationsFile == null)
			{
				dataSourceConfigurationsFile = sqldatasource.getDefaultDataSourceConfigurationsFileName();
			}

			if (dataSourceConfigurationsFile == null)
			{
				return null;
			}

		}
		return dataSourceConfigurationsFile;
	}

	/**
	 * @return the defaultDataSourceConfigurationsFileName
	 */
	public static String getDefaultDataSourceConfigurationsFileName()
	{
		String name = de.folt.util.OpenTMSProperties.getInstance().getOpenTMSProperty("dataSourceConfigurationsFile");
		// System.out.println("getDefaultDataSourceConfigurationsFileName=" +
		// name);
		if ((name != null) && (!name.equals("")))
		{
			File f = new File(name);
			if (f.exists())
				return name;
		}
		return null;
	}

	/**
	 * getOpenTMSDatabases returns all the existing OpenTMS data sources based
	 * on the OpenTMS Property "dataSourceConfigurationsFile"<br>
	 * It uses the function String configfile =
	 * OpenTMSProperties.getInstance().getOpenTMSProperty
	 * ("dataSourceConfigurationsFile"); to determine the configuration file
	 * 
	 * @return a sorted vector with all the names of the data sources.
	 */
	public static Vector<String> getOpenTMSDatabases()
	{
		Vector<String> vec = new Vector<String>();
		try
		{
			// just for the configuration
			String configfile = OpenTMSProperties.getInstance().getOpenTMSProperty("dataSourceConfigurationsFile");
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

	/**
	 * getOpenTMSDatabasesWithType returns all the existing OpenTMS data sources
	 * together with its type based on the OpenTMS Property
	 * "dataSourceConfigurationsFile"<br>
	 * It uses the function String configfile =
	 * OpenTMSProperties.getInstance().getOpenTMSProperty
	 * ("dataSourceConfigurationsFile"); to determine the configuration file<br>
	 * 
	 * <pre>
	 * Example where tmxDatabases is a Vector(String[])
	 * String name = tmxDatabases.get(i)[0];
	 * String type = tmxDatabases.get(i)[1];
	 * </pre>
	 * 
	 * @return a vector of a two dimensional array where the first element
	 *         contains the name and the second the type of the data sources.
	 */
	public static Vector<String[]> getOpenTMSDatabasesWithType()
	{
		Vector<String[]> vec = new Vector<String[]>();
		try
		{
			// just for the configuration
			String configfile = OpenTMSProperties.getInstance().getOpenTMSProperty("dataSourceConfigurationsFile");
			if (configfile != null)
			{
				DataSourceConfigurations dsconfig = new DataSourceConfigurations(configfile);
				String names[] = dsconfig.getDataSources();
				if (names != null)
				{
					for (int i = 0; i < names.length; i++)
					{
						String[] param = new String[6];
						param[0] = names[i];
						param[1] = dsconfig.getDataSourceType(names[i]);
						param[2] = dsconfig.getProperty(names[i], "sync");
						if ((param[2] == null) || (param[2].equals("")))
							param[2] = "false";
						param[3] = dsconfig.getProperty(names[i], "user-id");
						param[4] = dsconfig.getProperty(names[i], "sync-user-id-list");
						param[5] = dsconfig.getDataSourceCreator(names[i]);
						vec.add(param);
					}
					Collections.sort(vec, new Comparator<String[]>()
					{
						public int compare(String[] o1, String[] o2)
						{
							return (o1[0]).compareTo(o2[0]);
						}
					});
				}
			}

			return vec;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return vec;
		}
	}

	/**
	 * main Parameters: <datasource> check if data source exists<br>
	 * -list - list all data sources from
	 * getDefaultDataSourceConfigurationsFileName<br>
	 * -remove <datasource> - removes the data source<br>
	 * if getDefaultDataSourceConfigurationsFileName file name does not exist it
	 * will be created and method returns
	 * 
	 * @param args
	 * @throws JDOMParseException
	 */
	public static void main(String[] args)
	{
		String configFile = getDefaultDataSourceConfigurationsFileName();
		if (configFile != null)
		{
			File f = new File(configFile);
			if (!f.exists())
			{
				DataSourceConfigurations.createDataSourceConfiguration(configFile);
				System.out.println("New configuration file: " + configFile);
				return;
			}
		}

		DataSourceConfigurations config = new DataSourceConfigurations(configFile);
		if (args.length == 1)
		{
			// check if config exists
			if (args[0].equalsIgnoreCase("-list"))
			{
				System.out.println("Data sources in: " + configFile);
				String[] datasources = config.getDataSources();
				for (int i = 0; i < datasources.length; i++)
				{
					Element configDataSource = config.getConfiguration(datasources[i]);
					System.out.println(i + "\t" + datasources[i] + "\t"
							+ configDataSource.getAttributeValue("datasourcetype"));
				}
				return;
			}
			String datasource = args[0];
			System.out.println("Status: " + datasource + " = " + config.bDataSourceExistsInConfiguration(datasource));
			return;
		}
		if (args.length == 2)
		{
			if (args[0].equalsIgnoreCase("-remove"))
			{
				String datasource = args[1];
				System.out.println("Status remove: " + datasource + " = " + config.removeConfiguration(datasource));
				return;
			}
		}

	}

	/**
	 * main
	 * 
	 * @param args
	 * @throws JDOMParseException
	 */
	public static void mainold(String[] args) throws JDOMParseException
	{
		createDataSourceConfiguration(args[0]);
		DataSourceConfigurations test = new DataSourceConfigurations(args[0]);
		test.addConfiguration("mynewConfig");
		test.saveToXmlFile();
	}

	private String	configurationFileName	= null;

	private Element	root					= null;

	/**
     * 
     */
	public DataSourceConfigurations()
	{
		// TODO Auto-generated constructor stub
	}

	/**
	 * Creates DataSourceConfigurations based on a given configurationFileName.
	 * Basically it reads the associated xml file and creates an XMLDocument
	 * from that.
	 * 
	 * @param configurationFileName
	 *            the configuration file to use
	 */
	public DataSourceConfigurations(String configurationFileName)
	{
		super();
		this.configurationFileName = configurationFileName;
		try
		{
			Document result = this.loadXmlFile(configurationFileName);
			if (result == null)
			{
				root = null;
				return;
			}
			root = this.getDocument().getRootElement();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			root = null;
			return;
		}
	}

	/**
	 * addConfiguration add a new configuration with the name configName
	 * 
	 * @param configName
	 *            the name of the unique configuration
	 * @return the new Element or null if the configuration already exists
	 */
	public Element addConfiguration(String configName)
	{
		Element config = new Element("DataSourceConfiguration");
		if (getConfiguration(configName) != null)
			return null;
		config.setAttribute("name", configName);
		config.setAttribute("creator", de.folt.util.OpenTMSSupportFunctions.getCurrentUser());
		config.setAttribute("creationDate", de.folt.util.OpenTMSSupportFunctions.getDateString());
		root.addContent(config);
		return config;
	}

	/**
	 * addConfiguration add a new configuration with the name configName, a
	 * dataSourceType and the dataSourceProperties. It copies all the key value
	 * from dataSourceProperties to the configuration (if they are of type
	 * String).
	 * 
	 * @param configName
	 *            the name of the unique configuration
	 * @param dataSourceProperties
	 *            the dataSourceProperties of the unique configuration
	 * @param dataSourceType
	 *            the type of the data source
	 * @return the new Element or null if the configuration already exists
	 */
	public Element addConfiguration(String configName, String dataSourceType, DataSourceProperties dataSourceProperties)
	{
		Element config = new Element("DataSourceConfiguration");
		if (getConfiguration(configName) != null)
			return null;
		config.setAttribute("name", configName);
		config.setAttribute("creator", de.folt.util.OpenTMSSupportFunctions.getCurrentUser());
		config.setAttribute("creationDate", de.folt.util.OpenTMSSupportFunctions.getDateString());
		config.setAttribute("datasourcetype", dataSourceType);

		try
		{
			Enumeration<String> keys = dataSourceProperties.keys();
			while (keys.hasMoreElements())
			{
				String key = keys.nextElement();
				Object value = dataSourceProperties.get(key);
				if (value.getClass().getName().equals("java.lang.String"))
				{
					Element el = new Element("property");
					el.setAttribute("name", key);
					el.setText((String) value);
					config.addContent(el);
				}
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		root.addContent(config);
		return config;
	}

	/**
	 * addPropertiesToDataModelProperties add all the properties stored to the supplied DataSourceProperties
	 * 
	 * @param name
	 *            name of the data source
	 * @param dataModelProperties
	 *            the DataSourceProperties which should be enhanced with the
	 *            properties
	 */
	@SuppressWarnings("unchecked")
	public void addPropertiesToDataModelProperties(String name, DataSourceProperties dataModelProperties)
	{
		Element elem = getConfiguration(name);
		if (elem == null)
			return;
		List<Element> elements = elem.getChildren("property");
		for (int i = 0; i < elements.size(); i++)
		{
			Element el = elements.get(i);
			String att = el.getAttributeValue("name");
			if (att != null)
			{
				dataModelProperties.put(att, el.getText());
			}
		}
	}

	/**
	 * bDataSourceExistsInConfiguration check if a specific data source give by
	 * its name is registered in the database configuration
	 * 
	 * @param configName
	 *            the name of data source
	 * @return true if it exists otherwise false
	 */
	@SuppressWarnings("unchecked")
	public boolean bDataSourceExistsInConfiguration(String configName)
	{
		List<Element> list = (List<Element>) root.getChildren("DataSourceConfiguration");
		for (int i = 0; i < list.size(); i++)
		{
			Element element = list.get(i);
			if (element.getAttributeValue("name").equals(configName))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * getConfiguration returns a dataSourceConfiguration Element based on its
	 * name
	 * 
	 * @param configName
	 *            the name of the configuration to find
	 * @return the Element configuration or null if not found
	 */
	@SuppressWarnings("unchecked")
	public Element getConfiguration(String configName)
	{
		if (root == null)
			return null;
		List<Element> list = (List<Element>) root.getChildren("DataSourceConfiguration");
		for (int i = 0; i < list.size(); i++)
		{
			Element element = list.get(i);
			String name = element.getAttributeValue("name");
			if (name.equals(configName))
			{
				return element;
			}
		}
		return null;
	}

	/**
	 * Get the name of file associated with the DataSourceConfigurations.
	 * 
	 * @return the configurationFileName
	 */
	public String getConfigurationFileName()
	{
		return configurationFileName;
	}

	/**
	 * getConfigurationIndex gets the configuration index
	 * 
	 * @param configName
	 *            the name of the configuration to find
	 * @return the index of the configuration (0..n) or -1 if not found
	 */
	@SuppressWarnings("unchecked")
	public int getConfigurationIndex(String configName)
	{
		List<Element> list = (List<Element>) root.getChildren("DataSourceConfiguration");
		for (int i = 0; i < list.size(); i++)
		{
			Element element = list.get(i);
			if (element.getAttributeValue("name").equals(configName))
			{
				return i;
			}
		}
		return -1;
	}

	/**
	 * get the creation date of this data source
	 * 
	 * @param name
	 * @return
	 */
	public String getDataSourceCreationDate(String name)
	{
		Element elem = this.getConfiguration(name);
		if (elem == null)
			return null;

		return elem.getAttributeValue("creationDate");
	}

	/**
	 * get the creator of this data source
	 * 
	 * @param name
	 * @return
	 */
	public String getDataSourceCreator(String name)
	{
		Element elem = this.getConfiguration(name);
		if (elem == null)
			return null;

		String value = elem.getAttributeValue("creator");
		
		if ((value == null) || value.equals(""))
			value = elem.getAttributeValue("sync-user-id");

		if ((value == null) || value.equals(""))
			return elem.getAttributeValue("user-id");
		return value;
	}
	

	/**
	 * getDataSources get all the data sources names in the configuration
	 * 
	 * @return a string array of data source names / null if root of data source
	 *         configuration is null (actually means data source configuration
	 *         does not exist)
	 */
	@SuppressWarnings("unchecked")
	public String[] getDataSources()
	{
		if (root == null)
		{
			return null;
		}
		List<Element> list = (List<Element>) root.getChildren("DataSourceConfiguration");
		String names[] = new String[list.size()];
		for (int i = 0; i < list.size(); i++)
		{
			Element element = list.get(i);
			String name = element.getAttributeValue("name");
			names[i] = name;
		}
		return names;
	}

	/**
	 * @param bSync
	 *            true / false if true sync databases will be returned in case
	 *            of false the non sync databases
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String[] getDataSources(boolean bSync)
	{
		if (root == null)
		{
			return null;
		}
		List<Element> list = (List<Element>) root.getChildren("DataSourceConfiguration");
		Vector<String> ds = new Vector<String>();
		for (int i = 0; i < list.size(); i++)
		{
			Element element = list.get(i);
			String configName = element.getAttributeValue("name");
			String sync = getProperty(configName, "sync");
			try
			{
				boolean bSyncDS = Boolean.parseBoolean(sync);
				if (bSyncDS == bSync)
				{
					ds.add(configName);
				}
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		String names[] = new String[ds.size()];
		for (int i = 0; i < ds.size(); i++)
		{
			names[i] = ds.get(i);
		}

		return names;
	}

	/**
	 * getDataSources get all the data sources in the configuration of a
	 * specific data source type; e.g.
	 * datasourcetype="de.folt.models.datamodel.sql.OpenTMSSQLDataSource"
	 * 
	 * @param dataSourceType
	 * @return a string array of data source names of specific types
	 */
	@SuppressWarnings("unchecked")
	public String[] getDataSources(String dataSourceType)
	{
		List<Element> list = (List<Element>) root.getChildren("DataSourceConfiguration");
		Vector<String> names = new Vector<String>();
		for (int i = 0; i < list.size(); i++)
		{
			Element element = list.get(i);
			String name = element.getAttributeValue("name");
			String type = element.getAttributeValue("datasourcetype");
			if (type.equals(dataSourceType))
			{
				names.add(name);
			}
		}

		if (names.size() == 0)
			return null;

		String[] namen = new String[names.size()];
		for (int i = 0; i < names.size(); i++)
		{
			namen[i] = names.get(i);
		}
		names = null;
		return namen;
	}

	/**
	 * @param dataSourceType
	 *            the data source types to search for
	 * @param bSync
	 *            true or false depending if sync or non snc databases should be
	 *            returned
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String[] getDataSources(String dataSourceType, boolean bSync)
	{
		List<Element> list = (List<Element>) root.getChildren("DataSourceConfiguration");
		Vector<String> names = new Vector<String>();
		for (int i = 0; i < list.size(); i++)
		{
			Element element = list.get(i);
			String name = element.getAttributeValue("name");
			String type = element.getAttributeValue("datasourcetype");
			String sync = getProperty(name, "sync");
			try
			{
				boolean bSyncDS = Boolean.parseBoolean(sync);
				if (!bSyncDS == bSync)
					continue;
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (type.equals(dataSourceType))
			{
				names.add(name);
			}
		}

		if (names.size() == 0)
			return null;

		String[] namen = new String[names.size()];
		for (int i = 0; i < names.size(); i++)
		{
			namen[i] = names.get(i);
		}
		names = null;
		return namen;
	}

	/**
	 * Method returns for all data source name, type and sync property
	 * 
	 * @return a vector of string arrays
	 */
	public Vector<String[]> getDataSourcesWithType()
	{
		@SuppressWarnings("unchecked")
		List<Element> list = (List<Element>) root.getChildren("DataSourceConfiguration");
		Vector<String[]> names = new Vector<String[]>();
		for (int i = 0; i < list.size(); i++)
		{
			Element element = list.get(i);
			String name = element.getAttributeValue("name");
			String type = element.getAttributeValue("datasourcetype");
			String sync = getProperty(name, "sync");
			String[] namearray = { name, type, sync };
			names.add(namearray);
		}

		return names;
	}

	/**
	 * getDataSourceType returns the type of a data source stored in a data
	 * source configuration file
	 * 
	 * @param name
	 *            the data sorce name
	 * @return the data source type
	 */
	public String getDataSourceType(String name)
	{
		Element elem = this.getConfiguration(name);
		if (elem == null)
			return null;

		return elem.getAttributeValue("datasourcetype");
	}

	/**
	 * getProperty returns a specific data configuration property based on the
	 * name <property name="connection.password">heartsome</property>
	 * 
	 * @param configName
	 *            the configuration name
	 * @param property
	 *            the property name
	 * @return the value of the property or null if not found
	 */
	@SuppressWarnings("unchecked")
	public String getProperty(String configName, String property)
	{
		Element elem = this.getConfiguration(configName);
		if (elem == null)
			return null;
		List<Element> elements = elem.getChildren("property");
		for (int i = 0; i < elements.size(); i++)
		{
			Element el = elements.get(i);
			String att = el.getAttributeValue("name");
			if (att != null)
			{
				if (att.equals(property))
					return el.getText();
			}
		}
		return null;
	}

	/**
	 * removeConfiguration remove a configuration from the data source
	 * configurations
	 * 
	 * @param databasename
	 * @return true if removed otherwise false
	 */

	public boolean removeConfiguration(String configName)
	{
		Element el = getConfiguration(configName);
		return root.removeContent(el);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.folt.models.documentmodel.document.XmlDocument#saveToXmlFile(java.
	 * lang.String)
	 */
	@Override
	public boolean saveToXmlFile(String filename)
	{
		if (xmlOutputter == null)
			xmlOutputter = new XMLOutputter();
		if (xmlOutputter != null)
			this.xmlOutputter.setFormat(org.jdom.output.Format.getPrettyFormat());
		return super.saveToXmlFile(filename);
	}

	/**
	 * Explicitly set the name if DataSourceConfigurations
	 * 
	 * @param configurationFileName
	 *            the configurationFileName to set
	 */
	public void setConfigurationFileName(String configurationFileName)
	{
		this.configurationFileName = configurationFileName;
	}

	/**
	 * set properties of a data source / clear old properties based on given dataModelProperties
	 * 
	 * @param name
	 * @param dataModelProperties
	 */
	public void setDataModelProperties(String name, DataSourceProperties dataModelProperties)
	{
		try
		{
			Element elem = getConfiguration(name);
			if (elem == null)
				return;

			elem.removeContent();

			Enumeration<String> enumdataporps = dataModelProperties.keys();
			while (enumdataporps.hasMoreElements())
			{
				String key = enumdataporps.nextElement();
				String newValue = (String) dataModelProperties.get(key);
				Element elnew = new Element("property");
				elnew.setText(newValue);
				elnew.setAttribute(key, newValue);
			}
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void updateDataModelProperties(String name, String key, String value)
	{
		try
		{
			Element elem = getConfiguration(name);
			if (elem == null)
				return;

			boolean bUpdated = false;
			@SuppressWarnings("rawtypes")
			List keyelems = elem.getChildren("property");
			for (int i = 0; i < keyelems.size(); i++)
			{
				Element keyelem = (Element) keyelems.get(i);
				if (keyelem != null)
				{
					String attnamer = keyelem.getAttributeValue("name");
					if (attnamer.equals(key))
					{
						keyelem.setText(value);
						bUpdated = true;
					}
				}
			}

			if (bUpdated == false)
			{
				Element newElem = new Element("property");
				newElem.setAttribute("name", key);
				newElem.setText(value);
				elem.addContent(newElem);
			}
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
