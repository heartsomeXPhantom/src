package net.docliff.segmenter;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom.Element;
import org.jdom.EntityRef;
import org.jdom.Text;

import de.folt.util.StringUtil;

// import com.sun.transtech.alignment.Segment;

class ASegment implements Segment
{
	public static String cleanString(String input)
	{
		StringTokenizer tok = new StringTokenizer(input, "&<>", true);
		StringBuffer buff = new StringBuffer();
		while (tok.hasMoreElements())
		{
			String str = tok.nextToken();
			if (str.equals("&"))
			{
				buff.append("&amp;");
			}
			else if (str.equals("<"))
			{
				buff.append("&lt;");
			}
			else if (str.equals(">"))
			{
				buff.append("&gt;");
			}
			else
			{
				buff.append(str);
			}
		}
		input = buff.toString();
		tok = null;
		buff = null;
		return input;

	} // end cleanString

	private int hardBoundaryLevel = Segment.NOTBOUNDARY;

	private String segmentString = null;

	private String formattedSegmentString = null;

	private ArrayList retTmp = new ArrayList();

	private String hardBoundaryTagName = null;

	private String datatype = "";

	private String id = "";

	private List words = null;

	private List nonwords = null;

	private List numbers = null;

	public ASegment(List list, String datatype, String transUnit)
	{
		// For araya we do not consider mrk as inline tag
		// because it is just for protecting space, other tag at the
		// begining and trailing of segment.
		this.datatype = datatype;
		id = transUnit.replaceAll(".*id=\"(.*?)\".*", "$1");
		StringBuffer buff = new StringBuffer();
		Iterator it = list.iterator();

		while (it.hasNext())
		{
			Object obj = it.next();
			// <seg>Text in <bpt i="1" x="1">&lt;B></bpt>bold<ept i="1">&lt;/B></ept> and <bpt i="2" x="2">&lt;I></bpt>italic<ept i="2">&lt;/I></ept>.</seg>
			if (obj instanceof Element)
			{
				String elemName = ((Element) obj).getName();
				String text = cleanString(((Element) obj).getText());
				if (elemName.equals("ph"))
				{
					String ctype = ((Element) obj).getAttributeValue("ctype");
					buff.append("<ph");
					if (ctype != null)
					{
						buff.append(" type=\"" + ctype + "\"");
					}
					if (text.equals(""))
					{
						buff.append(" />");
					}
					else
					{
						buff.append(">");
						buff.append(text);
						buff.append("</ph>");
					}
				}
				else if (elemName.equals("it"))
				{
					String pos = ((Element) obj).getAttributeValue("pos");
					String ctype = ((Element) obj).getAttributeValue("ctype");
					buff.append("<it");
					if (pos != null)
					{
						buff.append(" pos=\"" + (pos.equals("open") ? "begin" : "end") + "\"");
					}
					if (ctype != null)
					{
						buff.append(" type=\"" + ctype + "\"");
					}
					if (text.equals(""))
					{
						buff.append(" />");
					}
					else
					{
						buff.append(">");
						buff.append(text);
						buff.append("</it>");
					}
				}
				else if (elemName.equals("bpt"))
				{
					String ctype = ((Element) obj).getAttributeValue("ctype");
					String id = ((Element) obj).getAttributeValue("id");
					buff.append("<bpt");
					if (ctype != null)
					{
						buff.append(" type=\"" + ctype + "\"");
					}
					if (id != null)
					{
						buff.append(" i=\"" + id + "\"");
					}
					if (text.equals(""))
					{
						buff.append(" />");
					}
					else
					{
						buff.append(">");
						buff.append(text);
						buff.append("</bpt>");
					}
				}
				else if (elemName.equals("ept"))
				{
					String id = ((Element) obj).getAttributeValue("id");
					buff.append("<ept");
					if (id != null)
					{
						buff.append(" i=\"" + id + "\"");
					}
					if (text.equals(""))
					{
						buff.append(" />");
					}
					else
					{
						buff.append(">");
						buff.append(text);
						buff.append("</ept>");
					}
				}
			}
			else if (obj instanceof EntityRef)
			{
				String entname = ((EntityRef) obj).getName();
				buff.append("&" + entname + ";");
			}
			else if (obj instanceof Text)
			{
				String txt = cleanString(((Text) obj).getText());
				buff.append(txt);
			}
		}
		segmentString = buff.toString();
		formattedSegmentString = segmentString;
		// Remove inline tag in segmentString
		// but still keep inline tags for formattedSegmentString
		Pattern formattagpattern = Pattern
				.compile("<mrk.*?>.*?<\\/mrk>|<ept.*?>.*?<\\/ept>|<bpt.*?>.*?<\\/bpt>|<x.*?>.*?<\\/x>|<it.*?>.*?<\\/it>|<ph.*?>.*?<\\/ph>|<.*?>");
		Matcher m = formattagpattern.matcher(segmentString);
		segmentString = m.replaceAll("");

		// wk 20.02.2007
		if ((formattedSegmentString.length() > 0) && (segmentString.length() == 0))
			segmentString = "DUMMYSEGMENT";
		hardBoundaryLevel = 0;
		// Here we split the text to be 3 kind of type
		// 1. Words
		// 2. Non-Words
		// 3. Numbers
		Hashtable hash = StringUtil.getTypeWords(segmentString);
		words = (List) hash.get("words");
		nonwords = (List) hash.get("nonwords");
		numbers = (List) hash.get("numbers");
	}

	public ASegment(String transUnit, int boundary, String datatype)
	{
		segmentString = "";
		words = new Vector();
		nonwords = new Vector();
		numbers = new Vector();
		formattedSegmentString = transUnit;
		id = transUnit.replaceAll(".*id=\"(.*?)\".*", "$1");
		hardBoundaryLevel = boundary;
		this.datatype = datatype;
	}

	public ASegment(String transUnit, int boundary, String tagName, String datatype)
	{
		this(transUnit, boundary, datatype);
		hardBoundaryTagName = tagName;
		this.datatype = datatype;
	}

	public String getDocumentFormat()
	{
		return datatype;
	}

	public String getFormattedSegmentString()
	{
		return formattedSegmentString;
	}

	public List getFormatting()
	{
		List formatlist = new Vector();
		StringBuffer strformat = new StringBuffer();
		Pattern formattagpattern = Pattern
				.compile("<mrk.*?>.*?<\\/mrk>|<ept.*?>.*?<\\/ept>|<bpt.*?>.*?<\\/bpt>|<x.*?>.*?<\\/x>|<it.*?>.*?<\\/it>|<ph.*?>.*?<\\/ph>|<.*?>");
		Matcher m = formattagpattern.matcher(formattedSegmentString);
		boolean result = m.find();
		while (result)
		{
			String group = m.group();
			strformat.append(group);
			formatlist.add(group);
			result = m.find();
		}
		List retlist = new Vector();
		retlist.add(strformat.toString());
		retlist.add(formatlist);
		return retlist;
		// ArrayList formattingList = new ArrayList();
		// String str = segmentString;
		// try {
		// int index = 0;
		// int mode = 0;
		// String node = "";
		// boolean inChildNode = false;
		// while (index < str.length()) {
		// switch (mode) {
		// case 0: // read text
		// int tagbegin = str.indexOf("<", index);
		// if (tagbegin < 0)
		// tagbegin = str.length();
		// String text = str.substring(index, tagbegin);
		// if (inChildNode) {
		// node+=text;
		// }
		// mode = 1;
		// index = tagbegin;
		// break;
		// case 1: // read PI target
		// int startpos = index;
		// int endpos = str.indexOf(">", startpos);
		// String tag = str.substring(startpos, endpos+1);
		// if (inChildNode) {
		// inChildNode = false;
		// node+=tag;
		// formattingList.add(node);
		// node = null;
		// } else if (str.charAt(endpos - 1) == '/' ||
		// tag.startsWith("<trans-unit") ||
		// tag.startsWith("</trans-unit") ||
		// tag.startsWith("<source") ||
		// tag.startsWith("</source")) {
		// inChildNode = false;
		// node = tag;
		// formattingList.add(node);
		// node = null;
		// } else {
		// inChildNode = true;
		// node = tag;
		// }
		// mode = 0;
		// index = endpos + 1;
		// tag = null;
		// break;
		// }
		// }
		// } catch (StringIndexOutOfBoundsException exc) {
		// System.out.println("ERROR : "+str);
		// }
		// str = null;
		// return formattingList;
	}

	/**
	 * @see com.sun.transtech.alignment.Segment#getHardBoundaryLevel()
	 */
	public int getHardBoundaryLevel()
	{
		return hardBoundaryLevel;
	}

	public String getHardboundaryTagName()
	{
		return hardBoundaryTagName;
	}

	/**
	 * @return Returns the id.
	 */
	public String getId()
	{
		return id;
	}

	public List getNonWords()
	{
		return nonwords;
	}

	public List getNumbers()
	{
		return numbers;
	}

	public String getSegmentString()
	{
		return segmentString;
		// String str = segmentString;
		// String result = "";
		// try {
		// int index = 0;
		// int mode = 0;
		// boolean inFormat = false;
		// while (index < str.length()) {
		// switch (mode) {
		// case 0: // read text
		// int tagbegin = str.indexOf("<", index);
		// if (tagbegin < 0)
		// tagbegin = str.length();
		// String text = str.substring(index, tagbegin);
		// if (!inFormat) {
		// result+=text;
		// }
		// mode = 1;
		// index = tagbegin;
		// break;
		// case 1: // read PI target
		// int startpos = index;
		// int endpos = str.indexOf(">", startpos);
		// String tag = str.substring(startpos, endpos+1);
		// if (inFormat) {
		// inFormat = false;
		// } else if (str.charAt(endpos - 1) == '/' ||
		// tag.startsWith("<trans-unit") ||
		// tag.startsWith("</trans-unit") ||
		// tag.startsWith("<source") ||
		// tag.startsWith("</source")) {
		// inFormat = false;
		// } else {
		// inFormat = true;
		// }
		// mode = 0;
		// index = endpos + 1;
		// break;
		// }
		// }
		// } catch (StringIndexOutOfBoundsException exc) {
		// System.out.println("ERROR : "+str);
		// }
		// str = null;
		// return result;
	}

	public List getWords()
	{
		return words;
	}

	public boolean isHardBoundary()
	{
		return hardBoundaryLevel > 0 ? true : false;
	}

	public void setFormattedSegmentString(String string)
	{
		formattedSegmentString = string;
	}

	/**
	 * Method setHardBoundaryLevel.
	 * 
	 * @param boundary
	 */
	protected void setHardBoundaryLevel(int boundary)
	{
		hardBoundaryLevel = boundary;
	}

	/**
	 * @param id
	 *            The id to set.
	 */
	public void setId(String id)
	{
		this.id = id;
	}

	public void setSegmentString(String string)
	{
		Pattern formattagpattern = Pattern
				.compile("<mrk.*?>.*?<\\/mrk>|<ept.*?>.*?<\\/ept>|<bpt.*?>.*?<\\/bpt>|<x.*?>.*?<\\/x>|<it.*?>.*?<\\/it>|<ph.*?>.*?<\\/ph>|<.*?>");
		Matcher m = formattagpattern.matcher(string);
		segmentString = m.replaceAll("");
	}
}