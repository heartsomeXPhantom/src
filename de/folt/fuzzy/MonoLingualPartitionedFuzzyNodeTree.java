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

import de.folt.models.datamodel.MonoLingualObject;
import de.folt.util.ObservableHashtable;

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
public class MonoLingualPartitionedFuzzyNodeTree extends Observable implements Serializable
{

    /**
     * 
     */
    private static final long serialVersionUID = -3145477746257042415L;
    private int iMaxIndex = 0;
    private ObservableHashtable<String, StringPartitionedFuzzyNodeTree<MonoLingualObject>> languageNodeTrees = new ObservableHashtable<String, StringPartitionedFuzzyNodeTree<MonoLingualObject>>();
    
    private int NODESMATCHED = 0;

    private int NODESPUSHED = 0;

    private int NODESSEARCHED = 0;

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
            StringPartitionedFuzzyNodeTree<MonoLingualObject> f = languageNodeTrees.get(key);
            iSum = iSum + f.countNodes();
        }

        return iSum;
    }

    /**
     * format return a formated partitioned fuzzy tree based on the fuzzy nodes
     * 
     * @return formatted partitioned fuzzy tree as string
     */
    public String format()
    {
        String str = "MonoLingualPartitionedFuzzyNodeTree: " + this + "\n";
        Enumeration<String> en = languageNodeTrees.keys();
        while(en.hasMoreElements())
        {
            String lang = en.nextElement();
            if (languageNodeTrees.containsKey(lang))
            {
                str = str + languageNodeTrees.get(lang).format();
            }
        }
        
        return str;
    };

    /**
     * getRoot returns a specific fuzzy Node tree
     * 
     * @param language
     *            the language of the partitioned FuzzyTree to return
     * @return the language partitioned fuzzy node tree corresponding to the KEYSUM of the fuzzy node of this tree
     */
    public StringPartitionedFuzzyNodeTree<MonoLingualObject> getFuzzyNodeTree(String language)
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
    public Hashtable<String, StringPartitionedFuzzyNodeTree<MonoLingualObject>> getLanguageNodeTrees()
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
     * @return true for success, false for failure
     */
    public synchronized boolean insertFuzzyNode(MonoLingualFuzzyNode fuzzyNodeToAdd)
    {
        Vector<MonoLingualObject> monos = fuzzyNodeToAdd.getValues();
        MonoLingualObject mono = monos.get(0);
        String language = mono.getLanguage();

        if (languageNodeTrees.containsKey(language))
        {
            StringPartitionedFuzzyNodeTree<MonoLingualObject> fnt = languageNodeTrees.get(language);
            return fnt.insertFuzzyNode((FuzzyNode<String, MonoLingualObject>) fuzzyNodeToAdd);
        }
        else
        {
            StringPartitionedFuzzyNodeTree<MonoLingualObject> fnt = new StringPartitionedFuzzyNodeTree<MonoLingualObject>();
            languageNodeTrees.put(language, fnt);
            return fnt.insertFuzzyNode(fuzzyNodeToAdd);
        }
    }
    
    /**
     * removeValue removes a value from the value list of the values of the node based on a key. The values of the key are the objects to remove from the list
     * 
     * @param fuzzyCompareKey
     *            the key containing the value to remove
     * @return true when successfully removed, otherwise false
     */
    public boolean removeValue(MonoLingualFuzzyNode fuzzyCompareKey)
    {
        Vector<FuzzyNodeSearchResult<String, MonoLingualObject>> searchresult = search(fuzzyCompareKey, 100);
        if ((searchresult != null) && (searchresult.size() > 0))
        {
            FuzzyNodeSearchResult<String, MonoLingualObject> result = searchresult.get(0); // we expect only one result as 100% match
            if (result.getFuzzyNode() != null)
            {
                boolean bRemoved = result.getFuzzyNode().removeValue(fuzzyCompareKey.getValues());
                if (bRemoved)
                {
                    this.setChanged();
                    this.notifyObservers(this);
                }
                return bRemoved;
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
     * @return a vector of FuzzyNodeSearchResult
     */
    public Vector<FuzzyNodeSearchResult<String, MonoLingualObject>> search(MonoLingualFuzzyNode fuzzyCompareKey, int similarity)
    {
        // get the root node for the node
        Vector<MonoLingualObject> monos = fuzzyCompareKey.getValues();
        MonoLingualObject mono = monos.get(0);
        String language = mono.getLanguage();

        if (languageNodeTrees.containsKey(language))
        {
            Vector<FuzzyNodeSearchResult<String, MonoLingualObject>> result = languageNodeTrees.get(language).search(fuzzyCompareKey, similarity);
            this.NODESMATCHED = fuzzyCompareKey.getNODESMATCHED();
            this.NODESPUSHED = fuzzyCompareKey.getNODESPUSHED();
            this.NODESSEARCHED = fuzzyCompareKey.getNODESSEARCHED();
            return result;
        }

        return null;
    }

}
