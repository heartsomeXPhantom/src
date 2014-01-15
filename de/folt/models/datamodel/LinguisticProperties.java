/*
 * Created on 14.12.2008
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.models.datamodel;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.persistence.Entity;

import de.folt.models.documentmodel.tmx.TmxProp;
import de.folt.util.ObservableHashMap;
import flexjson.JSONSerializer;

/**
 * @author klemens
 * 
 */
@Entity
public class LinguisticProperties extends ObservableHashMap<String, Object> implements Serializable
{
	/**
     * 
     */
	private static final long serialVersionUID = -7106066019100606946L;

	/**
	 * add add a LingusticProperty to the LinguisticProperties
	 * 
	 * @param lingProp
	 */
	@SuppressWarnings("unchecked")
	public void add(LinguisticProperty lingProp)
	{
		Object key = lingProp.getKey();

		if (this.containsKey(key))
		{
			Object value = this.get(key);
			if (value.getClass().getName().equals("java.util.Vector"))
			{
				Vector<Object> vector = (Vector<Object>) value;

				for (int i = 0; i < vector.size(); i++)
				{
					LinguisticProperty prop = (LinguisticProperty) vector.get(i);
					if (lingProp.getValue() == prop.getValue())
					{
						return;
					}
				}
				vector.add(lingProp);
			}
			else
			{
				Vector<Object> vector = new Vector<Object>();
				vector.add(value);
				vector.add(lingProp);
				this.put((String) key, (Object) vector);
			}
			return;
		}

		this.put((String) key, lingProp);
	}

	/**
	 * 
	 * Search for an attribute (name) and return value of the attribute
	 * 
	 * @param attribute
	 * @return the value of the attribute or null if not found
	 */
	public String search(String attribute)
	{
		String value = null;

		Set<String> keyset = this.keySet();
		Iterator<String> it = keyset.iterator();
		while (it.hasNext())
		{
			Object valueob = this.get(it.next());
			if ((valueob != null) && (valueob.getClass() == LinguisticProperty.class))
			{
				LinguisticProperty lingprop = (LinguisticProperty) valueob;
				Object lipropobject = lingprop.value;
				if (lipropobject != null)
				{
					if ((lipropobject.getClass() == TmxProp.class))
					{
						TmxProp tmxProp = (TmxProp) lipropobject;
						if (tmxProp.getType().equals(attribute))
						{
							return (String) tmxProp.getContent();
						}
					}
					else if (lingprop.key.equals(attribute) && lipropobject.getClass() == String.class)
					{
						return lipropobject.toString();
					}
				}
			}
		}

		return value;
	}

	/**
	 * bCompare compare against all the LinguisticProperty of
	 * LinguisticProperties based on bCompare(LinguisticProperty compLingProp);
	 * the keys and the values are "stringified"
	 * 
	 * @param compLingProp
	 *            the LinguisticProperties to compare against
	 * @return true if all the property matches, otherwise false
	 */
	@SuppressWarnings("unchecked")
	public boolean bCompare(LinguisticProperties compLingProps)
	{
		if (compLingProps == null)
			return false;

		Set<String> enumprop = this.keySet();
		Iterator<String> it = enumprop.iterator();
		while (it.hasNext())
		{
			String key = it.next();
			Object value = this.get(key);
			if (value.getClass().getName().equals("java.util.Vector"))
			{
				for (int i = 0; i < ((Vector<LinguisticProperty>) value).size(); i++)
				{
					LinguisticProperty ling = ((Vector<LinguisticProperty>) value).get(i);
					boolean bFound = ling.bCompare(compLingProps);
					if (bFound == false)
						return false;
				}
			}
			else
			{
				LinguisticProperty ling = (LinguisticProperty) this.get(key);
				boolean bFound = ling.bCompare(compLingProps);
				if (bFound == false)
					return false;
			}
		}

		return true;
	}

	/**
	 * format
	 * 
	 * @return the formatted LinguisticProperties
	 */
	@SuppressWarnings("unchecked")
	public synchronized String format()
	{
		Set<String> enumprop = this.keySet();
		String output = "\t\t\tLinguisticProperties\n";
		Iterator<String> it = enumprop.iterator();
		while (it.hasNext())
		{
			String key = it.next();
			Object value = this.get(key);
			if (value.getClass().getName().equals("java.util.Vector"))
			{
				output += "\t\t\t\t" + key + " -> (";
				for (int i = 0; i < ((Vector<LinguisticProperty>) value).size(); i++)
				{
					LinguisticProperty ling = ((Vector<LinguisticProperty>) value).get(i);
					Object lingValue = ling.getValue();
					String classname = lingValue.getClass().getName();
					if (classname.equals("de.folt.models.documentmodel.tmx.TmxProp"))
					{
						TmxProp tmxProp = (TmxProp) lingValue;
						output += "\t\t\t\t" + key + " -> " + tmxProp.format() + "\n";
					}
					else
						output += "\t\t\t\t" + key + " -> " + ling.getValue() + "\n";
				}
			}
			else
			{
				LinguisticProperty ling = (LinguisticProperty) this.get(key);
				Object lingValue = ling.getValue();
				String classname = lingValue.getClass().getName();
				if (classname.equals("de.folt.models.documentmodel.tmx.TmxProp"))
				{
					TmxProp tmxProp = (TmxProp) lingValue;
					output += "\t\t\t\t" + key + " -> " + tmxProp.format() + "\n";
				}
				else
					output += "\t\t\t\t" + key + " -> " + ling.getValue() + "\n";
			}
		}
		return output;
	}

	/**
	 * formatAsXml
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String formatAsXml()
	{
		Set<String> enumprop = this.keySet();
		String output = "\t\t\tLinguisticProperties\n";
		Iterator<String> it = enumprop.iterator();
		while (it.hasNext())
		{
			String key = it.next();
			Object value = this.get(key);
			if (value.getClass().getName().equals("java.util.Vector"))
			{
				output += "<keys name=\"" + key + "\">";
				for (int i = 0; i < ((Vector<LinguisticProperty>) value).size(); i++)
				{
					LinguisticProperty ling = ((Vector<LinguisticProperty>) value).get(i);
					Object lingValue = ling.getValue();
					String classname = lingValue.getClass().getName();
					if (classname.equals("de.folt.models.documentmodel.tmx.TmxProp"))
					{
						TmxProp tmxProp = (TmxProp) lingValue;
						output += "<key name=\"" + key + "\">" + tmxProp.format() + "<key>\n";
					}
					else
						output += "<key name=\"" + key + "\">" + ling.getValue() + "</key>\n";
				}
				output += "</keys>";
			}
			else
			{
				LinguisticProperty ling = (LinguisticProperty) this.get(key);
				Object lingValue = ling.getValue();
				String classname = lingValue.getClass().getName();
				if (classname.equals("de.folt.models.documentmodel.tmx.TmxProp"))
				{
					TmxProp tmxProp = (TmxProp) lingValue;
					output += "<key name=\"" + key + "\">" + tmxProp.format() + "</key>\n";
				}
				else
					output += "<key name=\"" + key + "\">" + ling.getValue() + "</key>\n";
			}
		}
		return output;
	}

	/**
	 * formatCoreAttributesToTermNote
	 * 
	 * @return
	 */
	public String formatCoreAttributesToTermNote()
	{
		return "";
	}

	/**
	 * formatCoreAttributesToTmx formats the attributes of the tuv element
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String formatCoreAttributesToTmx()
	{
		Set<String> enumprop = this.keySet();
		String output = "";
		Iterator<String> it = enumprop.iterator();
		while (it.hasNext())
		{
			String key = it.next();
			Object value = this.get(key);
			if (value.getClass().getName().equals("java.util.Vector"))
			{
				output += "\t\t\t\t" + key + " -> (";
				for (int i = 0; i < ((Vector<LinguisticProperty>) value).size(); i++)
				{
					LinguisticProperty ling = ((Vector<LinguisticProperty>) value).get(i);
					Object lingValue = ling.getValue();
					String classname = lingValue.getClass().getName();
					if (classname.equals("de.folt.models.documentmodel.tmx.TmxProp"))
					{
						TmxProp tmxProp = (TmxProp) lingValue;
						String coreAtt = toCoreProp(tmxProp);
						if ((coreAtt != null) && !coreAtt.equals(""))
							output += " " + toCoreProp(tmxProp);
					}
				}
			}
			else
			{
				LinguisticProperty ling = (LinguisticProperty) this.get(key);
				Object lingValue = ling.getValue();
				String classname = lingValue.getClass().getName();
				if (classname.equals("de.folt.models.documentmodel.tmx.TmxProp"))
				{
					TmxProp tmxProp = (TmxProp) lingValue;
					String coreAtt = toCoreProp(tmxProp);
					if ((coreAtt != null) && !coreAtt.equals(""))
						output += " " + toCoreProp(tmxProp);
				}
			}
		}
		return output;
	}

	/**
	 * formatToTmx returns a Prop/NoteFormatted string
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String formatNotePropToTmx()
	{
		Set<String> enumprop = this.keySet();
		String output = "";
		Iterator<String> it = enumprop.iterator();
		while (it.hasNext())
		{
			String key = it.next();
			Object value = this.get(key);
			if (value.getClass().getName().equals("java.util.Vector"))
			{
				output += "\t\t\t\t" + key + " -> (";
				for (int i = 0; i < ((Vector<LinguisticProperty>) value).size(); i++)
				{
					LinguisticProperty ling = ((Vector<LinguisticProperty>) value).get(i);
					Object lingValue = ling.getValue();
					String classname = lingValue.getClass().getName();
					if (classname.equals("de.folt.models.documentmodel.tmx.TmxProp"))
					{
						TmxProp tmxProp = (TmxProp) lingValue;
						output += toOrigProp(tmxProp);
					}
				}
			}
			else
			{
				LinguisticProperty ling = (LinguisticProperty) this.get(key);
				Object lingValue = ling.getValue();
				String classname = lingValue.getClass().getName();
				if (classname.equals("de.folt.models.documentmodel.tmx.TmxProp"))
				{
					TmxProp tmxProp = (TmxProp) lingValue;
					output += toOrigProp(tmxProp);
				}
				else
				{
					String origprop = "\t\t\t<prop type=\"" + key.toString() + "\">" + lingValue.toString()
							+ "</prop>\n";
					;
					output += origprop;
				}
			}
		}
		return output;
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
	 * remove remove a LinguisticProperty
	 * 
	 * @param lingProp
	 */
	@SuppressWarnings("unchecked")
	public void remove(LinguisticProperty lingProp)
	{
		Object key = lingProp.getKey();
		if (this.containsKey(key))
		{
			Object value = this.get(key);
			if (value.getClass().getName().equals("java.util.Vector"))
			{
				Vector<Object> vector = (Vector<Object>) value;
				for (int i = 0; i < vector.size(); i++)
				{
					LinguisticProperty prop = (LinguisticProperty) vector.get(i);
					if (lingProp.getValue() == prop.getValue())
					{
						vector.remove(i);
						return;
					}
				}
			}
			else
			{
				this.remove((String) key);
			}
			return;
		}
	}

	@SuppressWarnings("unchecked")
	public synchronized String simpleFormat()
	{
		Set<String> enumprop = this.keySet();
		Iterator<String> it = enumprop.iterator();
		String output = "";
		while (it.hasNext())
		{
			String key = it.next();
			Object value = this.get(key);
			if (value.getClass().getName().equals("java.util.Vector"))
			{
				output += key + " -> (";
				for (int i = 0; i < ((Vector<LinguisticProperty>) value).size(); i++)
				{
					LinguisticProperty ling = ((Vector<LinguisticProperty>) value).get(i);
					Object lingValue = ling.getValue();
					String classname = lingValue.getClass().getName();
					if (classname.equals("de.folt.models.documentmodel.tmx.TmxProp"))
					{
						TmxProp tmxProp = (TmxProp) lingValue;
						output += key + " -> " + tmxProp.simpleFormat() + "\n";
					}
					else
					{
						if (!key.startsWith("tu"))
							output += key + " -> " + ling.getValue() + "\n";
					}
				}
				output += key + " )";
			}
			else
			{
				LinguisticProperty ling = (LinguisticProperty) this.get(key);
				Object lingValue = ling.getValue();
				String classname = lingValue.getClass().getName();
				if (classname.equals("de.folt.models.documentmodel.tmx.TmxProp"))
				{
					TmxProp tmxProp = (TmxProp) lingValue;
					output += key + " -> " + tmxProp.simpleFormat() + "\n";
				}
				else
				{
					if (!key.startsWith("tu"))
						output += key + " -> " + ling.getValue() + "\n";
				}
			}
		}

		return output;
	}

	private String toCoreProp(TmxProp tmxProp)
	{
		String origprop = "";
		TmxProp.PropType proptype = tmxProp.getPropType();
		if (proptype.equals(TmxProp.PropType.CORE))
		{
			return tmxProp.getType() + "=\"" + tmxProp.getContent() + "\"";
		}

		return origprop;
	}

	private String toOrigProp(TmxProp tmxProp)
	{
		String origprop = "";
		TmxProp.PropType proptype = tmxProp.getPropType();
		if (proptype.equals(TmxProp.PropType.NOTE))
		{
			origprop = "\t\t\t<note";
			if (!tmxProp.getLang().equals(""))
			{
				origprop = origprop + " xml:lang=\"" + tmxProp.getLang() + "\"";
			}
			if (!tmxProp.getO_encoding().equals(""))
			{
				origprop = origprop + " o-encoding=\"" + tmxProp.getO_encoding() + "\"";
			}
			origprop = origprop + ">" + tmxProp.getContent() + "</note>\n";
			return origprop;
		}

		if (proptype.equals(TmxProp.PropType.PROP))
		{
			origprop = "\t\t\t<prop";
			if (tmxProp.getType() != null)
				if (!tmxProp.getType().equals(""))
				{
					origprop = origprop + " type=\"" + tmxProp.getType() + "\"";
				}
			if (tmxProp.getLang() != null)
				if (!tmxProp.getLang().equals(""))
				{
					origprop = origprop + " xml:lang=\"" + tmxProp.getLang() + "\"";
				}
			if (tmxProp.getO_encoding() != null)
				if (!tmxProp.getO_encoding().equals(""))
				{
					origprop = origprop + " o-encoding=\"" + tmxProp.getO_encoding() + "\"";
				}
			origprop = origprop + ">" + tmxProp.getContent() + "</prop>\n";
			return origprop;

		}

		return origprop;
	}
}
