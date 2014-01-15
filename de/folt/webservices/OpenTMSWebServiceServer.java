package de.folt.webservices;

import java.awt.HeadlessException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.xml.ws.Endpoint;

/**
 * Publishes OpenTMS as a Webservice
 * 
 * @author Klemens Waldhör
 */
public class OpenTMSWebServiceServer
{

	/**
	 * @param args
	 *            - controlling the server thru args<br>
	 *            arg[0] - the URL<br>
	 *            arg[1] - true if JoptionPane should be shown for control, false für stdin based control
	 */
	public static void main(String args[])
	{
		OpenTMSWebServiceServer server = null;
		if (args.length > 0)
		{
			String url = args[0];

			server = new OpenTMSWebServiceServer(url);
			if (args.length >= 2)
			{
				String withJOptionPane = args[1];
				if (withJOptionPane.equalsIgnoreCase("false"))
				{
					server.withJOptionPane = false;
				}
				else
				{
					server.withJOptionPane = true;
				}
			}
			server.createServer(url, true);
		}
		else
		{
			server = new OpenTMSWebServiceServer();

			server.createServer(true);
		}

	}

	private Endpoint						endpoint;

	private JFrame	frmOpt	= null;

	private OpenTMSWebServiceConstants		openTMSWebServiceConstants	= null;

	private OpenTMSWebServiceImplementation	server;

	private boolean							withJOptionPane				= true;

	/**
	 * 
	 */
	public OpenTMSWebServiceServer()
	{
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param url
	 */
	public OpenTMSWebServiceServer(String url)
	{
		// TODO Auto-generated constructor stub
	}

	public OpenTMSWebServiceImplementation createServer()
	{
		openTMSWebServiceConstants = new OpenTMSWebServiceConstants();
		return createServer(openTMSWebServiceConstants.openTMSWebServerService);
	}

	/**
	 * createServer create using the default URL
	 */
	public OpenTMSWebServiceImplementation createServer(boolean bStopDialogue)
	{
		openTMSWebServiceConstants = new OpenTMSWebServiceConstants();
		return createServer(openTMSWebServiceConstants.openTMSWebServerURL, bStopDialogue);
	}

	/**
	 * createServer create using the default URL
	 */
	public OpenTMSWebServiceImplementation createServer(String url)
	{
		return createServer(url, false);
	}

	/**
	 * @param url
	 *            the urls of the swerver
	 * @param bStopDialogue
	 *            true if created with stop dialogue
	 * @return
	 */
	public OpenTMSWebServiceImplementation createServer(String url, boolean bStopDialogue)
	{
		try
		{
			openTMSWebServiceConstants = new OpenTMSWebServiceConstants();
			openTMSWebServiceConstants.setOpenTMSWebServerURL(url);
			server = new OpenTMSWebServiceImplementation();

			System.out.println("openTMS WebServices Server start / with dialogue=" + bStopDialogue);
			System.out.println("openTMS WebServices log file=" + server.getLogfile());
			endpoint = Endpoint.publish(openTMSWebServiceConstants.getOpenTMSWebServerService(), server);
			System.out.println("openTMS WebServices Server started");
			String message = openTMSWebServiceConstants.openTMSWebServerURL + "\n"
					+ openTMSWebServiceConstants.openTMSWebServerNameSpace + "\n"
					+ openTMSWebServiceConstants.openTMSWebServerService + "\n"
					+ openTMSWebServiceConstants.openTMSWebServerWSDL;
			if (bStopDialogue)
			{
				if (withJOptionPane)
				{
					System.out.println("Stop OpenTMS Server with Button ok\n" + message);
					showDataSourceEditorMessage(null, message + "\n\nStop OpenTMS Web Service Server with Button OK\n", "OpenTMS Web Service Server", JOptionPane.OK_OPTION);
					endpoint.stop();
					System.out.println("OpenTMS Server shutdown / stopped");
				}
				else
				{
					System.out.println("Terminal: Stop OpenTMS Web Service Server with any input ok\n" + message);
					System.in.read();
					endpoint.stop();
					System.out.println("Terminal: OpenTMS Server shutdown / stopped");
				}
			}
			return server;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @return the endpoint
	 */
	public Endpoint getEndpoint()
	{
		return endpoint;
	}

	/**
	 * @return the openTMSWebServiceConstants
	 */
	public OpenTMSWebServiceConstants getOpenTMSWebServiceConstants()
	{
		return openTMSWebServiceConstants;
	}

	public OpenTMSWebServiceImplementation getServer()
	{
		return server;
	}

	public boolean isWithJOptionPane()
	{
		return withJOptionPane;
	}

	/**
	 * @param endpoint
	 *            the endpoint to set
	 */
	public void setEndpoint(Endpoint endpoint)
	{
		this.endpoint = endpoint;
	}

	public void setOpenTMSWebServiceConstants(OpenTMSWebServiceConstants openTMSWebServiceConstants)
	{
		this.openTMSWebServiceConstants = openTMSWebServiceConstants;
	}

	public void setServer(OpenTMSWebServiceImplementation server)
	{
		this.server = server;
	}

	public void setWithJOptionPane(boolean withJOptionPane)
	{
		this.withJOptionPane = withJOptionPane;
	}

	public void showDataSourceEditorMessage(JFrame jFrame, String message, String value, int messageType)
	{
		showDataSourceEditorMessage(message, value, messageType);
		return;
	}

	public void showDataSourceEditorMessage(String message, String value, int messageType)
	{
		if (frmOpt == null)
			frmOpt = new JFrame();
		frmOpt.setVisible(true);
		frmOpt.setLocation(300, 300);
		frmOpt.setAlwaysOnTop(true);
		frmOpt.setVisible(false);
		JOptionPane.showMessageDialog(frmOpt, message, value, messageType);
		frmOpt.setVisible(false);
		return;
	}

	public void shutdownServer()
	{
		try
		{
			endpoint.stop();
			System.out.println("openTMS WebServices Server shutdown");
		}
		catch (HeadlessException e)
		{
			e.printStackTrace();
		}
	}
}
