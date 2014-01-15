/*
 * Created on 01.07.2009
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.util;

import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

/**
 * This class contains several methods dealing with code page handling.
 * @author klemens
 *
 */
public class CodePageHandling
{
    /**
     * getPageCodes get the available code pages from the system
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    public static String[] getCodePages()
    {
        @SuppressWarnings("rawtypes")
		TreeMap charsets = new TreeMap(Charset.availableCharsets());
        @SuppressWarnings("rawtypes")
		Set keys = charsets.keySet();
        String[] codes = new String[keys.size()];

        @SuppressWarnings("rawtypes")
		Iterator i = keys.iterator();
        int j = 0;
        while (i.hasNext())
        {
            Charset cset = (Charset) charsets.get(i.next());
            codes[j++] = cset.displayName();
        }
        return codes;
    }
}
