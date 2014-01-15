/*
 * Created on 07.04.2011
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.webservices;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import de.folt.util.OpenTMSSupportFunctions;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

/**
 * @author klemens
 * 
 *         To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and Comments
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class OpenTMSWebServiceResult extends HashMap
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1600816256232882225L;

	/**
	 * deserializeToString
	 * 
	 * @param jsonString
	 */
	public static String deserializeToString(HashMap rethash)
	{
		return deserializeToString(rethash, "");
	}

	/**
	 * deserializeToString
	 * 
	 * @param rethash
	 * @param ins
	 * @return
	 */
	public static String deserializeToString(HashMap rethash, String ins)
	{
		String result = "";
		// System.out.println("rethash size: " + rethash.size());
		Set<String> setstring = rethash.keySet();
		Iterator<String> it = setstring.iterator();
		while (it.hasNext())
		{
			Object key = it.next();
			Object value = rethash.get(key);
			if (value == null)
			{
				result += ins + "\"" + key + "\": \"" + value + "\"\n";
			}
			else if (value.getClass().equals(java.lang.String.class))
			{
				result += ins + "\"" + key + "\": \"" + value + "\"\n";
			}
			else if (value.getClass().equals(java.lang.Integer.class))
			{
				result += ins + "\"" + key + "\": \"" + value + "\"\n";
			}
			else if (value.getClass().equals(java.util.ArrayList.class))
			{
				result += ins + "\"" + key + "\": \"" + value + "\"\n";
			}
			else if (value.getClass().equals(java.util.HashMap.class))
			{
				HashMap rethash1 = (HashMap) value;
				result += ins + "\"" + key + ": \"" + java.util.HashMap.class.getName() + "\"\n";
				result += deserializeToString(rethash1, "\t") + "\n";
			}
			else
			{
				result += ins + "\"" + key + "\": \"" + value.getClass().getName() + "\"\n";
			}
		}
		return result;
	}

	/**
	 * deserializeToString
	 * 
	 * @param jsonString
	 * @return
	 */
	public static String deserializeToString(String jsonString)
	{
		HashMap rethash = (HashMap) new JSONDeserializer().deserialize(jsonString);
		return deserializeToString(rethash);
	}

	/**
	 * jsonDeserialise
	 * 
	 * @param jsonString
	 * @return
	 */
	public static HashMap jsonDeserialise(String jsonString)
	{
		HashMap result = (HashMap) new JSONDeserializer().deserialize(jsonString);
		return result;
	}

	/**
	 * jsonSerialise
	 * 
	 * @return
	 */
	public String jsonSerialise()
	{
		JSONSerializer serializer = new JSONSerializer();
		String result = serializer.deepSerialize(this);
		return result;
	}

	/**
	 * jsonSerialise
	 * 
	 * @return
	 */
	public static String jsonSerialise(HashMap map)
	{
		JSONSerializer serializer = new JSONSerializer();
		String result = serializer.deepSerialize(map);
		return result;
	}

	/**
	 * @param errorCode
	 *            the errorCode to set
	 */
	public void setError(int errorCode, String errorMessage)
	{
		this.put("ErrorCode", errorCode);
		this.put("ErrorMessage", errorMessage);
	}

	/**
	 * setException
	 * 
	 * @param e
	 */
	public void setException(Exception e)
	{
		try
		{
			this.put("Exception", OpenTMSSupportFunctions.exceptionToString(e));
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
		}
	}

	/**
	 * setMethod
	 * 
	 * @param method
	 */
	public void setMethod(String method)
	{
		this.put("Method", method);
	}

	/**
	 * setParameters
	 * 
	 * @param parameters
	 */
	public void setParameters(Hashtable parameters)
	{
		this.put("Parameters", parameters);
	}

	/**
	 * setParameters
	 * 
	 * @param parameters
	 */
	public void setParameters(String... parameters)
	{
		int len = parameters.length;
		if (len % 2 != 0)
			return;
		Hashtable params = (Hashtable) this.get("Parameters");
		if (params == null)
			params = new Hashtable();
		for (int i = 0; i < len; i = i + 2)
		{
			String name = parameters[i];
			String value = parameters[i + 1];
			if ((name != null) && (value != null))
				params.put(name, value);
		}
		this.put("Parameters", params);
	}
}
