/*
 * Created on 11.02.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.test.testdata;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import de.folt.fuzzy.StringFuzzyNode;
import de.folt.fuzzy.StringPartitionedFuzzyNodeTree;
import de.folt.util.Timer;

/**
 * @author klemens
 * 
 *         To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and Comments
 */
public class TestDataReader
{

    private static int iLinesRead = 0;

    private static int iStringsInserted = 0;

    private static int NODESSEARCHED = 0;

    private static int NODESMATCHED = 0;

    private static int NODESPUSHED = 0;

    /**
     * main
     * 
     * @param args
     */
    public static void main(String[] args)
    {
        Timer readTimer = new Timer();
        int similarity = 70;
        int iKeyLen = 41;
        String fileName = TestDataGenerator.getFileName();
        if (args.length > 0)
            fileName = args[0];
        readTimer.startTimer();
        StringPartitionedFuzzyNodeTree<String> root = readTestdata(fileName, iKeyLen);
        readTimer.stopTimer();
        System.out.println(readTimer.timerString("Read: " + TestDataGenerator.getFileName() + " " + iLinesRead + " " + iStringsInserted, iStringsInserted));
        System.out.println("Nodes: " + root.countNodes());
        while (similarity <= 100)
        {
            readTimer.startTimer();
            iStringsInserted = 0;
            searchTestdata(fileName, iKeyLen, similarity, root, 20);
            readTimer.stopTimer();
            long timeNeeded = readTimer.getStopTime() - readTimer.getStartTime();
            // System.out.println(readTimer.timerString("Search: " + TestDataGenerator.getFileName() + " " + iLinesRead + " " + iStringsInserted, iStringsInserted));
            String summaryinfo = iStringsInserted + "\t" + iKeyLen + "\t" + similarity + "\t" + timeNeeded + "\t" + root.countNodes() + "\t" + NODESMATCHED + "\t" + NODESPUSHED + "\t" + NODESSEARCHED + "\t" + (float)timeNeeded/(float)iStringsInserted + "\n";
            System.out.print(summaryinfo);
            similarity = similarity + 10;
        }
    }

    /**
     * readTestdata read the test data and compile them into a StringPartitionedFuzzyNodeTree
     * 
     * @param fileName
     *            file name to read
     * @param iKeyLen
     *            key length for fuzzy node keys
     * @return the root StringPartitionedFuzzyNodeTree
     */
    public static StringPartitionedFuzzyNodeTree<String> readTestdata(String fileName, int iKeyLen)
    {
        StringPartitionedFuzzyNodeTree<String> root = new StringPartitionedFuzzyNodeTree<String>();
        iLinesRead = 0;
        iStringsInserted = 0;
        try
        {
            BufferedReader f = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
            f.readLine(); // over read first line
            String line = "";
            while ((line = f.readLine()) != null)
            {
                iLinesRead++;
                String[] content = line.split("\t");
                for (int j = 0; j < content.length; j++)
                {
                    StringFuzzyNode<String> fuzzyNodeToAdd = new StringFuzzyNode<String>(content[j], content[j], iKeyLen);
                    root.insertFuzzyNode(fuzzyNodeToAdd);
                    iStringsInserted++;
                }
            }
            f.close();

            return root;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * searchTestdata search the test data with similarity
     * 
     * @param fileName
     *            file name to read
     * @param iKeyLen
     *            key length for fuzzy node keys
     * @param similarity
     *            search similarity
     * @param searchPercent
     *            % of segments to be searched from total records read
     */
    public static void searchTestdata(String fileName, int iKeyLen, int similarity, StringPartitionedFuzzyNodeTree<String> root, int searchPercent)
    {
        iLinesRead = 0;
        iStringsInserted = 0;
        try
        {
            BufferedReader f = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
            f.readLine(); // over read first line
            String line = "";
            NODESSEARCHED = 0;
            NODESMATCHED = 0;
            NODESPUSHED = 0;
            while ((line = f.readLine()) != null)
            {
                iLinesRead++;
                if (!((iLinesRead % searchPercent) == 1))
                    continue;
                String[] content = line.split("\t");

                for (int j = 1; j < content.length; j++)
                {
                    StringFuzzyNode<String> fuzzyCompareKey = new StringFuzzyNode<String>(content[j], content[j], iKeyLen);
                    root.search(fuzzyCompareKey, similarity);
                    NODESMATCHED = NODESMATCHED + root.getNODESMATCHED();
                    NODESPUSHED = NODESPUSHED + root.getNODESPUSHED();
                    NODESSEARCHED = NODESSEARCHED + root.getNODESSEARCHED();
                    iStringsInserted++;
                }
            }
            f.close();
            return;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return;
        }
    }
}
