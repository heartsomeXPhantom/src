/*
 * Created on 26.01.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.models.documentmodel.tbx;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;

import de.folt.models.datamodel.GeneralLinguisticObject.LinguisticTypes;
import de.folt.models.datamodel.LinguisticProperties;
import de.folt.models.datamodel.LinguisticProperty;
import de.folt.models.datamodel.MonoLingualObject;
import de.folt.models.datamodel.MultiLingualObject;
import de.folt.models.documentmodel.document.XmlDocument;
import de.folt.util.OpenTMSSupportFunctions;

/**
 * This class implements a tbx based data source
 * 
 * @author klemens
 * 
 */
public class TbxDocument extends XmlDocument
{

	/**
     * 
     */
	private static final long serialVersionUID = -7023631168152621412L;

	private String version = "0.2";

	/**
	 * main
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		if (args.length == 0)
		{
			System.out
					.println("Usage: java -jar <jarfiles> de.folt.models.documentmodel.tbx.TbxDocument <infile tbx> [outfile tbx]\nVersion: "
							+ new TbxDocument().getVersion()
							+ " Compile Date: "
							+ OpenTMSSupportFunctions.getCompileDate(TbxDocument.class));
			return;
		}
		try
		{
			String tbxfile = args[0];
			String outtbxfile = tbxfile + ".id.tbx";
			if (args.length > 1)
				outtbxfile = args[1];
			System.out.println("TBX ID Writter (Version " + new TbxDocument().getVersion() + ") " + " Compile Date: "
					+ OpenTMSSupportFunctions.getCompileDate(TbxDocument.class) + "\nAdapting TBX file: " + tbxfile
					+ " >> Outputfile: " + outtbxfile);
			TbxDocument tbxDocument = new TbxDocument();
			@SuppressWarnings("unused")
			String outcome = tbxDocument.bGenerateIds(tbxfile, outtbxfile);
			if (tbxDocument.getErrorCode() == 0)
				System.out.println("Succes with error code: " + tbxDocument.getErrorCode() + " Outputfile: " + outtbxfile);
			else
				System.out.println("Failure with error code: " + tbxDocument.getErrorCode());
			System.exit(tbxDocument.getErrorCode());
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			System.exit(1);
		}
	}

	private int errorCode = 0;

	private Element tbxBody = null;

	private Element tbxHeader = null;

	public boolean bGenerateIds()
	{
		boolean bResult = true;

		long termEntryCounter = 0;
		long tigCounter = 0;
		long termCounter = 0;

		long totelTermEntryCounter = 0;
		long totalTigCounter = 0;
		long totalTermCounter = 0;

		String termEntryID = ""; // <termEntry id="termEntry_382">
		String tigId = ""; // <tig id="tig_382_1">
		String termId = ""; // <term id="term_382_1_de-de">

		List<Element> termentries = getTermEntryList();
		long iSize = termentries.size();
		String nulls = de.folt.util.StringUtil.repeatString("0", (iSize + "").length());
		DecimalFormat dfTermEntry = new DecimalFormat(nulls);

		String nullsAll = de.folt.util.StringUtil.repeatString("0", (iSize + "").length() + 3);
		DecimalFormat dfAll = new DecimalFormat(nullsAll);

		for (int i = 0; i < termentries.size(); i++)
		{
			Element termEntry = termentries.get(i);
			totelTermEntryCounter++;
			// do not make changes for entries which have an id
			String termEntryNumber = dfTermEntry.format(i + 1);
			if ((termEntry.getAttribute("id") == null) || termEntry.getAttribute("id").equals(""))
			{
				termEntryID = "termEntry_" + termEntryNumber;
				termEntry.setAttribute("id", termEntryID);
				termEntryCounter++;
			}
			// get Langset
			List<Element> langSet = getLangSetList(termEntry);
			if (langSet == null)
				continue;
			for (int j = 0; j < langSet.size(); j++)
			{
				List<Element> tigs = getTigList(langSet.get(j));
				if (tigs == null)
					continue;
				String lang = langSet.get(j).getAttributeValue("lang", Namespace.XML_NAMESPACE);
				if (lang == null)
					lang = langSet.get(j).getAttributeValue("lang");
				String nullstigs = de.folt.util.StringUtil.repeatString("0", (tigs.size() + "").length());
				DecimalFormat dftigs = new DecimalFormat(nullstigs);
				for (int k = 0; k < tigs.size(); k++)
				{
					Element tig = tigs.get(k);
					if (tig == null)
						continue;
					totalTigCounter++;
					if ((tig.getAttribute("id") == null) || tig.getAttribute("id").equals(""))
					{
						tigId = "tig_" + termEntryNumber + "_" + dftigs.format(k + 1) + "_"
								+ dfAll.format(totalTigCounter) + "_" + lang;
						tig.setAttribute("id", tigId);
						tigCounter++;
					}
					List<Element> terms = getTermList(tig);
					if (terms == null)
						continue;
					String nullterms = de.folt.util.StringUtil.repeatString("0", (terms.size() + "").length());
					DecimalFormat dfterms = new DecimalFormat(nullterms);
					for (int l = 0; l < terms.size(); l++)
					{
						Element term = terms.get(l);
						if (term == null)
							continue;
						totalTermCounter++;
						if ((term.getAttribute("id") != null) && !term.getAttribute("id").equals(""))
							continue;
						termId = "term_" + termEntryNumber + "_" + dftigs.format(k + 1) + "_" + lang + "_"
								+ dfterms.format(l + 1) + "_" + dfAll.format(totalTermCounter);
						term.setAttribute("id", termId);
						termCounter++;
					}
				}
			}
		}

		System.out.println("termEntry IDs added (" + totelTermEntryCounter + "): " + termEntryCounter
				+ "\ntig IDs added (" + totalTigCounter + "): " + tigCounter + "\nterm IDs added (" + totalTermCounter
				+ "): " + termCounter);
		return bResult;
	}

	/**
	 * Generate ids for all missing ids in a tbx file
	 * 
	 * @param tbxfile
	 *            tbx file to generate ids for
	 * @return output tbx file generates as tbxfile + ".id.tbx"
	 */
	public String bGenerateIds(String tbxfile)
	{
		String outtbxfile = tbxfile + ".id.tbx";
		return bGenerateIds(tbxfile, outtbxfile);
	}

	/**
	 * Generate ids for all missing ids in a tbx file
	 * 
	 * @param tbxfile
	 *            tbxfile tbx file to generate ids for
	 * @param outtbxfile
	 *            output tbx file
	 * @return output tbx file
	 */
	public String bGenerateIds(String tbxfile, String outtbxfile)
	{
		try
		{
			TbxDocument tbxDocument = new TbxDocument();
			tbxDocument.loadXmlFile(tbxfile);
			try
			{
				tbxDocument.bGenerateIds();
			}
			catch (Exception e)
			{
				e.printStackTrace();
				setErrorCode(6);
			}
			tbxDocument.saveToXmlFile(outtbxfile);
			return outtbxfile;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			setErrorCode(2);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.folt.models.documentmodel.document.XmlDocument#createDocument(java
	 * .lang.String)
	 */
	@Override
	public boolean createDocument(String xmlDocumentName)
	{
		try
		{
			File outfile = new File(xmlDocumentName);

			FileWriter fileWriter = new FileWriter(outfile);
			fileWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			fileWriter.write("<martif type=\"DXLT\"><text><body></body></text>");
			fileWriter.write("</martif>");
			fileWriter.close();

			return true;
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			setErrorCode(3);
			return false;
		}
	}

	public int getErrorCode()
	{
		return errorCode;
	}

	/**
	 * getTermEntryList
	 * 
	 * @param termEntry
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<Element> getLangSetList(Element termEntry)
	{
		List<Element> langSet = termEntry.getChildren("langSet");
		return langSet;
	}

	public Element getTbxBody()
	{
		if (tbxBody == null)
			tbxBody = getDocument().getRootElement().getChild("text").getChild("body");
		return tbxBody;
	}

	/**
	 * getTbxHeader
	 * 
	 * @return the header of the TMX document
	 */
	public Element getTbxHeader()
	{
		if (tbxHeader == null)
			tbxHeader = getDocument().getRootElement().getChild("martifHeader");
		return tbxHeader;
	}

	/**
	 * getTbxVersion get the TBX version of the tmx document
	 * 
	 * @return the TBX version of the document
	 */
	public String getTbxVersion()
	{
		String version = "1.0";
		return version;
	}

	/**
	 * getTermEntryList
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Element> getTermEntryList()
	{
		return this.tbxBody.getChildren("termEntry");
	}

	/**
	 * getTermList
	 * 
	 * @param element
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<Element> getTermList(Element element)
	{
		List<Element> term = element.getChildren("term");
		return term;
	}

	/**
	 * getTigList
	 * 
	 * @param element
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<Element> getTigList(Element element)
	{
		List<Element> tigSet = element.getChildren("tig");
		return tigSet;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.folt.models.documentmodel.document.XmlDocument#loadXmlFile(java.io
	 * .File)
	 */
	public Document loadXmlFile(File newFile)
	{
		try
		{
			super.loadXmlFile(newFile);
			if (document != null)
			{
				getTbxBody();
				getTbxHeader();
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			setErrorCode(4);
			document = null;
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
	public Document loadXmlFile(String filename)
	{
		try
		{
			super.loadXmlFile(filename);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			setErrorCode(5);
			document = null;
		}
		if (document != null)
		{
			getTbxBody();
			getTbxHeader();
		}
		return document;
	}

	public void setErrorCode(int errorCode)
	{
		this.errorCode = errorCode;
	}

	/**
	 * @param tbxBody
	 *            the tbxBody to set
	 */
	public void setTbxBody(Element tbxBody)
	{
		this.tbxBody = tbxBody;
	}

	/**
	 * @param tbxHeader
	 *            the tbxHeader to set
	 */
	public void setTbxHeader(Element tbxHeader)
	{
		this.tbxHeader = tbxHeader;
	}

	/**
	 * tuToMultiLingualObject converts a given tu element into a
	 * MultiLingualObject
	 * 
	 * @param tu
	 *            the tu Element
	 * @return the MultiLingualObject for the given tu
	 * 
	 */
	@SuppressWarnings("unchecked")
	public MultiLingualObject termEntryToMultiLingualObject(Element termEntry)
	{
		LinguisticProperties linguisticProperties = new LinguisticProperties();
		MultiLingualObject multi = new MultiLingualObject(linguisticProperties, LinguisticTypes.TERM);
		LinguisticProperty termEntryProp = new LinguisticProperty("termEntry", termEntry);
		multi.addLinguisticProperty(termEntryProp);
		List<Attribute> attrlist = termEntry.getAttributes();
		for (int i = 0; i < attrlist.size(); i++)
		{
			Attribute attr = attrlist.get(i);
			String value = attr.getValue();
			String name = attr.getName();
			termEntryProp = new LinguisticProperty("ATT:" + name, value);
			multi.addLinguisticProperty(termEntryProp);
		}

		List<Element> langsets = this.getLangSetList(termEntry);
		for (int i = 0; i < langsets.size(); i++)
		{
			List<Element> tigentries = this.getTigList(langsets.get(i));
			String language = langsets.get(i).getAttributeValue("lang", Namespace.XML_NAMESPACE);
			if (language == null)
				language = langsets.get(i).getAttributeValue("lang");
			language.replaceFirst("^.*?:(.*)", "$1");
			for (int j = 0; j < tigentries.size(); j++)
			{
				MonoLingualObject mono = tigToMonoLingualObject(tigentries.get(j), language);
				if (mono != null)
				{
					multi.addMonoLingualObject(mono);
				}
			}
		}
		return multi;
	}

	/**
	 * tuvToMonoLingualObject converts a given tuv element into a
	 * MonoLingualObject
	 * 
	 * @param tuv
	 *            the tuv Element
	 * @return the MonoLingualObject for the given tuv
	 */
	@SuppressWarnings("unchecked")
	public MonoLingualObject tigToMonoLingualObject(Element tig, String language)
	{
		// get the attributes, props and notes
		LinguisticProperties linguisticProperties = new LinguisticProperties();
		LinguisticProperty termEntryProp = new LinguisticProperty("termEntry", tig);
		String segment = ""; // tig.getChildText("term");
		Element term = tig.getChild("term");
		String termid = "";
		if (term != null)
		{
			segment = term.getText();
			termid = term.getAttributeValue("id");
		}
		String plainTextSegment = MonoLingualObject.simpleComputePlainText(segment);
		MonoLingualObject mono = new MonoLingualObject(linguisticProperties, LinguisticTypes.TERM, segment,
				plainTextSegment, language);
		mono.addLinguisticProperty(termEntryProp);
		mono.addStringLinguisticProperty("termelementid", termid);

		List<Attribute> attrlist = tig.getAttributes();
		for (int i = 0; i < attrlist.size(); i++)
		{
			Attribute attr = attrlist.get(i);
			String value = attr.getValue();
			String name = attr.getName();
			termEntryProp = new LinguisticProperty("ATT:" + name, value);
			mono.addLinguisticProperty(termEntryProp);
		}

		List<Element> termNotes = tig.getChildren("termNote");
		for (int i = 0; i < termNotes.size(); i++)
		{
			Element termNote = termNotes.get(i);
			termEntryProp = new LinguisticProperty("termNote." + termNote.getAttributeValue("type"), termNote.getText());
			mono.addLinguisticProperty(termEntryProp);
		}
		return mono;
	}

	public String getVersion()
	{
		return version;
	}

	public void setVersion(String version)
	{
		this.version = version;
	}

}
