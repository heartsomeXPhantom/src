/*
 * Created on 13.07.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.models.datamodel.phrasetranslate;

import java.io.File;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.jdom.Element;
import org.jdom.Namespace;

import de.folt.models.datamodel.BasicDataSource;
import de.folt.models.datamodel.DataSource;
import de.folt.models.datamodel.DataSourceConfigurations;
import de.folt.models.datamodel.DataSourceInstance;
import de.folt.models.datamodel.DataSourceProperties;
import de.folt.models.datamodel.LinguisticProperties;
import de.folt.models.datamodel.MonoLingualObject;
import de.folt.models.datamodel.MultiLingualObject;
import de.folt.models.documentmodel.xliff.XliffDocument;
import de.folt.util.OpenTMSException;

/**
 * Class implements a phrase translator (PT).<br>
 * Basic idea: A PT is a structure source with source language && target with
 * target language<br>
 * The PTs are added thru an add function. The source PT is segmented into
 * words. Each word gets an unique long value. A hash table <string> - <long>
 * identifies the mapping.<br>
 * From that a hash table is filled with a Long array and as target the
 * translation. The array is mapping of the word in the word array to the long
 * array (actually mapped to a String....<br>
 * Example:
 * 
 * <pre>
 * This is openTMS.
 * gives
 * [is] [openTMS] >>> ist openTMS.
 *   1      2
 * </pre>
 * 
 * Word -> long hash table
 * 
 * <pre>
 *   is      -> 1
 *   openTms -> 2 
 *   a       -> 3 (latter adding a >>> ein)
 *   initiative -> 5 (latter adding initiative >>> Initiative)
 *   ...
 * </pre>
 * 
 * Phrase hash table table
 * 
 * <pre>
 *   [1][2] >>> ist openTMS
 *     key             value
 * </pre>
 * 
 * Searching for "This is openTMS - a folt initiative." Convert to word array (0
 * indicates unknown word)
 * 
 * <pre>
 *  [This] [is] [openTMS] [a] [folt] [initiative]
 *    0     1      2       3    0         5
 * </pre>
 * 
 * Now search creates all substring which do not contain a 0.
 * 
 * <pre>
 *  [1] [2] [3]
 *  [2] [3]
 *  [5]
 * </pre>
 * 
 * and and now seach thru the hash table to find possible candidates resulting a
 * match set:
 * 
 * <pre>
 *  [1][2] >>> ist openTMS
 *  [5] >>> Initiative
 * 
 * 
 * @author klemens
 * 
 */
public class PhraseTranslate
{

	/**
	 * main testing the phrase translator functions
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		PhraseTranslate phraseTranslate = new PhraseTranslate("de", "en");
		if (args.length == 0)
		{
			try
			{
				phraseTranslate.bAddPhrase("gehen nach Hause", "go home");
				phraseTranslate.bAddPhrase("Haus", "home");
				phraseTranslate.bAddPhrase("openTMS ist wunderbar.", "openTMS is wonderful.");
				phraseTranslate.bAddPhrase("gehen", "go");
				phraseTranslate.bAddPhrase("gehen", "go");
				phraseTranslate.bAddPhrase("gehen", "walk");
				phraseTranslate.bAddPhrase("gehen", "walk");
				phraseTranslate.bAddPhrase("gehen", "ride");
				phraseTranslate.bAddPhrase("gehen", "go");
				phraseTranslate.bAddPhrase("nach", "after");
				phraseTranslate.bAddPhrase("nach", "afterwards");
				phraseTranslate.bAddPhrase("ist", "ist");
				phraseTranslate.bAddPhrase("ist", "ist");
				Vector<PhraseTranslateResult> result = phraseTranslate.findTranslation("Haus");
				phraseTranslate.printPhraseTranslateResults("Haus", result);
				result = phraseTranslate.findTranslation("Wir gehen nach Hause");
				phraseTranslate.printPhraseTranslateResults("Wir gehen nach Hause", result);
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
		else if (args.length == 1) // data source
		{
			String dataSourceName = args[0];
			// get the type of the database
			BasicDataSource basicdata = new BasicDataSource();
			String configFile = basicdata.getDefaultDataSourceConfigurationsFileName();
			File f = new File(configFile);
			String dataSourceType = "";
			if (f.exists())
			{
				DataSourceConfigurations config = new DataSourceConfigurations(configFile);
				dataSourceType = config.getDataSourceType(dataSourceName);
				if (dataSourceType == null)
					dataSourceType = "";
				DataSourceProperties model = new DataSourceProperties();
				model.put("dataModelClass", dataSourceType);
				model.put("dataSourceName", dataSourceName);
				model.put("dataSourceConfigurationsFile", configFile);
				try
				{
					DataSource datasource = DataSourceInstance.createInstance(dataSourceType + ":" + dataSourceName,
							model);
					phraseTranslate.bAddPhrases(datasource, "de", "en");
					datasource.initEnumeration();
					int i = 0;
					while (datasource.hasMoreElements())
					{
						MultiLingualObject multi = datasource.nextElement();
						Vector<MonoLingualObject> monossource = multi.getMonoLingualObjectsAsVector("de");
						if ((monossource.size() > 1) || (monossource.size() <= 0))
							continue;
						String sourcePhrase = monossource.get(0).getPlainTextSegment();
						Vector<PhraseTranslateResult> result = phraseTranslate.findTranslation(sourcePhrase);
						phraseTranslate.printPhraseTranslateResults(sourcePhrase, result);
						i++;
					}
					System.out.println("Phrases read = " + i);
				}
				catch (OpenTMSException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	private boolean bBilingualMode = false;

	private boolean bStoreMonoLingualTerms = false;

	private boolean bStoreUniqueId = false;

	private long currentWordCounter = 1l;

	private Hashtable<String, String> linguisticPropertyTable = new Hashtable<String, String>();

	private Hashtable<String, Boolean> longArrayToMatch;

	private Hashtable<String, String> longArrayToSource;

	private Hashtable<String, String> longArrayToTranslation;

	private Hashtable<String, Long> longWordHash;

	private char sepChar = new PhraseTranslateResult().getSepChar();

	private String sourceLanguage;

	private String sourceTargetSepString = new PhraseTranslateResult().getSourceTargetSepString();

	private Hashtable<String, String> sourceTargetUniqueIds;

	private String storedLinguisticProperties = null;

	private String[] storedLinguisticPropertiesArray = null;

	private String targetLanguage;

	private String targetPhraseSeperator = "|";

	private PhraseTranslate targetPhraseTranslate = null;

	/**
	 * @param sourceLanguage
	 * @param targetLanguage
	 */
	public PhraseTranslate(String sourceLanguage, String targetLanguage)
	{
		super();
		this.sourceLanguage = sourceLanguage;
		this.targetLanguage = targetLanguage;
		longWordHash = new Hashtable<String, Long>();
		longArrayToTranslation = new Hashtable<String, String>();
		longArrayToSource = new Hashtable<String, String>();
		longArrayToMatch = new Hashtable<String, Boolean>();
		sourceTargetUniqueIds = new Hashtable<String, String>();
		currentWordCounter = 1l;
	}

	/**
	 * @param sourceLanguage
	 * @param targetLanguage
	 */
	public PhraseTranslate(String sourceLanguage, String targetLanguage, boolean bBilingualMode)
	{
		super();
		this.sourceLanguage = sourceLanguage;
		this.targetLanguage = targetLanguage;
		longWordHash = new Hashtable<String, Long>();
		longArrayToTranslation = new Hashtable<String, String>();
		longArrayToSource = new Hashtable<String, String>();
		longArrayToMatch = new Hashtable<String, Boolean>();
		sourceTargetUniqueIds = new Hashtable<String, String>();
		currentWordCounter = 1l;
		this.bBilingualMode = bBilingualMode;
		targetPhraseTranslate = new PhraseTranslate(targetLanguage, sourceLanguage);
	}

	/**
	 * @param sourceLanguage
	 * @param targetLanguage
	 */
	public PhraseTranslate(String sourceLanguage, String targetLanguage, boolean bBilingualMode,
			boolean bStoreMonoLingualTerms)
	{
		super();
		this.sourceLanguage = sourceLanguage;
		this.targetLanguage = targetLanguage;
		longWordHash = new Hashtable<String, Long>();
		longArrayToTranslation = new Hashtable<String, String>();
		longArrayToSource = new Hashtable<String, String>();
		longArrayToMatch = new Hashtable<String, Boolean>();
		sourceTargetUniqueIds = new Hashtable<String, String>();
		currentWordCounter = 1l;
		this.bBilingualMode = bBilingualMode;
		targetPhraseTranslate = new PhraseTranslate(targetLanguage, sourceLanguage);
		this.bStoreMonoLingualTerms = bStoreMonoLingualTerms;
		targetPhraseTranslate.bStoreMonoLingualTerms = bStoreMonoLingualTerms;
	}

	/**
	 * addToTransUnit adds a vector of PhraseTranslateResult to a transUnit
	 * 
	 * @param transUnit
	 *            the transUnit
	 * @param result
	 *            the vector of PhraseTranslateResult
	 * @param dataSource
	 * @return the modified transUnit
	 */
	public Element addToTransUnit(Element transUnit, Vector<PhraseTranslateResult> result, String sourceLanguage,
			String targetLanguage, DataSource dataSource)
	{
		XliffDocument xliff = new XliffDocument();
		for (int i = 0; i < result.size(); i++)
		{
			PhraseTranslateResult res = result.get(i);

			if (bExistPhraseTranslateResult(res, xliff.getTransUnitPhraseEntries(transUnit)))
				continue;

			Element propgroup = new Element("prop-group");
			propgroup.setAttribute("name", "subSegmentTranslate:" + dataSource.getDataSourceName());
			Element sourceprop = new Element("prop");
			sourceprop.setAttribute("lang", sourceLanguage, Namespace.XML_NAMESPACE);
			sourceprop.setAttribute("prop-type", "source");
			sourceprop.setText(res.getSourcePhrase());
			Element targetprop = new Element("prop");
			targetprop.setAttribute("lang", targetLanguage, Namespace.XML_NAMESPACE);
			targetprop.setAttribute("prop-type", "target");
			targetprop.setText(res.getTargetPhrase());
			propgroup.addContent(sourceprop);
			propgroup.addContent(targetprop);
			transUnit.addContent(propgroup);
		}
		return transUnit;
	}

	/**
	 * bAddPhrase add a phrase and its translation to the database
	 * 
	 * @param sourcePhrase
	 *            the source phrase to add
	 * @param targetPhrase
	 *            the target phrase to add
	 * @return
	 */
	public boolean bAddPhrase(String sourcePhrase, String targetPhrase)
	{
		return bAddPhrase(sourcePhrase, null, targetPhrase, null);
	}

	/**
	 * bAddPhrase add a phrase and its translation to the database
	 * 
	 * @param sourcePhrase
	 *            the source phrase to add
	 * @param uniqueIdSource
	 *            the source id to add
	 * @param targetPhrase
	 *            the target phrase to add
	 * @param uniqueIdTarget
	 *            the target id to add
	 * @return
	 */
	public boolean bAddPhrase(String sourcePhrase, String uniqueIdSource, String targetPhrase, String uniqueIdTarget)
	{
		boolean bAdded = false;
		// split into words

		String[] source = new de.folt.util.WordHandling().segmentToWordArray(sourcePhrase);
		String longs = "";
		for (int i = 0; i < source.length; i++)
		{
			Long lkey = 0l;
			if (longWordHash.containsKey(source[i]))
			{
				lkey = longWordHash.get(source[i]);
			}
			else
			{
				longWordHash.put(source[i], new Long(currentWordCounter));
				lkey = currentWordCounter;
				currentWordCounter++;
			}
			longs = longs + sepChar + lkey;
		}

		// just one translation allowed
		if (!longArrayToTranslation.containsKey(longs))
		{
			longArrayToTranslation.put(longs, targetPhrase);
			longArrayToSource.put(longs, sourcePhrase);
			if ((uniqueIdSource != null) && (uniqueIdTarget != null))
				sourceTargetUniqueIds.put(sourcePhrase + sourceTargetSepString + targetPhrase, uniqueIdSource
						+ sourceTargetSepString + uniqueIdTarget);
			bAdded = true;
		}
		// here we go for multiple translations
		else
		{
			String currentTargetPhrases = longArrayToTranslation.get(longs);
			// not so good could be made much faster - to be changed in the
			// future
			boolean bContained = currentTargetPhrases.equals(targetPhrase);
			if (bContained == false)
			{
				bContained = currentTargetPhrases
						.contains(targetPhraseSeperator + targetPhrase + targetPhraseSeperator);

				if (bContained == false)
				{
					bContained = currentTargetPhrases.startsWith(targetPhrase + targetPhraseSeperator);
					if (bContained == false)
					{
						bContained = currentTargetPhrases.endsWith(targetPhraseSeperator + targetPhrase);
					}
				}
			}
			if (!bContained)
			{
				// this is simple for the moment; we assume phrases do not
				// contain any ";"
				currentTargetPhrases = currentTargetPhrases + targetPhraseSeperator + targetPhrase;
				longArrayToTranslation.put(longs, currentTargetPhrases);
				if ((uniqueIdSource != null) && (uniqueIdTarget != null))
					sourceTargetUniqueIds.put(sourcePhrase + "::" + targetPhrase, uniqueIdSource + "::"
							+ uniqueIdTarget);
				bAdded = true;
			}
		}

		if (bBilingualMode)
		{
			this.getTargetPhraseTranslate().bAddPhrase(targetPhrase, uniqueIdTarget, sourcePhrase, uniqueIdSource);
		}

		return bAdded;
	}

	/**
	 * bAddPhrases add phrases from a data source
	 * 
	 * @param datasource
	 *            the data source to use
	 * @param sourceLanguage
	 *            the source language of the phrases
	 * @param targetLanguage
	 *            the target language of the phrases
	 * @return the number of phrases read
	 */
	public int bAddPhrases(DataSource datasource, String sourceLanguage, String targetLanguage)
	{
		datasource.initEnumeration();
		int i = 0;
		while (datasource.hasMoreElements())
		{
			MultiLingualObject multi = datasource.nextElement();
			Vector<MonoLingualObject> monossource = multi.getMonoLingualObjectsAsVector(sourceLanguage);
			Vector<MonoLingualObject> monostarget = multi.getMonoLingualObjectsAsVector(targetLanguage);
			for (int j = 0; j < monossource.size(); j++)
			{
				for (int k = 0; k < monostarget.size(); k++)
				{
					if (this.bStoreUniqueId == true)
					{
						this.bAddPhrase(monossource.get(j).getPlainTextSegment(), monossource.get(j).getUniqueID(),
								monostarget.get(k).getPlainTextSegment(), monostarget.get(k).getStUniqueID());
					}
					else
					{
						this.bAddPhrase(monossource.get(j).getPlainTextSegment(), monostarget.get(k)
								.getPlainTextSegment());
					}

					i++;
				}

				if ((this.getStoredLinguisticPropertiesArray() != null)
						&& this.getStoredLinguisticPropertiesArray().length > 0)
				{
					LinguisticProperties lingProp = monossource.get(j).getLinguisticProperties();
					for (int l = 0; l < getStoredLinguisticPropertiesArray().length; l++)
					{
						String val = lingProp.search(getStoredLinguisticPropertiesArray()[l]);
						if (val != null)
						{
							this.linguisticPropertyTable.put(monossource.get(j).getUniqueID() + sourceTargetSepString
									+ getStoredLinguisticPropertiesArray()[l], val);
						}
					}
				}

				if ((monostarget.size() == 0) && this.bStoreMonoLingualTerms)
				{
					if (this.bStoreUniqueId == true)
					{
						this.bAddPhrase(monossource.get(j).getPlainTextSegment(), monossource.get(j).getUniqueID(), "",
								"");
					}
					else
					{
						this.bAddPhrase(monossource.get(j).getPlainTextSegment(), "");
					}
				}
			}

			if (this.bBilingualMode && this.bStoreMonoLingualTerms)
			{
				// here we want to save the target terms with no translation for
				// the source language
				for (int j = 0; j < monostarget.size(); j++)
				{
					if ((monossource.size() == 0))
					{
						if (this.bStoreUniqueId == true)
						{
							this.getTargetPhraseTranslate().bAddPhrase(monostarget.get(j).getPlainTextSegment(),
									monostarget.get(j).getUniqueID(), "", "");
						}
						else
						{
							this.getTargetPhraseTranslate().bAddPhrase(monostarget.get(j).getPlainTextSegment(), "");
						}

						if ((this.getTargetPhraseTranslate().getStoredLinguisticPropertiesArray() != null)
								&& this.getTargetPhraseTranslate().getStoredLinguisticPropertiesArray().length > 0)
						{
							LinguisticProperties lingProp = monostarget.get(j).getLinguisticProperties();
							for (int l = 0; l < getTargetPhraseTranslate().getStoredLinguisticPropertiesArray().length; l++)
							{
								String val = lingProp.search(getTargetPhraseTranslate()
										.getStoredLinguisticPropertiesArray()[l]);
								if (val != null)
								{
									this.linguisticPropertyTable.put(monostarget.get(j).getUniqueID()
											+ sourceTargetSepString
											+ getTargetPhraseTranslate().getStoredLinguisticPropertiesArray()[l], val);
								}
							}
						}
					}
				}
			}
		}
		System.out.println("Phrases read = " + i);
		return i;
	}

	/**
	 * bExistPhraseTranslateResult check if a PhraseTranslateResult exists in a
	 * given vector of PhraseTranslateResults
	 * 
	 * @param resultToCheck
	 *            the PhraseTranslateResult to check if it exists
	 * @param compareResults
	 *            the vector of PhraseTranslateResults which are checked against
	 *            resultToCheck
	 * @return true if it exists, otherwise false
	 */
	public boolean bExistPhraseTranslateResult(PhraseTranslateResult resultToCheck,
			Vector<PhraseTranslateResult> compareResults)
	{
		if (resultToCheck == null)
			return false;
		if (compareResults == null)
			return false;
		for (int i = 0; i < compareResults.size(); i++)
		{
			if (resultToCheck.getSourcePhrase().equals(compareResults.get(i).getSourcePhrase()))
			{
				if (resultToCheck.getTargetPhrase().equals(compareResults.get(i).getTargetPhrase()))
				{
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * findTranslation finds a phrase translation
	 * 
	 * @param sourcePhrase
	 *            the source phrase to search
	 * @return a vector of phrase result
	 */
	public Vector<PhraseTranslateResult> findTranslation(String sourcePhrase)
	{
		String[] source = new de.folt.util.WordHandling().segmentToWordArray(sourcePhrase);
		return findTranslation(source);
	}

	/**
	 * findTranslation finds a phrase translation
	 * 
	 * @param source
	 *            the source phrase as string araya to search
	 * @return a vector of phrase result
	 */
	public Vector<PhraseTranslateResult> findTranslation(String[] source)
	{
		Vector<PhraseTranslateResult> phraseResults = new Vector<PhraseTranslateResult>();
		Long[] longs = new Long[source.length];
		for (int i = 0; i < source.length; i++)
		{
			Long lkey = 0l;
			if (longWordHash.containsKey(source[i]))
			{
				lkey = longWordHash.get(source[i]);
			}
			longs[i] = lkey;
		}

		for (int i = 0; i < longs.length; i++)
		{
			if (longs[i] == 0l)
				continue;
			Vector<Long> ptWords = new Vector<Long>();
			for (int j = i; j < longs.length; j++)
			{
				if (longs[j] == 0l)
					break;
				// search from i..j
				ptWords.add(longs[j]);
				String longsearch = "";
				for (int k = 0; k < ptWords.size(); k++)
				{
					longsearch = longsearch + sepChar + ptWords.get(k);
				}
				String translation = longArrayToTranslation.get(longsearch);
				if (translation != null)
				{
					PhraseTranslateResult phRes = new PhraseTranslateResult(i, longsearch, translation);
					phRes.setSourcePhrase(longArrayToSource.get(longsearch));
					// split multiple translations
					String targetarr[] = phRes.getTargetPhrase().split("\\|");
					for (int l = 0; l < targetarr.length; l++)
					{
						String idkey = this.sourceTargetUniqueIds.get(phRes.getSourcePhrase() + sourceTargetSepString
								+ targetarr[l]);
						if (idkey != null)
						{
							phRes.setSourceTargetUniqueId(idkey);
						}
					}
					phraseResults.add(phRes);
					longArrayToMatch.put(longsearch, Boolean.TRUE);
				}
			}
		}
		// sort phraseResults by length of long search results; longest first
		Collections.sort(phraseResults);
		return phraseResults;
	}

	/**
	 * findTranslation finds a phrase translation
	 * 
	 * @param source
	 *            a vector of strings which form a phrase
	 * @return a vector of phrase result
	 */
	public Vector<PhraseTranslateResult> findTranslation(Vector<String> source)
	{
		String[] sourceArray = (String[]) source.toArray();
		return findTranslation(sourceArray);
	}

	/**
	 * findTranslation finds a phrase translation starting from iStart till eEnd
	 * of the supplied vector
	 * 
	 * @param source
	 *            a vector of strings which form a phrase
	 * @param iStart
	 *            start position (inclusive)
	 * @param iEnd
	 *            end position (inclusive)
	 * @return a vector of phrase result
	 */
	public Vector<PhraseTranslateResult> findTranslation(Vector<String> source, int iStart, int iEnd)
	{
		String[] sourceArray = new String[iEnd - iStart + 1];

		for (int i = iStart; i <= iEnd; i++)
		{
			sourceArray[i - iStart] = source.get(i);
		}
		return findTranslation(sourceArray);
	}

	/**
	 * @return the currentWordCounter
	 */
	public long getCurrentWordCounter()
	{
		return currentWordCounter;
	}

	public Hashtable<String, String> getLinguisticPropertyTable()
	{
		return linguisticPropertyTable;
	}

	/**
	 * @return the longArrayToMatch
	 */
	public Hashtable<String, Boolean> getLongArrayToMatch()
	{
		return longArrayToMatch;
	}

	public Hashtable<String, String> getLongArrayToSource()
	{
		return longArrayToSource;
	}

	/**
	 * @return the longArrayToTranslation
	 */
	public Hashtable<String, String> getLongArrayToTranslation()
	{
		return longArrayToTranslation;
	}

	/**
	 * @return the longWordHash
	 */
	public Hashtable<String, Long> getLongWordHash()
	{
		return longWordHash;
	}

	public char getSepChar()
	{
		return sepChar;
	}

	/**
	 * @return the sourceLanguage
	 */
	public String getSourceLanguage()
	{
		return sourceLanguage;
	}

	public String getSourceTargetSepString()
	{
		return sourceTargetSepString;
	}

	public String getStoredLinguisticProperties()
	{
		return storedLinguisticProperties;
	}

	public String[] getStoredLinguisticPropertiesArray()
	{
		return storedLinguisticPropertiesArray;
	}

	/**
	 * @return the targetLanguage
	 */
	public String getTargetLanguage()
	{
		return targetLanguage;
	}

	public String getTargetPhraseSeperator()
	{
		return targetPhraseSeperator;
	}

	public PhraseTranslate getTargetPhraseTranslate()
	{
		return targetPhraseTranslate;
	}

	public boolean isbBilingualMode()
	{
		return bBilingualMode;
	}

	public boolean isbStoreMonoLingualTerms()
	{
		return bStoreMonoLingualTerms;
	}

	public boolean isbStoreUniqueId()
	{
		return bStoreUniqueId;
	}

	/**
	 * phraseTranslateResultsToGlossary write a csv separated string a glossary
	 * Element; <, >, & are converted to XML pendants
	 * 
	 * @return an array of glossaries (size 1) with the matching subsegment
	 *         matches; null if no subsegment matches were found
	 */
	public Element[] phraseTranslateResultsToGlossary()
	{
		if (longArrayToMatch.size() == 0)
			return null;
		Element[] glossaries = new Element[1];
		Element glossary = new Element("glossary");
		Element internalfile = new Element("internal-file");
		internalfile.setAttribute("form", "text");
		glossary.addContent(internalfile);
		Enumeration<String> enumKeys = this.longArrayToMatch.keys();
		String glosstext = sourceLanguage + ";" + targetLanguage + "|\n";
		while (enumKeys.hasMoreElements())
		{
			String longsearch = enumKeys.nextElement();
			glosstext = glosstext + this.longArrayToSource.get(longsearch) + ";"
					+ longArrayToTranslation.get(longsearch) + "|\n";
		}
		glosstext = glosstext.replaceAll("&", "&amp;");
		glosstext = glosstext.replaceAll("<", "&lt;");
		glosstext = glosstext.replaceAll(">", "&gt;");

		internalfile.setText(glosstext);
		glossaries[0] = glossary;
		return glossaries;
	}

	/**
	 * printPhraseTranslateResults prints a vector of PhraseTranslateResult to
	 * stdout
	 * 
	 * @param searchPhrase
	 *            the search phrase
	 * @param result
	 *            the vector of PhraseTranslateResults
	 */
	public void printPhraseTranslateResults(String searchPhrase, Vector<PhraseTranslateResult> result)
	{
		System.out.println("Search phrase: " + searchPhrase);
		for (int i = 0; i < result.size(); i++)
		{
			PhraseTranslateResult res = result.get(i);
			System.out.println(i + ":" + res.getIStartPosition() + ":" + res.getTargetPhrase());
		}
	}

	public void setbBilingualMode(boolean bBilingualMode)
	{
		this.bBilingualMode = bBilingualMode;
	}

	public void setbStoreMonoLingualTerms(boolean bStoreMonoLingualTerms)
	{
		this.bStoreMonoLingualTerms = bStoreMonoLingualTerms;
	}

	public void setbStoreUniqueId(boolean bStoreUniqueId)
	{
		this.bStoreUniqueId = bStoreUniqueId;
	}

	public void setCurrentWordCounter(long currentWordCounter)
	{
		this.currentWordCounter = currentWordCounter;
	}

	public void setLinguisticPropertyTable(Hashtable<String, String> linguisticPropertyTable)
	{
		this.linguisticPropertyTable = linguisticPropertyTable;
	}

	/**
	 * @param longArrayToMatch
	 *            the longArrayToMatch to set
	 */
	public void setLongArrayToMatch(Hashtable<String, Boolean> longArrayToMatch)
	{
		this.longArrayToMatch = longArrayToMatch;
	}

	public void setLongArrayToSource(Hashtable<String, String> longArrayToSource)
	{
		this.longArrayToSource = longArrayToSource;
	}

	/**
	 * @param longArrayToTranslation
	 *            the longArrayToTranslation to set
	 */
	public void setLongArrayToTranslation(Hashtable<String, String> longArrayToTranslation)
	{
		this.longArrayToTranslation = longArrayToTranslation;
	}

	/**
	 * @param longWordHash
	 *            the longWordHash to set
	 */
	public void setLongWordHash(Hashtable<String, Long> longWordHash)
	{
		this.longWordHash = longWordHash;
	}

	public void setSepChar(char sepChar)
	{
		this.sepChar = sepChar;
	}

	/**
	 * @param sourceLanguage
	 *            the sourceLanguage to set
	 */
	public void setSourceLanguage(String sourceLanguage)
	{
		this.sourceLanguage = sourceLanguage;
	}

	public void setSourceTargetSepString(String sourceTargetSepString)
	{
		this.sourceTargetSepString = sourceTargetSepString;
	}

	public void setStoredLinguisticProperties(String storedLinguisticProperties)
	{
		this.storedLinguisticProperties = storedLinguisticProperties;
		this.setStoredLinguisticPropertiesArray(storedLinguisticProperties.split(";"));
	}

	public void setStoredLinguisticPropertiesArray(String[] storedLinguisticPropertiesArray)
	{
		this.storedLinguisticPropertiesArray = storedLinguisticPropertiesArray;
	}

	/**
	 * @param targetLanguage
	 *            the targetLanguage to set
	 */
	public void setTargetLanguage(String targetLanguage)
	{
		this.targetLanguage = targetLanguage;
	}

	public void setTargetPhraseSeperator(String targetPhraseSeperator)
	{
		this.targetPhraseSeperator = targetPhraseSeperator;
	}

	public void setTargetPhraseTranslate(PhraseTranslate targetPhraseTranslate)
	{
		this.targetPhraseTranslate = targetPhraseTranslate;
	}
}
