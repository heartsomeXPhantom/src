/*
 * Created on 22.01.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.test.db4o;

import java.io.File;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import org.jdom.Element;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Predicate;

import de.folt.fuzzy.FuzzyNodeSearchResult;
import de.folt.fuzzy.StringFuzzyNode;
import de.folt.models.datamodel.MonoLingualObject;
import de.folt.models.datamodel.MultiLingualObject;
import de.folt.models.documentmodel.tmx.TmxDocument;
import de.folt.util.Timer;

/**
 * @author klemens
 * 
 *         To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and Comments
 */
public class Test
{

    public class TestObserver implements Observer
    {

        /*
         * (non-Javadoc)
         * 
         * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
         */
        @SuppressWarnings("unchecked")
        @Override
        public void update(Observable arg0, Object arg1)
        {
            db.store((StringFuzzyNode<String>) arg1);
            System.out.println("Change Fuzzy Node: " + ((StringFuzzyNode<String>) arg1).getNodeID());
        }

    }

    private static ObjectContainer db = null;

    private static String searchString = null;

    public static void listMonoLingualObjectResult(ObjectSet<MonoLingualObject> result, MonoLingualObject mono)
    {
        System.out.println(result.size());
        while (result.hasNext())
        {
            MonoLingualObject monofound = (MonoLingualObject) result.next();
            int levenPlainSimilarity = 0;
            if (mono.getPlainTextSegment() != null)
                levenPlainSimilarity = de.folt.similarity.LevenshteinSimilarity.levenshteinSimilarity(mono.getPlainTextSegment(), monofound.getPlainTextSegment());
            System.out.println("Found: levenPlainSimilarity(" + mono.getPlainTextSegment() + ")=" + levenPlainSimilarity + "\n" + monofound.format());
        }
    }

    public static void listMultiLingualObjectResult(ObjectSet<MultiLingualObject> result)
    {
        System.out.println(result.size());
        while (result.hasNext())
        {
            System.out.println(((MultiLingualObject) result.next()).format());
        }
    }

    /**
     * main
     * 
     * @param args
     */
    public static void main(String[] args)
    {
        // TODO Auto-generated method stub
        Test test = new Test();
        de.folt.models.datamodel.Test multtest = new de.folt.models.datamodel.Test();
        multtest.test(false);
        test.multvector = multtest.getMultvector();
        // now open a db40 database
        if (args.length == 0)
        {
            db = Db4o.openFile("foltdb4o");

            try
            {
                // do something with db4o
                Timer timer = new Timer();
                timer.startTimer();
                System.out.println("Start Time: " + timer.getStartTime());
                for (int i = 0; i < test.multvector.size(); i++) // generate 1000 MUL Objects
                {
                    MultiLingualObject multi = test.multvector.get(i);
                    db.store(multi);
                    // System.out.println("MultiLingualObject " + i + ": " + test.multvector.get(i).toString());
                    // System.out.println((test.multvector.get(i)).format());
                }
                timer.stopTimer();
                System.out.println("Stop Time: " + timer.getStopTime());
                System.out.println(timer.timerString("Writting to  db4o foltdb4o", test.multvector.size()));
                timer.startTimer();
                MultiLingualObject protomulti = new MultiLingualObject();
                ObjectSet<MultiLingualObject> result = db.queryByExample(protomulti);
                listMultiLingualObjectResult(result);
                timer.stopTimer();
                System.out.println("Stop Time: " + timer.getStopTime());
                System.out.println(timer.timerString("Reading all MULs  db4o foltdb4o", test.multvector.size()));

            }
            finally
            {
                db.close();
            }
        }
        else
        {
            String tmxfile = args[0];
            String database = tmxfile.replaceAll("tmx$", "");
            if (args.length >= 2)
                database = args[1];
            else if (args.length == 1)
            {
                File f = new File(database);
                if (f.exists())
                    f.delete();
            }
            System.out.println("TMX file; " + tmxfile + ": database " + database);
            db = Db4o.openFile(database);
            try
            {
                Timer timer = new Timer();
                timer.startTimer();
                File f = new File(tmxfile);
                TmxDocument doc = new TmxDocument();
                // load the xml file
                doc.loadXmlFile(f);
                timer.stopTimer();
                System.out.println(timer.timerString("TMX file read " + tmxfile + ": Version " + doc.getTmxVersion()));

                System.out.println("TMX file #tuvs = " + doc.getTuList().size());
                StringFuzzyNode<String> root = null;
                TestObserver testob = test.new TestObserver();
                int iFuzzyNodes = 0;
                for (int i = 0; i < doc.getTuList().size(); i++)
                {
                    Element tu = doc.getTuList().get(i);
                    MultiLingualObject multi = doc.tuToMultiLingualObject(tu);
                    db.store(multi);
                    System.out.println("TMX entry: " + i);
                    multi = null;
                    if (1 == 2)
                    {
                        Vector<MonoLingualObject> monos = multi.getMonoLingualObjectsAsVector();
                        for (int j = 0; j < monos.size(); j++)
                        {
                            MonoLingualObject mono = monos.get(j);
                            if (root == null)
                            {
                                root = new StringFuzzyNode<String>(mono.getPlainTextSegment(), mono.getUniqueID());
                                db.store(root);
                                root.addObserver(testob);
                                iFuzzyNodes++;
                            }
                            else
                            {
                                StringFuzzyNode<String> newnode = new StringFuzzyNode<String>(mono.getPlainTextSegment(), mono.getUniqueID());
                                root.insertFuzzyNode(newnode);
                                db.store(newnode);
                                newnode.addObserver(testob);
                                iFuzzyNodes++;
                            }
                        }
                    }
                }

                timer.stopTimer();
                System.out.println("Stop Time: " + timer.getStopTime());
                System.out.println(timer.timerString("Writting to  db4o " + database + " ", doc.getTuList().size()));

                if (1 == 2)
                {
                    StringFuzzyNode<String> newnode = new StringFuzzyNode<String>();
                    ObjectSet<StringFuzzyNode<String>> resultnodes = db.queryByExample(newnode);
                    System.out.println("# Fuzzy Nodes = " + resultnodes.size() + " / iFuzzyNodes = " + iFuzzyNodes);

                    timer.startTimer();

                    int similarity = 80;

                    for (int i = 0; i < doc.getTuList().size(); i++)
                    {
                        Element tu = doc.getTuList().get(i);
                        MultiLingualObject multi = doc.tuToMultiLingualObject(tu);
                        Vector<MonoLingualObject> monos = multi.getMonoLingualObjectsAsVector();
                        for (int j = 0; j < monos.size(); j++)
                        {
                            MonoLingualObject mono = monos.get(j);
                            System.out.println("Search now exact match " + mono.getFormattedSegment() + " / " + mono.getLanguage());
                            MonoLingualObject protomono = new MonoLingualObject(mono.getFormattedSegment(), mono.getLanguage());
                            protomono.setLastAccessTime(0);
                            protomono.setLinguisticProperties(null);
                            protomono.setUniqueID(null);
                            ObjectSet<MonoLingualObject> resultmono = db.queryByExample(protomono);
                            listMonoLingualObjectResult(resultmono, mono);

                            System.out.println("Search now Fuzzy Nodes with similarity = " + similarity + " for " + mono.getPlainTextSegment());
                            Vector<FuzzyNodeSearchResult<String, String>> fuzzyresult = root.search(mono.getPlainTextSegment(), similarity);
                            System.out.println("# Fuzzy Node search results = " + fuzzyresult.size());
                            for (int k = 0; k < fuzzyresult.size(); k++)
                            {
                                FuzzyNodeSearchResult<String, String> fzresult = fuzzyresult.get(k);
                                protomono = new MonoLingualObject();
                                protomono.clearObject();
                                Vector<String> monosfuzzy = fzresult.getFuzzyNode().getValues();
                                System.out.println("# Fuzzy Node Values for results " + k + " = " + monosfuzzy.size());
                                for (int l = 0; l < monosfuzzy.size(); l++)
                                {
                                    String uniqueID = monosfuzzy.get(l);
                                    protomono.setUniqueID(uniqueID);
                                    resultmono = db.queryByExample(protomono);
                                    System.out.println("Fuzzy Node search results for " + l + "= " + resultmono.size());
                                    listMonoLingualObjectResult(resultmono, mono);
                                }

                            }

                            Timer dbotimer = new Timer();
                            dbotimer.startTimer();
                            searchString = mono.getFormattedSegment();
                            List<MonoLingualObject> result = db.query(new Predicate<MonoLingualObject>()
                            {
                                /**
                             * 
                             */
                                private static final long serialVersionUID = -4048622586926913772L;

                                public boolean match(MonoLingualObject mono)
                                {
                                    if (mono.getFormattedSegment().equals(searchString))
                                        return true;
                                    return false;
                                }
                            });
                            dbotimer.stopTimer();
                            System.out.println(timer.timerString(searchString, 1));
                            System.out.println("# dbo native query = " + i + " / results = " + result.size());
                        }
                    }

                    MonoLingualObject protomono = new MonoLingualObject();
                    protomono.clearObject();
                    protomono.setLanguage("de");
                    ObjectSet<MonoLingualObject> resultmono = db.queryByExample(protomono);
                    listMonoLingualObjectResult(resultmono, protomono);

                    timer.stopTimer();
                    System.out.println("Stop Time: " + timer.getStopTime());
                    System.out.println(timer.timerString("Reading all MULs  db4o foltdb4o", doc.getTuList().size()));
                }
                doc = null;
            }
            finally
            {
                db.close();
            }
        }
    }

    private Vector<MultiLingualObject> multvector = null;

}
