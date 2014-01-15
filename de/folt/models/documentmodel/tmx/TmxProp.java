/*
 * Created on 28.01.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.models.documentmodel.tmx;

import java.io.Serializable;
import java.util.Observable;

import org.jdom.Element;
import org.jdom.Namespace;

import de.folt.models.datamodel.LinguisticProperty;

/**
 * This class implements the linguîstic properties reflecting the prop (from
 * prop-grop) or note from tmx. e.g. <prop xml:lang="de"
 * type="Att::Maschinentyp">Radblocksysteme</prop>
 * 
 * <pre>
 * content = the value of the property (&quot;Radblocksysteme&quot;)
 * lang = language of the property (&quot;de&quot;)
 * o_encoding = encoding of the property
 * propType = type of the property (PROP or NOTE)
 * type = the type of the property (&quot;Att::Maschinentyp&quot;)
 * &lt;/prep&gt;
 * @author klemens
 * 
 */

public class TmxProp extends Observable implements Serializable
{
	public enum PropType
	{
		CORE, NOTE, PROP
	}

	private static Integer idValue = 1;

	/**
     * 
     */
	private static final long serialVersionUID = -120786767645879729L;

	/**
	 * mapTo maps a LingusticProperty to a TmxProp
	 * 
	 * @param lingProp
	 *            the LingusticProperty to map
	 * @return the mapped TmxProp
	 */
	public static TmxProp mapTo(LinguisticProperty lingProp)
	{
		Object lingValue = lingProp.getValue();
		String classname = lingValue.getClass().getName();
		if (classname.equals("de.folt.models.documentmodel.tmx.TmxProp"))
		{
			return (TmxProp) lingProp.getValue();
		}
		return null;
	}

	private String content; // content of prop

	private Integer id;

	private String lang; // prop -> lang or xml:lang

	private String o_encoding; // prop -> o_encoding

	private PropType propType = PropType.PROP;

	private String type; // prop -> type;

	/**
	 * A simple constructor / settings see below
	 * 
	 * <pre>
	 * this.id = null;
	 * this.content = &quot;&quot;;
	 * this.propType = TmxProp.PropType.PROP;
	 * this.o_encoding = &quot;&quot;;
	 * this.lang = &quot;&quot;;
	 * this.type = &quot;&quot;;
	 * </pre>
	 */
	public TmxProp()
	{
		this.id = null;
		this.content = "";
		this.propType = TmxProp.PropType.PROP;
		this.o_encoding = "";
		this.lang = "";
		this.type = "";
	}

	/**
	 * Creates a TmxProp from a given Tmx prop element.
	 * 
	 * @param prop
	 *            the property of a TMX element (either a prop or a note)
	 */
	public TmxProp(Element prop)
	{
		super();

		if (prop.getName().equals("note"))
		{
			this.type = prop.getAttributeValue("note");
			propType = PropType.NOTE;
		}
		else if (prop.getName().equals("prop"))
		{
			this.type = prop.getAttributeValue("type");
			propType = PropType.PROP;
		}
		else
		{
			this.type = prop.getAttributeValue("core");
			propType = PropType.CORE;
		}

		this.lang = prop.getAttributeValue("lang", Namespace.XML_NAMESPACE);
		if (this.lang == null)
			prop.getAttributeValue("lang");
		this.o_encoding = prop.getAttributeValue("o-encoding");
		if (this.o_encoding == null)
			this.o_encoding = "";
		this.content = prop.getText();
		if (this.content == null)
			this.content = "";

		if (this.type == null)
			this.type = "";
	}

	/**
	 * @param prop
	 * @param id
	 */
	public TmxProp(Element prop, Integer id)
	{
		super();

		if (prop.getName().equals("note"))
		{
			this.type = prop.getAttributeValue("note");
			propType = PropType.NOTE;
		}
		else if (prop.getName().equals("prop"))
		{
			this.type = prop.getAttributeValue("type");
			propType = PropType.PROP;
		}
		else
		{
			this.type = prop.getAttributeValue("core");
			propType = PropType.CORE;
		}

		this.lang = prop.getAttributeValue("lang", Namespace.XML_NAMESPACE);
		if (this.lang == null)
			prop.getAttributeValue("lang");
		this.o_encoding = prop.getAttributeValue("o-encoding");
		if (this.o_encoding == null)
			this.o_encoding = "";
		this.content = prop.getText();
		if (this.content == null)
			this.content = "";

		if (this.type == null)
			this.type = "";

		this.id = id;
	}

	/**
	 * @param content
	 * @param lang
	 * @param o_encoding
	 * @param propType
	 * @param type
	 */
	public TmxProp(String content, String lang, String o_encoding, PropType propType, String type)
	{
		super();
		this.content = content;
		this.lang = lang;
		this.o_encoding = o_encoding;
		this.propType = propType;
		this.type = type;

		if (this.type == null)
			this.type = "";
		if (this.lang == null)
			this.lang = "";
		if (this.o_encoding == null)
			this.o_encoding = "";
		if (this.content == null)
			this.content = "";

		this.id = idValue++;
	}

	/**
	 * @param content
	 * @param lang
	 * @param o_encoding
	 * @param propType
	 * @param type
	 * @param id
	 */
	public TmxProp(String content, String lang, String o_encoding, PropType propType, String type,
			Integer id)
	{
		super();
		this.content = content;
		this.lang = lang;
		this.o_encoding = o_encoding;
		this.propType = propType;
		this.type = type;
		this.id = id;

		if (this.type == null)
			this.type = "";
		if (this.lang == null)
			this.lang = "";
		if (this.o_encoding == null)
			this.o_encoding = "";
		if (this.content == null)
			this.content = "";
		if (this.id == null)
			this.id = -1;
	}

	/**
	 * Creates a TmxProp from the given parameters.
	 * 
	 * @param type
	 *            the TMX type
	 * @param lang
	 *            the TMX lang or xml:lang
	 * @param o_encoding
	 *            the o-encoding of the property
	 * @param content
	 *            the content of the property or note
	 */
	public TmxProp(String type, String lang, String o_encoding, String content)
	{
		super();
		this.type = type;
		this.lang = lang;
		this.o_encoding = o_encoding;
		this.content = content;
		this.propType = PropType.PROP;
		if (this.type == null)
			this.type = "";
		if (this.lang == null)
			this.lang = "";
		if (this.o_encoding == null)
			this.o_encoding = "";
		if (this.content == null)
			this.content = "";

	}

	/**
	 * format
	 * 
	 * @return a formatted version of the prop
	 */
	public String format()
	{
		String str = this.getClass().getName() + " {type: " + this.getType() + " content: "
				+ this.getContent() + " / lang: " + this.getLang() + " o-encoding: "
				+ this.getO_encoding() + "}";
		return str;
	}

	/**
	 * @return the content
	 */
	public String getContent()
	{
		return content;
	}

	/**
	 * @return the id
	 */
	public Integer getId()
	{
		return id;
	}

	/**
	 * @return the lang
	 */
	public String getLang()
	{
		return lang;
	}

	/**
	 * @return the o_encoding
	 */
	public String getO_encoding()
	{
		return o_encoding;
	}

	/**
	 * @return the propType
	 */
	public PropType getPropType()
	{
		return propType;
	}

	/**
	 * @return the type
	 */
	public String getType()
	{
		return type;
	}

	/**
	 * mapFrom converts a TmxProp into a LinguisticProperty
	 * 
	 * @return the new LinguisticProperty
	 */
	public LinguisticProperty mapFrom()
	{
		LinguisticProperty tmxProp = new LinguisticProperty(this.toString(), this);
		return tmxProp;
	}

	/**
	 * @param content
	 *            the content to set
	 */
	public void setContent(String content)
	{
		this.content = content;
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
	 * @param lang
	 *            the lang to set
	 */
	public void setLang(String lang)
	{
		this.lang = lang;
	}

	/**
	 * @param o_encoding
	 *            the o_encoding to set
	 */
	public void setO_encoding(String o_encoding)
	{
		this.o_encoding = o_encoding;
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
	 * @param type
	 *            the type to set
	 */
	public void setType(String type)
	{
		this.type = type;
	}

	/**
	 * simpleformat
	 * 
	 * @return
	 */
	public String simpleFormat()
	{
		String str = "";
		String type = this.getType();
		if (!type.startsWith("tu"))
		{
			str = "{type: " + this.getType();
			str = str + " content: " + this.getContent();
			if ((this.getLang() != null) && !this.getLang().equals(""))
				str = str + " / lang: " + this.getLang();
			if ((this.getO_encoding() != null) && !this.getO_encoding().equals(""))
				str = str + " / o-encoding: " + this.getO_encoding();
			str = str + "}";
		}
		return str;
	}
}
