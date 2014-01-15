/*
 * Created on 07.07.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.constants;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.sun.star.io.IOException;

/**
 * This class acts as the <b>central version information center</b> of OpenTMS. All applications have to define their version info here. A version info is automatically determined using either
 * getFullVersionString() or getVersionString(). Those methods assume a filed defined like that:<br>
 * The caller class must implement a file (variable) named with the name of the caller class in this class OpenTMSVersionConstants. Two versions are supported: a) A version with the simple name of the
 * class and a version with the long name of a class. In case of the canonical name the "." in the class name path needs to be replaced with "_". These are two examples:
 * 
 * <pre>
 * Simple name version:    public final static String OpenTMSServer = "0.9"; where OpenTMSServer is the simple name of the OpenTMSServer class.
 * Canonical name version: public final static String de_folt_models_applicationmodel_guimodel_editor_datasourceeditor_DataSourceEditor = "0.4.1"; where OpenTMSServer is the canonical name of the class is used.
 * </pre>
 * 
 * Based on this the version is determined using the sun reflection API. See for details the method getFullVersionString(). The version scheme used comes from @see <a
 * href="http://www.everything2.com/index.pl?node_id=1128644">Software version numbering</a> <br>
 * Examples from there:
 * 
 * <pre>
 * 0.5.2 -> (minor revision increase) -> 0.5.3
 * 1.0 -> (moderate revision increase) -> 1.1
 * 2.9 -> (major revision increase) -> 3.0
 * 1.0.0.9 -> (minor revision increase) -> 1.0.1.0
 * 1.00.99 -> (moderate revision increase) -> 1.01.00
 * 5.5 -> (major revision increase) -> 6.0
 * 1.27g -> (minor revision increase) -> 1.27h
 * 1.50.4582 -> (build increase) -> 1.50.4583
 * 5.22.999 -> (build increase) -> 5.22.1000
 * </pre>
 * 
 * @author klemens
 * 
 */
public class OpenTMSVersionConstants
{

	// ----------------------------------------------
	// Insert the version numbers for classes here!!!
	// ----------------------------------------------

	/**
	 * The version of the de_coopmedia_models_documentmodel_tck_Tck Converter
	 */
	public final static String de_coopmedia_models_documentmodel_tck_Tck = "2.0.0";

	/**
	 * OpenTMSVersionConstants version
	 */
	public final static String de_folt_constants_OpenTMSVersionConstants = "0.2.1";

	/**
	 * The version of the data source editor
	 */
	public final static String de_folt_models_applicationmodel_guimodel_editor_datasourceeditor_DataSourceEditor = "0.8.3";

	/**
	 * The version of the data source form editor
	 */
	public final static String de_folt_models_applicationmodel_guimodel_editor_datasourceeditor_DataSourceForm = "0.7.4";

	/**
	 * The version of the BasicDataSource class
	 */
	public final static String de_folt_models_datamodel_BasicDataSource = "0.7.1";

	/**
	 * The version of the Csv data source
	 */
	public final static String de_folt_models_datamodel_csv_Csv = "0.1.1";

	/**
	 * The version of the DataSource interface
	 */
	public final static String de_folt_models_datamodel_DataSource = "0.6.4";

	/**
	 * The version of the GoogleTranslate class
	 */
	public final static String de_folt_models_datamodel_googletranslate_GoogleTranslate = "0.1.2";

	/**
	 * The version of the IateTerminology class
	 */
	public final static String de_folt_models_datamodel_iate_IateTerminology = "0.1.1";

	/**
	 * The version of the MicrosoftTranslate class
	 */
	public final static String de_folt_models_datamodel_microsofttranslate_MicrosoftTranslate = "0.1.2";

	/**
	 * The version of the Moses MT translation system
	 */
	public final static String de_folt_models_datamodel_mtmoses_MTMoses = "0.1.1";

	/**
	 * The version of the de_folt_models_datamodel_multipledatasource_MultipleDataSource data source
	 */
	public final static String de_folt_models_datamodel_multipledatasource_MultipleDataSource = "0.1.2";

	/**
	 * The version of the OpenTMSSQLDataSource class
	 */
	public final static String de_folt_models_datamodel_sql_OpenTMSSQLDataSource = "0.9.0";

	/**
	 * The version of the TbxFileDataSource data source
	 */
	public final static String de_folt_models_datamodel_tbxfile_TbxFileDataSource = "0.3.3";

	/**
	 * The version of the TmxFileDataSource class
	 */
	public final static String de_folt_models_datamodel_tmxfile_TmxFileDataSource = "0.6.5";

	/**
	 * The version of the TradosTMDataSource class
	 */
	public final static String de_folt_models_datamodel_trados_TradosTMDataSource = "0.1.3";

	/**
	 * The version of the XliffFileDataSource class
	 */
	public final static String de_folt_models_datamodel_xlifffile_XliffFileDataSource = "0.6.4";

	/**
	 * The version of the OpenTMS XliffDocument class
	 */

	public final static String de_folt_models_documentmodel_xliff_XliffDocument = "0.2.1";

	/**
	 * The version of the OpenTMS XML RPC Server
	 */
	public final static String de_folt_rpc_webserver_OpenTMSServer = "0.9.3";

	/**
	 * The version of the OpenTMS XML RPC Server shut down class
	 */
	public final static String de_folt_rpc_webserver_Shutdown = "0.9.3";

	/**
	 * The version of the docliff xliff editor class
	 */
	public final static String net_docliff_models_applicationmodel_guimodel_editor_XliffEditor = "0.4.0";

	/**
	 * The version of the XliffEditorWindow data source
	 */
	public final static String net_docliff_models_applicationmodel_guimodel_editor_XliffEditorWindow = "0.4.0";
	
	/**
	 * The version of the SynchronizeService
	 */
	public final static String de_folt_webservices_SynchronizeService = "0.1.2";
	
	/**
	 * The version of the OpenTMSWebServiceImplementation
	 */
	public final static String de_folt_webservices_OpenTMSWebServiceImplementation = "0.1.2";
	
	/**
	 * The version of the de_folt_webservices_OpenTMSWebServiceInterface
	 */
	public final static String de_folt_webservices_OpenTMSWebServiceInterface = "0.1.2";
	

	/**
	 * Recursive method used to find all classes in a given directory and subdirs.
	 * 
	 * @param directory
	 *            The base directory
	 * @param packageName
	 *            The package name for classes found inside the base directory
	 * @return The classes
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("rawtypes")
	private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException
	{
		List<Class> classes = new ArrayList<Class>();
		if (!directory.exists())
		{
			return classes;
		}
		File[] files = directory.listFiles();
		for (File file : files)
		{
			if (file.isDirectory())
			{
				assert !file.getName().contains(".");
				classes.addAll(findClasses(file, packageName + "." + file.getName()));
			}
			else if (file.getName().endsWith(".class"))
			{
				classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
			}
		}
		return classes;
	}

	/**
	 * getAllLoadedClasses
	 */
	@SuppressWarnings("rawtypes")
	public static Vector<Class> getAllLoadedClasses()
	{
		Package[] knownPackages = Package.getPackages();
		Vector<Class> classes = new Vector<Class>();
		for (int i = 0; i < knownPackages.length; i++)
		{
			Package pack = knownPackages[i];
			try
			{
				Class[] knownClasses = getClasses(pack.getName());
				for (int j = 0; j < knownClasses.length; j++)
					classes.addElement(knownClasses[j]);
			}
			catch (ClassNotFoundException e)
			{
				// e.printStackTrace();
			}
			catch (Exception e)
			{
				// e.printStackTrace();
			}
		}
		return classes;
	}

	/**
	 * getAllVersions return all the known version numbers in a string array
	 * 
	 * @return the versions in a field
	 */
	public static String[] getAllVersions()
	{
		try
		{
			Field[] fields = OpenTMSVersionConstants.class.getFields();
			// versionInfoString = field.getName();
			String[] versions = new String[fields.length];
			for (int i = 0; i < fields.length; i++)
			{
				try
				{
					versions[i] = (String) fields[i].getName() + " " + (String) fields[i].get(null);
				}
				catch (Exception e)
				{
					// e.printStackTrace();
				}
			}
			return versions;
		}
		catch (Exception e)
		{
			// e.printStackTrace();
		}
		return null;
	}

	/**
	 * getAllVersions return all the known version numbers in a string array
	 * 
	 * @return the versions in a hash table (key: simple class name; value: version)
	 */
	public static Hashtable<String, String> getAllVersionsAsHashtable()
	{
		try
		{
			Field[] fields = OpenTMSVersionConstants.class.getFields();
			// versionInfoString = field.getName();
			Hashtable<String, String> versions = new Hashtable<String, String>();
			for (int i = 0; i < fields.length; i++)
			{
				try
				{
					versions.put((String) fields[i].getName().replace('_', '.'), (String) fields[i].get(null));
				}
				catch (Exception e)
				{
					// e.printStackTrace();
				}
			}
			return versions;
		}
		catch (Exception e)
		{
			// e.printStackTrace();
		}
		return null;
	}

	/**
	 * getAllVersions return all the known version numbers in a string
	 * 
	 * @return the versions in a field
	 */
	public static String getAllVersionsAsString()
	{
		try
		{
			Field[] fields = OpenTMSVersionConstants.class.getFields();
			// versionInfoString = field.getName();
			String versions = "";
			for (int i = 0; i < fields.length; i++)
			{
				try
				{
					String name = (String) fields[i].getName().replace('_', '.');
					@SuppressWarnings("rawtypes")
					Class classn = null;
					try
					{
						classn = Class.forName(name);
					}
					catch (NoClassDefFoundError e)
					{
						continue;
					}
					catch (ExceptionInInitializerError e)
					{
						continue;
					}

					Date compileDate = null;
					if (classn != null)
						compileDate = de.folt.util.OpenTMSSupportFunctions.getCompileDate(classn);
					versions = versions + name + " " + (String) fields[i].get(null);
					if (compileDate != null)
						versions = versions + " (" + compileDate.toString() + ")";
					versions = versions + "\n";
				}
				catch (Exception e)
				{
					// e.printStackTrace();
				}
			}
			return versions;
		}
		catch (Exception e)
		{
			// e.printStackTrace();
		}
		return "";
	}

	/**
	 * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
	 * 
	 * @param packageName
	 *            The base package
	 * @return The classes
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	private static Class[] getClasses(String packageName) throws ClassNotFoundException, IOException
	{
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		assert classLoader != null;
		String path = packageName.replace('.', '/');
		Enumeration<URL> resources;
		try
		{
			resources = classLoader.getResources(path);
		}
		catch (java.io.IOException e)
		{
			// e.printStackTrace();
			return null;
		}
		List<File> dirs = new ArrayList<File>();
		while (resources.hasMoreElements())
		{
			URL resource = resources.nextElement();
			dirs.add(new File(resource.getFile()));
		}
		ArrayList<Class> classes = new ArrayList<Class>();
		for (File directory : dirs)
		{
			classes.addAll(findClasses(directory, packageName));
		}
		return classes.toArray(new Class[classes.size()]);
	}

	/**
	 * getFullVersionString return the full version string for a (caller) class and the basic version for this class. It automatically determines the version by using the follwoing steps:
	 * 
	 * <pre>
	 * Class classname = sun.reflect.Reflection.getCallerClass(2);
	 * String callingClassName = classname.getSimpleName();
	 * Date compileDate = de.folt.util.OpenTMSSupportFunctions.getCompileDate(classname);
	 * String versionInfoString = &quot;No version found&quot;;
	 * Field field = OpenTMSVersionConstants.class.getField(callingClassName);
	 * versionInfoString = (String) field.get(null);
	 * return versionInfoString + &quot; (&quot; + compileDate.toString() + &quot;)&quot;;
	 * </pre>
	 * 
	 * Example returned: xmlRpcServerVersion = "0.4 (Tue Jul 07 16:13:12 CEST 2009)"
	 * 
	 * @return the full version string or "No version found" if the version could not be determined.
	 */
	public static String getFullVersionString()
	{
		String versionInfoString = "No version found";
		try
		{
			@SuppressWarnings("rawtypes")
			Class classname = sun.reflect.Reflection.getCallerClass(2);
			String callingClassName = classname.getSimpleName();
			Date compileDate = de.folt.util.OpenTMSSupportFunctions.getCompileDate(classname);

			// determine the version string via the name of the class
			// de.folt.OpenTMSVersionConstants.<classname>
			// e.g. de.folt.OpenTMSVersionConstants.OpenTMSServer
			try
			{
				String classVarName = classname.getCanonicalName().replace('.', '_');
				Field field = OpenTMSVersionConstants.class.getField(classVarName);
				if (field == null)
					field = OpenTMSVersionConstants.class.getField(callingClassName);
				// versionInfoString = field.getName();
				versionInfoString = (String) field.get(null);
			}
			catch (SecurityException e)
			{
				// e.printStackTrace();
			}
			catch (NoSuchFieldException e)
			{
				// e.printStackTrace();
			}
			catch (IllegalArgumentException e)
			{
				// e.printStackTrace();
			}
			catch (IllegalAccessException e)
			{
				// e.printStackTrace();
			}

			return versionInfoString + " (" + compileDate.toString() + ")";
		}
		catch (Exception e)
		{
			// e.printStackTrace();
			return versionInfoString;
		}
	}

	/**
	 * getVersionString return the version string of class. Method as follows:
	 * 
	 * <pre>
	 * String versionInfoString = &quot;No version found&quot;;
	 * Class classname = sun.reflect.Reflection.getCallerClass(2);
	 * String callingClassName = classname.getSimpleName();
	 * Field field = OpenTMSVersionConstants.class.getField(callingClassName);
	 * versionInfoString = (String) field.get(null);
	 * </pre>
	 * 
	 * @return the version string or "No version found" if the version could not be determined.
	 */
	public static String getVersionString()
	{
		String versionInfoString = "No version found";
		try
		{
			@SuppressWarnings("rawtypes")
			Class classname = sun.reflect.Reflection.getCallerClass(2);
			return getVersionString(classname);
		}
		catch (Exception e)
		{
			// e.printStackTrace();
			return versionInfoString;
		}
	}

	/**
	 * getVersionString get the version string for a class
	 * 
	 * @param classname
	 *            the class to search for the version
	 * @return the version string
	 */
	public static String getVersionString(@SuppressWarnings("rawtypes") Class classname)
	{
		String versionInfoString = "No version found";
		try
		{
			String callingClassName = classname.getSimpleName();
			@SuppressWarnings("unused")
			Date compileDate = de.folt.util.OpenTMSSupportFunctions.getCompileDate(classname);

			// determine the version string via the name of the class
			// de.folt.OpenTMSVersionConstants.<classname>
			// e.g. de.folt.OpenTMSVersionConstants.OpenTMSServer
			try
			{
				String classVarName = classname.getCanonicalName().replace('.', '_');
				Field field = OpenTMSVersionConstants.class.getField(classVarName);
				if (field == null)
					field = OpenTMSVersionConstants.class.getField(callingClassName);
				// versionInfoString = field.getName();
				versionInfoString = (String) field.get(null);
			}
			catch (SecurityException e)
			{
				// e.printStackTrace();
			}
			catch (NoSuchFieldException e)
			{
				// e.printStackTrace();
			}
			catch (IllegalArgumentException e)
			{
				// e.printStackTrace();
			}
			catch (IllegalAccessException e)
			{
				// e.printStackTrace();
			}
		}
		catch (Exception ex)
		{
			;
		}
		return versionInfoString;
	}

	/**
	 * getVersionString return the version of a class where the class name is a string
	 * 
	 * @param classstringname
	 *            the class name as a string e.g. de.folt.models.datamodel.googletranslate.GoogleTranslate
	 * @return the version of the class a string
	 */
	public static String getVersionString(String classstringname)
	{
		String versionInfoString = "No version found";
		try
		{
			// classstringname = classstringname.replace('.', '_');
			@SuppressWarnings("rawtypes")
			Class classname = Class.forName(classstringname);
			return getVersionString(classname);
		}
		catch (Exception e)
		{
			// e.printStackTrace();
			return versionInfoString;
		}
	}

	public static void main(String[] args)
	{
		new OpenTMSVersionConstants();
		@SuppressWarnings("rawtypes")
		Vector<Class> classes = OpenTMSVersionConstants.getAllLoadedClasses();
		for (int i = 0; i < classes.size(); i++)
		{
			System.out.println(classes.get(i).getName() + " " + de.folt.util.OpenTMSSupportFunctions.getCompileDate(classes.get(i)) + " "
					+ getVersionString(classes.get(i)));
		}

	}
}
