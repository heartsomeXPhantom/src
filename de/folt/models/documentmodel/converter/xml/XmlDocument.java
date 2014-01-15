/*
 * Created on 04.07.2011
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.models.documentmodel.converter.xml;


/**
 * @author klemens
 * 
 *         To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and Comments
 */
public class XmlDocument
{

	public int correctXMLFile(String fileName)
	{
		int result = 0;

		// \xhh
		String replString = "[";
		for (int i = 1; i < 32; i++)
		{
			if (i == 10)
				continue;
			if (i == 13)
				continue;
			if (i == 9)
				continue;

			char c = (char) i;

			replString = replString + c;

			if (i != 31)
				replString = replString + "|";
		}
		replString = replString + "]";

		String unicodeEncoding = de.folt.util.OpenTMSSupportFunctions.determineBOMFromFile(fileName);
		if (unicodeEncoding.indexOf("Nobom") > -1)
		{
			unicodeEncoding = "UTF-8";
		}
		de.folt.util.OpenTMSSupportFunctions.replaceStringInFile(fileName, replString, "", true, unicodeEncoding);

		return result;
	}

	/**
	 * main
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		XmlDocument xmldoc = new XmlDocument();
		xmldoc.correctXMLFile(args[0]);
	}
}
