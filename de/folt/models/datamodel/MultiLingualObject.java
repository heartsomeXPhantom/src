/*
 * Created on 13.12.2008
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.models.datamodel;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Observer;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.persistence.OneToMany;

import org.json.JSONException;
import org.json.JSONObject;

import de.folt.util.ObservableHashtable;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import flexjson.locators.TypeLocator;

/**
 * This class implements a MultiLingualObject {@see <a
 * href="http://www.opentms.de/files/techspec.html">OpenTMS Software
 * Architecture</a>}. It extends a GeneralLinguisticObject.<br>
 * Its main component is:
 * 
 * <pre>
 * monoLingualObjects - this is an ObservableHashtable which reference the MonoLingualObjects in different languages. They are referenced by their getUniqueID().&lt;br&gt;
 * </pre>
 * 
 * Other components are inherited from GeneralLinguisticObject {@see
 * de.folt.models.datamodel.GeneralLinguisticObject}.
 * 
 * @author klemens
 */

public class MultiLingualObject extends GeneralLinguisticObject implements Serializable
{

	/**
     * 
     */
	private static final long serialVersionUID = 6931779697697027952L;

	public static MultiLingualObject fromJson(String jsonMulti)
	{

		MultiLingualObject multipat = new MultiLingualObject();
		MultiLingualObject multi = MultiLingualObject.fromJsonInternal(jsonMulti);
		multi.monoLingualObjects = multipat.monoLingualObjects;
		try
		{
			JSONObject jMulti = new JSONObject(jsonMulti);
			String jlinguisticPropertiesString = jMulti.getString("linguisticProperties");
			JSONObject jlinguisticProperties = new JSONObject(jlinguisticPropertiesString);
			@SuppressWarnings("rawtypes")
			java.util.Iterator it = jlinguisticProperties.keys();
			while (it.hasNext())
			{
				String key = (String) it.next();
				String value = jlinguisticProperties.getString(key);
				LinguisticProperty linguisticProperty = LinguisticProperty.fromJson(value);
				multi.addLinguisticProperty(linguisticProperty);
			}

			String jMonosString = jMulti.getString("monoLingualObjects");
			JSONObject jMonos = new JSONObject(jMonosString);
			it = jMonos.keys();
			while (it.hasNext())
			{
				String key = (String) it.next();
				String value = jMonos.getString(key);
				MonoLingualObject mono = MonoLingualObject.fromJson(value);
				multi.addMonoLingualObject(mono);
			}
		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		return multi;
	}

	/**
	 * fromJson convert into a GeneralLinguisticObject from JSON String
	 * 
	 * @param jsonMono
	 *            the json formatted string
	 * @return the GeneralLinguisticObject
	 */
	public static MultiLingualObject fromJsonInternal(String jsonMulti)
	{
		try
		{
			// @formatter:off

			return new JSONDeserializer<MultiLingualObject>()
			// now for linguisticProperties
					.use("linguisticProperties", new TypeLocator<String>("linguisticProperties")
					//
							.add("LinguisticProperty", LinguisticProperty.class))
					// now for monoLingualObjects
					.use("monoLingualObjects", new TypeLocator<String>("monoLingualObjects")
					//
							.add("MonoLingualObject", MonoLingualObject.class))
					// now for MultiLingualObject.monoLingualObjects
					.use("MultiLingualObject.monoLingualObjects", ObservableHashtable.class)
					// now for MultiLingualObject.monoLingualObjects
					.use("MultiLingualObject.monoLingualObjects.keys", String.class)
					// now for MultiLingualObject.monoLingualObjects
					.use("MultiLingualObject.monoLingualObjects.values", MonoLingualObject.class)
					// now for MultiLingualObject.linguisticProperties
					.use("MultiLingualObject.linguisticProperties", LinguisticProperties.class)
					// now for MultiLingualObject.linguisticProperties
					.use("MultiLingualObject.linguisticProperties.keys", String.class)
					// now for MultiLingualObject.linguisticProperties
					.use("MultiLingualObject.linguisticProperties.values", LinguisticProperty.class)
					// GeneralLinguisticObject.linguisticProperties
					.use("GeneralLinguisticObject.linguisticProperties", LinguisticProperties.class)
					// now for MultiLingualObject.linguisticProperties
					.use("GeneralLinguisticObject.linguisticProperties.keys", String.class)
					// now for MultiLingualObject.linguisticProperties
					.use("GeneralLinguisticObject.linguisticProperties.values", LinguisticProperty.class)
					// deserialize
					.deserialize(jsonMulti, MultiLingualObject.class);
			// @formatter:on
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
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
	 * test simple test method for generating some general linguistic objects
	 */
	public static void test()
	{
		try
		{
			@SuppressWarnings("rawtypes")
			Class[] classes = new Class[2];
			classes[0] = String.class;
			classes[1] = Object.class;
			Method method = MonoLingualObject.class.getMethod("simpleComputePlainText", classes);

			MultiLingualObject multi = new MultiLingualObject();
			multi.addStringLinguisticProperty("OpenTMS1", "Folt");
			multi.addStringLinguisticProperty(1 + "de", "Foltde" + 1);
			System.out.println(multi.format());
			String jsonMulti1 = multi.mapToJson();
			System.out.println(jsonMulti1);
			MultiLingualObject multijson1 = fromJson(jsonMulti1);
			if (multijson1 != null)
				System.out.println(multijson1.format());
			else
				System.out.println("Null MultiLingualObject\n");

			System.out.println("================================================================\n");

			int n = 1;
			MultiLingualObject[] multiArray = new MultiLingualObject[n];
			for (int i = 0; i < n; i++) // generate 20 MUL Objects
			{

				multiArray[i] = new MultiLingualObject();

				MonoLingualObject mono = new MonoLingualObject("Mein kleiner Satz! " + i, "de",
						MonoLingualObject.class, method, new Object());
				multiArray[i].addStringLinguisticProperty("OpenTMS1", "Folt");
				multiArray[i].addStringLinguisticProperty(i + "de", "Foltde" + i);
				mono.addObjectLinguisticProperty("mono1", "mono1");
				mono.addObjectLinguisticProperty("monox", "monox");
				multiArray[i].addMonoLingualObject(mono);

				mono = new MonoLingualObject("My small segment! " + i, "en", MonoLingualObject.class, method,
						new Object());
				multiArray[i].addStringLinguisticProperty("OpenTMS2", "Folt");
				multiArray[i].addStringLinguisticProperty(i + "en", "Folten" + i);
				mono.addObjectLinguisticProperty("mono2", "mono2");
				mono.addObjectLinguisticProperty("monox", "monox");
				multiArray[i].addMonoLingualObject(mono);

				mono = new MonoLingualObject("My small french segment! " + i, "fr", MonoLingualObject.class, method,
						new Object());
				multiArray[i].addStringLinguisticProperty("OpenTMS3", "Folt");
				multiArray[i].addStringLinguisticProperty(i + "fr", "Folten" + i);
				mono.addObjectLinguisticProperty("mono3", "mono3");
				mono.addObjectLinguisticProperty("monox", "monox");
				multiArray[i].addMonoLingualObject(mono);

				System.out.println("toString\n");
				System.out.println(multiArray[i].toString());
				System.out.println("format\n");
				System.out.println(multiArray[i].format());
				System.out.println("mapToTransUnit\n");
				System.out.println(multiArray[i].mapToTransUnit());
				System.out.println("mapToTu\n");
				System.out.println(multiArray[i].mapToTu());
				System.out.println("mapToTermEntry\n");
				System.out.println(multiArray[i].mapToTermEntry());
				System.out.println("mapToJson\n");
				String jsonMulti = multiArray[i].mapToJson();
				System.out.println(jsonMulti);
				MultiLingualObject multijson = fromJson(jsonMulti);
				System.out.println("Control check against Original\n");
				if (multijson != null)
					System.out.println(multijson.format());
				else
					System.out.println("Null MultiLingualObject\n");

				System.out.println(multiArray[i].bCompare(multiArray[i]));
				if (i > 0)
				{
					System.out.println(multiArray[i].bCompare(multiArray[i - 1]));
				}
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Map required for Hibernate - must be an interface and not a concrete
	 * class
	 */
	protected Map<String, MonoLingualObject> monoLingualObjects = new ObservableHashtable<String, MonoLingualObject>();

	/**
     * 
     */
	public MultiLingualObject()
	{
		super();
	}

	/**
	 * @param linguisticProperties
	 * @param lingType
	 */
	public MultiLingualObject(LinguisticProperties linguisticProperties, LinguisticTypes lingType)
	{
		super(linguisticProperties, lingType);
	}

	/**
	 * @param uniqueID
	 * @param linguisticProperties
	 * @param lingType
	 */
	public MultiLingualObject(String uniqueID, LinguisticProperties linguisticProperties, LinguisticTypes lingType)
	{
		super(uniqueID, linguisticProperties, lingType);
	}

	/**
	 * addMonoLingualObject adds a MonoLingualObject to the given
	 * MultiLingualObject
	 * 
	 * @param mono
	 *            the MonoLingualObject to add
	 * @return true = success / false could not be added
	 */
	public boolean addMonoLingualObject(MonoLingualObject mono)
	{
		boolean bAdded = false;
		if (monoLingualObjects == null)
			monoLingualObjects = new ObservableHashtable<String, MonoLingualObject>();
		monoLingualObjects.put(mono.getUniqueID(), mono);
		setChanged();
		this.notifyObservers(this);
		bAdded = true;
		mono.setParentMultiLingualObject(this);
		return bAdded;
	}

	/**
	 * addMonoLingualObjectIfNotExist adds a MonoLingualObject to the given
	 * MultiLingualObject if the formattedSegment and language combination does
	 * not exist in the MUL
	 * 
	 * @param mono
	 *            the MonoLingualObject to add
	 * @return true = success / false could not be added
	 */
	public boolean addMonoLingualObjectIfNotExist(MonoLingualObject mono)
	{
		boolean bAdded = false;
		if (bContainsMonoLingualObject(mono))
		{
			return false;
		}
		if (monoLingualObjects == null)
			monoLingualObjects = new ObservableHashtable<String, MonoLingualObject>();
		monoLingualObjects.put(mono.getUniqueID(), mono);
		setChanged();
		this.notifyObservers(this);
		bAdded = true;
		mono.setParentMultiLingualObject(this);
		return bAdded;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observable#addObserver(java.util.Observer)
	 */
	public void addObserver(Observer o)
	{
		((ObservableHashtable<String, MonoLingualObject>) monoLingualObjects).addObserver(o);
		super.addObserver(o);
	}

	/**
	 * bCompare compare this MUL against another MUL
	 * 
	 * @param mulObj
	 *            the MUL to compare against
	 * @return true if the MOLs and LinguisticProperties match (without core
	 *         GeneralLinguisticObject attributes)
	 */
	public boolean bCompare(MultiLingualObject mulObj)
	{
		return bCompare(mulObj, false);
	}

	/**
	 * bCompare compare this MUL against another MUL
	 * 
	 * @param mulObj
	 *            the MUL to compare against
	 * @param bCompareCoreAttributes
	 * @return true if the formattedSegment and language and
	 *         LinguisticProperties match (including the core
	 *         GeneralLinguisticObject attributes)
	 */
	public boolean bCompare(MultiLingualObject mulObj, boolean bCompareCoreAttributes)
	{
		if (mulObj == null)
			return false;

		Vector<MonoLingualObject> thismono = this.getMonoLingualObjectsAsVector();
		Vector<MonoLingualObject> mulObjmono = mulObj.getMonoLingualObjectsAsVector();
		for (int i = 0; i < thismono.size(); i++)
		{
			boolean bFound = false;
			for (int j = 0; j < mulObjmono.size(); j++)
			{
				if (mulObjmono.get(j).bCompare(thismono.get(i), bCompareCoreAttributes))
				{
					bFound = true;
					break;
				}
			}
			if (bFound == false)
				return false;
		}

		return super.bCompare(mulObj, bCompareCoreAttributes);
	}

	/**
	 * bContainsMonoLingualObject checks if for a MOL based on the formatted
	 * segment and the language the MOL exists in the MUL
	 * 
	 * @param mono
	 *            the MonoLingualObject to check
	 * @return true = MOL (formatted segment + language) exists / false
	 *         otherwise
	 */
	boolean bContainsMonoLingualObject(MonoLingualObject mono)
	{
		Vector<MonoLingualObject> monos = this.getMonoLingualObjectsAsVector(mono.getLanguage());
		if (monos.size() > 0)
		{
			for (int i = 0; i < monos.size(); i++)
			{
				if (monos.get(i).getFormattedSegment().equals(mono.getFormattedSegment()))
					return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observable#deleteObserver(java.util.Observer)
	 */
	@Override
	public synchronized void deleteObserver(Observer o)
	{
		((ObservableHashtable<String, MonoLingualObject>) monoLingualObjects).deleteObserver(o);
		super.deleteObserver(o);
	}

	/**
	 * format formats a MultilingualObject
	 * 
	 * @return formatted MultilingualObject
	 */
	public String format()
	{
		String str = "MultilingualObject:\n" + super.format();
		if (monoLingualObjects == null)
			return str;
		Enumeration<MonoLingualObject> en = ((ObservableHashtable<String, MonoLingualObject>) monoLingualObjects)
				.elements();
		while (en.hasMoreElements())
		{
			MonoLingualObject mono = en.nextElement();
			str = str + mono.format();
		}
		return str;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.GeneralLinguisticObject#formatAsXml()
	 */
	public String formatAsXml()
	{
		if (monoLingualObjects == null)
			return "";
		String str = "<MultilingualObject>\n" + super.format() + "<MonolingualObjects>\n";
		Enumeration<MonoLingualObject> en = ((ObservableHashtable<String, MonoLingualObject>) monoLingualObjects)
				.elements();
		while (en.hasMoreElements())
		{
			MonoLingualObject mono = en.nextElement();
			str = str + mono.format();
		}
		return "</MonolingualObjects>\n" + str + "</MultilingualObject>\n";
	}

	/**
	 * @return the monoLingualObjects
	 * @uml.property name="monoLingualObjects"
	 */
	@OneToMany(mappedBy = "parentMultiLingualObject")
	public Map<String, MonoLingualObject> getMonoLingualObjects()
	{
		return monoLingualObjects;
	}

	/**
	 * getMonoLingualObjectsAsVector returns the MonoLingualObjects of the
	 * MultiLingualObject as a vector
	 * 
	 * @return Vector of MonoLingualObjects
	 */
	public Vector<MonoLingualObject> getMonoLingualObjectsAsVector()
	{
		Vector<MonoLingualObject> retvec = new Vector<MonoLingualObject>();
		if (monoLingualObjects == null)
			return null;
		Enumeration<MonoLingualObject> enumm = ((ObservableHashtable<String, MonoLingualObject>) monoLingualObjects)
				.elements();
		while (enumm.hasMoreElements())
		{
			retvec.add(enumm.nextElement());
		}
		return retvec;
	}

	/**
	 * getMonoLingualObjectsAsVector Method takes a language code (independent
	 * of lower / upper case) and returns the MonoLingualObjects of a specific
	 * language of the MultiLingualObject as a vector
	 * 
	 * @param language
	 *            the language to retrieve the MonoLingualObjects for
	 * @return returns the MonoLingualObjects of a specific language of the
	 *         MultiLingualObject as a vector
	 */
	public Vector<MonoLingualObject> getMonoLingualObjectsAsVector(String language)
	{
		Vector<MonoLingualObject> retvec = new Vector<MonoLingualObject>();
		Enumeration<MonoLingualObject> enumm = ((ObservableHashtable<String, MonoLingualObject>) monoLingualObjects)
				.elements();
		while (enumm.hasMoreElements())
		{
			MonoLingualObject mono = enumm.nextElement();
			String monoLanguage = mono.getLanguage();
			if (monoLanguage.equalsIgnoreCase(language))
				retvec.add(mono);
		}
		return retvec;
	}

	// wk 12.11.2012
	/**
	 * getMonoLingualObjectsAsVector Method takes a language code (independent
	 * of lower / upper case) and returns the MonoLingualObjects of a specific
	 * language of the MultiLingualObject as a vector
	 * 
	 * @param language
	 *            the language to retrieve the MonoLingualObjects for<br />
	 *            language pattern used: de finds de-de de-DE; de-de only finds
	 *            de-de de-DE
	 * @param bFindAllSublanguages
	 *            if true applies language pattern as defined, if false only
	 *            exact matches are found (independent of lower / upper case)
	 * @return returns the MonoLingualObjects of a specific language of the
	 *         MultiLingualObject as a vector
	 */
	public Vector<MonoLingualObject> getMonoLingualObjectsAsVector(String language, boolean bFindAllSublanguages)
	{
		Vector<MonoLingualObject> retvec = new Vector<MonoLingualObject>();
		Enumeration<MonoLingualObject> enumm = ((ObservableHashtable<String, MonoLingualObject>) monoLingualObjects)
				.elements();

		boolean bshortLanguage = !language.contains("-");
		String myLanguage = language + Pattern.quote("-") + ".*";
		while (enumm.hasMoreElements())
		{
			MonoLingualObject mono = enumm.nextElement();
			String monoLanguage = mono.getLanguage();
			boolean bshortMonoLanguage = !monoLanguage.contains("-");
			if (monoLanguage.equalsIgnoreCase(language))
			{
				retvec.add(mono);
			}
			else if (bshortLanguage && bFindAllSublanguages)
			{
				if (monoLanguage.matches(myLanguage))
					retvec.add(mono);
			}
			else if (!bshortLanguage && bFindAllSublanguages && bshortMonoLanguage)
			{
				String myMonoLanguage = monoLanguage + Pattern.quote("-") + ".*";
				if (language.matches(myMonoLanguage))
					retvec.add(mono);
			}
		}
		return retvec;
	}

	// end wk 12.11.2012

	/**
	 * getMonoLingualObjectsAsVector Method takes an array of language codes
	 * (independent of lower / upper case) and returns the MonoLingualObjects
	 * for the specific languages of the MultiLingualObject as a vector
	 * 
	 * @param language
	 *            array the languages to retrieve the MonoLingualObjects for
	 * @return returns the MonoLingualObjects of a specific language of the
	 *         MultiLingualObject as a vector
	 */
	public Vector<MonoLingualObject> getMonoLingualObjectsAsVector(String[] language)
	{
		Vector<MonoLingualObject> retvec = new Vector<MonoLingualObject>();
		Enumeration<MonoLingualObject> enumm = ((ObservableHashtable<String, MonoLingualObject>) monoLingualObjects)
				.elements();
		while (enumm.hasMoreElements())
		{
			MonoLingualObject mono = enumm.nextElement();
			for (int i = 0; i < language.length; i++)
			{
				if (mono.getLanguage().equalsIgnoreCase(language[i]))
					retvec.add(mono);
			}
		}
		return retvec;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.folt.models.datamodel.GeneralLinguisticObject#mapToJson()
	 */
	@Override
	public String mapToJson()
	{
		JSONSerializer serializer = new JSONSerializer().exclude("monoLingualObjectsAsVector").exclude("exception");
		String result = serializer.deepSerialize(this);
		return result;
	}

	/**
	 * mapToTermEntry
	 * 
	 * @return
	 */
	public String mapToTermEntry()
	{
		@SuppressWarnings("unused")
		String coreAttributes = ((LinguisticProperties) this.getLinguisticProperties())
				.formatCoreAttributesToTermNote();
		String termEntry = "\t<termEntry id=\"" + this.getStUniqueID() + "\">\n";
		// termEntry = termEntry + ((LinguisticProperties)
		// this.getLinguisticProperties()).formatNotePropToTmx();
		for (int i = 0; i < this.getMonoLingualObjectsAsVector().size(); i++)
		{
			MonoLingualObject mono = this.getMonoLingualObjectsAsVector().get(i);
			termEntry = termEntry + mono.mapToTig();
		}

		termEntry = termEntry + "\t</termEntry>\n";
		return termEntry;
	}

	/**
	 * mapToTu converts the MultiLingualObject into a tu formatted string
	 * 
	 * @return the tu string
	 */
	public String mapToTransUnit()
	{
		String tu = "\t\t<trans-unit " + " id=\"" + this.getId() + "\" helpid=\"" + this.getStUniqueID() + "\">\n";
		for (int i = 0; i < this.getMonoLingualObjectsAsVector().size(); i++)
		{
			MonoLingualObject mono = this.getMonoLingualObjectsAsVector().get(i);
			if (i == 0)
			{
				tu = tu + "\t\t\t<source xml:lang=\"" + mono.getLanguage() + "\"><seg>" + mono.getFormattedSegment()
						+ "</seg></source>\n";
			}
			else
			{
				tu = tu + "\t\t\t<target xml:lang=\"" + mono.getLanguage() + "\"><seg>" + mono.getFormattedSegment()
						+ "</seg></target>\n";
			}
		}

		tu = tu + "\t\t</trans-unit>\n";
		return tu;
	}

	/**
	 * mapToTu converts the MultiLingualObject into a tu formatted string
	 * 
	 * @return the tu string
	 */
	public String mapToTu()
	{
		int mononumber = this.getMonoLingualObjectsAsVector().size();
		if (mononumber == 0)
			return "";
		String coreAttributes = ((LinguisticProperties) this.getLinguisticProperties()).formatCoreAttributesToTmx();
		String tu = "\t<tu " + coreAttributes + ">\n";
		// tu = tu + "\t\t\t<prop type=\"lastAccessTime\">" +
		// this.lastAccessTime + "</prop>\n";
		// tu = tu + "\t\t\t<prop type=\"internal-id\">" + this.id +
		// "</prop>\n";
		// tu = tu + "\t\t\t<prop type=\"unique-id\">" + this.getStUniqueID() +
		// "</prop>\n";

		tu = tu + ((LinguisticProperties) this.getLinguisticProperties()).formatNotePropToTmx();
		for (int i = 0; i < mononumber; i++)
		{
			MonoLingualObject mono = this.getMonoLingualObjectsAsVector().get(i);
			tu = tu + mono.mapToTuv();
		}

		tu = tu + "\t</tu>\n";
		return tu;
	}

	/**
	 * mapToTuWithoutTUV converts the MultiLingualObject into a tu formatted
	 * string without the MonoLingualObjects
	 * 
	 * @return the tu string
	 */
	public String mapToTuWithoutTUV()
	{
		int mononumber = this.getMonoLingualObjectsAsVector().size();
		if (mononumber == 0)
			return "";
		String coreAttributes = ((LinguisticProperties) this.getLinguisticProperties()).formatCoreAttributesToTmx();
		String tu = "\t<tu " + coreAttributes + ">\n";
		// tu = tu + "\t\t\t<prop type=\"lastAccessTime\">" +
		// this.lastAccessTime + "</prop>\n";
		// tu = tu + "\t\t\t<prop type=\"internal-id\">" + this.id +
		// "</prop>\n";
		// tu = tu + "\t\t\t<prop type=\"unique-id\">" + this.getStUniqueID() +
		// "</prop>\n";

		tu = tu + ((LinguisticProperties) this.getLinguisticProperties()).formatNotePropToTmx();

		tu = tu + "\t</tu>\n";
		return tu;
	}

	/**
	 * removeMonoLingualObject removes a MonoLingualObject to the given
	 * MultiLingualObject
	 * 
	 * @param mono
	 *            the MonoLingualObject to remove
	 * @return true = success / false could not be removed
	 */
	public boolean removeMonoLingualObject(MonoLingualObject mono)
	{
		boolean bRemoved = false;
		if (monoLingualObjects.containsKey(mono.getUniqueID()))
			monoLingualObjects.remove(mono.getUniqueID());
		else
			return false;
		setChanged();
		this.notifyObservers(this);
		bRemoved = true;
		return bRemoved;
	}

	/**
	 * removeMonoLingualObjects remove all MOLs from the MUL
	 */
	public void removeMonoLingualObjects()
	{
		Vector<MonoLingualObject> monos = this.getMonoLingualObjectsAsVector();
		if (monos.size() > 0)
		{
			for (int i = 0; i < monos.size(); i++)
			{
				monos.set(i, null);
			}
		}
		this.monoLingualObjects = null;
	}

	/**
	 * search searches for given MOL in the mul depending on language if
	 * searchParameters or searchMonoLingualObject contain a language; if no
	 * language is supplied all MOLs of th MUL are searched.
	 * 
	 * @param searchMonoLingualObject
	 *            the MOL (formattedSegment) to search for
	 * @param searchParameters
	 *            currently used: <br>
	 *            language = language (de, en etc.) <br>
	 *            fuzzy = true / false & similarity = integer as String / 100% =
	 *            exact match is used if not present <br>
	 *            regexp = the regular expression to use (".*" is used if not
	 *            present) <br>
	 *            wordbased = true/false (as String) <br>
	 *            priority of searchParameters in this order: fuzzy -> regexp ->
	 *            wordbased <br>
	 *            matchMonoLingualLinguisticProperties = a search criteria to be
	 *            applied to matchLinguisticProperties; Example:
	 *            "%OpenTMS% == \"FoltNew\"" or "<JavaScript>b = true; b;"
	 * @return searchParameters Vector of matching MOLs or null ion case of
	 *         error
	 */
	public Vector<MonoLingualObject> search(MonoLingualObject searchMonoLingualObject,
			Hashtable<String, Object> searchParameters)
	{
		if (searchMonoLingualObject == null)
			return null;

		if (searchParameters == null)
			searchParameters = new Hashtable<String, Object>();

		String criteriaCondition = null;

		if (searchParameters.contains("matchMonoLingualLinguisticProperties"))
		{
			criteriaCondition = (String) searchParameters.get("matchMonoLingualLinguisticProperties");
		}

		String language = (String) searchParameters.get("language");
		Vector<MonoLingualObject> retVec = new Vector<MonoLingualObject>();
		if (language == null)
		{
			language = searchMonoLingualObject.getLanguage();
		}
		Vector<MonoLingualObject> monos = null;
		boolean bFuzzy = false;
		boolean bWordBased = false;
		boolean bOrBased = true;
		String regularExpression = ".*";
		String[] wordmono = null;
		int minPercent = 100;
		if (searchParameters.containsKey("fuzzy") && searchParameters.get("fuzzy").equals("true"))
		{
			bFuzzy = true;
			try
			{
				String aMinPer = (String) searchParameters.get("similarity");
				minPercent = Integer.parseInt(aMinPer);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		if (searchParameters.containsKey("regexp"))
		{
			regularExpression = ".*";
			try
			{
				regularExpression = (String) searchParameters.get("regexp");
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

		}

		if (searchParameters.containsKey("wordbased") && searchParameters.get("wordbased").equals("true"))
		{
			bWordBased = false;
			try
			{
				String abRegExp = (String) searchParameters.get("wordbased");
				bWordBased = Boolean.parseBoolean(abRegExp);
				wordmono = new de.folt.util.WordHandling().segmentToWordArray(searchMonoLingualObject
						.getFormattedSegment());
				// sort and remove duplicates
				Arrays.sort(wordmono);
				List<String> list = Arrays.asList(wordmono);
				Set<String> set = new HashSet<String>(list);
				String[] result = new String[set.size()];
				set.toArray(result);
				wordmono = result;
				if (searchParameters.containsKey("orbased") && searchParameters.get("orbased").equals("true"))
				{
					bOrBased = false;
					try
					{
						String orRegExp = (String) searchParameters.get("orbased");
						bOrBased = Boolean.parseBoolean(orRegExp);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
				else
					bOrBased = false;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		if ((language == null) || language.equals(""))
		{
			monos = this.getMonoLingualObjectsAsVector();
		}
		else
		{
			monos = this.getMonoLingualObjectsAsVector(language);
		}

		for (int i = 0; i < monos.size(); i++)
		{
			if (criteriaCondition != null)
			{
				if (this.matchLinguisticProperties(criteriaCondition) == false)
					continue;
			}

			if (bFuzzy)
			{
				int sim = de.folt.similarity.LevenshteinSimilarity.levenshteinSimilarity(monos.get(i)
						.getFormattedSegment(), searchMonoLingualObject.getFormattedSegment(), minPercent);
				if (sim >= minPercent)
					retVec.add(monos.get(i));
			}
			else if (searchParameters.containsKey("regexp") && regularExpression != null)
			{
				try
				{
					if (monos.get(i).getPlainTextSegment().matches(regularExpression))
						retVec.add(monos.get(i));
				}
				catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else if (bWordBased)
			{
				if (wordmono != null)
				{
					// TODO very simple for the moment - must be improved later!
					// Just for the moment...
					String[] arraymono = new de.folt.util.WordHandling().segmentToWordArray(monos.get(i)
							.getPlainTextSegment());
					Arrays.sort(arraymono);

					if (bOrBased)
					{
						for (int j = 0; j < arraymono.length; j++)
						{
							int index = Arrays.binarySearch(wordmono, arraymono[j]);
							if (index >= 0)
							{
								retVec.add(monos.get(i));
								break;
							}
						}
					}
					else
					{
						boolean bFound = true;
						for (int j = 0; j < wordmono.length; j++)
						{
							int index = Arrays.binarySearch(arraymono, wordmono[j]);
							if (index < 0)
							{
								bFound = false;
								break;
							}
						}
						if (bFound)
							retVec.add(monos.get(i));
					}
				}
			}
			else if (monos.get(i).getFormattedSegment().equals(searchMonoLingualObject.getFormattedSegment()))
			{
				retVec.add(monos.get(i));
			}
		}
		return retVec;
	}

	/**
	 * @param monoLingualObjects
	 *            the monoLingualObjects to set
	 */
	public void setMonoLingualObjects(Map<String, MonoLingualObject> monoLingualObjects)
	{
		this.monoLingualObjects = monoLingualObjects;
	}

	/**
	 * setMonoLingualObjectsAsVector sets the MOls of this MUL to the MOLs of
	 * the given vector
	 * 
	 * @param monovector
	 *            a vetor of MOLs
	 */
	public void setMonoLingualObjectsAsVector(Vector<MonoLingualObject> monovector)
	{
		monoLingualObjects = null;
		monoLingualObjects = new ObservableHashtable<String, MonoLingualObject>();
		for (int i = 0; i < monovector.size(); i++)
		{
			addMonoLingualObject(monovector.get(i));
		}
		return;
	}

	public void x(String jsonMulti)
	{

		String mols = "";
		int start = jsonMulti.indexOf("\"monoLingualObjects\":");
		if (start != -1)
		{
			int startpar = jsonMulti.indexOf("{", start);
			int iCount = 0;
			int end = 0;
			int start1 = start;
			while (true)
			{
				mols = mols + jsonMulti.charAt(start);
				if (jsonMulti.charAt(start) == '{')
				{
					iCount++;
				}
				if (jsonMulti.charAt(start) == '}')
				{
					iCount--;
				}
				if ((iCount == 0) && (start > startpar))
				{
					break;
				}
				start++;
			}

			end = start;
			jsonMulti = jsonMulti.substring(0, start1) + jsonMulti.substring(end);
			System.out.println("new\n" + jsonMulti);
		}
	}
}
