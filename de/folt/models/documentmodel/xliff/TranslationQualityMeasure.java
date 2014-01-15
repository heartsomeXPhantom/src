package de.folt.models.documentmodel.xliff;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

import org.jdom.Element;
import org.jdom.Namespace;
import org.lingutil.bleu.BleuMeasurer;

import de.folt.util.Timer;

public class TranslationQualityMeasure
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		if (args.length < 2)
			return;
		String translatedDocument = args[0], MTDocument = args[1];
		String mode = "1";
		if (args.length >= 3)
			mode = args[2];
		String[] percents = null;
		if (args.length >= 4)
		{
			percents = new String[args.length - 3];
			for (int i = 0; i < percents.length; i++)
			{
				percents[i] = args[i + 3];
			}
		}
		else
			percents = null;
		TranslationQualityMeasure translationQualityMeasure = new TranslationQualityMeasure(mode, percents);
		translationQualityMeasure.compareTranslations(translatedDocument, MTDocument);
	}

	// private Vector<Boolean> bMTTranslation = null;

	private String		mode											= "1";

	private String[]	percents;

	private String		splitchars										= "[\\s+" + Pattern.quote(";!\\.-()]+*[]{}") + "]+";

	private String		tokenStringsConditionalSplitCharactersWord[]	= { "[ \\.\\+\\?\\!\\\"\\\t\\p{Punct}]+" };

	public TranslationQualityMeasure(String mode, String[] percents)
	{
		this.mode = mode;
		this.setPercents(percents);
	}

	private String analysisTable(XliffStatistics boundaries, XliffStatistics bleuXliffStatistics,
			XliffStatistics levenXliffStatistics, XliffStatistics wordlevenXliffStatistics)
	{

		String table = "";
		table = table + "<table><tr><td>";
		table = table + "\t\t\t<table class=\"infotable\">\n";
		String width = (int) 60 / 11 + "";
		table = table + "<colgroup><col width=\"40%\"><col width=\"" + width + "%\"><col width=\"" + width
				+ "%\"><col width=\"" + width + "%\"><col width=\"" + width + "%\"><col width=\"" + width
				+ "%\"><col width=\"" + width + "%\"><col width=\"" + width + "%\"><col width=\"" + width
				+ "%\"><col width=\"" + width + "%\"><col width=\"" + width + "%\"><col width=\"" + width
				+ "%\"></colgroup>\n";
		table = table + "\t\t\t\t<tr><th class=\"thstatright\">% Limits</th>";
		for (int i = 0; i < boundaries.getBoundaries().length; i++)
		{
			table = table + "<th class=\"thstatright\">" + boundaries.getBoundaries()[i] + "</th>";
		}
		table = table + "</tr>";

		table = table + "\t\t\t\t<tr><th class=\"thstatbottom\">% BLEU</th>";
		for (int i = 0; i < bleuXliffStatistics.getStatFields().length; i++)
		{
			table = table + "<td class=\"tdstat\">" + bleuXliffStatistics.getStatFields()[i] + "</td>";
		}
		table = table + "</tr>";

		table = table + "\t\t\t\t<tr><th class=\"thstatbottom\">% Leven</th>";
		for (int i = 0; i < levenXliffStatistics.getStatFields().length; i++)
		{
			table = table + "<td class=\"tdstat\">" + levenXliffStatistics.getStatFields()[i] + "</td>";
		}
		table = table + "</tr>";

		table = table + "\t\t\t\t<tr><th class=\"thstatlast\">% Leven Word</th>";
		for (int i = 0; i < wordlevenXliffStatistics.getStatFields().length; i++)
		{
			table = table + "<td class=\"tdstat\">" + wordlevenXliffStatistics.getStatFields()[i] + "</td>";
		}
		table = table + "</tr>";

		table = table + "\t\t\t</table>";

		table = table + "</td><td>";

		table = table + "\t\t\t<table class=\"infotable\">";
		table = table
				+ "\t\t\t\t<tr><th class=\"thstatright\">Segments</th><th class=\"thstatright\">BLEU</th><th class=\"thstatright\">Leven</th><th class=\"thstatright\">Leven Word</th></tr>";

		long[] bleu1 = new long[3];
		long[] leven1 = new long[3];
		long[] levenWord1 = new long[3];

		for (int i = 0; i < bleuXliffStatistics.getBoundaries().length; i++)
		{
			String bound = bleuXliffStatistics.getBoundaries()[i];
			int ibound = 0;
			try
			{
				ibound = Integer.parseInt(bound);
			}
			catch (NumberFormatException e)
			{
				e.printStackTrace();
			}

			if (ibound < 70)
			{
				bleu1[0] = bleu1[0] + bleuXliffStatistics.getStatFields()[i];
				leven1[0] = leven1[0] + levenXliffStatistics.getStatFields()[i];
				levenWord1[0] = levenWord1[0] + wordlevenXliffStatistics.getStatFields()[i];
			}
			else if (ibound < 100)
			{
				bleu1[1] = bleu1[1] + bleuXliffStatistics.getStatFields()[i];
				leven1[1] = leven1[1] + levenXliffStatistics.getStatFields()[i];
				levenWord1[1] = levenWord1[1] + wordlevenXliffStatistics.getStatFields()[i];
			}
			else
			{
				bleu1[2] = bleu1[2] + bleuXliffStatistics.getStatFields()[i];
				leven1[2] = leven1[2] + levenXliffStatistics.getStatFields()[i];
				levenWord1[2] = levenWord1[2] + wordlevenXliffStatistics.getStatFields()[i];
			}
		}

		table = table + "\t\t\t\t<tr><td class=\"thstatbottom\">0-70%</td><td class=\"tdstat\">" + bleu1[0]
				+ "</td><td class=\"tdstat\">" + leven1[0] + "</td><td class=\"tdstat\">" + levenWord1[0]
				+ "</td></tr>";
		table = table + "\t\t\t\t<tr><td class=\"thstatbottom\">71-99%</td><td class=\"tdstat\">" + bleu1[1]
				+ "</td><td class=\"tdstat\">" + leven1[1] + "</td><td class=\"tdstat\">" + levenWord1[1]
				+ "</td></tr>";
		table = table + "\t\t\t\t<tr><td class=\"thstatlast\">100%</td><td class=\"tdstat\">" + bleu1[2]
				+ "</td><td class=\"tdstat\">" + leven1[2] + "</td><td class=\"tdstat\">" + levenWord1[2]
				+ "</td></tr>";
		table = table + "\t\t\t</table>";

		table = table + "</td><td>";

		table = table + "\t\t\t<table class=\"infotable\">";
		table = table
				+ "\t\t\t\t<tr><th class=\"thstatright\">Word Human</th><th class=\"thstatright\">BLEU</th><th class=\"thstatright\">Leven</th><th class=\"thstatright\">Leven Word</th></tr>";

		bleu1 = new long[3];
		leven1 = new long[3];
		levenWord1 = new long[3];

		for (int i = 0; i < bleuXliffStatistics.getBoundaries().length; i++)
		{
			String bound = bleuXliffStatistics.getBoundaries()[i];
			int ibound = 0;
			try
			{
				ibound = Integer.parseInt(bound);
			}
			catch (NumberFormatException e)
			{
				e.printStackTrace();
			}

			if (ibound < 70)
			{
				bleu1[0] = bleu1[0] + bleuXliffStatistics.getWordFieldsHuman()[i];
				leven1[0] = leven1[0] + levenXliffStatistics.getWordFieldsHuman()[i];
				levenWord1[0] = levenWord1[0] + wordlevenXliffStatistics.getWordFieldsHuman()[i];
			}
			else if (ibound < 100)
			{
				bleu1[1] = bleu1[1] + bleuXliffStatistics.getWordFieldsHuman()[i];
				leven1[1] = leven1[1] + levenXliffStatistics.getWordFieldsHuman()[i];
				levenWord1[1] = levenWord1[1] + wordlevenXliffStatistics.getWordFieldsHuman()[i];
			}
			else
			{
				bleu1[2] = bleu1[2] + bleuXliffStatistics.getWordFieldsHuman()[i];
				leven1[2] = leven1[2] + levenXliffStatistics.getWordFieldsHuman()[i];
				levenWord1[2] = levenWord1[2] + wordlevenXliffStatistics.getWordFieldsHuman()[i];
			}
		}

		table = table + "\t\t\t\t<tr><td class=\"thstatbottom\">0-70%</td><td class=\"tdstat\">" + bleu1[0]
				+ "</td><td class=\"tdstat\">" + leven1[0] + "</td><td class=\"tdstat\">" + levenWord1[0]
				+ "</td></tr>";
		table = table + "\t\t\t\t<tr><td class=\"thstatbottom\">71-99%</td><td class=\"tdstat\">" + bleu1[1]
				+ "</td><td class=\"tdstat\">" + leven1[1] + "</td><td class=\"tdstat\">" + levenWord1[1]
				+ "</td></tr>";
		table = table + "\t\t\t\t<tr><td class=\"thstatlast\">100%</td><td class=\"tdstat\">" + bleu1[2]
				+ "</td><td class=\"tdstat\">" + leven1[2] + "</td><td class=\"tdstat\">" + levenWord1[2]
				+ "</td></tr>";
		table = table + "</td></tr></table>";
		table = table + "</td><td>";

		table = table + "\t\t\t<table class=\"infotable\">";
		table = table
				+ "\t\t\t\t<tr><th class=\"thstatright\">Word MT</th><th class=\"thstatright\">BLEU</th><th class=\"thstatright\">Leven</th><th class=\"thstatright\">Leven Word</th></tr>";

		bleu1 = new long[3];
		leven1 = new long[3];
		levenWord1 = new long[3];

		for (int i = 0; i < bleuXliffStatistics.getBoundaries().length; i++)
		{
			String bound = bleuXliffStatistics.getBoundaries()[i];
			int ibound = 0;
			try
			{
				ibound = Integer.parseInt(bound);
			}
			catch (NumberFormatException e)
			{
				e.printStackTrace();
			}

			if (ibound < 70)
			{
				bleu1[0] = bleu1[0] + bleuXliffStatistics.getWordFieldsMT()[i];
				leven1[0] = leven1[0] + levenXliffStatistics.getWordFieldsMT()[i];
				levenWord1[0] = levenWord1[0] + wordlevenXliffStatistics.getWordFieldsMT()[i];
			}
			else if (ibound < 100)
			{
				bleu1[1] = bleu1[1] + bleuXliffStatistics.getWordFieldsMT()[i];
				leven1[1] = leven1[1] + levenXliffStatistics.getWordFieldsMT()[i];
				levenWord1[1] = levenWord1[1] + wordlevenXliffStatistics.getWordFieldsMT()[i];
			}
			else
			{
				bleu1[2] = bleu1[2] + bleuXliffStatistics.getWordFieldsMT()[i];
				leven1[2] = leven1[2] + levenXliffStatistics.getWordFieldsMT()[i];
				levenWord1[2] = levenWord1[2] + wordlevenXliffStatistics.getWordFieldsMT()[i];
			}
		}

		table = table + "\t\t\t\t<tr><td class=\"thstatbottom\">0-70%</td><td class=\"tdstat\">" + bleu1[0]
				+ "</td><td class=\"tdstat\">" + leven1[0] + "</td><td class=\"tdstat\">" + levenWord1[0]
				+ "</td></tr>";
		table = table + "\t\t\t\t<tr><td class=\"thstatbottom\">71-99%</td><td class=\"tdstat\">" + bleu1[1]
				+ "</td><td class=\"tdstat\">" + leven1[1] + "</td><td class=\"tdstat\">" + levenWord1[1]
				+ "</td></tr>";
		table = table + "\t\t\t\t<tr><td class=\"thstatlast\">100%</td><td class=\"tdstat\">" + bleu1[2]
				+ "</td><td class=\"tdstat\">" + leven1[2] + "</td><td class=\"tdstat\">" + levenWord1[2]
				+ "</td></tr>";
		table = table + "</td></tr></table>";

		table = table + "</td><td>";

		DecimalFormat df = new DecimalFormat(",##0.00");
		double per = ((float) bleuXliffStatistics.getWordSumHuman() / (float) bleuXliffStatistics.getWordSumMT() * 100.0);

		table = table
				+ "<table  class=\"infotable\"><tr><td class=\"thstatright\"><b>MT Words:</b></td><td class=\"thstatright\">"
				+ bleuXliffStatistics.getWordSumMT()
				+ "</td></tr><tr><td class=\"thstatright\"><b>Human Words:</b></td><td class=\"thstatright\">"
				+ bleuXliffStatistics.getWordSumHuman()
				+ "</td></tr><tr><td  class=\"thstatright\"><b>Difference MT Words - Human Words: </b></td\"><td class=\"thstatright\">"
				+ (bleuXliffStatistics.getWordSumMT() - bleuXliffStatistics.getWordSumHuman())
				+ "</td></tr><tr><td class=\"thstatlast\"><b>% Human Words / MT Words: </td><td class=\"tdstat\">"
				+ df.format(per) + "</td></tr></table>";

		table = table + "\t\t\t</table>";

		return table;
	}

	/**
	 * @param translatedDocument
	 * @param MTDocument
	 */
	public void compareTranslations(String translatedDocument, String MTDocument)
	{
		FileOutputStream fileout = null;
		FileOutputStream xmlfileout = null;
		FileOutputStream htmlfileout = null;
		OutputStreamWriter xmlfileoututf8 = null;
		try
		{
			fileout = new FileOutputStream(translatedDocument + ".csv");
			htmlfileout = new FileOutputStream(translatedDocument + ".html");
			xmlfileout = new FileOutputStream(translatedDocument + ".xml");
			xmlfileoututf8 = new OutputStreamWriter(xmlfileout, "UTF-8");
		}
		catch (FileNotFoundException e1)
		{
			e1.printStackTrace();
			return;
		}
		catch (UnsupportedEncodingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		XliffStatistics bleuXliffStatistics = new XliffStatistics(this.percents);
		XliffStatistics levenXliffStatistics = new XliffStatistics(this.percents);
		XliffStatistics wordlevenXliffStatistics = new XliffStatistics(this.percents);

		Timer timer = new Timer();
		timer.startTimer();
		File ft = new File(translatedDocument);
		File fm = new File(MTDocument);
		XliffDocument transdoc = new XliffDocument();
		XliffDocument mtdoc = new XliffDocument();
		// load the xml file
		transdoc.loadXmlFile(ft);
		mtdoc.loadXmlFile(fm);
		timer.stopTimer();
		System.out.println(timer.timerString("XLIFF translated file read    " + translatedDocument + ": Version "
				+ transdoc.getXliffVersion()));
		System.out.println(timer.timerString("XLIFF mt translated file read " + MTDocument + ": Version "
				+ mtdoc.getXliffVersion()));
		timer.startTimer();

		List<Element> files = transdoc.getFiles();
		int iSize = files.size();
		System.out.println("# XLIFF File Mode: " + this.mode);
		System.out.println("# XLIFF Translated Files: " + iSize);

		String sourceLanguage = "";
		String targetLanguage = "";
		if (files.size() > 0)
		{
			sourceLanguage = files.get(0).getAttributeValue("source-language");
			targetLanguage = files.get(0).getAttributeValue("target-language");
		}

		Vector<XliffMTQualityObject> translatedtargets = null;
		Vector<XliffMTQualityObject> mttranslatedtargets = null;

		if (mode.equals("2"))
		{
			translatedtargets = readTransUnitsMode2(transdoc, true);
			mttranslatedtargets = readTransUnitsMode2(mtdoc, false);
		}
		else if (mode.equals("3"))
		{
			translatedtargets = readTransUnitsMode3(transdoc, true);
			mttranslatedtargets = readTransUnitsMode3(mtdoc, false);
		}
		else
		{
			translatedtargets = readTransUnitsMode1(transdoc, true);
			mttranslatedtargets = readTransUnitsMode1(mtdoc, false);
		}

		int iMTSegments = 0;
		for (int i = 0; i < translatedtargets.size(); i++)
		{
			if (mttranslatedtargets.get(i).isbMT() == true)
				iMTSegments++;
		}

		int iMTUsedLines = 0;

		int ipos = 0, iminus = 0, iequal = 0, inum = 0;
		int iminusword = 0, iequalword = 0, iposword = 0;

		if (translatedtargets.size() == mttranslatedtargets.size())
		{
			String content = "#;BLEU;LevenshteinChar;DifferenceBLEULevenshteinChar;LevenshteinWord;DifferenceBLEULevenshteinWord;Ttokenlength;Tcharlength;Translation;MTokenlength;MTcharlength;MTtranslation\n";
			String xmlcontent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<translationMetric referenceDocument=\""
					+ translatedDocument + "\" candidateDocument=\"" + MTDocument + "\" source-language=\""
					+ sourceLanguage + "\" target-language=\"" + targetLanguage + "\">\n";

			try
			{
				htmlfileout
						.write("<!DOCTYPE html>\n<html>\n\t<head><title>Analysis of Human vs. MT translated XLIFF Files</title>"
								.getBytes());
				String css = "<!-- \n"
						+ ".filename { border-collapse:collapse; border:1px solid red; margin:0; } \n"
						+ ".infotable { border-collapse:collapse; border:1px solid blue; margin: 3; } \n"
						+ " .tdstat { font: 1em/50% Courier; vertical-align:text-middle; text-align: right; border-style: dashed; border-width:thin; } \n"
						+ " .thstatbottom { border-bottom-style: dashed; border-bottom-width:thin; text-align: right; } \n"
						+ " .thstatright { border-bottom-style: dashed; border-bottom-width:thin; border-right-style: dashed; border-right-width:thin; text-align: right; } \n"
						+ " .thstatlast { text-align: right; } \n" + " -->";

				htmlfileout.write(("<style type=\"text/css\">" + css + "</style></head>\n\t<body>\n").getBytes());
				htmlfileout
						.write(("<h1>MT Analysis </h1><table class=\"filename\"><tr><td><b>Human Translated Document</b></td><td>"
								+ translatedDocument
								+ "</td></tr><td><b>Machine Translated Document</b></td><td>"
								+ MTDocument + "</td></tr><table><p />\n").getBytes());
			}
			catch (IOException e1)
			{

				e1.printStackTrace();
			}

			try
			{
				fileout.write(content.getBytes());
				// xmlfileout.write(xmlcontent.getBytes());
				xmlfileoututf8.write(xmlcontent);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}

			for (int i = 0; i < translatedtargets.size(); i++)
			{
				if (mttranslatedtargets.get(i).isbMT() == false)
					continue;
				BleuMeasurer bm;
				bm = new BleuMeasurer();
				String candLine = null, refLine = null;
				String[] candTokens;
				String[] refTokens;
				candLine = mttranslatedtargets.get(i).getText();
				refLine = translatedtargets.get(i).getText();
				if (candLine == null)
					continue;
				if (refLine == null)
					continue;
				if (candLine.length() == 0)
					continue;
				if (refLine.length() == 0)
					continue;
				candLine = candLine.trim();
				refLine = refLine.trim();
				candTokens = candLine.split(splitchars); // tokenStringsConditionalSplitCharactersWord
															// splitchars
				refTokens = refLine.split(splitchars); // tokenStringsConditionalSplitCharactersWord
														// splitchars
				if (candTokens.length == 0)
					continue;
				if (refTokens.length == 0)
					continue;

				iMTUsedLines++;

				// add sentence to stats
				bm.addSentence(refTokens, candTokens);

				int leven = de.folt.similarity.LevenshteinSimilarity.levenshteinSimilarity(candLine, refLine, 0);
				int bleu = (int) (bm.bleu() * 100.0);
				int diff = bleu - leven;

				int levenwordbased = de.folt.similarity.LevenshteinSimilarity.levenshteinWordBasedSimilarity(candLine,
						refLine, 0);
				int diffword = bleu - levenwordbased;

				int mtwords = candTokens.length; // mt
				int humanwords = refTokens.length; // human

				bleuXliffStatistics.addPercentage(bleu, mtwords, humanwords);
				levenXliffStatistics.addPercentage(leven, mtwords, humanwords);
				wordlevenXliffStatistics.addPercentage(levenwordbased, mtwords, humanwords);

				if (diff > 0)
					ipos++;
				else if ((bleu - leven) < 0)
					iminus++;
				else
					iequal++;

				if (diffword > 0)
					iposword++;
				else if ((bleu - leven) < 0)
					iminusword++;
				else
					iequalword++;

				content = i + ";" + bleu + ";" + leven + ";" + diff + ";" + levenwordbased + ";" + diffword + ";"
						+ refTokens.length + ";" + refLine.length() + ";\"" + refLine + "\";" + candTokens.length + ";"
						+ candLine.length() + ";\"" + candLine + "\"\n";
				xmlcontent = "\t<trans-unit id=\"" + i + "\" BleuQuality=\"" + bleu + "\" LevenCharacterQuality=\""
						+ leven + "\" BleuLevenCharacterDifference=\"" + diff + "\" LevenWordQuality=\""
						+ levenwordbased + "\" BleuLevenWordDifference=\"" + diffword + "\">";
				xmlcontent = xmlcontent + "\n\t\t<source TotalWordCount=\"" + refTokens.length
						+ "\" TotalCharacterCount=\"" + refLine.length() + "\">" + refLine + "</source>";
				xmlcontent = xmlcontent + "\n\t\t<target TotalWordCount=\"" + candTokens.length
						+ "\" TotalCharacterCount=\"" + candLine.length() + "\">" + candLine + "</target>";
				xmlcontent = xmlcontent + "\n\t</trans-unit>\n";
				try
				{
					fileout.write(content.getBytes());
					xmlfileoututf8.write(xmlcontent);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				inum++;
			}
		}

		try
		{
			fileout.close();
			String xmlcontent = "\n</translationMetric>\n";

			xmlfileoututf8.write(xmlcontent);
			xmlfileoututf8.close();
			xmlfileout.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		System.out.println("Number of overall segments    \t" + translatedtargets.size());
		System.out.println("Number of MT segments    \t" + iMTSegments);
		System.out.println("Number of really used MT segments    \t" + iMTUsedLines);

		System.out.println("% Limits    \t" + java.util.Arrays.toString(bleuXliffStatistics.getBoundaries()));
		System.out.println("Segment Based Analysis");
		System.out.println("% BLEU      \t" + java.util.Arrays.toString(bleuXliffStatistics.getStatFields()));
		System.out.println("% Leven     \t" + java.util.Arrays.toString(levenXliffStatistics.getStatFields()));
		System.out.println("% Leven Word\t" + java.util.Arrays.toString(wordlevenXliffStatistics.getStatFields()));
		System.out.println("Word Based MT Analysis");
		System.out.println("% BLEU      \t" + java.util.Arrays.toString(bleuXliffStatistics.getWordFieldsMT())
				+ "\tSum Words: " + bleuXliffStatistics.getWordSumMT());
		System.out.println("% Leven     \t" + java.util.Arrays.toString(levenXliffStatistics.getWordFieldsMT())
				+ "\tSum Words: " + levenXliffStatistics.getWordSumMT());
		System.out.println("% Leven Word\t" + java.util.Arrays.toString(wordlevenXliffStatistics.getWordFieldsMT())
				+ "\tSum Words: " + wordlevenXliffStatistics.getWordSumMT());
		System.out.println("Word Based Human Analysis");
		System.out.println("% BLEU      \t" + java.util.Arrays.toString(bleuXliffStatistics.getWordFieldsHuman())
				+ "\tSum Words: " + bleuXliffStatistics.getWordSumHuman());
		System.out.println("% Leven     \t" + java.util.Arrays.toString(levenXliffStatistics.getWordFieldsHuman())
				+ "\tSum Words: " + levenXliffStatistics.getWordSumHuman());
		System.out.println("% Leven Word\t" + java.util.Arrays.toString(wordlevenXliffStatistics.getWordFieldsHuman())
				+ "\tSum Words: " + wordlevenXliffStatistics.getWordSumHuman());

		String analysisTable = analysisTable(bleuXliffStatistics, bleuXliffStatistics, levenXliffStatistics,
				wordlevenXliffStatistics);
		try
		{
			htmlfileout.write(analysisTable.getBytes());
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try
		{
			htmlfileout.write("\t</body>\n</html>".getBytes());
			htmlfileout.close();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		timer.stopTimer();
		System.out.println("#;bleu > leven;%;bleu < leven;%;bleu == leven;%");
		System.out.println(inum + ";" + ipos + ";" + ((float) ipos / (float) inum) * 100 + ";" + iminus + ";"
				+ ((float) iminus / (float) inum) * 100 + ";" + iequal + ";" + ((float) iequal / (float) inum) * 100);

		System.out.println(inum + ";" + iposword + ";" + ((float) iposword / (float) inum) * 100 + ";" + iminusword
				+ ";" + ((float) iminusword / (float) inum) * 100 + ";" + iequalword + ";"
				+ ((float) iequalword / (float) inum) * 100);

		System.out.println(timer.timerString("Finished with comparing:  " + inum + ": " + translatedDocument + " : "
				+ MTDocument));

	}

	public String[] getPercents()
	{
		return percents;
	}

	public String getSplitchars()
	{
		return splitchars;
	}

	public String[] getTokenStringsConditionalSplitCharactersWord()
	{
		return tokenStringsConditionalSplitCharactersWord;
	}

	/**
	 * @param doc
	 * @return
	 */
	private Vector<XliffMTQualityObject> readTransUnitsMode1(XliffDocument doc, boolean bSource)
	{
		try
		{
			List<Element> files = doc.getFiles();
			int iSize = files.size();
			System.out.println("# XLIFF  Files: " + iSize);
			Vector<XliffMTQualityObject> translatedtargets = new Vector<XliffMTQualityObject>();
			for (int i = 0; i < iSize; i++)
			{
				Element file = files.get(i);
				Element body = doc.getXliffBody(file);
				List<Element> transunits = null;
				if (body != null)
				{
					transunits = doc.getTransUnitList(body);
				}
				else
				{
					transunits = doc.getTransUnitList(file);
				}
				if (transunits != null)
				{
					for (int j = 0; j < transunits.size(); j++)
					{
						String note = transunits.get(j).getChildText("note");
						if ((note != null) && note.contains("[OLD]"))
						{
							XliffMTQualityObject xliffMTQualityObject = new XliffMTQualityObject(transunits.get(j).getChildText("target"), false, bSource);
							translatedtargets.add(xliffMTQualityObject);
							continue;
						}

						XliffMTQualityObject xliffMTQualityObject = new XliffMTQualityObject(transunits.get(j).getChildText("target"), true, bSource);
						translatedtargets.add(xliffMTQualityObject);
					}
				}
			}
			return translatedtargets;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	private Vector<XliffMTQualityObject> readTransUnitsMode2(XliffDocument doc, boolean bSource)
	{
		try
		{
			List<Element> files = doc.getFiles();
			int iSize = files.size();
			System.out.println("# XLIFF  Files: " + iSize);
			Vector<XliffMTQualityObject> translatedtargets = new Vector<XliffMTQualityObject>();
			// bMTTranslation = new Vector<Boolean>();
			Namespace ns = Namespace.getNamespace("iws", "http://www.idiominc.com/ws/asset");
			for (int i = 0; i < iSize; i++)
			{
				Element file = files.get(i);
				Element body = doc.getXliffBody(file);
				List<Element> transunits = null;
				if (body != null)
				{
					transunits = doc.getTransUnitList(body);
				}
				else
				{
					transunits = doc.getTransUnitList(file);
				}
				if (transunits != null)
				{
					for (int j = 0; j < transunits.size(); j++)
					{
						Element transunitt = transunits.get(j);
						Namespace ntrans = transunitt.getNamespace();
						Element iwsmetadata = transunitt.getChild("segment-metadata", ns);
						Element target = transunitt.getChild("target", ntrans);
						String targettext = target.getText();
						boolean bMTStatus = false;
						if (iwsmetadata != null)
						{
							Element iwsstatus = iwsmetadata.getChild("status", ns);
							if (iwsstatus != null)
							{
								// <iws:status translation_type="machine_translation"/>
								String mt = iwsstatus.getAttributeValue("translation_type");
								if ((mt != null) && mt.equals("machine_translation"))
									bMTStatus = true;
							}
						}
						XliffMTQualityObject xliffMTQualityObject = new XliffMTQualityObject(targettext, bMTStatus, bSource);
						translatedtargets.add(xliffMTQualityObject);
					}
				}
			}
			return translatedtargets;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	private Vector<XliffMTQualityObject> readTransUnitsMode3(XliffDocument doc, boolean bSource)
	{
		try
		{
			List<Element> files = doc.getFiles();
			int iSize = files.size();
			System.out.println("# XLIFF  Files: " + iSize);
			Vector<XliffMTQualityObject> translatedtargets = new Vector<XliffMTQualityObject>();
			// bMTTranslation = new Vector<Boolean>();
			Namespace ns = Namespace.getNamespace("iws", "http://www.idiominc.com/ws/asset");
			for (int i = 0; i < iSize; i++)
			{
				Element file = files.get(i);
				Element body = doc.getXliffBody(file);
				List<Element> transunits = null;
				if (body != null)
				{
					transunits = doc.getTransUnitList(body);
				}
				else
				{
					transunits = doc.getTransUnitList(file);
				}
				if (transunits != null)
				{
					for (int j = 0; j < transunits.size(); j++)
					{
						Element transunitt = transunits.get(j);
						Namespace ntrans = transunitt.getNamespace();
						Element iwsmetadata = transunitt.getChild("segment-metadata", ns);
						Element target = transunitt.getChild("target", ntrans);
						String targettext = target.getText();
						boolean bMTTranslation = false;
						if (targettext.equals(""))
						{
							bMTTranslation = false;
						}
						else if ((iwsmetadata != null) && (bSource == false))
						{
							Element iwsstatus = iwsmetadata.getChild("status", ns);
							String tmscore = iwsmetadata.getAttributeValue("tm_score");
							if (tmscore != null && tmscore.equals("100.00"))
							{
								bMTTranslation = false;
							}
							if (iwsstatus != null)
							{
								String mt = iwsstatus.getAttributeValue("match-quality"); // <iws:status match-quality="guaranteed"
								if ((mt != null) && (mt.equals("guaranteed")))
								{
									bMTTranslation = false;
								}
								else
								{
									bMTTranslation = true;
								}
							}
							else
							{
								bMTTranslation = true;
							}
						}
						else
						{
							bMTTranslation = true;
						}
						XliffMTQualityObject xliffMTQualityObject = new XliffMTQualityObject(targettext, bMTTranslation, bSource);
						translatedtargets.add(xliffMTQualityObject);
						// match-quality="guaranteed" - <iws:status match-quality="guaranteed" tm_origin="from_ws_tm" translation_status="finished" />
					}
				}
			}
			return translatedtargets;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public void setPercents(String[] percents)
	{
		this.percents = percents;
	}

	public void setSplitchars(String splitchars)
	{
		this.splitchars = splitchars;
	}

	public void setTokenStringsConditionalSplitCharactersWord(String tokenStringsConditionalSplitCharactersWord[])
	{
		this.tokenStringsConditionalSplitCharactersWord = tokenStringsConditionalSplitCharactersWord;
	}

}
