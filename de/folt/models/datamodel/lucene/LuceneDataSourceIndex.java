/*
 * Created on 01.04.2010
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.models.datamodel.lucene;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import de.folt.models.datamodel.DataSource;
import de.folt.models.datamodel.DataSourceInstance;
import de.folt.models.datamodel.LinguisticProperties;
import de.folt.models.datamodel.LinguisticProperty;
import de.folt.models.datamodel.MonoLingualObject;
import de.folt.models.datamodel.MultiLingualObject;
import de.folt.models.documentmodel.tmx.TmxProp;
import de.folt.util.OpenTMSException;
import de.folt.util.OpenTMSInitialJarFileLoader;
import de.folt.util.OpenTMSProperties;

/**
 * @author klemens
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class LuceneDataSourceIndex
{
    public static void main(String[] args)
    {
        if (args.length == 0)
        {
            System.out.println("Usage: java -cp lib/openTMS.jar;lib/external.jar -Xmx1000M de.folt.models.datamodel.lucene.LuceneDataSourceIndex <mode>\n");
            System.out.println("where <mode> is on of:");
            System.out.println("Index Mode:  index <datasource> ");
            System.out.println("Search Mode: <datasource> <defaultqueryfield> <query>+");
            return;
        }
        try
        {
            LuceneDataSourceIndex indexer = null;
            @SuppressWarnings("unused")
            OpenTMSInitialJarFileLoader openTMSInitialJarFileLoader = new OpenTMSInitialJarFileLoader();
            try
            {
                String dataSourceName = args[0];
                if (args[0].equalsIgnoreCase("index"))
                {
                    dataSourceName = args[1];
                    System.out.println("Path where the index will be created for data source: " + dataSourceName);
                    indexer = new LuceneDataSourceIndex(dataSourceName, true);
                    indexer.createIndex();
                    System.out.println("Indexing for data source: " + args[0] + " finished");
                }
                else
                // search
                {
                    String field = args[1];
                    for (int i = 2; i < args.length; i++)
                    {
                        String queryString = args[i];
                        System.out.println(">>>Search for: \"" + queryString + "\"");
                        indexer = new LuceneDataSourceIndex(dataSourceName, false);
                        Vector<OpenTMSLuceneSearchResult> documents = indexer.search(field, queryString);
                        indexer.printAllDocuments(documents, field);
                    }
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                System.out.println("Cannot create index..." + ex.getMessage());
                System.exit(-1);
            }
            indexer.closeIndex();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    private StandardAnalyzer analyzer;

    private String configfile = null;

    private DataSource dataSource = null;;

    private IndexWriter dataSourceLuceneIndex = null;

    private FSDirectory luceneIndexDirectory = null;

    private IndexSearcher luceneIndexSearcher = null;

    private QueryParser luceneParser = null;

    private int maxSearch = 10000;;

    /**
     * Constructor
     * @param indexDir the name of the folder in which the index should be created
     * @param bCreate if bCreate the index will be created; if exists all the documents in there will be deleted
     * @throws java.io.IOException
     */
    @SuppressWarnings("deprecation")
    LuceneDataSourceIndex(String dataSourceName, boolean bCreate) throws IOException
    {
        // get the standard database directory for the index
        try
        {
            dataSource = DataSourceInstance.createInstance(dataSourceName);
            configfile = OpenTMSProperties.getInstance().getOpenTMSProperty("dataSourceConfigurationsFile");

            String dataSourceIndexDirectory = OpenTMSProperties.getInstance().getOpenTMSProperty("dataSourceLuceneIndexDirectory");
            if (dataSourceIndexDirectory == null)
                dataSourceIndexDirectory = OpenTMSProperties.getInstance().getOpenTMSProperty("OpenTMS.dir") + "/database";
            if ((new File(dataSourceIndexDirectory)).exists())
                ;
            else
                (new File(dataSourceIndexDirectory)).mkdir();
            System.out.println("dataSourceIndexDirectory = \"" + dataSourceIndexDirectory + "\"");
            // ok should do some preprocessing on the datasourcename to create a good name...
            // in case of a file name - just the name of the file
            File f = new File(dataSourceName);
            if (f.exists())
            {
                dataSourceName = f.getName();
            }
            else
            {
                dataSourceName.replaceAll("/", "_");
                dataSourceName.replaceAll("\\\\", "_");
            }
            dataSourceIndexDirectory = dataSourceIndexDirectory + "/" + dataSourceName;

            luceneIndexDirectory = FSDirectory.open(new File(dataSourceIndexDirectory));
            dataSourceLuceneIndex = new IndexWriter(luceneIndexDirectory, new StandardAnalyzer(Version.LUCENE_CURRENT), bCreate, IndexWriter.MaxFieldLength.LIMITED);
            if (bCreate)
                dataSourceLuceneIndex.deleteAll();
            setLuceneIndexSearcher(new IndexSearcher(luceneIndexDirectory));
            analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);
        }
        catch (OpenTMSException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * addLinguisticProperties 
     * @param lings
     */
    private void addLinguisticProperties(LinguisticProperties lings)
    {
        if (lings == null)
            return;
        Set<String> enumprop = lings.keySet();
        Iterator<String> it = enumprop.iterator();
        while (it.hasNext())
        {
            String key = (String) it.next();
            Object value = (Object) lings.get(key);
            if (value.getClass().getName().equals("java.util.Vector"))
            {
                ; // igonre vector values
            }
            else
            {
                LinguisticProperty ling = (LinguisticProperty) lings.get(key);
                addLinguisticProperty(ling, key);
            }
        }
    }

    /**
     * addLinguisticProperty 
     * @param ling
     * @param key
     */
    private void addLinguisticProperty(LinguisticProperty ling, String key)
    {
        if (ling == null)
            return;
        Object lingValue = ling.getValue();
        String classname = lingValue.getClass().getName();
        if (classname.equals("de.folt.models.documentmodel.tmx.TmxProp"))
        {
            TmxProp tmxProp = (TmxProp) lingValue;
            Document doc = new Document();
            addToDoc("language", (String) tmxProp.getLang(), doc);
            addToDoc("content", (String) tmxProp.getContent(), doc);
            addToDoc("type", (String) tmxProp.getType(), doc);
            addToDoc("id", tmxProp.getId() + "", doc);
            addToDoc("key", key, doc);
            try
            {
                dataSourceLuceneIndex.addDocument(doc);
            }
            catch (CorruptIndexException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void addMonoLingualObject(MonoLingualObject mono)
    {
        Document doc = new Document();
        addToDoc("stOwner", mono.getStOwner(), doc);
        addToDoc("uniqueId", mono.getStUniqueID(), doc);
        addToDoc("id", mono.getId() + "", doc);
        addToDoc("lingType", mono.getLingType() + "", doc);
        addToDoc("lastAccessTime", mono.getLastAccessTime() + "", doc);
        addToDoc("language", mono.getLanguage(), doc);
        addToDoc("formattedSegment", mono.getFormattedSegment(), doc);
        addToDoc("plainTextSegment", mono.getPlainTextSegment(), doc);
        try
        {
            dataSourceLuceneIndex.addDocument(doc);
        }
        catch (CorruptIndexException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        LinguisticProperties lings = mono.getLinguisticProperties();
        addLinguisticProperties(lings);
    }

    /**
     * addMultiLingualObject 
     * @param multi
     */
    private void addMultiLingualObject(MultiLingualObject multi)
    {
        Document doc = new Document();
        addToDoc("stOwner", multi.getStOwner(), doc);
        addToDoc("uniqueId", multi.getStUniqueID(), doc);
        addToDoc("id", multi.getId() + "", doc);
        addToDoc("lingType", multi.getLingType() + "", doc);
        addToDoc("lastAccessTime", multi.getLastAccessTime() + "", doc);
        try
        {
            dataSourceLuceneIndex.addDocument(doc);
        }
        catch (CorruptIndexException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        addLinguisticProperties(multi.getLinguisticProperties());
        Vector<MonoLingualObject> monos = multi.getMonoLingualObjectsAsVector();
        for (int i = 0; i < monos.size(); i++)
        {
            MonoLingualObject mono = monos.get(i);
            addMonoLingualObject(mono);
        }

    }

    private void addToDoc(String field, String property, Document doc)
    {
        if ((field != null) && (property != null) && (doc != null))
        {
            Field.Index iUse = Field.Index.NOT_ANALYZED;
            if (field.matches(".*Segment.*"))
                iUse = Field.Index.ANALYZED;
            doc.add(new Field(field, property, Field.Store.YES, iUse));
        }
    }

    /**
     * Close the index.
     * @throws java.io.IOException
     */
    public void closeIndex() throws IOException
    {
        dataSourceLuceneIndex.optimize();
        dataSourceLuceneIndex.close();
    }

    /**
     * createIndex 
     */
    private void createIndex()
    {
        try
        {
            dataSource.initEnumeration();
            while (dataSource.hasMoreElements())
            {
                MultiLingualObject multi = dataSource.nextElement();
                addMultiLingualObject(multi);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * @return the analyzer
     */
    public StandardAnalyzer getAnalyzer()
    {
        return analyzer;
    }

    /**
     * @return the configfile
     */
    public String getConfigfile()
    {
        return configfile;
    }

    /**
     * @return the dataSource
     */
    public DataSource getDataSource()
    {
        return dataSource;
    }

    /**
     * @return the dataSourceLuceneIndex
     */
    public IndexWriter getDataSourceLuceneIndex()
    {
        return dataSourceLuceneIndex;
    }

    /**
     * @return the luceneIndexDirectory
     */
    public FSDirectory getLuceneIndexDirectory()
    {
        return luceneIndexDirectory;
    }

    /**
     * @return the luceneIndexSearcher
     */
    public IndexSearcher getLuceneIndexSearcher()
    {
        return luceneIndexSearcher;
    }

    /**
     * @return the luceneParser
     */
    public QueryParser getLuceneParser()
    {
        return luceneParser;
    }

    /**
     * @return the maxSearch
     */
    public int getMaxSearch()
    {
        return maxSearch;
    }

    private void printAllDocuments(Vector<OpenTMSLuceneSearchResult> documents, String searchField)
    {
        for (int i = 0; i < documents.size(); i++)
        {
            System.out.println("Document " + i + ": ID=" + documents.get(i).getScoreDocument().doc + " Score = " + documents.get(i).getScoreDocument().score);
            printDocument(documents.get(i).getDocument(), searchField);
        }
    }

    private void printDocument(Document document, String searchField)
    {
        List<Fieldable> docfields = document.getFields();
        for (int k = 0; k < docfields.size(); k++)
        {
            String locField = docfields.get(k).name();
            String[] values = document.getValues(locField);
            String value = "";
            for (int l = 0; l < values.length; l++)
            {
                value = value + values[l] + ";";
            }
            if ((searchField != null) && searchField.equals(locField))
            {
                System.out.println("+++#: " + k + " Field: \"" + locField + "\" Value: \"" + value + "\"");
            }
            else
            {
                System.out.println("---#: " + k + " Field: \"" + locField + "\" Value: \"" + value + "\"");
            }

        }
    }

    /**
     * search 
     * @param field
     */
    @SuppressWarnings("deprecation")
    private Vector<OpenTMSLuceneSearchResult> search(String field, String queryString)
    {
        Vector<OpenTMSLuceneSearchResult> result = new Vector<OpenTMSLuceneSearchResult>();
        try
        {
            setLuceneParser(new QueryParser(Version.LUCENE_CURRENT, field, analyzer));
            Query query = this.luceneParser.parse(queryString);
            TopDocs hits = this.luceneIndexSearcher.search(query, maxSearch);
            int totalHit = hits.totalHits;
            System.out.println("hits.totalHits = " + totalHit);

            for (int j = 0; j < Math.min(hits.totalHits, maxSearch); j++)
            {
                ScoreDoc scoreDoc = hits.scoreDocs[j];
                // float fdocmatch = scoreDoc.score;
                int documentId = scoreDoc.doc;
                // System.out.println(j + ": doc = " + documentId + " = " + fdocmatch);
                Document document = this.luceneIndexSearcher.doc(documentId);
                OpenTMSLuceneSearchResult searchResult = new OpenTMSLuceneSearchResult(document, scoreDoc);
                result.add(searchResult);
            }
        }
        catch (CorruptIndexException e)
        {
            e.printStackTrace();
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @param analyzer the analyzer to set
     */
    public void setAnalyzer(StandardAnalyzer analyzer)
    {
        this.analyzer = analyzer;
    }

    /**
     * @param configfile the configfile to set
     */
    public void setConfigfile(String configfile)
    {
        this.configfile = configfile;
    }

    /**
     * @param dataSource the dataSource to set
     */
    public void setDataSource(DataSource dataSource)
    {
        this.dataSource = dataSource;
    }

    /**
     * @param dataSourceLuceneIndex the dataSourceLuceneIndex to set
     */
    public void setDataSourceLuceneIndex(IndexWriter dataSourceLuceneIndex)
    {
        this.dataSourceLuceneIndex = dataSourceLuceneIndex;
    }

    /**
     * @param luceneIndexDirectory the luceneIndexDirectory to set
     */
    public void setLuceneIndexDirectory(FSDirectory luceneIndexDirectory)
    {
        this.luceneIndexDirectory = luceneIndexDirectory;
    }

    /**
     * @param luceneIndexSearcher the luceneIndexSearcher to set
     */
    public void setLuceneIndexSearcher(IndexSearcher luceneIndexSearcher)
    {
        this.luceneIndexSearcher = luceneIndexSearcher;
    }

    /**
     * @param luceneParser the luceneParser to set
     */
    public void setLuceneParser(QueryParser luceneParser)
    {
        this.luceneParser = luceneParser;
    }

    /**
     * @param maxSearch the maxSearch to set
     */
    public void setMaxSearch(int maxSearch)
    {
        this.maxSearch = maxSearch;
    }

}
