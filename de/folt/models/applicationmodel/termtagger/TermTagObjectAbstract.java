package de.folt.models.applicationmodel.termtagger;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import de.folt.models.documentmodel.xliff.XliffElementHandler;
import de.folt.util.WordHandling;

public class TermTagObjectAbstract
{

	public static int countOccurrences(String haystack, char needle)
	{
		int count = 0;
		for (char c : haystack.toCharArray())
		{
			if (c == needle)
			{
				++count;
			}
		}
		return count;
	}

	protected Hashtable<String, String>	attributes					= new Hashtable<String, String>();

	protected boolean					bSource						= true;

	protected String					encodedXliffString;

	protected String					language					= null;

	protected long[]					longArrayRepresentation		= null;

	protected String					longLCRepresentation		= "";

	protected String					longRepresentation			= "";

	protected String					longStemmedRepresentation	= "";

	protected String					term						= null;

	protected String					termBaseForm				= null;

	// id of tig -> term -> id
	protected String					termElementID				= "";								// term

	protected String					termID						= "";

	protected String					termLowercase				= null;

	protected TermTagObjectAbstract		translation					= null;

	protected String					uniqueID					= "";

	protected WordHandling				wordHandling				= new WordHandling();

	protected Vector<String>			wordsInEncodedVector		= null;

	public TermTagObjectAbstract()
	{
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param language
	 * @param term
	 */
	public TermTagObjectAbstract(String language, String term)
	{
		super();
		this.language = language;
		this.term = term;
		this.termBaseForm = WordHandling.stem(term, language);
		this.setTermLowercase(term.toLowerCase());
	}

	/**
	 * @param language
	 * @param term
	 * @param termID
	 * @param uniqueID
	 */
	public TermTagObjectAbstract(String language, String term, String termID, String uniqueID)
	{
		super();
		this.language = language;
		this.term = term;
		this.termID = termID;
		this.uniqueID = uniqueID;
		this.setTermLowercase(term.toLowerCase());
		this.termBaseForm = WordHandling.stem(term, language);

	}

	/**
	 * @param term
	 * @param language
	 * @param termID
	 * @param uniqueID
	 * @param bSource
	 */
	public TermTagObjectAbstract(String term, String language, String termID, String uniqueID, boolean bSource)
	{
		super();
		this.bSource = bSource;
		this.language = language;
		this.term = term;
		this.termID = termID;
		this.uniqueID = uniqueID;
		this.setTermLowercase(term.toLowerCase());
		this.termBaseForm = WordHandling.stem(term, language);
	}

	/**
	 * @param term
	 * @param language
	 * @param termID
	 * @param uniqueID
	 * @param longRepresentation
	 * @param bSource
	 */
	public TermTagObjectAbstract(String term, String language, String termID, String uniqueID, String longRepresentation,
			boolean bSource, WordHandling wordHandling)
	{
		super();
		this.bSource = bSource;
		this.language = language;
		this.term = term;
		this.termID = termID;
		this.uniqueID = uniqueID;
		this.longRepresentation = longRepresentation;
		this.setTermLowercase(term.toLowerCase());
		this.termBaseForm = WordHandling.stem(term, language);
		this.wordHandling = wordHandling;

		XliffElementHandler XliffElementHandler = new XliffElementHandler(this.wordHandling);
		encodedXliffString = XliffElementHandler.encode(term);
		encodedXliffString = XliffElementHandler.encodeSpaceChars(encodedXliffString);
		encodedXliffString = XliffElementHandler.encodeWordSplitChars(encodedXliffString);

		wordsInEncodedVector = new Vector<String>();
		int i = 0;
		StringBuffer buf = new StringBuffer();
		while (i < encodedXliffString.length())
		{

			if (encodedXliffString.charAt(i) < de.folt.models.documentmodel.xliff.XliffElementHandler
					.getStartReplaceChar())
			{
				buf.append(encodedXliffString.charAt(i));
			}
			else
			{
				wordsInEncodedVector.add(buf.toString());
				wordsInEncodedVector.add(encodedXliffString.charAt(i) + "");
				buf = new StringBuffer();
			}
			i++;
		}

		if (buf.length() > 0)
		{
			wordsInEncodedVector.add(buf.toString());
		}

	}

	public boolean addAttribute(String key, String value)
	{
		if (attributes.containsKey(key))
			return false;
		this.attributes.put(key, value);
		return true;
	}

	public boolean containsKey(String key)
	{
		return attributes.containsKey(key);
	}

	public boolean containsValue(String value)
	{
		return attributes.containsValue(value);
	}

	/**
	 * @param string
	 * @return
	 */
	public String format(String comment, String string)
	{
		return comment + " \"" + string + "\": ";
	}

	/**
	 * @param att
	 * @return
	 */
	public String formatAttribute(String att)
	{
		String ret = "";
		Enumeration<String> keys = attributes.keys();
		while (keys.hasMoreElements())
		{
			String key = keys.nextElement();
			if (key.equals(att))
			{
				ret = key + ":" + attributes.get(key);
				return ret;
			}
		}

		return ret;
	}

	/**
	 * @return
	 */
	public String formatAttributes()
	{
		String ret = "\"Attributes:";
		Enumeration<String> keys = attributes.keys();
		while (keys.hasMoreElements())
		{
			String key = keys.nextElement();
			ret = ret + " " + key + ":" + attributes.get(key);
		}

		return ret + "\"";
	}

	public String getAttribute(String key)
	{
		return attributes.get(key);
	}

	public Hashtable<String, String> getAttributes()
	{
		return attributes;
	}

	public String getEncodedXliffString()
	{
		return encodedXliffString;
	}

	public String getLanguage()
	{
		return language;
	}

	public long[] getLongArrayRepresentation()
	{
		return longArrayRepresentation;
	}

	public String getLongLCRepresentation()
	{
		return longLCRepresentation;
	}

	public String getLongRepresentation()
	{
		return longRepresentation;
	}

	public String getLongStemmedRepresentation()
	{
		return longStemmedRepresentation;
	}

	public String getTerm()
	{
		return term;
	}

	public String getTermBaseForm()
	{
		return termBaseForm;
	}

	public String getTermElementID()
	{
		return termElementID;
	}

	public String getTermID()
	{
		return termID;
	}

	public String getTermLowercase()
	{
		return termLowercase;
	}

	public TermTagObjectAbstract getTranslation()
	{
		return translation;
	}

	public String getUniqueID()
	{
		return uniqueID;
	}

	public WordHandling getWordHandling()
	{
		return wordHandling;
	}

	public Vector<String> getWordsInEncodedVector()
	{
		return wordsInEncodedVector;
	}

	public boolean isbSource()
	{
		return bSource;
	}

	public boolean removeAttribute(String key, String value)
	{
		if (!attributes.containsKey(key))
			return false;
		this.attributes.remove(key);
		return true;
	}

	public void setAttributes(Hashtable<String, String> attributes)
	{
		this.attributes = attributes;
	}

	public void setbSource(boolean bSource)
	{
		this.bSource = bSource;
	}

	public void setEncodedXliffString(String encodedXliffString)
	{
		this.encodedXliffString = encodedXliffString;
	}

	public void setLanguage(String language)
	{
		this.language = language;
	}

	public void setLongArrayRepresentation(long[] longArrayRepresentation)
	{
		this.longArrayRepresentation = longArrayRepresentation;
	}

	public void setLongLCRepresentation(String longLCRepresentation)
	{
		this.longLCRepresentation = longLCRepresentation;
	}

	public void setLongRepresentation(String longRepresentation)
	{
		this.longRepresentation = longRepresentation;
	}

	public void setLongStemmedRepresentation(String longStemmedRepresentation)
	{
		this.longStemmedRepresentation = longStemmedRepresentation;
	}

	public void setTerm(String term)
	{
		this.term = term;
	}

	public void setTermBaseForm(String termBaseForm)
	{
		this.termBaseForm = termBaseForm;
	}

	public void setTermElementID(String termElementID)
	{
		this.termElementID = termElementID;
	}

	public void setTermID(String termID)
	{
		this.termID = termID;
	}

	public void setTermLowercase(String termLowercase)
	{
		this.termLowercase = termLowercase;
	}

	public void setTranslation(TermTagObjectAbstract translation)
	{
		this.translation = translation;
	}

	public void setUniqueID(String uniqueID)
	{
		this.uniqueID = uniqueID;
	}

	public void setWordHandling(WordHandling wordHandling)
	{
		this.wordHandling = wordHandling;
	}

	public void setWordsInEncodedVector(Vector<String> wordsInEncodedVector)
	{
		this.wordsInEncodedVector = wordsInEncodedVector;
	}

	/**
	 * @return
	 */
	public String stringify()
	{
		String ret = format("term", this.term) + format("language", this.language)
				+ format("termBaseForm", this.termBaseForm) + format("termLowercase", this.termLowercase)
				+ format("longRepresentation", this.longRepresentation)
				+ format("longStemmedRepresentation", this.longStemmedRepresentation)
				+ format("longLCRepresentation", this.longLCRepresentation) + format("uniqueID", this.uniqueID)
				+ format("termID", this.termID + " " + formatAttributes());
		if (this.translation != null)
		{
			ret = ret + "\n\tTranslation: " + this.stringifyTranslation();
		}
		return ret;
	}

	/**
	 * @return
	 */
	public String stringifyTranslation()
	{
		String ret = format("term", this.translation.term) + format("language", this.translation.language)
				+ format("termBaseForm", this.translation.termBaseForm)
				+ format("termLowercase", this.translation.termLowercase)
				+ format("longRepresentation", this.translation.longRepresentation)
				+ format("longStemmedRepresentation", this.translation.longStemmedRepresentation)
				+ format("longLCRepresentation", this.translation.longLCRepresentation)
				+ format("uniqueID", this.translation.uniqueID)
				+ format("termID", this.translation.termID + " " + this.translation.formatAttributes());
		return ret;
	}

}
