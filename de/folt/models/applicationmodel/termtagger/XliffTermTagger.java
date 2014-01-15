/**
 * 
 */
package de.folt.models.applicationmodel.termtagger;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom.Attribute;
import org.jdom.Content;
import org.jdom.Element;
import org.jdom.Namespace;

import de.folt.models.applicationmodel.termtagger.TermTagObjectMatch.MATCHTYPE;
import de.folt.models.datamodel.DataSource;
import de.folt.models.datamodel.DataSourceInstance;
import de.folt.models.datamodel.DataSourceProperties;
import de.folt.models.documentmodel.xliff.XliffDocument;
import de.folt.models.documentmodel.xliff.XliffElementHandler;
import de.folt.models.documentmodel.xliff.XliffTokenizer;
import de.folt.util.CorrectTradosIllegalXMLCharacter;
import de.folt.util.OpenTMSException;
import de.folt.util.OpenTMSSupportFunctions;
import de.folt.util.Timer;
import de.folt.util.WordHandling;

/**
 * @author Klemens
 * 
 */
public class XliffTermTagger
{

	public class StructuredMatch
	{
		String	matchCore	= null;

		String	matchEnd	= null;

		String	matchStart	= null;

		int		status		= 0;

		public StructuredMatch(String matchStart, String matchCore, String matchEnd, int status)
		{
			super();
			this.matchStart = matchStart;
			this.matchCore = matchCore;
			this.matchEnd = matchEnd;
			this.status = status;
		}

		public String getMatchCore()
		{
			return matchCore;
		}

		public String getMatchEnd()
		{
			return matchEnd;
		}

		public String getMatchStart()
		{
			return matchStart;
		}

		public int getStatus()
		{
			return status;
		}

		public void setMatchCore(String matchCore)
		{
			this.matchCore = matchCore;
		}

		public void setMatchEnd(String matchEnd)
		{
			this.matchEnd = matchEnd;
		}

		public void setMatchStart(String matchStart)
		{
			this.matchStart = matchStart;
		}

		public void setStatus(int stautus)
		{
			this.status = stautus;
		}

	}

	private static boolean	globalDebug	= false;

	private static String	sourceLanguageWordSplitChars;

	private static String	targetLanguageWordSplitChars;

	private static String	version		= "3.11";

	static boolean getGlobalDebug()
	{
		return XliffTermTagger.globalDebug;
	}

	public static String getSourceLanguageWordSplitChars()
	{
		return sourceLanguageWordSplitChars;
	}

	public static String getTargetLanguageWordSplitChars()
	{
		return targetLanguageWordSplitChars;
	}

	public static String getVersion()
	{
		return version;
	}

	public static boolean isGlobalDebug()
	{
		return globalDebug;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");

			String datasourceName = null;
			String sourceLanguage = null;
			String targetLanguage = null;

			Timer termTagTimer = new Timer();
			termTagTimer.startTimer();
			
			if ((args.length == 1) && args[0].equals("-v"))
			{
				System.out.println("XliffTermTagger - Version " + XliffTermTagger.getVersion() + " Compile Date: "
						+ OpenTMSSupportFunctions.getCompileDate(XliffTermTagger.class));
			}

			if (args.length < 4)
			{
				usage();
				System.exit(10);
				return;
			}

			System.out.println("XliffTermTagger - Version " + XliffTermTagger.getVersion() + " Compile Date: "
					+ OpenTMSSupportFunctions.getCompileDate(XliffTermTagger.class));

			Hashtable<String, String> arguments = de.folt.util.OpenTMSSupportFunctions.argumentReader(args, true);
			String xliffFiles[] = null;

			if (arguments.get("-xliffFile") != null)
			{
				xliffFiles = arguments.get("-xliffFile").split(
						de.folt.util.OpenTMSSupportFunctions.getArgumentsConcatenationString());
				if (xliffFiles.length < 1)
				{
					System.out.println("No xliff file specified");
					System.exit(1);
					return;
				}
			}

			int iProcessors = Runtime.getRuntime().availableProcessors();
			System.out.println("# Processors used: " + iProcessors);

			datasourceName = arguments.get("-dataSource");
			sourceLanguage = arguments.get("-sourceLanguage").toLowerCase();
			targetLanguage = arguments.get("-targetLanguage").toLowerCase();

			sourceLanguageWordSplitChars = arguments.get("-sourceLanguageWordSplitChars");
			targetLanguageWordSplitChars = arguments.get("-targetLanguageWordSplitChars");

			Vector<String> xliffFilesRead = new Vector<String>();

			String filesFromCommandFile = arguments.get("-xliffFileList");
			if (filesFromCommandFile != null)
			{
				xliffFilesRead = OpenTMSSupportFunctions.readFileIntoVector(filesFromCommandFile, "UTF-8");
			}

			if (xliffFiles != null)
			{
				for (int i = 0; i < xliffFiles.length; i++)
				{
					xliffFilesRead.add(xliffFiles[i]);
				}
			}

			String stDebug = arguments.get("-debug");
			if (stDebug != null)
			{
				if (stDebug.equalsIgnoreCase("true"))
					setGlobalDebug(true);
			}

			XliffTermTagger xliffTermTagger = new XliffTermTagger(getGlobalDebug());

			String sdlXliff = arguments.get("-sdlxliff");
			if (sdlXliff != null)
			{
				if (sdlXliff.equalsIgnoreCase("false"))
					xliffTermTagger.setbSDLXliff(false);
				else
					xliffTermTagger.setbSDLXliff(true);
			}

			String segmentsToTag = arguments.get("-segmentsToTag");
			if (segmentsToTag != null)
			{
				String[] segNums = segmentsToTag.split(",");

				Set<Integer> set = new HashSet<Integer>();
				for (int i = 0; i < segNums.length; i++)
				{
					try
					{
						set.add(Integer.parseInt(segNums[i]));
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}

				List<Integer> list = new ArrayList<Integer>(set);
				xliffTermTagger.iSegNums = new int[list.size()];
				for (int i = 0; i < list.size(); i++)
				{
					xliffTermTagger.iSegNums[i] = list.get(i);
				}
				Arrays.sort(xliffTermTagger.iSegNums);
			}

			/* 30.09.2013 */
			if ((sourceLanguage == null) || (targetLanguage == null))
			{
				if (xliffFilesRead.size() > 0)
				{
					Hashtable<String, String> lans = XliffDocument.getFileSourceTargetLanguage(xliffFilesRead.get(0));
					if ((sourceLanguage == null) && (lans.containsKey("source-language")))
					{
						sourceLanguage = lans.get("source-language").toLowerCase();
						System.out.println("TermTagger sourceLanguage read from Xliff file: " + xliffFilesRead.get(0) + " " + sourceLanguage);
					}
					if ((targetLanguage == null) && (lans.containsKey("target-language")))
					{
						targetLanguage = lans.get("target-language").toLowerCase();
						System.out.println("TermTagger targetLanguage read from Xliff file: " + xliffFilesRead.get(0) + " " + targetLanguage);
					}
				}
			}
			/* end 30.09.2013 */

			xliffTermTagger.wordHandling = new WordHandling();
			xliffTermTagger.sourceLanguage = sourceLanguage.toLowerCase();
			xliffTermTagger.targetLanguage = targetLanguage.toLowerCase();
			xliffTermTagger.wordHandling.init();
			if (sourceLanguageWordSplitChars != null)
			{
				xliffTermTagger.wordHandling.setlanguageWordSplitChars(sourceLanguage, sourceLanguageWordSplitChars);
			}
			if (targetLanguageWordSplitChars != null)
			{
				xliffTermTagger.wordHandling.setlanguageWordSplitChars(targetLanguage, targetLanguageWordSplitChars);
			}

			xliffTermTagger.bFuzzy = false;
			xliffTermTagger.fuzzyPercent = 90;
			String stFuzzy = arguments.get("-fuzzyPercent");
			if (stFuzzy != null)
			{
				try
				{
					xliffTermTagger.fuzzyPercent = Integer.parseInt(stFuzzy);
					xliffTermTagger.bFuzzy = true;
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			else
				xliffTermTagger.bFuzzy = false;
			xliffTermTagger.bLowerCase = false;
			String stLowerCase = arguments.get("-lowercase");
			if (stLowerCase != null)
			{
				if (stLowerCase.equalsIgnoreCase("true"))
					xliffTermTagger.bLowerCase = true;
			}

			xliffTermTagger.bStemmed = false;
			String stStemmed = arguments.get("-stemmed");
			if (stStemmed != null)
			{
				if (stStemmed.equalsIgnoreCase("true"))
					xliffTermTagger.bStemmed = true;
			}

			xliffTermTagger.minFuzzyStringLength = 0;
			try
			{
				String stminFuzzyStringLength = arguments.get("-minFuzzyStringLength");
				if (stminFuzzyStringLength != null)
				{
					xliffTermTagger.minFuzzyStringLength = Integer.parseInt(stminFuzzyStringLength);
				}
			}
			catch (Exception e2)
			{
				e2.printStackTrace();
			}

			xliffTermTagger.maxWordLengthSearch = 2;
			try
			{
				String stminFuzzyStringLength = arguments.get("-maxWordLengthSearch");
				if (stminFuzzyStringLength != null)
				{
					xliffTermTagger.maxWordLengthSearch = Integer.parseInt(stminFuzzyStringLength);
				}
			}
			catch (Exception e2)
			{
				e2.printStackTrace();
			}

			xliffTermTagger.minFuzzyStartLength = 2;
			try
			{
				String stminFuzzyStringLength = arguments.get("-minFuzzyStartLength");
				if (stminFuzzyStringLength != null)
				{
					xliffTermTagger.minFuzzyStartLength = Integer.parseInt(stminFuzzyStringLength);
				}
			}
			catch (Exception e2)
			{
				e2.printStackTrace();
			}

			// -sourceStringMatch bzw. -targetStringMatch - 17.12.2013

			xliffTermTagger.bSourceStringMatch = false;
			String sourceStringMatch = arguments.get("-sourceStringMatch");
			if (sourceStringMatch != null)
			{
				if (sourceStringMatch.equalsIgnoreCase("true"))
					xliffTermTagger.bSourceStringMatch = true;
			}

			xliffTermTagger.bTargetStringMatch = false;
			String targetStringMatch = arguments.get("-targetStringMatch");
			if (targetStringMatch != null)
			{
				if (targetStringMatch.equalsIgnoreCase("true"))
					xliffTermTagger.bTargetStringMatch = true;
			}

			// end 17.12.2013

			System.out.println("TermTagger bLowerCase:              " + xliffTermTagger.bLowerCase);
			System.out.println("TermTagger bStemmed:                " + xliffTermTagger.bStemmed);
			System.out.println("TermTagger bFuzzy:                  " + xliffTermTagger.bFuzzy + " / " + xliffTermTagger.fuzzyPercent);
			System.out.println("TermTagger minFuzzyStringLength:    " + xliffTermTagger.minFuzzyStringLength);
			System.out.println("TermTagger maxWordLengthSearch:     " + xliffTermTagger.maxWordLengthSearch);
			System.out.println("TermTagger minFuzzyStartLength:     " + xliffTermTagger.minFuzzyStartLength);
			System.out.println("TermTagger bSourceStringMatch:      " + xliffTermTagger.bSourceStringMatch); // end 17.12.2013
			System.out.println("TermTagger bTargetStringMatch:      " + xliffTermTagger.bTargetStringMatch); // end 17.12.2013
			if (xliffTermTagger.iSegNums != null)
			{
				System.out.print("TermTagger iSegNums:                ");
				for (int i = 0; i < xliffTermTagger.iSegNums.length; i++)
				{
					System.out.print(xliffTermTagger.iSegNums[i] + " ");
				}
				System.out.println();
			}
			else
				System.out.println("TermTagger iSegNums:                Tagging all segments");

			DataSource datasource = null;
			try
			{
				datasource = DataSourceInstance.createInstance(datasourceName);
				try
				{
					DataSourceProperties model = new DataSourceProperties();
					model.put("dataModelClass", "de.folt.models.datamodel.tbxfile.TbxFileDataSource");
					model.put("tbxfile", datasourceName);
					// model.put("dataModelUrl", "openTMS.jar");
					System.out.println(model.toString());
					System.out.println("createInstance" + " TBX:" + datasourceName);
					datasource = DataSourceInstance.createInstance("TBX:" + datasourceName, model);
					System.out.println("createInstance" + " TBX:" + datasourceName + " getLastErrorCode="
							+ datasource.getLastErrorCode() + " >>> " + datasource);
				}
				catch (Exception e1)
				{
					e1.printStackTrace();
					System.exit(2);
					return;
				}
			}
			catch (Exception e)
			{
				try
				{
					DataSourceProperties model = new DataSourceProperties();
					model.put("dataModelClass", "de.folt.models.datamodel.tbxfile.TbxFileDataSource");
					model.put("tbxfile", datasourceName);
					// model.put("dataModelUrl", "openTMS.jar");
					System.out.println(model.toString());
					System.out.println("createInstance" + " TBX:" + datasourceName);
					datasource = DataSourceInstance.createInstance("TBX:" + datasourceName, model);
					System.out.println("createInstance" + " TBX:" + datasourceName + " getLastErrorCode="
							+ datasource.getLastErrorCode() + " >>> " + datasource);
				}
				catch (Exception e1)
				{
					e1.printStackTrace();
					System.exit(3);
					return;
				}
			}

			TermTagObjectTable termTagObjectTable = new TermTagObjectTable(sourceLanguage, targetLanguage);
			termTagObjectTable.setWordHandling(xliffTermTagger.wordHandling);
			termTagObjectTable.setStoredLinguisticProperties("termNote.normativeAuthorization");
			termTagObjectTable.setbFuzzy(xliffTermTagger.bFuzzy);
			termTagObjectTable.setbLowercase(xliffTermTagger.bLowerCase);
			termTagObjectTable.setFuzzyPercent(xliffTermTagger.fuzzyPercent);
			termTagObjectTable.setbStemmed(xliffTermTagger.bStemmed);
			termTagObjectTable.setbSourceStringMatch(xliffTermTagger.bSourceStringMatch);// end 17.12.2013
			termTagObjectTable.setbTargetStringMatch(xliffTermTagger.bTargetStringMatch);// end 17.12.2013
			termTagObjectTable.bAddPhrases(datasource, sourceLanguage, targetLanguage);
			termTagObjectTable.sortPhraseTableSizes();
			termTagObjectTable.setMinFuzzyStringLength(xliffTermTagger.minFuzzyStringLength);
			termTagObjectTable.setMinFuzzyStartLength(xliffTermTagger.minFuzzyStartLength);
			termTagObjectTable.setMaxWordLengthSearch(xliffTermTagger.maxWordLengthSearch);
			// termTagObjectTable.setMaxSourcePhraseLength(xliffTermTagger.maxWordLengthSearch);
			// termTagObjectTable.setMaxTargetPhraseLength(xliffTermTagger.maxWordLengthSearch);

			System.out.println(termTagObjectTable.paramsToString());

			System.out.println("Termtable: Max Source Word Length:      " + termTagObjectTable.getMaxSourcePhraseLength());
			System.out.println("Termtable: Max Target Word Length:      " + termTagObjectTable.getMaxTargetPhraseLength());
			System.out.println("Termtable: Source Length Table:         " + termTagObjectTable.getSourceLengthTable().size());
			System.out.println("Termtable: Target Length Table:         " + termTagObjectTable.getTargetLengthTable().size());
			System.out.println("Termtable: Source Word Length Table:    " + termTagObjectTable.getSourceWordLengthTable().size());
			System.out.println("Termtable: Target Word Length Table:    " + termTagObjectTable.getTargetWordLengthTable().size());

			if (XliffTermTagger.getGlobalDebug())
			{
				System.out.println("Termtable:");
				System.out.println(termTagObjectTable.stringify());
			}

			termTagObjectTable.printALengthTable("Source Length Table", termTagObjectTable.getSourceLengthTable());
			termTagObjectTable.printALengthTable("Target Length Table", termTagObjectTable.getTargetLengthTable());
			termTagObjectTable.printALengthTable("Source Word Length Table", termTagObjectTable.getSourceWordLengthTable());
			termTagObjectTable.printALengthTable("Target Word Length Table", termTagObjectTable.getTargetWordLengthTable());

			// create a list of string length sorted entries
			termTagObjectTable.createTermStringLengthSortedList();
			if (XliffTermTagger.getGlobalDebug())
			{
				System.out.println("Sorted Termtable List:");
				System.out.println(termTagObjectTable.lengthPrint());
			}

			for (int i = 0; i < xliffFilesRead.size(); i++)
			{
				System.out.println("Tagging xliff file " + (i + 1) + ": " + xliffFilesRead.get(i));
				try
				{
					File f = new File(xliffFilesRead.get(i));
					if (!f.exists())
					{
						System.out.println("xliff file " + (i + 1) + ": " + xliffFilesRead.get(i) + " does not exist!");
						System.exit(4);
					}
					boolean bSuccess = xliffTermTagger.termTag(xliffFilesRead.get(i), sourceLanguage, targetLanguage,
							termTagObjectTable);
					if (!bSuccess)
					{
						System.exit(5);
					}
				}
				catch (Exception e)
				{
					System.out.println("Tagging xliff file " + (i + 1) + ": " + xliffFilesRead.get(i) + " failed!");
					e.printStackTrace();
					System.exit(6);
				}
			}

			termTagObjectTable.getExactTimer().endTimer();
			if (xliffTermTagger.bLowerCase)
				termTagObjectTable.getLcTimer().endTimer();
			if (xliffTermTagger.bStemmed)
				termTagObjectTable.getStemmerTimer().endTimer();
			if (xliffTermTagger.bFuzzy)
				termTagObjectTable.getFuzzyTimer().endTimer();
			xliffTermTagger.markTimer.endTimer();
			System.out.println("Time in exact search:     " + (termTagObjectTable.getExactTimer().timeNeeded()) / 1000.00);
			System.out.println("Time in lowercase search: " + (termTagObjectTable.getLcTimer().timeNeeded()) / 1000.00);
			System.out.println("Time in stemmer search:   " + (termTagObjectTable.getStemmerTimer().timeNeeded()) / 1000.00);
			System.out.println("Time in fuzzy search:     " + (termTagObjectTable.getFuzzyTimer().timeNeeded()) / 1000.00);
			System.out.println("Time in overall search:   "
					+ ((termTagObjectTable.getExactTimer().timeNeeded())
							+ (termTagObjectTable.getLcTimer().timeNeeded())
							+ (termTagObjectTable.getStemmerTimer().timeNeeded())
							+ (termTagObjectTable.getFuzzyTimer().timeNeeded())) / 1000.00);
			System.out.println("Time in Markup:           " + (xliffTermTagger.markTimer.timeNeeded()) / 1000.00);
			System.out.println("findTerms called:                     " + termTagObjectTable.getiFindTermsCalled());
			System.out.println("All Words in document:                " + termTagObjectTable.getIfindTermsWords());
			System.out.println("All Word Combinations searched:       " + termTagObjectTable.getiAllSourceWordCombination());
			System.out.println("Similarity called:                    " + termTagObjectTable.getiSimilarityComputed());
			System.out.println("Similarity matched:                   " + termTagObjectTable.getiSimilarityMatched());
			System.out.println("iResultingVecSizeZero:                " + termTagObjectTable.getiResultingVecSizeZero());
			System.out.println("iWordNotFoundInLengthWordTable:       " + termTagObjectTable.getiWordNotFoundInLengthWordTable());
			System.out.println("iStartFuzzyDoesNotMatch:              " + termTagObjectTable.getiStartFuzzyDoesNotMatch());
			System.out.println("getiVecSizeToCheck:                   " + termTagObjectTable.getiVecSizeToCheck());
			termTagTimer.stopTimer();
			termTagTimer.endTimer();
			System.out.println(termTagTimer.timerString("Finished Termtagging " + xliffFilesRead.size() + " files tagged: "));

		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(7);
		}

		System.exit(0);

	}

	public static void setGlobalDebug(boolean globalDebug)
	{
		XliffTermTagger.globalDebug = globalDebug;
	}

	public static void setSourceLanguageWordSplitChars(String sourceLanguageWordSplitChars)
	{
		XliffTermTagger.sourceLanguageWordSplitChars = sourceLanguageWordSplitChars;
	}

	public static void setTargetLanguageWordSplitChars(String targetLanguageWordSplitChars)
	{
		XliffTermTagger.targetLanguageWordSplitChars = targetLanguageWordSplitChars;
	}

	public static void setVersion(String version)
	{
		XliffTermTagger.version = version;
	}

	public static void usage()
	{
		System.out.println("XliffTermTagger - Version " + XliffTermTagger.getVersion() + " Compile Date: "
				+ OpenTMSSupportFunctions.getCompileDate(XliffTermTagger.class));
		System.out
				.println("Usage: java-cp \".;lib\\OpenTMS.jar;lib\\external.jar;\" -Xmx1000M de.folt.models.applicationmodel.termtagger.XliffTermTagger -xliffFile <ein oder mehrere xliff Dateien durch Leerzeichen getrennt> -xliffFileList <a file name containing the files to be tagged; line per line> -dataSource <tbx Datei (mit absoluter Pfadangabe!)> -sourceLanguage <Ausgangssprache> -targetLanguage <Zielsprache> [-stemmed <{false}|true>] [-fuzzyPercent <integer>] [-sourceStringMatch <{false}|true>] [-targetStringMatch <{false}|true>] [-debug <{false}|true>]");
		System.out.println("\t[...] optionaler Parameter / {...} Default Wert");
		System.out
			.println("Usage: java-cp \".;lib\\OpenTMS.jar;lib\\external.jar;\" -Xmx1000M de.folt.models.applicationmodel.termtagger.XliffTermTagger -v | Ausgabe der Versionsinformation");

	}

	private boolean				bFuzzy					= false;

	private boolean				bLowerCase				= false;

	private boolean				bSDLXliff				= true;

	private boolean				bSourceStringMatch		= false;

	private boolean				bStemmed				= false;

	private boolean				bTargetStringMatch		= false;

	private boolean				debug					= false;

	private XliffDocument		doc						= null;

	private int					fuzzyPercent			= 100;

	private int[]				iSegNums				= null;

	private Timer				markTimer				= new Timer();

	private int					maxWordLengthSearch		= -1;

	private int					minFuzzyStartLength;

	private int					minFuzzyStringLength	= -1;

	private int					numberOfTaggings		= 0;

	private String				sourceLanguage			= "";

	private String				targetLanguage			= "";

	private TermTagObjectTable	termTagObjectTable		= null;

	private WordHandling		wordHandling			= new WordHandling();

	private XliffTokenizer		xliffTokenizer			= null;

	public XliffTermTagger(boolean debug)
	{
		this.debug = debug;
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
	public Element addToTransUnit(Element transUnit, Vector<TermTagObject> result, String sourceLanguage,
			String targetLanguage, DataSource dataSource)
	{
		Element propgroup = new Element("prop-group");
		propgroup.setAttribute("name", "termTagger:" + dataSource.getDataSourceName());
		for (int i = 0; i < result.size(); i++)
		{
			TermTagObject res = result.get(i);
			Element sourceprop = new Element("prop");
			sourceprop.setAttribute("lang", sourceLanguage, Namespace.XML_NAMESPACE);
			sourceprop.setAttribute("prop-type", "term:" + res.getUniqueID());
			sourceprop.setText(res.getTerm() + ":" + res.formatAttribute("termNote.normativeAuthorization"));
			propgroup.addContent(sourceprop);

		}
		transUnit.addContent(propgroup);
		return transUnit;
	}

	/**
	 * @param term
	 * @param segmentString
	 * @return
	 */
	public boolean bContainsTerm(String term, String segmentString)
	{
		boolean bFound = false;
		if (segmentString == null)
			return false;
		if (segmentString.equals(""))
			return false;

		if (term.equals(segmentString))
			return true;

		XliffElementHandler xliffsegmentString = new XliffElementHandler(segmentString);
		segmentString = xliffsegmentString.getEncodedXliffString();

		// XliffElementHandler xliffterm = new XliffElementHandler(term);
		XliffElementHandler xliffterm = new XliffElementHandler(term);
		term = xliffterm.getEncodedXliffString();

		String patternuser = "[" + XliffElementHandler.getStartReplaceChar() + "-"
				+ XliffElementHandler.getFinalReplaceChar() + "]+";
		String patternusers = ".*?([" + Pattern.quote(WordHandling.getDefaultWordSplitChars()) + "\\s"
				+ XliffElementHandler.getStartReplaceChar() + "-" + XliffElementHandler.getFinalReplaceChar() + "]";
		String patternusere = "[" + Pattern.quote(WordHandling.getDefaultWordSplitChars()) + "\\s"
				+ XliffElementHandler.getStartReplaceChar() + "-" + XliffElementHandler.getFinalReplaceChar() + "]).*?";

		String patternterm = term.replaceAll("\\s+", patternuser);
		patternterm = Pattern.quote(patternterm); // wk 05.12.2012
		String pattern1 = patternusers + patternterm + patternusere; // between
		String pattern2 = "^(" + patternterm + patternusere; // at start
		String pattern3 = patternusers + patternterm + ")$"; // at end
		String pattern4 = "^(" + patternterm + ")$"; // just the term
		String pattern5 = "(" + XliffElementHandler.getStopReplaceChar() + patternterm
				+ XliffElementHandler.getStopReplaceChar() + ")";
		String pattern = pattern1 + "|" + pattern2 + "|" + pattern3 + "|" + pattern4 + "|" + pattern5;

		Pattern matchPattern = Pattern.compile(pattern);
		Matcher matcher = matchPattern.matcher(segmentString);
		if (matcher.matches())
			return true;
		return bFound;
	}

	/**
	 * @param xliffSourceCoded
	 * @param language
	 * @param termTagObjectTable
	 * @return
	 */
	@SuppressWarnings("unused")
	private Vector<TermTagObjectMatch> convertToTermTagObjectMatches(XliffElementHandler xliffSourceCoded,
			String language,
			TermTagObjectTable termTagObjectTable)
	{
		Vector<TermTagObjectMatch> result = new Vector<TermTagObjectMatch>();
		String sourceResult = xliffSourceCoded.getEncodedXliffString();
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
					result.add(termTagObjectMatch);
					buf = new StringBuffer();
				}
				while ((i < sourceResult.length())
						&& sourceResult.charAt(i) >= XliffElementHandler.getStartReplaceChar())
				{
					buf.append(sourceResult.charAt(i));
					i++;
				}
				termTagObjectMatch = new TermTagObjectMatch(language, buf.toString());
				result.add(termTagObjectMatch);
				buf = new StringBuffer();
				i--;
			}
			i++;
		}

		if (buf.length() > 0)
		{
			termTagObjectMatch = new TermTagObjectMatch(language, buf.toString());
			result.add(termTagObjectMatch);
		}

		return result;
	}

	private void debugOut(Element source, Element target, Vector<String> sourceVec, Vector<TermTagObjectMatch> phraseSourceFound, String marker)
	{
		System.out.println(marker);
		System.out.println("Source Segment:" + doc.elementContentToString(source, false));
		if (target != null)
			System.out.println("Target Segment:" + doc.elementContentToString(target, false));
		System.out.println();
		for (int k = 0; k < sourceVec.size(); k++)
		{
			System.out.print("\"" + sourceVec.get(k) + "\"");
			if (k != (sourceVec.size() - 1))
				System.out.print(", ");
			else
				System.out.println();
		}
		for (int k = 0; k < phraseSourceFound.size(); k++)
		{
			System.out.println(phraseSourceFound.get(k).stringify());
		}
	}

	public int getFuzzyPercent()
	{
		return fuzzyPercent;
	}

	public int[] getiSegNums()
	{
		return iSegNums;
	}

	public int getNumberOfTaggings()
	{
		return numberOfTaggings;
	}

	public String getSourceLanguage()
	{
		return sourceLanguage;
	}

	public String getTargetLanguage()
	{
		return targetLanguage;
	}

	public TermTagObjectTable getTermTagObjectTable()
	{
		return termTagObjectTable;
	}

	public WordHandling getWordHandling()
	{
		return wordHandling;
	}

	public XliffTokenizer getXliffTokenizer()
	{
		return xliffTokenizer;
	}

	public boolean isbFuzzy()
	{
		return bFuzzy;
	}

	public boolean isbLowerCase()
	{
		return bLowerCase;
	}

	public boolean isbSDLXliff()
	{
		return bSDLXliff;
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

	public boolean isDebug()
	{
		return debug;
	}

	@SuppressWarnings("unused")
	private Vector<String> removeDuplicateEntries(Vector<String> vector)
	{
		return new Vector<String>(new LinkedHashSet<String>(vector));
	}

	private Vector<String> removeWhiteStuff(Element element, String language)
	{
		Vector<String> vector = xliffTokenizer.tokenizeToVector(element, language);
		return removeWhiteStuff(vector, language);
	}

	private Vector<String> removeWhiteStuff(Vector<String> vector, String language)
	{
		vector = xliffTokenizer.removeMrkTokens(vector);
		vector = xliffTokenizer.removeElement(vector, "source");
		vector = xliffTokenizer.removeElement(vector, "seg-source");
		vector = xliffTokenizer.removeElement(vector, "target");
		vector = xliffTokenizer.removeString(vector, " +");
		vector = xliffTokenizer.removeString(vector, "<.*?>");
		vector = xliffTokenizer.removeInString(vector, "\\s+");
		vector = xliffTokenizer.removeInString(vector, "\\r");
		vector = xliffTokenizer.removeInString(vector, "\\n");
		vector = xliffTokenizer.removeInString(vector, "\\t");
		vector = xliffTokenizer.removeString(vector,
				"[" + Pattern.quote(xliffTokenizer.getWordHandling().getSplitChars(language)) + "]+");
		vector = xliffTokenizer.removeString(vector, "^$");
		return vector;
	}

	public void setbFuzzy(boolean bFuzzy)
	{
		this.bFuzzy = bFuzzy;
	}

	public void setbLowerCase(boolean bLowerCase)
	{
		this.bLowerCase = bLowerCase;
	}

	public void setbSDLXliff(boolean bSDLXliff)
	{
		this.bSDLXliff = bSDLXliff;
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

	public void setDebug(boolean debug)
	{
		this.debug = debug;
	}

	// <mrk mtype="x-term-transNotFound-admitted" mid="term_1_de-DE_1">
	// <mrk mtype="x-term-admitted-transFound" mid="term_1_de-DE_1">
	// <mrk mtype="x-term-transNotFound" mid="term_3_de-DE_1">
	// <mrk mtype="x-term-preferred" mid="term_2_en-GB_1">
	// <mrk mtype="x-term-notRecommended" mid="term_2_en-GB_2">

	public void setFuzzyPercent(int fuzzyPercent)
	{
		this.fuzzyPercent = fuzzyPercent;
	}

	public void setiSegNums(int[] iSegNums)
	{
		this.iSegNums = iSegNums;
	}

	public void setNumberOfTaggings(int numberOfTaggings)
	{
		this.numberOfTaggings = numberOfTaggings;
	}

	public void setSourceLanguage(String sourceLanguage)
	{
		this.sourceLanguage = sourceLanguage;
	}

	public void setTargetLanguage(String targetLanguage)
	{
		this.targetLanguage = targetLanguage;
	}

	public void setTermTagObjectTable(TermTagObjectTable termTagObjectTable)
	{
		this.termTagObjectTable = termTagObjectTable;
	}

	public void setWordHandling(WordHandling wordHandling)
	{
		this.wordHandling = wordHandling;
	}

	public void setXliffTokenizer(XliffTokenizer xliffTokenizer)
	{
		this.xliffTokenizer = xliffTokenizer;
	}

	/**
	 * @param matchedString
	 * @param patternuser
	 * @param xliffSourceCoded
	 * @return
	 */
	@SuppressWarnings("unused")
	private StructuredMatch structureMatchedString(String term, String patternuser, XliffElementHandler xliffSourceCoded)
	{
		// we do not allow tags in matches

		String matchedString = term;
		if ((matchedString != null) && false)
		{
			if (xliffSourceCoded.checkForTermMrk(matchedString))
				return new StructuredMatch("", "", "", 0);
			if (!xliffSourceCoded.isValidElementOrText(matchedString, doc))
				return new StructuredMatch("", "", "", 0);
			// if
			// (xliffSourceCoded.checkForNotEmptyElement(matchedString))
			// continue;
		}

		if (matchedString == null)
			matchedString = term;

		String matchStart = matchedString.replaceFirst("^(" + patternuser + ").*", "$1");
		String matchEnd = matchedString.replaceFirst(".*?(" + patternuser + ")$", "$1");
		String matchCore = matchedString.replaceFirst("^" + patternuser + "(.*?)" + patternuser + "$", "$1");
		if (matchCore.equals(matchedString))
			matchCore = matchedString.replaceFirst("^" + patternuser + "(.*?)$", "$1");
		if (matchCore.equals(matchedString))
			matchCore = matchedString.replaceFirst("(.*)" + patternuser + "$", "$1");

		if (matchStart.equals(matchedString))
			matchStart = "";
		if (matchEnd.equals(matchedString))
			matchEnd = "";

		// we must check if the matchStart and matchEnd contains < and >
		// elements; elements cannot be moved outside the mrk context
		// except if they are empty tags
		// and must be appended to the core term
		// this could obviously be improved

		// first count number of tags in

		int l = matchStart.length();
		int iStartTagCount = 0;
		if (l > 0)
		{
			for (int n = 0; n < l; n++)
			{
				char c = matchStart.charAt(n);
				if (xliffSourceCoded.checkForElement(c))
				{
					iStartTagCount++;
				}
			}
		}

		l = matchEnd.length();
		int iEndTagCount = 0;
		if (l > 0)
		{
			for (int n = l - 1; n >= 0; n--)
			{
				char c = matchEnd.charAt(n);
				if (xliffSourceCoded.checkForElement(c))
				{
					iEndTagCount++;
				}
			}
		}

		l = matchStart.length();
		if ((l > 0) && (iEndTagCount > 0))
		{
			for (int n = 0; n < l; n++)
			{
				char c = matchStart.charAt(n);
				if (xliffSourceCoded.checkForElement(c))
				{
					String mafrom = matchStart.substring(n);
					matchCore = mafrom + matchCore;
					matchStart = matchStart.substring(0, n);
					break;
				}
			}
		}

		l = matchEnd.length();
		if (l > 0 && (iStartTagCount > 0))
		{
			for (int n = l - 1; n >= 0; n--)
			{
				char c = matchEnd.charAt(n);
				if (xliffSourceCoded.checkForElement(c))
				{
					matchCore = matchCore + matchEnd.substring(0, n + 1);
					matchEnd = matchEnd.substring(n + 1);
					break;
				}
			}
		}

		// we do not allow tags in matches
		if (matchedString != null)
		{
			if (xliffSourceCoded.checkForTermMrk(matchCore))
				return new StructuredMatch(matchStart, matchCore, matchEnd, 0);
			if (!xliffSourceCoded.isValidElementOrText(matchCore, doc))
				return new StructuredMatch(matchStart, matchCore, matchEnd, 0);
			// if
			// (xliffSourceCoded.checkForNotEmptyElement(matchedString))
			// continue;
		}

		// this is a final check now
		// we check if we can move starting and terminating elements to
		// matchStart and matchEnd

		l = matchCore.length();
		if (l > 1)
		{
			StringBuffer coreBuffer = new StringBuffer(matchCore);
			int n = 0;
			while (n < coreBuffer.length())
			{
				char cStart = coreBuffer.charAt(n);
				char cEnd = coreBuffer.charAt(coreBuffer.length() - n - 1);
				String elementStart = xliffSourceCoded.getElementName(cStart);
				String elementEnd = xliffSourceCoded.getElementName(cEnd);
				if ((elementStart != null) && (elementEnd != null) && elementStart.equals(elementEnd))
				{
					// do something
					matchEnd = cEnd + matchEnd;
					matchStart = matchStart + cStart;
					coreBuffer.deleteCharAt(coreBuffer.length() - n - 1);
					coreBuffer.deleteCharAt(n);
				}
				else
				{
					break;
				}
				n++;
			}
			matchCore = coreBuffer.toString();
		}

		return new StructuredMatch(matchStart, matchCore, matchEnd, 1);

	}

	// bTagSource = true = source taggen, false target taggen
	private String tagStringWithMrk(String sourceString, String targetString, Vector<TermTagObjectMatch> phraseSourceFound,
			Vector<TermTagObjectMatch> phraseTargetFound, boolean bTagSource)
	{
		String language = this.sourceLanguage;
		ArrayList<TermTagObject> table = termTagObjectTable.getSourceLengthSortedlist();
		if (bTagSource == false)
		{
			language = this.targetLanguage;
			table = termTagObjectTable.getTargetLengthSortedlist();
		}
		
		
		XliffElementHandler xliffSourceCoded = new XliffElementHandler(sourceString, null, language);
		String sourceResult = xliffSourceCoded.getEncodedXliffString();
		Hashtable<String, String> markTable = new Hashtable<String, String>();
		for (int k = 0; k < table.size(); k++)
		{
			TermTagObject source = table.get(k);

			if (sourceResult.contains(source.getTerm()))
			{
				String mid = source.getTermElementID();
				if ((mid == null) || mid.equals(""))
					mid = source.getUniqueID().replaceAll("tig", "term");
				String mtype = "x-term-";

				if ((source.getAttribute("termNote.normativeAuthorization") != null)
						&& !source.getAttribute("termNote.normativeAuthorization").equals(""))
				{
					mtype = mtype
							+ source.getAttribute("termNote.normativeAuthorization").replaceAll("termNote\\.", "");
				}
				else
				{
					mtype = mtype + "admittedTerm";
				}
				
				TermTagObject target = (TermTagObject) table.get(k).getTranslation();
				String targetterm = null;
				boolean bTargetFound = false;

				if ((target != null) && (targetString != null) && !targetString.equals(""))
				{
					targetterm = target.getTerm();
					bTargetFound = bContainsTerm(targetterm, targetString);
				}
				
				if (bTargetFound)
				{
					mtype = mtype + "-transFound";
				}
				else if (source.getTranslation() != null)
				{
					mtype = mtype + "-transNotFound";
				}
				else
				{
					mtype = mtype + "-transNotDefined";
				}

				String matchtype = "EXACT";

				mtype = mtype + "-" + (matchtype + "").toLowerCase();
				String replacement = "<mrk  mtype=\"" + mtype + "\" mid=\"" + mid + "\">" + source.getTerm() + "</mrk>";
				String mark = XliffElementHandler.getStartReplaceChar() + k + "";
				markTable.put(mark, replacement);
				sourceResult = sourceResult.replaceAll(Matcher.quoteReplacement(source.getTerm()), mark);
			}
		}

		Enumeration<String> en = markTable.keys();
		while (en.hasMoreElements())
		{
			String mark = en.nextElement();
			String replacement = markTable.get(mark);
			sourceResult = sourceResult.replaceAll(mark, replacement);
		}

		sourceResult = xliffSourceCoded.getDecodedXliffString(sourceResult);
		sourceResult = xliffSourceCoded.getDecodedXliffString(sourceResult);
		
		return sourceResult;
	}

	// bTagSource = true = source taggen, false target taggen
	private String tagWithMrk(String sourceString, String targetString, Vector<TermTagObjectMatch> phraseSourceFound,
			Vector<TermTagObjectMatch> phraseTargetFound, boolean bTagSource)
	{
		String language = this.sourceLanguage;
		if (bTagSource == false)
		{
			language = this.targetLanguage;
		}

		XliffElementHandler xliffSourceCoded = null;
		String sourceResult = null;

		if (bTagSource && this.isbSourceStringMatch())
		{
			return tagStringWithMrk(sourceString, targetString, phraseSourceFound, phraseTargetFound, bTagSource);
		}
		if (!bTagSource && this.isbTargetStringMatch())
		{
			return tagStringWithMrk(sourceString, targetString, phraseSourceFound, phraseTargetFound, bTagSource);
		}

		if (xliffSourceCoded == null)
		{
			xliffSourceCoded = new XliffElementHandler(sourceString, this.wordHandling, language);
			sourceResult = xliffSourceCoded.getEncodedXliffString();
		}
		// a list is now created from the source result where each word appears
		// as single list element
		String defaultWordSplitchars = this.wordHandling.getSplitChars(this.sourceLanguage);
		if (bTagSource == false)
		{
			defaultWordSplitchars = this.wordHandling.getSplitChars(this.targetLanguage);
		}
		if (debug)
		{
			System.out.println("_____" + xliffSourceCoded.stringify(sourceResult));
		}
		String patternuser = "[" + XliffElementHandler.getStartReplaceChar() + "-"
				+ XliffElementHandler.getFinalReplaceChar() + "]+";
		String patternusers = ".*?([" + Pattern.quote(defaultWordSplitchars) + "\\s"
				+ XliffElementHandler.getStartReplaceChar() + "-" + XliffElementHandler.getFinalReplaceChar() + "]";
		String patternusere = "[" + Pattern.quote(defaultWordSplitchars) + "\\s"
				+ XliffElementHandler.getStartReplaceChar() + "-" + XliffElementHandler.getFinalReplaceChar() + "]).*?";

		// here we have generic problem the phrases are checked in this order
		// it can appear now that it finds a source phrase where the same source
		// phrase (actually appearing as two source phrase with the same
		// segment) has two translations
		// where one translation exists in the target. Depending on the order it
		// will use one of them... and could result in transFound or
		// transNotFound
		for (int k = 0; k < phraseSourceFound.size(); k++)
		{
			TermTagObjectMatch source = phraseSourceFound.get(k);

			// now we need a loop across all TermInternalMatches
			Vector<TermInternalMatch> termInternalMatches = source.getTermInternalMatch();
			imloop: for (int im = 0; im < termInternalMatches.size(); im++)
			{
				String term = termInternalMatches.get(im).getTermMatch(); // source.getTerm(); // wk 26.11.2012
				MATCHTYPE matchtype = termInternalMatches.get(im).getMatchType();
				// if (matchtype == MATCHTYPE.FUZZY)

				// commented out 20.12 term = Pattern.quote(term); // wk 05.12.2012
				TermTagObject target = (TermTagObject) phraseSourceFound.get(k).getTranslation();
				String targetterm = null;
				boolean bTargetFound = false;

				if ((target != null) && (targetString != null) && !targetString.equals(""))
				{
					targetterm = target.getTerm();
					bTargetFound = bContainsTerm(targetterm, targetString);
				}
				// now let us check if the following term is the same term
				if (!bTargetFound)
				{
					for (int j = k + 1; j < phraseSourceFound.size(); j++)
					{
						TermTagObjectMatch sourcenext = phraseSourceFound.get(j);
						String termnext = sourcenext.getTermMatch(); // sourcenext.getTerm(); wk 26.11.2012

						if (term.equals(termnext))
						{
							TermTagObject translationnext = (TermTagObject) sourcenext.getTranslation();
							if (translationnext != null)
							{
								String translation = translationnext.getTerm();
								bTargetFound = bContainsTerm(translation, targetString);
								if (bTargetFound)
								{
									break;
								}
							}
						}
					}
					if (bTargetFound)
						continue;
				}

				// quote only words
				// term = Pattern.quote(term);
				// term = term.replaceAll("^(.*?)(\\s+)", "\\Q" + "$1" + "\\E" + "$1");
				// term = term.replaceAll("(.*?)(\\s+)$", "$1\\Q" + "$2" + "\\E");
				// term = term.replaceAll("(\\s+)(.*?)(\\s+)", "$1" + "\\Q" + "$2" + "\\E" + "$3");

				String patternterm = term.replaceAll("\\s+", "\\\\E" + patternuser + "\\\\Q");
				patternterm = "\\Q" + patternterm + "\\E";
				String pattern2 = patternusers + patternterm + patternusere; // between
				String pattern1 = "^(" + patternterm + patternusere; // at start
				String pattern3 = patternusers + patternterm + ")$"; // at end
				String pattern4 = "^(" + patternterm + ")$"; // just the term
				String pattern5 = ".*?(" + XliffElementHandler.getStopReplaceChar() + patternterm
						+ XliffElementHandler.getStopReplaceChar() + ").*?";
				String pattern6 = ".*?(" + XliffElementHandler.getStopReplaceChar() + patternterm + patternusere;
				String pattern7 = ".*?(" + XliffElementHandler.getStopReplaceChar() + patternterm + ")$";
				String pattern8 = ".*?(" + patternterm + XliffElementHandler.getStopReplaceChar() + ")";
				// String pattern = pattern1 + "|" + pattern2 + "|" + pattern3 + "|" + pattern4 + "|" + pattern5;
				String[] patterns = { pattern1, pattern2, pattern3, pattern4, pattern5, pattern6, pattern7, pattern8 };

				if (debug)
				{
					for (int m = 0; m < patterns.length; m++)
					{
						System.out.println("=====(" + m + "):" + xliffSourceCoded.stringify(patterns[m]));
					}
				}
				while (true)
				{
					String startSourceResult = sourceResult;
					for (int m = 0; m < patterns.length; m++)
					{
						Pattern matchPattern = Pattern.compile(patterns[m]);
						// String startSourceResult = sourceResult;
						Matcher matcher = matchPattern.matcher(sourceResult);
						while (matcher.find())
						{
							String matchedString = term;
							for (int i = 1; i <= matcher.groupCount(); i++)
							{
								matchedString = matcher.group(i);
								if (matchedString != null)
								{
									if (debug)
									{
										System.out.println("????? " + i);
									}
								}

								if (matchedString == null)
									continue; // matchedString = term;

								StructuredMatch structuredMatch = structureMatchedString(matchedString, patternuser, xliffSourceCoded);

								String matchStart = structuredMatch.getMatchStart();
								String matchEnd = structuredMatch.getMatchEnd();
								String matchCore = structuredMatch.getMatchCore();

								if (structuredMatch.getStatus() == 0)
								{
									continue;
								}

								// 06.12.2013
								// we must check if this is a fuzzy match
								// if fuzzy match we should check if the next following matches contain an exact match or a better fuzzy match
								if (matchtype == MATCHTYPE.FUZZY)
								{
									for (int in = im + 1; in < termInternalMatches.size(); in++)
									{
										String termnext = termInternalMatches.get(in).getTermMatch(); // source.getTerm(); // wk 26.11.2012
										@SuppressWarnings("unused")
										MATCHTYPE matchtypenext = termInternalMatches.get(in).getMatchType();
										String plainmatchedString = matchedString.substring(1, matchedString.length() - 1);
										// theoretically we should also check if there is better fuzzy match...
										if (termnext.equals(plainmatchedString) || termInternalMatches.get(in).getFuzzy() > termInternalMatches.get(im).getFuzzy())
										{
											// break into loop im
											System.out.println("????? Check next match after" + im + " -> " + in);
											break imloop;
										}
									}
								}

								String mid = source.getTermElementID();
								if ((mid == null) || mid.equals(""))
									mid = source.getUniqueID().replaceAll("tig", "term");
								String mtype = "x-term-";

								if ((source.getAttribute("termNote.normativeAuthorization") != null)
										&& !source.getAttribute("termNote.normativeAuthorization").equals(""))
								{
									mtype = mtype
											+ source.getAttribute("termNote.normativeAuthorization").replaceAll("termNote\\.", "");
								}
								else
								{
									mtype = mtype + "admittedTerm";
								}

								// Im Zielsegment gefunden - transFound
								// Im Zielsegment nicht gefunden, aber Übersetzung in TBX
								// definiert - transNotFound
								// Im Zielsegment nicht gefunden, und Übersetzung in TBX NICHT
								// definiert - transNotDefined
								// bTargetFound = false - nicht gefunden im Ziel / true im Ziel
								// gefunden
								if (!bTargetFound && (phraseTargetFound != null))
								{
									// fuzzy etc. matching may cause a problem
									// simple check if the same is appears in the target phrase
									String sID = source.getTermElementID();
									sID = sID.replaceAll("(.*?_.*?)_.*", "$1");
									for (int l = 0; l < phraseTargetFound.size(); l++)
									{
										String tID = phraseTargetFound.get(l).getTermElementID().replaceAll("(.*?_.*?)_.*", "$1");
										if (sID.equals(tID))
											bTargetFound = true;
									}
								}

								if (bTargetFound)
								{
									mtype = mtype + "-transFound";
								}
								else if (source.getTranslation() != null)
								{
									mtype = mtype + "-transNotFound";
								}
								else
								{
									mtype = mtype + "-transNotDefined";
								}

								// MATCHTYPE matchtype = termInternalMatches.get(im).getMatchType();
								mtype = mtype + "-" + (matchtype + "").toLowerCase();
								if (matchtype == MATCHTYPE.FUZZY)
									mtype = mtype + "-" + termInternalMatches.get(im).getFuzzy();
								if (bSDLXliff == false)
									mtype = mtype + "-" + termInternalMatches.get(im).getTermMatch();

								String mrk = matchStart + "<MRK mtype=\"" + mtype + "\" mid=\"" + mid + "\">" + matchCore + "</MRK>"
										+ matchEnd;

								if (debug)
								{
									System.out.println("-----" + xliffSourceCoded.stringify(matchedString) + " ->" + mrk);
									System.out.println("....." + xliffSourceCoded.stringify(sourceResult) + " ->" + mrk);
								}

								while (true)
								{
									char nextFreeSourceChar = xliffSourceCoded.add(mrk);
									String oldSourceresult = sourceResult;
									sourceResult = sourceResult.replaceFirst(Pattern.quote(matchedString), XliffElementHandler.getStopReplaceChar() + ""
											+ nextFreeSourceChar + "" + XliffElementHandler.getStopReplaceChar());
									if (sourceResult.equals(oldSourceresult))
									{
										break;
									}
									if (debug)
									{
										System.out.println(":::::" + xliffSourceCoded.stringify(sourceResult) + " ->" + mrk);
									}
								}

								numberOfTaggings++;
							}
						}
						// if (startSourceResult.equals(sourceResult))
						// break;
					}
					if (startSourceResult.equals(sourceResult))
						break;
				}
			}
		}
		// must be done twice
		sourceResult = xliffSourceCoded.getDecodedXliffString(sourceResult);
		sourceResult = xliffSourceCoded.getDecodedXliffString(sourceResult);
		sourceResult = sourceResult.replaceAll("<MRK mtype=", "<mrk mtype=");
		sourceResult = sourceResult.replaceAll(Pattern.quote("</MRK>"), "</mrk>");
		return sourceResult;
	}

	/**
	 * @param xliffFile
	 * @param sourceLanguage
	 * @param targetLanguage
	 * @param termTagObjectTable
	 */
	public boolean termTag(String xliffFile, String sourceLanguage, String targetLanguage,
			TermTagObjectTable termTagObjectTable)
	{
		boolean bSuccess = false;
		try
		{
			bSuccess = true;
			Timer timer = new Timer();
			timer.startTimer();
			this.setTermTagObjectTable(termTagObjectTable);

			// first correct possible incorrect entries
			CorrectTradosIllegalXMLCharacter corrector = new CorrectTradosIllegalXMLCharacter();
			System.out.println(xliffFile + ": " + corrector.encodeIncorrectSDLTradosXLIFFFile(xliffFile) + " - changed: " + corrector.getIchanged());
			byte[] bom = OpenTMSSupportFunctions.returnBOMFromFile(xliffFile);

			File f = new File(xliffFile);
			doc = new XliffDocument();
			// load the xml file
			doc.loadXmlFile(f);
			timer.stopTimer();
			System.out
					.println(timer.timerString("XLIFF file read " + xliffFile + ": Version " + doc.getXliffVersion()));
			timer.startTimer();

			List<Element> files = doc.getFiles();
			int iSize = files.size();
			System.out.println("# XLIFF Files: " + iSize);

			xliffTokenizer = new XliffTokenizer(doc, wordHandling);
			// xliffTokenizer.setWordHandling(wordHandling);

			for (int i = 0; i < iSize; i++)
			{
				Element file = files.get(i);
				Element body = doc.getXliffBody(file);
				List<Element> transunits = doc.getTransUnitList(body);
				int tSize = transunits.size();
				System.out.println("# XLIFF trans-units: " + tSize);
				termTagTransUnits(transunits);

				transunits = doc.getGroupTransUnitList(body);
				tSize = transunits.size();
				System.out.println("# XLIFF trans-units (groups): " + tSize);
				termTagTransUnits(transunits);
			}

			doc.saveToXmlFile(xliffFile + ".xlf");
			if (bom != null)
			{
				System.out.println("BOM detected " + bom + ":" + bom.length);
				bSuccess = OpenTMSSupportFunctions.writeBOMToFile(xliffFile + ".xlf", bom);
				System.out.println("BOM written to " + xliffFile + ".xlf" + " : Success: " + bSuccess);
			}
			System.out.println("Finished with " + this.numberOfTaggings + " taggings for " + xliffFile + " >>> "
					+ xliffFile + ".xlf");
			System.out.println(xliffFile + ": " + corrector.decodeIncorrectSDLTradosXLIFFFile(xliffFile));
			System.out.println(xliffFile + ".xlf" + ": " + corrector.decodeIncorrectSDLTradosXLIFFFile(xliffFile + ".xlf"));
			timer.stopTimer();
			timer.endTimer();
			System.out.println(timer.timerString("Finished XLIFF file tagged " + xliffFile));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println("Finished XLIFF file with error " + xliffFile);
		}
		return bSuccess;
	}

	/**
	 * @param transunits
	 */
	public void termTagTransUnits(List<Element> transunits)
	{
		int tSize = transunits.size();
		for (int j = 0; j < tSize; j++)
		{
			boolean bIgnore = false;
			if (iSegNums != null)
			{
				bIgnore = true;
				for (int i = 0; i < iSegNums.length; i++)
				{
					if (iSegNums[i] == j)
					{
						bIgnore = false;
						break;
					}
				}
			}

			if (bIgnore == true)
			{
				System.out.println("Ignore segment " + j);
				continue;
			}
			boolean bSegSource = true;
			Element transUnit = transunits.get(j);
			Element source = doc.getTransUnitSegSource(transUnit);
			if (source == null)
			{
				source = doc.getTransUnitSource(transUnit);
				bSegSource = false;
			}

			if (source == null)
				continue;

			Element target = doc.getTransUnitTarget(transUnit);

			Vector<String> sourceVec = removeWhiteStuff(source, sourceLanguage);
			if (sourceVec.size() <= 0)
				continue;

			String sourceContent = doc.elementContentToString(source, false);
			sourceContent = sourceContent.replaceFirst("^<" + "source" + ".*?>", "");
			sourceContent = sourceContent.replaceAll("</" + "source" + ".*?>$", "");
			sourceContent = sourceContent.replaceFirst("^<" + "seg-source" + ".*?>", "");
			sourceContent = sourceContent.replaceAll("</" + "seg-source" + ".*?>$", "");
			String targetContent = null;
			if (target != null)
			{
				targetContent = doc.elementContentToString(target, false);
				targetContent = targetContent.replaceFirst("^<" + "target" + ".*?>", "");
				targetContent = targetContent.replaceAll("</" + "target" + ".*?>$", "");
			}

			// XliffElementHandler xliffSourceCoded = new XliffElementHandler(sourceContent, this.wordHandling, sourceLanguage, termTagObjectTable);
			// XliffElementHandler xliffTargetCoded = new XliffElementHandler(targetContent, this.wordHandling, targetLanguage, termTagObjectTable);
			// sourceVec = removeDuplicateEntries(sourceVec);

			int iStart = 0;
			int iEnd = sourceVec.size() - 1;
			Vector<TermTagObjectMatch> phraseSourceFound = termTagObjectTable.findTerms(sourceVec, iStart, iEnd, true);
			// Vector<TermTagObjectMatch> phraseSourceFound1 = termTagObjectTable.findTerms(xliffSourceCoded, xliffTargetCoded, sourceLanguage, targetLanguage);
			if (debug)
			{
				debugOut(source, target, sourceVec, phraseSourceFound, ">>>>>\nphraseSourceFound\n");
			}

			Vector<String> targetVec = null;
			Vector<TermTagObjectMatch> phraseTargetFound = null;
			if (target != null)
			{
				targetVec = removeWhiteStuff(target, targetLanguage);
				iStart = 0;
				iEnd = targetVec.size() - 1;
				phraseTargetFound = termTagObjectTable.findTerms(targetVec, iStart, iEnd, false);
			}

			if (phraseSourceFound.size() > 0)
			{
				this.markTimer.continueTimer();
				String resultString = "";
				if (bSegSource)
					resultString = "<seg-source>"
							+ tagWithMrk(sourceContent, targetContent, phraseSourceFound, phraseTargetFound, true)
							+ "</seg-source>";
				else
					resultString = "<source>"
							+ tagWithMrk(sourceContent, targetContent, phraseSourceFound, phraseTargetFound, true)
							+ "</source>";

				if (debug)
				{
					System.out.println("+++++ " + resultString);
				}

				try
				{
					Element newsource = doc.buildElement(resultString);
					@SuppressWarnings({ "unchecked" })
					List<Attribute> atts = source.getAttributes();
					for (int m = 0; m < atts.size(); m++)
					{
						newsource.setAttribute(atts.get(m).getName(), atts.get(m).getValue(), atts.get(m)
								.getNamespace());
					}
					int index = transUnit.indexOf(source);
					boolean bRemoved = false;
					if (bSegSource)
					{
						// bRemoved = transUnit.removeChildren("seg-source");
						bRemoved = transUnit.removeContent(source);
					}
					else
					{
						// bRemoved = transUnit.removeChild("source");
						bRemoved = transUnit.removeContent(source);
					}
					if (!bRemoved)
					{
						System.out.println(">> Problem: Could not remove source / seg-source " + bSegSource + "/"
								+ index + ": " + resultString);
						// transUnit.addContent(index+1, newsource);
					}
					else
					{
						transUnit.addContent(index, newsource);
					}
				}
				catch (OpenTMSException e)
				{
					e.printStackTrace();
				}
				if (debug)
					System.out.println(resultString);
			}
			this.markTimer.stopTimer();

			if (target == null)
				continue;

			if (debug)
			{
				debugOut(target, source, targetVec, phraseTargetFound, "<<<<<\nphraseTargetFound\n");
			}

			if (phraseTargetFound.size() > 0)
			{
				this.markTimer.continueTimer();
				String resultString = "<target>"
						+ tagWithMrk(targetContent, sourceContent, phraseTargetFound, phraseSourceFound, false)
						+ "</target>";
				try
				{
					Element newtarget = doc.buildElement(resultString);
					@SuppressWarnings({ "unchecked" })
					List<Attribute> atts = target.getAttributes();
					for (int m = 0; m < atts.size(); m++)
					{
						newtarget.setAttribute(atts.get(m).getName(), atts.get(m).getValue(), atts.get(m)
								.getNamespace());
					}
					int index = transUnit.indexOf(target);
					@SuppressWarnings("unused")
					Content bRemoved = transUnit.removeContent(index);
					// boolean bRemoved = transUnit.removeChild("target");
					transUnit.addContent(index, newtarget);
				}
				catch (OpenTMSException e)
				{
					System.out.println(resultString);
					e.printStackTrace();
				}
				if (debug)
					System.out.println(resultString);
				this.markTimer.stopTimer();
			}
		}
	}
}
