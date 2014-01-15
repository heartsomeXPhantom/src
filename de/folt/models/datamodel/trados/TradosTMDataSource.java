/*
 * Created on 23.07.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.models.datamodel.trados;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import de.folt.constants.OpenTMSConstants;
import de.folt.fuzzy.MonoLingualPartitionedFuzzyNodeTree;
import de.folt.models.datamodel.BasicDataSource;
import de.folt.models.datamodel.DataSource;
import de.folt.models.datamodel.DataSourceConfigurations;
import de.folt.models.datamodel.DataSourceInstance;
import de.folt.models.datamodel.DataSourceProperties;
import de.folt.models.datamodel.MonoLingualObject;
import de.folt.models.datamodel.MultiLingualObject;
import de.folt.util.OpenTMSException;
import de.folt.util.Timer;

/**
 * Class implements reading a &gt;TrU File from Trados. Pattern used as follows<pre>
 * &lt;TrU>
 * &lt;Att L=Client>Turbol.
 * &lt;Att L=Domain>gelb
 * &lt;CrD>16012007, 11:10:22
 * &lt;CrU>
 * &lt;ChD>16012007, 11:10:22
 * &lt;ChU>Harry
 * &lt;Seg L=de>Technische Dokumentation
 * &lt;Seg L=en>Technical Documentation
 * &lt;/TrU>
 * &lt;/pre>
 * @author klemens
 *
 */
public class TradosTMDataSource extends BasicDataSource
{

    /**
     * main 
     * @param args
     */
    public static void main(String[] args)
    {
        if (args.length <= 2)
        {
            DataSourceProperties model = new DataSourceProperties();
            model.put("dataModelClass", "de.folt.models.datamodel.trados.TradosTMDataSource");
            model.put("dataSourceName", args[1]);
            if (args[0].equalsIgnoreCase(("-read")))
            {
                try
                {
                    DataSource datasource = DataSourceInstance.createInstance("de.folt.models.datamodel.trados.TradosTMDataSource" + ":" + args[1], model);
                    DataSourceProperties modeltarget = new DataSourceProperties();
                    modeltarget.put("dataModelClass", "de.folt.models.datamodel.tmxfile.TmxFileDataSource");
                    String tmxFile = args[1] + ".tmx";
                    modeltarget.put("tmxfile", tmxFile);
                    File f = new File(tmxFile);
                    if (!f.exists())
                    {
                        de.folt.models.datamodel.tmxfile.TmxFileDataSource tmx = new de.folt.models.datamodel.tmxfile.TmxFileDataSource();
                        boolean bCreated = tmx.createDataSource(modeltarget);
                        if (bCreated == false)
                            return;
                    }

                    DataSource datasourceTarget = DataSourceInstance.createInstance("TMX:" + tmxFile, modeltarget);
                    datasource.copyTo(datasourceTarget);
                    datasourceTarget.bPersist();
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }
    }

    private Integer multiID = 1;

    /**
     * 
     */
    public TradosTMDataSource()
    {
        super();
    }

    /**
     * @param dataSourceProperties
     */
    public TradosTMDataSource(DataSourceProperties dataSourceProperties)
    {
        super(dataSourceProperties);
        String tradosFile = (String) dataSourceProperties.getDataSourceProperty("tradosfile");
        if (tradosFile == null)
            tradosFile = (String) dataSourceProperties.getDataSourceProperty("tradosFile");
        if (tradosFile == null)
            tradosFile = (String) dataSourceProperties.getDataSourceProperty("dataSourceName");

        if (tradosFile != null)
        {
            this.dataSourceProperties.put("dataSourceName", tradosFile);
            this.dataSourceProperties.put("tradosfile", tradosFile);
            this.dataSourceProperties.put("datasource", tradosFile);
            this.dataSourceProperties.put("datasourcename", tradosFile);
        }
        
        String dataSourceConfigurationsFile = (String) dataSourceProperties.get("dataSourceConfigurationsFile");
        dataSourceConfigurationsFile = DataSourceConfigurations.getConfigurationFileName(dataSourceConfigurationsFile);
        if (dataSourceConfigurationsFile == null)
        {    
                return;
        }
        dataSourceProperties.put("dataSourceConfigurationsFile", dataSourceConfigurationsFile);
        
        fuzzyTree = new MonoLingualPartitionedFuzzyNodeTree();
        this.multiLingualObjectCache.addObserver(new BasicDataSource.BasicDataSourceObserver());
        File f = new File(tradosFile);
        if (!f.exists())
        {
            this.setLastErrorCode(OpenTMSConstants.OpenTMS_TMXDATASOURCE_FILE_NOTFOUND_ERROR);
            return;
        }
        Timer timer = new Timer();
        timer.startTimer();
        loadTradosEntries();
        timer.stopTimer();
    }

    @Override
    public String getDataSourceType()
    {
        return de.folt.models.datamodel.trados.TradosTMDataSource.class.getName();
    }

    /**
     * loadTradosEntries 
     */
    private int loadTradosEntries()
    {
        String tradosFile = (String) this.dataSourceProperties.getDataSourceProperty("tradosfile");
        String codepage = (String) this.dataSourceProperties.getDataSourceProperty("codepage");
        if (codepage == null)
            codepage = "ISO-8859-1";
        try
        {
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(tradosFile), codepage));
            String line = "";
            MultiLingualObject multi = null;
            while ((line = in.readLine()) != null)
            {
                if (line.matches(".*?<Seg +L=(.*?)>(.*)"))
                {
                    String language = line.replaceAll(".*?<Seg +L=(.*?)>.*", "$1");
                    String segment = line.replaceAll(".*?<Seg +L=.*?>(.*)", "$1");
                    segment = segment.replaceAll("&", "&amp;");
                    segment = segment.replaceAll("<", "&lt;");
                    segment = segment.replaceAll(">", "&gt;");
                    MonoLingualObject mono = new MonoLingualObject();
                    mono.setLanguage(language);
                    mono.setFormattedSegment(segment);
                    mono.setPlainTextSegment(segment);
                    if (multi != null)
                        multi.addMonoLingualObject(mono);
                }
                else if (line.matches(".*?<\\/TrU>"))
                {
                    multi.setId(multiID++);
                    super.addMultiLingualObject(multi, false);
                    multi = null;
                }
                else if (line.matches(".*?<TrU>"))
                {
                    multi = new MultiLingualObject();
                }
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

        return multiID;

    }

    /*
     * (non-Javadoc)
     * 
     * @see de.folt.models.datamodel.BasicDataSource#createDataSource(de.folt.models.datamodel.DataSourceProperties)
     */
    @Override
    public boolean createDataSource(DataSourceProperties dataModelProperties) throws OpenTMSException
    {

        if (dataSourceProperties == null)
            dataSourceProperties = dataModelProperties;
        String tradosFile = (String) dataSourceProperties.getDataSourceProperty("tradosfile");
        if (tradosFile == null)
            tradosFile = (String) dataSourceProperties.getDataSourceProperty("tradosFile");
        if (tradosFile == null)
            tradosFile = (String) dataSourceProperties.getDataSourceProperty("dataSourceName");

        if (tradosFile != null)
        {
            this.dataSourceProperties.put("dataSourceName", tradosFile);
            this.dataSourceProperties.put("tradosfile", tradosFile);
            this.dataSourceProperties.put("datasource", tradosFile);
            this.dataSourceProperties.put("datasourcename", tradosFile);
        }

        File f = new File(tradosFile);
        if (!f.exists())
        {
            return false;
        }
        
        String dataSourceConfigurationsFile = (String) dataModelProperties.get("dataSourceConfigurationsFile");
        dataSourceConfigurationsFile = DataSourceConfigurations.getConfigurationFileName(dataSourceConfigurationsFile);
        if (dataSourceConfigurationsFile == null)
        {    
                return false ;
        }
        dataSourceProperties.put("dataSourceConfigurationsFile", dataSourceConfigurationsFile);
        DataSourceConfigurations config = new DataSourceConfigurations(dataSourceConfigurationsFile);
        if (config.bDataSourceExistsInConfiguration(tradosFile))
            return true;
        DataSourceProperties props = new DataSourceProperties();
        props.put("dataSourceConfigurationsFile", dataSourceConfigurationsFile);
        props.put("tradosFile", tradosFile);
        props.put("codepage", this.dataSourceProperties.getDataSourceProperty("codepage"));
        // props.put("datasourcetype", "de.folt.models.datamodel.tbxfile.TmxFileDataSource");

        config.addConfiguration(tradosFile, this.getDataSourceType(), props);
        config.saveToXmlFile();

        return true;
    }
}
