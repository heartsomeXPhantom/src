/*
 * Created on 24.06.2010
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.test;

import java.util.List;

import de.folt.jsp.XliffFile;

/**
 * @author klemens
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class TestXliffDocumentModell
{

	/**
	 * main
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub
		String sourceDocument = args[0];
		String updateDocument = args[1];
		if (1 == 2)
		{
			try
			{
				int iRes = de.folt.models.documentmodel.xliff.XliffDocument.updateXliffDocument(
						sourceDocument, updateDocument);
				System.out.println(iRes);
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}
		}

		XliffFile xliffFile = new XliffFile();

		String teststring = " Intern verwendet das Werkzeug die Lokalisierungs-Standards <ph ctype=\"bold\" id=\"0\">&lt;b&gt;</ph>XLIFF<ph id=\"1\">&lt;/b&gt;</ph> (Localisation Interchange XML Format) und <ph ctype=\"bold\" id=\"2\">&lt;b&gt;</ph>TMX<ph id=\"3\">&lt;/b&gt;</ph> (Translation Memory Exchange Format).";
		System.out.println(teststring);
		String newString = xliffFile.convertXliffStringToHtml(teststring, "", "0", "274");
		newString = "<trans-unit approved=\"no\" id=\"274\" resname=\"res20\" xml:space=\"preserve\"><source xml:lang=\"de\">" +  newString + "</trans-unit>";
		System.out.println(newString);
		newString = xliffFile.reInsertFormatInformation(newString);  // convertHtmlStringToXliff
		System.out.println(newString);

		teststring = "<ph type=\"un\"/>*** Das ist ein Test <ph type=\"un\"/> und hier <ph ctype=\"bold\" id=\"0\">&lt;b&gt;</ph> gehts <ph type=\"un\" /> weiter ";
		System.out.println(teststring);
		newString = xliffFile.convertXliffStringToHtml(teststring, "", "", "");
		System.out.println(newString);
		newString = xliffFile.reInsertFormatInformation(newString);
		System.out.println(newString);

		// teststring = "<source xml:lang=\"de\">Der Server ist das <A id=A0 title='<a href=\"#\" id=\"A0\" name=\"Coded:PHBoIGN0eXBlPSJib2xkIiBpZD0iMCI+Jmx0O2ImZ3Q7PC9waD4=\" onclick=\"displayXliffFormatValue(this);\" title=\"<ph ctype=\"bold\" id=\"0\"><b></ph>\">' onclick=displayXliffFormatValue(this); href=\"#\" name=\"Coded:PGEgaHJlZj0iIyIgaWQ9IkEwIiBuYW1lPSJDb2RlZDpQSEJvSUdOMGVYQmxQU0ppYjJ4a0lpQnBa&#13;&#10;RDBpTUNJK0pteDBPMkltWjNRN1BDOXdhRDQ9IiBvbmNsaWNrPSJkaXNwbGF5WGxpZmZGb3JtYXRW&#13;&#10;YWx1ZSh0aGlzKTsiIHRpdGxlPSImbHQ7cGggY3R5cGU9JnF1b3Q7Ym9sZCZxdW90OyBpZD0mcXVv&#13;&#10;dDswJnF1b3Q7Jmd0OyZsdDtiJmd0OyZsdDsvcGgmZ3Q7Ij4=\">/0/</A>/0/<A id=A1 title=\"</a>\" onclick=displayXliffFormatValue(this); href=\"#\" name=Coded:PC9hPg==>/1/</A>zentrale webbasierte Übersetzungswerkzeug<A id=A2 title='<a href=\"#\" id=\"A1\" name=\"Coded:PHBoIGlkPSIxIj4mbHQ7L2ImZ3Q7PC9waD4=\" onclick=\"displayXliffFormatValue(this);\" title=\"<ph id=\"1\"></b></ph>\">' onclick=displayXliffFormatValue(this); href=\"#\" name=\"Coded:PGEgaHJlZj0iIyIgaWQ9IkExIiBuYW1lPSJDb2RlZDpQSEJvSUdsa1BTSXhJajRtYkhRN0wySW1a&#13;&#10;M1E3UEM5d2FEND0iIG9uY2xpY2s9ImRpc3BsYXlYbGlmZkZvcm1hdFZhbHVlKHRoaXMpOyIgdGl0&#13;&#10;bGU9IiZsdDtwaCBpZD0mcXVvdDsxJnF1b3Q7Jmd0OyZsdDsvYiZndDsmbHQ7L3BoJmd0OyI+\">/2/</A>/1/<A id=A3 title=\"</a>\" onclick=displayXliffFormatValue(this); href=\"#\" name=Coded:PC9hPg==>/3/</A>.</source>";
		// System.out.println(teststring);
		// newString = xliffFile.convertHtmlFromBrowserStringToXliff(teststring);
		// System.out.println(newString);

		if (false)
		{
			xliffFile = new XliffFile(sourceDocument);
			List<org.jdom.Element> fileElements = xliffFile.getFiles();
			if (fileElements.size() > 0)
			{
				org.jdom.Element fileElement = xliffFile.getFiles().get(0);
				xliffFile.setFileElement(fileElement);
				org.jdom.Element bodyElement = xliffFile.getBody(fileElement);
				xliffFile.setBodyElement(bodyElement);
				List<org.jdom.Element> transUnits = xliffFile.getTransUnitList(bodyElement);
				xliffFile.setTransUnits(transUnits);
				int len = transUnits.size();
				for (int iNumber = 0; iNumber < len; iNumber++)
				{
					String res = xliffFile.getFuzzyMatchStatus(iNumber);
					System.out.println(iNumber + " Fuzzyness: " + res);
				}
			}
		}
	}

}
