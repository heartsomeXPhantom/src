/*
 * Created on 08.01.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.models.datamodel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Vector;

import org.jdom.Element;

import de.folt.constants.OpenTMSConstants;
import de.folt.fuzzy.FuzzyNode;
import de.folt.fuzzy.FuzzyNodeSearchResult;
import de.folt.fuzzy.MonoLingualFuzzyNode;
import de.folt.fuzzy.MonoLingualPartitionedFuzzyNodeTree;
import de.folt.models.documentmodel.xliff.XliffDocument;
import de.folt.util.ObservableHashtable;
import de.folt.util.OpenTMSException;

/**
 * @author klemens
 * 
 *         To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and Comments
 */
public class MultiLingualObjectCollection extends BasicDataSource
{

    /**
     * 
     */
    @SuppressWarnings("unused")
	private static final long serialVersionUID = -3951339701744972050L;
    
    private Enumeration<MultiLingualObject> multiEnum = null;

    private MonoLingualPartitionedFuzzyNodeTree fuzzyTree = null;

    private final char formSegmentChar = (char) 14000;

    private final char langTermChar = (char) 14002;

    private final char plainSegmentChar = (char) 14001;

    private DataSourceProperties dataSourceProperties;

    /**
     * 
     */
    public MultiLingualObjectCollection()
    {
        super();

        multiLingualObjectCache = new ObservableHashtable<String, MultiLingualObject>();
        fuzzyTree = new MonoLingualPartitionedFuzzyNodeTree();
    }

    /**
     * @param dataSourceProperties
     *            key: "dataSource" if exists a) will read the data source from this file and b) if bPersists(9 is called will be used to save the dataSource in this file
     */
    @SuppressWarnings({ "unchecked", "unused" })
    public MultiLingualObjectCollection(DataSourceProperties dataSourceProperties)
    {
        super(dataSourceProperties);
        multiLingualObjectCache = new ObservableHashtable<String, MultiLingualObject>();
        fuzzyTree = new MonoLingualPartitionedFuzzyNodeTree();
        this.dataSourceProperties = dataSourceProperties;

        String filename = (String) dataSourceProperties.get("dataSource");

        if (filename != null)
        {
            File f = new File(filename);
            if (f.exists())
            {
                FileInputStream fileIn;
                try
                {
                    fileIn = new FileInputStream(filename);
                    if (fileIn == null)
                        return;
                    ObjectInputStream in = new ObjectInputStream(fileIn);
                    this.multiLingualObjectCache = (ObservableHashtable<String, MultiLingualObject>) in.readObject();
                    in.close();
                    fileIn.close();
                }
                catch (FileNotFoundException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                catch (IOException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                catch (ClassNotFoundException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            
            String configFile = this.getDefaultDataSourceConfigurationsFileName();
            f = new File(configFile);
            if (!f.exists())
            {
                DataSourceConfigurations.createDataSourceConfiguration(configFile);
            }
            
            dataSourceProperties.put("dataSourceConfigurationsFile", configFile);
            DataSourceConfigurations config = new DataSourceConfigurations(configFile);
            if (config.bDataSourceExistsInConfiguration(filename))
                return;
            DataSourceProperties props = new DataSourceProperties();
            props.put("dataSourceConfigurationsFile", configFile);
            props.put("tmxfile", filename);

            config.addConfiguration(filename, this.getDataSourceType(), props);
            config.saveToXmlFile();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.folt.models.datamodel.BasicDataSource#addMonoLingualObject(de.folt.models.datamodel.MonoLingualObject, boolean)
     */
    @Override
    public boolean addMonoLingualObject(MonoLingualObject monoLingualObject, boolean mergeObjects)
    {
        // TODO Auto-generated method stub
        return super.addMonoLingualObject(monoLingualObject, mergeObjects);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.folt.models.datamodel.BasicDataSource#addMultiLingualObject(de.folt.models.datamodel.MultiLingualObject, boolean)
     */
    @Override
    public boolean addMultiLingualObject(MultiLingualObject multiLingualObject, boolean mergeObjects)
    {
        if (mergeObjects)
        {
            // need to check if any of the monos exist
            Vector<MonoLingualObject> monos = multiLingualObject.getMonoLingualObjectsAsVector();
            int iSize = monos.size();
            Vector<MultiLingualObject> mergeMULS = new Vector<MultiLingualObject>();
            for (int i = 0; i < iSize; i++)
            {
                MultiLingualObject multimatch = this.getFormattedSegment(monos.get(i).getFormattedSegment(), monos.get(i).getLanguage());
                if (multimatch != null) // must merge
                {
                    if (!mergeMULS.contains(multimatch))
                        mergeMULS.add(multimatch);
                }
            }
            int mSize = mergeMULS.size();
            if (mSize > 0)
            {
                // we add now all the other monos to the multiLingualObject from the other objects and remove the others from the data source
                for (int i = 0; i < mSize; i++)
                {
                    MultiLingualObject mergeMUL = mergeMULS.get(i);
                    monos = mergeMUL.getMonoLingualObjectsAsVector();
                    for (int k = 0; k < mSize; k++)
                    {
                        multiLingualObject.addMonoLingualObjectIfNotExist(monos.get(k));
                    }
                    this.removeMultiLingualObject(mergeMUL);
                }
            }
                
        }

        this.put(multiLingualObject);
        return true;
    }

    /* (non-Javadoc)
     * @see de.folt.models.datamodel.BasicDataSource#hasMoreElements()
     */
    @Override
    public boolean hasMoreElements()
    {
        return multiEnum.hasMoreElements();
    }

    /* (non-Javadoc)
     * @see de.folt.models.datamodel.BasicDataSource#initEnumeration()
     */
    @Override
    public void initEnumeration()
    {
        this.multiEnum = this.multiLingualObjectCache.elements();
    }

    /* (non-Javadoc)
     * @see de.folt.models.datamodel.BasicDataSource#nextElement()
     */
    @Override
    public MultiLingualObject nextElement()
    {
        return this.multiEnum.nextElement();
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.folt.models.datamodel.BasicDataSource#cleanDataSource()
     */
    @Override
    public void cleanDataSource()
    {
        this.bPersist();
        multiLingualObjectCache.clear();
        fuzzyTree = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.folt.models.datamodel.BasicDataSource#clearDataSource()
     */
    @Override
    public boolean clearDataSource() throws OpenTMSException
    {
        multiLingualObjectCache.clear();
        fuzzyTree = new MonoLingualPartitionedFuzzyNodeTree();
        return true;
    }

    /**
     * containsFormattedSegment checks if a formatted segment for a given language is contained in the Collection
     * 
     * @param formattedSegment
     *            the formatted text segment
     * @param language
     *            the language to search for
     * @return true if contained
     */
    public boolean containsFormattedSegment(String formattedSegment, String language)
    {
        return multiLingualObjectCache.containsKey(formSegmentChar + language + langTermChar + formattedSegment);
    }

    /**
     * containsPlainTextSegment checks if a plainText segment for a given language is contained in the Collection
     * 
     * @param plainTextSegment
     *            the plan text segment
     * @param language
     *            the language to search for
     * @return true if contained
     */
    public boolean containsPlainTextSegment(String plainTextSegment, String language)
    {
        return multiLingualObjectCache.containsKey(plainSegmentChar + language + langTermChar + plainTextSegment);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.folt.models.datamodel.BasicDataSource#createDataSource(de.folt.models.datamodel.DataSourceProperties)
     */
    @Override
    public boolean createDataSource(DataSourceProperties dataModelProperties) throws OpenTMSException
    {
        // TODO Auto-generated method stub
        return super.createDataSource(dataModelProperties);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.folt.models.datamodel.BasicDataSource#deleteDataSource(de.folt.models.datamodel.DataSourceProperties)
     */
    @Override
    public boolean deleteDataSource(DataSourceProperties dataModelProperties) throws OpenTMSException
    {
        // TODO Auto-generated method stub
        return super.deleteDataSource(dataModelProperties);
    }

    /**
     * get the MUL based on its id
     * 
     * @param id
     *            an id to search
     * @return the MUL
     */
    public synchronized MultiLingualObject get(Integer id)
    {
        return multiLingualObjectCache.get(id + "");
    }

    /**
     * getFormattedSegment the MUL based on a formatted segment and language
     * 
     * @param formattedSegment
     * @param language
     * @return the MUL
     */
    public MultiLingualObject getFormattedSegment(String formattedSegment, String language)
    {
        return multiLingualObjectCache.get(formSegmentChar + language + langTermChar + formattedSegment);
    }

    /**
     * getPlainTextSegment the MUL based on a plaintext segment and language
     * 
     * @param plainTextSegment
     * @param language
     * @return the MUL
     */
    public MultiLingualObject getPlainTextSegment(String plainTextSegment, String language)
    {
        return multiLingualObjectCache.get(plainSegmentChar + language + langTermChar + plainTextSegment);
    }

    public MultiLingualObject put(MultiLingualObject value)
    {
        Vector<MonoLingualObject> monos = value.getMonoLingualObjectsAsVector();

        for (int i = 0; i < monos.size(); i++)
        {
            this.multiLingualObjectCache.put(formSegmentChar + monos.get(i).getLanguage() + langTermChar + monos.get(i).getFormattedSegment(), value);
            this.multiLingualObjectCache.put(plainSegmentChar + monos.get(i).getLanguage() + langTermChar + monos.get(i).getPlainTextSegment(), value);
            MonoLingualFuzzyNode fuzzyNodeToAdd = new MonoLingualFuzzyNode(monos.get(i));
            fuzzyTree.insertFuzzyNode(fuzzyNodeToAdd);
        }

        // store MuL id
        this.multiLingualObjectCache.put(value.getId() + "", value);

        return value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.folt.models.datamodel.BasicDataSource#removeDataSource()
     */
    @Override
    public void removeDataSource()
    {
        // TODO Auto-generated method stub
        multiLingualObjectCache.clear();
        multiLingualObjectCache = null;
        super.removeDataSource();
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.folt.models.datamodel.BasicDataSource#removeMonoLingualObject(de.folt.models.datamodel.MonoLingualObject)
     */
    @Override
    public boolean removeMonoLingualObject(MonoLingualObject monoLingualObject)
    {
        MultiLingualObject multi = this.getFormattedSegment(monoLingualObject.getFormattedSegment(), monoLingualObject.getLanguage());
        if (multi == null)
            return false;
        Vector<MonoLingualObject> monos = multi.getMonoLingualObjectsAsVector();
        for (int i = 0; i < monos.size(); i++)
        {
            if (monos.get(i).getFormattedSegment().equals(monoLingualObject.getFormattedSegment()))
            {
                multi.removeMonoLingualObject(monos.get(i));
                return true;
            }
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
        if (this.multiLingualObjectCache.containsKey(multiLingualObject.getId()))
        {
            this.multiLingualObjectCache.remove(multiLingualObject.getId());
            return true;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.folt.models.datamodel.BasicDataSource#search(de.folt.models.datamodel.MonoLingualObject, java.util.Hashtable)
     */
    @Override
    public Vector<MonoLingualObject> search(MonoLingualObject searchMonoLingualObject, Hashtable<String, Object> searchParameters)
    {
        MultiLingualObject multi = null;
        if ((multi = this.getFormattedSegment(searchMonoLingualObject.getFormattedSegment(), searchMonoLingualObject.getLanguage())) != null)
        {
            Vector<MonoLingualObject> monos = multi.getMonoLingualObjectsAsVector(searchMonoLingualObject.getLanguage());
            for (int i = 0; i < monos.size(); i++)
            {
                if (monos.get(i).getFormattedSegment().equals(searchMonoLingualObject.getFormattedSegment()))
                {
                    Vector<MonoLingualObject> retVec = new Vector<MonoLingualObject>();
                    retVec.add(monos.get(i));
                    return retVec;
                }
            }
        }
        return null;

    }

    /*
     * (non-Javadoc)
     * 
     * @see de.folt.models.datamodel.BasicDataSource#translate(org.jdom.Element, de.folt.models.documentmodel.document.XmlDocument, java.lang.String, java.lang.String, int, java.util.Hashtable)
     */
    @Override
    public Element translate(Element transUnit, Element file, XliffDocument xliffDocument, String sourceLanguage, String targetLanguage, int matchSimilarity, Hashtable<String, Object> translationParameters)
            throws OpenTMSException
    {
        // TODO Auto-generated method stub
        String approved = transUnit.getAttributeValue("approved");
        if ((approved != null) && approved.equals("yes"))
            return transUnit;

        Element source = transUnit.getChild("source");
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
                if (targetmonos == null)
                    continue;
                // we must check the Levenshtein similarity now
                if (fzresult.getLevenDistance()[j] < matchSimilarity)
                    continue;
                Element alttrans = xliffDocument.addAltTrans(transUnit, sourceMono, targetmonos, (int) (fzresult.getLevenDistance()[j]), translationParameters);
                alttrans.setAttribute("origin", (String) dataSourceProperties.get("MultiLingualObjectCollection"));
            }
        }
        return transUnit;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.folt.models.datamodel.BasicDataSource#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable arg0, Object arg1)
    {
        // TODO Auto-generated method stub
        super.update(arg0, arg1);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.folt.models.datamodel.BasicDataSource#bPersist()
     */
    @Override
    public boolean bPersist()
    {
        String filename = (String) dataSourceProperties.get("dataSource");
        if (filename == null)
            return false;
        FileOutputStream fileOut;
        try
        {
            fileOut = new FileOutputStream(filename);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(multiLingualObjectCache);
            out.close();
            fileOut.close();
            return true;
        }
        catch (FileNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

}
