package de.folt.util;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.regex.Pattern;

public class CorrectTradosIllegalXMLCharacter
{

	private static char						replaceCRChar			= '\uF90D';

	private static char						replaceLFChar			= '\uF90A';

	public static char getReplaceCRChar()
	{
		return CorrectTradosIllegalXMLCharacter.replaceCRChar;
	}

	public static char getReplaceLFChar()
	{
		return CorrectTradosIllegalXMLCharacter.replaceLFChar;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		CorrectTradosIllegalXMLCharacter cor = new CorrectTradosIllegalXMLCharacter();
		System.out.println(args[0] + ": " + cor.encodeIncorrectSDLTradosXLIFFFile(args[0]) + " changed: " + cor.getIchanged());
		System.out.println(args[0] + ": " + cor.decodeIncorrectSDLTradosXLIFFFile(args[0]));
	}

	public static void setReplaceCRChar(char replaceCRChar)
	{
		CorrectTradosIllegalXMLCharacter.replaceCRChar = replaceCRChar;
	}

	public static void setReplaceLFChar(char replaceLFChar)
	{
		CorrectTradosIllegalXMLCharacter.replaceLFChar = replaceLFChar;
	}

	private int								ichanged				= 0;

	private Vector<String>					illegalEntityList		= null;

	private char							illegalReplaceCharUser	= '\uE000';							// U+E000..U+F8FF -'\uF8FE';

	private Hashtable<String, Integer>		mapTable				= new Hashtable<String, Integer>();	// &#x1E;

	private Hashtable<Character, String>	reverseMapTable			= new Hashtable<Character, String>();	// &#x1E;

	public CorrectTradosIllegalXMLCharacter()
	{
		createillegalEntityList();
	}

	/**
	 * 
	 */
	public void createillegalEntityList()
	{
		illegalEntityList = new Vector<String>();
		for (int i = 0; i < 31; i++)
		{
			if ((i != 9) && (i != 10) && (i != 13)) // 01.10.2013 - 12 -> 13
			{
				String s = "&#x" + Integer.toHexString(i).toUpperCase() + ";";
				illegalEntityList.add(s);
				mapTable.put(s, i);
				char repchar = (char) (illegalReplaceCharUser + i);
				reverseMapTable.put(repchar, s);
			}
		}
	}

	/**
	 * @param filename
	 * @return
	 */
	public boolean decodeIncorrectSDLTradosXLIFFFile(String filename)
	{
		try
		{
			String content = de.folt.util.OpenTMSSupportFunctions.readFileIntoString(filename, "UTF8");

			Enumeration<Character> enumme = reverseMapTable.keys();
			while (enumme.hasMoreElements())
			{
				Character repchar = enumme.nextElement();
				content = content.replaceAll(repchar + "", reverseMapTable.get(repchar));
			}

			// 01.10.2013
			for (int i = 0; i < illegalEntityList.size(); i++)
			{
				char repchar = (char) (illegalReplaceCharUser + i);
				content = content.replaceAll(repchar + "", illegalEntityList.get(i));
			}
			// end 01.10.2013

			// content = content.replaceAll("&#xD;", "\r");

			content = content.replace(getReplaceLFChar(), '\n');
			content = content.replace(getReplaceCRChar(), '\r');
			de.folt.util.OpenTMSSupportFunctions.simpleCopyStringToFile(content, filename);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * @param filename
	 * @return
	 */
	public boolean encodeIncorrectSDLTradosXLIFFFile(String filename)
	{
		try
		{
			String content = de.folt.util.OpenTMSSupportFunctions.readFileIntoString(filename, "UTF8");
			Vector<Character> incorrectChars = de.folt.util.StringUtil.returnInvalidXMLCharsAsVector(content);
			if (incorrectChars != null)
			{
				for (int i = 0; i < incorrectChars.size(); i++)
				{
					char c = incorrectChars.get(i);
					char repchar = (char) (illegalReplaceCharUser + c);
					content = content.replace(c, repchar);
				}
			}

			for (int i = 0; i < illegalEntityList.size(); i++)
			{
				char repchar = (char) (illegalReplaceCharUser + i);
				// 01.10.2013 - Pattern.quote
				content = content.replaceAll(Pattern.quote(illegalEntityList.get(i)), repchar + "");
				// end 01.10.2013
			}

			// content = content.replaceAll("\r", "&#xD;");

			// now do the replacement od source, target and seg-sourc
			// replace \n \r with resp. replacement characters for protection against loosing them when XMLoutputting

			String element[] = new String[3];
			element[0] = "source";
			element[1] = "seg-source";
			element[2] = "target";
			char[] source = new char[2];
			source[0] = '\n';
			source[1] = '\r';
			char[] replace = new char[2];
			replace[0] = getReplaceLFChar();
			replace[1] = getReplaceCRChar();

			content = replaceCharInElement(element, content, source, replace);

			de.folt.util.OpenTMSSupportFunctions.simpleCopyStringToFile(content, filename);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public int getIchanged()
	{
		return ichanged;
	}

	/**
	 * @param element
	 * @param string
	 * @param source
	 * @param replace
	 * @return
	 */
	public String replaceCharInElement(String element[], String string, char[] source, char[] replace)
	{
		int iSourceStart = 0;
		int iSourceEnd = -10;
		setIchanged(0);
		StringBuffer stringbuffer = new StringBuffer(string);
		for (int i = 0; i < element.length; i++)
		{
			iSourceStart = stringbuffer.indexOf("<" + element[i], iSourceStart);
			if (iSourceStart == -1)
			{
				iSourceStart = 0;
				iSourceEnd = -10;
				continue;
			}
			int iSourceElementEnd = stringbuffer.indexOf(">", iSourceStart+1);
			while (iSourceElementEnd > -1)
			{
				String elem;
				try
				{
					elem = stringbuffer.substring(iSourceStart, iSourceElementEnd + 1);
				}
				catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					break;
				}
				if (elem.contains("/"))
				{
					iSourceEnd = stringbuffer.indexOf("/", iSourceElementEnd + 1);
					iSourceStart = iSourceEnd + 1;
					break;
				}
				iSourceEnd = stringbuffer.indexOf("</" + element[i], iSourceStart);
				if (iSourceEnd > -1)
				{
					for (int j = 0; j < source.length; j++)
					{
						for (int k = iSourceElementEnd + 1; k < iSourceEnd - 2; k++)
						{
							if (stringbuffer.charAt(k) == source[j])
							{
								stringbuffer.setCharAt(k, replace[j]);
								setIchanged(getIchanged() + 1);
							}
						}
					}
				}
				iSourceStart = stringbuffer.indexOf("<" + element[i], iSourceEnd);
				if (iSourceStart == -1)
				{
					iSourceStart = 0;
					iSourceEnd = -10;
					break;
				}
				iSourceElementEnd = stringbuffer.indexOf(">", iSourceStart+1);
			}
		}
		return stringbuffer.toString();
	}

	public void setIchanged(int ichanged)
	{
		this.ichanged = ichanged;
	}
}
