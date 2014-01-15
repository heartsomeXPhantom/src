package de.folt.models.documentmodel.xliff;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.folt.models.applicationmodel.termtagger.TermTagObjectMatch;
import de.folt.models.applicationmodel.termtagger.TermTagObjectTable;
import de.folt.util.WordHandling;

public class XliffElementHandler
{

	private static char		finalReplaceChar	= '\uF8FE';					// 63742 old: '\uF8FF'

	private static char		startReplaceChar	= '\uE040';					// 57408

	private static char		stopReplaceChar		= '\uF8FF';					// 63743 (char) ((int) finalReplaceChar + 1);

	// "sub" just for the moment not really correct
	private static String[]	xliffEmptylineCodes	= { "b", "e", "x" };

	private static String[]	xliffInlineCodes	= { "bpt", "ept", "it", "ph" };

	private static String[]	xliffSublineCode	= { "sub", "g", "mrk" };

	/**
	 * Check if a character is replace char
	 * 
	 * @param c
	 *            the replacement char
	 * @return true if replacement char
	 */
	public static boolean bIsReplaceChar(char c)
	{
		return startReplaceChar <= c;
	}

	public static char getFinalReplaceChar()
	{
		return finalReplaceChar;
	}

	public static char getStartReplaceChar()
	{
		return startReplaceChar;
	}

	public static char getStopReplaceChar()
	{
		return stopReplaceChar;
	}

	public static String[] getXliffEmptylineCodes()
	{
		return xliffEmptylineCodes;
	}

	public static String[] getXliffInlineCodes()
	{
		return xliffInlineCodes;
	}

	public static String[] getXliffSublineCode()
	{
		return xliffSublineCode;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{

		String testString = "!Haus.";
		XliffElementHandler xliffElementHandler = new XliffElementHandler(testString);
		System.out.println("\"" + testString + "\"\n>>> \"" + xliffElementHandler.encodedXliffString + "\""
				+ "\" >>>\n\"" + xliffElementHandler.decode() + "\"");
		testString = "Das ist <b></b>ein <ph>c <sub> </sub>cc</ph> und<g/> <e /> <ph id=\"c\">blbals</ph> test<ept>vv</ept>string sa <ph />";
		xliffElementHandler = new XliffElementHandler(testString);
		System.out.println("\"" + testString + "\"\n>>> \"" + xliffElementHandler.encodedXliffString + "\""
				+ "\" >>>\n\"" + xliffElementHandler.decode() + "\"");
		testString = "[Fe(CN)6]3";
		xliffElementHandler = new XliffElementHandler(testString);
		System.out.println("\"" + testString + "\"\n>>> \"" + xliffElementHandler.encodedXliffString + "\""
				+ "\" >>>\n\"" + xliffElementHandler.decode() + "\"");
		testString = "Eine chemische Formel [Fe(CN)6]3 ist das.";
		xliffElementHandler = new XliffElementHandler(testString);
		System.out.println("\"" + testString + "\"\n>>> \"" + xliffElementHandler.encodedXliffString + "\""
				+ "\" >>>\n\"" + xliffElementHandler.decode() + "\"");

		testString = "Eine Abkürzung AcM.G ist das.";
		xliffElementHandler = new XliffElementHandler(testString);
		System.out.println("\"" + testString + "\"\n>>> \"" + xliffElementHandler.encodedXliffString + "\""
				+ "\" >>>\n\"" + xliffElementHandler.decode() + "\"");

		testString = "Ei'ne Abkürzung AcM'G ist das.";
		xliffElementHandler = new XliffElementHandler(testString);
		System.out.println("\"" + testString + "\"\n>>> \"" + xliffElementHandler.encodedXliffString + "\""
				+ "\" >>>\n\"" + xliffElementHandler.decode() + "\"");

	}

	public static void setFinalReplaceChar(char finalReplaceChar)
	{
		XliffElementHandler.finalReplaceChar = finalReplaceChar;
	}

	public static void setStartReplaceChar(char startReplaceChar)
	{
		XliffElementHandler.startReplaceChar = startReplaceChar;
	}

	public static void setStopReplaceChar(char stopReplaceChar)
	{
		XliffElementHandler.stopReplaceChar = stopReplaceChar;
	}

	public static void setXliffEmptylineCodes(String[] xliffEmptylineCodes)
	{
		XliffElementHandler.xliffEmptylineCodes = xliffEmptylineCodes;
	}

	public static void setXliffInlineCodes(String[] xliffInlineCodes)
	{
		XliffElementHandler.xliffInlineCodes = xliffInlineCodes;
	}

	public static void setXliffSublineCode(String[] xliffSublineCode)
	{
		XliffElementHandler.xliffSublineCode = xliffSublineCode;
	}

	@SuppressWarnings("unused")
	private String						decodedXliffString			= "";

	private Hashtable<String, String>	elementMap					= new Hashtable<String, String>();

	private String						encodedXliffString			= "";

	private char						highestReplaceChar			= startReplaceChar;

	private String						language					= "un";

	private Vector<TermTagObjectMatch>	termTagObjectMatchVector	= null;

	private TermTagObjectTable			termTagObjectTable			= null;

	private WordHandling				wordHandling				= new WordHandling();

	private String						xliffString;

	/**
	 * @param string
	 */
	public XliffElementHandler(String string)
	{
		encodedXliffString = this.encode(string);
		encodedXliffString = this.encodeSpaceChars(encodedXliffString);
		encodedXliffString = this.encodeWordSplitChars(encodedXliffString);
	}

	/**
	 * @param string
	 * @param wordHandling
	 */
	public XliffElementHandler(String string, WordHandling wordHandling)
	{
		this.setWordHandling(wordHandling);
		encodedXliffString = this.encode(string);
		encodedXliffString = this.encodeSpaceChars(encodedXliffString);
		encodedXliffString = this.encodeWordSplitChars(encodedXliffString);
		this.convertToTermTagObjectMatches();
	}

	/**
	 * @param string
	 * @param wordHandling
	 * @param language
	 */
	public XliffElementHandler(String string, WordHandling wordHandling, String language)
	{
		this.setLanguage(language);
		this.setWordHandling(wordHandling);
		encodedXliffString = this.encode(string);
		if (wordHandling != null)
			encodedXliffString = this.encodeSpaceChars(encodedXliffString);
		if (wordHandling != null)
			encodedXliffString = this.encodeWordSplitChars(encodedXliffString);
		this.convertToTermTagObjectMatches();
	}

	/**
	 * @param string
	 * @param wordHandling
	 * @param language
	 * @param termTagObjectTable
	 */
	public XliffElementHandler(String string, WordHandling wordHandling, String language, TermTagObjectTable termTagObjectTable)
	{
		this.setLanguage(language);
		this.setWordHandling(wordHandling);
		this.setTermTagObjectTable(termTagObjectTable);
		if (string == null)
			return;
		encodedXliffString = this.encode(string);
		encodedXliffString = this.encodeSpaceChars(encodedXliffString);
		encodedXliffString = this.encodeWordSplitChars(encodedXliffString);
		this.convertToTermTagObjectMatches();
	}

	/**
	 * @param wordHandling
	 */
	public XliffElementHandler(WordHandling wordHandling)
	{
		super();
		this.wordHandling = wordHandling;
	}

	/**
	 * @param string
	 */
	public char add(String string)
	{
		elementMap.put(highestReplaceChar + "", string);
		char lastchar = highestReplaceChar;
		incrementHighestReplaceChar();
		return lastchar;
	}

	/**
	 * 
	 */
	public void addWordSplitChars()
	{
		String string = WordHandling.getDefaultWordSplitChars();
		for (int m = 0; m < string.length(); m++)// check for mrk
		{
			char testch = string.charAt(m);
			this.add(testch + "");
		}
	}

	/**
	 * @param testch
	 * @return
	 */
	public boolean checkForElement(char testch)
	{
		if (testch >= getStartReplaceChar())
		{
			if (elementMap.containsKey(testch + ""))
			{
				String match = elementMap.get(testch + "");
				if ((match != null) && (match.indexOf("<") > -1))
				{
					if (match.matches("^<.*?/>$")) // moveable element
						return false;
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @param string
	 * @return
	 */
	public boolean checkForElement(String string)
	{
		for (int m = 0; m < string.length(); m++)// check for an element
		{
			char testch = string.charAt(m);
			if (testch >= getStartReplaceChar())
			{
				if (elementMap.containsKey(testch + ""))
				{
					String match = elementMap.get(testch + "");
					if ((match != null) && (match.indexOf("<") > -1))
						return true;
				}
			}
		}
		return false;
	}

	/**
	 * @param string
	 * @return
	 */
	public boolean checkForMrk(String string)
	{
		for (int m = 0; m < string.length(); m++)// check for mrk
		{
			char testch = string.charAt(m);
			if (testch >= getStartReplaceChar())
			{
				if (elementMap.containsKey(testch + ""))
				{
					String match = elementMap.get(testch + "");
					if ((match != null) && (match.indexOf("<mrk ") > -1))
						return true;
				}
			}
		}
		return false;
	}

	/**
	 * @param string
	 * @return
	 */
	public boolean checkForNotEmptyElement(char testch)
	{
		if (testch >= getStartReplaceChar())
		{
			if (elementMap.containsKey(testch + ""))
			{
				String match = elementMap.get(testch + "");
				if ((match != null) && (match.indexOf("<") > -1))
				{
					if (match.matches("^<.*?/>$")) // moveable element
						return false;
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @param string
	 * @return
	 */
	public boolean checkForNotEmptyElement(String string)
	{
		for (int m = 0; m < string.length(); m++)// check for an element
		{
			char testch = string.charAt(m);
			if (testch >= getStartReplaceChar())
			{
				if (elementMap.containsKey(testch + ""))
				{
					String match = elementMap.get(testch + "");
					if ((match != null) && (match.indexOf("<") > -1))
					{
						if (match.matches("^<.*?/>$")) // moveable element
							return false;
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * @param string
	 * @return
	 */
	public boolean checkForTermMrk(String string)
	{
		for (int m = 0; m < string.length(); m++)// check for mrk
		{
			char testch = string.charAt(m);
			if (testch >= getStartReplaceChar())
			{
				if (elementMap.containsKey(testch + ""))
				{
					String match = elementMap.get(testch + "");
					if ((match != null) && (match.indexOf("<MRK ") > -1))
					{
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * @param termTagObjectTable
	 * @return
	 */
	public Vector<TermTagObjectMatch> convertToTermTagObjectMatches()
	{
		termTagObjectMatchVector = new Vector<TermTagObjectMatch>();
		String sourceResult = this.getEncodedXliffString();
		int i = 0;
		StringBuffer buf = new StringBuffer();
		TermTagObjectMatch termTagObjectMatch;
		while (i < sourceResult.length())
		{

			if (sourceResult.charAt(i) < XliffElementHandler.getStartReplaceChar())
			{
				buf.append(sourceResult.charAt(i));
			}
			else
			{
				if (i > 0)
				{
					termTagObjectMatch = new TermTagObjectMatch(language, buf.toString());
					termTagObjectMatchVector.add(termTagObjectMatch);
					buf = new StringBuffer();
				}
				while ((i < sourceResult.length())
						&& sourceResult.charAt(i) >= XliffElementHandler.getStartReplaceChar())
				{
					buf.append(sourceResult.charAt(i));
					i++;
				}
				termTagObjectMatch = new TermTagObjectMatch(language, buf.toString());
				termTagObjectMatchVector.add(termTagObjectMatch);
				buf = new StringBuffer();
				i--;
			}
			i++;
		}

		if (buf.length() > 0)
		{
			termTagObjectMatch = new TermTagObjectMatch(language, buf.toString());
			termTagObjectMatchVector.add(termTagObjectMatch);
		}

		return termTagObjectMatchVector;
	}

	/**
	 * @return
	 */
	public String decode()
	{
		String resultString = encodedXliffString;
		Enumeration<String> enumKeys = elementMap.keys();
		while (enumKeys.hasMoreElements())
		{
			String key = enumKeys.nextElement();
			resultString = resultString.replaceAll(key, elementMap.get(key));
		}
		decodedXliffString = resultString;
		return resultString;
	}

	/**
	 * Dncode an xliff encoded string by replacing all unicoded elements with
	 * their real element code
	 * 
	 * @param string
	 * @return the decoded string
	 */
	public String decode(String string)
	{
		String resultString = string;
		resultString = resultString.replaceAll(getStopReplaceChar() + "", "");
		Enumeration<String> enumKeys = elementMap.keys();
		while (enumKeys.hasMoreElements())
		{
			String key = enumKeys.nextElement();
			String value = elementMap.get(key);
			resultString = resultString.replaceAll(key, value);
		}
		return resultString;
	}

	/**
	 * Encode an xliff string by replacing all elements with a unicode character
	 * (incremented) from the private area
	 * 
	 * @param string
	 * @return encode string
	 */
	public String encode(String string)
	{
		String resultString = string;

		for (int i = 0; i < xliffInlineCodes.length; i++)
		{
			String elementPattern = "<" + xliffInlineCodes[i] + ".*?>.*?</" + xliffInlineCodes[i] + ".*?>";
			Pattern pattern = Pattern.compile(elementPattern);
			Matcher matcher = pattern.matcher(string);
			while (matcher.find())
			{
				char mapped = add(matcher.group());
				resultString = resultString.replaceFirst(Pattern.quote(matcher.group()), mapped + "");
			}
		}

		for (int i = 0; i < xliffEmptylineCodes.length; i++)
		{
			String elementPattern = "<" + xliffEmptylineCodes[i] + ".*?/.*?>";
			Pattern pattern = Pattern.compile(elementPattern);
			Matcher matcher = pattern.matcher(string);
			while (matcher.find())
			{
				char mapped = add(matcher.group());
				resultString = resultString.replaceFirst(Pattern.quote(matcher.group()), mapped + "");
			}
		}

		for (int i = 0; i < xliffInlineCodes.length; i++)
		{
			String elementPattern = "<" + xliffInlineCodes[i] + ".*?/.*?>";
			Pattern pattern = Pattern.compile(elementPattern);
			Matcher matcher = pattern.matcher(string);
			while (matcher.find())
			{
				char mapped = add(matcher.group());
				resultString = resultString.replaceFirst(Pattern.quote(matcher.group()), mapped + "");
			}
		}

		for (int i = 0; i < xliffSublineCode.length; i++)
		{
			String elementPattern = "<" + xliffSublineCode[i] + ".*?>";
			Pattern pattern = Pattern.compile(elementPattern);
			Matcher matcher = pattern.matcher(string);
			while (matcher.find())
			{
				char mapped = add(matcher.group());
				resultString = resultString.replaceFirst(Pattern.quote(matcher.group()), mapped + "");

			}
		}

		for (int i = 0; i < xliffSublineCode.length; i++)
		{
			String elementPattern = "</" + xliffSublineCode[i] + ">";
			Pattern pattern = Pattern.compile(elementPattern);
			Matcher matcher = pattern.matcher(string);
			while (matcher.find())
			{
				char mapped = add(matcher.group());
				resultString = resultString.replaceFirst(Pattern.quote(matcher.group()), mapped + "");
			}
		}

		return resultString;
	}

	/**
	 * @param string
	 * @return
	 */
	public String encodeSpaceChars(String string)
	{
		String elementPattern = "\\s+";
		Pattern pattern = Pattern.compile(elementPattern);
		Matcher matcher = pattern.matcher(string);
		while (matcher.find())
		{
			char replaceChar = (char) this.getHighestReplaceChar();
			string = string.replaceFirst(Pattern.quote(matcher.group()), replaceChar + "");
			add(matcher.group());
		}

		return string;
	}

	public String encodeWordSplitChars(String string)
	{
		// do some preprocessing for single ' here
		if (this.wordHandling == null)
			return string;
		StringBuffer buffer = new StringBuffer(string);

		char wq = (char) ('\'' + WordHandling.getQuoteCharStart());
		for (int i = 0; i < buffer.length(); i++)
		{
			if (buffer.charAt(i) == '\'')
			{
				if (i == 0)
				{
					buffer.setCharAt(i, wq);
				}
				else if (!this.wordHandling.getSplitChars(this.language).contains(buffer.charAt(i - 1) + ""))
				{
					buffer.setCharAt(i, wq);
				}
			}
		}
		string = buffer.toString();
		String elementPattern = "[" + Pattern.quote(this.wordHandling.getSplitChars(this.language)) + "]+";
		Pattern pattern = Pattern.compile(elementPattern);
		Matcher matcher = pattern.matcher(string);
		boolean bEntityRecognition = false;
		while (matcher.find())
		{
			String matching = matcher.group();
			if (bEntityRecognition)
			{
				int pos = matcher.start();
				if (!(string.charAt(pos) + "").matches("\\s"))
				// check if the next char is a space or end of string
				{
					if ((pos + 1) < string.length())
					{
						String cpos = string.charAt(pos + 1) + "";
						if (pos == (string.length() - 1))
						{

						}
						else
						{
							if (!cpos.matches("\\s"))
								continue;
						}
					}
				}
			}
			char replaceChar = (char) this.getHighestReplaceChar();
			string = string.replaceFirst(Pattern.quote(matching), replaceChar + "");
			add(matching);
		}

		buffer = new StringBuffer(string);
		for (int j = 0; j < buffer.length(); j++)
		{
			if (buffer.charAt(j) == wq)
			{
				buffer.setCharAt(j, '\'');
			}
		}
		string = buffer.toString();
		return string;
	}

	public String getDecodedXliffString()
	{
		return decode(encodedXliffString);
	}

	public String getDecodedXliffString(String string)
	{
		return decode(string);
	}

	public Hashtable<String, String> getElementMap()
	{
		return elementMap;
	}

	/**
	 * @param cStart
	 * @return
	 */
	public String getElementName(char testch)
	{
		if (testch >= getStartReplaceChar())
		{
			if (elementMap.containsKey(testch + ""))
			{
				String match = elementMap.get(testch + "");
				if ((match != null) && (match.indexOf("<") > -1))
				{
					Pattern matchPattern = Pattern.compile("</?(\\w+).*>");
					Matcher matcher = matchPattern.matcher(match);
					if (matcher.find())
					{
						return matcher.group(1);
					}
				}
			}
		}
		return null;
	}

	public String getEncodedXliffString()
	{
		return encodedXliffString;
	}

	public char getHighestReplaceChar()
	{
		return highestReplaceChar;
	}

	public String getLanguage()
	{
		return language;
	}

	public int getNextFreeReplaceChar()
	{
		return highestReplaceChar + 1;
	}

	/**
	 * Get the replacement string for a char
	 * 
	 * @param c
	 * @return the replaced string
	 */
	public String getReplacedElement(char c)
	{
		return elementMap.get(c + "");
	}

	public Vector<TermTagObjectMatch> getTermTagObjectMatchVector()
	{
		return termTagObjectMatchVector;
	}

	public TermTagObjectTable getTermTagObjectTable()
	{
		return termTagObjectTable;
	}

	public WordHandling getWordHandling()
	{
		return wordHandling;
	}

	public String getXliffString()
	{
		return xliffString;
	}

	private char incrementHighestReplaceChar()
	{
		highestReplaceChar++;
		return highestReplaceChar;
	}

	/**
	 * @param string
	 * @param doc
	 * @return
	 */
	public boolean isValidElement(String string, XliffDocument doc)
	{
		String xmlstring = decode(string);
		return doc.isValidElement(xmlstring);
	}

	/**
	 * @param string
	 * @param doc
	 * @return
	 */
	public boolean isValidElementOrText(String string, XliffDocument doc)
	{
		String xmlstring = "<yy>" + decode(string) + "</yy>"; // just a dummy
																// tag around
																// it...
		if (xmlstring.indexOf('<') == -1)
			return true;
		return doc.isValidElement(xmlstring);
	}

	public void setDecodedXliffString(String decodedXliffString)
	{
		this.decodedXliffString = decodedXliffString;
	}

	public void setElementMap(Hashtable<String, String> elementMap)
	{
		this.elementMap = elementMap;
	}

	public void setEncodedXliffString(String encodedXliffString)
	{
		this.encodedXliffString = encodedXliffString;
	}

	public void setHighestReplaceChar(char highestReplaceChar)
	{
		this.highestReplaceChar = highestReplaceChar;
	}

	public void setLanguage(String language)
	{
		this.language = language;
	}

	public void setTermTagObjectMatchVector(Vector<TermTagObjectMatch> termTagObjectMatchVector)
	{
		this.termTagObjectMatchVector = termTagObjectMatchVector;
	}

	public void setTermTagObjectTable(TermTagObjectTable termTagObjectTable)
	{
		this.termTagObjectTable = termTagObjectTable;
	}

	public void setWordHandling(WordHandling wordHandling)
	{
		this.wordHandling = wordHandling;
	}

	public void setXliffString(String xliffString)
	{
		this.xliffString = xliffString;
	}

	public String stringify(String string)
	{
		String resultString = string;
		resultString = resultString.replaceAll(getStopReplaceChar() + "", "[" + (int) getStopReplaceChar() + "]");
		Enumeration<String> enumKeys = elementMap.keys();
		while (enumKeys.hasMoreElements())
		{
			String key = enumKeys.nextElement();
			String value = elementMap.get(key);
			resultString = resultString.replaceAll(key, "[" + (int) key.charAt(0) + "=\"" + value + "\"]");
		}
		resultString = resultString.replaceAll(XliffElementHandler.getFinalReplaceChar() + "", "["
				+ (int) XliffElementHandler.getFinalReplaceChar() + "\"]");
		return resultString;
	}
}
