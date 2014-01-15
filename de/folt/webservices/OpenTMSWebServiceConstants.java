package de.folt.webservices;

public class OpenTMSWebServiceConstants
{

	public String openTMSWebServerURL = "http://localhost:8082";

	public String openTMSWebServerNameSpace = "http://webservices.folt.de/";

	public String openTMSWebServerService = openTMSWebServerURL + "/openTMS";

	public String openTMSWebServerWSDL = openTMSWebServerService + "?wsdl";

	/**
	 * @return the openTMSWebServerNameSpace
	 */
	public String getOpenTMSWebServerNameSpace()
	{
		return openTMSWebServerNameSpace;
	}

	/**
	 * @return the openTMSWebServerService
	 */
	public String getOpenTMSWebServerService()
	{
		return openTMSWebServerService;
	}

	/**
	 * @return the openTMSWebServerURL
	 */
	public String getOpenTMSWebServerURL()
	{
		return openTMSWebServerURL;
	}

	/**
	 * @return the openTMSWebServerWSDL
	 */
	public String getOpenTMSWebServerWSDL()
	{
		return openTMSWebServerWSDL;
	}

	/**
	 * @param openTMSWebServerService
	 *            the openTMSWebServerService to set
	 */
	public void setOpenTMSWebServerService(String openTMSWebServerService)
	{
		this.openTMSWebServerService = openTMSWebServerService;
	}

	/**
	 * @param openTMSWebServerURL
	 *            the openTMSWebServerURL to set
	 */
	public void setOpenTMSWebServerURL(String openTMSWebServerURL)
	{
		this.openTMSWebServerURL = openTMSWebServerURL;
		openTMSWebServerService = openTMSWebServerURL + "/openTMS";
		openTMSWebServerWSDL = openTMSWebServerService + "?wsdl";
		// openTMSWebServerNameSpace = openTMSWebServerURL + "/";
	}

	/**
	 * @param openTMSWebServerWSDL
	 *            the openTMSWebServerWSDL to set
	 */
	public void setOpenTMSWebServerWSDL(String openTMSWebServerWSDL)
	{
		this.openTMSWebServerWSDL = openTMSWebServerWSDL;
	}

}
