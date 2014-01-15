/*
 * Created on 21.01.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.models.datamodel;

import java.lang.reflect.Method;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import java.util.Vector;

import de.folt.util.Timer;

/**
 * @author klemens
 * 
 *         To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and Comments
 */
public class Test
{
    public class TestObserver implements Observer
    {

        /*
         * (non-Javadoc)
         * 
         * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
         */
        @Override
        public void update(Observable arg0, Object arg1)
        {
            // System.out.println("Something has changed: " + arg0 + " " + arg1);
        }

    }

    /**
     * main
     * 
     * @param args
     */
    public static void main(String[] args)
    {
        Test test = new Test();
        test.test(true);
    }

    String[] langs =
        {
                "de", "en", "fr", "it", "pl", "gr", "ar", "es", "ru"
        };

    private Vector<MultiLingualObject> multvector = null;

    Random Rand = new Random();

    String string = "abcde fghi jklmno pqr stuvwx yzüöäABCD EFGHI JKLMNOP QRSTUV WXYZÖ ÄÜ";

    private TestObserver testobserver = new TestObserver();

    /**
     * createRandomLinguisticProperty create a random LinguisticProperty for testing purposes
     * 
     * @return the property
     */
    public LinguisticProperty createRandomLinguisticProperty()
    {
        String key = randomString(6);
        key = key.replaceAll(" ", ""); // no blanks in key
        if (key.length() == 0)
            key = "key";
        String value = randomString(10);
        LinguisticProperty ling = new LinguisticProperty(key, value);
        ling.addObserver(testobserver);
        return ling;
    }

    /**
     * createRandomMonoLingualObject create a random MonoLingualObject for testing purposes
     * 
     * @return MonoLingualObject
     */
    public MonoLingualObject createRandomMonoLingualObject()
    {
        try
        {
            @SuppressWarnings("rawtypes")
			Class[] classes = new Class[2];
            classes[0] = String.class;
            classes[1] = Object.class;
            Method method = MonoLingualObject.class.getMethod("simpleComputePlainText", classes);
            String segment = randomString(100);
            String language = langs[Rand.nextInt(langs.length)];
            MonoLingualObject mono = new MonoLingualObject(segment, language, MonoLingualObject.class, method, null);

            int iRand = Rand.nextInt(5) + 1;
            for (int i = 0; i < iRand; i++) // generate some linguistic properties
            {
                mono.addLinguisticProperty(createRandomLinguisticProperty());
            }
            mono.addObserver(testobserver);
            return mono;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return null;
        }

    }

    /**
     * createRandomMultiLingualObject create a random MultiLingualObject for testing purposes and add some random MonoLingualObjects (max. 7)
     * 
     * @return MultiLingualObject
     */
    public MultiLingualObject createRandomMultiLingualObject()
    {
        MultiLingualObject multi = new MultiLingualObject();
        multi.addObserver(testobserver);

        int iRand = Rand.nextInt(7) + 1;
        for (int i = 0; i < iRand; i++) // generate some linguistic properties
        {
            multi.addLinguisticProperty(createRandomLinguisticProperty());
        }

        iRand = Rand.nextInt(7) + 1;
        for (int i = 0; i < iRand; i++) // generate some linguistic properties
        {
            multi.addMonoLingualObject(createRandomMonoLingualObject());
        }

        multi.addObserver(testobserver);
        return multi;
    }

    /**
     * @return the multvector
     */
    public Vector<MultiLingualObject> getMultvector()
    {
        return multvector;
    }

    /**
     * nExactStrings creates iNumber random strings of length iLenght
     * 
     * @param iNumber
     *            number of strings to generate
     * @param iLength
     *            exact length of the string
     * @return
     */
    public String[] nExactStrings(int iNumber, int iLength)
    {
        String[] result = new String[iNumber];
        for (int i = 0; i < iNumber; i++)
        {
            result[i] = this.randomExcactString(iLength);
        }

        return result;
    }

    /**
     * nRandomStrings creates iNumber random strings of random length iLenght
     * 
     * @param iNumber
     *            number of strings to generate
     * @param iLength
     *            maximum length of the string
     * @return
     */
    public String[] nRandomStrings(int iNumber, int iLength)
    {
        String[] result = new String[iNumber];
        for (int i = 0; i < iNumber; i++)
        {
            result[i] = this.randomString(iLength);
        }

        return result;
    }

    /**
     * randomExcactString create a string of iLen characters
     * 
     * @param iMaxLen
     *            the maximum length of the string
     * @return the generated String
     */
    public String randomExcactString(int iLength)
    {
        StringBuffer buf = new StringBuffer(iLength);
        for (int i = 0; i < iLength; i++)
        {
            char ch = string.charAt(Rand.nextInt(string.length()));
            buf.append(ch);
        }
        return buf.toString();
    }

    /**
     * randomString create a random string of maximum iMaxLen characters
     * 
     * @param iMaxLen
     *            the maximum length of the string
     * @return the generated String
     */
    public String randomString(int iMaxLen)
    {
        int iRandLen = Rand.nextInt(iMaxLen) + 1;
        String str = "";
        for (int i = 0; i < iRandLen; i++)
        {
            char ch = string.charAt(Rand.nextInt(string.length()));
            str = str + ch;
        }
        return str;
    }

    /**
     * test
     * 
     * @param bPrintMessage
     */
    public void test(boolean bPrintMessage)
    {
        test(bPrintMessage, 10000);
    }

    /**
     * test simple test method for generating some general linguistic objects
     * 
     * @param bPrintMessage
     *            print the formatted MultiLingualObject
     * @param iNumberOfMultiLingualObjects
     *            how many MultiLingualObject to create
     */
    public void test(boolean bPrintMessage, int iNumberOfMultiLingualObjects)
    {
        try
        {
            @SuppressWarnings("rawtypes")
			Class[] classes = new Class[2];
            classes[0] = String.class;
            classes[1] = Object.class;
            @SuppressWarnings("unused")
            Method method = MonoLingualObject.class.getMethod("simpleComputePlainText", classes);

            Test test = new Test();

            Timer timer = new Timer();

            multvector = new Vector<MultiLingualObject>();
            timer.startTimer();
            int iNum = iNumberOfMultiLingualObjects;
            for (int i = 0; i < iNum; i++) // generate 1000 MUL Objects
            {

                MultiLingualObject multi = test.createRandomMultiLingualObject();
                multvector.add(multi);
            }
            timer.stopTimer();
            if (bPrintMessage)
            {
                for (int i = 0; i < multvector.size(); i++) // generate 1000 MUL Objects
                {
                    System.out.println("MultiLingualObject " + i + ": " + multvector.get(i).toString());
                    System.out.println((multvector.get(i)).format());
                }

                System.out.println(timer.timerString("Generating MultiLingualObjects", iNum));
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
