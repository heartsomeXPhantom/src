/*
 * Created on 24.01.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.fuzzy;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import de.folt.models.datamodel.MonoLingualObject;
import de.folt.models.datamodel.MultiLingualObject;
import de.folt.util.Timer;

/**
 * This class implements a StringFuzzyNode and uses a MonoLingualObject {@see de.folt.models.datamodel.MonoLingualObject} as the reference value. The fuzzy key is computed from the plain text segment of the MonoLingualObject.
 * @author klemens
 * 
 *         
 */
public class MonoLingualFuzzyNode extends StringFuzzyNode<MonoLingualObject>
{

    /**
     * 
     */
    private static final long serialVersionUID = -7554227310640186759L;


    /**
     * main
     * 
     * @param args
     */
    public static void main(String[] args)
    {
        int iTestNumber = 5;
        if (args.length > 0)
        {
            try
            {
                iTestNumber = Integer.parseInt(args[0]);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                return;
            }
        }
        de.folt.models.datamodel.Test multtest = new de.folt.models.datamodel.Test();
        multtest.test(false, iTestNumber);
        Vector<MultiLingualObject> multvector = multtest.getMultvector();

        try
        {
            // do something with db4o
            MonoLingualFuzzyNode root = null;
            Timer timer = new Timer();
            timer.startTimer();
            System.out.println("Start Time: " + timer.getStartTime());
            int k = 0;

            for (int i = 0; i < multvector.size(); i++) // generate 1000 MUL Objects
            {
                MultiLingualObject multi = multvector.get(i);
                Hashtable<String, MonoLingualObject> monohash = (Hashtable<String, MonoLingualObject>)multi.getMonoLingualObjects();
                Enumeration<MonoLingualObject> enummono = monohash.elements();
                int j = 0;
                while (enummono.hasMoreElements())
                {
                    MonoLingualObject mono = enummono.nextElement();
                    System.out.println("#: " + i + " / " + (j++) + "/" + (k++));
                    MonoLingualFuzzyNode fuzzyNodeToAdd = new MonoLingualFuzzyNode(mono);
                    if (root == null)
                    {
                        root = fuzzyNodeToAdd;
                    }
                    else
                    {
                        root.insertFuzzyNode(fuzzyNodeToAdd);
                    }
                }
            }

            timer.stopTimer();
            System.out.println("Stop Time: " + timer.getStopTime());
            System.out.println(timer.timerString("Inserting  MULs in fuzzy nodes ", k));

            System.out.println("\nFuzzy Tree\n" + root.formatTree() + "\n");

            Timer timer1 = new Timer();
            timer1.startTimer();
            for (int i = 0; i < multvector.size(); i++) // generate 1000 MUL Objects
            {
                MultiLingualObject multi = multvector.get(i);
                Hashtable<String, MonoLingualObject> monohash = (Hashtable<String, MonoLingualObject>)multi.getMonoLingualObjects();
                Enumeration<MonoLingualObject> enummono = monohash.elements();
                while (enummono.hasMoreElements())
                {
                    MonoLingualObject mono = enummono.nextElement();
                    String segment = mono.getFormattedSegment();
                    timer.startTimer();
                    MonoLingualFuzzyNode fuzzyNodeToSearch = new MonoLingualFuzzyNode(mono);
                    Vector<FuzzyNodeSearchResult<String, MonoLingualObject>> found = root.search(fuzzyNodeToSearch, 80);
                    
                    timer.stopTimer();
                    String str = "NODESMATCHED=" + fuzzyNodeToSearch.getNODESMATCHED() + " / NODESPUSHED=" + fuzzyNodeToSearch.getNODESPUSHED() + " / NODESSEARCHED=" + fuzzyNodeToSearch.getNODESSEARCHED()  + "\n";
                    if (found != null)
                    {
                        for (int j = 0; j < found.size(); j++)
                        {
                            FuzzyNodeSearchResult<String, MonoLingualObject> f = found.get(j);
                            FuzzyNode<String, MonoLingualObject> fn = f.getFuzzyNode();
                            str = str + "found #" + j + " " + f.format() + " / " + fn.getFuzzyNodeKey().getKeysum() + "\n";
                            Vector<MonoLingualObject> mls = fn.getValues();
                            for (int n = 0; n < mls.size(); n++)
                            {
                                str = str + "MO#:" + n + ":" + mls.get(n).getFormattedSegment() + "\n";
                            }
                        }
                    }
                    System.out.println(timer.timerString(i + ": Searching with exact MOL in fuzzy nodes \n" + segment + "\nFound: " + found.size() + " / " + fuzzyNodeToSearch.getFuzzyNodeKey().getKeysum()
                            + "\n" + str, 1));

                    timer.startTimer();
                    segment = " " + segment + " ";
                    fuzzyNodeToSearch = new MonoLingualFuzzyNode(mono);
                    found = root.search(fuzzyNodeToSearch, 80);
                    timer.stopTimer();
                    str = "NODESMATCHED=" + fuzzyNodeToSearch.getNODESMATCHED() + " / NODESPUSHED=" + fuzzyNodeToSearch.getNODESPUSHED() + " / NODESSEARCHED=" + fuzzyNodeToSearch.getNODESSEARCHED()  + "\n";

                    if (found != null)
                    {
                        for (int j = 0; j < found.size(); j++)
                        {
                            FuzzyNodeSearchResult<String, MonoLingualObject> f = found.get(j);
                            FuzzyNode<String, MonoLingualObject> fn = f.getFuzzyNode();
                            str = str + "found #" + j + " " + f.format() + " / " + fn.getFuzzyNodeKey().getKeysum() + "\n";
                            Vector<MonoLingualObject> mls = fn.getValues();
                            for (int n = 0; n < mls.size(); n++)
                            {
                                str = str + "MO#:" + n + ":" + mls.get(n).getFormattedSegment() + "\n";
                            }
                        }
                    }
                    System.out.println(timer.timerString(i + ": Searching with modified MOL in fuzzy nodes \n" + segment + "\nFound: " + found.size() + "\n" + str, 1));
                }
            }

            timer1.stopTimer();

            System.out.println(timer1.timerString("Searching MULs in fuzzy nodes ", k * 2));
        }
        finally
        {

        }
    }


    /**
     * Create a new MonoLingualFuzzyNode based on the MonoLingualObject mono. Call MonoLingualFuzzyNode(MonoLingualObject mono, int iKeyLen) with iKeyLen = FuzzyNodeKey.getFuzzyKeyLength(). See {@see de.folt.fuzzy.MonoLingualFuzzyNode#MonoLingualFuzzyNode(MonoLingualObject, int)}
     * @param mono the MonolingualObject to use.
     */
    public MonoLingualFuzzyNode(MonoLingualObject mono)
    {
        this(mono, FuzzyNodeKey.getDefaultFuzzyKeyLength());
    }

    
    /**
     * This constructs a FuzzyNode for a MonoLingualObject. It uses the plain text of the MonoLingualobject (@see {@link de.folt.models.datamodel.MonoLingualObject#getPlainTextSegment()} for the key construction.
     * @param mono the MonolingualObject to use.
     * @param iKeyLen the key length to use for generating the FuzzyNodKey
     */
    public MonoLingualFuzzyNode(MonoLingualObject mono, int iKeyLen)
    {
        if (values == null)
            values = new Vector<MonoLingualObject>();
        values.add(mono);
        fuzzyNodeKey = new FuzzyNodeKey(mono.getPlainTextSegment(), nGram, iKeyLen);
        leftSon = null;
        rightSon = null;
        status = FUZZYNODESTATUS.NEW;
        LEVEL = 0;
        maxID = 0;
        nodeID = 0;
    }
    
    
    /* (non-Javadoc)
     * @see de.folt.fuzzy.FuzzyNode#search(de.folt.fuzzy.FuzzyNode, int)
     */
    @Override
    public Vector<FuzzyNodeSearchResult<String, MonoLingualObject>> search(FuzzyNode<String, MonoLingualObject> fuzzyCompareKey, int similarity)
    {
        Vector<FuzzyNodeSearchResult<String, MonoLingualObject>> resVec =  super.search(fuzzyCompareKey, similarity);
        String monosearch = fuzzyCompareKey.getValues().get(0).getPlainTextSegment();
        for (int i = 0; i < resVec.size(); i++)
        {
            FuzzyNodeSearchResult<String, MonoLingualObject> fuzzyRes = resVec.get(i);
            MonoLingualFuzzyNode monoFuzzy = (MonoLingualFuzzyNode) fuzzyRes.getFuzzyNode();
            Vector<MonoLingualObject> monos = monoFuzzy.getValues();
            float [] levenDistance = new float[monos.size()];
            for (int j = 0; j < monos.size(); j++)
            {
                String plaintext = monos.get(j).getPlainTextSegment();
                levenDistance[j] = de.folt.similarity.LevenshteinSimilarity.levenshteinSimilarity(monosearch, plaintext, similarity);
            }
            fuzzyRes.setLevenDistance(levenDistance);
        }
        
        return resVec;
    }

    /**
     * search search for a StringFuzzyNode
     * @param fuzzyCompareKey the string fuzzy key to compare with
     * @param similarity the similarity in % (0..100)
     * @return
     */
    public Vector<FuzzyNodeSearchResult<String, MonoLingualObject>> search(StringFuzzyNode<MonoLingualObject> fuzzyCompareKey, int similarity)
    {
        Vector<FuzzyNodeSearchResult<String, MonoLingualObject>> resVec =  super.search(fuzzyCompareKey, similarity);
        String monosearch = fuzzyCompareKey.getValues().get(0).getPlainTextSegment();
        for (int i = 0; i < resVec.size(); i++)
        {
            FuzzyNodeSearchResult<String, MonoLingualObject> fuzzyRes = resVec.get(i);
            MonoLingualFuzzyNode monoFuzzy = (MonoLingualFuzzyNode) fuzzyRes.getFuzzyNode();
            Vector<MonoLingualObject> monos = monoFuzzy.getValues();
            float [] levenDistance = new float[monos.size()];
            for (int j = 0; j < monos.size(); j++)
            {
                String plaintext = monos.get(j).getPlainTextSegment();
                levenDistance[j] = de.folt.similarity.LevenshteinSimilarity.getLevenshteinDistance(monosearch, plaintext, similarity);
            }
            fuzzyRes.setLevenDistance(levenDistance);
        }
        
        return resVec;
    }
}
