package de.folt.similarity;

import java.util.Hashtable;
import java.util.regex.Pattern;

import de.folt.util.Timer;

/**
 * Class computes the Levenshtein distance and similarity. The main function to be used is<br>
 * int levenshteinSimilarity(String sKey, String sPattern).<br>
 * It returns a % value where 100 (%) means identical strings.<br>
 * Code is partially based on {@see <a href="http://www.merriampark.com/ld.htm">merriampark</a>}.
 * 
 * @author Klemens Waldhör
 * 
 */
public class LevenshteinSimilarity
{

    /**
     * bCharValueDifference returns true if the character sum difference between the two strings is > then percent given. A very simple comparision function.
     * 
     * @param source
     *            string 1
     * @param match
     *            string 2
     * @param percent
     *            the difference between the two strings
     * @return true if match, false otherwise
     */
    public static boolean bCharValueDifference(String source, String match, int percent)
    {
        boolean bResult = false;

        int iSourceSum = 0;
        if (source == null)
            return false;
        if (match == null)
            return false;
        if (source.length() == 0)
            return false;
        if (match.length() == 0)
            return false;

        for (int i = 0; i < source.length(); i++)
            iSourceSum = iSourceSum + (int) source.charAt(i);
        float fSourceMean = iSourceSum / source.length();

        int iMatchSum = 0;
        for (int i = 0; i < match.length(); i++)
            iMatchSum = iMatchSum + (int) match.charAt(i);
        float fMatchMean = iMatchSum / match.length();

        float fDiff = Math.abs(fSourceMean - fMatchMean);
        float fAllowedDiff = fSourceMean * (100 - percent) / 100;

        if (fDiff <= fAllowedDiff)
            return true;

        return bResult;
    }

    /**
     * getLevenshteinDistance computes the Levenshtein distance. The value as such does not state much, basically the edit distance between the two strings; it is suggested to use
     * levenshteinSimilarity instead as this method returns a % value.
     * 
     * @param string1
     *            String 1
     * @param string2
     *            String
     * @return the Levenshtein distance 0 = identical strings
     * 
     */
    public static int getLevenshteinDistance(String string1, String string2)
    {
        return getLevenshteinDistance(string1, string2, -1);
    }

    /**
     * getLevenshteinDistance computes the Levenshtein distance. The value as such does not state much, basically the edit distance between the two strings; it is suggested to use
     * levenshteinSimilarity instead as this method returns a % value.
     * 
     * @param string1
     *            String 1
     * @param string2
     *            String
     * @param minPercent
     *            minimul percentage to be used 100% = Strings have to be identical, -1 ignore this parameter
     * @return the Levenshtein distance 0 = identical strings
     */
    public static int getLevenshteinDistance(String string1, String string2, int minPercent)
    {
        if (string1 == null || string2 == null)
        {
            throw new IllegalArgumentException("Strings must not be null");
        }

        /*
         * The difference between this impl. and the previous is that, rather than creating and retaining a matrix of size s.length()+1 by t.length()+1, we maintain two single-dimensional arrays of
         * length s.length()+1. The first, d, is the 'current working' distance array that maintains the newest distance cost counts as we iterate through the characters of String s. Each time we
         * increment the index of String t we are comparing, d is copied to p, the second int[]. Doing so allows us to retain the previous cost counts as required by the algorithm (taking the minimum
         * of the cost count to the left, up one, and diagonally up and to the left of the current cost count being calculated). (Note that the arrays aren't really copied anymore, just
         * switched...this is clearly much better than cloning an array or doing a System.arraycopy() each time through the outer loop.)
         * 
         * Effectively, the difference between the two implementations is this one does not cause an out of memory condition when calculating the LD over two very large strings.
         */

        int n = string1.length(); // length of s
        int m = string2.length(); // length of t

        char[] chararr1 = string1.toCharArray();
        char[] chararr2 = string2.toCharArray();

        int maxlddiff = (100 - minPercent) * Math.max(n, m) / 100 + 2;

        if (n == 0)
        {
            return m;
        }
        else if (m == 0)
        {
            return n;
        }

        int p[] = new int[n + 1]; // 'previous' cost array, horizontally
        int d[] = new int[n + 1]; // cost array, horizontally
        int _d[]; // placeholder to assist in swapping p and d

        // indexes into strings s and t
        int i; // iterates through s
        int j; // iterates through t

        char t_j; // jth character of t

        int cost; // cost

        int best = 0;

        for (i = 0; i <= n; i++)
        {
            p[i] = i;
        }

        int a;
        int b;

        for (j = 1; j <= m; j++)
        {
            t_j = chararr2[j - 1];
            // t_j = string2.charAt(j - 1);
            d[0] = j;
            best = maxlddiff;
            for (i = 1; i <= n; i++)
            {
                cost = chararr1[i - 1] == t_j ? 0 : 1;
                // cost = string1.charAt(i - 1) == t_j ? 0 : 1;
                // minimum of cell to the left+1, to the top+1, diagonally left and up +cost
                // minVal = (a < b; a ? b);
                // ternary operators better?
                a = d[i - 1] + 1;
                b = p[i] + 1;
                a = (a < b ? a : b);
                // a = Math.min(d[i - 1] + 1, p[i] + 1);
                b = p[i - 1] + cost;
                d[i] = (a < b ? a : b);
                // d[i] = Math.min(Math.min(d[i - 1] + 1, p[i] + 1), p[i - 1] + cost);
                if ((minPercent > -1) && (d[i] < best))
                    best = d[i];
            }

            if ((minPercent > -1) && (best >= maxlddiff))
            {
                chararr1 = null;
                chararr2 = null;
                return -1;
            }
            // copy current distance counts to 'previous row' distance counts
            _d = p;
            p = d;
            d = _d;
        }

        // our last action in the above loop was to switch d and p, so p now
        // actually has the most recent cost counts
        chararr1 = null;
        chararr2 = null;
        return p[n];
    }

    /**
     * levenSimilarity computes the Levenshtein similarity of two strings
     * 
     * @param compareString1
     *            String 1
     * @param compareString2
     *            String 2
     * @return the Levenshtein similarity (100 = exact match) in %
     */
    public static int levenshteinSimilarity(String compareString1, String compareString2)
    {
        return levenshteinSimilarity(compareString1, compareString2, -1);
    }

    /**
     * levenSimilarity computes the Levenshtein similarity of two strings The similarity in % is computed by using:
     * 
     * <pre>
     * percent = 100 - (dlw * 100) / maxlwlm;
     * </pre>
     * 
     * where dlw is Levenshtein edit distance and maxlwlm the maximum of the length of the two strings
     * 
     * @param compareString1
     *            String 1
     * @param compareString2
     *            String 2
     * @param minPercent
     *            the minimum percentage to be used; can be used to optimize the similarity computations
     * @return the Levenshtein similarity (100 = exact match) in %
     */
    public static int levenshteinSimilarity(String compareString1, String compareString2, int minPercent)
    {
        // return the % value now ...
        int dlw = getLevenshteinDistance(compareString1, compareString2, minPercent);
        if ((minPercent > -1) && (dlw == -1))
            return 0;
        int maxlwlm = Math.max(compareString1.length(), compareString2.length());
        int percent = 100;
        if (maxlwlm > 0)
        {
            // percent = 100 - (int) ((float) dlw / (float) maxlwlm * 100);
            percent = 100 - (dlw * 100) / maxlwlm;
            // (percent-100)*mxlwlm = - dlw*100 / (percent-100)*mxlwlm/100 = - dlw / dlw = (100-percent)*mxlwlm/100
            if (percent >= 100) // we need to check if the 100% is not a rounding artefact!
            {
                percent = 100;
                if (!compareString1.equals(compareString2))
                    percent = percent - 1;

            }
            else if (percent < 0)
            {
                percent = 0;
            }
        }

        return percent;
    }
    
    /**
     * levenshteinWordBasedSimilarity computes the Levenshtein similarity of two strings on a word basis. 
     * 
     * @param compareString1
     *            String 1
     * @param compareString2
     *            String 2
     * @param minPercent
     *            the minimum percentage to be used; can be used to optimize the similarity computations
     * @return the Levenshtein similarity (100 = exact match) in %
     */
    public static int levenshteinWordBasedSimilarity(String compareString1, String compareString2, int minPercent)
    {
    	if (compareString1 == null)
    		return 0;
    	if (compareString2 == null)
    		return 0;
    	compareString1 = compareString1.trim();
    	compareString2 = compareString2.trim();
    	if (compareString1.equals(""))
    		return 0;
    	if (compareString2.equals(""))
    		return 0;
    	
    	String splitchars = "[\\s+" + Pattern.quote(";!\\.-()]+*[]{}") + "]+";
		String[] compareString1Tokens = compareString1.split(splitchars);
		String[] compareString2Tokens = compareString2.split(splitchars);
		Hashtable<String, String> wordtable = new Hashtable<String, String>();
		String cs1 = "";
		String cs2 = "";
		char startChar = 'a';
		for (int i = 0; i < compareString1Tokens.length; i++)
		{
			if (wordtable.containsKey(compareString1Tokens[i]))
			{
				cs1 = cs1 + wordtable.get(compareString1Tokens[i]);
			}
			else
			{
				cs1 = cs1 + startChar;
				wordtable.put(compareString1Tokens[i], startChar+"");
				startChar++;
			}
		}
		
		for (int i = 0; i < compareString2Tokens.length; i++)
		{
			if (wordtable.containsKey(compareString2Tokens[i]))
			{
				cs2 = cs2 + wordtable.get(compareString2Tokens[i]);
			}
			else
			{
				cs2 = cs2 + startChar;
				wordtable.put(compareString2Tokens[i], startChar+"");
				startChar++;
			}
		}
		
        return levenshteinSimilarity(cs1, cs2, minPercent);
    }

    // implementation taken from http://www.merriampark.com/ldjava.htm

    /**
     * Function LevenTest Description test function Parameter Type Comment Returns print test Annotation:
     */
    public static void main(String[] args)
    {
        String sk = "Hallo here am I und wo bist du";
        String sp = "Hello hier bin ich and where bist you";

        int val = 0;
        
        
        if (args.length > 1)
        {
            System.out.print("\t");
        	for (int i = 0; i < args.length; i++)
            {
            	System.out.print(args[i] + "\t");
            }
            System.out.print("\n");
        	for (int i = 0; i < args.length; i++)
            {
            	System.out.print(args[i] + "\t");
            	for (int j = 0; j < args.length; j++)
                {
                	val = levenshteinSimilarity(args[i], args[j], 30);
                	System.out.print(val + "\t");
                }
                System.out.print("\n");
            }

            return;
        }

        {
            val = levenshteinSimilarity(sk, sp, -1);
            System.out.println("% (-1)= " + val);
            val = levenshteinSimilarity(sk, sp, 100);
            System.out.println("% (100)= " + val);
            val = levenshteinSimilarity(sk, sp, 80);
            System.out.println("% (80)= " + val);
            val = levenshteinSimilarity(sk, sp, 57);
            System.out.println("% (57)= " + val);
            val = levenshteinSimilarity(sk, sp, 55);
            System.out.println("% (55)= " + val);
            val = levenshteinSimilarity(sk, sp, 50);
            System.out.println("% (50)= " + val);
            val = levenshteinSimilarity(sk, sp, 40);
            System.out.println("% (40)= " + val);
            val = levenshteinSimilarity(sk, sp, 30);
            System.out.println("% (30)= " + val);
        }

        Timer timer = new Timer();
        timer.startTimer();
        for (int j = 0; j < 10000; j++)
        {
            val = levenshteinSimilarity(sk, sp, 30);
        }
        timer.stopTimer();
        String st = timer.timerString("LevenshteinSimilarity.levenshteinSimilarity% = " + val + ":\n" + sk + "\n" + sp + "\n", 10000);
        System.out.println(st);
    }

}