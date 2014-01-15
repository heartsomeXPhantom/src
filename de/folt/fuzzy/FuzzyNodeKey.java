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
import java.util.Observable;

/**
 * Basically a FuzzyNodeKey is an array of shorts of length maxFuzzyKeyLength. This array represents a point in an n-dimensional space (dimension = maxFuzzyKeyLength {@see
 * de.folt.fuzzy.FuzzyNodeKey#setMaxFuzzyKeyLength(int)}).<br>
 * The key is used by the FuzzyNode search algorithm search (and addFuzzyNode) to search {@see de.folt.fuzzy.FuzzyNode#search(FuzzyNode, int)} resp. insert FuzzyNode {@see
 * de.folt.fuzzy.FuzzyNode#insertFuzzyNode(FuzzyNode)} (for more details: {@see de.folt.fuzzy.FuzzyNode}).<br>
 * A specific method is available for generating a key from a String {@see de.folt.fuzzy.FuzzyNodeKey#FuzzyNodeKey(String)} . FuzzyNodeKey(String string) and its versions {@see
 * de.folt.fuzzy.FuzzyNodeKey#FuzzyNodeKey(String, int)} and {@see de.folt.fuzzy.FuzzyNodeKey#FuzzyNodeKey(String, int, int)} generate a FuzzyNodeKey based on n grams.
 * 
 * @author klemens
 * 
 */
public class FuzzyNodeKey extends Observable implements Serializable
{

    /**
     * this is the default of the key generating for strings (currently assuming 2 byte Unicode characters = 256*256)
     */
    private static long defaultFuzzyBaseCharNumber = 256 * 256;

    private static int defaultFuzzyKeyLength = 48;

    /**
     * 
     */
    private static final long serialVersionUID = 1464904734721882468L;

    /**
     * @return the defaultFuzzyBaseCharNumber
     */
    public static long getDefaultFuzzyBaseCharNumber()
    {
        return defaultFuzzyBaseCharNumber;
    }

    /**
     * @return the defaultFuzzyKeyLength
     */
    public static int getDefaultFuzzyKeyLength()
    {
        return defaultFuzzyKeyLength;
    }

    /**
     * @param defaultFuzzyBaseCharNumber
     *            the defaultFuzzyBaseCharNumber to set
     */
    public static void setDefaultFuzzyBaseCharNumber(long defaultFuzzyBaseCharNumber)
    {
        FuzzyNodeKey.defaultFuzzyBaseCharNumber = defaultFuzzyBaseCharNumber;
    }

    /**
     * @param defaultFuzzyKeyLength
     *            the defaultFuzzyKeyLength to set
     */
    public static void setDefaultFuzzyKeyLength(int defaultFuzzyKeyLength)
    {
        FuzzyNodeKey.defaultFuzzyKeyLength = defaultFuzzyKeyLength;
    }

    /**
     * this is the character number of the key generating for strings (currently assuming 2 byte Unicode characters)
     */
    private long fuzzyBaseCharNumber = defaultFuzzyBaseCharNumber; // this defines the size of the character base supported

    /**
     * This is the default length of the fuzzy key (currently 48)
     */
    private int fuzzyKeyLength = 48;

    private short key[] = null; // this actually represents an n-dimensional array where the elements of the key is a counter how often current element was hit (increased)

    private int keysum = 0; // this is the sum of all key elements

    int nGrams = 0; // this keeps the nGrams used for computing the key elements for strings

    /**
     * Generate a FuzzyNodeKey based on a short array. The array size must be equal to FuzzyKeyLength! The method computes the key sum using @see {@link de.folt.fuzzy.FuzzyNodeKey#computeKeySum()}
     * 
     * @param key
     *            the key for the fuzzy node key to generate the array representing a key (n-dimensional space)
     */
    public FuzzyNodeKey(short[] key)
    {
        super();
        this.key = key;
        this.computeKeySum();
    }

    /**
     * FuzzyNodeKey generates a FuzzyNodeKey for a string. It uses Trigrams (nGram = 3) and the default key length (maxFuzzyKeyLength) for key generation.
     * 
     * @param key
     *            the string from which the key should be generated (using 3 for nGram and maxFuzzyKeyLength) this is a short array
     */
    public FuzzyNodeKey(String string)
    {
        this(string, 3, defaultFuzzyKeyLength);
    }

    /**
     * FuzzyNodeKey generates a FuzzyNodeKey for a string. It uses the default key length (maxFuzzyKeyLength) for key generation.
     * 
     * @param string
     *            the string from which the key should be generated - based on nGrams
     * @param nGram
     *            nGrams the length of an nGram
     */
    public FuzzyNodeKey(String string, int nGram)
    {
        this(string, nGram, defaultFuzzyKeyLength);
    }

    /**
     * FuzzyNodeKey generates a FuzzyNodeKey for a string. An ngram of a string looks like that (shown for 3 = TriGram)<br>
     * Hallo = [Hal, all, llo] The position of the ngram in the key is computed like that:<br>
     * 
     * <pre>
     * for (i = 0; i &lt; k - nGrams + 1; i++)
     * { // loop over all chars until the last n
     *     lNum = 0;
     *     for (j = 0; j &lt; nGrams; j++)
     *     {
     *         // sum up the n gram values
     *         // if we have n chars to sum up
     *         // Example: FUZZYBASE_CHARNUM = 10, 3Grams
     *         // Value of the trigram &quot;abc&quot; = a*10*10 + b*10 + c = 61*100 + 62*10 + 63 = 6783
     *         lNum = lNum * (long) FUZZYBASE_CHARNUM + (long) string.charAt(i + j);
     *     }
     *     // Position for Example: maxfuzzy = 48: 6783%48 = 15
     *     lNum = lNum % ((long) (maxfuzzy - 1)); // position of the nGram in the key - increment +1
     *     // Example: we increment now element 15 of key +1
     *     key[(int) lNum]++;
     * }
     * </pre>
     * 
     * The method computes the key sum using @see {@link de.folt.fuzzy.FuzzyNodeKey#computeKeySum()}
     * 
     * @param string
     *            the string from which the key should be generated - based on nGrams
     * @param nGrams
     *            the length of an nGram
     * @param maxfuzzy
     *            the length of the fuzzy key to generate
     */
    public FuzzyNodeKey(String string, int nGrams, int maxfuzzy)
    {
        super();
        int i, j;
        long lNum = 0;

        int k = string.length();
        key = new short[maxfuzzy];
        for (int z = 0; z < maxfuzzy; z++)
        {
            key[z] = 0;
        }

        this.nGrams = nGrams;
        int sum = k - nGrams + 1; // how many ngrams can be made ?
        if (sum < 0)
        {
            sum = 0;
            nGrams = k;
        }

        for (i = 0; i < k - nGrams + 1; i++)
        { // loop over all chars until the last n
            lNum = 0;
            for (j = 0; j < nGrams; j++)
            {
                // sum up the n gram values
                // if we have n chars to sum up
                // Example: FUZZYBASE_CHARNUM = 10, 3Grams
                // Value of the trigram "abc" = a*10*10 + b*10 + c = 61*100 + 62*10 + 63 = 6783
                lNum = lNum * (long) fuzzyBaseCharNumber + (long) string.charAt(i + j);
            }
            // Position for Example: maxfuzzy = 48: 6783%48 = 15
            lNum = lNum % ((long) maxfuzzy); // position of the nGram in the key - increment +1
            // Example: we increment now element 15 of key +1
            key[(int) lNum]++;
        }

        keysum = this.computeKeySum();
    }

    /**
     * computeKeyDistance computes the distance between the two fuzzy node keys.<br>
     * The difference between the two keys is computed and the final sum divided by two.<br>
     * Algorithm:<br>
     * 
     * <pre>
     * distance = distance + Math.abs((int)fuzzyNodeKey.key[i] - (int)this.key[i]);&lt;br&gt;
     * distance = distance / 2;
     * </pre>
     * 
     * @param fuzzyNodeKey
     * @return the distance between the two keys; a positive number
     */
    public int computeKeyDistance(FuzzyNodeKey fuzzyNodeKey)
    {
        int distance = 0;
        int diff = 0;
        for (int i = 0; i < this.key.length; i++)
        {
            diff = ((int) fuzzyNodeKey.key[i] - (int) this.key[i]);
            // optimise a little bit...
            if (diff < 0)
                distance = distance - diff;
            else
                distance = distance + diff;
            // distance = distance + Math.abs((int) fuzzyNodeKey.key[i] - (int) this.key[i]);
        }

        distance = distance / 2;
        return distance;
    }

    /**
     * computeKeySum computes the sum of the key elements (counters for a specific ngram)
     * 
     * <pre>
     * for (int i = 0; i &lt; this.key.length; i++)
     * {
     *     isum += (int) key[i];
     * }
     * </pre>
     * 
     * @return the key sum of the fuzzy key
     */
    public int computeKeySum()
    {
        int isum = 0;
        for (int i = 0; i < this.key.length; i++)
        {
            isum += (int) key[i];
        }
        return isum;
    }

    /**
     * computeKeySumTillLevel computes the keysum till the level of the key node
     * 
     * @param level
     *            the level for the node
     * @return the summed up key value till level
     */
    public int computeKeySumTillLevel(int level)
    {
        int iSum = 0;
        for (int i = 0; i < level + 1; i++)
        {
            iSum = iSum + key[i];
        }
        return iSum;
    }

    /**
     * format produces a string which represents the key elements in a string separated by ","<br>
     * Example:
     * 
     * <pre>
     * 4, [3,0,1,5]
     * </pre>
     * 
     * @return a formated version of the key; key elements are separated by ","
     */
    public String format()
    {
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb, Locale.GERMAN);
        formatter.format("%5d", this.keysum);
        String str1 = formatter.toString();
        formatter.close();

        String str = "";
        for (int i = 0; i < this.key.length; i++)
        {
            sb = new StringBuilder();
            formatter = new Formatter(sb, Locale.GERMAN);
            formatter.format("%3d", this.key[i]);
            String fstr = formatter.toString();
            formatter.close();
            if (i == (this.key.length - 1))
                str = str + fstr;
            else
                str = str + fstr + ",";
        }

        return "KESUM: " + str1 + " [" + str + "]";
    }

    /**
     * @return the fUZZYBASE_CHARNUM
     */
    public long getFuzzyBaseCharNumber()
    {
        return fuzzyBaseCharNumber;
    }

    /**
     * @return the maxFuzzyKeyLength
     */
    public int getFuzzyKeyLength()
    {
        return fuzzyKeyLength;
    }

    /**
     * @return the key
     */
    public short[] getKey()
    {
        return key;
    }

    /**
     * return the sum of the keys. Normally this is computed using the function @see de.folt.fuzzy.FuzzyNodeKey#computeKeySum()
     * 
     * @return the keysum
     */
    public int getKeysum()
    {
        return keysum;
    }

    /**
     * @return the nGrams
     */
    public int getNGrams()
    {
        return nGrams;
    }

    /**
     * @param fuzzybase_charnum
     *            the fUZZYBASE_CHARNUM to set
     */
    public void setFuzzyBaseCharNumber(long fuzzybase_charnum)
    {
        fuzzyBaseCharNumber = fuzzybase_charnum;
    }

    /**
     * This sets the length of the FuzzyKey. The Fuzzy key "key" is an array of shorts.
     * 
     * @param maxFuzzyKeyLength
     *            the maxFuzzyKeyLength to set
     */
    public void setFuzzyKeyLength(int maxFuzzyKeyLength)
    {
        this.fuzzyKeyLength = maxFuzzyKeyLength;
    }

    /**
     * @param key
     *            the key to set
     */
    public void setKey(short[] key)
    {
        this.key = key;
    }

    /**
     * @param keysum
     *            the kEYSUM to set
     */
    public void setKeysum(int keysum)
    {
        this.keysum = keysum;
    }

    /**
     * @param grams
     *            the nGrams to set
     */
    public void setNGrams(int grams)
    {
        nGrams = grams;
    }
}
