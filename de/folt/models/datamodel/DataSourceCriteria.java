/*
 * Created on 09.08.2010
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.models.datamodel;

import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * @author klemens
 * 
 */
public class DataSourceCriteria extends LinguisticProperty
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4344801253135644504L;

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
	 * test
	 */
	private static void test()
	{
		try
		{
			MonoLingualObject mono1 = new MonoLingualObject("My small segment!", "en");
			mono1.addStringLinguisticProperty("OpenTMS", "Folt");
			DataSourceCriteria crit = new DataSourceCriteria("OpenTMS", "Folt");
			System.out.println(crit.getCriteriaName() + " equals "
					+ crit.checkDataSourceCriteria(mono1, "equals"));
			System.out.println(crit.format() + " not-equals "
					+ crit.checkDataSourceCriteria(mono1, "not-equals"));
			crit = new DataSourceCriteria("OpenTMS", "Araya");
			System.out.println(crit.format() + " equals "
					+ crit.checkDataSourceCriteria(mono1, "equals"));
			crit = new DataSourceCriteria("Araya", "Folt");
			System.out.println(crit.format() + " equals "
					+ crit.checkDataSourceCriteria(mono1, "equals"));

			mono1.addStringLinguisticProperty("OpenTMS", "FoltNew");
			crit = new DataSourceCriteria("OpenTMS", "Folt");
			System.out.println(crit.format() + " equals "
					+ crit.checkDataSourceCriteria(mono1, "equals"));

			mono1.getLinguisticProperties().remove(new LinguisticProperty("OpenTMS", "Folt"));
			System.out.println(crit.format() + " equals "
					+ crit.checkDataSourceCriteria(mono1, "equals"));

			mono1.addLinguisticProperty(new LinguisticProperty("Zahl", (Integer) 10));
			crit = new DataSourceCriteria("Zahl", (Integer) 10);
			System.out.println(crit.format() + " equals "
					+ crit.checkDataSourceCriteria(mono1, "=="));
			crit = new DataSourceCriteria("Zahl", (Integer) 11);
			System.out.println(crit.format() + " equals "
					+ crit.checkDataSourceCriteria(mono1, "=="));
			crit = new DataSourceCriteria("Zahl", (Integer) 11);
			System.out.println(crit.format() + " equals "
					+ crit.checkDataSourceCriteria(mono1, "<"));
			crit = new DataSourceCriteria("Zahl", (Integer) 11);
			System.out.println(crit.format() + " equals "
					+ crit.checkDataSourceCriteria(mono1, ">"));
			System.out.println(crit.format() + " equals "
					+ crit.checkDataSourceCriteria(mono1, "!="));

			mono1.addLinguisticProperty(new LinguisticProperty("NochWas", (Integer) 23));
			mono1.addLinguisticProperty(new LinguisticProperty("Value", (Boolean) true));
			mono1.addLinguisticProperty(new LinguisticProperty("Value1", (Boolean) false));

			// now the more complex functionality...
			DataSourceCriteria[] critArray = new DataSourceCriteria[3];
			DataSourceCriteria crit1 = new DataSourceCriteria((Object) "OpenTMS",
					(Object) "FoltNew", "equals");
			DataSourceCriteria crit2 = new DataSourceCriteria((Object) "Zahl", (Integer) 10, "==");
			DataSourceCriteria crit3 = new DataSourceCriteria((Object) "NochWas", (Integer) 12,
					"==");
			critArray[0] = crit1;
			critArray[1] = crit2;
			critArray[2] = crit3;
			System.out.println(crit.checkDataSourceCriteria(mono1, "OpenTMS || Zahl", critArray));
			System.out.println(crit.checkDataSourceCriteria(mono1, "OpenTMS && (Zahl || NochWas)",
					critArray));
			System.out.println(crit.checkDataSourceCriteria(mono1, "OpenTMS && (Zahl && NochWas)",
					critArray));
			System.out.println(crit.checkDataSourceCriteria(mono1, "OpenTMS", critArray));
			System.out.println(crit.checkDataSourceCriteria(mono1, "!OpenTMS", critArray));

			System.out.println("%OpenTMS% == \"FoltNew\": " + mono1.matchLinguisticProperties("%OpenTMS% == \"FoltNew\""));
			System.out.println("%Zahl% == 10: " + mono1.matchLinguisticProperties("%Zahl% == 10"));
			System.out.println("%Zahl% == 12: " + mono1.matchLinguisticProperties("%Zahl% == 12"));
			System.out.println("%Zahl% < 12: " + mono1.matchLinguisticProperties("%Zahl% < 12"));
			System.out.println("%Blabal% < 12: " + mono1.matchLinguisticProperties("%Blabal% < 12"));
			if (mono1.getException() != null)
			{
				mono1.getException().printStackTrace();
			}
			System.out.println("<JavaScript>b = true; b; " + mono1.matchLinguisticProperties("<JavaScript>b = true; b;"));
			System.out.println("<JavaScript>b = Value; b;: " + mono1.matchLinguisticProperties("<JavaScript>b = Value; b;"));
			System.out.println("<JavaScript>b = Value1; b;: " + mono1.matchLinguisticProperties("<JavaScript>b = Value1; b;"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private String criteriaName = null;

	private ScriptEngineManager mgr = null;

	private ScriptEngine jsEngine = null;

	private String comparisionOperator = "==";

	/**
	 * @param lingProperty
	 */
	public DataSourceCriteria(LinguisticProperty lingProperty)
	{
		this.value = lingProperty.getValue();
		this.key = lingProperty.getKey();
		this.criteriaName = (String) this.key;
		this.propType = LinguisticProperty.PropType.CRITERIA;
	}

	/**
	 * @param lingProperty
	 * @param comparisionOperator
	 */
	public DataSourceCriteria(LinguisticProperty lingProperty, String comparisionOperator)
	{
		this.value = lingProperty.getValue();
		this.key = lingProperty.getKey();
		this.criteriaName = (String) this.key;
		this.propType = LinguisticProperty.PropType.CRITERIA;
		this.comparisionOperator = comparisionOperator;
	}

	/**
	 * @param key
	 * @param value
	 */
	public DataSourceCriteria(Object key, Object value)
	{
		super(key, value);
		this.criteriaName = (String) this.key;
		this.propType = LinguisticProperty.PropType.CRITERIA;
	}

	/**
	 * @param key
	 * @param value
	 * @param comparisionOperator
	 */
	public DataSourceCriteria(Object key, Object value, String comparisionOperator)
	{
		super(key, value);
		this.criteriaName = (String) this.key;
		this.propType = LinguisticProperty.PropType.CRITERIA;
		this.comparisionOperator = comparisionOperator;
	}

	/**
	 * @param criteriaName
	 * @param lingProperty
	 */
	public DataSourceCriteria(String criteriaName, LinguisticProperty lingProperty)
	{
		this.value = lingProperty.getValue();
		this.key = lingProperty.getKey();
		this.criteriaName = criteriaName;
		this.propType = LinguisticProperty.PropType.CRITERIA;
	}

	/**
	 * @param criteriaName
	 * @param lingProperty
	 */
	public DataSourceCriteria(String criteriaName, LinguisticProperty lingProperty,
			String comparisionOperator)
	{
		this.value = lingProperty.getValue();
		this.key = lingProperty.getKey();
		this.criteriaName = criteriaName;
		this.propType = LinguisticProperty.PropType.CRITERIA;
		this.comparisionOperator = comparisionOperator;
	}

	/**
	 * @param criteriaName
	 * @param key
	 * @param value
	 */
	public DataSourceCriteria(String criteriaName, Object key, Object value)
	{
		super(key, value);
		this.criteriaName = criteriaName;
		this.propType = LinguisticProperty.PropType.CRITERIA;
	}

	/**
	 * @param criteriaName
	 * @param key
	 * @param value
	 */
	public DataSourceCriteria(String criteriaName, Object key, Object value,
			String comparisionOperator)
	{
		super(key, value);
		this.criteriaName = criteriaName;
		this.propType = LinguisticProperty.PropType.CRITERIA;
		this.comparisionOperator = comparisionOperator;
	}

	/**
	 * checkDataSourceCriteria
	 * 
	 * @param generalLinguisticObject
	 * @return
	 */
	public boolean checkDataSourceCriteria(GeneralLinguisticObject generalLinguisticObject)
	{
		return checkDataSourceCriteria(generalLinguisticObject.getLinguisticProperties(),
				this.comparisionOperator);
	}

	/**
	 * checkDataSourceCriteria
	 * 
	 * @param mono
	 * @param comparisionOperator
	 * @return
	 */
	public boolean checkDataSourceCriteria(GeneralLinguisticObject generalLinguisticObject,
			String comparisionOperator)
	{
		return checkDataSourceCriteria(generalLinguisticObject.getLinguisticProperties(),
				comparisionOperator);
	}

	/**
	 * checkDataSourceCriteria check an array of (named) DataSourceCriteria
	 * against a GeneralLinguisticObject using a boolExpression. 
	 * 
	 * @param generalLinguisticObject
	 *            the GeneralLinguisticObject to check
	 * @param boolExpression
	 *            a string containing the boolean expression based on the names
	 *            of the DataSourceCriteria
	 * @param dataSourceCriterias
	 *            the array of DataSourceCriteria
	 * @return true if the boolExpression matches, otherwise false
	 */
	public boolean checkDataSourceCriteria(GeneralLinguisticObject generalLinguisticObject,
			String boolExpression, DataSourceCriteria[] dataSourceCriterias)
	{
		try
		{
			// run all the DataSourceCriteria
			for (int i = 0; i < dataSourceCriterias.length; i++)
			{
				if (dataSourceCriterias[i] != null)
				{
					boolean bResults = dataSourceCriterias[i]
							.checkDataSourceCriteria(generalLinguisticObject);
					String name = dataSourceCriterias[i].getCriteriaName();
					boolExpression = boolExpression.replaceAll(name, bResults + "");
				}
			}
			// now call the JavaScript scripting engine to check the boolean
			// values
			if (mgr == null)
			{
				mgr = new ScriptEngineManager();
				jsEngine = mgr.getEngineByName("JavaScript");
			}
			String evalcode = "res = " + boolExpression + "; res;";
			boolean result = (Boolean) jsEngine.eval(evalcode);
			return result;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return false;
	}

	/**
	 * checkDataSourceCriteria compares LinguisticProperties with AND or OR
	 * 
	 * @param lingProperties
	 *            the LinguisticProperties
	 * @param comparisionOperator
	 *            the comparision operator to use
	 * @param bOr
	 *            if true use OR to compare; otherwise AND
	 * @return true or false
	 */
	@SuppressWarnings("unchecked")
	public boolean checkDataSourceCriteria(LinguisticProperties lingProperties,
			String comparisionOperator)
	{
		try
		{
			Set<String> enumprop = lingProperties.keySet();
			@SuppressWarnings("rawtypes")
			Iterator it = enumprop.iterator();

			while (it.hasNext())
			{
				String key = (String) it.next();
				Object value = (Object) lingProperties.get(key);
				if (value.getClass().getName().equals("java.util.Vector"))
				{
					for (int i = 0; i < ((Vector<LinguisticProperty>) value).size(); i++)
					{
						LinguisticProperty ling = ((Vector<LinguisticProperty>) value).get(i);
						boolean bWhat = this.checkDataSourceCriteria(ling, comparisionOperator);
						if (bWhat == true)
							return true;
					}
				}
				else
				{
					LinguisticProperty ling = (LinguisticProperty) lingProperties.get(key);
					boolean bWhat = this.checkDataSourceCriteria(ling, comparisionOperator);
					if (bWhat == true)
						return true;
				}
			}

			return false;

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return false;
	}

	/**
	 * checkDataSourceCriteria a simple = comparison of key and value
	 * 
	 * @param lingProperty
	 *            the LinguisticProperty to compare
	 * @return true if = equal
	 */
	public boolean checkDataSourceCriteria(LinguisticProperty lingProperty)
	{
		if ((this.key == lingProperty.key) && (this.value == lingProperty.value))
			return true;
		return false;
	}

	/**
	 * checkDataSourceCriteria a simple = comparison of key and value
	 * 
	 * @param lingProperty
	 *            the LinguisticProperty to compare
	 * @param comparisionOperator
	 *            a comparison operator equals, not-equals for string objects >,
	 *            <, !=, <=, >= for numeric objects like int, float, long,
	 *            double
	 * @return true if = equal
	 */
	public boolean checkDataSourceCriteria(LinguisticProperty lingProperty,
			String comparisionOperator)
	{
		try
		{
			if (!((String) this.key).equals((String) lingProperty.key))
				return false;

			@SuppressWarnings("rawtypes")
			Class crittClass = this.value.getClass();
			@SuppressWarnings({ "unused", "rawtypes" })
			Class propClass = lingProperty.value.getClass();

			String crittClassName = this.value.getClass().getName();
			String propClassName = lingProperty.value.getClass().getName();
			if (!crittClassName.equals(propClassName))
				return false;

			if (crittClass == String.class)
			{
				String valueCrit = (String) this.value;
				String valueProp = (String) lingProperty.value;
				if (comparisionOperator.equals("equals"))
				{
					return valueCrit.equals(valueProp);
				}
				else if (comparisionOperator.equals("equalsIgnoreCase"))
				{
					return valueCrit.equalsIgnoreCase(valueProp);
				}
				else if (comparisionOperator.equals("not-equals"))
				{
					return !(valueCrit.equals(valueProp));
				}
				else if (comparisionOperator.equals("not-equalsIgnoreCase"))
				{
					return !(valueCrit.equalsIgnoreCase(valueProp));
				}
				return false;
			}
			if ((crittClass == int.class) || (crittClass == Integer.class))
			{
				if (comparisionOperator.equals("=="))
					return (Integer) this.value == ((Integer) lingProperty.value);
				if (comparisionOperator.equals("!="))
					return (Integer) this.value != ((Integer) lingProperty.value);

				if (comparisionOperator.equals("<"))
					return (Integer) this.value < ((Integer) lingProperty.value);
				if (comparisionOperator.equals(">"))
					return (Integer) this.value > ((Integer) lingProperty.value);

				if (comparisionOperator.equals("<="))
					return (Integer) this.value <= ((Integer) lingProperty.value);
				if (comparisionOperator.equals(">="))
					return (Integer) this.value >= ((Integer) lingProperty.value);
			}
			if ((crittClass == float.class) || (crittClass == Float.class))
			{
				if (comparisionOperator.equals("=="))
					return (Float) this.value == ((Float) lingProperty.value);
				if (comparisionOperator.equals("!="))
					return (Float) this.value != ((Float) lingProperty.value);

				if (comparisionOperator.equals("<"))
					return (Float) this.value < ((Float) lingProperty.value);
				if (comparisionOperator.equals(">"))
					return (Float) this.value > ((Float) lingProperty.value);

				if (comparisionOperator.equals("<="))
					return (Float) this.value <= ((Float) lingProperty.value);
				if (comparisionOperator.equals(">="))
					return (Float) this.value >= ((Float) lingProperty.value);
			}
			if ((crittClass == long.class) || (crittClass == Long.class))
			{
				if (comparisionOperator.equals("=="))
					return (Long) this.value == ((Long) lingProperty.value);
				if (comparisionOperator.equals("!="))
					return (Long) this.value != ((Long) lingProperty.value);

				if (comparisionOperator.equals("<"))
					return (Long) this.value < ((Long) lingProperty.value);
				if (comparisionOperator.equals(">"))
					return (Long) this.value > ((Long) lingProperty.value);

				if (comparisionOperator.equals("<="))
					return (Long) this.value <= ((Long) lingProperty.value);
				if (comparisionOperator.equals(">="))
					return (Long) this.value >= ((Long) lingProperty.value);
			}
			if ((crittClass == double.class) || (crittClass == Double.class))
			{
				if (comparisionOperator.equals("=="))
					return (Double) this.value == ((Double) lingProperty.value);
				if (comparisionOperator.equals("!="))
					return (Double) this.value != ((Double) lingProperty.value);

				if (comparisionOperator.equals("<"))
					return (Double) this.value < ((Double) lingProperty.value);
				if (comparisionOperator.equals(">"))
					return (Double) this.value > ((Double) lingProperty.value);

				if (comparisionOperator.equals("<="))
					return (Double) this.value <= ((Double) lingProperty.value);
				if (comparisionOperator.equals(">="))
					return (Double) this.value >= ((Double) lingProperty.value);
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return false;
	}


	public synchronized String format()
	{
		return this.getCriteriaName() + ": " + super.format();
	}

	/**
	 * @return the comparisionOperator
	 */
	public String getComparisionOperator()
	{
		return comparisionOperator;
	}

	/**
	 * @return the criteriaName
	 */
	public String getCriteriaName()
	{
		return criteriaName;
	}

	/**
	 * @param comparisionOperator
	 *            the comparisionOperator to set
	 */
	public void setComparisionOperator(String comparisionOperator)
	{
		this.comparisionOperator = comparisionOperator;
	}

	/**
	 * @param criteriaName
	 *            the criteriaName to set
	 */
	public void setCriteriaName(String criteriaName)
	{
		this.criteriaName = criteriaName;
	}

}
