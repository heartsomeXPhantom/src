package de.folt.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Path("/opentms")
public class OpenTMSResource
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub

	}

	// The Java method will process plain GET requests
	@GET
	// The Java method will produce content identified by the MIME Media
	// type "text/plain"
	@Produces("text/plain")
	public String openTMSSelectorText(@QueryParam("method") String method, @QueryParam("parameter") String parameter)
	{
		return "openTMS Rest Webserver\nMethod=" + method + "\nParameter=" + parameter;
	}
	
	// The Java method will process HTTP GET requests
	@GET
	// The Java method will produce content identified by the MIME Media
	// type "text/plain"
	@Produces("text/html")
	public String openTMSSelectorHTML(@QueryParam("method") String method, @QueryParam("parameter") String parameter)
	{
		return "openTMS Rest Webserver<br>Method=" + method + "<br>Parameter=" + parameter;
	}
	
	// The Java method will process xml GET requests
	@GET
	// The Java method will produce content identified by the MIME Media
	// type "application/xml"
	@Produces("application/xml")
	public String openTMSSelectorXML(@QueryParam("method") String method, @QueryParam("parameter") String parameter)
	{
		return "<text><message>openTMS Rest Webserver<message><method>" + method + "</method><parameter>" + parameter+ "</parameter><text>";
	}
	
	// The Java method will process HTTP GET requests
	@GET
	@Path("/more")
	// The Java method will produce content identified by the MIME Media
	// type "text/plain"
	@Produces("text/html")
	public String openTMSSelectorHTML2(@QueryParam("method") String method, @QueryParam("parameter") String parameter)
	{
		return "openTMS Rest Webserver Method 2 HTML<br>Method=" + method + "<br>Parameter=" + parameter;
	}
	
	// The Java method will process xml GET requests
	@GET
	@Path("/more")
	// The Java method will produce content identified by the MIME Media
	// type "application/xml"
	@Produces("application/xml")
	public String openTMSSelectorXML2(@QueryParam("method") String method, @QueryParam("parameter") String parameter)
	{
		return "<text><message>openTMS Rest Webserver Method 2 XML<message><method>" + method + "</method><parameter>" + parameter+ "</parameter><text>";
	}
}
