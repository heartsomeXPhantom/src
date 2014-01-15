package de.folt.util;

import java.util.Hashtable;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages
{
    
    private static Hashtable<String, Messages> resourceBundles = new Hashtable<String, Messages>();
    
    private ResourceBundle RESOURCE_BUNDLE = null;
    
    private Messages()
    {
        // do not allow instances of this class
    }
    
    /**
     * @param name
     * @param language
     */
    private Messages(String name, String language)
    {
        String BUNDLE_NAME = name;
        Locale loc = Locale.ENGLISH;
        if (language.equals("de"))
            loc = Locale.GERMAN;
        try
        {
            try
            {
                RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME, loc, this.getClass().getClassLoader());
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            if (RESOURCE_BUNDLE == null)
                ResourceBundle.getBundle(BUNDLE_NAME);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * getInstance get a language and resource name specific message
     * @param name the name of the bundle
     * @param language the language
     * @return the language and name specific message
     */
    public static Messages getInstance(String name, String language)
    {
        if (resourceBundles.containsKey(name + language))
            return resourceBundles.get(name + language);
        Messages message;
        try
        {
            message = new Messages(name, language); 
            resourceBundles.put(name + language, message);
        }
        catch (Exception e)
        {
            message = new Messages();
            resourceBundles.put(name + language, message);
        }
        return message;
    }
       

    /**
     * getString get a language specific resource bundle string
     * @param key the key for the message/string to return
     * @return the language specific message
     */
    public String getString(String key)
    {
        try
        {
            return RESOURCE_BUNDLE.getString(key);
        }
        catch (MissingResourceException e)
        {
            return '!' + key + '!';
        }
        catch (Exception e)
        {
            return '!' + key + '!';
        }
    }
}