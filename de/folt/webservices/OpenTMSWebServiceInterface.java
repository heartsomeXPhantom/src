/*
 * Created on 04.04.2011
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.webservices;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

/**
 * @author klemens
 * 
 *         To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and Comments
 */
@WebService(name = "OpenTMS", targetNamespace = "http://de.folt.webservices/")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface OpenTMSWebServiceInterface
{
	@WebMethod
	@WebResult(partName = "return")
	public boolean bExistsDataSource(@WebParam(name = "dataSourceName", partName = "dataSourceName") String arg0);

	/**
	 * 
	 * @param arg1
	 * @param arg0
	 * @return returns long
	 */
	@WebMethod
	@WebResult(partName = "return")
	public String getDataSources(@WebParam(name = "model", partName = "model") String model);

	@WebMethod
	@WebResult(partName = "return")
	public String getLanguages();

	@WebMethod
	@WebResult(partName = "return")
	public String getMonolingualObject(@WebParam(name = "id", partName = "id") String id);

	@WebMethod
	@WebResult(partName = "return")
	public String getMultilingualObject(@WebParam(name = "id", partName = "id") String id);

	@WebMethod
	@WebResult(partName = "return")
	public String shutdown(@WebParam(name = "user", partName = "user") String user,
			@WebParam(name = "password", partName = "password") String password);

	/**
	 * synchronize This interface describes the openTMS synchronisation web services
	 * 
	 * @param parameters
	 *            this is a json formatted string with all parameters; main param is "method" which specified the method to be applied
	 * @return result as JSON string
	 */
	@WebMethod
	@WebResult(partName = "return")
	public String synchronize(@WebParam(name = "parameters", partName = "parameters") String parameters);

	@WebMethod
	@WebResult(partName = "return")
	public String translate(@WebParam(name = "sourceSegment", partName = "sourceSegment") String sourceSegment,
			@WebParam(name = "sourceLanguage", partName = "sourceLanguage") String sourceLanguage,
			@WebParam(name = "targetLanguage", partName = "targetLanguage") String targetLanguage,
			@WebParam(name = "datasource", partName = "datasource") String datasource,
			@WebParam(name = "matchSimilarity", partName = "matchSimilarity") int matchSimilarity,
			@WebParam(name = "type", partName = "type") String type);

}
