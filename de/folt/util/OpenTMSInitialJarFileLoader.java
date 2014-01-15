/*
 * Created on 04.04.2010
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.util;

import java.io.File;
import java.util.Vector;

/**
 * @author klemens
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class OpenTMSInitialJarFileLoader
{

	/**
	 * main test function for loading the jar files for data sources and
	 * hibernate
	 * 
	 * @param args
	 */
	public static void main(String args[])
	{
		new OpenTMSInitialJarFileLoader();
	}

	static private Vector<String> loadedJarFiles = new Vector<String>();

	/**
     * 
     */
	public OpenTMSInitialJarFileLoader()
	{
		// Load all the necessary jar files
		String databasejars = OpenTMSProperties.getInstance().getOpenTMSProperty("OpenTMS.dir") + "/lib/datasources";
		String hibernatejars = OpenTMSProperties.getInstance().getOpenTMSProperty("OpenTMS.dir") + "/lib/hibernate";

		File f = new File(databasejars);
		if (!f.exists())
		{
			databasejars = OpenTMSProperties.getInstance().getOpenTMSProperty("OpenTMS.dir") + "/datasources";
			f = new File(databasejars);
			if (f.exists())
				databasejars = f.getAbsolutePath();
		}
		f = new File(hibernatejars);
		if (!f.exists())
		{
			hibernatejars = OpenTMSProperties.getInstance().getOpenTMSProperty("OpenTMS.dir") + "/hibernate";
			f = new File(hibernatejars);
			if (f.exists())
				hibernatejars = f.getAbsolutePath();
		}

		OpenTMSSupportFunctions openTMSSupportFunctions = new OpenTMSSupportFunctions();
		System.out.println("Data Source Jar Root Directory: \"" + databasejars + "\"");
		System.out.println("Hibernate Jar Root Directory:   \"" + hibernatejars + "\"");
		// now load all classes from there
		Vector<String> databases = openTMSSupportFunctions.getAllFiles(databasejars);
		for (int i = 0; i < databases.size(); i++)
		{
			if (databases.get(i).matches(".*jar$"))
			{
				if (!loadedJarFiles.contains(databases.get(i)))
				{
					@SuppressWarnings("unused")
					JarFileLoader cl = new JarFileLoader(databases.get(i), false);
					loadedJarFiles.add(databases.get(i));
				}
			}
		}
		Vector<String> hibernate = openTMSSupportFunctions.getAllFiles(hibernatejars);
		for (int i = 0; i < hibernate.size(); i++)
		{
			if (hibernate.get(i).matches(".*jar$"))
			{
				if (!loadedJarFiles.contains(hibernate.get(i)))
				{
					@SuppressWarnings("unused")
					JarFileLoader cl = new JarFileLoader(hibernate.get(i), false);
					loadedJarFiles.add(hibernate.get(i));
				}
			}
		}
	}

	// 

	public OpenTMSInitialJarFileLoader(String openTMSDir)
	{
		// Load all the necessary jar files
		String databasejars = openTMSDir + "/lib/datasources";
		String hibernatejars = openTMSDir + "/lib/hibernate";

		File f = new File(databasejars);
		if (!f.exists())
		{
			databasejars = openTMSDir + "/datasources";
			f = new File(databasejars);
			if (f.exists())
				databasejars = f.getAbsolutePath();
		}
		f = new File(hibernatejars);
		if (!f.exists())
		{
			hibernatejars = openTMSDir + "/hibernate";
			f = new File(hibernatejars);
			if (f.exists())
				hibernatejars = f.getAbsolutePath();
		}

		OpenTMSSupportFunctions openTMSSupportFunctions = new OpenTMSSupportFunctions();
		System.out.println("Data Source Jar Root Directory: \"" + databasejars + "\"");
		System.out.println("Hibernate Jar Root Directory:   \"" + hibernatejars + "\"");
		// now load all classes from there
		Vector<String> databases = openTMSSupportFunctions.getAllFiles(databasejars);
		for (int i = 0; i < databases.size(); i++)
		{
			if (databases.get(i).matches(".*jar$"))
			{
				if (!loadedJarFiles.contains(databases.get(i)))
				{
					@SuppressWarnings("unused")
					JarFileLoader cl = new JarFileLoader(databases.get(i), false);
					loadedJarFiles.add(databases.get(i));
				}
			}
		}
		Vector<String> hibernate = openTMSSupportFunctions.getAllFiles(hibernatejars);
		for (int i = 0; i < hibernate.size(); i++)
		{
			if (hibernate.get(i).matches(".*jar$"))
			{
				if (!loadedJarFiles.contains(hibernate.get(i)))
				{
					@SuppressWarnings("unused")
					JarFileLoader cl = new JarFileLoader(hibernate.get(i), false);
					loadedJarFiles.add(hibernate.get(i));
				}
			}
		}
	}
}
