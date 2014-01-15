/**
 * 
 */
package de.folt.models.documentmodel.xliff;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.jdom.Element;

import de.folt.util.OpenTMSException;
import flexjson.JSONDeserializer;

/**
 * Based on FEISGILTT 2012 - 3rd International XLIFF Symposium, Seattle -
 * Bringing Procedural Knowledge to XLIFF by Klemens Waldhör
 * 
 * @author Klemens
 * 
 */
public class XliffScriptingSupport
{

	/**
	 * @author klemens
	 * 
	 */
	public class XliffScriptingSupportException extends Exception
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -8909710438080414704L;

		public XliffScriptingSupportException(String msg)
		{
			super(msg);
		}
	}

	/**
	 * @param args
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args)
	{
		String javaScriptCode = "function isPrim($zahl) {  for ($i = 2; $i < Math.sqrt($zahl); $i++)  { if (($zahl % $i) == 0)  return 0; }  return 1; } function returnTransUnit() { return transunit; }";
		XliffScriptingSupport xliffScriptingSupport = new XliffScriptingSupport();
		String javaScript = "";
		String onenter = "";
		String onexit = "";
		try
		{
			if (1 == 2)
			{
				System.out.println(xliffScriptingSupport.eval("1+2;", (Object[]) null));
				System.out.println(xliffScriptingSupport.eval("(1+2)/4;", (Object[]) null));
				System.out.println(xliffScriptingSupport.eval("a;", new String("a"), new String("hallo")));
				System.out.println(xliffScriptingSupport.eval(javaScriptCode + "isPrim(5);", (Object[]) null));
				System.out.println(xliffScriptingSupport.eval(javaScriptCode + "isPrim(10);", (Object[]) null));
				javaScriptCode = "importPackage(java.awt); importClass(java.awt.Frame); var frame = new java.awt.Frame(\"hello\"); frame.setVisible(true); frame.title;";
				// System.out.println(xliffScriptingSupport.eval(javaScriptCode,
				// (Object[]) null));
			}
			XliffDocument xliffdoc = new XliffDocument();
			xliffdoc.loadXmlFile(args[0]);
			List<Element> files = xliffdoc.getFiles();
			Element file = files.get(0);
			Element header = file.getChild("header");
			// Element script = header.getChild("script");
			javaScript = xliffScriptingSupport.loadScript(header);
			// System.out.println(javaScript);
			Element body = xliffdoc.getBody(file);
			List<Element> transunits = xliffdoc.getAllTransUnitsList(body);
			for (int i = 0; i < transunits.size(); i++)
			{
				Element transunit = transunits.get(i);
				int iPos = body.indexOf(transunit);
				onenter = transunit.getAttributeValue("on-enter");
				onexit = transunit.getAttributeValue("on-exit");
				Object[] obj = new Object[2];
				obj[0] = "transunit";
				obj[1] = xliffdoc.elementToString(transunit);
				xliffScriptingSupport.jsEngine.put("transunit", xliffdoc.elementToString(transunit));
				System.out.println(obj[1]);
				try
				{
					String transunitbefore = (String) xliffScriptingSupport.eval(javaScript + "\n" + onenter, obj);
					System.out.println(transunitbefore);
					@SuppressWarnings("rawtypes")
					JSONDeserializer jsonseser = new JSONDeserializer();
					@SuppressWarnings("rawtypes")
					HashMap res1 = (HashMap) jsonseser.deserialize(transunitbefore);
					transunitbefore = (String) res1.get("transunit");
					Element transuinitbefore = xliffdoc.buildElement(transunitbefore);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}

				try
				{
					String transunitafter = (String) xliffScriptingSupport.eval(javaScript + "\n" + onexit, obj);
					System.out.println(transunitafter);
					@SuppressWarnings("rawtypes")
					JSONDeserializer jsonseser = new JSONDeserializer();
					@SuppressWarnings("rawtypes")
					HashMap res1 = (HashMap) jsonseser.deserialize(transunitafter);
					transunitafter = (String) res1.get("transunit");
					Element transuinitafter = xliffdoc.buildElement(transunitafter);

					body.removeContent(iPos);
					body.addContent(iPos, transuinitafter);
				}
				catch (OpenTMSException e)
				{
					e.printStackTrace();
				}
			}
			xliffdoc.saveToXmlFile(xliffdoc.getXmlDocumentName() + ".after.xlf");
		}
		catch (ScriptException e)
		{
			System.out.println(javaScript);
			System.out.println("on-enter: " + onenter);
			System.out.println("on-exit: " + onexit);
			e.printStackTrace();
		}
		catch (XliffScriptingSupportException e)
		{
			System.out.println(javaScript);
			System.out.println("on-enter: " + onenter);
			System.out.println("on-exit: " + onexit);
			e.printStackTrace();
		}

	}

	private int debug = 0;

	private ScriptEngine jsEngine = null;

	private ScriptEngineManager mgr = null;

	/**
	 * 
	 */
	public XliffScriptingSupport()
	{
		super();
		if (mgr == null)
		{
			mgr = new ScriptEngineManager();
			jsEngine = mgr.getEngineByName("JavaScript");
		}
	}

	/**
	 * @param javaScriptCode
	 * @param params
	 * @return
	 * @throws ScriptException
	 * @throws XliffScriptingSupportException
	 */
	public Object eval(String javaScriptCode, Object... params) throws ScriptException, XliffScriptingSupportException
	{
		if (jsEngine == null)
			throw new XliffScriptingSupportException("jsEngine: not initialised");

		if (params != null)
		{
			int iL = params.length;
			if ((iL % 2) != 0)
			{
				System.out.println("javaScriptCode:" + javaScriptCode);
				if (params != null)
				{
					for (int i = 0; i < params.length; i++)
					{
						System.out.println("param[" + i + "]: " + params[i]);
					}
				}
				throw new XliffScriptingSupportException("jsEngine: parameter number " + params.length
						+ " not even number or 0");
			}
			if (debug == 1)
				System.out.println("javaScriptCode=" + javaScriptCode);
			for (int i = 0; i < iL; i++)
			{
				String var = (String) params[i];
				var = var.replaceAll("::", "");
				var = var.replaceAll("__", "");
				i++;
				Object val = (Object) params[i];
				jsEngine.put(var, val);
				if (debug == 1)
					System.out.println("var=" + var + "=" + val);
			}
		}

		try
		{
			Object obj = jsEngine.eval(javaScriptCode);
			return obj;

		}
		catch (Exception e)
		{
			System.out.println("javaScriptCode: \"" + javaScriptCode + "\"");
			e.printStackTrace();
		}

		return null;
	}

	public int getDebug()
	{
		return debug;
	}

	public ScriptEngine getJsEngine()
	{
		return jsEngine;
	}

	public ScriptEngineManager getMgr()
	{
		return mgr;
	}

	/**
	 * @param elem
	 * @return
	 */
	public String loadScript(Element elem)
	{
		String javaScript = "";
		@SuppressWarnings("unchecked")
		List<Element> scripts = elem.getChildren("script");
		for (int i = 0; i < scripts.size(); i++)
		{
			javaScript = javaScript + "\n" + scripts.get(i).getText();
			if (scripts.get(i).getAttributeValue("src") != null)
			{
				try
				{
					URL oracle = new URL(scripts.get(i).getAttributeValue("src"));
					BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));

					String inputLine = "";
					while ((inputLine = in.readLine()) != null)
					{
						javaScript = javaScript + "\n" + inputLine;
					}
					in.close();
				}
				catch (MalformedURLException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return javaScript;
	}

	public void setDebug(int debug)
	{
		this.debug = debug;
	}

	public void setJsEngine(ScriptEngine jsEngine)
	{
		this.jsEngine = jsEngine;
	}

	public void setMgr(ScriptEngineManager mgr)
	{
		this.mgr = mgr;
	}

}
