/*
 * Created on 19.11.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.models.datamodel.parallelcorpus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Hashtable;
import java.util.Vector;

import org.jdom.Element;

import de.folt.constants.OpenTMSConstants;
import de.folt.models.datamodel.BasicDataSource;
import de.folt.models.datamodel.DataSourceConfigurations;
import de.folt.models.datamodel.DataSourceProperties;
import de.folt.models.datamodel.MonoLingualObject;
import de.folt.models.datamodel.MultiLingualObject;
import de.folt.models.documentmodel.xliff.XliffDocument;
import de.folt.util.OpenTMSException;

/**
 * @author klemens
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class ParallelCorpus extends BasicDataSource
{

    /**
     * main 
     * @param args
     */
    public static void main(String[] args)
    {
        // TODO Auto-generated method stub

    }

    /**
     * 
     */
    public ParallelCorpus()
    {
        super();
        languagesLoaded = new Hashtable<String, String>();
    }

    Hashtable<String, String> languagesLoaded = null;

    /**
     * @param dataSourceProperties
     */
    public ParallelCorpus(DataSourceProperties dataSourceProperties)
    {
        super(dataSourceProperties);
        languagesLoaded = new Hashtable<String, String>();
        String dataSourceName = (String) dataSourceProperties.get("dataSourceName");
        // basically needed just for creating the data source. But not really needed - in case of translation the corlus needs to be loaded anyway.
        String sourceLanguage = (String) dataSourceProperties.get("sourceLanguage");
        String targetLanguage = (String) dataSourceProperties.get("targetLanguage");
        // read the entries and store them
        File fsource = new File(dataSourceName + "." + sourceLanguage);
        if (!fsource.exists())
        {
            this.setLastErrorCode(OpenTMSConstants.OpenTMS_ID_SUCCESS);
            return;
        }
        File ftarget = new File(dataSourceName + "." + targetLanguage);
        if (!ftarget.exists())
        {
            this.setLastErrorCode(OpenTMSConstants.OpenTMS_ID_SUCCESS);
            return;
        }
        loadParallelCorpusEntries(dataSourceName + "." + sourceLanguage, dataSourceName + "." + targetLanguage);

    }

    /**
     * @param propertiesFileName
     */
    public ParallelCorpus(String propertiesFileName)
    {
        super(propertiesFileName);
        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see de.folt.models.datamodel.BasicDataSource#bPersist()
     */
    @Override
    public boolean bPersist()
    {
        String sourceLanguage = (String) dataSourceProperties.get("sourceLanguage");
        String targetLanguage = (String) dataSourceProperties.get("targetLanguage");
        String dataSourceName = (String) dataSourceProperties.get("dataSourceName");
        String encoding = (String) dataSourceProperties.get("encoding");
        if (encoding == null)
            encoding = (String) dataSourceProperties.get("codepage");
        if (encoding == null)
            encoding = "ISO-8859-1";
        String sourceFile = dataSourceName + "." + sourceLanguage;
        String targetFile = dataSourceName + "." + targetLanguage;

        try
        {
            FileOutputStream sourceOut = new FileOutputStream(sourceFile);
            Writer sourceOutWriter = new OutputStreamWriter(sourceOut, encoding);

            FileOutputStream targetOut = new FileOutputStream(targetFile);
            Writer targetOutWriter = new OutputStreamWriter(targetOut, encoding);

            this.initEnumeration();
            while (this.hasMoreElements())
            {
                MultiLingualObject multi = this.nextElement();
                Vector<MonoLingualObject> sourceMonos = multi.getMonoLingualObjectsAsVector(sourceLanguage);
                if (sourceMonos.size() == 0)
                    continue;
                Vector<MonoLingualObject> targetMonos = multi.getMonoLingualObjectsAsVector(targetLanguage);
                if (targetMonos.size() == 0)
                    continue;
                sourceOutWriter.write(sourceMonos.get(0).getFormattedSegment() + "\n");
                targetOutWriter.write(targetMonos.get(0).getFormattedSegment() + "\n");
            }
            targetOutWriter.close();
            sourceOutWriter.close();
            targetOut.close();
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
     * @see de.folt.models.datamodel.BasicDataSource#cleanDataSource()
     */
    @Override
    public void cleanDataSource()
    {
        // TODO Auto-generated method stub
        super.cleanDataSource();
    }

    /* (non-Javadoc)
     * @see de.folt.models.datamodel.BasicDataSource#clearDataSource()
     */
    @Override
    public boolean clearDataSource() throws OpenTMSException
    {
        // TODO Auto-generated method stub
        return super.clearDataSource();
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
        String sourceLanguage = (String) dataSourceProperties.get("sourceLanguage");
        String targetLanguage = (String) dataSourceProperties.get("targetLanguage");
        String encoding = (String) dataSourceProperties.get("encoding");
        if (dataSourceName != null)
            props.put("dataSourceName", dataSourceName);
        if (targetLanguage != null)
            props.put("targetLanguage", targetLanguage);
        if (sourceLanguage != null)
            props.put("sourceLanguage", sourceLanguage);
        if (encoding != null)
            props.put("encoding", encoding);
        // props.put("datasourcetype", "de.folt.models.datamodel.tbxfile.TmxFileDataSource");

        config.addConfiguration(dataSourceName, this.getDataSourceType(), props);
        config.saveToXmlFile();

        return true;
    }

    /* (non-Javadoc)
     * @see de.folt.models.datamodel.BasicDataSource#getDataSourceType()
     */
    /* (non-Javadoc)
     * @see de.folt.models.datamodel.BasicDataSource#getDataSourceType()
     */
    @Override
    public String getDataSourceType()
    {
        return this.getClass().getName();
    }

    private int loadParallelCorpusEntries(String sourceFile, String targetFile)
    {
        String sourceLanguage = (String) dataSourceProperties.get("sourceLanguage");
        String targetLanguage = (String) dataSourceProperties.get("targetLanguage");
        String encoding = (String) dataSourceProperties.get("encoding");
        if (encoding == null)
            encoding = "ISO-8859-1";
        int multiID = 0;
        try
        {
            BufferedReader sourceBuffer = new BufferedReader(new InputStreamReader(new FileInputStream(sourceFile), encoding));
            BufferedReader targetBuffer = new BufferedReader(new InputStreamReader(new FileInputStream(targetFile), encoding));
            String sourceLine = "";
            String targetLine = "";

            while (((sourceLine = sourceBuffer.readLine()) != null) && ((targetLine = targetBuffer.readLine()) != null))
            {
                if (sourceLine.equals(""))
                    continue;
                if (targetLine.equals(""))
                    continue;

                MultiLingualObject multi = new MultiLingualObject();
                multi.setId(multiID++);
                MonoLingualObject sourceMono = new MonoLingualObject();
                sourceMono.setLanguage(sourceLanguage);
                sourceMono.setFormattedSegment(sourceLine);
                sourceMono.setPlainTextSegment(sourceLine);

                MonoLingualObject targetMono = new MonoLingualObject();
                targetMono.setLanguage(targetLanguage);
                targetMono.setFormattedSegment(targetLine);
                targetMono.setPlainTextSegment(targetLine);

                Vector<MonoLingualObject> sourceresult = this.search(sourceMono, null);
                Vector<MonoLingualObject> targetresult = this.search(targetMono, null);
                boolean bFound = false;
                if ((sourceresult != null) && (sourceresult.size() > 0) && (targetresult != null) && (targetresult.size() > 0))
                {
                    for (int i = 0; i < sourceresult.size(); i++)
                    {
                        for (int j = 0; j < sourceresult.size(); j++)
                        {
                            if (sourceresult.get(i).getParentMultiLingualObject() == targetresult.get(j).getParentMultiLingualObject())
                            {
                                bFound = true;
                                break;
                            }
                        }
                        if (bFound)
                            break;
                    }
                    if (bFound)
                        continue;
                }

                multi.addMonoLingualObject(sourceMono);
                multi.addMonoLingualObject(targetMono);

                super.addMultiLingualObject(multi, false);
                multi = null;

            }
            sourceBuffer.close();
            targetBuffer.close();
            languagesLoaded.put(sourceLanguage + ":" + targetLanguage, "true");
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

        return multiID;

    }

    /* (non-Javadoc)
     * @see de.folt.models.datamodel.BasicDataSource#translate(org.jdom.Element, de.folt.models.documentmodel.xliff.XliffDocument, java.lang.String, java.lang.String, int, java.util.Hashtable)
     */
    @Override
    public Element translate(Element transUnit, Element file, XliffDocument xliffDocument, String sourceLanguage, String targetLanguage, int matchSimilarity, Hashtable<String, Object> translationParameters)
            throws OpenTMSException
    {
        if (!languagesLoaded.containsKey(sourceLanguage + ":" + targetLanguage))
        {
            String dataSourceName = (String) dataSourceProperties.get("dataSourceName");
            String sourceFile = dataSourceName + "." + sourceLanguage;
            String targetFile = dataSourceName + "." + targetLanguage;
            loadParallelCorpusEntries(sourceFile, targetFile);
        }
        return super.translate(transUnit, file, xliffDocument, sourceLanguage, targetLanguage, matchSimilarity, translationParameters);
    }
}
