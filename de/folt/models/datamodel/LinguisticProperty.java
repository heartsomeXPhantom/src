/*
 * Created on 14.12.2008
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.models.datamodel;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Observable;
import java.util.Set;
import java.util.Vector;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.json.JSONException;
import org.json.JSONObject;

import flexjson.JSONSerializer;

/**
 * A LinguisticProperty is an object which is aimed for storing (attribute/value) information for a MOL or MUL.<br>
 * It is built up from a <b>key</b> which identifies the property<br>
 * a <b>value</b>, e.g. a TmxProp or similar object<br>
 * a <b>status</b> which identifies the object<br>
 * a <b>status</b> which allows to attach some additional data with the object. The rationality behind the data (in contrast to the value) is that this variable can be used to asscoiate the original
 * object with the property (e.g. an XML Element, if the TmxProp is derived from a Tmx TU or TUV property.<br>
 * 
 * @author klemens
 */
@Entity
public class LinguisticProperty extends Observable implements Serializable
{

	public enum PropStatus
	{
		OLD, CHANGED, DELETED, NEW
	}

	public enum PropType
	{
		CORE, NOTE, PROP, CRITERIA
	}

	/**
     * 
     */
	private static final long serialVersionUID = 3616033138003284706L;

	/**
	 * fromJson
	 * 
	 * @param value2
	 * @return
	 */
	public static LinguisticProperty fromJson(String linguisticProperty)
	{
		// "data":null,"key":"1de","propStatus":"NEW","propType":"PROP","value":"Foltde1"
		try
		{
			JSONObject jlinguisticProperty = new JSONObject(linguisticProperty);
			String data = jlinguisticProperty.getString("data");
			String key = jlinguisticProperty.getString("key");
			String propStatus = jlinguisticProperty.getString("data");
			String propType = jlinguisticProperty.getString("propType");
			String value = jlinguisticProperty.getString("value");
			LinguisticProperty rLinguisticProperty = new LinguisticProperty();
			if ((data != null) && !data.equals("null"))
				rLinguisticProperty.setData(data);
			if ((key != null) && !key.equals("null"))
				rLinguisticProperty.setKey(key);

			PropType pt = PropType.CORE;
			if (propType != null)
			{
				if (propType.equals("CORE"))
					pt = PropType.CORE;
				else if (propType.equals("NOTE"))
					pt = PropType.NOTE;
				else if (propType.equals("CRITERIA"))
					pt = PropType.CRITERIA;
				else if (propType.equals("PROP"))
					pt = PropType.PROP;
			}
			rLinguisticProperty.setPropType(pt);

			PropStatus pst = PropStatus.NEW;
			if (propStatus != null)
			{
				if (propStatus.equals("NEW"))
					pst = PropStatus.NEW;
				else if (propStatus.equals("CHANGED"))
					pst = PropStatus.CHANGED;
				else if (propStatus.equals("DELETED"))
					pst = PropStatus.DELETED;
				else if (propStatus.equals("OLD"))
					pst = PropStatus.OLD;
			}
			rLinguisticProperty.setPropStatus(pst);
			if ((value != null) && !value.equals("null"))
				rLinguisticProperty.setKey(key);
			rLinguisticProperty.setValue(value);
			return rLinguisticProperty;
		}
		catch (JSONException e)
		{
			e.printStackTrace();
			return null;
		}

		// return new
		// JSONDeserializer<LinguisticProperty>().deserialize(linguisticProperty,
		// LinguisticProperty.class);
	}

	/**
     */
	protected Object data;

	/**
     */
	protected Object key;

	private PropStatus propStatus = PropStatus.NEW;

	/**
     */
	protected Object value;

	protected PropType propType = PropType.PROP;

	/**
	 * 
	 */
	public LinguisticProperty()
	{
		super();
	}

	/**
	 * @param value
	 * @param key
	 */

	public LinguisticProperty(Object key, Object value)
	{
		super();
		this.value = value;
		this.key = key;
	}

	/**
	 * @param key
	 *            the key for the Property; should be unique
	 * @param propStatus
	 *            the status of the property
	 * @param value
	 *            the associated value (e.g. a TmxProp)
	 */
	public LinguisticProperty(Object key, PropStatus propStatus, Object value)
	{
		super();
		this.key = key;
		this.propStatus = propStatus;
		this.value = value;
	}

	/**
	 * bCompare check if this LinguisticProperty exists in compLingProps; if yes, return true, otherwise false
	 * 
	 * @param compLingProps
	 *            a set of LinguisticProperty (LinguisticProperties)
	 * @return true if contained otherwise false
	 */
	@SuppressWarnings("unchecked")
	public boolean bCompare(LinguisticProperties compLingProps)
	{
		if (compLingProps == null)
			return false;

		Set<String> enumprop = compLingProps.keySet();
		Iterator<String> it = enumprop.iterator();
		while (it.hasNext())
		{
			String key = (String) it.next();
			Object value = (Object) compLingProps.get(key);
			if (value.getClass().getName().equals("java.util.Vector"))
			{
				for (int i = 0; i < ((Vector<LinguisticProperty>) value).size(); i++)
				{
					LinguisticProperty ling = ((Vector<LinguisticProperty>) value).get(i);
					if (this.bCompare(ling))
						return true;
				}
			}
			else
			{
				LinguisticProperty ling = (LinguisticProperty) compLingProps.get(key);
				if (this.bCompare(ling))
					return true;
			}
		}
		return false;
	}

	/**
	 * bCompare compare against a LinguisticProperty; the key and the value are "stringified"
	 * 
	 * @param compLingProp
	 *            the LinguisticProperty to compare against
	 * @return true if the property matches, otherwise false
	 */
	public boolean bCompare(LinguisticProperty compLingProp)
	{
		if (compLingProp == null)
			return false;
		if (this.getKey().toString().equals(compLingProp.getKey().toString()))
		{
			if (this.getValue().toString().equals(compLingProp.getValue().toString()))
				return true;
		}
		return false;
	}

	/**
	 * format
	 * 
	 * @return a formatted version of the linguistic property
	 */
	public String format()
	{
		String str = this.getClass().getName() + " {key: " + this.getKey() + " value: " + this.getValue() + " propStatus: " + this.propStatus
				+ " propType: " + this.propType + "}";
		return str;
	}

	/**
	 * @return the data
	 */
	public Object getData()
	{
		return data;
	}

	/**
	 * @return the key
	 * @uml.property name="key"
	 */
	@Column(name = "getKey")
	public Object getKey()
	{
		return key;
	}

	/**
	 * @return the propStatus
	 */
	public PropStatus getPropStatus()
	{
		return propStatus;
	}

	/**
	 * @return the propType
	 */
	public PropType getPropType()
	{
		return propType;
	}

	/**
	 * @return the value
	 * @uml.property name="value"
	 */
	@Column(name = "getValue")
	public Object getValue()
	{
		return value;
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
	 * @param data
	 *            the data to set
	 */
	public void setData(Object data)
	{
		this.data = data;
	}

	/**
	 * @param key
	 *            the key to set
	 * @uml.property name="key"
	 */
	public void setKey(Object key)
	{
		this.key = key;
		this.setChanged();
		this.notifyObservers(key);
	}

	/**
	 * @param propStatus
	 *            the propStatus to set
	 */
	public void setPropStatus(PropStatus propStatus)
	{
		this.propStatus = propStatus;
	}

	/**
	 * @param propType
	 *            the propType to set
	 */
	public void setPropType(PropType propType)
	{
		this.propType = propType;
	}

	/**
	 * @param value
	 *            the value to set
	 * @uml.property name="value"
	 */
	public void setValue(Object value)
	{
		this.value = value;
		this.setChanged();
		this.notifyObservers(value);
	}
}
