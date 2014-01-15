package de.folt.similarity;

public class Test
{

	/**
	 * @param args
	 */

	public static void main(String args[])
	{
		try
		{
			// String s = LCSAlgorithm(args[0], args[1]);
			System.out.println("Leven\tLCS\tTrigram\tSift");
			System.out.println(runTest("Das ist ein Haus", "Das ist mein Haus"));
			System.out.println(runTest("Das ist ein Haus", "ist mein Das Haus"));
			System.out.println(runTest("Das ist dein Haus", "Das ist nicht mein Haus"));
			System.out.println(runTest("Dort ist ein Haus", "Das ist mein Haus"));
			System.out.println(runTest("", "Das ist mein Haus"));
			System.out.println(runTest("", ""));

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private static String runTest(String a, String b)
	{
		return LevenshteinSimilarity.levenshteinSimilarity(a, b, 30) + "\t" + LCS.LCSAlgorithm(a, b).getSimilarity() + "\t" + TrigramSimilarity.TrigramSim(a, b).getSimilarity()
				+ "\t" + SiftSimilarity.siftSimilarity(a, b, 5) + "";
	}
}
