package de.folt.webservices;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import de.folt.util.OpenTMSSupportFunctions;

import flexjson.JSONDeserializer;

/**
 * Implementation des Clients der einen WebService anspricht.
 * 
 * @author Klemens Waldhör
 */
public class OpenTMSWebServiceClient
{

	public static boolean syncmode = true;

	public static OpenTMSWebServiceServer server = null;
	public static boolean bWithServer = true;
	public static String url = null;

	public static OpenTMSWebService service = null;
	public static OpenTMSWebServiceInterface openTMSService = null;

	public static String helpString = "* Test Client for openTMS Webservice.\n* Parameters are supplied as key value pairs: -<key> <value> ...\n"
			+ "* -f filename or -file  filename - file with key value pairs\n"
			+ "* -withServer true/false - start webservice if true - default = true\n"
			+ "* -url url of the webservice (if not specified default is used = \"http://localhost:8082\")\n"
			+ "* -test test1 / test2 / test3 / test4 call various test procedures\n"
			+ "* -json / -json[0-99] jsonstring supply a web service method as a json string\n"
			+ "* -script / -script[0-99] scriptfilename supply a script containing lines of json strings with web service methods\n"
			+ "* -param value - supply various params for the webservice (note that the - will be removed from the key for the web service calls)\n"
			+ "* Example of a script file (* can  be used for comment lines):\n"
			+ "* * test query method of synchronize call\n"
			+ "* *\n"
			+ "* * query list of shared memories available for the given user\n"
			+ "* {\"method\":\"query\",\"user-id\":\"klemens\",\"password\":\"blabla\"}\n"
			+ "* *\n"
			+ "* * query details for a specific shared memory\n"
			+ "* {\"method\":\"query\",\"user-id\":\"klemens\",\"password\":\"blabla\",\"name\":\"mysqlsynctest\"}\n"
			+ "* Example of a json parameter from command line\n"
			+ "* java -cp \\\"%JARS%\\\" -Xmx1000M de.folt.webservices.OpenTMSWebServiceClient -url http://localhost:8090 -withServer false -json \"{\\\"method\\\":\\\"query\\\",\\\"user-id\\\":\\\"klemens\\\",\\\"password\\\":\\\"blabla\\\"}\\\" -json0 \\\"{\\\"method\\\":\\\"query\\\",\\\"user-id\\\":\\\"klemens\\\",\\\"password\\\":\\\"blabla\\\",\\\"name\\\":\\\"mysqlsynctest\\\"}\" > webserviceclient.log 2>&1\n";

	private static String testMode = "test4";

	/**
	 * Test Client for openTMS Webservice.<br />
	 * Parameters are supplied as key value pairs: -<key> <value> ... <br />
	 * -f &lt;filename&gt; or -file &lt;filename&gt; - file with key value pairs<br />
	 * -withServer true/false - start webservice if true - default = true<br />
	 * -url url of the webservice (if not specified default is used =
	 * "http://localhost:8082")<br />
	 * -test test1 / test2 / test3 / test4 call various test procedures<br />
	 * -json / -json[0-99] jsonstring supply a web service method as a json
	 * string<br />
	 * -script / -script[0-99] &lt;scriptfilename&gt; supply a script containing
	 * lines of json strings with web service methods<br />
	 * -param value - supply various params for the webservice (note that the -
	 * will be removed from the key for the web service calls)<br />
	 * Example of a script file (* can be used for comment lines):<br />
	 * 
	 * <pre>
	 * * test query method of synchronize call
	 * *
	 * * query list of shared memories available for the given user
	 * {"method":"query","user-id":"klemens","password":"blabla"}
	 * *
	 * * query details for a specific shared memory
	 * {"method":"query","user-id":"klemens","password":"blabla","name":"mysqlsynctest"}
	 * </pre>
	 * 
	 * Example of a json parameter from command line<br />
	 * 
	 * <pre>
	 * java -cp  "%JARS%" -Xmx1000M de.folt.webservices.OpenTMSWebServiceClient -url http://localhost:8090 -withServer false -json "{\"method\":\"query\",\"user-id\":\"klemens\",\"password\":\"blabla\"}" -json0 "{\"method\":\"query\",\"user-id\":\"klemens\",\"password\":\"blabla\",\"name\":\"mysqlsynctest\"}" > webserviceclient.log 2>&1
	 * </pre>
	 * 
	 * @param args
	 */
	public static void main(String args[])
	{
		if (args.length == 0)
		{
			System.out.println(helpString);
			return;
		}

		try
		{
			Hashtable<String, String> arguments = OpenTMSSupportFunctions.argumentReader(args);

			if (arguments.containsKey("-f"))
			{
				OpenTMSSupportFunctions.argumentReader(arguments.get("-f"), arguments);
			}
			if (arguments.containsKey("-file"))
			{
				OpenTMSSupportFunctions.argumentReader(arguments.get("-file"), arguments);
			}

			if (arguments.containsKey("-withServer"))
			{
				try
				{
					bWithServer = Boolean.parseBoolean(arguments.get("-withServer"));
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}

			if (arguments.containsKey("-url"))
			{
				try
				{
					url = arguments.get("-url");
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}

			if (bWithServer)
			{
				if (url != null)
				{
					url = args[0];
					server = new OpenTMSWebServiceServer(url);
					server.createServer(url, false);
				}
				else
				{
					server = new OpenTMSWebServiceServer();
					server.createServer(false);
				}
			}

			OpenTMSWebServiceConstants consts = new OpenTMSWebServiceConstants();
			if (server != null)
				consts = server.getOpenTMSWebServiceConstants();
			if (url != null)
				consts.setOpenTMSWebServerURL(url);
			service = new OpenTMSWebService(consts);
			openTMSService = service.getOpenTMSPort();

			if (arguments.containsKey("-test"))
			{
				testMode = arguments.get("-test");
				arguments.remove("-test");
				test();
			}

			// support several test strings from json0 ... json9
			for (int i = 0; i < 100; i++)
			{
				if (arguments.containsKey("-test" + i))
				{
					testMode = arguments.get("-test" + i);
					test();
					arguments.remove("-test" + i);
				}
			}

			// insert a json string like
			// {"method":"query","user-id":"klemens","password":"blabla","name":"mysqlsynctest"}
			if (arguments.containsKey("-json"))
			{
				String parameters = arguments.get("-json");
				String result = openTMSService.synchronize(parameters);
				System.out.println("result: " + result);
				System.out.println(OpenTMSWebServiceResult.deserializeToString(result));
				arguments.remove("-json");
			}

			// support several json strings from json0 ... json9
			for (int i = 0; i < 100; i++)
			{
				if (arguments.containsKey("-json" + i))
				{
					String parameters = arguments.get("-json" + i);
					String result = openTMSService.synchronize(parameters);
					System.out.println("result: " + result);
					System.out.println(OpenTMSWebServiceResult.deserializeToString(result));
					arguments.remove("-json" + i);
				}
			}

			if (arguments.containsKey("-script"))
			{
				String file = arguments.get("-script");
				Vector<String> commands = OpenTMSSupportFunctions.readFileIntoVector(file, "UTF-8");
				for (int i = 0; i < commands.size(); i++)
				{
					String parameters = commands.get(i);
					if (parameters.startsWith("*"))
						continue;
					if (parameters.matches("[ \\t]*\\*"))
						continue;
					String result = openTMSService.synchronize(parameters);
					System.out.println("result: " + result);
					System.out.println(OpenTMSWebServiceResult.deserializeToString(result));
				}
				arguments.remove("-script");
			}
			// support several script strings from scriptn0 ... json99
			for (int k = 0; k < 100; k++)
			{
				if (arguments.containsKey("-script" + k))
				{
					String file = arguments.get("-script" + k);
					Vector<String> commands = OpenTMSSupportFunctions.readFileIntoVector(file, "UTF-8");
					for (int i = 0; i < commands.size(); i++)
					{
						String parameters = commands.get(i);
						if (parameters.startsWith("*"))
							continue;
						if (parameters.matches("[ \\t]*\\*"))
							continue;
						String result = openTMSService.synchronize(parameters);
						System.out.println("result: " + result);
						System.out.println(OpenTMSWebServiceResult.deserializeToString(result));
					}
					arguments.remove("-script" + k);
				}
			}

			// now we must replace all -keys in the arguments
			Enumeration<String> enumst = arguments.keys();
			while (enumst.hasMoreElements())
			{
				String key = enumst.nextElement();
				String value = arguments.get(key);
				arguments.remove(key);
				key = key.replaceFirst("^\\-", "");
				arguments.put(key, value);
			}

			HashMap<String, String> paramHash = new HashMap<String, String>();
			paramHash.putAll(arguments);

			String action = null;
			if (arguments.containsKey("action"))
			{
				action = arguments.get("action");
			}

			if ((action == null) || action.equals("sync") || action.equals("synchronize"))
			{
				// does it contain a method?
				if (paramHash.containsKey("method"))
				{
					String parameters = OpenTMSWebServiceResult.jsonSerialise(paramHash);
					String result = openTMSService.synchronize(parameters);
					System.out.println("result: " + result);
					System.out.println(OpenTMSWebServiceResult.deserializeToString(result));
				}
			}
			else
			{
				System.out.println("Method not supported: " + action);
			}

			if (server != null)
				server.shutdownServer();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (server != null)
			{
				server.shutdownServer();
			}
		}
	}

	private static void test()
	{
		String result = openTMSService.getDataSources("ALL");
		System.out.println("result openTMSService.getDataSources(\"ALL\"): " + result);

		if (testMode.equals("test"))
		{
			@SuppressWarnings({ "rawtypes", "unused" })
			HashMap res = OpenTMSWebServiceResult.jsonDeserialise(result);
			System.out.println(OpenTMSWebServiceResult.deserializeToString(result));
			try
			{
				result = openTMSService.translate("Wir übersetzen mit \" openTMS \",", "de", "en",
						"MicrosoftTranslate", 80, "SIMPLESTRING");
				System.out
						.println("result: openTMSService.translate(\"Hallo mit \" und \" test\", \"de\", \"en\", \"MicrosoftTranslate\", 80, \"SIMPLESTRING\");:\n"
								+ result);
				System.out.println(OpenTMSWebServiceResult.deserializeToString(result));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			try
			{
				String xml = "<trans-unit approved=\"yes\" help-id=\"37\" id=\"37\" resname=\"res37\" ts=\"1977380306\" xml:space=\"preserve\">	      <source xml:lang=\"de\">Blockieren oder Abbremsen des Ventilators durch Hineinstecken von Gegenständen.</source><target ts=\"m1773372607:c1773372607\" xml:lang=\"en\">Blocking or braking of the fan by inserting objects.</target><alt-trans extradata=\"9.2.2010 17:47:48,300\" match-quality=\"100\" origin=\"Araya_bal_int\" tool=\"TM Search\" ts=\"1977380306\">	<source xml:lang=\"de\">Blockieren oder Abbremsen des Ventilators durch Hineinstecken von Gegenständen.</source>	<target ts=\"m1773372607:c1773372607\" xml:lang=\"en\">Blocking or braking of the fan by inserting objects.</target>	<prop-group>		<prop prop-type=\"entryid\">1267a4c888310_sauber</prop>		<prop prop-type=\"SCD\">9.2.2010 17:47:48,300</prop>		<prop prop-type=\"SMD\">9.2.2010 17:47:48,300</prop>		<prop prop-type=\"SCA\">pubantz</prop>		<prop prop-type=\"SMA\">pubantz</prop></prop-group></alt-trans></trans-unit>";
				result = openTMSService.translate(xml, "de", "en", "MicrosoftTranslate", 80, "XLIFF");
				System.out
						.println("result: openTMSService.translate(xml, \"de\", \"en\", \"MicrosoftTranslate\", 80, \"XLIFF\")\n"
								+ result);
				System.out.println(OpenTMSWebServiceResult.deserializeToString(result));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			try
			{
				result = openTMSService.getMonolingualObject("1");
				System.out.println("result: " + result);
				res = OpenTMSWebServiceResult.jsonDeserialise(result);
				System.out.println(OpenTMSWebServiceResult.deserializeToString(result));
			}
			catch (Exception e2)
			{
				e2.printStackTrace();
			}

			try
			{
				result = openTMSService.getMultilingualObject("1");
				res = OpenTMSWebServiceResult.jsonDeserialise(result);
				System.out.println("result: " + result);
				System.out.println(OpenTMSWebServiceResult.deserializeToString(result));
			}
			catch (Exception e1)
			{
				e1.printStackTrace();
			}

			try
			{
				result = openTMSService.getLanguages();
				res = OpenTMSWebServiceResult.jsonDeserialise(result);
				System.out.println("result: " + result);
				System.out.println(OpenTMSWebServiceResult.deserializeToString(result));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

		}

		String parameters = "";
		HashMap<String, String> paramHash = new HashMap<String, String>();

		if (testMode.equals("test1"))
		{
			try
			{
				paramHash.put("method", "create");
				paramHash.put("user-id", "klemens");
				paramHash.put("password", "blabla");
				paramHash.put("name", "mysqlsynctest");
				// String parameter =
				// "dataSourceName=mssqlsynctest;dataSourceGenericType=de.folt.models.datamodel.sql.OpenTMSSQLDataSource;dataSourceType=hibernate.mysql.cfg.xml";

				String parameter = "dataSourceName=mssqlsynctest;dataSourceGenericType=de.folt.models.datamodel.sql.OpenTMSSQLDataSource;dataSourceType=hibernate.mssqlserver.cfg.xml";
				parameter = parameter
						+ ";dataSourceServer=localhost;dataSourcePort=1433;dataSourceUser=sa;dataSourcePassword=blabla";
				paramHash.put("parameter", parameter);
				paramHash.put("user-id-list", "klemens,michael,stefan");
				parameters = OpenTMSWebServiceResult.jsonSerialise(paramHash);
				result = openTMSService.synchronize(parameters);
				System.out.println("result: " + result);
				System.out.println(OpenTMSWebServiceResult.deserializeToString(result));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

		}
		if (testMode.equals("test2"))
		{
			try
			{
				paramHash.put("method", "xxdelete");
				paramHash.put("user-id", "klemens");
				paramHash.put("password", "blabla");
				paramHash.put("name", "mysqlsynctest");
				String parameter = "";
				paramHash.put("parameter", parameter);
				paramHash.put("user-id-list", "klemens,michael,stefan");
				parameters = OpenTMSWebServiceResult.jsonSerialise(paramHash);
				result = openTMSService.synchronize(parameters);
				System.out.println("result: " + result);
				System.out.println(OpenTMSWebServiceResult.deserializeToString(result));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		if (testMode.equals("test3"))
		{
			try
			{
				String tmx_document = "c:\\Program Files\\OpenTMS\\test\\tekom2009\\sample.tmx";
				tmx_document = de.folt.util.OpenTMSSupportFunctions.copyFileToString(tmx_document);
				paramHash.put("method", "upload");
				paramHash.put("user-id", "klemens");
				paramHash.put("password", "blabla");
				paramHash.put("name", "mssqlsynctest");
				paramHash.put("parameter", "empty");
				// paramHash.put("user-id-list", "klemens,michael,stefan");
				paramHash.put("tmx-document", tmx_document);
				paramHash.put("encoding", "BASE64");
				paramHash.put("update-counter", "");
				parameters = OpenTMSWebServiceResult.jsonSerialise(paramHash);
				result = openTMSService.synchronize(parameters);
				System.out.println("result: " + result);
				System.out.println(OpenTMSWebServiceResult.deserializeToString(result));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		if (testMode.equals("test4"))
		{
			String updateCounter = "";
			try
			{
				paramHash.put("method", "getUpdateCounter");
				parameters = OpenTMSWebServiceResult.jsonSerialise(paramHash);
				result = openTMSService.synchronize(parameters);
				System.out.println("result: " + result);
				System.out.println(OpenTMSWebServiceResult.deserializeToString(result));
				@SuppressWarnings("rawtypes")
				HashMap rethash = (HashMap) new JSONDeserializer().deserialize(result);
				updateCounter = (String) rethash.get("update-counter");
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			try
			{
				paramHash.put("method", "query");
				paramHash.put("user-id", "klemens");
				paramHash.put("password", "blabla");
				paramHash.put("name", "opentm2test");
				String parameter = "";
				paramHash.put("parameter", parameter);
				paramHash.put("user-id-list", "klemens,michael,stefan");
				parameters = OpenTMSWebServiceResult.jsonSerialise(paramHash);
				result = openTMSService.synchronize(parameters);
				System.out.println("result: " + result);
				System.out.println(OpenTMSWebServiceResult.deserializeToString(result));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			try
			{
				paramHash.put("method", "query");
				paramHash.put("user-id", "gerhard");
				paramHash.put("password", "password");
				paramHash.put("name", "");
				String parameter = "";
				paramHash.put("parameter", parameter);
				paramHash.put("user-id-list", "klemens,michael,stefan");
				parameters = OpenTMSWebServiceResult.jsonSerialise(paramHash);
				result = openTMSService.synchronize(parameters);
				System.out.println("result: " + result);
				System.out.println(OpenTMSWebServiceResult.deserializeToString(result));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			try
			{
				paramHash.put("method", "query");
				paramHash.put("user-id", "gerhard");
				paramHash.put("password", "password");
				paramHash.put("name", "mysqlsynctest");
				String parameter = "";
				paramHash.put("parameter", parameter);
				paramHash.put("user-id-list", "klemens,michael,stefan");
				parameters = OpenTMSWebServiceResult.jsonSerialise(paramHash);
				result = openTMSService.synchronize(parameters);
				System.out.println("result: " + result);
				System.out.println(OpenTMSWebServiceResult.deserializeToString(result));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			try
			{
				paramHash.put("method", "unknown");
				paramHash.put("user-id", "");
				paramHash.put("name", "xxxx");
				String parameter = "";
				paramHash.put("parameter", parameter);
				paramHash.put("user-id-list", "klemens,michael,stefan");
				parameters = OpenTMSWebServiceResult.jsonSerialise(paramHash);
				result = openTMSService.synchronize(parameters);
				System.out.println("result: " + result);
				System.out.println(OpenTMSWebServiceResult.deserializeToString(result));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			try
			{
				paramHash.put("method", "download");
				paramHash.put("user-id", "klemens");
				paramHash.put("password", "blabla");
				paramHash.put("name", "mssqlsynctest");
				paramHash.put("parameter", "empty");
				// paramHash.put("user-id-list", "klemens,michael,stefan");
				// paramHash.put("encoding", "BASE64");
				paramHash.put("update-counter", updateCounter);
				parameters = OpenTMSWebServiceResult.jsonSerialise(paramHash);
				result = openTMSService.synchronize(parameters);
				System.out.println("result: " + result);
				System.out.println(OpenTMSWebServiceResult.deserializeToString(result));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			try
			{
				updateCounter = "";
				updateCounter = "1324537000000";
				paramHash.put("method", "download");
				paramHash.put("user-id", "klemens");
				paramHash.put("password", "blabla");
				paramHash.put("name", "mysqlclient");
				paramHash.put("parameter", "empty");
				// paramHash.put("user-id-list", "klemens,michael,stefan");
				paramHash.put("update-counter", updateCounter);
				parameters = OpenTMSWebServiceResult.jsonSerialise(paramHash);
				result = openTMSService.synchronize(parameters);
				System.out.println("result: " + result);
				System.out.println(OpenTMSWebServiceResult.deserializeToString(result));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			try
			{
				String tmx_document = "<tmx version=\"1.4\"><header srclang=\"en-US\" datatype=\"html \"></header><body><tu tuid=\"1\" datatype=\"html\" creationdate= \"20000310T091706Z\"><prop type=\"tmgr-segNum\">165</prop><prop type= \"tmgr-markup\">EQFHTML4</prop><prop type=\"tmgr-docname \">SHOWME.WP</prop><tuv xml:lang=\"en-US\"><prop type=\"tmgr-language \">English(U.S.)</prop><seg>A small test sentence.</seg></tuv><tuv xml:lang=\"de-DE\"><prop type=\"tmgr-language\">German (Reform)</prop><seg>Ein kleiner Testsatz.</seg></tuv></tu></body></tmx>";
				paramHash.put("method", "upload");
				paramHash.put("user-id", "klemens");
				paramHash.put("password", "blabla");
				paramHash.put("name", "mysqlclient");
				paramHash.put("parameter", "empty");
				// paramHash.put("user-id-list", "klemens,michael,stefan");
				paramHash.put("tmx-document", tmx_document);
				paramHash.put("encoding", "BASE64");
				paramHash.put("update-counter", "");
				parameters = OpenTMSWebServiceResult.jsonSerialise(paramHash);
				result = openTMSService.synchronize(parameters);
				System.out.println("result: " + result);
				System.out.println(OpenTMSWebServiceResult.deserializeToString(result));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			try
			{
				String tmx_document = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><tmx version=\"1.4\"><header srclang=\"en-US\" datatype=\"html \"></header><body><tu tuid=\"1\" datatype=\"html\" creationdate= \"20000310T091706Z\"><prop type=\"tmgr-segNum\">165</prop><prop type= \"tmgr-markup\">EQFHTML4</prop><prop type=\"tmgr-docname \">SHOWME.WP</prop><tuv xml:lang=\"en-US\"><prop type=\"tmgr-language \">English(U.S.)</prop><seg>A small test sentence.</seg></tuv><tuv xml:lang=\"de-DE\"><prop type=\"tmgr-language\">German (Reform)</prop><seg>Ein kleiner Testsatz.</seg></tuv></tu></body></tmx>";
				paramHash.put("method", "upload");
				paramHash.put("user-id", "klemens");
				paramHash.put("password", "blabla");
				paramHash.put("name", "mysqlclient");
				paramHash.put("parameter", "empty");
				// paramHash.put("user-id-list", "klemens,michael,stefan");
				paramHash.put("tmx-document", tmx_document);
				paramHash.put("encoding", "UTF-8");
				paramHash.put("update-counter", "");
				parameters = OpenTMSWebServiceResult.jsonSerialise(paramHash);
				result = openTMSService.synchronize(parameters);
				System.out.println("result: " + result);
				System.out.println(OpenTMSWebServiceResult.deserializeToString(result));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		if (testMode.equals("test5"))
		{
			try
			{
				paramHash.put("method", "download");
				paramHash.put("user-id", "klemens");
				paramHash.put("password", "blabla");
				paramHash.put("name", "opentm2test");
				paramHash.put("parameter", "empty");
				paramHash.put("user-id-list", "klemens,michael,stefan");
				parameters = OpenTMSWebServiceResult.jsonSerialise(paramHash);
				result = openTMSService.synchronize(parameters);
				System.out.println("result: " + result);
				System.out.println(OpenTMSWebServiceResult.deserializeToString(result));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			try
			{
				paramHash.put("method", "query");
				paramHash.put("user-id", "klemens");
				paramHash.put("password", "blabla");
				paramHash.put("name", "opentm2test");
				String parameter = "";
				paramHash.put("parameter", parameter);
				paramHash.put("user-id-list", "klemens,michael,stefan");
				parameters = OpenTMSWebServiceResult.jsonSerialise(paramHash);
				result = openTMSService.synchronize(parameters);
				System.out.println("result: " + result);
				System.out.println(OpenTMSWebServiceResult.deserializeToString(result));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			try
			{
				paramHash.put("method", "blabl");
				paramHash.put("user-id", "klemens");
				paramHash.put("password", "blabla");
				paramHash.put("name", "opentm2test");
				paramHash.put("parameter", "empty");
				paramHash.put("user-id-list", "klemens,michael,stefan");
				parameters = OpenTMSWebServiceResult.jsonSerialise(paramHash);
				result = openTMSService.synchronize(parameters);
				System.out.println("result: " + result);
				System.out.println(OpenTMSWebServiceResult.deserializeToString(result));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

		}

	}
}
