/*
 * Created on 22.01.2009
 * 
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.fuzzy;

import java.io.Serializable;
import java.util.Formatter;
import java.util.Locale;
import java.util.Stack;
import java.util.Vector;

/**
 * This class implements a {@see <a href="http://en.wikipedia.org/wiki/Kd-tree">KD-TREE</a>}.<br>
 * A fuzzy node consists of a n-dimensional fuzzy key {@see de.folt.fuzzy.FuzzyNodeKey} where each element of the n-dim vector is a number. This number is normally generated
 * based on a tri-gram index for strings, but essentially can be computed in whatever way is convenient or necessary.
 * Each node has a left and right son (which can be null if there is none). The insert algorithms ensures that the left son is always lower than the right son. It has also a vector of values associated with it which contain the real data of the node (ids, objects or whatever). The values are typed.
 * The normal usage of the class is that it is sub classed where &lt;T&gt; gets a specific instantiation. For an example see of this see {@see de.folt.fuzzy.MonoLingualFuzzyNode}
 * <p>
 * Basic fuzzy search mechanism works as follows:
 * 
 * <pre>
 * int percentDiff = 100 - similarity; // the difference percentage value
 * Vector&lt;FuzzyNodeSearchResult&lt;K, T&gt;&gt; matches = new Vector&lt;FuzzyNodeSearchResult&lt;K, T&gt;&gt;();
 * Stack&lt;FuzzyNode&lt;K, T&gt;&gt; stack = new Stack&lt;FuzzyNode&lt;K, T&gt;&gt;(); // the stack for the search
 * // use the minimum of the key sum now
 * int minkeysum = this.getFuzzyNodeKey().getKeysum();
 * // difference allowed between search node and node inspected
 * float dist = 0;
 * if (percentDiff &gt; 0)
 * {
 * 	dist = (int) ((minkeysum * percentDiff) / 100) + 2;
 * }
 * 
 * stack.push(this);
 * while (stack.size() &gt; 0)
 * {
 * 	FuzzyNode&lt;K, T&gt; currentFuzzyNode = stack.pop(); // node to inspect
 * 	int difference = currentFuzzyNode.computeKeyDistance(fuzzyCompareKey);
 * 	boolean compval = (dist &gt;= difference);
 * 	if (compval)
 * 	{
 * 		FuzzyNodeSearchResult&lt;K, T&gt; result = new FuzzyNodeSearchResult&lt;K, T&gt;(percentdiffreal, similarity, dist, difference, currentFuzzyNode, NODESSEARCHED, NODESMATCHED, NODESPUSHED);
 * 		matches.add(result);
 * 	}
 * 
 * 	int currentNodeLevelSum = currentFuzzyNode.getFuzzyNodeKey().computeKeySumTillLevel(currentFuzzyNode.LEVEL);
 * 	int compareNodeLevelSum = fuzzyCompareKey.getFuzzyNodeKey().computeKeySumTillLevel(currentFuzzyNode.LEVEL);
 * 
 * 	if (currentNodeLevelSum &gt;= (compareNodeLevelSum - dist))
 * 	{
 * 		FuzzyNode&lt;K, T&gt; entry = currentFuzzyNode.getLeftSon() != null ? currentFuzzyNode.getLeftSon() : null;
 * 		if (entry != null)
 * 		{
 * 			stack.push(entry);
 * 		}
 * 	}
 * 
 * 	if (currentNodeLevelSum &lt;= (compareNodeLevelSum + dist))
 * 	{
 * 		FuzzyNode&lt;K, T&gt; entry = currentFuzzyNode.getRightSon() != null ? currentFuzzyNode.getRightSon() : null;
 * 		if (entry != null)
 * 		{
 * 			stack.push(entry);
 * 			fuzzyCompareKey.NODESPUSHED = fuzzyCompareKey.NODESPUSHED + 1;
 * 		}
 * 	}
 * }
 * </pre>
 * 
 * Pattern used: Factory
 * 
 * @author klemens
 * 
 * 
 */
public class FuzzyNode<K, T> extends FuzzyDataStructureElement<K, T> implements Serializable
{

	public enum FUZZYNODESTATUS
	{

		ACTIVE, DELETED, NEW, UPDATED
	}

	private static int			iNodes				= 0;

	protected static int		nGram				= 3;

	private static int			recLevel			= 0;

	/**
     * 
     */
	private static final long	serialVersionUID	= 941527626468241824L;

	/**
	 * Get the NGrams associated with the FuzzyNode (for FuzzyNodes based on Strings!)
	 * 
	 * @return the nGram
	 */
	public static int getNGram()
	{
		return nGram;
	}

	boolean						bInsertmode		= true;

	protected FuzzyNodeKey		fuzzyNodeKey	= null;

	protected FuzzyNode<K, T>	leftSon			= null;

	protected int				LEVEL			= 0;

	protected long				maxID			= 0;

	protected long				nodeID			= 0;

	private int					NODESMATCHED	= 0;

	private int					NODESPUSHED		= 0;

	private int					NODESSEARCHED	= 0;

	protected FuzzyNode<K, T>	rightSon		= null;

	protected FUZZYNODESTATUS	status			= FUZZYNODESTATUS.NEW;

	protected Vector<T>			values			= null;

	/**
	 * This is the default constructor, basically does nothing. Use the set functions for adding a FuuzyNodeKey and the left and right son.
	 */
	public FuzzyNode()
	{
		super();
	}

	/**
	 * computeKeyDistance computes the distance between two fuzzy nodes based on its keys<br>
	 * For the implementation see FuzzyNodeKey
	 * 
	 * @param fuzzyNode
	 *            the fuzzy node to compare against the actual fuzzy node
	 * @return the distance
	 */
	public int computeKeyDistance(FuzzyNode<K, T> fuzzyNode)
	{
		return this.getFuzzyNodeKey().computeKeyDistance(fuzzyNode.getFuzzyNodeKey());
	}

	/**
	 * countNodes count all the values below the actual nodes (the actual node is not included!)
	 * 
	 * @return the number of all nodes left and right (0, 1 or 2)
	 */
	public int countNodes()
	{
		int iSum = 0;
		if (this.leftSon != null)
		{
			iSum++;
			iSum = iSum + this.leftSon.countNodes();
		}
		if (this.rightSon != null)
		{
			iSum++;
			iSum = iSum + this.rightSon.countNodes();
		}
		if (iSum == 0)
			return iSum;

		return iSum;
	}

	/**
	 * countSons count the number of sons
	 * 
	 * @return the number of direct sons (0, 1 or 2)
	 */

	public int countSons()
	{
		int iSum = 0;
		if (this.leftSon != null)
			iSum++;
		if (this.rightSon != null)
			iSum++;
		return iSum;
	}

	/**
	 * countValues return the number of values of a node
	 * 
	 * @return the number of values in a node
	 */
	public int countValues()
	{
		return this.values.size();
	}

	/**
	 * format formats a FuzzyNode and returns a formatted string of the fuzzy node<br>
	 * 
	 * <pre>
	 * this + &quot;:&quot; + this.nodeID + &quot;(&quot; + this.maxID + &quot;):&quot; + &quot;:&quot; + this.status + &quot; LE:&quot; + this.LEVEL + &quot; :LS(&quot; + ils + &quot; &quot; + this.leftSon + &quot;):RS(&quot; + irs + &quot; &quot; + this.rightSon + &quot;):&quot;
	 * 		+ this.fuzzyNodeKey.format()
	 * </pre>
	 * 
	 * @return formatted FuzzyNode
	 */
	public String format()
	{
		long ils = -1;
		if (this.leftSon != null)
			ils = this.leftSon.nodeID;

		long irs = -1;
		if (this.rightSon != null)
			irs = this.rightSon.nodeID;
		return this + ":" + this.nodeID + "(" + this.maxID + "):" + ":" + this.status + " LE:" + this.LEVEL + " :LS(" + ils + " " + this.leftSon + "):RS(" + irs + " " + this.rightSon + "):"
				+ this.fuzzyNodeKey.format() + format(this.values) + "\n";
	}

	private String format(Vector<T> values)
	{
		StringBuffer res = new StringBuffer();
		res.append(" #(" + values.size() + ") [");
		for (int i = 0; i < values.size(); i++)
		{
			if (i == 0)
				res.append("\"" + values.get(i).toString() + "\"");
			else
				res.append(", \"" + values.get(i).toString()  + "\"");
		}
		res.append("]");
		return res.toString();
	}

	/**
	 * formatTree formats a FuzzyNode and its children and returns a formatted string of the fuzzy nodes
	 * 
	 * @return formatted FuzzyNode and its children
	 */
	public String formatTree()
	{
		return formatTree(true);
	}

	/**
	 * formatTree formats a FuzzyNode and its children and returns a formatted string of the fuzzy nodes
	 * 
	 * @param bShortFormat
	 *            true = use a short format display for node / false = long format
	 * @return formatted FuzzyNode and its children
	 */
	public String formatTree(boolean bShortFormat)
	{
		String str = "";
		if (bShortFormat)
			str = shortFormat();
		else
			str = format();
		recLevel++;
		iNodes++;

		if ((getLeftSon() == null) && (getRightSon() == null))
		{
			recLevel--;
			return str;
		}

		if (leftSon != null)
		{
			str = str + getLeftSon().formatTree();
		}

		if (rightSon != null)
		{
			str = str + getRightSon().formatTree();
		}
		recLevel--;
		return str;
	}

	/**
	 * getDepth get the depth of the KD-TREE fuzzy node
	 * 
	 * @return the depth of the node
	 */
	public int getDepth()
	{
		int lDepth = 0;
		int rDepth = 0;

		if (this.getLeftSon() != null)
		{
			lDepth = this.getLeftSon().getDepth();
		}
		if (this.getRightSon() != null)
		{
			rDepth = getRightSon().getDepth();
		}
		if (lDepth >= rDepth)
		{
			return lDepth + 1;
		}
		else
		{
			return rDepth + 1;
		}
	}

	/**
	 * Get the FuzzyNodeKey associated with the FuzzyNode
	 * 
	 * @return the fuzzyNodeKey
	 */
	public FuzzyNodeKey getFuzzyNodeKey()
	{
		return fuzzyNodeKey;
	}

	/**
	 * Get the left son of the FuzzyNode
	 * 
	 * @return the leftSon
	 */
	public FuzzyNode<K, T> getLeftSon()
	{
		return leftSon;
	}

	/**
	 * Get the maximum number of FuzzyNodes associated with this FuzzyNode
	 * 
	 * @return the maxID
	 */
	public long getMaxID()
	{
		return maxID;
	}

	/**
	 * Get the unique identifier of the FuzzyNode
	 * 
	 * @return the nodeID
	 */
	public long getNodeID()
	{
		return nodeID;
	}

	/**
	 * @return the nODESMATCHED number of matching nodes for the search
	 */
	public int getNODESMATCHED()
	{
		return NODESMATCHED;
	}

	/**
	 * @return the nODESPUSHED number of nodes pushed for the search
	 */
	public int getNODESPUSHED()
	{
		return NODESPUSHED;
	}

	/**
	 * @return the nODESSEARCHED number of search nodes for the search
	 */
	public int getNODESSEARCHED()
	{
		return NODESSEARCHED;
	}

	/**
	 * Set the right son of the FuzzyNode
	 * 
	 * @return the rightSon
	 */
	public FuzzyNode<K, T> getRightSon()
	{
		return rightSon;
	}

	/**
	 * Get the status of the node
	 * 
	 * @return the status
	 */
	public FUZZYNODESTATUS getStatus()
	{
		return status;
	}

	/**
	 * Get the values associated with the FuzzyNode. The value basically is a vector. Values with produce the same FuzzyNodeKey are added to the vector.
	 * 
	 * @return the values
	 */
	public Vector<T> getValues()
	{
		return values;
	}

	/**
	 * iBalance determine the height depth) difference in the nodes sons
	 * 
	 * @return the difference between heigth left and right branch
	 */
	public int iBalance()
	{
		int lHeigth = 0;
		if (this.getLeftSon() != null)
			lHeigth = this.getLeftSon().getDepth();
		int rHeigth = 0;
		if (this.getRightSon() != null)
			rHeigth = this.getRightSon().getDepth();
		int iDiff = Math.abs(lHeigth - rHeigth);
		return iDiff;
	}

	/**
	 * iLeafNodes return the number of leaf nodes
	 * 
	 * @return number of leaf nodes
	 */
	int iLeafNodes()
	{
		int iL = 0;

		if ((getLeftSon() == null) && (getRightSon() == null))
		{
			return 1;
		}

		if (leftSon != null)
		{
			iL = iL + getLeftSon().iLeafNodes();
		}

		if (rightSon != null)
		{
			iL = iL + getRightSon().iLeafNodes();
		}

		return iL;
	}

	/**
	 * insertFuzzyNode inserts a new FuzzyNode in the current FuzzyNode - which is actually a tree
	 * 
	 * @param fuzzyNodeToAdd
	 *            the fuzzy node to add
	 * @return true if successfully inserted
	 */
	public synchronized boolean insertFuzzyNode(FuzzyNode<K, T> fuzzyNodeToAdd)
	{
		return insertFuzzyNode(fuzzyNodeToAdd, true);
	}

	/**
	 * insertFuzzyNode insert a Fuzzy Node at a specific position in the tree applying a KDTREE insert algorithm.
	 * 
	 * @param fuzzyNodeToAdd
	 *            the node to add to the fuzzy node
	 * @param bInsertMode
	 *            if true the entry is added even if the node exists (values added), if false the found node has a value and only one value per node is allowed
	 * @return true if successfully inserted
	 */
	public synchronized boolean insertFuzzyNode(FuzzyNode<K, T> fuzzyNodeToAdd, boolean bInsertMode)
	{
		if (fuzzyNodeToAdd == null)
		{
			return false;
		}
		int level = 0;
		this.bInsertmode = bInsertMode;
		FuzzyNode<K, T> actualnode = this;

		while (actualnode != null)
		{ // # an obvious endless loop :-)
			if (actualnode.computeKeyDistance(fuzzyNodeToAdd) == 0)
			{
				// System.out.println("Update: "+ actualnode.nodeID);
				return updateFuzzyNode(fuzzyNodeToAdd, bInsertMode); // # node (vector) exists already in tree (collision)
			}

			// left or right son? compare the value of the key element at position level
			// if smaller or equal > left son / else right son
			int currentNodeLevelSum = actualnode.getFuzzyNodeKey().computeKeySumTillLevel(level);
			int compareNodeLevelSum = fuzzyNodeToAdd.getFuzzyNodeKey().computeKeySumTillLevel(level);

			// if (actualnode.getFuzzyNodeKey().getKey()[level] <= fuzzyNodeToAdd.getFuzzyNodeKey().getKey()[level])
			if (currentNodeLevelSum <= compareNodeLevelSum)
			{
				if (actualnode.getRightSon() == null)
				{
					maxID = maxID + 1; // increment the root node
					fuzzyNodeToAdd.status = FuzzyNode.FUZZYNODESTATUS.ACTIVE;
					actualnode.setSon(true, fuzzyNodeToAdd, level, maxID);
					this.setChanged();
					this.notifyObservers(this);
					return true;
				}
				actualnode = actualnode.getRightSon();
				actualnode.maxID = maxID;
			}
			else
			{
				if (actualnode.getLeftSon() == null)
				{
					maxID = maxID + 1; // increment the root node
					fuzzyNodeToAdd.status = FuzzyNode.FUZZYNODESTATUS.ACTIVE;
					actualnode.setSon(false, fuzzyNodeToAdd, level, maxID);
					this.setChanged();
					this.notifyObservers(this);
					return true;
				}
				actualnode = actualnode.getLeftSon();
				actualnode.maxID = maxID;
			}
			level = level + 1;
			level = level % (getFuzzyNodeKey().getKey().length - 1);
			// System.out.println("level->" + level);
		}

		return false;
	}

	/**
	 * isAVLTree check if AVL tree
	 * 
	 * @return true if yes otherwise false
	 */
	public boolean isAVLTree()
	{
		int lHeigth = 0;
		if (this.getLeftSon() != null)
			lHeigth = this.getLeftSon().getDepth();
		int rHeigth = 0;
		if (this.getRightSon() != null)
			rHeigth = this.getRightSon().getDepth();
		int iDiff = Math.abs(lHeigth - rHeigth);
		if (iDiff <= 1)
			return true;
		else
			return false;
	}

	/**
	 * Get the insermode of the FuzzyNode
	 * 
	 * @return the bInsertmode
	 */
	public boolean isBInsertMode()
	{
		return bInsertmode;
	}

	/**
	 * remove remove for a fuzzy node based on the value of a node and removes this value from the value list. It searches thru all nodes to find the (Objec) value
	 * 
	 * @param value
	 * @return return all the matching FuzzyNodes in a vector
	 */
	public void remove(Object value)
	{
		this.removeValue(value);
		if (this.leftSon != null)
		{
			this.leftSon.remove(value);
		}
		if (this.rightSon != null)
		{
			this.rightSon.remove(value);
		}

		return;
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
	 * removeValue removes a value from the value list of the values of the node
	 * 
	 * @param value
	 *            the object to remove
	 * @return true when successfully removed, otherwise false
	 */
	public boolean removeValue(Object value)
	{
		boolean bRemoved = this.getValues().remove(value);
		if (bRemoved)
		{
			this.setChanged();
			this.notifyObservers(this);
		}
		return bRemoved;
	}

	/**
	 * removeValue removes a vector of values from the value list of the values of the node
	 * 
	 * @param values
	 *            the vector of object values to remove
	 * @return true when successfully removed, otherwise false
	 */
	public boolean removeValue(Vector<Object> values)
	{
		Vector<T> vecvalues = this.getValues();
		boolean bRemoved = vecvalues.removeAll(values);
		if (bRemoved)
		{
			this.setChanged();
			this.notifyObservers(this);
		}
		return bRemoved;
	}

	/**
	 * search searches FuzzyNode and its sons with a given similarity and returns a Vector of matching keys.
	 * 
	 * @param fuzzyCompareKey
	 *            the node to search for in the current node and its sons
	 * @param similarity
	 *            the similarity in % (100% = perfect match)
	 * @return a vector of FuzzyNodeSearchResult<K, T>
	 */

	public Vector<FuzzyNodeSearchResult<K, T>> search(FuzzyNode<K, T> fuzzyCompareKey, int similarity)
	{
		int percentDiff = 100 - similarity; // the difference percentage value
		fuzzyCompareKey.NODESSEARCHED = 0;
		fuzzyCompareKey.NODESMATCHED = 0;
		fuzzyCompareKey.NODESPUSHED = 0;
		Vector<FuzzyNodeSearchResult<K, T>> matches = new Vector<FuzzyNodeSearchResult<K, T>>();
		Stack<FuzzyNode<K, T>> stack = new Stack<FuzzyNode<K, T>>(); // the stack for the search
		// use the minimum of the key sum now
		// int minkeysum = Math.max(fuzzyCompareKey.getFuzzyNodeKey().KEYSUM, this.getFuzzyNodeKey().KEYSUM);
		int minkeysum = this.getFuzzyNodeKey().getKeysum();
		// difference allowed between search node and node inspected
		float dist = 0;
		if (percentDiff > 0)
		{
			dist = (int) ((minkeysum * percentDiff) / 100) + 2;
		}

		stack.push(this);
		while (stack.size() > 0)
		{
			FuzzyNode<K, T> currentFuzzyNode = stack.pop(); // node to inspect
			int difference = currentFuzzyNode.computeKeyDistance(fuzzyCompareKey);
			boolean compval = (dist >= difference);
			if (compval)
			{
				if (currentFuzzyNode.status != FuzzyNode.FUZZYNODESTATUS.DELETED)
				{
					float percentdiffreal = 100;
					if (currentFuzzyNode.getFuzzyNodeKey().getKeysum() > 0)
					{
						percentdiffreal = (int) ((float) difference / (float) currentFuzzyNode.getFuzzyNodeKey().getKeysum() * 100);
					}
					percentdiffreal = percentdiffreal / 2;
					fuzzyCompareKey.NODESMATCHED = fuzzyCompareKey.NODESMATCHED + 1;
					FuzzyNodeSearchResult<K, T> result = new FuzzyNodeSearchResult<K, T>(percentdiffreal, similarity, dist, difference, currentFuzzyNode, NODESSEARCHED, NODESMATCHED, NODESPUSHED);
					matches.add(result);
				}
			}

			// int currentNodeLevelSum = currentFuzzyNode.getFuzzyNodeKey().getKey()[LEVEL];
			// int compareNodeLevelSum = fuzzyCompareKey.getFuzzyNodeKey().getKey()[LEVEL];

			int currentNodeLevelSum = currentFuzzyNode.getFuzzyNodeKey().computeKeySumTillLevel(currentFuzzyNode.LEVEL);
			int compareNodeLevelSum = fuzzyCompareKey.getFuzzyNodeKey().computeKeySumTillLevel(currentFuzzyNode.LEVEL);

			// System.out.println("LEVEL: " + LEVEL + " CUN: " + currentNodeLevelSum + " CON: " + compareNodeLevelSum + " Dist " + dist + " " + (currentNodeLevelSum > (compareNodeLevelSum - dist)));
			if (currentNodeLevelSum >= (compareNodeLevelSum - dist))
			{
				FuzzyNode<K, T> entry = currentFuzzyNode.getLeftSon() != null ? currentFuzzyNode.getLeftSon() : null;
				if (entry != null)
				{
					stack.push(entry);
					fuzzyCompareKey.NODESPUSHED = fuzzyCompareKey.NODESPUSHED + 1;
				}
			}

			if (currentNodeLevelSum <= (compareNodeLevelSum + dist))
			{
				FuzzyNode<K, T> entry = currentFuzzyNode.getRightSon() != null ? currentFuzzyNode.getRightSon() : null;
				if (entry != null)
				{
					stack.push(entry);
					fuzzyCompareKey.NODESPUSHED = fuzzyCompareKey.NODESPUSHED + 1;
				}
			}
			fuzzyCompareKey.NODESSEARCHED = fuzzyCompareKey.NODESSEARCHED + 1;
		}

		return matches;
	}

	/**
	 * search search for a fuzzy node based on the value of a node
	 * 
	 * @param value
	 * @return return all the matching FuzzyNodes in a vector
	 */
	public Vector<FuzzyNode<K, T>> search(Object value)
	{
		Vector<FuzzyNode<K, T>> matches = new Vector<FuzzyNode<K, T>>();
		Vector<T> values = this.values;
		for (int i = 0; i < values.size(); i++)
		{
			if (values.get(i).equals(value))
				matches.add(this);
		}
		if (this.leftSon != null)
		{
			matches.addAll(this.leftSon.search(value));
		}
		if (this.rightSon != null)
		{
			matches.addAll(this.rightSon.search(value));
		}

		return matches;
	}

	/**
	 * Set the insert mode of the FuzzyNode. "true" will add a value to the existing values list, "false" will ignore the (new) value if one exists.
	 * 
	 * @param insertmode
	 *            the bInsertmode to set
	 */
	public void setBInsertmode(boolean insertmode)
	{
		bInsertmode = insertmode;
	}

	/**
	 * Set the FuzzyNodeKey associated with the FuzzyNode
	 * 
	 * @param fuzzyNodeKey
	 *            the fuzzyNodeKey to set
	 */
	public void setFuzzyNodeKey(FuzzyNodeKey fuzzyNodeKey)
	{
		this.fuzzyNodeKey = fuzzyNodeKey;
	}

	/**
	 * Set the left son of the FuzzyNode
	 * 
	 * @param leftSon
	 *            the leftSon to set
	 */
	public void setLeftSon(FuzzyNode<K, T> leftSon)
	{
		this.leftSon = leftSon;
	}

	/**
	 * Set the maximum number of nodes for a given FuzzyNode.
	 * 
	 * @param maxID
	 *            the maxID to set
	 */
	public void setMaxID(long maxID)
	{
		this.maxID = maxID;
	}

	/**
	 * Set the uneque identifier of the FuzzyNode
	 * 
	 * @param nodeID
	 *            the nodeID to set
	 */
	public void setNodeID(long nodeID)
	{
		this.nodeID = nodeID;
	}

	/**
	 * Set the left son of the FuzzyNode
	 * 
	 * @param rightSon
	 *            the rightSon to set
	 */
	public void setRightSon(FuzzyNode<K, T> rightSon)
	{
		this.rightSon = rightSon;
	}

	/**
	 * setSon sets the son of a FuzzyNode
	 * 
	 * @param bWhichSon
	 *            false = leftSon / true = rightSon
	 * @param fuzzyNodeToAdd
	 *            - the node to add to the cirrent node
	 * @param level
	 *            - the level of the node
	 * @param maxID
	 *            - the max id so far
	 */
	private void setSon(boolean bWhichSon, FuzzyNode<K, T> fuzzyNodeToAdd, int level, long maxID)
	{
		fuzzyNodeToAdd.LEVEL = (level + 1) % (getFuzzyNodeKey().getKey().length - 1);
		this.maxID = maxID; // update the maxID of the actualNode too.
		fuzzyNodeToAdd.nodeID = maxID; // a new node gets the maxID too
		fuzzyNodeToAdd.maxID = maxID;
		if (bWhichSon)
			setRightSon(fuzzyNodeToAdd);
		else
			setLeftSon(fuzzyNodeToAdd);
		// System.out.println(actualnode.nodeID + " R->" + fuzzyNodeToAdd.nodeID);
		// ok need to inform about change
		this.setChanged();
		this.notifyObservers(this);
	}

	/**
	 * Set the status of the node
	 * 
	 * @param status
	 *            the status to set
	 */
	public void setStatus(FUZZYNODESTATUS status)
	{
		this.status = status;
	}

	/**
	 * Set the values associated with the FuzzyNode. Oerwrites the current values.
	 * 
	 * @param values
	 *            the values to set
	 */
	public void setValues(Vector<T> values)
	{
		this.values = values;
	}

	/**
	 * shortformat formats a FuzzyNode and returns a formatted string of the fuzzy node in a more compact way than format()
	 * 
	 * @return formatted FuzzyNode
	 */
	public String shortFormat()
	{
		long ils = -1;
		if (this.leftSon != null)
			ils = this.leftSon.nodeID;

		long irs = -1;
		if (this.rightSon != null)
			irs = this.rightSon.nodeID;

		StringBuilder sb = new StringBuilder();
		Formatter formatter = new Formatter(sb, Locale.GERMAN);
		formatter.format("ID:%5d\tLE:%2d\tLS:%5d\tRS:%5d\t[%s]\t{%s}", this.nodeID, this.LEVEL, ils, irs, this.fuzzyNodeKey.format(), format(this.values));
		String str = formatter.toString() + "\n";
		formatter.close();
		// return this.nodeID + " LE:" + this.LEVEL + " :LS(" + ils + "):RS(" + irs + "):" + this.fuzzyNodeKey.format() + this.values + "\n";
		return str;
	}

	/**
	 * updateFuzzyNode updates an existing fuzzy node with the value of the fuzzyNodeToAdd.
	 * 
	 * @param fuzzyNodeToAdd
	 *            the fuzzy node from which the value should be added
	 * @param bInsertMode
	 *            if true the entry is added even if the node exists (values added), if false the found node has a value and only one value per node is allowed
	 * @return true if added otherwise false
	 */
	public synchronized boolean updateFuzzyNode(FuzzyNode<K, T> fuzzyNodeToAdd, boolean bInsertMode)
	{
		if (!this.bInsertmode)
			return false; // # if we do not use the insert mode we return with false - indicating error or that the node exists

		// add the data info if not contained
		if (this.values.containsAll(fuzzyNodeToAdd.values))
		{
			return false; // # key exists then return not added
		}

		this.values.addAll(fuzzyNodeToAdd.values);
		this.status = FUZZYNODESTATUS.UPDATED;
		this.setChanged();
		this.notifyObservers(this);
		return true;
	}
}
