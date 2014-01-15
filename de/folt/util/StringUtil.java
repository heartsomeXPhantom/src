package de.folt.util;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author unascribed
 * @version 1.0
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

public class StringUtil
{

	/**
	 * bCheckValidXMLChars
	 *  http://de.selfhtml.org/xml/regeln/zeichen.htm
	 *  Erlaubt sind Unicode-Zeichen mit den Hexadezimalwerten #x20 bis #xD7FF, #xE000 bis #xFFFD und #x10000 bis #x10FFFF. 
	 *  Nicht erlaubt sind lediglich die beiden Zeichen mit den Hexadezimalwerten #xFFFE und #xFFFF, da diese beiden keine Unicode-Zeichen darstellen.
	 *  Ferner sind folgende Steuerzeichen erlaubt: Tabulator-Zeichen (hexadezimal #x9), Zeilenvorschub-Zeichen (#xA) und Wagenrücklaufzeichen (#xD).
	 *  Diese drei Zeichen plus das normale Leerzeichen (#x20) bilden die so genannten Leerraumzeichen.
	 * 
	 * @param c
	 * @return
	 */
	public static boolean bCheckValidXMLChars(char c)
	{
		if (c >= 0x20 && c <= 0xD7FF)
			return true;
		if (c >= 0xE000 && c <= 0xFFFD)
			return true;
		if (c >= 0x10000 && c <= 0x10FFFF)
			return true;
		if (c == 0xA || c == 0xD || c == 0x9)
			return true;

		return false;
	}

	public static boolean containsPresentationForm(String string)
	{
		int length = string.length();
		for (int i = 0; i < length; i++)
		{
			char c = string.charAt(i);
			if ((c >= '\uFB50' && c <= '\uFDFF') || (c >= '\uFE70' && c <= '\uFEFE'))
			{
				// System.out.println((int) c);
				return true;
			}
		}
		return false;
	}

	// Here we need to split 3 kind of word from String
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Hashtable getTypeWords(String text)
	{
		List words = new Vector();
		List nonwords = new Vector();
		List numbers = new Vector();
		Hashtable hash = new Hashtable();
		hash.put("words", words);
		hash.put("nonwords", nonwords);
		hash.put("numbers", numbers);

		StringTokenizer tok = new StringTokenizer(text, " \t\r\n\u00A0[]()");
		while (tok.hasMoreTokens())
		{
			String str = tok.nextToken();
			if (isFormatNumber(str))
			{
				numbers.add(str);
			}
			else
			{
				StringTokenizer tok2 = new StringTokenizer(str, ".,-/<>+*:;&");
				while (tok2.hasMoreTokens())
				{
					str = tok2.nextToken();
					if (isFormatNumber(str))
					{
						numbers.add(str);
					}
					else if (isNonWord(str))
					{
						nonwords.add(str);
					}
					else
					{
						words.add(str);
					}
				}
			}
		}
		return hash;
	}

	public static int getUntranstedTextLeading(String str)
	{
		int numberOfWhiteSpace = 0;
		char[] chars = str.toCharArray();
		for (numberOfWhiteSpace = 0; numberOfWhiteSpace < chars.length; numberOfWhiteSpace++)
		{
			int ch = chars[numberOfWhiteSpace];
			if (ch != '\n' && ch != '\r' && ch != '\t' && ch != ' ' && ch != '\u00A0')
			{
				break;
			}
		}
		chars = null;
		return numberOfWhiteSpace;
	}

	public static int getUntranstedTextTrailing(String str)
	{
		int numberOfWhiteSpace = 0;
		char[] chars = str.toCharArray();
		int i = chars.length - 1;
		for (; i >= 0; numberOfWhiteSpace++, i--)
		{
			int ch = chars[i];
			if (ch != '\n' && ch != '\r' && ch != '\t' && ch != ' ' && ch != '\u00A0')
			{
				break;
			}
		}
		chars = null;
		return numberOfWhiteSpace;
	}

	public static StringBuffer hex2CharsBuffer(String data)
	{
		if (data.length() == 0)
			return null;
		StringBuffer charsBuffer = new StringBuffer();
		StringTokenizer hexToken = new StringTokenizer(data, "\\");
		for (int i = hexToken.countTokens(); i > 0; i--)
		{
			String hexValue = hexToken.nextToken().trim();
			char ch = (char) Integer.parseInt(hexValue, 16);
			charsBuffer.append(ch);
		}

		return charsBuffer;
	}

	public static boolean isFormatNumber(String str)
	{
		char[] chars = str.toCharArray();
		boolean hasDigit = false;
		for (int i = 0; i < chars.length; i++)
		{
			if (Character.isDigit(chars[i]))
			{
				hasDigit = true;
			}
			else if (chars[i] != '/' && chars[i] != '.' && chars[i] != ',' && chars[i] != '-' && chars[i] != '>'
					&& chars[i] != '<')
			{
				return false;
			}
		}
		chars = null;
		return hasDigit;
	}

	private static boolean isNonWord(String str)
	{
		char[] chars = str.toCharArray();
		boolean hasDigit = false;
		for (int i = 0; i < chars.length; i++)
		{
			if (Character.isDigit(chars[i]))
			{
				hasDigit = true;
			}
		}
		chars = null;
		return hasDigit;
	}

	public static boolean isUntranslatedText(String str)
	{
		// 0-47
		// 58-64
		// 91-96
		// 123-127
		// char [] untrans = new char[] {
		// '\u0000','\u0001','\u0002','\u0003','\u0004','\u0005',
		// '\u0006','\u0007','\u0008','\u0009','\u0010','\u0011',
		// '\u0012','\u0013','\u0014','\u0015','\u0016','\u0017',
		// '\u0018','\u0019','\u0020','\u0021','\u0022','\u0023',
		// '\u0024','\u0025','\u0026','\u0028','\u0029','\u0030',
		// '\u0031','\u0032','\u0033','\u0034','\u0035','\u0036',
		// '\u0037','\u0038','\u0039','\u0040','\u0041','\u0042',
		// '\u0043','\u0044','\u0045','\u0046','\u0047','\u0058',
		// '\u0059','\u0060','\u0061','\u0062','\u0063','\u0064',
		// '\u0091','\u0092','\u0093','\u0094','\u0095','\u0096',
		// '\u0123','\u0124','\u0125','\u0126','\u0127'
		// };
		char[] chars = str.toCharArray();
		for (int i = 0; i < chars.length; i++)
		{
			int ch = chars[i];
			if (ch == 46)
			{ // period
				return false;
			}
			else if (ch >= 0 && ch <= 47)
			{
			}
			else if (ch >= 58 && ch <= 64)
			{
			}
			else if (ch >= 91 && ch <= 96)
			{
			}
			else if (ch >= 123 && ch <= 127)
			{
			}
			else if (ch == 160)
			{ // node breking space
			}
			else
			{
				chars = null;
				return false;
			}
		}
		chars = null;
		return true;
	}

	public static String removeControlChar(String str)
	{
		StringBuffer buff = new StringBuffer(str);
		for (int i = 0; i < buff.length(); i++)
		{
			char ch = buff.charAt(i);
			if (ch >= 0 && ch <= 31)
			{
				if (ch != '\n' && ch != '\r' && ch != '\t')
				{
					buff.deleteCharAt(i);
					i--;
				}
			}
		}
		return buff.toString();
	}

	/**
	 * remove a specific string from a string array
	 * 
	 * @param input
	 *            the input string
	 * @param toDelete
	 *            the string to remove from Array
	 * @return the cleaned array
	 */
	public static String[] removeElements(String[] input, String toDelete)
	{
		if (input != null)
		{
			List<String> list = new ArrayList<String>(Arrays.asList(input));
			for (int i = 0; i < list.size(); i++)
			{
				if (list.get(i).equals(toDelete))
				{
					list.remove(i);
				}
			}
			return list.toArray(new String[0]);
		}
		else
		{
			return new String[0];
		}
	}

	/**
	 * Repeat a string n times
	 * 
	 * @param str
	 *            the string to repeat
	 * @param times
	 *            how often
	 * @return repeated string
	 */
	public static String repeatString(String str, int times)
	{
		return new String(new char[times]).replace("\0", str);
	}

	// public static String getString(byte [] bytes, String charset) {
	// try {
	// return new String(bytes, charset);
	// } catch (UnsupportedEncodingException e) {
	// }
	// throws EMXException {
	// StringBuffer s = new StringBuffer();
	// try {
	// InputStreamReader stringReader = new InputStreamReader(new
	// ByteArrayInputStream(bytes), charset);
	// int ch;
	// while ((ch=stringReader.read()) != -1 ) {
	// s.append((char)ch);
	// }
	// stringReader.close();
	// return s.toString();
	// } catch (UnsupportedEncodingException ex) {
	// throw new EMXException(
	// EMXConstants.ID_UNSUPPORTED_ENCODING,
	// new String[] {"__ENCODING__",charset}, ex);
	// } catch (IOException ex) {
	// }
	// return "";
	// }

	/*
	 * 
	 * Char ::= #x9 | #xA | #xD | [#x20-#xD7FF] | [#xE000-#xFFFD] |
	 * [#x10000-#x10FFFF] /* any Unicode character, excluding the surrogate
	 * blocks, FFFE, and FFFF.
	 */

	/**
	 * Replaces any occurence of string old in string str with string repl.
	 * 
	 * @param str
	 *            the original string
	 * @param old
	 *            the substring to be replaced
	 * @param repl
	 *            the replacement string for old
	 */
	public static String replaceSubstring(String str, String old, String repl)
	{
		int index = 0;
		int fromindex = 0;
		String result = "";
		while ((index = str.indexOf(old, fromindex)) != -1)
		{
			result += str.substring(fromindex, index) + repl;
			fromindex = index + old.length();
		}
		result += str.substring(fromindex);
		return result;
	}

	public static String replaceSubstring(String str, String old, String repl, int fromindex)
	{
		int index = 0;
		String result = "";
		if ((index = str.indexOf(old, fromindex)) != -1)
		{
			result += str.substring(0, index) + repl;
			fromindex = index + old.length();
		}
		result += str.substring(fromindex);
		return result;
	}

	public static Vector<String> returnInvalidXMLChars(String string)
	{
		Vector<String> vec = new Vector<String>();
		for (int i = 0; i < string.length(); i++)
		{
			char c = string.charAt(i);
			boolean bValid = bCheckValidXMLChars(c);
			if (bValid == false)
			{
				int ic1 = c;
				String ic = ic1 + "";
				vec.add("\"" + c + "\"::" + ic + "::0x" + Integer.toHexString(ic1) + "::" + i);
			}
		}
		if (vec.size() == 0)
			vec = null;
		return vec;
	}

	public static Vector<Character> returnInvalidXMLCharsAsVector(String string)
	{
		Vector<Character> vec = new Vector<Character>();
		for (int i = 0; i < string.length(); i++)
		{
			char c = string.charAt(i);
			boolean bValid = bCheckValidXMLChars(c);
			if (bValid == false)
			{
				vec.add(c);
			}
		}
		if (vec.size() == 0)
			vec = null;
		return vec;
	}

	/**
	 * String to a hexadecimal buffer representation
	 * 
	 * @param str
	 * @return
	 */
	public static StringBuffer toHexBuffer(String str)
	{
		int length = str.length();
		if (length == 0)
			return null;
		StringBuffer hexBuffer = new StringBuffer();
		for (int i = 0; i < length; i++)
		{
			int ch = (int) str.charAt(i);
			String hexStr = "\\" + Integer.toHexString(ch);
			hexBuffer.append(hexStr);
		}
		return hexBuffer;
	}

}
