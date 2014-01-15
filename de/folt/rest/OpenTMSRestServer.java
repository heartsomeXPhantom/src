package de.folt.rest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.sun.grizzly.http.SelectorThread;
import com.sun.jersey.api.container.grizzly.GrizzlyWebContainerFactory;

public class OpenTMSRestServer
{

	private String baseUri = "http://localhost:9998/";
	private String packagename = "de.folt.rest";

	private String jerseypackage = "com.sun.jersey.config.property.packages";

	public static void main(String[] args) throws IOException
	{

		OpenTMSRestServer openTMSRestServer = new OpenTMSRestServer();

		if (args.length > 0)
			openTMSRestServer.baseUri = args[0];
		if (args.length > 1)
			openTMSRestServer.packagename = args[1];

		Map<String, String> initParams = new HashMap<String, String>();

		initParams.put(openTMSRestServer.jerseypackage,
				openTMSRestServer.packagename);

		System.out.println("Starting openTMS Rest Web Service...");
		SelectorThread threadSelector = GrizzlyWebContainerFactory.create(
				openTMSRestServer.baseUri, initParams);
		System.out.println(String.format(
				"Jersey app started with WADL available at %sapplication.wadl\n"
						+ "Try out %sopentms\nHit enter to stop it...",
				openTMSRestServer.baseUri, openTMSRestServer.baseUri));
		System.in.read();
		threadSelector.stopEndpoint();
		System.out.println("Terminated");
		System.exit(0);
	}
}
