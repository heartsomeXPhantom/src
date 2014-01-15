package de.folt.models.applicationmodel.termtagger;

import java.util.Hashtable;

import de.folt.models.datamodel.DataSource;
import de.folt.models.datamodel.DataSourceInstance;
import de.folt.models.datamodel.DataSourceProperties;
import de.folt.util.WordHandling;

public class TermTagTest
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			String datasourceName = null;
			String sourceLanguage = null;
			String targetLanguage = null;
			DataSource datasource = null;

			String type = "tbx";

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
			sourceLanguage = arguments.get("-sourceLanguage");
			targetLanguage = arguments.get("-targetLanguage");

			type = arguments.get("-type");

			String sourceLanguageWordSplitChars = arguments.get("-sourceLanguageWordSplitChars");
			String targetLanguageWordSplitChars = arguments.get("-targetLanguageWordSplitChars");

			XliffTermTagger xliffTermTagger = new XliffTermTagger(XliffTermTagger.getGlobalDebug());
			xliffTermTagger.setWordHandling(new WordHandling());
			xliffTermTagger.setSourceLanguage(sourceLanguage);
			xliffTermTagger.setTargetLanguage(targetLanguage);
			xliffTermTagger.getWordHandling().init();
			if (sourceLanguageWordSplitChars != null)
			{
				xliffTermTagger.getWordHandling().setlanguageWordSplitChars(sourceLanguage, sourceLanguageWordSplitChars);
			}
			if (targetLanguageWordSplitChars != null)
			{
				xliffTermTagger.getWordHandling().setlanguageWordSplitChars(targetLanguage, targetLanguageWordSplitChars);
			}

			xliffTermTagger.setbFuzzy(false);
			xliffTermTagger.setFuzzyPercent(90);
			datasource = DataSourceInstance.createInstance(datasourceName);
			try
			{
				if (type.equalsIgnoreCase("tmx"))
				{
					DataSourceProperties model = new DataSourceProperties();
					model.put("dataModelClass", "de.folt.models.datamodel.tmxfile.TmxFileDataSource");
					model.put("tmxfile", datasourceName);
					// model.put("dataModelUrl", "openTMS.jar");
					System.out.println(model.toString());
					System.out.println("createInstance" + " TMX:" + datasourceName);
					datasource = DataSourceInstance.createInstance("TBX:" + datasourceName, model);
					System.out.println("createInstance" + " TMX:" + datasourceName + " getLastErrorCode="
							+ datasource.getLastErrorCode() + " >>> " + datasource);
				}
				else
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
			}
			catch (Exception e1)
			{
				e1.printStackTrace();
				System.exit(2);
				return;
			}

			TermTagObjectTable termTagObjectTable = new TermTagObjectTable(sourceLanguage, targetLanguage);
			termTagObjectTable.setWordHandling(xliffTermTagger.getWordHandling());
			termTagObjectTable.setStoredLinguisticProperties("termNote.normativeAuthorization");
			termTagObjectTable.setbFuzzy(xliffTermTagger.isbFuzzy());
			termTagObjectTable.setbLowercase(xliffTermTagger.isbLowerCase());
			termTagObjectTable.setFuzzyPercent(xliffTermTagger.getFuzzyPercent());
			termTagObjectTable.setbStemmed(xliffTermTagger.isbStemmed());
			termTagObjectTable.bAddPhrases(datasource, sourceLanguage, targetLanguage);
			termTagObjectTable.sortPhraseTableSizes();

			if (XliffTermTagger.getGlobalDebug())
			{
				System.out.println("Termtable:");
				System.out.println(termTagObjectTable.stringify());
			}
			
			System.out.println("Test finished");
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
