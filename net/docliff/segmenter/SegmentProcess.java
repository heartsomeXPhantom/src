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
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Vector;

import org.jdom.CDATA;
import org.jdom.Comment;
import org.jdom.Element;
import org.jdom.EntityRef;
import org.jdom.Namespace;
import org.jdom.ProcessingInstruction;
import org.jdom.Text;

import de.folt.models.documentmodel.xliff.XliffDocument;
import de.folt.util.OpenTMSException;
import de.folt.util.StringUtil;

public class SegmentProcess
{
	private XliffDocument doc = null;

	private String outFileName = null;

	private SegmentRule segRule = null;

	private Element currentSource = null;

	private String srcLanguage = null;

	private boolean enableSegmentation;

	private int totalParagraph = 1;

	private int breakOnCrlf = 0;

	public SegmentProcess(XliffDocument doc, String iniFileName, boolean enableSegRule, int breakOnCrlf) throws OpenTMSException
	{
		this.doc = doc;
		String datatype = "xliff";
		enableSegmentation = enableSegRule;

		segRule = new SegmentRule(iniFileName);
		this.breakOnCrlf = 0;

	}

	public SegmentProcess(XliffDocument doc, String iniFileName) throws OpenTMSException
	{
		this(doc, iniFileName, true, 0);
	}

	public void setXliffDocument(XliffDocument doc)
	{
		this.doc = doc;
	}

	private boolean isWhiteSpace(String str)
	{
		char[] chars = str.toCharArray();
		for (int i = 0; i < chars.length; i++)
		{
			if (chars[i] != '\n' && chars[i] != '\r' && chars[i] != '\t' && chars[i] != ' ' && chars[i] != '\u00a0')
			{
				return false;
			}
		}
		return true;
	}

	private int idsequence = 1;

	private Element createNewSegment(Element body, String id, String srcLan, String extradata, String boundary, String dataType)
	{
		Namespace namespace = Namespace.XML_NAMESPACE;
		; // Namespace.getNamespace("xml", "");
		String lang = srcLan == null ? srcLanguage : srcLan;
		Element newTransUnit = new Element("trans-unit");
		if (dataType != null)
		{
			if (dataType.equals("plaintext") || dataType.equals("rtf") || dataType.equals("xml"))
			{
				newTransUnit.setAttribute("space", "preserve", namespace);
			}
		}
		newTransUnit.setAttribute("id", id);

		newTransUnit.setAttribute("help-id", totalParagraph + "");

		if (extradata != null && !extradata.equals(""))
			newTransUnit.setAttribute("extradata", extradata);
		newTransUnit.setAttribute("ts", boundary);
		Element source = new Element("source");
		source.setAttribute("lang", srcLan, namespace);
		newTransUnit.addContent(source);
		body.addContent(newTransUnit);
		return source;
	}

	// private int getWhitSpaceLeading(String data) {
	// int numberOfWhiteSpace=0;
	// int len = data.length();
	// for (numberOfWhiteSpace=0;numberOfWhiteSpace<len;numberOfWhiteSpace++) {
	// char ch = data.charAt(numberOfWhiteSpace);
	// if (ch != '\n' &&
	// ch != '\r' &&
	// ch != '\t' &&
	// ch != ' ' &&
	// ch != '\u00A0') {
	// break;
	// }
	// }
	// return numberOfWhiteSpace;
	// }
	// currBptHash to keep id of current segment formatting
	// allBptHash to keep id for all
	private Hashtable currBptHash;

	private Hashtable allBptHash;

	private String SEGMENTER = "beo logisch Segmenter";

	private String SEGMENTER_VERSION = "1.0";

	private Element formattingHandling(Element source)
	{
		String id = "";
		Object node = null;
		Iterator nodes_it = source.getContent().iterator();
		while (nodes_it.hasNext())
		{
			node = nodes_it.next();
			if (node instanceof Element)
			{
				String nodeName = ((Element) node).getName();
				if (nodeName.equals("bpt"))
				{
					id = ((Element) node).getAttributeValue("id");
					currBptHash.put(id, node);
					allBptHash.put(id, node);
				}
				else if (nodeName.equals("ept"))
				{
					id = ((Element) node).getAttributeValue("id");
					Element tmpNode = (Element) currBptHash.get(id);
					if (tmpNode == null)
					{
						tmpNode = (Element) allBptHash.get(id);
						if (tmpNode == null)
						{
							System.out.println("There is ept without bpt " + id);
						}
						else
						{
							String tmpId = ((Element) tmpNode).getAttributeValue("id");
							String tmpCtype = ((Element) tmpNode).getAttributeValue("ctype");

							Element it = (Element) node;
							it.setName("it");
							it.setAttribute("ctype", tmpCtype);
							it.setAttribute("id", tmpId);
							it.setAttribute("pos", "close");
						}
					}
					else
					{
						currBptHash.remove(id);
					}
				}
			}
		}
		if (currBptHash.size() != 0)
		{
			Enumeration enum1 = currBptHash.elements();
			while (enum1.hasMoreElements())
			{
				Element bpt = (Element) enum1.nextElement();
				String txt = bpt.getText();
				String tmpId = bpt.getAttributeValue("id");
				String tmpCtype = bpt.getAttributeValue("ctype");
				Element it = bpt;
				it.setName("it");
				it.setAttribute("ctype", tmpCtype);
				it.setAttribute("id", tmpId);
				it.setAttribute("pos", "open");
			}
			currBptHash.clear();
		}
		return source;
	}

	private void beginEpt2trailEpt(Element body)
	{
		List trans_unitList = body.getChildren("trans-unit");
		int count = trans_unitList.size();
		for (int i = 0; i < count; i++)
		{
			Element transUnit = (Element) trans_unitList.get(i);
			Element source = transUnit.getChild("source");
			Element previousTransUnit = i == 0 ? null : (Element) trans_unitList.get(i - 1);
			if (previousTransUnit != null)
			{
				List nodeList = source.getContent();
				int len = nodeList.size();
				int lastEpt = -1;
				for (int n = 0; n < len; n++)
				{
					Object node = nodeList.get(n);
					if (node instanceof Element)
					{
						String nodeName = ((Element) node).getName();
						if (nodeName.equals("ept"))
						{
							lastEpt = n;
						}
						else if (nodeName.equals("bpt"))
						{
							break;
						}
					}
					else if (node instanceof Text)
					{
						String str = ((Text) node).getText();
						if (!StringUtil.isUntranslatedText(str))
						{
							break;
						}
					}
				}
				Element previousSource = previousTransUnit.getChild("source");
				int n = 0;
				while (n <= lastEpt)
				{
					// Object node = DOMUtil.getFirstChild(source);
					Object node = nodeList.get(0);
					addContent(previousSource, node);
					// DOMUtil.removeContent(source, node);
					nodeList.remove(0);
					n++;
				}
			}
		}
		trans_unitList = null;
	}

	public static void addContent(Element elem, Object obj)
	{
		if (obj instanceof Element)
			elem.addContent((Element) obj);
		else if (obj instanceof Text)
			elem.addContent((Text) obj);
		else if (obj instanceof EntityRef)
			elem.addContent((EntityRef) obj);
		else if (obj instanceof CDATA)
			elem.addContent((CDATA) obj);
		else if (obj instanceof Comment)
			elem.addContent((Comment) obj);
		else if (obj instanceof ProcessingInstruction)
			elem.addContent((ProcessingInstruction) obj);
	}

	public static Object clone(Object obj)
	{
		Object cloneObj = null;
		if (obj instanceof Element)
			cloneObj = ((Element) obj).clone();
		else if (obj instanceof Text)
			cloneObj = ((Text) obj).clone();
		else if (obj instanceof EntityRef)
			cloneObj = ((EntityRef) obj).clone();
		else if (obj instanceof CDATA)
			cloneObj = ((CDATA) obj).clone();
		else if (obj instanceof Comment)
			cloneObj = ((Comment) obj).clone();
		else if (obj instanceof ProcessingInstruction)
			cloneObj = ((ProcessingInstruction) obj).clone();
		return cloneObj;
	}

	private boolean markAtPreviousTrailing(List list, int curIndex, Element transUnit, Element source)
	{
		Element previousTransUnit = curIndex > 0 ? (Element) list.get(curIndex - 1) : null;
		if (previousTransUnit != null)
		{
			List nodeList = source.getContent();
			int len = nodeList.size();
			int lastEpt = -1;
			// To complete format tags here move bpt/ept from
			// untransted segment to previous segment and
			// enclose ph and whitespace with mrk node and then move to previous
			// Example :
			// <trans-unit><source><bpt>bold</bpt>Bold Text</source></trans-unit>
			// <trans-unit><source><ept></ept> </source></trans-unit>
			// Result : <trans-unit><source><bpt>bold</bpt>Bold Text<ept></ept><mrk> </mrk></source></trans-unit>
			for (int i = 0; i < len; i++)
			{
				Object node = nodeList.get(i);
				if (node instanceof Element)
				{
					String nodeName = ((Element) node).getName();
					if (nodeName.equals("ept"))
					{
						lastEpt = i;
					}
					else if (nodeName.equals("bpt"))
					{
						break;
					}
				}
			}
			Element previousSource = previousTransUnit.getChild("source");
			StringBuffer text = new StringBuffer();
			Element mrk = previousSource.getChild("mrk");
			boolean bNewMark = false;
			if (mrk == null)
			{
				mrk = new Element("mrk");
				bNewMark = true;
			}
			for (int i = 0; i < len; i++)
			{
				Object node = nodeList.get(i);
				if (i <= lastEpt)
				{
					addContent(previousSource, clone(node));
				}
				else
				{
					if (node instanceof Text)
					{
						// text.append(((Text) node).getText());
						mrk.addContent(new Text(((Text) node).getText()));
					}
					else if (node instanceof Element)
					{
						// text.append(DOMUtil.node2string((Element) node));
						mrk.addContent((Element) ((Element) node).clone());
					}
					else if (node instanceof EntityRef)
					{
						String entname = ((EntityRef) node).getName();
						if (entname.equals("apos"))
						{
							// text.append('\'');
							mrk.addContent("'");
						}
						else if (entname.equals("gt"))
						{
							// text.append('>');
							mrk.addContent(">");
						}
						else if (entname.equals("lt"))
						{
							// text.append('<');
							mrk.addContent("<");
						}
						else if (entname.equals("quot"))
						{
							// text.append('"');
							mrk.addContent("\"");
						}
						else if (entname.equals("amp"))
						{
							// text.append('&');
							mrk.addContent("&");
						}
					}
				}
			}
			if (bNewMark && mrk.getContent().size() > 0)
			{
				mrk.setAttribute("mtype", "protected");
				// mrk.addContent(text.toString());
				previousSource.addContent(mrk);
			}
			return true;
		}

		previousTransUnit = null;
		return false;
	}

	private void noneSegment2trail(Element body)
	{
		List transUnitList = body.getChildren("trans-unit");
		int count = transUnitList.size();
		for (int i = 0; i < count; i++)
		{
			Element curTransUnit = (Element) transUnitList.get(i);
			Element source = curTransUnit.getChild("source");
			if (source != null)
			{
				List nodeList = source.getContent();
				Iterator it = nodeList.iterator();
				Object node = it.hasNext() ? it.next() : null;
				if (node == null)
				{
					String id = curTransUnit.getAttributeValue("id");
					if (!id.equals("s") && !id.startsWith("crlf-"))
					{
						System.err.println("Warning : There is empty segment id " + id);
					}
					transUnitList.remove(i--);
					count--;
					// body.removeContent(curTransUnit);
				}
				else
				{
					boolean isSegment = false;
					// Here we check if the segment is empty then remove it.
					boolean bContainTag = false;
					boolean bEmptyStr = true;
					// ------------------------------------------------

					// None segment is segment that contain only inline tags and white space.
					while (node != null && !isSegment)
					{
						if (node instanceof Text)
						{
							String data = ((Text) node).getText();
							if (bEmptyStr && !data.equals(""))
							{
								bEmptyStr = false;
							}
							// isSegment = !isWhiteSpace(data);
							isSegment = !StringUtil.isUntranslatedText(data);
						}
						else
						{
							bContainTag = true;
						}
						node = it.hasNext() ? it.next() : null;
					}
					if (!isSegment)
					{
						// if it is none segment then mark as trailing at previous segment
						if (!bContainTag && bEmptyStr)
						{
							transUnitList.remove(i--);
							count--;
						}
						else
						{
							if (markAtPreviousTrailing(transUnitList, i, curTransUnit, source))
							{
								String id = curTransUnit.getAttributeValue("id");
								if (!id.equals("s") && !id.startsWith("crlf-"))
								{
									System.err.println("Warning : There is empty segment id " + id);
								}
								transUnitList.remove(i--);
								count--;
								// body.removeContent(curTransUnit);
							}
						}
					}
				}
			}
		}
		transUnitList = null;
	}

	private void markAtBegining(Element body)
	{
		List sourceList = body.getChildren("source");
		int count = sourceList.size();
		for (int i = 0; i < count; i++)
		{
			Element mrk = new Element("mrk");
			// if (i == 201)
			// System.out.println(i);
			int numberOfRemoved = 0;
			Element source = (Element) sourceList.get(i);
			List nodeList = source.getContent();
			int size = nodeList.size();
			String leading = "";
			String newData = "";
			Object node = null;
			for (int z = 0; z < size; z++)
			{
				node = nodeList.get(z);
				if (node instanceof Element)
				{
					String nodeName = ((Element) node).getName();
					if (nodeName.equals("bpt"))
					{
						break;
					}
					else
					{
						numberOfRemoved++;
						// leading += DOMUtil.node2string((Element) node);
						mrk.addContent((Element) ((Element) node).clone());
					}
				}
				else if (node instanceof Text)
				{
					String data = ((Text) node).getText();
					int num = StringUtil.getUntranstedTextLeading(data);
					if (num > 0)
					{
						numberOfRemoved++;
						// Keep leading space to add at the end of content of mrk element
						// leading += data.substring(0, num);
						mrk.addContent(new Text(data.substring(0, num)));
						if (num != data.length())
						{
							newData = data.substring(num);
							break;
						}
					}
					else
					{
						break;
					}
				}
			}
			// if (!leading.equals("")) {
			if (mrk.getContent().size() > 0)
			{
				// <mrk mtype="protected" />
				for (int z = 0; z < numberOfRemoved; z++)
				{
					nodeList.remove(0);
				}
				// node = nodeList.get(0);
				// If the text is cut leading space then we must put the new text instead
				// Old text " This is a book." is removed
				// New text "This is a book." is added.
				if (!newData.equals(""))
				{
					Text textNode = new Text(newData);
					nodeList.add(0, textNode);
				}
				mrk.setAttribute("mtype", "protected");
				// mrk.addContent(leading);
				nodeList.add(0, mrk);
			}
		}

		sourceList = null;
	}

	private void markAtTrailing(Element body)
	{
		List transUnitList = body.getChildren("trans-unit");
		int count = transUnitList.size();
		for (int i = 0; i < count; i++)
		{
			int numberOfRemoved = 0;
			Element source = ((Element) transUnitList.get(i)).getChild("source");
			List nodeList = source.getContent();
			int size = nodeList.size();
			String trailing = "";
			String newData = "";
			Object node = null;
			Element mrk = null;
			List mrkContent = null;
			int index = 0;
			for (int z = size - 1; z >= 0; z--)
			{
				node = nodeList.get(z);
				if (node instanceof Element)
				{
					String nodeName = ((Element) node).getName();
					if (nodeName.equals("bpt") || nodeName.equals("ept"))
					{
						break;
					}
					if (nodeName.equals("mrk"))
					{
						mrk = ((Element) node);
						mrkContent = mrk.getContent();
					}
					else
					{
						if (mrk == null)
						{
							mrk = new Element("mrk");
							mrk.setAttribute("mtype", "protected");
							mrkContent = mrk.getContent();
							source.addContent(mrk);
						}
						Element removed = (Element) nodeList.remove(z);
						numberOfRemoved++;
						// trailing += DOMUtil.node2string((Element) node);
						mrkContent.add(0, removed);
					}
				}
				else if (node instanceof Text)
				{
					String data = ((Text) node).getText();
					int num = StringUtil.getUntranstedTextTrailing(data);
					if (num > 0)
					{
						numberOfRemoved++;
						// Remove the old Text node
						nodeList.remove(z);
						// Keep trailing space to add at the end of content of mrk element
						int len = data.length();
						if (mrk == null)
						{
							mrk = new Element("mrk");
							mrk.setAttribute("mtype", "protected");
							mrkContent = mrk.getContent();
							source.addContent(mrk);
						}
						mrkContent.add(0, new Text(data.substring(len - num)));
						// trailing += data.substring(len-num);
						if (num != data.length())
						{
							newData = data.substring(0, len - num);
							// If the text is cut trailing space then we must put the new text instead
							// Old text "This is a book " is removed
							// New text "This is a book." is added.
							if (!newData.equals(""))
							{
								Text textNode = new Text(newData);
								nodeList.add(z, textNode);
								textNode = null;
							}
							break;
						}
					}
					else
					{
						break;
					}
				}
			}
			// if (!trailing.equals("")) {
			// int last = nodeList.size();
			// if (mrk != null) {
			// String cText = mrk.getText();
			// mrk.setText(trailing + cText);
			// } else {
			// Element mrkElem = new Element("mrk");
			// mrkElem.setAttribute("mtype", "protected");
			// mrkElem.addContent(trailing);
			// nodeList.add(last, mrkElem);
			// mrkElem = null;
			// }
			// }
			nodeList = null;
		}
		transUnitList = null;
	}

	private void pt2it(Element body)
	{
		List sourceList = body.getChildren("source");
		int count = sourceList.size();
		currBptHash = new Hashtable();
		allBptHash = new Hashtable();
		for (int i = 0; i < count; i++)
		{
			Element source = (Element) sourceList.get(i);
			Element node = formattingHandling(source);
		}

		sourceList = null;
		currBptHash.clear();
		allBptHash.clear();
		currBptHash = null;
		allBptHash = null;
	}

	private void segmentReorderTagID(Element body)
	{

		List transUnitList = body.getChildren("trans-unit");
		int count = transUnitList.size();
		Iterator it = transUnitList.iterator();
		Hashtable hid = new Hashtable();
		int id = 1;
		while (it.hasNext())
		{
			Element source = (Element) it.next();
			String idx = source.getAttributeValue("id");
			source.setAttribute("id", Integer.toString(id));
			idx = source.getAttributeValue("id");
			id++;
		}

		List sourceList = body.getChildren("source");
		it = sourceList.iterator();
		hid = new Hashtable();
		while (it.hasNext())
		{
			Element source = (Element) it.next();
			// source.setAttribute("id", Integer.toString(id));
			// source.removeAttribute("id");
			// id++;
			hid.clear();
			List list = source.getChildren();
			Iterator elements = list.iterator();
			while (elements.hasNext())
			{
				Element elem = (Element) elements.next();
				String strID = elem.getAttributeValue("id");
				if (strID != null && !strID.equals(""))
				{
					Integer existingID = (Integer) hid.get(strID);
					if (existingID == null)
					{
						hid.put(strID, new Integer(id));
						elem.setAttribute("id", Integer.toString(id));
						id++;
					}
					else
					{
						// elem.setAttribute("id", existingID.toString());
						elem.setAttribute("id", Integer.toString(id));
						id++;
					}
				}
			}
			source.removeAttribute("id");
		}
	}

	// wk 29.11.2003
	private void rearrangeWhitespaceSegments(Element body)
	{
		List transUnitList = body.getChildren("trans-unit");
		int count = transUnitList.size();
		int iEmpty = 0;
		for (int i = 0; i < count; i++)
		{
			int numberOfRemoved = 0;
			Element transunit = ((Element) transUnitList.get(i));
			Element source = ((Element) transUnitList.get(i)).getChild("source");
			List nodeList = source.getContent();
			int size = nodeList.size();
			String content = source.getText();
			// LogPrint.println("rearrangeWhitespaceSegments index=" + i + " \"" + content + "\"");

			if ((content.matches("[\n\r ]+")) || content.equals(""))
			{
				// now remove segment ...
				// and copy content into previous segment
				// need to get extra data too
				iEmpty++;
				String id = transunit.getAttributeValue("id");
				String ts = transunit.getAttributeValue("ts");
				if (ts.equals("sentence(0)"))
					ts = "";
				// LogPrint.println("rearrangeWhitespaceSegments index=" + i + " \"" + "empty found id=" + id + " ts=" + ts + "\"");
				if (i > 0)
				{
					Element lastsource = ((Element) transUnitList.get(i - 1)).getChild("source");
					lastsource.addContent(content);
					if (ts.equals("") == false)
					{
						// add ts now
						Element transunitold = ((Element) transUnitList.get(i - 1));
						String tsold = transunitold.getAttributeValue("ts");
						tsold = tsold + ts;
						transunitold.setAttribute("ts", tsold);
					}
					boolean brem = body.removeContent(((Element) transUnitList.get(i)));
					// LogPrint.println("brem="+brem);
					// transUnitList.remove(i--);
					count = transUnitList.size();
				}
				else if (content.equals(""))
				{
					// teh first element is empty we need to copy the next segment in
					iEmpty++;
					// LogPrint.println("rearrangeWhitespaceSegments index=" + i + " \"" + "empty (null) found id=" + id + " ts=" + ts + "\"");
					if (count >= 2)
					{
						Element currenttrans = ((Element) transUnitList.get(i));
						Element nexttrans = ((Element) transUnitList.get(i + 1));
						String tsold = currenttrans.getAttributeValue("ts");
						String tsnext = nexttrans.getAttributeValue("ts");
						tsold = tsold + tsnext;
						nexttrans.setAttribute("ts", tsold);
						boolean brem = body.removeContent(((Element) transUnitList.get(i)));
						count = transUnitList.size();
					}
				}
			}
		}
		// LogPrint.println("rearrangeWhitespaceSegments: found " + iEmpty + " segments");
		return;
	}

	private void arrangeSegment(Element body)
	{

		beginEpt2trailEpt(body);
		// System.out.println("noneSegment2trail");
		noneSegment2trail(body);
		// System.out.println("markAtBegining");
		markAtBegining(body);
		// System.out.println("markAtTrailing");
		markAtTrailing(body);
		pt2it(body);

		// check if we have any white space empty segments those must be copied into the previous segment element
		rearrangeWhitespaceSegments(body);
		segmentReorderTagID(body);
	}

	// This will mark segment breaking cr/lf and put the number of cr/lf into list
	private List markCrlf(String text)
	{
		List ret = new Vector();
		List numberList = new Vector();
		StringBuffer buff = new StringBuffer();
		char[] chars = text.toCharArray();
		int numberOfFound = 0;
		boolean breakON = false;
		for (int i = 0; i < chars.length; i++)
		{
			char ch = chars[i];
			if (ch == '\n')
			{
				numberOfFound++;
				if (numberOfFound == breakOnCrlf)
				{
					breakON = true;
				}
			}
			else
			{
				if (breakON)
				{
					buff.append(SegmentRule.CRLF_MARKER);
					numberList.add(new Integer(numberOfFound));
					breakON = false;
				}
				else
				{
					for (int n = 0; n < numberOfFound; n++)
					{
						buff.append(' ');
					}
				}
				numberOfFound = 0;
				buff.append(ch);
			}
		}
		if (breakON)
		{
			buff.append(SegmentRule.CRLF_MARKER);
			numberList.add(new Integer(numberOfFound));
		}
		else
		{
			for (int n = 0; n < numberOfFound; n++)
			{
				buff.append(' ');
			}
		}
		ret.add(numberList);
		ret.add(buff.toString());
		return ret;
	}

	private Element segment(Element body, String dataType)
	{
		Namespace namespace = Namespace.XML_NAMESPACE; // Namespace.getNamespace("xml", "");
		totalParagraph = 1;
		Element transUnit = body.getChild("trans-unit");
		Element source = transUnit.getChild("source");
		String lang = source.getAttributeValue("lang", namespace);
		if (lang == null)
			lang = srcLanguage;
		List nodeList = new Vector(source.getContent());
		// source.setContent(null);
		source.removeContent();
		// body.setContent(null);
		body.removeContent();

		int size = nodeList.size();

		// wk 28.11.2003 - this variable indicates if we have some text in the text buffer
		// in order to handl cr/lf correctly - not to create two segments after each other where
		// the second on is empty
		// hard to correct:
		// problem appears when we have the situation: .\r\n<x ..> etc. In this case it inserts
		// a new segment for .. Then it is followed by an x - which means it immediatly creates another segment
		// this possible results in an empty segment - bad -
		// maybe we we have to do some clean up after the segmentation to avoid this
		// esp. for alignment
		boolean bSomeCurrentText = false;

		currentSource = createNewSegment(body, transUnit.getAttributeValue("id"), lang, transUnit.getAttributeValue("extradata"), "xliff(5)",
				dataType);
		for (int i = 0; i < size; i++)
		{
			Object node = nodeList.get(i);
			if (node instanceof Element)
			{
				String nodeName = ((Element) node).getName();
				if (nodeName.equals("x"))
				{
					String id = ((Element) node).getAttributeValue("id");
					String ctype = ((Element) node).getAttributeValue("ctype");
					String boundary = ((Element) node).getAttributeValue("ts");
					if (boundary == null || boundary.equals(""))
						boundary = "0";
					// LogPrint.println("createNewSegment index=" + nodeName + " \"" + id + " \"" + ctype + " \"" + boundary + "\"");
					id = idsequence + "";
					idsequence++;
					currentSource = createNewSegment(body, id, lang, ctype, boundary, dataType);
					totalParagraph++;
					bSomeCurrentText = false;
				}
				else
				{
					currentSource.addContent((Element) node);
				}
			}
			else if (node instanceof EntityRef)
			{
				currentSource.addContent((EntityRef) node);
			}
			else if (node instanceof Text)
			{
				String text = ((Text) node).getText();
				bSomeCurrentText = true;

				// Here now to protect space at the begining and trailing
				// before doing text normalize
				text = "|" + text + "|";
				text = Text.normalizeString(text);
				text = text.substring(1);
				text = text.substring(0, text.length() - 1);

				text = text.replaceAll("\r", "");
				List crlfnumberList = null;
				// if (breakOnCrlf == 0) {
				// Here repalce \n in case of not breaking on cr/lf so that
				// text = text.replaceAll("\n"," ");
				// } else {
				// List results = markCrlf(text);
				// crlfnumberList = (List)results.get(0);
				// text = (String)results.get(1);
				// }
				if (enableSegmentation || breakOnCrlf != 0)
				{
					StringBuffer strBuffer = null;
					if (enableSegmentation)
					{
						strBuffer = segRule.markAbbreviation(text);
						strBuffer = segRule.markURL(strBuffer);
						strBuffer = segRule.markClasses(strBuffer);
						strBuffer = segRule.markNumberClass(strBuffer.toString());
						strBuffer = segRule.segmentBreakByChar(strBuffer, nodeList, i);
						strBuffer = segRule.unmark(strBuffer);
					}
					else
					{
						strBuffer = new StringBuffer(text);
					}
					if (breakOnCrlf > 0)
					{
						List results = markCrlf(strBuffer.toString());
						crlfnumberList = (List) results.get(0);
						text = (String) results.get(1);
						strBuffer = new StringBuffer(text);
					}
					else
					{
						text = text.replaceAll("\n", " ");
					}
					// String marker = ""+SegmentRule.MARKER;
					// int index = strBuffer.indexOf(marker, 0);
					int lastIndex = 0;
					int length = strBuffer.length();
					int indexCrlf = 0;
					for (int index = 0; index < length; index++)
					{
						char ch = strBuffer.charAt(index);
						// in case of NOT breaking on cr/lf
						// cr/lf in text will be replaced by above code with space.
						if (ch == SegmentRule.MARKER || ch == SegmentRule.CRLF_MARKER)
						{
							String aSeg = strBuffer.substring(lastIndex, (index));
							currentSource.addContent(aSeg);
							String id = null;
							if (ch == SegmentRule.CRLF_MARKER)
							{
								id = "crlf-" + ((Integer) crlfnumberList.get(indexCrlf)).intValue();
								indexCrlf++;
								id = idsequence + "";
								idsequence++;
							}
							else
							{
								id = "s";
								id = idsequence + "";
								idsequence++;
							}
							// LogPrint.println("createNewSegment index=" + index + " \"" + text + "\"");
							currentSource = createNewSegment(body, id, lang, "", "sentence(0)", dataType);
							lastIndex = index + 1;
						}
					}
					// while (index >= 0) {
					// String aSeg = strBuffer.substring(lastIndex, (index));
					// currentSource.addContent(aSeg);
					// currentSource = createNewSegment(body, "s", lang, "", "sentence(0)");
					// lastIndex = index+1;
					// index = strBuffer.indexOf(marker, lastIndex);
					// }
					if (lastIndex != (strBuffer.length()))
					{
						String aSeg = strBuffer.substring(lastIndex);
						currentSource.addContent(aSeg);
					}
					strBuffer.setLength(0);
					strBuffer = null;
					bSomeCurrentText = false;
				}
				else
				{
					currentSource.addContent(text);
				}
			}
		}
		arrangeSegment(body);
		System.out.println("Paragraphs Total" + Integer.toString(totalParagraph));
		List transUnitList = body.getChildren("trans-unit");
		System.out.println("Segments Total " + Integer.toString(transUnitList.size()));

		transUnitList = null;
		transUnit = null;
		source = null;
		currentSource = null;

		return null;
	}

	private void addHistory(Element fileElem)
	{
		Element header = fileElem.getChild("header");
		Element phaseGroup = header.getChild("phase-group");
		Element phase = phaseGroup.getChild("phase");
		phase = (Element) phase.clone();
		Calendar calendar = Calendar.getInstance(Locale.US);
		String time = calendar.get(Calendar.DATE) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.YEAR) + " "
				+ calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND);
		((Element) phase).setAttribute("tool", SEGMENTER + SEGMENTER_VERSION);
		((Element) phase).setAttribute("phase-name", "2");
		((Element) phase).setAttribute("process-name", "Segmentation");
		((Element) phase).setAttribute("date", time);
		phaseGroup.addContent(phase);

		header = null;
		phaseGroup = null;
		phase = null;
		calendar = null;
		time = null;
	}

	public void process() throws OpenTMSException
	{
		System.out.println("Starting processing segments");
		List fileList = doc.getRoot().getChildren("file");
		int fileCount = fileList.size();
		for (int i = 0; i < fileCount; i++)
		{
			Element fileElem = (Element) fileList.get(i);
			String dataType = fileElem.getAttributeValue("datatype");
			srcLanguage = fileElem.getAttributeValue("source-language");
			if (enableSegmentation)
				segRule.loadRule(srcLanguage);
			System.out.println("process: Rules loaded");
			addHistory(fileElem);
			System.out.println("process: History updated");
			Element body = fileElem.getChild("body");
			System.out.println("process: Retrieve body");
			segment(body, dataType);
		}

		fileList = null;
	}

	private void createSegment(Element transUnit, List list, List listtags)
	{
		String boundary = transUnit.getAttributeValue("ts");
		if (boundary == null || boundary.equals(""))
			boundary = "";

		StringTokenizer tok = new StringTokenizer(boundary, "()");
		if (tok.countTokens() < 2)
		{
			createSegmentNoTagName(transUnit, list);
		}
		else
		{
			String trans = transUnit.getText();
			while (tok.hasMoreTokens())
			{
				String tagname = tok.nextToken();
				int aboundary = 0;
				if (tok.hasMoreTokens())
				{
					try
					{
						aboundary = Integer.parseInt(tok.nextToken());
					}
					catch (NumberFormatException ex)
					{
						aboundary = 0;
					}
				}

				list.add(new ASegment(trans, aboundary, tagname));

				// break; // add only one hard boundary
			}
		}
	}

	private void createSegmentNoTagName(Element transUnit, List list)
	{
		String boundary = transUnit.getAttributeValue("ts");
		if (boundary == null || boundary.equals(""))
			boundary = "0";

		char[] boundaries = boundary.toCharArray();
		String trans = transUnit.getText();
		for (int i = 0; i < boundaries.length; i++)
		{
			String str = Character.toString(boundaries[i]);
			if (str.equals("-"))
			{
				str = str + boundaries[++i];
			}
			list.add(new ASegment(trans, Integer.parseInt(str), ""));
		}
	}

	private Segment createSegment(List list, String datatype, String transUnit)
	{
		return new ASegment(list, datatype, transUnit);
	}

	// private static int i=0;
	public Segment[] processForAlignment() throws OpenTMSException
	{
		List listtags = new Vector();
		List transUnitList = doc.getTransUnitList(doc.getBody(doc.getRoot().getChild("file")));
		int count = transUnitList.size();
		if (count == 1)
		{
			try
			{
				process();
			}
			catch (OpenTMSException e)
			{
				throw e;
			}
			count = transUnitList.size();
		}
		// doc.storeAs(new java.io.File("x"+i+".xml"));
		// i++;
		List list = new ArrayList();
		int index = 0;
		for (int i = 0; i < count; i++)
		{
			Element transUnit = (Element) transUnitList.get(i);
			if (i == 0 && count > 1)
			{
				// changed wk 20.02.2006
				// transUnit = createStartingSegment(transUnit, (Element) transUnitList.get(1));
				transUnit = createStartingSegment(transUnit, (Element) transUnitList.get(0));
			}
			Element source = transUnit.getChild("source");
			// String test = source.toString();
			createSegment(transUnit, list, listtags);
			list.add(createSegment(source.getContent(), transUnit.getText(), ""));
			if (i == (count - 1))
			{
				createSegment(createEndingSegment(listtags), list, listtags);
			}
		}
		transUnitList = null;
		Segment[] segments = new Segment[list.size()];

		for (int i = 0; i < segments.length; i++)
		{
			segments[i] = (Segment) list.get(i);
		}
		// for (int i=0;i<segments.length;i++) {
		// com.araya.alignment.Segment s = segments[i];
		// if (s.getHardBoundaryLevel() == 0) {
		// List words = s.getWords();
		// List nonwords = s.getNonWords();
		// List numbers = s.getNumbers();
		// Iterator it = words.iterator();
		// System.out.println("\nWORDS");
		// while (it.hasNext()) {
		// System.out.print((String)it.next()+" | ");
		// }
		// System.out.println("\nNON-WORDS");
		// it = nonwords.iterator();
		// while (it.hasNext()) {
		// System.out.print((String)it.next()+" | ");
		// }
		// System.out.println("\nNUMBERS");
		// it = numbers.iterator();
		// while (it.hasNext()) {
		// System.out.print((String)it.next()+" | ");
		// }
		// }
		// }
		// XMLSegmentNode segmentNode = readXMLSegmentNodes(segments);
		// printXMLSegmentNodes(segmentNode);
		return segments;
	}

	/**
	 * createStartingSegment
	 * 
	 * @param firsttrans
	 * @param transUnit
	 * @return
	 */
	private Element createStartingSegment(Element firsttrans, Element transUnit)
	{
		String boundary = transUnit.getAttributeValue("ts");
		StringBuffer ts = new StringBuffer();
		if (boundary == null || boundary.equals(""))
			boundary = "";

		StringTokenizer tok = new StringTokenizer(boundary, "()");
		if (tok.countTokens() < 2)
		{
			return transUnit;
		}
		else
		{
			while (tok.hasMoreTokens())
			{
				String tagname = tok.nextToken();
				int aboundary = 0;
				if (tok.hasMoreTokens())
				{
					try
					{
						String xLevel = tok.nextToken();
						aboundary = Integer.parseInt(xLevel);
						if (aboundary < 0)
						{
							ts.append(tagname + "(" + (aboundary * -1) + ")");
						}
						else
						{
							ts.append(tagname + "(" + aboundary + ")");
						}
					}
					catch (NumberFormatException ex)
					{
						aboundary = 0;
					}
				}

				// break; // only one to add
			}
			ts.insert(0, "(xliff)(5)");
			firsttrans.setAttribute("ts", ts.toString());
		}
		return firsttrans;
	}

	/**
	 * createEndingSegment
	 * 
	 * @param listtags
	 * @return
	 */
	private Element createEndingSegment(List listtags)
	{
		Element transUnit = new Element("trans-unit");
		String strboundaries = "";
		Iterator it = listtags.iterator();
		while (it.hasNext())
		{
			strboundaries += (String) it.next();
		}

		strboundaries += "(xliff)(-5)";
		transUnit.setAttribute("ts", strboundaries);
		return transUnit;
	}

	// Alignment Improvement
	private void printXMLSegmentNodes(XMLSegmentNode node)
	{
		System.out.println("-----------------------------------------");
		System.out.println("Parent   : " + (node.parent != null ? node.parent.tag : "null"));
		System.out.println("tag name : " + node.tag);
		String sons = "";
		Iterator it = node.sons.iterator();
		while (it.hasNext())
		{
			sons += ((XMLSegmentNode) it.next()).tag + ",";
		}
		System.out.println("Sons     : " + sons);
		System.out.println("tag level: " + node.level);
		System.out.println("Start num: " + node.startSegNumber);
		System.out.println("End num  : " + node.endSegNumber);
		it = node.sons.iterator();
		while (it.hasNext())
		{
			printXMLSegmentNodes((XMLSegmentNode) it.next());
		}
	}

	public XMLSegmentNode readXMLSegmentNodes(com.araya.alignment.Segment[] segments)
	{
		if (segments == null || segments.length < 2)
		{
			return null;
		}
		Vector parents = new Vector();
		int index = 1;
		XMLSegmentNode parent = null;
		for (int i = 0; i < segments.length; i++)
		{
			if (segments[i].getHardBoundaryLevel() > 0)
			{
				parent = parents.isEmpty() ? null : (XMLSegmentNode) parents.lastElement();
				int level = segments[i].getHardBoundaryLevel();
				String tagname = segments[i].getHardboundaryTagName();
				XMLSegmentNode current = new XMLSegmentNode(parent, segments[i].getHardboundaryTagName(), segments[i].getHardBoundaryLevel(), index++);
				if (parent != null)
				{
					parent.addSon(current);
				}
				parents.add(current);
			}
			else if (segments[i].getHardBoundaryLevel() < 0)
			{
				parent = parents.isEmpty() ? null : (XMLSegmentNode) parents.lastElement();
				if (parent != null)
				{
					parent.endSegNumber = index++;
					parents.remove(parent);
				}
			}
		}
		return parent;
	}
	// End Alignment Improvement
}