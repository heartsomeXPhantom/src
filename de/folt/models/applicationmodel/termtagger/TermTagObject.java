package de.folt.models.applicationmodel.termtagger;

import java.util.Vector;

import de.folt.models.documentmodel.xliff.XliffElementHandler;
import de.folt.util.WordHandling;

public class TermTagObject extends TermTagObjectAbstract implements Comparable<TermTagObject>
{

	public static boolean	bCompareStringMode	= false;

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

	public static boolean isbCompareStringMode()
	{
		return bCompareStringMode;
	}

	public static void setbCompareStringMode(boolean bCompareStringMode)
	{
		TermTagObject.bCompareStringMode = bCompareStringMode;
	}

	public TermTagObject()
	{
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param language
	 * @param term
	 */
	public TermTagObject(String language, String term)
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
	public TermTagObject(String language, String term, String termID, String uniqueID)
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
	public TermTagObject(String term, String language, String termID, String uniqueID, boolean bSource)
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
	public TermTagObject(String term, String language, String termID, String uniqueID, String longRepresentation,
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

	@Override
	public int compareTo(TermTagObject o)
	{
		// 1 if o smaller than this, 0 if equal, -1 if o greater than this
		// the longest matches should come first

		if (bCompareStringMode)
		{
			// prefer translated objects
			if ((this.getTranslation() == null) && (o.getTranslation() != null))
				return 1;
			if ((o.getTranslation() == null) && (this.getTranslation() != null))
				return -1;

			// next sort on term length
			if (this.term.length() > o.term.length())
				return -1;
			if (this.term.length() < o.term.length())
				return 1;
			return 0;
		}
		int is = countOccurrences(this.longRepresentation, '!');
		int it = countOccurrences(o.longRepresentation, '!');
		if (is > it)
			return -1;
		else if (is == it)
		{
			if (this.term.length() > o.term.length())
				return -1;
			if (this.term.length() < o.term.length())
				return 1;
			return 0;
		}
		return 1;
	}

}
