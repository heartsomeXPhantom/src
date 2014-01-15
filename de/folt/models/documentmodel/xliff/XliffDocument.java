/*
 * Created on 26.01.2009
 * 
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.models.documentmodel.xliff;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;

import de.folt.constants.OpenTMSConstants;
import de.folt.models.datamodel.BasicDataSource;
import de.folt.models.datamodel.DataSource;
import de.folt.models.datamodel.DataSourceInstance;
import de.folt.models.datamodel.LinguisticProperties;
import de.folt.models.datamodel.LinguisticProperty;
import de.folt.models.datamodel.MonoLingualObject;
import de.folt.models.datamodel.MultiLingualObject;
import de.folt.models.datamodel.GeneralLinguisticObject.LinguisticTypes;
import de.folt.models.datamodel.phrasetranslate.PhraseTranslateResult;
import de.folt.models.documentmodel.document.XmlDocument;
import de.folt.models.documentmodel.tbx.TbxDocument;
import de.folt.models.documentmodel.tmx.TmxProp;
import de.folt.rpc.webserver.OpenTMSServer;
import de.folt.util.CorrectTradosIllegalXMLCharacter;
import de.folt.util.OpenTMSException;
import de.folt.util.Timer;

/**
 * This class implements several utility functions for reading the main elements
 * and attributes of a XLIFF document. <br>
 * A key method is the translate method. It translates the xliff document based
 * on source language and target language and similarity using a specific data
 * source. The method supports multi-threading. Depending on the number of
 * processors available it distributes the translation of a set of trans-unit
 * towards several processors. The version currently supports Xliff 1.1 and 1.2.<br>
 * When using the JDOM Element methods like getChild etc. it is recommended
 * explicitly setting the NAMESPACE. Example: getChild("target",
 * this.getNamespace()) or Element source = new Element("source",
 * this.getNamespace()); As Xliff 1.2 may contain a reference to the Xliff
 * specification like
 * 
 * <pre>
 * &lt;xliff xmlns="urn:oasis:names:tc:xliff:document:1.2" 
 *        xmlns:tek="http://www.tektronix.com"
 *        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
 *        xsi:schemaLocation="urn:oasis:names:tc:xliff:document:1.2         
 *        xliff-core-1.2-strict.xsd http://www.tektronix.com tek_code_trial.xsd" 
 *        version="1.2"&gt;
 * </pre>
 * 
 * this ensures correct getting and setting of the elements.
 * <p>
 * For details of XLIFF: {@see <a
 * href="http://docs.oasis-open.org/xliff/xliff-core/xliff-core.html">XLIFF</a>} <br>
 * Currently JDOM is used for parsing the XML XLIFF documents.
 * 
 * @author klemens
 * 
 */
public class XliffDocument extends XmlDocument
{

	/**
     * 
     */
	private static final long	serialVersionUID	= 1189334996710165556L;

	/**
	 * getFileSourceTargetLanguage returns the source and target language of a xliff document from the file element (the first file element)
	 * 
	 * @param xliffFile
	 *            the path to the XLIFF file
	 * @return Hashtable with keys source-language and target-language
	 */
	public static Hashtable<String, String> getFileSourceTargetLanguage(String xliffFile)
	{
		Hashtable<String, String> lan = new Hashtable<String, String>();

		XliffDocument doc;
		try
		{
			File f = new File(xliffFile);
			doc = new XliffDocument();
			doc.loadXmlFile(f);
			f = new File(xliffFile);
			List<Element> files = doc.getFiles();
			int iSize = files.size();
			if (iSize > 0) // just a
			{
				Element file = files.get(0);
				String sl = doc.getFileSourceLanguage(file);
				String tl = doc.getFileTargetLanguage(file);
				if (sl != null)
					lan.put("source-language", sl);
				if (tl != null)
					lan.put("target-language", tl);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		doc = null;
		return lan;
	}

	/**
	 * main
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			String xlifffile = args[0];
			String tmxfile = null;

			String sourceLanguage = null;
			String targetLanguage = null;

			if (args.length > 1)
			{
				tmxfile = args[1];
			}

			if (args.length > 2)
			{
				sourceLanguage = args[2];
			}

			if (args.length > 3)
			{
				targetLanguage = args[3];
			}

			Timer timer = new Timer();
			timer.startTimer();
			File f = new File(xlifffile);
			XliffDocument doc = new XliffDocument();
			// load the xml file
			doc.loadXmlFile(f);
			timer.stopTimer();
			System.out
					.println(timer.timerString("XLIFF file read " + xlifffile + ": Version " + doc.getXliffVersion()));
			timer.startTimer();
			// save it
			doc.saveToXmlFile(xlifffile + ".copy.xlf");
			timer.stopTimer();
			System.out
					.println(timer.timerString("XLIFF file save " + xlifffile + ": Version " + doc.getXliffVersion()));

			List<Element> files = doc.getFiles();
			int iSize = files.size();
			System.out.println("# XLIFF Files: " + iSize);

			int iProcessors = Runtime.getRuntime().availableProcessors();
			System.out.println("# Processors used: " + iProcessors);

			for (int i = 0; i < iSize; i++)
			{
				Element file = files.get(i);
				Element body = doc.getXliffBody(file);
				System.out.println("# XLIFF trans-unit: " + doc.getTransUnitList(body).size());
				List<Element> transunits = doc.getTransUnitList(body);
				int tSize = transunits.size();
				for (int j = 0; j < tSize; j++)
				{
					Element transunit = transunits.get(j);
					List<Element> alttranss = doc.getAltTransList(transunit);
					System.out.println("trans-unit " + j);
					int altSize = alttranss.size();
					for (int k = 0; k < altSize; k++)
					{
						Element alttrans = alttranss.get(k);
						float fqual = doc.getMatchQualityAsFloat(alttrans);
						System.out.println("\talt-trans " + k + ": fqual " + fqual);
					}

				}
			}

			if (tmxfile == null)
			{
				doc = null;
				System.exit(0);
			}
			System.out.println("createInstance" + " datasource " + tmxfile);
			DataSource datasource = DataSourceInstance.createInstance(tmxfile); // DataSourceInstance.createInstance("TMX:"
			// +
			// tmxfile,
			// model);
			System.out.println("createInstance" + " datasource:" + tmxfile + " getLastErrorCode="
					+ datasource.getLastErrorCode() + " >>> " + datasource);

			// datasource = DataSourceInstance.getInstance("TMX:" + tmxfile);
			System.out.println("getInstance" + " :" + tmxfile + " >>> " + datasource);
			System.out.println("Instances: ");
			BasicDataSource tmxdatasource = (BasicDataSource) datasource;
			tmxdatasource.cleanDataSource();
			if (tmxdatasource.getFuzzyTree() != null)
				System.out.println("Number of fuzzy nodes: " + tmxdatasource.getFuzzyTree().countNodes());

			String[] inst = DataSourceInstance.getAllDataSourceInstanceNames();
			for (int i = 0; i < inst.length; i++)
			{
				System.out.println(i + ": " + inst[i]);
			}

			timer.startTimer();
			doc.translate(datasource, sourceLanguage, targetLanguage, 70, -1, null);
			doc.subSegmentTranslate(datasource, sourceLanguage, targetLanguage, null);
			timer.stopTimer();
			System.out.println(timer.timerString("XLIFF translation " + xlifffile + ": datasource " + tmxfile));

			doc.saveToXmlFile(xlifffile + ".translate.xlf");
			DataSourceInstance.removeInstance(tmxfile);

			doc = null;
			System.exit(0);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * updateXliffDocument updates a given xliff document with an update xliff
	 * document. It compares the ids and if matches updates approved status and
	 * target.
	 * 
	 * @param sourceDocument
	 *            the document to update
	 * @param updateDocument
	 *            the document containing changed entries
	 * @return the number of updates done
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	public static int updateXliffDocument(File sourceDocument, File updateDocument)
	{
		try
		{
			int iUpdates = 0;
			if (sourceDocument == null)
				return -1;
			if (updateDocument == null)
				return -2;
			if (!sourceDocument.exists())
				return -3;
			if (!updateDocument.exists())
				return -4;

			XliffDocument sourceXliff = new XliffDocument(sourceDocument);
			XliffDocument updateXliff = new XliffDocument(updateDocument);
			if (sourceXliff == null)
				return -5;
			if (updateXliff == null)
				return -6;

			List<Element> sourcefile = sourceXliff.getFiles();
			List<Element> updatefile = updateXliff.getFiles();
			for (int i = 0; i < sourcefile.size(); i++)
			{
				Element sourceFileElement = sourcefile.get(i);
				Element sourceBody = sourceFileElement.getChild("body");
				List<Element> sourceTransUnit = sourceXliff.getTransUnitList(sourceBody);
				for (int j = 0; j < updatefile.size(); j++)
				{
					Element updateFileElement = updatefile.get(j);
					Element updateBody = updateFileElement.getChild("body");
					List<Element> updateTransUnit = updateXliff.getTransUnitList(updateBody);
					for (int k = 0; k < sourceTransUnit.size(); k++)
					{
						for (int l = 0; l < updateTransUnit.size(); l++)
						{
							if (sourceTransUnit.get(k).getAttributeValue("id")
									.equals(updateTransUnit.get(l).getAttributeValue("id")))
							{
								String approved = updateTransUnit.get(l).getAttributeValue("approved");
								sourceTransUnit.get(k).setAttribute("approved", approved);
								sourceTransUnit.get(k).removeChild("target");
								Element target = updateTransUnit.get(l).getChild("target");
								target.detach();
								Element transUnit = sourceTransUnit.get(k);
								List<Element> transUnitElements = transUnit.getChildren();
								if (transUnitElements.size() == 1)
								{
									sourceTransUnit.get(k).addContent(target);
								}
								else
								{
									sourceTransUnit.get(k).addContent(1, target);
								}
							}
						}
					}
				}
			}

			sourceXliff.saveToXmlFile();
			return iUpdates;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return -99;
		}
	}

	/**
	 * updateXliffDocument updates a given xliff document with an update xliff
	 * document. It compares the ids and if matches updates approved status and
	 * target.
	 * 
	 * @param sourceDocument
	 *            the document to update
	 * @param updateDocument
	 *            the document containing changed entries
	 * @return the number of updates done
	 */
	public static int updateXliffDocument(String sourceDocument, String updateDocument)
	{
		if (sourceDocument == null)
			return -1;
		if (updateDocument == null)
			return -2;
		return updateXliffDocument(new File(sourceDocument), new File(updateDocument));
	}

	private List<Element>	files			= null;

	/**
	 * the source language given in the file statement
	 */
	private String			sourceLanguage	= "";

	/**
	 * the target language given in the file statement
	 */
	private String			targetLanguage	= "";

	/**
     * 
     */
	public XliffDocument()
	{
		super();
	}

	/**
	 * @param file
	 */
	public XliffDocument(File file)
	{
		super(file);
	}

	/**
	 * @param fileName
	 */
	public XliffDocument(String fileName)
	{
		super(fileName);
	}

	/**
	 * addAltTrans adds an alt-trans to the given trans-unit
	 * 
	 * @param transunit
	 *            the transunit
	 * @param alttrans
	 *            the alt-trans to add
	 */
	public void addAltTrans(Element transunit, Element alttrans)
	{
		transunit.addContent(alttrans);
	}

	/**
	 * addAltTrans a a new alt-trans using source = sourceMono and target =
	 * targetMono
	 * 
	 * @param transUnit
	 *            the trans-unit <trans-unit ... >
	 * @param sourceMono
	 *            source mono lingual object
	 * @param targetMono
	 *            target mono lingual object
	 * @param iMatchquality
	 *            the matchquality
	 * @return the alt-trans element
	 */
	public Element addAltTrans(Element transUnit, MonoLingualObject sourceMono, MonoLingualObject targetMono,
			int iMatchquality)
	{
		try
		{
			Element alttrans = new Element("alt-trans");

			boolean bAlreadyFound = bAltTransContained(transUnit, sourceMono, targetMono);
			if (bAlreadyFound)
				return null;

			Element source = new Element("source", this.getNamespace());
			convertMonoLingualObjectToElement(source, sourceMono);
			alttrans.addContent(source);
			Element target = new Element("target");
			convertMonoLingualObjectToElement(target, targetMono);
			alttrans.addContent(target);
			alttrans.setAttribute("match-quality", iMatchquality + "");
			alttrans.setAttribute("id", sourceMono.getUniqueID());
			alttrans.setAttribute("space", "preserve", Namespace.XML_NAMESPACE);
			addAltTransProperties(alttrans, sourceMono, targetMono);
			addAltTransSorted(transUnit, alttrans);
			return alttrans;
		}
		catch (Exception ex)
		{
			return null;
		}
	}

	/**
	 * addAltTrans a a new alt-trans using source = sourceMono and target =
	 * targetMono
	 * 
	 * @param transUnit
	 *            the trans-unit <trans-unit ... >
	 * @param sourceMono
	 *            source mono lingual object
	 * @param targetmonos
	 *            target mono lingual objects
	 * @param iMatchquality
	 *            the matchquality
	 * @param translationParameters
	 *            a hash table containing key value pairs defining whih
	 *            attrobutes etc. to add.<br>
	 *            ignoreProps = true / do not write attributes from
	 *            LingustocObjects MUL/MOL<br>
	 *            formatMatchPenalty <number> as String - this penalty is
	 *            subtracted if formatted segment and plain text segment do not
	 *            match; default = 1; plain text segment = segment where all
	 *            tags like ph etc. are removed.
	 * @return the alt-trans element
	 */
	public Element addAltTrans(Element transUnit, MonoLingualObject sourceMono, Vector<MonoLingualObject> targetmonos,
			int iMatchquality, Hashtable<String, Object> translationParameters)
	{
		try
		{
			// we should check if the source/target is already contained in the
			// transUnit

			boolean bAlreadyFound = bAltTransContained(transUnit, sourceMono, targetmonos);

			if (bAlreadyFound)
			{
				System.out.println(transUnit.getAttributeValue("id") + ": alt-trans contained = " + bAlreadyFound);
				return null;
			}
			Element alttrans = new Element("alt-trans", this.getNamespace());
			boolean bAddAttributes = true;
			int iFormatMatchPenalty = 1;
			if (translationParameters != null)
			{
				String addattr = (String) translationParameters.get("ignoreProps");
				if (addattr != null)
				{
					if (addattr.equals("true"))
						bAddAttributes = false;
				}
				addattr = (String) translationParameters.get("formatMatchPenalty");
				if (addattr != null)
				{
					try
					{
						iFormatMatchPenalty = Integer.parseInt(addattr);
					}
					catch (Exception ex)
					{
						iFormatMatchPenalty = 1;
					}
				}
			}

			Element elem = null;

			Element source = convertMonoLingualObjectToSource(sourceMono);
			alttrans.addContent(source);

			for (int k = 0; k < targetmonos.size(); k++)
			{
				MonoLingualObject targetMono = targetmonos.get(k);
				Element target = convertMonoLingualObjectToTarget(targetMono);
				alttrans.addContent(target);
				if (bAddAttributes && (targetMono.getLinguisticProperties() != null))
				{
					elem = linguisticPropertiesToProp("TARGET-MOL-" + targetMono.getUniqueID(),
							(LinguisticProperties) targetMono.getLinguisticProperties());
					if (elem != null)
						alttrans.addContent(elem);
				}
			}
			if (bAddAttributes && (sourceMono.getLinguisticProperties() != null))
			{
				elem = linguisticPropertiesToProp("SOURCE-MOL" + sourceMono.getUniqueID(),
						(LinguisticProperties) sourceMono.getLinguisticProperties());
				if (elem != null)
					alttrans.addContent(elem);
			}

			if (bAddAttributes && (sourceMono.getParentMultiLingualObject() != null)
					&& (sourceMono.getParentMultiLingualObject().getUniqueID() != null))
			{
				elem = linguisticPropertiesToProp("MULTI-" + sourceMono.getParentMultiLingualObject().getUniqueID(),
						(LinguisticProperties) sourceMono.getParentMultiLingualObject().getLinguisticProperties());
				if (elem == null)
				{
					TmxProp tmxProp = new TmxProp(sourceMono.getParentMultiLingualObject().getUniqueID(), "", "",
							TmxProp.PropType.CORE, "creationid", 0);
					LinguisticProperty lingProp = new LinguisticProperty(tmxProp.toString(), tmxProp);
					(sourceMono.getParentMultiLingualObject()).addLinguisticProperty(lingProp);
					elem = linguisticPropertiesToProp(
							"MULTI-" + sourceMono.getParentMultiLingualObject().getUniqueID(),
							(LinguisticProperties) sourceMono.getParentMultiLingualObject().getLinguisticProperties());
				}
				if (elem != null)
					alttrans.addContent(elem);
			}

			if (iFormatMatchPenalty > 0)
			{
				if (translationParameters != null)
				{
					String segment = (String) translationParameters.get("translate.SourceFormattedSegment");
					if (segment != null)
					{
						String formattedMatch = sourceMono.getFormattedSegment();
						if (!segment.equals(formattedMatch))
						{
							LinguisticProperties matprop = new LinguisticProperties();
							LinguisticProperty prop = new LinguisticProperty("plain-match-quality", iMatchquality + "");
							matprop.put("plain-match-quality", prop);
							iMatchquality = iMatchquality - iFormatMatchPenalty;
							elem = linguisticPropertiesToProp("quality-info", matprop);
							if (elem != null)
								alttrans.addContent(elem);
							matprop = null;
						}
					}
				}
			}
			alttrans.setAttribute("match-quality", iMatchquality + ""); // ,
			// this.getNamespace());
			alttrans.setAttribute("id", sourceMono.getUniqueID()); // ,
			// this.getNamespace());
			alttrans.setAttribute("space", "preserve", Namespace.XML_NAMESPACE);
			addAltTransSorted(transUnit, alttrans);
			return alttrans;
		}
		catch (Exception ex)
		{
			System.out.println(transUnit.getAttributeValue("id") + ": alt-trans  Exception");
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * addAltTransProperties adds the alt-trans propierties from source MOL and
	 * target MOL
	 * 
	 * @param alttrans
	 *            the alt-trans element
	 * @param sourceMono
	 *            source monolingual object
	 * @param targetMono
	 *            target monolingual object
	 */
	private void addAltTransProperties(Element alttrans, MonoLingualObject sourceMono, MonoLingualObject targetMono)
	{
		Element propgroup = linguisticPropertiesToProp("MUL", (LinguisticProperties) sourceMono
				.getParentMultiLingualObject().getLinguisticProperties());
		alttrans.addContent(propgroup);
		propgroup = linguisticPropertiesToProp("SOURCE-MOL",
				(LinguisticProperties) sourceMono.getLinguisticProperties());
		alttrans.addContent(propgroup);
		propgroup = linguisticPropertiesToProp("TARGET-MOL",
				(LinguisticProperties) targetMono.getLinguisticProperties());
		alttrans.addContent(propgroup);
	}

	/**
	 * addAltTransSorted adds an alt-trans to the given trans-unit by searching
	 * for the next lower match-quality
	 * 
	 * @param transunit
	 *            the trans-unit
	 * @param alttrans
	 *            the alt-trans to add sorted
	 */
	@SuppressWarnings("unchecked")
	public void addAltTransSorted(Element transunit, Element alttrans)
	{
		List<Element> children = (List<Element>) transunit.getChildren("alt-trans", this.getNamespace());
		float qualadd = getMatchQualityAsFloat(alttrans);
		int iSize = children.size();
		for (int i = 0; i < iSize; i++)
		{
			Element at = children.get(i);
			float qual = getMatchQualityAsFloat(at);
			if (qualadd > qual)
			{
				int iPos = transunit.indexOf(at);
				transunit.addContent(iPos, alttrans);
				return;
			}
		}
		transunit.addContent(alttrans);
		return;
	}

	/**
	 * addPhase adds a new phase to a phase group - Example: <phase
	 * company-name="Heartsome Europe GmbH" date="Thu Nov 20 10:28:26 CET 2008"
	 * phase-name="1" process-name="pre-process" tool="XML2XLIFF version 2.0" />
	 * 
	 * @param phaseGroup
	 *            the phase group
	 * @param company
	 *            the company name
	 * @param processName
	 *            the process name - if null "OpenTMS Translate" will be used
	 * @param tool
	 *            the tool - if null "OpenTMS Translate" will be used
	 * @param jobId
	 *            - job-id
	 * @param contactName
	 *            -contact-name
	 * @param contactEmail
	 *            contact-email
	 * @param contactPhone
	 *            -contact phone
	 */
	public void addPhase(Element phaseGroup, String company, String processName, String tool, String jobId,
			String contactName, String contactEmail, String contactPhone)
	{
		Element phase = new Element("phase");
		int iPhases = getPhases(phaseGroup).size() + 1;
		phase.setAttribute("phase-name", iPhases + "");
		Date cal = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		phase.setAttribute("date", df.format(cal));
		if (company != null)
			phase.setAttribute("company-name", company);
		if (processName != null)
			phase.setAttribute("process-name", processName);
		else
			phase.setAttribute("process-name", "OpenTMS Translate");
		if (tool != null)
			phase.setAttribute("tool", tool);
		else
			phase.setAttribute("tool", "OpenTMS Translate");
		if (contactName != null)
			phase.setAttribute("contact-name", contactName);
		if (contactEmail != null)
			phase.setAttribute("contact-e-mal", contactEmail);
		if (contactPhone != null)
			phase.setAttribute("contact-phone", contactPhone);
		if (jobId != null)
			phase.setAttribute("job-id", jobId);
		phaseGroup.addContent(phase);
		cal = null;
		df = null;

	}

	/**
	 * addSubSegmentTranslationToGlossary add a phrase (source and target) to
	 * the internal dictionary
	 * 
	 * @param file
	 *            the file element where to add the phrase
	 * @param sourceTerm
	 *            the source term
	 * @param targetTerm
	 *            the target term
	 * @return
	 */
	public Element[] addSubSegmentTranslationToGlossary(Element file, String sourceTerm, String targetTerm)
	{
		Element glossary = file.getChild("glossary", this.getNamespace());
		Element internalfile = null;
		String glosstext = "";
		if (glossary == null)
		{
			glossary = new Element("glossary", this.getNamespace());
			internalfile = new Element("internal-file", this.getNamespace());
			internalfile.setAttribute("form", "csv"); // , this.getNamespace());
			glossary.addContent(internalfile);
			glosstext = this.getFileSourceLanguage(file) + ";" + this.getFileTargetLanguage(file) + "|\n";

		}
		else
		{
			internalfile = glossary.getChild("internal-file", this.getNamespace());
			glosstext = getGlossary(file);
		}
		Element[] glossaries = new Element[1];

		String newTerm = sourceTerm + ";" + targetTerm + "|";
		newTerm = newTerm.replaceAll("&", "&amp;");
		newTerm = newTerm.replaceAll("<", "&lt;");
		newTerm = newTerm.replaceAll(">", "&gt;");
		if (glosstext.indexOf(newTerm) == -1)
		{
			glosstext = glosstext + newTerm;
		}

		internalfile.setText(glosstext);
		glossaries[0] = glossary;
		return glossaries;
	}

	/**
	 * addSubSegmentTranslationToGlossary
	 * 
	 * @param file
	 * @param sourceTerms
	 * @param targetTerms
	 */
	public Element[] addSubSegmentTranslationToGlossary(Element file, Vector<String> sourceTerms,
			Vector<String> targetTerms)
	{

		Element glossary = file.getChild("glossary", this.getNamespace());
		Element internalfile = null;
		String glosstext = "";
		if (glossary == null)
		{
			glossary = new Element("glossary", this.getNamespace());
			internalfile = new Element("internal-file", this.getNamespace());
			internalfile.setAttribute("form", "csv"); // , this.getNamespace());
			glossary.addContent(internalfile);
			glosstext = this.getFileSourceLanguage(file) + ";" + this.getFileTargetLanguage(file) + "|\n";
			file.addContent(glossary);
		}
		else
		{
			internalfile = glossary.getChild("internal-file", this.getNamespace());
			glosstext = getGlossary(file);
		}
		Element[] glossaries = new Element[1];

		for (int i = 0; i < sourceTerms.size(); i++)
		{

			String newTerm = sourceTerms.get(i) + ";" + targetTerms.get(i) + "|";
			newTerm = newTerm.replaceAll("&", "&amp;");
			newTerm = newTerm.replaceAll("<", "&lt;");
			newTerm = newTerm.replaceAll(">", "&gt;");
			if (glosstext.indexOf(newTerm) == -1)
			{
				glosstext = glosstext + newTerm + "\n";
			}
		}

		internalfile.setText(glosstext);
		glossaries[0] = glossary;
		return glossaries;

	}

	/**
	 * bAltTransContained check if a source mono / target monos combination
	 * already exists in the trans-unit
	 * 
	 * @param transUnit
	 *            the trans-unit to check
	 * @param sourceMono
	 *            the source mono - sourceMono.getFormattedSegment() used for
	 *            comparison
	 * @param targetmono
	 *            the target mono - translation
	 * @return true if contained (all targets are contained too!), false
	 *         otherwise
	 */
	public boolean bAltTransContained(Element transUnit, MonoLingualObject sourceMono, MonoLingualObject targetmono)
	{
		Vector<MonoLingualObject> targetmonos = new Vector<MonoLingualObject>();
		targetmonos.add(targetmono);
		return bAltTransContained(transUnit, sourceMono, targetmonos);
	}

	/**
	 * bAltTransContained check if a source mono / target monos combination
	 * already exists in the trans-unit
	 * 
	 * @param transUnit
	 *            the trans-unit to check
	 * @param sourceMono
	 *            the source mono - sourceMono.getFormattedSegment() used for
	 *            comparison
	 * @param targetmonos
	 *            the target monos - targetmonos.get(i).getFormattedSegment()
	 *            used for comparison; identical targets are removed from
	 *            targetmonos vector
	 * @return true if contained (all targets are contained too!), false
	 *         otherwise
	 */
	@SuppressWarnings("unchecked")
	public boolean bAltTransContained(Element transUnit, MonoLingualObject sourceMono,
			Vector<MonoLingualObject> targetmonos)
	{
		boolean retValue = false;
		List<Element> altransmatches = (List<Element>) transUnit.getChildren("alt-trans", this.getNamespace());
		if (altransmatches.size() == 0)
			return false;

		for (int i = 0; i < altransmatches.size(); i++)
		{
			Element altTrans = altransmatches.get(i);
			String source = elementToString(altTrans.getChild("source", this.getNamespace()));
			source = source.replaceAll("<source.*?>", "");
			source = source.replaceAll("</source>", "");
			if (!source.equals(sourceMono.getFormattedSegment()))
			{
				continue;
			}
			List<Element> targetmatches = (List<Element>) altransmatches.get(i).getChildren("target",
					this.getNamespace());
			for (int j = 0; j < targetmatches.size(); j++)
			{
				String target = elementToString(targetmatches.get(j));
				target = target.replaceAll("<target.*?>", "");
				target = target.replaceAll("</target>", "");
				for (int k = 0; k < targetmonos.size(); k++)
				{
					if (target.equals(targetmonos.get(k).getFormattedSegment()))
					{
						targetmonos.remove(k);
					}
				}
			}
		}

		if (targetmonos.size() == 0)
		{
			retValue = true;
		}

		return retValue;
	}

	/**
	 * convertMonoLingualObjectToElement converts a MonoLingualObject to an
	 * Element converting the formatted segment to content
	 * 
	 * @param element
	 * @param mono
	 * @return
	 */
	public Element convertMonoLingualObjectToElement(Element element, MonoLingualObject mono) throws OpenTMSException
	{
		element.setAttribute("lang", mono.getLanguage(), Namespace.XML_NAMESPACE);
		try
		{
			Element segment = this.buildElement(mono.getFormattedSegment());
			element.addContent(segment); // ok must be changed to setContent and
			// convert getFormattedSegment() to
			// Element!!!!
		}
		catch (Exception ex)
		{
			throw new OpenTMSException("convertMonoLingualObjectToElement", "buildElement",
					OpenTMSConstants.OpenTMS_BUILDELEMET_ERROR, XliffDocument.class.getName(), ex);
		}

		return element;
	}

	/**
	 * convertMonoLingualObjectToSource converts a MonoLingualObject to a source
	 * element converting the formatted segment to content
	 * 
	 * @param mono
	 *            the mono lingual object
	 * @return the source element
	 */
	public Element convertMonoLingualObjectToSource(MonoLingualObject mono) throws OpenTMSException
	{
		try
		{
			Element element = this.buildElement("<source>" + mono.getFormattedSegment() + "</source>");
			element.setNamespace(this.getNamespace());
			element.setAttribute("lang", mono.getLanguage(), Namespace.XML_NAMESPACE);
			return element;
		}
		catch (Exception ex)
		{
			throw new OpenTMSException("convertMonoLingualObjectToElement", "buildElement",
					OpenTMSConstants.OpenTMS_BUILDELEMET_ERROR, XliffDocument.class.getName(), ex);
		}
	}

	/**
	 * convertMonoLingualObjectToTarget converts a MonoLingualObject to a target
	 * element converting the formatted segment to content
	 * 
	 * @param mono
	 *            the mono lingual object
	 * @return the target element
	 */
	public Element convertMonoLingualObjectToTarget(MonoLingualObject mono) throws OpenTMSException
	{
		try
		{
			Element element = this.buildElement("<target>" + mono.getFormattedSegment() + "</target>");
			element.setNamespace(this.getNamespace());
			element.setAttribute("lang", mono.getLanguage(), Namespace.XML_NAMESPACE);
			return element;
		}
		catch (Exception ex)
		{
			throw new OpenTMSException("convertMonoLingualObjectToElement", "buildElement",
					OpenTMSConstants.OpenTMS_BUILDELEMET_ERROR, XliffDocument.class.getName(), ex);
		}
	}

	/**
	 * convertTmxSegStringToXliffString converts a tmx seg String to a Xliff
	 * segment, esp. type= becomes ctype=
	 * 
	 * @param segstring
	 *            the tuv segment
	 * @return the xliff formatted sring
	 */
	public String convertTmxSegStringToXliffString(String segstring)
	{
		return segstring.replaceAll("(type=\".*?\")", "c$1");
	}

	/**
	 * existsTranslationBasedOnOrigin check if for a given origin value a
	 * translation exists
	 * 
	 * @param transUnit
	 *            the tran-unit to check
	 * @param originValue
	 *            the value of the origin attribute
	 * @return true if exists otherwise false
	 */
	@SuppressWarnings("unchecked")
	public boolean existsTranslationBasedOnOrigin(Element transUnit, String originValue)
	{
		if (originValue == null)
			return false;
		List<Element> altTrans = (List<Element>) transUnit.getChildren("alt-trans", this.getNamespace());
		for (int i = 0; i < altTrans.size(); i++)
		{
			Element alt = altTrans.get(i);
			if ((alt != null) && alt.getAttributeValue("origin").equals(originValue))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * exportInternalOpenTMSTerminology export the internal term dictionary of
	 * the xliff file to a tbx file; it creates first a new tbxDocument
	 * tbxDocumentName if it does not exist; otherwise appends entries to
	 * existing tbx document; it exports all the terms in all files contained in
	 * the xliff document.
	 * 
	 * @param tbxDocumentName
	 *            the output tbx file name
	 * @return true in case of success, otherwise false
	 */
	public boolean exportInternalOpenTMSTerminology(String tbxDocumentName)
	{
		try
		{
			File outfile = new File(tbxDocumentName);
			if (!outfile.exists())
			{
				TbxDocument tbxnew = new TbxDocument();
				boolean bRes = tbxnew.createDocument(tbxDocumentName);
				if (!bRes)
					return bRes;
				tbxnew = null;
			}
			// open file
			TbxDocument tbxdocument = new TbxDocument();
			tbxdocument.setXmlDocumentName(tbxDocumentName);
			tbxdocument.loadXmlFile(tbxDocumentName);

			Element root = tbxdocument.getRoot();
			Element text = root.getChild("text");
			if (text == null)
			{
				text = new Element("text");
				root.addContent(text);
			}
			Element body = text.getChild("body");
			if (body == null)
			{
				body = new Element("body");
				text.addContent(body);
			}
			// Set root element
			List<Element> files = this.getFiles();
			for (int k = 0; k < files.size(); k++)
			{
				Element file = files.get(k);
				Element header = file.getChild("header", this.getNamespace());
				if (header == null)
					continue;

				Element glossary = header.getChild("glossary", this.getNamespace());
				if (glossary == null)
					continue;

				Element internalFile = glossary.getChild("internal-file", this.getNamespace());
				if (internalFile == null)
					continue;

				String type = internalFile.getAttributeValue("form"); // ,
				// this.getNamespace());
				if ((type == null) || !type.equals("csv")) // not an openTMS
				// internal
				// dictionary
				{
					continue;
				}
				String dictionary = internalFile.getText();
				String[] entries = dictionary.split("\\|");
				// get list of children
				String sourceLanguage = "";
				String targetLanguage = "";
				for (int i = 0; i < entries.length; i++)
				{
					String entry = entries[i];
					entry = entry.replace("\n", "");
					entry = entry.replace("\r", "");
					String[] splitter = entry.split(";");
					if (splitter.length != 2)
						continue;
					if (i == 0)
					{
						sourceLanguage = splitter[0];
						targetLanguage = splitter[1];
						continue;
					}
					Element termEntry = new Element("termEntry");

					Element langSetSource = new Element("langSet");
					langSetSource.setAttribute("lang", sourceLanguage, Namespace.XML_NAMESPACE);
					termEntry.addContent(langSetSource);
					Element tigSource = new Element("tig");
					langSetSource.addContent(tigSource);
					Element termSource = new Element("term");
					tigSource.addContent(termSource);
					termSource.setText(splitter[0]);

					Element langSetTarget = new Element("langSet");
					langSetTarget.setAttribute("lang", targetLanguage, Namespace.XML_NAMESPACE);
					termEntry.addContent(langSetTarget);
					Element tigTarget = new Element("tig");
					langSetTarget.addContent(tigTarget);
					Element termTarget = new Element("term");
					tigTarget.addContent(termTarget);
					termTarget.setText(splitter[1]);

					body.addContent(termEntry);
				}
			}

			tbxdocument.saveToXmlFile();

			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	/**
	 * exportToTextFile exports the xliff file to a simple line feed separated
	 * text file
	 * 
	 * @param file
	 *            the file to export in the xliff document
	 * @param fileName
	 *            the file name to export to
	 * @param removeCRLF
	 *            true if CR/LF in segments should be removed
	 * @param removeTags
	 *            true if tags in segments should be removed
	 * @return true in case of success
	 */
	@SuppressWarnings({ "unused" })
	public boolean exportToTextFile(Element file, String fileName, boolean removeCRLF, boolean removeTags)
	{
		try
		{
			FileOutputStream sourceOut = new FileOutputStream(fileName);
			if (sourceOut == null)
				return false;
			Writer sourceOutWriter = new OutputStreamWriter(sourceOut, "UTF-8");
			if (sourceOutWriter == null)
			{
				sourceOut.close();
				return false;
			}

			@SuppressWarnings("rawtypes")
			Class[] classes = new Class[2];
			classes[0] = String.class;
			classes[1] = Object.class;
			Method determinePlaintext = null;
			try
			{
				determinePlaintext = MonoLingualObject.class.getMethod("simpleComputePlainText", classes);
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}

			if (file == null)
				return false;
			Element body = file.getChild("body", this.getNamespace());
			if (body == null)
				return false;

			List<Element> tus = this.getTransUnitList(body);
			for (int i = 0; i < tus.size(); i++)
			{
				String segment = tus.get(i).getChildText("source", this.getNamespace());
				segment = (String) determinePlaintext.invoke(MonoLingualObject.class, segment, null);
				if (removeCRLF)
				{
					segment = segment.replaceAll("\n", "");
					segment = segment.replaceAll("\r", "");
				}
				sourceOutWriter.write(segment + "\n");
			}

			sourceOutWriter.close();
			sourceOut.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			return false;
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
			return false;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
			return false;
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
			return false;
		}
		catch (InvocationTargetException e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * getAllTransUnitsList get all the trans units of a body including contained in groups
	 * 
	 * @param body
	 *            the body element
	 * @return the transunits
	 */
	@SuppressWarnings("unchecked")
	public List<Element> getAllTransUnitsList(Element body)
	{
		List<Element> results = (List<Element>) body.getChildren("trans-unit", this.getNamespace());
		results.addAll(getGroupTransUnitList(body));
		return results;
	}

	/**
	 * getAltTransId
	 * 
	 * @param altrans
	 * @return
	 */
	public String getAltTransId(Element altrans)
	{
		Element multipropgroup = this.getsubElementFromElementAttributeNameValueRegExp(altrans, "prop-group", "name",
				"^MULTI.*?");
		if (multipropgroup == null)
			return "currentMULIdNoId";
		String idvalue = multipropgroup.getAttributeValue("name");
		if (idvalue == null)
			return null;
		if (idvalue.equals(""))
			return null;
		idvalue = idvalue.replaceAll("^MULTI\\-(.*?)", "$1");
		return idvalue;
	}

	/**
	 * getAltTransList the alt-trans elements of a trans-unit
	 * 
	 * @param transUnit
	 *            the trans-unit element
	 * @return the alt-trans elements
	 */
	@SuppressWarnings("unchecked")
	public List<Element> getAltTransList(Element transUnit)
	{
		return transUnit.getChildren("alt-trans", this.getNamespace());
	}

	/**
	 * getAltTransMultiId get the creation-id of a an alt-trans match<br>
	 * Example:<br>
	 * 
	 * <pre>
	 * &lt;alt-trans>&lt;&lt;source...>...&lt;target...>...<br>
	 * &lt;prop-group name="MULTI-624649fa-7e65-42ee-928d-aa2b10b12f27">&lt;prop prop-value="creationid">1233c020bc55054_klemens&lt;/prop>&lt;prop prop-value="usagecount">0&lt;/prop>&lt;prop prop-value="entrynumber">21477&lt;/prop>&lt;prop prop-value="datatype">125447&lt;/prop>&lt;prop prop-value="changeid">1233c020bc55054_klemens&lt;/prop>&lt;/prop-group><br> 
	 * &lt;/alt-trans>
	 * getAltTransMultiId(alt-trans) will return "1233c020bc55054_klemens"
	 * </pre>
	 * 
	 * @param altrans
	 *            the alt-trans element to search in
	 * @return the creation-id of a an alt-trans match
	 */
	public String getAltTransMultiCreationId(Element altrans)
	{
		Element multipropgroup = this.getsubElementFromElementAttributeNameValueRegExp(altrans, "prop-group", "name",
				"^MULTI.*?");
		if (multipropgroup != null)
		{
			Element resultelem = this.getsubElementFromElementAttributeNameValue(multipropgroup, "prop", "prop-value",
					"creationid");
			if (resultelem != null)
				return resultelem.getText();
			else
				return null;

		}
		return null;
	}

	/**
	 * getBody get the header of the xliff file element
	 * 
	 * @param file
	 *            the file element
	 * @return the header element
	 */
	public Element getBody(Element file)
	{
		return file.getChild("body", this.getNamespace());
	}

	/**
	 * getFiles the file (s) elements of an xliff file
	 * 
	 * @return the file elements
	 */
	@SuppressWarnings("unchecked")
	public List<Element> getFiles()
	{
		root = getDocument().getRootElement();
		if (root == null)
			return null;
		files = root.getChildren("file", this.getNamespace());
		if ((files == null) || (files.size() == 0)) // very suspect....
		{
			List<Element> children = root.getChildren();
			files = new LinkedList<Element>();
			for (int i = 0; i < children.size(); i++)
			{
				if (children.get(i).getName().equals("file"))
				{
					files.add(children.get(i));
				}
			}
			if (files.size() == 0)
				files = null;
		}

		return files;
	}

	/**
	 * getFileSourceLanguage gets the source language of a file element
	 * 
	 * @param file
	 *            the file element
	 * @return the source language of the file
	 */
	public String getFileSourceLanguage(Element file)
	{
		return file.getAttributeValue("source-language");
	}

	/**
	 * getFileTargetLanguage gets the target language of a file element
	 * 
	 * @param file
	 *            the file element
	 * @return the target language of the file
	 */
	public String getFileTargetLanguage(Element file)
	{
		return file.getAttributeValue("target-language");
	}

	/**
	 * getGlossary get the glossary element text
	 * 
	 * @param file
	 *            the header which contains the glossary
	 * @return the glossary as string
	 */
	public String getGlossary(Element file)
	{
		if (file == null)
			return "";
		Element glossary = file.getChild("glossary", this.getNamespace());
		if (glossary != null)
		{
			Element internalfile = glossary.getChild("internal-file", this.getNamespace());
			if (internalfile != null)
			{
				return internalfile.getText();
			}
		}
		return "";
	}

	/**
	 * getTransUnitList get all the trans units of all the groups in the body
	 * 
	 * @param body
	 *            the body element
	 * @return the transunits
	 */
	public List<Element> getGroupTransUnitList(Element body)
	{
		List<Element> results = new LinkedList<Element>();
		@SuppressWarnings("unchecked")
		List<Element> groups = body.getChildren("group", this.getNamespace());
		for (int i = 0; i < groups.size(); i++)
		{
			@SuppressWarnings("unchecked")
			List<Element> transUnits = (List<Element>) groups.get(i).getChildren("trans-unit", this.getNamespace());
			results.addAll(transUnits);
		}
		return results;
	}

	/**
	 * getHeader get the header of the xliff file element
	 * 
	 * @param file
	 *            the file element
	 * @return the header element
	 */
	public Element getHeader(Element file)
	{
		return file.getChild("header", this.getNamespace());
	}

	/**
	 * getLanguage gets the language of an element
	 * 
	 * @param element
	 *            the element with a language code
	 * @return the language code
	 */
	public String getLanguage(Element element)
	{
		return element.getAttributeValue("lang", Namespace.XML_NAMESPACE);
	}

	/**
	 * getMatchQualityAsFloat gets the match quality of an alt-trans element
	 * 
	 * @param alttrans
	 *            the alt-trans element
	 * @return the match quality, -1 in case of error or match-quality not a
	 *         numbe
	 */
	public float getMatchQualityAsFloat(Element alttrans)
	{
		String qual = "";
		try
		{
			qual = alttrans.getAttributeValue("match-quality");
			return Float.parseFloat(qual);
		}
		catch (Exception ex)
		{
			return -1;
		}
	}

	/**
	 * getMatchQualityAsInt gets the match quality of an alt-trans element
	 * 
	 * @param alttrans
	 *            the alt-trans element
	 * @return the match quality, -1 in case of error or match-quality not a
	 *         numbe
	 */
	public int getMatchQualityAsInt(Element alttrans)
	{
		String qual = "";
		try
		{
			qual = alttrans.getAttributeValue("match-quality");
			return Integer.parseInt(qual);
		}
		catch (Exception ex)
		{
			// try float
			try
			{
				return (int) (Float.parseFloat(qual));
			}
			catch (Exception e)
			{
				return -1;
			}
		}
	}

	/**
	 * getPhaseGroup get the phase group of the header
	 * 
	 * @param header
	 * @return the phase-group element
	 */
	public Element getPhaseGroup(Element header)
	{
		return header.getChild("phase-group", this.getNamespace());
	}

	/**
	 * getPhases get all the phases for a file element resp. its header
	 * 
	 * @param phaseGroup
	 *            the phase group
	 * @return the phases
	 */
	@SuppressWarnings("unchecked")
	public List<Element> getPhases(Element phaseGroup)
	{
		return (List<Element>) phaseGroup.getChildren("phase", this.getNamespace());
	}

	/**
	 * @return the root
	 */
	public Element getRoot()
	{
		return root;
	}

	/**
	 * getSklFile get either the external or internal skl file
	 * 
	 * @param header
	 *            the header element
	 * @return the skl file element or null if not exists
	 */
	public Element getSklFile(Element header)
	{
		Element skl = header.getChild("skl", this.getNamespace());
		if (skl == null)
			return null;

		Element internalfile = skl.getChild("internal-file", this.getNamespace());
		if (internalfile != null)
			return internalfile;

		Element externalfile = skl.getChild("external-file", this.getNamespace());
		if (externalfile != null)
			return externalfile;

		return null;
	}

	/**
	 * @return the sourceLanguage
	 */
	public String getSourceLanguage()
	{
		return sourceLanguage;
	}

	/**
	 * getSourceLanguage get source language from file element
	 * 
	 * @param file
	 *            the file element
	 * @return the source language
	 */
	public String getSourceLanguage(Element file)
	{
		this.sourceLanguage = file.getAttributeValue("source-language");
		return file.getAttributeValue("source-language");
	}

	/**
	 * @return the targetLanguage
	 */
	public String getTargetLanguage()
	{
		return targetLanguage;
	}

	/**
	 * getTargetLanguage get target language from file element
	 * 
	 * @param file
	 *            the file element
	 * @return the target language
	 */
	public String getTargetLanguage(Element file)
	{
		this.targetLanguage = file.getAttributeValue("target-language");
		return file.getAttributeValue("target-language");
	}

	/**
	 * getTransUnitList get all the trans units of a body
	 * 
	 * @param body
	 *            the body element
	 * @return the transunits
	 */
	@SuppressWarnings("unchecked")
	public List<Element> getTransUnitList(Element body)
	{
		return (List<Element>) body.getChildren("trans-unit", this.getNamespace());
	}

	/**
	 * getTransUnitList3 get all the trans units of a body - no namespace used
	 * 
	 * @param body
	 *            the body element
	 * @return the transunits
	 */
	@SuppressWarnings("unchecked")
	public List<Element> getTransUnitListNoNS(Element body)
	{
		return (List<Element>) body.getChildren("trans-unit");
	}

	/**
	 * getTransUnitPhraseEntries return the phrase entries of the entry Example:
	 * 
	 * <pre>
	 * &lt;prop-group name="subSegmentTranslate:C:\Program Files\OpenTMS\test\editor\sample.tbx">
	 *   &lt;prop xml:lang="de" prop-type="source">Vorwort&lt;/prop>
	 *   &lt;prop xml:lang="en" prop-type="target">Preface&lt;/prop>
	 * &lt;/prop-group>&lt;/pre>
	 * @param transUnit the trans-unit with the phrase entries
	 * @return a Vector of elements with the phrases formatted as above
	 */
	@SuppressWarnings("unchecked")
	public Vector<PhraseTranslateResult> getTransUnitPhraseEntries(Element transUnit)
	{
		List<Element> propGroups = (List<Element>) transUnit.getChildren("prop-group", this.getNamespace());
		Vector<PhraseTranslateResult> phraseGroups = new Vector<PhraseTranslateResult>();
		for (int i = 0; i < propGroups.size(); i++)
		{
			Element propgroup = propGroups.get(i);
			List<Attribute> attributes = propgroup.getAttributes();
			for (int j1 = 0; j1 < attributes.size(); j1++)
			{
				Attribute att = attributes.get(j1);
				String name = att.getName();
				String value = att.getValue();
				if (name.equals("name") && value.startsWith("subSegmentTranslate"))
				{
					String source = "";
					String target = "";
					List<Element> props = propgroup.getChildren("prop", this.getNamespace());
					for (int j = 0; j < props.size(); j++)
					{
						Element prop = props.get(j);
						String type = prop.getAttributeValue("prop-type"); // ,
						// this.getNamespace());
						if (type.equals("source"))
							source = prop.getText();
						else if (type.equals("target"))
							target = prop.getText();
					}
					PhraseTranslateResult phraseTranslateResult = new PhraseTranslateResult();
					phraseTranslateResult.setSourcePhrase(source);
					phraseTranslateResult.setTargetPhrase(target);
					phraseGroups.add(phraseTranslateResult);
				}
			}
		}
		return phraseGroups;
	}

	/**
	 * getTransUnitSegSource returns the seg-source element of a trans-unit
	 * 
	 * @param transUnit
	 *            the trans-unit element
	 * @return the seg-source element
	 */

	public Element getTransUnitSegSource(Element transUnit)
	{
		return transUnit.getChild("seg-source", this.getNamespace());
	}

	/**
	 * getTransUnitSource returns the source element of a trans-unit
	 * 
	 * @param transUnit
	 *            the trans-unit element
	 * @return the source element
	 */
	public Element getTransUnitSource(Element transUnit)
	{
		return transUnit.getChild("source", this.getNamespace());
	}

	/**
	 * getTransUnitTarget returns the target element of a trans-unit
	 * 
	 * @param transUnit
	 *            the trans-unit element
	 * @return the target element
	 */
	public Element getTransUnitTarget(Element transUnit)
	{
		return transUnit.getChild("target", this.getNamespace());
	}

	/**
	 * getXliffBody gets the body of a file element
	 * 
	 * @param file
	 *            the file element
	 * @return the body element
	 */
	public Element getXliffBody(Element file)
	{
		return file.getChild("body", this.getNamespace());
	}

	/**
	 * getXliffHeader gets the header of a file element
	 * 
	 * @param file
	 *            the file element
	 * @return the header
	 */
	public Element getXliffHeader(Element file)
	{
		return file.getChild("header", this.getNamespace());
	}

	/**
	 * getXliffVersion get the TMX version of the tmx docuemnt
	 * 
	 * @return the XLIFF version of the document
	 */
	public String getXliffVersion()
	{
		try
		{
			return getDocument().getRootElement().getAttributeValue("version");
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * isExternalSkl determine if external or internal skl file
	 * 
	 * @param skl
	 *            the skl File element from getSklFile(Element header)
	 * @return true if external, false if internal
	 */
	public boolean isExternalSkl(Element skl)
	{
		if (skl.getAttribute("href") != null)
			return true;
		return false;
	}

	/**
	 * linguisticPropertiesToProp converts Linguistic Properties to a prop-group
	 * 
	 * @param name
	 *            the name for the new prop-group
	 * @param properties
	 *            the linguistic properties
	 * @return the name prop-group
	 */
	public Element linguisticPropertiesToProp(String name, LinguisticProperties properties)
	{
		try
		{
			if (properties == null)
				return null;
			Element propGroup = new Element("prop-group", this.getNamespace());
			propGroup.setAttribute("name", name); // , this.getNamespace());
			// Enumeration<Object> enummulti = properties.elements();
			Set<String> enumprop = properties.keySet();
			Iterator<String> it = enumprop.iterator();
			while (it.hasNext())
			{
				// String key = (String) properties.get(it.next());
				LinguisticProperty lingprop = (LinguisticProperty) properties.get(it.next()); // enummulti.nextElement();
				Element prop = new Element("prop");
				prop.setAttribute("prop-value", (String) lingprop.getKey());
				Object tmxprop = (Object) lingprop.getValue();
				if (tmxprop.getClass().getName().equals("java.lang.String"))
				{
					prop.setText((String) tmxprop);
					propGroup.addContent(prop);
				}
				else if (tmxprop.getClass().getName().equals("de.folt.models.documentmodel.tmx.TmxProp"))
				{
					String ty = ((TmxProp) tmxprop).getType();
					if (ty == null)
						ty = "unknowntype";
					String co = ((TmxProp) tmxprop).getContent();
					if (co != null)
					{
						prop.setText(((TmxProp) tmxprop).getContent());
						prop.setAttribute("prop-value", ty);
						propGroup.addContent(prop);
					}
				}
			}
			return propGroup;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.folt.models.documentmodel.document.XmlDocument#loadXmlFile(java.io
	 * .File)
	 */
	@Override
	public Document loadXmlFile(File newFile)
	{
		document = null;
		try
		{
			CorrectTradosIllegalXMLCharacter corrector = new CorrectTradosIllegalXMLCharacter();
			corrector.encodeIncorrectSDLTradosXLIFFFile(newFile.getAbsolutePath());
			newFile = new File(newFile.getAbsolutePath());
			super.loadXmlFile(newFile);
			this.getSourceLanguage();
			this.getTargetLanguage();
			corrector.decodeIncorrectSDLTradosXLIFFFile(newFile.getAbsolutePath());
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return document;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.folt.models.documentmodel.document.XmlDocument#loadXmlFile(java.lang
	 * .String)
	 */
	@Override
	public Document loadXmlFile(String filename)
	{
		document = null;
		try
		{
			this.setBExpandExternalEntities(false);
			super.loadXmlFile(filename);
			this.getSourceLanguage();
			this.getTargetLanguage();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return document;
	}

	/**
	 * removeAllAltTransElements remove all the alt-trans from the all
	 * trans-units
	 * 
	 * @param body
	 *            the body element of the file element
	 */
	public void removeAllAltTransElements(Element body)
	{
		List<Element> transUnits = getTransUnitList(body);
		for (int i = 0; i < transUnits.size(); i++)
		{
			transUnits.get(i).removeChildren("alt-trans", this.getNamespace());
		}
	}

	/**
	 * removeAllTargetAndAltTransElements remove all the alt-trans and targets
	 * from the all trans-units
	 * 
	 * @param body
	 *            the body element of the file element
	 */
	public void removeAllTargetAndAltTransElements(Element body)
	{
		List<Element> transUnits = getTransUnitList(body);
		for (int i = 0; i < transUnits.size(); i++)
		{
			transUnits.get(i).removeChildren("target", this.getNamespace());
			transUnits.get(i).removeChildren("alt-trans", this.getNamespace());
		}
	}

	/**
	 * removeAllTargetElements remove all the targets from the all trans-units
	 * 
	 * @param body
	 *            the body element of the file element
	 */
	public void removeAllTargetElements(Element body)
	{
		List<Element> transUnits = getTransUnitList(body);
		for (int i = 0; i < transUnits.size(); i++)
		{
			transUnits.get(i).removeChildren("target", this.getNamespace());
		}
	}

	/**
	 * removeTranslationBasedOnOrigin removes an alt-trans element based on the
	 * origin attribute
	 * 
	 * @param transUnit
	 *            the transunit to check
	 * @param originValeu
	 *            the value for the origin attribute
	 * @return true if removed, otherwise false
	 */
	@SuppressWarnings("unchecked")
	public boolean removeTranslationBasedOnOrigin(Element transUnit, String originValue)
	{
		if (originValue == null)
			return false;
		List<Element> altTrans = (List<Element>) transUnit.getChildren("alt-trans", this.getNamespace());
		Vector<Element> remove = new Vector<Element>();
		for (int i = 0; i < altTrans.size(); i++)
		{
			Element alt = altTrans.get(i);
			if (alt != null)
			{
				String att = alt.getAttributeValue("origin");
				if ((att != null) && att.equals(originValue))
				{
					remove.add(alt);
				}
			}
		}
		for (int i = 0; i < remove.size(); i++)
		{
			transUnit.removeContent(remove.get(i));
		}
		return true;
	}

	/**
	 * @param root
	 *            the root to set
	 */
	public void setRoot(Element root)
	{
		this.root = root;
	}

	/**
	 * setSourceLanguage set the source language of a file element
	 * 
	 * @param file
	 *            the file element
	 * @param sourceLanguage
	 *            the source language
	 */
	public void setSourceLanguage(Element file, String sourceLanguage)
	{
		file.setAttribute("source-language", sourceLanguage); // ,
		// this.getNamespace());
		return;
	}

	/**
	 * @param sourceLanguage
	 *            the sourceLanguage to set
	 */
	public void setSourceLanguage(String sourceLanguage)
	{
		this.sourceLanguage = sourceLanguage;
	}

	/**
	 * setSourceLanguage set the target language of a file element
	 * 
	 * @param file
	 *            the file element
	 * @param targetLanguage
	 *            the target language
	 */
	public void setTargetLanguage(Element file, String targetLanguage)
	{
		file.setAttribute("target-language", targetLanguage); // ,
		// this.getNamespace());
		return;
	}

	/**
	 * @param targetLanguage
	 *            the targetLanguage to set
	 */
	public void setTargetLanguage(String targetLanguage)
	{
		this.targetLanguage = targetLanguage;
	}

	/**
	 * subSegmentTranslate translates an xliff document based on the source and
	 * target language provided on a sub segment bases. The method also informs
	 * the observers about the translation progress. A vector is supplied where
	 * the first element contains the number of trans-units overall to translate
	 * and the second element contains the actual translated trans-unit number.
	 * (0..trans-unit size - 1). If the trans-unit contains the attribute
	 * translate="no" the trans-unit no search is applied.
	 * 
	 * @param dataSource
	 *            the data source to use
	 * @param transunits
	 *            the trans-units to translate
	 * @param sourceLanguage
	 *            the source language to use
	 * @param targetLanguage
	 *            the target language to use
	 * @param translationParameters
	 *            a hash table with additional translation parameters; usage is
	 *            up to the implementation
	 * @throws OpenTMSXException
	 */
	public void subSegmentTranslate(DataSource dataSource, List<Element> transunits, String sourceLanguage,
			String targetLanguage, Hashtable<String, Object> translationParameters) throws OpenTMSException
	{
		int transSize = transunits.size();
		Vector<Integer> posVec = new Vector<Integer>();
		posVec.add((Integer) transSize);
		posVec.add((Integer) transSize);
		for (int j = 0; j < transSize; j++)
		{
			Element transUnit = transunits.get(j);
			// do not translate when translate="no"
			String translateAttribute = transUnit.getAttributeValue("translate"); // ,
			// this.getNamespace());
			if ((translateAttribute != null) && translateAttribute.equals("no"))
				continue;
			// call the translate method of the data source
			transUnit = dataSource.subSegmentTranslate(transUnit, this, sourceLanguage, targetLanguage,
					translationParameters);

			// inform the Observer about change
			posVec.set(1, (Integer) j);
			this.setChanged();
			this.notifyObservers(posVec);
		}
	}

	/**
	 * translate translates an xliff document based on the source and target
	 * language provided
	 * 
	 * @param dataSource
	 *            the data source to use
	 * @param sourceLanguage
	 *            the source language to use
	 * @param targetLanguage
	 *            the target language to use
	 * @param matchSimilarity
	 *            the similarity (fuzzy) match quality (0 - 100) to use
	 * @param iMaxThreads
	 *            the maximum number of threads to use / -1 = use as many
	 *            threads as processors available; 0 = do not create a thread
	 *            (run in main process); 1 run in main process; > 1 run dividing
	 *            trans-units to translate in several threads; maximum is the
	 *            number of processors; if number of trans-units less that
	 *            processors use processor = 1
	 * @param translationParameters
	 *            the translation parameters to use; some paramaters supported
	 *            (written to the phasegroup):<br>
	 *            tool = (String) translationParameters.get("tool"); (for this
	 *            the version is tried to determined by using
	 *            de.folt.constants.OpenTMSVersionConstants
	 *            .getVersionString(tool)<br>
	 *            jobId = (String) translationParameters.get("jobId");<br>
	 *            contactName = (String)
	 *            translationParameters.get("contactName");<br>
	 *            contactEmail = (String)
	 *            translationParameters.get("contactEmail");<br>
	 *            contactPhone = (String)
	 *            translationParameters.get("contactPhone");
	 * @throws OpenTMSXException
	 */
	public void subSegmentTranslate(DataSource dataSource, String sourceLanguage, String targetLanguage,
			Hashtable<String, Object> translationParameters) throws OpenTMSException
	{
		List<Element> files = this.getFiles();
		System.out.println("# XLIFF Files: " + files.size());

		String tool = null;
		if (translationParameters != null)
			tool = (String) translationParameters.get("tool");
		String version = OpenTMSServer.getVersion();
		if ((tool == null) || (tool.equals("")))
		{
			// tool = "OpenTMS Translate Tool";
			tool = dataSource.getDataSourceType();
			version = de.folt.constants.OpenTMSVersionConstants.getVersionString(tool);
		}
		else
		{
			// try to get the version info
			version = de.folt.constants.OpenTMSVersionConstants.getVersionString(tool);
		}
		String toolinfo = tool + " " + version;

		String company = "OpenTMS";
		String processName = "OpenTMS SubSegment Translate";

		String jobId = null;
		String contactName = null;
		String contactEmail = null;
		String contactPhone = null;

		if (translationParameters != null)
		{
			jobId = (String) translationParameters.get("jobId");
			contactName = (String) translationParameters.get("contactName");
			contactEmail = (String) translationParameters.get("contactEmail");
			contactPhone = (String) translationParameters.get("contactPhone");
		}

		for (int i = 0; i < files.size(); i++)
		{
			Element file = files.get(i);

			if (sourceLanguage == null)
				sourceLanguage = this.getSourceLanguage(file);

			if (targetLanguage == null)
				targetLanguage = this.getTargetLanguage(files.get(i));

			if ((this.getSourceLanguage(file) == null) || this.getSourceLanguage(file).equals(""))
				this.setSourceLanguage(file, sourceLanguage);

			if ((this.getTargetLanguage(file) == null) || this.getTargetLanguage(file).equals(""))
				this.setTargetLanguage(file, targetLanguage);

			Element header = getXliffHeader(file);
			if (header != null)
			{
				Element phaseGroup = getPhaseGroup(header);
				if (phaseGroup != null)
					addPhase(phaseGroup, company, processName, toolinfo, jobId, contactName, contactEmail, contactPhone);
			}

			List<Element> transunits = getTransUnitList(getXliffBody(file));

			subSegmentTranslate(dataSource, transunits, sourceLanguage, targetLanguage, translationParameters);
			Element[] glossaries = dataSource.subSegmentResultsToGlossary(sourceLanguage, targetLanguage);
			if ((glossaries != null) && (glossaries.length > 0))
			{
				for (int k = 0; k < glossaries.length; k++)
				{
					header.addContent(glossaries[k]);
				}
			}
		}

	}

	/**
	 * subSegmentTranslate translates an xliff document based on the source and
	 * target language provided; the translation parameters are set to null.
	 * 
	 * @param dataSourceName
	 *            the name of data source to use
	 * @param sourceLanguage
	 *            the source language to use
	 * @param targetLanguage
	 *            the target language to use
	 * @throws OpenTMSXException
	 */

	public void subSegmentTranslate(String dataSourceName, String sourceLanguage, String targetLanguage)
			throws OpenTMSException
	{
		try
		{
			DataSource dataSource = DataSourceInstance.getInstance(dataSourceName);
			this.subSegmentTranslate(dataSource, sourceLanguage, targetLanguage, null);
		}
		catch (Exception ex)
		{
			return;
		}
	}

	/**
	 * translate translates an xliff document based on the source and target
	 * language provided. The method also informs the observers about the
	 * translation progress. A vector is supplied where the first element
	 * contains the number of trans-units overall to translate and the second
	 * element contains the actual translated trans-unit number. (0..trans-unit
	 * size - 1). If the trans-unit contains the attribute translate="no" the
	 * trans-unit no search is applied. Per default a 100% match is copied to
	 * the target (if it is empty). This can be avoided by specifying in the
	 * hash table translationParameters "Copy100ToTarget" >> "no". Copy is only
	 * done when there is only one 100% match and only one target in this match.
	 * 
	 * @param dataSource
	 *            the data source to use
	 * @param file
	 * @param transunits
	 *            the trans-units to translate
	 * @param sourceLanguage
	 *            the source language to use
	 * @param targetLanguage
	 *            the target language to use
	 * @param matchSimilarity
	 *            the similarity (fuzzy) match quality (0 - 100) to use
	 * @param translationParameters
	 *            a hashtable with additional translation paramters; usage is up
	 *            to the implementation
	 * @throws OpenTMSXException
	 */
	@SuppressWarnings("unchecked")
	public void translate(DataSource dataSource, Element file, List<Element> transunits, String sourceLanguage,
			String targetLanguage, int matchSimilarity, Hashtable<String, Object> translationParameters)
			throws OpenTMSException
	{
		int transSize = transunits.size();
		Vector<Integer> posVec = new Vector<Integer>();
		posVec.add((Integer) transSize);
		posVec.add((Integer) transSize);
		for (int j = 0; j < transSize; j++)
		{
			Element transUnit = transunits.get(j);
			// do not translate when translate="no"
			String translateAttribute = transUnit.getAttributeValue("translate");
			if ((translateAttribute != null) && translateAttribute.equals("no"))
				continue;
			// call the translate method of the data source
			dataSource.translate(transUnit, file, this, sourceLanguage, targetLanguage, matchSimilarity,
					translationParameters);
			boolean bCopy100MatchToTarget = true;
			if (translationParameters != null)
			{
				String copy100 = (String) translationParameters.get("copy100ToTarget");
				if ((copy100 != null) && copy100.equals("no"))
					bCopy100MatchToTarget = false;
			}

			if (bCopy100MatchToTarget)
			{
				Element target = transUnit.getChild("target", this.getNamespace());
				if ((target == null) || target.getText().equals(""))
				{
					// get the 100% matches
					List<Element> alttranslist = (List<Element>) transUnit
							.getChildren("alt-trans", this.getNamespace());
					int iCount100 = 0;
					for (int i = 0; i < alttranslist.size(); i++)
					{
						Element alttrans = alttranslist.get(i);
						String matchquality = alttrans.getAttributeValue("match-quality"); // ,
						// this.getNamespace());
						if ((matchquality != null) && matchquality.equals("100"))
						{
							List<Element> targets = (List<Element>) alttrans.getChildren("target", this.getNamespace());
							if (targets.size() > 0)
								iCount100 = iCount100 + targets.size();
						}

						if (iCount100 > 1)
							break;
					}
					if (iCount100 == 1)
					{
						for (int i = 0; i < alttranslist.size(); i++)
						{
							Element alttrans = alttranslist.get(i);
							if (alttrans != null)
							{
								String matchquality = alttrans.getAttributeValue("match-quality"); // ,
								// this.getNamespace());
								if ((matchquality != null) && matchquality.equals("100"))
								{
									if (target != null)
										transUnit.removeChildren("target");
									Element alttarget = alttrans.getChild("target", this.getNamespace());
									if (alttarget != null)
									{
										String altTransTarget = elementContentToString(alttarget);
										target = this.buildElement("<target>" + altTransTarget + "</target>");
										transUnit.addContent(target);
										break;
									}
								}
							}
						}
					}
				}
			}

			// inform the Observer about change
			posVec.set(1, (Integer) j);
			this.setChanged();
			this.notifyObservers(posVec);
		}
	}

	/**
	 * translate translates an xliff document based on the source and target
	 * language provided
	 * 
	 * @param dataSource
	 *            the data source to use
	 * @param sourceLanguage
	 *            the source language to use
	 * @param targetLanguage
	 *            the target language to use
	 * @param matchSimilarity
	 *            the similarity (fuzzy) match quality (0 - 100) to use
	 * @param iMaxThreads
	 *            the maximum number of threads to use / -1 = use as many
	 *            threads as processors available; 0 = do not create a thread
	 *            (run in main process); 1 run in main process; > 1 run dividing
	 *            trans-units to translate in several threads; maximum is the
	 *            number of processors; if number of trans-units less that
	 *            processors use processor = 1
	 * @param translationParameters
	 *            the translation parameters to use; some paramaters supported
	 *            (written to the phasegroup):<br>
	 *            tool = (String) translationParameters.get("tool"); (for this
	 *            the version is tried to determined by using
	 *            de.folt.constants.OpenTMSVersionConstants
	 *            .getVersionString(tool)<br>
	 *            jobId = (String) translationParameters.get("jobId");<br>
	 *            contactName = (String)
	 *            translationParameters.get("contactName");<br>
	 *            contactEmail = (String)
	 *            translationParameters.get("contactEmail");<br>
	 *            contactPhone = (String)
	 *            translationParameters.get("contactPhone");
	 * @throws OpenTMSXException
	 */
	public void translate(DataSource dataSource, String sourceLanguage, String targetLanguage, int matchSimilarity,
			int iMaxThreads, Hashtable<String, Object> translationParameters) throws OpenTMSException
	{
		List<Element> files = this.getFiles();
		System.out.println("# XLIFF Files: " + files.size());
		String tool = null;
		if (translationParameters != null)
			tool = (String) translationParameters.get("tool");
		String version = OpenTMSServer.getVersion();
		if ((tool == null) || (tool.equals("")))
		{
			// tool = "OpenTMS Translate Tool";
			tool = dataSource.getDataSourceType();
			version = de.folt.constants.OpenTMSVersionConstants.getVersionString(tool);
		}
		else
		{
			// try to get the version info
			version = de.folt.constants.OpenTMSVersionConstants.getVersionString(tool);
		}
		String toolinfo = tool + " " + version;

		Timer translatetimer = new Timer();
		translatetimer.startTimer();

		String company = "OpenTMS";
		String processName = "OpenTMS Translate";

		String jobId = null;
		String contactName = null;
		String contactEmail = null;
		String contactPhone = null;

		if (translationParameters != null)
		{
			jobId = (String) translationParameters.get("jobId");
			contactName = (String) translationParameters.get("contactName");
			contactEmail = (String) translationParameters.get("contactEmail");
			contactPhone = (String) translationParameters.get("contactPhone");
		}

		for (int i = 0; i < files.size(); i++)
		{
			Element file = files.get(i);

			if (sourceLanguage == null)
				sourceLanguage = this.getSourceLanguage(file);

			if (targetLanguage == null)
				targetLanguage = this.getTargetLanguage(files.get(i));

			if ((this.getSourceLanguage(file) == null) || this.getSourceLanguage(file).equals(""))
				this.setSourceLanguage(file, sourceLanguage);

			if ((this.getTargetLanguage(file) == null) || this.getTargetLanguage(file).equals(""))
				this.setTargetLanguage(file, targetLanguage);

			Element header = getXliffHeader(file);
			if (header != null)
			{
				Element phaseGroup = getPhaseGroup(header);
				if (phaseGroup != null)
					addPhase(phaseGroup, company, processName, toolinfo, jobId, contactName, contactEmail, contactPhone);
			}

			this.saveToXmlFile();

			List<Element> transunits = getTransUnitList(getXliffBody(file));
			int iProcessors = Runtime.getRuntime().availableProcessors();

			if (iMaxThreads == 0) // 0 indicates use just one thread
				iProcessors = 0;
			if (iMaxThreads != -1)
			{
				if (iMaxThreads < iProcessors) // only as max threads as
					// processors
					iProcessors = iMaxThreads;
			}

			if (!dataSource.bSupportMultiThreading())
			{
				int iRealProcessors = iProcessors;
				iProcessors = 1;
				System.out.println("# " + dataSource.getDataSourceType() + " use " + iProcessors
						+ " processors (really " + iRealProcessors + ")");
			}
			else
			{
				System.out.println("# " + dataSource.getDataSourceType() + " use " + iProcessors
						+ " processors (really " + iProcessors + ")");
			}

			// we only use multi threading if we have
			// either more than one processor
			// and if the number of trans-units is > than the number of
			// available processors
			if ((iProcessors == 1) || (transunits.size() <= iProcessors))
			{
				translate(dataSource, file, transunits, sourceLanguage, targetLanguage, matchSimilarity,
						translationParameters);
			}
			else
			{
				// we must now make a call to several trans-units at once
				int transSize = transunits.size();
				int iTransSizePerProcessor = transSize / iProcessors;
				Thread threads[] = new Thread[iProcessors];
				int iLowerBound = 0;
				int iUpperBound = 0;
				for (int ip = 0; ip < iProcessors; ip++)
				{
					iUpperBound = iLowerBound + iTransSizePerProcessor;
					if (ip == (iProcessors - 1))
						iUpperBound = transSize;
					XliffTranslateThread xliffThread = new XliffTranslateThread(dataSource, file, transunits,
							iLowerBound, iUpperBound, this, sourceLanguage, targetLanguage, matchSimilarity,
							translationParameters);
					threads[ip] = new Thread(xliffThread);
					iLowerBound = iUpperBound;
				}

				for (int ip = 0; ip < iProcessors; ip++)
				{
					threads[ip].start();
				}

				for (int ip = 0; ip < iProcessors; ip++)
				{
					try
					{
						threads[ip].join();
					}
					catch (Exception ex)
					{
						ex.printStackTrace();
					}
				}

				threads = null;
			}
		}
		translatetimer.endTimer();
		float translatetimertime = (float) ((float) translatetimer.timeNeeded() / 1000.0);
		System.out.println("# Translation Time needed for XLIFF Files: " + translatetimertime);
	}

	/**
	 * translate translates an xliff document based on the source and target
	 * language provided;; the translation parameters are set to null.
	 * 
	 * @param dataSourceName
	 *            the name of data source to use
	 * @param sourceLanguage
	 *            the source language to use
	 * @param targetLanguage
	 *            the target language to use
	 * @param matchSimilarity
	 *            the similarity (fuzzy) match quality (0 - 100) to use
	 * @param iMaxThreads
	 *            the maximum nuber of threads (processors) to use for
	 *            translation
	 * @throws OpenTMSXException
	 */

	public void translate(String dataSourceName, String sourceLanguage, String targetLanguage, int matchSimilarity,
			int iMaxThreads) throws OpenTMSException
	{
		try
		{
			Timer timer = new Timer();
			timer.startTimer();
			System.out.println("Load " + dataSourceName);
			DataSource dataSource = DataSourceInstance.getInstance(dataSourceName);
			if (dataSource == null)
			{
				System.out.println("Data source " + dataSourceName + " not found!");
				return;
			}
			System.out.println("Start translate with " + dataSourceName);
			this.translate(dataSource, sourceLanguage, targetLanguage, matchSimilarity, iMaxThreads, null);
			timer.endTimer();
			System.out.println("End translate with " + dataSourceName + " timer needed = " + timer.timeNeeded());
		}
		catch (Exception ex)
		{
			return;
		}
	}

	/**
	 * transUnitToMultiLingualObject creates a MUL from source and target of a
	 * trans-unit if approved
	 * 
	 * @param element
	 *            the transunit
	 * @return a MUL object or null if not approved or source or target do not
	 *         exist
	 */
	public MultiLingualObject transUnitToMultiLingualObject(Element element)
	{
		return transUnitToMultiLingualObject(element, false);
	}

	/**
	 * transUnitToMultiLingualObject creates a MUL from source and target of a
	 * trans-unit if approved
	 * 
	 * @param element
	 *            the transunit
	 * @param bLoadAllTargets
	 *            if true loads all target translation irrespectively if
	 *            approved or not
	 * @return a MUL object or null if not approved or source or target do not
	 *         exist
	 */
	public MultiLingualObject transUnitToMultiLingualObject(Element element, boolean bLoadAllTargets)
	{
		String approved = element.getAttributeValue("approved");
		if ((approved == null) && (bLoadAllTargets == false))
			return null;
		if ((approved != null) && approved.equals("no") && (bLoadAllTargets == false))
			return null;

		Element source = element.getChild("source", this.getNamespace());
		if (source == null)
			return null;
		Element target = element.getChild("target", this.getNamespace());
		if (target == null)
			return null;

		LinguisticProperties linguisticProperties = new LinguisticProperties();
		MultiLingualObject multi = new MultiLingualObject(linguisticProperties, LinguisticTypes.XLIFF);

		LinguisticProperty transunit = new LinguisticProperty("trans-unit", element);
		multi.addLinguisticProperty(transunit);

		LinguisticProperty sourceElement = new LinguisticProperty("trans-unit-source", source);
		multi.addLinguisticProperty(sourceElement);

		LinguisticProperty targetElement = new LinguisticProperty("trans-unit-target", target);
		multi.addLinguisticProperty(targetElement);

		LinguisticProperties linguisticPropertiesMono = new LinguisticProperties();
		String segment = this.elementContentToString(source);
		String plainTextSegment = MonoLingualObject.simpleComputePlainText(segment);
		String sourceLanguage = getLanguage(source);
		if ((sourceLanguage == null) || sourceLanguage.equals(""))
		{
			sourceLanguage = this.getSourceLanguage();
		}
		MonoLingualObject sourcemono = new MonoLingualObject(linguisticPropertiesMono, LinguisticTypes.XLIFF, segment,
				plainTextSegment, sourceLanguage);
		multi.addMonoLingualObject(sourcemono);
		segment = this.elementContentToString(target);
		String targetLanguage = getLanguage(target);
		if ((targetLanguage == null) || targetLanguage.equals(""))
		{
			targetLanguage = this.targetLanguage;
		}
		else if (this.targetLanguage == null)
		{
			this.targetLanguage = targetLanguage;
		}
		plainTextSegment = MonoLingualObject.simpleComputePlainText(segment);
		MonoLingualObject targetmono = new MonoLingualObject(linguisticPropertiesMono, LinguisticTypes.XLIFF, segment,
				plainTextSegment, targetLanguage);
		multi.addMonoLingualObject(targetmono);

		return multi;
	}

}
