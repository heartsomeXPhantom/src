package de.folt.models.datamodel;

import java.util.Enumeration;
import java.util.Hashtable;
import org.jdom.Element;

import de.folt.models.documentmodel.xliff.XliffDocument;

/**
 * This class contains statistic data for a match. Currently it has two main
 * properties:<br />
 * Basic idea: A translation was found for a source - target language pair. Now
 * additional statistics are computed: First how many translations does this
 * source segment have in languages except the target language. This is done for
 * all "non target languages"<br />
 * targetMatches<br />
 * iNumberOfMatchingSourcesForTarget - this number contains the number of source
 * segments for all found target segments (inverse counter). Thus if a target
 * segment has more than one corresponding source segment (except the found) one
 * this value is incremented.<br />
 * specifity - specifity of a translation - 0 (bad) - 1 (excellent) - the less
 * target translation a source has the better; the less alternative source
 * translation the better; the less traget - other language number translation
 * the better; more "target languages" the better; a further extension could
 * deal with looking at similarity matches too to improve this quality measure
 * for a whole tm database
 * 
 * @author klemens
 * 
 */
public class TranslationStatistics
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub

	}

	private double atnWeight = 0.1;
	private int iNumberOfMatchingSourcesForTarget = 0;
	private double snWeight = 0.4;

	private double specifity = 0;
	private Hashtable<String, Integer> targetMatches = new Hashtable<String, Integer>();
	private double tnWeight = 0.4;
	private XliffDocument xliffDocument;

	/**
	 * Generate an empty statistics instance
	 */
	public TranslationStatistics()
	{
		super();
		iNumberOfMatchingSourcesForTarget = 0;
		targetMatches = new Hashtable<String, Integer>();
	}

	/**
	 * Generate an empty statistics instance
	 */
	public TranslationStatistics(XliffDocument xliffDocument)
	{
		super();
		iNumberOfMatchingSourcesForTarget = 0;
		targetMatches = new Hashtable<String, Integer>();
		this.xliffDocument = xliffDocument;
	}

	/**
	 * Set the number of matching sources for the targets found for match
	 * 
	 * @param language
	 *            the "other" target languages
	 * @param iNumber
	 *            the number of matches
	 */
	public void addTargetStatistics(String language, int iNumber)
	{
		targetMatches.put(language, iNumber);
	}

	/**
	 * Compute the specifity of the translation
	 * 
	 * @return the specifity of the translation
	 */
	public double computeSpecifity()
	{

		try
		{
			specifity = 1 / (float) iNumberOfMatchingSourcesForTarget * this.tnWeight;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		if (xliffDocument != null)
		{
			try
			{
				specifity = specifity + 1 / (float) targetMatches.get(xliffDocument.getSourceLanguage())
						* this.snWeight;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		try
		{
			Enumeration<String> values = targetMatches.keys();
			while (values.hasMoreElements())
			{
				String lang = values.nextElement();
				int iNumber = targetMatches.get(lang);
				specifity = specifity + 1 / (float) iNumber * this.atnWeight / (float) targetMatches.size();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return this.specifity;
	}

	public double getAtnWeight()
	{
		return atnWeight;
	}

	public int getiNumberOfMatchingSourcesForTarget()
	{
		return iNumberOfMatchingSourcesForTarget;
	}

	/**
	 * Get the number of target - source language matches
	 * 
	 * @return the number of source segments which are translations of the
	 *         target segment
	 */
	public int getNumberOfMatchingSourcesForTarget()
	{
		return iNumberOfMatchingSourcesForTarget;
	}

	public double getSnWeight()
	{
		return snWeight;
	}

	public double getSpecifity()
	{
		return specifity;
	}

	/**
	 * Get the number hashtable for the source - target matches independently of
	 * the language
	 * 
	 * @return the hashtable for the source - target matches independently of
	 *         the language
	 */
	public Hashtable<String, Integer> getTargetMatches()
	{
		return targetMatches;
	}

	/**
	 * Get the matching number for this source segment given the source language
	 * 
	 * @param language
	 *            the language to search for
	 * @return the number of matching segments for this source segment given the
	 *         source language
	 */
	public int getTargetMatches(String language)
	{
		if ((language == null) || (language == ""))
		{
			int iNumber = 0;
			Enumeration<Integer> values = targetMatches.elements();
			while (values.hasMoreElements())
			{
				iNumber = iNumber + values.nextElement();
			}
			return iNumber;
		}
		return targetMatches.get(language);
	}

	public double getTnWeight()
	{
		return tnWeight;
	}

	/**
	 * Increase the number of target - source matches
	 */
	public void incNumberOfMatchingSourcesForTarget()
	{
		this.iNumberOfMatchingSourcesForTarget++;
	}

	/**
	 * Increment the target language number for a given target language
	 * 
	 * @param language
	 *            the language to increment for
	 */
	public void incTargetStatistics(String language)
	{
		int iNumber = 1;
		if (targetMatches.containsKey(language))
			iNumber = targetMatches.get(language);
		targetMatches.put(language, iNumber);
	}

	public void setAtnWeight(double atnWeight)
	{
		this.atnWeight = atnWeight;
	}

	public void setiNumberOfMatchingSourcesForTarget(int iNumberOfMatchingSourcesForTarget)
	{
		this.iNumberOfMatchingSourcesForTarget = iNumberOfMatchingSourcesForTarget;
	}

	/**
	 * @param numberOfMatchingSourcesForTarget
	 */
	public void setNumberOfMatchingSourcesForTarget(int numberOfMatchingSourcesForTarget)
	{
		this.iNumberOfMatchingSourcesForTarget = numberOfMatchingSourcesForTarget;
	}

	public void setSnWeight(double snWeight)
	{
		this.snWeight = snWeight;
	}

	public void setSpecifity(double specifity)
	{
		this.specifity = specifity;
	}

	public void setSpecifity(float specifity)
	{
		this.specifity = specifity;
	}

	/**
	 * @param targetMatches
	 */
	public void setTargetMatches(Hashtable<String, Integer> targetMatches)
	{
		this.targetMatches = targetMatches;
	}

	public void setTnWeight(double tnWeight)
	{
		this.tnWeight = tnWeight;
	}

	/**
	 * Create a prop-group representation of the Statistics
	 * 
	 * @param xliffDocument
	 *            the cliffDocument for the Statistics
	 * @return a prop-group with the statistics
	 */
	public Element toProp()
	{
		Element propGroup = new Element("prop-group", xliffDocument.getNamespace());
		try
		{
			propGroup.setAttribute("name", "BeoRec:TranslationStatistics");

			Element prop = new Element("prop", xliffDocument.getNamespace());
			prop.setAttribute("name", "BeoRec:sourceMatches");
			prop.setAttribute("xml:lang", xliffDocument.getSourceLanguage());
			prop.setText(this.getNumberOfMatchingSourcesForTarget() + "");
			propGroup.addContent(prop);

			prop = new Element("prop", xliffDocument.getNamespace());
			prop.setAttribute("name", "BeoRec:specifity");
			prop.setAttribute("xml:lang", xliffDocument.getSourceLanguage());
			prop.setText(this.computeSpecifity() + "");
			propGroup.addContent(prop);

			Enumeration<String> values = targetMatches.keys();
			while (values.hasMoreElements())
			{
				String lang = values.nextElement();
				int iNumber = targetMatches.get(lang);
				prop = new Element("prop", xliffDocument.getNamespace());
				prop.setAttribute("prop-type", "BeoRec:target");
				prop.setAttribute("xml:lang", lang);
				prop.setText(iNumber + "");
				propGroup.addContent(prop);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return propGroup;
	}
}
