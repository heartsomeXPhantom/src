/*
 * Created on 19.03.2009
 * 
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.rpc.connect;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Observer;
import java.util.Vector;

import com.araya.eaglememex.util.LogPrint;

import de.folt.models.datamodel.BasicDataSource;
import de.folt.models.datamodel.DataSource;
import de.folt.models.datamodel.DataSourceConfigurations;
import de.folt.models.datamodel.DataSourceInstance;
import de.folt.models.datamodel.DataSourceProperties;
import de.folt.models.datamodel.multipledatasource.MultipleDataSource;
import de.folt.models.documentmodel.document.XmlDocument;
import de.folt.util.OpenTMSException;
import de.folt.util.OpenTMSInitialJarFileLoader;
import de.folt.util.OpenTMSProperties;
import de.folt.util.OpenTMSSupportFunctions;

/**
 * @author klemens
 * 
 */
public class Interface
{
	/**
	 * fillParam
	 * 
	 * @param hash
	 * @param type
	 * @return
	 */
	/**
	 * fillParam
	 * 
	 * @param message
	 * @param type
	 * @return
	 */
	public static String fillParam(Hashtable<String, Object> message, String type)
	{
		String value = (String) message.get(type);
		if (value == null)
			return "";
		else
			return value;
	}

	/**
	 * getOpenTMSVersion
	 * 
	 * @param message
	 *            (empty)
	 * @return Vector: 0 = success 1: version info 2: current log file 3: start
	 *         log string
	 */
	public static Vector<String> getOpenTMSVersion(Hashtable<String, Object> message)
	{
		Vector<String> vec = new Vector<String>();
		try
		{
			vec.add(0, "0");
			String text = de.folt.rpc.webserver.OpenTMSServer.getVersion();
			if (text == null)
				text = "getVersion=null";
			vec.add(1, text);
			text = de.folt.util.OpenTMSLogger.returnLogFileName();
			if (text == null)
				text = "returnLogFileName=null";
			vec.add(2, text);
			text = de.folt.rpc.webserver.OpenTMSServer.getStartLogString();
			if (text == null)
				text = "getStartLogString=null";
			vec.add(3, text);
			text = de.folt.constants.OpenTMSVersionConstants.getAllVersionsAsString();
			if (text == null)
				text = "getAllVersionsAsString=null";
			vec.add(4, text);
			return vec;
		}
		catch (Exception ex)
		{
			vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_FAILURE + "");
			vec.add(ex.getMessage());
			ex.printStackTrace();
			return vec;
		}
	}

	/**
	 * main
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		String result = "";
		if (args.length == 0)
			return;
		if (args.length > 1)
		{
			result = runInterfaceMethod(args);
		}
		else
			result = runInterfaceMethod(args[0]);
		System.out.println("Start Result:\n" + result + "\nEnd Result");
	}

	/**
	 * runBackConvertDocument
	 * 
	 * @param message
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Vector<String> runBackConvertDocument(Hashtable<String, Object> message)
	{
		Vector<String> vec = new Vector<String>();
		try
		{
			System.out.println("runBackConvertDocument...");
			// file parameters
			// file name params
			// wk 14.11.2004 - support different values for the input file
			// FILE ... indicates content is in the file name specified
			// ... indicates inputFileName is the which contains
			// the contents
			// ZIP ... contents is zipped
			// MIME ... contents is MIME format
			String inputFileName = de.folt.util.OpenTMSSupportFunctions.fillParam(message, "sourceDocument");
			String sklFileName = de.folt.util.OpenTMSSupportFunctions.fillParam(message, "sklDocument");
			String outputBackFileName = de.folt.util.OpenTMSSupportFunctions
					.fillParam(message, "backConvertedDocument");

			System.out.println("sourceDocument=           \"" + inputFileName + "\"");
			System.out.println("sklDocument=               \"" + sklFileName + "\"");
			System.out.println("backConvertedDocument=     \"" + outputBackFileName + "\"");

			message.put("xliffDocument", de.folt.util.OpenTMSSupportFunctions.fillParam(message, "sourceDocument"));
			message.put("translatedDocument", de.folt.util.OpenTMSSupportFunctions.fillParam(message, "sourceDocument"));
			message.put("segDocument", de.folt.util.OpenTMSSupportFunctions.fillParam(message, "sourceDocument"));

			// language and encoding
			String srcLan = de.folt.util.OpenTMSSupportFunctions.fillParam(message, "sourceDocumentLanguage");
			String trgLan = de.folt.util.OpenTMSSupportFunctions.fillParam(message, "targetDocumentLanguage");
			String srcEnc = de.folt.util.OpenTMSSupportFunctions.fillParam(message, "sourceDocumentEncoding");
			String trgEnc = de.folt.util.OpenTMSSupportFunctions.fillParam(message, "targetDocumentEncoding");
			System.out.println("sourceDocumentLanguage=     \"" + srcLan + "\"");
			System.out.println("targetDocumentLanguage=     \"" + trgLan + "\"");
			System.out.println("sourceDocumentEncoding=     \"" + srcEnc + "\"");
			System.out.println("targetDocumentEncoding=     \"" + trgEnc + "\"");

			String propFile = OpenTMSProperties.getInstance().getOpenTMSProperty("ArayaPropertiesFile");
			message.put("ArayaPropertiesFile", propFile);
			System.out.println("ArayaPropertiesFile=\"" + propFile + "\"");

			File fi = new File(inputFileName);
			if (!fi.exists())
			{
				System.out.println("runBackConvertDocument " + inputFileName + " finished!");
				vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_XLIFFDATASOURCE_FILE_NOTFOUND_ERROR + "");
				vec.add(inputFileName + " does not exist!");
				vec.add(-1 + "");
				return vec;
			}

			int iResult = -1;

			XmlDocument doc = new de.folt.models.documentmodel.document.XmlDocument();
			doc.loadXmlFile(inputFileName);
			String rootname = "";
			if (doc.getDocument() == null)
			{
				;
			}
			else
			{
				rootname = doc.getRootElementName();
				System.out.println("xliffFile(rootname)=  \"" + rootname + "\"");
			}
			String content = "";

			if (rootname.equals("xliff"))
			{
				message.put("openTMSTranslationPhase", "BACK");
				System.out.println("Conversion Phase: " + message.get("openTMSTranslationPhase"));
				@SuppressWarnings("unused")
				Vector<String> retVec = com.araya.OpenTMS.Interface.runConverter(message);
				iResult = 0;
				fi = new File(outputBackFileName);
				if (!fi.exists())
				{
					System.out.println("runBackConvertDocument " + outputBackFileName + " finished!");
					vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_FAILURE + "");
					vec.add(outputBackFileName + " not back converted!");
					vec.add(-1 + "");
					return vec;
				}
				content = de.folt.util.OpenTMSSupportFunctions.copyFileToString(outputBackFileName);
				System.out.println("runBackConvertDocument " + outputBackFileName + " finished!");
				vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_SUCCESS + "");
				vec.add(content);
				vec.add(outputBackFileName + " successfully back converted!");
				vec.add(iResult + "");
				return vec;
			}
			else
			{
				System.out.println("runBackConvertDocument " + inputFileName + " finished!");
				vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_FAILURE + "");
				vec.add(inputFileName + " not back converted (not an xliff file)!");
				vec.add(-1 + "");
				return vec;
			}
		}
		catch (Exception ex)
		{
			vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_FAILURE + "");
			vec.add(ex.getMessage());
			ex.printStackTrace();
			return vec;
		}
	}

	/**
	 * Clean a connection to a datasource
	 * 
	 * @param message
	 * @return
	 */
	public static Vector<String> runCleanDB(Hashtable<String, Object> message)
	{
		Vector<String> vec = new Vector<String>();
		try
		{
			System.out.println("Interface runDeleteDB ");
			String dataSourceName = (String) fillParam(message, "dataSourceName"); // folttm
			System.out.println("dataSourceName=    \"" + dataSourceName + "\"");

			String dataSourceConfigurationsFile = (String) message.get("dataSourceConfigurationsFile");
			dataSourceConfigurationsFile = DataSourceConfigurations
					.getConfigurationFileName(dataSourceConfigurationsFile);
			System.out.println("dataSourceConfigurationsFile=          \"" + dataSourceConfigurationsFile + "\"");

			String dataSourceType = (String) fillParam(message, "dataSourceType");
			if ((dataSourceType == null) || dataSourceType.equals(""))
			{
				if (dataSourceConfigurationsFile != null)
				{
					DataSourceConfigurations dsconfig = new DataSourceConfigurations(dataSourceConfigurationsFile);
					dataSourceType = dsconfig.getDataSourceType(dataSourceName);
					if (dataSourceType == null)
					{
						dataSourceType = "";
						System.out.println("runDeleteDB " + dataSourceName + " finished with error!");
						vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_FAILURE + "");
						vec.add(dataSourceName + " no data source type defined! (dataSourceConfigurationsFile=" + dataSourceConfigurationsFile + ")");
						dataSourceType = "";
						return vec;
					}
				}
				else
				{
					System.out.println("runDeleteDB " + dataSourceName + " finished with error!");
					vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_FAILURE + "");
					vec.add(dataSourceName + " no data source type defined! (dataSourceConfigurationsFile=" + dataSourceConfigurationsFile + ")");
					dataSourceType = "";
					return vec;
				}
			}
			System.out.println("dataSourceType=    \"" + dataSourceType + "\"");

			if (dataSourceType.equalsIgnoreCase("tmx")
					|| dataSourceType.equalsIgnoreCase("de.folt.models.datamodel.tmxfile.TmxFileDataSource"))
			{
				DataSourceProperties dataprop = new DataSourceProperties();
				dataprop.put("dataSourceName", dataSourceName);
				de.folt.models.datamodel.tmxfile.TmxFileDataSource sqlinst = new de.folt.models.datamodel.tmxfile.TmxFileDataSource(
						dataprop);
				sqlinst.cleanDataSource();
				System.out.println("TMX: runCleanDB " + dataSourceName + " finished!");
				vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_SUCCESS + "");
				vec.add(dataSourceName + " (TMX) successfully cleaned!");

			}
			else if (dataSourceType.equalsIgnoreCase("xliff")
					|| dataSourceType.equalsIgnoreCase("de.folt.models.datamodel.xlifffile.XliffFileDataSource"))
			{
				DataSourceProperties dataprop = new DataSourceProperties();
				dataprop.put("dataSourceName", dataSourceName);
				de.folt.models.datamodel.xlifffile.XliffFileDataSource sqlinst = new de.folt.models.datamodel.xlifffile.XliffFileDataSource(
						dataprop);
				sqlinst.cleanDataSource();
				System.out.println("TMX: runCleanDB " + dataSourceName + " finished!");
				vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_SUCCESS + "");
				vec.add(dataSourceName + " (TMX) successfully cleaned!");
			}
			else if (dataSourceType.equalsIgnoreCase("sql")
					|| dataSourceType.equalsIgnoreCase("de.folt.models.datamodel.sql.OpenTMSSQLDataSource"))
			{

				de.folt.models.datamodel.sql.OpenTMSSQLDataSource sqlinst = new de.folt.models.datamodel.sql.OpenTMSSQLDataSource();
				message.put("dataSourceConfigurationsFile", dataSourceConfigurationsFile);
				sqlinst.cleanDataSource();
				System.out.println("TMX: runCleanDB " + dataSourceName + " finished!");
				vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_SUCCESS + "");
				vec.add(dataSourceName + " (TMX) successfully cleaned!");
			}
			else
			{
				// run standard remove method
				DataSource sqlinst = de.folt.models.datamodel.DataSourceInstance.createInstance(dataSourceName);
				if (sqlinst == null)
				{
					System.out.println("deleteDataSource " + dataSourceName + " finished!");
					vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_FAILURE + "");
					vec.add(dataSourceName + " not removed!");
					return vec;
				}
				DataSourceProperties dataprop = new DataSourceProperties();
				dataprop.put("dataSourceName", dataSourceName);
				sqlinst.cleanDataSource();
				System.out.println("TMX: runCleanDB " + dataSourceName + " finished!");
				vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_SUCCESS + "");
				vec.add(dataSourceName + " (TMX) successfully cleaned!");
			}
			return vec;
		}
		catch (Exception ex)
		{
			vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_FAILURE + "");
			vec.add(ex.getMessage());
			ex.printStackTrace();
			return vec;
		}
	}

	/**
	 * runCopyFromDataSource copy a daasource to another datasource
	 * 
	 * @param param
	 *            param.get("fromDataSource") -> param.get("toDataSource")
	 * @return success or failure vector
	 */
	public static Vector<String> runCopyFromDataSource(Hashtable<String, Object> param)
	{
		Vector<String> result = new Vector<String>();
		String fromDataSourceName = (String) param.get("fromDataSource");
		String toDataSourceName = (String) param.get("toDataSource");
		String dataSourceConfigurationsFile = (String) param.get("dataSourceConfigurationsFile");
		dataSourceConfigurationsFile = DataSourceConfigurations.getConfigurationFileName(dataSourceConfigurationsFile);
		LogPrint.println("fromDataSourceName ---> " + fromDataSourceName);
		LogPrint.println("toDataSourceName ---> " + toDataSourceName);
		LogPrint.println("toDataSourceName ---> " + dataSourceConfigurationsFile);
		if (dataSourceConfigurationsFile == null)
		{
			result.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_FAILURE + "");
			result.add(dataSourceConfigurationsFile);
			return result;
		}

		try
		{
			DataSource fromDataSource = DataSourceInstance.createInstance(fromDataSourceName,
					dataSourceConfigurationsFile);
			if (fromDataSource == null)
			{
				result.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_FAILURE + "");
				result.add(fromDataSourceName);
				return result;
			}
			DataSource toDataSource = DataSourceInstance.createInstance(toDataSourceName, dataSourceConfigurationsFile);
			if (toDataSource == null)
			{
				result.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_FAILURE + "");
				result.add(toDataSourceName);
				return result;
			}
			int resultValue = fromDataSource.copyTo(toDataSource);
			toDataSource.bPersist();
			result.add(0 + "");
			result.add(resultValue + "");
			result.add(fromDataSourceName);
			result.add(toDataSourceName);
		}
		catch (Exception e)
		{
			result.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_FAILURE + "");
			result.add(e.getMessage());
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * runCreateDB
	 * 
	 * @param message
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Vector<String> runCreateDB(Hashtable<String, Object> message)
	{
		Vector<String> vec = new Vector<String>();
		try
		{
			System.out.println("Running runCreateDB ");
			String dataSourceName = (String) fillParam(message, "dataSourceName"); // folttm
			String dataSourceType = (String) fillParam(message, "dataSourceType"); // MySQL
			String dataSourceServer = (String) fillParam(message, "dataSourceServer"); // localhost
			String dataSourcePort = (String) fillParam(message, "dataSourcePort"); // 2341
			String dataSourceUser = (String) fillParam(message, "dataSourceUser"); // sa
			@SuppressWarnings("unused")
			String dataSourcePassword = (String) fillParam(message, "dataSourcePassword"); // my
			// password
			String encoding = (String) fillParam(message, "codepage");

			System.out.println("dataSourceName=    \"" + dataSourceName + "\"");
			System.out.println("dataSourceUser=    \"" + dataSourceUser + "\"");
			System.out.println("dataSourceType=    \"" + dataSourceType + "\"");
			System.out.println("dataSourceServer=  \"" + dataSourceServer + "\"");
			System.out.println("dataSourcePort=    \"" + dataSourcePort + "\"");
			System.out.println("codepage=          \"" + encoding + "\"");

			String sync = (String) fillParam(message, "sync");
			System.out.println("sync=          \"" + sync + "\"");
			String userid = (String) fillParam(message, "user-id");
			System.out.println("user-id=          \"" + userid + "\"");
			String useridlist = (String) fillParam(message, "user-id-list");
			System.out.println("user-id-list=          \"" + useridlist + "\"");

			String dataSourceConfigurationsFile = (String) message.get("dataSourceConfigurationsFile");
			dataSourceConfigurationsFile = DataSourceConfigurations
					.getConfigurationFileName(dataSourceConfigurationsFile);

			if (dataSourceConfigurationsFile == null)
			{
				vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_FAILURE + "");
				vec.add("OpenTMSProperties Path used=" + OpenTMSProperties.getPropfileName());
				return vec;

			}
			System.out.println("dataSourceConfigurationsFile=          \"" + dataSourceConfigurationsFile + "\"");
			// check if the file exists, if not create one
			File f = new File(dataSourceConfigurationsFile);
			if (!f.exists())
			{
				System.out.println("Trying to create dataSourceConfigurationsFile=\"" + dataSourceConfigurationsFile
						+ "\" as it does not exist!");
				DataSourceConfigurations.createDataSourceConfiguration(dataSourceConfigurationsFile);
				f = new File(dataSourceConfigurationsFile);
				if (!f.exists())
				{
					vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_DATASOURCECONFIGURATIONFILECREATION_ERROR + "");
					vec.add("DataSourceConfigurations could not be created =" + dataSourceConfigurationsFile);
				}
			}

			if ((dataSourceType != null) && dataSourceType.equals("de.folt.models.datamodel.sql.OpenTMSSQLDataSource"))
			{
				dataSourceType = "sql";
			}

			// check if the dataSourceType is a known data source class!
			@SuppressWarnings("rawtypes")
			Class dataSourceClass = null;
			try
			{
				dataSourceClass = Class.forName(dataSourceType);
			}
			catch (Exception e)
			{
				dataSourceClass = null;
			}
			if ((dataSourceClass != null)
					&& !dataSourceType.equals("de.folt.models.datamodel.sql.OpenTMSSQLDataSource"))
			{
				try
				{
					message.put("dataModelClass", dataSourceType);
					Class<DataSource> exeClass = dataSourceClass;
					@SuppressWarnings("rawtypes")
					Class[] classparams = new Class[1];
					classparams[0] = DataSourceProperties.class;
					@SuppressWarnings("rawtypes")
					Constructor cons = exeClass.getConstructor(classparams);
					Object param[] = new Object[1];
					DataSourceProperties dataprop = new DataSourceProperties();
					dataprop.copyHashtable(message);
					param[0] = dataprop;
					// create a new DataModel
					DataSource datamodel = (DataSource) cons.newInstance(param);
					boolean bResult = datamodel.createDataSource(dataprop);
					if (bResult)
					{
						System.out.println("runCreateDB " + dataSourceName + " finished!");
						vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_SUCCESS + "");
						vec.add(dataSourceName + " successfully created!");
					}
					else
					{
						System.out.println("runCreateDB " + dataSourceName + " finished (Error)!");
						vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_FAILURE + "");
						vec.add(dataSourceName + " not created!");
					}
				}
				catch (Exception e)
				{
					System.out.println("runCreateDB " + dataSourceName + " finished (Error)!");
					vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_FAILURE + "");
					vec.add(dataSourceName + " not created!");
					vec.add(e.getMessage());
					vec.add(OpenTMSSupportFunctions.exceptionToString(e));
					e.printStackTrace();
				}
			}
			else if (dataSourceType.equalsIgnoreCase("tmx"))
			{
				DataSourceProperties dataprop = new DataSourceProperties();
				dataprop.put("dataSourceName", dataSourceName);
				dataprop.put("dataSourceConfigurationsFile", dataSourceConfigurationsFile);
				de.folt.models.datamodel.tmxfile.TmxFileDataSource sqlinst = new de.folt.models.datamodel.tmxfile.TmxFileDataSource(
						dataprop);
				boolean bResult = sqlinst.createDataSource(dataprop);
				if (bResult)
				{
					System.out.println("runCreateDB " + dataSourceName + " finished!");
					vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_SUCCESS + "");
					vec.add(dataSourceName + " successfully created!");
				}
				else
				{
					System.out.println("runCreateDB " + dataSourceName + " finished (Error)!");
					vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_FAILURE + "");
					vec.add(dataSourceName + " not created!");
				}
			}
			else if (dataSourceType.equalsIgnoreCase("xliff"))
			{
				DataSourceProperties dataprop = new DataSourceProperties();
				dataprop.put("dataSourceName", dataSourceName);
				dataprop.put("sourceLanguage", (String) fillParam(message, "sourceLanguage"));
				dataprop.put("targetLanguage", (String) fillParam(message, "targetLanguage"));
				dataprop.put("dataSourceConfigurationsFile", dataSourceConfigurationsFile);
				de.folt.models.datamodel.xlifffile.XliffFileDataSource sqlinst = new de.folt.models.datamodel.xlifffile.XliffFileDataSource(
						dataprop);
				boolean bResult = sqlinst.createDataSource(dataprop);
				if (bResult)
				{
					System.out.println("runCreateDB " + dataSourceName + " finished!");
					vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_SUCCESS + "");
					vec.add(dataSourceName + " successfully created!");
				}
				else
				{
					System.out.println("runCreateDB " + dataSourceName + " finished (Error)!");
					vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_FAILURE + "");
					vec.add(dataSourceName + " not created!");
				}
			}
			else if (dataSourceType.equalsIgnoreCase("multiple"))
			{
				DataSourceProperties dataprop = new DataSourceProperties();
				dataprop.put("dataSourceName", dataSourceName);
				dataprop.put("dataSourceConfigurationsFile", dataSourceConfigurationsFile);

				de.folt.models.datamodel.multipledatasource.MultipleDataSource sqlinst = new de.folt.models.datamodel.multipledatasource.MultipleDataSource();
				boolean bResult = sqlinst.createDataSource(dataprop);
				MultipleDataSource multnew = (MultipleDataSource) DataSourceInstance.createInstance(dataSourceName);

				if ((multnew != null) && (message.get("dataSources") != null))
				{
					dataprop.put("dataSources", message.get("dataSources"));
					boolean bAdd = multnew.addDataSource((Vector<String>) message.get("dataSources"));
					System.out.println("runCreateDB " + dataSourceName + " bAdd=" + bAdd);
				}

				if (bResult)
				{
					System.out.println("runCreateDB " + dataSourceName + " finished!");
					vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_SUCCESS + "");
					vec.add(dataSourceName + " successfully created!");
				}
				else
				{
					System.out.println("runCreateDB " + dataSourceName + " finished (Error)!");
					vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_FAILURE + "");
					vec.add(dataSourceName + " not created!");
				}
			}
			else if (dataSourceType.equalsIgnoreCase("trados"))
			{
				DataSourceProperties dataprop = new DataSourceProperties();
				dataprop.put("dataSourceConfigurationsFile", dataSourceConfigurationsFile);
				dataprop.put("dataSourceName", dataSourceName);
				dataprop.put("codepage", encoding);
				de.folt.models.datamodel.trados.TradosTMDataSource sqlinst = new de.folt.models.datamodel.trados.TradosTMDataSource(
						dataprop);
				boolean bResult = sqlinst.createDataSource(dataprop);
				if (bResult)
				{
					System.out.println("runCreateDB " + dataSourceName + " finished!");
					vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_SUCCESS + "");
					vec.add(dataSourceName + " successfully created!");
				}
				else
				{
					System.out.println("runCreateDB " + dataSourceName + " finished (Error)!");
					vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_FAILURE + "");
					vec.add(dataSourceName + " not created!");
				}
			}
			else if (dataSourceType.equalsIgnoreCase("tbx"))
			{
				DataSourceProperties dataprop = new DataSourceProperties();
				dataprop.put("dataSourceName", dataSourceName);
				dataprop.put("sourceLanguage", (String) fillParam(message, "sourceLanguage"));
				dataprop.put("targetLanguage", (String) fillParam(message, "targetLanguage"));
				dataprop.put("dataSourceConfigurationsFile", dataSourceConfigurationsFile);
				de.folt.models.datamodel.tbxfile.TbxFileDataSource sqlinst = new de.folt.models.datamodel.tbxfile.TbxFileDataSource(
						dataprop);
				boolean bResult = sqlinst.createDataSource(dataprop);
				if (bResult)
				{
					System.out.println("runCreateDB " + dataSourceName + " finished!");
					vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_SUCCESS + "");
					vec.add(dataSourceName + " successfully created!");
				}
				else
				{
					System.out.println("runCreateDB " + dataSourceName + " finished (Error)!");
					vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_FAILURE + "");
					vec.add(dataSourceName + " not created!");
				}
			}
			else if (dataSourceType.equalsIgnoreCase("Araya"))
			{
				DataSourceProperties dataprop = new DataSourceProperties();
				dataprop.put("dataSourceConfigurationsFile", dataSourceConfigurationsFile);
				dataprop.put("dataSourceName", dataSourceName);
				dataprop.put("sourceLanguage", (String) fillParam(message, "sourceLanguage"));
				dataprop.put("targetLanguage", (String) fillParam(message, "targetLanguage"));
				com.araya.OpenTMS.ArayaDataSource sqlinst = new com.araya.OpenTMS.ArayaDataSource(dataprop);
				boolean bResult = sqlinst.createDataSource(dataprop);
				if (bResult)
				{
					System.out.println("runCreateDB " + dataSourceName + " finished!");
					vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_SUCCESS + "");
					vec.add(dataSourceName + " successfully created!");
				}
				else
				{
					System.out.println("runCreateDB " + dataSourceName + " finished (Error)!");
					vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_FAILURE + "");
					vec.add(dataSourceName + " not created!");
				}
			}
			else
			{
				de.folt.models.datamodel.sql.OpenTMSSQLDataSource sqlinst = new de.folt.models.datamodel.sql.OpenTMSSQLDataSource();
				boolean bResult = sqlinst.createDatabase(message);
				if (bResult)
				{
					System.out.println("runCreateDB " + dataSourceName + " finished!");
					vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_SUCCESS + "");
					vec.add(dataSourceName + " successfully created!");
				}
				else
				{
					System.out.println("runCreateDB " + dataSourceName + " finished (Error)!");
					vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_FAILURE + "");
					vec.add(dataSourceName + " not created!");
				}
			}
			return vec;
		}
		catch (Exception ex)
		{
			vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_FAILURE + "");
			vec.add(ex.getMessage());
			ex.printStackTrace();
			return vec;
		}
	}

	/**
	 * runDeleteTMXDB
	 * 
	 * @param message
	 * @return
	 */
	public static Vector<String> runDeleteDB(Hashtable<String, Object> message)
	{
		Vector<String> vec = new Vector<String>();
		try
		{
			System.out.println("Interface runDeleteDB ");
			String dataSourceName = (String) fillParam(message, "dataSourceName"); // folttm
			System.out.println("dataSourceName=    \"" + dataSourceName + "\"");

			String dataSourceConfigurationsFile = (String) message.get("dataSourceConfigurationsFile");
			dataSourceConfigurationsFile = DataSourceConfigurations
					.getConfigurationFileName(dataSourceConfigurationsFile);
			System.out.println("dataSourceConfigurationsFile=          \"" + dataSourceConfigurationsFile + "\"");

			String dataSourceType = (String) fillParam(message, "dataSourceType");
			if ((dataSourceType == null) || dataSourceType.equals(""))
			{
				if (dataSourceConfigurationsFile != null)
				{
					DataSourceConfigurations dsconfig = new DataSourceConfigurations(dataSourceConfigurationsFile);
					dataSourceType = dsconfig.getDataSourceType(dataSourceName);
					if (dataSourceType == null)
					{
						dataSourceType = "";
						System.out.println("runDeleteDB " + dataSourceName + " finished with error!");
						vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_FAILURE + "");
						vec.add(dataSourceName + " no data source type defined! (dataSourceConfigurationsFile=" + dataSourceConfigurationsFile + ")");
						dataSourceType = "";
						return vec;
					}
				}
				else
				{
					System.out.println("runDeleteDB " + dataSourceName + " finished with error!");
					vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_FAILURE + "");
					vec.add(dataSourceName + " no data source type defined! (dataSourceConfigurationsFile=" + dataSourceConfigurationsFile + ")");
					dataSourceType = "";
					return vec;
				}
			}
			System.out.println("dataSourceType=    \"" + dataSourceType + "\"");

			if (dataSourceType.equalsIgnoreCase("tmx")
					|| dataSourceType.equalsIgnoreCase("de.folt.models.datamodel.tmxfile.TmxFileDataSource"))
			{
				DataSourceProperties dataprop = new DataSourceProperties();
				dataprop.put("dataSourceName", dataSourceName);
				de.folt.models.datamodel.tmxfile.TmxFileDataSource sqlinst = new de.folt.models.datamodel.tmxfile.TmxFileDataSource(
						dataprop);
				boolean bResult = sqlinst.deleteDataSource(dataprop);
				if (bResult)
				{
					System.out.println("TMX: runDeleteDB " + dataSourceName + " finished!");
					vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_SUCCESS + "");
					vec.add(dataSourceName + " (TMX) successfully deleted!");
				}
				else
				{
					System.out.println("TMX: runDeleteDB " + dataSourceName + " finished!");
					vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_FAILURE + "");
					vec.add(dataSourceName + " (TMX) not deleted!");
				}
			}
			else if (dataSourceType.equalsIgnoreCase("xliff")
					|| dataSourceType.equalsIgnoreCase("de.folt.models.datamodel.xlifffile.XliffFileDataSource"))
			{
				DataSourceProperties dataprop = new DataSourceProperties();
				dataprop.put("dataSourceName", dataSourceName);
				de.folt.models.datamodel.xlifffile.XliffFileDataSource sqlinst = new de.folt.models.datamodel.xlifffile.XliffFileDataSource(
						dataprop);
				boolean bResult = sqlinst.deleteDataSource(dataprop);
				if (bResult)
				{
					System.out.println("XLIFF: runDeleteDB " + dataSourceName + " finished!");
					vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_SUCCESS + "");
					vec.add(dataSourceName + " (XLIFF) successfully deleted!");
				}
				else
				{
					System.out.println("XLIFF: runDeleteDB " + dataSourceName + " finished!");
					vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_FAILURE + "");
					vec.add(dataSourceName + " (XLIFF) not deleted!");
				}
			}
			else if (dataSourceType.equalsIgnoreCase("sql")
					|| dataSourceType.equalsIgnoreCase("de.folt.models.datamodel.sql.OpenTMSSQLDataSource"))
			{

				de.folt.models.datamodel.sql.OpenTMSSQLDataSource sqlinst = new de.folt.models.datamodel.sql.OpenTMSSQLDataSource();
				message.put("dataSourceConfigurationsFile", dataSourceConfigurationsFile);
				boolean bResult = sqlinst.deleteDatabase(message);
				if (bResult)
				{
					System.out.println("runDeleteDB " + dataSourceName + " finished!");
					vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_SUCCESS + "");
					vec.add(dataSourceName + " successfully deleted!");
				}
				else
				{
					System.out.println("runDeleteDB " + dataSourceName + " finished!");
					vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_FAILURE + "");
					vec.add(dataSourceName + " not deleted!");
				}
			}
			else
			{
				// run standard remove method
				DataSource sqlinst = de.folt.models.datamodel.DataSourceInstance.createInstance(dataSourceName);
				if (sqlinst == null)
				{
					System.out.println("deleteDataSource " + dataSourceName + " finished!");
					vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_FAILURE + "");
					vec.add(dataSourceName + " not removed!");
					return vec;
				}
				DataSourceProperties dataprop = new DataSourceProperties();
				dataprop.put("dataSourceName", dataSourceName);
				boolean bResult = sqlinst.deleteDataSource(dataprop);
				if (bResult)
				{
					System.out.println("deleteDataSource " + dataSourceName + " finished!");
					vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_SUCCESS + "");
					vec.add(dataSourceName + " successfully removed!");
				}
				else
				{
					System.out.println("deleteDataSource " + dataSourceName + " finished!");
					vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_FAILURE + "");
					vec.add(dataSourceName + " not removed!");
				}
			}
			return vec;
		}
		catch (Exception ex)
		{
			vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_FAILURE + "");
			vec.add(ex.getMessage());
			ex.printStackTrace();
			return vec;
		}
	}

	/**
	 * runExportOpenTMSDataSource
	 * 
	 * @param message
	 * @return
	 */
	public static Vector<String> runExportOpenTMSDataSource(Hashtable<String, Object> message)
	{
		Vector<String> vec = new Vector<String>();
		try
		{
			System.out.println("runExportOpenTMSDataSource ");
			String dataSourceName = (String) fillParam(message, "dataSourceName"); // folttm
			String exportFile = (String) fillParam(message, "exportFile"); // folttm
			if ((exportFile == null) || exportFile.equals(""))
			{
				File fimp = File.createTempFile("temp", ".tmx");
				exportFile = fimp.getAbsolutePath();
				message.put("exportFile", exportFile);
			}
			System.out.println("dataSourceName=        \"" + dataSourceName + "\"");
			System.out.println("exportFile=            \"" + exportFile + "\"");
			int iResult = -1;

			String update_counter = (String) fillParam(message, "update-counter");
			System.out.println("update-counter=        \"" + update_counter + "\"");
			// get the type of the database
			String dataSourceConfigurationsFile = DataSourceConfigurations.getConfigurationFileName(message);
			if (dataSourceConfigurationsFile == null)
			{
				vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_FAILURE + "");
				vec.add("OpenTMSProperties Path used=" + OpenTMSProperties.getPropfileName());
				return vec;
			}
			File f = new File(dataSourceConfigurationsFile);
			String dataSourceType = "";
			if (f.exists())
			{
				DataSourceConfigurations config = new DataSourceConfigurations(dataSourceConfigurationsFile);
				dataSourceType = config.getDataSourceType(dataSourceName);
				if (dataSourceType == null)
					dataSourceType = "";
			}

			Observer observer = (Observer) message.get("observer");
			if (observer != null)
				System.out.println("observer=            \"" + observer.getClass().getName() + "\"");

			if (dataSourceType.equals("de.folt.models.datamodel.tmxfile.TmxFileDataSource"))
			{
				DataSourceProperties dataprops = new DataSourceProperties();
				dataprops.put("dataSourceName", dataSourceName);
				dataprops.put("dataSourceConfigurationsFile", dataSourceConfigurationsFile);
				de.folt.models.datamodel.tmxfile.TmxFileDataSource sqlinst = new de.folt.models.datamodel.tmxfile.TmxFileDataSource(
						dataprops);
				String tmxFile = (String) message.get("exportFile");
				iResult = sqlinst.exportTmxFile(tmxFile);

				if (iResult >= 0)
				{
					String content = de.folt.util.OpenTMSSupportFunctions.copyFileToString(exportFile);
					System.out.println("runExportOpenTMSDataSource " + exportFile + " finished!");
					vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_SUCCESS + "");
					vec.add(content);
					vec.add(dataSourceName + " successfully exported (TmxFileDataSource)!");
					vec.add(iResult + "");
				}
				else
				{
					System.out.println("runExportOpenTMSDataSource " + exportFile + " finished!");
					vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_FAILURE + "");
					vec.add(dataSourceName + " not exported (TmxFileDataSource)!");
					vec.add(-1 + "");
				}
			}
			else if (dataSourceType.equals("de.folt.models.datamodel.xlifffile.XliffFileDataSource"))
			{
				DataSourceProperties dataprops = new DataSourceProperties();
				dataprops.put("dataSourceName", dataSourceName);
				dataprops.put("dataSourceConfigurationsFile", dataSourceConfigurationsFile);
				de.folt.models.datamodel.xlifffile.XliffFileDataSource sqlinst = new de.folt.models.datamodel.xlifffile.XliffFileDataSource(
						dataprops);
				String tmxFile = (String) message.get("exportFile");
				iResult = sqlinst.exportTmxFile(tmxFile);

				if (iResult >= 0)
				{
					String content = de.folt.util.OpenTMSSupportFunctions.copyFileToString(exportFile);
					System.out.println("runExportOpenTMSDataSource " + exportFile + " finished (XliffFileDataSource)!");
					vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_SUCCESS + "");
					vec.add(content);
					vec.add(dataSourceName + " successfully exported (XliffFileDataSource)!");
					vec.add(iResult + "");
				}
				else
				{
					System.out.println("runExportOpenTMSDataSource " + exportFile + " finished!");
					vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_FAILURE + "");
					vec.add(dataSourceName + " not exported!");
					vec.add(-1 + "");
				}
			}
			else if (dataSourceType.equals("de.folt.models.datamodel.tbxfile.TbxFileDataSource"))
			{
				DataSourceProperties dataprops = new DataSourceProperties();
				dataprops.put("dataSourceName", dataSourceName);
				dataprops.put("dataSourceConfigurationsFile", dataSourceConfigurationsFile);
				de.folt.models.datamodel.tbxfile.TbxFileDataSource sqlinst = new de.folt.models.datamodel.tbxfile.TbxFileDataSource(
						dataprops);
				String tmxFile = (String) message.get("exportFile");
				iResult = sqlinst.exportTmxFile(tmxFile);

				if (iResult >= 0)
				{
					String content = de.folt.util.OpenTMSSupportFunctions.copyFileToString(exportFile);
					System.out.println("runExportOpenTMSDataSource " + exportFile + " finished (XliffFileDataSource)!");
					vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_SUCCESS + "");
					vec.add(content);
					vec.add(dataSourceName + " successfully exported (XliffFileDataSource)!");
					vec.add(iResult + "");
				}
				else
				{
					System.out.println("runExportOpenTMSDataSource " + exportFile + " finished!");
					vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_FAILURE + "");
					vec.add(dataSourceName + " not exported!");
					vec.add(-1 + "");
				}
			}
			else
			{
				de.folt.models.datamodel.sql.OpenTMSSQLDataSource sqlinst = new de.folt.models.datamodel.sql.OpenTMSSQLDataSource();

				message.put("dataSourceConfigurationsFile", dataSourceConfigurationsFile);
				iResult = sqlinst.exportTmx(message);
				if (iResult >= 0)
				{
					String content = de.folt.util.OpenTMSSupportFunctions.copyFileToString(exportFile);
					System.out.println("runExportOpenTMSDataSource " + exportFile + " finished!");
					vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_SUCCESS + "");
					vec.add(content);
					vec.add(dataSourceName + " successfully exported!");
					vec.add(iResult + "");
				}
				else if (iResult == -2)
				{
					vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_FAILURE + "");
					vec.add("Access denied for user" + dataSourceName + " not exported!");
					vec.add(-1 + "");
				}
				else
				{
					System.out.println("runExportOpenTMSDataSource " + exportFile + " finished!");
					vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_FAILURE + "");
					vec.add(dataSourceName + " not exported!");
					vec.add(-1 + "");
				}
			}
			return vec;
		}
		catch (Exception ex)
		{
			vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_FAILURE + "");
			vec.add(ex.getMessage());
			ex.printStackTrace();
			return vec;
		}
	}

	public static String runGetDescriptionOpenTMSDataSource(HashMap<String, String> paramHash)
	{
		String description = "";
		try
		{
			description = "";
			String name = paramHash.get("name");
			String dataSourceConfigurationsFile = (String) paramHash.get("dataSourceConfigurationsFile");
			dataSourceConfigurationsFile = DataSourceConfigurations.getConfigurationFileName(dataSourceConfigurationsFile);

			if (dataSourceConfigurationsFile != null)
			{
				try
				{
					DataSource dataSource = DataSourceInstance.createInstance(name, dataSourceConfigurationsFile);
					DataSourceProperties dataSourceProperties = dataSource.getDataSourceProperties();
					Enumeration<String> props = dataSourceProperties.keys();
					while (props.hasMoreElements())
					{
						String key = props.nextElement();
						try
						{
							Object value = dataSourceProperties.get(key);
							description = description + key + ":" + value.toString() + ",";
						}
						catch (Exception e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
							description = description + key + ":" + e.getMessage() + ",";
						}
					}
					description = description.replaceAll("\\,$", "");
				}
				catch (OpenTMSException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					description = e.getMessage();
				}

			}
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			description = e.getMessage();
		}

		return description;
	}

	/**
	 * runImportOpenTMSDataSource
	 * 
	 * @param message
	 * @return
	 */
	public static Vector<String> runImportOpenTMSDataSource(Hashtable<String, Object> message)
	{
		Vector<String> vec = new Vector<String>();
		String tempdir = OpenTMSProperties.getInstance().getOpenTMSProperty("OpenTMS.tempdir");
		try
		{
			System.out.println("runImportOpenTMSDataSource ");
			String dataSourceName = (String) fillParam(message, "dataSourceName"); // folttm
			String importFile = (String) fillParam(message, "sourceDocument"); // folttm
			String inputDocumentType = (String) fillParam(message, "inputDocumentType"); // folttm
			String update_counter = (String) fillParam(message, "update-counter");
			String encoding = (String) fillParam(message, "encoding");
			System.out.println("dataSourceName=        \"" + dataSourceName + "\"");
			System.out.println("update-counter=        \"" + update_counter + "\"");
			String dataSourceConfigurationsFile = (String) message.get("dataSourceConfigurationsFile");
			dataSourceConfigurationsFile = DataSourceConfigurations
					.getConfigurationFileName(dataSourceConfigurationsFile);
			if (dataSourceConfigurationsFile == null)
			{
				vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_FAILURE + "");
				vec.add("OpenTMSProperties Path used=" + OpenTMSProperties.getPropfileName());
				return vec;
			}
			System.out.println("dataSourceConfigurationsFile=          \"" + dataSourceConfigurationsFile + "\"");
			File fi;
			if (inputDocumentType.equals("") || inputDocumentType.equals("FILE"))
			{
				fi = new File(importFile);
				if (!fi.exists())
				{
					System.out.println("runImportOpenTMSDataSource " + importFile + " finished!");
					vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_FILE_NOTFOUND_ERROR + "");
					vec.add(importFile + " does not exist!");
					vec.add(-1 + "");
					return vec;
				}
				System.out.println("sourceDocument=     \"" + importFile + "\"");
			}
			else
			// a string is supplied
			{
				System.out.println("sourceDocument(len)    \"" + importFile.length() + "\"");
				System.out.println("inputDocumentType=     \"" + inputDocumentType + "\"");
				// convert to a file
				String content = importFile;

				if ((tempdir != null) && (!tempdir.equals("")))
				{
					File fimp = File.createTempFile("temp", ".tmx", new File(tempdir));
					importFile = fimp.getAbsolutePath();
				}
				else
				{
					File fimp = File.createTempFile("temp", ".tmx");
					importFile = fimp.getAbsolutePath();
				}
				if ((encoding != null) && !encoding.equals("BASE64"))
				{
					// file written as UTF-8
					de.folt.util.OpenTMSSupportFunctions.simpleCopyStringToFile(content, importFile);
				}
				else
				{
					de.folt.util.OpenTMSSupportFunctions.copyStringToFile(content, importFile);
				}
				System.out.println("sourceDocument(temp)= \"" + importFile + "\"");
				System.out.println("content(length)      = \"" + content.length() + "\"");
				fi = new File(importFile);
				if (!fi.exists())
				{
					System.out.println("runImportOpenTMSDataSource " + importFile + " finished!");
					vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_FILE_NOTFOUND_ERROR + "");
					vec.add(importFile + " does not exist!");
					vec.add(-1 + "");
					return vec;
				}
				message.put("sourceDocument", importFile);
			}

			XmlDocument doc = new de.folt.models.documentmodel.document.XmlDocument();
			doc.loadXmlFile(importFile);
			String rootname = doc.getRootElementName();
			if (rootname == null)
			{
				System.out.println("runImportOpenTMSDataSource " + importFile + " finished!");
				vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_TMXDATASOURCE_FILE_NULL_ERROR + "");
				vec.add(importFile + " root not found!");
				vec.add(-1 + "");
				return vec;
			}
			System.out.println("importFile(rootname)=  \"" + rootname + "\"");

			Observer importObserver = (Observer) message.get("observer");
			if (importObserver != null)
				System.out.println("observer=            \"" + importObserver.getClass().getName() + "\"");

			int iResult = -1;

			File f = new File(dataSourceConfigurationsFile);
			String dataSourceType = "";
			if (f.exists())
			{
				DataSourceConfigurations config = new DataSourceConfigurations(dataSourceConfigurationsFile);
				dataSourceType = config.getDataSourceType(dataSourceName);
				if (dataSourceType == null)
					dataSourceType = "";
			}

			if (dataSourceType.equals("de.folt.models.datamodel.tmxfile.TmxFileDataSource"))
			{
				DataSourceProperties dataprops = new DataSourceProperties();
				dataprops.put("dataSourceName", dataSourceName);
				de.folt.models.datamodel.tmxfile.TmxFileDataSource sqlinst = new de.folt.models.datamodel.tmxfile.TmxFileDataSource(
						dataprops);
				if (rootname.equals("xliff"))
				{
					iResult = sqlinst.importXliffFile((String) message.get("sourceDocument"));
				}
				else if (rootname.equals("tmx"))
				{
					iResult = sqlinst.importTmxFile((String) message.get("sourceDocument"));
				}
				else if (rootname.equals("martif"))
				{
					iResult = sqlinst.importTbxFile((String) message.get("sourceDocument"));
				}

				if (iResult >= 0)
				{
					sqlinst.bPersist();
					System.out.println("runImportOpenTMSDataSource " + importFile + " finished!");
					vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_SUCCESS + "");
					vec.add(dataSourceName + " successfully imported (TmxFileDataSource)!");
					vec.add(iResult + "");
					return vec;
				}
				else
				{
					System.out.println("runImportOpenTMSDataSource " + importFile + " finished!");
					vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_FAILURE + "");
					vec.add(dataSourceName + " not imported (TmxFileDataSource)!");
					vec.add(-1 + "");
					return vec;
				}
			}
			else if (dataSourceType.equals("de.folt.models.datamodel.xlifffile.XliffFileDataSource"))
			{
				DataSourceProperties dataprops = new DataSourceProperties();
				dataprops.put("dataSourceName", dataSourceName);
				de.folt.models.datamodel.xlifffile.XliffFileDataSource sqlinst = new de.folt.models.datamodel.xlifffile.XliffFileDataSource(
						dataprops);
				if (rootname.equals("xliff"))
				{
					iResult = sqlinst.importXliffFile((String) message.get("sourceDocument"));
				}
				else if (rootname.equals("tmx"))
				{
					iResult = sqlinst.importTmxFile((String) message.get("sourceDocument"));
				}
				else if (rootname.equals("martif"))
				{
					iResult = sqlinst.importTbxFile((String) message.get("sourceDocument"));
				}

				if (iResult >= 0)
				{
					sqlinst.bPersist();
					System.out.println("runImportOpenTMSDataSource " + importFile + " finished (XliffFileDataSource)!");
					vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_SUCCESS + "");
					vec.add(dataSourceName + " successfully imported (XliffFileDataSource)!");
					vec.add(iResult + "");
					return vec;
				}
				else
				{
					System.out.println("runImportOpenTMSDataSource " + importFile + " finished!");
					vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_FAILURE + "");
					vec.add(dataSourceName + " not imported!");
					vec.add(-1 + "");
					return vec;
				}
			}
			else if (dataSourceType.equals("de.folt.models.datamodel.tbxfile.TbxFileDataSource"))
			{
				DataSourceProperties dataprops = new DataSourceProperties();
				dataprops.put("dataSourceName", dataSourceName);
				de.folt.models.datamodel.tbxfile.TbxFileDataSource sqlinst = new de.folt.models.datamodel.tbxfile.TbxFileDataSource(
						dataprops);
				if (rootname.equals("xliff"))
				{
					iResult = sqlinst.importXliffFile((String) message.get("sourceDocument"));
				}
				else if (rootname.equals("tmx"))
				{
					iResult = sqlinst.importTmxFile((String) message.get("sourceDocument"));
				}
				else if (rootname.equals("martif"))
				{
					iResult = sqlinst.importTbxFile((String) message.get("sourceDocument"));
				}

				if (iResult >= 0)
				{
					sqlinst.bPersist();
					System.out.println("runImportOpenTMSDataSource " + importFile + " finished (TbxFileDataSource)!");
					vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_SUCCESS + "");
					vec.add(dataSourceName + " successfully imported (TbxFileDataSource)!");
					vec.add(iResult + "");
					return vec;
				}
				else
				{
					System.out.println("runImportOpenTMSDataSource " + importFile + " finished!");
					vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_FAILURE + "");
					vec.add(dataSourceName + " not imported!");
					vec.add(-1 + "");
					return vec;
				}
			}
			else
			{
				de.folt.models.datamodel.sql.OpenTMSSQLDataSource sqlinst = new de.folt.models.datamodel.sql.OpenTMSSQLDataSource();

				if (rootname.equals("xliff"))
				{
					iResult = sqlinst.importXliffFile(message);
				}
				else if (rootname.equals("tmx"))
				{
					iResult = sqlinst.importTmxFile(message);
				}
				else if (rootname.equals("martif"))
				{
					// iResult = sqlinst.importTbxFile((String)
					// message.get("sourceDocument"));

					iResult = sqlinst.importTbxFile(message);
				}
				if (iResult >= 0)
				{
					System.out.println("runImportOpenTMSDataSource " + importFile + " finished!");
					vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_SUCCESS + "");
					vec.add(dataSourceName + " successfully imported!");
					vec.add(iResult + "");
				}
				else
				{
					System.out.println("runImportOpenTMSDataSource " + importFile + " finished!");
					vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_FAILURE + "");
					vec.add((String) message.get("sourceDocument") + " into " + dataSourceName + " not imported!");
					vec.add(-1 + "");
				}
				return vec;
			}
		}
		catch (Exception ex)
		{
			vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_FAILURE + "");
			vec.add(ex.getMessage());
			ex.printStackTrace();
			return vec;
		}
	}

	/**
	 * runInterfaceMethod runs one of the (static) methods defined in the class.
	 * The parameters and returns will change in the future to support more
	 * complex value (e.g. serialized hash table and result also as serialized
	 * vector)
	 * 
	 * @param parameter
	 *            as a hash table.
	 * @return the result of the method (will be improved) Linefeed separtes
	 *         string<br>
	 *         Example:<br>
	 *         &lt;vector><br>
	 *         &lt;result id="0">0&lt;/result><br>
	 *         &lt;result
	 *         id="1">de.folt.models.datamodel.sql.OpenTMSSQLDataSource
	 *         |mynewopentms
	 *         |*********|*********|*********|*********|*********&lt;/result><br>
	 *         &lt;/vector><br>
	 *         Error is returned as &lt;Error>error message&lt/Error>
	 */
	@SuppressWarnings("unchecked")
	public static String runInterfaceMethod(Hashtable<String, String> paramHash)
	{
		// now add all keys/values without "-" for the keys
		Hashtable<String, String> copyHash = new Hashtable<String, String>();
		Enumeration<String> enumh = paramHash.keys();
		System.out.println("Input Parameters: " + paramHash.size());
		int k = 0;
		while (enumh.hasMoreElements())
		{
			String key = enumh.nextElement();
			String value = paramHash.get(key);
			System.out.print(k + ":" + key + "=" + value);
			if (key.startsWith("-"))
			{
				String newkey = key.substring(1);
				System.out.print(" >>> " + newkey);
				copyHash.put(newkey, value);
			}
			System.out.println();
			k++;
		}

		paramHash = copyHash;

		System.out.println("Adapted Parameters: " + paramHash.size());
		enumh = paramHash.keys();
		k = 0;
		while (enumh.hasMoreElements())
		{
			String key = enumh.nextElement();
			String value = paramHash.get(key);
			System.out.println(k + ":" + key + "=" + value);
			k++;
		}

		// get the method to run
		String method = paramHash.get("method");
		if ((method == null) || method.equals(""))
			return "No or wrong method supplied " + method;
		paramHash.remove("-method");
		// now run the method
		@SuppressWarnings("rawtypes")
		Class[] classes = new Class[1];
		classes[0] = Hashtable.class;
		Method localMethod = null;

		String openTMSDir = paramHash.get("openTMSDirectory");
		@SuppressWarnings("unused")
		OpenTMSInitialJarFileLoader openTMSInitialJarFileLoader = new OpenTMSInitialJarFileLoader(openTMSDir);

		System.out.println("Run method=" + method);
		try
		{
			localMethod = Interface.class.getMethod(method, classes);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return "<Error>Get Method: \n" + ex.getLocalizedMessage() + "\n" + ex.getMessage() + "</Error>";
		}

		try
		{
			Vector<String> resultVector = (Vector<String>) localMethod.invoke(Interface.class, paramHash);
			String result = "<vector>\n";
			for (int i = 0; i < resultVector.size(); i++)
			{
				result = result + "\t<result id=\"" + i + "\">" + resultVector.get(i) + "</result>\n";
			}
			result = result + "</vector>";
			return result;
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			String mess = sw.toString();
			pw.close();
			try
			{
				sw.close();
			}
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
			return "<Error>IllegalArgumentException: \n" + mess + "\n" + e.getLocalizedMessage() + "\n"
					+ e.getMessage() + "</Error>";
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			String mess = sw.toString();
			pw.close();
			try
			{
				sw.close();
			}
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
			return "<Error>IllegalAccessException: \n" + mess + "\n" + e.getLocalizedMessage() + "\n" + e.getMessage()
					+ "</Error>";
		}
		catch (InvocationTargetException e)
		{
			e.printStackTrace();
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			String mess = sw.toString();
			pw.close();
			try
			{
				sw.close();
			}
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
			return "<Error>InvocationTargetException: \n" + mess + "\n" + e.getLocalizedMessage() + "\n"
					+ e.getMessage() + "</Error>";
		}
	}

	/**
	 * runInterfaceMethod runs one of the (static) methods defined in the class.
	 * The parameters and returns will change in the future to support more
	 * complex value (e.g. serialized hash table and result also as serialized
	 * vector)
	 * 
	 * @param parameter
	 *            as a string. All parameters which are needed have to appear in
	 *            the string. Delimiter used to split up the String parameter
	 *            into key / value pairs for the
	 *            de.folt.util.OpenTMSSupportFunctions.argumentReader is " "
	 *            (blank). This will be improved in the future too. The key
	 *            should start with "-key"; the "-" is removed when the
	 *            specified method is called. "-dataSourceType xxx" is handed
	 *            over to the method as key=dataSourceType value=xxx as entry in
	 *            the hashtable.<br>
	 *            The method to run is specified using "-method <methodname>".
	 *            All parameters are given as key / value pairs.<br>
	 *            Examples for parameters<br>
	 *            "-method getOpenTMSVersion"<br>
	 *            "-method runCreateDB -dataSourceName myblasource -dataSourceType xxx -dataSourceServer localhost -dataSourcePort 5324 -dataSourceUser blauser -dataSourcePassword blabla -codepage UTF-8"
	 * @return the result of the method (will be improved) Linefeed separtes
	 *         string<br>
	 *         Example:<br>
	 *         &lt;vector><br>
	 *         &lt;result id="0">0&lt;/result><br>
	 *         &lt;result
	 *         id="1">de.folt.models.datamodel.sql.OpenTMSSQLDataSource
	 *         |mynewopentms
	 *         |*********|*********|*********|*********|*********&lt;/result><br>
	 *         &lt;/vector><br>
	 *         Error is returned as &lt;Error>error message&lt/Error>
	 */
	public static String runInterfaceMethod(String parameter)
	{
		String[] params = parameter.split(" ");
		Hashtable<String, String> paramHash = de.folt.util.OpenTMSSupportFunctions.argumentReader(params);

		return runInterfaceMethod(paramHash);
	}

	/**
	 * runInterfaceMethod runs one of the (static) methods defined in the class.
	 * The parameters and returns will change in the future to support more
	 * complex value (e.g. serialized hash table and result also as serialized
	 * vector)
	 * 
	 * @param parameters
	 *            the parameters to be used as an array of strings - will be
	 *            mapped to Hashtable<String, String>
	 * @return the result of the method (will be improved) Linefeed separtes
	 *         string<br>
	 *         Example:<br>
	 *         &lt;vector><br>
	 *         &lt;result id="0">0&lt;/result><br>
	 *         &lt;result
	 *         id="1">de.folt.models.datamodel.sql.OpenTMSSQLDataSource
	 *         |mynewopentms
	 *         |*********|*********|*********|*********|*********&lt;/result><br>
	 *         &lt;/vector><br>
	 *         Error is returned as &lt;Error>error message&lt/Error>
	 */
	private static String runInterfaceMethod(String[] parameters)
	{
		Hashtable<String, String> paramHash = de.folt.util.OpenTMSSupportFunctions.argumentReader(parameters);
		return runInterfaceMethod(paramHash);
	}

	/**
	 * runReturnDBs
	 * 
	 * @param message
	 * @return
	 */
	public static Vector<String> runReturnDBs(Hashtable<String, Object> message)
	{
		Vector<String> vec = new Vector<String>();
		try
		{
			// just for the configuration
			if (message.containsKey("OpenTMSPropertiesPath"))
			{
				String opPath = (String) message.get("OpenTMSPropertiesPath");
				OpenTMSProperties inst = OpenTMSProperties.getInstance(opPath);
				vec.add("OpenTMSPropertiesPath supplied=" + opPath);
				vec.add("OpenTMSProperties supplied=" + inst);
				if (inst.getException() != null)
				{
					vec.add("OpenTMSProperties exception="
							+ de.folt.util.OpenTMSSupportFunctions.exceptionToString(inst.getException()));
				}
				vec.add("OpenTMSProperties Path used start=" + OpenTMSProperties.getPropfileName());
				vec.add("OpenTMSProperties supplied=" + OpenTMSProperties.getInstance());
			}

			String dataSourceConfigurationsFile = (String) message.get("dataSourceConfigurationsFile");
			BasicDataSource sqldatasource = new BasicDataSource();
			if (dataSourceConfigurationsFile == null)
				dataSourceConfigurationsFile = sqldatasource.getDefaultDataSourceConfigurationsFileName();

			File fhd = new File(dataSourceConfigurationsFile);
			if (!fhd.exists())
			{

				File f = new File(sqldatasource.getDefaultDataSourceConfigurationsFileName());
				if (!f.exists())
				{
					vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_FAILURE + "");
					vec.add("OpenTMSProperties Path used=" + OpenTMSProperties.getPropfileName());
					return vec;
				}

				dataSourceConfigurationsFile = sqldatasource.getDefaultDataSourceConfigurationsFileName();
			}

			DataSourceConfigurations dsconfig = new DataSourceConfigurations(dataSourceConfigurationsFile);
			String names[] = dsconfig.getDataSources();
			if (names == null)
			{
				vec.add(0, "0");
				vec.add(1, "No datasources exist");
				vec.add("OpenTMSProperties Path used=" + OpenTMSProperties.getPropfileName());
				vec.add("OpenTMSProperties UrlClassPath used=" + OpenTMSProperties.getUrlClassPath());
				vec.add("DataSourceConfigurationsFileName used="
						+ sqldatasource.getDefaultDataSourceConfigurationsFileName());
				System.out.println("No data sources exist");
				return vec;
			}

			System.out.println("# of data sources = " + names.length);
			for (int i = 0; i < names.length; i++)
			{
				System.out.println("Data source " + i + ": " + names[i]);
				String name = names[i];
				String user = "*********";
				String password = "";
				password = "*********";
				String type = "*********";
				String server = "*********";
				String port = "*********";
				String datasourcetype = dsconfig.getDataSourceType(name);
				String value = datasourcetype + "|" + name + "|" + user + "|" + password + "|" + type + "|" + server
						+ "|" + port;
				vec.add(value);
			}
			sqldatasource = null;
			vec.add(0, "0");
			return vec;
		}
		catch (Exception ex)
		{
			vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_FAILURE + "");
			vec.add("OpenTMSProperties Path used=" + OpenTMSProperties.getPropfileName());
			vec.add(ex.getMessage());
			vec.add(de.folt.util.OpenTMSSupportFunctions.exceptionToString(ex));
			ex.printStackTrace();
			return vec;
		}
	}

	/**
	 * runTranslateDocument
	 * 
	 * @param message
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Vector<String> runTranslateDocument(Hashtable<String, Object> message)
	{
		Vector<String> vec = new Vector<String>();
		try
		{
			System.out.println("runTranslateDocument ");
			// file parameters
			// file name params
			// wk 14.11.2004 - support different values for the input file
			// FILE ... indicates content is in the file name specified
			// ... indicates inputFileName is the which contains
			// the contents
			// ZIP ... contents is zipped
			// MIME ... contents is MIME format
			String inputFileName = de.folt.util.OpenTMSSupportFunctions.fillParam(message, "sourceDocument");
			String inputType = de.folt.util.OpenTMSSupportFunctions.fillParam(message, "inputDocumentType");
			File fi;
			if (inputType.equals("") || inputType.equals("FILE"))
			{
				fi = new File(inputFileName);
				if (!fi.exists())
				{
					System.out.println("runTranslateDocument " + inputFileName + " finished!");
					vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_FILE_NOTFOUND_ERROR + "");
					vec.add(inputFileName + " does not exist!");
					vec.add(-1 + "");
					return vec;
				}
				System.out.println("sourceDocument=                  \"" + inputFileName + "\"");
				System.out.println("inputDocumentType=               \"" + inputType + "\"");
			}
			else
			{
				System.out.println("sourceDocument(len)              \"" + inputFileName.length() + "\"");
				System.out.println("inputDocumentType=               \"" + inputType + "\"");
			}

			String sklFileName = de.folt.util.OpenTMSSupportFunctions.fillParam(message, "sklDocument");
			String outputFileName = de.folt.util.OpenTMSSupportFunctions.fillParam(message, "xliffDocument");
			if ((outputFileName == null) || outputFileName.equals(""))
				outputFileName = inputFileName;
			String outputSegFileName = de.folt.util.OpenTMSSupportFunctions.fillParam(message, "segDocument");
			String outputTransFileName = de.folt.util.OpenTMSSupportFunctions.fillParam(message, "translatedDocument");
			if ((outputTransFileName == null) || outputTransFileName.equals(""))
				outputTransFileName = inputFileName;
			String outputBackFileName = de.folt.util.OpenTMSSupportFunctions
					.fillParam(message, "backConvertedDocument");
			String srcLan = de.folt.util.OpenTMSSupportFunctions.fillParam(message, "sourceLanguage");
			String trgLan = de.folt.util.OpenTMSSupportFunctions.fillParam(message, "targetLanguage");
			message.put("sourceLanguage", srcLan);
			message.put("targetLanguage", trgLan);
			String srcEnc = de.folt.util.OpenTMSSupportFunctions.fillParam(message, "sourceDocumentEncoding");
			String trgEnc = de.folt.util.OpenTMSSupportFunctions.fillParam(message, "targetDocumentEncoding");
			String breakOnCrlf = de.folt.util.OpenTMSSupportFunctions.fillParam(message, "segmentBreakOnCrLf");
			String paraseg = de.folt.util.OpenTMSSupportFunctions.fillParam(message, "paragraphBasesSegmentation");
			// tmx translate params
			String dataSourceName = de.folt.util.OpenTMSSupportFunctions.fillParam(message, "dataSourceName"); // folttm
			String dataSourceType = de.folt.util.OpenTMSSupportFunctions.fillParam(message, "dataSourceType"); // MySQL
			@SuppressWarnings("unused")
			String dataSourceServer = de.folt.util.OpenTMSSupportFunctions.fillParam(message, "dataSourceServer"); // localhost
			@SuppressWarnings("unused")
			String dataSourcePort = de.folt.util.OpenTMSSupportFunctions.fillParam(message, "dataSourcePort"); // 2341
			@SuppressWarnings("unused")
			String dataSourceUser = de.folt.util.OpenTMSSupportFunctions.fillParam(message, "dataSourceUser"); // sa
			@SuppressWarnings("unused")
			String dataTMXSourcePassword = de.folt.util.OpenTMSSupportFunctions
					.fillParam(message, "dataSourcePassword"); // my
			// password
			String dataSourceMatchQuality = de.folt.util.OpenTMSSupportFunctions.fillParam(message,
					"dataSourceMatchQuality");
			@SuppressWarnings("unused")
			String dataSourceMatchMaximum = de.folt.util.OpenTMSSupportFunctions.fillParam(message,
					"dataSourceMatchMaximum");
			@SuppressWarnings("unused")
			String dataSourceReplacementClasses = de.folt.util.OpenTMSSupportFunctions.fillParam(message,
					"dataSourceReplacementClasses");
			// Parameters for SubSegmentMatching
			// tmx translate params
			String dataSubSegmentMatchingSourceName = de.folt.util.OpenTMSSupportFunctions.fillParam(message,
					"dataSubSegmentMatchingSourceName"); // folttm
			@SuppressWarnings("unused")
			String dataSubSegmentMatchingSourceType = de.folt.util.OpenTMSSupportFunctions.fillParam(message,
					"dataSubSegmentMatchingSourceType"); // MySQL
			@SuppressWarnings("unused")
			String dataSubSegmentMatchingSourceServer = de.folt.util.OpenTMSSupportFunctions.fillParam(message,
					"dataSubSegmentMatchingSourceServer"); // localhost
			@SuppressWarnings("unused")
			String dataSubSegmentMatchingSourcePort = de.folt.util.OpenTMSSupportFunctions.fillParam(message,
					"dataSubSegmentMatchingSourcePort"); // 2341
			@SuppressWarnings("unused")
			String dataSubSegmentMatchingSourceUser = de.folt.util.OpenTMSSupportFunctions.fillParam(message,
					"dataSubSegmentMatchingSourceUser"); // sa
			@SuppressWarnings("unused")
			String dataSubSegmentMatchingSourcePassword = de.folt.util.OpenTMSSupportFunctions.fillParam(message,
					"dataSubSegmentMatchingSourcePassword"); // my
																// password
			message.put("similarity", dataSourceMatchQuality);
			String propFile = OpenTMSProperties.getInstance().getOpenTMSProperty("ArayaPropertiesFile");
			String ignoreProps = de.folt.util.OpenTMSSupportFunctions.fillParam(message, "ignoreProps");
			String formatMatchPenalty = de.folt.util.OpenTMSSupportFunctions.fillParam(message, "formatMatchPenalty");
			String dataSourceConfigurationsFile = de.folt.util.OpenTMSSupportFunctions.fillParam(message,
					"dataSourceConfigurationsFile");
			dataSourceConfigurationsFile = DataSourceConfigurations
					.getConfigurationFileName(dataSourceConfigurationsFile);

			if (ignoreProps == null)
				ignoreProps = "false";
			message.put("ignoreProps", ignoreProps);
			if (formatMatchPenalty == null)
				formatMatchPenalty = "1";
			message.put("formatMatchPenalty", formatMatchPenalty);
			message.put("ArayaPropertiesFile", propFile);

			System.out.println("sklDocument=                     \"" + sklFileName + "\"");
			System.out.println("xliffDocument=                   \"" + outputFileName + "\"");
			System.out.println("segDocument=                     \"" + outputSegFileName + "\"");
			System.out.println("translatedDocument=              \"" + outputTransFileName + "\"");
			System.out.println("backConvertedDocument=           \"" + outputBackFileName + "\"");
			System.out.println("sourceLanguage=                  \"" + srcLan + "\"");
			System.out.println("targetLanguage=                  \"" + trgLan + "\"");
			System.out.println("sourceDocumentEncoding=          \"" + srcEnc + "\"");
			System.out.println("targetDocumentEncoding=          \"" + trgEnc + "\"");
			System.out.println("segmentBreakOnCrLf=              \"" + breakOnCrlf + "\"");
			System.out.println("paragraphBasesSegmentation=      \"" + paraseg + "\"");
			System.out.println("dataSourceName=                  \"" + dataSourceName + "\"");
			System.out.println("dataSourceType=                  \"" + dataSourceType + "\"");
			System.out.println("dataSourceMatchQuality=          \"" + dataSourceMatchQuality + "\"");
			System.out.println("ArayaPropertiesFile=             \"" + propFile + "\"");
			System.out.println("ignoreProps=                     \"" + ignoreProps + "\"");
			System.out.println("formatMatchPenalty=              \"" + formatMatchPenalty + "\"");
			System.out.println("dataSubSegmentMatchingSourceName=\"" + dataSubSegmentMatchingSourceName + "\"");
			System.out.println("dataSourceConfigurationsFile=    \"" + dataSourceConfigurationsFile + "\"");

			int iResult = -1;
			// ok - here we should check if we have an xliff file
			String rootname = "";

			if (XmlDocument.bIsXmlDocument(inputFileName))
			{
				try
				{
					XmlDocument doc = new de.folt.models.documentmodel.document.XmlDocument();
					doc.loadXmlFile(inputFileName);
					if (doc.getDocument() == null)
					{
						rootname = "";
					}
					else
					{
						rootname = doc.getRootElementName();
						System.out.println("xliffFile(rootname)=  \"" + rootname + "\"");
					}
				}
				catch (Exception ex)
				{
					rootname = "";
					System.out.println("inputFileName" + inputFileName + " - no xliff file");
				}
			}

			String content = "";
			String contentskl = "";

			if (rootname.equals("xliff"))
			{
				outputFileName = inputFileName;
				// copy to outputTransFileName
				if (!outputFileName.equals(outputTransFileName))
					de.folt.util.OpenTMSSupportFunctions.copyFile(outputFileName, outputTransFileName);
			}
			else
			{
				// run conversion first
				// CONVERT SEGMENT TMTRANSLATE TERMTRANSLATE BACKCONVERT
				// TRANS BACK CONV SEG PT
				message.put("openTMSTranslationPhase", "CONVERTSEG");
				System.out.println("Conversion Phase: " + message.get("openTMSTranslationPhase"));
				@SuppressWarnings("unused")
				Vector<String> retVec = com.araya.OpenTMS.Interface.runConverter(message);
				fi = new File(outputSegFileName);
				if (!fi.exists())
				{
					System.out.println("runTranslateDocument " + outputSegFileName + " finished!");
					vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_FAILURE + "");
					vec.add(dataSourceName + " not translated!");
					vec.add(-1 + "");
					return vec;
				}
				if (!outputFileName.equals(outputTransFileName))
					de.folt.util.OpenTMSSupportFunctions.copyFile(outputSegFileName, outputTransFileName);
				message.put("sourceDocument", outputTransFileName);
			}

			// do it now dynamically depending on the dataSourceType - and
			// support some short cut names...
			if (dataSourceType.equalsIgnoreCase("googleTranslate"))
				dataSourceType = "de.folt.models.datamodel.googletranslate.GoogleTranslate";
			else if (dataSourceType.equalsIgnoreCase("microsoftTranslate"))
				dataSourceType = "de.folt.models.datamodel.microsofttranslate.MicrosoftTranslate";
			else if (dataSourceType.equalsIgnoreCase("IATETranslate"))
				dataSourceType = "de.folt.models.datamodel.iate.IateTerminology";
			else if (dataSourceType.equalsIgnoreCase("xliff"))
				dataSourceType = "de.folt.models.datamodel.xlifffile.XliffFileDataSource";
			else if (dataSourceType.equalsIgnoreCase("tmx"))
				dataSourceType = "de.folt.models.datamodel.tmxfile.TmxFileDataSource";
			else if (dataSourceType.equalsIgnoreCase("trados"))
				dataSourceType = "de.folt.models.datamodel.trados.TradosTMDataSource";
			else if (dataSourceType.equalsIgnoreCase("sql"))
				dataSourceType = "de.folt.models.datamodel.sql.OpenTMSSQLDataSource";
			else if (dataSourceType.equalsIgnoreCase("sql"))
				dataSourceType = "de.folt.models.datamodel.sql.OpenTMSSQLDataSource"; // default...

			de.folt.models.documentmodel.xliff.XliffDocument document = new de.folt.models.documentmodel.xliff.XliffDocument();
			DataSource dataSource = null;
			DataSourceProperties model = new DataSourceProperties();
			if (dataSourceType.equals("de.folt.models.datamodel.googletranslate.GoogleTranslate"))
			{
				model.put("dataModelClass", "de.folt.models.datamodel.googletranslate.GoogleTranslate");
				dataSource = DataSourceInstance.createInstance("GoogleTranslate:translate", model);
			}
			else if (dataSourceType.equals("de.folt.models.datamodel.microsofttranslate.MicrosoftTranslate"))
			{
				model.put("dataModelClass", "de.folt.models.datamodel.microsofttranslate.MicrosoftTranslate");
				dataSource = DataSourceInstance.createInstance("Microsoft:translate", model);
			}
			else if (dataSourceType.equals("de.folt.models.datamodel.iate.IateTerminology"))
			{
				model.put("dataModelClass", "de.folt.models.datamodel.iate.IateTerminology");
				dataSource = DataSourceInstance.createInstance("Microsoft:translate", model);
			}
			else
			{
				try
				{
					dataSource = DataSourceInstance.createInstance(dataSourceName, dataSourceConfigurationsFile);
				}
				catch (Exception e)
				{
					dataSource = null;
					e.printStackTrace();
					iResult = -10;
				}
			}

			if ((dataSourceName != null) && (dataSource != null))
			{
				int iSimilarity = 70;
				try
				{
					iSimilarity = Integer.parseInt(dataSourceMatchQuality);
				}
				catch (Exception ex)
				{

				}
				try
				{
					document.loadXmlFile(outputTransFileName);
					document.translate(dataSource, srcLan, trgLan, iSimilarity, -1, model);
					content = de.folt.util.OpenTMSSupportFunctions.copyFileToString(outputTransFileName);
					document.saveToXmlFile();
					iResult = 10;
				}
				catch (Exception e)
				{
					dataSource = null;
					e.printStackTrace();
					iResult = -20;
				}
			}

			if ((dataSubSegmentMatchingSourceName != null) && !dataSubSegmentMatchingSourceName.equals(""))
			{
				try
				{
					document = new de.folt.models.documentmodel.xliff.XliffDocument();
					document.loadXmlFile(outputTransFileName);
					DataSource subsegmentDataSource;

					subsegmentDataSource = DataSourceInstance.createInstance(dataSubSegmentMatchingSourceName,
							dataSourceConfigurationsFile);
					Hashtable<String, Object> translationParameters = new Hashtable<String, Object>();
					document.subSegmentTranslate(subsegmentDataSource, srcLan, trgLan, translationParameters);
					document.saveToXmlFile();
					iResult = 20;
				}
				catch (Exception e)
				{
					iResult = iResult - 100;
					e.printStackTrace();
				}
			}

			if (iResult >= 0)
			{
				content = de.folt.util.OpenTMSSupportFunctions.copyFileToString(outputTransFileName);
				contentskl = de.folt.util.OpenTMSSupportFunctions.copyFileToString(sklFileName);
				System.out.println("runTranslateDocument " + outputTransFileName + " finished!");
				vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_SUCCESS + "");
				vec.add(content);
				vec.add(contentskl);
				vec.add(outputTransFileName + " successfully translated!");
				vec.add(iResult + "");
			}
			else
			{
				System.out.println("runTranslateDocument " + outputTransFileName + " finished!");
				vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_FAILURE + "");
				vec.add(outputTransFileName + " not translated!");
				vec.add(-1 + "");
				vec.add(iResult + "");
			}
			return vec;
		}
		catch (Exception ex)
		{
			vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_FAILURE + "");
			vec.add(ex.getMessage());
			ex.printStackTrace();
			return vec;
		}
	}

	/**
	 * writeLogMessage write a message to the Web server log file
	 * 
	 * @param message
	 *            message to write
	 * @return Vector: 0: -> 0; 1: the message logged
	 */
	public static Vector<String> writeLogMessage(Hashtable<String, Object> message)
	{
		Vector<String> vec = new Vector<String>();
		try
		{
			String logMessage = de.folt.util.OpenTMSSupportFunctions.fillParam(message, "logMessage");
			System.out.println(logMessage);
			vec.add(0, "0");
			vec.add(1, logMessage);
			return vec;
		}
		catch (Exception ex)
		{
			vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_FAILURE + "");
			vec.add(ex.getMessage());
			ex.printStackTrace();
			return vec;
		}
	}
}
