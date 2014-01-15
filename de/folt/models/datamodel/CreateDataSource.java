package de.folt.models.datamodel;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import de.folt.util.OpenTMSProperties;

public class CreateDataSource
{
	/**
	 * @param dataSourceName
	 * @param dataSourceType
	 * @param dataSourceGenericType
	 * @param dataSourceServer
	 * @param dataSourcePort
	 * @param dataSourceUser
	 * @param dataSourcePassword
	 * @param encoding
	 * @param sourceLanguage
	 * @param targetLanguage
	 * @param codepage
	 * @return
	 */
	public static Vector<String> createDataSource(String dataSourceName,
			String dataSourceType, String dataSourceGenericType,
			String dataSourceServer, String dataSourcePort,
			String dataSourceUser, String dataSourcePassword, String encoding,
			String sourceLanguage, String targetLanguage, String codepage)
	{
		Hashtable<String, Object> param = new Hashtable<String, Object>();
		if (dataSourceGenericType
				.equals(de.folt.models.datamodel.tmxfile.TmxFileDataSource.class
						.getName()))
		{
			dataSourceType = "tmx";
			param.put("dataSourceType", dataSourceType);
			param.put("dataSourceName", dataSourceName);
		}
		else if (dataSourceGenericType
				.equals(de.folt.models.datamodel.xlifffile.XliffFileDataSource.class
						.getName()))
		{
			dataSourceType = "xliff";
			param.put("dataSourceType", dataSourceType);
			param.put("dataSourceName", dataSourceName);
		}
		else if (dataSourceGenericType
				.equals(de.folt.models.datamodel.tbxfile.TbxFileDataSource.class
						.getName()))
		{
			dataSourceType = "tbx";
			param.put("dataSourceType", dataSourceType);
			param.put("dataSourceName", dataSourceName);
		}
		else if (dataSourceGenericType
				.equals(de.folt.models.datamodel.trados.TradosTMDataSource.class
						.getName()))
		{
			dataSourceType = "trados";
			param.put("dataSourceType", dataSourceType);
			param.put("codepage", encoding);
			param.put("dataSourceName", dataSourceName);
		}
		else if (dataSourceGenericType
				.equals(com.araya.OpenTMS.ArayaDataSource.class.getName()))
		{
			dataSourceType = "Araya";
			param.put("dataSourceType", dataSourceType);
			param.put("dataSourceName", dataSourceName);
			// here we must call now a dialog to choose all the data
			// source to add
			// String propertiesFile =
			// OpenTMSProperties.getInstance().getOpenTMSProperty("ArayaPropertiesFile");
			// EMXProperties.setPropfileName(propertiesFile);
			// String arayaDatabaseListFile =
			// EMXProperties.getInstance().getEMXProperty("database.list");
			// com.araya.tm.DatabaseSelector chooser = new
			// com.araya.tm.DatabaseSelector(arayaDatabaseListFile);
			// chooser.show();
			// String data = chooser.getText();
			param.put("dataSourceName", dataSourceName);
		}
		else
		{
			@SuppressWarnings("rawtypes")
			Class dataSourceClass = null;
			try
			{
				dataSourceClass = Class.forName(dataSourceGenericType);

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
						.getOpenTMSProperty("hibernateConfigurationsDirectory");
				param.put("hibernateConfigFile", directory + "/"
						+ dataSourceType);
				param.put("dataSourceType", directory + "/" + dataSourceType); // MySQL
				param.put("dataSourceServer", dataSourceServer); // localhost
				param.put("dataSourcePort", dataSourcePort); // 2341
				param.put("dataSourceUser", dataSourceUser); // sa
				param.put("dataSourcePassword", dataSourcePassword); // my
				// password
			}
		}

		param.put("dataSourceName", dataSourceName); // folttm
		param.put("targetLanguage", targetLanguage);
		param.put("sourceLanguage", sourceLanguage);
		param.put("codepage", codepage);

		Vector<String> result = de.folt.rpc.connect.Interface
				.runCreateDB(param);

		return result;
	}

	/**
	 * @param dataSourceName
	 * @param parameter
	 * @return
	 */
	public static Vector<String> createDataSource(String dataSourceName,
			String parameter)
	{
		Hashtable<String, Object> param = new Hashtable<String, Object>();
		String[] explode = parameter.split(";");
		for (int i = 0; i < explode.length; i++)
		{
			String[] keyvalue = explode[i].split("=");
			if (keyvalue.length == 1)
			{
				param.put(keyvalue[0], "");
			}
			else
			{
				param.put(keyvalue[0], keyvalue[1]);
			}

		}

		Enumeration<String> paramenum = param.keys();
		while (paramenum.hasMoreElements())
		{
			String key = paramenum.nextElement();
			System.out.println("Key: \"" + key + "\" value: \""
					+ param.get(key) + "\"");
		}

		Vector<String> result = de.folt.rpc.connect.Interface
				.runCreateDB(param);
		return result;
	}

	public static Vector<String> createDataSource(String dataSourceName,
			HashMap<String, String> paramHash, String parameter)
	{
		Hashtable<String, Object> param = new Hashtable<String, Object>();
		String[] explode = parameter.split(";");
		for (int i = 0; i < explode.length; i++)
		{
			String[] keyvalue = explode[i].split("=");
			if (keyvalue.length == 1)
			{
				param.put(keyvalue[0], "");
			}
			else
			{
				param.put(keyvalue[0], keyvalue[1]);
			}

		}
				
		Iterator<Map.Entry<String, String>> it = paramHash.entrySet().iterator();
	    while (it.hasNext()) 
	    {
	        Map.Entry<String, String> pairs = (Map.Entry<String, String>)it.next();
	        param.put(pairs.getKey(), pairs.getValue());
	    }

		Enumeration<String> paramenum = param.keys();
		while (paramenum.hasMoreElements())
		{
			String key = paramenum.nextElement();
			System.out.println("Key: \"" + key + "\" value: \""
					+ param.get(key) + "\"");
		}

		Vector<String> result = de.folt.rpc.connect.Interface
				.runCreateDB(param);
		return result;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// paramHash.put("parameter",
		// "user=root;password=;datasource=de.folt.models.datamodel.sql.OpenTMSSQLDataSource;type=hibernate.mysql.cfg.xml");
		String parameter = "dataSourceName=myminitest10;dataSourceGenericType=de.folt.models.datamodel.sql.OpenTMSSQLDataSource;dataSourceType=hibernate.mysql.cfg.xml";
		if (args.length > 0)
			parameter = args[0];
		parameter = parameter
				+ ";dataSourceServer=localhost;dataSourcePort=3306;dataSourceUser=root;dataSourcePassword=";
		parameter = parameter + ";user-id=" + "klemens" + ";user-id-list="
				+ "klemens,maria,stefan,michael" + ";sync=true";
		createDataSource("myminitest10", parameter);

		int a = 1;
		if (a == 2)
		{
			String dataSourceConfigurationsFile = null;
			dataSourceConfigurationsFile = DataSourceConfigurations
					.getConfigurationFileName(dataSourceConfigurationsFile);
			if (dataSourceConfigurationsFile != null)
			{
				DataSourceConfigurations dsconfig = new DataSourceConfigurations(
						dataSourceConfigurationsFile);
				String[] syncDataSources = dsconfig.getDataSources(true);
				String ds = "";
				for (int i = 0; i < syncDataSources.length; i++)
				{
					if (i != (syncDataSources.length - 1))
						ds = ds + syncDataSources[i] + ",";
					else
						ds = ds + syncDataSources[i];
				}
				System.out.println("Sync DBs:" + ds);
			}
		}
	}

}
