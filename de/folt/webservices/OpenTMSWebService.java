package de.folt.webservices;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;

/**
 * This class was generated by the JAX-WS RI. JAX-WS RI 2.1.1 in JDK 6 Generated
 * source version: 2.1
 * 
 */
@WebServiceClient(name = "OpenTMS", targetNamespace = "http://webservices.folt.de/", wsdlLocation = "http://localhost:8082/openTMS?wsdl")
public class OpenTMSWebService extends Service
{

	private static OpenTMSWebServiceConstants OPENTMSWEBSERVICECONSTANTS = new OpenTMSWebServiceConstants();

	private static URL getWSDL_LOCATION()
	{
		URL url = null;
		try
		{
			url = new URL(OPENTMSWEBSERVICECONSTANTS.openTMSWebServerWSDL);
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		return url;
	}

	/**
	 * getWSDL_LOCATION
	 * 
	 * @param openTMSWebServiceConstants2
	 * @return
	 */
	private static URL getWSDL_LOCATION(
			OpenTMSWebServiceConstants openTMSWebServiceConstants)
	{
		String urlstring = openTMSWebServiceConstants.getOpenTMSWebServerWSDL();
		URL url = null;
		try
		{
			url = new URL(urlstring);
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		return url;

	}

	private OpenTMSWebServiceConstants openTMSWebServiceConstants;

	public OpenTMSWebService()
	{
		super(getWSDL_LOCATION(), new QName(
				OPENTMSWEBSERVICECONSTANTS.getOpenTMSWebServerNameSpace(),
				"OpenTMSWebServiceImplementationService"));
	}

	public OpenTMSWebService(
			OpenTMSWebServiceConstants openTMSWebServiceConstants)
	{
		super(getWSDL_LOCATION(openTMSWebServiceConstants), new QName(
				openTMSWebServiceConstants.getOpenTMSWebServerNameSpace(),
				"OpenTMSWebServiceImplementationService"));
		this.openTMSWebServiceConstants = openTMSWebServiceConstants;
		String message = openTMSWebServiceConstants.openTMSWebServerURL + "\n"
				+ openTMSWebServiceConstants.openTMSWebServerNameSpace + "\n"
				+ openTMSWebServiceConstants.openTMSWebServerService + "\n"
				+ openTMSWebServiceConstants.openTMSWebServerWSDL;
		System.out.println("openTMS Web Service found with parameters:\n"
				+ message);
	}

	public OpenTMSWebService(URL wsdlLocation, QName serviceName)
	{
		super(wsdlLocation, serviceName);
	}

	/**
	 * 
	 * @return returns Calculator
	 */
	@WebEndpoint(name = "OpenTMSPort")
	public OpenTMSWebServiceInterface getOpenTMSPort()
	{
		QName qName = new QName(
				getOpenTMSWebServiceConstants().openTMSWebServerNameSpace,
				"OpenTMSPort");
		OpenTMSWebServiceInterface openTMSWebServiceInterface = (OpenTMSWebServiceInterface) super.getPort(qName,
				OpenTMSWebServiceInterface.class);
		return openTMSWebServiceInterface;
	}

	/**
	 * 
	 * @param features
	 *            A list of {@link javax.xml.ws.WebServiceFeature} to configure
	 *            on the proxy. Supported features not in the
	 *            <code>features</code> parameter will have their default
	 *            values.
	 * @return returns Calculator
	 */
	@WebEndpoint(name = "OpenTMSPort")
	public OpenTMSWebServiceImplementation getOpenTMSPort(
			WebServiceFeature... features)
	{
		return (OpenTMSWebServiceImplementation) super
				.getPort(
						new QName(
								getOpenTMSWebServiceConstants().openTMSWebServerNameSpace,
								"OpenTMSPort"),
						OpenTMSWebServiceImplementation.class, features);
	}

	/**
	 * @return the openTMSWebServiceConstants
	 */
	public OpenTMSWebServiceConstants getOpenTMSWebServiceConstants()
	{
		return openTMSWebServiceConstants;
	}

	/**
	 * @param openTMSWebServiceConstants
	 *            the openTMSWebServiceConstants to set
	 */
	public void setOpenTMSWebServiceConstants(
			OpenTMSWebServiceConstants openTMSWebServiceConstants)
	{
		this.openTMSWebServiceConstants = openTMSWebServiceConstants;
	}

}
