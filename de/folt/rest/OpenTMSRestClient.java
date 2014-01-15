package de.folt.rest;

import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class OpenTMSRestClient
{

	private String baseUri = "http://localhost:9998/opentms";
	private String packagename = "de.folt.rest";

	private String jerseypackage = "com.sun.jersey.config.property.packages";
	
	public static void main(String[] args)
	{

		OpenTMSRestClient openTMSRestClient = new OpenTMSRestClient();
		Client client = Client.create();

		// URL des WebService
		WebResource wr = client
				.resource(openTMSRestClient.baseUri);

		// Parameter für WebService
		MultivaluedMap<String, String> params = new MultivaluedMapImpl();

		params.add("method", "datasources");
		params.add("parameter", "empty");

		String sr;
		try
		{
			sr = wr.queryParams(params)
					.accept(MediaType.APPLICATION_XML_TYPE)
					.get(new GenericType<String>()
					{
					});

			// Ergebnis ausgeben
			System.out.println(sr);

		}
		catch (UniformInterfaceException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try
		{
			sr = wr.queryParams(params)
					.accept(MediaType.TEXT_HTML_TYPE)
					.get(new GenericType<String>()
					{
					});
			System.out.println(sr);
		}
		catch (UniformInterfaceException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try
		{
			sr = wr.queryParams(params)
					.accept(MediaType.TEXT_PLAIN_TYPE)
					.get(new GenericType<String>()
					{
					});
			System.out.println(sr);
		}
		catch (UniformInterfaceException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
