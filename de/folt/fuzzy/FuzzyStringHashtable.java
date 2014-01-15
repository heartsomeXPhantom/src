/*
 * Created on 08.02.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.fuzzy;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import de.folt.similarity.LevenshteinSimilarity;

/**
 * A fuzzy string class where the strings are stored in a hash table which uses the length of the string as a key. The element stored (value) is a FuzzyDataStructureElement<String, T>>.<br>
 * The search uses Levenshtein similarity to determine the strings which match. 
 * @author klemens
 * 
 */
public class FuzzyStringHashtable<T> extends FuzzyDataStructure<String, T, FuzzyStringHashtableResult<T>>
{

    /**
     * 
     */
    private static final long serialVersionUID = -7766722423120265238L;

    private int NODESMATCHED = 0;

    private int NODESPUSHED = 0;

    private int NODESSEARCHED = 0;

    private Hashtable<String, Vector<FuzzyDataStructureElement<String, T>>> stringHashtable = null;

    /*
     * (non-Javadoc)
     * 
     * @see de.folt.fuzzy.FuzzyDataStructure#countNodes()
     */
    @Override
    public int countNodes()
    {
        int iSum = 0;
        Enumeration<Vector<FuzzyDataStructureElement<String, T>>> enummy = stringHashtable.elements();
        while (enummy.hasMoreElements())
        {
            iSum = iSum + enummy.nextElement().size();
        }
        
        return iSum;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.folt.fuzzy.FuzzyDataStructure#format()
     */
    @Override
    public String format()
    {
        return "";
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.folt.fuzzy.FuzzyDataStructure#getNODESMATCHED()
     */
    @Override
    public int getNODESMATCHED()
    {
        return NODESMATCHED;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.folt.fuzzy.FuzzyDataStructure#getNODESPUSHED()
     */
    @Override
    public int getNODESPUSHED()
    {
        return NODESPUSHED;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.folt.fuzzy.FuzzyDataStructure#getNODESSEARCHED()
     */
    @Override
    public int getNODESSEARCHED()
    {
        return NODESSEARCHED;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.folt.fuzzy.FuzzyDataStructure#getRoot()
     */
    @Override
    public FuzzyDataStructureElement<String, T> getRoot()
    {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.folt.fuzzy.FuzzyDataStructure#insertFuzzyNode(de.folt.fuzzy.FuzzyDataStructureElement)
     */
    @Override
    public boolean insertFuzzyNode(FuzzyDataStructureElement<String, T> fuzzyNodeToAdd)
    {
        if (stringHashtable == null)
            stringHashtable = new Hashtable<String, Vector<FuzzyDataStructureElement<String, T>>>();

        int iLen = ((FuzzyStringHashtableElement<T>) fuzzyNodeToAdd).getString().length();
        if (stringHashtable.containsKey(iLen + ""))
        {
            stringHashtable.get(iLen + "").add((FuzzyStringHashtableElement<T>) fuzzyNodeToAdd);
        }
        else
        {
            Vector<FuzzyDataStructureElement<String, T>> vec = new Vector<FuzzyDataStructureElement<String, T>>();
            vec.add((FuzzyStringHashtableElement<T>) fuzzyNodeToAdd);
            stringHashtable.put(iLen + "", vec);
        }

        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.folt.fuzzy.FuzzyDataStructure#search(de.folt.fuzzy.FuzzyDataStructureElement, int)
     */
    @Override
    public Vector<FuzzyStringHashtableResult<T>> search(FuzzyDataStructureElement<String, T> fuzzyCompareKey, int similarity)
    {
        int iLen = ((FuzzyStringHashtableElement<T>) fuzzyCompareKey).getString().length();
        int iLower = Math.max(0, iLen - (iLen * similarity) / 100);
        int iUpper = iLen + (iLen * similarity) / 100;
        Vector<FuzzyStringHashtableResult<T>> resvec = new Vector<FuzzyStringHashtableResult<T>>();
        NODESMATCHED = 0;
        NODESPUSHED = 0;
        NODESSEARCHED = 0;

        for (int i = iLower; i <= iUpper; i++)
        {
            if (stringHashtable.containsKey(i + ""))
            {
                Vector<FuzzyDataStructureElement<String, T>> vec = stringHashtable.get(i + "");
                for (int j = 0; j < vec.size(); j++)
                {
                    String search = ((FuzzyStringHashtableElement<T>) vec.get(j)).getString();
                    String compare = ((FuzzyStringHashtableElement<T>) fuzzyCompareKey).getString();
                    int compsimilarity = LevenshteinSimilarity.levenshteinSimilarity(search, compare);
                    NODESSEARCHED++;
                    NODESPUSHED++;
                    if (compsimilarity > similarity)
                    {
                        FuzzyStringHashtableResult<T> res = new FuzzyStringHashtableResult<T>(((FuzzyStringHashtableElement<T>) vec.get(j)));
                        resvec.add(res);
                        NODESMATCHED++;
                    }
                }
            }
        }

        return resvec;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.folt.fuzzy.FuzzyDataStructure#setNODESMATCHED(int)
     */
    @Override
    public void setNODESMATCHED(int nodesmatched)
    {
        NODESMATCHED = nodesmatched;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.folt.fuzzy.FuzzyDataStructure#setNODESPUSHED(int)
     */
    @Override
    public void setNODESPUSHED(int nodespushed)
    {
        NODESPUSHED = nodespushed;

    }

    /*
     * (non-Javadoc)
     * 
     * @see de.folt.fuzzy.FuzzyDataStructure#setNODESSEARCHED(int)
     */
    @Override
    public void setNODESSEARCHED(int nodessearched)
    {
        NODESSEARCHED = nodessearched;
    }

}
