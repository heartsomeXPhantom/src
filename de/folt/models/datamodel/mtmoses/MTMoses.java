/*
 * Created on 20.11.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.models.datamodel.mtmoses;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Hashtable;
import java.util.Vector;

import org.jdom.Element;

import de.folt.models.datamodel.BasicDataSource;
import de.folt.models.datamodel.DataSource;
import de.folt.models.datamodel.DataSourceConfigurations;
import de.folt.models.datamodel.DataSourceInstance;
import de.folt.models.datamodel.DataSourceProperties;
import de.folt.models.datamodel.MonoLingualObject;
import de.folt.models.datamodel.MultiLingualObject;
import de.folt.models.documentmodel.xliff.XliffDocument;
import de.folt.util.OpenTMSException;
import de.folt.util.OpenTMSProperties;

/**
 * @author klemens
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class MTMoses extends BasicDataSource
{

    public static void main(String[] args)
    {
        DataSourceProperties model = new DataSourceProperties();
        model.put("dataModelClass", "de.folt.models.datamodel.mtmoses.MTMoses");
        if (args.length == 0)
        {
            try
            {
                MTMoses mtmoses = (MTMoses) DataSourceInstance.createInstance("Moses", model);
                mtmoses.start();
                String segment = "mein kleines haus";
                String result = mtmoses.translate(segment);
                System.out.println(segment + " = " + result);
                segment = "kleines haus";
                result = mtmoses.translate(segment);
                System.out.println(segment + " = " + result);
                segment = "ich wohne in einem kleines haus";
                result = mtmoses.translate(segment);
                System.out.println(segment + " = " + result);
                mtmoses.stop();
            }
            catch (Exception e)
            {
                // TODO: handle exception
            }
        }
        if (args.length == 1)
        {
            try
            {
                de.folt.models.documentmodel.xliff.XliffDocument doc = new de.folt.models.documentmodel.xliff.XliffDocument();
                doc.loadXmlFile(args[0]);
                // model.put("MosesCommand", "cmd /c start/wait c:/projekte/MT-Systems/Moses/Release/moses-cmd");
                // model.put("MosesCommand", "\"c:\\projekte\\MT-Systems\\Moses\\moses.bat\"");
                // model.put("MosesCommand", "c:\\projekte\\MT-Systems\\Moses\\Release\\moses-cmd");
                // model.put("MosesIniFile", "C:\\projekte\\MT-Systems\\Moses\\sample\\sample-models2\\phrase-model\\moses.ini");
                // model.put("MosesParameterString", ""); //"-t -d");

                DataSource mtmoses = DataSourceInstance.createInstance("Moses", model);

                // MTMoses mtmoses = new MTMoses(model);
                Hashtable<String, Object> translationParameters = new Hashtable<String, Object>();
                translationParameters.put("tool", MTMoses.class.getName());
                try
                {
                    doc.translate(mtmoses, "de", "en", 100, -1, translationParameters);
                }
                catch (Exception e)
                {
                }
                doc.saveToXmlFile();
            }
            catch (OpenTMSException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return;
        }
    }

    boolean bFirstSegment = true;

    private int currentTu = 0;

    private InputStream inputstream;

    private String mosescommand = "";

    private String mosesinifile;

    private InputStreamReader mosesInp;

    private OutputStreamWriter mosesOup;

    private String mosesParameterString;

    private Process mosesProcess;

    private BufferedReader mosesReader;

    private BufferedWriter mosesWriter;

    private OutputStream outputstream;

    private Vector<String> translations;

    /**
     * 
     */
    public MTMoses()
    {
        super();
    }

    /**
     * @param dataSourceProperties
     */
    public MTMoses(DataSourceProperties dataSourceProperties)
    {
        super(dataSourceProperties);
        mosescommand = (String) dataSourceProperties.get("MosesCommand");
        if (mosescommand == null)
            mosescommand = OpenTMSProperties.getInstance().getOpenTMSProperty("MTMoses.MosesCommand");
        if ((mosescommand == null) || mosescommand.equals(""))
            return;
        mosesinifile = (String) dataSourceProperties.get("MosesIniFile");
        if (mosesinifile == null)
            mosesinifile = OpenTMSProperties.getInstance().getOpenTMSProperty("MTMoses.MosesIniFile");

        mosesParameterString = (String) dataSourceProperties.get("MosesParameterString");
        if (mosesParameterString == null)
            mosesParameterString = OpenTMSProperties.getInstance().getOpenTMSProperty("MTMoses.MosesParameterString");
    }

    /**
     * @param propertiesFileName
     */
    public MTMoses(String propertiesFileName)
    {
        super(propertiesFileName);
    }

    /* (non-Javadoc)
     * @see de.folt.models.datamodel.BasicDataSource#createDataSource(de.folt.models.datamodel.DataSourceProperties)
     */
    @Override
    public boolean createDataSource(DataSourceProperties dataModelProperties) throws OpenTMSException
    {
        String dataSourceName = (String) dataModelProperties.get("dataSourceName");
        String dataSourceConfigurationsFile = (String) dataModelProperties.get("dataSourceConfigurationsFile");
        dataSourceConfigurationsFile = DataSourceConfigurations.getConfigurationFileName(dataSourceConfigurationsFile);
        if (dataSourceConfigurationsFile == null)
        {
            return false;
        }
        BasicDataSource sqldatasource = new BasicDataSource();
        File fhd = new File(dataSourceConfigurationsFile);
        if (!fhd.exists())
        {
            File fx = new File(sqldatasource.getDefaultDataSourceConfigurationsFileName());
            if (!fx.exists())
            {
                return false;
            }

            dataSourceConfigurationsFile = sqldatasource.getDefaultDataSourceConfigurationsFileName();
        }

        dataSourceProperties.put("dataSourceConfigurationsFile", dataSourceConfigurationsFile);
        DataSourceConfigurations config = new DataSourceConfigurations(dataSourceConfigurationsFile);
        if (config.bDataSourceExistsInConfiguration(dataSourceName))
            return true;
        DataSourceProperties props = new DataSourceProperties();
        props.put("dataSourceConfigurationsFile", dataSourceConfigurationsFile);
        props.put("dataSourceName", dataSourceName);
        // props.put("datasourcetype", "de.folt.models.datamodel.tmxfile.TmxFileDataSource");

        config.addConfiguration(dataSourceName, this.getDataSourceType(), props);
        config.saveToXmlFile();

        return true;
    }

    /* (non-Javadoc)
     * @see de.folt.models.datamodel.BasicDataSource#getDataSourceType()
     */
    @Override
    public String getDataSourceType()
    {
        return MTMoses.class.getName();
    }

    /**
     * runMoses translate a given segment
     * @param segment the segment to translate
     * @return the translated segment
     */
    public Vector<String> runMoses(String inFile)
    {
        Vector<String> translations = new Vector<String>();
        try
        {
            ProcessBuilder mosespb = new ProcessBuilder(mosescommand, "-f", mosesinifile, "-input-file", inFile, "-t", "-d"); // , outFile);
            mosespb.redirectErrorStream(true);
            mosesProcess = mosespb.start();
            if (mosesProcess == null)
                return translations;

            InputStream inputstream = mosesProcess.getInputStream();
            InputStreamReader mosesInp = new InputStreamReader(inputstream);
            BufferedReader mosesReader = new BufferedReader(mosesInp, 10000);

            String response = "";
            boolean bTranslation = false;
            while ((response = mosesReader.readLine()) != null)
            {
                if (bTranslation)
                {
                    translations.add(response);
                    bTranslation = false;
                }
                if (response.startsWith("BEST TRANSLATION:"))
                {
                    bTranslation = true;
                }
            }

            mosesReader.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return translations;
        }

        return translations;
    }

    /**
     * startMoses start the moses in input / output mode
     * @return the start output produces by Moses
     */
    public String start()
    {
        String result = "";
        if (mosesProcess != null)
        {
            stop();
        }
        try
        {
            ProcessBuilder mosespb = new ProcessBuilder(mosescommand, "-f", mosesinifile, "-t", "-d"); // , outFile);
            mosespb.redirectErrorStream(true);

            mosesProcess = mosespb.start();
            if (mosesProcess == null)
                return result;

            inputstream = mosesProcess.getInputStream();
            mosesInp = new InputStreamReader(inputstream);
            mosesReader = new BufferedReader(mosesInp, 10000);

            outputstream = mosesProcess.getOutputStream();
            mosesOup = new OutputStreamWriter(outputstream);
            mosesWriter = new BufferedWriter(mosesOup, 10000);

            String response = "";
            while ((response = mosesReader.readLine()) != null)
            {
                System.out.println(response);
                result = result + response + "\n";
                if (response.startsWith("Created input-output object"))
                    break;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return result;
        }

        return result;
    }

    /**
     * stop stop the moses process
     * @return true in case of sucess
     */
    public boolean stop()
    {
        try
        {
            mosesReader.close();
            mosesInp.close();
            inputstream.close();

            mosesWriter.close();
            mosesOup.close();
            outputstream.close();
            mosesProcess.destroy();
            mosesProcess = null;
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;
    }

    /* (non-Javadoc)
     * @see de.folt.models.datamodel.BasicDataSource#translate(org.jdom.Element, de.folt.models.documentmodel.xliff.XliffDocument, java.lang.String, java.lang.String, int, java.util.Hashtable)
     */
    @Override
    public Element translate(Element transUnit, Element file, XliffDocument xliffDocument, String sourceLanguage, String targetLanguage, int matchSimilarity,
            Hashtable<String, Object> translationParameters) throws OpenTMSException
    {
        Element source = transUnit.getChild("source", xliffDocument.getNamespace());
        String segment = xliffDocument.elementContentToString(source);
        if (bFirstSegment)
        {
            bFirstSegment = false;
            String inFileName = xliffDocument.getXmlDocumentName() + ".in";
            boolean bRemoveCRLF = true;
            boolean bRemoveTags = true;
            boolean bSuccess = xliffDocument.exportToTextFile(file, inFileName, bRemoveCRLF, bRemoveTags);
            if (!bSuccess)
            {
                return transUnit;
            }
            translations = runMoses(inFileName);
            // now get all segments from input file...

            if (!bSuccess)
            {
                return transUnit;
            }
            // String encoding = "UTF-8";
            // translations = OpenTMSSupportFunctions.readFileIntoVector(outFileName, encoding);
        }

        if (translations.size() == 0)
        {
            return transUnit;
        }
        if (translations.size() > currentTu)
        {
            Vector<MonoLingualObject> targetmonos = new Vector<MonoLingualObject>();
            MultiLingualObject multi = new MultiLingualObject();
            MonoLingualObject sourceMono = new MonoLingualObject();
            sourceMono.setFormattedSegment(segment);
            sourceMono.setLanguage(sourceLanguage);
            MonoLingualObject targetMono = new MonoLingualObject();
            targetMono.setFormattedSegment(translations.get(currentTu));
            targetMono.setLanguage(targetLanguage);
            multi.addMonoLingualObject(sourceMono);
            multi.addMonoLingualObject(targetMono);
            targetmonos.add(targetMono);
            xliffDocument.removeTranslationBasedOnOrigin(transUnit, this.getDataSourceType());
            Element alttrans = xliffDocument.addAltTrans(transUnit, sourceMono, targetmonos, (int) 0, translationParameters);
            if (alttrans != null)
                alttrans.setAttribute("origin", this.getDataSourceType());
            if (alttrans != null)
                alttrans.setAttribute("match-quality", "MT");
            currentTu++;
        }
        
		translationParameters.put(":file", file);
		translationParameters.put(":xliffDocument", xliffDocument);
		translationParameters.put(":sourceLanguage", sourceLanguage);
		translationParameters.put(":targetLanguage", targetLanguage);
		translationParameters.put(":matchSimilarity", (Integer)matchSimilarity);
		translationParameters.put(":dataSource", this);
		translationParameters.put(":instanceOpenTMSProperties", de.folt.util.OpenTMSProperties.getInstance());
		transUnit = runFilterMethod(translationParameters, transUnit);

        return transUnit;
    }

    /**
     * translate translate a segment
     * @param segment the segment to translate
     * @return the translates segment
     */
    public String translate(String segment)
    {
        String result = "";
        try
        {
            mosesWriter.write(segment);
            mosesWriter.write("\n");
            mosesWriter.flush();
            boolean bTranslation = false;
            String response;
            while ((response = mosesReader.readLine()) != null)
            {
                if (bTranslation)
                {
                    result = response;
                    bTranslation = false;
                }
                if (response.startsWith("BEST TRANSLATION:"))
                {
                    bTranslation = true;
                }
                if (response.startsWith("Finished translating"))
                {
                    return result;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return result;
    }

}
