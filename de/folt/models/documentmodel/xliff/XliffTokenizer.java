package de.folt.models.documentmodel.xliff;

import java.util.Arrays;
import java.util.Vector;
import java.util.regex.Pattern;

import org.jdom.Element;

import de.folt.models.datamodel.DataSource;
import de.folt.models.datamodel.DataSourceInstance;
import de.folt.models.datamodel.DataSourceProperties;
import de.folt.models.datamodel.phrasetranslate.PhraseTranslate;
import de.folt.util.OpenTMSException;
import de.folt.util.WordHandling;

/**
 * This class tokenizes xliff source, seg-source or target elements into word
 * list Actually it can tokenize any Element. The tokenizer works language
 * independent.
 * 
 * @author Klemens
 * 
 */
public class XliffTokenizer
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{

		if ((args.length == 2) && args[0].equalsIgnoreCase(("-test")))
		{
			test(args);
			return;
		}

		String testString = "¡spanisch ende. .hallo Das <x />  (welt) H20 <mrk id=\"12\">ist</mrk> W&ouml;rter  eine    schöne, \t\t1234 neue <g>Testzeichenkette,\n\n\r &amp; während 12.4 ACM.GF {ein} {zwei worte} (ein) (zwei worte) [ein] [zwei worte] das <bx id=\"v\"> jjdfdf </bx>keine ist, oder [Fe(CN)6]3 c?</g> blabla.";
		XliffTokenizer xliffTokenizer = new XliffTokenizer();
		Vector<String> vectokens = xliffTokenizer.tokenize(testString);
		for (int j = 0; j < vectokens.size(); j++)
		{
			System.out.println("T" + j + ": \"" + vectokens.get(j) + "\"");
		}

		System.out.println(xliffTokenizer.markUpTokens(vectokens));

	}

	/**
	 * test simple test method for generating DataModelInstances
	 */
	public static void test(String[] args)
	{
		try
		{
			String tbxfile = args[1];
			DataSourceProperties model = new DataSourceProperties();
			model.put("dataModelClass", "de.folt.models.datamodel.tbxfile.TbxFileDataSource");
			model.put("tbxfile", tbxfile);
			// model.put("dataModelUrl", "openTMS.jar");
			System.out.println(model.toString());
			System.out.println("createInstance" + " TBX:" + tbxfile);
			DataSource datasource = DataSourceInstance.createInstance("TBX:" + tbxfile, model);
			System.out.println("createInstance" + " TBX:" + tbxfile + " getLastErrorCode="
					+ datasource.getLastErrorCode() + " >>> " + datasource);

			datasource = DataSourceInstance.getInstance("TBX:" + tbxfile);
			System.out.println("getInstance" + " TBX:" + tbxfile + " >>> " + datasource);

			PhraseTranslate phraseTranslate = new PhraseTranslate("de-DE", "en-GB");
			// stored for getting the term attributes
			phraseTranslate.setbStoreUniqueId(true);
			phraseTranslate.bAddPhrases(datasource, "de-DE", "en-GB");

			DataSourceInstance.removeInstance("TBX:" + tbxfile);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private String language = "un";

	private String specialMarkString = "ffffqqqqffff";
	
	private String entexp = Pattern.quote("&") + "(\\w+?)" + Pattern.quote(";");

	private String tokenStrings[] = { "\\s+" };

	private WordHandling wordHandling = new WordHandling();

	private XliffDocument xliffDocument;

	private String xliffRegExpElems[] = { "<g.*?>", "</g>", "<x.*?/>", "<bx.*?>", "</bx>", "<ex.*?>", "</ex>",
			"<bpt.*?>", "</bpt>", "<ept.*?>", "</ept>", "<ph.*?>", "</ph>", "<it.*?>", "</it>", "<mrk.*?>", "</mrk>",
			"<source.*?>", "</source>", "<target.*?>", "</target>", "<seg-source.*?>", "</seg-source>" };

	/**
	 * 
	 */
	public XliffTokenizer()
	{
		super();
	}

	/**
	 * @param xliffDocument
	 */
	public XliffTokenizer(XliffDocument xliffDocument)
	{
		super();
		this.xliffDocument = xliffDocument;
	}

	public XliffTokenizer(XliffDocument xliffDocument, String language)
	{
		super();
		this.xliffDocument = xliffDocument;
		this.language = language;
	}

	public XliffTokenizer(XliffDocument xliffDocument, String language, WordHandling wordHandling)
	{
		super();
		this.xliffDocument = xliffDocument;
		this.language = language;
		this.wordHandling = wordHandling;
	}

	public XliffTokenizer(XliffDocument xliffDocument, WordHandling wordHandling)
	{
		super();
		this.xliffDocument = xliffDocument;
		this.language = "un";
		this.wordHandling = wordHandling;
	}

	public String getLanguage()
	{
		return language;
	}

	public WordHandling getWordHandling()
	{
		return wordHandling;
	}

	/**
	 * @return
	 */
	public XliffDocument getXliffDocument()
	{
		return xliffDocument;
	}

	/**
	 * Constructs a string from the tokens and marks them up
	 * 
	 * @param tokens
	 *            the vector of tokens
	 * @return string with each token marked up
	 */
	public String markUpTokens(Vector<String> tokens)
	{
		String result = "";
		int k = 0;
		for (int i = 0; i < tokens.size(); i++)
		{
			if (tokens.get(i).contains("<"))
			{
				result = result + tokens.get(i);
				continue;
			}
			if (tokens.get(i).matches(tokenStrings[0]))
				result = result + "<mrk mtype=\"x-stop-token\" mid=\"" + k + "\">" + tokens.get(i) + "</mrk>";
			else
				result = result + "<mrk mtype=\"x-word-token\" mid=\"" + k + "\">" + tokens.get(i) + "</mrk>";
			k++;
		}

		return result;
	}

	/**
	 * @param tokens
	 * @param element
	 * @return
	 */
	public Vector<String> removeElement(Vector<String> tokens, String element)
	{
		Vector<String> newtokens = new Vector<String>();
		for (int i = 0; i < tokens.size(); i++)
		{
			if (tokens.get(i).matches("<" + element + ">"))
			{
				continue;
			}
			else if (tokens.get(i).matches("<" + element + " .*?>"))
			{
				continue;
			}
			else if (tokens.get(i).matches("</" + element + ".*?>"))
			{
				continue;
			}
			else if (tokens.get(i).matches("<" + element + ".*?/>"))
			{
				continue;
			}

			newtokens.add(tokens.get(i));
		}
		return newtokens;
	}

	/**
	 * @param tokens
	 * @param string
	 * @return
	 */
	public Vector<String> removeInString(Vector<String> tokens, String string)
	{
		for (int i = 0; i < tokens.size(); i++)
		{
			tokens.set(i, tokens.get(i).replaceAll(string, ""));
		}
		return tokens;
	}

	/**
	 * remove mrk word/stop tokens from vector
	 * 
	 * @param tokens
	 *            the tokens where to remove word and stop tokens
	 * @return cleaned vector
	 */
	public Vector<String> removeMrkTokens(Vector<String> tokens)
	{
		Vector<String> newtokens = new Vector<String>();
		boolean bRemoveNextCloseToken = false;
		for (int i = 0; i < tokens.size(); i++)
		{
			if (tokens.get(i).matches("</?mrk.*?mtype=\"x-.*?-token.*?>"))
			{
				bRemoveNextCloseToken = true;
				continue;
			}
			if (bRemoveNextCloseToken && tokens.get(i).matches("</?mrk.*?>"))
			{
				bRemoveNextCloseToken = false;
				continue;
			}
			newtokens.add(tokens.get(i));
		}
		return newtokens;

	}

	/**
	 * @param tokens
	 * @param string
	 * @return
	 */
	public Vector<String> removeString(Vector<String> tokens, String string)
	{
		Vector<String> newtokens = new Vector<String>();
		for (int i = 0; i < tokens.size(); i++)
		{
			if (tokens.get(i).matches(string))
			{
				continue;
			}
			newtokens.add(tokens.get(i));
		}
		return newtokens;
	}

	public void setLanguage(String language)
	{
		this.language = language;
	}

	public void setWordHandling(WordHandling wordHandling)
	{
		this.wordHandling = wordHandling;
	}

	/**
	 * @param xliffDocument
	 */
	public void setXliffDocument(XliffDocument xliffDocument)
	{
		this.xliffDocument = xliffDocument;
	}

	/**
	 * Tokenize an xliff Element
	 * 
	 * @param element
	 * @return Element tokenized
	 */
	public Element tokenize(Element element)
	{
		try
		{
			String string = xliffDocument.elementToString(element);
			string = markUpTokens(tokenize(string));
			return xliffDocument.buildElement(string);
		}
		catch (OpenTMSException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @param string
	 * @return
	 */
	public Vector<String> tokenize(String string)
	{
		return tokenize(string, "un");
	}

	/**
	 * Tokenize a string
	 * 
	 * @param string
	 * @return a vector of tokens
	 */
	public Vector<String> tokenize(String string, String language)
	{
		Vector<String> vector = new Vector<String>();

		// dont split this: <g>, <x/>, <bx/>, <ex/>, <bpt> , <ept>, <ph>, <it> ,
		// <mrk>

		for (int i = 0; i < xliffRegExpElems.length; i++)
		{
			string = string.replaceAll("(" + xliffRegExpElems[i] + ")", specialMarkString + "$1" + specialMarkString);
		}

		Vector<String> userchar = new Vector<String>();
		for (int i = 0; i < string.length(); i++)
		{
			if (XliffElementHandler.bIsReplaceChar(string.charAt(i)) && !userchar.contains(string.charAt(i) + ""))
				userchar.add(string.charAt(i) + "");
		}

		for (int i = 0; i < userchar.size(); i++)
		{
			string = string.replaceAll("(" + Pattern.quote(userchar.get(i)) + ")", specialMarkString + "$1"
					+ specialMarkString);
		}

		String[] splitString = string.split(Pattern.quote(specialMarkString));

		// two types of Word splitters
		// a) unconditional splitters like " "
		// b) conditional splitters like "-"

		for (int i = 0; i < splitString.length; i++)
		{
			if (splitString[i].contains("<"))
			{
				vector.add(splitString[i]);
				continue;
			}
			for (int j = 0; j < tokenStrings.length; j++)
			{
				splitString[i] = splitString[i].replaceAll("(" + tokenStrings[j] + ")", specialMarkString + "$1"
						+ specialMarkString);
			}
			String[] splitString1 = splitString[i].split(Pattern.quote(specialMarkString));
			vector.addAll(Arrays.asList(splitString1));
		}

		vector.removeAll(Arrays.asList(""));

		Vector<String> vector1 = new Vector<String>();
		String tokenStringsWordsConditional = wordHandling.getSplitChars(language); // language
																					// specific
																					// word
																					// tokens
		for (int i = 0; i < vector.size(); i++)
		{
			if (vector.get(i).contains("<"))
			{
				vector1.add(vector.get(i));
				continue;
			}
			// for (int j = 0; j < tokenStringsWordsConditional.length(); j++)
			{
				// old: tokenStringsConditionalSplitCharactersWord[j]
				String currentString = vector.get(i);
				char cs = currentString.charAt(0);
				char ce = currentString.charAt(currentString.length() - 1);
				// the following if avoids removing parenthesis at position 0 if
				// they appear anywhere closed in the term (simple for the
				// moment)
				if (currentString.matches(entexp)) // handle entities
				{
					vector1.add(currentString);
					continue;
				}
				if (cs == '(')
				{
					if (ce == ')')
					{
						;
					}
					else if (currentString.contains(")"))
					{
						vector1.add(currentString);
						continue;
					}
				}
				else if (cs == '{')
				{
					if (ce == '}')
					{
						;
					}
					else if (currentString.contains("}"))
					{
						vector1.add(currentString);
						continue;
					}
				}
				else if (cs == '[')
				{
					if (ce == ']')
					{
						;
					}
					else if (currentString.contains("]"))
					{
						vector1.add(currentString);
						continue;
					}
				}

				currentString = currentString.replaceAll(entexp, "asdfer$1asdfer");
				
				String split = currentString.replaceAll("([" + Pattern.quote(tokenStringsWordsConditional) + "])",
						specialMarkString + "$1" + specialMarkString);
				split = split.replaceAll("asdfer(.+?)asdfer", "&$1;"); 
				vector.set(i, split);
			}
			String[] splitString1 = (vector.get(i)).split(Pattern.quote(specialMarkString));
			vector1.addAll(Arrays.asList(splitString1));
		}

		// remove any empty tokens
		vector1.removeAll(Arrays.asList(""));

		return vector1;
	}

	public Vector<String> tokenizeToVector(Element element)
	{
		try
		{
			String string = xliffDocument.elementToString(element);
			return tokenizeToVector(string);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Tokenize an element to a vector
	 * 
	 * @param element
	 * @return tokinzed vector
	 */
	public Vector<String> tokenizeToVector(Element element, String language)
	{
		try
		{
			String string = xliffDocument.elementToString(element);
			return tokenizeToVector(string, language);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @param string
	 * @return
	 */
	public Vector<String> tokenizeToVector(String string)
	{
		try
		{
			string = markUpTokens(tokenize(string));
			return tokenize(string);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @param string
	 * @param language
	 * @return
	 */
	public Vector<String> tokenizeToVector(String string, String language)
	{
		try
		{
			string = markUpTokens(tokenize(string));
			return tokenize(string, language);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

}
