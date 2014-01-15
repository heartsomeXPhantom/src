/*
 * Created on 23.07.2010
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.test;

import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * see https://scripting.dev.java.net/
 * @author klemens
 *
 */
public class ScriptingEngine
{

	/**
	 * main
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		ScriptEngineManager mgr = new ScriptEngineManager();
		ScriptEngine jsEngine = mgr.getEngineByName("JavaScript");
		try
		{
			jsEngine.eval("print('Hello, world!\\n')");
			boolean result = (Boolean)jsEngine.eval("a = 12; b = 13; res = a < b; print(res); print('\\n'); res;");
			System.out.println("result = " + result + "\n");
			jsEngine.eval("print(true && false); print('\\n')");
			jsEngine.eval("print(true || false); print('\\n')");
			
			String xString = "a = 12; b = 13; res = a < b; print(res); print('\\n'); res;";
			result = (Boolean)jsEngine.eval(xString);
			System.out.println("result xString = " + result + "\n");
		}
		catch (ScriptException ex)
		{
			ex.printStackTrace();
		}

		 mgr = new ScriptEngineManager();
		List<ScriptEngineFactory> factories = mgr.getEngineFactories();
		for (ScriptEngineFactory factory : factories)
		{
			System.out.println("\nScriptEngineFactory Info");
			String engName = factory.getEngineName();
			String engVersion = factory.getEngineVersion();
			String langName = factory.getLanguageName();
			String langVersion = factory.getLanguageVersion();
			System.out.printf("\tScript Engine: %s (%s)\n", engName, engVersion);
			List<String> engNames = factory.getNames();
			for (String name : engNames)
			{
				System.out.printf("\tEngine Alias: %s\n", name);
			}
			System.out.printf("\tLanguage: %s (%s)\n", langName, langVersion);
		}

	}

}
