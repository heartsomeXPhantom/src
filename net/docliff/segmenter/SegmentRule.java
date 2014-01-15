package net.docliff.segmenter;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */
import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.jdom.Element;
import org.jdom.Text;

import de.folt.constants.OpenTMSConstants;
import de.folt.models.documentmodel.document.XmlDocument;
import de.folt.util.OpenTMSException;
import de.folt.util.OpenTMSProperties;
import de.folt.util.StringUtil;

public class SegmentRule
{
	class Abbreviation
	{
		boolean caseSensitive;
		String abbrev;
		int abbrevLen;

		public Abbreviation(String abbrev, boolean caseSensitive)
		{
			this.caseSensitive = caseSensitive;
			this.abbrev = abbrev;
			this.abbrevLen = abbrev.length();
		}
	}

	class AClass
	{
		ArrayList arrayList = new ArrayList();

		public AClass(List list)
		{
			Iterator it = list.iterator();
			while (it.hasNext())
			{
				Object obj = it.next();
				if (obj instanceof Element)
				{
					ClassName className = new ClassName(((Element) obj).getName());
					arrayList.add(className);
				}
				else if (obj instanceof Text)
				{
					arrayList.add(((Text) obj).getText());
				}
			}
		}

		protected List getList()
		{
			return arrayList;
		}
	}

	class ClassName
	{
		String className;

		public ClassName(String className)
		{
			this.className = className;
		}
	}

	private File initFile = null;
	private String[] breakChars = null;

	private Hashtable followCharRule = null;

	private ArrayList abbrevList = null;

	private ArrayList classesList = null;

	int abbrevCount = 0;

	private final int E0_START = 57344;

	private final int E0_END = 57595;
	private Object[] wordbreakChars;
	private String[] wordChars;
	private XmlDocument initDoc;

	protected static final char MARKER = '\u0002';

	protected static final char CRLF_MARKER = '\u0003';

	/**
	 * 
	 */
	public SegmentRule() throws OpenTMSException
	{
		String iniDir = OpenTMSProperties.getInstance().getOpenTMSProperty("OpenTMS.dir") + "/ini";
		String iniSeg = OpenTMSProperties.getInstance().getOpenTMSProperty("OpenTMS.segmenter.default");
		String initFileName = iniDir + "/" + iniSeg;
		initFile = new File(initFileName);
		if (initFile == null)
		{
			throw new OpenTMSException(OpenTMSConstants.OpenTMS_FILE_NOTFOUND_ERROR + "", new String[] { "__FILE__", initFileName });
		}
	}

	public SegmentRule(String initFileName) throws OpenTMSException
	{
		String iniDir = OpenTMSProperties.getInstance().getOpenTMSProperty("OpenTMS.dir") + "/ini";
		initFile = new File(iniDir + "/" + initFileName);
		if (initFile == null)
		{
			throw new OpenTMSException(OpenTMSConstants.OpenTMS_FILE_NOTFOUND_ERROR + "", new String[] { "__FILE__", initFileName });
		}
	}

	/**
	 * @return the abbrevCount
	 */
	public int getAbbrevCount()
	{
		return abbrevCount;
	}

	/**
	 * @return the abbrevList
	 */
	public ArrayList getAbbrevList()
	{
		return abbrevList;
	}

	/**
	 * @return the breakChars
	 */
	public String[] getBreakChars()
	{
		return breakChars;
	}

	/**
	 * @return the followCharRule
	 */
	/**
	 * @return
	 */
	public Hashtable getFollowCharRule()
	{
		return followCharRule;
	}

	/**
	 * @return the initFile
	 */
	public File getInitFile()
	{
		return initFile;
	}

	/**
	 * @param index
	 * @param strBuffer
	 * @param list
	 * @param curIndex
	 * @return
	 */
	private char getNextChar(int index, StringBuffer strBuffer, List list, int curIndex)
	{
		// if return value is '\0' that mean we don't need to spearate sentence
		// by braking character
		// Example : if it follow with x node
		int size = list.size();
		int len = strBuffer.length();
		if ((++index) < strBuffer.length())
		{
			return strBuffer.charAt(index);
		}
		else
		{
			for (int i = curIndex + 1; i < size; i++)
			{
				Object node = list.get(i);
				if (node instanceof Element)
				{
					String nodeName = ((Element) node).getName();
					if (nodeName.equals("x"))
					{
						return '\0';
					}
				}
				else if (node instanceof Text)
				{
					String str = ((Text) node).getText();
					if (!str.equals(""))
					{
						return str.charAt(0);
					}
					break;
				}
			}
		}
		return '\0';
	}

	public Element getSegSection(String lang)
	{
		Element applElem = initDoc.getRoot().getChild("segmenter");
		List list = applElem.getChildren("seg-section");
		Iterator it = list.iterator();
		lang += ";";
		while (it.hasNext())
		{
			Element elem = (Element) it.next();
			String langs = elem.getAttributeValue("language");
			if (langs.charAt(langs.length() - 1) != ';')
				langs += ";";
			if (langs.indexOf(lang) >= 0)
			{
				return elem;
			}
		}
		return null;
	}

	/**
	 * @return the wordChars
	 */
	public String[] getWordChars()
	{
		return wordChars;
	}

	/**
	 * @param chararr
	 * @param iStart
	 * @return
	 */
	public boolean isAbbreviation(char[] chararr, int iStart)
	{
		String string = "";
		int iEnd = iStart + 15;
		if (iEnd >= chararr.length)
			iEnd = chararr.length;
		for (int i = iStart; i < iEnd; i++)
		{
			string = string + chararr[i];
		}
		for (int i = 0; i < this.abbrevList.size(); i++)
		{
			if (string.startsWith((String) this.abbrevList.get(i)))
				return true;
		}
		return false;
	}

	/**
	 * @param string
	 * @return
	 */
	public boolean isAbbreviation(String string)
	{
		for (int i = 0; i < this.abbrevList.size(); i++)
		{
			if (string.startsWith((String) this.abbrevList.get(i)))
				return true;
		}
		return false;
	}

	/**
	 * @param ch
	 * @return
	 */
	public boolean isBreakChar(char ch)
	{
		for (int i = 0; i < this.breakChars.length; i++)
		{
			if ((ch + "").equals(this.breakChars[i]))
				return true;
		}
		return false;
	}

	/**
	 * @param ch
	 * @return
	 */
	public boolean isWordBreakChar(char ch)
	{
		for (int i = 0; i < this.wordChars.length; i++)
		{
			if ((ch + "").equals(this.wordChars[i]))
				return true;
		}
		return false;
	}

	public void loadRule(String lang) throws OpenTMSException
	{
		if (followCharRule != null)
		{
			followCharRule.clear();
			followCharRule = null;
		}
		followCharRule = new Hashtable();
		abbrevList = new ArrayList();
		abbrevCount = 0;
		System.out.println("Initialize File" + initFile.getAbsolutePath());
		initDoc = new XmlDocument(initFile);
		Element segSection = getSegSection(lang);
		if (segSection != null)
		{
			Element breakSegment = segSection.getChild("break-segment");
			if (breakSegment != null)
			{
				List nodeList = breakSegment.getChildren("break-char");
				int count = nodeList.size();
				if (count > 0)
				{
					breakChars = new String[count];
					for (int i = 0; i < count; i++)
					{
						Element elem = (Element) nodeList.get(i);
						String breakChar = elem.getAttributeValue("character");
						if (breakChar.startsWith("\\u"))
						{
							breakChars[i] = Character.toString((char) Integer.parseInt(breakChar.substring(2), 16));
						}
						else
						{
							breakChars[i] = breakChar;
						}
						String followWith = elem.getAttributeValue("follow-with");
						if (!followWith.equals(""))
						{
							StringTokenizer st = new StringTokenizer(followWith, ";", false);
							ArrayList arrayList = new ArrayList();
							for (int z = st.countTokens(); z > 0; z--)
							{
								String value = "";
								String str = st.nextToken().trim();
								if (str.startsWith("\\u"))
								{
									value = Character.toString((char) Integer.parseInt(str.substring(2), 16));
								}
								else
								{
									value = str;
								}
								arrayList.add(value);
							}
							followCharRule.put(breakChars[i], arrayList);
						}
					}
				}
			}
			Element abbreviations = segSection.getChild("abbreviations");
			if (abbreviations != null)
			{
				List nodeList = abbreviations.getChildren("abbrev");
				int count = nodeList.size();
				if (count > 0)
				{
					abbrevList = new ArrayList();
					for (int i = 0; i < count; i++)
					{
						Element elem = (Element) nodeList.get(i);
						String cs = elem.getAttributeValue("case-sensitive");
						if (cs == null)
							cs = "no";
						boolean caseSensitive = cs.equals("yes");
						String abbrev = elem.getText();
						if (abbrev.indexOf("\\u000a") >= 0)
						{
							abbrev = abbrev.replaceAll("\\\\u000a", "\n");
						}
						Abbreviation abbreviation = new Abbreviation(abbrev, caseSensitive);
						abbrevList.add(abbreviation);
					}
					abbrevCount = abbrevList.size();
					// for (int z=0;z<abbrevCount;z++) {
					// Abbreviation abb = (Abbreviation)abbrevList.get(z);
					// }
				}
			}

			Element breakWord = segSection.getChild("break-word");
			if (breakWord != null)
			{
				List nodeList = breakWord.getChildren("break-char");
				int count = nodeList.size();
				if (count > 0)
				{
					wordChars = new String[count + 4];
					// Default word breaks
					wordChars[0] = " ";
					wordChars[1] = "\n";
					wordChars[2] = "\r";
					wordChars[3] = "\t";

					for (int i = 4; i < wordChars.length; i++)
					{
						Element elem = (Element) nodeList.get(i - 4);
						String breakChar = elem.getAttributeValue("character");
						if (breakChar.startsWith("\\u"))
						{
							wordChars[i] = Character.toString((char) Integer.parseInt(breakChar.substring(2), 16));
						}
						else
						{
							wordChars[i] = breakChar;
						}
					}
				}
				else
				{
					wordChars = new String[4];
					// Default word breaks
					wordChars[0] = " ";
					wordChars[1] = "\n";
					wordChars[2] = "\r";
					wordChars[3] = "\t";
				}
			}

			Element classes = segSection.getChild("classes");
			if (classes != null)
			{
				List nodeList = classes.getChildren("class");
				if (nodeList.size() > 0)
				{
					classesList = new ArrayList();
					Iterator classesIt = nodeList.iterator();
					while (classesIt.hasNext())
					{
						Element elem = (Element) classesIt.next();
						AClass aClass = new AClass(elem.getContent());
						classesList.add(aClass);
					}
				}
			}

		}
	}

	protected StringBuffer markAbbreviation(String str)
	{
		StringBuffer strBuffer = new StringBuffer(str);
		StringBuffer strBufferLower = new StringBuffer(str.toLowerCase());
		if (abbrevCount == 0)
			return strBuffer;
		for (int i = 0; i < abbrevCount; i++)
		{
			Abbreviation abb = (Abbreviation) abbrevList.get(i);
			int index = 0;
			if (abb.caseSensitive)
			{
				index = strBuffer.indexOf(abb.abbrev, 0);
			}
			else
			{
				index = strBufferLower.indexOf(abb.abbrev.toLowerCase(), 0);
			}
			while (index >= 0)
			{
				// System.out.println("Index = "+index);
				int before = index - 1;
				boolean needMark = true;
				if (before >= 0)
				{
					// The abbreviation must be lead with white space.
					char ch = strBuffer.charAt(before);
					if (ch != '\n' && ch != '\r' && ch != '\t' && ch != ' ' && ch != '\u00A0')
					{
						needMark = false;
					}
				}
				if (needMark)
				{
					for (int z = index; z < (index + abb.abbrevLen); z++)
					{
						char ch = strBuffer.charAt(z);
						// Replace char with E0 + char if char <= 255 to protect
						// break char in next process
						if (((int) ch) <= 255)
						{
							if (ch == '\n')
							{
								ch = ' ';
							}
							ch = (char) (((int) ch) + E0_START);
						}
						// System.out.println("Hex "+Integer.toHexString((int)e0char));
						strBuffer.setCharAt(z, ch);
					}
				}
				// System.out.println("StringReplaced = "+strBuffer.toString());
				if (abb.caseSensitive)
				{
					index = strBuffer.indexOf(abb.abbrev, index + 1);
				}
				else
				{
					index = strBufferLower.indexOf(abb.abbrev.toLowerCase(), index + 1);
				}
			}
		}
		return strBuffer;
	}

	protected StringBuffer markClasses(StringBuffer inbuff)
	{
		// If there is no anyclass need to be mark then return.
		if (classesList == null)
			return inbuff;

		String text = inbuff.toString();
		Iterator it = classesList.iterator();
		// Now start to mark for all formatting classes in list
		while (it.hasNext())
		{
			AClass aclass = (AClass) it.next();
			List list = aclass.getList();
			text = markup(list, text);
		}
		return new StringBuffer(text);
	}

	protected StringBuffer markNumberClass(String text)
	{
		// to check more in case of the number class is at trailing
		// so the sentence must be breaked that mean it dose not need
		// to be marked
		String tokenString = " \t\r\n\u00A0()";
		boolean hardReturn = true;
		StringBuffer buff = new StringBuffer();
		StringTokenizer tok = new StringTokenizer(text, tokenString, true);
		while (tok.hasMoreTokens())
		{
			String str = tok.nextToken();
			if (de.folt.util.StringUtil.isFormatNumber(str))
			{
				// Assume that if the first word is number class (The position
				// is after hardReturn) then markup
				if (hardReturn)
				{
					buff.append(markupClasses(str));
				}
				else
				{
					String next = null;
					String temp = "";
					// Here now we need to check the next word is
					// begun with uppercase character or not.
					while (tok.hasMoreTokens())
					{
						next = tok.nextToken();
						// if the string is not token string; then break for
						// another checking which loop out
						if (tokenString.indexOf(next) < 0)
						{
							break;
						}
						else
						{
							temp += next;
						}
						next = null;
					}
					if (next != null)
					{
						char ch = next.charAt(0);
						// Here if the first letter is uppercase mean that
						// need to break sentence here, not need to be marked
						if (Character.isUpperCase(ch))
						{
							buff.append(str);
							buff.append(temp);
							buff.append(next);
							hardReturn = false;
							continue;
						}
					}
					buff.append(markupClasses(str));
					if (!temp.equals(""))
					{
						buff.append(temp);
					}
					if (next != null)
					{
						buff.append(next);
					}
				}
				hardReturn = false;
			}
			else
			{
				if (str.equals("\n"))
				{
					hardReturn = true;
				}
				else if (hardReturn && tokenString.indexOf(str) >= 0)
				{
					hardReturn = true;
				}
				else
				{
					hardReturn = false;
				}
				buff.append(str);
			}
		}
		return buff;
	}

	protected String markup(List list, String text)
	{
		String tokenString = " \t\r\n\u00A0().";
		StringBuffer buff = new StringBuffer();
		StringTokenizer tok = new StringTokenizer(text, tokenString, true);
		int offset = 0;
		while (tok.hasMoreTokens())
		{
			String str = tok.nextToken();
			boolean bMatch = false;
			// Here now we need to find the first one
			// For example <number>. Dezember
			// So, need to find <number> first
			int size = list.size();
			if (StringUtil.isFormatNumber(str))
			{
				bMatch = true;
			}
			offset += str.length();
			// In future here we can add more code to check the class
			// else if (obj instanceof String) {
			// int index = text.indexOf((String)obj, offset);
			// if (index == offset) {
			// bMatch = true;
			// }
			// }
			// If Match, then need to check next...
			// But here I do not full handle perfect,
			// Just work on classes now we use like <number>. Dezember.
			if (bMatch)
			{
				String matchStr = str;
				if (size > 1)
				{
					String s = (String) list.get(1);
					// now find next match
					int index = text.indexOf(s, offset);
					if (index == offset)
					{
						// if match, then combine string for markup
						matchStr += s;
					}
					else
					{
						bMatch = false;
					}
					if (bMatch)
					{
						// markup matchStr and put into buffer
						matchStr = markupClasses(matchStr);
						buff.append(matchStr);

						// then have to substring and reset text...
						// because do not need to token text that is already
						// match
						offset += (s.length());
						if (offset < text.length())
						{
							text = text.substring(offset);
							tok = new StringTokenizer(text, tokenString, true);
							offset = 0;
						}
						else
						{
							// break loop to return.
							break;
						}
					}
					else
					{
						buff.append(str);
					}
				}
				else
				{
					buff.append(str);
				}
			}
			else
			{
				buff.append(str);
			}
		}
		return buff.toString();
	}

	private String markupClasses(String str)
	{
		StringBuffer buff = new StringBuffer();
		char[] chars = str.toCharArray();
		for (int i = 0; i < chars.length; i++)
		{
			// Replace char with E0 + char if char <= 255 to protect break char
			// in next process
			if (((int) chars[i]) <= 255)
				chars[i] = (char) (((int) chars[i]) + E0_START);
			// System.out.println("Hex "+Integer.toHexString((int)e0char));
			buff.append(chars[i]);
		}
		return buff.toString();
	}

	protected StringBuffer markURL(StringBuffer buff)
	{
		// Here we need to mark http:// because the characters inside can not be
		// considered as breaking char
		String str = buff.toString();
		str = str.toLowerCase();
		int len = buff.length();
		int index = str.indexOf("http://");
		while (index >= 0)
		{
			int i = index;
			while (i < len)
			{
				char ch = buff.charAt(i);
				if (ch != '\n' && ch != '\r' && ch != '\t' && ch != ' ' && ch != '\u00A0')
				{
					if (((int) ch) <= 255)
					{
						if (ch == '.')
						{
							if ((i + 1) < len)
							{
								char afterChar = buff.charAt(i + 1);
								if (afterChar != '\n' && afterChar != '\r' && afterChar != '\t' && afterChar != ' ' && afterChar != '\u00A0')
								{
									ch = (char) (((int) ch) + E0_START);
									buff.setCharAt(i, ch);
								}
								else
								{
									break;
								}
							}
						}
						else
						{
							ch = (char) (((int) ch) + E0_START);
							buff.setCharAt(i, ch);
						}
					}
				}
				else
				{
					break;
				}
				i++;
			}
			index = str.indexOf("http://", i);
		}
		index = str.indexOf("https://");
		while (index >= 0)
		{
			int i = index;
			while (i < len)
			{
				char ch = buff.charAt(i);
				if (ch != '\n' && ch != '\r' && ch != '\t' && ch != ' ' && ch != '\u00A0')
				{
					if (((int) ch) <= 255)
					{
						if (ch == '.')
						{
							if ((i + 1) < len)
							{
								char afterChar = buff.charAt(i + 1);
								if (afterChar != '\n' && afterChar != '\r' && afterChar != '\t' && afterChar != ' ' && afterChar != '\u00A0')
								{
									ch = (char) (((int) ch) + E0_START);
									buff.setCharAt(i, ch);
								}
								else
								{
									break;
								}
							}
						}
						else
						{
							ch = (char) (((int) ch) + E0_START);
							buff.setCharAt(i, ch);
						}
					}
				}
				else
				{
					break;
				}
				i++;
			}
			index = str.indexOf("https://", i);
		}
		return buff;
	}

	/**
	 * @param chararr
	 * @param iStart
	 * @return
	 */
	public String matchAbbreviation(char[] chararr, int iStart)
	{
		String string = "";
		int iEnd = iStart + 15;
		if (iEnd >= chararr.length)
			iEnd = chararr.length;
		for (int i = iStart; i < iEnd; i++)
		{
			string = string + chararr[i];
		}
		for (int i = 0; i < this.abbrevList.size(); i++)
		{
			SegmentRule.Abbreviation abb = (Abbreviation) this.abbrevList.get(i);
			if (string.startsWith(abb.abbrev))
				return abb.abbrev;
		}
		return null;
	}

	protected StringBuffer segmentBreakByChar(StringBuffer strBuffer, List list, int curIndex)
	{
		if (breakChars != null)
		{
			for (int i = 0; i < breakChars.length; i++)
			{
				int index = strBuffer.indexOf(breakChars[i], 0);
				while (index >= 0)
				{
					ArrayList arrayList = (ArrayList) followCharRule.get(breakChars[i]);
					if (arrayList != null)
					{
						char ch = getNextChar(index, strBuffer, list, curIndex);
						if (ch != '\0')
						{
							String str = Character.toString(ch);
							int arrayLen = arrayList.size();
							for (int z = 0; z < arrayLen; z++)
							{
								String followStr = (String) arrayList.get(z);
								if (followStr.equals(str))
								{
									strBuffer.insert(index + 1, MARKER);
									break;
								}
							}
						}
						index = strBuffer.indexOf(breakChars[i], index + 1);
					}
					else
					{
						// We can not ensure anymore if before breaking char
						// is standard ascii then we have to consider it
						// as european
						if ((index - 1) >= 0)
						{
							char beforeChar = strBuffer.charAt(index - 1);
							if (beforeChar <= 255)
							{
								char afterChar = getNextChar(index, strBuffer, list, curIndex);
								if (afterChar != 0)
								{
									if (afterChar == '\n' || afterChar == '\r' || afterChar == '\t' || afterChar == ' ' || afterChar == '\u00A0')
									{
										strBuffer.insert(index + 1, MARKER);
									}
								}
							}
							else
							{
								strBuffer.insert(index + 1, MARKER);
							}
						}
						else
						{
							strBuffer.insert(index + 1, MARKER);
						}
						index = strBuffer.indexOf(breakChars[i], index + 1);
					}
				}
			}
		}
		return strBuffer;
	}

	/**
	 * @param abbrevCount
	 *            the abbrevCount to set
	 */
	public void setAbbrevCount(int abbrevCount)
	{
		this.abbrevCount = abbrevCount;
	}

	/**
	 * @param abbrevList
	 *            the abbrevList to set
	 */
	public void setAbbrevList(ArrayList abbrevList)
	{
		this.abbrevList = abbrevList;
	}

	/**
	 * @param breakChars
	 *            the breakChars to set
	 */
	public void setBreakChars(String[] breakChars)
	{
		this.breakChars = breakChars;
	}

	/**
	 * @param followCharRule
	 *            the followCharRule to set
	 */
	public void setFollowCharRule(Hashtable followCharRule)
	{
		this.followCharRule = followCharRule;
	}

	/**
	 * @param initFile
	 *            the initFile to set
	 */
	public void setInitFile(File initFile)
	{
		this.initFile = initFile;
	}

	/**
	 * @param wordChars
	 *            the wordChars to set
	 */
	public void setWordChars(String[] wordChars)
	{
		this.wordChars = wordChars;
	}

	protected StringBuffer unmark(StringBuffer strBuffer)
	{
		// if (abbrevCount == 0)
		// return strBuffer;
		for (int i = 0; i < strBuffer.length(); i++)
		{
			int ch = (int) strBuffer.charAt(i);
			if (ch >= E0_START && ch <= E0_END)
			{
				ch -= E0_START;
				// System.out.println("Hex ch "+Integer.toHexString(ch));
				strBuffer.setCharAt(i, (char) ch);
			}
		}
		return strBuffer;
	}

}