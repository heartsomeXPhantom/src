/*
 * Created on 05.02.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.models.datamodel.db4o;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.jdom.Element;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;

import de.folt.constants.OpenTMSConstants;
import de.folt.fuzzy.FuzzyNode;
import de.folt.fuzzy.FuzzyNodeSearchResult;
import de.folt.fuzzy.LanguagePartitionedFuzzyNodeTree;
import de.folt.fuzzy.StringFuzzyNode;
import de.folt.models.datamodel.BasicDataSource;
import de.folt.models.datamodel.DataSource;
import de.folt.models.datamodel.DataSourceConfigurations;
import de.folt.models.datamodel.DataSourceInstance;
import de.folt.models.datamodel.DataSourceProperties;
import de.folt.models.datamodel.MonoLingualObject;
import de.folt.models.datamodel.MultiLingualObject;
import de.folt.models.datamodel.TranslationCheckResult;
import de.folt.models.datamodel.tmxfile.TmxFileDataSource;
import de.folt.models.documentmodel.xliff.XliffDocument;
import de.folt.similarity.LevenshteinSimilarity;
import de.folt.util.OpenTMSException;
import de.folt.util.Timer;

/**
 * @author klemens
 * 
 */
public class DB4O extends BasicDataSource
{

    private ObjectContainer db4o = null;

    ObjectSet<MultiLingualObject> multiLingualObjectCache = null;

    /**
     * main
     * 
     * @param args
     */
    public static void main(String[] args)
    {
        String database = "";
        try
        {
            String xlifffile = args[0];
            database = null;

            String sourceLanguage = null;
            String targetLanguage = null;

            if (args.length > 1)
            {
                database = args[1];
            }

            if (args.length > 2)
            {
                sourceLanguage = args[2];
            }

            if (args.length > 3)
            {
                targetLanguage = args[3];
            }

            if (database == null)
            {
                System.exit(0);
            }

            Timer timer = new Timer();
            timer.startTimer();
            File f = new File(xlifffile);
            XliffDocument doc = new XliffDocument();
            // load the xml file
            doc.loadXmlFile(f);
            timer.stopTimer();
            System.out.println(timer.timerString("XLIFF file read " + xlifffile + ": Version " + doc.getXliffVersion()));

            List<Element> files = doc.getFiles();
            System.out.println("# XLIFF Files: " + files.size());

            DataSourceProperties model = new DataSourceProperties();
            model.put("dataModelClass", "de.folt.models.datamodel.db4o.DB4O");
            model.put("database", database);
            // model.put("dataModelUrl", "openTMS.jar");
            System.out.println(model.toString());
            System.out.println("createInstance" + " database:" + database);
            DataSource datasource = DataSourceInstance.createInstance("DB4O:" + database, model);
            System.out.println("createInstance" + " database:" + database + " getLastErrorCode=" + datasource.getLastErrorCode() + " >>> " + datasource);
            System.out.println("Instances: ");
            String[] inst = DataSourceInstance.getAllDataSourceInstanceNames();
            for (int i = 0; i < inst.length; i++)
            {
                System.out.println(i + ": " + inst[i]);
            }
            timer.startTimer();
            doc.translate(datasource, sourceLanguage, targetLanguage, 70, -1, null);
            timer.stopTimer();
            System.out.println(timer.timerString("XLIFF translation " + xlifffile + ": db " + database));
            doc.saveToXmlFile(xlifffile + ".translate.xlf");
            DataSourceInstance.removeInstance("DB4O:" + database);

            doc = null;
            System.exit(0);
        }
        catch (Exception e)
        {
            try
            {
                DataSourceInstance.removeInstance("DB4O:" + database);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    private LanguagePartitionedFuzzyNodeTree<String> fuzzyTree = null;

    /**
     * @param dataModelProperties
     *            the data model parameters<br>
     *            Main Key: dataModelProperties.getDataSourceProperty("database"); - the name of the database
     */
    public DB4O(DataSourceProperties dataModelProperties)
    {
        super();
        this.dataSourceProperties = dataModelProperties;
        String database = (String) dataModelProperties.getDataSourceProperty("database");
        if ((database == null) || database.equals(""))
            database = (String) dataModelProperties.getDataSourceProperty("dataSourceName");
        // File f = new File(database);
        // if (!f.exists())
        // {
        //    this.setLastErrorCode(OpenTMSConstants.OpenTMS_TMXDATASOURCE_FILE_NOTFOUND_ERROR);
        //    return;
        // }

        dataModelProperties.put("dataSourceName", database);
        dataModelProperties.put("datasource", database);
        dataModelProperties.put("datasourcename", database);

        this.dataSourceProperties.put("dataSourceName", database);
        this.dataSourceProperties.put("datasource", database);
        this.dataSourceProperties.put("datasourcename", database);

        Timer timer = new Timer();
        timer.startTimer();
        db4o = Db4o.openFile(database);
        if (db4o == null)
        {
            this.setLastErrorCode(OpenTMSConstants.OpenTMS_ID_FAILURE);
            System.out.println(timer.timerString("db4o databases not exists/created " + database + "=" + OpenTMSConstants.OpenTMS_ID_FAILURE));
            return;
        }

        fuzzyTree = new LanguagePartitionedFuzzyNodeTree<String>();
        // now we need to create the fuzzy index ...
        MonoLingualObject protomono = new MonoLingualObject();
        protomono.clearObject();
        ObjectSet<MonoLingualObject> resultmono = db4o.queryByExample(protomono);
        loadMonoLingualObjectResult(resultmono);

        timer.stopTimer();
        System.out.println(timer.timerString("db4o databases loaded " + database));
    }

    /* (non-Javadoc)
     * @see de.folt.models.datamodel.BasicDataSource#addMonoLingualObject(de.folt.models.datamodel.MonoLingualObject, boolean)
     */
    @Override
    public boolean addMonoLingualObject(MonoLingualObject monoLingualObject, boolean mergeObjects)
    {
        String uniqueid = monoLingualObject.getUniqueID();
        String plaintext = monoLingualObject.getPlainTextSegment();
        String language = monoLingualObject.getLanguage();
        // now create fuzzy node

        db4o.store(monoLingualObject);
        if (fuzzyTree != null)
        {
            StringFuzzyNode<String> node = new StringFuzzyNode<String>(plaintext, uniqueid);
            fuzzyTree.insertFuzzyNode(node, language);
        }
        return true;
    }

    /* (non-Javadoc)
     * @see de.folt.models.datamodel.BasicDataSource#addMultiLingualObject(de.folt.models.datamodel.MultiLingualObject, boolean)
     */
    @Override
    public boolean addMultiLingualObject(MultiLingualObject multiLingualObject, boolean mergeObjects)
    {
        db4o.store(multiLingualObject);
        Vector<MonoLingualObject> monos = multiLingualObject.getMonoLingualObjectsAsVector();
        if (fuzzyTree != null)
        {
            for (int i = 0; i < monos.size(); i++)
            {
                String uniqueid = monos.get(i).getUniqueID();
                String plaintext = monos.get(i).getPlainTextSegment();
                String language = monos.get(i).getLanguage();
                StringFuzzyNode<String> node = new StringFuzzyNode<String>(plaintext, uniqueid);
                fuzzyTree.insertFuzzyNode(node, language);
            }
        }
        return true;
    }

    /* (non-Javadoc)
     * @see de.folt.models.datamodel.BasicDataSource#bPersist()
     */
    @Override
    public boolean bPersist()
    {
        // TODO Auto-generated method stub
        return super.bPersist();
    }

    /* (non-Javadoc)
     * @see de.folt.models.datamodel.BasicDataSource#checkIfTranslationExistsInDataSource(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public TranslationCheckResult checkIfTranslationExistsInDataSource(String sourceSegment, String sourceLanguage, String targetSegment, String targetLanguage)
    {
        // TODO Auto-generated method stub
        return super.checkIfTranslationExistsInDataSource(sourceSegment, sourceLanguage, targetSegment, targetLanguage);
    }

    /* (non-Javadoc)
     * @see de.folt.models.datamodel.BasicDataSource#cleanDataSource()
     */
    @Override
    public void cleanDataSource()
    {
        db4o.commit();
        db4o.close();
    }

    /* (non-Javadoc)
     * @see de.folt.models.datamodel.BasicDataSource#clearDataSource()
     */
    @Override
    public boolean clearDataSource() throws OpenTMSException
    {
        MonoLingualObject protomono = new MonoLingualObject();
        protomono.clearObject();
        ObjectSet<MonoLingualObject> resultmono = db4o.queryByExample(protomono);
        for (int i = 0; i < resultmono.size(); i++)
        {
            MonoLingualObject mono = resultmono.get(i);
            db4o.delete(mono);
        }

        MultiLingualObject protomulti = new MultiLingualObject();
        protomulti.clearObject();
        ObjectSet<MultiLingualObject> resultmulti = db4o.queryByExample(protomulti);
        for (int i = 0; i < resultmulti.size(); i++)
        {
            MultiLingualObject multi = resultmulti.get(i);
            db4o.delete(multi);
        }

        db4o.commit();
        return true;
    }

    /* (non-Javadoc)
     * @see de.folt.models.datamodel.BasicDataSource#copyFrom(de.folt.models.datamodel.DataSource)
     */
    @Override
    public int copyFrom(DataSource dataSource)
    {
        return super.copyFrom(dataSource);
    }

    /* (non-Javadoc)
     * @see de.folt.models.datamodel.BasicDataSource#copyTo(de.folt.models.datamodel.DataSource)
     */
    @Override
    public int copyTo(DataSource dataSource)
    {
        // TODO Auto-generated method stub
        return super.copyTo(dataSource);
    }

    /* (non-Javadoc)
     * @see de.folt.models.datamodel.BasicDataSource#createDataSource(de.folt.models.datamodel.DataSourceProperties)
     */
    @Override
    public boolean createDataSource(DataSourceProperties dataModelProperties) throws OpenTMSException
    {
        String dataSourceName = (String) dataModelProperties.get("dataSourceName");
        String dataSourceConfigurationsFile = (String) dataModelProperties.get("dataSourceConfigurationsFile");
        dataSourceConfigurationsFile = DataSourceConfigurations.getConfigurationFileName(dataSourceConfigurationsFile);
        if (dataSourceConfigurationsFile == null)
        {
            return false;
        }
        BasicDataSource sqldatasource = new BasicDataSource();
        File fhd = new File(dataSourceConfigurationsFile);
        if (!fhd.exists())
        {
            File fx = new File(sqldatasource.getDefaultDataSourceConfigurationsFileName());
            if (!fx.exists())
            {
                return false;
            }

            dataSourceConfigurationsFile = sqldatasource.getDefaultDataSourceConfigurationsFileName();
        }

        dataSourceProperties.put("dataSourceConfigurationsFile", dataSourceConfigurationsFile);
        DataSourceConfigurations config = new DataSourceConfigurations(dataSourceConfigurationsFile);
        if (config.bDataSourceExistsInConfiguration(dataSourceName))
            return true;
        DataSourceProperties props = new DataSourceProperties();
        props.put("dataSourceConfigurationsFile", dataSourceConfigurationsFile);
        props.put("dataSourceName", dataSourceName);
        // props.put("datasourcetype", "de.folt.models.datamodel.tmxfile.TmxFileDataSource");

        config.addConfiguration(dataSourceName, this.getDataSourceType(), props);
        config.saveToXmlFile();

        return true;
    }

    /* (non-Javadoc)
     * @see de.folt.models.datamodel.BasicDataSource#deleteDataSource(de.folt.models.datamodel.DataSourceProperties)
     */
    @Override
    public boolean deleteDataSource(DataSourceProperties dataModelProperties) throws OpenTMSException
    {
        // TODO Auto-generated method stub
        return super.deleteDataSource(dataModelProperties);
    }

    /* (non-Javadoc)
     * @see de.folt.models.datamodel.BasicDataSource#getDataSourceType()
     */
    @Override
    public String getDataSourceType()
    {
        return this.getClass().getName();
    }

    /* (non-Javadoc)
     * @see de.folt.models.datamodel.BasicDataSource#hasMoreElements()
     */
    @Override
    public boolean hasMoreElements()
    {
        return multiLingualObjectCache.hasNext();
    }

    /* (non-Javadoc)
     * @see de.folt.models.datamodel.BasicDataSource#initEnumeration()
     */
    @Override
    public void initEnumeration()
    {
        MultiLingualObject protomulti = new MultiLingualObject();
        protomulti.clearObject();
        multiLingualObjectCache = db4o.queryByExample(protomulti);
    }

    /**
     * loadMonoLingualObjectResult loads all MonlingualObjects found in db4o into the fuzzy tree the value is the unique id for the translate function
     * 
     * @param result
     */
    private void loadMonoLingualObjectResult(ObjectSet<MonoLingualObject> result)
    {
        while (result.hasNext())
        {
            MonoLingualObject monofound = (MonoLingualObject) result.next();
            String uniqueid = monofound.getUniqueID();
            String plaintext = monofound.getPlainTextSegment();
            String language = monofound.getLanguage();
            // now create fuzzy node
            StringFuzzyNode<String> node = new StringFuzzyNode<String>(plaintext, uniqueid);
            fuzzyTree.insertFuzzyNode(node, language);
        }
    }

    /* (non-Javadoc)
     * @see de.folt.models.datamodel.BasicDataSource#nextElement()
     */
    @Override
    public MultiLingualObject nextElement()
    {
        return multiLingualObjectCache.next();
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
        db4o.close();
        db4o = null;
        fuzzyTree = null;
    }

    /* (non-Javadoc)
     * @see de.folt.models.datamodel.BasicDataSource#removeMonoLingualObject(de.folt.models.datamodel.MonoLingualObject)
     */
    @Override
    public boolean removeMonoLingualObject(MonoLingualObject monoLingualObject)
    {
        db4o.delete(monoLingualObject);
        return true;
    }

    /* (non-Javadoc)
     * @see de.folt.models.datamodel.BasicDataSource#removeMultiLingualObject(de.folt.models.datamodel.MultiLingualObject)
     */
    @Override
    public boolean removeMultiLingualObject(MultiLingualObject multiLingualObject)
    {
        db4o.delete(multiLingualObject);
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.folt.models.datamodel.BasicDataSource#setDataSourceType()
     */
    @Override
    public void setDataSourceType()
    {
        this.dataSourceType = TmxFileDataSource.class.getName();
    }

    // private String comparemonoid = "";

    /*
     * (non-Javadoc)
     * 
     * @see de.folt.models.datamodel.BasicDataSource#translate(org.jdom.Element, de.folt.models.documentmodel.document.XmlDocument, java.lang.String, java.lang.String, int)
     */
    @SuppressWarnings(
        {"rawtypes"
        })
    @Override
    public Element translate(Element transUnit, Element file, XliffDocument xliffDocument, String sourceLanguage, String targetLanguage, int matchSimilarity, Hashtable<String, Object> translationParameters)
            throws OpenTMSException
    {
        Element source = transUnit.getChild("source", xliffDocument.getNamespace());
        String segment = xliffDocument.elementContentToString(source);
        // now run a fuzzy search
        Class[] classes = new Class[2];
        classes[0] = String.class;
        classes[1] = Object.class;
        Method method = null;
        try
        {
            method = MonoLingualObject.class.getMethod("simpleComputePlainText", classes);
            segment = (String) method.invoke(MonoLingualObject.class, segment, null);
        }
        catch (Exception ex)
        {
            throw new OpenTMSException("translate", "simpleComputePlainText", OpenTMSConstants.OpenTMS_TRANSLATE_PLAINTEXTMETHOD_NOTFOUND_ERROR, (Object) this, ex);
        }

        // StringFuzzyNode<String> fuzzyCompareKey = new StringFuzzyNode<String>(segment, null);
        // Vector<FuzzyNodeSearchResult<String, String>> fuzzyresult = fuzzyTree.search(fuzzyCompareKey, matchSimilarity, sourceLanguage);

        try
        {
            // MonoLingualObject mono = new MonoLingualObject(segment, sourceLanguage, MonoLingualObject.class, method, null);
            StringFuzzyNode<String> fuzzyCompareKey = new StringFuzzyNode<String>(segment, null);
            Vector<FuzzyNodeSearchResult<String, String>> fuzzyresult = fuzzyTree.search(fuzzyCompareKey, matchSimilarity, sourceLanguage);

            if (fuzzyresult == null)
                return transUnit;

            for (int i = 0; i < fuzzyresult.size(); i++)
            {
                FuzzyNodeSearchResult<String, String> fzresult = fuzzyresult.get(i);
                FuzzyNode<String, String> fuzzyNode = fzresult.getFuzzyNode();
                Vector<String> monos = fuzzyNode.getValues();
                for (int j = 0; j < monos.size(); j++)
                {
                    String sourceMonoid = monos.get(j);
                    // comparemonoid = sourceMonoid;
                    MonoLingualObject protomono = new MonoLingualObject();
                    protomono.clearObject();
                    protomono.setUniqueID(sourceMonoid);

                    /*
                    List<MonoLingualObject> resultmono = db4o.query(new Predicate<MonoLingualObject>()
                    {

                        public boolean match(MonoLingualObject mono)
                        {
                            return mono.getStUniqueID().equals(comparemonoid);
                        }
                    });
                    */
                    ObjectSet<MonoLingualObject> resultmono = db4o.queryByExample(protomono);
                    if ((resultmono == null) || (resultmono.size() == 0))
                        continue;
                    MonoLingualObject sourceMono;
                    // curious exception occur here with db4o!!!
                    try
                    {
                        sourceMono = resultmono.get(0);
                    }
                    catch (Exception e)
                    {
                        System.out.println("Error: " + segment);
                        continue;
                    }
                    String matchsegment = sourceMono.getPlainTextSegment();
                    // loadMonoLingualObjectResult(resultmono);
                    MultiLingualObject parentMulti = sourceMono.getParentMultiLingualObject();
                    if (parentMulti == null)
                        continue;
                    Vector<MonoLingualObject> targetmonos = parentMulti.getMonoLingualObjectsAsVector(targetLanguage);
                    if (targetmonos == null)
                        continue;
                    // we must check the Levenshtein similarity now
                    int levenSimilarity = LevenshteinSimilarity.levenshteinSimilarity(segment, matchsegment);
                    if (levenSimilarity < matchSimilarity)
                        continue;
                    Element alttrans = xliffDocument.addAltTrans(transUnit, sourceMono, targetmonos, levenSimilarity, translationParameters);
                    if (alttrans != null)
                    {
                        if (dataSourceProperties.containsKey("dataSourceName"))
                        {
                            if ((String) dataSourceProperties.get("dataSourceName") != null)
                                alttrans.setAttribute("origin", (String) dataSourceProperties.get("dataSourceName"));
                            else
                                alttrans.setAttribute("origin", this.getClass().getName());
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            System.out.println("Error: " + segment);
            e.printStackTrace();
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
}
