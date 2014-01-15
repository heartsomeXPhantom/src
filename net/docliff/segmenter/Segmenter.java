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
import java.util.Vector;

import com.araya.eaglememex.util.EMXProperties;
import com.araya.eaglememex.util.TTConstants;

import de.folt.constants.OpenTMSConstants;
import de.folt.models.documentmodel.xliff.XliffDocument;
import de.folt.util.OpenTMSException;
import de.folt.util.OpenTMSProperties;

public class Segmenter
{
	static void usage()
	{
		System.out.println("Usage:\n" + "    java -cp converters.jar com.hscl.eaglememex.segmenter.Segmenter "
				+ "<Parameter file> [output file name] [-p properties file]\n");
	}

	// run with supllied Xliff
	public static void run(XliffDocument doc, String outputFileName, boolean enableSegRule, int breakOnCrlf) throws OpenTMSException
	{
		String iniFile = EMXProperties.getInstance().getEMXProperty(TTConstants.INIT_SEGMENTER);
		SegmentProcess segProc = new SegmentProcess(doc, iniFile, enableSegRule, breakOnCrlf);
		segProc.process();
		System.out.println("Creating Segmented File" + outputFileName);
		// doc.print(outputFileName);
		doc.saveToXmlFile(new File(outputFileName));

		doc = null;
		segProc = null;
		iniFile = null;
	}

	public static Vector run(String inputFileName, String outputFileName, String iniFileName, String logFileName)
	{
		System.out.println("beologisch segmenter 1.0");
		Vector err = new Vector();
		try
		{
			String iniFile = EMXProperties.getInstance().getEMXProperty(TTConstants.INIT_SEGMENTER);
			System.out.println("Open Xliff Document" + inputFileName);
			XliffDocument doc = new XliffDocument(new File(inputFileName));
			SegmentProcess segProc = new SegmentProcess(doc, iniFile);
			segProc.process();
			System.out.println("Creating Segmented File" + outputFileName);
			// doc.print(outputFileName);
			doc.saveToXmlFile(new File(outputFileName));

			doc = null;
			segProc = null;
			iniFile = null;
		}
		catch (OpenTMSException ex)
		{
			err.add(OpenTMSConstants.OpenTMS_EXCEPTION_ERROR);
		}
		err.add(OpenTMSConstants.OpenTMS_ID_SUCCESS);
		return err;
	}

	public static Vector run(String[] args)
	{
		System.out.println("beologisch segmenter 1.0");
		String paraFileName = args[0];
		Vector err = new Vector();
		try
		{
			// Here get paramet from parameter file
			// ParaDocument para = new ParaDocumentImpl(new File(paraFileName));
			// String iniFile = para.getInitFileName();
			String iniFile = OpenTMSProperties.getInstance().getOpenTMSProperty("INIT_SEGMENTER");
			System.out.println("Initialize File" + iniFile);
			String outFile = null;

			for (int i = 1; i < args.length; i++)
			{
				if (args[i].equalsIgnoreCase("-p"))
				{
					if ((i + 1) < args.length)
					{
						// new EMXProperties(args[++i]);
					}
					else
					{
						usage();
						return err;
					}
				}
				else
				{
					outFile = args[i];
				}
			}
			if (EMXProperties.getInstance() == null)
			{
				String errorCode = OpenTMSConstants.OpenTMS_ID_FAILURE + "";
				err.add(errorCode);
				return err;
			}
			
			if (outFile == null)
				outFile = ""; // para.getOutputFileName();

			// InputFileProperties inputProps = para.getInputFileProps();

			// para = null;

			// Here get values from inputfile properties
			// String inFile = inputProps.getFileName();
			// String srcLanguage = inputProps.getLanguage();
			// String encoding = inputProps.getEncoding();
			// System.out.println("Open Xliff Document" + inFile);
			String inFile ="";
			XliffDocument doc = new XliffDocument(new File(inFile));
			SegmentProcess segProc = new SegmentProcess(doc, "init_segmenter.xml", true, 0);
			segProc.process();
			System.out.println("Creating Segmented File" + outFile);
			// doc.print(outFile);
			doc.saveToXmlFile(new File(outFile));

			String inputProps = null;
			inFile = null;
			String srcLanguage = null;
			String encoding = null;
			doc = null;
			segProc = null;
			iniFile = null;
			outFile = null;

		}
		catch (OpenTMSException ex)
		{
			return err;
		}
		return err;
	}

	public static void main(String[] args)
	{
		if (args.length == 0)
		{
			usage();
			System.exit(OpenTMSConstants.OpenTMS_ID_SUCCESS);
		}
		Vector vector = run(args);
		String exitCode = (String) vector.get(0);
		vector = null;
		System.exit(Integer.parseInt(exitCode));
	}
}