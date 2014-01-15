/*
 * Created on 23.05.2010
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.similarity;

import de.folt.util.Timer;

/**
 * @author klemens
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class SiftSimilarity
{

    private static int _maxOffset = 5;

    
    public static float Distance(String s1, String s2)
    {
        if ((s1 == null) || s1.equals(""))
            return
            (s2 == null) ? 0 : s2.length();
        
        if ((s2 == null) || s2.equals(""))
            return s1.length();
        int c = 0;
        int offset1 = 0;
        int offset2 = 0;
        int lcs = 0;
        while ((c + offset1 < s1.length())
               && (c + offset2 < s2.length()))
        {
            if (s1.charAt(c + offset1) == s2.charAt(c + offset2)) lcs++;
            else
            {
                c += (offset1 + offset2)/2;
                if (c >= s1.length()) c = s1.length() - 1;
                if (c >= s2.length()) c = s2.length() - 1;

                offset1 = 0;
                offset2 = 0;
                if (s1.charAt(c) == s2.charAt(c))
                {
                    c++;
                    continue;
                }
                for (int i = 1; i < _maxOffset; i++)
                {
                    if ((c + i < s1.length())
                        && (s1.charAt(c + i) == s2.charAt(c)))
                    {
                        offset1 = i;
                        break;
                    }
                    if ((c + i < s2.length())
                        && (s1.charAt(c) == s2.charAt(c + i)))
                    {
                        offset2 = i;
                        break;
                    }
                }
            }
            c++;
        }
        return (s1.length() + s2.length())/2 - lcs;
    }

    /**
     * @return the _maxOffset
     */
    public static int get_maxOffset()
    {
        return _maxOffset;
    }

    /**
     * main 
     * @param args
     */
    public static void main(String[] args)
    {
        String sk = "Hallo here am I und wo bist du";
        String sp = "Hello hier bin ich and where bist you";

        int val = 0;
        val = LevenshteinSimilarity.levenshteinSimilarity(sk, sp, -1);
        System.out.println("LevenshteinSimilarity.levenshteinSimilarity % (-1)= " + val);
        val = siftSimilarity(sk, sp, 1);
        System.out.println("SiftSimilarity.siftSimilarity% (-1)= " + val);
        val = siftSimilarity(sk, sp, 2);
        System.out.println("SiftSimilarity.siftSimilarity% (100)= " + val);
        val = siftSimilarity(sk, sp, 3);
        System.out.println("SiftSimilarity.siftSimilarity% (80)= " + val);
        val = siftSimilarity(sk, sp, 4);
        System.out.println("SiftSimilarity.siftSimilarity% (57)= " + val);
        val = siftSimilarity(sk, sp, 5);
        System.out.println("SiftSimilarity.siftSimilarity% (55)= " + val);
        val = siftSimilarity(sk, sp, 10);
        System.out.println("SiftSimilarity.siftSimilarity% (50)= " + val);
        val = siftSimilarity(sk, sp, 20);
        System.out.println("SiftSimilarity.siftSimilarity% (40)= " + val);
        val = siftSimilarity(sk, sp, 30);
        System.out.println("SiftSimilarity.siftSimilarity% (30)= " + val);
        
        int iNumTest = 10000; 
        Timer timer = new Timer();
        timer.startTimer();
        for (int j = 0; j < iNumTest; j++)
        {
            val = siftSimilarity(sk, sp, 5);
        }
        timer.stopTimer();
        String st = timer.timerString("SiftSimilarity.siftSimilarity% = " + val + ":\n" + sk + "\n" + sp + "\n", 10000);
        System.out.println(st + "\n");
        
        timer = new Timer();
        timer.startTimer();
        for (int j = 0; j < iNumTest; j++)
        {
            val = LevenshteinSimilarity.levenshteinSimilarity(sk, sp, 30);
        }
        timer.stopTimer();
        st = timer.timerString("LevenshteinSimilarity.levenshteinSimilarity% = " + val + ":\n" + sk + "\n" + sp + "\n", 10000);
        System.out.println(st);
    }

    /**
     * @param offset the _maxOffset to set
     */
    public static void set_maxOffset(int offset)
    {
        _maxOffset = offset;
    }

    /// <summary>
    /// Calculate the similarity of two strings, as a percentage.
    /// </summary>
    /// <param name="s1"></param>
    /// <param name="s2"></param>
    /// <returns></returns>
    public static int siftSimilarity(String s1, String s2)
    {
        if (s1.equals(s2))
        	return 100;
    	float dis = Distance(s1, s2);
        float maxLen = Math.max(Math.max(s1.length(), s2.length()), dis);
        if (maxLen == 0)
            return 1;
        return 100 - (int)((dis / maxLen)*100.0);
    }
    
    public static int siftSimilarity(String s1, String s2, int offset)
    {
        if (s1.equals(s2))
        	return 100;
    	_maxOffset = offset;
        float dis = Distance(s1, s2);
        float maxLen = Math.max(Math.max(s1.length(), s2.length()), dis);
        if (maxLen == 0)
            return 1;
        return 100 - (int)((dis / maxLen)*100.0);
    }

}
