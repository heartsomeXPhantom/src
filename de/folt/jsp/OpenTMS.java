/*
 * Created on 13.06.2010
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.jsp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import de.folt.models.datamodel.DataSource;
import de.folt.models.datamodel.DataSourceInstance;
import de.folt.models.datamodel.MonoLingualObject;
import de.folt.models.datamodel.MultiLingualObject;
import de.folt.models.datamodel.TranslationCheckResult;
import de.folt.models.documentmodel.xliff.XliffDocument;
import de.folt.util.OpenTMSException;

/**
 * @author klemens
 * 
 *         To change the template for this generated type comment go to Window -
 *         Preferences - Java - Code Generation - Code and Comments
 */
public class OpenTMS extends TagSupport
{

	/**
     * 
     */
	private static final long serialVersionUID = -942949584266003088L;

	private String datasource = "";

	private String dataSourceConfigurationsFile;

	private String[] dataSources;

	private String logfile;

	private String opmethod = "";

	private String properties;

	private String propfile;

	private String similarity;

	private String sourceLanguage;

	private String targetLanguage;

	private String sourceSegment;

	private String targetSegment;

	private String javaScript;

	private String id;

	private URL logFileUrl = null;

	/**
	 * @return the id
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id)
	{
		this.id = id;
	}

	protected XMLOutputter xmlOutputter = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.jsp.tagext.TagSupport#doAfterBody()
	 */
	@Override
	public int doAfterBody() throws JspException
	{
		// TODO Auto-generated method stub
		return super.doAfterBody();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.jsp.tagext.TagSupport#doEndTag()
	 */
	@Override
	public int doEndTag() throws JspException
	{
		// TODO Auto-generated method stub
		return super.doEndTag();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.jsp.tagext.TagSupport#doStartTag()
	 */
	@Override
	public int doStartTag() throws JspException
	{
		try
		{
			setBasicOpenTMSVariables();

			if (this.getOpmethod().equals("getOpenTMSDataSources"))
			{
				getOpenTMSDataSources();

			}
			else if (this.getOpmethod().equals("translateWithOpenTMS"))
			{
				translateOpenTMS();
			}
			else if (this.getOpmethod().equals("saveToOpenTMS"))
			{
				saveToOpenTMS();
			}
			else if (this.getOpmethod().equals("getLogFile"))
			{
				getLogFileName();
			}
			else if (this.getOpmethod().equals("persistDataSource"))
			{
				persistDataSource();
			}
		}
		catch (Exception ioe)
		{
			System.out.println("Error in openTM: " + ioe);
			ioe.printStackTrace();
			JspWriter out = pageContext.getOut();
			try
			{
				out.print("Error in openTMS: " + ioe);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return (SKIP_BODY); // EVAL_BODY_INCLUDE
	}

	/**
	 * persistDataSource
	 */
	private void persistDataSource()
	{
		try
		{
			pageContext.getRequest().setCharacterEncoding("UTF-8");
			DataSource datasourceinstance = DataSourceInstance.createInstance(this.getDatasource());
			boolean bSuccess = datasourceinstance.bPersist();
			System.out.print("persistDataSource=" + bSuccess);
			String transResString = "Action";
			String param = "persistDataSource: " + bSuccess;
			pageContext.getRequest().setAttribute(transResString, param);
			pageContext.setAttribute(transResString, param);
		}
		catch (UnsupportedEncodingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			String transResString = "Action";
			String param = "persistDataSource failure: " + false;
			pageContext.getRequest().setAttribute(transResString, param);
			pageContext.setAttribute(transResString, param);
			return;
		}
		catch (OpenTMSException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			String transResString = "Action";
			String param = "persistDataSource failure: " + false;
			pageContext.getRequest().setAttribute(transResString, param);
			pageContext.setAttribute(transResString, param);
			return;
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			String transResString = "Action";
			String param = "persistDataSource general failure: " + false;
			pageContext.getRequest().setAttribute(transResString, param);
			pageContext.setAttribute(transResString, param);
			return;
		}
	}

	/**
	 * getLogFileName
	 */
	private void getLogFileName()
	{
		try
		{

			pageContext.getRequest().setAttribute("logfile", de.folt.util.OpenTMSLogger.returnLogFile());
			pageContext.setAttribute("logfile", de.folt.util.OpenTMSLogger.returnLogFile());
			pageContext.getRequest().setAttribute("urllogfile", this.logFileUrl.toExternalForm());
			pageContext.setAttribute("urllogfile", this.logFileUrl.toExternalForm());
		}
		catch (Exception e)
		{
			pageContext.getRequest().setAttribute("logfile", "Error retrieving log file name");
			pageContext.setAttribute("logfile", "Error retrieving log file name");
			e.printStackTrace();
		}
		return;
	}

	public String elementContentToString(Element element)
	{
		String str = elementToString(element);
		str = str.replaceAll("[\t\n\r ]*$", "");
		str = str.replaceAll("^[\t\n\r ]*", "");
		String elementname = element.getName();
		str = str.replaceFirst("^<" + elementname + ".*?>", "");
		str = str.replaceAll("</" + elementname + ".*?>$", "");
		return str;
	}

	public String elementToString(Element element)
	{
		String str = "";
		try
		{
			str = "";
			StringWriter strwriter = new StringWriter();
			if (xmlOutputter == null)
				xmlOutputter = new XMLOutputter();
			xmlOutputter.output(element, strwriter);
			str = strwriter.toString();
			strwriter.close();
			strwriter = null;
			return str;
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return str;
	}

	/**
	 * getAltTrans
	 * 
	 * @param iNumber
	 * @return
	 */
	public String getAltTrans(Element transUnit, String javaScript)
	{
		XliffFile xl = new XliffFile();
		return xl.formatAltTransAsTable(transUnit, javaScript);
	}

	/**
	 * @return the datasource
	 */
	public String getDatasource()
	{
		return datasource;
	}

	/**
	 * @return the dataSourceConfigurationsFile
	 */
	public String getDataSourceConfigurationsFile()
	{
		return dataSourceConfigurationsFile;
	}

	/**
	 * @return the dataSources
	 */
	public String[] getDataSources()
	{
		return dataSources;
	}

	/**
	 * @return the javaScript
	 */
	public String getJavaScript()
	{
		return javaScript;
	}

	/**
	 * @return the logfile
	 */
	public String getLogfile()
	{
		return logfile;
	}

	private void getOpenTMSDataSources()
	{
		Vector<String> vecsources = de.folt.models.datamodel.DataSourceConfigurations.getOpenTMSDatabases();
		dataSources = null;
		if (vecsources != null)
		{
			dataSources = new String[vecsources.size()];
			for (int i = 0; i < vecsources.size(); i++)
			{
				dataSources[i] = vecsources.get(i);
			}
			pageContext.getRequest().setAttribute("dataSources", dataSources);
			pageContext.setAttribute("dataSources", dataSources);
		}
	}

	/**
	 * @return the opmethod
	 */
	public String getOpmethod()
	{
		return opmethod;
	}

	/**
	 * @return the properties
	 */
	public String getProperties()
	{
		return properties;
	}

	/**
	 * @return the propfile
	 */
	public String getPropfile()
	{
		return propfile;
	}

	/**
	 * @return the similarity
	 */
	public String getSimilarity()
	{
		return similarity;
	}

	/**
	 * @return the sourceLanguage
	 */
	public String getSourceLanguage()
	{
		return sourceLanguage;
	}

	/**
	 * @return the sourceSegment
	 */
	public String getSourceSegment()
	{
		return sourceSegment;
	}

	/**
	 * @return the targetLanguage
	 */
	public String getTargetLanguage()
	{
		return targetLanguage;
	}

	/**
	 * @return the targetSegment
	 */
	public String getTargetSegment()
	{
		return targetSegment;
	}

	/**
	 * saveToOpenTMS
	 */
	private void saveToOpenTMS() throws Exception
	{
		try
		{
			pageContext.getRequest().setCharacterEncoding("UTF-8");
			DataSource datasourceinstance = DataSourceInstance.createInstance(this.getDatasource());

			TranslationCheckResult translationCheckResult = datasourceinstance.checkIfTranslationExistsInDataSource(this.getSourceSegment(), this
					.getSourceLanguage(), this.getTargetSegment(), this.getTargetLanguage());
			@SuppressWarnings("unused")
			boolean bSourceFound = translationCheckResult.getStatus().equals(TranslationCheckResult.TranslationCheckStatus.SOURCEFOUND);
			@SuppressWarnings("unused")
			boolean bTargetFound = translationCheckResult.getStatus().equals(TranslationCheckResult.TranslationCheckStatus.TARGETFOUND);
			@SuppressWarnings("unused")
			boolean bSourceTargetFound = translationCheckResult.getStatus()
					.equals(TranslationCheckResult.TranslationCheckStatus.SOURCEANDTARGETFOUND);

			String transResString = "TranslationCheckStatus";
			String param = translationCheckResult.getStatus() + " / " + this.getDatasource() + " / " + sourceLanguage + " / " + targetLanguage;
			pageContext.getRequest().setAttribute(transResString, param);
			pageContext.setAttribute(transResString, param);

			de.folt.util.OpenTMSLogger.println(this.getDatasource() + "/ " + sourceLanguage + " / " + this.getSourceSegment() + " / "
					+ targetLanguage + " / " + this.getTargetSegment());

			if (this.getSourceSegment() == null)
			{
				transResString = "Action";
				param = "SourceAndTargetNotSaved; source segment null";
				pageContext.getRequest().setAttribute(transResString, param);
				pageContext.setAttribute(transResString, param);
				return;
			}
			if (this.getTargetSegment() == null)
			{
				transResString = "Action";
				param = "SourceAndTargetNotSaved; target segment null";
				pageContext.getRequest().setAttribute(transResString, param);
				pageContext.setAttribute(transResString, param);
				return;
			}

			if (translationCheckResult.getSourceAndTargetSegmentMatches().size() > 0)
			{
				transResString = "Action";
				param = "SourceAndTargetFoundInOneMultiLingualObject";
				pageContext.getRequest().setAttribute(transResString, param);
				pageContext.setAttribute(transResString, param);
				return;
			}

			MultiLingualObject multi = new MultiLingualObject();
			MonoLingualObject sourceMono = new MonoLingualObject(this.getSourceSegment(), sourceLanguage);
			MonoLingualObject targetMono = new MonoLingualObject(this.getTargetSegment(), targetLanguage);
			multi.addMonoLingualObject(sourceMono);
			multi.addMonoLingualObject(targetMono);
			boolean bSaved = datasourceinstance.addMultiLingualObject(multi, true);
			if (bSaved)
			{
				transResString = "Action";
				param = "SourceAndTargetSaved";
				pageContext.getRequest().setAttribute(transResString, param);
				pageContext.setAttribute(transResString, param);
			}
			else
			{
				transResString = "Action";
				param = "SourceAndTargetNotSaved";
				pageContext.getRequest().setAttribute(transResString, param);
				pageContext.setAttribute(transResString, param);
			}
		}
		catch (Exception ioe)
		{
			String transResString = "Action";
			String param = "Error in saving translation" + " / " + this.getSourceSegment() + " / " + this.getDatasource() + " / " + sourceLanguage
					+ " / " + targetLanguage;
			pageContext.getRequest().setAttribute(transResString, param);
			pageContext.setAttribute(transResString, param);
			throw ioe;
		}

	}

	@SuppressWarnings("deprecation")
	private void setBasicOpenTMSVariables()
	{

		logfile = de.folt.util.OpenTMSLogger.returnLogFile();
		// do not create one if the log file exists in the current session/page
		// context
		if ((logfile == null) || (logfile.equals("")))
			logfile = de.folt.util.OpenTMSSupportFunctions.getCurrentUser() + "." + de.folt.util.OpenTMSSupportFunctions.getDateStringFine() + ".log";
		File f = new File(logfile);
		if (f != null)
		{
			try
			{
				logfile = f.getCanonicalPath();
				logFileUrl = f.toURL(); // new URL(saveFile);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		de.folt.util.OpenTMSLogger.setLogFile(logfile);
		logfile = de.folt.util.OpenTMSLogger.returnLogFile();

		if (logfile != null)
		{
			// out.print("<br /> logfile  : " + logfile);
			pageContext.getRequest().setAttribute("logfile", logfile);
			pageContext.setAttribute("logfile", logfile);
		}
		propfile = de.folt.util.OpenTMSProperties.getPropfileName();
		f = new File(propfile);
		if (f != null)
		{
			try
			{
				propfile = f.getCanonicalPath();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (propfile != null)
		{
			pageContext.getRequest().setAttribute("propfile", propfile);
			pageContext.setAttribute("propfile", propfile);
		}

		de.folt.util.OpenTMSProperties.getInstance(propfile);
		if (de.folt.util.OpenTMSProperties.getInstance() != null)
		{
			properties = de.folt.util.OpenTMSProperties.getInstance().getOpenTMSPropertiesAsString();
			pageContext.getRequest().setAttribute("propertiesString", properties);
			pageContext.setAttribute("propertiesString", properties);
		}

		dataSourceConfigurationsFile = de.folt.util.OpenTMSProperties.getInstance().getOpenTMSProperty("dataSourceConfigurationsFile");

		pageContext.getRequest().setAttribute("dataSourceConfigurationsFile", dataSourceConfigurationsFile);
		pageContext.setAttribute("dataSourceConfigurationsFile", dataSourceConfigurationsFile);

	}

	/**
	 * @param datasource
	 *            the datasource to set
	 */
	public void setDatasource(String datasource)
	{
		this.datasource = datasource;
	}

	/**
	 * @param dataSourceConfigurationsFile
	 *            the dataSourceConfigurationsFile to set
	 */
	public void setDataSourceConfigurationsFile(String dataSourceConfigurationsFile)
	{
		this.dataSourceConfigurationsFile = dataSourceConfigurationsFile;
	}

	/**
	 * @param dataSources
	 *            the dataSources to set
	 */
	public void setDataSources(String[] dataSources)
	{
		this.dataSources = dataSources;
	}

	/**
	 * @param javaScript
	 *            the javaScript to set
	 */
	public void setJavaScript(String javaScript)
	{
		this.javaScript = javaScript;
	}

	/**
	 * @param logfile
	 *            the logfile to set
	 */
	public void setLogfile(String logfile)
	{
		this.logfile = logfile;
	}

	/**
	 * @param opmethod
	 *            the opmethod to set
	 */
	public void setOpmethod(String opmethod)
	{
		this.opmethod = opmethod;
	}

	/**
	 * @param properties
	 *            the properties to set
	 */
	public void setProperties(String properties)
	{
		this.properties = properties;
	}

	/**
	 * @param propfile
	 *            the propfile to set
	 */
	public void setPropfile(String propfile)
	{
		this.propfile = propfile;
	}

	/**
	 * @param similarity
	 *            the similarity to set
	 */
	public void setSimilarity(String similarity)
	{
		this.similarity = similarity;
	}

	/**
	 * @param sourceLanguage
	 *            the sourceLanguage to set
	 */
	public void setSourceLanguage(String sourceLanguage)
	{
		this.sourceLanguage = sourceLanguage;
	}

	/**
	 * @param sourceSegment
	 *            the sourceSegment to set
	 */
	public void setSourceSegment(String sourceSegment)
	{
		this.sourceSegment = sourceSegment;
	}

	/**
	 * @param targetLanguage
	 *            the targetLanguage to set
	 */
	public void setTargetLanguage(String targetLanguage)
	{
		this.targetLanguage = targetLanguage;
	}

	/**
	 * @param targetSegment
	 *            the targetSegment to set
	 */
	public void setTargetSegment(String targetSegment)
	{
		this.targetSegment = targetSegment;
	}

	private void translateOpenTMS() throws Exception
	{
		try
		{
			pageContext.getRequest().setCharacterEncoding("UTF-8");
			DataSource datasourceinstance = DataSourceInstance.createInstance(this.getDatasource());
			int iSimilarity = Integer.parseInt(this.getSimilarity());
			Hashtable<String, Object> transParam = new Hashtable<String, Object>();
			transParam.put("ignoreApproveAttribute", "yes");
			XliffDocument xliffDocument = new XliffDocument();
			xliffDocument.loadXmlFile("empty.xlf"); // a dummy we need
			Element translationUnit = new Element("trans-unit");
			if (this.getId() != null)
			{
				translationUnit.setAttribute("id", this.getId());
				translationUnit.setAttribute("help-id", "h" + this.getId());
			}
			else
			{
				translationUnit.setAttribute("id", "genTranslateOpenTMS");
				translationUnit.setAttribute("help-id", "hgenTranslateOpenTMS");
			}
			Element source = new Element("source");
			source.setText(this.getSourceSegment());
			translationUnit.addContent(source);
			Element propGroup = new Element("prop-group");
			Element prop = new Element("prop");
			prop.setAttribute("SCA", "jsp:translateOpenTMS");
			prop.setAttribute("SMD", System.getProperty("user.name"));
			prop.setAttribute("SCD", de.folt.util.OpenTMSSupportFunctions.getDateString());
			prop.setAttribute("SMD", de.folt.util.OpenTMSSupportFunctions.getDateString());
			propGroup.addContent(prop);
			translationUnit.addContent(propGroup);
			Element transunit = datasourceinstance.translate(translationUnit, xliffDocument.getFiles().get(0), xliffDocument, sourceLanguage,
					targetLanguage, iSimilarity, transParam);
			// String transUnit =
			// xliffDocument.elementContentToString(transunit);
			String myJavaScript = this.getJavaScript();
			if ((myJavaScript == null) || myJavaScript.equals(""))
				myJavaScript = "return false;";
			String transUnit = getAltTrans(transunit, myJavaScript);
			pageContext.getRequest().setAttribute("transUnit", transUnit);
			pageContext.setAttribute("transUnit", transUnit);
			// de.folt.util.OpenTMSLogger.println(this.getSourceSegment() + "/ "
			// + this.getDatasource() + " / " + sourceLanguage + " / " +
			// targetLanguage);
		}
		catch (Exception ioe)
		{
			pageContext.getRequest().setAttribute(
					"transUnit",
					"Error in translation" + " / " + this.getSourceSegment() + " / " + this.getDatasource() + " / " + sourceLanguage + " / "
							+ targetLanguage);
			pageContext.setAttribute("transUnit", "Error in translation" + " / " + this.getSourceSegment() + " / " + this.getDatasource() + " / "
					+ sourceLanguage + " / " + targetLanguage);
			throw ioe;
		}
	}

	/**
	 * @param logFileUrl
	 *            the logFileUrl to set
	 */
	public void setLogFileUrl(URL logFileUrl)
	{
		this.logFileUrl = logFileUrl;
	}

	/**
	 * @return the logFileUrl
	 */
	public URL getLogFileUrl()
	{
		return logFileUrl;
	}
}
