package de.folt.webservices;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;
import java.util.regex.Pattern;

import de.folt.constants.OpenTMSConstants;
import de.folt.constants.OpenTMSVersionConstants;
import de.folt.models.datamodel.DataSource;
import de.folt.models.datamodel.DataSourceConfigurations;
import de.folt.models.datamodel.DataSourceInstance;
import de.folt.models.datamodel.DataSourceProperties;
import de.folt.models.datamodel.sql.OpenTMSSQLDataSource;
import de.folt.util.OpenTMSException;
import de.folt.util.OpenTMSSupportFunctions;

public class SynchronizeService
{

	private String					method			= "";

	private String					name			= "";

	private String					parameter		= "";

	private HashMap<String, String>	paramHash		= null;

	private String					password		= "";

	private String					user_id			= "";

	private String					user_id_list	= "";

	/**
	 * @param paramHash
	 * @return
	 */
	public HashMap<String, String> addUser(HashMap<String, String> paramHash)
	{
		try
		{
			@SuppressWarnings("unused")
			OpenTMSSQLDataSource otemp = new OpenTMSSQLDataSource();

			String datasourcename = paramHash.get("name"); // the name of the datasource
			String newuser = paramHash.get("newuser"); // the name of the datasource

			DataSource datasource = DataSourceInstance.createInstance(datasourcename);
			if (datasource == null)
			{
				paramHash.put("errorstring", "Unknown data source " + datasourcename);
				paramHash.put("errorno", "100050");
				paramHash.put("result", "error");
				return paramHash;
			}
			DataSourceProperties dataProps = datasource.getDataSourceProperties();

			if (!bCheckUserId(paramHash, datasource))
			{
				return paramHash;
			}

			String userlist = (String) dataProps.get("sync-user-id-list");
			if ((userlist == null) || userlist.equals("") || userlist.equals("null"))
				userlist = newuser;
			else
			// check if user exists
			{
				// 19.11.2013
				String[] userListList = userlist.split(Pattern.quote(","));
				for (int i = 0; i < userListList.length; i++)
				{
					if (userListList[i].equals(newuser))
					{
						paramHash.put("userlist", userlist);
						paramHash.put("result", "ok");
						paramHash.put("errorstring", newuser + " sync-user-id-list exist");
						return paramHash;
					}
				}
				// userlist = "";
				// end 19.11.2013
			}

			if (!userlist.equals(newuser))
				userlist = userlist + "," + newuser;
			userlist = userlist.replaceFirst("^,", "");
			dataProps.put("sync-user-id-list", userlist);
			// datasource.setDataSourceProperties(dataProps);
			datasource.updateDataSourceProperty("sync-user-id-list", userlist);

			paramHash.put("userlist", userlist);
			paramHash.put("result", "ok");
		}
		catch (OpenTMSException e)
		{
			e.printStackTrace();
			paramHash.put("errorstring", OpenTMSSupportFunctions.exceptionToString(e));
			paramHash.put("errorno", "100040");
			paramHash.put("result", "error");
		}
		return paramHash;
	}

	private boolean bCheckUserId(HashMap<String, String> paramHash, DataSource datasource)
	{
		if (datasource == null)
		{
			paramHash.put("errorstring", "100060: Access denied for " + (String) paramHash.get("user-id"));
			paramHash.put("errorno", "100060");
			paramHash.put("result", "error");
			return false;
		}
		DataSourceProperties dataProps;
		try
		{
			dataProps = datasource.getDataSourceProperties();
		}
		catch (OpenTMSException e)
		{
			paramHash.put("errorstring", "100060: Access denied for " + (String) paramHash.get("user-id") + "\n" + e.getMessage());
			paramHash.put("errorno", "100070");
			paramHash.put("result", "error");
			return false;
		}

		if (paramHash.containsKey("user-id"))
		{
			String useridlist = (String) dataProps.get("sync-user-id-list");
			String[] users = useridlist.split(",");
			for (int i = 0; i < users.length; i++)
			{
				if ((users[i] != null) && users[i].equals((String) paramHash.get("user-id")))
				{
					return true;
				}
			}
			useridlist = (String) datasource.getDataSourceConfigurations().getDataSourceCreator(datasource.getDataSourceName());
			if ((useridlist != null) && useridlist.equals((String) paramHash.get("user-id")))
			{
				return true;
			}

			paramHash.put("errorstring", "100070: Access denied for " + (String) paramHash.get("user-id"));
			paramHash.put("errorno", "100070");
			paramHash.put("result", "error");
			return false;
		}
		
		paramHash.put("errorstring", "100080: Access denied for " + (String) paramHash.get("user-id"));
		paramHash.put("errorno", "100080");
		paramHash.put("result", "error");
		return false;
	}

	private boolean bCheckUserId(HashMap<String, String> paramHash, String useridlist)
	{
		if (paramHash.containsKey("user-id"))
		{
			String[] users = useridlist.split(",");
			for (int i = 0; i < users.length; i++)
			{
				if ((users[i] != null) && users[i].equals((String) paramHash.get("user-id")))
				{
					return true;
				}
			}

			paramHash.put("errorstring", "100070: Access denied for " + (String) paramHash.get("user-id"));
			paramHash.put("errorno", "100070");
			paramHash.put("result", "error");
			return false;
		}
		return false;
	}

	/**
	 * Message for cleaning a datasource
	 * @param paramHash
	 * @return
	 */
	public HashMap<String, String>clean(HashMap<String, String> paramHash)
	{
		setValues(paramHash);
		Hashtable<String, Object> param = new Hashtable<String, Object>();
		param.put("dataSourceName", name);
		if (paramHash.containsKey("user-id"))
		{
			param.put("user-id", paramHash.get("user-id"));
		}
		Vector<String> result = de.folt.rpc.connect.Interface.runCleanDB(param);
		paramHash.put("result", "ok");
		if (result.size() == 1)
		{
			if (result.get(0).equals(de.folt.constants.OpenTMSConstants.OpenTMS_ID_SUCCESS + ""))
				paramHash.put("result", "ok");
			else
				paramHash.put("result", "error");
			return paramHash;

		}
		else if (result.size() == 2)
		{
			if (result.get(0).equals(de.folt.constants.OpenTMSConstants.OpenTMS_ID_SUCCESS + ""))
				paramHash.put("result", "ok");
			else
				paramHash.put("result", "error");
			paramHash.put("errorstring", result.get(1));
			paramHash.put("errorno", result.get(0));
			return paramHash;

		}
		else if (result.size() == 3)
		{
			if (result.get(0).equals(de.folt.constants.OpenTMSConstants.OpenTMS_ID_SUCCESS + ""))
				paramHash.put("result", "ok");
			else
				paramHash.put("result", "error");
			paramHash.put("errorstring", result.get(1));
			paramHash.put("errorno", result.get(0));
			paramHash.put("exception", result.get(2));
			return paramHash;
		}

		return paramHash;
	}

	/**
	 * @param paramHash
	 * @return
	 */
	public HashMap<String, String> create(HashMap<String, String> paramHash)
	{
		setValues(paramHash);

		parameter = parameter + ";user-id=" + user_id + ";user-id-list=" + user_id_list + ";sync=true";

		Vector<String> result = de.folt.models.datamodel.CreateDataSource.createDataSource(name, paramHash, parameter);
		paramHash.put("result", "ok");
		if (result.size() == 1)
		{
			if (result.get(0).equals(de.folt.constants.OpenTMSConstants.OpenTMS_ID_SUCCESS + ""))
				paramHash.put("result", "ok");
			else
				paramHash.put("result", "error");

		}
		else if (result.size() == 2)
		{
			if (result.get(0).equals(de.folt.constants.OpenTMSConstants.OpenTMS_ID_SUCCESS + ""))
				paramHash.put("result", "ok");
			else
				paramHash.put("result", "error");
			paramHash.put("errorstring", result.get(1));
			paramHash.put("errorno", result.get(0));

		}
		else if (result.size() == 3)
		{
			if (result.get(0).equals(de.folt.constants.OpenTMSConstants.OpenTMS_ID_SUCCESS + ""))
				paramHash.put("result", "ok");
			else
				paramHash.put("result", "error");
			paramHash.put("errorstring", result.get(1));
			paramHash.put("errorno", result.get(0));
			paramHash.put("exception", result.get(2));
		}

		return paramHash;
	}
	
	public HashMap<String, String> delete(HashMap<String, String> paramHash)
	{
		setValues(paramHash);
		Hashtable<String, Object> param = new Hashtable<String, Object>();
		param.put("dataSourceName", name);
		if (paramHash.containsKey("user-id"))
		{
			param.put("user-id", paramHash.get("user-id"));
		}
		Vector<String> result = de.folt.rpc.connect.Interface.runDeleteDB(param);
		paramHash.put("result", "ok");
		if (result.size() == 1)
		{
			if (result.get(0).equals(de.folt.constants.OpenTMSConstants.OpenTMS_ID_SUCCESS + ""))
				paramHash.put("result", "ok");
			else
				paramHash.put("result", "error");
			return paramHash;

		}
		else if (result.size() == 2)
		{
			if (result.get(0).equals(de.folt.constants.OpenTMSConstants.OpenTMS_ID_SUCCESS + ""))
				paramHash.put("result", "ok");
			else
				paramHash.put("result", "error");
			paramHash.put("errorstring", result.get(1));
			paramHash.put("errorno", result.get(0));
			return paramHash;

		}
		else if (result.size() == 3)
		{
			if (result.get(0).equals(de.folt.constants.OpenTMSConstants.OpenTMS_ID_SUCCESS + ""))
				paramHash.put("result", "ok");
			else
				paramHash.put("result", "error");
			paramHash.put("errorstring", result.get(1));
			paramHash.put("errorno", result.get(0));
			paramHash.put("exception", result.get(2));
			return paramHash;
		}

		return paramHash;
	}

	/**
	 * @param paramHash
	 * @return
	 */
	public HashMap<String, String> download(HashMap<String, String> paramHash)
	{
		setValues(paramHash);
		String update_counter = paramHash.get("update-counter");
		String name = paramHash.get("name");

		DataSource datasource = null;
		try
		{
			datasource = DataSourceInstance.createInstance(name);
		}
		catch (OpenTMSException e)
		{
			e.printStackTrace();
			paramHash.put("errorstring", OpenTMSSupportFunctions.exceptionToString(e));
			paramHash.put("errorno", "100040");
			paramHash.put("result", "error");
			return paramHash;
		}
		if (datasource == null)
		{
			paramHash.put("errorstring", "Unknown data source " + name);
			paramHash.put("errorno", "100050");
			paramHash.put("result", "error");
			return paramHash;
		}

		@SuppressWarnings("unused")
		DataSourceProperties dataProps = null;
		try
		{
			dataProps = datasource.getDataSourceProperties();
		}
		catch (OpenTMSException e)
		{
			paramHash.put("errorstring", "Unknown data source " + name);
			paramHash.put("errorno", "100050");
			paramHash.put("result", "error");
			return paramHash;
		}

		if (!bCheckUserId(paramHash, datasource))
		{
			return paramHash;
		}

		String encoding = paramHash.get("encoding");

		Hashtable<String, Object> param = new Hashtable<String, Object>();
		param.put("dataSourceName", name);
		param.put("dataSourceConfigurationsFile", "");
		param.put("update-counter", update_counter);
		param.put("sync", "download");
		if (encoding == null)
			encoding = "";
		param.put("encoding", encoding);
		if (paramHash.containsKey("user-id"))
		{
			param.put("user-id", paramHash.get("user-id"));
		}

		Vector<String> result = de.folt.rpc.connect.Interface.runExportOpenTMSDataSource(param);

		paramHash.remove("tmx-document");
		OpenTMSSQLDataSource os = new OpenTMSSQLDataSource();
		paramHash.put("update-counter", os.currentTimeMillis() + "");
		os = null;

		if (result.size() == 1)
		{
			if (result.get(0).equals(de.folt.constants.OpenTMSConstants.OpenTMS_ID_SUCCESS + ""))
				paramHash.put("result", "ok");
			else
				paramHash.put("result", "error");

		}
		else if (result.size() == 2)
		{
			if (result.get(0).equals(de.folt.constants.OpenTMSConstants.OpenTMS_ID_SUCCESS + ""))
				paramHash.put("result", "ok");
			else
				paramHash.put("result", "error");
			paramHash.put("errorstring", result.get(1));
			paramHash.put("errorno", result.get(0));

		}
		else if (result.size() == 3)
		{
			if (result.get(0).equals(de.folt.constants.OpenTMSConstants.OpenTMS_ID_SUCCESS + ""))
				paramHash.put("result", "ok");
			else
				paramHash.put("result", "error");
			paramHash.put("errorstring", result.get(1));
			paramHash.put("errorno", result.get(0));
			paramHash.put("exception", result.get(2));
		}
		else if (result.size() == 4)
		{
			if (result.get(0).equals(de.folt.constants.OpenTMSConstants.OpenTMS_ID_SUCCESS + ""))
				paramHash.put("result", "ok");
			else
				paramHash.put("result", "error");
			paramHash.put("errorno", result.get(0));
			String tmxdocument = result.get(1);
			tmxdocument = de.folt.util.OpenTMSSupportFunctions.decodeBASE64(tmxdocument);
			paramHash.put("tmx-document", tmxdocument);
			paramHash.put("errorstring", result.get(2));
			paramHash.put("exception", result.get(3));
		}

		return paramHash;
	}

	/**
	 * @param paramHash
	 * @return
	 */
	public HashMap<String, String> error(HashMap<String, String> paramHash)
	{
		paramHash.put("result", "error");
		paramHash.put("errorstring", OpenTMSConstants.OpenTMS_SYNC_UNKNOWN_METHODE_SERVICE_ERROR_MESSAGE);
		paramHash.put("errorno", OpenTMSConstants.OpenTMS_SYNC_UNKNOWN_METHODE_SERVICE_ERROR + "");
		return paramHash;
	}

	/**
	 * @param paramHash
	 * @return
	 */
	public HashMap<String, String> getCreator(HashMap<String, String> paramHash)
	{
		try
		{
			@SuppressWarnings("unused")
			OpenTMSSQLDataSource otemp = new OpenTMSSQLDataSource();

			String datasourcename = paramHash.get("name"); // the name of the datasource

			DataSource datasource = DataSourceInstance.createInstance(datasourcename);
			if (datasource == null)
			{
				paramHash.put("errorstring", "Unknown data source " + datasourcename);
				paramHash.put("errorno", "100050");
				paramHash.put("result", "error");
			}

			DataSourceProperties dataProps = datasource.getDataSourceProperties();
			if (!bCheckUserId(paramHash, datasource))
			{
				return paramHash;
			}

			String creatorName = (String) dataProps.get("user-id");
			if ((creatorName == null) || creatorName.equals(""))
				creatorName = datasource.getDataSourceConfigurations().getDataSourceCreator(datasourcename);
			String databaseCreatorName = (String) dataProps.get("connection.username");

			paramHash.put("creator", creatorName);
			paramHash.put("database-creator", databaseCreatorName);
			paramHash.put("result", "ok");
		}
		catch (OpenTMSException e)
		{
			e.printStackTrace();
			paramHash.put("errorstring", OpenTMSSupportFunctions.exceptionToString(e));
			paramHash.put("errorno", "100030");
			paramHash.put("result", "error");
		}
		return paramHash;
	}

	/**
	 * Return the contents of the log file of the server
	 * 
	 * @param paramHash
	 * @return
	 */
	public HashMap<String, String> getLogFile(HashMap<String, String> paramHash)
	{
		setValues(paramHash);
		try
		{
			paramHash.put("logFileContent", OpenTMSSupportFunctions.decodeBASE64(OpenTMSSupportFunctions.copyFileToString(paramHash.get("logfile"))));
			paramHash.put("result", "ok");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			paramHash.put("errorstring", OpenTMSSupportFunctions.exceptionToString(e));
			paramHash.put("errorno", "100000");
			paramHash.put("result", "error");
		}
		return paramHash;
	}

	public String getMethod()
	{
		return method;
	}

	public String getName()
	{
		return name;
	}

	public String getParameter()
	{
		return parameter;
	}

	public HashMap<String, String> getParamHash()
	{
		return paramHash;
	}

	public String getPassword()
	{
		return password;
	}

	/**
	 * @param paramHash
	 *            parameters for the data sources to return
	 * @return a list of data sources which support syncing seperated by ; and
	 *         as key syncDataSources in the param hash
	 */
	public HashMap<String, String> getSyncDataSources(HashMap<String, String> paramHash)
	{

		final Vector<String[]> tmxDatabases = de.folt.models.datamodel.DataSourceConfigurations
				.getOpenTMSDatabasesWithType();

		if (paramHash == null)
			paramHash = new HashMap<String, String>();

		String syndbs = "";

		if (tmxDatabases != null)
		{
			int size = tmxDatabases.size();
			if (size > 0)
			{
				for (int i = 0; i < size; i++)
				{
					String name = tmxDatabases.get(i)[0];
					// String type = tmxDatabases.get(i)[1];
					String sync = "false";
					if (tmxDatabases.size() > 2)
					{
						sync = tmxDatabases.get(i)[2];
						if (sync.equals("true"))
						{
							// check if user is allowed to access this datasource
							boolean isAllowed = false;
							if (tmxDatabases.get(i)[5] != null)
								isAllowed = bCheckUserId(paramHash, tmxDatabases.get(i)[3] + "," + tmxDatabases.get(i)[4] + "," + tmxDatabases.get(i)[5]);
							else
								isAllowed = bCheckUserId(paramHash, tmxDatabases.get(i)[3] + "," + tmxDatabases.get(i)[4]);
							if (isAllowed)
							{
								if (syndbs.equals(""))
									syndbs = name;
								else
									syndbs = syndbs + ";" + name;
							}
						}
					}
				}
			}
			paramHash.put("syncDataSources", syndbs);
		}

		paramHash.put("result", "ok");

		return paramHash;
	}

	/**
	 * @param paramHash
	 * @return
	 */
	public HashMap<String, String> getUpdateCounter(HashMap<String, String> paramHash)
	{
		paramHash = new HashMap<String, String>();
		OpenTMSSQLDataSource otemp = new OpenTMSSQLDataSource();
		paramHash.put("update-counter", otemp.currentTimeMillis() + "");
		paramHash.put("result", "ok");
		return paramHash;
	}

	public String getUser()
	{
		return user_id;
	}

	public String getUser_id_list()
	{
		return user_id_list;
	}

	/**
	 * Return the version information of openTMS
	 * 
	 * @param paramHash
	 * @return added version information
	 */
	public HashMap<String, String> getVersionInformation(HashMap<String, String> paramHash)
	{
		setValues(paramHash);
		try
		{
			paramHash.put("versionInfo", OpenTMSVersionConstants.getFullVersionString());
			paramHash.put("versionFullInfo", OpenTMSVersionConstants.getAllVersionsAsString());
			paramHash.put("result", "ok");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			paramHash.put("errorstring", OpenTMSSupportFunctions.exceptionToString(e));
			paramHash.put("errorno", "100000");
			paramHash.put("result", "error");
		}
		return paramHash;
	}

	/**
	 * @param paramHash
	 * @return
	 */
	public HashMap<String, String> listUser(HashMap<String, String> paramHash)
	{
		try
		{
			@SuppressWarnings("unused")
			OpenTMSSQLDataSource otemp = new OpenTMSSQLDataSource();

			String datasourcename = paramHash.get("name"); // the name of the datasource

			DataSource datasource = DataSourceInstance.createInstance(datasourcename);
			if (datasource == null)
			{
				paramHash.put("errorstring", "Unknown data source " + datasourcename);
				paramHash.put("errorno", "100050");
				paramHash.put("result", "error");
			}
			DataSourceProperties dataProps = datasource.getDataSourceProperties();

			if (!bCheckUserId(paramHash, datasource))
			{
				return paramHash;
			}

			String userlist = (String) dataProps.get("sync-user-id-list");

			paramHash.put("userlist", userlist);
			paramHash.put("result", "ok");
		}
		catch (OpenTMSException e)
		{
			e.printStackTrace();
			paramHash.put("errorstring", OpenTMSSupportFunctions.exceptionToString(e));
			paramHash.put("errorno", "100040");
			paramHash.put("result", "error");
		}
		return paramHash;
	}

	/**
	 * @param paramHash
	 * @return
	 */
	public HashMap<String, String> query(HashMap<String, String> paramHash)
	{
		// here we must retreive the sync files from the data source
		// configuration
		setValues(paramHash);
		String dataSourceConfigurationsFile = (String) paramHash.get("dataSourceConfigurationsFile");
		dataSourceConfigurationsFile = DataSourceConfigurations.getConfigurationFileName(dataSourceConfigurationsFile);

		if (dataSourceConfigurationsFile != null)
		{
			DataSourceConfigurations dsconfig = new DataSourceConfigurations(dataSourceConfigurationsFile);
			String[] syncDataSources = dsconfig.getDataSources(true);
			String ds = "";
			for (int i = 0; i < syncDataSources.length; i++)
			{
				if (i != (syncDataSources.length - 1))
					ds = ds + syncDataSources[i] + ",";
				else
					ds = ds + syncDataSources[i];

				if ((name != null) && syncDataSources[i].equalsIgnoreCase(name))
				{
					String description = de.folt.rpc.connect.Interface.runGetDescriptionOpenTMSDataSource(this.paramHash);
					paramHash.put("description", description);
				}
			}
			if ((name == null) || (name.equals("")))
				paramHash.put("memory-list", ds);
			else
				paramHash.put("memory-list", this.name);
		}
		else
		{
			paramHash.put("result", "error");
			paramHash.put("errorstring",
					OpenTMSConstants.OpenTMS_SYNC_DASTASOURCE_CONFIGURATION_NOT_FOUND_SERVICE_ERROR + "");
			paramHash.put("errorno",
					OpenTMSConstants.OpenTMS_SYNC_DASTASOURCE_CONFIGURATION_NOT_FOUND_SERVICE_ERROR_MESSAGE);
		}

		paramHash.put("result", "ok");
		return paramHash;
	}

	/**
	 * @param paramHash
	 * @return
	 */
	public HashMap<String, String> removeUser(HashMap<String, String> paramHash)
	{
		try
		{
			@SuppressWarnings("unused")
			OpenTMSSQLDataSource otemp = new OpenTMSSQLDataSource();

			String datasourcename = paramHash.get("name"); // the name of the datasource
			String removeuser = paramHash.get("removeuser"); // the name of the datasource

			DataSource datasource = DataSourceInstance.createInstance(datasourcename);
			if (datasource == null)
			{
				paramHash.put("errorstring", "Unknown data source " + datasourcename);
				paramHash.put("errorno", "100050");
				paramHash.put("result", "error");
			}
			DataSourceProperties dataProps = datasource.getDataSourceProperties();

			if (paramHash.containsKey("user-id"))
			{
				String useridlist = datasource.getDataSourceConfigurations().getProperty("name", "sync-user-id-list");
				if ((useridlist != null) && !useridlist.matches((String) paramHash.get("user-id")))
				{
					useridlist = datasource.getDataSourceConfigurations().getProperty("name", "creator");
					if ((useridlist != null) && !useridlist.matches((String) paramHash.get("user-id")))
					{
						paramHash.put("errorstring", "Access denied for " + (String) paramHash.get("user-id"));
						paramHash.put("errorno", "100050");
						paramHash.put("result", "error");
						return paramHash;
					}
				}
			}

			String userlist = (String) dataProps.get("sync-user-id-list");
			if ((userlist == null) || userlist.equals(""))
			{
				paramHash.put("userlist", userlist);
				paramHash.put("errorstring", "sync-user-id-list empty");
				paramHash.put("result", "ok");
				return paramHash;
			}

			paramHash.put("old-userlist", userlist);

			// 19.11.2013
			String[] userListList = userlist.split(Pattern.quote(","));
			String newUserlist = "";
			for (int i = 0; i < userListList.length; i++)
			{
				if (!userListList[i].equals(removeuser))
				{
					if (newUserlist.equals(""))
						newUserlist = newUserlist + userListList[i];
					else
						newUserlist = newUserlist + "," + userListList[i];
				}
			}
			// end 19.11.2013

			/*
			 * userlist = userlist.replaceFirst(Pattern.quote(removeuser), "");
			 * userlist = userlist.replaceFirst(",,", ",");
			 * userlist = userlist.replaceFirst(",$", "");
			 * userlist = userlist.replaceFirst("^,", "");
			 */

			userlist = newUserlist;

			dataProps.put("sync-user-id-list", userlist);
			// datasource.setDataSourceProperties(dataProps);
			datasource.updateDataSourceProperty("sync-user-id-list", userlist);

			paramHash.put("userlist", userlist);
			paramHash.put("result", "ok");
		}
		catch (OpenTMSException e)
		{
			e.printStackTrace();
			paramHash.put("errorstring", OpenTMSSupportFunctions.exceptionToString(e));
			paramHash.put("errorno", "100040");
			paramHash.put("result", "error");
		}
		return paramHash;
	}

	/**
	 * @param paramHash
	 * @return
	 */
	public HashMap<String, String> rename(HashMap<String, String> paramHash)
	{
		paramHash = new HashMap<String, String>();
		OpenTMSSQLDataSource otemp = new OpenTMSSQLDataSource();
		paramHash.put("rename", otemp.currentTimeMillis() + "");
		paramHash.put("errorstring", "Rename not supported.");
		paramHash.put("result", "error");

		return paramHash;
	}

	public void setMethod(String method)
	{
		this.method = method;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setParameter(String parameter)
	{
		this.parameter = parameter;
	}

	public void setParamHash(HashMap<String, String> paramHash)
	{
		this.paramHash = paramHash;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public void setUser(String user)
	{
		this.user_id = user;
	}

	public void setUser_id_list(String user_id_list)
	{
		this.user_id_list = user_id_list;
	}

	/**
	 * @param paramHash
	 */
	private void setValues(HashMap<String, String> paramHash)
	{
		this.paramHash = paramHash;
		user_id = paramHash.get("user-id");
		user_id_list = paramHash.get("user-id-list");
		password = paramHash.get("password");
		name = paramHash.get("name");
		parameter = paramHash.get("parameter");
		method = paramHash.get("method");
	}

	/**
	 * @param paramHash
	 * @return
	 */
	public HashMap<String, String> sync(HashMap<String, String> paramHash)
	{
		setValues(paramHash);
		HashMap<String, String> downresult = download(paramHash);
		if (downresult.get("result").equals("error"))
		{
			downresult.put("errorstring", downresult.get("errorstring")
					+ de.folt.constants.OpenTMSConstants.OpenTMS_SYNC_SYNC_ERROR_ERROR_MESSAGE);
			downresult.put("errorno", de.folt.constants.OpenTMSConstants.OpenTMS_SYNC_SYNC_ERROR + "");
			return downresult;
		}
		HashMap<String, String> upresult = upload(paramHash);
		upresult.put("tmx-document", downresult.get("tmx-document"));
		return upresult;
	}

	/**
	 * @param paramHash
	 * @return
	 */
	public HashMap<String, String> upload(HashMap<String, String> paramHash)
	{
		setValues(paramHash);
		String tmx_document = paramHash.get("tmx-document");
		String update_counter = paramHash.get("update-counter");
		String name = paramHash.get("name");
		String encoding = paramHash.get("encoding");

		Hashtable<String, Object> param = new Hashtable<String, Object>();
		param.put("dataSourceName", name);

		DataSource datasource = null;
		try
		{
			datasource = DataSourceInstance.createInstance(name);
		}
		catch (OpenTMSException e)
		{
			e.printStackTrace();
			paramHash.put("errorstring", OpenTMSSupportFunctions.exceptionToString(e));
			paramHash.put("errorno", "100040");
			paramHash.put("result", "error");
			return paramHash;
		}
		if (datasource == null)
		{
			paramHash.put("errorstring", "Unknown data source " + name);
			paramHash.put("errorno", "100050");
			paramHash.put("result", "error");
			return paramHash;
		}

		@SuppressWarnings("unused")
		DataSourceProperties dataProps = null;
		try
		{
			dataProps = datasource.getDataSourceProperties();
		}
		catch (OpenTMSException e)
		{
			paramHash.put("errorstring", "Unknown data source " + name);
			paramHash.put("errorno", "100050");
			paramHash.put("result", "error");
			return paramHash;
		}

		if (!bCheckUserId(paramHash, datasource))
		{
			return paramHash;
		}

		param.put("sourceDocument", tmx_document);
		param.put("inputDocumentType", "tmx");
		param.put("dataSourceConfigurationsFile", "");
		param.put("update-counter", update_counter);
		param.put("inputDocumentType", "String");
		param.put("sync", "upload");
		if (encoding == null)
			encoding = "";
		param.put("encoding", encoding);
		paramHash.put("result", "ok");

		if (paramHash.containsKey("user-id"))
		{
			param.put("user-id", paramHash.get("user-id"));
		}

		Vector<String> result = de.folt.rpc.connect.Interface.runImportOpenTMSDataSource(param);

		paramHash.remove("tmx-document");
		OpenTMSSQLDataSource os = new OpenTMSSQLDataSource();
		paramHash.put("update-counter", os.currentTimeMillis() + "");
		os = null;
		paramHash.put("result", "ok");
		if (result.size() == 1)
		{
			if (result.get(0).equals(de.folt.constants.OpenTMSConstants.OpenTMS_ID_SUCCESS + ""))
				paramHash.put("result", "ok");
			else
				paramHash.put("result", "error");

		}
		else if (result.size() == 2)
		{
			if (result.get(0).equals(de.folt.constants.OpenTMSConstants.OpenTMS_ID_SUCCESS + ""))
				paramHash.put("result", "ok");
			else
				paramHash.put("result", "error");

			paramHash.put("errorstring", result.get(1));
			paramHash.put("errorno", result.get(0));

		}
		else if (result.size() == 3)
		{
			if (result.get(0).equals(de.folt.constants.OpenTMSConstants.OpenTMS_ID_SUCCESS + ""))
				paramHash.put("result", "ok");
			else
				paramHash.put("result", "error");
			paramHash.put("errorstring", result.get(1));
			paramHash.put("errorno", result.get(0));
			paramHash.put("exception", result.get(2));
		}

		return paramHash;
	}

}
