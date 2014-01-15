/*
 * Created on 16.07.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.models.datamodel.iate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom.Element;

import de.folt.constants.OpenTMSConstants;
import de.folt.models.datamodel.BasicDataSource;
import de.folt.models.datamodel.DataSource;
import de.folt.models.datamodel.DataSourceInstance;
import de.folt.models.datamodel.DataSourceProperties;
import de.folt.models.datamodel.LinguisticProperty;
import de.folt.models.datamodel.MonoLingualObject;
import de.folt.models.datamodel.MultiLingualObject;
import de.folt.models.datamodel.GeneralLinguisticObject.LinguisticTypes;
import de.folt.models.documentmodel.xliff.XliffDocument;
import de.folt.util.OpenTMSException;

/**
 * Class implements a search interface to iate @see http://iate.europa.eu. This is just for experimental tests. Using the interface requires the permission of the CEC.<br><pre>
 * <table border="0" cellpadding="0" cellspacing="0" width="100%">
 * <tr height="23px">
 * <td colspan="5" class="lilRecordLeftFooter">Sonderorganisation der Vereinten Nationen, Leben in der Gesellschaft
 * [<label for="lil.institution.name" title="Rat der Europäischen Union">Council</label>]
 * </td>
 * <td colspan="2" class="lilRecordRigthFooter" align="right">
 * <a href="SearchByQuery.do?method=searchDetail&lilId=931334&langId=&query=Landwirtschaft&sourceLanguage=de&domain=0&matching=&start=0&next=1&amp;targetLanguages=en" title="Für den Zugang zum vollständigen Eintrag hier klicken">
 * <b>Vollständiger Eintrag</b></a></td></tr>
 * <tr height="20" onMouseover="changeColor(this,true)" onMouseOut="changeColor(this,false)" title="Für Details zur Sprachebene hier klicken" onclick="window.location.href='FindTermsByLilId.do?lilId=931334&langId=de'">
 * <div class="termRecord"><b>DE</b></div>
 * <td width="100%" class="lilRecord"><div class="termRecord">CSSA</div></td>
 * <td width="100%" class="lilRecord"><div class="termRecord">Ausschuss für den Arbeitsschutz in der <span style="color:blue;background:#CCE698">Landwirtschaft</span></div></td>
 * <td width="40" rowspan="2" class="lilRecord" align="center"> <div class="termRecord"><b>EN</b></div></td>
 * ...
 * </table>
 * </pre>
 * @author klemens
 */
public class IateTerminology extends BasicDataSource
{

    /**
     * main 
     * @param args
     */
    public static void main(String[] args)
    {
        String searchterm = "Landwirtschaft";
        String sourceLanguage = "de";
        String targetLanguage = "en";

        if (args.length == 0)
        {
            System.out.println("Usage: <term (Landwirtschaft)> <sourcelanguage (de)> <targetlanguage (en)>");
            System.out.println("or");
            System.out.println("Usage: -translate <xlifffile> <sourcelanguage> <targetlanguage>");
        }

        if ((args.length >= 4) && args[0].equalsIgnoreCase("-translate"))
        {
            DataSourceProperties model = new DataSourceProperties();
            model.put("dataModelClass", "de.folt.models.datamodel.iate.IateTerminology");

            try
            {
                de.folt.models.documentmodel.xliff.XliffDocument doc = new de.folt.models.documentmodel.xliff.XliffDocument();
                doc.loadXmlFile(args[1]);
                DataSource datasource = DataSourceInstance.createInstance("IateTerminology:translate", model);
                Hashtable<String, Object> translationParameters = new Hashtable<String, Object>();
                translationParameters.put("tool", "de.folt.models.datamodel.iate.IateTerminology");
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

        if (args.length > 0)
            searchterm = args[0];
        if (args.length > 1)
            sourceLanguage = args[1];
        if (args.length > 2)
            targetLanguage = args[2];
        IateTerminology iateTerminology = new IateTerminology();
        Vector<MultiLingualObject> result = iateTerminology.searchTerm(searchterm, sourceLanguage, targetLanguage);

        if (result != null)
        {
            System.out.println("Results found= " + result.size());
            for (int i = 0; i < result.size(); i++)
            {
                System.out.println(result.get(i).format());
                System.out.println();
            }
        }
        else
            System.out.println("Results found= " + 0);
    }

    private Hashtable<String, Vector<MultiLingualObject>> dictionaryTable = new Hashtable<String, Vector<MultiLingualObject>>();

    private String iateQeryString = "http://iate.europa.eu/iatediff/SearchByQuery.do?method=search&query=%s&sourceLanguage=%s&domain=0&typeOfSearch=s&targetLanguages=%s";

    private XliffDocument xliffDocument = new XliffDocument();

    /**
     * 
     */
    public IateTerminology()
    {
        super();
    }

    /**
     * @param dataSourceProperties
     */
    public IateTerminology(DataSourceProperties dataSourceProperties)
    {
        return;
    }

    /* (non-Javadoc)
     * @see de.folt.models.datamodel.BasicDataSource#getDataSourceType()
     */
    @Override
    public String getDataSourceType()
    {
        return IateTerminology.class.getName();
    }

    /**
     * @return the dictionaryTable
     */
    public Hashtable<String, Vector<MultiLingualObject>> getDictionaryTable()
    {
        return dictionaryTable;
    }

    /**
     * @return the queryString
     */
    public String getQueryString()
    {
        return iateQeryString;
    }

    /**
     * parseResult 
     * @param result
     * @return
     */
    private Vector<MultiLingualObject> parseResult(String queryresult)
    {
        queryresult = queryresult.replaceAll(" +", " ");
        queryresult = queryresult.replaceAll("\\n+", "");
        queryresult = queryresult.replaceAll("\\r+", "");
        queryresult = queryresult.replaceAll("\\t+", "");
        queryresult = queryresult.replaceAll("<img.*?</img>", "");
        queryresult = queryresult.replaceAll("<img .*?>", "");
        queryresult = queryresult.replaceAll("<a .*?>", "");
        queryresult = queryresult.replaceAll("<tr .*?>", "<tr>");
        String tr = "<tr>(.*?)</tr>";
        Pattern trPattern = Pattern.compile(tr);

        String term = ".*?<div class=\"termRecord\">(.*?)</div>.*";
        String marker = "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">(.*?)</table>";
        Pattern tablePattern = Pattern.compile(marker);
        Matcher Matcher = tablePattern.matcher(queryresult);
        Vector<MultiLingualObject> allTermTables = new Vector<MultiLingualObject>();

        int iMonos = 0;
        while (Matcher.find())
        {
            MultiLingualObject multi = new MultiLingualObject();

            String match = Matcher.group();
            match = match.replaceAll("<table .*?>", "<table>");
            match = match.replaceAll("colspan=\".*?\"", "");
            match = match.replaceAll("width=\".*?\"", "");
            match = match.replaceAll("rowspan=\".*?\"", "");
            match = match.replaceAll("style=\".*?\"", "");
            match = match.replaceAll("</a>", "");
            match = match.replaceAll(" +", " ");
            match = match.replaceAll("<td class=\"lilRecord\"></td>", "");
            match = match.replaceAll("<span.*?>", "");
            match = match.replaceAll("</span.*?>", " ");

            String domainPat = ".*?<td class=\"lilRecordLeftFooter\">(.*?)</td>.*";
            String domain = match.replaceAll(domainPat, "$1");

            if ((domain != null) && !domain.equals(match))
            {
                String labelfor = domain.replaceAll(".*?\\[<label for=\"(.*?)\".*?\\]", "$1");
                String labeltitel = domain.replaceAll(".*?\\[<label for=.*?title=\"(.*?)\".*?\\]", "$1");

                if ((labelfor != null) && !labelfor.equals(domain))
                {
                    LinguisticProperty lingProp = new LinguisticProperty("termFor", xliffDocument.quoteXMLString(labelfor));
                    multi.addLinguisticProperty(lingProp);
                }
                if ((labeltitel != null) && !labeltitel.equals(domain))
                {
                    LinguisticProperty lingProp = new LinguisticProperty("termTitle", xliffDocument.quoteXMLString(labeltitel));
                    multi.addLinguisticProperty(lingProp);
                }

                domain = domain.replaceAll("(.*?)\\[<label.*?\\].*", "$1");
                LinguisticProperty lingProp = new LinguisticProperty("termDomain", xliffDocument.quoteXMLString(domain));
                multi.addLinguisticProperty(lingProp);
            }

            match = match.replaceAll("<tr.*?>.*?Full entry.*?</tr>", "");

            // now match over all entries <tr>
            Matcher trMatcher = trPattern.matcher(match);
            String currentLanguage = "";
            MonoLingualObject mono = null;
            while (trMatcher.find())
            {
                String trmatch = trMatcher.group(1);

                // System.out.println(trmatch);

                String language = trmatch.replaceAll(".*?<b>(.*?)</b>.*", "$1");
                if (!language.equals(trmatch))
                {
                    if (!language.equalsIgnoreCase("Full Entry"))
                    {
                        currentLanguage = language.toLowerCase();
                        trmatch = trmatch.replaceFirst(".*?<b>.*?</b>", "");
                        // System.out.println("Detected language: " + currentLanguage);
                    }
                }
                String termcand = trmatch.replaceAll(term, "$1");
                if (!termcand.equals(trmatch))
                {
                    if (termcand.indexOf("<b>") == -1)
                    {
                        if (currentLanguage != null)
                        {
                            String realterm = termcand.replaceAll("<td.*?class=\"lilRecord\">(.*?)</td>.*", "$1");
                            realterm = realterm.replaceFirst("^ +", "");
                            realterm = realterm.replaceFirst(" +$", "");
                            realterm = realterm.replaceAll(" +", " ");
                            realterm = xliffDocument.quoteXMLString(realterm);
                            // System.out.println("Detected realterm: " + realterm);
                            mono = new MonoLingualObject();
                            mono.setLanguage(currentLanguage);
                            mono.setPlainTextSegment(realterm);
                            mono.setFormattedSegment(realterm);
                            mono.setLingType(LinguisticTypes.TERM);
                            multi.addMonoLingualObject(mono);
                            iMonos++;

                            String termRef = ".*?<td class=\"lilRecord\" title=\"Term Ref\\.: (.*?)\">.*";
                            String termRel = ".*?<td class=\"lilRecord\" title=\"Rel\\. value: (.*?)\">.*";

                            String termRelevance = trmatch.replaceAll(termRel, "$1");
                            String termReference = trmatch.replaceAll(termRef, "$1");
                            if ((termRelevance != null) && !termRelevance.equals(trmatch))
                            {
                                LinguisticProperty lingProp = new LinguisticProperty("termRelevance", xliffDocument.quoteXMLString(termRelevance));
                                mono.addLinguisticProperty(lingProp);
                            }
                            if ((termReference != null) && !termReference.equals(trmatch))
                            {
                                LinguisticProperty lingProp = new LinguisticProperty("termReference", xliffDocument.quoteXMLString(termReference));
                                mono.addLinguisticProperty(lingProp);
                            }

                        }
                    }
                }
            }
            if (iMonos > 0)
                allTermTables.add(multi);
        }
        return allTermTables;
    }

    private String runQuery(String urlString, String term, String sourceLanguage, String targetLanguage)
    {
        try
        {
            String searchString = String.format(urlString, URLEncoder.encode(term, "UTF-8"), sourceLanguage, targetLanguage);
            URL lurl = new URL(searchString);
            URLConnection lconn = lurl.openConnection();
            lconn.setRequestProperty("User-Agent", "");
            lconn.setRequestProperty("Content-Type", "text/plain");
            // Make sure we send a user-agent property, otherwise we get 403 error
            lconn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(lconn.getOutputStream());
            wr.write("");
            wr.flush();
            // Get the response
            BufferedReader lrd = new BufferedReader(new InputStreamReader(lconn.getInputStream(), "UTF-8"));
            StringBuilder lres = new StringBuilder();
            char[] lbuf = new char[2048];
            int lcount = 0;
            while ((lcount = lrd.read(lbuf)) != -1)
            {
                lres.append(lbuf, 0, lcount);
            }
            lrd.close();
            String queryresult = lres.toString();
            queryresult = queryresult.replaceAll(" +", " ");
            queryresult = queryresult.replaceAll("\\n+", "");
            queryresult = queryresult.replaceAll("\\r+", "");
            queryresult = queryresult.replaceAll("\\t+", "");
            queryresult = queryresult.replaceAll("<img.*?</img>", "");
            queryresult = queryresult.replaceAll("<img .*?>", "");
            queryresult = queryresult.replaceAll("<a .*?>", "");
            queryresult = queryresult.replaceAll("<tr .*?>", "<tr>");
            return queryresult;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * searchTerm search for a term in the IATE term database
     * @param term the term to search
     * @param sourceLanguage the source language
     * @param targetLanguage the target language
     * @return a Vector of MultiLingualObjects found
     */
    public Vector<MultiLingualObject> searchTerm(String term, String sourceLanguage, String targetLanguage)
    {
        Vector<MultiLingualObject> allTables = new Vector<MultiLingualObject>();
        try
        {
            String queryresult = runQuery(iateQeryString, term, sourceLanguage, targetLanguage);
            // System.out.println(queryresult);
            String numRes = queryresult.replaceFirst(".*?<strong>.*?</strong>.*?<strong>(.*?)</strong>.*", "$1");
            int iNumResults = 0;
            int iNumPages = 0;
            try
            {
                iNumResults = Integer.parseInt(numRes);
                iNumPages = iNumResults / 10;
                allTables = parseResult(queryresult);
            }
            catch (NumberFormatException e)
            {
                return allTables;
            }

            System.out.print("Term: " + term);
            System.out.print(" iNumResults: " + iNumResults);
            System.out.println(" iNumPages: " + iNumPages);

            for (int i = 1; i < iNumPages + 1; i++)
            {
                String tempUrl = iateQeryString + "&start=" + i * 10;
                // System.out.println("Run URL: " + tempUrl);
                queryresult = runQuery(tempUrl, term, sourceLanguage, targetLanguage);
                Vector<MultiLingualObject> tempTables = parseResult(queryresult);
                if (tempTables != null)
                {
                    allTables.addAll(tempTables);
                }
                else
                    break;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return allTables;
    }

    /**
     * @param dictionaryTable the dictionaryTable to set
     */
    public void setDictionaryTable(Hashtable<String, Vector<MultiLingualObject>> dictionaryTable)
    {
        this.dictionaryTable = dictionaryTable;
    }

    /**
     * @param queryString the queryString to set
     */
    public void setQueryString(String queryString)
    {
        this.iateQeryString = queryString;
    }

    /* (non-Javadoc)
     * @see de.folt.models.datamodel.BasicDataSource#translate(org.jdom.Element, de.folt.models.documentmodel.document.XmlDocument, java.lang.String, java.lang.String, int, java.util.Hashtable)
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Element translate(Element transUnit, Element file, XliffDocument xliffDocument, String sourceLanguage, String targetLanguage, int matchSimilarity,
            Hashtable<String, Object> translationParameters) throws OpenTMSException
    {
        try
        {
            this.xliffDocument = xliffDocument;
            Element source = transUnit.getChild("source", xliffDocument.getNamespace());
            String segment = xliffDocument.elementContentToString(source);
            Class[] classes = new Class[2];
            classes[0] = String.class;
            classes[1] = Object.class;
            @SuppressWarnings("unused")
            Method method = null;
            try
            {
                method = MonoLingualObject.class.getMethod("simpleComputePlainText", classes);
            }
            catch (Exception ex)
            {
                throw new OpenTMSException("translate", "simpleComputePlainText", OpenTMSConstants.OpenTMS_TRANSLATE_PLAINTEXTMETHOD_NOTFOUND_ERROR, (Object) this, ex);
            }

            String[] sourceTerms = new de.folt.util.WordHandling().segmentToWordArray(segment);
            // remove double words
            sourceTerms = (String[]) new HashSet(Arrays.asList(sourceTerms)).toArray(new String[] {});

            if (translationParameters != null)
                translationParameters.put("termMatch", "termMatch");
            for (int k = 0; k < sourceTerms.length; k++)
            {
                Vector<MultiLingualObject> multiresults = null;
                // we do just one access!!!
                if (dictionaryTable.containsKey(sourceTerms[k]))
                    multiresults = dictionaryTable.get(sourceTerms[k]);
                else
                {
                    multiresults = this.searchTerm(sourceTerms[k], sourceLanguage, targetLanguage);
                    dictionaryTable.put(sourceTerms[k], multiresults);
                }

                if ((multiresults != null))
                {
                    for (int i = 0; i < multiresults.size(); i++)
                    {
                        MultiLingualObject multi = multiresults.get(i);
                        Vector<MonoLingualObject> sourcemonos = multi.getMonoLingualObjectsAsVector(sourceLanguage);
                        Vector<MonoLingualObject> targetmonos = multi.getMonoLingualObjectsAsVector(targetLanguage);
                        for (int j = 0; j < sourcemonos.size(); j++)
                        {
                            if (sourcemonos.get(j).getPlainTextSegment().indexOf(sourceTerms[k]) == -1)
                                continue;
                            Element alttrans = xliffDocument.addAltTrans(transUnit, sourcemonos.get(j), targetmonos, (int) 0, translationParameters);
                            if (alttrans != null)
                                alttrans.setAttribute("origin", this.getDataSourceType());
                            if (alttrans != null)
                                alttrans.setAttribute("match-quality", "IATE");
                        }
                    }
                }
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
}
