/*
 * Created on 24.01.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.fuzzy;

import java.util.Vector;

/**
 * This class implements a FuzzyNode and uses a String as the reference value for the key. The fuzzy key is computed from the given string.
 * @author klemens
 *
  */
public class StringFuzzyNode<T> extends FuzzyNode<String, T>
{
    /**
     * 
     */
    private static final long serialVersionUID = -4222191262325345946L;

    /**
     * 
     */
    public StringFuzzyNode()
    {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param string the string for the FuzzyNodeKey construction.
     * @param value the value for the FuzzyNode
     */
    public StringFuzzyNode(String string, T value)
    {
        this(string, value, FuzzyNodeKey.getDefaultFuzzyKeyLength());
    }

    /**
     * @param string the string for the FuzzyNodeKey construction
     * @param value value the value for the FuzzyNode
     * @param iKeyLen the key length for the FuzzyNodeKey
     */
    public StringFuzzyNode(String string, T value, int iKeyLen)
    {
        if (values == null)
            values = new Vector<T>();
        values.add(value);
        fuzzyNodeKey = new FuzzyNodeKey(string, nGram, iKeyLen);
        leftSon = null;
        rightSon = null;
        status = FUZZYNODESTATUS.NEW;
        LEVEL = 0;
        maxID = 0;
        nodeID = 0;
    }

    /* (non-Javadoc)
     * @see de.folt.fuzzy.FuzzyNode#remove(java.lang.Object)
     */
    @Override
    public void remove(Object value)
    {
        // TODO Auto-generated method stub
        super.remove(value);
    }

    /* (non-Javadoc)
     * @see de.folt.fuzzy.FuzzyNode#removeValue(java.lang.Object)
     */
    @Override
    public boolean removeValue(Object value)
    {
        // TODO Auto-generated method stub
        return super.removeValue(value);
    }

    /* (non-Javadoc)
     * @see de.folt.fuzzy.FuzzyNode#search(de.folt.fuzzy.FuzzyNode, int)
     */
    @Override
    public Vector<FuzzyNodeSearchResult<String, T>> search(FuzzyNode<String, T> fuzzyCompareKey, int similarity)
    {
        // TODO Auto-generated method stub
        return super.search(fuzzyCompareKey, similarity);
    }

    /* (non-Javadoc)
     * @see de.folt.fuzzy.FuzzyNode#search(java.lang.Object)
     */
    @Override
    public Vector<FuzzyNode<String, T>> search(Object value)
    {
        // TODO Auto-generated method stub
        return super.search(value);
    }
    
    public Vector<FuzzyNodeSearchResult<String, T>> search(String string, int similarity)
    {
        StringFuzzyNode<T> fuzzyCompareKey = new StringFuzzyNode<T>(string, null);
        return search(fuzzyCompareKey, similarity);
    }

    /* (non-Javadoc)
     * @see de.folt.fuzzy.FuzzyNode#shortFormat()
     */
    @Override
    public String shortFormat()
    {
        // TODO Auto-generated method stub
        return super.shortFormat();
    }
    
    
}
