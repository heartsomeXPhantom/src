/*
 * Created on 02.02.2009
 * 
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.fuzzy;

import java.util.Hashtable;
import java.util.Vector;

import de.folt.similarity.LevenshteinSimilarity;
import de.folt.util.Timer;

/**
 * This class Implements a partitioned string fuzzy node tree. For details {@see de.folt.fuzzy.FuzzyNodeTree}
 * 
 * @author klemens
 * 
 */
public class StringPartitionedFuzzyNodeTree<T> extends PartitionedFuzzyNodeTree<String, T>
{

	public static boolean isPrime(int n)
	{
		if (n < 2)
			return false;
		if (n < 4)
			return true; // 2 und 3 sind prim
		if (n % 2 == 0 || n % 3 == 0)
			return false;
		int limit = (int) Math.sqrt(n);
		for (int i = 6; i - 1 <= limit; i += 6)
		{
			if (n % (i - 1) == 0 || n % (i + 1) == 0)
				return false;
		}
		return true;
	}

	/**
     * 
     */
	private static final long	serialVersionUID	= -3145477746257042415L;

	/**
	 * main
	 * 
	 * @param args
	 *            0 = number of strings to generate (default = 5) / 1 = length of string to generate (default = 20)
	 */
	public static void main(String[] args)
	{
		int iTestNumber = 10000;
		int iLength = 60;
		int similarity = 80;
		String filename = null;
		String encoding = "UTF-8";
		int howMany = 1;

		int iformat = 0;

		Hashtable<String, String> arguments = de.folt.util.OpenTMSSupportFunctions.argumentReader(args, true);

		try
		{
			if (arguments.containsKey("-testNumber"))
				iTestNumber = Integer.parseInt(arguments.get("-testNumber"));
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		try
		{
			if (arguments.containsKey("-howMany"))
				howMany = Integer.parseInt(arguments.get("-howMany"));
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		try
		{
			if (arguments.containsKey("-length"))
				iLength = Integer.parseInt(arguments.get("-length"));
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		try
		{
			if (arguments.containsKey("-similarity"))
				similarity = Integer.parseInt(arguments.get("-similarity"));
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		try
		{
			if (arguments.containsKey("-format"))
				iformat = Integer.parseInt(arguments.get("-format"));
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		try
		{
			if (arguments.containsKey("-filename"))
				filename = arguments.get("-filename");
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		try
		{
			if (arguments.containsKey("-encoding"))
				encoding = arguments.get("-encoding");
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		de.folt.models.datamodel.Test test = new de.folt.models.datamodel.Test();
		// String[] randomExcactStrings = test.nExactStrings(iTestNumber, iLength);
		String[] randomFuzzyStrings = null;
		String[] randomStrings = null;
		if (filename != null)
		{
			Vector<String> rvec = de.folt.util.OpenTMSSupportFunctions.readFileIntoVector(filename, encoding);
			randomFuzzyStrings = rvec.toArray(new String[rvec.size()]);
			iTestNumber = rvec.size();
		}
		else
		{
			randomFuzzyStrings = test.nRandomStrings(iTestNumber, iLength);
		}
		randomStrings = randomFuzzyStrings;
		int meanLen = 0;
		for (int i = 0; i < iTestNumber; i++)
		{
			if (iformat > 0)
				System.out.println(i + ": \"" + randomStrings[i] + "\"");
			meanLen = meanLen + randomStrings[i].length();
		}

		meanLen = meanLen / iTestNumber;
		System.out.println("Mean string length: " + meanLen);
		if (meanLen % 2 == 0)
			meanLen = meanLen + 1;

		String summaryinfo = "iTestNumber" + "\t" + "iLength" + "\t" + "iKeyLen" + "\t" + "similarity" + "\t" + "countNodes" + "\t" + "iCreationTime" + "\t" + "iSearchTime" +
				"\t" + "ALLNODESINTREE" + "\t" + "NODESMATCHED"
				+ "\t" + "NODESPUSHED" + "\t" + "NODESSEARCHED\n";
		try
		{

			int m2 = meanLen / 2;

			int m4 = meanLen / 4;
			
			boolean bMeanFound = false;
			boolean bM2Found = false;
			boolean bM4Found = false;

			for (int i = meanLen; i > 0; i--)
			{
				if (isPrime(i))
				{
					if (i <= meanLen && !bMeanFound) 
					{
						meanLen = i;
						bMeanFound = true;
					}
					if (i <= m2 && !bM2Found)
					{
						m2 = i;
						bM2Found = true;
					}
					if (i <= m4 && !bM4Found)
					{
						m4 = i;
						bM4Found = true;
					}
				}
			}

			int keyLenArray[] =
			{
					m4, m2, meanLen
			};

			System.out.println("Vector length: " + m4 + " / " + m2 + " / " + meanLen);
			System.out.println("Test Number:   " + iTestNumber);

			for (int testruns = 0; testruns < 1; testruns++)
			{
				for (int jk = 0; jk < keyLenArray.length; jk++)
				{
					int iKeyLen = keyLenArray[jk];
					// System.out.println("Current vector length: " + iKeyLen);
					StringPartitionedFuzzyNodeTree<String> root = new StringPartitionedFuzzyNodeTree<String>();
					Timer timer = new Timer();
					timer.startTimer();
					// System.out.println("Start Time: " + timer.getStartTime());

					for (int i = 0; i < randomStrings.length; i++) // generate 1000 MUL Objects
					{
						String string = randomStrings[i];
						StringFuzzyNode<String> fuzzyNodeToAdd = new StringFuzzyNode<String>(string, string, iKeyLen);
						root.insertFuzzyNode(fuzzyNodeToAdd);
					}
					if (iformat > 0)
					{
						System.out.println(root.format());
					}
					System.out.println("Number of nodes: " + root.countNodes());
					System.out.println("Distribution of number of nodes: ");
					int distribution[] = root.nodesDistributenCount();
					for (int i = 0; i < distribution.length; i++) // generate 1000 MUL Objects
					{
						if (i == 0)
							System.out.print(i + "/" + distribution[i]);
						else
							System.out.print(", " + i + "/" + distribution[i]);
					}
					System.out.println();

					timer.stopTimer();
					long iCreationTime = timer.getStopTime() - timer.getStartTime();
					int NODESSEARCHED = 0;
					int NODESMATCHED = 0;
					int NODESPUSHED = 0;
					int NODESALLINTREE = 0;
					Timer timer1 = new Timer();
					timer1.startTimer();
					for (int i = 0; i < randomStrings.length; i = i + howMany)
					{
						String string = randomStrings[i];
						StringFuzzyNode<String> fuzzyNodeToSearch = new StringFuzzyNode<String>(string, string, iKeyLen);
						Vector<FuzzyNodeSearchResult<String, String>> found = root.search(fuzzyNodeToSearch, similarity);
						NODESMATCHED = NODESMATCHED + root.getNODESMATCHED();
						NODESPUSHED = NODESPUSHED + root.getNODESPUSHED();
						NODESSEARCHED = NODESSEARCHED + root.getNODESSEARCHED();
						NODESALLINTREE = NODESALLINTREE + root.getNODESALLINTREE();
						// timer.stopTimer();
						if (iformat > 0)
						{
							System.out.println("String: " + i + ": \"" + string + "\"");
							String str = "";
							if (found != null)
							{
								if (found.size() > 0)
								{
									for (int j = 0; j < found.size(); j++)
									{
										FuzzyNodeSearchResult<String, String> f = found.get(j);
										FuzzyNode<String, String> fn = f.getFuzzyNode();
										str = str + "found #" + j + " " + fn.shortFormat() + "\n";
									}
									str = str.replaceAll(string, ">>>>>" + string + "<<<<<");
									System.out.println(str);
								}
								else
									System.out.println("WARNING: \"" + string + "\" not found!");
							}
							else
							{
								System.out.println("WARNING: \"" + string + "\" not found!");
							}

						}
					}

					timer1.stopTimer();

					long iSearchTime = timer1.getStopTime() - timer1.getStartTime();

					summaryinfo = summaryinfo + iTestNumber + "\t" + iLength + "\t" + iKeyLen + "\t" + similarity + "\t" + root.countNodes() + "\t" + iCreationTime + "\t"
							+ ((float) iSearchTime / (float) randomStrings.length/howMany) + "\t" + NODESALLINTREE / randomStrings.length/howMany + "\t" + NODESMATCHED / randomStrings.length/howMany + "\t"
							+ NODESPUSHED / randomStrings.length/howMany + "\t" + NODESSEARCHED
									/ randomStrings.length/howMany + "\n";
					root = null;
				}
			}
		}
		finally
		{
			System.out.println(summaryinfo);
		}
	}

	/**
	 * formatTree
	 * 
	 * @return
	 */
	@SuppressWarnings("unused")
	private String formatTree()
	{
		return super.format();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.fuzzy.PartitionedFuzzyNodeTree#insertFuzzyNode(de.folt.fuzzy.FuzzyNode)
	 */
	@Override
	public synchronized boolean insertFuzzyNode(FuzzyNode<String, T> fuzzyNodeToAdd)
	{
		return super.insertFuzzyNode(fuzzyNodeToAdd);
	}

	/**
	 * insertFuzzyNode inserts a String Fuzzy node into a partitioned fuzzy node tree;
	 * 
	 * @param fuzzyNodeToAdd
	 *            fuzzy node to insert
	 * @return true for success, false for failure
	 */
	public synchronized boolean insertFuzzyNode(StringFuzzyNode<T> fuzzyNodeToAdd)
	{
		return super.insertFuzzyNode(fuzzyNodeToAdd);
	}

	/**
	 * removeValue removes a value from the value list of the values of the node based on a key. The values of the key are the objects to remove from the list
	 * 
	 * @param fuzzyCompareKey
	 *            the key containing the value to remove
	 * @return true when successfully removed, otherwise false
	 */
	public boolean removeValue(FuzzyNode<String, T> fuzzyCompareKey)
	{
		Vector<FuzzyNodeSearchResult<String, T>> searchresult = search(fuzzyCompareKey, 100);
		if ((searchresult != null) && (searchresult.size() > 0))
		{
			FuzzyNodeSearchResult<String, T> result = searchresult.get(0); // we expect only one result as 100% match
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.fuzzy.PartitionedFuzzyNodeTree#search(de.folt.fuzzy.FuzzyNode, int)
	 */
	@Override
	public Vector<FuzzyNodeSearchResult<String, T>> search(FuzzyNode<String, T> fuzzyCompareKey, int similarity)
	{
		return super.search(fuzzyCompareKey, similarity);
	}

	/**
	 * search returns all a FuzzyNodeSearchResult of all matching MonoLingualFuzzyNodes
	 * 
	 * @param fuzzyCompareKey
	 *            the fuzzy node to search for with similarity
	 * @param similarity
	 *            the similarity to search for
	 * @return a vector of FuzzyNodeSearchResult
	 */
	@SuppressWarnings("unchecked")
	public Vector<FuzzyNodeSearchResult<String, T>> search(MonoLingualFuzzyNode fuzzyCompareKey, int similarity)
	{
		Vector<FuzzyNodeSearchResult<String, T>> result = super.search((FuzzyNode<String, T>) fuzzyCompareKey, similarity);
		return result;
	}

	/**
	 * search
	 * 
	 * @param fuzzyCompareKey
	 *            the fuzzy node to search for with similarity
	 * @param similarity
	 *            the similarity to search for
	 * @return a vector of FuzzyNodeSearchResult
	 */
	@SuppressWarnings("unchecked")
	public Vector<FuzzyNodeSearchResult<String, T>> search(SQLMonoLingualFuzzyNode fuzzyCompareKey, int similarity)
	{
		Vector<FuzzyNodeSearchResult<String, T>> result = super.search((FuzzyNode<String, T>) fuzzyCompareKey, similarity);
		return result;
	}

	/**
	 * search returns all a FuzzyNodeSearchResult of all matching StringFuzzyNodes
	 * 
	 * @param fuzzyCompareKey
	 *            the fuzzy node to search for with similarity
	 * @param similarity
	 *            the similarity to search for
	 * @return a vector of FuzzyNodeSearchResult
	 */

	public Vector<FuzzyNodeSearchResult<String, T>> search(StringFuzzyNode<T> fuzzyCompareKey, int similarity)
	{
		Vector<FuzzyNodeSearchResult<String, T>> result = super.search(fuzzyCompareKey, similarity);
		if (result != null)
		{
			for (int j = 0; j < result.size(); j++)
			{
				FuzzyNodeSearchResult<String, T> f = result.get(j);
				FuzzyNode<String, T> fn = f.getFuzzyNode();

				Vector<T> mls = fn.getValues();
				float[] levenSimilarity = new float[mls.size()];
				for (int n = 0; n < mls.size(); n++)
				{
					if ((fn.getValues().get(n)).getClass().getName().equals("java.lang.String"))
					{
						if (((String) fn.getValues().get(n) != null) && ((String) fuzzyCompareKey.getValues().get(0) != null))
							levenSimilarity[n] = (float) LevenshteinSimilarity.levenshteinSimilarity((String) fn.getValues().get(n), (String) fuzzyCompareKey.getValues().get(0), similarity);
						else
							levenSimilarity[n] = (float) 0;
					}
				}
				f.setLevenDistance(levenSimilarity);
			}
		}
		return result;
	}

}
