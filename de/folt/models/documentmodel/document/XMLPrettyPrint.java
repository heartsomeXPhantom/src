package de.folt.models.documentmodel.document;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.InputSource;

public class XMLPrettyPrint
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><tmx version=\"1.4\"><header><prop type=\"datasource\">mysqlclient</prop></header>"
				+ "<body>	<tu  datatype=\"186\" creationid=\"11d63a4372597_klemens\" usagecount=\"0\" changeid=\"11d63a4372597_klemens\">			"
				+ "<prop type=\"entrynumber\">79</prop>			<prop type=\"lastAccessTime\">1325868079000</prop>"
				+ "<prop type=\"internal-id\">193</prop>			<prop type=\"unique-id\">da83c4d9-7cb3-47dc-960b-43d1d9d62aa9</prop>"
				+ "<tuv xml:lang=\"en\"  datatype=\"187\" usagecount=\"0\" creationid=\"11d63a4372597_klemens.en\" changeid=\"11d63a4372597_klemens\">"
				+ "<prop type=\"unique-id\">219cd3bf-0234-424a-8cff-4acc178aeb07</prop>			<prop type=\"lastAccessTime\">1325868079000</prop>"
				+ "<prop type=\"internal-id\">386</prop>"
				+ "<prop type=\"action\">delete</prop>			<prop type=\"deletetime\">1330263692110</prop>"
				+ "<seg>Hydropur, without flange, with larger diameterS</seg>		</tuv>		</tu>	</body></tmx>";
		System.out.println(formatXml(xml));
	}

	public static String formatXml(String xml)
	{
		try
		{
			xml = xml.replaceAll(">[ \\t\\n]+<", "><");
			Transformer serializer = SAXTransformerFactory.newInstance().newTransformer();
			serializer.setOutputProperty(OutputKeys.INDENT, "yes");
			// serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			// serializer.setOutputProperty("{http://xml.customer.org/xslt}indent-amount", "2");
			Source xmlSource = new SAXSource(new InputSource(new ByteArrayInputStream(xml.getBytes())));
			StreamResult res = new StreamResult(new ByteArrayOutputStream());
			serializer.transform(xmlSource, res);
			return new String(((ByteArrayOutputStream) res.getOutputStream()).toByteArray());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return xml;
		}
	}
}
