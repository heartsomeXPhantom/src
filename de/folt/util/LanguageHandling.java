/*
 * Created on 27.05.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * This class contains several methods for handling standard language codes. Language codes are read from a file and stored in a hash table. The file format looks currently like this (lancodes.txt in the in directory.<br>
 * A language can be accessed in various ways: (Example Abkhazian=ab)<br>
 * the short name: ab<br>
 * the long name:  Abkhazian <br>
 * the combined name: Abkhazian ab<br>
 * <pre>
 * Afar=aa
 * Abkhazian=ab
 * Afrikaans=af
 * ...
 * Arabic (Egypt)=ar-eg
 * Arabic (Jordan)=ar-jo
 * Arabic (Kuwait)=ar-kw</pre>
 * Actually three tables are used which have the following structure (Example Abkhazian=ab)
 *  * <pre> 
 *        languagecombinedtable     Key: "Abkhazian ab"   Value: "ab"
 *        lanshorttable             Key: "Abkhazian"      Value: "ab"
 *        lanlongtable              Key: "ab"             Value: "Abkhazian"
 *        </pre>
 * </pre> 
 * @author klemens
 *

 */
public class LanguageHandling
{

    private static Hashtable<String, String> languagecombinedtable = null;

    private static Hashtable<String, String> lanlongtable = null;

    private static Hashtable<String, String> lanshorttable = null;

    /**
     * getCombinedLanguageCodeFromShortLanguageCode return the combined language code from a short language looking like this "ab" -> "ab Abkhazian"<br>
     * Method uses hash table lanlongtable to get the correct language.
     * 
     * @param language the short name of a language, e.g. "ab" 
     * @return the short language code, e.g. "ab Abkhazian"
     */
    public static String getCombinedLanguageCodeFromShortLanguageCode(String language)
    {

        if (language.equals(""))
        {
            return "";
        }

        if ((lanlongtable == null) || (lanshorttable == null))
        {
            languagecombinedtable = readLanguageCodesFromFile();
            if (lanshorttable != null)
            {
                return language + " "  + (String) lanshorttable.get(language);
            }
        }

        return language + " "  + (String) lanshorttable.get(language);
    }
    
    /**
     * getCombinedLanguages return a sorted array of language codes
     * @return the sorted array with the combined language codes
     */
    public static String[] getCombinedLanguages()
    {
        
        if ((lanlongtable == null) || (lanshorttable == null))
        {
            languagecombinedtable = readLanguageCodesFromFile();
            if (languagecombinedtable == null)
            {
                return null;
            }
        }
        
        int size = languagecombinedtable.size();
        String [] array = new String[size];
        Enumeration<String> e = languagecombinedtable.keys();
        int i = 0;
        while (e.hasMoreElements())
        {
            array[i] = e.nextElement();
            i++;
        }
        Arrays.sort(array);
        return array;
    }

    /**
     * @return the languagecodetable
     */
    public static Hashtable<String, String> getLanguagecodetable()
    {
        return languagecombinedtable;
    }

    /**
     * @return the lanlongtable
     */
    public static Hashtable<String, String> getLanlongtable()
    {
        return lanlongtable;
    }
    
    /**
     * @return the lanshorttable
     */
    public static Hashtable<String, String> getLanshorttable()
    {
        return lanshorttable;
    }
    
    /**
     * getLongLanguageCodeFromShortLanguageCode return the long language code from a short language looking like this "ab" -> "Abkhazian"<br>
     * Method uses hash table lanlongtable to get the correct language.
     * 
     * @param language the long name of a language, e.g. "ab" 
     * @return the short language code, e.g. "Abkhazian"
     */
    public static String getLongLanguageCodeFromShortLanguageCode(String language)
    {

        if (language.equals(""))
        {
            return "";
        }

        if ((lanlongtable == null) || (lanshorttable == null))
        {
            languagecombinedtable = readLanguageCodesFromFile();
            if (lanlongtable != null)
            {
                return (String) lanlongtable.get(language);
            }
        }

        return (String) lanlongtable.get(language);
    }
    
    /**
     * getLongLanguages return a sorted array of language codes
     * @return the sorted array with the combined language codes
     */
    public static String[] getLongLanguages()
    {
        if ((lanlongtable == null) || (lanshorttable == null))
        {
            languagecombinedtable = readLanguageCodesFromFile();
            if (languagecombinedtable == null)
            {
                return null;
            }
        }
        
        int size = lanlongtable.size();
        String [] array = new String[size];
        Enumeration<String> e = lanlongtable.elements();
        int i = 0;
        while (e.hasMoreElements())
        {
            array[i] = e.nextElement();
            i++;
        }
        Arrays.sort(array);
        return array;
    }

    /**
     * getShortLanguageCodeFromCombinedTable return the short language code for a language looking like this "Abkhazian ab"<br>
     * Method uses hash table languagecombinedtable to get the correct language.
     * @param language the long name of a language, e.g. "Abkhazian ab" 
     * @return the short language code, e.g. "ab"
     */
    public static String getShortLanguageCodeFromCombinedTable(String language)
    {

        if (language.equals(""))
        {
            return "";
        }

        if ((lanlongtable == null) || (lanshorttable == null))
        {
            languagecombinedtable = readLanguageCodesFromFile();
            if (languagecombinedtable != null)
            {
                return (String) languagecombinedtable.get(language);
            }
        }

        return (String) languagecombinedtable.get(language);
    }

    /**
     * getShortLanguageCodeFromLongLanguageCode return the short language code from a long language looking like this   "Abkhazian" -> "ab"<br>
     * Method uses hash table lanshorttable to get the correct language.
     * @param language the long name of a language, e.g. "Abkhazian" 
     * @return the short language code, e.g. "ab"
     */
    public static String getShortLanguageCodeFromLongLanguageCode(String language)
    {

        if (language.equals(""))
        {
            return "";
        }

        if ((lanlongtable == null) || (lanshorttable == null))
        {
            languagecombinedtable = readLanguageCodesFromFile();
            if (lanshorttable != null)
            {
                return (String) lanshorttable.get(language);
            }
        }

        return (String) lanshorttable.get(language);
    }
    
    /**
     * getShortLanguages return a sorted array of the short language codes
     * @return the sorted array with the short language codes
     */
    public static String[] getShortLanguages()
    {
        if ((lanlongtable == null) || (lanshorttable == null))
        {
            languagecombinedtable = readLanguageCodesFromFile();
            if (languagecombinedtable == null)
            {
                return null;
            }
        }
        
        int size = lanshorttable.size();
        String [] array = new String[size];
        Enumeration<String> e = lanshorttable.elements();
        int i = 0;
        while (e.hasMoreElements())
        {
            array[i] = e.nextElement();
            i++;
        }
        Arrays.sort(array);
        return array;
    }
    
    /**
     * readLanguageCodesFromFile read the language codes from a default file (lancodes.txt in the ini directory)
     * 
     * @return a hash table key short code - value long language code - Key: Abkhazian ab Value: ab
     */
    public static Hashtable<String, String> readLanguageCodesFromFile()
    {

        String filename = OpenTMSProperties.getInstance().getOpenTMSProperty("opentms.languagecodefile");
        return readLanguageCodesFromFile(filename);
    }
    
    /**
     * readLanguageCodesFromFile read the language codes from a  file
     * 
     * @param langCodeFileName the file name to use
     * @return a hash table key short code - value long language code - Key: Abkhazian ab Value: ab
     */
    public static Hashtable<String, String> readLanguageCodesFromFile(String langCodeFileName)
    {
        lanlongtable = new Hashtable<String, String>();
        lanshorttable = new Hashtable<String, String>();
        languagecombinedtable = new Hashtable<String, String>();

        File lanCodeFile = new File(langCodeFileName);
        if (!lanCodeFile.exists())
        {
            langCodeFileName = OpenTMSProperties.getInstance().getOpenTMSProperty("opentms.languagecodefile");
            lanCodeFile = new File(langCodeFileName);
            if (!lanCodeFile.exists())
                return null;
        }

        try
        {
            File extractfile = new File(langCodeFileName);
            Reader fiin = new InputStreamReader(new FileInputStream(extractfile), "UTF-8");
            BufferedReader finstream = new BufferedReader(fiin);

            String line = "";
            while ((line = finstream.readLine()) != null)
            {
                String[] lanline = line.split("=");
                if (lanline.length == 2)
                {
                    String langshortcode = lanline[1];
                    String langlongcode = lanline[0];
                    lanlongtable.put(langlongcode, langshortcode);
                    lanshorttable.put(langshortcode, langlongcode);
                    languagecombinedtable.put(langshortcode + " " + langlongcode, langshortcode);
                }
            }

            finstream.close();
            fiin.close();

            System.out.println(languagecombinedtable.size() + " languages read from " + langCodeFileName);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return null;
        }

        return languagecombinedtable;
    }

}
