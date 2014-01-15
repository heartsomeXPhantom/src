/*
 * Created on 27.01.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.models.documentmodel.document;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Observable;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.folt.constants.OpenTMSConstants;

/**
 * This class XmlDocument defines the methods for reading and writing an XML
 * Documents specific for the FOLT/OpenTMS needs.<br>
 * 
 * 
 * @author klemens
 * 
 */
public class XmlDocument extends Observable implements Serializable
{

	/**
	 * This class implements a specific EntityResolver. If a given entity (dtd
	 * etc.) cannot be found it uses the systemId for resolving. This is done a)
	 * by checking if the entity exists (e.g. in the file system). If yes the
	 * absolute path is returned as an InputSource. If not the entity (e.g. dtd)
	 * is looked up in the OpenTMS ini directory (Property of OpenTMS.properties
	 * - OpenTMS.XmlEntityResolverDirectory: e.g.
	 * OpenTMS.XmlEntityResolverDirectory=%OpenTMS.dir%/ini/) and this path
	 * returned as the InputSource.
	 * 
	 * @author klemens
	 * 
	 */
	protected class XmlEntityResolver implements EntityResolver
	{
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.xml.sax.EntityResolver#resolveEntity(java.lang.String,
		 * java.lang.String)
		 */
		public InputSource resolveEntity(final java.lang.String publicId, final java.lang.String systemId)
				throws SAXException, java.io.IOException
		{
			InputSource isource = null;
			try
			{
				@SuppressWarnings("unused")
				InputStream inputStream = new URL(systemId).openStream();
				// isource = new InputSource(inputStream);
				return null;
			}
			catch (Exception exc)
			{
				File file = new File(systemId);
				String fileName = file.getName();
				File f = new File(fileName);
				String resolveDtd = fileName;
				// System.out.println("0:" + resolveDtd);
				if (!f.exists())
				{
					resolveDtd = catalogueDirectory + "/" + fileName;
					f = new File(resolveDtd);
				}
				if (!f.exists())
				{
					String iniDir = de.folt.util.OpenTMSProperties.getInstance().getOpenTMSProperty(
							"OpenTMS.XmlEntityResolverDirectory");
					if (iniDir == null)
						iniDir = "ini/";
					resolveDtd = iniDir + fileName;
					f = new File(resolveDtd);
					if (!f.exists())
					{
						return null;
					}
					// System.out.println("1:" + resolveDtd);
				}
				else
				{
					resolveDtd = f.getAbsolutePath();
					// System.out.println("2:" + resolveDtd);
				}

				isource = new InputSource(resolveDtd);
			}

			return isource;

		}
	}

	/**
     * 
     */
	private final static long serialVersionUID = 8365721445074469868L;

	/**
	 * bIsXmlDocument check if a document is an xml document - look for <xml.*>
	 * 
	 * @param fileName
	 *            the file to check
	 * @return true if xml document false otherwise
	 */
	public static boolean bIsXmlDocument(String fileName)
	{
		File fi = new File(fileName);
		if (!fi.exists())
			return false;
		try
		{
			XmlDocument doc = new XmlDocument();
			doc.loadXmlFile(fi);
			doc = null;
			return true;
		}
		catch (Exception e)
		{
			// e.printStackTrace();
			return false;
		}
	}

	/**
	 * getRootElementName gets the name of the root element of an xml file
	 * 
	 * @param fileName
	 *            the xml file to check
	 * @return the name of the root element
	 */
	public static String getRootElementName(String fileName)
	{
		if (XmlDocument.bIsXmlDocument(fileName))
		{
			String rootname = "";
			try
			{
				XmlDocument doc = new de.folt.models.documentmodel.document.XmlDocument();
				doc.loadXmlFile(fileName);
				if (doc.getDocument() == null)
				{
					rootname = "";
				}
				else
				{
					rootname = doc.getRootElementName();
				}
			}
			catch (Exception ex)
			{
				rootname = "";
			}
			return rootname;
		}
		return "";
	}

	/**
	 * main - test xml document just supply a xml document
	 * 
	 * @param args
	 *            0: xml document file
	 */
	public static void main(String[] args)
	{
		String xmlfile = args[0];
		XmlDocument doc = new XmlDocument();
		if (args.length > 1)
			doc.setCatalogueDirectory(args[1]);
		doc.loadXmlFile(xmlfile);
		if (args.length > 2)
			doc.saveToXmlFile(args[2]);
		doc = null;
	}

	private boolean bExpandExternalEntities = true;

	private String catalogueDirectory = "";

	protected Document document = null;

	protected Namespace namespace = Namespace.NO_NAMESPACE;

	protected List<Namespace> namespacelist = null;

	protected final String OUTPUT_ENCODING = "UTF-8";

	protected Element root;

	private String xmlDocumentName = null; // the name of the xml file
											// associated with the document
											// (required load!)

	protected XMLOutputter xmlOutputter = null;

	protected URL xmlURL = null;

	/**
	 * Constructor does nothing.
	 */
	public XmlDocument()
	{
		super();
	}

	/**
	 * @param file
	 *            create an xml document and load an xml file stream
	 */
	public XmlDocument(File file)
	{
		super();
		this.loadXmlFile(file);
	}

	/**
	 * @param fileName
	 *            create an xml document and load an xml file
	 */
	public XmlDocument(String fileName)
	{
		super();
		this.loadXmlFile(fileName);
	}

	/**
	 * bIsXmLFile simple check if a file is an xml file - just check if we find
	 * at the start "<?xml"
	 * 
	 * @param fileName
	 *            file to check
	 * @return true for an xml file otherwise false
	 */
	public boolean bIsXmLFile(String fileName)
	{
		// simple check if xml file ... wk - 22.10.2009
		// avoid the exception below...
		File newFile = new File(fileName);
		if (!newFile.exists())
			return false;
		String format = de.folt.util.OpenTMSSupportFunctions.determineBOMFromFile(fileName);
		if (format.equals("UTF-8-Nobom"))
			format = "UTF-8";
		else if (format.equals("Nobom"))
			format = "UTF-8";
		try
		{
			FileInputStream fi = new FileInputStream(newFile);
			int i = 0;
			int iread;
			int maxsize = 300;
			byte buff[] = new byte[maxsize];
			while ((iread = fi.read()) != -1 && i < maxsize)
			{
				byte nByte = (byte) iread;
				if ((nByte != '\n') && (nByte != '\r'))
				{
					buff[i++] = nByte;
				}
			}
			fi.close();
			String str = new String(buff, format);
			if (str.indexOf("<?xml") > -1)
				return true;
			return false;
		}
		catch (FileNotFoundException e1)
		{
			e1.printStackTrace();
			return false;
		}
		//
		catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * buildElement creates an Element from a string representation of the
	 * element. The new element is created by using a StringReader as
	 * InputSource for parsing the string. Please note that the method creates
	 * from &lt;element&gt;...&lt;/element&gt; real elements (Element type); it
	 * does not convert &lt;element&gt; to "&lt;element&gt;" to
	 * "&amp;lt;element&amp;gt;".
	 * 
	 * @param elementString
	 *            the string from which an element should be created.
	 * @return the element generated from the string
	 * @throws de.folt.util.OpenTMSException
	 */
	public Element buildElement(String elementString) throws de.folt.util.OpenTMSException
	{
		try
		{
			String text = "<?xml version=\"1.0\"  encoding=\"UTF-8\" ?>\n";
			if (elementString.indexOf("<?xml") > -1)
				text = "";

			text = text + elementString;
			StringReader xmlReader = new StringReader(text);
			InputSource in = new InputSource(xmlReader);

			boolean validate = false;
			SAXBuilder b = new SAXBuilder(validate);
			Document d = b.build(in);
			Element e = d.getRootElement();
			e.setNamespace(this.getNamespace());
			e.detach();
			d = null;
			return e;
		}
		catch (Exception ex)
		{
			throw new de.folt.util.OpenTMSException("buildElement Error", "buildElement",
					OpenTMSConstants.OpenTMS_BUILDELEMET_ERROR, elementString, ex);
		}
	}
	
	/**
	 * createDocument creates an xml document; must be overridden by subclass
	 * 
	 * @param xmlDocumentName
	 *            the document to create
	 * @return true in case of success, otherwise false
	 */
	public boolean createDocument(String xmlDocumentName)
	{
		return false;
	}

	/**
	 * @param element
	 * @return
	 */
	public String elementContentToString(Element element)
	{
		return elementContentToString(element, true);
	}

	/**
	 * elementContentToString returns a pure string version of the content of
	 * the element. Only the first opening and last closing tag of the element
	 * is removed! <pr> String str = this.elementToString(element); String
	 * elementname = element.getName(); str = str.replaceFirst("^<" +
	 * elementname + ".*?>", ""); str = str.replaceAll("</" + elementname +
	 * ".*?>$", ""); </pr> Example:
	 * 
	 * <pre>
	 *  TU string a (0) = &quot;&lt;seg&gt;&lt;ph&gt;&lt;img class=&quot;face&quot; src=&quot;../img/face/klemens_waldhoer.jpg&quot; alt=&quot;&lt;/ph&gt;Dr. Klemens Waldhör&lt;ph&gt;&quot; /&gt;&lt;/ph&gt;&lt;/seg&gt;&quot;
	 *  TU string b (0) = &quot;&lt;ph&gt;&lt;img class=&quot;face&quot; src=&quot;../img/face/klemens_waldhoer.jpg&quot; alt=&quot;&lt;/ph&gt;Dr. Klemens Waldhör&lt;ph&gt;&quot; /&gt;&lt;/ph&gt;&quot;
	 * </pre>
	 * 
	 * @param element
	 *            the element to create a string for the content
	 * @return the string representation of the element content (meaning just
	 *         the xml element as it is>
	 */
	public String elementContentToString(Element element, boolean bDoNotKeepElements)
	{
		String str = elementToString(element);
		str = str.replaceAll("[\t\n\r ]*$", "");
		str = str.replaceAll("^[\t\n\r ]*", "");
		
		str = str.replaceAll(" clone=\"yes\"", "");
		
		if (bDoNotKeepElements)
		{
			String elementname = element.getName();
			str = str.replaceFirst("^<" + elementname + ".*?>", "");
			str = str.replaceAll("</" + elementname + ".*?>$", "");
		}		
		return str;
	}

	/**
	 * elementToString creates a pure string version of the element (including
	 * the tag name etc.) Example:
	 * 
	 * <pre>
	 *  &quot;&lt;seg&gt;&lt;ph&gt;&lt;img class=&quot;face&quot; src=&quot;../img/face/klemens_waldhoer.jpg&quot; alt=&quot;&lt;/ph&gt;Dr. Klemens Waldhör&lt;ph&gt;&quot; /&gt;&lt;/ph&gt;&lt;/seg&gt;&quot;
	 * </pre>
	 * 
	 * @param element
	 *            the element to create a string from
	 * @return the string representation of the element (meaning just the xml
	 *         element as it is>
	 */
	public String elementToString(Element element)
	{
		String str = "";
		try
		{
			str = "";
			StringWriter strwriter = new StringWriter();
			if (xmlOutputter == null)
				xmlOutputter = new XMLOutputter(Format.getRawFormat()); // 02.12.2013
			// xmlOutputter.setFormat(Format.getPrettyFormat());
			Format fold = xmlOutputter.getFormat();
			Format fnew = xmlOutputter.getFormat();
			fnew.setLineSeparator("\n");
			xmlOutputter.setFormat(fnew);
			xmlOutputter.output(element, strwriter);
			str = strwriter.toString();
			xmlOutputter.setFormat(fold);
			strwriter.close();
			strwriter = null;
			return str;
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return str;
	}
	/**
	 * @return the catalogueDirectors
	 */
	public String getCatalogueDirectory()
	{
		return catalogueDirectory;
	}

	/**
	 * @return the document
	 */
	public Document getDocument()
	{
		return document;
	}

	/**
	 * @return the namespace
	 */
	public Namespace getNamespace()
	{
		return namespace;
	}

	/**
	 * @return the namespacelist
	 */
	public List<Namespace> getNamespacelist()
	{
		return namespacelist;
	}

	/**
	 * getRoot get the root element of the XmlDocument
	 * 
	 * @return the root element
	 */
	public Element getRoot()
	{
		try
		{
			return document.getRootElement();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * getRootElementName gets the name of the root element of the loaded
	 * document
	 * 
	 * @return the name of the root element
	 */
	public String getRootElementName()
	{
		try
		{
			Element root = document.getRootElement();
			return root.getName();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * getsubElementFromElementAttributeNameValue get the (sub) element with
	 * string name subElementName of an element where the subelement has an
	 * attribute with name attributeName and an attribute value of
	 * attributeValue<br>
	 * Example:<br>
	 * 
	 * <pre>
	 * &lt;prop-group name="MULTI-624649fa-7e65-42ee-928d-aa2b10b12f27">&lt;prop prop-value="creationid">1233c020bc55054_klemens&lt;/prop>&lt;prop prop-value="usagecount">0&lt;/prop>&lt;prop prop-value="entrynumber">21477&lt;/prop>&lt;prop prop-value="datatype">125447&lt;/prop>&lt;prop prop-value="changeid">1233c020bc55054_klemens&lt;/prop>&lt;/prop-group><br> 
	 * getsubElementFromElementAttributeNameValue(prop-group, "prop", "prop-value", "creation-id") will return<br>&lt;prop prop-value="creationid">1233c020bc55054_klemens&lt;/prop>
	 * </pre>
	 * 
	 * @param element
	 *            the element which is the super element
	 *            ("surrounds subElementName")
	 * @param subElementName
	 *            the string name of the sub element
	 * @param attributeName
	 *            the searched attribute name of the sub element
	 * @param attributeValue
	 *            the searched attribute value of the searched attribute name of
	 *            the sub element
	 * @return null if not found, otherwise the sub element
	 */
	@SuppressWarnings("unchecked")
	public Element getsubElementFromElementAttributeNameValue(Element element, String subElementName,
			String attributeName, String attributeValue)
	{
		List<Element> propGroup = element.getChildren(subElementName);
		for (int k = 0; k < propGroup.size(); k++)
		{
			List<Attribute> attributes = propGroup.get(k).getAttributes();
			for (int m = 0; m < attributes.size(); m++)
			{
				String nameprop = attributes.get(m).getName();
				if (nameprop.equals(attributeName) && (attributes.get(m).getValue()).equals(attributeValue))
				{
					return propGroup.get(k);
				}
			}
		}

		return null;
	}

	/**
	 * getsubElementFromElementAttributeNameValueRegExp get the (sub) element
	 * with string name subElementName of an element where the subelement has an
	 * attribute with name attributeName and an attribute value of
	 * attributeValue which is a regular expression<br>
	 * Example:<br>
	 * 
	 * <pre>
	 * &lt;prop-group name="MULTI-624649fa-7e65-42ee-928d-aa2b10b12f27">&lt;prop prop-value="creationid">1233c020bc55054_klemens&lt;/prop>&lt;prop prop-value="usagecount">0&lt;/prop>&lt;prop prop-value="entrynumber">21477&lt;/prop>&lt;prop prop-value="datatype">125447&lt;/prop>&lt;prop prop-value="changeid">1233c020bc55054_klemens&lt;/prop>&lt;/prop-group><br> 
	 * getsubElementFromElementAttributeNameValue(prop-group, "prop", "prop-value", "$creation.*") will return<br>&lt;prop prop-value="creationid">1233c020bc55054_klemens&lt;/prop>
	 * </pre>
	 * 
	 * @param element
	 *            the element which is the super element
	 *            ("surrounds subElementName")
	 * @param subElementName
	 *            the string name of the sub element
	 * @param attributeName
	 *            the searched attribute name of the sub element
	 * @param attributeValue
	 *            the searched attribute value of the searched attribute name of
	 *            the sub element; the search is based on a regular expression
	 *            for the attributeValue
	 * @return null if not found, otherwise the sub element
	 */
	@SuppressWarnings("unchecked")
	public Element getsubElementFromElementAttributeNameValueRegExp(Element element, String subElementName,
			String attributeName, String attributeValue)
	{
		List<Element> propGroup = element.getChildren(subElementName);
		for (int k = 0; k < propGroup.size(); k++)
		{
			List<Attribute> attributes = propGroup.get(k).getAttributes();
			for (int m = 0; m < attributes.size(); m++)
			{
				String nameprop = attributes.get(m).getName();
				String value = attributes.get(m).getValue();
				if (nameprop.equals(attributeName))
				{
					if (value.matches(attributeValue))
					{
						return propGroup.get(k);
					}
				}
			}
		}

		return null;
	}

	/**
	 * @return the xmlDocumentName
	 */
	public String getXmlDocumentName()
	{
		return xmlDocumentName;
	}

	/**
	 * @return the xmlOutputter
	 */
	public XMLOutputter getXmlOutputter()
	{
		return xmlOutputter;
	}

	/**
	 * @return the xmlURL
	 */
	public URL getXmlURL()
	{
		return xmlURL;
	}

	/**
	 * @return the bExpandExternalEntities
	 */
	public boolean isBExpandExternalEntities()
	{
		return bExpandExternalEntities;
	}

	/**
	 * @param elementString
	 * @return
	 */
	public boolean isValidElement(String elementString)
	{
		try
		{
			String text = "<?xml version=\"1.0\"  encoding=\"UTF-8\" ?>\n";
			if (elementString.indexOf("<?xml") > -1)
				text = "";

			text = text + elementString;
			StringReader xmlReader = new StringReader(text);
			InputSource in = new InputSource(xmlReader);

			boolean validate = false;
			SAXBuilder b = new SAXBuilder(validate);
			Document d = b.build(in);
			Element e = d.getRootElement();
			e.setNamespace(this.getNamespace());
			e.detach();
			d = null;
			return true;
		}
		catch (Exception ex)
		{
			return false;
		}
	}
	
	/*
	 * http://dequeue.blogspot.com/2008/12/completely-ignoring-dtd-with.html
private Document parseXmlDocumentFromString(String input) throws JDOMException, IOException {
    SAXBuilder builder = new SAXBuilder(false);
    builder.setValidation(false);
    builder.setFeature("http://xml.org/sax/features/validation", false);
    builder.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
    builder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
    return builder.build(new StringReader(input));                            
}
	 */

	/**
	 * loadXmlFile loads an XML file
	 * 
	 * @param newFile
	 *            the File of the XML File
	 * @return the document loaded / null if file does not exist or is not an
	 *         xml file
	 */
	@SuppressWarnings("unchecked")
	public Document loadXmlFile(File newFile)
	{
		String filename = newFile.getAbsolutePath();
		if (!bIsXmLFile(filename))
			return null;

		SAXBuilder builder = new SAXBuilder(false);
		builder.setValidation(false);
	    builder.setFeature("http://xml.org/sax/features/validation", false);
	    builder.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
	    builder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		builder.setExpandEntities(bExpandExternalEntities);
		builder.setEntityResolver(new XmlEntityResolver());

		try
		{
			if (filename.indexOf("file:") != -1)
			{ //$NON-NLS-1$
				URI uri = new URI(filename);
				document = builder.build(uri.toURL());
				setXmlDocumentName(filename);
				uri = null;
			}
			else
			{
				File file = new File(filename);
				URI uri = file.toURI();
				document = builder.build(uri.toURL());
				setXmlDocumentName(filename);
				uri = null;
				file = null;
			}

		}
		catch (JDOMException e)
		{
			e.printStackTrace();
			document = null;
			return document;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			document = null;
			return document;
		}

		root = this.document.getRootElement();
		if (root != null)
		{
			namespace = root.getNamespace();
			namespacelist = (List<Namespace>) root.getAdditionalNamespaces();
		}

		return document;
	}

	/**
	 * loadXmlFile loads an XML file as a document
	 * 
	 * @param filename
	 *            the file name of the XML file
	 * @return the Document loaded
	 */
	@SuppressWarnings("unchecked")
	public Document loadXmlFile(String filename)
	{
		if (filename == null)
			return null;
		File f = new File(filename);
		if (!f.exists())
		{
			document = null;
			return null;
		}
		try
		{
			document = loadXmlFile(f);
		}
		catch (Exception ex)
		{
			document = null;
			ex.printStackTrace();
			return document;
		}

		if (document == null)
		{
			return null;
		}
		root = this.document.getRootElement();
		if (root != null)
		{
			namespace = root.getNamespace();
			namespacelist = (List<Namespace>) root.getAdditionalNamespaces();
		}
		return document;
	}

	/**
	 * quoteXMLString convert character & < and > to their entity equivalent
	 * 
	 * <pre>
	 * string = string.replaceAll(&quot;&lt;&quot;, &quot;&lt;&quot;);
	 * string = string.replaceAll(&quot;&gt;&quot;, &quot;&gt;&quot;);
	 * string = string.replaceAll(&quot;&amp;&quot;, &quot;&amp;&quot;);
	 * </pre>
	 * 
	 * @param string
	 *            the string to quote
	 * @return the quoted string
	 */
	public String quoteXMLString(String string)
	{
		string = string.replaceAll("<", "&lt;");
		string = string.replaceAll(">", "&gt;");
		string = string.replaceAll("&", "&amp;");
		return string;
	}

	/**
	 * saveToXmlFile saves the currently loaded document back to the original
	 * filename
	 * 
	 * @return true in case of success / false otherwise
	 */
	public boolean saveToXmlFile()
	{
		if (getXmlDocumentName() != null)
		{
			return saveToXmlFile(getXmlDocumentName());
		}
		return false;
	}

	/**
	 * saveToXmlFile
	 * 
	 * @param outFile
	 *            the File handle for the xml output
	 * @return true in case of success / false otherwise
	 */
	public boolean saveToXmlFile(File outFile)
	{
		try
		{
		
			FileOutputStream outStream = new FileOutputStream(outFile);
			OutputStreamWriter outwriter = new OutputStreamWriter(outStream, OUTPUT_ENCODING);
			
			if (xmlOutputter == null)
				xmlOutputter = new XMLOutputter(Format.getRawFormat()); // 02.12.2013
			Format fold = xmlOutputter.getFormat();
			Format fnew = xmlOutputter.getFormat();
			fnew.setLineSeparator("\n");
			xmlOutputter.setFormat(fnew);
			xmlOutputter.output(document, outwriter);
			xmlOutputter.setFormat(fold);
			outwriter.close();
			outStream.close();
			return true;
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * saveToXmlFile writes the document to the given file name
	 * 
	 * @param filename
	 *            the file anme for the output xml file
	 * @return true in case of success / false otherwise
	 */
	public boolean saveToXmlFile(String filename)
	{
		File f = new File(filename);
		return saveToXmlFile(f);
	}

	/**
	 * @param bExpandExternalEntities
	 *            the bExpandExternalEntities to set
	 */
	public void setBExpandExternalEntities(boolean bExpandExternalEntities)
	{
		this.bExpandExternalEntities = bExpandExternalEntities;
	}

	/**
	 * @param catalogueDirectors
	 *            the catalogueDirectors to set
	 */
	public void setCatalogueDirectory(String catalogueDirectory)
	{
		this.catalogueDirectory = catalogueDirectory;
	}

	/**
	 * @param document
	 *            the document to set
	 */
	public void setDocument(Document document)
	{
		this.document = document;
	}

	/**
	 * @param namespace
	 *            the namespace to set
	 */
	public void setNamespace(Namespace namespace)
	{
		this.namespace = namespace;
	}

	/**
	 * @param namespacelist
	 *            the namespacelist to set
	 */
	public void setNamespacelist(List<Namespace> namespacelist)
	{
		this.namespacelist = namespacelist;
	}

	/**
	 * Method stores the name of the associated XML document; in addition it
	 * creates an URL from the file name and stores in xmLURL.
	 * 
	 * @param xmlDocumentName
	 *            the xmlDocumentName to set;
	 */
	public void setXmlDocumentName(String xmlDocumentName)
	{
		this.xmlDocumentName = xmlDocumentName;
		try
		{
			if (xmlDocumentName.indexOf("file:") != -1)
			{ //$NON-NLS-1$
				URI uri = new URI(xmlDocumentName);
				xmlURL = uri.toURL();
			}
			else
			{
				File file = new File(xmlDocumentName);
				URI uri = file.toURI();
				xmlURL = uri.toURL();
			}
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
			xmlURL = null;
		}
		catch (URISyntaxException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * @param xmlURL
	 *            the xmlURL to set
	 */
	public void setXmlURL(URL xmlURL)
	{
		this.xmlURL = xmlURL;
	}
}
