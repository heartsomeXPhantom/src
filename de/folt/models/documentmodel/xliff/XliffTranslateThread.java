/*
 * Created on 14.02.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.models.documentmodel.xliff;

import java.util.Hashtable;
import java.util.List;

import org.jdom.Element;

import de.folt.models.datamodel.DataSource;
import de.folt.util.OpenTMSException;

/**
 * Class implements a translate operation as a thread. It should be used to parallelize translation tasks.
 * 
 * @author klemens
 * 
 */
public class XliffTranslateThread implements Runnable
{

    private DataSource dataSource;

    private Element file;

    private int iLowerBound;

    private int iUpperBound;

    private int matchSimilarity;

    private String sourceLanguage;

    private String targetLanguage;

    private XliffDocument thisXliff;
    
    private Hashtable<String, Object> translationParameters;

    private List<Element> transunits;

    /**
     * XliffTranslateThread translates a trans-unit given the source language, target Language and match similarity
     * 
     * @param dataSource
     *            the datasource to be used
     * @param file the file element to translate
     * @param transunits
     *            the transunits to translate
     * @param thisXliff
     *            the basic xliff document
     * @param iLowerBound
     *            start the translation process from this element
     * @param iUpperBound
     *            stop the translation process with this element
     * @param sourceLanguage
     *            the source language to use
     * @param targetLanguage
     *            the target language to use
     * @param matchSimilarity
     *            the similarity (fuzzy) match quality (0 - 100) to use
     * @param translationParameters
     *            the hash table contains parameters which control some parameters, e.g. should header/source/target properties be written to alt-trans
     * @throws OpenTMSException
     */
    public XliffTranslateThread(DataSource dataSource, Element file, List<Element> transunits, int iLowerBound, int iUpperBound, XliffDocument thisXliff, String sourceLanguage, String targetLanguage,
            int matchSimilarity, Hashtable<String, Object> translationParameters) throws OpenTMSException
    {
        this.dataSource = dataSource;
        this.iLowerBound = iLowerBound;
        this.iUpperBound = iUpperBound;
        this.transunits = transunits;
        this.matchSimilarity = matchSimilarity;
        this.thisXliff = thisXliff;
        this.sourceLanguage = sourceLanguage;
        this.targetLanguage = targetLanguage;
        this.translationParameters = translationParameters;
        this.file = file;
        // System.out.println("XliffTranslateThread: " + iLowerBound + "-" + iUpperBound);
    }

    /**
     * translates a trans-unit given the source language, target Language and match similarity. The parameters are set when the thread object is created. If the trans-unit contains the attribute
     * translate="no" the trans-unit no search is applied.
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run()
    {
        try
        {
            for (int j = iLowerBound; j < iUpperBound; j++)
            {
                Element transUnit = transunits.get(j);
                // call the translate method of the data source
                // System.out.println("Translate: " + this.toString() + " " + j);
                String translateAttribute = transUnit.getAttributeValue("translate");
                if ((translateAttribute != null) && translateAttribute.equals("no"))
                    continue;
                dataSource.translate(transUnit, file, thisXliff, sourceLanguage, targetLanguage, matchSimilarity, translationParameters);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

}
