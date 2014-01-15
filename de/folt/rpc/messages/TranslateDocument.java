package de.folt.rpc.messages;

import java.util.Hashtable;
import java.util.Vector;

import de.folt.rpc.services.RPCMessage;
import de.folt.util.OpenTMSProperties;

/**
 * @author Klemens Waldhör
 * 
 *         To change this generated comment edit the template variable "typecomment": Window>Preferences>Java>Templates. To enable and disable the creation of type comments go to
 *         Window>Preferences>Java>Code Generation.
 */
public class TranslateDocument implements RPCMessage
{
    /** TranslateDocument translates a document using an OpenTMS data sources. Parameters are provided through a hash table. The hashtable contains the following keys:<p>
     * openTMSTranslationPhase - the phase to apply - values can be CONVERT SEGMENT TMTRANSLATE TERMTRANSLATE BACKCONVERT TRANS BACK CONV SEG PT and their concatenation<br>
     * sourceDocument - the document to translate<br>
     * inputDocumentType - the type of the input document - either FILE if the document is supplied as a file or STRING if it is a string, MIME for MIME encoded string, ZIP for zpped files.<br>
     * sklDocument - the name of the skelton file to be produced<br>
     * xliffDocument - the name of the xliff file to be produced<br>
     * segDocument - the name of the segmented xliff file to be produced<br>
     * translatedDocument - the name of the translated xliff file<br>
     * backConvertedDocument - the name of the back converted document<br>
     * sourceDocumentLanguage - the source language<br>
     * targetDocumentLanguage - the target language<br>
     * sourceDocumentEncoding - the source encoding<br>
     * targetDocumentEncoding - the target encoding<br>
     * segmentBreakOnCrLf - for text file true if cr/lf constiture segment end<br>
     * paragraphBasesSegmentation - true use paragraph based segmentation<br>
     * dataTMXSourceName - the name of the TMX data source<br>
     * dataTMXSourceType - the type of the data source; any defined database, e.g. MySQl<br>
     * dataTMXSourceServer - the name of the server, e.g. localhost or IP address<br>
     * dataTMXSourcePort - the port of the data source, e.g. 1433<br>
     * dataTMXSourceUser - the user of the data source, e.g. sa<br>
     * dataTMXSourcePassword - the name of the data source, e.g. folt<br>
     * dataTMXSourceMatchQuality - the match quality in % as a umber between 0 and 100<br>
     * dataTMXSourceMatchMaximum - number of maximum matchs to retrieve / -1 = all <br>
     * dataTMXSourceReplacementClasses true if replacement classes shou,d be applied, otherwise false<br>
     * dataTermSourceName - the name of the terminology data source<br>
     * dataTermSourceType - the type of the data source; any defined database, e.g. MySQl<br>
     * dataTermSourceServer - the name of the server, e.g. localhost or IP address<br>
     * dataTermSourcePort - the port of the data source, e.g. 1433<br>
     * dataTermSourceUser - the user of the data source, e.g. sa<br>
     * dataTermSourcePassword - the name of the data source, e.g. folt<br>
     * some Araya special setting<br>
     * Araya_tmxfile - upon back conversion write the translations to this tmx file<br>
     * Araya_useextensions - use filename extensions to determine document format (true/false)<br>
     * Araya_donotresolveentities - for xml files - do not convert entities (true/false)<br>
     * Araya_donotresolveentitiesfile - the name of the file containing entities which should not be resolved<br>
     * Araya_useTranslateAttribute - use the translated attribute for matching (if attribute is no the segment is not translated) (true/false)<p>
     * It returns:<p>
     *  vec.add("BACKSTRING"); - for a back converted file<br>
     *  vec.add(content); - the back converted file as a string MIME encoded - BASE64Encoder().encode<br>
     *  vec.add(dataSourceName + " successfully imported!");<p>
     *  or<br>
     *  vec.add("0");<br>
     *  vec.add("TRANSSTRING");<br>
     *  vec.add(content); - the transled file as a string MIME encoded - BASE64Encoder().encode<br>
     *  vec.add("SKLSTRING");<br>
     *  vec.add(content); - the skeleton string MIME encoded - BASE64Encoder().encode<br>
     * or<p>
     *  vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_FAILURE +"");<br>
     *  vec.add(ex.getMessage());<br>
     *  <p>
     *  Here is an example translation call:<br>
     *  call java -Xmx1024m %OPENTMSJAVABASE% de.folt.rpc.client.OpenTMSClient "message=TranslateDocument" "openTMSTranslationPhase=CONVERTSEGMENTTMTRANSLATETERMTRANSLATE" "sourceDocument=docs/Expose_TMOSS_12102007.odt" "sklDocument=docs/Expose_TMOSS_12102007.odt.skl" "xliffDocument=docs/Expose_TMOSS_12102007.odt.xlf" "segDocument=docs/Expose_TMOSS_12102007.odt.seg.xlf" "translatedDocument=docs/Expose_TMOSS_12102007.odt.trans.xlf" "paragraphBasesSegmentation=yes" "segmentBreakOnCrLf=1" "dataTMXSourceName=%OPENTMSTMX%" %EXAMPLEDBTMXSERVER% "dataTermSourceName=%OPENTMSTBX%" %EXAMPLEDBTBXSERVER% dataTMXSourceMatchQuality=80 dataTMXSourceMatchMaximum=10 sourceDocumentLanguage=de dataTMXSourceReplacementClasses=no targetDocumentLanguage=en sourceDocumentEncoding=UTF-8 targetDocumentEncoding=UTF-8 inputDocumentType=FILE<br>
     *  Here is an example back conversion call:<br>
     *  call java -Xmx1024m %OPENTMSJAVABASE% de.folt.rpc.client.OpenTMSClient "message=TranslateDocument" "openTMSTranslationPhase=BACKCONVERT" "sourceDocument=docs/Expose_TMOSS_12102007.odt.trans.xlf"  "backConvertedDocument=docs/Expose_TMOSS_12102007.odt.trans.odt" sourceDocumentLanguage=de  targetDocumentLanguage=en sourceDocumentEncoding=UTF-8 targetDocumentEncoding=UTF-8 inputDocumentType=FILE<br>
     * @see de.folt.rpc.services.RPCMessage#execute(java.util.Hashtable)
     */
    @SuppressWarnings("unchecked")
    public Vector execute(Hashtable message)
    {
        Vector vec = new Vector();
        try
        {
            // the translation phase
            // CONVERT SEGMENT TMTRANSLATE TERMTRANSLATE BACKCONVERT
            String phase = de.folt.util.OpenTMSSupportFunctions.fillParam(message, "openTMSTranslationPhase");
            System.out.println("openTMSTranslationPhase=     \"" + phase + "\"");
            
            // file parameters
            // file name params
            // wk 14.11.2004 - support different values for the input file
            // FILE ... indicates content is in the file name specified
            // ... indicates inputFileName is the which contains
            // the contents
            // ZIP ... contents is zipped
            // MIME ... contents is MIME format
            String inputFileName = de.folt.util.OpenTMSSupportFunctions.fillParam(message, "sourceDocument");
            String inputType = de.folt.util.OpenTMSSupportFunctions.fillParam(message, "inputDocumentType");
            String sklFileName = de.folt.util.OpenTMSSupportFunctions.fillParam(message, "sklDocument");
            String outputFileName = de.folt.util.OpenTMSSupportFunctions.fillParam(message, "xliffDocument");
            String outputSegFileName = de.folt.util.OpenTMSSupportFunctions.fillParam(message, "segDocument");
            String outputTransFileName = de.folt.util.OpenTMSSupportFunctions.fillParam(message, "translatedDocument");
            String outputBackFileName = de.folt.util.OpenTMSSupportFunctions.fillParam(message, "backConvertedDocument");
            
            System.out.println("sourceDocument=     \"" + inputFileName + "\"");
            System.out.println("inputDocumentType=     \"" + inputType + "\"");
            System.out.println("sklDocument=     \"" + sklFileName + "\"");
            System.out.println("xliffDocument=     \"" + outputFileName + "\"");
            System.out.println("segDocument=     \"" + outputSegFileName + "\"");
            System.out.println("translatedDocument=     \"" + outputTransFileName + "\"");
            System.out.println("backConvertedDocument=     \"" + outputBackFileName + "\"");
            
            // language and encoding
            String srcLan = de.folt.util.OpenTMSSupportFunctions.fillParam(message, "sourceDocumentLanguage");
            String trgLan = de.folt.util.OpenTMSSupportFunctions.fillParam(message, "targetDocumentLanguage");
            String srcEnc = de.folt.util.OpenTMSSupportFunctions.fillParam(message, "sourceDocumentEncoding");
            String trgEnc = de.folt.util.OpenTMSSupportFunctions.fillParam(message, "targetDocumentEncoding");
            System.out.println("sourceDocumentLanguage=     \"" + srcLan + "\"");
            System.out.println("targetDocumentLanguage=     \"" + trgLan + "\"");
            System.out.println("sourceDocumentEncoding=     \"" + srcEnc + "\"");
            System.out.println("targetDocumentEncoding=     \"" + trgEnc + "\"");
            
            //  segementation params
            String breakOnCrlf = de.folt.util.OpenTMSSupportFunctions.fillParam(message, "segmentBreakOnCrLf");
            String paraseg = de.folt.util.OpenTMSSupportFunctions.fillParam(message, "paragraphBasesSegmentation");
            System.out.println("segmentBreakOnCrLf=     \"" + breakOnCrlf + "\"");
            System.out.println("paragraphBasesSegmentation=     \"" + paraseg + "\"");
            
            // tmx translate params
            String dataTMXSourceName = de.folt.util.OpenTMSSupportFunctions.fillParam(message, "dataTMXSourceName"); // folttm
            String dataTMXSourceType = de.folt.util.OpenTMSSupportFunctions.fillParam(message, "dataTMXSourceType"); // MySQL
            String dataTMXSourceServer = de.folt.util.OpenTMSSupportFunctions.fillParam(message, "dataTMXSourceServer"); // localhost
            String dataTMXSourcePort = de.folt.util.OpenTMSSupportFunctions.fillParam(message, "dataTMXSourcePort"); // 2341
            String dataTMXSourceUser = de.folt.util.OpenTMSSupportFunctions.fillParam(message, "dataTMXSourceUser"); // sa
            @SuppressWarnings("unused")
            String dataTMXSourcePassword = de.folt.util.OpenTMSSupportFunctions.fillParam(message, "dataTMXSourcePassword"); // my password

            String dataTMXSourceMatchQuality = de.folt.util.OpenTMSSupportFunctions.fillParam(message, "dataTMXSourceMatchQuality");
            String dataTMXSourceMatchMaximum = de.folt.util.OpenTMSSupportFunctions.fillParam(message, "dataTMXSourceMatchMaximum");
            String dataTMXSourceReplacementClasses = de.folt.util.OpenTMSSupportFunctions.fillParam(message, "dataTMXSourceReplacementClasses");

            System.out.println("dataTMXSourceName=     \"" + dataTMXSourceName + "\"");
            System.out.println("dataTMXSourceUser=     \"" + dataTMXSourceUser + "\"");
            // LogPrint.println("dataTMXSourcePassword= \"" + dataTMXSourcePassword + "\"");
            System.out.println("dataTMXSourceType=     \"" + dataTMXSourceType + "\"");
            System.out.println("dataTMXSourceServer=   \"" + dataTMXSourceServer + "\"");
            System.out.println("dataSourcePort=     \"" + dataTMXSourcePort + "\"");
            System.out.println("dataTMXSourceMatchQuality=     \"" + dataTMXSourceMatchQuality + "\"");
            System.out.println("dataTMXSourceMatchMaximum=     \"" + dataTMXSourceMatchMaximum + "\"");
            System.out.println("dataTMXSourceReplacementClasses=     \"" + dataTMXSourceReplacementClasses + "\"");

            // term translate params
            String dataTermSourceName = de.folt.util.OpenTMSSupportFunctions.fillParam(message, "dataTermSourceName"); // folttm
            String dataTermSourceType = de.folt.util.OpenTMSSupportFunctions.fillParam(message, "dataTermSourceType"); // MySQL
            String dataTermSourceServer = de.folt.util.OpenTMSSupportFunctions.fillParam(message, "dataTermSourceServer"); // localhost
            String dataTermSourcePort = de.folt.util.OpenTMSSupportFunctions.fillParam(message, "dataTermSourcePort"); // 2341
            String dataTermSourceUser = de.folt.util.OpenTMSSupportFunctions.fillParam(message, "dataTermSourceUser"); // sa
            @SuppressWarnings("unused")
            String dataTermSourcePassword = de.folt.util.OpenTMSSupportFunctions.fillParam(message, "dataTermSourcePassword"); // my password

            System.out.println("dataTermSourceName=     \"" + dataTermSourceName + "\"");
            System.out.println("dataTermSourceUser=     \"" + dataTermSourceUser + "\"");
            // LogPrint.println("dataTermSourcePassword= \"" + dataTermSourcePassword + "\"");
            System.out.println("dataTermSourceType=     \"" + dataTermSourceType + "\"");
            System.out.println("dataTermSourceServer=   \"" + dataTermSourceServer + "\"");
            System.out.println("dataSourcePort=     \"" + dataTermSourcePort + "\"");
            
            // some Araya special setting
            String tmxFile = de.folt.util.OpenTMSSupportFunctions.fillParam(message, "Araya_tmxfile");
            String useExtensions = de.folt.util.OpenTMSSupportFunctions.fillParam(message, "Araya_useextensions");
            String donotresolveentities = de.folt.util.OpenTMSSupportFunctions.fillParam(message, "Araya_donotresolveentities");
            String donotresolveentitiesfile = de.folt.util.OpenTMSSupportFunctions.fillParam(message, "Araya_donotresolveentitiesfile");
            String supportTranslateAttribute = de.folt.util.OpenTMSSupportFunctions.fillParam(message, "Araya_useTranslateAttribute");
            
            System.out.println("Araya_tmxfile=     \"" + tmxFile + "\"");
            System.out.println("Araya_useextensions=     \"" + useExtensions + "\"");
            System.out.println("Araya_donotresolveentities=     \"" + donotresolveentities + "\"");
            System.out.println("Araya_donotresolveentitiesfile=     \"" + donotresolveentitiesfile + "\"");
            System.out.println("Araya_useTranslateAttribute=     \"" + supportTranslateAttribute + "\"");

            String propFile = OpenTMSProperties.getInstance().getOpenTMSProperty("ArayaPropertiesFile");
            message.put("ArayaPropertiesFile", propFile);
            System.out.println("ArayaPropertiesFile=\"" + propFile + "\"");
            Vector retVec = com.araya.OpenTMS.Interface.runConverter(message);
            System.out.println("TranslateDocument " + " finished!");
            vec = retVec;
            return vec;
        }
        catch (Exception ex)
        {
            vec.add(de.folt.constants.OpenTMSConstants.OpenTMS_ID_FAILURE +"");
            vec.add(ex.getMessage());
            ex.printStackTrace();
            return vec;
        }
    }
}