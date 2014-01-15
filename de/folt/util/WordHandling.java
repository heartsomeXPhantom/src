/*
 * Created on 27.05.2009
 * 
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.util;

import java.util.Hashtable;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.ar.ArabicAnalyzer;
import org.apache.lucene.analysis.bg.BulgarianAnalyzer;
import org.apache.lucene.analysis.br.BrazilianAnalyzer;
import org.apache.lucene.analysis.ca.CatalanAnalyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.cz.CzechAnalyzer;
import org.apache.lucene.analysis.da.DanishAnalyzer;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.analysis.el.GreekAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.analysis.eu.BasqueAnalyzer;
import org.apache.lucene.analysis.fa.PersianAnalyzer;
import org.apache.lucene.analysis.fi.FinnishAnalyzer;
import org.apache.lucene.analysis.fr.FrenchAnalyzer;
import org.apache.lucene.analysis.ga.IrishAnalyzer;
import org.apache.lucene.analysis.gl.GalicianAnalyzer;
import org.apache.lucene.analysis.hi.HindiAnalyzer;
import org.apache.lucene.analysis.hu.HungarianAnalyzer;
import org.apache.lucene.analysis.hy.ArmenianAnalyzer;
import org.apache.lucene.analysis.id.IndonesianAnalyzer;
import org.apache.lucene.analysis.it.ItalianAnalyzer;
import org.apache.lucene.analysis.lv.LatvianAnalyzer;
import org.apache.lucene.analysis.nl.DutchAnalyzer;
import org.apache.lucene.analysis.no.NorwegianAnalyzer;
import org.apache.lucene.analysis.pt.PortugueseAnalyzer;
import org.apache.lucene.analysis.ro.RomanianAnalyzer;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.sv.SwedishAnalyzer;
import org.apache.lucene.analysis.th.ThaiAnalyzer;
import org.apache.lucene.analysis.tr.TurkishAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.util.Version;

/**
 * This class implements several methods dealing with word handling, e.g.
 * splitting up segments into words. The default split string is defined as:
 * 
 * <pre>
 * defaultSplitString = &quot;\\s&quot; + &quot;|[&quot; + Pattern.quote(&quot;.*()[]:;,'#+=?!$%&amp;\&quot;&quot;) + &quot;]&quot;;
 * </pre>
 * 
 * @author klemens
 * 
 */
public class WordHandling
{

	private static String			defaultWordSplitChars	= ".*()[]:;,'#+=?!$%&\"{}<>" + CorrectTradosIllegalXMLCharacter.getReplaceCRChar() + CorrectTradosIllegalXMLCharacter.getReplaceLFChar();

	private static final String		LUCENE_ESCAPE_CHARS		= "'";

	@SuppressWarnings("unused")
	private static final Pattern	LUCENE_PATTERN			= Pattern.compile(LUCENE_ESCAPE_CHARS);

	private static char				quoteCharStart			= '\uFFEE';

	@SuppressWarnings("unused")
	private static final String		REPLACEMENT_STRING		= "\\\\$0";

	private static Analyzer getAnalyzer(String language)
	{
		language = language.toLowerCase();
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
		if (language.startsWith("en"))
			analyzer = new EnglishAnalyzer(Version.LUCENE_36); // JapaneseAnalyzer();
		else if (language.startsWith("ar"))
			analyzer = new ArabicAnalyzer(Version.LUCENE_36);
		else if (language.startsWith("bg"))
			analyzer = new BulgarianAnalyzer(Version.LUCENE_36);
		else if (language.startsWith("ca"))
			analyzer = new CatalanAnalyzer(Version.LUCENE_36);
		else if (language.startsWith("cs"))
			analyzer = new CzechAnalyzer(Version.LUCENE_36);
		else if (language.startsWith("da"))
			analyzer = new DanishAnalyzer(Version.LUCENE_36);
		else if (language.startsWith("de"))
			analyzer = new GermanAnalyzer(Version.LUCENE_36);
		else if (language.startsWith("el"))
			analyzer = new GreekAnalyzer(Version.LUCENE_36);
		else if (language.startsWith("es"))
			analyzer = new SpanishAnalyzer(Version.LUCENE_36);
		else if (language.startsWith("eu"))
			analyzer = new BasqueAnalyzer(Version.LUCENE_36);
		else if (language.startsWith("fa"))
			analyzer = new PersianAnalyzer(Version.LUCENE_36);
		else if (language.startsWith("fi"))
			analyzer = new FinnishAnalyzer(Version.LUCENE_36);
		else if (language.startsWith("fr"))
			analyzer = new FrenchAnalyzer(Version.LUCENE_36);
		else if (language.startsWith("ga"))
			analyzer = new IrishAnalyzer(Version.LUCENE_36);
		else if (language.startsWith("gl"))
			analyzer = new GalicianAnalyzer(Version.LUCENE_36);
		else if (language.startsWith("hi"))
			analyzer = new HindiAnalyzer(Version.LUCENE_36);
		else if (language.startsWith("hu"))
			analyzer = new HungarianAnalyzer(Version.LUCENE_36);
		else if (language.startsWith("hy"))
			analyzer = new ArmenianAnalyzer(Version.LUCENE_36);
		else if (language.startsWith("id"))
			analyzer = new IndonesianAnalyzer(Version.LUCENE_36);
		else if (language.startsWith("it"))
			analyzer = new ItalianAnalyzer(Version.LUCENE_36);
		else if (language.startsWith("lv"))
			analyzer = new LatvianAnalyzer(Version.LUCENE_36);
		else if (language.startsWith("nl"))
			analyzer = new DutchAnalyzer(Version.LUCENE_36);
		else if (language.startsWith("no"))
			analyzer = new NorwegianAnalyzer(Version.LUCENE_36);
		else if (language.startsWith("pt-br"))
			analyzer = new BrazilianAnalyzer(Version.LUCENE_36);
		else if (language.startsWith("pt"))
			analyzer = new PortugueseAnalyzer(Version.LUCENE_36);
		else if (language.startsWith("ro"))
			analyzer = new RomanianAnalyzer(Version.LUCENE_36);
		else if (language.startsWith("ru"))
			analyzer = new RussianAnalyzer(Version.LUCENE_36);
		else if (language.startsWith("sv"))
			analyzer = new SwedishAnalyzer(Version.LUCENE_36);
		else if (language.startsWith("th"))
			analyzer = new ThaiAnalyzer(Version.LUCENE_36);
		else if (language.startsWith("tr"))
			analyzer = new TurkishAnalyzer(Version.LUCENE_36);
		else if (language.startsWith("ko"))
			analyzer = new CJKAnalyzer(Version.LUCENE_36);
		else if (language.startsWith("ja"))
			analyzer = new CJKAnalyzer(Version.LUCENE_36); // JapaneseAnalyzer();
		else if (language.startsWith("ja-lu"))
			analyzer = new CJKAnalyzer(Version.LUCENE_36);
		else if (language.startsWith("zn"))
			analyzer = new CJKAnalyzer(Version.LUCENE_36);
		else if (language.startsWith("zh"))
			analyzer = new CJKAnalyzer(Version.LUCENE_36);
		return analyzer;
	}

	public static String getDefaultWordSplitChars()
	{
		return WordHandling.defaultWordSplitChars;
	}

	public static char getQuoteCharStart()
	{
		return quoteCharStart;
	}

	public static void main(String[] args)
	{

		String testString = "¡spanisch .hallo Das <x />  (welt) H20 <mrk id=\"12\">ist</mrk> W&ouml;rter  eine    schöne, \t\t1234 neue <g>Testzeichenkette,\n\n\r &amp; während 12.4 ACM.GF {ein} {zwei worte} (ein) (zwei worte) [ein] [zwei worte] das keine ist, oder [Fe(CN)6]3 c?</g> blabla. CIP-Datei oder.";
		WordHandling wordHandling = new WordHandling();
		String vectokens[] = wordHandling.segmentToWordArray(testString, "de");
		for (int j = 0; j < vectokens.length; j++)
		{
			System.out.println(j + ": \"" + vectokens[j] + "\"");
		}

		vectokens = wordHandling.segmentToWordArray(testString, "es");
		for (int j = 0; j < vectokens.length; j++)
		{
			System.out.println("es " + j + ": \"" + vectokens[j] + "\"");
		}

		System.out.println(WordHandling.stem("gelaufen", "de"));
		System.out.println(WordHandling.stem("kaufte", "de"));
		System.out.println(WordHandling.stem("kaufen", "de"));
		System.out.println(WordHandling.stem("gekauft", "de"));
		System.out.println(WordHandling.stem("running", "en"));
		System.out.println(WordHandling.stem("runner", "en"));
		System.out.println(WordHandling.stem("Carte d'option OR", "fr"));
		System.out.println(WordHandling.stem("Carte d'option", "fr"));
		System.out.println(WordHandling.stem("Carte d option OR", "fr"));
	}

	public static void setDefaultWordSplitChars(String defaultWordSplitChars)
	{
		WordHandling.defaultWordSplitChars = defaultWordSplitChars;
	}

	public static void setQuoteCharStart(char quoteCharStart)
	{
		WordHandling.quoteCharStart = quoteCharStart;
	}

	/**
	 * @param text
	 * @param language
	 * @return
	 */
	public static String stem(String text, String language)
	{
		if (text.equals(""))
			return text;
		try
		{
			Analyzer stemmer = getAnalyzer(language);
			QueryParser parser = new QueryParser(Version.LUCENE_30, "", stemmer);
			// text = LUCENE_PATTERN.matcher(text).replaceAll(REPLACEMENT_STRING);
			String stemmed = parser.parse(QueryParser.escape(text)).toString();
			return stemmed;
		}
		catch (ParseException e)
		{
			// TODO Auto-generated catch block
			// e.printStackTrace();
			return text;
		}

	}

	private boolean						bXmlMode						= true;

	private String						defaultSplitString				= defaultWordSplitChars;

	private String						defaultSplitStringDE			= defaultWordSplitChars;

	private String						defaultSplitStringEN			= defaultWordSplitChars;

	private String						defaultSplitStringES			= ".*()[]:;,'#+=?!$%&\"{}¡<>";

	private String						entexp							= Pattern.quote("&") + "(\\w+?)" + Pattern.quote(";");	// regex
																																// for
																																// entities

	private Hashtable<String, String>	languageDefaultWordSplitChars	= new Hashtable<String, String>();

	public WordHandling()
	{
		super();
		init();
	}

	private String createSplitRegEx(String splitCharaceters)
	{
		// splitCharacters = "\\s" + "|(" + Pattern.quote(splitCharacters) +
		// "]\\s)";
		splitCharaceters = "\\s+"; // use only white space characters
		// splitCharaceters = "\\s+" + "|([" + Pattern.quote(splitCharaceters) +
		// "]+\\s)" + "|(["
		// + Pattern.quote(splitCharaceters) + "]+$)" + "|(^[" +
		// Pattern.quote(splitCharaceters) + "]+)";
		return splitCharaceters;
	}

	/**
	 * @return the defaultSplitString
	 */
	public String getDefaultSplitString()
	{
		return defaultSplitString;
	}

	public String getDefaultSplitStringDE()
	{
		return defaultSplitStringDE;
	}

	public String getDefaultSplitStringEN()
	{
		return defaultSplitStringEN;
	}

	public String getDefaultSplitStringES()
	{
		return defaultSplitStringES;
	}

	public Hashtable<String, String> getLanguageDefaultWordSplitChars()
	{
		return languageDefaultWordSplitChars;
	}

	/**
	 * @param language
	 * @return
	 */
	public String getSplitChars(String language)
	{
		String splitChars = defaultSplitString;
		if (languageDefaultWordSplitChars.containsKey(language.toLowerCase()))
		{
			splitChars = languageDefaultWordSplitChars.get(language.toLowerCase());
		}
		else if (language.length() > 2)
		{
			String shortLanguage = language.substring(0, 2).toLowerCase();
			splitChars = languageDefaultWordSplitChars.get(shortLanguage.toLowerCase());
		}

		if (splitChars == null)
		{
			splitChars = defaultSplitString.toLowerCase();
		}

		return splitChars;
	}

	/**
	 * Initialise the word split chars
	 */
	public void init()
	{
		languageDefaultWordSplitChars = new Hashtable<String, String>();
		setlanguageWordSplitChars("de-de", defaultSplitString);
		setlanguageWordSplitChars("es-es", defaultSplitString + "¡");
		setlanguageWordSplitChars("en-us", defaultSplitString);
		setlanguageWordSplitChars("en-us", defaultSplitString);
		setlanguageWordSplitChars("un-un", defaultSplitString);
		setlanguageWordSplitChars("fr-fr", defaultSplitString);
		// non European languages
		setlanguageWordSplitChars("zh-cn", defaultSplitString);
		setlanguageWordSplitChars("zh-hk", defaultSplitString);
		setlanguageWordSplitChars("zh-sg", defaultSplitString);
		setlanguageWordSplitChars("zh-tw", defaultSplitString);
		setlanguageWordSplitChars("ja", defaultSplitString);
		setlanguageWordSplitChars("ko", defaultSplitString);
		setlanguageWordSplitChars("ko-jb", defaultSplitString);
	}

	public boolean isbXmlMode()
	{
		return bXmlMode;
	}

	/**
	 * segmentToWordArray segments a string into an array of words using
	 * defaultSplitString (Pattern.quote(".*()[]:;,'#+=?!$%&\"{}<>"))
	 * 
	 * @param string
	 *            the string to segment
	 * @return the array with all the words
	 */
	public String[] segmentToWordArray(String string)
	{
		return segmentToWordArray(string, "un");
	}

	/**
	 * segmentToWordArray segments a string into an array of words based on a
	 * language
	 * 
	 * @param string
	 *            the string to segment
	 * @param language
	 *            language to use (e.g. de-de; will search first for de-de and
	 *            then for de; if nothing found defaultSplitString will be used
	 *            (Pattern.quote(".*()[]:;,'#+=?!$%&\"{}"))
	 * @return the array with all the words
	 */
	public String[] segmentToWordArray(String string, String language)
	{
		if (this.bXmlMode)
		{
			// remove all tags; content of tags kept (could sometimes be a
			// problem...)
			string = string.replaceAll("\\<.*?\\>", "");
			// protect entities from beeing split up
			string = string.replaceAll(entexp, "asdfer$1asdfer"); // do some
																	// "special mark up"
																	// for
																	// entities
																	// to avoid
																	// splitting
																	// them up
		}
		String splitChars = defaultSplitString;

		// handle short and long language versions
		if (languageDefaultWordSplitChars.containsKey(language.toLowerCase()))
		{
			splitChars = languageDefaultWordSplitChars.get(language.toLowerCase());
		}
		else if (language.length() > 2) // short coded language
		{
			String shortLanguage = language.substring(0, 2).toLowerCase();
			splitChars = languageDefaultWordSplitChars.get(shortLanguage.toLowerCase());
		}
		else
		{
			splitChars = defaultSplitString.toLowerCase();
		}

		if (splitChars == null)
		{
			splitChars = defaultSplitString.toLowerCase();
		}

		// create the regex for the splitter
		String splitCharsRegEx = createSplitRegEx(splitChars);

		// do some preprocessing for single ' here
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
				else if (!splitChars.contains(buffer.charAt(i - 1) + ""))
				{
					buffer.setCharAt(i, wq);
				}
			}
		}

		string = buffer.toString();
		String[] splits = string.split(splitCharsRegEx);
		splits = StringUtil.removeElements(splits, ""); // remove any empty lines
		// in the following loop the "word terminator characters" like ";:" etc.
		// are removed from the term; special handling for ([ etc. and entities
		for (int i = 0; i < splits.length; i++)
		{
			int lLenght = splits[i].length();
			if (bXmlMode)
			{
				if (splits[i].matches(entexp)) // handle entities
				{
					continue;
				}
			}

			char cs = splits[i].charAt(0);
			char ce = splits[i].charAt(lLenght - 1);
			// the following if avoids removing parenthesis at position 0 if
			// they appear anywhere closed in the term (simple for the moment)
			if (cs == '(')
			{
				if (ce == ')')
				{
					// do something latter here
				}
				else if (splits[i].contains(")"))
					continue;
			}
			else if (cs == '{')
			{
				if (ce == '}')
				{
					// do something latter here
				}
				else if (splits[i].contains("}"))
					continue;
			}
			else if (cs == '[')
			{
				if (ce == ']')
				{
					// do something latter here
				}
				else if (splits[i].contains("]"))
					continue;
			}
			// end parenthesis

			// A problem are words like that: pavillon d'aspiration d'aspiration should not be split (??)

			// remove split chars like .; etc. at the end and start of a term
			splits[i] = splits[i].replaceAll("^[" + Pattern.quote(splitChars) + "]+", "");
			splits[i] = splits[i].replaceAll("[" + Pattern.quote(splitChars) + "]+$", "");
			if (bXmlMode)
			{
				splits[i] = splits[i].replaceAll("asdfer(.+?)asdfer", "&$1;"); // reinsert
																				// entities
			}

			buffer = new StringBuffer(splits[i]);
			for (int j = 0; j < buffer.length(); j++)
			{
				if (buffer.charAt(j) == ('\'' + quoteCharStart))
				{
					buffer.setCharAt(j, '\'');
				}
			}
			splits[i] = buffer.toString();
		}

		return splits;
	}

	public void setbXmlMode(boolean bXmlMode)
	{
		this.bXmlMode = bXmlMode;
	}

	public void setDefaultSplitString(String defaultSplitString)
	{
		this.defaultSplitString = defaultSplitString;
	}

	public void setDefaultSplitStringDE(String defaultSplitStringDE)
	{
		this.defaultSplitStringDE = defaultSplitStringDE;
	}

	public void setDefaultSplitStringEN(String defaultSplitStringEN)
	{
		this.defaultSplitStringEN = defaultSplitStringEN;
	}

	public void setDefaultSplitStringES(String defaultSplitStringES)
	{
		this.defaultSplitStringES = defaultSplitStringES;
	}

	public void setLanguageDefaultWordSplitChars(Hashtable<String, String> languageDefaultWordSplitChars)
	{
		this.languageDefaultWordSplitChars = languageDefaultWordSplitChars;
	}

	/**
	 * Add or replace a split character for a language
	 * 
	 * @param language
	 *            the language code
	 * @param splitChars
	 *            the chars to use (will be Quoted for regular Expressions and
	 *            \s from regular expression added)
	 */
	public void setlanguageWordSplitChars(String language, String splitChars)
	{
		// splitChars = "\\s" + "|[" + Pattern.quote(splitChars) + "]";
		String shortLanguage = language.substring(0, 2);
		languageDefaultWordSplitChars.put(language, splitChars);
		if (language.length() > 2)
			languageDefaultWordSplitChars.put(shortLanguage, splitChars);
	}

}
