/*
 * Created on 17.02.2010
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.util;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Vector;

/**
 * @author klemens
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class JarFileLoader
{
    @SuppressWarnings("rawtypes")
	private static final Class[] parameters = new Class[]
        {
            URL.class
        };

    private static URLClassLoader getURLClassLoader(URL jarURL)
    {
        return new URLClassLoader(new URL[]
            {
                jarURL
            });
    }

    public static void main(String args[])
    {
        String filename = "arayaserver-opentms.jar";
        String classname = "com.araya.tmx.TmxConstants";

        if (args.length > 0)
        {
            filename = args[0];
            if (args.length > 1)
            {
                classname = args[1];
            }
            try
            {
                System.out.println("1. try for " + classname);
                Class.forName(classname);
            }
            catch (Exception ex)
            {
                System.out.println("1. try: Failed for" + classname);
            }
            File f = new File(filename);
            @SuppressWarnings("unused")
            JarFileLoader cl = null;
            if (f.exists())
                cl = new JarFileLoader(filename, true);
            else
                cl = new JarFileLoader(filename, false);
            try
            {
                Class.forName(classname);
                System.out.println("2. try: Success for " + classname);
            }
            catch (Exception ex)
            {
                System.out.println("2. try: Failed for " + classname);
                ex.printStackTrace();
            }
        }
        else
        {
            try
            {
                System.out.println("1. try for " + classname);
                Class.forName(classname);
            }
            catch (Exception ex)
            {
                System.out.println("1. try: Failed for" + classname);
            }

            try
            {
                JarFileLoader cl = new JarFileLoader();
                @SuppressWarnings("unused")
                boolean bx = cl.addFile(filename);
                Class.forName(classname);
                System.out.println("2. try: Success for " + classname);
            }
            catch (Exception ex)
            {
                System.out.println("2. try: Failed for " + classname);
                ex.printStackTrace();
            }
        }
    }

    /**
     * 
     */
    public JarFileLoader()
    {

    }

    /**
     * Dynamically load jar files given in a file (separated there with ";") 
     * @param jarName use the file name to get the list of jar files to add to the system class loader
     */
    public JarFileLoader(String jarName, boolean bIsFile)
    {
        try
        {
            if (bIsFile)
            {
                String newname = de.folt.util.OpenTMSSupportFunctions.readFileIntoString(jarName, "UTF-8");
                newname = newname.replaceAll("\r\n", ";");
                newname = newname.replaceAll("\r", ";");
                newname = newname.replaceAll("\n", ";");
                new JarFileLoader(newname.split(";"));
            }
            else
                this.addFile(jarName);
        }
        catch (MalformedURLException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Dynamically load a jar file with name jarName
     * @param jarName add this jar file
     */
    public JarFileLoader(String jarName)
    {
        try
        {
            if (jarName.indexOf(";") > -1)
            {
                String[] jarNames = jarName.split(";");
                System.out.println("Number of jar file to load: " + jarNames.length);
                new JarFileLoader(jarNames);
                return;
            }
            this.addFile(jarName);
        }
        catch (MalformedURLException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Dynamically load jar files from a string array
     * @param jarNames add many jar files from String array
     */
    public JarFileLoader(String[] jarNames)
    {
        for (int i = 0; i < jarNames.length; i++)
            try
            {
                this.addFile(jarNames[i]);
            }
            catch (MalformedURLException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
    }

    /**
     * Dynamically load jar files from a vector of strings
     * @param jarNames add many jar files from Vector of strings
     */
    public JarFileLoader(Vector<String> jarNames)
    {
        for (int i = 0; i < jarNames.size(); i++)
            try
            {
                this.addFile(jarNames.get(i));
            }
            catch (MalformedURLException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
    }

    /**
     * addFile add a jar file incl classes - taken from hhttp://stackoverflow.com/questions/60764/how-should-i-load-jars-dynamically-at-runtime and adapted
     * @param jarName the the file to add to the classpath
     * @return true if successful
     * @throws MalformedURLException
     */
    @SuppressWarnings(
        {
                "deprecation", "unchecked"
        })
    public boolean addFile(String jarName) throws MalformedURLException
    {
        File f = new File(jarName);
        if (!f.exists())
        {
            File curDir = new File(".");
            
        	System.out.println(curDir.getAbsolutePath() + ": Jar file \"" + jarName +  "\" (" + f.getAbsolutePath() + ") does not exist");
            return false;
        }
        else 
            System.out.println("Load Jar file \"" + jarName + "\" (" + f.getAbsolutePath() + ")");
        jarName = f.getAbsolutePath();
        f = new File(jarName);
        @SuppressWarnings("unused")
        URLClassLoader urlLoader = getURLClassLoader(new URL("file", null, jarName));
        URLClassLoader sysLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        @SuppressWarnings("rawtypes")
		Class sysclass = URLClassLoader.class;

        try
        {
            URL u = f.toURL();
            Method method = sysclass.getDeclaredMethod("addURL", parameters);
            method.setAccessible(true);
            method.invoke(sysLoader, new Object[]
                {
                    u
                });
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }

        return true;

    }

}
