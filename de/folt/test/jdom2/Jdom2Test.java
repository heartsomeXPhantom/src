package de.folt.test.jdom2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import org.jdom2.output.Format;
import org.jdom2.output.Format.TextMode;
import org.jdom2.output.LineSeparator;
import org.jdom2.output.XMLOutputter;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaders;

public class Jdom2Test
{

	public static void main(String[] args)
	{
		// String infilename = "d:/eclipse/workspace/openTMS/test/TermTagger/softreturn/test.xlf";
		// if (args.length > 0)
		//	infilename = args[0];

		test("d:/eclipse/workspace/openTMS/test/TermTagger/softreturn/test.xlf");
		test("d:/eclipse/workspace/openTMS/test/TermTagger/softreturn/testcrlf.xlf");
		test("d:/eclipse/workspace/openTMS/test/TermTagger/softreturn/test-seg51.sdlxliff");
	}

	/**
	 * @param args
	 */
	public static void test(String infilename)
	{
		// TODO Auto-generated method stub
		try
		{
			File f = new File(infilename);
			f = new File(f.getAbsolutePath());
			SAXBuilder builder = new SAXBuilder(XMLReaders.DTDVALIDATING);
			// builder.setValidation(false);
			builder.setFeature("http://xml.org/sax/features/validation", false);
			builder.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
			builder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			builder.setExpandEntities(true);
			// builder.setEntityResolver(new XmlEntityResolver());
			String filename = f.getAbsolutePath();
			// URI uri = new URI("file://" + infilename);
			Document document = builder.build(f);
			File outFile = new File(filename + "new.xlf");
			saveToXmlFile(outFile, document);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static boolean saveToXmlFile(File outFile, Document document)
	{
		try
		{

			FileOutputStream outStream = new FileOutputStream(outFile);
			OutputStreamWriter outwriter = new OutputStreamWriter(outStream, "UTF-8");

			XMLOutputter xmlOutputter = new XMLOutputter(); // 02.12.2013
			Format fnew = xmlOutputter.getFormat();
			fnew.setLineSeparator(LineSeparator.NONE);
			fnew.setTextMode(TextMode.PRESERVE);
			xmlOutputter.setFormat(fnew);
			xmlOutputter.output(document, outwriter);
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

}
