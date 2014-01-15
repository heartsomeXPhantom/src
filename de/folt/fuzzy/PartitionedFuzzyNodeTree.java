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

/**
 * This class implements a partitioned fuzzy node tree; the fuzzy node trees are partitioned into buckets where each bucket corresponds to the KEYSUm of the fuzzy node root. * The rationality behind
 * this tree is to split up a fuzzy node into a tree consisting of several fuzzy node roots based on the key sum of the fuzzy node. * This helps to restrict the number of fuzzy nodes searched to a
 * boundary around the key sum of a search fuzzy key. This may not be an appropriate strategy for all types of fuzzy searches!<br>
 * For each KEYSUM a root node is generated. The search is then done on the root nodes LowerLimit < KEYSUM SearchNODE < UpperLimit, where the limits are computed based on the similarity supplied. int
 * iDiff (the limit difference for upper and lower) = (iNGram * (100 - similarity) / 100))
 * 
 * <pre>
 *  Structure:
 *  [0] [1] [2]           ....        [i] .... [n] (a hash table)
 *   |                                 |
 *   references the root fuzzy node    references the root fuzzy node 
 *   with key sum 0                    with key sum i
 * </pre>
 * 
 * Pattern used: Composite
 * 
 * @author klemens
 * 
 */
public class PartitionedFuzzyNodeTree<K, T> extends Observable implements Serializable
{

	/**
     * 
     */
	private static final long						serialVersionUID	= -3145477746257042415L;

	private Hashtable<String, FuzzyNodeTree<K, T>>	fuzzyNodeTrees		= new Hashtable<String, FuzzyNodeTree<K, T>>();

	private int										iMaxIndex			= 0;

	private int										NODESMATCHED		= 0;

	private int										NODESPUSHED			= 0;

	private int										NODESSEARCHED		= 0;

	private int										NODESALLINTREE		= 0;

	/**
	 * countNodes count all the nodes in the tree
	 * 
	 * @return the nodes in the tree
	 */
	public int countNodes()
	{
		Enumeration<String> iter = fuzzyNodeTrees.keys();
		int iSum = 0;
		while (iter.hasMoreElements())
		{
			String key = iter.nextElement();
			FuzzyNodeTree<K, T> f = fuzzyNodeTrees.get(key);
			iSum = iSum + f.countNodes();
		}

		return iSum;
	}

	public int[] nodesDistributenCount()
	{
		Enumeration<String> iter = fuzzyNodeTrees.keys();
		int iSum[] = new int[fuzzyNodeTrees.size()];
		while (iter.hasMoreElements())
		{
			String key = iter.nextElement();
			int iNum = Integer.parseInt(key);
			if (iNum >= iSum.length)
				continue;
			FuzzyNodeTree<K, T> f = fuzzyNodeTrees.get(key);
			iSum[iNum] = iSum[iNum] + f.countNodes();
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
		String str = "Partionened Fuzzy Node Tree: " + this + "\n";
		for (int i = 0; i < iMaxIndex + 1; i++)
		{
			if (fuzzyNodeTrees.containsKey(i + ""))
			{
				str = str + fuzzyNodeTrees.get(i + "").format();
			}
		}

		return str;
	}

	/**
	 * getRoot returns a specific fuzzy Node tree
	 * 
	 * @param i
	 *            the index of the FuzzyTree to return
	 * @return the ith fuzzy node tree corresponding to the KEYSUM of the fuzzy node of this tree
	 */
	public FuzzyNodeTree<K, T> getFuzzyNodeTree(int i)
	{
		return fuzzyNodeTrees.get(i + "");
	}

	/**
	 * @return the iMaxIndex -highest KEYSUm currently in the partitioned Fuzzy Node Tree
	 */
	public int getIMaxIndex()
	{
		return iMaxIndex;
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
	 * @return the roots
	 */
	public Hashtable<String, FuzzyNodeTree<K, T>> getRoots()
	{
		return fuzzyNodeTrees;
	}

	/**
	 * insertFuzzyNode inserts a Fuzzy node into a partitioned fuzzy node tree;
	 * 
	 * @param fuzzyNodeToAdd
	 *            fuzzy node to insert
	 * @return true for success, false for failure
	 */
	public synchronized boolean insertFuzzyNode(FuzzyNode<K, T> fuzzyNodeToAdd)
	{
		// get the root node for the node
		int iNGram = fuzzyNodeToAdd.getFuzzyNodeKey().getKeysum();

		if (fuzzyNodeTrees.containsKey(iNGram + ""))
		{
			return fuzzyNodeTrees.get(iNGram + "").insertFuzzyNode(fuzzyNodeToAdd);
		}
		else
		{
			FuzzyNodeTree<K, T> fnt = new FuzzyNodeTree<K, T>();
			fuzzyNodeTrees.put(iNGram + "", fnt);
			if (iMaxIndex < iNGram)
				iMaxIndex = iNGram;
			return fuzzyNodeTrees.get(iNGram + "").insertFuzzyNode(fuzzyNodeToAdd);
		}
	}

	/**
	 * removeValue removes a value from the value list of the values of the node based on a key. The values of the key are the objects to remove from the list
	 * 
	 * @param fuzzyCompareKey
	 *            the key containing the value to remove
	 * @return true when successfully removed, otherwise false
	 */
	public boolean removeValue(FuzzyNode<K, T> fuzzyCompareKey)
	{
		Vector<FuzzyNodeSearchResult<K, T>> searchresult = search(fuzzyCompareKey, 100);
		if ((searchresult != null) && (searchresult.size() > 0))
		{
			FuzzyNodeSearchResult<K, T> result = searchresult.get(0); // we expect only one result as 100% match
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
	public Vector<FuzzyNodeSearchResult<K, T>> search(FuzzyNode<K, T> fuzzyCompareKey, int similarity)
	{
		// get the root node for the node
		int iNGram = fuzzyCompareKey.getFuzzyNodeKey().getKeysum();
		// compute the similarity amount low and high boundaries
		int iLowIndex = 0;
		int iHighIndex = 0;

		int iDiff = (int) ((float) iNGram * ((float) (((float) 100 - (float) similarity) / (float) 100)));
		iLowIndex = iNGram - iDiff;
		if (iLowIndex < 0)
			iLowIndex = 0;
		iHighIndex = iNGram + iDiff;
		if (iHighIndex > iMaxIndex)
			iHighIndex = iMaxIndex;

		this.NODESALLINTREE = 0;
		this.NODESPUSHED = 0;
		this.NODESSEARCHED = 0;
		this.NODESMATCHED = 0;

		Vector<FuzzyNodeSearchResult<K, T>> allresults = new Vector<FuzzyNodeSearchResult<K, T>>();

		for (int i = iLowIndex; i < iHighIndex + 1; i++)
		{
			if (fuzzyNodeTrees.containsKey(i + ""))
			{
				this.NODESALLINTREE = this.NODESALLINTREE + fuzzyNodeTrees.get(i + "").countNodes();
				Vector<FuzzyNodeSearchResult<K, T>> fresult = fuzzyNodeTrees.get(i + "").search(fuzzyCompareKey, similarity);
				this.NODESMATCHED = this.NODESMATCHED + fuzzyCompareKey.getNODESMATCHED();
				this.NODESPUSHED = this.NODESPUSHED + fuzzyCompareKey.getNODESPUSHED();
				this.NODESSEARCHED = this.NODESSEARCHED + fuzzyCompareKey.getNODESSEARCHED();
				if ((fresult != null) && (fresult.size() > 0))
					allresults.addAll(fresult);
			}
		}

		return allresults;
	}

	public int getNODESALLINTREE()
	{
		return NODESALLINTREE;
	}

	public void setNODESALLINTREE(int nODESALLINTREE)
	{
		NODESALLINTREE = nODESALLINTREE;
	}

}
