/*
 * Created on 08.02.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.fuzzy;

import java.io.Serializable;
import java.util.Observable;
import java.util.Vector;

/**
 * This class defines the (abstract) method for a general fuzzy data structure, a class which is intended to support fuzzy = similarity search.<br>
 * It is created three types:<br>
 * K    represent a key structure<br>
 * T    represents a value object<br>
 * S    represents a Search Result (result of class SearchResult)<br>
 * Examples are:<br>
 * FuzzyNodeTree<K, T> extends FuzzyDataStructure<K, T, FuzzyNodeSearchResult<K, T>> implements Serializable<br>
 * 
 * @author klemens
 *
 */
public abstract class FuzzyDataStructure<K, T, S> extends Observable implements Serializable
{

    /**
     * 
     */
    private static final long serialVersionUID = -3787460395946459699L;

    /**
     * countNodes count all the nodes in the tree (includes the root node!)
     * @return the nodes in the tree
     */
    public abstract int countNodes();


    /**
     * format return a formated fuzzy tree based on the fuzzy nodes
     * 
     * @return formatted fuzzy tree as string
     */
    public abstract String format();

    /**
     * @return the nODESMATCHED
     */
    public abstract int getNODESMATCHED();

    /**
     * @return the nODESPUSHED
     */
    public abstract int getNODESPUSHED();

    /**
     * @return the nODESSEARCHED
     */
    public abstract int getNODESSEARCHED();

    /**
     * @return the root - the root node of the Fuzzy Tree
     */
    public abstract FuzzyDataStructureElement<K, T> getRoot();

    /**
     * insertFuzzyNode inserts a Fuzzy node into the tree; if called for the first time the root is set to the fuzzyNodeToAdd
     * 
     * @param fuzzyNodeToAdd
     *            fuzzy node to insert
     * @return true for success, false for failure
     */
    public abstract boolean insertFuzzyNode(FuzzyDataStructureElement<K, T> fuzzyNodeToAdd);

    /**
     * search searches for FuzzyNodes matching similarity
     * 
     * @param fuzzyCompareKey
     *            the fuzzy node to search for
     * @param similarity
     *            the similarity for the nodes to search
     * @return a vector of FuzzyNodeSearchResult's
     */
    public abstract Vector<S> search(FuzzyDataStructureElement<K, T> fuzzyCompareKey, int similarity);

    /**
     * @param nodesmatched the nODESMATCHED to set
     */
    public abstract void setNODESMATCHED(int nodesmatched);

    /**
     * @param nodespushed the nODESPUSHED to set
     */
    public abstract void setNODESPUSHED(int nodespushed);

    /**
     * @param nodessearched the nODESSEARCHED to set
     */
    public abstract void setNODESSEARCHED(int nodessearched);


}