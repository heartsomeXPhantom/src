/*
 * Created on 11.12.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.models.datamodel.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Vector;

import de.folt.models.datamodel.DataSourceConfigurations;
import de.folt.models.datamodel.DataSourceProperties;
import de.folt.models.datamodel.ExtendedBasicDataSource;
import de.folt.models.datamodel.MonoLingualObject;
import de.folt.models.datamodel.MultiLingualObject;
import de.folt.util.OpenTMSException;

/**
 * @author klemens
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class Csv extends ExtendedBasicDataSource
{

    private Vector<String> languagesLoaded = new Vector<String>();

    private int multiID = 1;

    /**
     * 
     */
    public Csv()
    {
        super();
    }

    /**
     * @param dataSourceProperties
     */
    public Csv(DataSourceProperties dataSourceProperties)
    {
        super(dataSourceProperties);
    }

    /**
     * @param propertiesFileName
     */
    public Csv(String propertiesFileName)
    {
        super(propertiesFileName);
    }

    /* (non-Javadoc)
     * @see de.folt.models.datamodel.BasicDataSource#addMonoLingualObject(de.folt.models.datamodel.MonoLingualObject, boolean)
     */
    @Override
    public boolean addMonoLingualObject(MonoLingualObject monoLingualObject, boolean mergeObjects)
    {
        if (!this.languagesLoaded.contains(monoLingualObject.getLanguage()))
            this.languagesLoaded.add(monoLingualObject.getLanguage());
        return super.addMonoLingualObject(monoLingualObject, mergeObjects);
    }

    /* (non-Javadoc)
     * @see de.folt.models.datamodel.BasicDataSource#addMultiLingualObject(de.folt.models.datamodel.MultiLingualObject, boolean)
     */
    @Override
    public boolean addMultiLingualObject(MultiLingualObject multiLingualObject, boolean mergeObjects)
    {
        Vector<MonoLingualObject> monos = multiLingualObject.getMonoLingualObjectsAsVector();
        for (int j = 0; j < monos.size(); j++)
        {
            MonoLingualObject monoLingualObject = monos.get(j);
            if (!this.languagesLoaded.contains(monoLingualObject.getLanguage()))
                this.languagesLoaded.add(monoLingualObject.getLanguage());
        }
        return super.addMultiLingualObject(multiLingualObject, mergeObjects);
    }

    /* (non-Javadoc)
     * @see de.folt.models.datamodel.BasicDataSource#bPersist()
     */
    @Override
    public boolean bPersist()
    {
        if (!this.bChanged)
            return false;

        String dataSourceName = (String) dataSourceProperties.get("dataSourceName");
        String codepage = (String) this.dataSourceProperties.getDataSourceProperty("encoding");
        if (codepage == null)
            codepage = (String) dataSourceProperties.get("codepage");
        if (codepage == null)
            codepage = "UTF-8";

        String seperator = (String) this.dataSourceProperties.getDataSourceProperty("seperator");
        if (seperator == null)
            seperator = ";";

        try
        {
            FileOutputStream sourceOut = new FileOutputStream(dataSourceName);
            Writer sourceOutWriter = new OutputStreamWriter(sourceOut, codepage);
            String outString = "";
            for (int i = 0; i < languagesLoaded.size(); i++)
            {
                outString = outString + languagesLoaded.get(i);
                if (i != (languagesLoaded.size() - 1))
                {
                    outString = outString + seperator;
                }
            }
            sourceOutWriter.write(outString + "\n");
            this.initEnumeration();
            while (this.hasMoreElements())
            {
                MultiLingualObject multi = this.nextElement();
                outString = "";
                for (int i = 0; i < languagesLoaded.size(); i++)
                {
                    Vector<MonoLingualObject> sourceMonos = multi.getMonoLingualObjectsAsVector(languagesLoaded.get(i));
                    if (sourceMonos.size() == 0)
                        continue;
                    outString = outString + sourceMonos.get(0).getFormattedSegment();
                    if (i != (languagesLoaded.size() - 1))
                    {
                        outString = outString + seperator;
                    }
                }
                sourceOutWriter.write(outString + "\n");
            }
            sourceOutWriter.close();
            sourceOut.close();
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
            return true;
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            return true;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return true;
        }

        return true;
    }

    /* (non-Javadoc)
     * @see de.folt.models.datamodel.BasicDataSource#createDataSource(de.folt.models.datamodel.DataSourceProperties)
     */
    @Override
    public boolean createDataSource(DataSourceProperties dataModelProperties) throws OpenTMSException
    {
        if (this.dataSourceProperties == null)
            this.dataSourceProperties = dataModelProperties;
        String dataSourceName = (String) dataModelProperties.get("dataSourceName");
        File f = new File(dataSourceName);
        if (!f.exists())
        {
            try
            {
                f.createNewFile();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        String dataSourceConfigurationsFile = (String) dataModelProperties.get("dataSourceConfigurationsFile");
        dataSourceConfigurationsFile = DataSourceConfigurations.getConfigurationFileName(dataSourceConfigurationsFile);
        if (dataSourceConfigurationsFile == null)
        {
            return false;
        }
        dataSourceProperties.put("dataSourceConfigurationsFile", dataSourceConfigurationsFile);
        DataSourceConfigurations config = new DataSourceConfigurations(dataSourceConfigurationsFile);
        if (config.bDataSourceExistsInConfiguration(dataSourceName))
            return true;
        DataSourceProperties props = new DataSourceProperties();
        props.put("dataSourceConfigurationsFile", dataSourceConfigurationsFile);
        String encoding = (String) this.dataSourceProperties.getDataSourceProperty("encoding");
        if (encoding == null)
            encoding = (String) dataSourceProperties.get("codepage");
        if (encoding == null)
        {
            encoding = "UTF-8";
            dataSourceProperties.put("encoding", encoding);
        }
        if (dataSourceName != null)
            props.put("dataSourceName", dataSourceName);
        props.put("encoding", encoding);
        String seperator = (String) this.dataSourceProperties.getDataSourceProperty("seperator");
        if (seperator == null)
            seperator = ";";
        props.put("seperator", seperator);
        config.addConfiguration(dataSourceName, this.getDataSourceType(), props);
        config.saveToXmlFile();

        return true;
    }

    @Override
    public String getDataSourceType()
    {
        return this.getClass().getName();
    }

    /* (non-Javadoc)
     * @see de.folt.models.datamodel.ExtendedBasicDataSource#load()
     */
    @Override
    protected void load()
    {
        languagesLoaded = new Vector<String>();
        String dataSourceName = (String) dataSourceProperties.get("dataSourceName");
        String codepage = (String) this.dataSourceProperties.getDataSourceProperty("encoding");
        if (codepage == null)
            codepage = (String) dataSourceProperties.get("codepage");
        if (codepage == null)
        {
            codepage = "UTF-8";
            dataSourceProperties.put("encoding", codepage);
        }
        String seperator = (String) this.dataSourceProperties.getDataSourceProperty("seperator");
        if (seperator == null)
        {
            seperator = ";";
            dataSourceProperties.put("seperator", seperator);
        }
        try
        {
            String newdatasourcefile = de.folt.util.OpenTMSSupportFunctions.removeBOMFromFile(dataSourceName);
            if (!newdatasourcefile.equals(""))
            {
                System.out.println("BOM removed from " + dataSourceName);
            }
            else
            {
                newdatasourcefile = dataSourceName;
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(newdatasourcefile), codepage));
            String languagesstring = in.readLine();
            if (languagesstring == null)
                return;
            String languagesLoadedString[] = languagesstring.split(seperator);
            if (languagesLoadedString.length == 0)
                return;

            for (int i = 0; i < languagesLoadedString.length; i++)
            {
                languagesLoaded.add(languagesLoadedString[i] + "");
            }

            String line = "";

            while ((line = in.readLine()) != null)
            {
                String[] entries = line.split(seperator);
                if (entries.length != languagesLoadedString.length)
                    continue;
                MultiLingualObject multi = new MultiLingualObject();
                multi.setId(multiID++);
                for (int i = 0; i < entries.length; i++)
                {
                    String segment = entries[i] + "";
                    String language = languagesLoadedString[i] + "";
                    segment = segment.replaceAll("&", "&amp;");
                    segment = segment.replaceAll("<", "&lt;");
                    segment = segment.replaceAll(">", "&gt;");
                    MonoLingualObject mono = new MonoLingualObject();
                    mono.setLanguage(language);
                    mono.setFormattedSegment(segment);
                    mono.setPlainTextSegment(segment);
                    multi.addMonoLingualObject(mono);
                }
                super.addMultiLingualObject(multi, false);
            }
            in.close();

        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        bChanged = false; // we have just read it...

        return;
    }

}
