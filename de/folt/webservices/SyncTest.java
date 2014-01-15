package de.folt.webservices;

import java.util.HashMap;

public class SyncTest
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		
		SynchronizeService synchronizeService = new SynchronizeService();
		
		HashMap<String, String> paramHash = new HashMap<String, String>();
		
		String tmx_document = "d:/Program Files/OpenTMS/tmp/testreplace.tmx";
		tmx_document = de.folt.util.OpenTMSSupportFunctions.copyFileToString(tmx_document);
		String update_counter = "1325868064382";
		String name = "mysqlclient";
		String encoding = "BASE64";

		paramHash.put("name", name);
		paramHash.put("tmx-document", tmx_document);
		paramHash.put("inputDocumentType", "tmx");
		paramHash.put("dataSourceConfigurationsFile", "");
		paramHash.put("update-counter", update_counter);
		paramHash.put("inputDocumentType", "String");
		paramHash.put("sync", "upload");
		paramHash.put("encoding", encoding);
		paramHash = synchronizeService.upload(paramHash );
		
		tmx_document = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><tmx version=\"1.4\"><header srclang=\"en-US\" datatype=\"html \"></header><body><tu tuid=\"1\" datatype=\"html\" creationdate= \"20000310T091706Z\"><prop type=\"tmgr-segNum\">165</prop><prop type= \"tmgr-markup\">EQFHTML4</prop><prop type=\"tmgr-docname \">SHOWME.WP</prop><tuv xml:lang=\"en-US\"><prop type=\"tmgr-language \">English(U.S.)</prop><seg>A small test sentence.</seg></tuv><tuv xml:lang=\"de-DE\"><prop type=\"tmgr-language\">German (Reform)</prop><seg>Ein kleiner Testsatz.</seg></tuv></tu></body></tmx>";
		paramHash.put("method", "upload");
		paramHash.put("user-id", "klemens");
		paramHash.put("password", "blabla");
		paramHash.put("name", name);
		paramHash.put("parameter", "empty");
		// paramHash.put("user-id-list", "klemens,michael,stefan");
		paramHash.put("tmx-document", tmx_document);
		paramHash.put("encoding", "UTF-8");
		paramHash.put("update-counter", "");
		
		paramHash = synchronizeService.upload(paramHash );
		/* 
		parameters = OpenTMSWebServiceResult
				.jsonSerialise(paramHash);
		result = openTMSService.synchronize(parameters);
		System.out.println("result: " + result);
		System.out.println(OpenTMSWebServiceResult
				.deserializeToString(result));
				*/
	}

}
