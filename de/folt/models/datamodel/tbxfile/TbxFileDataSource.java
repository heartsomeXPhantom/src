/*
 * Created on 04.02.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.models.datamodel.tbxfile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.jdom.Element;

import de.folt.constants.OpenTMSConstants;
import de.folt.fuzzy.FuzzyNode;
import de.folt.fuzzy.FuzzyNodeSearchResult;
import de.folt.fuzzy.MonoLingualFuzzyNode;
import de.folt.fuzzy.MonoLingualPartitionedFuzzyNodeTree;
import de.folt.models.datamodel.BasicDataSource;
import de.folt.models.datamodel.DataSource;
import de.folt.models.datamodel.DataSourceConfigurations;
import de.folt.models.datamodel.DataSourceInstance;
import de.folt.models.datamodel.DataSourceProperties;
import de.folt.models.datamodel.LinguisticProperty;
import de.folt.models.datamodel.MonoLingualObject;
import de.folt.models.datamodel.MultiLingualObject;
import de.folt.models.documentmodel.tbx.TbxDocument;
import de.folt.models.documentmodel.xliff.XliffDocument;
import de.folt.util.OpenTMSException;
import de.folt.util.Timer;

/**
 * This class implements a data source based on a TMX file.
 * 
 * @author klemens
 * 
 */
public class TbxFileDataSource extends BasicDataSource
{

    /**
     * main
     * 
     * @param args
     * <br>
     *            -test - run some test functions <br>
     *            -create <datasourcename> create a tbx data source; creates a data source tmx file if it does not exist <br>
     *            -delete <datasourcename> deletes a tbx data source; delete the data source tmx file
     */
    public static void main(String[] args)
    {
        if (args.length <= 0)
        {
            return;
        }
        if (args[0].equalsIgnoreCase(("-test")))
        {
            test(args);
            return;
        }
        if (args.length <= 2)
        {
            DataSourceProperties model = new DataSourceProperties();
            model.put("dataModelClass", "de.folt.models.datamodel.tbxfile.TbxFileDataSource");
            model.put("tbxfile", args[1]);
            if (args[0].equalsIgnoreCase(("-create")))
            {
                try
                {
                    DataSource datasource = DataSourceInstance.createInstance("TBX:" + args[1], model);
                    datasource.createDataSource(model);
                    return;
                }
                catch (OpenTMSException e)
                {
                    e.printStackTrace();
                    return;
                }
            }
            if (args[0].equalsIgnoreCase(("-delete")))
            {
                try
                {
                    DataSource datasource = DataSourceInstance.createInstance("TBX:" + args[1], model);
                    datasource.deleteDataSource(model);
                    return;
                }
                catch (OpenTMSException e)
                {
                    e.printStackTrace();
                    return;
                }
            }
            if (args[0].equalsIgnoreCase(("-copyTo")))
            {
                try
                {
                    DataSource datasource = DataSourceInstance.createInstance("TBX:" + args[1], model);
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

                    modeltarget = new DataSourceProperties();
                    modeltarget.put("dataModelClass", "de.folt.models.datamodel.xlifffile.XliffFileDataSource");
                    tmxFile = args[1] + ".xlf";
                    modeltarget.put("xlifffile", tmxFile);
                    f = new File(tmxFile);
                    if (!f.exists())
                    {
                        de.folt.models.datamodel.xlifffile.XliffFileDataSource xliff = new de.folt.models.datamodel.xlifffile.XliffFileDataSource();
                        boolean bCreated = xliff.createDataSource(modeltarget);
                        if (bCreated == false)
                            return;
                    }
                    datasourceTarget = DataSourceInstance.createInstance("XLIFF:" + tmxFile, modeltarget);
                    datasource.copyTo(datasourceTarget);
                    datasourceTarget.bPersist();
                    return;
                }
                catch (OpenTMSException e)
                {
                    e.printStackTrace();
                    return;
                }
            }
            if (args[0].equalsIgnoreCase("-copyFromTmx") || args[0].equalsIgnoreCase("-copyFrom"))
            {
                try
                {
                    String tbxfile = args[1] + ".tbx";
                    model.put("tbxfile", tbxfile);
                    File f = new File(tbxfile);
                    if (!f.exists())
                    {
                        TbxFileDataSource tmx = new TbxFileDataSource();
                        boolean bCreated = tmx.createDataSource(model);
                        if (bCreated == false)
                            return;
                    }
                    DataSource datasource = DataSourceInstance.createInstance("TBX:" + tbxfile, model);
                    DataSourceProperties modeltarget = new DataSourceProperties();
                    modeltarget.put("dataModelClass", "de.folt.models.datamodel.tmxfile.TmxFileDataSource");
                    modeltarget.put("tmxfile", args[1]);
                    DataSource datasourceTarget = DataSourceInstance.createInstance("TMX:" + args[1], modeltarget);
                    datasourceTarget.copyTo(datasource);
                    datasource.bPersist();
                    return;
                }
                catch (OpenTMSException e)
                {
                    e.printStackTrace();
                    return;
                }
            }
            if (args[0].equalsIgnoreCase(("-copyFromXliff")))
            {
                try
                {
                    String tbxfile = args[1] + ".tbx";
                    model.put("tbxfile", tbxfile);
                    File f = new File(tbxfile);
                    if (!f.exists())
                    {
                        TbxFileDataSource tmx = new TbxFileDataSource();
                        boolean bCreated = tmx.createDataSource(model);
                        if (bCreated == false)
                            return;
                    }
                    DataSource datasource = DataSourceInstance.createInstance("TBX:" + tbxfile, model);
                    DataSourceProperties modeltarget = new DataSourceProperties();
                    modeltarget.put("dataModelClass", "de.folt.models.datamodel.xlifffile.XliffFileDataSource");
                    modeltarget.put("xlifffile", args[1]);
                    DataSource datasourceTarget = DataSourceInstance.createInstance("XLIFF:" + args[1], modeltarget);
                    datasourceTarget.copyTo(datasource);
                    datasource.bPersist();
                    return;
                }
                catch (OpenTMSException e)
                {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    /**
     * test simple test method for generating DataModelInstances
     */
    public static void test(String[] args)
    {
        try
        {
            String tbxfile = args[1];
            DataSourceProperties model = new DataSourceProperties();
            model.put("dataModelClass", "de.folt.models.datamodel.tbxfile.TbxFileDataSource");
            model.put("tbxfile", tbxfile);
            // model.put("dataModelUrl", "openTMS.jar");
            System.out.println(model.toString());
            System.out.println("createInstance" + " TBX:" + tbxfile);
            DataSource datasource = DataSourceInstance.createInstance("TBX:" + tbxfile, model);
            System.out.println("createInstance" + " TBX:" + tbxfile + " getLastErrorCode=" + datasource.getLastErrorCode() + " >>> " + datasource);
            TbxFileDataSource tbxdatasource = (TbxFileDataSource) datasource;
            System.out.println("Number of fuzzy nodes: " + tbxdatasource.fuzzyTree.countNodes());
            System.out.println(tbxdatasource.fuzzyTree.format());

            datasource = DataSourceInstance.getInstance("TBX:" + tbxfile);
            System.out.println("getInstance" + " TBX:" + tbxfile + " >>> " + datasource);
            System.out.println("Instances: ");
            String[] inst = DataSourceInstance.getAllDataSourceInstanceNames();
            for (int i = 0; i < inst.length; i++)
            {
                System.out.println(i + ": " + inst[i]);
            }
            DataSourceInstance.removeInstance("TBX:" + tbxfile);
            // testing not existing instance ...
            DataSourceInstance.removeInstance("bla:" + tbxfile);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private TbxDocument doc = null;

    private Integer multiID = 1;

    /**
     * @param dataModelProperties
     *            the data model parameters<br>
     *            Main Key: dataModelProperties.getDataSourceProperty("tbxfile"); - the tmx file to use (alternatively "tbxFile" or "dataSourceName" can be used too)
     */
    public TbxFileDataSource(DataSourceProperties dataModelProperties)
    {
        this.dataSourceProperties = dataModelProperties;
        String tbxFile = (String) dataModelProperties.getDataSourceProperty("tbxfile");
        if (tbxFile == null)
            tbxFile = (String) dataModelProperties.getDataSourceProperty("tbxFile");
        if (tbxFile == null)
            tbxFile = (String) dataModelProperties.getDataSourceProperty("dataSourceName");

        dataSourceProperties.put("dataSourceName", tbxFile);
        dataSourceProperties.put("tbxFile", tbxFile);
        dataSourceProperties.put("datasource", tbxFile);
        dataSourceProperties.put("datasourcename", tbxFile);
        
        String dataSourceConfigurationsFile = (String) dataModelProperties.get("dataSourceConfigurationsFile");
        dataSourceConfigurationsFile = DataSourceConfigurations.getConfigurationFileName(dataSourceConfigurationsFile);
        if (dataSourceConfigurationsFile == null)
        {    
            System.out.println("dataSourceConfigurationsFile = null");    
        	return;
        }
        dataSourceProperties.put("dataSourceConfigurationsFile", dataSourceConfigurationsFile);

        fuzzyTree = new MonoLingualPartitionedFuzzyNodeTree();
        this.multiLingualObjectCache.addObserver(new BasicDataSource.BasicDataSourceObserver());
        File f = new File(tbxFile);
        if (!f.exists())
        {
            this.setLastErrorCode(OpenTMSConstants.OpenTMS_TMXDATASOURCE_FILE_NOTFOUND_ERROR);
            System.out.println("tbxFile not found: " + tbxFile ); 
            return;
        }
        Timer timer = new Timer();
        timer.startTimer();
        doc = new TbxDocument();
        // load the xml file
        doc.loadXmlFile(f);

        timer.stopTimer();
        System.out.println(timer.timerString("TBX file load " + tbxFile + ": Version " + doc.getTbxVersion()));
        timer = new Timer();
        timer.startTimer();
        loadTBXEntries();
        timer.stopTimer();
        System.out.println(timer.timerString("TBX file read entries " + tbxFile + ": Version " + doc.getTbxVersion()));
    }

    /**
     * 
     */
    public TbxFileDataSource()
    {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.folt.models.datamodel.BasicDataSource#addMultiLingualObject(de.folt.models.datamodel.MultiLingualObject, boolean)
     */
    @Override
    public boolean addMultiLingualObject(MultiLingualObject multiLingualObject, boolean mergeObjects)
    {
        multiLingualObject.setId(this.multiID++);

        // tu must be added to body ...
        Element body = doc.getTbxBody();
        String multistring = multiLingualObject.mapToTermEntry();
        try
        {
            // must be a termEntry
            Element multielem = doc.buildElement(multistring);
            body.addContent(multielem);
        }
        catch (OpenTMSException e)
        {
            e.printStackTrace();
        }
        return super.addMultiLingualObject(multiLingualObject, mergeObjects);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.folt.models.datamodel.BasicDataSource#bPersist()
     */
    @Override
    public boolean bPersist()
    {
        if (doc != null)
            return doc.saveToXmlFile();
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.folt.models.datamodel.DataSource#cleanDataSource()
     */
    @Override
    public void cleanDataSource()
    {
        doc = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.folt.models.datamodel.BasicDataSource#clearDataSource()
     */
    @Override
    public boolean clearDataSource() throws OpenTMSException
    {
        // TODO Auto-generated method stub
        return super.clearDataSource();
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.folt.models.datamodel.BasicDataSource#createDataSource(de.folt.models.datamodel.DataSourceProperties)
     */
    @Override
    public boolean createDataSource(DataSourceProperties dataModelProperties) throws OpenTMSException
    {
        if (this.dataSourceProperties == null)
            this.dataSourceProperties = dataModelProperties;
        String tbxFile = (String) dataModelProperties.get("tbxfile");
        if (tbxFile == null)
        {
            if (tbxFile == null)
                tbxFile = (String) dataModelProperties.getDataSourceProperty("tbxFile");
            tbxFile = (String) dataModelProperties.get("datasourcename");
            if (tbxFile == null)
            {
                tbxFile = (String) dataModelProperties.get("datasource");
            }
            if (tbxFile == null)
            {
                tbxFile = (String) dataModelProperties.get("dataSourceName");
            }
            if (tbxFile == null)
            {
                return false;
            }
        }

        dataSourceProperties.put("dataSourceName", tbxFile);
        dataSourceProperties.put("tbxFile", tbxFile);
        dataSourceProperties.put("datasource", tbxFile);
        dataSourceProperties.put("datasourcename", tbxFile);

        File f = new File(tbxFile);
        if (!f.exists())
        {
            // create the new file;
            FileOutputStream write = null;
            OutputStreamWriter writer = null;
            try
            {
                write = new FileOutputStream(tbxFile);
                writer = new OutputStreamWriter(write, "UTF-8");
                writer.write(tbxHeader(0));
                writer.write(tbxFooter());
                writer.close();
                write.close();
                f = new File(tbxFile);
            }
            catch (Exception ex)
            {
                try
                {
                    writer.close();
                    write.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                ex.printStackTrace();
                f = new File(tbxFile);
                if (f.exists())
                {
                    f.delete();
                }
                return false;
            }
        }

        Timer timer = new Timer();
        timer.startTimer();
        doc = new TbxDocument();
        // load the xml file
        doc.loadXmlFile(f);

        timer.stopTimer();
        System.out.println(timer.timerString("TBX file load " + tbxFile + ": Version " + doc.getTbxVersion()));
        timer = new Timer();
        timer.startTimer();
        loadTBXEntries();
        timer.stopTimer();
        System.out.println(timer.timerString("TBX file read entries " + tbxFile + ": Version " + doc.getTbxVersion()));

        String dataSourceConfigurationsFile = (String) dataModelProperties.get("dataSourceConfigurationsFile");
        dataSourceConfigurationsFile = DataSourceConfigurations.getConfigurationFileName(dataSourceConfigurationsFile);
        if (dataSourceConfigurationsFile == null)
        {    
                return false ;
        }
        dataSourceProperties.put("dataSourceConfigurationsFile", dataSourceConfigurationsFile);
        DataSourceConfigurations config = new DataSourceConfigurations(dataSourceConfigurationsFile);
        if (config.bDataSourceExistsInConfiguration(tbxFile))
            return true;
        DataSourceProperties props = new DataSourceProperties();
        props.put("dataSourceConfigurationsFile", dataSourceConfigurationsFile);
        props.put("tbxfile", tbxFile);
        // props.put("datasourcetype", "de.folt.models.datamodel.tbxfile.TmxFileDataSource");

        config.addConfiguration(tbxFile, this.getDataSourceType(), props);
        config.saveToXmlFile();

        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.folt.models.datamodel.BasicDataSource#deleteDataSource(de.folt.models.datamodel.DataSourceProperties)
     */
    @Override
    public boolean deleteDataSource(DataSourceProperties dataModelProperties) throws OpenTMSException
    {
        String tbxFile = (String) dataModelProperties.get("tbxfile");
        if (tbxFile == null)
        {
            if (tbxFile == null)
                tbxFile = (String) dataModelProperties.getDataSourceProperty("tbxFile");
            tbxFile = (String) dataModelProperties.get("dataSourceName");
            if (tbxFile == null)
            {
                tbxFile = (String) dataModelProperties.get("dataSource");
            }
            if (tbxFile == null)
            {
                System.out.println("tbxFile not specified");
                return false;
            }
        }

        if (tbxFile != null)
        {
            dataModelProperties.put("dataSourceName", tbxFile);
            dataModelProperties.put("tbxFile", tbxFile);
            dataModelProperties.put("datasource", tbxFile);
            dataModelProperties.put("datasourcename", tbxFile);
            dataModelProperties.put("tbxfile", tbxFile);
        }

        File f = new File(tbxFile);
        if (f.exists())
        {
            if (f.delete() == false)
            {
                System.out.println("File " + tbxFile + " could not be deleted");
                return false;
            }
            String configFile = this.getDefaultDataSourceConfigurationsFileName();
            f = new File(configFile);
            if (!f.exists())
            {
                DataSourceConfigurations.createDataSourceConfiguration(configFile);
                return false;
            }
            dataSourceProperties.put("dataSourceConfigurationsFile", configFile);
            DataSourceConfigurations config = new DataSourceConfigurations(configFile);
            if (!config.bDataSourceExistsInConfiguration(tbxFile))
            {
                System.out.println("File " + tbxFile + " not found in " + configFile);
                return false;
            }

            config.removeConfiguration(tbxFile);
            config.saveToXmlFile();
            return true;
        }
        System.out.println("File " + tbxFile + " does not exist");
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.folt.models.datamodel.BasicDataSource#exportTmxFile(java.lang.String)
     */
    @Override
    public int exportTmxFile(String tbxFile)
    {
        FileOutputStream write = null;
        OutputStreamWriter writer = null;
        int iCurrentNumber = 0;
        int iNumber = 0;
        try
        {
            write = new FileOutputStream(tbxFile);
            writer = new OutputStreamWriter(write, "UTF-8");
            Timer timer = new Timer();
            timer.startTimer();

            // List<SQLMultiLingualObject> newresults = query.list();
            writer.write(tbxHeader(this.multiLingualObjectCache.size()));
            Vector<Integer> posVec = new Vector<Integer>();
            posVec.add((Integer) 0);
            posVec.add((Integer) 1);

            this.initEnumeration();
            while (this.hasMoreElements())
            {
                MultiLingualObject multi = this.nextElement();
                String multiString = multi.mapToTu();
                writer.write(multiString);

                posVec.set(0, (Integer) iCurrentNumber);
                posVec.set(1, (Integer) iNumber);
                this.setChanged();
                this.notifyObservers(posVec);
                iCurrentNumber++;
            }
            writer.write(tbxFooter());
            writer.close();
            write.close();
            timer.stopTimer();
            System.out.println(timer.timerString("exportTmxFile " + tbxFile + " >> " + iCurrentNumber));
            return iCurrentNumber;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            try
            {
                if (writer != null)
                {
                    writer.write(tbxFooter());
                    writer.close();
                }
                if (write != null)
                    write.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            System.out.println("Error when " + tbxFile + " exporting at # " + iCurrentNumber + " of " + iNumber);
            return de.folt.constants.OpenTMSConstants.OpenTMS_TMX_EXPORT_ERROR;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.folt.models.datamodel.BasicDataSource#exportXliffFile(java.lang.String)
     */
    @Override
    public int exportXliffFile(String xliffFile)
    {
        // TODO Auto-generated method stub
        return super.exportXliffFile(xliffFile);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.folt.models.datamodel.BasicDataSource#getDataSourceType()
     */
    @Override
    public String getDataSourceType()
    {
        // TODO Auto-generated method stub
        return de.folt.models.datamodel.tbxfile.TbxFileDataSource.class.getName();
    }

    /**
     * returns the basic fuzzy tree
     * 
     * @return the fuzzyTree
     */
    public MonoLingualPartitionedFuzzyNodeTree getFuzzyTree()
    {
        return fuzzyTree;
    }

    /* (non-Javadoc)
     * @see de.folt.models.datamodel.BasicDataSource#getMultiLingualObjectFromUniqueId(java.lang.String)
     */
    @Override
    public MultiLingualObject getMultiLingualObjectFromUniqueId(String id)
    {
        return super.getMultiLingualObjectFromUniqueId(id);
    }

    /* (non-Javadoc)
     * @see de.folt.models.datamodel.BasicDataSource#getUniqueIds()
     */
    @Override
    public Vector<String> getUniqueIds()
    {
        return super.getUniqueIds();
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.folt.models.datamodel.BasicDataSource#hasMoreElements()
     */
    @Override
    public boolean hasMoreElements()
    {
        return super.hasMoreElements();
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.folt.models.datamodel.BasicDataSource#initEnumeration()
     */
    @Override
    public void initEnumeration()
    {
        // multiEnum = multiLingualObjectCache.elements();
        super.initEnumeration();
    }

    /**
     * loadTMXEntries loads the tmx entries into main memory
     * 
     * @return the number of TUVs read
     */
    private int loadTBXEntries()
    {
        List<Element> termEntries = doc.getTermEntryList();
        int iDocTuListLength = termEntries.size();
        System.out.println("TBX file #termentries = " + iDocTuListLength);
        for (int i = 0; i < iDocTuListLength; i++)
        {
            MultiLingualObject multi = doc.termEntryToMultiLingualObject(termEntries.get(i));
            multi.setId(multiID++);
            super.addMultiLingualObject(multi, false); // addMultiLingualObject(multi, false);
        }

        lastReadDate = java.lang.System.currentTimeMillis();

        return iDocTuListLength;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.folt.models.datamodel.BasicDataSource#nextElement()
     */
    @Override
    public MultiLingualObject nextElement()
    {
        return super.nextElement();
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.folt.models.datamodel.BasicDataSource#removeDataSource()
     */
    @Override
    public void removeDataSource()
    {
        super.removeDataSource();
        this.bPersist();
        doc = null;
        langMonoLingualHashtable = null;
        fuzzyTree = null;

    }

    /*
     * (non-Javadoc)
     * 
     * @see de.folt.models.datamodel.BasicDataSource#removeMonoLingualObject(de.folt.models.datamodel.MonoLingualObject)
     */
    @Override
    public boolean removeMonoLingualObject(MonoLingualObject monoLingualObject)
    {
        LinguisticProperty tigprop = monoLingualObject.getObjectLinguisticProperty("termEntry");
        Element tig = (Element) tigprop.getValue();
        if (tig == null)
            return false;
        List<Element> termentries = doc.getTermEntryList();
        for (int i = 0; i < termentries.size(); i++)
        {
            Element langset = termentries.get(i);
            boolean bFound = langset.removeContent(tig);
            if (bFound)
                return super.removeMonoLingualObject(monoLingualObject);
        }

        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.folt.models.datamodel.BasicDataSource#removeMultiLingualObject(de.folt.models.datamodel.MultiLingualObject)
     */
    @Override
    public boolean removeMultiLingualObject(MultiLingualObject multiLingualObject)
    {
        LinguisticProperty lingtu = multiLingualObject.getObjectLinguisticProperty("term");
        Element tu = (Element) lingtu.getValue();
        if (tu == null)
            return false;
        boolean bFound = doc.getTbxBody().removeContent(tu);
        if (bFound)
            return super.removeMultiLingualObject(multiLingualObject);
        else
            return false;
    }

    /* (non-Javadoc)
     * @see de.folt.models.datamodel.BasicDataSource#saveModifiedMonoLingualObject(de.folt.models.datamodel.MonoLingualObject)
     */
    @Override
    public boolean saveModifiedMonoLingualObject(MonoLingualObject monoLingualObject)
    {
        try
        {
            LinguisticProperty lingtuv = monoLingualObject.getObjectLinguisticProperty("tuv");
            Element tuv = (Element) lingtuv.getValue();
            if (tuv == null)
                return false;
            tuv.removeChildren("seg");
            Element seg;
            try
            {
                seg = doc.buildElement("<seg>" + monoLingualObject.getFormattedSegment() + "</seg>");
            }
            catch (OpenTMSException e)
            {
                e.printStackTrace();
                return false;
            }
            tuv.addContent(seg);
            // now save the LinguisticProperties

            // doc.saveModifiedLinguisticProperties(monoLingualObject);

            MonoLingualFuzzyNode fuzzyNodeToAdd = new MonoLingualFuzzyNode(monoLingualObject);
            fuzzyTree.insertFuzzyNode(fuzzyNodeToAdd);
            return true; // super.saveModifiedMonoLingualObject(monoLingualObject);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /* (non-Javadoc)
     * @see de.folt.models.datamodel.BasicDataSource#saveModifiedMultiLingualObject(de.folt.models.datamodel.MultiLingualObject)
     */
    @Override
    public boolean saveModifiedMultiLingualObject(MultiLingualObject multiLingualObject)
    {
        try
        {
            LinguisticProperty lingtuv = multiLingualObject.getObjectLinguisticProperty("term");
            Element tuv = (Element) lingtuv.getValue();
            if (tuv == null)
                return false;

            // now save the LinguisticProperties

            // doc.saveModifiedLinguisticProperties(multiLingualObject);

            return true; // super.saveModifiedMonoLingualObject(monoLingualObject);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.folt.models.datamodel.BasicDataSource#setDataSourceType()
     */
    @Override
    public void setDataSourceType()
    {
        this.dataSourceType = TbxFileDataSource.class.getName();
    }

    /**
     * tmxFooter
     * 
     * @return
     */
    private String tbxFooter()
    {
        return "</body></text>\n</martif>";
    }

    /**
     * tmxHeader
     * 
     * @return
     */
    private String tbxHeader(int iNumberEntries)
    {
        String tmx = "";
        tmx = tmx + "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"; //$NON-NLS-1$
        tmx = tmx + "<martif type='DXLT'>\n"; //$NON-NLS-1$
        tmx = tmx + "\t<martifHeader>\n"; //$NON-NLS-1$
        tmx = tmx + "\t\t<fileDesc>\n";
        tmx = tmx + "\t\t<sourceDesc>" + dataSourceProperties.get("dataSourceName") + " " + de.folt.util.OpenTMSSupportFunctions.getDateString() + "</sourceDesc>\n";
        tmx = tmx + "\t\t</fileDesc>\n";
        tmx = tmx + "\t</martifHeader>\n";
        tmx = tmx + "\t<text>\n";
        tmx = tmx + "\t<body>\n"; //$NON-NLS-1$
        return tmx;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.folt.models.datamodel.BasicDataSource#translate(org.jdom.Element, java.lang.String, java.lang.String, int)
     */
    @Override
    public Element translate(Element transUnit, Element file, XliffDocument xliffDocument, String sourceLanguage, String targetLanguage, int matchSimilarity, Hashtable<String, Object> translationParameters)
            throws OpenTMSException
    {
        if (translationParameters == null)
        {
            String approved = transUnit.getAttributeValue("approved");
            if ((approved != null) && approved.equals("yes"))
                return transUnit;
        }
        else
        {
            if (translationParameters.containsKey("ignoreApproveAttribute") && ((String) (translationParameters.get("ignoreApproveAttribute"))).equals("yes"))
            {
                ;
            }
            else
            {
                String approved = transUnit.getAttributeValue("approved");
                if ((approved != null) && approved.equals("yes"))
                    return transUnit;
            }

        }

        Element source = transUnit.getChild("source", xliffDocument.getNamespace());
        String segment = xliffDocument.elementContentToString(source);
        if (translationParameters == null)
            translationParameters = new Hashtable<String, Object>();
        translationParameters.put("translate.SourceFormattedSegment", segment);
        // now run a fuzzy search
        @SuppressWarnings("rawtypes")
		Class[] classes = new Class[2];
        classes[0] = String.class;
        classes[1] = Object.class;
        Method method = null;
        try
        {
            method = MonoLingualObject.class.getMethod("simpleComputePlainText", classes);
        }
        catch (Exception ex)
        {
            throw new OpenTMSException("translate", "simpleComputePlainText", OpenTMSConstants.OpenTMS_TRANSLATE_PLAINTEXTMETHOD_NOTFOUND_ERROR, (Object) this, ex);
        }

        MonoLingualObject mono = new MonoLingualObject(segment, sourceLanguage, MonoLingualObject.class, method, null);
        MonoLingualFuzzyNode fuzzyCompareKey = new MonoLingualFuzzyNode(mono);
        Vector<FuzzyNodeSearchResult<String, MonoLingualObject>> fuzzyresult = fuzzyTree.search(fuzzyCompareKey, matchSimilarity);

        if (fuzzyresult == null)
            return transUnit;

        // System.out.println("# Fuzzy Node search results = " + fuzzyresult.size());
        int iFLenght = fuzzyresult.size();

        for (int i = 0; i < iFLenght; i++)
        {
            FuzzyNodeSearchResult<String, MonoLingualObject> fzresult = fuzzyresult.get(i);
            FuzzyNode<String, MonoLingualObject> fuzzyNode = fzresult.getFuzzyNode();
            Vector<MonoLingualObject> monos = fuzzyNode.getValues();
            for (int j = 0; j < monos.size(); j++)
            {
                MonoLingualObject sourceMono = monos.get(j);
                MultiLingualObject parentMulti = sourceMono.getParentMultiLingualObject();
                if (parentMulti == null)
                    continue;
                Vector<MonoLingualObject> targetmonos = parentMulti.getMonoLingualObjectsAsVector(targetLanguage);
                if ((targetmonos == null) || (targetmonos.size() == 0))
                    continue;
                // we must check the Levenshtein similarity now
                if (fzresult.getLevenDistance()[j] < matchSimilarity)
                    continue;
                Element alttrans = xliffDocument.addAltTrans(transUnit, sourceMono, targetmonos, (int) (fzresult.getLevenDistance()[j]), translationParameters);
                if ((alttrans != null))
                    alttrans.setAttribute("origin", this.getDataSourceType());
            }
        }
        return transUnit;
    }
}
