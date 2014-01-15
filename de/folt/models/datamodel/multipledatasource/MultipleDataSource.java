/*
 * Created on 03.08.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.models.datamodel.multipledatasource;

import java.io.File;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.jdom.Element;

import de.folt.models.datamodel.BasicDataSource;
import de.folt.models.datamodel.DataSource;
import de.folt.models.datamodel.DataSourceConfigurations;
import de.folt.models.datamodel.DataSourceInstance;
import de.folt.models.datamodel.DataSourceProperties;
import de.folt.models.documentmodel.xliff.XliffDocument;
import de.folt.util.OpenTMSException;

/**
 * @author klemens
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class MultipleDataSource extends BasicDataSource
{

    /**
     * main 
     * @param args
     */
    @SuppressWarnings("unused")
	public static void main(String[] args)
    {
        String dataSourceName = "multiTestDataSource";
        MultipleDataSource mult = new MultipleDataSource();
        DataSourceProperties model = new DataSourceProperties();
        model.put("dataModelClass", "de.folt.models.datamodel.multipledatasource.MultipleDataSource");
        model.put("dataSourceName", dataSourceName);
        try
        {
            boolean bCreated = mult.createDataSource(model);
            System.out.println(dataSourceName + ": bCreated =" + bCreated);
            // if (bCreated == false)
            //    return;
            MultipleDataSource multnew = (MultipleDataSource) DataSourceInstance.createInstance(dataSourceName);
            if (multnew == null)
                return;
            // check if the data source exists as a known data source...
            String configFile = multnew.getDefaultDataSourceConfigurationsFileName();
            File f = new File(configFile);
            if (!f.exists())
            {
                return;
            }
            DataSourceConfigurations config = new DataSourceConfigurations(configFile);
            if (config == null)
                return;
            String[] knownDataSources = config.getDataSources();
            for (int i = 0; i < knownDataSources.length; i++)
            {
                // multnew.addDataSource(knownDataSources[i]);
            }
            boolean bAdded = multnew.addDataSource("ms500");
            System.out.println("p500" + ": bAdded =" + bAdded);
            bAdded = multnew.addDataSource("C:\\Program Files\\OpenTMS\\data\\FoltTerm.tbx");
            System.out.println("C:\\Program Files\\OpenTMS\\data\\FoltTerm.tbx" + ": bAdded =" + bAdded);
        }
        catch (OpenTMSException e)
        {
            e.printStackTrace();
        }
    }

    DataSourceConfigurations config = null;

    private Vector<String> dataSourceNames = null;
    
    private Vector<DataSource> dataSources = null;

    /**
     * 
     */
    public MultipleDataSource()
    {
        super();
        DataSourceProperties dataSourceProperties = new DataSourceProperties();
        dataSources = new Vector<DataSource>();
          
        String configFile = this.getDefaultDataSourceConfigurationsFileName();
        File f = new File(configFile);
        if (!f.exists())
        {
            DataSourceConfigurations.createDataSourceConfiguration(configFile);
        }
        dataSourceProperties.put("dataSourceConfigurationsFile", configFile);
        config = new DataSourceConfigurations(configFile);
    }

    /**
     * @param dataSourceProperties
     */
    @SuppressWarnings("unchecked")
    public MultipleDataSource(DataSourceProperties dataSourceProperties)
    {
        super(dataSourceProperties);
        dataSources = new Vector<DataSource>();
        dataSourceNames = new Vector<String>();
        String configFile = this.getDefaultDataSourceConfigurationsFileName();
        File f = new File(configFile);
        if (!f.exists())
        {
            DataSourceConfigurations.createDataSourceConfiguration(configFile);
        }
        dataSourceProperties.put("dataSourceConfigurationsFile", configFile);
        config = new DataSourceConfigurations(configFile);
        if (config == null)
            return;

        System.out.println("Reading MultipleDataSource " + dataSourceProperties.get("dataSourceName"));
        boolean bLoadOnly = false;
        if (dataSourceProperties.containsKey("loadDataSource"))
        {
            if (dataSourceProperties.get("loadDataSource").equals("false"))
                bLoadOnly = true;
        }

        Element configSource = config.getConfiguration((String) dataSourceProperties.get("dataSourceName"));
        List<Element> children = configSource.getChildren("property");
        for (int i = 0; i < children.size(); i++)
        {
            String att = children.get(i).getAttributeValue("name");
            if (att != null)
            {
                if (att.equals("dataSource"))
                {
                    String dataSourceName = children.get(i).getText();
                    try
                    {
                        System.out.println("Reading DataSource " + i + ": " + dataSourceName);
                        if (bLoadOnly == false)
                        {
                            DataSource ds = DataSourceInstance.createInstance(dataSourceName);
                            if (ds != null)
                            {
                                this.dataSources.add(ds);
                                this.dataSourceNames.add(dataSourceName);
                            }
                        }
                        else
                        {
                            this.dataSourceNames.add(dataSourceName);
                        }
                    }
                    catch (OpenTMSException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * addDataSource adds a data source to the Multiple DateSource; checks if data source is already in the list of data sources
     * @param dataSourceName the data source name to be added
     * @return true if the data source could be added; false if data source exists in list or data source is not a (known) data source
     */
    @SuppressWarnings("unchecked")
    public boolean addDataSource(String dataSourceName)
    {
        Element configSource = config.getConfiguration((String) dataSourceProperties.get("dataSourceName"));
        List<Element> children = configSource.getChildren("property");
        for (int i = 0; i < children.size(); i++)
        {
            String att = children.get(i).getAttributeValue("name");
            if (att != null)
            {
                if (att.equals("dataSource") && children.get(i).getText().equals(dataSourceName))
                {
                    return false;
                }
            }
        }

        boolean bKnown = false;
        // check if the data source exists as a known data source...
        String[] knownDataSources = config.getDataSources();
        for (int i = 0; i < knownDataSources.length; i++)
        {
            if (knownDataSources[i].equals(dataSourceName))
            {
                bKnown = true;
                break;
            }
        }

        if (!bKnown)
            return false;
        // check if it can be instantiated
        try
        {
            DataSource dataSource = DataSourceInstance.createInstance(dataSourceName);
            if (dataSource == null)
                return false;
        }
        catch (OpenTMSException e)
        {
            e.printStackTrace();
            return false;
        }

        Element child = new Element("property");
        child.setAttribute("name", "dataSource");
        child.setText(dataSourceName);
        configSource.addContent(child);
        config.saveToXmlFile();
        if (dataSourceNames == null)
            dataSourceNames = new Vector<String>();
        dataSourceNames.add(dataSourceName);
        return true;
    }

    /**
     * addDataSource adds a vector of data source to the Multiple DateSource; checks if data source is already in the list of data sources
     * @param dataSourceNames Vector with data source name
     * @return always true
     */
    public boolean addDataSource(Vector<String> dataSourceNames)
    {
        boolean bAdded = false;
        for (int i = 0; i < dataSourceNames.size(); i++)
        {
            bAdded = this.addDataSource(dataSourceNames.get(i));
            System.out.println("addDataSource " + dataSourceNames.get(i) + " bAdd=" + bAdded);
        }

        return bAdded;
    }

    /* (non-Javadoc)
     * @see de.folt.models.datamodel.BasicDataSource#cleanDataSource()
     */
    @Override
    public void cleanDataSource()
    {
        for (int i = 0; i < dataSources.size(); i++)
        {
            dataSources.get(i).cleanDataSource();
        }
    }

    /* (non-Javadoc)
     * @see de.folt.models.datamodel.BasicDataSource#clearDataSource()
     */
    @Override
    public boolean clearDataSource() throws OpenTMSException
    {
        for (int i = 0; i < dataSources.size(); i++)
        {
            dataSources.get(i).clearDataSource();
        }

        return true;
    }

    /* (non-Javadoc)
     * @see de.folt.models.datamodel.BasicDataSource#createDataSource(de.folt.models.datamodel.DataSourceProperties)
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean createDataSource(DataSourceProperties dataModelProperties) throws OpenTMSException
    {
        if (this.dataSourceProperties == null)
            this.dataSourceProperties = dataModelProperties;
        String dataSourceName = (String) dataModelProperties.get("dataSourceName");

        // check if the data source exists as a known data source...
        String[] knownDataSources = config.getDataSources();
        for (int i = 0; i < knownDataSources.length; i++)
        {
            if (knownDataSources[i].equals(dataSourceName))
            {
                return false;
            }
        }

        dataModelProperties.put("dataSourceName", dataSourceName);
        dataModelProperties.put("datasource", dataSourceName);
        dataModelProperties.put("datasourcename", dataSourceName);

        String dataSourceConfigurationsFile = (String) dataModelProperties.get("dataSourceConfigurationsFile");
        dataSourceConfigurationsFile = DataSourceConfigurations.getConfigurationFileName(dataSourceConfigurationsFile);
        if (dataSourceConfigurationsFile == null)
        {    
                return false ;
        }
        
        DataSourceConfigurations config = new DataSourceConfigurations(dataSourceConfigurationsFile);
        if (config.bDataSourceExistsInConfiguration(dataSourceName))
            return true;
        DataSourceProperties props = new DataSourceProperties();
        props.put("dataSourceConfigurationsFile", dataSourceConfigurationsFile);
        props.put("dataSourceName", dataSourceName);
        config.addConfiguration(dataSourceName, this.getDataSourceType(), props);
        config.saveToXmlFile();

        Vector<String> dataSources = (Vector<String>) dataModelProperties.get("dataSources");
        if (dataSources != null)
        {
            for (int i = 0; i < dataSources.size(); i++)
            {
                @SuppressWarnings("unused")
                boolean bAdded = this.addDataSource(dataSources.get(i));
            }
        }

        return true;
    }

    /**
     * @return the dataSourceNames
     */
    public Vector<String> getDataSourceNames()
    {
        return dataSourceNames;
    }

    /* (non-Javadoc)
     * @see de.folt.models.datamodel.BasicDataSource#getDataSourceType()
     */
    @Override
    public String getDataSourceType()
    {
        return this.getClass().getName();
    }

    /**
     * removeDataSource removes a data source to the Multiple DateSource; checks if data source is already in the list of data sources
     * @param dataSourcename the data source name to be removed
     * @return true if the data source could be removed; otherwise false
     */
    @SuppressWarnings("unchecked")
    public boolean removeDataSource(String dataSourcename)
    {
        Element configSource = config.getConfiguration((String) dataSourceProperties.get("dataSourceName"));
        // check if exists
        List<Element> children = configSource.getChildren("property");
        for (int i = 0; i < children.size(); i++)
        {
            Element child = children.get(i);
            if (child.getAttribute("name").equals("dataSource"))
            {
                if (child.getText().equals(dataSourcename))
                {
                    configSource.removeContent(child);
                    config.saveToXmlFile();
                    return true;
                }
            }
        }

        return false;
    }

    /* (non-Javadoc)
     * @see de.folt.models.datamodel.BasicDataSource#translate(org.jdom.Element, de.folt.models.documentmodel.document.XmlDocument, java.lang.String, java.lang.String, int, java.util.Hashtable)
     */
    @Override
    public Element translate(Element transUnit, Element file, XliffDocument xliffDocument, String sourceLanguage, String targetLanguage, int matchSimilarity, Hashtable<String, Object> translationParameters)
            throws OpenTMSException
    {
        for (int i = 0; i < dataSources.size(); i++)
        {
            dataSources.get(i).translate(transUnit, file, xliffDocument, sourceLanguage, targetLanguage, matchSimilarity, translationParameters);
        }
        
		translationParameters.put(":file", file);
		translationParameters.put(":xliffDocument", xliffDocument);
		translationParameters.put(":sourceLanguage", sourceLanguage);
		translationParameters.put(":targetLanguage", targetLanguage);
		translationParameters.put(":matchSimilarity", (Integer)matchSimilarity);
		translationParameters.put(":dataSource", this);
		translationParameters.put(":instanceOpenTMSProperties", de.folt.util.OpenTMSProperties.getInstance());
		transUnit = runFilterMethod(translationParameters, transUnit);
        return transUnit;
    }

    /* (non-Javadoc)
     * @see de.folt.models.datamodel.BasicDataSource#subSegmentResultsToGlossary(java.lang.String, java.lang.String)
     */
    @Override
    public Element[] subSegmentResultsToGlossary(String sourceLangauge, String targetLanguage)
    {
        
        Vector<Element> vec = new Vector<Element>();
        for (int i = 0; i < dataSources.size(); i++)
        {
            Element[] gloss = dataSources.get(i).subSegmentResultsToGlossary(sourceLangauge, targetLanguage);
            if (gloss == null)
                continue;
            for (int j = 0; j < gloss.length; j++)
            {
                vec.add(gloss[j]);
            }
        }
        Element[] glossaries = new Element[vec.size()];
        glossaries = (Element[]) vec.toArray();
        return glossaries;
    }

    /* (non-Javadoc)
     * @see de.folt.models.datamodel.BasicDataSource#subSegmentTranslate(org.jdom.Element, de.folt.models.documentmodel.document.XmlDocument, java.lang.String, java.lang.String, java.util.Hashtable)
     */
    @Override
    public Element subSegmentTranslate(Element transUnit, XliffDocument xliffDocument, String sourceLanguage, String targetLanguage, Hashtable<String, Object> translationParameters)
            throws OpenTMSException
    {
        for (int i = 0; i < dataSources.size(); i++)
        {
            dataSources.get(i).subSegmentTranslate(transUnit, xliffDocument, sourceLanguage, targetLanguage, translationParameters);
        }
        return transUnit;
    }

}
