package de.folt.similarity;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * TriGram Similarityx Implementtation
 * Author: Klemens Waldhör, 2013
 * 
 * http://bix.ucsd.edu/bioalgorithms/downloads/code/LCS.java
 * 
 */
public class TrigramSimilarity
{

	private static int compare(Hashtable<String, Integer> sHash, Hashtable<String, Integer> tHash)
	{
		Enumeration<String> en = sHash.keys();
		int iIdeTri = 0;
		while (en.hasMoreElements())
		{
			String tri = en.nextElement();
			if (tHash.containsKey(tri))
			{
				iIdeTri = iIdeTri + Math.min(sHash.get(tri), tHash.get(tri));
			}
		}
		return iIdeTri;
	}

	private static Hashtable<String, Integer> fillTable(Hashtable<String, Integer> sHash, String a)
	{
		for (int i = 0; i < a.length() - 3; i++)
		{
			String tri = a.substring(i, i + 3);
			if (sHash.containsKey(tri))
				sHash.put(tri, sHash.get(tri) + 1);
			else
				sHash.put(tri, 1);
		}
		return sHash;
	}

	public static void main(String args[])
	{
		try
		{
			// String s = LCSAlgorithm(args[0], args[1]);
			System.out.println(TrigramSim("Das ist mein Haus", "Das ist mein Haus").getSimilarity());
			System.out.println(TrigramSim("Das ist ein Haus", "Das ist mein Haus").getSimilarity());
			System.out.println(TrigramSim("Das ist dein Haus", "Das ist nicht mein Haus").getSimilarity());
			System.out.println(TrigramSim("Dort ist ein Haus", "Das ist mein Haus").getSimilarity());
			System.out.println(TrigramSim("", "Das ist mein Haus").getSimilarity());
			System.out.println(TrigramSim("", "").getSimilarity());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static MatchSimilarity TrigramSim(String a, String b)
	{
		float similarity = 0;
		if ((a.length() < 3) || b.length() < 3)
		{
			if (a.equals(b))
			{
				similarity = 100;
			}
		}
		else
		{
			Hashtable<String, Integer> sHash = new Hashtable<String, Integer>();
			Hashtable<String, Integer> tHash = new Hashtable<String, Integer>();

			fillTable(sHash, a);
			fillTable(tHash, b);

			try
			{
				similarity = compare(sHash, tHash);
				float la = (float)a.length();
				float lb = (float)b.length();
				similarity = (float)similarity * 2 / (la + lb - 6) * 100; // Math.min(la, lb)/Math.max(la, lb);
			}
			catch (Exception e)
			{
				similarity = 0;
			}
		}

		MatchSimilarity match = new MatchSimilarity(a, (int)similarity);

		return match;
	}
}
