package de.folt.models.applicationmodel.termtagger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import de.folt.models.applicationmodel.termtagger.TermTagObjectMatch.MATCHTYPE;
import de.folt.models.datamodel.DataSource;
import de.folt.models.datamodel.LinguisticProperties;
import de.folt.models.datamodel.LinguisticProperty;
import de.folt.models.datamodel.MonoLingualObject;
import de.folt.models.datamodel.MultiLingualObject;
import de.folt.models.datamodel.phrasetranslate.PhraseTranslateResult;
import de.folt.models.documentmodel.xliff.XliffElementHandler;
import de.folt.similarity.LevenshteinSimilarity;
import de.folt.util.Timer;
import de.folt.util.WordHandling;

public class TermTagObjectTable
{
	private boolean										bFuzzy							= false;

	private boolean										bLowercase						= false;

	private boolean										bSourceStringMatch				= false;

	private boolean										bStemmed						= false;

	private boolean										bTargetStringMatch				= false;

	private long										currentWordCounter				= 1l;

	@SuppressWarnings("unused")
	private String										endmarker						= XliffElementHandler.getStopReplaceChar() + "";

	private Timer										exactTimer						= new Timer();

	private Hashtable<String, Integer>					fuzzyMathReuseTable				= new Hashtable<String, Integer>();

	private int											fuzzyPercent					= 70;

	private Timer										fuzzyTimer						= new Timer();

	private int											iAllSourceWordCombination		= 0;

	private int											iFindTermsCalled				= 0;

	private int											ifindTermsWords					= 0;

	private int											iResultingVecSizeZero			= 0;

	private int											iSimilarityComputed				= 0;

	private int											iSimilarityMatched				= 0;

	private int											iStartFuzzyDoesNotMatch			= 0;

	private int											iVecSizeToCheck					= 0;

	private int											iWordNotFoundInLengthWordTable	= 0;

	private Timer										lcTimer							= new Timer();

	private Hashtable<String, Long>						longWordHash					= new Hashtable<String, Long>();

	private int											maxSourcePhraseLength			= 0;

	private int											maxTargetPhraseLength			= 0;

	private int											maxWordLengthSearch				= -1;

	private int											minFuzzyStartLength				= 2;

	private int											minFuzzyStringLength			= 0;

	private Vector<Integer>								phraseSourceTableSizes			= new Vector<Integer>();

	private Vector<Integer>								phraseTargetTableSizes			= new Vector<Integer>();

	private char										sepChar							= new PhraseTranslateResult().getSepChar();

	private String										sourceLanguage;

	private Hashtable<String, Vector<TermTagObject>>	sourceLCTable					= new Hashtable<String, Vector<TermTagObject>>();

	private ArrayList<TermTagObject>					sourceLengthSortedlist			= new ArrayList<TermTagObject>();

	private Hashtable<Integer, Vector<TermTagObject>>	sourceLengthTable				= new Hashtable<Integer, Vector<TermTagObject>>();

	private Hashtable<String, Vector<TermTagObject>>	sourceStemTable					= new Hashtable<String, Vector<TermTagObject>>();

	private Hashtable<String, Vector<TermTagObject>>	sourceTable						= new Hashtable<String, Vector<TermTagObject>>();

	private String										sourceTargetSepString			= new PhraseTranslateResult().getSourceTargetSepString();

	private Hashtable<Integer, Vector<TermTagObject>>	sourceWordLengthTable			= new Hashtable<Integer, Vector<TermTagObject>>();

	private Timer										stemmerTimer					= new Timer();

	private String										stmarker						= XliffElementHandler.getStartReplaceChar() + "";

	private String										storedLinguisticProperties;

	private String[]									storedLinguisticPropertiesArray;

	private String										targetLanguage;

	private Hashtable<String, Vector<TermTagObject>>	targetLCTable					= new Hashtable<String, Vector<TermTagObject>>();

	private ArrayList<TermTagObject>					targetLengthSortedlist			= new ArrayList<TermTagObject>();

	private Hashtable<Integer, Vector<TermTagObject>>	targetLengthTable				= new Hashtable<Integer, Vector<TermTagObject>>();

	private Hashtable<String, Vector<TermTagObject>>	targetStemTable					= new Hashtable<String, Vector<TermTagObject>>();

	private Hashtable<String, Vector<TermTagObject>>	targetTable						= new Hashtable<String, Vector<TermTagObject>>();

	private Hashtable<Integer, Vector<TermTagObject>>	targetWordLengthTable			= new Hashtable<Integer, Vector<TermTagObject>>();

	private WordHandling								wordHandling					= new WordHandling();

	/**
	 * @param sourceLanguage
	 * @param targetLanguage
	 */
	public TermTagObjectTable(String sourceLanguage, String targetLanguage)
	{
		super();
		this.sourceLanguage = sourceLanguage;
		this.targetLanguage = targetLanguage;
		this.fuzzyTimer = new Timer();
		this.lcTimer = new Timer();
		this.stemmerTimer = new Timer();
		this.exactTimer = new Timer();
	}

	private void addtoTable(Hashtable<String, Vector<TermTagObject>> table, String monoLongs, TermTagObject term)
	{
		if (table.containsKey(monoLongs))
		{
			table.get(monoLongs).add(term);
		}
		else
		{
			Vector<TermTagObject> obj = new Vector<TermTagObject>();
			obj.add(term);
			table.put(monoLongs, obj);
		}
	};

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
		int is = 0;
		int it = 0;
		int ist = 0;
		int dist = 0;
		while (datasource.hasMoreElements())
		{
			MultiLingualObject multi = datasource.nextElement();
			Vector<MonoLingualObject> monossource = multi.getMonoLingualObjectsAsVector(sourceLanguage, true); // wk // 12.11.2012
			Vector<MonoLingualObject> monostarget = multi.getMonoLingualObjectsAsVector(targetLanguage, true); // wk
																												// 12.11.2012
			for (int j = 0; j < monossource.size(); j++)
			{
				for (int k = 0; k < monostarget.size(); k++)
				{
					this.bAddTermPair(monossource.get(j), monostarget.get(k));
					ist++;
				}
				if (monostarget.size() == 0)
				{
					this.bAddTerm(monossource.get(j), true);
					is++;
				}
			}
			for (int k = 0; k < monostarget.size(); k++)
			{
				if (monossource.size() == 0)
				{
					this.bAddTerm(monostarget.get(k), false);
					it++;
				}
			}
			dist++;
		}
		System.out.println("Phrases read: all=" + dist);
		System.out.println("Phrases read: s/t=" + ist + " s=" + is + " t=" + it);
		return ist + is + it;
	}

	/**
	 * @param bSource
	 * @param source
	 * @return
	 */
	public boolean bAddTerm(MonoLingualObject mono, boolean bSource)
	{

		long[] monoLongs = this.createLongTermArray(mono.getPlainTextSegment(), this.sourceLanguage);

		if (bSource)
			maxSourcePhraseLength = Math.max(monoLongs.length, maxSourcePhraseLength);
		else
			maxTargetPhraseLength = Math.max(monoLongs.length, maxTargetPhraseLength);

		String monoStringLongs = createLongTermKey(monoLongs);

		String clmonoLongs = createLongTermKey(mono.getPlainTextSegment().toLowerCase());
		LinguisticProperties lingPropSource = mono.getLinguisticProperties();
		String val = lingPropSource.search("ATT:id");

		TermTagObject term = new TermTagObject(mono.getPlainTextSegment(), mono.getLanguage(), mono.getUniqueID(), val,
				monoStringLongs, mono.getLanguage() == this.sourceLanguage, this.wordHandling);

		try
		{
			if (mono.getLinguisticProperties() != null)
			{
				LinguisticProperty termid = (LinguisticProperty) mono.getLinguisticProperties().get("termelementid");
				if (termid != null)
				{
					term.setTermElementID((String) termid.getValue());
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		term.setTranslation(null);
		if ((this.getStoredLinguisticPropertiesArray() != null) && this.getStoredLinguisticPropertiesArray().length > 0)
		{

			for (int l = 0; l < getStoredLinguisticPropertiesArray().length; l++)
			{
				val = lingPropSource.search(getStoredLinguisticPropertiesArray()[l]);
				if (val != null)
				{
					term.addAttribute(getStoredLinguisticPropertiesArray()[l], val);
				}
			}
		}

		term.setLongLCRepresentation(clmonoLongs);
		String stemmonoLongs = createLongTermKey(term.getTermBaseForm());
		term.setLongStemmedRepresentation(stemmonoLongs);

		Hashtable<String, Vector<TermTagObject>> table = null;
		Hashtable<String, Vector<TermTagObject>> lctable = null;
		Hashtable<String, Vector<TermTagObject>> stemtable = null;
		if ((mono.getLanguage().equals(this.sourceLanguage) || mono.getLanguage().startsWith(this.sourceLanguage)))
		{
			table = sourceTable;
			lctable = this.sourceLCTable;
			stemtable = this.sourceStemTable;
			if (this.sourceLengthTable.containsKey(term.term.length()))
			{
				Vector<TermTagObject> vec = this.sourceLengthTable.get(term.term.length());
				vec.add(term);
				this.sourceLengthTable.put(Integer.valueOf(term.term.length()), vec);
			}
			else
			{
				Vector<TermTagObject> vec = new Vector<TermTagObject>();
				vec.add(term);
				this.sourceLengthTable.put(Integer.valueOf(term.term.length()), vec);
			}

			if (this.sourceWordLengthTable.containsKey(monoLongs.length))
			{
				Vector<TermTagObject> vec = this.sourceWordLengthTable.get(monoLongs.length);
				vec.add(term);
				this.sourceWordLengthTable.put(Integer.valueOf(monoLongs.length), vec);
			}
			else
			{
				Vector<TermTagObject> vec = new Vector<TermTagObject>();
				vec.add(term);
				this.sourceWordLengthTable.put(Integer.valueOf(monoLongs.length), vec);
			}
		}
		else
		{
			table = targetTable;
			lctable = this.targetLCTable;
			stemtable = this.targetStemTable;
			if (this.targetLengthTable.containsKey(term.term.length()))
			{
				Vector<TermTagObject> vec = this.targetLengthTable.get(term.term.length());
				vec.add(term);
				this.targetLengthTable.put(Integer.valueOf(term.term.length()), vec);
			}
			else
			{
				Vector<TermTagObject> vec = new Vector<TermTagObject>();
				vec.add(term);
				this.targetLengthTable.put(Integer.valueOf(term.term.length()), vec);
			}

			if (this.targetWordLengthTable.containsKey(monoLongs.length))
			{
				Vector<TermTagObject> vec = this.targetWordLengthTable.get(monoLongs.length);
				vec.add(term);
				this.targetWordLengthTable.put(Integer.valueOf(monoLongs.length), vec);
			}
			else
			{
				Vector<TermTagObject> vec = new Vector<TermTagObject>();
				vec.add(term);
				this.targetWordLengthTable.put(Integer.valueOf(monoLongs.length), vec);
			}
		}

		addtoTable(table, monoStringLongs, term);
		if (bStemmed) // 02.12.2013
			addtoTable(stemtable, stemmonoLongs, term);
		if (!bStemmed && bLowercase) // 02.12.2013
			addtoTable(lctable, clmonoLongs, term);

		return true;
	}

	/**
	 * @param source
	 * @param target
	 * @return
	 */
	public boolean bAddTermPair(MonoLingualObject source, MonoLingualObject target)
	{
		boolean bAdded = false;
		// split into words

		long[] sourceArrayLongs = this.createLongTermArray(source.getPlainTextSegment(), this.sourceLanguage);
		long[] targetArrayLongs = this.createLongTermArray(target.getPlainTextSegment(), this.targetLanguage);

		maxSourcePhraseLength = Math.max(sourceArrayLongs.length, maxSourcePhraseLength);
		maxTargetPhraseLength = Math.max(targetArrayLongs.length, maxTargetPhraseLength);

		String sourceLongs = createLongTermKey(sourceArrayLongs);
		String targetLongs = createLongTermKey(targetArrayLongs);
		LinguisticProperties lingPropSource = source.getLinguisticProperties();
		String val = lingPropSource.search("ATT:id");

		TermTagObject sourceTerm = new TermTagObject(source.getPlainTextSegment(), sourceLanguage,
				source.getUniqueID(), val, sourceLongs, true, this.wordHandling);
		LinguisticProperties lingPropTarget = target.getLinguisticProperties();
		val = lingPropTarget.search("ATT:id");
		TermTagObject targetTerm = new TermTagObject(target.getPlainTextSegment(), targetLanguage,
				target.getUniqueID(), val, targetLongs, false, this.wordHandling);
		sourceTerm.setTranslation(targetTerm);
		try
		{
			if (source.getLinguisticProperties() != null)
			{
				LinguisticProperty termid = (LinguisticProperty) source.getLinguisticProperties().get("termelementid");
				if (termid != null)
				{
					sourceTerm.setTermElementID((String) termid.getValue());
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		targetTerm.setTranslation(sourceTerm);
		try
		{
			if (target.getLinguisticProperties() != null)
			{
				LinguisticProperty termid = (LinguisticProperty) target.getLinguisticProperties().get("termelementid");
				if (termid != null)
				{
					targetTerm.setTermElementID((String) termid.getValue());
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		sourceTerm.setLongLCRepresentation(createLongTermKey(sourceTerm.getTermLowercase()));
		sourceTerm.setLongStemmedRepresentation(createLongTermKey(sourceTerm.getTermBaseForm()));

		targetTerm.setLongLCRepresentation(createLongTermKey(targetTerm.getTermLowercase()));
		targetTerm.setLongStemmedRepresentation(createLongTermKey(targetTerm.getTermBaseForm()));

		if ((this.getStoredLinguisticPropertiesArray() != null) && this.getStoredLinguisticPropertiesArray().length > 0)
		{

			for (int l = 0; l < getStoredLinguisticPropertiesArray().length; l++)
			{
				val = lingPropSource.search(getStoredLinguisticPropertiesArray()[l]);
				if (val != null)
				{
					sourceTerm.addAttribute(getStoredLinguisticPropertiesArray()[l], val);
				}
			}

			for (int l = 0; l < getStoredLinguisticPropertiesArray().length; l++)
			{
				val = lingPropTarget.search(getStoredLinguisticPropertiesArray()[l]);
				if (val != null)
				{
					targetTerm.addAttribute(getStoredLinguisticPropertiesArray()[l], val);
				}
			}
		}

		addtoTable(sourceTable, sourceLongs, sourceTerm);
		addtoTable(sourceStemTable, createLongTermKey(sourceTerm.getTermBaseForm()), sourceTerm);
		addtoTable(sourceLCTable, createLongTermKey(sourceTerm.getTermLowercase()), sourceTerm);

		addtoTable(targetTable, targetLongs, targetTerm);
		addtoTable(targetStemTable, createLongTermKey(targetTerm.getTermLowercase()), targetTerm);
		addtoTable(targetLCTable, createLongTermKey(targetTerm.getTermLowercase()), targetTerm);

		if (this.sourceLengthTable.containsKey(sourceTerm.term.length()))
		{
			Vector<TermTagObject> vec = this.sourceLengthTable.get(sourceTerm.term.length());
			vec.add(sourceTerm);
			this.sourceLengthTable.put(Integer.valueOf(sourceTerm.term.length()), vec);
		}
		else
		{
			Vector<TermTagObject> vec = new Vector<TermTagObject>();
			vec.add(sourceTerm);
			this.sourceLengthTable.put(Integer.valueOf(sourceTerm.term.length()), vec);
		}

		if (this.sourceWordLengthTable.containsKey(sourceArrayLongs.length))
		{
			Vector<TermTagObject> vec = this.sourceWordLengthTable.get(sourceArrayLongs.length);
			vec.add(sourceTerm);
			this.sourceWordLengthTable.put(Integer.valueOf(sourceArrayLongs.length), vec);
		}
		else
		{
			Vector<TermTagObject> vec = new Vector<TermTagObject>();
			vec.add(sourceTerm);
			this.sourceWordLengthTable.put(Integer.valueOf(sourceArrayLongs.length), vec);
		}

		if (this.targetLengthTable.containsKey(targetTerm.term.length()))
		{
			Vector<TermTagObject> vec = this.targetLengthTable.get(targetTerm.term.length());
			vec.add(targetTerm);
			this.targetLengthTable.put(Integer.valueOf(targetTerm.term.length()), vec);
		}
		else
		{
			Vector<TermTagObject> vec = new Vector<TermTagObject>();
			vec.add(targetTerm);
			this.targetLengthTable.put(Integer.valueOf(targetTerm.term.length()), vec);
		}

		if (this.targetWordLengthTable.containsKey(targetArrayLongs.length))
		{
			Vector<TermTagObject> vec = this.targetWordLengthTable.get(targetArrayLongs.length);
			vec.add(targetTerm);
			this.targetWordLengthTable.put(Integer.valueOf(targetArrayLongs.length), vec);
		}
		else
		{
			Vector<TermTagObject> vec = new Vector<TermTagObject>();
			vec.add(targetTerm);
			this.targetWordLengthTable.put(Integer.valueOf(targetArrayLongs.length), vec);
		}

		return bAdded;
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
	public boolean bAddTermPair(String sourcePhrase, String sourceLanguage, String uniqueIdSource, String sourceTermId,
			String targetPhrase, String targetLanguage, String uniqueIdTarget, String targetTermId)
	{
		boolean bAdded = false;

		long[] sourceArrayLongs = this.createLongTermArray(sourcePhrase, this.sourceLanguage);
		long[] targetArrayLongs = this.createLongTermArray(targetPhrase, this.targetLanguage);

		// split into words

		String sourceLongs = createLongTermKey(sourcePhrase, this.sourceLanguage);
		String targetLongs = createLongTermKey(targetPhrase, this.targetLanguage);

		TermTagObject sourceTerm = new TermTagObject(sourcePhrase, sourceLanguage, uniqueIdSource, sourceTermId,
				sourceLongs, true, this.wordHandling);
		TermTagObject targetTerm = new TermTagObject(targetPhrase, targetLanguage, uniqueIdTarget, targetTermId,
				targetLongs, false, this.wordHandling);
		sourceTerm.setTranslation(targetTerm);
		targetTerm.setTranslation(sourceTerm);

		sourceTerm.setLongLCRepresentation(createLongTermKey(sourceTerm.getTermLowercase()));
		sourceTerm.setLongStemmedRepresentation(createLongTermKey(sourceTerm.getTermBaseForm()));

		targetTerm.setLongLCRepresentation(createLongTermKey(targetTerm.getTermLowercase()));
		targetTerm.setLongStemmedRepresentation(createLongTermKey(targetTerm.getTermBaseForm()));

		addtoTable(sourceTable, sourceLongs, sourceTerm);
		addtoTable(sourceStemTable, createLongTermKey(sourceTerm.getTermBaseForm()), sourceTerm);
		addtoTable(sourceLCTable, createLongTermKey(sourceTerm.getTermLowercase()), sourceTerm);

		addtoTable(targetTable, targetLongs, targetTerm);
		addtoTable(targetStemTable, createLongTermKey(targetTerm.getTermLowercase()), targetTerm);
		addtoTable(targetLCTable, createLongTermKey(targetTerm.getTermLowercase()), targetTerm);

		if (this.sourceLengthTable.containsKey(sourceTerm.term.length()))
		{
			Vector<TermTagObject> vec = this.sourceLengthTable.get(sourceTerm.term.length());
			vec.add(sourceTerm);
			this.sourceLengthTable.put(Integer.valueOf(sourceTerm.term.length()), vec);
		}
		else
		{
			Vector<TermTagObject> vec = new Vector<TermTagObject>();
			vec.add(sourceTerm);
			this.sourceLengthTable.put(Integer.valueOf(sourceTerm.term.length()), vec);
		}

		if (this.sourceWordLengthTable.containsKey(sourceArrayLongs.length))
		{
			Vector<TermTagObject> vec = this.sourceWordLengthTable.get(sourceArrayLongs.length);
			vec.add(sourceTerm);
			this.sourceWordLengthTable.put(Integer.valueOf(sourceArrayLongs.length), vec);
		}
		else
		{
			Vector<TermTagObject> vec = new Vector<TermTagObject>();
			vec.add(sourceTerm);
			this.sourceWordLengthTable.put(Integer.valueOf(sourceArrayLongs.length), vec);
		}

		if (this.targetLengthTable.containsKey(targetTerm.term.length()))
		{
			Vector<TermTagObject> vec = this.targetLengthTable.get(targetTerm.term.length());
			vec.add(targetTerm);
			this.targetLengthTable.put(Integer.valueOf(targetTerm.term.length()), vec);
		}
		else
		{
			Vector<TermTagObject> vec = new Vector<TermTagObject>();
			vec.add(targetTerm);
			this.targetLengthTable.put(Integer.valueOf(targetTerm.term.length()), vec);
		}

		if (this.targetWordLengthTable.containsKey(targetArrayLongs.length))
		{
			Vector<TermTagObject> vec = this.targetWordLengthTable.get(targetArrayLongs.length);
			vec.add(targetTerm);
			this.targetWordLengthTable.put(Integer.valueOf(targetArrayLongs.length), vec);
		}
		else
		{
			Vector<TermTagObject> vec = new Vector<TermTagObject>();
			vec.add(targetTerm);
			this.targetWordLengthTable.put(Integer.valueOf(targetArrayLongs.length), vec);
		}

		return bAdded;
	}

	private int containsResult(Vector<TermTagObjectMatch> termResults, TermTagObject termTagObject)
	{
		for (int i = 0; i < termResults.size(); i++)
		{
			if (termTagObject.longRepresentation.equals(termResults.get(i).longRepresentation))
			{
				if (termTagObject.translation == termResults.get(i).translation)
				{
					termResults.get(i).incrementNumberOfMatches();
					// termResults.get(i).getTermInternalMatch().add(new TermInternalMatch(termTagObject.termMatch, matchType, fuzzy));
					return i;
				}
			}
		}
		return -1;
	}

	/**
	 * @param term
	 * @param language
	 * @return
	 */
	public long[] createLongTermArray(String term, String language)
	{
		String[] termArray = wordHandling.segmentToWordArray(term, language);
		if (this.sourceLanguage == language)
		{
			if (!this.phraseSourceTableSizes.contains(termArray.length) && (termArray.length != 0))
				this.phraseSourceTableSizes.add(termArray.length);
		}
		else
		{
			if (!this.phraseTargetTableSizes.contains(termArray.length))
				this.phraseTargetTableSizes.add(termArray.length);
		}
		long[] longArray = new long[termArray.length];
		for (int i = 0; i < termArray.length; i++)
		{
			if (longWordHash.containsKey(termArray[i]))
			{
				longArray[i] = longWordHash.get(termArray[i]);
			}
			else
			{
				longWordHash.put(termArray[i], new Long(currentWordCounter));
				longArray[i] = longWordHash.get(termArray[i]);
				currentWordCounter++;
			}
		}
		return longArray;
	}

	/**
	 * @param longArray
	 * @return
	 */
	public String createLongTermKey(long[] longArray)
	{
		String longs = "";
		for (int i = 0; i < longArray.length; i++)
		{
			if (i == 0)
				longs = longs + sepChar + longArray[i];
			else
				longs = longs + sepChar + longArray[i];
		}
		return longs;
	}

	/**
	 * @param term
	 * @return
	 */
	public String createLongTermKey(String term)
	{
		String[] termArray = wordHandling.segmentToWordArray(term);

		if (!this.phraseSourceTableSizes.contains(termArray.length) && (termArray.length != 0))
			this.phraseSourceTableSizes.add(termArray.length);
		String longs = "";
		for (int i = 0; i < termArray.length; i++)
		{
			Long lkey = 0l;
			if (longWordHash.containsKey(termArray[i]))
			{
				lkey = longWordHash.get(termArray[i]);
			}
			else
			{
				longWordHash.put(termArray[i], new Long(currentWordCounter));
				lkey = currentWordCounter;
				currentWordCounter++;
			}
			longs = longs + sepChar + lkey;
		}
		return longs;
	}

	/**
	 * @param term
	 * @param language
	 * @return
	 */
	public String createLongTermKey(String term, String language)
	{
		String[] termArray = wordHandling.segmentToWordArray(term, language);
		if (this.sourceLanguage == language)
		{
			if (!this.phraseSourceTableSizes.contains(termArray.length) && (termArray.length != 0))
				this.phraseSourceTableSizes.add(termArray.length);
		}
		else
		{
			if (!this.phraseTargetTableSizes.contains(termArray.length))
				this.phraseTargetTableSizes.add(termArray.length);
		}
		String longs = "";
		for (int i = 0; i < termArray.length; i++)
		{
			Long lkey = 0l;
			if (longWordHash.containsKey(termArray[i]))
			{
				lkey = longWordHash.get(termArray[i]);
			}
			else
			{
				longWordHash.put(termArray[i], new Long(currentWordCounter));
				lkey = currentWordCounter;
				currentWordCounter++;
			}
			longs = longs + sepChar + lkey;
		}
		return longs;
	}

	public void createTermStringLengthSortedList()
	{
		TermTagObject.setbCompareStringMode(true);
		if (this.bSourceStringMatch)
		{
			Enumeration<Vector<TermTagObject>> enumi = sourceTable.elements();
			while (enumi.hasMoreElements())
			{
				Vector<TermTagObject> vec = enumi.nextElement();
				for (int i = 0; i < vec.size(); i++)
				{
					TermTagObject termob = vec.get(i);
					sourceLengthSortedlist.add(termob);

				}
			}
			Collections.sort(sourceLengthSortedlist);
		}

		if (this.bTargetStringMatch)
		{
			Enumeration<Vector<TermTagObject>> enumi = targetTable.elements();
			while (enumi.hasMoreElements())
			{
				Vector<TermTagObject> vec = enumi.nextElement();
				for (int i = 0; i < vec.size(); i++)
				{
					TermTagObject termob = vec.get(i);
					targetLengthSortedlist.add(termob);

				}
			}
			Collections.sort(targetLengthSortedlist);
		}

		TermTagObject.setbCompareStringMode(false);
	}

	private Vector<TermTagObjectMatch> findFuzzyTerms(String[] source, boolean bSource, boolean bLowercase, boolean bStemmed,
			boolean bFuzzy, Vector<TermTagObjectMatch> termResults)
	{
		this.fuzzyTimer.continueTimer();
		String table[] = new String[source.length * (source.length + 1) / 2];
		int tableWordLength[] = new int[source.length * (source.length + 1) / 2];
		int k = 0;

		// maxWordLengthSearch = 2; // source.length
		// setMinFuzzyStartLength(2);

		for (int i = 0; i < source.length; i++)
		{
			String term = source[i];
			table[k] = term;
			tableWordLength[k] = 1;
			if (table[k].length() < this.minFuzzyStringLength)
				tableWordLength[k] = -1;
			k++;
			for (int j = i + 1; j < source.length; j++)
			{
				table[k] = table[k - 1] + " " + source[j];
				tableWordLength[k] = tableWordLength[k - 1] + 1;
				if (table[k].length() < this.minFuzzyStringLength)
					tableWordLength[k] = -1;
				k++;
			}
		}
		boolean bUseLCS = false;
		Hashtable<Integer, Vector<TermTagObject>> lengthTable = null;
		Hashtable<Integer, Vector<TermTagObject>> lengthWordTable = null;
		if (bSource)
		{
			lengthTable = this.sourceLengthTable;
			lengthWordTable = this.sourceWordLengthTable;
		}
		else
		{
			lengthTable = this.targetLengthTable;
			lengthWordTable = this.targetWordLengthTable;
		}

		for (int i = 0; i < table.length; i++)
		{
			setiAllSourceWordCombination(getiAllSourceWordCombination() + 1);
			String term = table[i];
			int iw = tableWordLength[i];
			if ((iw == -1) || ((maxWordLengthSearch > 0) && (tableWordLength[i] > maxWordLengthSearch)))
			{
				setiWordNotFoundInLengthWordTable(getiWordNotFoundInLengthWordTable() + 1);
				continue;
			}
			Vector<TermTagObject> vecwords = lengthWordTable.get(iw);
			if (vecwords == null)
			{
				setiWordNotFoundInLengthWordTable(getiWordNotFoundInLengthWordTable() + 1);
				continue;
			}

			int fuzzyDeviation = (int) (((float) term.length() * (float) (100 - this.fuzzyPercent)) / 100.00);
			int mintermLen = term.length() - fuzzyDeviation;
			int maxtermLen = term.length() + fuzzyDeviation;
			Vector<TermTagObject> vec = new Vector<TermTagObject>();
			for (int l = 0; l < vecwords.size(); l++)
			{
				if ((vecwords.get(l).getTerm().length() <= maxtermLen) && (vecwords.get(l).getTerm().length() >= mintermLen))
				{
					if (this.minFuzzyStartLength > 0)
					{
						if (vecwords.get(l).getTerm().length() >= (this.minFuzzyStartLength + 1))
						{
							String stx = vecwords.get(l).getTerm().substring(0, this.minFuzzyStartLength + 1);
							if (!term.startsWith(stx, 0))
							{
								iStartFuzzyDoesNotMatch++;
								continue;
							}
						}
						else
						{
							iStartFuzzyDoesNotMatch++;
							continue;
						}
					}

					vec.add(vecwords.get(l));
				}
			}

			if (vec.size() == 0)
			{
				setiResultingVecSizeZero(getiResultingVecSizeZero() + 1);
				continue;
			}

			setiVecSizeToCheck(getiVecSizeToCheck() + 1);

			if (XliffTermTagger.getGlobalDebug())
				System.out.println("Search term: \"" + term + "\" - len: " + term.length() + " i: " + i + " vec.size(): " + vec.size() + " - mintermLen: " + mintermLen + " maxtermLen: "
						+ maxtermLen);

			for (int l = mintermLen; l < maxtermLen + 1; l++)
			{
				if (!lengthTable.containsKey((Integer) l))
					continue;
				// Vector<TermTagObject> vec = lengthTable.get(l);
				// System.out.println("Length: " + l + " mintermLen: " + mintermLen + " maxtermLen: " + maxtermLen);

				// Enumeration<Vector<TermTagObject>> enumi;
				// if (bSource)
				// enumi = sourceTable.elements();
				// else
				// enumi = targetTable.elements();
				// while (enumi.hasMoreElements())
				// {
				// Vector<TermTagObject> vec = enumi.nextElement();

				for (int le = 0; le < vec.size(); le++)
				{
					TermTagObject termob = vec.get(le);

					int iFuzzy = 0;

					if ((termob.getTerm().length() > maxtermLen) || (termob.getTerm().length() < mintermLen))
						iFuzzy = 0;
					else
					{
						// String hashKey = term + "--" + termob.getTerm();
						// if (false && fuzzyMathReuseTable.containsKey(hashKey))
						// {
						// iFuzzy = fuzzyMathReuseTable.get(hashKey);
						// }
						// else
						{
							if (bUseLCS)
								iFuzzy = de.folt.similarity.LCS.LCSAlgorithm(term, termob.getTerm()).getSimilarity();
							else
								iFuzzy = LevenshteinSimilarity.levenshteinSimilarity(term, termob.getTerm(),
										this.fuzzyPercent);
							// fuzzyMathReuseTable.put(hashKey, iFuzzy);
							iSimilarityComputed++;
						}
						// hashKey = null;
					}
					if (iFuzzy > this.fuzzyPercent)
					{
						int iNum = -1;
						iSimilarityMatched++;
						if ((iNum = containsResult(termResults, termob)) == -1)
						{
							TermTagObjectMatch vermTagObjectMatch = new TermTagObjectMatch(termob);
							vermTagObjectMatch.setMatchType(TermTagObjectMatch.MATCHTYPE.FUZZY);
							vermTagObjectMatch.setFuzzy(iFuzzy);
							vermTagObjectMatch.setTermMatch(term);
							vermTagObjectMatch.incrementNumberOfMatches();
							vermTagObjectMatch.addSorted(new TermInternalMatch(term, TermTagObjectMatch.MATCHTYPE.FUZZY, iFuzzy));
							termResults.add(vermTagObjectMatch);
						}
						else
						{
							TermTagObjectMatch vermTagObjectMatch = termResults.get(iNum);
							vermTagObjectMatch.addSorted(new TermInternalMatch(term, TermTagObjectMatch.MATCHTYPE.FUZZY, iFuzzy));
						}
					}
				}
			}
		}
		this.fuzzyTimer.stopTimer();

		return termResults;
	}

	/**
	 * @param source
	 * @param bSource
	 * @return
	 */
	private Vector<TermTagObjectMatch> findStringTerms(String[] source, boolean bSource)
	{
		Vector<TermTagObjectMatch> result = new Vector<TermTagObjectMatch>();
		ArrayList<TermTagObject> lengthList = sourceLengthSortedlist;
		if (!bSource)
			lengthList = targetLengthSortedlist;
		String fullString = source[0]; // normally this should just ba one string!!!
		Vector<String> array = new Vector<String>();
		for (int i = 1; i < source.length; i++)
		{
			fullString = fullString + " " + source[i];
		}
		array.add(fullString);
		int iPos = -1;

		String lan = this.sourceLanguage;
		if (!bSource)
			lan = this.targetLanguage;

		for (int i = 0; i < lengthList.size(); i++)
		{
			int j = 0;
			while (true)
			{
				if (array.get(j).startsWith(stmarker))
				{
					j++;
				}
				else if ((iPos = array.get(j).indexOf(lengthList.get(i).term)) > -1)
				{
					String left = "";
					if (iPos > 0)
						left = array.get(j).substring(0, iPos);
					String found = array.get(j).substring(iPos, iPos + lengthList.get(i).term.length());
					String right = "";
					int iLengthList = lengthList.get(i).term.length();
					if (array.get(j).length() >= (iLengthList + iPos))
						right = array.get(j).substring(iPos + iLengthList, array.get(j).length());
					array.set(j, stmarker + lengthList.get(i).termID + stmarker + lengthList.get(i).getUniqueID() + stmarker + found);
					if (!right.equals(""))
						array.add(j + 1, right);
					if (!left.equals(""))
						if (j == 0)
							array.add(0, left);
						else
							array.add(j - 1, left);
					j = 0;
				}
				else
				{
					j++;
				}

				if (j >= array.size())
					break;
			}
		}

		for (int i = 0; i < array.size(); i++)
		{
			if (array.get(i).startsWith(stmarker))
			{

				String[] parts = array.get(i).split(stmarker);
				TermTagObjectMatch termTagObjectMatch = new TermTagObjectMatch(parts[3], lan, parts[1], parts[2], bSource);
				termTagObjectMatch.setMatchType(MATCHTYPE.EXACT);
				result.add(termTagObjectMatch);
			}
		}
		return result;
	}

	/**
	 * findTerms finds a phrase translation
	 * 
	 * @param sourcePhrase
	 *            the source phrase to search
	 * @return a vector of phrase result
	 */
	public Vector<TermTagObjectMatch> findTerms(String sourcePhrase, boolean bSource)
	{
		String language = this.sourceLanguage;
		if (!bSource)
			language = this.targetLanguage;
		String[] source = wordHandling.segmentToWordArray(sourcePhrase, language);
		return findTerms(source, bSource);
	}

	/**
	 * finds a phrase translation using exact matches
	 * 
	 * @param source
	 *            the source phrase as string array to search
	 * @param bSource
	 *            true for source language search
	 * @return
	 */
	public Vector<TermTagObjectMatch> findTerms(String[] source, boolean bSource)
	{
		return findTerms(source, bSource, this.bLowercase, this.bStemmed, this.bFuzzy);
	}

	/**
	 * findTerms finds a phrase translation
	 * 
	 * @param source
	 *            the source phrase as string array to search
	 * @param bSource
	 *            true for source language search
	 * @param bFuzzy
	 *            true for fuzzy search
	 * @return a vector of phrase result
	 */
	public Vector<TermTagObjectMatch> findTerms(String[] source, boolean bSource, boolean bLowercase, boolean bStemmed,
			boolean bFuzzy)
	{
		setiFindTermsCalled(getiFindTermsCalled() + 1);

		if (this.bSourceStringMatch && bSource)
		{
			return findStringTerms(source, bSource);
		}
		else if (this.bTargetStringMatch && !bSource)
		{
			return findStringTerms(source, bSource);
		}

		Vector<TermTagObjectMatch> termResults = new Vector<TermTagObjectMatch>();
		Long[] longs = new Long[source.length];
		Long[] lclongs = new Long[source.length];
		Long[] stemlongs = new Long[source.length];
		setIfindTermsWords(getIfindTermsWords() + source.length);
		// create key representation of segment
		for (int i = 0; i < source.length; i++)
		{
			/*
			 * if (bSource)
			 * {
			 * if (i > this.maxSourcePhraseLength)
			 * break;
			 * }
			 * else
			 * {
			 * if (i > this.maxTargetPhraseLength)
			 * break;
			 * }
			 */
			Long lkey = 0l;
			if (longWordHash.containsKey(source[i]))
			{
				lkey = longWordHash.get(source[i]);
			}
			longs[i] = lkey;

			if (bLowercase && !bStemmed) // only use when not stemming . 02.12.2013
			{
				lkey = 0l;
				if (longWordHash.containsKey(source[i].toLowerCase()))
				{
					lkey = longWordHash.get(source[i].toLowerCase());
				}
				lclongs[i] = lkey;
			}

			if (bStemmed)
			{
				lkey = 0l;
				String lang = this.sourceLanguage;
				if (!bSource)
					lang = this.targetLanguage;
				if (longWordHash.containsKey(WordHandling.stem(source[i], lang)))
				{
					lkey = longWordHash.get(WordHandling.stem(source[i], lang));
				}
				stemlongs[i] = lkey;
			}
		}

		this.exactTimer.continueTimer();
		// create all n-Gram-word combinations
		for (int i = 0; i < longs.length; i++)
		{
			if (longs[i] == 0l)
				continue;
			Vector<Long> ptWords = new Vector<Long>();
			String termMatch = "";
			for (int j = i; j < longs.length; j++)
			{
				if (bSource)
				{
					if ((j - i) > this.maxSourcePhraseLength)
						break;
				}
				else
				{
					if ((j - i) > this.maxTargetPhraseLength)
						break;
				}
				if (longs[j] == 0l)
					break;
				// search from i..j
				ptWords.add(longs[j]);
				String longsearch = "";
				if (j == i)
					termMatch = source[j];
				else
					termMatch = termMatch + " " + source[j];

				for (int k = 0; k < ptWords.size(); k++)
				{
					longsearch = longsearch + sepChar + ptWords.get(k);
				}
				Vector<TermTagObject> matchTerms;
				if (bSource)
					matchTerms = this.sourceTable.get(longsearch);
				else
					matchTerms = this.targetTable.get(longsearch);

				if (matchTerms != null)
				{
					for (int m = 0; m < matchTerms.size(); m++)
					{
						int iNum = -1;
						if ((iNum = containsResult(termResults, matchTerms.get(m))) == -1)
						{
							TermTagObjectMatch vermTagObjectMatch = new TermTagObjectMatch(matchTerms.get(m));
							vermTagObjectMatch.setMatchType(TermTagObjectMatch.MATCHTYPE.EXACT);
							vermTagObjectMatch.setTermMatch(termMatch);
							vermTagObjectMatch.incrementNumberOfMatches();
							vermTagObjectMatch.addSorted(new TermInternalMatch(termMatch, TermTagObjectMatch.MATCHTYPE.EXACT, 100, i));
							termResults.add(vermTagObjectMatch);
						}
						else
						{
							TermTagObjectMatch vermTagObjectMatch = termResults.get(iNum);
							vermTagObjectMatch.addSorted(new TermInternalMatch(termMatch, TermTagObjectMatch.MATCHTYPE.EXACT, 100, i));
						}
					}
				}
			}
		}
		this.exactTimer.stopTimer();

		// termResults = new Vector<TermTagObjectMatch>(new
		// LinkedHashSet<TermTagObjectMatch>(termResults));

		if (bLowercase && !bStemmed)
		{
			this.lcTimer.continueTimer();
			// create all n-Gram-word lowercased combinations
			for (int i = 0; i < lclongs.length; i++)
			{
				/*
				 * if (bSource)
				 * {
				 * if (i > this.maxSourcePhraseLength)
				 * break;
				 * }
				 * else
				 * {
				 * if (i > this.maxTargetPhraseLength)
				 * break;
				 * }
				 */
				if (lclongs[i] == 0l)
					continue;
				String termMatch = "";
				Vector<Long> ptWords = new Vector<Long>();
				for (int j = i; j < lclongs.length; j++)
				{
					if (bSource)
					{
						if ((j - i) > this.maxSourcePhraseLength)
							break;
					}
					else
					{
						if ((j - i) > this.maxTargetPhraseLength)
							break;
					}
					if (lclongs[j] == 0l)
						break;

					if (j == i)
						termMatch = source[j];
					else
						termMatch = termMatch + " " + source[j];
					// search from i..j
					ptWords.add(lclongs[j]);
					String longsearch = "";
					for (int k = 0; k < ptWords.size(); k++)
					{
						longsearch = longsearch + sepChar + ptWords.get(k);
					}
					Vector<TermTagObject> matchTerms;
					if (bSource)
						matchTerms = this.sourceLCTable.get(longsearch);
					else
						matchTerms = this.targetLCTable.get(longsearch);

					if (matchTerms != null)
					{
						for (int m = 0; m < matchTerms.size(); m++)
						{
							int iNum = -1;
							if ((iNum = containsResult(termResults, matchTerms.get(m))) == -1)
							{
								TermTagObjectMatch vermTagObjectMatch = new TermTagObjectMatch(matchTerms.get(m));
								vermTagObjectMatch.setMatchType(TermTagObjectMatch.MATCHTYPE.LOWERCASE);
								vermTagObjectMatch.setTermMatch(termMatch);
								vermTagObjectMatch.incrementNumberOfMatches();
								vermTagObjectMatch.addSorted(new TermInternalMatch(termMatch, TermTagObjectMatch.MATCHTYPE.LOWERCASE, 100, i));
								termResults.add(vermTagObjectMatch);
							}
							else
							{
								TermTagObjectMatch vermTagObjectMatch = termResults.get(iNum);
								vermTagObjectMatch.addSorted(new TermInternalMatch(termMatch, TermTagObjectMatch.MATCHTYPE.LOWERCASE, 100, i));
							}
						}
					}
				}
			}
			this.lcTimer.stopTimer();
		}

		// termResults = new Vector<TermTagObjectMatch>(new
		// LinkedHashSet<TermTagObjectMatch>(termResults));

		if (bStemmed)
		{
			// create all n-Gram-word lowercased combinations
			this.stemmerTimer.continueTimer();
			for (int i = 0; i < stemlongs.length; i++)
			{
				/*
				 * if (bSource)
				 * {
				 * if (i > this.maxSourcePhraseLength)
				 * break;
				 * }
				 * else
				 * {
				 * if (i > this.maxTargetPhraseLength)
				 * break;
				 * }
				 */
				if (stemlongs[i] == 0l)
					continue;
				String termMatch = "";
				Vector<Long> ptWords = new Vector<Long>();
				for (int j = i; j < stemlongs.length; j++)
				{
					if (bSource)
					{
						if ((j - i) > this.maxSourcePhraseLength)
							break;
					}
					else
					{
						if ((j - i) > this.maxTargetPhraseLength)
							break;
					}
					if (stemlongs[j] == 0l)
						break;
					if (j == i)
						termMatch = source[j];
					else
						termMatch = termMatch + " " + source[j];
					// search from i..j
					ptWords.add(stemlongs[j]);
					String longsearch = "";
					for (int k = 0; k < ptWords.size(); k++)
					{
						longsearch = longsearch + sepChar + ptWords.get(k);
					}
					Vector<TermTagObject> matchTerms;
					if (bSource)
						matchTerms = this.sourceStemTable.get(longsearch);
					else
						matchTerms = this.targetStemTable.get(longsearch);

					if (matchTerms != null)
					{
						for (int m = 0; m < matchTerms.size(); m++)
						{
							int iNum = -1;
							if ((iNum = containsResult(termResults, matchTerms.get(m))) == -1)
							{
								TermTagObjectMatch vermTagObjectMatch = new TermTagObjectMatch(matchTerms.get(m));
								vermTagObjectMatch.setMatchType(TermTagObjectMatch.MATCHTYPE.STEMMED);
								vermTagObjectMatch.setTermMatch(termMatch);
								vermTagObjectMatch.incrementNumberOfMatches();
								vermTagObjectMatch.addSorted(new TermInternalMatch(termMatch, TermTagObjectMatch.MATCHTYPE.STEMMED, 100, i));
								termResults.add(vermTagObjectMatch);
							}
							else
							{
								TermTagObjectMatch vermTagObjectMatch = termResults.get(iNum);
								vermTagObjectMatch.addSorted(new TermInternalMatch(termMatch, TermTagObjectMatch.MATCHTYPE.STEMMED, 100, i));
							}
						}
					}
				}
			}
			this.stemmerTimer.stopTimer();
		}

		// termResults = new Vector<TermTagObjectMatch>(new
		// LinkedHashSet<TermTagObjectMatch>(termResults));

		if (bFuzzy)
		{
			termResults = findFuzzyTerms(source, bSource, bLowercase, bStemmed, bFuzzy, termResults);
		}

		// termResults = new Vector<TermTagObjectMatch>(new
		// LinkedHashSet<TermTagObjectMatch>(termResults));

		for (int i = 0; i < termResults.size(); i++)
		{
			TermTagObjectMatch vermTagObjectMatch = termResults.get(i);
			vermTagObjectMatch.setTermMatch(vermTagObjectMatch.getTermInternalMatch().get(0).getTermMatch());
			vermTagObjectMatch.setMatchType(vermTagObjectMatch.getTermInternalMatch().get(0).getMatchType());
			vermTagObjectMatch.setFuzzy(vermTagObjectMatch.getTermInternalMatch().get(0).getFuzzy());
		}

		Collections.sort(termResults);
		return termResults;
	}

	/**
	 * findTerms finds a phrase translation
	 * 
	 * @param source
	 *            a vector of strings which form a phrase
	 * @return a vector of phrase result
	 */
	public Vector<TermTagObjectMatch> findTerms(Vector<String> source, boolean bSource)
	{
		String[] sourceArray = (String[]) source.toArray();
		return findTerms(sourceArray, bSource);
	}

	/**
	 * findTerms finds a phrase translation starting from iStart till eEnd of
	 * the supplied vector
	 * 
	 * @param source
	 *            a vector of strings which form a phrase
	 * @param iStart
	 *            start position (inclusive)
	 * @param iEnd
	 *            end position (inclusive)
	 * @return a vector of phrase result
	 */
	public Vector<TermTagObjectMatch> findTerms(Vector<String> source, int iStart, int iEnd, boolean bSource)
	{
		String[] sourceArray = new String[iEnd - iStart + 1];

		for (int i = iStart; i <= iEnd; i++)
		{
			sourceArray[i - iStart] = source.get(i);
		}
		return findTerms(sourceArray, bSource);
	}

	private Vector<TermTagObjectMatch> findTerms(XliffElementHandler xliffCoded, String language)
	{

		return null;
	}

	@SuppressWarnings("unused")
	public Vector<TermTagObjectMatch> findTerms(XliffElementHandler xliffSourceCoded, XliffElementHandler xliffTargetCoded, String sourceLanguage, String targetLanguage)
	{
		Vector<TermTagObjectMatch> sourceTermsFound = findTerms(xliffSourceCoded, sourceLanguage);
		Vector<TermTagObjectMatch> targetTermsFound = findTerms(xliffTargetCoded, targetLanguage);
		return null;
	}

	public long getCurrentWordCounter()
	{
		return currentWordCounter;
	}

	public Timer getExactTimer()
	{
		return exactTimer;
	}

	public Hashtable<String, Integer> getFuzzyMathReuseTable()
	{
		return fuzzyMathReuseTable;
	}

	public int getFuzzyPercent()
	{
		return fuzzyPercent;
	}

	public Timer getFuzzyTimer()
	{
		return fuzzyTimer;
	}

	public int getiAllSourceWordCombination()
	{
		return iAllSourceWordCombination;
	}

	public int getiFindTermsCalled()
	{
		return iFindTermsCalled;
	}

	public int getIfindTermsWords()
	{
		return ifindTermsWords;
	}

	public int getiResultingVecSizeZero()
	{
		return iResultingVecSizeZero;
	}

	public int getiSimilarityComputed()
	{
		return iSimilarityComputed;
	}

	public int getiSimilarityMatched()
	{
		return iSimilarityMatched;
	}

	public int getiStartFuzzyDoesNotMatch()
	{
		return iStartFuzzyDoesNotMatch;
	}

	public int getiVecSizeToCheck()
	{
		return iVecSizeToCheck;
	}

	public int getiWordNotFoundInLengthWordTable()
	{
		return iWordNotFoundInLengthWordTable;
	}

	public Timer getLcTimer()
	{
		return lcTimer;
	}

	public Hashtable<String, Long> getLongWordHash()
	{
		return longWordHash;
	}

	public int getMaxSourcePhraseLength()
	{
		return maxSourcePhraseLength;
	}

	public int getMaxTargetPhraseLength()
	{
		return maxTargetPhraseLength;
	}

	public int getMaxWordLengthSearch()
	{
		return maxWordLengthSearch;
	}

	public int getMinFuzzyStartLength()
	{
		return minFuzzyStartLength;
	}

	public int getMinFuzzyStringLength()
	{
		return minFuzzyStringLength;
	}

	public Vector<Integer> getPhraseSourceTableSizes()
	{
		return phraseSourceTableSizes;
	}

	public Vector<Integer> getPhraseTargetTableSizes()
	{
		return phraseTargetTableSizes;
	}

	public char getSepChar()
	{
		return sepChar;
	}

	public String getSourceLanguage()
	{
		return sourceLanguage;
	}

	public Hashtable<String, Vector<TermTagObject>> getSourceLCTable()
	{
		return sourceLCTable;
	}

	public ArrayList<TermTagObject> getSourceLengthSortedlist()
	{
		return sourceLengthSortedlist;
	}

	public Hashtable<Integer, Vector<TermTagObject>> getSourceLengthTable()
	{
		return sourceLengthTable;
	}

	public Hashtable<String, Vector<TermTagObject>> getSourceStemTable()
	{
		return sourceStemTable;
	}

	public Hashtable<String, Vector<TermTagObject>> getSourceTable()
	{
		return sourceTable;
	}

	public String getSourceTargetSepString()
	{
		return sourceTargetSepString;
	}

	public Hashtable<Integer, Vector<TermTagObject>> getSourceWordLengthTable()
	{
		return sourceWordLengthTable;
	}

	public Timer getStemmerTimer()
	{
		return stemmerTimer;
	}

	public String getStoredLinguisticProperties()
	{
		return storedLinguisticProperties;
	}

	public String[] getStoredLinguisticPropertiesArray()
	{
		return storedLinguisticPropertiesArray;
	}

	public String getTargetLanguage()
	{
		return targetLanguage;
	}

	public Hashtable<String, Vector<TermTagObject>> getTargetLCTable()
	{
		return targetLCTable;
	}

	public ArrayList<TermTagObject> getTargetLengthSortedlist()
	{
		return targetLengthSortedlist;
	}

	public Hashtable<Integer, Vector<TermTagObject>> getTargetLengthTable()
	{
		return targetLengthTable;
	}

	public Hashtable<String, Vector<TermTagObject>> getTargetStemTable()
	{
		return targetStemTable;
	}

	public Hashtable<String, Vector<TermTagObject>> getTargetTable()
	{
		return targetTable;
	}

	public Hashtable<Integer, Vector<TermTagObject>> getTargetWordLengthTable()
	{
		return targetWordLengthTable;
	}

	public WordHandling getWordHandling()
	{
		return wordHandling;
	}

	public boolean isbFuzzy()
	{
		return bFuzzy;
	}

	public boolean isbLowercase()
	{
		return bLowercase;
	}

	public boolean isbSourceStringMatch()
	{
		return bSourceStringMatch;
	}

	public boolean isbStemmed()
	{
		return bStemmed;
	}

	public boolean isbTargetStringMatch()
	{
		return bTargetStringMatch;
	}

	public String lengthPrint()
	{
		StringBuffer buf = new StringBuffer();
		if (this.bSourceStringMatch)
		{
			buf.append("Source String Length Sorted List\n");
			for (int i = 0; i < sourceLengthSortedlist.size(); i++)
			{
				TermTagObject term = sourceLengthSortedlist.get(i);
				buf.append(term.term.length() + "::\"" + term.term + "\"\n");
			}
			buf.append("\n\n\n");
		}

		if (this.bTargetStringMatch)
		{
			buf.append("Target String Length Sorted List\n");
			for (int i = 0; i < targetLengthSortedlist.size(); i++)
			{
				TermTagObject term = targetLengthSortedlist.get(i);
				buf.append(term.term.length() + "::\"" + term.term + "\"\n");
			}
		}
		return buf.toString();
	}

	public String paramsToString()
	{
		return ("TermTagObjectTable Parameters\n\tSource Language=" + this.sourceLanguage + "\n\tTarget Language=" + this.targetLanguage + "\n\tFuzzy=" + this.bFuzzy + " Fuzzy Percent="
				+ this.fuzzyPercent + "\n\tLowercase=" + this.bLowercase + "\n\tStemmed=" + this.bStemmed);
	}

	public void printALengthTable(String comment, Hashtable<Integer, Vector<TermTagObject>> lengthTable)
	{
		System.out.println(comment);

		Vector<Integer> v = new Vector<Integer>(lengthTable.keySet());
		Collections.sort(v);
		for (int i = 0; i < v.size(); i++)
		{
			Integer key = v.elementAt(i);
			System.out.println(key + ": " + lengthTable.get(key).size());
		}
		/*
		 * Enumeration<Integer> enum1 = lengthTable.keys();
		 * while (enum1.hasMoreElements())
		 * {
		 * Integer i = enum1.nextElement();
		 * System.out.println(i + ": " + lengthTable.get(i).size());
		 * }
		 */
	}

	public void setbFuzzy(boolean bFuzzy)
	{
		this.bFuzzy = bFuzzy;
	}

	public void setbLowercase(boolean bLowercase)
	{
		this.bLowercase = bLowercase;
	}

	public void setbSourceStringMatch(boolean bSourceStringMatch)
	{
		this.bSourceStringMatch = bSourceStringMatch;
	}

	public void setbStemmed(boolean bStemmed)
	{
		this.bStemmed = bStemmed;
	}

	public void setbTargetStringMatch(boolean bTargetStringMatch)
	{
		this.bTargetStringMatch = bTargetStringMatch;
	}

	public void setCurrentWordCounter(long currentWordCounter)
	{
		this.currentWordCounter = currentWordCounter;
	}

	public void setExactTimer(Timer exactTimer)
	{
		this.exactTimer = exactTimer;
	}

	public void setFuzzyMathReuseTable(Hashtable<String, Integer> fuzzyMathReuseTable)
	{
		this.fuzzyMathReuseTable = fuzzyMathReuseTable;
	}

	public void setFuzzyPercent(int fuzzyPercent)
	{
		this.fuzzyPercent = fuzzyPercent;
	}

	public void setFuzzyTimer(Timer fuzzyTimer)
	{
		this.fuzzyTimer = fuzzyTimer;
	}

	public void setiAllSourceWordCombination(int iAllSourceWordCombination)
	{
		this.iAllSourceWordCombination = iAllSourceWordCombination;
	}

	public void setiFindTermsCalled(int iFindTermsCalled)
	{
		this.iFindTermsCalled = iFindTermsCalled;
	}

	public void setIfindTermsWords(int ifindTermsWords)
	{
		this.ifindTermsWords = ifindTermsWords;
	}

	public void setiResultingVecSizeZero(int iResultingVecSizeZero)
	{
		this.iResultingVecSizeZero = iResultingVecSizeZero;
	}

	public void setiSimilarityComputed(int iSimilarityComputed)
	{
		this.iSimilarityComputed = iSimilarityComputed;
	}

	public void setiSimilarityMatched(int iSimilarityMatched)
	{
		this.iSimilarityMatched = iSimilarityMatched;
	}

	public void setiStartFuzzyDoesNotMatch(int iStartFuzzyDoesNotMatch)
	{
		this.iStartFuzzyDoesNotMatch = iStartFuzzyDoesNotMatch;
	}

	public void setiVecSizeToCheck(int iVecSizeToCheck)
	{
		this.iVecSizeToCheck = iVecSizeToCheck;
	}

	public void setiWordNotFoundInLengthWordTable(int iWordNotFoundInLengthWordTable)
	{
		this.iWordNotFoundInLengthWordTable = iWordNotFoundInLengthWordTable;
	}

	public void setLcTimer(Timer lcTimer)
	{
		this.lcTimer = lcTimer;
	}

	public void setLongWordHash(Hashtable<String, Long> longWordHash)
	{
		this.longWordHash = longWordHash;
	}

	public void setMaxSourcePhraseLength(int maxSourcePhraseLength)
	{
		this.maxSourcePhraseLength = maxSourcePhraseLength;
	}

	public void setMaxTargetPhraseLength(int maxTargetPhraseLength)
	{
		this.maxTargetPhraseLength = maxTargetPhraseLength;
	}

	public void setMaxWordLengthSearch(int maxWordLengthSearch)
	{
		this.maxWordLengthSearch = maxWordLengthSearch;
	}

	public void setMinFuzzyStartLength(int minFuzzyStartLenght)
	{
		this.minFuzzyStartLength = minFuzzyStartLenght;
	}

	public void setMinFuzzyStringLength(int minFuzzyStringLength)
	{
		this.minFuzzyStringLength = minFuzzyStringLength;
	}

	public void setPhraseSourceTableSizes(Vector<Integer> phraseSourceTableSizes)
	{
		this.phraseSourceTableSizes = phraseSourceTableSizes;
	}

	public void setPhraseTargetTableSizes(Vector<Integer> phraseTargetTableSizes)
	{
		this.phraseTargetTableSizes = phraseTargetTableSizes;
	}

	public void setSepChar(char sepChar)
	{
		this.sepChar = sepChar;
	}

	public void setSourceLanguage(String sourceLanguage)
	{
		this.sourceLanguage = sourceLanguage;
	}

	public void setSourceLCTable(Hashtable<String, Vector<TermTagObject>> sourceLCTable)
	{
		this.sourceLCTable = sourceLCTable;
	}

	public void setSourceLengthSortedlist(ArrayList<TermTagObject> sourceLengthSortedlist)
	{
		this.sourceLengthSortedlist = sourceLengthSortedlist;
	}

	public void setSourceLengthTable(Hashtable<Integer, Vector<TermTagObject>> sourceLengthTable)
	{
		this.sourceLengthTable = sourceLengthTable;
	}

	public void setSourceStemTable(Hashtable<String, Vector<TermTagObject>> sourceStemTable)
	{
		this.sourceStemTable = sourceStemTable;
	}

	public void setSourceTable(Hashtable<String, Vector<TermTagObject>> sourceTable)
	{
		this.sourceTable = sourceTable;
	}

	public void setSourceTargetSepString(String sourceTargetSepString)
	{
		this.sourceTargetSepString = sourceTargetSepString;
	}

	public void setSourceWordLengthTable(Hashtable<Integer, Vector<TermTagObject>> sourceWordLengthTable)
	{
		this.sourceWordLengthTable = sourceWordLengthTable;
	}

	public void setStemmerTimer(Timer stemmerTimer)
	{
		this.stemmerTimer = stemmerTimer;
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

	public void setTargetLanguage(String targetLanguage)
	{
		this.targetLanguage = targetLanguage;
	}

	public void setTargetLCTable(Hashtable<String, Vector<TermTagObject>> targetLCTable)
	{
		this.targetLCTable = targetLCTable;
	}

	public void setTargetLengthSortedlist(ArrayList<TermTagObject> targetLengthSortedlist)
	{
		this.targetLengthSortedlist = targetLengthSortedlist;
	}

	public void setTargetLengthTable(Hashtable<Integer, Vector<TermTagObject>> targetLengthTable)
	{
		this.targetLengthTable = targetLengthTable;
	}

	public void setTargetStemTable(Hashtable<String, Vector<TermTagObject>> targetStemTable)
	{
		this.targetStemTable = targetStemTable;
	}

	public void setTargetTable(Hashtable<String, Vector<TermTagObject>> targetTable)
	{
		this.targetTable = targetTable;
	}

	public void setTargetWordLengthTable(Hashtable<Integer, Vector<TermTagObject>> targetWordLengthTable)
	{
		this.targetWordLengthTable = targetWordLengthTable;
	}

	public void setWordHandling(WordHandling wordHandling)
	{
		this.wordHandling = wordHandling;
	}

	/**
	 * 
	 */
	public void sortPhraseTableSizes()
	{
		Collections.sort(this.phraseSourceTableSizes);
		Collections.sort(this.phraseTargetTableSizes);
	}

	/**
	 * @return
	 */
	public String stringify()
	{
		Enumeration<Vector<TermTagObject>> enumi = sourceTable.elements();
		// String ret = "Source Table:\n\n";
		StringBuffer ret = new StringBuffer();
		ret.append("Source Table:\n\n");
		while (enumi.hasMoreElements())
		{
			Vector<TermTagObject> vec = enumi.nextElement();
			for (int i = 0; i < vec.size(); i++)
			{
				TermTagObject termob = vec.get(i);
				ret.append(termob.stringify());
			}
			ret.append("\n\n");
		}
		ret.append("Target Table:\n\n");
		enumi = targetTable.elements();
		while (enumi.hasMoreElements())
		{
			Vector<TermTagObject> vec = enumi.nextElement();
			for (int i = 0; i < vec.size(); i++)
			{
				TermTagObject termob = vec.get(i);
				ret.append(termob.stringify());
			}
			ret.append("\n\n");
		}
		return ret.toString();
	}
}
