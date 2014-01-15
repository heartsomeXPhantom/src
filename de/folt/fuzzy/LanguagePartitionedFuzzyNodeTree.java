/*
 * Created on 02.02.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.fuzzy;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Vector;

import de.folt.util.ObservableHashtable;
import de.folt.util.OpenTMSSupportFunctions;

/**
 * This class extends the class StringFuzzyNodeTree by adding an additional layer to the structure of the underlying tree.<br>
 * For each language resulting from a MonoLingualObject a separate  StringFuzzyTree is generated and stored in a ObservableHashtable.
 *
 *  <pre>
 *  Structure:
 *  [de] [en] [fr]           ....        [es] .... [it] (a hash table)
 *   |                                     |
 *   |                                    references the corresponding StringFuzzyTree for language es   
 *   |                          
 *   references the corresponding StringFuzzyTreefor language de 
 *   | (this references the StringFuzzyTree level)
  *  [0] [1] [2]           ....        [i] .... [n] (a hash table)
 *   |                                 |
 *   references the root fuzzy node    references the root fuzzy node 
 *   with key sum 0                    with key sum i
 *  
 *  </pre>
 * @author klemens
 * 
 */
public class LanguagePartitionedFuzzyNodeTree<T> extends Observable implements Serializable
{

    /**
     * 
     */
    private static final long serialVersionUID = -3145477746257042415L;

    private Thread[] fuzzySearchThreads = null;

    private FuzzyNodeSearchThread<String, T>[] fuzzyThreadNodes = null;

    protected int iFuzzySearchThreads = OpenTMSSupportFunctions.iGetThreadNumber(-1);

    private int iMaxIndex = 0;

    private int iThreadsUsed = 0;

    private ObservableHashtable<String, StringPartitionedFuzzyNodeTree<T>> languageNodeTrees = new ObservableHashtable<String, StringPartitionedFuzzyNodeTree<T>>();

    private int NODESMATCHED = 0;

    private int NODESPUSHED = 0;

    private int NODESSEARCHED = 0;

    private Vector<FuzzyNodeSearchResult<String, T>> threadFuzzyNodeSearchResult = new Vector<FuzzyNodeSearchResult<String, T>>();

    /**
     * checkFuzzyNodeSearchThreadStatus checks if a multi thread fuzzy node search has finished. If yes copies result to threadFuzzyNodeSearchResult and gives thread free for other usage
     * @return true if all threads are finished otherwise false
     */
    public boolean checkFuzzyNodeSearchThreadStatus()
    {
        boolean bAllFinished = false;
        for (int i = 0; i < iFuzzySearchThreads; i++)
        {
            if (fuzzySearchThreads[i] != null)
            {
                if (!fuzzySearchThreads[i].isAlive())
                {
                    threadFuzzyNodeSearchResult.addAll(fuzzyThreadNodes[i].getFuzzySearchResult());
                    fuzzySearchThreads[i] = null;
                    fuzzyThreadNodes[i] = null;
                    iThreadsUsed--;
                }
            }
        }
        if (iThreadsUsed == 0)
            bAllFinished = true;
        return bAllFinished;
    }

    /**
     * countNodes count all the nodes in the tree
     * 
     * @return the nodes in the tree
     */
    public int countNodes()
    {
        Enumeration<String> iter = languageNodeTrees.keys();
        int iSum = 0;
        while (iter.hasMoreElements())
        {
            String key = iter.nextElement();
            StringPartitionedFuzzyNodeTree<T> f = languageNodeTrees.get(key);
            iSum = iSum + f.countNodes();
        }

        return iSum;
    };

    /**
     * format return a formated partitioned fuzzy tree based on the fuzzy nodes
     * 
     * @return formatted partitioned fuzzy tree as string
     */
    public String format()
    {
        String str = "LanguagePartitionedFuzzyNodeTree: " + this + "\n";
        Enumeration<String> en = languageNodeTrees.keys();
        while (en.hasMoreElements())
        {
            String lang = en.nextElement();
            if (languageNodeTrees.containsKey(lang))
            {
                str = str + languageNodeTrees.get(lang).format();
            }
        }

        return str;
    }

    /**
     * getRoot returns a specific fuzzy Node tree
     * 
     * @param language
     *            the language of the partitioned FuzzyTree to return
     * @return the language partitioned fuzzy node tree corresponding to the KEYSUM of the fuzzy node of this tree
     */
    public StringPartitionedFuzzyNodeTree<T> getFuzzyNodeTree(String language)
    {
        return languageNodeTrees.get(language);
    }

    /**
     * @return the iMaxIndex -highest KEYSUm currently in the partitioned Fuzzy Node Tree
     */
    public int getIMaxIndex()
    {
        return iMaxIndex;
    }

    /**
     * @return the languageNodeTrees
     */
    public Hashtable<String, StringPartitionedFuzzyNodeTree<T>> getLanguageNodeTrees()
    {
        return languageNodeTrees;
    }

    /**
     * @return the nODESMATCHED
     */
    public int getNODESMATCHED()
    {
        return NODESMATCHED;
    }

    /**
     * @return the nODESPUSHED
     */
    public int getNODESPUSHED()
    {
        return NODESPUSHED;
    }

    /**
     * @return the nODESSEARCHED
     */
    public int getNODESSEARCHED()
    {
        return NODESSEARCHED;
    }

    /**
     * insertFuzzyNode inserts a Fuzzy node into a partitioned fuzzy node tree;
     * 
     * @param fuzzyNodeToAdd
     *            fuzzy node to insert
     * @param language language of the node    
     * @return true for success, false for failure
     */
    public synchronized boolean insertFuzzyNode(StringFuzzyNode<T> fuzzyNodeToAdd, String language)
    {
        if (languageNodeTrees.containsKey(language))
        {
            StringPartitionedFuzzyNodeTree<T> fnt = languageNodeTrees.get(language);
            return fnt.insertFuzzyNode((FuzzyNode<String, T>) fuzzyNodeToAdd);
        }
        else
        {
            StringPartitionedFuzzyNodeTree<T> fnt = new StringPartitionedFuzzyNodeTree<T>();
            languageNodeTrees.put(language, fnt);
            return fnt.insertFuzzyNode(fuzzyNodeToAdd);
        }
    }

    /**
     * pushToFuzzyNodeSearchThread if there a threads given the processor number available runs a search in a new thread
     * @param searchNode the node to start search with
     * @param fuzzyCompareKey the key to compare against
     * @param similarity the similarity used for the search
     * @return true if the search is done in a thread otherwise false
     */
    public boolean pushToFuzzyNodeSearchThread(FuzzyNode<String, T> searchNode, FuzzyNode<String, T> fuzzyCompareKey, int similarity)
    {
        if (fuzzySearchThreads == null)
        {
            fuzzySearchThreads = new Thread[iFuzzySearchThreads];
        }
        // search for free thread
        for (int i = 0; i < iFuzzySearchThreads; i++)
        {
            if (fuzzySearchThreads[i] == null)
            {
                fuzzyThreadNodes[iThreadsUsed] = new FuzzyNodeSearchThread<String, T>(searchNode, fuzzyCompareKey, similarity);
                fuzzySearchThreads[iThreadsUsed] = new Thread(fuzzyThreadNodes[iThreadsUsed]);
                fuzzySearchThreads[iThreadsUsed].start();
                iThreadsUsed++;
                return true;
            }
        }

        return false;
    }

    /**
     * search returns all a FuzzyNodeSearchResult of all matching FuzzyNodes
     * 
     * @param fuzzyCompareKey
     *            the fuzzy node to search for with similarity
     * @param similarity
     *            the similarity to search for
     * @param language language for the search    
     * @return a vector of FuzzyNodeSearchResult
     */
    public Vector<FuzzyNodeSearchResult<String, T>> search(StringFuzzyNode<T> fuzzyCompareKey, int similarity, String language)
    {
        if (languageNodeTrees.containsKey(language))
        {
            Vector<FuzzyNodeSearchResult<String, T>> result = languageNodeTrees.get(language).search(fuzzyCompareKey, similarity);
            this.NODESMATCHED = fuzzyCompareKey.getNODESMATCHED();
            this.NODESPUSHED = fuzzyCompareKey.getNODESPUSHED();
            this.NODESSEARCHED = fuzzyCompareKey.getNODESSEARCHED();
            return result;
        }

        return null;
    }
}
