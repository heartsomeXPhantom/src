/*
 * Created on 22.03.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.models.applicationmodel.guimodel.editor.datasourceeditor;

import java.io.File;
import java.io.FilenameFilter;

import de.folt.util.OpenTMSProperties;

import org.hibernate.cfg.AnnotationConfiguration;

/**
 * This class contains several support functions for reading OpenTMs sql data sources.
 * @author klemens
 *
 */
public class OpenTMSDataSourceTypes
{

    /**
     * 
     */
    public OpenTMSDataSourceTypes()
    {
        super();
    }

    private String configFiles[] = null;

    /**
     * sqlDataSourceTypes gets all the SQL data source types supported by OpenTMS (OpenTMS.properties); read from getOpenTMSProperty("database.path") + "hibernate"<br>
     * hibernateConfigurationsDirectory=%OpenTMS.dir%/hibernate
     * @return a string array of the available SQL data source types of OpenTMS
     */
    public String[] sqlDataSourceTypes()
    {
        String directory = OpenTMSProperties.getInstance().getOpenTMSProperty("hibernateConfigurationsDirectory");
        File f = new File(directory);
        File[] filearray = f.listFiles(new FilenameFilter()
        {
            public boolean accept(File arg0, String arg1)
            {
                if (arg1.startsWith("hibernate.") && arg1.endsWith(".cfg.xml"))
                {
                    return true;
                }
                return false;
            }
        });

        if ((filearray != null) && (filearray.length > 0))
        {
            configFiles = new String[filearray.length];
            for (int i = 0; i < filearray.length; i++)
            {
                String name = filearray[i].getName();
                configFiles[i] = name;
            }
        }
        else
            configFiles = null;

        return configFiles;
    }

    /**
     * getPort get the port of an sql data source
     * @param dataSourceConfigurationsFile the data source configuration file (e.g. hibernate.progress.cfg.xml)<br>
     *  &lt;property name="OpenTMS.defaultport"&gt;5432&lt;/property&gt;
     * @return the port of the sql data source
     */
    public String getPort(String dataSourceConfigurationsFile)
    {
        String directory = OpenTMSProperties.getInstance().getOpenTMSProperty("hibernateConfigurationsDirectory");
        AnnotationConfiguration configuration = new AnnotationConfiguration();
        File f = new File(directory + "/" + dataSourceConfigurationsFile);
        if (!f.exists())
            return null;
        configuration.configure(f);

        String port = (String) configuration.getProperty("OpenTMS.defaultport");
        configuration = null;
        return port;
    }

    /**
     * getServer get the server of an sql data source (e.g. hibernate.progress.cfg.xml)<br>
     * &lt;property name="OpenTMS.defaultserver"&gt;localhost&lt;/property&gt;
     * @param dataSourceConfigurationsFile
     * @return the server of the sql data source
     */
    public String getServer(String dataSourceConfigurationsFile)
    {
        String directory = OpenTMSProperties.getInstance().getOpenTMSProperty("hibernateConfigurationsDirectory");
        AnnotationConfiguration configuration = new AnnotationConfiguration();
        File f = new File(directory + "/" + dataSourceConfigurationsFile);
        if (!f.exists())
            return null;
        configuration.configure(f);
        String server = (String) configuration.getProperty("OpenTMS.defaultserver");
        configuration = null;
        return server;
    }
}
