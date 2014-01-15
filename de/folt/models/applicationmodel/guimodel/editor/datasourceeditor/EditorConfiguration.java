/*
 * Created on 26-nov-2003
 *
 */
package de.folt.models.applicationmodel.guimodel.editor.datasourceeditor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import de.folt.models.documentmodel.document.XmlDocument;
import de.folt.util.Messages;

/**
 * @author klemens
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class EditorConfiguration
{

    private String configFile = "";

    private Shell currentShell = null;

    private String currentUser = "";

    private XmlDocument doc;

    private Messages message = null;

    private Element root = null;

    private Element user = null;

    private String userLanguage = "en";

    /**
     * @param currentShell the shell - can be null; for error messages
     * @param configDirectory the directory where the configuration file should be stored
     * @param currentUser the user to create/read the configurations from
     */
    public EditorConfiguration(Shell currentShell, String configDirectory, String currentUser)
    {
        super();
        this.currentUser = adaptToAscii(currentUser);
        this.currentShell = currentShell;
        message = de.folt.util.Messages.getInstance("de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.datasourceeditor", userLanguage);
        configFile = adaptToAscii(configDirectory + "/" + "datasourceeditor." + currentUser + ".xml");
        user = this.getUserRoot();
    }
    
    /**
     * @param currentShell the shell - can be null; for error messages
     * @param configDirectory the directory where the configuration file should be stored
     * @param applicationName the name of the application
     * @param currentUser the user to create/read the configurations from
     */
    public EditorConfiguration(Shell currentShell, String configDirectory, String applicationName, String currentUser)
    {
        super();
        this.currentUser = adaptToAscii(currentUser);
        this.currentShell = currentShell;
        message = de.folt.util.Messages.getInstance("de.folt.models.applicationmodel.guimodel.editor.datasourceeditor.datasourceeditor", userLanguage);
        configFile = adaptToAscii(configDirectory + "/" + applicationName + "." + currentUser + ".xml");
        user = this.getUserRoot();
    }

    /**
     * adaptToAscii replaces non ASCII chars with "_" in the given string
     * 
     * @param string the string name to adapt
     * @return the adapted string
     */
    public String adaptToAscii(String string)
    {
        // String oldfilename = filename;
        // string = string.replaceAll(" ", "_");
        string = string.replaceAll("ö", "oe");
        string = string.replaceAll("ä", "ae");
        string = string.replaceAll("ü", "ue");
        string = string.replaceAll("Ö", "Oe");
        string = string.replaceAll("Ä", "Ae");
        string = string.replaceAll("Ü", "Ue");
        string = string.replaceAll("ß", "sz");

        // ok -need to replace all other non ascii characters
        for (int i = 0; i < string.length(); i++)
        {
            char ch = string.charAt(i);
            if ((ch >= 'a') && (ch <= 'z'))
                continue;
            if ((ch >= 'A') && (ch <= 'Z'))
                continue;
            if (ch == '_')
                continue;
            if (ch == ' ')
                continue;
            if (ch == '.')
                continue;
            if (ch == '-')
                continue;
            if ((ch >= '0') && (ch <= '9'))
                continue;
            if (ch == '/')
                continue;
            if (ch == '\\')
                continue;
            if (ch == ':')
                continue;
            if (ch == '[')
                continue;
            if (ch == ']')
                continue;
            if (ch == '(')
                continue;
            if (ch == ')')
                continue;
            string = string.replaceAll(ch + "", "_");
        }

        return string;
    }

    /**
     * createFile created the configuration file and adds user currentUser
     * @return
     */
    private Element createFile()
    {
        try
        {
            System.out.println("New configfile created:" + configFile);
            configFile = adaptToAscii(configFile);
            currentUser = adaptToAscii(currentUser);
            FileOutputStream write = null;
            OutputStreamWriter writer = null;
            // create the new file;
            File f = null;
            try
            {
                write = new FileOutputStream(configFile);
                writer = new OutputStreamWriter(write, "UTF-8");
                String configuration = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
                configuration = configuration + "<configuration creationDate=\"" + de.folt.util.OpenTMSSupportFunctions.getDateString() + "\">\n</configuration>";
                writer.write(configuration);
                writer.close();
                write.close();
                f = new File(configFile);
            }
            catch (Exception ex)
            {
                try
                {
                    writer.close();
                    write.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                ex.printStackTrace();
                f = new File(configFile);
                if (f.exists())
                {
                    f.delete();
                }
                return null;
            }
            doc = new XmlDocument();
            doc.loadXmlFile(f);
            root = doc.getDocument().getRootElement();
            user = new Element("user");
            user.setAttribute("creationDate", de.folt.util.OpenTMSSupportFunctions.getDateString());
            user.setAttribute("name", adaptToAscii(currentUser));
            root.addContent(user);
            XMLOutputter outputter = new XMLOutputter();
            outputter.output(doc.getDocument(), new FileOutputStream(adaptToAscii(configFile)));
            return user;
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private Element getUserRoot()
    {
        File f = new File(configFile);
        if (!f.exists())
        {
            return createFile();
        }

        try
        {
            doc = new XmlDocument();
            doc.loadXmlFile(f);
            root = doc.getDocument().getRootElement();
            List<Element> users = (List<Element>) root.getChildren("user");
            @SuppressWarnings("rawtypes")
			Iterator i = users.iterator();
            while (i.hasNext())
            {
                Element e = (Element) i.next();
                if (e.getAttributeValue("name", "").equals(currentUser))
                {
                    return e;
                }
            }
            Element e = new Element("user");
            e.setAttribute("name", adaptToAscii(currentUser));
            e.setAttribute("creationDate", de.folt.util.OpenTMSSupportFunctions.getDateString());
            root.addContent(e);
            return e;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("New configfile:" + configFile);
            return createFile();
        }
    }

    /**
     * loadBooleanValueForKey load a value for a given key
     * @param key the key to search for
     * @return the boolean value for the key
     */
    public Boolean loadBooleanValueForKey(String key)
    {
        user = getUserRoot();
        if (user == null)
        {
            if (currentShell != null)
            {
                MessageBox box = new MessageBox(currentShell, SWT.ICON_ERROR | SWT.OK);
                box.setMessage(message.getString("Error_Reading_Configuration_File"));
                box.open();
            }
            return false;
        }

        Element e1 = user.getChild(key); //$NON-NLS-1$
        if (e1 == null)
        {
            e1 = new Element(key); //$NON-NLS-1$
        }
        String val = e1.getText();
        try
        {
            return Boolean.parseBoolean(val);
        }
        catch (Exception ex)
        {
            return false;
        }
    }
    
    /**
     * loadBooleanValueForKey load a value for a given key
     * @param key the key to search for
     * @return the int value for the key
     */
    public int loadIntValueForKey(String key)
    {
        user = getUserRoot();
        if (user == null)
        {
            if (currentShell != null)
            {
                MessageBox box = new MessageBox(currentShell, SWT.ICON_ERROR | SWT.OK);
                box.setMessage(message.getString("Error_Reading_Configuration_File"));
                box.open();
            }
            return -1;
        }

        Element e1 = user.getChild(key); //$NON-NLS-1$
        if (e1 == null)
        {
            e1 = new Element(key); //$NON-NLS-1$
        }
        String val = e1.getText();
        try
        {
            return Integer.parseInt(val);
        }
        catch (Exception ex)
        {
            return -1;
        }
    }
    
    /**
     * loadValueForKey load a value for a given key
     * @param key the key to search for
     * @return the value for the key
     */
    public String loadValueForKey(String key)
    {
        user = getUserRoot();
        if (user == null)
        {
            if (currentShell != null)
            {
                MessageBox box = new MessageBox(currentShell, SWT.ICON_ERROR | SWT.OK);
                box.setMessage(message.getString("Error_Reading_Configuration_File"));
                box.open();
            }
            return "";
        }

        Element e1 = user.getChild(key); //$NON-NLS-1$
        if (e1 == null)
        {
            e1 = new Element(key); //$NON-NLS-1$
        }
        return e1.getText();
    }

    /**
     * @param configurationFile
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void saveDoc(String configurationFile) throws FileNotFoundException, IOException
    {
        XMLOutputter outputter = new XMLOutputter();
        configurationFile = adaptToAscii(configurationFile);
        try
        {
            outputter.output(doc.getDocument(), new FileOutputStream(configurationFile));
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * saveKeyValuePair saves a value for the given user for a key
     * @param key the key for the value
     * @param value the value to store
     * @return true if successfully stored otherwise false
     */
    public boolean saveKeyValuePair(String key, boolean value)
    {
        return saveKeyValuePair(key, value+"");
    }
    
    /**
     * saveKeyValuePair saves a value for the given user for a key
     * @param key the key for the value
     * @param value the value to store
     * @return true if successfully stored otherwise false
     */
    public boolean saveKeyValuePair(String key, int value)
    {
        return saveKeyValuePair(key, value+"");
    }

    /**
     * saveKeyValuePair saves a value for the given user for a key
     * @param key the key for the value
     * @param value the value to store
     * @return true if successfully stored otherwise false
     */

    public boolean saveKeyValuePair(String key, String value)
    {
        user = getUserRoot();
        if (user == null)
        {
            MessageBox box = new MessageBox(currentShell, SWT.ICON_ERROR | SWT.OK);
            box.setMessage(message.getString("Error_Reading_Configuration_File"));
            box.open();
            return false;
        }
        Element e1 = user.getChild(key); //$NON-NLS-1$
        if (e1 == null)
        {
            e1 = new Element(key); //$NON-NLS-1$
            e1.setAttribute("creationDate", de.folt.util.OpenTMSSupportFunctions.getDateString());
            user.addContent(e1);
            e1.setText(value);
        }
        else
        {
            e1.setText(value);
            e1.setAttribute("modificationDate", de.folt.util.OpenTMSSupportFunctions.getDateString());
        }
        try
        {
            saveDoc(configFile);
            return true;
        }
        catch (FileNotFoundException e)
        {
            if (currentShell != null)
            {
                MessageBox box = new MessageBox(currentShell, SWT.ICON_ERROR | SWT.OK);
                box.setMessage(e.getLocalizedMessage());
                box.open();
            }
            e.printStackTrace();
            return false;
        }
        catch (IOException e)
        {
            if (currentShell != null)
            {
                MessageBox box = new MessageBox(currentShell, SWT.ICON_ERROR | SWT.OK);
                box.setMessage(e.getLocalizedMessage());
                box.open();
            }
            e.printStackTrace();
            return false;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }

}