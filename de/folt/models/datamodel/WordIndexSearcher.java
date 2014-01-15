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
public interface WordIndexSearcher extends Searcher
{
    /**
     * search runs a search on a set of MonoLingualObjects. All parameters should be supplied as Strings.
     * @param searchparameters a hashtable with the parameters for the search (segment, wordlist, nOfWords, or, stemmed, ...)
     * @return a Vector of matching MonoLingualObjects 
     */
    public Vector<MonoLingualObject> search(Hashtable<String, String> searchparameters);
}
