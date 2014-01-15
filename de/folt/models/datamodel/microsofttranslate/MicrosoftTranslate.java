/*
 * Created on 07.06.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.models.datamodel.microsofttranslate;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;
import java.util.Vector;

import org.jdom.Element;

import de.folt.constants.OpenTMSConstants;
import de.folt.models.datamodel.BasicDataSource;
import de.folt.models.datamodel.DataSource;
import de.folt.models.datamodel.DataSourceConfigurations;
import de.folt.models.datamodel.DataSourceInstance;
import de.folt.models.datamodel.DataSourceProperties;
import de.folt.models.datamodel.MonoLingualObject;
import de.folt.models.documentmodel.xliff.XliffDocument;
import de.folt.util.OpenTMSException;

/**
 * This class implements a data source which allows to translate a text with Microsoft translate. See http://msdn.microsoft.com/en-us/library/dd576286.aspx or http://sdk.microsofttranslator.com/HTTP/HTTPDemo2.aspx
 * <pre>
 *  Example 
C#
    string translateUri = "http://api.microsofttranslator.com/V1/Http.svc/Translate?appId=myAppId&from=en&to=es";
    HttpWebRequest httpWebRequest = (HttpWebRequest)WebRequest.Create(translateUri);
    // Request must be HTTP POST to include translation text 
    httpWebRequest.Method = "POST";
    httpWebRequest.ContentType = "text/plain";
    byte[] bytes = Encoding.ASCII.GetBytes("Translate this text");
    Stream os = null;

    try
    {
        // Text is inserted into the body of the request 
        httpWebRequest.ContentLength = bytes.Length;
        os = httpWebRequest.GetRequestStream();
        os.Write(bytes, 0, bytes.Length);
    }
    finally
    {
        // Close stream if successful 
        if (os != null)
        {
            os.Close();
        }
    }
    
    string output;
    try
    {
        WebResponse response = httpWebRequest.GetResponse();
        Stream stream = response.GetResponseStream();
        StreamReader reader = new StreamReader(stream);
        output = reader.ReadToEnd();
    }
    catch (Exception ex)
    {
        // An error may be thrown if the request body is not recognizable as text 
        // An error may be thrown if the from and to parameters are the same 
        output = "Error - " + ex.Message;
    }
    
    Console.WriteLine("Result: " + output);   
 * </pre>
 *
 */

public class MicrosoftTranslate extends BasicDataSource
{

    /**
     * main 
     * @param args
     */
    public static void main(String[] args)
    {
        DataSourceProperties model = new DataSourceProperties();
        model.put("dataModelClass", "de.folt.models.datamodel.microsofttranslate.MicrosoftTranslate");
        if (args.length <= 0)
        {
            try
            {
                MicrosoftTranslate datasource = (MicrosoftTranslate) DataSourceInstance.createInstance("GoogleTranslate:translate", model);
                String result = datasource.translate("Das ist mein Haus.\nUnd hier kommt noch ein Satz. Wie geht es nun weiter? Der Übersetzer funktioniert nun!", "de", "en");
                System.out.println("result = \"" + result + "\"");
            }
            catch (OpenTMSException e)
            {
                e.printStackTrace();
            }
            return;
        }
        if (args.length >= 4)
        {
            if (args[0].equalsIgnoreCase(("-translate")))
            {
                try
                {
                    de.folt.models.documentmodel.xliff.XliffDocument doc = new de.folt.models.documentmodel.xliff.XliffDocument();
                    doc.loadXmlFile(args[1]);
                    Hashtable<String, Object> translationParameters = new Hashtable<String, Object>();
                    translationParameters.put("tool", "de.folt.models.datamodel.microsofttranslate.MicrosoftTranslate");
                    DataSource datasource = DataSourceInstance.createInstance("MicrosoftTranslate:translate", model);
                    doc.translate(datasource, args[2], args[3], 100, -1, translationParameters);
                    doc.saveToXmlFile();
                    return;
                }
                catch (OpenTMSException e)
                {
                    e.printStackTrace();
                    return;
                }
            }
        }
        else
        {
            model = new DataSourceProperties();
            model.put("dataModelClass", "de.folt.models.datamodel.microsofttranslate.MicrosoftTranslate");

            try
            {
                de.folt.models.documentmodel.xliff.XliffDocument doc = new de.folt.models.documentmodel.xliff.XliffDocument();
                DataSource datasource = DataSourceInstance.createInstance("MicrosoftTranslate:translate", model);
                doc.translate(datasource, null, null, 100, -1, null);
                doc.saveToXmlFile();
                return;
            }
            catch (OpenTMSException e)
            {
                e.printStackTrace();
                return;
            }

        }

    }

    /**
     * 
     */
    public MicrosoftTranslate()
    {
        super();
    }

    /**
     * @param dataSourceProperties
     */
    public MicrosoftTranslate(DataSourceProperties dataSourceProperties)
    {
        super(dataSourceProperties);
    }

    /* (non-Javadoc)
     * @see de.folt.models.datamodel.BasicDataSource#getDataSourceType()
     */
    @Override
    public String getDataSourceType()
    {
        return MicrosoftTranslate.class.getName();
    }

    /* (non-Javadoc)
     * @see de.folt.models.datamodel.BasicDataSource#translate(org.jdom.Element, de.folt.models.documentmodel.document.XmlDocument, java.lang.String, java.lang.String, int, java.util.Hashtable)
     */
    @SuppressWarnings("unchecked")
    @Override
    public Element translate(Element transUnit, Element file, XliffDocument xliffDocument, String sourceLanguage, String targetLanguage, int matchSimilarity,
            Hashtable<String, Object> translationParameters) throws OpenTMSException
    {
        try
        {
            if (translationParameters == null)
                translationParameters = new Hashtable<String, Object>();
            translationParameters.put("ignoreProps", "true");
            Element source = transUnit.getChild("source", xliffDocument.getNamespace());
            String segment = xliffDocument.elementContentToString(source);
            Class[] classes = new Class[2];
            classes[0] = String.class;
            classes[1] = Object.class;
            Method method = null;
            try
            {
                method = MonoLingualObject.class.getMethod("simpleComputePlainText", classes);
            }
            catch (Exception ex)
            {
                throw new OpenTMSException("translate", "simpleComputePlainText", OpenTMSConstants.OpenTMS_TRANSLATE_PLAINTEXTMETHOD_NOTFOUND_ERROR, (Object) this, ex);
            }
            MonoLingualObject sourceMono = new MonoLingualObject(segment, sourceLanguage, MonoLingualObject.class, method, null);
            segment = sourceMono.getPlainTextSegment();
            String translation = translate(sourceMono, sourceLanguage, targetLanguage);

            if ((translation != null) && !translation.equals(""))
            {
                MonoLingualObject targetMono = new MonoLingualObject(translation, targetLanguage, MonoLingualObject.class, method, null);
                Vector<MonoLingualObject> targetmonos = new Vector<MonoLingualObject>();
                targetmonos.add(targetMono);
                xliffDocument.removeTranslationBasedOnOrigin(transUnit, "MicrosSoftTranslate");
                Element alttrans = xliffDocument.addAltTrans(transUnit, sourceMono, targetmonos, (int) 90, translationParameters);
                if (alttrans != null)
                    alttrans.setAttribute("origin", "MicrosSoftTranslate");
                if (alttrans != null)
                    alttrans.setAttribute("match-quality", "MT");
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
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

    private String translate(MonoLingualObject sourceMono, String sourceLanguage, String targetLanguage)
    {
        return translate(sourceMono.getPlainTextSegment(), sourceLanguage, targetLanguage);
    }

    public String translate(String segment, String sourceLanguage, String targetLanguage)
    {
        try
        {
            String translation = "";
            String address = "http://api.microsofttranslator.com/V1/Http.svc/Translate"; // http://api.microsofttranslator.com/V1/Http.svc/Translate?appId=myAppId&from=en&to=es
            // String baseQuery = "?appId=OpenTMS&text=%s&from=%s&to=%s"; // ?text=%s&hl=en&ie=UTF8&langpair=%s|%s&oe=UTF8";
            String baseQuery = "appId=1B25382C82AA5FFC00EC0A5043C9D98D329E956B&from=%s&to=%s";
            String microsoftURL = de.folt.util.OpenTMSProperties.getInstance().getOpenTMSProperty("Microsoft.URL");
            if ((microsoftURL != null) && !microsoftURL.equals(""))
            {
                address = microsoftURL;
            }
            String microsoftQuery = de.folt.util.OpenTMSProperties.getInstance().getOpenTMSProperty("Microsoft.Query");
            if ((microsoftQuery != null) && !microsoftQuery.equals(""))
            {
                baseQuery = microsoftQuery;
            }

            String query = address + ""; // String.format(baseQuery, URLEncoder.encode(sourceMono.getPlainTextSegment(), "UTF-8"), sourceLanguage, targetLanguage);
            String data = String.format(baseQuery, /* URLEncoder.encode(segment, "UTF-8"), */sourceLanguage, targetLanguage);
            String fullQuery = query + "?" + data;
            // System.out.println(query);
            // System.out.println(fullQuery);
            URL url = new URL(fullQuery);
            URLConnection conn = url.openConnection();
            // Make sure we send a user-agent property, otherwise we get 403 error
            conn.setRequestProperty("User-Agent", "");
            conn.setRequestProperty("Content-Type", "text/plain"); //"text/plain");
            conn.setAllowUserInteraction(false);
            conn.setDoInput(true);
            conn.setRequestProperty("Content-length", data.length() + "");
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            wr.write(segment);
            wr.flush();
            wr.close();

            // Get the response
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder res = new StringBuilder();
            char[] buf = new char[2048];
            int count = 0;
            while ((count = rd.read(buf)) != -1)
            {
                res.append(buf, 0, count);
            }
            rd.close();
            res.deleteCharAt(0);
            translation = res.toString();
            return translation;
        }
        catch (UnsupportedEncodingException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (MalformedURLException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

}
