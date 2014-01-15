/*
 * Created on 13.12.2008
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.models.datamodel;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;

import javax.persistence.Column;
import javax.persistence.OneToMany;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import flexjson.locators.TypeLocator;

/**
 * This class forms the basis of all OpenTMS language related classes. {@see <a
 * href="http://www.opentms.de/files/techspec.html">OpenTMS Software
 * Architecture</a>}<br>
 * Its main components are:<br>
 * 
 * <pre>
 * stUniqueID - the unique id of the object, a string generated normally by UUID.randomUUID().toString()
 * lastAccessTime - the time when the Object was accessed the last time
 * lingType - the linguistic type associated with the class; derived from LinguisticTypes
 * linguisticProperties - a set of properties associated with the object
 * stOwner - the owner of the object (e.g. user)
 * </pre>
 * 
 * @author klemens
 */

public class GeneralLinguisticObject extends Observable implements Serializable
{

	/**
	 * Currently supported LinguisticTypes (TMX, TERM, XLIFF)
	 * 
	 * @author klemens
	 * 
	 */
	public enum LinguisticTypes
	{
		TERM, TMX, XLIFF
	}

	public class TestObserver implements Observer
	{

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Observer#update(java.util.Observable,
		 * java.lang.Object)
		 */
		@Override
		public void update(Observable arg0, Object arg1)
		{
			System.out.println("Something has changed: " + arg0 + " " + arg1);
		}

	}

	private static boolean bLogExceptions = false;

	/**
     * 
     */
	private static final long serialVersionUID = 4608912959983889141L;

	/**
	 * fromJson convert into a GeneralLinguisticObject from JSON String
	 * 
	 * @param jsonMono
	 *            the json formatted string
	 * @return the GeneralLinguisticObject
	 */
	public static GeneralLinguisticObject fromJson(String jsonMono)
	{
		try
		{
			return new JSONDeserializer<GeneralLinguisticObject>()
			//
					.use("linguisticProperties", new TypeLocator<String>("linguisticProperties").add("LinguisticProperty", LinguisticProperty.class))
					//		
					.use("GeneralLinguisticObject.linguisticProperties", LinguisticProperties.class)
					//
					.deserialize(jsonMono, GeneralLinguisticObject.class);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @return the bLogExceptions
	 */
	public static boolean isbLogExceptions()
	{
		return bLogExceptions;
	}

	/**
	 * main
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		test();
	}

	/**
	 * @param bLogExceptions
	 *            the bLogExceptions to set
	 */
	public static void setbLogExceptions(boolean bLogExceptions)
	{
		GeneralLinguisticObject.bLogExceptions = bLogExceptions;
	}

	/**
	 * test simple test method for generating some general linguistic objects
	 */
	public static void test()
	{
		try
		{
			GeneralLinguisticObject gen1 = new GeneralLinguisticObject();
			gen1.addStringLinguisticProperty("OpenTMS", "Folt");
			System.out.println(gen1.toString());
			System.out.println(gen1.format());
			GeneralLinguisticObject gen2 = new GeneralLinguisticObject();
			System.out.println(gen2.toString());
			System.out.println(gen2.format());
			GeneralLinguisticObject gen3 = new GeneralLinguisticObject(null, GeneralLinguisticObject.LinguisticTypes.TERM);
			System.out.println(gen3.toString());
			System.out.println(gen3.format());
			GeneralLinguisticObject gen4 = new GeneralLinguisticObject("hallo", null, GeneralLinguisticObject.LinguisticTypes.TERM);
			System.out.println(gen4.toString());
			System.out.println(gen4.format());

			gen1.addStringLinguisticProperty("mykey", "myvalue");
			LinguisticProperty ling1 = gen1.getObjectLinguisticProperty("mykey");
			ling1.addObserver(gen1.new TestObserver());

			LinguisticProperties lings1 = (LinguisticProperties) gen1.getLinguisticProperties();
			lings1.addObserver(gen1.new TestObserver());

			System.out.println(gen1.format());
			gen1.addStringLinguisticProperty("mynextkey", "mynextvalue");
			System.out.println(gen1.format());

			ling1.setValue("newvalue");
			System.out.println(gen1.format());

			ling1.setValue("backvalue");
			System.out.println(gen1.format());

		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
     */
	/**
	 * 
	 */
	protected Long creationTime = 0l;
	
	/**
	 * 
	 */
	private Exception exception = null;

	/**
	 * 
	 */
	protected Integer id = 0;

	/**
	 * 
	 */
	private ScriptEngine jsEngine = null;

	/**
     */
	protected Long lastAccessTime = 0l;

	// private Map<String, Object> linguisticProperties = new
	// LinguisticProperties();

	/**
     * 
     */
	protected LinguisticTypes lingType = LinguisticTypes.TMX;

	/**
	 * 
	 */
	protected LinguisticProperties linguisticProperties = new LinguisticProperties();

	/**
	 * 
	 */
	private ScriptEngineManager mgr = null;

	/**
	 * 
	 */
	protected Long modificationTime = 0l;

	/**
     * 
     */
	protected String stOwner = System.getProperty("user.name");
	
	/**
     */
	protected String stUniqueID = "";

	/**
	 * 
	 */
	protected Long usageNumber = 0l;

	/**
     * 
     */
	public GeneralLinguisticObject()
	{
		super();
		this.stUniqueID = UUID.randomUUID().toString();
		this.stOwner = System.getProperty("user.name");
		this.linguisticProperties = new LinguisticProperties();
		this.creationTime = System.currentTimeMillis();
		this.modificationTime = System.currentTimeMillis();
		this.usageNumber = 0l;
		this.id = 0;
	}

	/**
	 * @param id
	 * @param lingType
	 * @param linguisticProperties
	 * @param stUniqueID
	 */
	public GeneralLinguisticObject(Integer id, LinguisticTypes lingType, LinguisticProperties linguisticProperties, String stUniqueID)
	{
		super();
		this.id = id;
		this.lingType = lingType;
		this.linguisticProperties = linguisticProperties;
		this.stUniqueID = stUniqueID;
		this.creationTime = System.currentTimeMillis();
		this.modificationTime = System.currentTimeMillis();
		this.usageNumber = 0l;
	}

	/**
	 * @param linguisticProperties
	 * @param lingType
	 */
	public GeneralLinguisticObject(LinguisticProperties linguisticProperties, LinguisticTypes lingType)
	{
		super();
		this.stUniqueID = UUID.randomUUID().toString();
		this.linguisticProperties = linguisticProperties;
		this.lingType = lingType;
		this.stOwner = System.getProperty("user.name");
		this.id = 0;
		this.creationTime = System.currentTimeMillis();
		this.modificationTime = System.currentTimeMillis();
		this.usageNumber = 0l;
	}
	
	/**
	 * @param uniqueID
	 * @param linguisticProperties
	 * @param lingType
	 */
	public GeneralLinguisticObject(String uniqueID, LinguisticProperties linguisticProperties, LinguisticTypes lingType)
	{
		super();
		stUniqueID = uniqueID;
		this.lastAccessTime = 0l;
		this.linguisticProperties = linguisticProperties;
		this.lingType = lingType;
		stOwner = System.getProperty("user.name");
		this.id = 0;
		this.creationTime = System.currentTimeMillis();
		this.modificationTime = System.currentTimeMillis();
		this.usageNumber = 0l;
	}
	
	

	/**
	 * addLinguisticPropertiy
	 * 
	 * @param linguisticProperty
	 */
	public void addLinguisticProperty(LinguisticProperty linguisticProperty)
	{
		if (this.linguisticProperties == null)
			this.linguisticProperties = new LinguisticProperties();
		linguisticProperties.put((String) linguisticProperty.getKey(), linguisticProperty);
		this.notifyObservers(linguisticProperties);
	}

	/**
	 * addObjectLinguisticPropertiy
	 * 
	 * @param key
	 *            any object - must be possible to generate a String value from
	 *            it
	 * @param value
	 *            the value to associate with the property
	 */
	public void addObjectLinguisticProperty(Object key, Object value)
	{
		if (this.linguisticProperties == null)
			this.linguisticProperties = new LinguisticProperties();
		LinguisticProperty linguisticProperty = new LinguisticProperty(key, value);
		linguisticProperties.put((String) linguisticProperty.getKey(), linguisticProperty);
		this.setChanged();
		this.notifyObservers(linguisticProperties);
	}

	/**
	 * addStringLinguisticPropertiy
	 * 
	 * @param key
	 *            the key as a string to associate with the property
	 * @param value
	 *            the string to associate with the property
	 */
	public void addStringLinguisticProperty(String key, String value)
	{
		if (this.linguisticProperties == null)
			this.linguisticProperties = new LinguisticProperties();
		LinguisticProperty linguisticProperty = new LinguisticProperty(key, value);
		// linguisticProperties.put((String) linguisticProperty.getKey(),
		// linguisticProperty);
		linguisticProperties.add(linguisticProperty);
		this.setChanged();
		this.notifyObservers(linguisticProperties);
	}

	/**
	 * bCompare compare two GeneralLinguisticObject; if all the values and
	 * LinguisticProperties are identical return true;
	 * id/stUniqueID/lastAccessTime are never compared
	 * 
	 * @param genObj
	 *            the object to compare against
	 * @return true if LinguisticProperties match
	 */
	public boolean bCompare(GeneralLinguisticObject genObj)
	{
		return bCompare(genObj, false);
	}

	/**
	 * bCompare compare two GeneralLinguisticObject; if all the values and
	 * LinguisticProperties are identical return true;
	 * id/stUniqueID/lastAccessTime are never compared
	 * 
	 * @param genObj
	 *            the object to compare against
	 * @param bCompareCoreAttributes
	 *            if true compare lingType and stOwner too
	 * @return true if lingType, stOwner attributes and LinguisticProperties
	 *         match
	 */
	public boolean bCompare(GeneralLinguisticObject genObj, boolean bCompareCoreAttributes)
	{
		if (genObj == null)
			return false;

		if (bCompareCoreAttributes)
		{
			if (!this.lingType.equals(genObj.lingType))
				return false;
			if (!this.stOwner.equals(genObj.stOwner))
				return false;
		}

		return genObj.getLinguisticProperties().bCompare(this.getLinguisticProperties());
	}

	/**
	 * checkDataSourceCriteria checks a GLO against a dataSourceCriteria
	 * 
	 * @param dataSourceCriteria
	 *            the DataSourceCriteria
	 * @param comparisionOperator
	 *            the comparisionOperator to use
	 * @param bOr
	 *            use OR (= true) or AND (= false)
	 * @return true or false if match
	 */
	public boolean checkDataSourceCriteria(DataSourceCriteria dataSourceCriteria, String comparisionOperator)
	{
		return dataSourceCriteria.checkDataSourceCriteria(this, comparisionOperator);
	}

	/**
	 * checkDataSourceCriteria checks a GLO against a vector of
	 * dataSourceCriteria
	 * 
	 * @param dataSourceCriterias
	 *            a vector of DataSourceCriteria
	 * @param comparisionOperator
	 *            the comparisionOperator to use
	 * @return true if any matches
	 */
	public boolean checkDataSourceCriteria(Vector<DataSourceCriteria> dataSourceCriterias, String comparisionOperator)
	{
		for (int i = 0; i < dataSourceCriterias.size(); i++)
		{
			if (dataSourceCriterias.get(i).checkDataSourceCriteria(this, comparisionOperator))
				return true;
		}
		return false;
	}

	/**
	 * checkDataSourceCriteria checks a GLO against a vector of
	 * dataSourceCriteria
	 * 
	 * @param dataSourceCriterias
	 *            a vector of DataSourceCriteria
	 * @param comparisionOperator
	 *            the comparisionOperator to use
	 * @param bOr
	 *            use OR (= true) or AND (= false) for individual comparisions
	 * @return true if any matches
	 */
	public boolean checkDataSourceCriteria(Vector<DataSourceCriteria> dataSourceCriterias, String comparisionOperator, boolean bOr)
	{
		for (int i = 0; i < dataSourceCriterias.size(); i++)
		{
			boolean bRes = dataSourceCriterias.get(i).checkDataSourceCriteria(this, comparisionOperator);
			if (bRes && bOr) // the OR case; if one is true result is true
				return bRes;
			if ((bRes == false) && (bOr == false)) // the and case; if one is
				// false everything is wrong
				return false;
		}
		if (bOr)
			return false;

		return true;
	}

	/**
	 * clearObject sets all values to null or zero
	 */
	public void clearObject()
	{
		this.lastAccessTime = null;
		this.lingType = null;
		this.linguisticProperties = null;
		this.stOwner = null;
		this.stUniqueID = null;
		this.id = null;
		this.modificationTime = null;
		this.creationTime = null;
		this.usageNumber = null;
	}

	/**
	 * format
	 * 
	 * @return the formatted string
	 */
	public String format()
	{
		String str = "";

		str = str + "\tgetUniqueID -> " + this.getUniqueID() + "\n";
		str = str + "\tgetLastAccessTime -> " + this.getLastAccessTime() + "\n";
		str = str + "\tgetStOwner -> " + this.getStOwner() + "\n";
		str = str + "\tgetLingType -> " + this.getLingType() + "\n";
		str = str + "\tgetId -> " + this.getId() + "\n";
		if (this.getLinguisticProperties() != null)
			str += ((LinguisticProperties) this.getLinguisticProperties()).format();

		return str;
	}

	/**
	 * formatAsXml
	 * 
	 * @return
	 */
	public String formatAsXml()
	{
		String str = "";

		str = str + "<UniqueID>" + this.getUniqueID() + "</UniqueID>\n";
		str = str + "<LastAccessTime>" + this.getLastAccessTime() + "</LastAccessTime>\n";
		str = str + "<StOwner>" + this.getStOwner() + "</StOwner>\n";
		str = str + "<LingType>" + this.getLingType() + "</LingType>\n";
		str = str + "<Id>" + this.getId() + "</Id>\n";
		if (this.getLinguisticProperties() != null)
			str += ((LinguisticProperties) this.getLinguisticProperties()).format();

		return str;
	}

	public Long getCreationTime()
	{
		return creationTime;
	}

	/**
	 * @return the exception
	 */
	public Exception getException()
	{
		return exception;
	};

	/**
	 * @return the id
	 */
	public Integer getId()
	{
		return id;
	}

	/**
	 * @return the lastAccessTime
	 * @uml.property name="lastAccessTime"
	 */
	@Column(name = "lastAccessTime")
	public Long getLastAccessTime()
	{
		return lastAccessTime;
	}

	/**
	 * @return the lingType - an enumeration currently either TMX oder TERM
	 */
	@Column(name = "lingType")
	public LinguisticTypes getLingType()
	{
		return lingType;
	}

	/**
	 * @return the linguisticProperties
	 */
	@OneToMany
	public LinguisticProperties getLinguisticProperties()
	{
		return linguisticProperties;
	}

	public Long getModificationTime()
	{
		return modificationTime;
	}

	/**
	 * getObjectLinguisticPropertiy
	 * 
	 * @param key
	 * @return a LinguisticProperty for key
	 */
	public LinguisticProperty getObjectLinguisticProperty(Object key)
	{
		if (this.linguisticProperties == null)
			return null;
		LinguisticProperty linguisticProperty = (LinguisticProperty) this.linguisticProperties.get(key);
		return linguisticProperty;
	}

	/**
	 * @return the stOwner
	 */
	@Column(name = "stOwner")
	public String getStOwner()
	{
		return stOwner;
	}

	/**
	 * @return the stUniqueID
	 */
	public String getStUniqueID()
	{
		return stUniqueID;
	}

	/**
	 * @return the stUniqueID
	 * @uml.property name="iUniqueID"
	 */
	@Column(name = "stUniqueID")
	public String getUniqueID()
	{
		return stUniqueID;
	}

	public Long getUsageNumber()
	{
		return usageNumber;
	}

	/**
	 * mapToJson
	 * 
	 * @return
	 */
	public String mapToJson()
	{
		JSONSerializer serializer = new JSONSerializer();
		String result = serializer.deepSerialize(this);
		return result;
	}

	/**
	 * matchLinguisticProperties the method compares the LinguisticProperties of
	 * a GeneralLinguisticObject against a logical expression. It returns true
	 * if the expression matches, otherwise false.<br>
	 * The method loops thru all properties. Depending on the type of property
	 * the the property value is treated as string or as int or similar.<br>
	 * The comparison is done using the JavaScript ScriptEngine (see
	 * http://download
	 * .oracle.com/javase/6/docs/api/javax/script/ScriptEngineManager.html).<br>
	 * The names of the properties are represented as "%prop-name%"<br>
	 * The returning expression is constructed as: <br>
	 * evalcode = "res = " + comparisionString + "; res;";<br>
	 * If the evalcode should not be constructed in this way the logical
	 * expression start with: <b>&lt;JavaScript&gt;</b>.<br>
	 * In this case the comparison expression is responsible returning "true" or
	 * "false" as the last value; otherwise the method will always return false.<br>
	 * The following key - value pairs are supplied to the script:
	 * <table>
	 * <tr>
	 * <th>Key</th>
	 * <th>Value
	 * </tr>
	 * <tr>
	 * <td>prop-name[1..n]</td>
	 * <td>value of property[1..n]</td>
	 * </tr>
	 * <tr>
	 * <td>comparisionString</td>
	 * <td>comparisionString</td>
	 * </tr>
	 * <tr>
	 * <td>finalComparisionString</td>
	 * <td>final comparisionString</td>
	 * </tr>
	 * <tr>
	 * <td>evalCode</td>
	 * <td>evalcode</td>
	 * </tr>
	 * </table>
	 * <b>Examples of logical expressions:</b><br>
	 * MonoLingualObject mono1 = new MonoLingualObject("My small segment!",
	 * "en");<br>
	 * mono1.addStringLinguisticProperty("OpenTMS", "FoltNew");<br>
	 * mono1.addLinguisticProperty(new LinguisticProperty("Zahl", (Integer)
	 * 10));<br>
	 * System.out.println(mono1.matchLinguisticProperties(
	 * "%OpenTMS% == \"FoltNew\""));<br>
	 * System.out.println(mono1.matchLinguisticProperties("%Zahl% == 10"));<br>
	 * System.out.println(mono1.matchLinguisticProperties("%Zahl% == 12"));<br>
	 * System.out.println(mono1.matchLinguisticProperties("%Zahl% < 12"));<br>
	 * System.out.println("&lt;JavaScript&gt;b = true; b; " +
	 * mono1.matchLinguisticProperties("<JavaScript>b = true; b;"));<br>
	 * 
	 * @param comparisionString
	 *            the logical string against which the LinguisticProperties of
	 *            the GeneralLinguisticObject is compared
	 * @return true if comparisionString matches based on the
	 *         LinguisticProperties; otherwise false; false if an exception
	 *         occurs; the exception can be checked with .getException(); null
	 *         if successful
	 */
	@SuppressWarnings( { "unchecked" })
	public boolean matchLinguisticProperties(String comparisionString)
	{
		try
		{
			if (mgr == null)
			{
				mgr = new ScriptEngineManager();
				jsEngine = mgr.getEngineByName("JavaScript");
			}
			exception = null;
			LinguisticProperties lingProperties = this.getLinguisticProperties();

			Set<String> enumprop = lingProperties.keySet();
			Iterator<String> it = enumprop.iterator();

			jsEngine.put("comparisionString", comparisionString);

			while (it.hasNext())
			{
				String key = (String) it.next();
				Object value = (Object) lingProperties.get(key);
				if (value.getClass().getName().equals("java.util.Vector"))
				{
					for (int i = 0; i < ((Vector<LinguisticProperty>) value).size(); i++)
					{
						LinguisticProperty ling = ((Vector<LinguisticProperty>) value).get(i);
						String name = (String) ling.getKey();
						Object val = ling.getValue();
						String replace = val.toString();
						if (val.getClass() == String.class)
							replace = "\"" + replace + "\"";
						comparisionString = comparisionString.replaceAll("%" + name + "%", replace);
						jsEngine.put(name, val);
					}
				}
				else
				{
					LinguisticProperty ling = (LinguisticProperty) lingProperties.get(key);
					String name = (String) ling.getKey();
					Object val = ling.getValue();
					String replace = val.toString();
					if (val.getClass() == String.class)
						replace = "\"" + replace + "\"";
					comparisionString = comparisionString.replaceAll("%" + name + "%", replace);
					jsEngine.put(name, val);
				}
			}

			jsEngine.put("finalComparisionString", comparisionString);

			String evalcode = comparisionString;
			if (comparisionString.startsWith("<JavaScript>"))
				evalcode = evalcode.replaceFirst("<JavaScript>", "");
			else
				evalcode = "res = " + comparisionString + "; res;";
			jsEngine.put("evalCode", evalcode);
			boolean result = (Boolean) jsEngine.eval(evalcode);
			return result;
		}
		catch (Exception ex)
		{
			if (bLogExceptions)
			{
				System.out.println("Error: " + comparisionString);
				ex.printStackTrace();
			}
			exception = ex;
		}
		return false;

	}

	/**
	 * removeLinguisticProperty
	 * 
	 * @param linguisticProperty
	 */
	public void removeLinguisticProperty(LinguisticProperty linguisticProperty)
	{
		if (this.linguisticProperties == null)
			return;
		linguisticProperties.remove(linguisticProperty.getKey());
		this.notifyObservers(linguisticProperties);
	}

	public void setCreationTime(Long creationTime)
	{
		this.creationTime = creationTime;
	}

	/**
	 * @param exception
	 *            the exception to set
	 */
	public void setException(Exception exception)
	{
		this.exception = exception;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Integer id)
	{
		this.id = id;
	}

	/**
	 * @param lastAccessTime
	 *            the lastAccessTime to set
	 * @uml.property name="lastAccessTime"
	 */
	public void setLastAccessTime(long lastAccessTime)
	{
		this.lastAccessTime = lastAccessTime;
		this.setChanged();
		this.notifyObservers(lastAccessTime);
	}

	/**
	 * @param lingType
	 *            the lingType to set
	 */
	public void setLingType(LinguisticTypes lingType)
	{
		this.lingType = lingType;
		this.setChanged();
		this.notifyObservers(lingType);
	}

	/**
	 * @param linguisticProperties
	 *            the linguisticProperties to set
	 */
	public void setLinguisticProperties(LinguisticProperties linguisticProperties)
	{
		this.linguisticProperties = linguisticProperties;
		setChanged();
		this.notifyObservers(linguisticProperties);
	}

	public void setModificationTime(Long modificationTime)
	{
		this.modificationTime = modificationTime;
	}

	/**
	 * @param stOwner
	 *            the stOwner to set
	 */
	public void setStOwner(String stOwner)
	{
		this.stOwner = stOwner;
		this.setChanged();
		this.notifyObservers(stOwner);
	}

	/**
	 * @param stUniqueID
	 *            the stUniqueID to set
	 */
	public void setStUniqueID(String stUniqueID)
	{
		this.stUniqueID = stUniqueID;
	}

	/**
	 * @param uniqueID
	 *            the stUniqueID to set; this should be an unique id and no
	 *            conflict with other (subclasses) of this class
	 * @uml.property name="iUniqueID"
	 */
	public void setUniqueID(String uniqueID)
	{
		stUniqueID = uniqueID;
	}

	/**
	 * @param usageNumber the new usage number
	 */
	public void setUsageNumber(Long usageNumber)
	{
		this.usageNumber = usageNumber;
	}
	
	/**
	 * set the modification time to the current time stamp
	 */
	public void updateModificationTime()
	{
		this.modificationTime = System.currentTimeMillis();
	}
	
	/**
	 * increment the usage counter
	 */
	public void updateUsageNumber()
	{
		this.usageNumber++;
	}
}
