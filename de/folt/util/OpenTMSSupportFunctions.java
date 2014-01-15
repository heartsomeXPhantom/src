/*
 * Created on 10.09.2008
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.TimeZone;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import javax.jws.WebParam;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class OpenTMSSupportFunctions
{

	private static String argumentsConcatenationString = ";";

	private static final int BOM_SIZE = 4;

	/**
	 * argumentReader reads arguments from a file into a hash table
	 * 
	 * @param fileName
	 *            the name of the file containing the arguments
	 * @return a hash table with with arguments
	 */

	public static Hashtable<String, String> argumentReader(String fileName)
	{
		Vector<String> args = OpenTMSSupportFunctions.readFileIntoVector(fileName, "UTF-8");
		// String[] argsarray = (String[]) args.toArray();
		Hashtable<String, String> resultArgumentHash = new Hashtable<String, String>();
		for (int i = 0; i < args.size(); i++)
		{
			String arg = args.get(i);
			String[] keyval = arg.split("=");
			if (keyval.length <= 1)
				continue;
			String st = "";
			for (int j = 1; j < keyval.length; j++)
			{
				st = st + keyval[j];
			}
			resultArgumentHash.put(keyval[0], st);
		}

		return resultArgumentHash;
	}

	/**
	 * argumentReader reads arguments from a file into a given hash table
	 * 
	 * @param fileName
	 *            the name of the file containing the arguments
	 * @param hashArgs
	 *            the hash table where to add the arguments
	 */
	public static void argumentReader(String fileName, Hashtable<String, String> hashArgs)
	{
		Hashtable<String, String> resultArgumentHash = argumentReader(fileName);
		Enumeration<String> enumst = resultArgumentHash.keys();
		while (enumst.hasMoreElements())
		{
			String key = enumst.nextElement();
			String value = resultArgumentHash.get(key);
			hashArgs.put(key, value);
		}
		return;
	}

	/**
	 * argumentReader stores a string array which is parameter encoded as
	 * key=value in a hash table the behavior is a follows<br>
	 * key starts with - ("-key") then the following string is the value of the
	 * key and stores as (key, value)<br>
	 * Exception is if the value after the key starts again with "-" the key is
	 * stored as (key, key) in the hash table<br>
	 * otherwise it is stored as (i, string) where i the string is the i-th
	 * element in the string array
	 * 
	 * @param args
	 *            a string array which is parameter as described above
	 * @return the arguments in a hash table
	 */
	@SuppressWarnings("unchecked")
	public static Hashtable<String, String> argumentReader(String[] args)
	{
		@SuppressWarnings("rawtypes")
		Hashtable arguments = new Hashtable();

		if (args == null)
			return arguments;

		for (int i = 0; i < args.length; i++)
		{
			String key = args[i];
			if (key.startsWith("-"))
			{
				// check next parameter
				if ((i + 1) == args.length)
				{
					arguments.put(key, key); // last param
				}
				else
				{
					if (args[i + 1].startsWith("-"))
					{
						arguments.put(key, key);
					}
					else
					{
						String value = args[i + 1];
						arguments.put(key, value);
						i++;
						if ((i + 1) == args.length)
							break;
					}
				}

			}
			else
			{
				arguments.put("" + i, key); // just copy as i-th parameter
			}
		}

		System.out.println("Parameters read: " + arguments.size());
		Enumeration<String> enumh = arguments.keys();
		int k = 0;
		while (enumh.hasMoreElements())
		{
			String key = enumh.nextElement();
			String value = (String) arguments.get(key);
			if (key.startsWith("-"))
			{
				System.out.println(k + ":" + key + "=" + value);
				k++;
			}
			else
			{
				System.out.println(k + ":" + key + "=" + value);
				k++;
			}
		}

		return arguments;
	}

	/**
	 * argumentReader stores a string array which is parameter encoded as
	 * key=value in a hash table the behavior is a follows<br>
	 * key starts with - ("-key") then the following string is the value of the
	 * key and stores as (key, value)<br>
	 * Exception is if the value after the key starts again with "-" the key is
	 * stored as (key, key) in the hash table<br>
	 * otherwise it is stored as (i, string) where i the string is the i-th
	 * element in the string array
	 * 
	 * @param args
	 *            a string array which is parameter as described above
	 * @param combineArgs
	 *            if true then multiple arguments following -key arg1 arg2 arg3
	 *            are concatenated into a string "arg1;arg2;arg3" otherwise
	 *            default behavior (multiple arguments get just a key counter as
	 *            key
	 * @return the arguments in a hash table
	 */
	@SuppressWarnings("unchecked")
	public static Hashtable<String, String> argumentReader(String[] args, boolean combineArgs)
	{
		if (combineArgs == false)
			return argumentReader(args);

		@SuppressWarnings("rawtypes")
		Hashtable arguments = new Hashtable();

		if (args == null)
			return arguments;

		String oldkey = "";
		for (int i = 0; i < args.length; i++)
		{
			String key = args[i];
			if (key.startsWith("-"))
			{
				// check next parameter
				if ((i + 1) == args.length)
				{
					arguments.put(key, key); // last param
				}
				else
				{
					if (args[i + 1].startsWith("-"))
					{
						arguments.put(key, key);
					}
					else
					{
						String value = args[i + 1];
						arguments.put(key, value);
						i++;
						if ((i + 1) == args.length)
							break;
					}
				}
				oldkey = key;
			}
			else
			{
				if (arguments.containsKey(oldkey))
					arguments.put(oldkey, arguments.get(oldkey) + argumentsConcatenationString + key);
				else
					arguments.put(oldkey, key);
			}

		}

		System.out.println("Parameters read: " + arguments.size());
		Enumeration<String> enumh = arguments.keys();
		int k = 0;
		while (enumh.hasMoreElements())
		{
			String key = enumh.nextElement();
			String value = (String) arguments.get(key);
			if (key.startsWith("-"))
			{
				System.out.println(k + ":" + key + "=" + value);
				k++;
			}
			else
			{
				System.out.println(k + ":" + key + "=" + value);
				k++;
			}
		}

		return arguments;
	}

	/**
	 * checkUTF8Normal checks if an xml file is UTF-8 encoded; looks
	 * "encoding=\"UTF-8\""
	 * 
	 * @param file
	 *            the file to check
	 * @return String "UTF-8-Nobom" if UTF-8 xml file, otherwise string "Nobom"
	 *         otherwise
	 */
	public static String checkUTF8Normal(String file)
	{
		String encoding = "Nobom";
		int iReadsize = 1000;
		byte bom[] = new byte[iReadsize];

		try
		{
			FileInputStream fiin = new FileInputStream(file);

			int n = fiin.read(bom, 0, bom.length);
			if (n == -1)
				return encoding;

			String string = new String(bom, "UTF-8");
			int iPos = string.indexOf("encoding=\"UTF-8\"");
			if (iPos > -1)
			{
				fiin.close();
				return "UTF-8-Nobom";
			}

			fiin.close();

			return encoding;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return "Exception";
		}
	}

	/**
	 * convertToDuration converts a duration to a time string
	 * 
	 * @param duration
	 *            the duration time in seconds
	 * @return the time as a string xxh:mm:yys
	 */
	public static String convertToDuration(long duration)
	{
		String stduration = "";
		if (duration == 0)
			return "00h:00m:00s";
		if (duration < 0)
		{
			stduration = convertToDuration(duration * (-1));
			stduration = "-" + stduration;
		}

		long hour = duration / 3600;
		stduration = stduration + hour + "h:";
		long minutes = duration / 60 - hour * 60;
		stduration = stduration + minutes + "m:";
		long seconds = duration - hour * 3600 - minutes * 60;
		stduration = stduration + seconds + "s";

		return stduration;
	}

	/**
	 * copyFile make a copy of a file
	 * 
	 * @param inFile
	 *            file to be copies
	 * @param outFile
	 *            target file
	 ** @return true when successful
	 * @throws Exception
	 */
	public static boolean copyFile(File in, File out) throws Exception
	{
		try
		{
			FileInputStream fis = new FileInputStream(in);
			FileOutputStream fos = new FileOutputStream(out);
			byte[] buf = new byte[1024];
			int i = 0;
			while ((i = fis.read(buf)) != -1)
			{
				fos.write(buf, 0, i);
			}
			fis.close();
			fos.close();
			return true;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	/**
	 * copyFile make a copy of a file
	 * 
	 * @param inFile
	 *            file to be copies
	 * @param outFile
	 *            target file
	 * @return true when successful
	 * @throws Exception
	 */
	public static boolean copyFile(String inFile, String outFile) throws Exception
	{
		try
		{
			File in = new File(inFile);
			if (!in.exists())
				return false;
			File out = new File(outFile);
			return copyFile(in, out);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	/**
	 * copyFileToString - copies the content of a file to a string (byte copy
	 * usage) encoded as Base64
	 * 
	 * @param filename
	 *            the file to copy to the string
	 * @return the content of the file as a string
	 */
	public static String copyFileToString(String filename)
	{
		try
		{
			System.out.println("Copy file into string: \"" + filename + "\"");
			File ifile = new File(filename);
			if (!ifile.exists())
			{
				return "";
			}
			int filesize = (int) ifile.length();
			OpenTMSLogger.println(filename + " filesize: \"" + filesize + "\"");
			FileInputStream in = new FileInputStream(filename);
			String content = "";
			byte bytes[] = new byte[filesize];
			in.read(bytes, 0, filesize);
			in.close();
			content = new BASE64Encoder().encode(bytes);
			System.out.println("Copied " + content.length() + " characters to file into string: \"" + filename + "\"");
			return content;
		}
		catch (IOException ioex)
		{
			ioex.printStackTrace();
			System.out.println("Could not copy file into buffer: \"" + filename + "\"");
			return "";
		}
	}

	/**
	 * copyStringToFile copies a string to a file where the string is base64
	 * encoded
	 * 
	 * @param content
	 *            the base64 encoded string
	 * @param filename
	 *            output file name
	 * @return
	 */
	public static boolean copyStringToFile(String content, String filename)
	{
		try
		{
			OpenTMSLogger
					.println("Copy string to file: \"" + filename + "\" with length content = " + content.length());
			FileOutputStream fileout = new FileOutputStream(filename);
			byte[] bytes = new BASE64Decoder().decodeBuffer(content);
			OpenTMSLogger.println("Length in bytes BASE64 decoded: \"" + bytes.length + "\"");
			fileout.write(bytes);
			fileout.close();
			// fileout.finalize();
			return true;
		}
		catch (IOException ioex)
		{
			ioex.printStackTrace();
			OpenTMSLogger.println("Could not copy buffer into file: \"" + filename + "\"");
			return false;
		}
	}

	/**
	 * decodeBASE64 returns a decoded string from a Base64 encoded string
	 * 
	 * @param string
	 *            the encoded string
	 * @return the decoded string
	 */

	public static String decodeBASE64(String string)
	{
		try
		{
			byte[] dec = new sun.misc.BASE64Decoder().decodeBuffer(string);
			// Decode using utf-8
			return new String(dec, "UTF8");
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * determineBOMFromFile determines the format of a file; return values are:<br>
	 * 
	 * <pre>
	 * "UTF-8-Nobom" for a standard xml file UTF-8encoded
	 * "UTF-8"    Check: (bom[0] == (byte) 0xEF) && (bom[1] == (byte) 0xBB) && (bom[2] == (byte) 0xBF))
	 * "UTF-16BE" Check: ((bom[0] == (byte) 0xFE) && (bom[1] == (byte) 0xFF))
	 * "UTF-16LE" Check: ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE))
	 * "UTF-32BE" Check: ((bom[0] == (byte) 0x00) && (bom[1] == (byte) 0x00) && (bom[2] == (byte) 0xFE) && (bom[3] == (byte) 0xFF))
	 * "UTF-32LE" Check: ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE) && (bom[2] == (byte) 0x00) && (bom[3] == (byte) 0x00))
	 * "Nobom" otherwise
	 * </pre>
	 * 
	 * @param file
	 * @return the bom of the file as a string indicator
	 */
	public static String determineBOMFromFile(String file)
	{
		String encoding = "Nobom";
		byte bom[] = new byte[BOM_SIZE];

		try
		{
			FileInputStream fiin = new FileInputStream(file);

			int n = fiin.read(bom, 0, bom.length);
			if (n == -1)
				return encoding;

			if ((bom[0] == (byte) 0xEF) && (bom[1] == (byte) 0xBB) && (bom[2] == (byte) 0xBF))
			{
				encoding = "UTF-8";
			}
			else if ((bom[0] == (byte) 0xFE) && (bom[1] == (byte) 0xFF))
			{
				encoding = "UTF-16BE";
			}
			else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE))
			{
				encoding = "UTF-16LE";
			}
			else if ((bom[0] == (byte) 0x00) && (bom[1] == (byte) 0x00) && (bom[2] == (byte) 0xFE)
					&& (bom[3] == (byte) 0xFF))
			{
				encoding = "UTF-32BE";
			}
			else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE) && (bom[2] == (byte) 0x00)
					&& (bom[3] == (byte) 0x00))
			{
				encoding = "UTF-32LE";
			}
			else
			{
				fiin.close();
				return checkUTF8Normal(file);
			}

			fiin.close();

			return encoding;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return "Exception";
		}
	}
	
	
	/**
	 * encodeBASE64 returns a string as Base64 encoded string
	 * 
	 * @param string
	 *            string to encode
	 * @return the encoded string
	 */
	public static String encodeBASE64(String string)
	{
		byte[] bytes = null;
		try
		{
			bytes = string.getBytes("UTF8");
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
		return new BASE64Encoder().encode(bytes);
	}
	

	/**
	 * exceptionToString writes the stack trace to a string
	 * 
	 * @param e
	 *            - the exception to print
	 * @return the stack trace as string
	 */
	public static String exceptionToString(Exception e)
	{
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		String mess = sw.toString();
		pw.close();
		try
		{
			sw.close();
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
		}
		return mess;
	}

	/**
	 * fillParam
	 * 
	 * @param hash
	 * @param type
	 * @return value of the type
	 */
	public static String fillParam(@SuppressWarnings("rawtypes") Hashtable hash, String type)
	{
		try
		{
			Object val = hash.get(type);
			String value = "";
			if (val.getClass().getName().equals("java.lang.String"))
			{
				value = (String) hash.get(type);
			}
			else if (val.getClass().getName().equals("java.lang.Integer"))
			{
				Integer i = (Integer) hash.get(type);
				value = i.toString();
			}
			else
			{
				System.out.println("Object: " + val.getClass() + "::" + val.toString());
				value = val.toString();
			}
			if (value == null)
				return "";
			else
				return value;
		}
		catch (Exception ex)
		{
			return "";
		}
	}

	public static String getArgumentsConcatenationString()
	{
		return argumentsConcatenationString;
	}

	/**
	 * getCallingMethod
	 * 
	 * @param iLevel
	 * @return
	 */
	public static String getCallingMethod(int iLevel)
	{
		// return Thread.currentThread().getStackTrace()[3].getMethodName();
		StackTraceElement st[] = new Exception().getStackTrace();
		int l = st.length;
		/*
		 * for (int i = 0; i < l; i++) { StackTraceElement stact = st[i]; String
		 * x = stact.getMethodName(); String y = "" + stact.getLineNumber();
		 * String c = stact.getClassName(); LogPrint.println("getCallingMethod("
		 * + i + ")= Class" + c + " Method=" + x + " Line= " + y); }
		 */
		if (l > iLevel)
		{
			StackTraceElement stact = st[iLevel];
			String x = stact.getMethodName();
			if (x.equals("LogPrint") && (l > (iLevel + 1)))
			{
				stact = st[iLevel + 1];
				x = stact.getMethodName();
			}
			String y = "" + stact.getLineNumber();
			String c = stact.getClassName();
			return x + "|" + y + "|" + c + "|";
		}
		else
			return "|" + "|" + "|";
	}

	/**
	 * getCompileDate get the compile time of a class
	 * 
	 * @param classname
	 *            the class name
	 * @return the date of the compile time
	 */
	public static Date getCompileDate(@SuppressWarnings("rawtypes") Class classname)
	{
		try
		{
			String filename = "./bin/" + classname.getName().replace(".", "/") + ".class";
			File f = new File(filename);
			if (f.exists())
				return new Date(f.lastModified());
			else
			{
				File jarFileName = new File(classname.getProtectionDomain().getCodeSource().getLocation().toURI());
				if (jarFileName.exists())
				{
					String jarfilename = jarFileName.getAbsolutePath();
					try
					{
						JarFile jarFile = new JarFile(jarfilename);
						String fname = classname.getName();
						fname = fname.replace('.', '/') + ".class";
						// System.out.println("fname = " + fname);
						ZipEntry zip = jarFile.getEntry(fname);
						if (zip != null)
						{
							// System.out.println(jarfilename + " " +
							// zip.getName());
							long modtime = zip.getTime();
							Date date = new Date(modtime);
							return date;
						}
					}
					catch (Exception e)
					{
						// e.printStackTrace();
					}

					return new Date(jarFileName.lastModified());
				}
				return null;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * getCurrentDirectory return the current directory
	 * 
	 * @return the current directory name
	 */
	public static String getCurrentDirectory()
	{
		String currentDirectory = "";
		File dir1 = new File(".");
		try
		{
			currentDirectory = dir1.getCanonicalPath();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return currentDirectory;
		}
		return currentDirectory;
	}

	/**
	 * getCurrentMethod get the method of the currently executing method
	 * 
	 * @return the method called
	 */
	public static Method getCurrentMethod()
	{
		StackTraceElement stackTraceElements[] = (new Throwable()).getStackTrace();
		String methodName = stackTraceElements[1].toString();
		return getMethod(methodName, stackTraceElements.getClass());
	}

	/**
	 * getCurrentMethodName get the method name of the currently executing
	 * method
	 * 
	 * @return
	 */
	public static String getCurrentMethodName()
	{
		StackTraceElement stackTraceElements[] = (new Throwable()).getStackTrace();
		return stackTraceElements[1].toString();
	}

	/**
	 * getCurrentUser returns the currently set user =
	 * System.getProperty("user.name")
	 * 
	 * @return
	 */
	public static String getCurrentUser()
	{
		return System.getProperty("user.name");
	}

	/**
	 * getDateString - returns a date formated as 11.08.2008 23:54:23:1234
	 * 
	 * @return a date formated as 11.08.2008 23:54:23:1234
	 */
	public static String getDateString()
	{
		Calendar calendar = new GregorianCalendar();
		String datestring = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
		int imonth = calendar.get(Calendar.MONTH) + 1;
		datestring = datestring + "." + Integer.toString(imonth);
		datestring = datestring + "." + Integer.toString(calendar.get(Calendar.YEAR));
		datestring = datestring + " " + Integer.toString(calendar.get(Calendar.HOUR_OF_DAY));
		datestring = datestring + ":" + Integer.toString(calendar.get(Calendar.MINUTE));
		datestring = datestring + ":" + Integer.toString(calendar.get(Calendar.SECOND));
		datestring = datestring + ":" + Integer.toString(calendar.get(Calendar.MILLISECOND));
		return datestring;
	}

	/**
	 * getDateStringFine returns date as 110820082354231234 (see also
	 * getDateString)
	 * 
	 * @return date as 110820082354231234 (see also getDateString)
	 */
	public static String getDateStringFine()
	{
		Calendar calendar = new GregorianCalendar();
		String datestring = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
		int imonth = calendar.get(Calendar.MONTH) + 1;
		datestring = datestring + Integer.toString(imonth);
		datestring = datestring + Integer.toString(calendar.get(Calendar.YEAR));
		datestring = datestring + Integer.toString(calendar.get(Calendar.HOUR_OF_DAY));
		datestring = datestring + Integer.toString(calendar.get(Calendar.MINUTE));
		datestring = datestring + Integer.toString(calendar.get(Calendar.SECOND));
		datestring = datestring + Integer.toString(calendar.get(Calendar.MILLISECOND));
		return datestring;
	}

	/**
	 * readLanguageCodesFromFile read the language codes from a file
	 * 
	 * @param fileName
	 *            the file name to use
	 * @return a hash table key short code - value long language code - Key:
	 *         Abkhazian ab Value: ab
	 */
	public static String[] getItemsFromFile(String fileName)
	{
		String items[] = null;
		Vector<String> itemsVector = new Vector<String>();
		File file = new File(fileName);
		if (!file.exists())
		{
			return null;
		}

		try
		{
			File extractfile = new File(fileName);
			Reader fiin = new InputStreamReader(new FileInputStream(extractfile), "UTF-8");
			BufferedReader finstream = new BufferedReader(fiin);

			String line = null;
			while ((line = finstream.readLine()) != null)
			{
				itemsVector.add(line);
			}

			finstream.close();
			fiin.close();

			items = new String[itemsVector.size()];
			for (int i = 0; i < itemsVector.size(); i++)
			{
				items[i] = itemsVector.get(i);
			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}

		return items;
	}

	/**
	 * getMethod get the method for a given Class
	 * 
	 * @param methodName
	 *            the method name as string
	 * @param classformethod
	 *            the class to search for method
	 * @return the method of the class
	 */
	public static Method getMethod(String methodName, @SuppressWarnings("rawtypes") Class classformethod)
	{
		Method[] theMethods = classformethod.getMethods();
		for (int i = 0; i < theMethods.length; i++)
		{
			String methodString = theMethods[i].getName();
			if (methodString.equals(methodName))
				return theMethods[i];
		}
		return null;
	}

	/**
	 * iGetThreadNumber returns the number of threads to use
	 * min(Runtime.getRuntime().availableProcessors(), iMaxThreads)
	 * 
	 * @param iMaxThreads
	 *            maximum number of threads
	 * @return threads possible to use
	 */
	public static int iGetThreadNumber(int iMaxThreads)
	{
		int iProcessors = Runtime.getRuntime().availableProcessors();

		if (iMaxThreads == 0) // 0 indicates use just one thread
			iProcessors = 0;
		if (iMaxThreads != -1)
		{
			if (iMaxThreads < iProcessors) // only as max threads as processors
				iProcessors = iMaxThreads;
		}
		return iProcessors;
	}

	public static void printParameterInfoFor(Method method)
	{
		if (method == null)
			return;
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();

		for (int parameterIndex = 0; parameterIndex < parameterAnnotations.length; parameterIndex++)
		{

			Class<?> parameterType = method.getParameterTypes()[parameterIndex];
			WebParam namedAnnotation = null;
			for (Annotation annotation : parameterAnnotations[parameterIndex])
			{
				if (annotation.annotationType().equals(WebParam.class))
				{
					namedAnnotation = (WebParam) annotation;
					break;
				}
			}
			System.out.println(String.format(
					"The parameter at index: %s of Method: %s with parameter type: %s is named by: %s", parameterIndex,
					method, parameterType, namedAnnotation.name()));
		}
	}

	/**
	 * quoteForCSVSExport quotes a String for CSV export <br>
	 * LF = \n CR = \r tab = \t \ = \\
	 * 
	 * @param string
	 *            the string to replace the characters in
	 * @return the quoted string
	 */
	public static String quoteForCSVSExport(String string)
	{
		if (string == null)
			return "\\N";
		String retString = string;

		retString = retString.replaceAll("\r", "\\r");
		retString = retString.replaceAll("\n", "\\n");
		retString = retString.replaceAll("\t", "\\t");
		retString = retString.replaceAll("\\\\", "\\\\");

		return retString;
	}

	/**
	 * quoteForCSVSExport quotes a String for CSV export <br>
	 * LF = \n CR = \r tab = \t \ = \\
	 * 
	 * @param string
	 *            the string to replace the characters in
	 * @param nullValue
	 *            the string to use for null value
	 * @return the quoted string
	 */
	public static String quoteForCSVSExport(String string, String nullValue)
	{
		if (string == null)
			return nullValue;
		String retString = string;

		retString = retString.replaceAll("\r", "\\r");
		retString = retString.replaceAll("\n", "\\n");
		retString = retString.replaceAll("\t", "\\t");
		retString = retString.replaceAll("\\\\", "\\\\");

		return retString;
	}

	/**
	 * readFileIntoString reads the contents of a file into a string
	 * 
	 * @param encoding
	 *            the encoding to use; if null UTF-8 is assumed
	 * @return the file content as a string
	 */
	public static String readFileIntoString(String filename, String encoding)
	{
		if (encoding == null)
			encoding = "UTF-8";
		try
		{
			BufferedReader sourceBuffer = new BufferedReader(new InputStreamReader(new FileInputStream(filename),
					encoding));
			StringBuffer sourceLine = new StringBuffer(1000);

			char[] buf = new char[1024];
			int numRead = 0;
			while ((numRead = sourceBuffer.read(buf)) != -1)
			{
				String readData = String.valueOf(buf, 0, numRead);
				sourceLine.append(readData);
				buf = new char[1024];
			}
			sourceBuffer.close();
			return sourceLine.toString();

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return "";
	}

	/**
	 * readFileIntoVector
	 * 
	 * @param encoding
	 * @return
	 */
	public static Vector<String> readFileIntoVector(String filename, String encoding)
	{
		Vector<String> result = new Vector<String>();
		if (encoding == null)
			encoding = "UTF-8";
		try
		{
			BufferedReader sourceBuffer = new BufferedReader(new InputStreamReader(new FileInputStream(filename),
					encoding));
			String sourceLine = "";
			while (((sourceLine = sourceBuffer.readLine()) != null))
			{
				sourceLine = sourceLine.replaceAll("\n", "");
				sourceLine = sourceLine.replaceAll("\r", "");
				result.add(sourceLine);
			}
			sourceBuffer.close();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return result;
	}

	/**
	 * removeBOMFromFile removes the bom indicators from a file; problem is that
	 * Java does not handle them correctly but reads them...
	 * 
	 * @param file
	 *            the file to check for the bom and possible remove it
	 * @return the file name
	 */
	public static String removeBOMFromFile(String file)
	{
		@SuppressWarnings("unused")
		String encoding = "";
		;
		byte bom[] = new byte[BOM_SIZE];

		try
		{
			FileInputStream fiin = new FileInputStream(file);

			int n, unread;
			n = fiin.read(bom, 0, bom.length);

			if ((bom[0] == (byte) 0xEF) && (bom[1] == (byte) 0xBB) && (bom[2] == (byte) 0xBF))
			{
				encoding = "UTF-8";
				unread = 3;
			}
			else if ((bom[0] == (byte) 0xFE) && (bom[1] == (byte) 0xFF))
			{
				encoding = "UTF-16BE";
				unread = 2;
			}
			else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE))
			{
				encoding = "UTF-16LE";
				unread = 2;
			}
			else if ((bom[0] == (byte) 0x00) && (bom[1] == (byte) 0x00) && (bom[2] == (byte) 0xFE)
					&& (bom[3] == (byte) 0xFF))
			{
				encoding = "UTF-32BE";
				unread = 4;
			}
			else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE) && (bom[2] == (byte) 0x00)
					&& (bom[3] == (byte) 0x00))
			{
				encoding = "UTF-32LE";
				unread = 4;
			}
			else
			{
				// Unicode BOM mark not found, unread all bytes
				// no need to remove anything
				unread = n;
				fiin.close();
				return file;
			}
			// ok here we must copy now the rest of the file to a new file...
			fiin.close();

			fiin = new FileInputStream(file);

			// read first unread chars
			fiin.read(bom, 0, unread);
			// now copy rest of the file

			FileOutputStream fiout = new FileOutputStream(file + ".nobom");

			while (true)
			{
				int i = fiin.read();
				if (i == -1)
					break;
				fiout.write(i);
			}
			fiin.close();
			fiout.close();
			return file + ".nobom";
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return file;
		}
	}

	/**
	 * replaceStringInFile replaces a given string with another string in a file
	 * 
	 * @param filename
	 *            filename where replacements should be done
	 * @param oldPattern
	 *            the old pattern
	 * @param replPattern
	 *            the new pattern
	 */
	public static void replaceStringInFile(String filename, String oldPattern, String replPattern)
	{
		String line;
		StringBuffer sb = new StringBuffer();
		@SuppressWarnings("unused")
		int iLines = 0;
		int iChanges = 0;

		// first make a copy of the original file
		String keepOrigFile = filename + ".bak";

		@SuppressWarnings("unused")
		String oldfilename = filename;
		@SuppressWarnings("unused")
		String replfile = filename.replaceAll(oldPattern, replPattern); // replace
		// the
		// file
		// name
		// if (!replfile.equals(filename))
		// filename = replfile;

		// LogPrint.printPure("Filename: " + filename + " (" + oldfilename +
		// ") oldPattern=" + oldPattern + " replPattern=" + replPattern + "\n");
		try
		{
			@SuppressWarnings("unused")
			boolean bCopied = copyFile(filename, keepOrigFile);
			FileInputStream fis = new FileInputStream(filename);
			BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
			while ((line = reader.readLine()) != null)
			{
				String oldline = line;
				iLines++;
				line = line.replaceAll(oldPattern, replPattern);
				if (!line.equals(oldline))
					iChanges++;
				sb.append(line + "\n");
				oldline = null;
			}
			reader.close();
			if (iChanges > 0)
			{
				BufferedWriter out = new BufferedWriter(new FileWriter(filename));
				out.write(sb.toString());
				out.close();
			}
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * replaceStringInFile replaces a given string from an array with another
	 * string array in a file.
	 * 
	 * @param filename
	 *            filename where replacements should be done
	 * @param oldPattern
	 *            the old pattern array
	 * @param replPattern
	 *            the new pattern array; oldPattern size must be equals to
	 *            replPattern size
	 */
	public static void replaceStringInFile(String filename, String oldPattern[], String replPattern[])
	{
		String line;
		StringBuffer sb = new StringBuffer();
		@SuppressWarnings("unused")
		int iLines = 0;
		int iChanges = 0;

		if (oldPattern.length != replPattern.length)
			return;

		// first make a copy of the original file
		String keepOrigFile = filename + ".bak";

		@SuppressWarnings("unused")
		String oldfilename = filename;
		// String replfile = filename.replaceAll(oldPattern, replPattern); //
		// replace
		// the
		// file
		// name
		// if (!replfile.equals(filename))
		// filename = replfile;

		// LogPrint.printPure("Filename: " + filename + " (" + oldfilename +
		// ") oldPattern=" + oldPattern + " replPattern=" + replPattern + "\n");
		try
		{
			@SuppressWarnings("unused")
			boolean bCopied = copyFile(filename, keepOrigFile);
			FileInputStream fis = new FileInputStream(filename);
			BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
			while ((line = reader.readLine()) != null)
			{
				String oldline = line;
				iLines++;
				for (int i = 0; i < oldPattern.length; i++)
				{
					line = line.replaceAll(oldPattern[i], replPattern[i]);
				}
				if (!line.equals(oldline))
					iChanges++;
				sb.append(line + "\n");
				oldline = null;
			}
			reader.close();
			if (iChanges > 0)
			{
				BufferedWriter out = new BufferedWriter(new FileWriter(filename));
				out.write(sb.toString());
				out.close();
			}
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * replaceStringInFile replaces a given string with another string in a file
	 * 
	 * @param filename
	 *            filename where replacements should be done
	 * @param oldPattern
	 *            the old pattern
	 * @param replPattern
	 *            the new pattern
	 * @param unicodeEncoding
	 */
	public static void replaceStringInFile(String filename, String oldPattern, String replPattern,
			boolean bWritePerLine, String unicodeEncoding)
	{
		String line;
		StringBuffer sb = new StringBuffer();
		@SuppressWarnings("unused")
		int iLines = 0;
		int iChanges = 0;

		// first make a copy of the original file
		String keepOrigFile = filename + ".bak";

		@SuppressWarnings("unused")
		String oldfilename = filename;

		try
		{

			@SuppressWarnings("unused")
			boolean bCopied = copyFile(filename, keepOrigFile);
			FileInputStream fis = new FileInputStream(keepOrigFile);
			BufferedReader reader = new BufferedReader(new InputStreamReader(fis, unicodeEncoding));
			BufferedWriter out = null;
			if (bWritePerLine)
			{
				out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), unicodeEncoding));
			}
			while ((line = reader.readLine()) != null)
			{
				String oldline = line;
				iLines++;
				line = line.replaceAll(oldPattern, replPattern);
				if (!line.equals(oldline))
					iChanges++;

				if (bWritePerLine)
				{
					out.write(line + "\n");
				}
				else
				{
					sb.append(line + "\n");
				}
				oldline = null;
			}
			reader.close();
			if (!bWritePerLine)
			{
				if (iChanges > 0)
				{
					out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), unicodeEncoding));
					out.write(sb.toString());
					out.close();
				}
			}
			else
			{
				out.close();
			}
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * replaceStringInFile replaces a given string with another string in a file
	 * using MULTILINE regexp expressions
	 * 
	 * @param filename
	 *            filename where replacements should be done
	 * @param oldPattern
	 *            the old pattern
	 * @param replPattern
	 *            the new pattern
	 */
	public static void replaceStringInFileMultiLine(String filename, String oldPattern, String replPattern)
	{
		String line;
		@SuppressWarnings("unused")
		int iLines = 0;
		@SuppressWarnings("unused")
		int iChanges = 0;

		// first make a copy of the original file
		String keepOrigFile = filename + ".bak";

		String oldfilename = filename;
		String replfile = filename.replaceAll(oldPattern, replPattern); // replace
		// the
		// file
		// name
		if (!replfile.equals(filename))
			filename = replfile;

		OpenTMSLogger.println("Filename: " + filename + " (" + oldfilename + ") oldPattern=" + oldPattern
				+ " replPattern=" + replPattern + " -multline\n");
		try
		{
			@SuppressWarnings("unused")
			boolean bCopied = copyFile(filename, keepOrigFile);
			FileInputStream fis = new FileInputStream(filename);
			BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
			String myLine = "";
			int iLenEnd = 61299;
			char c = (char) iLenEnd;
			while ((line = reader.readLine()) != null)
			{
				iLines++;
				// line = line.replaceAll(oldPattern, replPattern);
				myLine = myLine + line + c; // "krwalbnasdajksdjasjd";
			}

			// Pattern pat = Pattern.compile(oldPattern, Pattern.DOTALL);
			// Matcher mat = pat.matcher(myLine);
			// myLine = mat.replaceAll(replPattern);

			myLine = myLine.replaceAll(oldPattern, replPattern);
			myLine = myLine.replaceAll(c + "", "\n");

			// myLine = myLine.replaceAll(oldPattern, replPattern);
			reader.close();
			// if (iChanges > 0)
			{
				BufferedWriter out = new BufferedWriter(new FileWriter(filename));
				out.write(myLine);
				out.close();
			}
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Method determines the BOM (Byte Order Mark of a file and returns it
	 * @param file
	 * @return the BOM of a file
	 */
	public static byte[] returnBOMFromFile(String file)
	{
		byte[] encoding = null;
		byte bom[] = new byte[BOM_SIZE];

		try
		{
			FileInputStream fiin = new FileInputStream(file);

			int n = fiin.read(bom, 0, bom.length);
			if (n == -1)
				return null;

			if ((bom[0] == (byte) 0xEF) && (bom[1] == (byte) 0xBB) && (bom[2] == (byte) 0xBF))
			{
				encoding = new byte[3];
				encoding[0] = bom[0];
				encoding[1] = bom[1];
				encoding[2] = bom[2];
			}
			else if ((bom[0] == (byte) 0xFE) && (bom[1] == (byte) 0xFF))
			{
				encoding = new byte[2];
				encoding[0] = bom[0];
				encoding[1] = bom[1];
			}
			else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE))
			{
				encoding = new byte[2];
				encoding[0] = bom[0];
				encoding[1] = bom[1];
			}
			else if ((bom[0] == (byte) 0x00) && (bom[1] == (byte) 0x00) && (bom[2] == (byte) 0xFE)
					&& (bom[3] == (byte) 0xFF))
			{
				encoding = new byte[4];
				encoding[0] = bom[0];
				encoding[1] = bom[1];
				encoding[2] = bom[2];
				encoding[3] = bom[3];
			}
			else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE) && (bom[2] == (byte) 0x00)
					&& (bom[3] == (byte) 0x00))
			{
				encoding = new byte[4];
				encoding[0] = bom[0];
				encoding[1] = bom[1];
				encoding[2] = bom[2];
				encoding[3] = bom[3];
			}
			else
			{
				encoding = null;
			}

			fiin.close();

			return encoding;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}


	/**
	 * returnCurrentDate
	 * 
	 * @return current date string padded with "0" if length of month etc. == 1
	 *         28.03.2003 04:06:07
	 */
	public static String returnCurrentDate()
	{
		GregorianCalendar cal = new GregorianCalendar();
		TimeZone tz = TimeZone.getTimeZone("UTC");

		tz = TimeZone.getDefault();

		cal.setTimeZone(tz);
		String month = "" + (cal.get(Calendar.MONTH) + 1);
		if (month.length() == 1)
			month = "0" + month;
		String day = "" + cal.get(Calendar.DAY_OF_MONTH);
		if (day.length() == 1)
			day = "0" + day;
		String hour = "" + cal.get(Calendar.HOUR_OF_DAY);
		if (hour.length() == 1)
			hour = "0" + hour;
		String minute = "" + cal.get(Calendar.MINUTE);
		if (minute.length() == 1)
			minute = "0" + minute;
		String second = "" + cal.get(Calendar.SECOND);
		if (second.length() == 1)
			second = "0" + second;
		String lcreationdate = day + "." + month + "." + cal.get(Calendar.YEAR) + " " + hour + ":" + minute + ":"
				+ second;
		return lcreationdate;
	}

	public static void setArgumentsConcatenationString(String argumentsConcatenationString)
	{
		OpenTMSSupportFunctions.argumentsConcatenationString = argumentsConcatenationString;
	}

	/**
	 * simpleCopyStringToFile copies a string to a file where the string is
	 * base64 encoded
	 * 
	 * @param content
	 *            the base64 encoded string
	 * @param filename
	 *            output file name
	 * @return
	 */
	public static boolean simpleCopyStringToFile(String content, String filename)
	{
		try
		{
			OpenTMSLogger
					.println("Copy string to file: \"" + filename + "\" with length content = " + content.length());
			// OpenTMSLogger.println(content);

			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "UTF8"));

			out.write(content);

			out.close();

			/*
			 * FileOutputStream fileout = new FileOutputStream(filename); byte[]
			 * bytes = content.getBytes();
			 * OpenTMSLogger.println("Length in bytes: \"" + bytes.length +
			 * "\""); fileout.write(bytes); fileout.close(); //
			 * fileout.finalize();
			 */
			return true;
		}
		catch (IOException ioex)
		{
			ioex.printStackTrace();
			OpenTMSLogger.println("Could not copy buffer into file: \"" + filename + "\"");
			return false;
		}
	}

	/** Write a bom to a file
	 * @param filename the filename
	 * @param bom the bom to write to the file
	 * @return true if successfule
	 */
	public static boolean writeBOMToFile(String filename, byte[] bom)
	{
		boolean bSuccess = false;
		try
		{
			FileInputStream fiin = new FileInputStream(filename);
			FileChannel ch = fiin.getChannel();
	        int size = (int) ch.size();
			byte[] content = new byte[(int)size];
			@SuppressWarnings("unused")
			int n = fiin.read(content , 0, size);
			fiin.close();
			FileOutputStream fout = new FileOutputStream(filename + ".temp");
			fout.write(bom);
			fout.write(content);
			fout.close();
			File fin = new File(filename);
			bSuccess = fin.delete();
			if (bSuccess)
			{
				File dest = new File(filename + ".temp");
				bSuccess =  dest.renameTo(fin);
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			bSuccess = false; 
		}
		return bSuccess;
	}

	/**
	 * writeToOut
	 * 
	 * @param out
	 * @param string
	 * 
	 */
	public static void writeToOut(DataOutputStream out, String string)
	{
		try
		{
			for (int i = 0; i < string.length(); i++)
			{
				char c = string.charAt(i);
				byte byte0 = (byte) (c & 0xff);
				byte byte1 = (byte) (c >> 8 & 0xff);
				out.writeByte(byte0);
				out.writeByte(byte1);
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	private Vector<File> filevector;

	/**
	 * initial method to start getting all files in for a directory and its sub
	 * directories
	 * 
	 * @param file
	 *            directory where the search should start from
	 * @return the vector containing all the file names found
	 */
	public Vector<String> getAllFiles(String sourcepath)
	{
		filevector = new Vector<File>();
		File dir = new File(sourcepath);
		getAllRecFiles(dir, sourcepath);
		Vector<String> filenames = new Vector<String>();
		for (int i = 0; i < filevector.size(); i++)
		{
			filenames.add(filevector.get(i).getAbsolutePath());
		}
		return filenames;
	}

	/**
	 * Get all the files in the directory (file) specified and its sub
	 * directories sourcepath is the start path of file (to determine the
	 * absolute pathname for a file
	 * 
	 * @param file
	 *            directory where the search should start from
	 * @param sourcepath
	 *            sourcepath is the start path of file (to determine the
	 *            absolute pathname for a file)
	 */
	public void getAllRecFiles(File file, String sourcepath)
	{
		if (file.isDirectory())
		{
			String[] dircontents = file.list();
			for (int i = 0; i < dircontents.length; i++)
			{
				String filename = sourcepath + "/" + dircontents[i];
				File snfile = new File(filename);
				if (snfile.isDirectory())
					getAllRecFiles(snfile, filename);
				else if (snfile.isFile())
					filevector.add(snfile);
			}
		}
		else if (file.isFile())
			filevector.add(file);
		return;
	}

	/**
	 * getVersionfinal from
	 * http://stackoverflow.com/questions/1917686/compile-date-and-time
	 * 
	 * @param classe
	 * @return
	 */
	public String getVersionfinal(@SuppressWarnings("rawtypes") Class classe)
	{
		String version = null;
		String shortClassName = classe.getName().substring(classe.getName().lastIndexOf(".") + 1);
		try
		{
			ClassLoader cl = this.getClass().getClassLoader();
			String threadContexteClass = classe.getName().replace('.', '/');
			URL url = cl.getResource(threadContexteClass + ".class");
			if (url == null)
			{
				version = shortClassName + " $ (no manifest)";
			}
			else
			{
				String path = url.getPath();
				String jarExt = ".jar";
				int index = path.indexOf(jarExt);
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				if (index != -1)
				{
					String jarPath = path.substring(0, index + jarExt.length());
					File file = new File(jarPath);
					String jarVersion = file.getName();
					JarFile jarFile = new JarFile(new File(new URI(jarPath)));
					JarEntry entry = jarFile.getJarEntry("META-INF/MANIFEST.MF");
					version = shortClassName + " $ " + jarVersion.substring(0, jarVersion.length() - jarExt.length())
							+ " $ " + sdf.format(new Date(entry.getTime()));
					jarFile.close();
				}
				else
				{
					File file = new File(path);
					version = shortClassName + " $ " + sdf.format(new Date(file.lastModified()));
				}
			}
		}
		catch (Exception e)
		{
			version = shortClassName + " $ " + e.toString();
		}
		return version;
	}
}
