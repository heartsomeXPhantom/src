/*
 * Created on 14.12.2008
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.folt.models.datamodel;

import java.util.Hashtable;
import java.util.Vector;

/**
 * @author klemens
 * 
 */
public interface FuzzyIndexSearcher extends Searcher
{
    /**
     * search runs a fuzzy algorithm search on a set of MonoLingualObjects
     * @param searchparameters a hashtable with the parameters for the search (segment, percent, ...). All parameters should be supplied as Strings.
     * @return a Vector of matching MonoLingualObjects with similarity measures attached
     */
    public Vector<MonoLingualObject> search(Hashtable<String, String> searchparameters);
}
