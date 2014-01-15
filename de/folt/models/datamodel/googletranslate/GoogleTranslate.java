/*
 * Created on 07.06.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.models.datamodel.googletranslate;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom.Element;

import com.tecnick.htmlutils.htmlentities.HTMLEntities;

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
 * This class implements a data source which allows to translate a text with Google translate.
 * @author klemens
 *
 */
public class GoogleTranslate extends BasicDataSource
{

    /**
     * main 
     * @param args
     */
    public static void main(String[] args)
    {
        DataSourceProperties model = new DataSourceProperties();
        model.put("dataModelClass", "de.folt.models.datamodel.googletranslate.GoogleTranslate");
        if (args.length <= 0)
        {
            try
            {
                GoogleTranslate datasource = (GoogleTranslate) DataSourceInstance.createInstance("GoogleTranslate:translate", model);
                String result = datasource.translate("Das ist mein Haus.\nUnd hier kommt noch ein Satz. die Betriebsanleitung", "de", "en");
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

            Hashtable<String, Object> translationParameters = new Hashtable<String, Object>();
            translationParameters.put("tool", "de.folt.models.datamodel.googletranslate.GoogleTranslate");
            if (args[0].equalsIgnoreCase(("-translate")))
            {
                try
                {
                    de.folt.models.documentmodel.xliff.XliffDocument doc = new de.folt.models.documentmodel.xliff.XliffDocument();
                    doc.loadXmlFile(args[1]);
                    DataSource datasource = DataSourceInstance.createInstance("GoogleTranslate:translate", model);
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

    }

    /**
     * 
     */
    public GoogleTranslate()
    {
        super();
    }

    /**
     * @param dataSourceProperties
     */
    public GoogleTranslate(DataSourceProperties dataSourceProperties)
    {
        super(dataSourceProperties);
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
        return GoogleTranslate.class.getName();
    }

    /* (non-Javadoc)
     * @see de.folt.models.datamodel.BasicDataSource#translate(org.jdom.Element, de.folt.models.documentmodel.document.XmlDocument, java.lang.String, java.lang.String, int, java.util.Hashtable)
     */
    @SuppressWarnings({ "rawtypes" })
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
                // here we should check if it contains "GoogleTranslate" translation and remove the old one...
                xliffDocument.removeTranslationBasedOnOrigin(transUnit, "GoogleTranslate");
                Element alttrans = xliffDocument.addAltTrans(transUnit, sourceMono, targetmonos, (int) 90, translationParameters);
                if (alttrans != null)
                    alttrans.setAttribute("origin", "GoogleTranslate"); // query);
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

    private String translate(MonoLingualObject sourceMono, String sourceLanguage, String targetLanguage)
    {
        return translate(sourceMono.getPlainTextSegment(), sourceLanguage, targetLanguage);
    }

    private String translate(String segment, String sourceLanguage, String targetLanguage)
    {
        try
        {
            // prepare string
            segment = segment.replaceAll("\n", "<p>");
            segment = segment.replaceAll("\r", "<br>");
            // 'http://ajax.googleapis.com/ajax/services/language/translate?v=1.0&q=hello%20world&langpair=en%7Cit'
            // String address = "http://translate.google.com/translate_t?v=1.0";
            String address = "http://ajax.googleapis.com/ajax/services/language/translate?v=1.0";
            // String baseQuery = "?text=%s&hl=en&ie=UTF8&langpair=%s|%s&oe=UTF8";
            String baseQuery = "&q=%s&langpair=%s%%7C%s"; // %% neded to "quote" % for formatting
            // Pattern pattern = Pattern.compile("<div\\sid=result_box .*?>(.*?)</div>");
            Pattern pattern = Pattern.compile(".*translatedText\":\"(.*?)\"},.*");
            // String searchPattern = "{\"responseData\": {\"translatedText\":(.*?)\"}, \"responseDetails\":";
            String googleURL = de.folt.util.OpenTMSProperties.getInstance().getOpenTMSProperty("Google.URL");
            if ((googleURL != null) && !googleURL.equals(""))
            {
                address = googleURL;
            }
            String googleQuery = de.folt.util.OpenTMSProperties.getInstance().getOpenTMSProperty("Google.Query");
            if ((googleQuery != null) && !googleQuery.equals(""))
            {
                baseQuery = googleQuery;
            }
            String googlePattern = de.folt.util.OpenTMSProperties.getInstance().getOpenTMSProperty("Google.Pattern");
            if ((googlePattern != null) && !googlePattern.equals(""))
            {
                pattern = Pattern.compile(googlePattern);
            }
            String query = address + String.format(baseQuery, URLEncoder.encode(segment, "UTF-8"), sourceLanguage, targetLanguage);
            // System.out.println(query);
            URL url = new URL(query);
            URLConnection conn = url.openConnection();
            // Make sure we send a user-agent property, otherwise we get 403 error
            conn.setRequestProperty("User-Agent", "");
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
            String result = res.toString();
            // System.out.println(result);
            Matcher m = pattern.matcher(result);
            String translation = "";
            if (m.find())
            {
                translation = m.group(1);
            }
            String re = Pattern.quote("\\u003cp\\u003e");
            translation = translation.replaceAll(re, "\n");
            re = Pattern.quote("\\u003cbr\\u003e");
            translation = translation.replaceAll(re, "\r");
            re = Pattern.quote("\\u0026");
            translation = translation.replaceAll(re, "&");
            translation = HTMLEntities.unhtmlentities(translation);

            return translation;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.folt.models.datamodel.BasicDataSource#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(Observable arg0, Object arg1)
    {
        // TODO Auto-generated method stub
        super.update(arg0, arg1);
    }

}
