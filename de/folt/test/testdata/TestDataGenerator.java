/*
 * Created on 11.02.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.test.testdata;

import java.io.File;
import java.io.FileWriter;
import java.util.Random;

/**
 * Class creates test files for performance measuring.
 * 
 * @author klemens
 * 
 */
public class TestDataGenerator
{

    private static String alphabet = "1234567890 abcdefghijklmnopqrstuvwxyz ABCDEFGHIJKLMNOPQRTUVWXYZ";
    
    private static String fileName = "test.txt";
    private static int maxLen = 40;
    private static int minLen = 5;
    private static int numberOfEntries = 10000;
    private static int numberofSimilarEntries = 5;
    private static Random Rand = new Random();

    private static int[] similarity =
        {
                95, 90, 85, 80, 50
        };

    /**
     * The alphabet used to generate the test strings default: alphabet = "1234567890 abcdefghijklmnopqrstuvwxyz ABCDEFGHIJKLMNOPQRTUVWXYZ";
     * 
     * @return the alphabet
     */
    public static String getAlphabet()
    {
        return alphabet;
    }

    /**
     * @return the fileName
     */
    public static String getFileName()
    {
        return fileName;
    }

    /**
     * @return the maxLen
     */
    public static int getMaxLen()
    {
        return maxLen;
    }

    /**
     * @return the minLen
     */
    public static int getMinLen()
    {
        return minLen;
    }

    /**
     * @return the numberOfEntries
     */
    public static int getNumberOfEntries()
    {
        return numberOfEntries;
    }

    /**
     * @return the numberofSimilarEntries
     */
    public static int getNumberofSimilarEntries()
    {
        return numberofSimilarEntries;
    }

    /**
     * @return the similarity
     */
    public static int[] getSimilarity()
    {
        return similarity;
    }

    /**
     * main this method creates test strings.<br>
     * Default values: String fileName = "test.txt";<br>
     * numberOfEntries = 1000;<br>
     * minLen = 5;<br>
     * maxLen = 40;<br>
     * numberofSimilarEntries = 5; int[]<br>
     * similarity array = { 95, 90, 85, 80, 50 };<br>
     * 
     * @param args
     *            0 = file name, 1 = numberOfEntries, 2 min length, 3 = max length, 4 = numberofSimilarEntries; 5..5+numberofSimilarEntries % similarities
     */
    public static void main(String[] args)
    {
        TestDataGenerator testData = new TestDataGenerator();


        if (args.length > 0)
        {
            fileName = args[0];
        }

        if (args.length > 1)
        {
            numberOfEntries = Integer.parseInt(args[1]);
        }

        if (args.length > 2)
        {
            minLen = Integer.parseInt(args[2]);
        }

        if (args.length > 2)
        {
            maxLen = Integer.parseInt(args[3]);
        }

        if (args.length > 3)
        {
            numberofSimilarEntries = Integer.parseInt(args[4]);
        }

        if (args.length == 5)
        {
            similarity = new int[numberofSimilarEntries];
            for (int i = 0; i < numberofSimilarEntries; i++)
            {
                similarity[i] = Integer.parseInt(args[4]);
            }
        }
        else if (args.length > 5)
        {
            similarity = new int[numberofSimilarEntries];
            for (int i = 5; i < (numberofSimilarEntries + 4); i++)
            {
                similarity[i-5] = Integer.parseInt(args[i]);
            }
        }

        testData.generateTestData(fileName, numberOfEntries, minLen, maxLen, numberofSimilarEntries, similarity);
        
        System.out.println("Finished - " + fileName + " " + numberOfEntries + " " +  minLen + " " +  maxLen + " " +  numberofSimilarEntries + " " + similarity.toString());
    }

    /**
     * randomString create a random string of maximum iMaxLen characters
     * 
     * @param iMinLen
     *            the minimum length of the string
     * @param iMaxLen
     *            the maximum length of the string
     * @return the generated String
     */
    public static String randomString(int minLen, int iMaxLen)
    {
        int iRandLen = Rand.nextInt(iMaxLen) + 1 + minLen;
        if (iRandLen > iMaxLen) // ok not really equally distributed...
            iRandLen = iMaxLen;
        String str = "";
        for (int i = 0; i < iRandLen; i++)
        {
            char ch = alphabet.charAt(Rand.nextInt(alphabet.length()));
            str = str + ch;
        }
        return str;
    }

    /**
     * sets the alpahabet used to generate the test strings default: alphabet = "1234567890 abcdefghijklmnopqrstuvwxyz ABCDEFGHIJKLMNOPQRTUVWXYZ";
     * 
     * @param alphabet
     *            the alphabet to set
     */
    public static void setAlphabet(String alphabet)
    {
        TestDataGenerator.alphabet = alphabet;
    }

    /**
     * @param fileName the fileName to set
     */
    public static void setFileName(String fileName)
    {
        TestDataGenerator.fileName = fileName;
    }

    /**
     * @param maxLen the maxLen to set
     */
    public static void setMaxLen(int maxLen)
    {
        TestDataGenerator.maxLen = maxLen;
    }

    /**
     * @param minLen the minLen to set
     */
    public static void setMinLen(int minLen)
    {
        TestDataGenerator.minLen = minLen;
    }

    /**
     * @param numberOfEntries the numberOfEntries to set
     */
    public static void setNumberOfEntries(int numberOfEntries)
    {
        TestDataGenerator.numberOfEntries = numberOfEntries;
    }

    /**
     * @param numberofSimilarEntries the numberofSimilarEntries to set
     */
    public static void setNumberofSimilarEntries(int numberofSimilarEntries)
    {
        TestDataGenerator.numberofSimilarEntries = numberofSimilarEntries;
    }

    /**
     * @param similarity the similarity to set
     */
    public static void setSimilarity(int[] similarity)
    {
        TestDataGenerator.similarity = similarity;
    }

    /**
     * generateTestData generate numberOfEntries test string of length between minLen and maxLen and for each string generate in addition numberofSimilarEntries similar strings. The similar strings
     * are generated based on the % similarities given in the array similarity.
     * 
     * @param fileName
     *            out put file name
     * @param numberOfEntries
     *            number of different strings to generate
     * @param minLen
     *            minimum string length
     * @param maxLen
     *            maximum string length
     * @param numberofSimilarEntries
     *            number of similar entries to generate
     * @param similarity
     *            the similarity array in %
     */
    public void generateTestData(String fileName, int numberOfEntries, int minLen, int maxLen, int numberofSimilarEntries, int[] similarity)
    {
        File f = new File(fileName);
        if (f.exists())
            f.delete();
        try
        {
            FileWriter file = new FileWriter(fileName);
            file.write("#\tString");
            for (int j = 0; j < numberofSimilarEntries; j++)
            {
                file.write("\t" + similarity[j]);
            }
            file.write("\n");
            for (int i = 0; i < numberOfEntries; i++)
            {
                String randomString = randomString(minLen, maxLen);
                file.write(i + "\t" + randomString);

                for (int j = 0; j < numberofSimilarEntries; j++)
                {
                    StringBuffer simString = new StringBuffer(randomString);
                    int iNumChange = 0;
                    if ((similarity.length == 1) || !(similarity.length == numberofSimilarEntries))
                        iNumChange = simString.length() * similarity[0] / 100;
                    else
                        iNumChange = simString.length() * similarity[j] / 100;

                    for (int k = 0; k < iNumChange; k++)
                    {
                        simString.setCharAt(Rand.nextInt(simString.length()), alphabet.charAt(Rand.nextInt(alphabet.length())));
                    }
                    file.write("\t" + simString.toString());
                }
                file.write("\n");
            }
            file.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

}
