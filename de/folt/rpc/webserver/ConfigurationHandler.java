/*
 * Created on 16.09.2008
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.rpc.webserver;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import de.folt.util.OpenTMSProperties;

/**
 * @author   klemens  To change the template for this generated type comment go to  Window - Preferences - Java - Code Generation - Code and Comments
 */
public class ConfigurationHandler
{

    /**
     */
    private Hashtable<String, Object> transletTable = null;

    /**
     */
    URLClassLoader ucl = null;

    @SuppressWarnings("unused")
    private URL[] urls = null;

    private String xmlConfigurationFile = "defaultConfig.xml";

    /**
     * @param xmlConfigurationFile
     */
    public ConfigurationHandler(String xmlConfigurationFile)
    {
        super();
        this.xmlConfigurationFile = xmlConfigurationFile;

        // ok need to parse configuration file now
        try
        {
            File f = new File(xmlConfigurationFile);
            if (!f.exists())
                return;
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            // DOMImplementation imp = builder.newDocument().getImplementation();

            org.w3c.dom.Document document = builder.parse(xmlConfigurationFile);
            org.w3c.dom.Element root = document.getDocumentElement();
            // String application = root.getAttribute("app");
            loadJarFiles(root);
            loadTranslets(root);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return;
        }
    }

    /**
     * addTranslet
     * 
     * @param translet
     * @return
     */
    private boolean addTranslet(org.w3c.dom.Element translet)
    {
        try
        {

            String name = translet.getAttribute("name");

            System.out.println("Translet " + name);

            org.w3c.dom.NodeList transletClassList = translet.getElementsByTagName("translet-class");
            org.w3c.dom.Element transletClassElement = (org.w3c.dom.Element) transletClassList.item(0);
            String transletClass = transletClassElement.getTextContent();
            System.out.println("transletClass " + transletClass);

            org.w3c.dom.NodeList transletMethodList = translet.getElementsByTagName("translet-method");
            org.w3c.dom.Element transletMethodElement = (org.w3c.dom.Element) transletMethodList.item(0);
            String transletMethod = transletMethodElement.getTextContent();
            System.out.println("transletMethod " + transletMethod);

            String fullTransletMethod = transletClass + "." + transletMethod;
            System.out.println("fullTransletMethod " + fullTransletMethod);

            org.w3c.dom.NodeList transletparamsList = translet.getElementsByTagName("params");
            org.w3c.dom.Element transletparamsElement = (org.w3c.dom.Element) transletparamsList.item(0);

            Hashtable<String, Param> hashParams = new Hashtable<String, Param>();

            org.w3c.dom.NodeList transletparamList = transletparamsElement.getElementsByTagName("param");
            for (int i = 0; i < transletparamList.getLength(); i++)
            {
                org.w3c.dom.Element param = (org.w3c.dom.Element) transletparamList.item(i);
                String paraname = param.getAttribute("name");
                if (paraname == null)
                    continue;
                if (paraname.equals(""))
                    continue;
                String mapTo = param.getAttribute("map-to");
                if ((mapTo == null) || (mapTo.equals("")))
                    mapTo = paraname;
                String type = param.getAttribute("type");
                String content = param.getTextContent();

                if (type != null)
                {
                    if (type.equals("OpenTMSProperties"))
                        content = OpenTMSProperties.getInstance().getOpenTMSProperty(paraname);
                }
                Param parameter = new Param(paraname, mapTo, type, content);
                System.out.println("Param " + i + ": " + paraname + "==>>" + mapTo + " / " + type + " / " + content);
                hashParams.put(paraname, parameter);
            }

            Translet transletImpl = new Translet(transletClass, transletMethod, hashParams);
            transletTable.put(name, transletImpl);

            return true;

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }

    }

    /**
     * bMethodSupported
     * 
     * @param method
     * @return true if method is supported
     */
    public boolean bMethodSupported(String method)
    {
        if (transletTable == null)
            return false;

        return transletTable.containsKey(method);
    }

    /**
     * executeTranslet executes a Translet
     * 
     * @param message which should be eecutes 
     * @param hashParams contains the parameters for the execution
     * @return a Vector with result of the execution
     */
    @SuppressWarnings("unchecked")
    public Vector<String> executeTranslet(String message, Hashtable<String, String> hashParams)
    {
        Vector<String> vec = new Vector<String>();
        try
        {
            // classname = <translet-class>com.araya.OpenTMS.Interface</translet-class> + <translet-method>runCreateTMXDB</translet-method>
            String classname = getClassName(message);
            // method = <translet-method>runCreateTMXDB</translet-method>
            String method = getMethod(message);
            // classname.method
            // String fullQualifiedMethod = getMethodFullName(message);

            Hashtable<String, String> newHashParams = mapParams(message, hashParams);
            System.out.println("Found External Execute class: " + classname + "." + method + " for " + message);

            // ok, bug was here ucl was not set due to the new method of loading 
            // set ucl to the system class loader...
            ucl = (URLClassLoader) ClassLoader.getSystemClassLoader();
            // 
            Object objconv = ucl.loadClass(classname).newInstance();
            Class< ? extends Object> exeClass = objconv.getClass();

            Method[] me = exeClass.getMethods();
            int iWhichMethod = -1;
            for (int i = 0; i < me.length; i++)
            {
                Method m = me[i];
                Type[] t = m.getGenericParameterTypes();
                String types = "";
                for (int j = 0; j < t.length; j++)
                {
                    Type tm = t[j];
                    types = types + tm.toString();
                }
                if (method.equals(m.getName()))
                    iWhichMethod = i;
                if (iWhichMethod != -1)
                {
                    System.out.println("Found:" + classname + " : " + m.getName() + " / " + types);
                    break;
                }
                else
                {
                    // System.out.println(classname + " : " + m.getName() + " / " + types);
                }

            }

            if (iWhichMethod == -1)
            {
                vec = new Vector<String>();
                vec.add("9025");
                vec.add("No method found : " + method);
                return vec;
            }

            Object[] obj = new Object[1];
            obj[0] = newHashParams;
            // Erzeuge Instanz
            Class[] nullclass = null;
            Constructor< ? extends Object> cons = exeClass.getConstructor(nullclass);
            Object[] nullpara = null;
            Object objinst = cons.newInstance(nullpara);
            vec = (Vector<String>) me[iWhichMethod].invoke((Object) objinst, obj);

            return vec;
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
            vec = new Vector<String>();
            vec.add("9005");
            vec.add("No requested service : " + e.getMessage());
        }
        catch (InstantiationException e)
        {
            e.printStackTrace();
            vec.add("9010");
            vec.add(e.getMessage());
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
            vec.add("9015");
            vec.add(e.getMessage());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            vec = new Vector<String>();
            vec.add("9020");
            vec.add(ex.getMessage());
        }

        return vec;
    }

    /**
     * getClassName
     * 
     * @param method the class name of the method
     * @return
     */
    public String getClassName(String method)
    {
        Translet transletImpl = (Translet) transletTable.get(method);

        return transletImpl.getTransletClass();
    }

    /**
     * getMethod
     * 
     * @param method
     * @return the method
     */
    public String getMethod(String method)
    {
        Translet transletImpl = (Translet) transletTable.get(method);

        return transletImpl.getTransletMethod();
    }

    /**
     * getMethodFullName
     * 
     * @param method
     * @return the full (qualified) name of the method
     */
    public String getMethodFullName(String method)
    {
        Translet transletImpl = (Translet) transletTable.get(method);

        return transletImpl.getFullTransletMethod();
    }

    /**
     * @return   the transletTable
     * @uml.property  name="transletTable"
     */
    public Hashtable<String, Object> getTransletTable()
    {
        return transletTable;
    }

    /**
     * @return   the ucl
     * @uml.property  name="ucl"
     */
    public URLClassLoader getUcl()
    {
        return ucl;
    }

    /**
     * loadJarFiles
     * 
     * @param root
     */
    private int loadJarFiles(org.w3c.dom.Element root)
    {
        int k = 0;
        try
        {
            String jarFileNamesAsString = "";
            org.w3c.dom.NodeList jarFiles = root.getElementsByTagName("jar-files");
            int iNumJarFiles = 0;
            for (int j = 0; j < jarFiles.getLength(); j++)
            {
                org.w3c.dom.Element jarFilesElement = (org.w3c.dom.Element) jarFiles.item(j);
                org.w3c.dom.NodeList jarFilesList = jarFilesElement.getElementsByTagName("jar-file");
                for (int i = 0; i < jarFilesList.getLength(); i++)
                {
                    org.w3c.dom.Element jarFile = (org.w3c.dom.Element) jarFilesList.item(i);
                    if (jarFile.getNodeName().equals("jar-file"))
                    {
                        String jarFileName = jarFile.getAttribute("name");
                        File f = new File(jarFileName);
                        // now load the jar file
                        if (f.exists())
                        {
                            iNumJarFiles++;
                            if (jarFileNamesAsString.equals(""))
                                jarFileNamesAsString = jarFileName;
                            else
                                jarFileNamesAsString = jarFileNamesAsString + ";" + jarFileName;
                        }
                    }
                }
            }

            System.out.println("Jar Files to load: \"" + jarFileNamesAsString + "\"");
            
            new de.folt.util.JarFileLoader(jarFileNamesAsString);
            try
            {
                k = jarFileNamesAsString.split(";").length;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                k = -1;
            }
            /*
            URL[] urls = new URL[iNumJarFiles];

            for (int j = 0; j < jarFiles.getLength(); j++)
            {
                org.w3c.dom.Element jarFilesElement = (org.w3c.dom.Element) jarFiles.item(j);
                org.w3c.dom.NodeList jarFilesList = jarFilesElement.getElementsByTagName("jar-file");
                for (int i = 0; i < jarFilesList.getLength(); i++)
                {
                    org.w3c.dom.Element jarFile = (org.w3c.dom.Element) jarFilesList.item(i);
                    if (jarFile.getNodeName().equals("jar-file"))
                    {
                        String jarFileName = jarFile.getAttribute("name");
                        File f = new File(jarFileName);
                        // now load the jar file
                        if (f.exists())
                        {
                            System.out.println("Load Jar File: " + i + "/" + k + " " + f.getAbsolutePath());

                            urls[k] = new URL("file:/" + f.getAbsolutePath());
                            k++;
                        }
                    }
                    else
                    {
                        System.out.println("Unknown element name: " + i + "/" + jarFile.getNodeName());
                    }
                }
                ucl = new URLClassLoader(urls);
                if (ucl == null)
                {
                    System.out.println("Loading " + k + " Jar Files failed!");
                }
            }
            
            */

            return k;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return k;
        }
    }

    /**
     * loadTranslets
     * 
     * @param root
     * @return
     */
    private int loadTranslets(org.w3c.dom.Element root)
    {
        int k = 0;
        transletTable = new Hashtable<String, Object>();
        try
        {
            org.w3c.dom.NodeList translets = root.getElementsByTagName("translet");

            for (int i = 0; i < translets.getLength(); i++)
            {
                System.out.println("Loading Translet " + i);
                org.w3c.dom.Element translet = (org.w3c.dom.Element) translets.item(i);
                addTranslet(translet);
            }

            return k;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return k;
        }
    }

    /**
     * mapParams maps the parameters of the input hash table to the parameters of the method map table
     * 
     * @param method
     * @param inhash
     * @return mapped hash table of the input hash table
     */
    public Hashtable<String, String> mapParams(String method, Hashtable<String, String> inhash)
    {
        Hashtable<String, String> outhash = new Hashtable<String, String>();
        Translet transletImpl = (Translet) transletTable.get(method);

        if (transletImpl == null)
            return null;

        Hashtable<String, Param> map = transletImpl.getParamTable(); // Param parameter / hashParams.put(paraname, parameter);
        Enumeration<String> en = inhash.keys();
        while (en.hasMoreElements())
        {
            String paramname = en.nextElement();
            String value = inhash.get(paramname);
            System.out.println("ConfigurationHandler: mapParams: " + paramname + " / " + value);
            if (value == null)
                value = "";
            if (map.containsKey(paramname))
            {
                Param parameter = (Param) map.get(paramname);
                String newParaName = parameter.getMapTo();
                if (parameter.getType().equals("OpenTMSProperties"))
                {
                    value = (String) parameter.getValue();
                    if (value == null)
                        value = "";
                    outhash.put(newParaName, value);
                    System.out.println("PROP: " + method + ": " + paramname + " > " + newParaName + " >> \"" + value + "\"");
                }
                else
                {
                    outhash.put(newParaName, value);
                    System.out.println("STANDARD:" + method + ": " + paramname + " > " + newParaName + " >> \"" + value + "\"");
                }
            }
            else if (paramname.equals("message"))
            {
                outhash.put(paramname, value);
                System.out.println("MESS:" + method + ": " + paramname + " >> \"" + value + "\"");
            }
            else
            {
                outhash.put(paramname, value);
                System.out.println("NOMAP:" + method + ": " + paramname + " >> \"" + value + "\"");
            }
        }

        // we need now to add all properties which are fixed in the config file (OpenTMSProperties)
        Enumeration<Param> enpa = map.elements();
        while (enpa.hasMoreElements())
        {
            Param parameter = enpa.nextElement();
            String type = parameter.getType();
            if ((type != null) && !type.equals(""))
            {
                String value = (String) parameter.getValue();
                String paramname = (String) parameter.getName();
                outhash.put(paramname, value);
                System.out.println("PROP (from " + xmlConfigurationFile + "): " + method + ": " + paramname + " > \"" + value + "\"");
            }
        }

        return outhash;
    }

    /**
     * @param transletTable   the transletTable to set
     * @uml.property  name="transletTable"
     */
    public void setTransletTable(Hashtable<String, Object> transletTable)
    {
        this.transletTable = transletTable;
    }

    /**
     * @param ucl   the ucl to set
     * @uml.property  name="ucl"
     */
    public void setUcl(URLClassLoader ucl)
    {
        this.ucl = ucl;
    }
}
