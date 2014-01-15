/*
 * Created on 05.01.2009
 * 
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.models.datamodel;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Observer;
import java.util.Vector;

import org.jdom.Element;

import de.folt.models.documentmodel.xliff.XliffDocument;
import de.folt.util.OpenTMSException;

/**
 * Interface implements the main methods a data source must support.
 * 
 * @author klemens
 * 
 */
public interface DataSource extends Observer, Enumeration<MultiLingualObject>
{
	/**
	 * addData this allows to add a whatever object to the data source based on
	 * a key
	 * 
	 * @param key
	 *            the key for the object
	 * @param object
	 *            the value for the key
	 */
	public void addData(String key, Object object);

	/**
	 * addMultiLingualObject adds a MonoLingualObject to the given
	 * MultiLingualObject to the data source
	 * 
	 * @param monoLingualObject
	 *            the MOL object to add
	 * @param bMergeObjects
	 *            if true first a search is done if the containing
	 *            MonoLingualObjects exist in the data source (based on the
	 *            segment text); if they exist they are merged; if false the MUL
	 *            is added as new entry
	 * @return true if added; false if the MultiLingualObject does not exist
	 */
	public boolean addMonoLingualObject(MonoLingualObject monoLingualObject, boolean bMergeObjects);

	/**
	 * addMultiLingualObject adds a MultiLingualObject to the data source
	 * 
	 * @param multiLingualObject
	 *            the object to add
	 * @param bMergeObjects
	 *            if true first a search is done if the containing
	 *            MonoLingualObjects exist in the data source (based on the
	 *            segment text); if they exist they are merged; if false the MUL
	 *            is added as new entry
	 * @return true if added; false if not
	 */
	public boolean addMultiLingualObject(MultiLingualObject multiLingualObject, boolean bMergeObjects);

	/**
	 * bAuthenticate checks if for a given data source access is granted thru
	 * user and password
	 * 
	 * @param userName
	 *            the user name
	 * @param password
	 *            the password
	 * @return true if user / password does match and access granted, false if
	 *         user / password does not match
	 */
	public boolean bAuthenticate(String userName, String password);

	/**
	 * bPersist method persists any data in the data source
	 * 
	 * @return true in case of success or false otherwise
	 */
	public boolean bPersist();

	/**
	 * bSupportMultiThreading defines support of multi threading (mainly for
	 * translate method)
	 * 
	 * @return true if multi threading of data source access (for translate) is
	 *         supported or not (=false)
	 */
	public boolean bSupportMultiThreading();

	/**
	 * changedMonolingualObjects
	 * 
	 * @return a Vector of changed or new MonoLingualObjects since the last read
	 *         operation
	 */
	public Vector<MonoLingualObject> changedMonolingualObjects();;

	/**
	 * checkIfTranslationExistsInDataSource Method checks if a for a given
	 * source and target MOL matches in the data source exist.<br>
	 * This is a more complex version of
	 * checkIfTranslationExistsInDataSource(String sourceSegment, String
	 * sourceLanguage, String targetSegment, String targetLanguage) as it checks
	 * for the linguistic properties of the involved mono lingual objects too.
	 * 
	 * @param source
	 *            the source MOL
	 * @param target
	 *            the target MOL
	 * @return a SearchStatusResult object
	 */
	public TranslationCheckResult checkIfTranslationExistsInDataSource(MonoLingualObject source, MonoLingualObject target);

	/**
	 * checkIfTranslationExistsInDataSource function checks if for a given
	 * combination of source segment/source language - target segment
	 * (translation)/target language a translation exists
	 * 
	 * @param sourceSegment
	 *            the source segment
	 * @param sourceLanguage
	 *            the source language
	 * @param targetSegment
	 *            the target segment
	 * @param targetLanguage
	 *            the target language
	 * @return a TranslationCheckResult (for more details see {@see
	 *         de.folt.models.datamodel.TranslationCheckResult})
	 */
	public TranslationCheckResult checkIfTranslationExistsInDataSource(String sourceSegment, String sourceLanguage, String targetSegment,
			String targetLanguage);

	/**
	 * cleanDataSource method is used to allow some clean operations e.g. after
	 * an instance has been loaded. <br>
	 * An example is disposing a tmx document after having read all the entries.
	 */
	public void cleanDataSource();

	/**
	 * clearDataSource removes the content in a data source. In addition it can
	 * be used to delete a TMX data source.
	 * 
	 * @return true if success
	 * @throws OpenTMSException
	 */
	public boolean clearDataSource() throws OpenTMSException;

	/**
	 * containsKey checks if a key exists
	 * 
	 * @param key
	 *            the key to search for
	 * @return true if key exists, otherwise false
	 */
	public boolean containsKey(String key);

	/**
	 * containsValue checks if a key exists
	 * 
	 * @param object
	 *            the object (value) to search for
	 * @return true if key exists, otherwise false
	 */
	public boolean containsValue(Object object);

	/**
	 * copyFrom copies the content of the data source dataSource to the this
	 * data source; this requires that the data source implements the methods
	 * nextElement; iniEnumeration and hasMoreElements. Those methods are used
	 * to get the MultiLingualObject and store them.
	 * 
	 * @param dataSource
	 */
	public int copyFrom(DataSource dataSource);

	/**
	 * copyTo copies the content of this data source to the specified data
	 * source; this requires that the data source implements the methods
	 * nextElement; iniEnumeration and hasMoreElements. Those methods are used
	 * to get the MultiLingualObject and store them.
	 * 
	 * @param dataSource
	 */
	public int copyTo(DataSource dataSource);

	/**
	 * createDataSource creates a new Data source; this is esp. intended for
	 * creating a new database, e.g. in MySQL or Ms SQL Server. In addition it
	 * can be used to create a new empty TMX data source.
	 * 
	 * @param dataModelProperties
	 *            the parameters of the data source
	 * @return true if success
	 * @throws OpenTMSException
	 */
	public boolean createDataSource(DataSourceProperties dataModelProperties) throws OpenTMSException;

	/**
	 * currentTimeMillis This method returns the current time as a String. The
	 * method should return the date/time where the data source is running (e.g.
	 * the database server time). The method is mainly used to add time stamps
	 * to the MUL/MOL objetcs (creation, update, delete). It should return the
	 * server time in order to allow comparsion between different users running
	 * (client sided) OpenTMS software. The result is used to synchronise the
	 * acesses between differetn users/processes.
	 * 
	 * @return date/time as a long - similar to currentTimeMillis() in Java (the
	 *         difference, measured in milliseconds, between the current time
	 *         and midnight, January 1, 1970 UTC.)
	 */
	public long currentTimeMillis();

	/**
	 * deleteDataSource deletes an existing Data source; this is esp. intended
	 * for deleting a database, e.g. in MySQL or Ms SQL Server. In addition it
	 * can be used to delete a TMX data source.<br>
	 * Currently supported key values are:<br>
	 * tmxfile, tmxFile, dataSourceName, dataSource, xlifffile, xliffFile -
	 * specifies names of the data sources<br>
	 * sourceLanguage if an xliff File datasource is created default = de<br>
	 * targetLanguage if an xliff File datasource is created default = en<br>
	 * 
	 * @param dataModelProperties
	 *            the parameters of the data source
	 * @return true if success
	 * @throws OpenTMSException
	 */
	public boolean deleteDataSource(DataSourceProperties dataModelProperties) throws OpenTMSException;

	/**
	 * exportTmxFile exports the data source into an tmx file
	 * 
	 * @param tmxFile
	 *            the tmx file to export to
	 * @return the number of entries exported
	 */
	public int exportTmxFile(String tmxFile);

	/**
	 * exportXliffFile export the xliff file from the data source;
	 * 
	 * @param xliffFile
	 *            the xliff file to export
	 * @return the number of entries exported
	 */
	public int exportXliffFile(String xliffFile);

	/**
	 * Write all Attributes and values to output file
	 * 
	 * @param outputfile
	 *            target output file
	 */
	public void getAllAttributes(String outputfile);

	/**
	 * getChangedIds this method returns a vector of Integers of changed MULs
	 * which uniquely identify each MUL of the data source
	 * 
	 * @return a vector with the MUL unique ids
	 */
	public Vector<Integer> getChangedIds();

	/**
	 * getData retrieves an object associated with the key
	 * 
	 * @param key
	 *            the key to search for
	 * @return the object found
	 */
	public Object getData(String key);

	/**
	 * Get the data source configuarions for the data source
	 * @return
	 */
	public DataSourceConfigurations getDataSourceConfigurations();

	/**
	 * getDataSourceName returns the name of the data source
	 * 
	 * @return
	 */
	public String getDataSourceName();

	/**
	 * getDataSourceProperties returns the DataSourceProperties of the data
	 * source
	 * 
	 * @return the DataSourceProperties
	 * @throws OpenTMSException
	 */
	public DataSourceProperties getDataSourceProperties() throws OpenTMSException;

	/**
	 * getDataSourceType get the type of the database
	 * 
	 * @return the data source type
	 */
	public String getDataSourceType();

	/**
	 * getDefaultDataSourceConfigurationsFileName return the name of the default
	 * data source configurations file name
	 * 
	 * @return the default configuration file name
	 */

	public String getDefaultDataSourceConfigurationsFileName();

	/**
	 * getIds this method returns a vector of Integers which uniquely identify
	 * each MUL of the data source
	 * 
	 * @return a vector with the unique ids
	 */
	public Vector<Integer> getIds();

	/**
	 * getLastErrorCode Method returns the last error code for an operation done
	 * by the data source
	 * 
	 * @return an OpenTMS Error code describing the error which occurred for the
	 *         last operation
	 */
	public int getLastErrorCode();

	/**
	 * getMonoLingualObjectFromId search an MOL based on its (integer) id
	 * 
	 * @param uniqueID
	 * @return the MOL found or null otherwise
	 */
	public MonoLingualObject getMonoLingualObjectFromId(String uniqueID);

	/**
	 * getMonoLingualObjectFromUniqueId search an MOL based on its (integer) id
	 * 
	 * @param uniqueID
	 *            the id to search (as String)
	 * @return the MOL found or null otherwise
	 */
	public MonoLingualObject getMonoLingualObjectFromUniqueId(String uniqueID);

	/**
	 * getMultiLingualObjectFromId search an MUL based on its unique id
	 * 
	 * @param id
	 *            the id to search (as String)
	 * @return the MOL found or null otherwise
	 */
	public MultiLingualObject getMultiLingualObjectFromId(String id);

	/**
	 * getMultiLingualObjectFromUniqueId search an MUL based on its unique id
	 * 
	 * @param id
	 *            the id to search (as String)
	 * @return the MUL found or null otherwise
	 */
	public MultiLingualObject getMultiLingualObjectFromUniqueId(String id);

	/**
	 * getUniqueIds this method returns a vector of strings which uniquely
	 * identify each MUL of the data source
	 * 
	 * @return
	 */
	public Vector<String> getUniqueIds();

	/**
	 * importTbxFile imports a tbx file into the data source;
	 * 
	 * @param tbxFile
	 *            the tbx file to import
	 * @return the number of entries imported
	 */
	public int importTbxFile(String tbxFile);

	/**
	 * importTmxFile import a tmx file into the datasource
	 * 
	 * @param tmxFile
	 *            the tmx file to import
	 * @return
	 */
	public int importTmxFile(String tmxFile);

	/**
	 * importXliffFile imports an xliff file into the data source; it imports
	 * the approved source / target's of the trans-units
	 * 
	 * @param xliffFile
	 *            the xliff file to import
	 * @return the number of entries imported
	 */
	public int importXliffFile(String xliffFile);

	/**
	 * initEnumeration initialises the Enumeration of the data source
	 */
	public void initEnumeration();

	/**
	 * This method determines if the attributes of MultiLingualObjects or
	 * MonoLingualObjects should be loaded at the time when the data source is
	 * created (false) or when they are actually needed (true).
	 * 
	 * @return the bLoadAttributesLazy true if the should be loaded when need /
	 *         false when they should be loaded at creation time of the data
	 *         source.
	 */
	public boolean isBLoadAttributesLazy();

	/**
	 * This methods returns true if the data source (the instance of the data source) is a synchronizable data source instance.
	 * 
	 * @return true if this is a synchronizable data source
	 */
	public boolean isSyncDataSource();

	/**
	 * removeData removes an object associated with the key
	 * 
	 * @param key
	 *            the key to remove
	 * @return the object removed
	 */
	public Object removeData(String key);

	/**
	 * removeDataSource method called when the DataSource should be removed
	 * 
	 * @throws OpenTMSException
	 */
	public void removeDataSource() throws OpenTMSException;

	/**
	 * removeMonoLingualObject removes a MonoLingualObject from the data source;
	 * not all data source may support this method and may return false in any
	 * case (e.g. XliffDataSource)
	 * 
	 * @param monoLingualObject
	 *            the MUL to remove
	 * @return true if removed; false if not
	 */
	public boolean removeMonoLingualObject(MonoLingualObject monoLingualObject);

	/**
	 * removeMultiLingualObject removes a MultiLingualObject from the data
	 * source
	 * 
	 * @param multiLingualObject
	 *            the MOL to remove
	 * @return true if removed; false if not
	 */
	public boolean removeMultiLingualObject(MultiLingualObject multiLingualObject);

	/**
	 * saveModifiedMonoLingualObject save a given MonoLingualObject in the data
	 * source by replacing the old values. It is now allowed to change the
	 * uniquei and the id of the MOL.
	 * 
	 * @param monoLingualObject
	 *            the mono lingual object to save return true for success, false
	 *            otherwise
	 */
	public boolean saveModifiedMonoLingualObject(MonoLingualObject monoLingualObject);

	/**
	 * saveModifiedMultiLingualObject saves any modification of the
	 * MultiLingualObject, esp. doen to the LinguisticProperties
	 * 
	 * @param mul
	 *            the MultiLingualObject
	 * @return true if success
	 */
	public boolean saveModifiedMultiLingualObject(MultiLingualObject mul);

	/**
	 * search search for a MonoLingualObject; if no language is given in the
	 * searchMonoLingualObject the search is done independent of the language
	 * 
	 * @param searchMonoLingualObject
	 *            the MonoLingualObject to search for
	 * @param searchParameters
	 *            the hash table contains search parameters (e.g. language to
	 *            search for etc.) <br>
	 *            Example: matchMultiLingualLinguisticProperties for
	 *            MultiLingualObjects or matchMonoLingualLinguisticProperties
	 *            for MonoLingualObjects as filters
	 * @return a vector of MOL which contain the segments of the given
	 *         monoLingualObject
	 */
	public Vector<MonoLingualObject> search(MonoLingualObject searchMonoLingualObject, Hashtable<String, Object> searchParameters);

	/**
	 * search searchRegExp for a MonoLingualObject where the plain text
	 * represents a regular expression; if no language is given in the
	 * searchMonoLingualObject the search is done independent of the language
	 * 
	 * @param searchMonoLingualObject
	 *            the MonoLingualObject to search for
	 * @param searchParameters
	 *            the hash table contains search parameters (e.g. language to
	 *            search for etc.)
	 * @return a vector of MOL which contain segment of the given
	 *         monoLingualObject
	 */
	public Vector<MonoLingualObject> searchRegExp(MonoLingualObject searchMonoLingualObject, Hashtable<String, Object> searchParameters);

	/**
	 * search searchWordBased for a MonoLingualObject where the plain text
	 * segment is searched by splitting it up into words; if no language is
	 * given in the searchMonoLingualObject the search is done independent of
	 * the language
	 * 
	 * @param searchMonoLingualObject
	 *            the MonoLingualObject to search for
	 * @param searchParameters
	 *            the hash table contains search parameters (e.g. language to
	 *            search for etc.);
	 * @return a vector of MOL which contain segment of the given
	 *         monoLingualObject
	 */
	public Vector<MonoLingualObject> searchWordBased(MonoLingualObject searchMonoLingualObject, Hashtable<String, Object> searchParameters);

	/**
	 * This method sets loading of the the attributes of MultiLingualObjects or
	 * MonoLingualObjects; false = they should be loaded at the time when the
	 * data source is created or true = when they are actually needed.
	 * 
	 * @param loadAttributesLazy
	 *            the bLoadAttributesLazy to set; false = load at creation time;
	 *            true load at time when needed
	 */
	public void setBLoadAttributesLazy(boolean loadAttributesLazy);

	/**
	 * Set the dataSourceConfigurations for the data source
	 * @param dataSourceConfigurations
	 */
	public void setDataSourceConfigurations(DataSourceConfigurations dataSourceConfigurations);

	/**
	 * Methods sets a specific data source property and writes back to data source property file
	 * 
	 * @param dataProps
	 */
	public void setDataSourceProperties(DataSourceProperties dataProps);

	/**
	 * setDataSourceType sets the type of the data source
	 */
	public void setDataSourceType();

	/**
	 * setDefaultDataSourceConfigurationsFileName set the name of the default
	 * data source configurations file name
	 * 
	 * @param defaultDataSourceConfigurationsFileName
	 *            the name of the default data source configurations file name
	 */
	public void setDefaultDataSourceConfigurationsFileName(String defaultDataSourceConfigurationsFileName);

	/**
	 * setLastErrorCode sets the error code of the currently running method of
	 * the data source
	 * 
	 * @param lastErrorCode
	 *            the error code
	 */
	public void setLastErrorCode(int lastErrorCode);

	/**
	 * subSegmentResultsToGlossary returns an array of glossary elements for the
	 * subSegment matching translations
	 * 
	 * @param sourceLanguage
	 *            the source language for the glossary
	 * @param targetLanguage
	 *            the target language for the glossary
	 * @return the glossary element
	 */

	public Element[] subSegmentResultsToGlossary(String sourceLanguage, String targetLanguage);

	/**
	 * translate subSegmentTranslate a trans-unit given the source language,
	 * target Language on a sub segment level (e.g.phrase or terminology based)
	 * 
	 * @param transUnit
	 *            the trans unit to translate to use
	 * @param xliffDocument
	 *            the basic xliff document
	 * @param sourceLanguage
	 *            the source language to use
	 * @param targetLanguage
	 *            the target language to use
	 * @param translationParameters
	 *            the hash table contains parameters which control some
	 *            parameters, e.g. should header/source/target properties be
	 *            written to alt-trans
	 * @return the modified trans-unit with new translation
	 * @throws OpenTMSException
	 */
	public Element subSegmentTranslate(Element transUnit, XliffDocument xliffDocument, String sourceLanguage, String targetLanguage,
			Hashtable<String, Object> translationParameters) throws OpenTMSException;

	/**
	 * translate translates a trans-unit given the source language, target
	 * Language and match similarity
	 * 
	 * @param transUnit
	 *            the trans unit to translate to use
	 * @param file
	 *            the file element currently to translate
	 * @param xliffDocument
	 *            the basic xliff document
	 * @param sourceLanguage
	 *            the source language to use
	 * @param targetLanguage
	 *            the target language to use
	 * @param matchSimilarity
	 *            the similarity (fuzzy) match quality (0 - 100) to use
	 * @param translationParameters
	 *            the hash table contains parameters which control some
	 *            parameters, e.g. should header/source/target properties be
	 *            written to alt-trans
	 * @return the modified trans-unit with new translation
	 * @throws OpenTMSException
	 */
	public Element translate(Element transUnit, Element file, XliffDocument xliffDocument, String sourceLanguage, String targetLanguage,
			int matchSimilarity, Hashtable<String, Object> translationParameters) throws OpenTMSException;
	
	/**
	 * This method can be called after insert or similar actions to update internal stati, fuzzy trees etc.
	 */
	public void update();

	/**
	 * @param string
	 * @param userlist
	 */
	public void updateDataSourceProperty(String string, String userlist);

}
