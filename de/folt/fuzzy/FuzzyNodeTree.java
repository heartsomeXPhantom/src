/*
 * Created on 02.02.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.fuzzy;

import java.io.Serializable;
import java.util.Vector;

/**
 * This class implements a tree based on fuzzy nodes. The main fuzzy node is the root node where all further nodes are added too.
 * 
 * <pre>
 *        root (fuzzy node)
 *             |
 *    ------------------
 *    lefts son    right son
 * </pre>
 * 
 * Pattern: Composite
 * 
 * @author klemens
 * 
 */
public class FuzzyNodeTree<K, T> extends FuzzyDataStructure<K, T, FuzzyNodeSearchResult<K, T>> implements Serializable
{

    /**
     * 
     */
    private static final long serialVersionUID = -2892078891538344997L;
    private int NODESMATCHED = 0;
    
    private int NODESPUSHED = 0;

    private int NODESSEARCHED = 0;

    private FuzzyNode<K, T> root = null;

    /**
     * 
     */
    public FuzzyNodeTree()
    {
        super();
    }

    /* (non-Javadoc)
     * @see de.folt.fuzzy.FuzzyDataStructure#countNodes()
     */
    public int countNodes()
    {
        if (root != null)
            return root.countNodes() + 1;
        else
            return 0;
    }

    /* (non-Javadoc)
     * @see de.folt.fuzzy.FuzzyDataStructure#format()
     */
    public String format()
    {
        return "Fuzzy Node Tree: " + this + " Depth: " + root.getDepth() + " Bal: " + root.iBalance() + " AVL: " + root.isAVLTree() +  " Leafnodes: " + root.iLeafNodes()  + " Balfactor: " + (float)root.countNodes()/(float)root.iLeafNodes() + " KEYSUM: "  + root.getFuzzyNodeKey().getKeysum() + "\n" + root.formatTree();
    }

    /* (non-Javadoc)
     * @see de.folt.fuzzy.FuzzyDataStructure#getNODESMATCHED()
     */
    public int getNODESMATCHED()
    {
        return NODESMATCHED;
    }
    
    /* (non-Javadoc)
     * @see de.folt.fuzzy.FuzzyDataStructure#getNODESPUSHED()
     */
    public int getNODESPUSHED()
    {
        return NODESPUSHED;
    }

    /* (non-Javadoc)
     * @see de.folt.fuzzy.FuzzyDataStructure#getNODESSEARCHED()
     */
    public int getNODESSEARCHED()
    {
        return NODESSEARCHED;
    }

    /* (non-Javadoc)
     * @see de.folt.fuzzy.FuzzyDataStructure#getRoot()
     */
    public FuzzyNode<K, T> getRoot()
    {
        return root;
    }



    /* (non-Javadoc)
     * @see de.folt.fuzzy.FuzzyDataStructure#insertFuzzyNode(de.folt.fuzzy.FuzzyDataStructureElement)
     */
    @Override
    public boolean insertFuzzyNode(FuzzyDataStructureElement<K, T> fuzzyNodeToAdd)
    {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see de.folt.fuzzy.FuzzyDataStructure#insertFuzzyNode(de.folt.fuzzy.FuzzyNode)
     */
    public synchronized boolean insertFuzzyNode(FuzzyNode<K, T> fuzzyNodeToAdd)
    {
        if (root == null)
        {
            root =  fuzzyNodeToAdd;
            return true;
        }
        return root.insertFuzzyNode(fuzzyNodeToAdd);
    }

    /* (non-Javadoc)
     * @see de.folt.fuzzy.FuzzyDataStructure#search(de.folt.fuzzy.FuzzyDataStructureElement, int)
     */
    @Override
    public Vector<FuzzyNodeSearchResult<K, T>> search(FuzzyDataStructureElement<K, T> fuzzyCompareKey, int similarity)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see de.folt.fuzzy.FuzzyDataStructure#search(de.folt.fuzzy.FuzzyNode, int)
     */
    public Vector<FuzzyNodeSearchResult<K, T>> search(FuzzyNode<K, T> fuzzyCompareKey, int similarity)
    {
        if (root != null)
        {
            Vector<FuzzyNodeSearchResult<K, T>> result = root.search(fuzzyCompareKey, similarity);
            this.NODESMATCHED = fuzzyCompareKey.getNODESMATCHED();
            this.NODESPUSHED = fuzzyCompareKey.getNODESPUSHED();
            this.NODESSEARCHED = fuzzyCompareKey.getNODESSEARCHED();
            return result;
        }

        return null;
    }

    /* (non-Javadoc)
     * @see de.folt.fuzzy.FuzzyDataStructure#setNODESMATCHED(int)
     */
    public void setNODESMATCHED(int nodesmatched)
    {
        NODESMATCHED = nodesmatched;
    }

    /* (non-Javadoc)
     * @see de.folt.fuzzy.FuzzyDataStructure#setNODESPUSHED(int)
     */
    public void setNODESPUSHED(int nodespushed)
    {
        NODESPUSHED = nodespushed;
    }

    /* (non-Javadoc)
     * @see de.folt.fuzzy.FuzzyDataStructure#setNODESSEARCHED(int)
     */
    public void setNODESSEARCHED(int nodessearched)
    {
        NODESSEARCHED = nodessearched;
    }
    
    /**
     * removeValue removes a value from the value list of the values of the tree based on a key. The values of the key are the objects to remove from the list
     * 
     * @param fuzzyCompareKey
     *            the key containing the value to remove
     * @return true when successfully removed, otherwise false
     */
    public boolean removeValue(FuzzyNode<K, T> fuzzyCompareKey)
    {
        return root.removeValue(fuzzyCompareKey);
    }

}
