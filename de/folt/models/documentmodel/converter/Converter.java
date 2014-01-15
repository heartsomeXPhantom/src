/*
 * Created on 29.06.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.models.documentmodel.converter;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Hashtable;
import java.util.Vector;

/**
 * @author klemens
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class Converter implements ConverterInterface
{

    /**
     * 
     */
    public Converter()
    {
        // do nothing
    }

    /* (non-Javadoc)
     * @see de.folt.models.documentmodel.converter.ConverterInterface#describeConversionParameters()
     */
    @Override
    public Hashtable<String, Object> describeConversionParameters()
    {
        // do nothing
        return null;
    }

    /* (non-Javadoc)
     * @see de.folt.models.documentmodel.converter.ConverterInterface#describeReverseConversionParameters()
     */
    @Override
    public Hashtable<String, Object> describeReverseConversionParameters()
    {
        // do nothing
        return null;
    }

    /* 
     * This is a concrete implementation of the Interface method. The basic idea here is that a whatever generic conversion procedure can be called. This procedure receives the xliff file name as a string; the source language, the character encoding. The parameters supplied are provided too.
     * @see de.folt.models.documentmodel.converter.ConverterInterface#runConversion(java.util.Hashtable)<br>
     * The minimum key value pairs are:
     * <pre>
     *      String method = (String) parameters.get("method");
     *      String classname = (String) parameters.get("classname");
     *      String sourceDocument = (String) parameters.get("sourceDocument");
     *      String sourceLanguage = (String) parameters.get("sourceDocumentLanguage");
     *      String sourceEncoding = (String) parameters.get("sourceDocumentEncoding");
     *      String jarFile = (String) parameters.get("jarFile");
     * </pre>
     */
    @SuppressWarnings("unchecked")
    @Override
    public Hashtable<String, Object> runConversion(Hashtable<String, Object> parameters)
    {
        Hashtable<String, Object> hashResult = new Hashtable<String, Object>();
        try
        {
            URLClassLoader ucl = null;
            Object[] obj = new Object[4];
            String method = (String) parameters.get("method");
            String classname = (String) parameters.get("classname");

            Class classForClassname = null;
            // check if class loaded
            try
            {
                classForClassname = java.lang.ClassLoader.getSystemClassLoader().loadClass(classname);
            }
            catch (Exception e)
            {
                classForClassname = null;
            }

            String sourceDocument = (String) parameters.get("sourceDocument");
            String sourceLanguage = (String) parameters.get("sourceDocumentLanguage");
            String sourceEncoding = (String) parameters.get("sourceDocumentEncoding");
            String jarFile = (String) parameters.get("jarFile");
            // ok the parameters go here
            obj[0] = sourceDocument;
            obj[1] = sourceLanguage;
            obj[2] = sourceEncoding;
            obj[3] = parameters;
            Vector<String> vec = new Vector<String>();
            // now we need to call the method
            Class[] nullclass = null;
            Object objconv;

            if (classForClassname == null)
            {
                URL[] urls = new URL[1];
                File f = new File(jarFile);
                // now load the jar file
                if (f.exists())
                {
                    System.out.println("Load Jar File: " + f.getAbsolutePath());
                    urls[0] = new URL("file:/" + f.getAbsolutePath());
                }
                else
                {
                    vec = new Vector<String>();
                    vec.add("10041");
                    vec.add("No jar file found : " + f.getAbsolutePath());
                    hashResult.put("resultVector", vec);
                    return hashResult;
                }
                ucl = new URLClassLoader(urls);
                if (ucl == null)
                {
                    System.out.println("URLClassLoader " + f.getAbsolutePath() + " Jar File failed!");
                    vec = new Vector<String>();
                    vec.add("10042");
                    vec.add("URLClassLoader error : " + f.getAbsolutePath());
                    hashResult.put("resultVector", vec);
                    return hashResult;
                }

                classForClassname = ucl.loadClass(classname);
            }

            objconv = classForClassname.newInstance();

            Class< ? extends Object> exeClass = objconv.getClass();
            Object objinst = null;
            Object[] nullpara = null;
            Constructor< ? extends Object> cons;

            cons = exeClass.getConstructor(nullclass);
            objinst = cons.newInstance(nullpara);

            Method[] me = exeClass.getMethods();

            // we check if the supplied method really exists!
            for (int i = 0; i < me.length; i++)
            {
                String currMethod = me[i].getName();
                if (currMethod.equals(method))
                {
                    hashResult = (Hashtable<String, Object>) me[i].invoke((Object) objinst, obj);
                    if (hashResult.get("resultVector") == null)
                    {
                        vec = new Vector<String>();
                        vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_SUCCESS + "");
                        vec.add("No result vector from method " + method + " returned");
                        hashResult.put("resultVector", vec);
                    }
                    return hashResult;
                }

            }
            vec = new Vector<String>();
            vec.add("10039");
            vec.add("No method found : " + method);
            hashResult.put("resultVector", vec);
            return hashResult;
        }
        catch (Exception e)
        {
            Vector<String> vec = new Vector<String>();
            vec.add("10030");
            vec.add(e.getMessage());
            e.printStackTrace();
            hashResult.put("resultVector", vec);
            return hashResult;
        }
    }

    /* (non-Javadoc)
     * @see de.folt.models.documentmodel.converter.ConverterInterface#runReverseConversion(java.util.Hashtable)
     */
    @Override
    public Hashtable<String, Object> runReverseConversion(Hashtable<String, Object> parameters)
    {
        // just pass the parameters to the converter
        return runConversion(parameters);
    }

    /**
     * main 
     * @param args
     */
    public static void main(String[] args)
    {
        // run a very simple araya test
        Converter converter = new Converter();
        Hashtable<String, Object> parameters = new Hashtable<String, Object>();
        parameters.put("method", "runConverter");
        parameters.put("classname", "com.araya.OpenTMS.Interface");
        parameters.put("sourceDocument", args[0]);
        parameters.put("sklDocument", args[0] + ".skl");
        parameters.put("xliffDocument", args[0] + ".xlf");
        parameters.put("sourceDocumentLanguage", args[1]);
        parameters.put("sourceDocumentEncoding", args[2]);

        parameters.put("openTMSTranslationPhase", "CONV");
        // for reverse conversion we assume a fourth parameter whcih should be BACK, default is convert to xliff
        if (args.length >= 4)
        {
            if (args[3].equals("BACK"))
            {
                parameters.put("targetDocumentEncoding", args[2]);
                parameters.put("targetDocumentLanguage", args[1]);
                parameters.put("nopostprocess", "true");
                parameters.put("openTMSTranslationPhase", args[3]);
            }
        }
        String arayaPropertiesFile = (String) de.folt.util.OpenTMSProperties.getInstance().getOpenTMSProperty("ArayaPropertiesFile");
        parameters.put("ArayaPropertiesFile", arayaPropertiesFile);
        // need to get the ar file from the openproperties file...
        String jarFile = (String) de.folt.util.OpenTMSProperties.getInstance().getOpenTMSProperty("ArayaJarFile");
        parameters.put("jarFile", jarFile);
        @SuppressWarnings("unused")
        Hashtable<String, Object> res = converter.runConversion(parameters);
        System.out.println("Finished!");

    }

}
