/*
 * Created on 28.11.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.fuzzy;

import java.util.Vector;

/**
 * @author klemens
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class FuzzyNodeSearchThread<K, T> implements Runnable
{

    private FuzzyNode<K, T> fuzzyCompareKey = null;

    private Vector<FuzzyNodeSearchResult<K, T>> fuzzySearchResult = null;

    private FuzzyNode<K, T> searchNode = null;

    private int similarity = 100;

    /**
     * @param searchNode
     * @param fuzzyCompareKey
     * @param similarity
     */
    public FuzzyNodeSearchThread(FuzzyNode<K, T> searchNode, FuzzyNode<K, T> fuzzyCompareKey, int similarity)
    {
        super();
        this.fuzzyCompareKey = fuzzyCompareKey;
        this.similarity = similarity;
        this.searchNode = searchNode;
    }

    /**
     * @return the fuzzySearchResult
     */
    public Vector<FuzzyNodeSearchResult<K, T>> getFuzzySearchResult()
    {
        return fuzzySearchResult;
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run()
    {
        fuzzySearchResult = searchNode.search(fuzzyCompareKey, similarity);
    }

    /**
     * @param fuzzySearchResult the fuzzySearchResult to set
     */
    public void setFuzzySearchResult(Vector<FuzzyNodeSearchResult<K, T>> fuzzySearchResult)
    {
        this.fuzzySearchResult = fuzzySearchResult;
    }

}
