/*
 * Created on 10.09.2008
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.constants;

/**
 * This class defines the constants used in OpenTMS. All the main constants should be added here. An OpenTMS constant should always start with OpenTMS_<constant name>
 * @author klemens
 *
 */
public class OpenTMSConstants
{

    /**
     * Constants defines if an xml element could not be generated from a string representation of an element.
     * For more details {@see de.folt.models.documentmodel.document.XmlDocument#buildElement}
     */
    public static final int OpenTMS_BUILDELEMET_ERROR = 8000;
    /**
     * OpenTMS_DATAMODEL_NOTFOUND_ERROR indicates that a given data model was not found
     */
    
    public static final int OpenTMS_DATAMODEL_NOTFOUND_ERROR = 1010;
    
    /**
     * OpenTMS_DATASOURCE_NOTFOUND_ERROR indicates that a given data source was not found in the DatSourceInstance hash table
     */
    public static final int OpenTMS_DATASOURCE_NOTFOUND_ERROR = 1000;
    
    
    /**
     * OpenTMS_EXCEPTION_ERROR indicates an error from an OpenTMS Exception.
     */
    public static final int OpenTMS_EXCEPTION_ERROR = 9;
    
    
    /**
     * OpenTMS_ID_FAILURE is the standard value for a not successful operation.
     */
    public static int OpenTMS_ID_FAILURE = 1;
    
    /**
     * OpenTMS_ID_SUCCESS is the standard value for a successful operation.
     */
    public static int OpenTMS_ID_SUCCESS = 0;
    
    
    /**
     * OpenTMS_TMXDATASOURCE_FILE_NOTFOUND_ERROR indicates that the tmx file specified was not found or does not exist.
     */
    public static final int OpenTMS_TMXDATASOURCE_FILE_NOTFOUND_ERROR = 2000;
    
    /**
     * OpenTMS_TMXDATASOURCE_FILE_NULL_ERROR the tmx file was null
     */
    public static final int OpenTMS_TMXDATASOURCE_FILE_NULL_ERROR = 2001;
    
    /**
     * OpenTMS_XLIFFDATASOURCE_FILE_NOTFOUND_ERROR indicates that the xliff file specified was not found or does not exist.
     */
    public static final int OpenTMS_XLIFFDATASOURCE_FILE_NOTFOUND_ERROR = 2010;
    
    /**
     * OpenTMS_FILE_NOTFOUND_ERROR indicates that the file specified was not found or does not exist.
     */
    public static final int OpenTMS_FILE_NOTFOUND_ERROR = 2020;
    
    
    /**
     * The PlainText method was not found for MonoLingualObject Creation
     */
    public static final int OpenTMS_TRANSLATE_PLAINTEXTMETHOD_NOTFOUND_ERROR = 7000;
    
    /**
     * The SQL Data source could not be initialized because the data source configuration file does not exist
     */
    public static final int OpenTMS_OPENTMSSQLDATASOURCE_ERROR_DATASOURCE_CONFIG_NOT_FOUND = 8000;
    
    /**
     * The SQL Data source could not be initialized because the  hibernate configuration file does not exist
     */
    public static final int OpenTMS_OPENTMSSQLDATASOURCE_ERROR_HIBERNATE_CONFIG_NOT_FOUND = 8010;
    
    /** 
     * The file is not an xliff file
     */
    public static final int OpenTMS_NOT_AN_XLIFF_FILE = 9000;
    
    /**
     * An error occured when exporting the data source to tmx; value is negative so that it cannot be confused with the number of entries returned from the export
     */
    public static final int OpenTMS_TMX_EXPORT_ERROR = -10000;
    
    /**
     * OpenTMS_UNSUPPORTED_ENCODING - the encoding is not supported.
     */
    public static final int OpenTMS_UNSUPPORTED_ENCODING = 12000;
    
    /**
     * OpenTMS_IOEXCEPTION - io exception has ocuured.
     */
    public static final int OpenTMS_IOEXCEPTION = 12100;
    
    /**
     * OpenTMS_FILE_NOTFOUND_ERROR indicates that the file specified was not found or does not exist.
     */
    public static final int OpenTMS_DATASOURCECONFIGURATIONFILECREATION_ERROR = 13000;

    /**
     * OpenTMS_SYNC_UNKNOWN_METHODE_SERVICE_ERROR indicates that an unknown sync method was called 
     */
    public static final int OpenTMS_SYNC_UNKNOWN_METHODE_SERVICE_ERROR = 20000;
    
    /**
     * OpenTMS_SYNC_UNKNOWN_METHODE_SERVICE_ERROR indicates that an unknown sync method was called 
     */
    public static final String OpenTMS_SYNC_UNKNOWN_METHODE_SERVICE_ERROR_MESSAGE = "Unknown sync method called";
    
    /**
     * OpenTMS_SYNC_UNKNOWN_METHODE_SERVICE_ERROR indicates that an unknown sync method was called 
     */
    public static final int OpenTMS_SYNC_DASTASOURCE_CONFIGURATION_NOT_FOUND_SERVICE_ERROR = 20010;
    
    /**
     * OpenTMS_SYNC_UNKNOWN_METHODE_SERVICE_ERROR indicates that an unknown sync method was called 
     */
    public static final String OpenTMS_SYNC_DASTASOURCE_CONFIGURATION_NOT_FOUND_SERVICE_ERROR_MESSAGE = "Data Source Configraion File not found";
    
    /**
     * OpenTMS_SYNC_UNKNOWN_METHODE_SERVICE_ERROR indicates that an unknown sync method was called 
     */
    public static final int OpenTMS_SYNC_SYNC_ERROR = 20020;
    
    /**
     * OpenTMS_SYNC_UNKNOWN_METHODE_SERVICE_ERROR indicates that an unknown sync method was called 
     */
    public static final String OpenTMS_SYNC_SYNC_ERROR_ERROR_MESSAGE = "upload not executed";
    
    /**
     * 
     */
    public static final int OpenTMS_SYNC_EXCEPTION_SERVICE_ERROR = 200050;
    
    /**
     * 
     */
    public static final String OpenTMS_JSONDESERIALISE_EXCEPTION_ERROR_MESSAGE = "JSON Deserialisation Exception";
    
    /**
     * 
     */
    public static final int OpenTMS_JSONDESERIALISE_EXCEPTION_SERVICE_ERROR = 200060;
    
    /**
     * 
     */
    public static final String OpenTMS_SYNC_EXCEPTION_ERROR_MESSAGE = "Synchronisation Exception";
    
}
