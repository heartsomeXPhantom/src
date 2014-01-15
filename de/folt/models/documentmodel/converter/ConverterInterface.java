/*
 * Created on 12.12.2008
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.models.documentmodel.converter;

import java.util.Hashtable;

public interface ConverterInterface
{
    /**
     * describeConversionParameters - describe parameters of the converter
     * @return returns a hashtable (key, value pairs) where the key is a supported conversion parameter and value a description of the key.
     */
    public Hashtable<String, Object> describeConversionParameters();
    
    /**
     * describeReverseConversionParameters - describe parameters of the reverse converter
     * @return returns a hashtable (key, value pairs) where the key is a supported reverse conversion parameter and value a description of the key.
     */
    public Hashtable<String, Object> describeReverseConversionParameters();
    
    /**
     * runConversion converts document to xliff document. The converter interface method is quite generic so that it is easy to add additional converters. 
     * @param parameters - Hashtable contains the parameters for the reverse conversion
     * @return Hashtable with key - value pairs containing the conversion results
     */
    public Hashtable<String, Object> runConversion(Hashtable<String, Object> parameters);
    
    /**
     * runReverseConversion converts   xliff document to original format document.  The converter interface method is quite generic so that it is easy to add additional converters. 
     * @param parameters - Hashtable contains the parameters for the conversion
     * @return Hashtable with key - value pairs containing the conversion results
     */
    public Hashtable<String, Object> runReverseConversion(Hashtable<String, Object> parameters);
}
