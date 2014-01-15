/*
 * Created on 30.06.2010
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.jsp;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.jdom.Element;

import de.folt.models.documentmodel.xliff.XliffDocument;

/**
 * @author klemens
 * 
 *         This is a wrapper class which shields some functionality from the
 *         XliffDocument; the class is esp. intended to be used with jsp
 */
public class XliffFile extends de.folt.models.documentmodel.xliff.XliffDocument
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7165211004761029005L;

	public static String OTAG = "" + '\u00AB'; // open green tag

	public static String CTAG = "" + '\u00BB'; // close green tag

	/**
	 * @return the cTAG
	 */
	public static String getCTAG()
	{
		return CTAG;
	}

	public static String getMatchQualityColor(String quality)
	{
		int iQuality = 0;

		if (quality.equals("MT"))
		{
			return "#FFA500";
		}
		try
		{
			iQuality = Integer.parseInt(quality);
		}
		catch (NumberFormatException e)
		{
			try
			{
				iQuality = (int) Float.parseFloat(quality);
			}
			catch (NumberFormatException e1)
			{
				e1.printStackTrace();
				return "#F5F5F5";
			}
		}

		if (iQuality == 100)
		{
			return "#25A8CC"; // "lightblue0";
		}
		if (iQuality >= 90)
		{
			return "#4FB9D6"; // "lightblue1";
		}
		if (iQuality >= 80)
		{
			return "#72C7DE"; // "lightblue2";
		}
		if (iQuality >= 70)
		{
			return "#9BD7E7"; // "lightblue3";
		}
		if (iQuality >= 60)
		{
			return "#C2D7E7"; // "lightblue4";
		}

		return "#FFFFFF";
	}

	/**
	 * @return the oTAG
	 */
	public static String getOTAG()
	{
		return OTAG;
	}

	/**
	 * main
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub
		String sourceDocument = args[0];
		XliffFile xliff = new XliffFile(sourceDocument);
		Element file = xliff.getFiles().get(0);
		Element body = xliff.getBody(file);
		xliff.transUnits = xliff.getTransUnitList(body);
		for (int i = 0; i < xliff.transUnits.size(); i++)
		{
			String out = xliff.getAltTrans(i, "return false;");
			System.out.println(out);
		}
	}

	@SuppressWarnings("unused")
	private static String replaceWithA(String xliffString, String javaScript)
	{
		String newString = "";
		int i = 0;
		for (int j = 0; j < xliffString.length(); j++)
		{
			if (xliffString.charAt(j) == '<')
			{
				// search next > and /
				for (int k = j + 1; k < xliffString.length(); k++)
				{
					if (xliffString.charAt(k) == '>')
					{
						int iEnd = xliffString.indexOf(">", k + 1);
						String match = xliffString.substring(j, iEnd + 1);
						String matchTitle = match.replaceAll("<", "&lt;");
						matchTitle = matchTitle.replaceAll(">", "&gt;");
						matchTitle = matchTitle.replaceAll("\"", "&quot;");
						String matchReplacement = de.folt.util.OpenTMSSupportFunctions.encodeBASE64(match);
						String replacement = "<a href=\"#\" id=\"A" + i + "\" name=\"Coded:" + matchReplacement + "\" onclick=\"" + javaScript
								+ "\" title=\"" + matchTitle + "\">/" + i + "/</a>";
						newString = newString + replacement;
						i++;
						j = iEnd;
						break;
					}
					else if (xliffString.charAt(k) == '/')
					{
						int iEnd = xliffString.indexOf(">", k + 1);
						String match = xliffString.substring(j, iEnd + 1);
						String matchTitle = match.replaceAll("<", "&lt;");
						matchTitle = matchTitle.replaceAll(">", "&gt;");
						matchTitle = matchTitle.replaceAll("\"", "&quot;");
						String matchReplacement = de.folt.util.OpenTMSSupportFunctions.encodeBASE64(match);
						String replacement = "<a href=\"#\" id=\"A" + i + "\" name=\"Coded:" + matchReplacement + "\" onclick=\"" + javaScript
								+ "\" title=\"" + matchTitle + "\">/" + i + "/</a>";
						newString = newString + replacement;
						i++;
						j = iEnd;
						break;
					}
				}
			}
			else
			{
				newString = newString + xliffString.charAt(j);
			}
		}
		return newString;
	}

	/**
	 * replaceWithA replace a pattern with a a href representation
	 * 
	 * @param xliffString
	 * @param pattern
	 * @param javaStript
	 *            the Java Script method to call onClick
	 * @return
	 */
	@SuppressWarnings("unused")
	private static String replaceWithA1(String xliffString, String pattern, String javaScript)
	{
		String newString = xliffString;
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(xliffString);
		int i = 0;
		while (m.find())
		{
			String match = m.group(1);
			String matchTitle = match.replaceAll("<", "&lt;");
			matchTitle = matchTitle.replaceAll(">", "&gt;");
			matchTitle = matchTitle.replaceAll("\"", "&quot;");
			String matchReplacement = de.folt.util.OpenTMSSupportFunctions.encodeBASE64(match);
			String replacement = "<a href=\"#\" id=\"A" + i + "\" name=\"" + matchReplacement + "\" onclick=\"" + javaScript + "\" title=\""
					+ matchTitle + "\">/" + i + "/</a>";
			newString = newString.replaceFirst(Pattern.quote(match), replacement);
			i++;
		}
		return newString;
	}

	/**
	 * @param cTAG
	 *            the cTAG to set
	 */
	public static void setCTAG(String cTAG)
	{
		CTAG = cTAG;
	}

	/**
	 * @param oTAG
	 *            the oTAG to set
	 */
	public static void setOTAG(String oTAG)
	{
		OTAG = oTAG;
	}

	private Hashtable<String, String> formatSegmentNumberBasedHashtable = new Hashtable<String, String>();

	private Hashtable<String, String> formatSegmentIdBasedHashtable = new Hashtable<String, String>();

	String saveFile = null;

	// colorNameRGB.put("lightblue0", new RGB(0x25, 0xA8, 0xCC));
	// colorNameRGB.put("lightblue1", new RGB(0x4F, 0xB9, 0xD6));
	// colorNameRGB.put("lightblue2", new RGB(0x72, 0xC7, 0xDE));
	// colorNameRGB.put("lightblue3", new RGB(0x9B, 0xD7, 0xE7));
	// colorNameRGB.put("lightblue4", new RGB(0xC2, 0xD7, 0xE7));

	String xliffContent = null;

	String shortFileName = null;

	public int iTransUnitCounter = 0;

	URL url = null;

	org.jdom.Element fileElement = null;

	org.jdom.Element bodyElement = null;

	List<org.jdom.Element> transUnits = null;

	private HttpServletRequest request = null;
	private String directory = null;

	/**
	 * 
	 */
	public XliffFile()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param file
	 */
	public XliffFile(File file)
	{
		super(file);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param request
	 * @param directory
	 */
	public XliffFile(HttpServletRequest request, String directory)
	{
		super();
		this.request = request;
		this.directory = directory;
		readXliffFile(request, directory);
	}

	/**
	 * @param fileName
	 */
	public XliffFile(String fileName)
	{
		super(fileName);
		// TODO Auto-generated constructor stub
	}

	public String convertHtmlFromBrowserStringToXliff(String xliffString)
	{
		// String newString = xliffString;
		String pattern = "<([Aa]) .*?name=\"Coded:(.*?)\".*?>\\/(.*?)\\/</\\1>";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(xliffString);
		int i = 0;
		// Vector<String> matchVector = new Vector<String>();
		// Vector<String> replacerVector = new Vector<String>();
		while (m.find())
		{
			String match = m.group(2);
			@SuppressWarnings("unused")
			String number = m.group(3);
			System.out.println(m.group(0));
			String completeMatch = m.group(0);
			String matchReplacement = de.folt.util.OpenTMSSupportFunctions.decodeBASE64(match);
			xliffString = xliffString.replaceAll(Pattern.quote(completeMatch), matchReplacement);
			m = p.matcher(xliffString);
			i++;
		}

		return xliffString;
	}

	/**
	 * convertHtmlStringToXliff
	 * 
	 * @param xliffString
	 * @return
	 */
	public String convertHtmlStringToXliff(String xliffString)
	{
		String newString = xliffString;
		String pattern = "<a href=.*? name=\"Coded:(.*?)\" onclick=.*? title=\".*?\">/.*?/</a>";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(xliffString);
		int i = 0;
		while (m.find())
		{
			String match = m.group(1);
			String matchReplacement = de.folt.util.OpenTMSSupportFunctions.decodeBASE64(match);
			newString = newString.replaceFirst(Pattern.quote(m.group(0)), matchReplacement);
			i++;
		}
		return newString;
	}

	/**
	 * convertXliffStringToHtml convert an xliff formatted string to html
	 * version; all tags are converted to an a link representatation <ph
	 * ctype="bold" id="0">&lt;b&gt;</ph> >>> <a href="" id=""
	 * name="encodeBASE64(String string)">n</a>
	 * 
	 * @param xliffString
	 * @return
	 */
	public String convertXliffStringToHtml(String xliffString, String iNumber, String id)
	{
		String newString = replaceWithTextMarkers(xliffString, "return false;", iNumber, id, false);
		return newString;
	}

	/**
	 * convertXliffStringToHtml convert an xliff formatted string to html
	 * version; all tags are converted to an a link representatation; it insert
	 * for the onClick the JavaScript code supplied
	 * 
	 * @param xliffString
	 *            the xliff String to encoide
	 * @param javaScript
	 *            the Java Script method to call onClick
	 * @param iNumber
	 *            the zero based segment number
	 * @param id
	 *            the id of the element
	 * @return formatted string
	 */
	public String convertXliffStringToHtml(String xliffString, String javaScript, String iNumber, String id)
	{
		// String newString = replaceWithA(xliffString, javaScript);
		String newString = convertXliffStringToHtml(xliffString, javaScript, iNumber, id, false);

		return newString;
	}

	/**
	 * convertXliffStringToHtml convert an xliff formatted string to html
	 * version; all tags are converted to an a link representatation; it insert
	 * for the onClick the JavaScript code supplied
	 * 
	 * @param xliffString
	 *            the xliff String to encoide
	 * @param javaScript
	 *            the Java Script method to call onClick
	 * @param iNumber
	 *            the zero based segment number
	 * @param id
	 *            the id of the element
	 * @param id
	 *            true if formats should be encapsulated into <div...>
	 * @return formatted string
	 */
	public String convertXliffStringToHtml(String xliffString, String javaScript, String iNumber, String id, boolean bDiv)
	{
		// String newString = replaceWithA(xliffString, javaScript);
		String newString = replaceWithTextMarkers(xliffString, javaScript, iNumber, id, bDiv);

		return newString;
	}

	/**
	 * formatAltTransAsTable
	 * 
	 * @param transUnit
	 * @param javaScript
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String formatAltTransAsTable(Element transUnit, String javaScript)
	{
		XliffDocument temp = new XliffDocument();
		List<org.jdom.Element> altTransChildren = transUnit.getChildren("alt-trans");
		if ((altTransChildren == null) || (altTransChildren.size() == 0))
			return "";
		String altTransString = "<table border=\"1\" width=\"100%\">";
		altTransString = altTransString + "<colgroup><col width=\"5%\" /><col width=\"5%\" /><col width=\"45%\" /><col width=\"45%\" /></colgroup>";
		altTransString = altTransString + "<tr><th>#</th><th>%</th><th>Source Segment</th><th>Target Segments</th></tr>";

		String idTransUnit = transUnit.getAttributeValue("id");
		for (int i = 0; i < altTransChildren.size(); i++)
		{
			org.jdom.Element altTrans = altTransChildren.get(i);
			String similarity = altTrans.getAttributeValue("match-quality");
			String color = XliffFile.getMatchQualityColor(similarity);
			org.jdom.Element altSource = altTrans.getChild("source");
			String altSourceString = convertXliffStringToHtml(temp.elementContentToString(altSource), "", "", idTransUnit);
			String altSourceStringQuoted = altSourceString.replaceAll(Pattern.quote("<"), "&lt;");
			altSourceStringQuoted = altSourceStringQuoted.replaceAll(Pattern.quote(">"), "&gt;");
			altSourceStringQuoted = altSourceStringQuoted.replaceAll(Pattern.quote("\""), "&quot;");
			List<org.jdom.Element> altTargetChildren = altTrans.getChildren("target");
			String altTargetString = "";
			for (int j = 0; j < altTargetChildren.size(); j++)
			{
				if (j > 0)
					altTargetString = altTargetString + "<br />";
				String id = "A" + Math.random() + "" + (j + 1);
				altTargetString = altTargetString + "<a href=\"#\" id=\"" + id + "\" name=\"" + id + "\" onclick=\"" + javaScript + "\" title=\""
						+ altSourceStringQuoted + "\">" + (j + 1) + "</a>: <span id=\"T" + id + "\">"
						+ convertXliffStringToHtml(temp.elementContentToString(altTargetChildren.get(j)), "", "at" + i + "." + j, idTransUnit)
						+ "</div>";
			}

			altTransString = altTransString + "<tr bgcolor=\"" + color + "\"><td align=\"center\">" + (i + 1) + "</td><td align=\"center\">"
					+ similarity + "</td><td>" + altSourceString + "</td><td>" + altTargetString + "</td></tr>";
			// altTransString = this.elementContentToString(altTrans);
			// altTransString =
			// XliffFile.convertXliffStringToHtml(altTransString,
			// "displayXliffFormatValue(this);");
			// altTransString = altTransString.replaceAll("<", "&lt;");
			// altTransString = altTransString.replaceAll(">", "&gt;");
			// altTransString = altTransString.replaceAll("\"", "&quot;");
		}
		altTransString = altTransString + "</table>";
		return altTransString;
	}

	/**
	 * getAltTrans
	 * 
	 * @param iNumber
	 * @param javaScript
	 * @return
	 */
	public String getAltTrans(int iNumber, String javaScript)
	{
		return getAltTrans(iNumber, javaScript, transUnits);
	}

	/**
	 * getAltTrans
	 * 
	 * @param iNumber
	 * @param javaScript
	 * @param transUnits
	 * @return
	 */
	public String getAltTrans(int iNumber, String javaScript, List<Element> transUnits)
	{
		org.jdom.Element transUnit = transUnits.get(iNumber);
		return formatAltTransAsTable(transUnit, javaScript);

	}

	/**
	 * @return the bodyElement
	 */
	public org.jdom.Element getBodyElement()
	{
		return bodyElement;
	}

	/**
	 * @return the directory
	 */
	public String getDirectory()
	{
		return directory;
	}

	/**
	 * getFileContents
	 * 
	 * @param request
	 * @param directory
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public String getFileContents(HttpServletRequest request, String directory)
	{
		try
		{
			String contentType = request.getContentType();
			if ((contentType != null) && (contentType.indexOf("multipart/form-data") >= 0))
			{
				DataInputStream in = new DataInputStream(request.getInputStream());
				int formDataLength = request.getContentLength();
				byte dataBytes[] = new byte[formDataLength];
				int byteRead = 0;
				int totalBytesRead = 0;
				while (totalBytesRead < formDataLength)
				{
					byteRead = in.read(dataBytes, totalBytesRead, formDataLength);
					totalBytesRead += byteRead;
				}
				String file = new String(dataBytes);
				saveFile = file.substring(file.indexOf("filename=\"") + 10);
				saveFile = saveFile.substring(0, saveFile.indexOf("\n"));
				saveFile = saveFile.substring(saveFile.lastIndexOf("\\") + 1, saveFile.indexOf("\""));
				int lastIndex = contentType.lastIndexOf("=");
				String boundary = contentType.substring(lastIndex + 1, contentType.length());
				int pos;
				pos = file.indexOf("filename=\"");
				pos = file.indexOf("\n", pos) + 1;
				pos = file.indexOf("\n", pos) + 1;
				pos = file.indexOf("\n", pos) + 1;
				int boundaryLocation = file.indexOf(boundary, pos) - 4;
				int startPos = ((file.substring(0, pos)).getBytes()).length;
				int endPos = ((file.substring(0, boundaryLocation)).getBytes()).length;

				long time = Calendar.getInstance().getTimeInMillis();

				String newFileName = saveFile + "." + time;
				if (directory != null)
				{
					newFileName = directory + "/" + saveFile + "." + time;
				}

				File f = new File(newFileName);
				FileOutputStream fileOut = new FileOutputStream(f);
				fileOut.write(dataBytes, startPos, (endPos - startPos));
				fileOut.flush();
				fileOut.close();
				file = file.substring(startPos, endPos);

				saveFile = f.getAbsolutePath();
				shortFileName = f.getName();
				url = f.toURL(); // new URL(saveFile);
				this.loadXmlFile(saveFile);
				List<org.jdom.Element> fileElements = this.getFiles();
				if (fileElements.size() > 0)
				{
					fileElement = this.getFiles().get(0);
					bodyElement = this.getBody(fileElement);
					transUnits = this.getTransUnitList(bodyElement);
				}
				return file;
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * @return the fileElement
	 */
	public org.jdom.Element getFileElement()
	{
		return fileElement;
	}

	/**
	 * getFormatIdBased get the format for a specific 0 based on the id
	 * 
	 * @param id
	 *            the trans-unit id
	 * @param iFormatNumber
	 *            the format number
	 * @return the format associated with the id and iFormatNumber
	 */
	public String getFormatIdBased(String id, String iFormatNumber)
	{
		return formatSegmentIdBasedHashtable.get(id + OTAG + iFormatNumber + CTAG);
	}

	/**
	 * getFormatIdBasedAsHiddenInput get the input format as hidden fields
	 * sorted by id
	 * 
	 * @return
	 */
	public String getFormatIdBasedAsHiddenInput()
	{
		String hiddeninputFields = "";
		Enumeration<String> enummy = formatSegmentIdBasedHashtable.keys();
		while (enummy.hasMoreElements())
		{
			String key = enummy.nextElement();
			String value = formatSegmentIdBasedHashtable.get(key);
			if (value == null)
				continue;
			value = de.folt.util.OpenTMSSupportFunctions.encodeBASE64(value);
			hiddeninputFields = hiddeninputFields + "<input id=\"" + key + "\" value=\"" + value + "\" type=\"hidden\" />";
		}

		return hiddeninputFields;
	}

	/**
	 * getFormatSegmentNumberBased get the format for a specific 0 based segment
	 * number
	 * 
	 * @param iSegmentNumber
	 *            the segment number
	 * @param iFormatNumber
	 *            the format number
	 * @return the format associated with the iSegmentNumber and iFormatNumber
	 */
	public String getFormatSegmentNumberBased(String iSegmentNumber, String iFormatNumber)
	{
		return formatSegmentNumberBasedHashtable.get(iSegmentNumber + OTAG + iFormatNumber + CTAG);
	}

	/**
	 * getFormatSegmentNumberBasedAsHiddenInput get the input format as hidden
	 * fields sorted by segment number
	 * 
	 * @return
	 */
	public String getFormatSegmentNumberBasedAsHiddenInput()
	{
		String hiddeninputFields = "";
		Enumeration<String> enummy = formatSegmentNumberBasedHashtable.keys();
		while (enummy.hasMoreElements())
		{
			String key = enummy.nextElement();
			String value = formatSegmentNumberBasedHashtable.get(key);
			if (value == null)
				continue;
			value = de.folt.util.OpenTMSSupportFunctions.encodeBASE64(value);
			hiddeninputFields = hiddeninputFields + "<input id=\"" + key + "\" value=\"" + value + "\" type=\"hidden\" />";
		}

		return hiddeninputFields;
	}

	/**
	 * getFormatTag
	 * 
	 * @param iSegmentNumber
	 * @param iFormatNumber
	 * @return
	 */
	public String getFormatTag(int iSegmentNumber, int iFormatNumber)
	{
		return getFormatSegmentNumberBased(iSegmentNumber + "", iFormatNumber + "");
	}

	/**
	 * getFuzzyMatchStatus returns "yes" if only real fuzzy matches, otherwise
	 * "no" means exact match
	 * 
	 * @param iNumber
	 *            the segment number
	 * @return yes or no
	 */
	@SuppressWarnings("unchecked")
	public String getFuzzyMatchStatus(int iNumber)
	{
		String isIsFuzzy = "yes";
		try
		{
			org.jdom.Element transUnit = transUnits.get(iNumber);
			List<org.jdom.Element> altTransChildren = transUnit.getChildren("alt-trans");
			if ((altTransChildren == null) || (altTransChildren.size() == 0))
				return "empty";
			for (int i = 0; i < altTransChildren.size(); i++)
			{
				org.jdom.Element cAltTrans = altTransChildren.get(i);
				String similarity = cAltTrans.getAttributeValue("match-quality");
				if ((similarity != null) && similarity.equals("100"))
					return "no";
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return isIsFuzzy;
	}

	/**
	 * @return the iTransUnitCounter
	 */
	public int getiTransUnitCounter()
	{
		return iTransUnitCounter;
	}

	/**
	 * @return the request
	 */
	public HttpServletRequest getRequest()
	{
		return request;
	}

	/**
	 * getSaveFile
	 * 
	 * @return
	 */
	public String getSaveFile()
	{
		if (saveFile == null)
			return "";
		return saveFile;
	}

	/**
	 * @return the shortFileName
	 */
	public String getShortFileName()
	{
		return shortFileName;
	}

	/**
	 * getShortName
	 * 
	 * @return
	 */
	public String getShortName()
	{
		if (shortFileName == null)
			return "";
		return shortFileName;
	}

	/**
	 * getSource
	 * 
	 * @param iNumber
	 * @return
	 */
	public String getSource(int iNumber)
	{
		org.jdom.Element transUnit = transUnits.get(iNumber);
		Element source = transUnit.getChild("source", this.getNamespace());
		String id = transUnit.getAttributeValue("id");
		String sourceString = this.elementContentToString(source);
		sourceString = convertXliffStringToHtml(sourceString, "displayXliffFormatValue(this);", iNumber + "", id);
		return sourceString;
	}

	/**
	 * getSourceWithDiv get the source html formatted with a div surrounding the
	 * formats
	 * 
	 * @param iNumber
	 * @return
	 */
	public String getSourceWithDiv(int iNumber)
	{
		org.jdom.Element transUnit = transUnits.get(iNumber);
		Element source = transUnit.getChild("source", this.getNamespace());
		String id = transUnit.getAttributeValue("id");
		String sourceString = this.elementContentToString(source);
		sourceString = convertXliffStringToHtml(sourceString, "displayXliffFormatValue(this);", iNumber + "", id, true);
		return sourceString;
	}

	/**
	 * @return the transUnits
	 */
	public List<org.jdom.Element> getTransUnits()
	{
		return transUnits;
	}

	/**
	 * getUrl
	 * 
	 * @return
	 */
	public String getUrl()
	{
		if (url == null)
			return "";
		return url.toExternalForm();
	}

	/**
	 * getXliffContent
	 * 
	 * @return
	 */
	public String getXliffContent()
	{
		return xliffContent;
	}

	/**
	 * readXliffFile
	 * 
	 * @param request
	 * @param directory
	 * @return
	 */
	public String readXliffFile(HttpServletRequest request, String directory)
	{
		xliffContent = getFileContents(request, directory);
		// xliffContent = xliffContent.replaceAll("<", "&lt;");
		// xliffContent = xliffContent.replaceAll(">", "&gt;");
		// xliffDocument = new
		// de.folt.models.documentmodel.xliff.XliffDocument(saveFile);
		return xliffContent;
	}

	/**
	 * reInsertFormatInformation reinsert the format information into the xliff
	 * string
	 * 
	 * @param xliffString
	 *            the xliff string with encoded format <<n>>
	 * @return the xliff string with the decoded formats <<n>> -->
	 *         <ph..>...</ph>
	 */
	public String reInsertFormatInformation(String xliffString)
	{
		String modifiedXliffString = xliffString;
		String formatMarkupPattern = "(" + OTAG + ".*?" + CTAG + ")";
		Pattern p = Pattern.compile(formatMarkupPattern, Pattern.DOTALL | Pattern.MULTILINE);

		String transUnitPattern = "<trans-unit.*?id=\"(.*?)\".*?>.*?</trans-unit>";
		Pattern pt = Pattern.compile(transUnitPattern, Pattern.DOTALL | Pattern.MULTILINE);
		Matcher mt = pt.matcher(xliffString);
		System.out.println("xliffString:\n" + xliffString + "\n");
		System.out.println("formatMarkupPattern: " + formatMarkupPattern + "\n");
		while (mt.find())
		{
			String id = mt.group(1);
			System.out.println("id" + id);
			String alttrans = mt.group();
			String origMatch = mt.group();
			Matcher m = p.matcher(alttrans);
			System.out.println("alttrans" + alttrans);
			while (m.find())
			{
				String match = m.group(1);
				String matchForTable = id + match;
				String matchReplacement = formatSegmentIdBasedHashtable.get(matchForTable);
				if (matchReplacement != null)
				{
					System.out.println("matchForTable found" + matchForTable + " : " + matchReplacement);
					alttrans = alttrans.replaceAll(Pattern.quote(match), matchReplacement);
				}
				else
				{
					System.out.println("matchForTable not found" + matchForTable);
				}
				m = p.matcher(alttrans);
			}
			modifiedXliffString = modifiedXliffString.replaceFirst(Pattern.quote(origMatch), alttrans);
		}
		return modifiedXliffString;
	}

	/**
	 * reInsertFormatInformation
	 * 
	 * @param xliffString
	 * @param id
	 * @return
	 */
	public String reInsertFormatInformation(String xliffString, String id)
	{
		String modifiedXliffString = xliffString;
		String formatMarkupPattern = "(" + OTAG + ".*?" + CTAG + ")";
		Pattern p = Pattern.compile(formatMarkupPattern, Pattern.DOTALL | Pattern.MULTILINE);
		Matcher mt = p.matcher(xliffString);
		System.out.println("xliffString:\n" + xliffString);
		System.out.println("formatMarkupPattern: " + formatMarkupPattern);
		while (mt.find())
		{
			String match = mt.group(1);
			String matchForTable = id + match;
			String matchReplacement = formatSegmentIdBasedHashtable.get(matchForTable);
			if (matchReplacement != null)
			{
				System.out.println("matchForTable found " + matchForTable + " : " + matchReplacement);
				modifiedXliffString = modifiedXliffString.replaceAll(Pattern.quote(match), matchReplacement);
			}
			else
			{
				System.out.println("matchForTable not found " + matchForTable);
			}

		}
		return modifiedXliffString;
	}

	private String replaceWithTextMarkers(String xliffString, String javaScript, String iNumber, String id, boolean bDiv)
	{
		String newString = "";
		int i = 0;
		for (int j = 0; j < xliffString.length(); j++)
		{
			if (xliffString.charAt(j) == '<')
			{
				// search next > and /
				for (int k = j + 1; k < xliffString.length(); k++)
				{
					if (xliffString.charAt(k) == '>')
					{
						int iEnd = xliffString.indexOf(">", k + 1);
						String match = xliffString.substring(j, iEnd + 1);
						String matchTitle = match.replaceAll("<", OTAG); // "&lt;");
						matchTitle = matchTitle.replaceAll(">", CTAG); // "&gt;");
						matchTitle = matchTitle.replaceAll("\"", "&quot;"); // "&quot;");
						// String matchReplacement =
						// de.folt.util.OpenTMSSupportFunctions
						// .encodeBASE64(match);
						// String replacement = "<a href=\"#\" id=\"A" + i +
						// "\" name=\"Coded:"
						// + matchReplacement + "\" onclick=\"" + javaScript +
						// "\" title=\""
						// + matchTitle + "\">/" + i + "/</a>";
						String formatMarkup = OTAG + i + CTAG;
						if (bDiv)
						{
							String mouseAction = "onmouseover=\"this.innerHTML = '" + matchTitle + "'\"";
							mouseAction = mouseAction + " onmouseout=\"this.innerHTML = '" + formatMarkup + "'\"";
							formatMarkup = "<span id=\"" + iNumber + OTAG + i + CTAG + "\" class=\"format\" " + mouseAction + ">" + formatMarkup
									+ "</span>";
						}
						newString = newString + formatMarkup;
						formatSegmentNumberBasedHashtable.put(iNumber + OTAG + i + CTAG, match);
						formatSegmentIdBasedHashtable.put(id + OTAG + i + CTAG, match);
						i++;
						j = iEnd;
						break;
					}
					else if (xliffString.charAt(k) == '/')
					{
						int iEnd = xliffString.indexOf(">", k + 1);
						String match = xliffString.substring(j, iEnd + 1);
						String matchTitle = match.replaceAll("<", OTAG); // "&lt;");
						matchTitle = matchTitle.replaceAll(">", CTAG); // "&gt;");
						matchTitle = matchTitle.replaceAll("\"", "&quot;"); // "&quot;");
						// String matchReplacement =
						// de.folt.util.OpenTMSSupportFunctions
						// .encodeBASE64(match);
						// String replacement = "<a href=\"#\" id=\"A" + i +
						// "\" name=\"Coded:"
						// + matchReplacement + "\" onclick=\"" + javaScript +
						// "\" title=\""
						// + matchTitle + "\">/" + i + "/</a>";
						String formatMarkup = OTAG + i + CTAG;
						if (bDiv)
						{
							String mouseAction = "onmouseover=\"this.innerHTML = '" + matchTitle + "'";
							mouseAction = mouseAction + " onmouseout=\"this.innerHTML = '" + formatMarkup + "'";
							formatMarkup = "<span id=\"" + iNumber + OTAG + i + CTAG + "\" class=\"format\" " + mouseAction + ">" + formatMarkup
									+ "</span>";
						}
						newString = newString + formatMarkup;
						formatSegmentNumberBasedHashtable.put(iNumber + OTAG + i + CTAG, match);
						formatSegmentIdBasedHashtable.put(id + OTAG + i + CTAG, match);
						i++;
						j = iEnd;
						break;
					}
				}
			}
			else
			{
				newString = newString + xliffString.charAt(j);
			}
		}
		return newString;
	}

	/**
	 * @param bodyElement
	 *            the bodyElement to set
	 */
	public void setBodyElement(org.jdom.Element bodyElement)
	{
		this.bodyElement = bodyElement;
	}

	/**
	 * @param directory
	 *            the directory to set
	 */
	public void setDirectory(String directory)
	{
		this.directory = directory;
	}

	/**
	 * @param fileElement
	 *            the fileElement to set
	 */
	public void setFileElement(org.jdom.Element fileElement)
	{
		this.fileElement = fileElement;
	}

	/**
	 * @param iTransUnitCounter
	 *            the iTransUnitCounter to set
	 */
	public void setiTransUnitCounter(int iTransUnitCounter)
	{
		this.iTransUnitCounter = iTransUnitCounter;
	}

	/**
	 * @param request
	 *            the request to set
	 */
	public void setRequest(HttpServletRequest request)
	{
		this.request = request;
	}

	/**
	 * @param saveFile
	 *            the saveFile to set
	 */
	public void setSaveFile(String saveFile)
	{
		this.saveFile = saveFile;
	}

	/**
	 * @param shortFileName
	 *            the shortFileName to set
	 */
	public void setShortFileName(String shortFileName)
	{
		this.shortFileName = shortFileName;
	}

	/**
	 * @param transUnits
	 *            the transUnits to set
	 */
	public void setTransUnits(List<org.jdom.Element> transUnits)
	{
		this.transUnits = transUnits;
	}

	/**
	 * @param url
	 *            the url to set
	 */
	public void setUrl(URL url)
	{
		this.url = url;
	}

	/**
	 * @param xliffContent
	 *            the xliffContent to set
	 */
	public void setXliffContent(String xliffContent)
	{
		this.xliffContent = xliffContent;
	}
}
